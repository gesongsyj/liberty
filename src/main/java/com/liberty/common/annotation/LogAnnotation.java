package com.liberty.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * @author pengjin 
 * @version 2.1 
 * @since 2.1 
 */  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface LogAnnotation { 
	/**
	 * @return 模块名称
	 */
    String operateModelNm();  
    
    /**
	 * @return 方法名称
	 */
    String operateFuncNm();  
    
    /**
	 * @return 内容描述
	 */
    String operateDescribe();  
}  