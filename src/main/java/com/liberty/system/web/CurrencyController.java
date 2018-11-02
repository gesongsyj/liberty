package com.liberty.system.web;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.common.utils.JsonToMap;
import com.liberty.common.web.BaseController;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.query.KlineQueryObject;
import com.liberty.system.service.CurrencyKit;
import com.liberty.system.service.impl.CurrencyKit_Gp;
import com.liberty.system.service.impl.CurrencyKit_Wh;

public class CurrencyController extends BaseController {

	@Before(Tx.class)
	public void updateCurrency() {
//		CurrencyKit currency=new CurrencyKit_Wh();
		CurrencyKit currency=new CurrencyKit_Gp();
		currency.update();
		renderText("ok");
	}
}