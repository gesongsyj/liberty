package com.liberty.system.strategy.executor.realtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.jfinal.kit.PropKit;
import com.jfplugin.mail.MailKit;
import com.liberty.common.plugins.threadPoolPlugin.ThreadPoolKit;
import com.liberty.common.utils.MailUtil;
import com.liberty.system.downloader.DownLoader;
import com.liberty.system.downloader.impl.DfcfDownLoader;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.model.Strategy;
import com.liberty.system.strategy.executor.Executor;
import com.liberty.system.strategy.executor.job.StrategeExecutor;

public class RealTimeStrategeExe1 extends StrategeExecutor implements Executor  {
	//昨日下跌或者涨幅不高的存在这个map中,第一轮筛选之后只循环这个map
	public static Map<String, Currency> currencyMap=new HashMap<String, Currency>();
	//已经发过邮件的不再处理
	public static Map<String, Currency> currencyMapMailed=new HashMap<String, Currency>();
	
	public RealTimeStrategeExe1() {
		this.strategy = Strategy.dao.findById(0);
	}

	@Override
	public Vector<Currency> execute(String code) {
		long start = System.currentTimeMillis();
		Vector<Currency> stayCurrency = new Vector<>();
		if(code==null) {
			List<Currency> allCurrency = Currency.dao.listAll();
			multiFilter(allCurrency);
			multiProExe(allCurrency, stayCurrency);
		}else {
			Currency currency = Currency.dao.findByCode(code);
			if (executeSingle(currency)) {
				if (notExistsRecord(currency)) {
					stayCurrency.add(currency);
				}
			}
		}
		if (stayCurrency.size() != 0) {
			MailUtil.sendMailToBuy(stayCurrency, super.getStrategy());
		}
		System.out.println("策略0执行完毕!");
		long end = System.currentTimeMillis();
		double time = (end - start) * 1.0 / 1000 / 60;
		MailKit.send("530256489@qq.com", null, "策略[" + strategy.getDescribe() + "]执行耗时提醒!", "此次策略执行耗时:" + time + "分钟!");
		return stayCurrency;
	}

	private void multiFilter(List<Currency> cs) {
		ThreadPoolExecutor executor = ThreadPoolKit.getExecutor();
		int queueSize = executor.getQueue().remainingCapacity();
		for (int i = 0; i < cs.size(); i++) {
			List<Future> futureList = new ArrayList<>();
			for (int j = 0; j < queueSize && i < cs.size(); j++, i++) {
				Currency currency = cs.get(i);
				Future<?> future = executor.submit(new Callable() {
					@Override
					public Object call() throws Exception {
						return filter(currency);
					}
				});
				futureList.add(future);
			}
			for (Future future : futureList) {
				try {
					Currency currency = (Currency) future.get();
					if(currency!=null) {
						cs.remove(currency);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			i--;
		}
	}

	protected Currency filter(Currency currency) {
		DownLoader downLoader = new DfcfDownLoader();
		List<Kline> ks = downLoader.downLoad(currency, "k", "get", 3);
		if(ks==null || ks.size()!=3) {
			return currency;
		}else {
			if((ks.get(1).getClose()-ks.get(0).getClose())/ks.get(0).getClose()<Double.valueOf(PropKit.get("aoilimit"))) {
				return null;
			}else {
				return currency;
			}
		}
	}

	@Override
	public boolean executeSingle(Currency currency) {
		
		return false;
	}

	@Override
	public boolean notExistsRecord(Currency currency) {
		return !currencyMapMailed.containsKey(currency.getCode());
	}
	
	
}
