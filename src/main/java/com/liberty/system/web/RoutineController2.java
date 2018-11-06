package com.liberty.system.web;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.liberty.common.web.BaseController;


/**
 * 定时任务
 */
public class RoutineController2 extends BaseController implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		CurrencyController currencyController = new CurrencyController();
		currencyController.updateCurrency();
	}
	
}
