package com.liberty.common.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 动态SQL拼接处理工具类，参数部分使用?|$|#代替。 
 * 其中，$代表常量(替换时不加单引号)，#代表变量(替换时要加单引号)
 * created by xiaofeixiang on 2016/1/9 15:28:18.
 */
public class DynamicSQL {

	private StringBuilder sql = new StringBuilder();

	private ArrayList<Object> params = new ArrayList<Object>();

	/**
	 * 参数不为NULL、“”的时候，拼接sqltmp。
	 * 
	 * @param <T>
	 * @param sqltmp
	 * @param param
	 * @return
	 * @author xiaofeixiang
	 */
	public <T> DynamicSQL isNotEmpty(String sqltmp, T param) {
		if (param == null || "".equals(param)) {
			return this;
		}
		this.sql.append(sqltmp);
		this.params.add(param);
		return this;
	}

	/**
	 * 参数为NULL、“”的时候，拼接sqltmp。
	 * 
	 * @param <T>
	 * @param sqltmp
	 * @param param
	 * @return
	 * @author xiaofeixiang
	 */
	public <T> DynamicSQL isEmpty(T param, String sqltmp) {
		if (param != null || !"".equals(param)) {
			return this;
		}
		this.sql.append(sqltmp);
		this.params.add(param);
		return this;
	}

	/**
	 * param==cmpVal || param equals cmpVal 的时候拼接
	 * 
	 * @param <T>
	 * @param sqltmp
	 * @param param
	 * @param cmpVal
	 * @return
	 * @author xiaofeixiang
	 */
	public <T> DynamicSQL isNotEqual(T param, T cmpVal, String sqltmp) {
		if (this.isBaseType(param) && param == cmpVal) {
			return this;
		}
		if (!this.isBaseType(param) && param.equals(cmpVal)) {
			return this;
		}
		this.sql.append(sqltmp);
		this.params.add(param);
		return this;
	}

	/**
	 * param!=cmpVal || param !equals cmpVal 的时候拼接
	 * 
	 * @param <T>
	 * @param sqltmp
	 * @param param
	 * @param cmpVal
	 * @return
	 * @author xiaofeixiang
	 */
	public <T> DynamicSQL isEqual(String sqltmp, T param, T cmpVal) {

		if (this.isBaseType(param) && param != cmpVal) {
			return this;
		}

		if (!this.isBaseType(param) && !param.equals(cmpVal)) {
			return this;
		}
		this.sql.append(sqltmp);
		this.params.add(param);
		return this;
	}

	/**
	 * param!=null 时候拼接
	 * 
	 * @param <T>
	 * @param sqltmp
	 * @param param
	 * @return
	 * @author xiaofeixiang
	 */
	public <T> DynamicSQL isNotNull(T param, String sqltmp) {
		if (param == null) {
			return this;
		}
		this.sql.append(sqltmp);
		this.params.add(param);
		return this;
	}

	/**
	 * param==null 时候拼接
	 * 
	 * @param <T>
	 * @param sqltmp
	 * @param param
	 * @return
	 * @author xiaofeixiang
	 */
	public <T> DynamicSQL isNull(T param, String sqltmp) {
		if (param != null) {
			return this;
		}
		this.sql.append(sqltmp);
		this.params.add(param);
		return this;
	}

	/**
	 * 正常拼接
	 * 
	 * @param sqltmp
	 * @return
	 * @author xiaofeixiang
	 */
	public DynamicSQL append(String sqltmp) {
		this.sql.append(sqltmp);
		return this;
	}

	/**
	 * 获取动态拼接后的sql，参数?模式。
	 * 
	 * @return
	 * @author xiaofeixiang
	 */
	public String getSql() {

		String _sql = trimOf();
		
		return _sql.replaceAll("\\$", "?").replaceAll("#", "?");
	}

	/**
	 * 获取参数的集合，有顺序与getSql()获取到的sql中的?参数一一对应。
	 * 
	 * @return
	 * @author xiaofeixiang
	 */
	public List<Object> getParams() {
		this.params.trimToSize();
		return this.params;
	}

	/**
	 * 获取参数的集合，有顺序与getSql()获取到的sql中的?参数一一对应。
	 * 
	 * @param <T>
	 * @param collection
	 * @return
	 * @author xiaofeixiang
	 */
	public <T extends Collection<Object>> T getParams(T collection) {
		this.params.trimToSize();
		collection.addAll(this.params);
		return collection;
	}

	/**
	 * 获取参数的集合，有顺序与getSql()获取到的sql中的?参数一一对应。
	 * 
	 * @param <T>
	 * @param parameter
	 * @param t
	 * @return
	 * @author xiaofeixiang
	 */
	public <T> T getParams(DynamicSQL.Parameter<T> parameter, T t) {
		this.params.trimToSize();
		return parameter.addAll(params, t);
	}

	/**
	 * 返回参数赋值之后完整的sql
	 */
	@Override
	public String toString() {
		Object[] pas = new String[this.params.size()];
		for (int i = 0; i < pas.length; i++) {
			if (this.params.get(i) == null) {
				pas[i] = null;
			} else {
				pas[i] = String.valueOf(this.params.get(i));
			}
		}
		String _sql = trimOf();
		_sql = _sql.replaceAll("#", "'%s'").replaceAll("\\$", "%s");
		
		return String.format(_sql, pas);
	}

	private boolean isBaseType(Object param) {
		return param instanceof Byte || param instanceof Short || param instanceof Integer || param instanceof Long
				|| param instanceof Float || param instanceof Double || param instanceof Boolean
				|| param instanceof Character || param == null;
	}

	private String trimOf() {
		String _sql = this.sql.toString().replaceAll("\t", " ");

		while (_sql.indexOf("  ") != -1) {
			_sql = _sql.replaceAll("  ", " ");
		}
		return _sql;
	}

	public interface Parameter<T> {
		public T addAll(ArrayList<Object> params, T t);
	}
	
	public void clear () {
		this.sql.setLength(0);
		this.params.clear();
	}
}