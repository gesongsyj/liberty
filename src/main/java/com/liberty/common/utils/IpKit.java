package com.liberty.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class IpKit {

	public static String getRealIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}

	/**
	 * 获取客户端源端口
	 * @param request
	 * @return
	 */
	public static Long getRemotePort(final HttpServletRequest request){
		try{
			String port = request.getHeader("remote-port");
			if( StringUtils.isNotEmpty(port )) {
				try{
					return Long.parseLong(port);
				}catch(NumberFormatException ex){
//					log.error("convert port to long error , port:	"+port);
					return 0l;
				}
			}else{
				return 0l;
			}
		}catch(Exception e){
//			log.error("get romote port error,error message:"+e.getMessage());
			return 0l;
		}
	}
}
