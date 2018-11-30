package com.liberty.system.web;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.liberty.common.web.BaseController;
import com.liberty.system.strategy.cuttor.LossCuttor;


/**
 * 定时任务
 */
public class RoutineController3 extends BaseController implements Job {

	/**
	 * 根据涨幅榜添加新的股票数据,一周左右更新一次,否则数据量可能过多,没必要
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		LossCuttor cuttor = new LossCuttor();
		cuttor.cut();
	}
	
}
