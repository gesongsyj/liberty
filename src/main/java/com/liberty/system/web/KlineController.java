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
		List<Integer> a=new ArrayList<>();
		a.add(1);
		a.add(2);
		a.add(3);
		a.add(4);
		List<Integer> b=new ArrayList<>();
		b.add(1);
		b.add(2);
		b.add(3);
		a.removeAll(b);
		System.err.println(a);
	}

	/**
	 * K线数据下载
	 * 
	 */
	@Before(Tx.class)
	public void downloadData() {
//		DownLoader downLoader = new XlDownLoader();
		DownLoader downLoader = new DfcfDownLoader();
		List<Currency> listAll = Currency.dao.listAll();

		String sql = "select * from dictionary where type='klineType_gp'";
		List<Record> klineType = Db.find(sql);
		Map<String, List<Kline>> klineMap = new HashMap<String, List<Kline>>();
		Map<String, Kline> lastKlineMap = new HashMap<String, Kline>();
		for (Record record : klineType) {
			// ================测试
			if (!record.getStr("key").equals("k")) {
				continue;
			}
			// ================

			for (Currency currency : listAll) {
				// ===========测试,只取eurusd
//				if (!currency.getCode().equals("600721")) {
//					continue;
//				}
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
			}
		}
		Kline.dao.saveMany(klineMap, lastKlineMap);
		renderText("ok");
	}

	@Before(Tx.class)
	public void createStroke() {
		// List<Kline> klines=new ArrayList<>();
		// klines.add(new Kline().setMax(100.00).setMin(10.0));
		// klines.add(new Kline().setMax(99.00).setMin(11.0));
		// klines.add(new Kline().setMax(98.00).setMin(12.0));
		// klines.add(new Kline().setMax(101.00).setMin(20.0));
		// klines.add(new Kline().setMax(102.00).setMin(30.0));
		// klines.add(new Kline().setMax(103.00).setMin(1.0));
		// klines.add(new Kline().setMax(102.00).setMin(20.0));
		// klines.add(new Kline().setMax(101.00).setMin(10.0));
		// klines.add(new Kline().setMax(103.00).setMin(9.0));
		// List<Kline> handleInclude = handleInclude(klines, null);

		Stroke lastStroke = Stroke.dao.getLast();

		if (lastStroke == null) {
			// 查询所有的K线
			List<Kline> klines = Kline.dao.listAllByCode("600721", "k");
			// 处理K线的包含关系
			List<Kline> handleInclude = handleInclude(klines, lastStroke);
			// 生成笔
			List<Stroke> strokes = processStrokes(handleInclude, lastStroke);
		} else {
			// 查询最后一笔之后的K线
			Date date = lastStroke.getEndDate();
			List<Kline> klines = Kline.dao.getListByDate("600721", "k", date);
			// 处理K线的包含关系
			List<Kline> handleInclude = handleInclude(klines, lastStroke);
			// 生成笔
			List<Stroke> strokes = processStrokes(handleInclude, lastStroke);
		}
		renderText("ok");
	}

	@Before(Tx.class)
	public void createLine() {
		Line lastLine = Line.dao.getLast();
		List<Line> storeLines=null;//生成的线段
		List<Stroke> strokes=null;
		if (lastLine == null) {
			// 查询所有的笔
			strokes = Stroke.dao.listAllByCode("600721", "k");
			int size=strokes.size();
			for (int i = 0; i < strokes.size() - 2; i++) {
				if ("0".equals(strokes.get(i).getDirection())) {
					if (strokes.get(i).getMax() < strokes.get(i + 2).getMax()
							&& strokes.get(i).getMin() < strokes.get(i + 2).getMin()) {
						strokes.remove(i);
						i--;
						size=strokes.size();
						continue;
					}
				}
				if ("1".equals(strokes.get(i).getDirection())) {
					if (strokes.get(i).getMax() > strokes.get(i + 2).getMax()
							&& strokes.get(i).getMin() > strokes.get(i + 2).getMin()) {
						strokes.remove(i);
						i--;
						size=strokes.size();
						continue;
					}
				}
				break;
			}
		} else {
			// 查询最后一条线段后的笔
			Date date = lastLine.getEndDate();
			strokes = Stroke.dao.getListByDate("600721", "k", date);
		}
		loopProcessLines(strokes, lastLine);
	}

}