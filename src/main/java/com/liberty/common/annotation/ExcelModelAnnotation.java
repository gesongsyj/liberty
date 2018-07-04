package com.liberty.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcelModelAnnotation {

	/**
	 * @Description: 列名 
	 * @date 2016年7月15日
	 */
	String[] ColumNames() default {};
	
	/**
	 * @Description: 标题 
	 * @date 2016年7月15日
	 */
	String title() default "数据汇总";
}
