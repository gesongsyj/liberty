package com.liberty.common.shiro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.liberty.system.model.Account;

/**
 * ClassName: MyShiroRealm
 *
 * @author Administrator
 * @Description: 基于db实现的shiro realm
 * @date 2016年6月13日
 */
public class MyShiroRealm extends AuthorizingRealm {

	/**
	 * 认证回调函数，登陆时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {

		String name = (String) authcToken.getPrincipal(); // 得到账户名
		String password = new String((char[]) authcToken.getCredentials()); // 得到密码

		clearCachedAuthorizationInfo(name);

		Account account = Account.dao.getAccountByName(name); // 根据用户名查询数据库中的用户
		if (account != null) {
			if (password.equals(account.getStr("password"))) {// 判断数据库中的密码与用户输入的密码是否一致
				return new SimpleAuthenticationInfo(account.getStr("accountName"), account.getStr("password"),
						getName());
			} else {
				throw new UnknownAccountException("密码不正确");
			}
		} else {
			throw new UnknownAccountException("没有找到该账号");
		}
	}

	/**
	 * 授权查询回调函数，进行授权但缓存中无用户的授权信息时调用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

		Set<String> roles = new HashSet<>(); // 角色集合
		Set<String> permissions = new HashSet<String>(); // 权限名的集合

		// 1. 得到账户名
		String name = (String) principals.getPrimaryPrincipal();

		Account account = Account.dao.getAccountByName(name);

		if (account != null) {
			roles.add(account.getStr("role"));
			permissions.add("blog:edit");
		}

		authorizationInfo.addRoles(roles);
		authorizationInfo.addStringPermissions(permissions);

		return authorizationInfo;
	}

	/**
	 * 清空用户关联权限认证，待下次使用时重新加载
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, super.getName());
		super.clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清空所有关联认证
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthenticationInfo> cache = getAuthenticationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}
}
