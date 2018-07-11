package com.liberty.system.model;

import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.liberty.system.model.base.BaseStroke;
import com.liberty.system.query.StrokeQueryObject;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Stroke extends BaseStroke<Stroke> {
	public static final Stroke dao = new Stroke().dao();
	
	public boolean update(String code,String type) {
		try {
			super.update();
			List<Kline> klines = Kline.dao.getByDateRange(code,type,this.getStartDate(),this.getEndDate());
			for (Kline kline : klines) {
				kline.setStrokeId(this.getId());
			}
			Db.batchUpdate(klines, klines.size());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean save(String code,String type){
		try {
			super.save();
			List<Kline> klines = Kline.dao.getByDateRange(code,type,this.getStartDate(),this.getEndDate());
			for (Kline kline : klines) {
				kline.setStrokeId(this.getId());
			}
			Db.batchUpdate(klines, klines.size());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Page<Stroke> paginate(StrokeQueryObject qo) {
		SqlPara sqlPara = getSqlParaFromTemplate(Kv.by("qo", qo));
		return dao.paginate(qo.getCurrentPage(), qo.getPageSize(), sqlPara);
	}
	
	public Stroke getLast() {
		String sql = getSqlFromTemplate();
		Stroke stroke = dao.findFirst(sql);
		return stroke;
	}
}
