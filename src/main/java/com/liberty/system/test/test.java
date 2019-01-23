package com.liberty.system.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.liberty.system.test.db.JfinalConfig;
import com.liberty.system.test.utils.BitStatesUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class test {
	public static void main(String[] args) throws Exception {
//		JfinalConfig.start();
////		int k=0;
//		List<Record> rs = Db.find("select * from timeinterval");
//		for (int i = 0; i < 1 << 12; i++) {
//			for (Record r : rs) {
//				if (BitStatesUtils.hasState(Long.valueOf(i), Long.valueOf(r.getInt("bitState")))) {
//					Record record = new Record().set("bitState", r.getInt("bitState")).set("bitNum", i);
////					k++;
////					System.out.println(k);
////					System.out.println(record);
//					Db.save("timeinterval_dict", record);
//				}
//			}
//		}
		ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(1024));
		List<Future> futureList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {

			for (int j = 0; j < 9 && i < 100; j++, i++) {
				int k = i;
				Future<?> future = executor.submit(new Runnable() {
					@Override
					public void run() {
						if (k == 101) {
							int k = 1 / 0;
						}
						System.out.println("111");
						try {
							// 每转移一次图片后暂停一个小时
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				});
				futureList.add(future);
			}
			for (Future future : futureList) {
				future.get();
			}
			System.out.println("222");
			i--;
			futureList.clear();
			System.out.println(i);
		}

	}
}
