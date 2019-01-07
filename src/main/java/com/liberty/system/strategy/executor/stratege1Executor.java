package com.liberty.system.strategy.executor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfplugin.mail.MailKit;
import com.liberty.common.utils.MailUtil;
import com.liberty.system.blackHouse.RemoveStrategyBh;
import com.liberty.system.model.Centre;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.model.Line;
import com.liberty.system.model.Strategy;
import com.liberty.system.model.Stroke;

public class stratege1Executor implements Executor {
	private Strategy strategy;

	public stratege1Executor() {
		this.strategy = Strategy.dao.findById(1);
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public Vector<Currency> execute(String code) {
		Vector<Currency> stayCurrency = new Vector<>();
		if (code == null) {
			List<Currency> allCurrency = Currency.dao.listAll();
			for (Currency currency : allCurrency) {
				if(RemoveStrategyBh.inBlackHouse(currency)) {//在小黑屋里面,跳过
					allCurrency.remove(currency);
				}
			}
			multiProExe(allCurrency, stayCurrency);
		} else {
			Currency currency = Currency.dao.findByCode(code);
			if (!RemoveStrategyBh.inBlackHouse(code)) {//不在小黑屋里且满足策略
				if(executeSingle(code)) {
					if(successStrategy(currency)) {
						stayCurrency.add(currency);
					}
				}else {
//					不自动从策略组中剔除,自动剔除容易错过符合条件的股票
//					Record record = Db.findFirst("select * from currency_strategy where cutLine is not null and currencyId=? and strategyId=?",
//							currency.getId(), strategy.getId());
//					if(record!=null) {
//						Db.delete("currency_strategy",record);
//					}
				}
			}
		}
		if(stayCurrency.size()!=0) {
			MailUtil.sendMailToBuy(stayCurrency, this.getStrategy());
		}
		return stayCurrency;
	}

	public void multiProExe(List<Currency> cs, Vector<Currency> sc) {
		long start = System.currentTimeMillis();
		List<Future> futureList = new ArrayList<>();
		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		for (Currency currency : cs) {
			Future<?> future = threadPool.submit(new Runnable() {
				@Override
				public void run() {
					if (executeSingle(currency.getCode())) {
						System.err.println("满足策略:"+currency.getCode()+":"+currency.getName());
						if(successStrategy(currency)) {
							System.err.println("不存在");
							sc.add(currency);
						}
						System.err.println("已存在");
					}else {
						Record record = Db.findFirst("select * from currency_strategy where cutLine is null and currencyId=? and strategyId=?",
								currency.getId(), strategy.getId());
						if(record!=null) {
							Db.delete("currency_strategy",record);
						}
					}
				}
			});
			futureList.add(future);
		}
		for (Future future : futureList) {
			try {
				future.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		double time = (end - start) * 1.0 / 1000 / 60;
		MailKit.send("530256489@qq.com", null, "策略[二三买重合]执行耗时提醒!", "此次策略执行耗时:" + time + "分钟!");
	}

	public boolean executeSingle(String code) {
		List<Stroke> strokes = null;
		List<Line> storeLines = new ArrayList<Line>();// 生成的线段
		Line lastLine = Line.dao.getLastByCode(code, "k");
		if (lastLine == null) {
			return false;
		} else {
			storeLines.add(lastLine);
			Date date = lastLine.getEndDate();
			strokes = Stroke.dao.getListByDate(code, "k", date);
			if (strokes == null || strokes.size() == 0) {
				return false;
			}
			return onStrategy(strokes, lastLine);
		}
	}

	/**
	 * 判断是否满足策略
	 * @param strokes
	 * @param lastLine
	 * @return
	 */
	private boolean onStrategy(List<Stroke> strokes, Line lastLine) {
		Integer currencyId = strokes.get(0).getCurrencyId();
		Currency currency = Currency.dao.findById(currencyId);

		double premax = strokes.get(0).getMax();
		double premin = strokes.get(0).getMin();
		int size = strokes.size();
		outter: for (int i = 0; i < size - 2; i++) {
			if (i == 2 && "0".equals(strokes.get(i).getDirection())) {
				// 第一笔包含第三笔
				if (strokes.get(i - 2).getMax() > strokes.get(i).getMax()
						&& strokes.get(i - 2).getMin() < strokes.get(i).getMin()) {
					premax = strokes.get(i).getMax() - 0.01;
				}
			}
			if (i == 2 && "1".equals(strokes.get(i).getDirection())) {
				// 第一笔包含第三笔
				if (strokes.get(i - 2).getMax() > strokes.get(i).getMax()
						&& strokes.get(i - 2).getMin() < strokes.get(i).getMin()) {
					premin = strokes.get(i).getMin() + 0.01;
				}
			}

			// 重新设置前最大最小值
			if (strokes.get(i + 2).getMax() > strokes.get(i).getMax() && "0".equals(strokes.get(i).getDirection())) {
				if (strokes.get(i).getMax() > premax) {
					premax = strokes.get(i).getMax();
				}
			}
			if (strokes.get(i + 2).getMin() < strokes.get(i).getMin() && "1".equals(strokes.get(i).getDirection())) {
				if (strokes.get(i).getMin() < premin) {
					premin = strokes.get(i).getMin();
				}
			}

			// 找到分界点[顶]
			if (strokes.get(i).getMax() > premax && strokes.get(i).getMax() > strokes.get(i + 2).getMax()
					&& "0".equals(strokes.get(i).getDirection())) {
				return false;
			}
			// 找到分解点[底]
			if (strokes.get(i).getMin() < premin && strokes.get(i).getMin() < strokes.get(i + 2).getMin()
					&& "1".equals(strokes.get(i).getDirection())) {
				// 1:笔破坏
				if (strokes.get(i + 1).getMax() > premin) {
					// 笔破坏最终确认--先找分界点的情况下笔破坏是必定成立的
					if (i + 2 == size - 1 && "1".equals(strokes.get(i + 2).getDirection())) {
						List<Stroke> list = null;
						if ("1".equals(lastLine.getDirection())) {// 与最后一条线段同向
							list = Stroke.dao.getByDateRange(currency.getId(), "k", lastLine.getStartDate(),
									strokes.get(i).getEndDate());
						} else {
							list = Stroke.dao.getByDateRange(currency.getId(), "k", lastLine.getEndDate(),
									strokes.get(i).getEndDate());
						}
						Centre lastCentre = buildLastCentre(list);
						if (lastCentre != null && strokes.get(i + 2).getMin() > lastCentre.getCentreMax()) {
							List<Kline> klines = Kline.dao.getListByDate(currency.getCode(), "k", strokes.get(i + 2).getEndDate());
							for (Kline kline : klines) {
								if(kline.getMin()<=lastCentre.getCentreMax()) {
									return false;
								}
							}
							return true;
						} else {
							return false;
						}
					}
				}
				// 2:线段破坏
				for (int j = i + 1; j < size - 2; j++) {
					if (strokes.get(j).getMax() > premin) {
						// 线段破坏成立
						if (j + 2 == size - 1 && "1".equals(strokes.get(j + 2).getDirection())) {
							List<Stroke> list = null;
							if ("1".equals(lastLine.getDirection())) {// 与最后一条线段同向
								list = Stroke.dao.getByDateRange(currency.getId(), "k", lastLine.getStartDate(),
										strokes.get(j).getEndDate());
							} else {
								list = Stroke.dao.getByDateRange(currency.getId(), "k", lastLine.getEndDate(),
										strokes.get(j).getEndDate());
							}
							Centre lastCentre = buildLastCentre(list);
							if (lastCentre != null && strokes.get(i + 2).getMin() > lastCentre.getCentreMax()) {
								List<Kline> klines = Kline.dao.getListByDate(currency.getCode(), "k", strokes.get(i + 2).getEndDate());
								for (Kline kline : klines) {
									if(kline.getMin()<=lastCentre.getCentreMax()) {
										return false;
									}
								}
								return true;
							} else {
								return false;
							}
						}
					}
					if (strokes.get(j + 1).getMin() < strokes.get(i).getMin()) {
						premin = strokes.get(i).getMin();
						i = j;
						continue outter;
					}
					j++;
				}
			}
		}
		return false;
	}

	/**
	 * 满足策略,判断记录是否存在,执行不同的操作
	 * @param currency
	 * @return
	 */
	public boolean successStrategy(Currency currency) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Record record = Db.findFirst("select * from currency_strategy where currencyId=? and strategyId=?",
				currency.getId(), this.strategy.getId());
		if (record == null) {
			record = new Record().set("currencyId", currency.getId()).set("strategyId", this.strategy.getId())
					.set("startDate", format.format(new Date()));
			Db.save("currency_strategy", record);
			return true;
		} else {
			record.set("startDate", format.format(new Date()));
			Db.update("currency_strategy", record);
			//如果已经存在该条记录,只是做更新时间的处理
			return false;
		}
	}

	/**
	 * 构建最近的一个中枢
	 * @param strokes
	 * @return
	 */
	public Centre buildLastCentre(List<Stroke> strokes) {
		double max, min, centreMax, centreMin;
		Centre centre = null;
		for (int i = 1; i < strokes.size() - 2; i++) {
			if (overlap(strokes.get(i), strokes.get(i + 1), strokes.get(i + 2)) == 0) {
				if ("0".equals(strokes.get(i).getDirection())) {
					if (strokes.get(i + 1).getMin() < strokes.get(i).getMin()) {
						min = strokes.get(i + 1).getMin();
						centreMin = strokes.get(i).getMin();
					} else {
						min = strokes.get(i).getMin();
						centreMin = strokes.get(i + 1).getMin();
					}
					if (strokes.get(i + 2).getMax() > strokes.get(i).getMax()) {
						max = strokes.get(i + 2).getMax();
						centreMax = strokes.get(i).getMax();
					} else {
						max = strokes.get(i).getMax();
						centreMax = strokes.get(i + 2).getMax();
					}
				} else {
					if (strokes.get(i + 1).getMax() > strokes.get(i).getMax()) {
						max = strokes.get(i + 1).getMax();
						centreMax = strokes.get(i).getMax();
					} else {
						max = strokes.get(i).getMax();
						centreMax = strokes.get(i + 1).getMax();
					}
					if (strokes.get(i + 2).getMin() < strokes.get(i).getMin()) {
						min = strokes.get(i + 2).getMin();
						centreMin = strokes.get(i).getMin();
					} else {
						min = strokes.get(i).getMin();
						centreMin = strokes.get(i + 2).getMin();
					}
				}
				centre = new Centre();
				centre.setMax(max).setMin(min).setCentreMax(centreMax).setCentreMin(centreMin);
				break;
			} else {
				continue;
			}
		}
		return centre;
	}

	/**
	 * 判断三笔是否重叠
	 * 
	 * @param s1
	 * @param s2
	 * @param s3
	 * @return 0:重叠;1:不重叠:方向向上;2:不重叠:方向向下
	 */
	protected int overlap(Stroke s1, Stroke s2, Stroke s3) {
		if ("0".equals(s1.getDirection()) && "0".equals(s3.getDirection())) {
			if (s1.getMin() > s3.getMax()) {
				return 2;
			}
		}
		if ("1".equals(s1.getDirection()) && "1".equals(s3.getDirection())) {
			if (s1.getMax() < s3.getMin()) {
				return 1;
			}
		}
		return 0;
	}
}
