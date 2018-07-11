package com.liberty.system.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.HTTPUtils;
import com.liberty.common.web.BaseController;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.model.Shape;
import com.liberty.system.model.Stroke;

public class KlineController extends BaseController {
	private static final Map<String, String> klineTypeNumberMap;
	private static final Map<String, Integer> klineTypeBetweenMap;
	static {
		klineTypeNumberMap = new HashMap<String, String>();
		klineTypeNumberMap.put("1", "-1440");// 5分钟线
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
		// Date date1 = new Date(2018, 7, 5, 5, 56, 12);
		// Date date2 = new Date(2018, 7, 3, 4, 56, 12);
		// long daysBetween = DateUtil.getNumberBetween(date2, date1, 86400000);
		// System.out.println(daysBetween);

		List<Shape> shapes = new ArrayList<>();
		shapes.add(new Shape().setMax(100.0));
		Shape last = shapes.get(shapes.size() - 1);
		System.out.println(last.equals(shapes.get(shapes.size() - 1)));
		last = new Shape().setMax(99.0);

		System.err.println(last.getMax());
		System.err.println(shapes.get(shapes.size() - 1).getMax());
		System.out.println(last.equals(shapes.get(shapes.size() - 1)));
	}

	/**
	 * K线数据下载
	 * 
	 */
	public void downloadData() {
		String response = "";
		String dataUrl = "http://webforex.hermes.hexun.com/forex/kline";
		Map<String, String> params = new HashMap<String, String>();
		List<Currency> listAll = Currency.dao.listAll();

		Date now = new Date();
		Date tomorrow = DateUtil.getNextDay(now);
		String tomorrowFormat = new SimpleDateFormat("yyyyMMdd").format(tomorrow);

		params.put("start", tomorrowFormat + "080000");
		String sql = "select * from dictionary where type='klineType'";
		List<Record> klineTyep = Db.find(sql);
		for (Record record : klineTyep) {
			//================测试
			if(!record.getStr("key").equals("1")){
				continue;
			}
			//================
			Map<String, List<Kline>> klineMap = new HashMap<String, List<Kline>>();
			Map<String, Kline> lastKlineMap = new HashMap<String, Kline>();
			params.put("type", record.getStr("key"));// K线级别

			for (Currency currency : listAll) {
				//===========测试
				if(!currency.getCode().equals("EURUSD")){
					continue;
				}
				//===========
				List<Kline> klineList = new ArrayList<Kline>();
				params.put("code", "FOREX" + currency.getCode());// 设置code参数
				// 取出最后两条数据,最新的一条数据可能随时变化,新增数据时此条记录先删除
				List<Kline> lastTwo = Kline.dao.getLastByCode(currency.getCode(), record.getStr("key"));
				// 设置number参数
				if (lastTwo == null || lastTwo.size() == 0) {
					params.put("number", klineTypeNumberMap.get(record.getStr("key")) == null ? "-1000"
							: klineTypeNumberMap.get(record.getStr("key")));
				} else {
					lastTwo.get(0).delete();
					lastKlineMap.put(currency.getCode(), lastTwo.get(1));
					Date lastDate = lastTwo.get(1).getDate();
					long between = DateUtil.getNumberBetween(DateUtil.getNextDay(now), lastDate,
							klineTypeBetweenMap.get(record.getStr("key")));
					String number = String.valueOf(between);
					params.put("number", "-" + number);
					// params.put("number", "-" + "10");//测试
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
						kline.setMax(
								Double.valueOf(parseArray2.get(4).toString()) / Double.valueOf(priceMul.toString()));
						kline.setMin(
								Double.valueOf(parseArray2.get(5).toString()) / Double.valueOf(priceMul.toString()));
						kline.setCurrencyId(currency.getId());
						kline.setType(record.getStr("key"));

						klineList.add(kline);
					}

					klineMap.put(currency.getCode(), klineList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Kline.dao.saveMany(klineMap, lastKlineMap);
		}
		renderText(response);
	}

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
			List<Kline> klines = Kline.dao.listAllByCode("EURUSD", "1");
			// 处理K线的包含关系
			List<Kline> handleInclude = handleInclude(klines, lastStroke);
			// 生成笔
			List<Stroke> strokes = processStroke(handleInclude, lastStroke);
		} else {
			// 查询最后一笔之后的K线
			Date date = lastStroke.getEndDate();
			List<Kline> klines = Kline.dao.getListByDate("EURUSD", "1", date);
			// 处理K线的包含关系
			List<Kline> handleInclude = handleInclude(klines, lastStroke);
			// 生成笔
			List<Stroke> strokes = processStroke(handleInclude, lastStroke);
		}
		renderText("ok");
	}

}