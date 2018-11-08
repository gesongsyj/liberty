package com.liberty.system.web;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.liberty.common.web.BaseController;


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
		klineController.downloadData(null);
		klineController.createStroke(null);
		klineController.createLine(null);
	}
	
}
