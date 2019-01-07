package com.liberty.system.test.db;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;

import cn.dreampie.quartz.QuartzPlugin;

public class JfinalConfig {
	public static void start() {
//		File file = new File("jfinal.properties");//项目根路径下
		PropKit.use("jfinal.properties");
		DruidPlugin dp = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("username"), PropKit.get("password"),
				PropKit.get("driverClass"));
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		arp.setDialect(new MysqlDialect());// 切记配置方言
		arp.setShowSql(true);
//		EhCachePlugin ehCachePlugin = new EhCachePlugin("ehcache.xml");//项目根路径下
//		//定时任务
//		PathKit.setWebRootPath(new File(file.getAbsolutePath()).getParent());
//		QuartzPlugin quartz = new QuartzPlugin();
//		quartz.setJobs("job.properties");
//		quartz.start();
//		ehCachePlugin.start();
		dp.start();
		arp.start();
	}

}
