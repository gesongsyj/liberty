package com.liberty.system.service;

import com.jfinal.plugin.activerecord.Page;
import com.liberty.system.model.Kline;
import com.liberty.system.query.KlineQueryObject;

public class KlineService{

	public Page<Kline> paginate(KlineQueryObject qo) {
		return Kline.dao.paginate(qo);
	}

}