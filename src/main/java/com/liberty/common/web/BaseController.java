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
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.upload.UploadFile;
import com.liberty.common.annotation.DataVerAnnotation;
import com.liberty.common.annotation.LogAnnotation;
import com.liberty.common.utils.DateUtil;
import com.liberty.common.utils.Encodes;
import com.liberty.common.utils.IpKit;
import com.liberty.common.utils.JsonToMap;
import com.liberty.system.model.Log;

import io.jsonwebtoken.Claims;

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
}
