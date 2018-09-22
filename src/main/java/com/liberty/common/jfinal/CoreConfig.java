package com.liberty.common.jfinal;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.OrderedFieldContainerFactory;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.liberty.common.interceptor.CoreInterceptor;
import com.liberty.common.jfinal._MappingKit;
import com.liberty.system.web.KlineController;

import cn.dreampie.quartz.QuartzPlugin;
import net.dreamlu.event.EventPlugin;

public class CoreConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		loadPropertyFile("jfinal.properties");
		me.setEncoding("UTF-8");
		me.setDevMode(true);
		me.setViewType(ViewType.FREE_MARKER);
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new CoreRoutes());
		me.setBaseViewPath("/WEB-INF/views/");
	}

	@Override
	public void configPlugin(Plugins me) {
		// 读取jdbc配置
		final String url = getProperty("jdbcUrl");
		final String username = getProperty("username");
		final String password = getProperty("password");
		final Integer initialSize = Integer.parseInt(getProperty("initialSize"));
		final Integer minIdle = Integer.parseInt(getProperty("minIdle"));
		final Integer maxActive = Integer.parseInt(getProperty("maxActive"));
		final String driverClass = getProperty("driverClass");

		DruidPlugin druidPlugin = new DruidPlugin(url, username, password, driverClass);
		druidPlugin.set(initialSize, minIdle, maxActive);
		druidPlugin.setFilters("stat,wall");// 监控统计："stat" ;防SQL注入："wall"
		me.add(druidPlugin);
		// 实体映射
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setShowSql(true);
		arp.setContainerFactory(new OrderedFieldContainerFactory());// 字段有序，保持和查询的顺序一致
		// 设置sql存放的根路径
		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath() + "/sql");
		arp.addSqlTemplate("all.sql");
		me.add(arp);
		// DB映射
		_MappingKit.mapping(arp);

		// 定时任务
		QuartzPlugin quartz = new QuartzPlugin();
		quartz.setJobs("job.properties");
		me.add(quartz);

		// 初始化事件插件
		EventPlugin plugin = new EventPlugin();
		plugin.async(); // 开启全局异步
		plugin.scanJar(); // 设置扫描jar包，默认不扫描
		// plugin.scanPackage("com.hotel.service.event"); // 设置监听器默认包，默认全扫描
		me.add(plugin);
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new Tx());
		me.add(new CoreInterceptor());
	}

	@Override
	public void configHandler(Handlers me) {

	}

	@Override
	public void configEngine(Engine me) {

	}

	@Override
	public void afterJFinalStart() {
		KlineController klineController = new KlineController();
		klineController.downloadData();
		klineController.createStroke();
	}

}