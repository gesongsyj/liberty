package com.liberty.system.strategy.executor.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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
import com.sun.tools.internal.xjc.model.SymbolSpace;

public class Stratege2Executor extends StrategeExecutor implements Executor {
	public Stratege2Executor() {
		this.strategy = Strategy.dao.findById(2);
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
					if (notExistsRecord(currency)) {
						stayCurrency.add(currency);
					}
				}
			}
		}
		if (stayCurrency.size() != 0) {
			MailUtil.sendMailToBuy(stayCurrency, this.getStrategy());
		}
		System.out.println("策略2执行完毕!");
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
			return onStrategy(strokes);
		}
	}
	
	/**
	 * 判断是否满足策略
	 * 
	 * @param strokes
	 * @param lastLine
	 * @return
	 */
	public boolean onStrategy(List<Stroke> strokes) {
		Integer currencyId = strokes.get(0).getCurrencyId();
		Currency currency = Currency.dao.findById(currencyId);

		Centre centre = new Centre();
		Stroke strokeBeforeCentre = new Stroke();
		Stroke strokeAfterCentre = new Stroke();
		if ("1".equals(strokes.get(strokes.size() - 1).getDirection())) {
			buildCentre(strokes, centre, strokeBeforeCentre, strokeAfterCentre);
			if (strokeBeforeCentre.getMax()==null) {
				System.err.println("strokeBeforeCentre is null!");
				return false;
			} else {
				System.out.println("strokeBeforeCentre:"+strokeBeforeCentre);
				System.out.println("strokeAfterCentre:"+strokeAfterCentre);
				System.out.println("strokeAfterCentre.getMin():"+strokeAfterCentre.getMin());
				System.out.println("centre.getMin():"+centre.getMin());
				if (strokeBeforeCentre.getMax() < centre.getMax() || strokeAfterCentre.getMin() > centre.getMin()) {
					return false;
				} else {
					if(strokeBeforeCentre.getMax()<centre.getMax()||strokeAfterCentre.getMin()>centre.getMin()) {
						return false;
					}
					List<Kline> klinesBeforeCentre = Kline.dao.getByDateRange(currency.getCode(), "k",
							strokeBeforeCentre.getStartDate(), strokeBeforeCentre.getEndDate());
					List<Kline> klinesAfterCentre = Kline.dao.getByDateRange(currency.getCode(), "k",
							strokeAfterCentre.getStartDate(), strokeAfterCentre.getEndDate());
					double barBeforeCentre = 0;
					double minDiffBeforeCentre=0;
					for (Kline kline : klinesBeforeCentre) {
						if(kline.getBar()<0) {
							barBeforeCentre += kline.getBar();
						}
						if(kline.getDiff()<0) {
							if(kline.getDiff()<minDiffBeforeCentre) {
								minDiffBeforeCentre=kline.getDiff();
							}
						}
					}
					double barAfterCentre = 0;
					double minDiffAfterCentre=0;
					for (Kline kline : klinesAfterCentre) {
						if(kline.getBar()<0) {
							barAfterCentre += kline.getBar();
						}
						if(kline.getDiff()<0) {
							if(kline.getDiff()<minDiffAfterCentre) {
								minDiffAfterCentre=kline.getDiff();
							}
						}
					}
					//负数,绝对值小的大
					if(barAfterCentre>barBeforeCentre||minDiffAfterCentre>minDiffBeforeCentre ) {
						return true;
					}else {
						return false;
					}
				}
			}
		} else {
			return false;
		}
	}

}
