package com.liberty.system.query;

abstract public class QueryObject {
	private Integer currentPage = 1;
	private Integer pageSize = 5;

	protected boolean hasLength(String str) {
		return str != null && !"".equals(str.trim());
	}

	// 返回分页的起点
	public Integer getStart() {
		return (currentPage - 1) * pageSize;
	}
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}