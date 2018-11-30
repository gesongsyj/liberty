package com.liberty.system.blackHouse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.PropKit;
import com.liberty.system.model.Currency;

public class RemoveStrategyBh {
	private static Logger log = LoggerFactory.getLogger(RemoveStrategyBh.class);
	private static Map<Currency, Date> map = new ConcurrentHashMap<Currency, Date>();

	public static boolean inBlackHouse(Currency currency) {
		for (Currency c : map.keySet()) {
			if (c.getCode().equals(currency.getCode())) {
				return true;
			}
		}
		return false;
	}

	public static boolean inBlackHouse(String code) {
		for (Currency c : map.keySet()) {
			if (c.getCode().equals(code)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 手动从策略组中移除,则关进小黑屋一段时间,期间就算满足策略也不进入策略池
	 */
	public static void add(Currency currency, Date putInTime) {
		map.put(currency, putInTime);
		log.info("关进小黑屋:{}", map.toString());
	}

	/**
	 * 定时任务:满足一定时间后从小黑屋中放出来
	 */
	public static void clear() {
		Integer day = PropKit.use("jfinal.properties").getInt("removeStrategyBhTime");
		for (Currency c : map.keySet()) {
			if (System.currentTimeMillis() - map.get(c).getTime() > day * 24 * 60 * 60 * 1000) {
				map.remove(c);
			}
		}
	}
}
