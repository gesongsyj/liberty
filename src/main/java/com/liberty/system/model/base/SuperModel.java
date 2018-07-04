package com.liberty.system.model.base;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.SqlPara;

public class SuperModel<T extends SuperModel> extends Model<T>{
	
	protected SqlPara getSqlParaFromTemplate(Kv kv) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getSimpleName().toLowerCase());
		sb.append(".");
		sb.append(Thread.currentThread().getStackTrace()[2].getMethodName());
		return Db.getSqlPara(sb.toString(),kv);
	}
	protected String getSqlFromTemplate() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getSimpleName().toLowerCase());
		sb.append(".");
		sb.append(Thread.currentThread().getStackTrace()[2].getMethodName());
		return Db.getSql(sb.toString());
	}
}