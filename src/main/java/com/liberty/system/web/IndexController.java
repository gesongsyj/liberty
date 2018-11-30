package com.liberty.system.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.NoUrlPara;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.liberty.common.utils.IpKit;
import com.liberty.common.utils.ResultMsg;
import com.liberty.common.utils.ResultStatusCode;
import com.liberty.common.web.BaseController;
import com.liberty.system.model.Currency;
import com.liberty.system.query.CurrencyQueryObject;

/**
 * 登录管理
 */
public class IndexController extends BaseController {
	/**
	 * 导向登陆界面
	 */
	@Before(NoUrlPara.class)
	public void index() {
		// String basePath = getRequest().getScheme() + "://" +
		// PropKit.use("jfinal.properties").get("URL") + ":" +
		// getRequest().getServerPort() +
		// getRequest().getContextPath();
		// String word = "var URL = "+"'"+basePath+"'"+";";
		// String key = "const URL";
		// String path =
		// getRequest().getSession().getServletContext().getRealPath("/")+"pages/js/login.js";
		// PathUtil.setURL(path,key,word);
		redirect("/index.html");
	}
	
	/**
	 * 用户登出
	 */
	public void logout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null && subject.isAuthenticated()) {
			subject.logout();
		}
		index();
	}

	/**
	 * 用户登陆
	 */
//	@Before(SessionInViewInterceptor.class)
	public void login() {
		String name = paras.get("accountName");
		String password = paras.get("password");
		if (name == null && password == null) {//没传参数时跳转到注册页面
			render("register/index.html");
		} else {
			// if (validateCaptcha("randomCode")) {
			map.clear();

			try {
				UsernamePasswordToken token = new UsernamePasswordToken(name, password);
				if ("1".equals(paras.get("rememberMe"))) {
					token.setRememberMe(true);
				} else {
					token.setRememberMe(false);
				}
				Subject subject = SecurityUtils.getSubject();
				subject.login(token);

				Record account = Db.findFirst("select id,accountName,accountType from account where accountName=?", name);// 根据用户名查询数据库中的用户

				String ip = IpKit.getRealIp(getRequest());
				Long port = IpKit.getRemotePort(getRequest());
				logger.info("登陆账户：" + name + " 登陆IP：" + ip + "Port：" + port);
				getSession().setAttribute("account", account.getStr("accountName"));
				redirect("/currency/list");
//				renderJson(new ResultMsg(ResultStatusCode.OK, account));

			} catch (AuthenticationException e) {
				renderJson(new ResultMsg(ResultStatusCode.INVALID_PASSWORD));
			}
			// } else {
			// renderJson(new ResultMsg(ResultStatusCode.RANDOM_ERROR));
			// }
		}
	}

	/**
	 * 生成验证码图片
	 */
	public void img() {
		renderCaptcha();
	}

	public void home() {
		render("home/index.html");
	}

}