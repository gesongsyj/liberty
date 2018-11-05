package com.liberty.system.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.service.CurrencyKit;

public class CurrencyKit_Gp implements CurrencyKit {
	private final int pagesize = 1;// 5页股票排行数据,每页20条,共100条数据
	private final String ex_url_shanghai = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&token=4f1862fc3b5e77c150a2b985b12db0fd&sty=FCOIATC&cmd=C.2&st=(ChangePercent)&sr=-1&p=1&ps=20&_=1538047395924";
	private final String ex_url_shenzhen = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&token=4f1862fc3b5e77c150a2b985b12db0fd&sty=FCOIATC&cmd=C._SZAME&st=(ChangePercent)&sr=-1&p=1&ps=20&_=1538047395924";

	@SuppressWarnings("deprecation")
	@Override
	public void update() {
		for (int i = 1; i <= pagesize; i++) {
			String url_shanghai = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&token=4f1862fc3b5e77c150a2b985b12db0fd&sty=FCOIATC&cmd=C.2&st=(ChangePercent)&sr=-1&p="
					+ i + "&ps=20&_=" + System.currentTimeMillis();
			String url_shenzhen = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&token=4f1862fc3b5e77c150a2b985b12db0fd&sty=FCOIATC&cmd=C._SZAME&st=(ChangePercent)&sr=-1&p="
					+ i + "&ps=20&_=" + System.currentTimeMillis();
			//上证100只股票
			String res = HTTPUtils.http(url_shanghai, null, "get");
			res=res.substring(res.indexOf("["), res.lastIndexOf("]")+1);
			List<String> Strs = JSON.parseArray(res, String.class);
			for (String string : Strs) {
				String[] split = string.split(",");
				Currency currency = new Currency();
				Currency c = Currency.dao.findByCode(split[1]);
				if(c!=null){
					continue;
				}
				currency.setCode(split[1]);
				currency.setName(split[2]);
				currency.setCurrencyType("1");
				currency.save();
			}
			//深证100只股票
			res = HTTPUtils.http(url_shenzhen, null, "get");
			res=res.substring(res.indexOf("["), res.lastIndexOf("]")+1);
			Strs = JSON.parseArray(res, String.class);
			for (String string : Strs) {
				String[] split = string.split(",");
				Currency currency = new Currency();
				Currency c = Currency.dao.findByCode(split[1]);
				if(c!=null){
					continue;
				}
				currency.setCode(split[1]);
				currency.setName(split[2]);
				currency.setCurrencyType("2");
				currency.save();
			}
		}
	}

}
