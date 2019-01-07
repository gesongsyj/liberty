package com.liberty.system.web;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.liberty.common.web.BaseController;
import com.liberty.system.blackHouse.RemoveStrategyBh;
import com.liberty.system.strategy.cuttor.LossCuttor;


/**
 * 定时任务
 */
public class RoutineController4 extends BaseController implements Job {

	/**
	 * 重置小黑屋
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		RemoveStrategyBh.clear();
	}
	
}
