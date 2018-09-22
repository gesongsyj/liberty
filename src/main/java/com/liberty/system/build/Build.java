package com.liberty.system.build;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.generator.MyGenerator.MyGenerator;
import com.jfinal.aop.Duang;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.liberty.common.utils.HTTPUtils;

public class Build {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		MyGenerator generator = new MyGenerator("com.liberty.system", "jfinal.properties", true);
		generator.setUsername("username");
		generator.setMappingKitPackageName("com.liberty.common.jfinal");
		generator.addIncludedTable("kline","line","stroke");
		generator.generate();
		
	}
}
