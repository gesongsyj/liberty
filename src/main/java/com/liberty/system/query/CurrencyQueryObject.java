package com.liberty.system.query;

public class CurrencyQueryObject extends QueryObject {
	private String name;
	private String code;
	private String keyword;
	private String cutLine;
	private String followed;
	
	public String getFollowed() {
		return followed;
	}
	public void setFollowed(String followed) {
		this.followed = followed;
	}
	public String getCutLine() {
		return cutLine;
	}
	public void setCutLine(String cutLine) {
		this.cutLine = cutLine;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}