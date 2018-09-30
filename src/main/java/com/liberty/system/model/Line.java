package com.liberty.system.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.liberty.system.model.base.BaseLine;
import com.liberty.system.model.base.BaseStroke;
import com.liberty.system.query.StrokeQueryObject;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Line extends BaseLine<Line> {
	public static final Line dao = new Line().dao();
	
	private List<Kline> allKlines=new ArrayList<Kline>();
	
	public void updateKline(){
		Db.batchUpdate(allKlines, allKlines.size());
		allKlines.clear();
	}
	
	public boolean update(String code,String type) {
		try {
			super.update();
			List<Kline> klines = Kline.dao.getByDateRange(code,type,this.getStartDate(),this.getEndDate());
			for (Kline kline : klines) {
				kline.setStrokeId(this.getId());
				allKlines.add(kline);
			}
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
				allKlines.add(kline);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Page<Line> paginate(StrokeQueryObject qo) {
		SqlPara sqlPara = getSqlParaFromTemplate(Kv.by("qo", qo));
		return dao.paginate(qo.getCurrentPage(), qo.getPageSize(), sqlPara);
	}
	
	public Line getLast() {
		String sql = getSqlFromTemplate();
		Line line = dao.findFirst(sql);
		return line;
	}
}