package com.liberty.system.strategy.executor;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.liberty.system.model.Currency;
import com.liberty.system.model.Strategy;

public class stratege1Executor implements Executor{
	private Strategy strategy;
	
	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public stratege1Executor() {
		this.strategy=Strategy.dao.findById(1);
	}

	@Override
	public Vector<Currency> execute() {
		List<Currency> allCurrency = Currency.dao.listAll();
		Vector<Currency> stayCurrency = new Vector<>();
		multiProExe(allCurrency,stayCurrency);
		return stayCurrency;
	}
	
	public void multiProExe(List<Currency> cs,Vector<Currency> sc) {
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for (Currency currency : cs) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					
				}
			});
		}
	}
}
