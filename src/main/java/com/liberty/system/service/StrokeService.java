package com.liberty.system.service;

import com.jfinal.plugin.activerecord.Page;
import com.liberty.system.model.Stroke;
import com.liberty.system.query.StrokeQueryObject;

public class StrokeService{

	public Page<Stroke> paginate(StrokeQueryObject qo) {
		return Stroke.dao.paginate(qo);
	}

}