package com.liberty.system.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;

public class JfinalConfig2 {
	public static void start() {
//		PropKit.use("jfinal2.properties");
//		DruidPlugin dp_139 = new DruidPlugin(PropKit.get("mySqlUrl_139"), PropKit.get("user_139"),
//				PropKit.get("password_139"), PropKit.get("driverClass"));
//		ActiveRecordPlugin arp_139 = new ActiveRecordPlugin("db139", dp_139);
//		arp_139.setDialect(new MysqlDialect());// 切记配置方言
//		arp_139.setShowSql(true);
//		dp_139.start();
//		arp_139.start();
//
//		DruidPlugin dp_cent = new DruidPlugin(PropKit.get("mySqlUrl_cent"), PropKit.get("user_cent"),
//				PropKit.get("password_cent"), PropKit.get("driverClass"));
//		ActiveRecordPlugin arp_cent = new ActiveRecordPlugin("dbcent", dp_cent);
//		arp_cent.setDialect(new MysqlDialect());// 切记配置方言
//		arp_cent.setShowSql(true);
//		dp_cent.start();
//		arp_cent.start();
	}

	public static void main(String[] args) {
//		start();
//		List<Record> dis_139 = Db.use("db139")
//				.find("select name as name,parentId as parent from district where parentId is null");
//		List<Record> dis_cent = Db.use("dbcent").find("select * from district where parent is null");
//		for (Record record : dis_139) {
//			int flag = 0;
//			for (Record record1 : dis_cent) {
//				if (record1.getStr("name").equals(record.getStr("name"))) {
//					flag = 1;
//					break;
//				}
//			}
//			if (flag == 0) {
//				Db.use("dbcent").save("district", record);
//			}
//
//		}

//		List<Record> dis_cent = Db.use("dbcent").find("select * from district where parent is null");
//		for (Record record1 : dis_cent) {
//			Record sheng_139 = Db.use("db139").findFirst("select * from district where name=?", record1.getStr("name"));
//			if (sheng_139 != null) {
//				List<Record> dis_139_shi = Db.use("db139").find("select name as  name from district where parentId=?",
//						sheng_139.get("id"));// 139某省下的所有市
//
//				for (Record record2 : dis_139_shi) {
//					Record shi_cent = Db.use("dbcent").findFirst(
//							"select d1.id,d1.name,d1.parent from district d1 join district d2 on d1.parent=d2.id where d1.name like concat('%',?,'%') and d2.name=?",
//							record2.getStr("name"), sheng_139.getStr("name"));
//					if (shi_cent == null) {
//						record2.set("parent", record1.getInt("id"));
//						Db.use("dbcent").save("district", record2);
//					} else {
//						shi_cent.set("name", record2.getStr("name"));
//						Db.use("dbcent").update("district", shi_cent);
//					}
//				}
//
//			} else {
//				System.out.println("null:" + record1);
//			}
//		}

//		List<Record> dis_cent = Db.use("dbcent")
//				.find("select d1.id,d1.name,d1.parent from district d1 join district d2 on d1.parent = d2.id where d2.parent is null");
//		for (Record record1 : dis_cent) {
//			Record sheng_139 = Db.use("db139").findFirst("select * from district where name=? and parentId is not null", record1.getStr("name"));
//			if (sheng_139 != null) {
//				List<Record> dis_139_shi = Db.use("db139").find("select name as name from district where parentId=?",
//						sheng_139.get("id"));// 139某省下的所有市
//
//				for (Record record2 : dis_139_shi) {
//					Record shi_cent = Db.use("dbcent").findFirst(
//							"select d1.id,d1.name,d1.parent from district d1 join district d2 on d1.parent=d2.id where d1.name=? and d2.name=?",
//							record2.getStr("name"), sheng_139.getStr("name"));
////					Record shi_cent = Db.use("dbcent").findFirst(
////							"select d1.id,d1.name,d1.parent from district d1 join district d2 on d1.parent=d2.id where d1.name like concat('%',?,'%') and d2.name=?",
////							record2.getStr("name"), sheng_139.getStr("name"));
//					if (shi_cent == null) {
//						record2.set("parent", record1.getInt("id"));
//						Db.use("dbcent").save("district", record2);
//					} else {
//						continue;
////						shi_cent.set("name", record2.getStr("name"));
////						Db.use("dbcent").update("district", shi_cent);
//					}
//				}
//
//			} else {
//				System.out.println("null:" + record1);
//			}
//		}
		
//		List<Record> all = Db.use("dbcent").find("select * from district");
//		for (Record record : all) {
//			Record findFirst = Db.use("db139").findFirst("select * from district where name=?",record.getStr("name"));
//			if(findFirst==null){
//				System.out.println(record);
//			}
//		}
		
//		Set<Integer> a=new HashSet<Integer>();
//		a.add(63);
//		a.add(64);
//		a.add(70);
//		a.add(79);
//		a.add(67);
//		a.add(88);
//		a.add(77);
//		a.add(87);
//		a.add(83);
//		a.add(89);
//		a.add(90);
//		a.add(91);
//		List<Record> all = Db.use("dbcent").
//				find("select d1.id,d1.name,d1.parent from district d1 join district d2 on d1.parent=d2.id where d2.parent is null");
//		for (Record record : all) {
//			if(a.contains(record.getInt("parent"))){
//				continue;
//			}
//			record.set("name", record.getStr("name")+"市");
//			Db.use("dbcent").update("district",record);
//		}
//		System.out.println("over");
	}
}
