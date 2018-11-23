package com.liberty.system.web;


import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.liberty.common.web.BaseController;
import com.liberty.system.model.Currency;


/**
 * 定时任务
 */
public class RoutineController1 extends BaseController implements Job {

	/**
	 * 定时更新数据库中已有股票的数据,半天或者一天左右更新一次
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
//		CurrencyController currencyController = new CurrencyController();
//		currencyController.updateCurrency();
		
		KlineController klineController = new KlineController();
		List<Currency> listAll = Currency.dao.listAll();
		klineController.multiProData(listAll);
	}
	
}
