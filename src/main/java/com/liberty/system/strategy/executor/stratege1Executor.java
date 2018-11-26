package com.liberty.system.strategy.executor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liberty.system.model.Centre;
import com.liberty.system.model.Currency;
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
			multiProExe(allCurrency, stayCurrency);
		} else {
			if (executeSingle(code)) {
				Currency currency = Currency.dao.findByCode(code);
				successStrategy(currency);
				stayCurrency.add(currency);
			}
		}
		return stayCurrency;
	}

	public void multiProExe(List<Currency> cs, Vector<Currency> sc) {
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for (Currency currency : cs) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					if (executeSingle(currency.getCode())) {
						successStrategy(currency);
						sc.add(currency);
					}
				}
			});
		}
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

	public void successStrategy(Currency currency) {
		Record record = Db.findFirst("select * from currency_strategy where currencyId=? and strategyId=?",
				currency.getId(), this.strategy.getId());
		if (record == null) {
			record = new Record().set("currencyId", currency.getId()).set("strategyId", this.strategy.getId());
			Db.save("currency_strategy", record);
		}
		System.err.println(record);
	}

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
