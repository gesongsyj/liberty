package com.liberty.system.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.liberty.system.blackHouse.RemoveStrategyBh;
import com.liberty.system.downloader.CurrencyKit;
import com.liberty.system.downloader.impl.CurrencyKit_Gp;
import com.liberty.system.downloader.impl.CurrencyKit_Wh;
import com.liberty.system.model.Account;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.model.Strategy;
import com.liberty.system.query.CurrencyQueryObject;
import com.liberty.system.query.KlineQueryObject;

public class CurrencyController extends BaseController {
	/**
	 * 查询所有策略
	 */
	@Before(Tx.class)
	public void queryStrategy() {
		List<Strategy> ss = Strategy.dao.getAll();
		renderJson(new ResultMsg(ResultStatusCode.OK, ss));
	}

	/**
	 * 添加到策略池
	 */
	@Before(Tx.class)
	public void addStrategy() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String id = paras.get("id");
		String strategyId = paras.get("strategyId");
		
		Record record = new Record().set("currencyId", id).set("strategyId", strategyId)
				.set("startDate", format.format(new Date()));
		Db.save("currency_strategy", record);
		redirect("/currency/list?qo.currentPage=" + qo.getCurrentPage());
	}

	/**
	 * 统一更新数据库所有股票的数据
	 */
	@Before(Tx.class)
	public void updateData() {
		KlineController klineController = new KlineController();
		List<Currency> listAll = Currency.dao.listAll();
		klineController.multiProData(listAll);
		redirect("/currency/list");
	}

	/**
	 * 统一更新策略池所有股票的数据
	 */
	@Before(Tx.class)
	public void updateStrategtData() {
		KlineController klineController = new KlineController();
		List<Currency> listAll = Currency.dao.listForStrategy();
		klineController.multiProData(listAll);
		redirect("/currency/list");
	}

	/**
	 * 抓取财经网站深证,沪证涨幅排行榜前n位的股票添加到数据库并下载数据
	 */
	@Before(Tx.class)
	public void updateCurrency() {
//		CurrencyKit currency=new CurrencyKit_Wh();
		CurrencyKit currencyKit = new CurrencyKit_Gp();
		List<Currency> cs = currencyKit.update();
		KlineController klineController = new KlineController();
		klineController.multiProData(cs);
		redirect("/currency/list");
	}

	/**
	 * 对该股添加标记,后续可能有期望的走势出现
	 */
	@Before(Tx.class)
	public void addFollow() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String currencyId = paras.get("currencyId");
		String followed = paras.get("followed");
		Currency currency = Currency.dao.findById(currencyId);
		currency.setFollowed(Boolean.valueOf(followed));
		currency.update();
		redirect("/currency/list", true);
	}

	/**
	 * 对该股添加标记,后续可能有期望的走势出现
	 */
	@Before(Tx.class)
	public void addFollowForStarage() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String currencyId = paras.get("currencyId");
		String followed = paras.get("followed");
		Currency currency = Currency.dao.findById(currencyId);
		currency.setFollowed(Boolean.valueOf(followed));
		currency.update();
		redirect("/currency/listStrategy", true);
	}

	/**
	 * 策略池股票的分页查询
	 */
	public void listStrategy() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		Page<Currency> paginate = Currency.dao.paginateToBuy(qo);
		setAttr("pageResult", paginate);
		setAttr("qo", qo);
		render("listStrategy.html");
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
		String keyword = qo.getKeyword();
		List<Currency> cs = new ArrayList<Currency>();
		if (keyword != null) {
			try {
				keyword = URLEncoder.encode(keyword, "utf-8");
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
	@Before(Tx.class)
	public void add() {
//		if (!paras.containsKey("code") || !paras.containsKey("name") || !paras.containsKey("currencyType")) {
//			renderJson(new ResultMsg(ResultStatusCode.INVALID_INPUT));
//			return;
//		}
		Currency c = getBean(Currency.class, "c");
		if (c.getCode() == null || c.getName() == null || c.getCurrencyType() == null) {
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
		redirect("/currency/list");
	}

	/**
	 * 删除该股
	 */
	@Before(Tx.class)
	public void delete() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String currencyId = paras.get("currencyId");
		Db.update("delete from kline where currencyId=?", currencyId);
		Db.update("UPDATE stroke SET prevId =null,nextId=null where currencyId=?", currencyId);
		Db.update("delete from stroke where currencyId=?", currencyId);
		Db.update("UPDATE line SET prevId =null,nextId=null where currencyId=?", currencyId);
		Db.update("delete from line where currencyId=?", currencyId);
		Db.update("delete from currency where id=?", currencyId);
		redirect("/currency/list?qo.currentPage=" + qo.getCurrentPage());
	}

	/**
	 * 设置止损线
	 */
	@Before(Tx.class)
	public void cutLine() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String id = paras.get("id");
		String cutStr = paras.get("cutLine").toString();
		Double cutLine = null;
		if (!"null".equals(cutStr)) {
			cutLine = Double.valueOf(paras.get("cutLine").toString());
		}
		Record record = Db.findById("currency_strategy", id);
		record.set("cutLine", cutLine);
		Db.update("currency_strategy", record);
		redirect("/currency/listStrategy?qo.currentPage=" + qo.getCurrentPage());
	}

	/**
	 * 从策略池移除
	 */
	@Before(Tx.class)
	public void removeFromStrategy() {
		CurrencyQueryObject qo = getBean(CurrencyQueryObject.class, "qo");
		String csId = paras.get("csId");
		Record record = Db.findById("currency_strategy", csId);
		Currency currency = Currency.dao.findById(record.getInt("currencyId"));
		RemoveStrategyBh.add(currency, new Date());// 关进小黑屋
		Db.delete("currency_strategy", record);
		redirect("/currency/listStrategy?qo.currentPage=" + qo.getCurrentPage());
	}
}