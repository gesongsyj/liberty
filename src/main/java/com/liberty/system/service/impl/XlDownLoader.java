package com.liberty.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.system.model.Kline;
import com.liberty.system.service.DownLoader;

public class XlDownLoader implements DownLoader {
	private static final Map<String, String> klineTypeNumberMap;
	private static final Map<String, String> paramTypeMap;
	private static final Map<String, Integer> klineTypeBetweenMap;
	static {
		paramTypeMap = new HashMap<String, String>();
		paramTypeMap.put("1", "1");// 1分钟线
		paramTypeMap.put("2", "5");// 5分钟线
		paramTypeMap.put("3", "15");// 15分钟线
		paramTypeMap.put("4", "30");// 30分钟线
		paramTypeMap.put("5", "60");// 60分钟线
		paramTypeMap.put("6", "240");// 4H线

		klineTypeNumberMap = new HashMap<String, String>();
		klineTypeNumberMap.put("1", "1440");// 1分钟线
		klineTypeNumberMap.put("2", "1440");// 5分钟线
		klineTypeNumberMap.put("3", "480");// 15分钟线
		klineTypeNumberMap.put("4", "240");// 30分钟线
		klineTypeNumberMap.put("5", "120");// 60分钟线
		klineTypeNumberMap.put("6", "60");// 4H线

		klineTypeBetweenMap = new HashMap<String, Integer>();
		klineTypeBetweenMap.put("1", 60 * 1000);
		klineTypeBetweenMap.put("2", 5 * 60 * 1000);
		klineTypeBetweenMap.put("3", 15 * 60 * 1000);
		klineTypeBetweenMap.put("4", 30 * 60 * 1000);
		klineTypeBetweenMap.put("5", 60 * 60 * 1000);
		klineTypeBetweenMap.put("6", 240 * 60 * 1000);
		klineTypeBetweenMap.put("7", 24 * 60 * 60 * 1000);
	}

	@Override
	public List<Kline> downLoad(String code, String type, String method, Kline lastKline) {
		Map<String, String> params = new HashMap<String, String>();
		List<Kline> klineList = new ArrayList<Kline>();
		String response = "";
		String url = "";
		Date now = new Date();
		if ("7".equals(type)) {// 日线
			url = "https://ex.sina.com.cn/forex/api/jsonp.php/var_fx_s" + code.toLowerCase()
					+ "2018_7_17=/NewForexService.getDayKLine";
			params.put("symbol", "fx_s" + code.toLowerCase());
			params.put("_", new SimpleDateFormat("yyyy_MM_dd").format(now));
			try {
				response = HTTPUtils.http(url, params, "get");
				response = response.substring(response.indexOf("String(\"") + 8);
				response = response.substring(0, response.lastIndexOf("\"") + 1);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			String[] dataArray = response.split("\\|");
			for (String data : dataArray) {
				String[] perData = data.split(",");
				Kline kline = new Kline();
				kline.setDate(DateUtil.strDate(perData[0], "yyyy-MM-dd"));
				kline.setMax(Double.valueOf(perData[3]));
				kline.setMin(Double.valueOf(perData[2]));
				klineList.add(kline);
			}

		} else {// 分钟线
			if (paramTypeMap.get(type) == null) {
				return null;// 没有该级别K线的数据
			}
			url = "https://ex.sina.com.cn/forex/api/jsonp.php/var_fx_s" + code.toLowerCase() + "_" + type + "_"
					+ now.getTime() + "=/NewForexService.getMinKline";
			params.put("symbol", "fx_s" + code.toLowerCase());
			params.put("scale", paramTypeMap.get(type));
			if (lastKline == null) {
				params.put("datalen", klineTypeNumberMap.get(type) == null ? "1000" : klineTypeNumberMap.get(type));
			} else {
				Date lastDate = lastKline.getDate();
				long between = DateUtil.getNumberBetween(DateUtil.getNextDay(now), lastDate,
						klineTypeBetweenMap.get(type));
				String number = String.valueOf(between);
				params.put("datalen", number);
			}
			try {
				response = HTTPUtils.http(url, params, "get");
				response = response.substring(response.indexOf("["));
				response = response.substring(0, response.lastIndexOf("]") + 1);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			JSONArray parseArray = JSON.parseArray(response);
			for (Object object : parseArray) {
				Map map = JSON.parseObject(object.toString(), Map.class);
				Kline kline = new Kline();
				kline.setMax(Double.valueOf(map.get("h").toString()));
				kline.setMin(Double.valueOf(map.get("l").toString()));
				kline.setDate(DateUtil.strDate(map.get("d").toString(), "yyyy-MM-dd HH:mm:ss"));
				klineList.add(kline);
			}
		}

		return klineList;
	}

}
