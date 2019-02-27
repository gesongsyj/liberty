package com.liberty.system.strategy.executor.job;

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
import com.liberty.system.strategy.executor.Executor;

public class Stratege1Executor extends StrategeExecutor implements Executor {

	public Stratege1Executor() {
		this.strategy = Strategy.dao.findById(1);
	}

	@Override
	public Vector<Currency> execute(String code) {
		long start = System.currentTimeMillis();
		Vector<Currency> stayCurrency = new Vector<>();
		if (code == null) {
			List<Currency> allCurrency = Currency.dao.listAll();
			for (Currency currency : allCurrency) {
				if (RemoveStrategyBh.inBlackHouse(currency)) {// 在小黑屋里面,跳过
					allCurrency.remove(currency);
				}
			}
			multiProExe(allCurrency, stayCurrency);
		} else {
			Currency currency = Currency.dao.findByCode(code);
			if (!RemoveStrategyBh.inBlackHouse(code)) {// 不在小黑屋里且满足策略
				if (executeSingle(currency)) {
					if (successStrategy(currency)) {
						stayCurrency.add(currency);
					}
				}
//				else {
//					不自动从策略组中剔除,自动剔除容易错过符合条件的股票
//					Record record = Db.findFirst("select * from currency_strategy where cutLine is not null and currencyId=? and strategyId=?",
//							currency.getId(), strategy.getId());
//					if(record!=null) {
//						Db.delete("currency_strategy",record);
//					}
//				}
			}
		}
		if (stayCurrency.size() != 0) {
			MailUtil.sendMailToBuy(stayCurrency, super.getStrategy());
		}
		System.out.println("策略1执行完毕!");
		long end = System.currentTimeMillis();
		double time = (end - start) * 1.0 / 1000 / 60;
		MailKit.send("530256489@qq.com", null, "策略[" + strategy.getDescribe() + "]执行耗时提醒!", "此次策略执行耗时:" + time + "分钟!");
		return stayCurrency;
	}

	@Override
	public boolean executeSingle(Currency currency) {
		List<Stroke> strokes = null;
		List<Line> storeLines = new ArrayList<Line>();// 生成的线段
		Line lastLine = Line.dao.getLastByCode(currency.getCode(), "k");
		if (lastLine == null) {
			return false;
		} else {
			storeLines.add(lastLine);
			Date date = lastLine.getEndDate();
			strokes = Stroke.dao.getListByDate(currency.getCode(), "k", date);
			if (strokes == null || strokes.size() == 0) {
				return false;
			}
			return onStrategy(strokes, lastLine);
		}
	}

	/**
	 * 判断是否满足策略
	 * 
	 * @param strokes
	 * @param lastLine
	 * @return
	 */
	public boolean onStrategy(List<Stroke> strokes, Line lastLine) {
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
						Centre lastCentre = buildLineCentre(list);
						if (lastCentre != null && strokes.get(i + 2).getMin() > lastCentre.getCentreMax()) {
							List<Kline> klines = Kline.dao.getListByDate(currency.getCode(), "k",
									strokes.get(i + 2).getEndDate());
							for (Kline kline : klines) {
								if (kline.getMin() <= lastCentre.getCentreMax()) {
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
							Centre lastCentre = buildLineCentre(list);
							if (lastCentre != null && strokes.get(i + 2).getMin() > lastCentre.getCentreMax()) {
								List<Kline> klines = Kline.dao.getListByDate(currency.getCode(), "k",
										strokes.get(i + 2).getEndDate());
								for (Kline kline : klines) {
									if (kline.getMin() <= lastCentre.getCentreMax()) {
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
	 * 
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
			// 如果已经存在该条记录,只是做更新时间的处理
			return false;
		}
	}

}
