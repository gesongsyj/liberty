package com.liberty.system.downloader.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.system.downloader.DownLoader;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;

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

	private String ex_url4 = "http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=6000861&token=4f1862fc3b5e77c150a2b985b12db0fd&cb=jQuery18306421890141422466_1551235703502&_=1551235703549";

	private String ex_url5 = "http://pdfm.eastmoney.com/EM_UBG_PDTI_Fast/api/js?rtntype=5&token=4f1862fc3b5e77c150a2b985b12db0fd&cb=jQuery18306421890141422466_1551235703515&id=6000861&type=r&iscr=false&_=1551235705100";

	public static void main(String[] args) {
		Date date = new Date((long) 199008983664574.0);
		System.out.println(date.toLocaleString());
	}

	@Override
	public List<Kline> downLoad(Currency currency, String type, String method, Date lastDate) {
		String url = "http://pdfm.eastmoney.com/EM_UBG_PDTI_Fast/api/js?rtntype=5&id=" + currency.getCode()
				+ currency.getCurrencyType() + "&type=" + type + "&_=" + System.currentTimeMillis();
		System.out.println(url);
		String response = HTTPUtils.http(url, null, method);
		response = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));
		Map responseMap = JSON.parseObject(response, Map.class);
		Object data = responseMap.get("data");
		List<String> dataArr = JSON.parseArray(data.toString(), String.class);
		List<Kline> klines = new ArrayList<Kline>();
		if (lastDate == null) {
			for (int i = dataArr.size() - 1; i >= 0; i--) {
				String[] str = dataArr.get(i).split(",");
				Date date = null;
				if (str[0].contains(" ")) {
					date = DateUtil.strDate(str[0], "yyyy-MM-dd HH:mm");
				} else {
					date = DateUtil.strDate(str[0], "yyyy-MM-dd");
				}
				Kline kline = new Kline();
				kline.setDate(date);
				kline.setOpen(Double.valueOf(str[1]));
				kline.setClose(Double.valueOf(str[2]));
				kline.setMax(Double.valueOf(str[3]));
				kline.setMin(Double.valueOf(str[4]));
				klines.add(kline);
			}
		} else {
			for (int i = dataArr.size() - 1; i >= 0; i--) {
				String[] str = dataArr.get(i).split(",");
				Date date = null;
				if (str[0].contains(" ")) {
					date = DateUtil.strDate(str[0], "yyyy-MM-dd HH:mm");
				} else {
					date = DateUtil.strDate(str[0], "yyyy-MM-dd");
				}
				if (date.getTime() > lastDate.getTime()) {
					Kline kline = new Kline();
					kline.setDate(date);
					kline.setOpen(Double.valueOf(str[1]));
					kline.setClose(Double.valueOf(str[2]));
					kline.setMax(Double.valueOf(str[3]));
					kline.setMin(Double.valueOf(str[4]));
					klines.add(kline);
				} else {
					break;
				}
			}
		}
		Collections.reverse(klines);
		return klines;
	}

	@Override
	public List<Kline> downLoad(Currency currency, String type, String method, int size) {
		String url = "http://pdfm.eastmoney.com/EM_UBG_PDTI_Fast/api/js?rtntype=5&id=" + currency.getCode()
				+ currency.getCurrencyType() + "&type=" + type + "&_=" + System.currentTimeMillis();
		System.out.println(url);
		String response = HTTPUtils.http(url, null, method);
		response = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));
		Map responseMap = JSON.parseObject(response, Map.class);
		Object data = responseMap.get("data");
		List<String> dataArr = JSON.parseArray(data.toString(), String.class);
		List<Kline> klines = new ArrayList<Kline>();
		for (int i = dataArr.size() - 1, j = 0; i >= 0 && j < size; i--, j++) {
			String[] str = dataArr.get(i).split(",");
			Date date = null;
			if (str[0].contains(" ")) {
				date = DateUtil.strDate(str[0], "yyyy-MM-dd HH:mm");
			} else {
				date = DateUtil.strDate(str[0], "yyyy-MM-dd");
			}
			Kline kline = new Kline();
			kline.setDate(date);
			kline.setOpen(Double.valueOf(str[1]));
			kline.setClose(Double.valueOf(str[2]));
			kline.setMax(Double.valueOf(str[3]));
			kline.setMin(Double.valueOf(str[4]));
			klines.add(kline);
		}
		Collections.reverse(klines);
		return klines;
	}

	@Override
	public void downLoadRealTimeData(Currency currency, String type, String method) {
		String url = "http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=" + currency.getCode()
				+ currency.getCurrencyType() + "&token=4f1862fc3b5e77c150a2b985b12db0fd&cb=jQuery18306421890141422466_"
				+ System.currentTimeMillis();
		url = url + "&_=" + System.currentTimeMillis();
		System.out.println(url);
		String response = HTTPUtils.http(url, null, method);
		response = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));
		Map responseMap = JSON.parseObject(response, Map.class);
		Object data = responseMap.get("Value");
		List<String> dataArr = JSON.parseArray(data.toString(), String.class);
		if(currency.getCode().equals(dataArr.get(1))) {
			currency.put("b1", dataArr.get(3));    //买一
			currency.put("b2", dataArr.get(4));    //买二
			currency.put("b3", dataArr.get(5));    //买三
			currency.put("b4", dataArr.get(6));    //买四
			currency.put("b5", dataArr.get(7));    //买五
			currency.put("s1", dataArr.get(8));    //卖一
			currency.put("s2", dataArr.get(9));    //卖二
			currency.put("s3", dataArr.get(10));   //卖三
			currency.put("s4", dataArr.get(11));   //卖四
			currency.put("s5", dataArr.get(12));   //卖五
			
			currency.put("b1n", dataArr.get(13));  //买一数量
			currency.put("b2n", dataArr.get(14));  //买二数量
			currency.put("b3n", dataArr.get(15));  //买三数量
			currency.put("b4n", dataArr.get(16));  //买四数量
			currency.put("b5n", dataArr.get(17));  //买五数量
			currency.put("s1n", dataArr.get(18));  //卖一数量
			currency.put("s2n", dataArr.get(19));  //卖二数量
			currency.put("s3n", dataArr.get(20));  //卖三数量
			currency.put("s4n", dataArr.get(21));  //卖四数量
			currency.put("s5n", dataArr.get(22));  //卖五数量
		}
	}

}
