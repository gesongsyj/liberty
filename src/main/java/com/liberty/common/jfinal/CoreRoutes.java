package com.liberty.common.jfinal;

import com.jfinal.config.Routes;
import com.liberty.system.web.CurrencyController;
import com.liberty.system.web.KlineController;
import com.liberty.system.web.StrokeController;
public class CoreRoutes extends Routes{

	@Override
	public void config() {
		setBaseViewPath("/WEB-INF/views/");
		add("/currency", CurrencyController.class);
		add("/kline", KlineController.class);
		add("/stroke", StrokeController.class);
	}

}