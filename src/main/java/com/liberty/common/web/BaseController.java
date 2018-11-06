package com.liberty.common.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.liberty.common.annotation.DataVerAnnotation;
import com.liberty.common.annotation.LogAnnotation;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.Encodes;
import com.liberty.common.utils.IpKit;
import com.liberty.common.utils.JsonToMap;
import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;
import com.liberty.system.model.Line;
import com.liberty.system.model.Log;
import com.liberty.system.model.Shape;
import com.liberty.system.model.Stroke;

import io.jsonwebtoken.Claims;
import sun.tools.jconsole.OutputViewer;

public class BaseController extends Controller {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Map<String, String> paras = new HashMap<String, String>();
	protected Map<String, Object> map = new HashMap<String, Object>();

	public String getAccount() {
		try {
			Subject subject = SecurityUtils.getSubject();
			return subject.getPrincipal().toString();
		} catch (Exception e) {
			// logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * token信息注入
	 * 
	 * @param claims
	 */
	public void injectClaims(Claims claims) {
		this.paras.put("account_username", claims.get("account_username").toString());
	}

	/**
	 * @Description: 参数注入
	 */
	public void injectParas() {
		// 处理GET参数
		paras.clear();
		Enumeration<String> paraNames = getParaNames();
		while (paraNames.hasMoreElements()) {
			String paraName = paraNames.nextElement();
			if (!"".equals(getPara(paraName)))
				paras.put(paraName, getPara(paraName));
		}

		// 处理POST参数
		map.clear();
		UploadFile file;
		String contentType = getRequest().getContentType(); // 获取Content-Type
		if ((contentType != null) && (contentType.toLowerCase().startsWith("multipart/"))) {
			file = getFile(); // 先读文件
			if (file != null) {
				map.put("file", file);
			}

			// 再取其他参数
			Enumeration<String> data = getParaNames();
			while (data.hasMoreElements()) {
				String paraName = data.nextElement();
				if (getParaValues(paraName).length > 1) {
					map.put(paraName, getParaValues(paraName));
				} else {
					if (!"".equals(getPara(paraName)))
						map.put(paraName, getPara(paraName));
				}
			}
		} else {
			try {
				String postJson = HttpKit.readData(getRequest());
				map = JsonToMap.toMap(postJson);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	/**
	 * 处理自定义注解
	 * 
	 * @param method
	 */
	public void handleAnnotation(Method method) {
		// 扫描数据版本注解
		if (method.isAnnotationPresent(DataVerAnnotation.class)) {
		}

		// 扫描操作日志注解
		if (method.isAnnotationPresent(LogAnnotation.class)) {
			LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
			// 解析操作日志
			String operateDescribe = logAnnotation.operateDescribe();
			// 获取被注解方法的参数，实现动态注解
			List<String> logArg = getArgs(operateDescribe, "%");
			for (String string : logArg) {
				Object value = "";
				if (paras.containsKey(string)) {
					value = paras.get(string);
				} else {
					if (map.containsKey(string)) {
						value = map.get(string);
					}
				}
				operateDescribe = operateDescribe.replace("%" + string + "%", value.toString());
			}

			// 以下是数据库操作
			new Log().set("log_ip", IpKit.getRealIp(getRequest())).set("log_time", DateUtil.getDate())
					.set("account_username", getAccount()).set("operateModelNm", logAnnotation.operateModelNm())
					.set("operateFuncNm", logAnnotation.operateFuncNm()).set("operateDescribe", operateDescribe).save();
		}
	}

	/**
	 * 获取字符串中满足特定分隔符的子串
	 * 
	 * @param source
	 * @param separator
	 * @return
	 */
	private List<String> getArgs(String source, String separator) {
		List<String> argList = new ArrayList<String>();
		String temp = source;
		while (temp.indexOf(separator) >= 0) {
			int beginIndex = temp.indexOf(separator);
			temp = temp.substring(beginIndex + 1);

			int endIndex = temp.indexOf(separator);
			argList.add(temp.substring(0, endIndex));

			temp = temp.substring(endIndex + 1);
		}
		return argList;
	}

	/**
	 * 判断是否拥有角色
	 */
	public boolean hasRole(String roleIdentifier) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.hasRole(roleIdentifier);
	}

	/**
	 * 判断是否拥有全部角色
	 */
	public boolean hasAllRoles(List<String> roleIdentifiers) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.hasAllRoles(roleIdentifiers);
	}

	public static boolean getPic(String base64, String path) {
		OutputStream out = null;
		if (base64 != null) {
			byte[] b = Encodes.decodeBase64(base64);
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			try {
				out = new FileOutputStream(path);
				out.write(b);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			return false;
		}
		return false;
	}

	protected Date getDate(String param) {
		if (param != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				return sdf.parse(param);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected Date getTime(String param) {
		if (param != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.parse(param);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected Date getDay(Date date) {
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				return sdf.parse(sdf.format(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getStringDay(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		}
		return null;
	}

	/**
	 * 计算当前时间前一天
	 */
	public Date getBeforeDay(Date date) {
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date = calendar.getTime();
				return sdf.parse(sdf.format(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 计算当前时间前一小时
	 */
	public Date getBeforHour(Date date) {
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
				date = calendar.getTime();
				return sdf.parse(sdf.format(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取当前时间的月份
	 */
	public int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 处理记录数的最大值
	 */
	public int handleMaxCount(int maxCount) {
		String in = String.valueOf(maxCount);
		String firStr = in.substring(0, 1);
		in = in.replaceAll("\\d", "0").replaceFirst("0", String.valueOf(Integer.parseInt(firStr) + 1));
		String out = in.length() > 1 ? in : "10";
		return Integer.parseInt(out);
	}

	public List<Kline> handleInclude(List<Kline> klines, Stroke stroke) {
		// flag表示标识符,当前笔的方向,0:向上;1:向下
		String flag = stroke == null ? null : stroke.getDirection();
		List<Kline> noIncludedKlines = new ArrayList<Kline>();

		for (int i = 0; i < klines.size() - 1; i++) {
			if (flag == null) {
				if (klines.get(i).getMax() >= klines.get(i + 1).getMax()
						&& klines.get(i).getMin() <= klines.get(i + 1).getMin()) {
					klines.remove(i);
					i--;
					continue;
				}
				if (klines.get(i).getMax() < klines.get(i + 1).getMax()
						&& klines.get(i).getMin() > klines.get(i + 1).getMin()) {
					klines.remove(i);
					i--;
					continue;
				}
				if (klines.get(i).getMax() > klines.get(i + 1).getMax()
						&& klines.get(i).getMin() > klines.get(i + 1).getMin()) {
					flag = "1";
					continue;
				}
				if (klines.get(i).getMax() < klines.get(i + 1).getMax()
						&& klines.get(i).getMin() < klines.get(i + 1).getMin()) {
					flag = "0";
					continue;
				}
				continue;
			} else if ("0".equals(flag)) {
				if (klines.get(i).getMax() >= klines.get(i + 1).getMax()
						&& klines.get(i).getMin() <= klines.get(i + 1).getMin()) {
					klines.get(i).setMin(klines.get(i + 1).getMin());
					klines.remove(i + 1);
					i--;
					continue;
				}
				if (klines.get(i).getMax() < klines.get(i + 1).getMax()
						&& klines.get(i).getMin() > klines.get(i + 1).getMin()) {
					klines.get(i + 1).setMin(klines.get(i).getMin());
					klines.remove(i);
					i--;
					continue;
				}
				if (klines.get(i).getMax() > klines.get(i + 1).getMax()
						&& klines.get(i).getMin() > klines.get(i + 1).getMin()) {
					flag = "1";
					continue;
				}
				continue;
			} else if ("1".equals(flag)) {
				if (klines.get(i).getMax() >= klines.get(i + 1).getMax()
						&& klines.get(i).getMin() <= klines.get(i + 1).getMin()) {
					klines.get(i).setMax(klines.get(i + 1).getMax());
					klines.remove(i + 1);
					i--;
					continue;
				}
				if (klines.get(i).getMax() < klines.get(i + 1).getMax()
						&& klines.get(i).getMin() > klines.get(i + 1).getMin()) {
					klines.get(i + 1).setMax(klines.get(i).getMax());
					klines.remove(i);
					i--;
					continue;
				}
				if (klines.get(i).getMax() < klines.get(i + 1).getMax()
						&& klines.get(i).getMin() < klines.get(i + 1).getMin()) {
					flag = "0";
					continue;
				}
				continue;
			}
		}
		noIncludedKlines.addAll(klines);
		return noIncludedKlines;
	}

	/**
	 * 判断三笔是否重叠
	 * 
	 * @param s1
	 * @param s2
	 * @param s3
	 * @return 0:重叠;1:不重叠:方向向上;2:不重叠:方向向下
	 */
	protected int overlap(Stroke s1, Stroke s2, Stroke s3) {
		if ("0".equals(s1.getDirection()) && "0".equals(s3.getDirection())) {
			if (s1.getMin() > s3.getMax()) {
				return 2;
			}
		}
		if ("1".equals(s1.getDirection()) && "1".equals(s3.getDirection())) {
			if (s1.getMax() < s3.getMin()) {
				return 1;
			}
		}
		return 0;
	}

	public void loopProcessLines3(List<Stroke> strokes, List<Line> lines) {
		Integer currencyId = strokes.get(0).getCurrencyId();
		String type = strokes.get(0).getType();
		int size = strokes.size();
		double premax, premin;
		if (lines.size() != 0) {
			premax = strokes.get(0).getMax();
			premin = strokes.get(0).getMin();
		} else {
			premax = strokes.get(1).getMax();
			premin = strokes.get(1).getMin();
		}
		outter: for (int i = 0; i < size - 3; i++) {
			if (i == 2 && "0".equals(strokes.get(i).getDirection()) && lines.size() != 0) {
				// 第一笔包含第三笔
				if (strokes.get(i - 2).getMax() > strokes.get(i).getMax()) {
					premax = strokes.get(i).getMax();
				}
			}
			if (i == 2 && "1".equals(strokes.get(i).getDirection()) && lines.size() != 0) {
				// 第一笔包含第三笔
				if (strokes.get(i - 2).getMin() < strokes.get(i).getMin()) {
					premin = strokes.get(i).getMin();
				}
			}

			// 重新设置前最大最小值
			if (strokes.get(i + 2).getMax() > strokes.get(i).getMax() && "0".equals(strokes.get(i).getDirection())) {
				if (strokes.get(i).getMax() > premax) {
					premax = strokes.get(i).getMax();
				}
			}
			if (strokes.get(i + 2).getMin() < strokes.get(i).getMin() && "1".equals(strokes.get(i).getDirection())) {
				if (strokes.get(i).getMin() < premin) {
					premin = strokes.get(i).getMin();
				}
			}

			// 找到分界点[顶]
			if (strokes.get(i).getMax() > premax && strokes.get(i).getMax() > strokes.get(i + 2).getMax()
					&& "0".equals(strokes.get(i).getDirection())) {
				// 1:笔破坏
				if (strokes.get(i + 1).getMin() < premax) {
					// 笔破坏最终确认--先找分界点的情况下笔破坏是必定成立的
					Line tmpLine = new Line();
					tmpLine.setEndDate(strokes.get(i).getEndDate());
					tmpLine.setMax(strokes.get(i).getMax());
					tmpLine.setDirection("0");

					if (lines.size() != 0) {
						Line preLine = lines.get(lines.size() - 1);
						// 与前线段同向
						if (tmpLine.getDirection().equals(preLine.getDirection())) {
							preLine.setEndDate(tmpLine.getEndDate());
							preLine.setMax(tmpLine.getMax());
							preLine.saveOrUpdate(currencyId, type);
						} else {
							tmpLine.setStartDate(preLine.getEndDate());
							tmpLine.setMin(preLine.getMin());
							preLine.saveOrUpdate(currencyId, type);
							tmpLine.setPrevId(preLine.getId());
							tmpLine.saveOrUpdate(currencyId, type);
							preLine.setNextId(tmpLine.getId());
							preLine.saveOrUpdate(currencyId, type);
							lines.add(tmpLine);
						}
					} else {
						tmpLine.setStartDate(strokes.get(0).getStartDate());
						tmpLine.setMin("0".equals(strokes.get(0).getDirection())?strokes.get(0).getMin():strokes.get(0).getMax());
//						tmpLine.setEndDate(strokes.get(i).getEndDate());
//						tmpLine.setMax(strokes.get(i).getMax());
						tmpLine.saveOrUpdate(currencyId, type);
						lines.add(tmpLine);
					}
					Line preLine = lines.get(lines.size() - 1);
					Line tmpLine2 = new Line();
					tmpLine2.setStartDate(strokes.get(i + 1).getStartDate());
					tmpLine2.setMax(strokes.get(i + 1).getMax());
					tmpLine2.setEndDate(strokes.get(i + 3).getEndDate());
					tmpLine2.setMin(strokes.get(i + 3).getMin());
					tmpLine2.setDirection("1");
					tmpLine2.setPrevId(preLine.getId());
					tmpLine2.saveOrUpdate(currencyId, type);
					preLine.setNextId(tmpLine2.getId());
					preLine.saveOrUpdate(currencyId, type);
					lines.add(tmpLine2);
					if (strokes.size() - 1 > i + 3) {
						List<Stroke> subList = strokes.subList(i + 4, strokes.size());
						loopProcessLines3(subList, lines);
					}
					break;
				}
				// 2:线段破坏
				for (int j = i + 1; j < size - 1; j++) {
					if (strokes.get(j).getMin() < premax) {
						// 线段破坏成立
						Line tmpLine = new Line();
						tmpLine.setEndDate(strokes.get(i).getEndDate());
						tmpLine.setMax(strokes.get(i).getMax());
						tmpLine.setDirection("0");
						if (lines.size() != 0) {
							Line preLine = lines.get(lines.size() - 1);
							// 与前线段同向
							if (tmpLine.getDirection().equals(preLine.getDirection())) {
								preLine.setEndDate(tmpLine.getEndDate());
								preLine.setMax(tmpLine.getMax());
								preLine.saveOrUpdate(currencyId, type);
							} else {
								tmpLine.setStartDate(preLine.getEndDate());
								tmpLine.setMin(preLine.getMin());
								preLine.saveOrUpdate(currencyId, type);
								tmpLine.setPrevId(preLine.getId());
								tmpLine.saveOrUpdate(currencyId, type);
								preLine.setNextId(tmpLine.getId());
								preLine.saveOrUpdate(currencyId, type);
								lines.add(tmpLine);
							}
						} else {
							tmpLine.setStartDate(strokes.get(0).getStartDate());
							tmpLine.setMin(strokes.get(0).getMin());
							tmpLine.saveOrUpdate(currencyId, type);
							lines.add(tmpLine);
						}
						Line preLine = lines.get(lines.size() - 1);
						Line tmpLine2 = new Line();
						tmpLine2.setStartDate(strokes.get(i + 1).getStartDate());
						tmpLine2.setMax(strokes.get(i + 1).getMax());
						tmpLine2.setEndDate(strokes.get(j).getEndDate());
						tmpLine2.setMin(strokes.get(j).getMin());
						tmpLine2.setDirection("1");
						tmpLine2.setPrevId(preLine.getId());
						tmpLine2.saveOrUpdate(currencyId, type);
						preLine.setNextId(tmpLine2.getId());
						preLine.saveOrUpdate(currencyId, type);
						lines.add(tmpLine2);

						if (strokes.size() - 1 > j) {
							List<Stroke> subList = strokes.subList(j + 1, strokes.size());
							loopProcessLines3(subList, lines);
						}
						break outter;
					}
					if (strokes.get(j + 1).getMax() > strokes.get(i).getMax()) {
						premax = strokes.get(i).getMax();
						i = j;
						continue outter;
					}
					j++;
				}
			}
			// 找到分解点[底]
			if (strokes.get(i).getMin() < premin && strokes.get(i).getMin() < strokes.get(i + 2).getMin()
					&& "1".equals(strokes.get(i).getDirection())) {
				// 1:笔破坏
				if (strokes.get(i + 1).getMax() > premin) {
					// 笔破坏最终确认--先找分界点的情况下笔破坏是必定成立的
					Line tmpLine = new Line();
					tmpLine.setEndDate(strokes.get(i).getEndDate());
					tmpLine.setMin(strokes.get(i).getMin());
					tmpLine.setDirection("1");

					if (lines.size() != 0) {
						Line preLine = lines.get(lines.size() - 1);
						// 与前线段同向
						if (tmpLine.getDirection().equals(preLine.getDirection())) {
							preLine.setEndDate(tmpLine.getEndDate());
							preLine.setMin(tmpLine.getMin());
							preLine.saveOrUpdate(currencyId, type);
						} else {
							tmpLine.setStartDate(preLine.getEndDate());
							tmpLine.setMax(preLine.getMax());
							preLine.saveOrUpdate(currencyId, type);
							tmpLine.setPrevId(preLine.getId());
							tmpLine.saveOrUpdate(currencyId, type);
							preLine.setNextId(tmpLine.getId());
							preLine.saveOrUpdate(currencyId, type);
							lines.add(tmpLine);
						}
					} else {
						tmpLine.setStartDate(strokes.get(0).getStartDate());
						tmpLine.setMax("0".equals(strokes.get(0).getDirection())?strokes.get(0).getMin():strokes.get(0).getMax());
//						tmpLine.setEndDate(strokes.get(i).getEndDate());
//						tmpLine.setMin(strokes.get(i).getMin());
						tmpLine.saveOrUpdate(currencyId, type);
						lines.add(tmpLine);
					}
					Line preLine = lines.get(lines.size() - 1);
					Line tmpLine2 = new Line();
					tmpLine2.setStartDate(strokes.get(i + 1).getStartDate());
					tmpLine2.setMin(strokes.get(i + 1).getMin());
					tmpLine2.setEndDate(strokes.get(i + 3).getEndDate());
					tmpLine2.setMax(strokes.get(i + 3).getMax());
					tmpLine2.setDirection("0");
					tmpLine2.setPrevId(preLine.getId());
					tmpLine2.saveOrUpdate(currencyId, type);
					preLine.setNextId(tmpLine2.getId());
					preLine.saveOrUpdate(currencyId, type);
					lines.add(tmpLine2);

					if (strokes.size() - 1 > i + 3) {
						List<Stroke> subList = strokes.subList(i + 4, strokes.size());
						loopProcessLines3(subList, lines);
					}
					break;
				}
				// 2:线段破坏
				for (int j = i + 1; j < size - 1; j++) {
					if (strokes.get(j).getMax() > premin) {
						// 线段破坏成立
						Line tmpLine = new Line();
						tmpLine.setEndDate(strokes.get(i).getEndDate());
						tmpLine.setMin(strokes.get(i).getMin());
						tmpLine.setDirection("1");
						if (lines.size() != 0) {
							Line preLine = lines.get(lines.size() - 1);
							// 与前线段同向
							if (tmpLine.getDirection().equals(preLine.getDirection())) {
								preLine.setEndDate(tmpLine.getEndDate());
								preLine.setMin(tmpLine.getMin());
								preLine.saveOrUpdate(currencyId, type);
							} else {
								tmpLine.setStartDate(preLine.getEndDate());
								tmpLine.setMax(preLine.getMax());
								preLine.saveOrUpdate(currencyId, type);
								tmpLine.setPrevId(preLine.getId());
								tmpLine.saveOrUpdate(currencyId, type);
								preLine.setNextId(tmpLine.getId());
								preLine.saveOrUpdate(currencyId, type);
								lines.add(tmpLine);
							}
						} else {
							tmpLine.setStartDate(strokes.get(0).getStartDate());
							tmpLine.setMax("0".equals(strokes.get(0).getDirection())?strokes.get(0).getMin():strokes.get(0).getMax());
							tmpLine.saveOrUpdate(currencyId, type);
							lines.add(tmpLine);
						}
						Line preLine = lines.get(lines.size() - 1);
						Line tmpLine2 = new Line();
						tmpLine2.setStartDate(strokes.get(i + 1).getStartDate());
						tmpLine2.setMin(strokes.get(i + 1).getMin());
						tmpLine2.setEndDate(strokes.get(j).getEndDate());
						tmpLine2.setMax(strokes.get(j).getMax());
						tmpLine2.setDirection("0");
						tmpLine2.setPrevId(preLine.getId());
						tmpLine2.saveOrUpdate(currencyId, type);
						preLine.setNextId(tmpLine2.getId());
						preLine.saveOrUpdate(currencyId, type);
						lines.add(tmpLine2);

						if (strokes.size() - 1 > j) {
							List<Stroke> subList = strokes.subList(j + 1, strokes.size());
							loopProcessLines3(subList, lines);
						}
						break outter;
					}
					if (strokes.get(j + 1).getMin() < strokes.get(i).getMin()) {
						premin = strokes.get(i).getMin();
						i = j;
						continue outter;
					}
					j++;
				}
			}
		}

		Line.dao.updateStroke();
	}

	public void loopProcessLines2(List<Stroke> strokes, Double prenum, List<Line> lines) {
		Integer currencyId = strokes.get(0).getCurrencyId();
		String type = strokes.get(0).getType();
		int size = strokes.size();
		double premax = strokes.get(1).getMax();// 前一个最大值
		double premin = strokes.get(1).getMin();// 前一个最小值
		outter: for (int i = 0; i < size - 5; i++) {
			if ("0".equals(strokes.get(i).getDirection())) {// 第一笔向上
				if (prenum != null && strokes.get(i).getMax() < prenum) {// 线段破坏后的下一线段刚开始几笔的处理
					i++;
					continue;
				}
				if (strokes.get(i + 1).getMax() > premax) {
					premax = strokes.get(i + 1).getMax();
				}
				if (strokes.get(i + 3).getMin() < premax && strokes.get(i + 3).getMax() > premax) {// 笔破坏,由上转下
					// 出现笔破坏,线段的startpoint就出现了
					// 看破坏是否最终成立[不成立的唯一一种情况:后三笔不成段]
					if (overlap(strokes.get(i + 3), strokes.get(i + 4), strokes.get(i + 5)) > 0) {
						i = i + 1;
						continue;
					} else {// 笔破坏最终成立
						// 笔破坏最终确立,即三笔成段,线段的endpoint就出现了
						Line tmpLine = new Line();
						tmpLine.setStartDate(strokes.get(0).getStartDate());
						tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
						tmpLine.setMax(strokes.get(i + 2).getMax());
						tmpLine.setMin(strokes.get(0).getMin());
						tmpLine.setDirection("0");
						if (lines.size() != 0) {
							Line preLine = lines.get(lines.size() - 1);
							tmpLine.setPrevId(preLine.getId());
							tmpLine.save(currencyId, type);
							preLine.setNextId(tmpLine.getId());
							preLine.update(currencyId, type);
						} else {
							tmpLine.save(currencyId, type);
						}
						lines.add(tmpLine);
						List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
						loopProcessLines(tmStrokes, premax, lines);
//						List<Line> tmpLines = loopProcessLines(tmStrokes, premax,lines);
//						lines.addAll(tmpLines);
						break;
					}
				} else {// 没有笔破坏,看是否出现线段破坏
					for (int j = i + 3; j < size - 1; j++) {
						if (strokes.get(j).getMin() < premax) {// 线段破坏
							Line tmpLine = new Line();
							tmpLine.setStartDate(strokes.get(0).getStartDate());
							tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
							tmpLine.setMax(strokes.get(i + 2).getMax());
							tmpLine.setMin(strokes.get(0).getMin());
							tmpLine.setDirection("0");
							if (lines.size() != 0) {
								Line preLine = lines.get(lines.size() - 1);
								tmpLine.setPrevId(preLine.getId());
								tmpLine.save(currencyId, type);
								preLine.setNextId(tmpLine.getId());
								preLine.update(currencyId, type);
							} else {
								tmpLine.save(currencyId, type);
							}
							lines.add(tmpLine);
							List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
							loopProcessLines(tmStrokes, premax, lines);
//							List<Line> tmpLines = loopProcessLines(tmStrokes, premax,lines);
//							lines.addAll(tmpLines);
							break outter;
						}
						if (strokes.get(j + 1).getMax() > strokes.get(i + 5).getMax()) {// 原趋势延续
							premax = strokes.get(i + 2).getMax();
							i = j - 2;
							continue outter;
						}
						continue;
					}

				}
			} else {// 第一笔向下
				if (prenum != null && strokes.get(i).getMin() > prenum) {// 线段破坏后的下一线段刚开始几笔的处理
					i++;
					continue;
				}
				if (strokes.get(i + 1).getMin() < premin) {
					premin = strokes.get(i + 1).getMin();
				}
				if (strokes.get(i + 3).getMax() > premin && strokes.get(i + 3).getMin() < premin) {// 笔破坏,由下转上
					// 出现笔破坏,线段的startpoint就出现了
					// 看破坏是否最终成立[不成立的唯一一种情况:后三笔不成段]
					if (overlap(strokes.get(i + 3), strokes.get(i + 4), strokes.get(i + 5)) > 0) {
						i = i + 1;
						continue;
					} else {// 笔破坏最终成立
						// 笔破坏最终确立,即三笔成段,线段的endpoint就出现了
						Line tmpLine = new Line();
						tmpLine.setStartDate(strokes.get(0).getStartDate());
						tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
						tmpLine.setMax(strokes.get(0).getMax());
						tmpLine.setMin(strokes.get(i + 2).getMin());
						tmpLine.setDirection("1");
						if (lines.size() != 0) {
							Line preLine = lines.get(lines.size() - 1);
							tmpLine.setPrevId(preLine.getId());
							tmpLine.save(currencyId, type);
							preLine.setNextId(tmpLine.getId());
							preLine.update(currencyId, type);
						} else {
							tmpLine.save(currencyId, type);
						}
						lines.add(tmpLine);
						List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
						loopProcessLines(tmStrokes, premin, lines);
//						List<Line> tmpLines = loopProcessLines(tmStrokes, premin,lines);
//						lines.addAll(tmpLines);
						break;
					}
				} else {// 没有笔破坏,看是否出现线段破坏
					for (int j = i + 3; j < size - 1; j++) {
						if (strokes.get(j).getMax() > premin) {// 线段破坏
							Line tmpLine = new Line();
							tmpLine.setStartDate(strokes.get(0).getStartDate());
							tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
							tmpLine.setMax(strokes.get(0).getMax());
							tmpLine.setMin(strokes.get(i + 2).getMin());
							tmpLine.setDirection("1");
							if (lines.size() != 0) {
								Line preLine = lines.get(lines.size() - 1);
								tmpLine.setPrevId(preLine.getId());
								tmpLine.save(currencyId, type);
								preLine.setNextId(tmpLine.getId());
								preLine.update(currencyId, type);
							} else {
								tmpLine.save(currencyId, type);
							}
							lines.add(tmpLine);
							List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
							loopProcessLines(tmStrokes, premin, lines);
//							List<Line> tmpLines = loopProcessLines(tmStrokes, premin,lines);
//							lines.addAll(tmpLines);
							break outter;
						}
						if (strokes.get(j + 1).getMin() < strokes.get(i + 5).getMin()) {// 原趋势延续
							premin = strokes.get(i + 2).getMin();
							i = j - 2;
							continue outter;
						}
						continue;
					}

				}
			}
		}
		Line.dao.updateStroke();
	}

	public void loopProcessLines(List<Stroke> strokes, Double prenum, List<Line> lines) {
		Integer currencyId = strokes.get(0).getCurrencyId();
		String type = strokes.get(0).getType();
		int size = strokes.size();
		double premax = strokes.get(1).getMax();// 前一个最大值
		double premin = strokes.get(1).getMin();// 前一个最小值
		outter: for (int i = 0; i < size - 5; i++) {
			if ("0".equals(strokes.get(i).getDirection())) {// 第一笔向上
				if (prenum != null && strokes.get(i).getMax() < prenum) {// 线段破坏后的下一线段刚开始几笔的处理
					i++;
					continue;
				}
				if (strokes.get(i + 1).getMax() > premax) {
					premax = strokes.get(i + 1).getMax();
				}
				if (strokes.get(i + 3).getMin() < premax) {// 笔破坏,由上转下
					// 出现笔破坏,线段的startpoint就出现了
					// 看破坏是否最终成立[不成立的唯一一种情况:后三笔不成段]
					if (overlap(strokes.get(i + 3), strokes.get(i + 4), strokes.get(i + 5)) > 0) {
						i = i + 1;
						continue;
					} else {// 笔破坏最终成立
						// 笔破坏最终确立,即三笔成段,线段的endpoint就出现了
						Line tmpLine = new Line();
						tmpLine.setStartDate(strokes.get(0).getStartDate());
						tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
						tmpLine.setMax(strokes.get(i + 2).getMax());
						tmpLine.setMin(strokes.get(0).getMin());
						tmpLine.setDirection("0");
						if (lines.size() != 0) {
							Line preLine = lines.get(lines.size() - 1);
							tmpLine.setPrevId(preLine.getId());
							tmpLine.save(currencyId, type);
							preLine.setNextId(tmpLine.getId());
							preLine.update(currencyId, type);
						} else {
							tmpLine.save(currencyId, type);
						}
						lines.add(tmpLine);
						List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
						loopProcessLines(tmStrokes, premax, lines);
//						List<Line> tmpLines = loopProcessLines(tmStrokes, premax,lines);
//						lines.addAll(tmpLines);
						break;
					}
				} else {// 没有笔破坏,看是否出现线段破坏
					for (int j = i + 3; j < size - 1; j++) {
						if (strokes.get(j).getMin() < premax) {// 线段破坏
							Line tmpLine = new Line();
							tmpLine.setStartDate(strokes.get(0).getStartDate());
							tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
							tmpLine.setMax(strokes.get(i + 2).getMax());
							tmpLine.setMin(strokes.get(0).getMin());
							tmpLine.setDirection("0");
							if (lines.size() != 0) {
								Line preLine = lines.get(lines.size() - 1);
								tmpLine.setPrevId(preLine.getId());
								tmpLine.save(currencyId, type);
								preLine.setNextId(tmpLine.getId());
								preLine.update(currencyId, type);
							} else {
								tmpLine.save(currencyId, type);
							}
							lines.add(tmpLine);
							List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
							loopProcessLines(tmStrokes, premax, lines);
//							List<Line> tmpLines = loopProcessLines(tmStrokes, premax,lines);
//							lines.addAll(tmpLines);
							break outter;
						}
						if (strokes.get(j + 1).getMax() > strokes.get(i + 5).getMax()) {// 原趋势延续
							premax = strokes.get(i + 2).getMax();
							i = j - 2;
							continue outter;
						}
						continue;
					}

				}
			} else {// 第一笔向下
				if (prenum != null && strokes.get(i).getMin() > prenum) {// 线段破坏后的下一线段刚开始几笔的处理
					i++;
					continue;
				}
				if (strokes.get(i + 1).getMin() < premin) {
					premin = strokes.get(i + 1).getMin();
				}
				if (strokes.get(i + 3).getMax() > premin) {// 笔破坏,由下转上
					// 出现笔破坏,线段的startpoint就出现了
					// 看破坏是否最终成立[不成立的唯一一种情况:后三笔不成段]
					if (overlap(strokes.get(i + 3), strokes.get(i + 4), strokes.get(i + 5)) > 0) {
						i = i + 1;
						continue;
					} else {// 笔破坏最终成立
						// 笔破坏最终确立,即三笔成段,线段的endpoint就出现了
						Line tmpLine = new Line();
						tmpLine.setStartDate(strokes.get(0).getStartDate());
						tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
						tmpLine.setMax(strokes.get(0).getMax());
						tmpLine.setMin(strokes.get(i + 2).getMin());
						tmpLine.setDirection("1");
						if (lines.size() != 0) {
							Line preLine = lines.get(lines.size() - 1);
							tmpLine.setPrevId(preLine.getId());
							tmpLine.save(currencyId, type);
							preLine.setNextId(tmpLine.getId());
							preLine.update(currencyId, type);
						} else {
							tmpLine.save(currencyId, type);
						}
						lines.add(tmpLine);
						List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
						loopProcessLines(tmStrokes, premin, lines);
//						List<Line> tmpLines = loopProcessLines(tmStrokes, premin,lines);
//						lines.addAll(tmpLines);
						break;
					}
				} else {// 没有笔破坏,看是否出现线段破坏
					for (int j = i + 3; j < size - 1; j++) {
						if (strokes.get(j).getMax() > premin) {// 线段破坏
							Line tmpLine = new Line();
							tmpLine.setStartDate(strokes.get(0).getStartDate());
							tmpLine.setEndDate(strokes.get(i + 2).getEndDate());
							tmpLine.setMax(strokes.get(0).getMax());
							tmpLine.setMin(strokes.get(i + 2).getMin());
							tmpLine.setDirection("1");
							if (lines.size() != 0) {
								Line preLine = lines.get(lines.size() - 1);
								tmpLine.setPrevId(preLine.getId());
								tmpLine.save(currencyId, type);
								preLine.setNextId(tmpLine.getId());
								preLine.update(currencyId, type);
							} else {
								tmpLine.save(currencyId, type);
							}
							lines.add(tmpLine);
							List<Stroke> tmStrokes = strokes.subList(i + 3, strokes.size());
							loopProcessLines(tmStrokes, premin, lines);
//							List<Line> tmpLines = loopProcessLines(tmStrokes, premin,lines);
//							lines.addAll(tmpLines);
							break outter;
						}
						if (strokes.get(j + 1).getMin() < strokes.get(i + 5).getMin()) {// 原趋势延续
							premin = strokes.get(i + 2).getMin();
							i = j - 2;
							continue outter;
						}
						continue;
					}

				}
			}
		}
		Line.dao.updateStroke();
	}

	public List<Stroke> processStrokes(List<Kline> klines, Stroke inStroke) {
		int currencyId = klines.get(0).getCurrencyId();
		Currency currency = Currency.dao.findById(currencyId);
		currency.setBuyPoint(false);
		currency.setSalePoint(false);
		String code = currency.getCode();
		String type = klines.get(0).getType();
		List<Stroke> strokes = new ArrayList<Stroke>();
		List<Shape> shapes = new ArrayList<Shape>();
		int index = 0;
		int strokeStartIndex = 0;
		int strokeEndIndex = 0;

		if (inStroke != null) {
			strokes.add(inStroke);
			Shape firstShape = new Shape().setDate(inStroke.getEndDate()).setType(inStroke.getDirection());
			if ("0".equals(inStroke.getDirection())) {
				firstShape.setMax(inStroke.getMax());
			} else if ("1".equals(inStroke.getDirection())) {
				firstShape.setMin(inStroke.getMin());
			}
			shapes.add(firstShape);// ++++++++++
		}

		outterFor: for (int i = 0; i < klines.size() - 2; i++) {
			if (Shape.dao.isHighShape(klines.get(i), klines.get(i + 1), klines.get(i + 2))) {// 顶分型
				Shape shape = new Shape().setType("0");
				shape.setMax(klines.get(i + 1).getMax());
				shape.setDate(klines.get(i + 1).getDate());
				strokeEndIndex = i + 1;

				if (shapes.size() == 0) {// 第一个分型
					shapes.add(shape);
					index = i + 1;
					strokeStartIndex = i + 1;
				} else {
					Shape lastShape = shapes.get(shapes.size() - 1);
					if (lastShape.getType().equals(shape.getType())) {// 与前一个分型类型相同
						if (shape.getMax() > lastShape.getMax()) {
							lastShape.setDate(shape.getDate());
							lastShape.setMax(shape.getMax());
							index = i + 1;
							strokeStartIndex = i + 1;
							if (strokes.size() != 0) {
								Stroke lastStroke = strokes.get(strokes.size() - 1);
								lastStroke.setMax(shape.getMax());
								lastStroke.setEndDate(shape.getDate());
								lastStroke.saveOrUpdate(code, type);// =================

								// 设置卖点
								if (i == klines.size() - 3) {
									currency.setSalePoint(true);
								}
							}
						}
					} else {
						if (i + 1 - index < 3) {// 分型不成立
							if (strokes.size() == 0) {
								continue;
							} else {// 分型不成立时,判断缺口成笔是否成立
								Stroke lastStroke = strokes.get(strokes.size() - 1);
								if (Shape.dao.gapToStroke(lastStroke, klines.get(i), klines.get(i + 1),
										klines.get(i + 2))) {
									shapes.add(shape);
									Stroke gapStroke = new Stroke();
									gapStroke.setCurrencyId(currencyId);
									gapStroke.setMax(shape.getMax());
									gapStroke.setMin(lastShape.getMin());
									gapStroke.setType(type);
									gapStroke.setStartDate(lastShape.getDate());
									gapStroke.setEndDate(shape.getDate());
									gapStroke.setDirection("0");
									gapStroke.setPrevId(lastStroke.getId());
									gapStroke.setFromGap(true);
									gapStroke.saveOrUpdate(code, type);// =================
									lastStroke.setNextId(gapStroke.getId());
									lastStroke.saveOrUpdate(code, type);// =================
									strokes.add(gapStroke);
									index = i + 1;
									strokeStartIndex = i + 1;

									// 设置卖点
									if (i == klines.size() - 3) {
										currency.setSalePoint(true);
									}

								} else {
									continue;
								}
							}
						} else {
							for (int j = strokeStartIndex + 1; j < strokeEndIndex; j++) {
								if (klines.get(j).getMax() > shape.getMax()
										|| klines.get(j).getMin() < lastShape.getMin()) {
									continue outterFor;
								}
							}
							shapes.add(shape);
							Stroke stroke = new Stroke();
							stroke.setCurrencyId(currencyId);
							stroke.setMax(shape.getMax());
							stroke.setMin(lastShape.getMin());
							stroke.setType(type);
							stroke.setStartDate(lastShape.getDate());
							stroke.setEndDate(shape.getDate());
							stroke.setDirection("0");
							if (strokes.size() != 0) {
								stroke.setPrevId(strokes.get(strokes.size() - 1).getId());
							}
							stroke.saveOrUpdate(code, type);// ===========
							if (strokes.size() != 0) {
								strokes.get(strokes.size() - 1).setNextId(stroke.getId()).saveOrUpdate(code, type);// =================
							}
							strokes.add(stroke);
							index = i + 1;
							strokeStartIndex = i + 1;

							// 设置卖点
							if (i == klines.size() - 3) {
								currency.setSalePoint(true);
							}
						}
					}

				}
			} else if (Shape.dao.isLowShape(klines.get(i), klines.get(i + 1), klines.get(i + 2))) {// 底分型
				Shape shape = new Shape().setType("1");
				shape.setMin(klines.get(i + 1).getMin());
				shape.setDate(klines.get(i + 1).getDate());
				strokeEndIndex = i + 1;// ++++++++++++++++++++++

				if (shapes.size() == 0) {// 第一个分型
					shapes.add(shape);
					index = i + 1;
					strokeStartIndex = i + 1;
				} else {
					Shape lastShape = shapes.get(shapes.size() - 1);
					if (lastShape.getType().equals(shape.getType())) {// 与前一个分型类型相同
						if (shape.getMin() < lastShape.getMin()) {
							lastShape.setDate(shape.getDate());
							lastShape.setMin(shape.getMin());
							index = i + 1;
							strokeStartIndex = i + 1;
							if (strokes.size() != 0) {
								Stroke lastStroke = strokes.get(strokes.size() - 1);
								lastStroke.setMin(shape.getMin());
								lastStroke.setEndDate(shape.getDate());
								lastStroke.saveOrUpdate(code, type);// =================

								// 设置买点
								if (i == klines.size() - 3) {
									currency.setBuyPoint(true);
								}
							}
						}
					} else {
						if (i + 1 - index < 3) {// 分型不成立
							if (strokes.size() == 0) {
								continue;
							} else {// 分型不成立时,判断缺口成笔是否成立
								Stroke lastStroke = strokes.get(strokes.size() - 1);
								if (Shape.dao.gapToStroke(lastStroke, klines.get(i), klines.get(i + 1),
										klines.get(i + 2))) {
									shapes.add(shape);
									Stroke gapStroke = new Stroke();
									gapStroke.setCurrencyId(currencyId);
									gapStroke.setMin(shape.getMin());
									gapStroke.setMax(lastShape.getMax());
									gapStroke.setType(type);
									gapStroke.setStartDate(lastShape.getDate());
									gapStroke.setEndDate(shape.getDate());
									gapStroke.setDirection("1");
									gapStroke.setPrevId(lastStroke.getId());
									gapStroke.setFromGap(true);
									gapStroke.saveOrUpdate(code, type);// ============
									lastStroke.setNextId(gapStroke.getId());
									lastStroke.saveOrUpdate(code, type);// =================
									strokes.add(gapStroke);
									index = i + 1;
									strokeStartIndex = i + 1;

									// 设置买点
									if (i == klines.size() - 3) {
										currency.setBuyPoint(true);
									}
								} else {
									continue;
								}
							}
						} else {
							for (int j = strokeStartIndex + 1; j < strokeEndIndex; j++) {
								if (klines.get(j).getMax() > lastShape.getMax()
										|| klines.get(j).getMin() < shape.getMin()) {
									continue outterFor;
								}
							}
							shapes.add(shape);
							Stroke stroke = new Stroke();
							stroke.setCurrencyId(currencyId);
							stroke.setMin(shape.getMin());
							stroke.setMax(lastShape.getMax());
							stroke.setType(type);
							stroke.setStartDate(lastShape.getDate());
							stroke.setEndDate(shape.getDate());
							stroke.setDirection("1");
							if (strokes.size() != 0) {
								stroke.setPrevId(strokes.get(strokes.size() - 1).getId());
							}
							stroke.saveOrUpdate(code, type);// ==========
							if (strokes.size() != 0) {
								strokes.get(strokes.size() - 1).setNextId(stroke.getId()).saveOrUpdate(code, type);// =================
							}
							strokes.add(stroke);
							index = i + 1;
							strokeStartIndex = i + 1;

							// 设置买点
							if (i == klines.size() - 3) {
								currency.setBuyPoint(true);
							}
						}
					}
				}
			}
		}
		Stroke.dao.updateKline();
		currency.update();
		return strokes;
	}
}
