package com.liberty.system.web;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.liberty.common.web.BaseController;
import com.liberty.system.model.Stroke;
import com.liberty.system.query.StrokeQueryObject;
import com.liberty.system.service.StrokeService;

public class StrokeController extends BaseController {
	StrokeService strokeService = new StrokeService();

	public void testSave() {
		for (int i = 0; i < 20; i++) {
			new Stroke().set("id", i + 100).save();
		}
		renderText("添加");
	}

	public void testPage() {
		StrokeQueryObject qo = new StrokeQueryObject();
		// qo.setKeyword("11");
		// qo.setParentId(110L);
		Page<Stroke> paginate = Stroke.dao.paginate(qo);
		System.out.println(paginate.getList());
		renderText("查询");
	}
}