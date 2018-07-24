package com.liberty.system.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EhCachePlugin;

public class JfinalConfig {
	public void start() {
		PropKit.use("jfinal.properties");
		DruidPlugin dp = new DruidPlugin(PropKit.get("mySqlUrl"), PropKit.get("user"), PropKit.get("password"),
				PropKit.get("driverClass"));
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		arp.setDialect(new MysqlDialect());// 切记配置方言
		EhCachePlugin ehCachePlugin = new EhCachePlugin();
		ehCachePlugin.start();
		dp.start();
		arp.start();
	}

	public Map<String, String> getMap() {
		List<Record> deviceList = Db.findByCache("deviceId_key", "deviceId_key",
				"select d.deviceid,c.publicKey from device d join company c on d.company=c.companyName");
		List<Record> device_nfcList = Db.findByCache("deviceId_key", "deviceuuId_key",
				"select d.device_uuid,c.publicKey from device_nfc d join company c on d.company=c.companyName");
		Map<String, String> deviceKeyMap = new HashMap<String, String>();
		for (Record record : deviceList) {
			deviceKeyMap.put(record.getStr("deviceid"), record.getStr("publicKey"));
		}
		for (Record record : device_nfcList) {
			deviceKeyMap.put(record.getStr("device_uuid"), record.getStr("publicKey"));
		}
		return deviceKeyMap;
	}

	public String getKey(String deviceid) {
		Map<String, String> map = getMap();
		return map.get(deviceid);
	}

	@Before(Tx.class)
	public void test() {
		// TODO Auto-generated method stub
		
	}

}
