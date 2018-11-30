package com.liberty.system.downloader.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.system.downloader.CurrencyKit;
import com.liberty.system.model.Currency;

public class CurrencyKit_Wh implements CurrencyKit{

	@Override
	public List<Currency> update() {
		ArrayList<Currency> cs = new ArrayList<Currency>();
		String response = "";
		String currencyUrl = "http://forex.wiapi.hexun.com/forex/sortlist";
		Map<String, String> params = new HashMap<String, String>();
		params.put("block", "302");
		params.put("number", "1000");
		params.put("title", "15");
		params.put("commodityid", "0");
		params.put("direction", "0");
		params.put("start", "0");
		params.put("column", "code,name");
		try {
			response = HTTPUtils.http(currencyUrl, params, "get");
			response = response.substring(response.indexOf("{"));
			response = response.substring(0, response.lastIndexOf("}") + 1);
			Map<String,Object> responseMap = JSON.parseObject(response, Map.class);
			Object object = responseMap.get("Data");
			JSONArray parseArray =JSON.parseArray(JSON.parseArray(object.toString()).get(0).toString());
			for (Object object2 : parseArray) {
				JSONArray parseArray2 = JSON.parseArray(object2.toString());
				
				Currency currency = Currency.dao.findByCode(parseArray2.get(0).toString());
				if(currency==null){
					currency=new Currency();
					currency.setCode(parseArray2.get(0).toString());
					currency.setName(parseArray2.get(1).toString());
					cs.add(currency);
					currency.save();
					
				}/*else{
					currency.setCode(parseArray2.get(0).toString());
					currency.setName(parseArray2.get(1).toString());
					currency.update();
				}*/
			}
//			System.out.println(parseArray.toString());
//			renderText(parseArray.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cs;
	}

}
