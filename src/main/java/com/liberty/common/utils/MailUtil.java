package com.liberty.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jfinal.kit.PropKit;
import com.jfplugin.mail.MailKit;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Strategy;

public class MailUtil {
	/**
	 * 满足策略时发送邮件
	 * 
	 * @param cs
	 * @param s
	 */
	public synchronized static void sendMailToBuy(Vector<Currency> cs, Strategy s) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < cs.size(); i++) {
			sb.append(cs.get(i).getCode()).append(":").append(cs.get(i).getName());
			if (i != cs.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("等股票满足策略:");
		sb.append(s.getDescribe());
		List<String> cc = new ArrayList<>();
		// 第二个参数是抄送对象
		MailKit.send("530256489@qq.com", null, "买入提醒!", sb.toString());
	}

	/**
	 * 跌破止损线时发送邮件
	 * 
	 * @param cs
	 * @param s
	 */
	public synchronized static void sendMailToSale(Currency currency, Strategy s, double cutLine, int csId) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(currency.getCode()).append(":").append(currency.getName());
		sb.append("]");
		sb.append("在策略:[");
		sb.append(s.getDescribe());
		sb.append("]下失败,跌破止损线:[");
		sb.append(cutLine);
		sb.append("].请及时卖出!");
		sb.append("如已知悉,点击链接清除止损线:");
		sb.append(PropKit.use("jfinal.properties").get("serverUrl"));
		sb.append("currency/cutLine?id=");
		sb.append(csId);
		sb.append("&cutLine=null");
		// 第二个参数是抄送对象
		MailKit.send("530256489@qq.com", null, "止损提醒!", sb.toString());
	}
}
