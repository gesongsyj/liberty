package com.liberty.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.system.model.Kline;
import com.liberty.system.service.DownLoader;

/**
 * 同花顺股票价格下载
 * 
 * @author Administrator
 *
 */
public class ThsDownLoader implements DownLoader {
	private String url_prefix = "http://d.10jqka.com.cn/v6/line/hs_";
	private String url_suffix = "/01/all.js";

	@Override
	public List<Kline> downLoad(String code, String type, String method, Kline lastKline) {
		String url = url_prefix + code + url_suffix;
		String response = HTTPUtils.http(url, null, "get");
		response=response.substring(response.indexOf("("+1),response.lastIndexOf(")"));
		Map<String,String> responseMap = JSON.parseObject(response, Map.class);
		String stockName=responseMap.get("name");
		Date startDate = DateUtil.strDate(responseMap.get("start"), "yyyyMMdd");
		String dates_day = responseMap.get("dates");
		String price = responseMap.get("price");
		int priceFactor=Integer.valueOf(responseMap.get("priceFactor"));
		
//		List<Date> dates=new ArrayList<Date>();
		List<Kline> klines=new ArrayList<Kline>();
		String[] dates_days = dates_day.split(",");
		String[] prices=price.split(",");
		int year=startDate.getYear();
		for (int i = 0; i < dates_days.length; i++) {
			Date date = DateUtil.strDate(year+dates_days[i], "yyyyMMdd");
			if(i!=0){
				if(date.before(klines.get(i).getDate())){
					year++;
					date = DateUtil.strDate(year+dates_days[i], "yyyyMMdd");
				}
			}
			
			Kline kline = new Kline();
			kline.setDate(date);
			kline.setMin(Double.valueOf(prices[i*4])/priceFactor);
			kline.setOpen((Double.valueOf(prices[i*4])+Double.valueOf(prices[i*4+1]))/priceFactor);
			kline.setMax((Double.valueOf(prices[i*4])+Double.valueOf(prices[i*4+2]))/priceFactor);
			kline.setClose((Double.valueOf(prices[i*4])+Double.valueOf(prices[i*4+3]))/priceFactor);
		}
		
		return klines;
	}

}
