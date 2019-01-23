package com.liberty.system.web;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfplugin.mail.MailKit;
import com.liberty.common.web.BaseController;

/**
 * 定时任务
 */
public class RoutineController6 extends BaseController implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MailKit.send("530256489@qq.com", null, "IMPORTANT!", "Stop working!");
	}

}
