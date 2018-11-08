package com.liberty.system.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.common.utils.ResultMsg;
import com.liberty.common.utils.ResultStatusCode;
import com.liberty.common.web.BaseController;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.model.Line;
import com.liberty.system.model.Shape;
import com.liberty.system.model.Stroke;
import com.liberty.system.service.DownLoader;
import com.liberty.system.service.impl.HxDownLoader;
import com.liberty.system.service.impl.DfcfDownLoader;
import com.liberty.system.service.impl.XlDownLoader;

public class KlineController extends BaseController {
	private static final Map<String, String> klineTypeNumberMap;
	private static final Map<String, Integer> klineTypeBetweenMap;
	static {
		klineTypeNumberMap = new HashMap<String, String>();
		klineTypeNumberMap.put("1", "-200000");// 5分钟线
		// klineTypeNumberMap.put("1", "-1440");// 5分钟线
		klineTypeNumberMap.put("2", "-960");// 15分钟线
		klineTypeNumberMap.put("3", "-960");// 30分钟线
		klineTypeNumberMap.put("4", "-720");// 60分钟线
		klineTypeNumberMap.put("5", "-1000");// 日线
		klineTypeNumberMap.put("6", "-520");// 周线
		klineTypeNumberMap.put("9", "-120");// 月线

		klineTypeBetweenMap = new HashMap<String, Integer>();
		klineTypeBetweenMap.put("1", 5 * 60 * 1000);
		klineTypeBetweenMap.put("2", 15 * 60 * 1000);
		klineTypeBetweenMap.put("3", 30 * 60 * 1000);
		klineTypeBetweenMap.put("4", 60 * 60 * 1000);
		klineTypeBetweenMap.put("5", 24 * 60 * 60 * 1000);
		klineTypeBetweenMap.put("6", 7 * 24 * 60 * 60 * 1000);
		klineTypeBetweenMap.put("9", 31 * 24 * 60 * 60 * 1000);
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		List<List<Integer>> a=new ArrayList<>();
		List<Integer> b=new ArrayList<>();
		b.add(1);
		b.add(2);
		b.add(3);
		a.add(b);
		a.add(b);
		System.err.println(a);
	}

	/**
	 * 更新该股的k线,笔,线段数据
	 */
	public void updateData() {
		String code = paras.get("code");
		KlineController klineController = new KlineController();
		klineController.downloadData(code);
		klineController.createStroke(code);
		klineController.createLine(code);
		setAttr("code", code);
		render("kline.html");
	}
	
	/**
	 * 查看k线,笔,线段图表
	 */
	public void charts() {
		String currencyId = paras.get("currencyId");
		if(currencyId==null) {
			renderJson(new ResultMsg(ResultStatusCode.INVALID_INPUT));
			return;
		}
		Currency currency = Currency.dao.findById(currencyId);
		setAttr("code", currency.getCode());
		render("kline.html");
	}
	
	/**
	 * 获取图表数据
	 */
	public void fetchData() {
		String code = paras.get("code");
		String kType=paras.containsKey("kType")?paras.get("kType"):"k";
		if(code==null) {
			renderJson(new ResultMsg(ResultStatusCode.INVALID_INPUT));
			return;
		}
		Currency currency =Currency.dao.findByCode(code);
		List<Kline> allKlines=Kline.dao.listAllByCode(code, kType);
		List<Stroke> allStrokes=Stroke.dao.listAllByCode(code, kType);
		List<Line> allLines=Line.dao.listAllByCode(code, kType);
		
		List<List<Object>> klines=new ArrayList<List<Object>>();
		for (int i = 0; i < allKlines.size(); i++) {
			List<Object> klineData=new ArrayList<Object>();
			klineData.add(allKlines.get(i).getDate());
			klineData.add(allKlines.get(i).getOpen());
			klineData.add(allKlines.get(i).getClose());
			klineData.add(allKlines.get(i).getMin());
			klineData.add(allKlines.get(i).getMax());
			klineData.add(allKlines.get(i).getDiff());//index:5
			klineData.add(allKlines.get(i).getDea());
			klineData.add(allKlines.get(i).getBar());
			klines.add(klineData);
		}
		
		List<List<Object>> strokes=new ArrayList<List<Object>>();
		for (int i = 0; i < allStrokes.size(); i++) {
			List<Object> strokeNums= new ArrayList<Object>();
			strokeNums.add(allStrokes.get(i).getStartDate());
			if("0".equals(allStrokes.get(i).getDirection())) {
				strokeNums.add(allStrokes.get(i).getMin());
			}else {
				strokeNums.add(allStrokes.get(i).getMax());
			}
			strokes.add(strokeNums);
			if(i==allStrokes.size()-1) {
				List<Object> strokeNums2= new ArrayList<Object>();
				strokeNums2.add(allStrokes.get(i).getEndDate());
				if("0".equals(allStrokes.get(i).getDirection())) {
					strokeNums2.add(allStrokes.get(i).getMax());
				}else {
					strokeNums2.add(allStrokes.get(i).getMin());
				}
				strokes.add(strokeNums2);
			}
		}
		
		List<List<Map<String, Object>>> lines=new ArrayList<List<Map<String, Object>>>();
		List<List<Object>> lineStrokes=new ArrayList<List<Object>>();
		for (int i = 0; i < allLines.size(); i++) {
			List<Map<String, Object>> perLine=new ArrayList<Map<String, Object>>();
			Map<String, Object> start=new HashMap<String, Object>();
			List<Object> startNum=new ArrayList<Object>();
			Map<String, Object> end=new HashMap<String, Object>();
			List<Object> endNum=new ArrayList<Object>();
			startNum.add(DateUtil.dateStr(allLines.get(i).getStartDate(), "yyyy-MM-dd HH:mm:ss"));
			if("0".equals(allLines.get(i).getDirection())) {
				startNum.add(allLines.get(i).getMin());
			}else {
				startNum.add(allLines.get(i).getMax());
			}
			start.put("coord", startNum);
			
			endNum.add(DateUtil.dateStr(allLines.get(i).getEndDate(), "yyyy-MM-dd HH:mm:ss"));
			if("0".equals(allLines.get(i).getDirection())) {
				endNum.add(allLines.get(i).getMax());
			}else {
				endNum.add(allLines.get(i).getMin());
			}
			end.put("coord", endNum);
			perLine.add(start);
			perLine.add(end);
			lines.add(perLine);
			
			List<Object> strokeLineNums= new ArrayList<Object>();
			strokeLineNums.add(allLines.get(i).getStartDate());
			if("0".equals(allLines.get(i).getDirection())) {
				strokeLineNums.add(allLines.get(i).getMin());
			}else {
				strokeLineNums.add(allLines.get(i).getMax());
			}
			if(i==allLines.size()-1) {
				strokeLineNums.add(allLines.get(i).getEndDate());
				if("0".equals(allLines.get(i).getDirection())) {
					strokeLineNums.add(allLines.get(i).getMax());
				}else {
					strokeLineNums.add(allLines.get(i).getMin());
				}
			}
			lineStrokes.add(strokeLineNums);
		}
		
		Map<String, Object> resultMap=new HashMap<String, Object>();
		resultMap.put("currency", currency);
		resultMap.put("klines", klines);
		resultMap.put("strokes", strokes);
		resultMap.put("lines", lines);
		resultMap.put("lineStrokes", lineStrokes);
		renderJson(new ResultMsg(ResultStatusCode.OK,resultMap));
	}
	
	/**
	 * K线数据下载
	 * 财经网站爬取数据
	 */
	@Before(Tx.class)
	public void downloadData(String includeCurrencyCode) {
//		DownLoader downLoader = new XlDownLoader();
		DownLoader downLoader = new DfcfDownLoader();
		List<Currency> listAll = Currency.dao.listAll();

		String sql = "select * from dictionary where type='klineType_gp'";
		List<Record> klineType = Db.find(sql);
		Map<String, List<Kline>> klineMap = new HashMap<String, List<Kline>>();
		Map<String, Kline> lastKlineMap = new HashMap<String, Kline>();
		for (Record record : klineType) {
			// ================测试
//			if (!record.getStr("key").equals("k")) {
//				continue;
//			}
			// ================

			for (Currency currency : listAll) {
				if (includeCurrencyCode !=null && !currency.getCode().equals(includeCurrencyCode)) {
					continue;
				}
				// ===========
				// 取出最后两条数据,最新的一条数据可能随时变化,新增数据时此条记录先删除
				List<Kline> lastTwo = Kline.dao.getLastByCode(currency.getCode(), record.getStr("key"));

				Kline lastKline = null;
				if (lastTwo == null || lastTwo.size() <= 1) {
					if (lastTwo.size() == 1) {
						lastTwo.get(0).delete();
					}
				} else {
					lastTwo.get(0).delete();
					lastKline = lastTwo.get(1);
					lastKlineMap.put(currency.getCode() + "_" + record.getStr("key"), lastKline);
				}
				List<Kline> klineList = null;
				klineList = downLoader.downLoad(currency, record.getStr("key"), "get", lastKline);
				if (klineList == null || klineList.size() == 0) {
					continue;
				}
				for (Kline kline : klineList) {
					kline.setCurrencyId(currency.getId());
					kline.setType(record.getStr("key"));
				}

				klineMap.put(currency.getCode() + "_" + record.getStr("key"), klineList);
				Kline.dao.saveMany(klineMap, lastKlineMap);
				klineMap.clear();
				lastKlineMap.clear();
			}
		}
		renderText("ok");
	}

	/**
	 * 根据k线数据生成笔
	 * @param includeCurrencyCode
	 */
	@Before(Tx.class)
	public void createStroke(String includeCurrencyCode) {
		List<Currency> listAll = Currency.dao.listAll();
		String sql = "select * from dictionary where type='klineType_gp'";
		List<Record> klineType = Db.find(sql);
		for (Record record : klineType) {
			// ================测试
//			if (!record.getStr("key").equals("k")) {
//				continue;
//			}
			System.out.println(record);
			for (Currency currency : listAll) {
				if (includeCurrencyCode !=null && !currency.getCode().equals(includeCurrencyCode)) {
					continue;
				}
				Stroke lastStroke = Stroke.dao.getLastByCode(currency.getCode(),record.getStr("key"));
				
				if (lastStroke == null) {
					// 查询所有的K线
					List<Kline> klines = Kline.dao.listAllByCode(currency.getCode(), record.getStr("key"));
					if(klines==null || klines.size()==0) {
						continue;
					}
					// 处理K线的包含关系
					List<Kline> handleInclude = handleInclude(klines, lastStroke);
					// 生成笔
					List<Stroke> strokes = processStrokes(handleInclude, lastStroke);
				} else {
					// 查询最后一笔之后的K线
					Date date = lastStroke.getEndDate();
					List<Kline> klines = Kline.dao.getListByDate(currency.getCode(), record.getStr("key"), date);
					if(klines==null|| klines.size()==0) {
						continue;
					}
					// 处理K线的包含关系
					List<Kline> handleInclude = handleInclude(klines, lastStroke);
					// 生成笔
					List<Stroke> strokes = processStrokes(handleInclude, lastStroke);
				}
			}
		}
		
		renderText("ok");
	}

	/**
	 * 根据笔数据生成线段
	 * @param includeCurrencyCode
	 */
	@Before(Tx.class)
	public void createLine(String includeCurrencyCode) {
		List<Stroke> strokes=null;
		
		List<Currency> listAll = Currency.dao.listAll();
		String sql = "select * from dictionary where type='klineType_gp'";
		List<Record> klineType = Db.find(sql);
		for (Record record : klineType) {
			// ================测试
//			if (!record.getStr("key").equals("k")) {
//				continue;
//			}
			for (Currency currency : listAll) {
				if (includeCurrencyCode !=null && !currency.getCode().equals(includeCurrencyCode)) {
					continue;
				}
				List<Line> storeLines=new ArrayList<Line>();//生成的线段
				Line lastLine = Line.dao.getLastByCode(currency.getCode(),record.getStr("key"));
				
				if (lastLine == null) {
					// 查询所有的笔
					strokes = Stroke.dao.listAllByCode(currency.getCode(), record.getStr("key"));
					if(strokes==null|| strokes.size()==0) {
						continue;
					}
				} else {
					storeLines.add(lastLine);
					// 查询最后一条线段后的笔
					Date date = lastLine.getEndDate();
					strokes = Stroke.dao.getListByDate(currency.getCode(), record.getStr("key"), date);
					if(strokes==null|| strokes.size()==0) {
						continue;
					}
				}
				if(strokes.size()>=3) {
					loopProcessLines3(strokes,storeLines);
				}
			}
		}
		
	}

}