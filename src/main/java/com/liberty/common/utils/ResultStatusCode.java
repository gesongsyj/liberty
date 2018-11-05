package com.liberty.common.utils;

/**
* @ClassName: ResultStatusCode
* @Description:添加返回状态枚举
* @author: Administrator
* @date: 2017年5月4日
* @version:
 */
public enum ResultStatusCode {

	OK(0, "OK"),
	FALSE(1, "false"),
	SYSTEM_ERR(30001, "System error"),
	LOGIN_ERR(30002, "Login error"),
	INVALID_CLIENTID(30003, "Invalid clientid"),
	INVALID_PASSWORD(30004, "User name or password is incorrect"),
	INVALID_CAPTCHA(30005, "Invalid captcha or captcha overdue"),
	INVALID_TOKEN(30006, "Invalid token"),
	INVALID_INPUT(30007, "Invalid input"),
	CURRENCY_EXISTS(30008,"currency exists");
	
	private int errcode;
	private String errmsg;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	private ResultStatusCode(int Errode, String ErrMsg) {
		this.errcode = Errode;
		this.errmsg = ErrMsg;
	}

}
