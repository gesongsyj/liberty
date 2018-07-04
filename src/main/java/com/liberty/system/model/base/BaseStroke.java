package com.liberty.system.model.base;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseStroke<T extends BaseStroke> extends SuperModel<T> implements IBean {

	public T setId(java.lang.Integer id) {
		set("id", id);
		return (T)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public T setMax(java.lang.Long max) {
		set("max", max);
		return (T)this;
	}
	
	public java.lang.Long getMax() {
		return getLong("max");
	}

	public T setMin(java.lang.Long min) {
		set("min", min);
		return (T)this;
	}
	
	public java.lang.Long getMin() {
		return getLong("min");
	}

	public T setParentId(java.lang.Integer parentId) {
		set("parentId", parentId);
		return (T)this;
	}
	
	public java.lang.Integer getParentId() {
		return getInt("parentId");
	}

}
