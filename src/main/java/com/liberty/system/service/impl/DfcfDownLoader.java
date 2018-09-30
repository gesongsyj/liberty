package com.liberty.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.service.DownLoader;

/**
 * 东方财富股票价格下载
 * 
 * @author Administrator
 *
 */
public class DfcfDownLoader implements DownLoader {
	// 日K
	private String ex_url3 = "http://pdfm.eastmoney.com/EM_UBG_PDTI_Fast/api/js?rtntype=5&id=" + "002113"
			+ "2&type=k&_=1538045294612";
	// 5分钟
	private String ex_url2 = "http://pdfm.eastmoney.com/EM_UBG_PDTI_Fast/api/js?rtntype=5&token=4f1862fc3b5e77c150a2b985b12db0fd&cb=jQuery183039199008983664574_1538044367388&id="
			+ "002113" + "2&type=" + "m5k&authorityType=&_=1538044665578";

	public static void main(String[] args) {
		Date date = new Date((long) 199008983664574.0);
		System.out.println(date.toLocaleString());
	}

	@Override
	public List<Kline> downLoad(Currency currency, String type, String method, Kline lastKline) {
		String url = "http://pdfm.eastmoney.com/EM_UBG_PDTI_Fast/api/js?rtntype=5&id=" + currency.getCode()
				+ currency.getCurrencyType() + "&type=" + type + "&_=" + System.currentTimeMillis();
		String response = HTTPUtils.http(url, null, method);
		response = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));
		Map responseMap = JSON.parseObject(response, Map.class);
		System.out.println(url);
		Object data = responseMap.get("data");
		List<String> dataArr = JSON.parseArray(data.toString(), String.class);
		List<Kline> klines = new ArrayList<Kline>();
		for (String s : dataArr) {
			String[] str = s.split(",");
			Date date = DateUtil.strDate(str[0], "yyyy-MM-dd");
			Kline kline = new Kline();
			kline.setDate(date);
			kline.setOpen(Double.valueOf(str[1]));
			kline.setClose(Double.valueOf(str[2]));
			kline.setMax(Double.valueOf(str[3]));
			kline.setMin(Double.valueOf(str[4]));
			klines.add(kline);
		}
		return klines;
	}

}
