package com.liberty.system.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liberty.system.test.db.JfinalConfig;
import com.liberty.system.test.utils.BitStatesUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class test {
	public static void main(String[] args) throws Exception {
		JfinalConfig.start();
//		int k=0;
		List<Record> rs = Db.find("select * from timeinterval");
		for (int i = 0; i < 1 << 12; i++) {
			for (Record r : rs) {
				if (BitStatesUtils.hasState(Long.valueOf(i), Long.valueOf(r.getInt("bitState")))) {
					Record record = new Record().set("bitState", r.getInt("bitState")).set("bitNum", i);
//					k++;
//					System.out.println(k);
//					System.out.println(record);
					Db.save("timeinterval_dict", record);
				}
			}
		}

	}
}
