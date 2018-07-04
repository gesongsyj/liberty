package com.liberty.system.web;

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
import com.liberty.common.web.BaseController;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;

public class KlineController extends BaseController {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Long long1 = Long.valueOf("12913000");
		Long long2 = Long.valueOf("10000");
		Long long3 = long1/long2;
		System.out.println(long3);
	}

	/**
	 * 日K线数据下载并处理包含关系
	 * 
	 */
	public void downloadData() {
		Map<String, List<Kline>> klineMap=new HashMap<String, List<Kline>>();
		Map<String, Kline> lastKlineMap=new HashMap<String,Kline>();
		String response = "";
		String dataUrl = "http://webforex.hermes.hexun.com/forex/kline";
		Map<String, String> params = new HashMap<String, String>();
		List<Currency> listAll = Currency.dao.listAll();

		Date now = new Date();
		Date tomorrow = DateUtil.getNextDay(now);
		String tomorrowFormat = new SimpleDateFormat("yyyyMMdd").format(tomorrow);

		params.put("start", tomorrowFormat + "080000");
		params.put("type", "5");// 日K线

		for (Currency currency : listAll) {
			List<Kline> klineList=new ArrayList<Kline>();
			params.put("code", "FOREX" + currency.getCode());// 设置code参数

			Kline lastKline = Kline.dao.getLastByCode(currency.getCode());
			// 设置number参数
			if (lastKline == null) {
				params.put("number", "-1000");
			} else {
				lastKlineMap.put(currency.getCode(), lastKline);
				Date lastDate = lastKline.getDate();
				long daysBetween = DateUtil.getDaysBetween(now, lastDate);
				String number = String.valueOf(daysBetween);
				params.put("number", number);
			}
			try {
				response = HTTPUtils.http(dataUrl, params, "get");
				response = response.substring(response.indexOf("{"));
				response = response.substring(0, response.lastIndexOf("}") + 1);
				Map<String, Object> responseMap = JSON.parseObject(response, Map.class);
				Object object = responseMap.get("Data");
				JSONArray parseArray = JSON.parseArray(object.toString());
				Object startDate = parseArray.get(1);// 开始时间
				Object endDate = parseArray.get(2);// 结束时间
				Object priceMul = parseArray.get(4);// 价格倍数

				JSONArray dataArray = JSON.parseArray(parseArray.get(0).toString());// 数据数组
				for (Object object2 : dataArray) {
					Kline kline = new Kline();
					JSONArray parseArray2 = JSON.parseArray(object2.toString());
					kline.setDate(DateUtil.strDate(parseArray2.get(0).toString(), "yyyyMMddHHmmss"));
					kline.setMax(Double.valueOf(parseArray2.get(4).toString()) / Double.valueOf(priceMul.toString()));
					kline.setMin(Double.valueOf(parseArray2.get(5).toString()) / Double.valueOf(priceMul.toString()));
					klineList.add(kline);
				}
				
				klineMap.put(currency.getCode(), klineList);
				renderText(response);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		renderText(response);
	}

}