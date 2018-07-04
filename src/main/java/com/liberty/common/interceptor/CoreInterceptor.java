package com.liberty.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.liberty.common.web.BaseController;

/**
 * @Description: 核心拦截器
 * @date 2016年6月15日
 */
public class CoreInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub
		System.out.println("================Before invoking " + inv.getActionKey());
		BaseController baseController=(BaseController)inv.getController();

		if (baseController.getRequest().getMethod().equals("OPTIONS")) {
			System.out.println("================this is OPTIONS: " + inv.getActionKey());
			inv.invoke();
		} else {
			baseController.injectParas();
			baseController.handleAnnotation(inv.getMethod());
			inv.invoke();
		}
		System.out.println("================After invoking " + inv.getActionKey());
	}
}
