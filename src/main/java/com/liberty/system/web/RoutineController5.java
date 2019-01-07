package com.liberty.system.web;


import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.liberty.common.web.BaseController;
import com.liberty.system.blackHouse.RemoveStrategyBh;
import com.liberty.system.strategy.cuttor.LossCuttor;
import com.liberty.system.strategy.executor.Executor;
import com.liberty.system.strategy.executor.stratege1Executor;


/**
 * 定时任务
 */
public class RoutineController5 extends BaseController implements Job {
	private List<Executor> exes=new ArrayList<Executor>();
	/**
	 * 策略执行
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		exes.add(new stratege1Executor());
		for (Executor executor : exes) {
			executor.execute(null);
		}
	}
	
}
