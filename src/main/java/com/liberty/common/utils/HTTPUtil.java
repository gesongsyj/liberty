package com.liberty.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HTTPUtil {
	private static final CloseableHttpClient httpclient;
	public static final String CHARSET = "UTF-8";

	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(50000).setSocketTimeout(30000).build();
		httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	public static String http(String url, Map<String, Object> params, String method) {
		return http(url, params, method, null);
	}

	public static String http(String url, Map<String, Object> params, String method, Map<String, String> header) {
		if ("get".equalsIgnoreCase(method)) {
			try {
				return sendGet(url, params, header);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("post".equalsIgnoreCase(method)) {
			try {
				return sendPost(url, params, header);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String sendGet(String url, Map<String, Object> params) throws Exception {
		return sendGet(url, params, null);
	}

	public static String sendGet(String url, Map<String, Object> params, Map<String, String> header) throws Exception {

		if (params != null && !params.isEmpty()) {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());

			for (String key : params.keySet()) {
				pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs), CHARSET);
		}

		HttpGet httpGet = new HttpGet(url);
		if (header != null) {
			for (String key : header.keySet()) {
				httpGet.setHeader(key, header.get(key));
			}
		}
		CloseableHttpResponse response = httpclient.execute(httpGet);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			httpGet.abort();
			throw new RuntimeException("HttpClient,error status code :" + statusCode);
		}
		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			response.close();
			return result;
		} else {
			return null;
		}
	}

	public static String sendPost(String url, Map<String, Object> params) throws Exception {
		return sendPost(url, params, null);
	}

	public static String sendPost(String url, Map<String, Object> params, Map<String, String> header) throws Exception {

		List<NameValuePair> pairs = null;
		if (params != null && !params.isEmpty()) {
			pairs = new ArrayList<NameValuePair>(params.size());
			for (String key : params.keySet()) {
				pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
		}
		HttpPost httpPost = new HttpPost(url);
		if (header != null) {
			for (String key : header.keySet()) {
				httpPost.setHeader(key, header.get(key));
			}
		}
		if (pairs != null && pairs.size() > 0) {
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
		}
		CloseableHttpResponse response = httpclient.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			httpPost.abort();
			throw new RuntimeException("HttpClient,error status code :" + statusCode);
		}
		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			response.close();
			return result;
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		Map<String, String> header = new HashMap<>();
		header.put("Referer",
				"http://tradingview.fx168.com/tradingview/charting_library-master/index.html?symbol=FESEUR&interval=1");

		String url = "http://tradingview.fx168.com/TradingInterface/history";

		Map<String, Object> params = new HashMap<>();
		params.put("symbol", "FESEUR");
		params.put("resolution", "1");
		params.put("from", "1531618260");
		params.put("to", "1531724825");
		String http = http(url, params, "get", header);
		System.out.println(http);
	}
}