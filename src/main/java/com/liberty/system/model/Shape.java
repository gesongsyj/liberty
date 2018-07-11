package com.liberty.system.model;

import com.jfinal.plugin.activerecord.Model;
import com.liberty.system.model.base.BaseShape;

@SuppressWarnings("serial")
public class Shape extends BaseShape<Shape> {
	public static final Shape dao = new Shape().dao();

	public boolean isHighShape(Kline k1, Kline k2, Kline k3) {
		return k2.getMax() >= k1.getMax() && k2.getMax() >= k3.getMax();
	}

	public boolean isLowShape(Kline k1, Kline k2, Kline k3) {
		return k2.getMin() <= k1.getMin() && k2.getMin() <= k3.getMin();
	}

	public boolean gapToStroke(Stroke stroke, Kline k1, Kline k2, Kline k3) {
		if (isHighShape(k1, k2, k3) && k2.getMin() > k1.getMax() && k2.getMin() > stroke.getMax()) {
			return true;
		}
		if (isLowShape(k1, k2, k3) && k2.getMax() < k1.getMin() && k2.getMax() < stroke.getMin()) {
			return true;
		}
		return false;
	}
}
