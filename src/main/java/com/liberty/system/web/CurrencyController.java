package com.liberty.system.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.common.utils.JsonToMap;
import com.liberty.common.utils.ResultMsg;
import com.liberty.common.utils.ResultStatusCode;
import com.liberty.common.web.BaseController;
import com.liberty.system.model.Account;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.query.CurrencyQueryObject;
import com.liberty.system.query.KlineQueryObject;
import com.liberty.system.service.CurrencyKit;
import com.liberty.system.service.impl.CurrencyKit_Gp;
import com.liberty.system.service.impl.CurrencyKit_Wh;

public class CurrencyController extends BaseController {
	/**
	 * 抓取财经网站深证,沪证涨幅排行榜前n位的股票添加到数据库
	 */
	@Before(Tx.class)
	public void updateCurrency() {
//		CurrencyKit currency=new CurrencyKit_Wh();
		CurrencyKit currency = new CurrencyKit_Gp();
		currency.update();
		renderText("ok");
	}

	/**
	 * 对该股添加标记,后续可能有期望的走势出现
	 */
	public void addFollow() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String currencyId = paras.get("currencyId");
		String followed=paras.get("followed");
		Currency currency = Currency.dao.findById(currencyId);
		currency.setFollowed(Boolean.valueOf(followed));
		currency.update();
		redirect("/currency/list?qo.currentPage="+qo.getCurrentPage());
	}
	
	/**
	 * 数据库已有股票的分页查询
	 */
	public void list() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		Page<Currency> paginate = Currency.dao.paginate(qo);
		setAttr("pageResult", paginate);
		setAttr("qo", qo);
		render("index.html");
	}

	/**
	 * 数据库中暂时没有的股票希望添加至数据库
	 */
	public void addSearch() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String keyword=qo.getKeyword();
		List<Currency> cs = new ArrayList<Currency>();
		if (keyword != null) {
			try {
				keyword=URLEncoder.encode(keyword, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		String url="http://searchapi.eastmoney.com/api/suggest/get?input="+code+"&type=14&token=D43BF722C8E33BDC906FB84D85E326E8&count=5";
			Map<String, String> params = new HashMap<String, String>();
			params.put("input", keyword);
			params.put("type", "14");
			params.put("token", "D43BF722C8E33BDC906FB84D85E326E8");
			params.put("count", "5");
			String res = HTTPUtils.http("http://searchapi.eastmoney.com/api/suggest/get", params, "get");
			res = res.substring(res.indexOf("["), res.lastIndexOf("]") + 1);
			List<String> Strs = JSON.parseArray(res, String.class);
			for (String string : Strs) {
				Map currencyMap = JSON.parseObject(string, Map.class);
				Currency currency = new Currency();
				currency.setCode(currencyMap.get("Code").toString());
				currency.setName(currencyMap.get("Name").toString());
				currency.setCurrencyType(currencyMap.get("MarketType").toString());
				cs.add(currency);
			}
		}
		setAttr("cs", cs);
		render("add.html");
	}

	/**
	 * 确认添加该股至数据库,从财经网站爬取k线数据,生成笔和线段
	 */
	public void add() {
//		if (!paras.containsKey("code") || !paras.containsKey("name") || !paras.containsKey("currencyType")) {
//			renderJson(new ResultMsg(ResultStatusCode.INVALID_INPUT));
//			return;
//		}
		Currency c = getBean(Currency.class, "c");
		if(c.getCode()==null || c.getName()==null || c.getCurrencyType()==null){
			renderJson(new ResultMsg(ResultStatusCode.INVALID_INPUT));
			return;
		}
		Currency find = Currency.dao.findByCode(c.getCode());
		if (find != null) {
			renderJson(new ResultMsg(ResultStatusCode.CURRENCY_EXISTS));
			return;
		}
//		Currency currency = new Currency();
//		currency.setCode(paras.get("code"));
//		currency.setName(paras.get("name"));
//		currency.setCurrencyType(paras.get("currencyType"));
//		currency.save();
		c.save();

		KlineController klineController = new KlineController();
		klineController.downloadData(c.getCode());
		klineController.createStroke(c.getCode());
		klineController.createLine(c.getCode());
		list();
	}
	
	public  void delete() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String currencyId = paras.get("currencyId");
		Db.update("delete from kline where currencyId=?", currencyId);
		Db.update("UPDATE stroke SET prevId =null,nextId=null where currencyId=?", currencyId);
		Db.update("delete from stroke where currencyId=?", currencyId);
		Db.update("UPDATE line SET prevId =null,nextId=null where currencyId=?", currencyId);
		Db.update("delete from line where currencyId=?", currencyId);
		Db.update("delete from currency where id=?",currencyId);
		redirect("/currency/list?qo.currentPage="+qo.getCurrentPage());
	}
}