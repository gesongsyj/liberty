package com.liberty.system.model.base;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseKline<T extends BaseKline> extends SuperModel<T> implements IBean {

	public T setId(java.lang.Integer id) {
		set("id", id);
		return (T)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public T setDate(java.util.Date date) {
		set("date", date);
		return (T)this;
	}
	
	public java.util.Date getDate() {
		return get("date");
	}

	public T setMax(java.lang.Double max) {
		set("max", max);
		return (T)this;
	}
	
	public java.lang.Double getMax() {
		return getDouble("max");
	}

	public T setMin(java.lang.Double min) {
		set("min", min);
		return (T)this;
	}
	
	public java.lang.Double getMin() {
		return getDouble("min");
	}

	public T setOpen(java.lang.Double open) {
		set("open", open);
		return (T)this;
	}
	
	public java.lang.Double getOpen() {
		return getDouble("open");
	}

	public T setClose(java.lang.Double close) {
		set("close", close);
		return (T)this;
	}
	
	public java.lang.Double getClose() {
		return getDouble("close");
	}

	public T setCurrencyId(java.lang.Integer currencyId) {
		set("currencyId", currencyId);
		return (T)this;
	}
	
	public java.lang.Integer getCurrencyId() {
		return getInt("currencyId");
	}

	public T setStrokeId(java.lang.Integer strokeId) {
		set("strokeId", strokeId);
		return (T)this;
	}
	
	public java.lang.Integer getStrokeId() {
		return getInt("strokeId");
	}

	public T setType(java.lang.String type) {
		set("type", type);
		return (T)this;
	}
	
	public java.lang.String getType() {
		return getStr("type");
	}

	public T setBar(java.lang.Double bar) {
		set("bar", bar);
		return (T)this;
	}
	
	public java.lang.Double getBar() {
		return getDouble("bar");
	}

	public T setEmaS(java.lang.Double emaS) {
		set("ema_s", emaS);
		return (T)this;
	}
	
	public java.lang.Double getEmaS() {
		return getDouble("ema_s");
	}

	public T setEmaL(java.lang.Double emaL) {
		set("ema_l", emaL);
		return (T)this;
	}
	
	public java.lang.Double getEmaL() {
		return getDouble("ema_l");
	}

	public T setDiff(java.lang.Double diff) {
		set("diff", diff);
		return (T)this;
	}
	
	public java.lang.Double getDiff() {
		return getDouble("diff");
	}

	public T setDea(java.lang.Double dea) {
		set("dea", dea);
		return (T)this;
	}
	
	public java.lang.Double getDea() {
		return getDouble("dea");
	}

	public T setAoi(java.lang.Double aoi) {
		set("aoi", aoi);
		return (T)this;
	}
	
	public java.lang.Double getAoi() {
		return getDouble("aoi");
	}
}
