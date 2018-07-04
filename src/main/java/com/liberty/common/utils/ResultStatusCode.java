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
	DATA_UPLOAD(30008, "请上传比对信息"),
	CHECK_CARD(30009, "身份证号不能为空"),
	PHOTO_NULL(30010, "照片不能为空"),
	UPLOAD_OVER(30011, "无法再次上传"),
//	YEAR_NULL(30012,"已选择月份时年份不能为空"),
	CARD_ID_NULL(30013,"证件id不能为空"),
	RANDOM_ERROR(30014, "验证码错误"),
	NOCARD_ZERO(30015, "无证入住次数为零"),
	HOTEL_REGISTER(30016, "酒店已注册"),
	REC_FAILED(30017, "识别失败"),
	OVER_DATA(30018, "数据超出10000,无法导出");
	
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
