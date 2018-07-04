package com.liberty.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* @ClassName: DataVerAnnotation
* @Description: 自定义注解，添加到基础数据变更方法上
* @author: Administrator
* @date: 2017年4月19日
* @version:
 */
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DataVerAnnotation {
	

}
