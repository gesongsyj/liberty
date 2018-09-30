package com.liberty.system.test.mycompress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

public class MyCompress {

	public static void main(String[] args) {
		byte[] bytes = "图 2.1 显示了一个如何使用 RLE 算法来对一个数据流编码的例子，其中出现六次的符号‘ 93 ’已经用 3 个字节来代替：一个标记字节（‘ 0 ’在本例中）重复的次数（‘ 6 ’）和符号本身（‘ 93 ’）。"
				.getBytes();
		List<String> ss = new ArrayList<>();
		Map<String, Double> map = new HashMap<>();
		double d1 = 0, d2 = 0, d3 = 0, d4 = 0;
		for (byte c : bytes) {
			String byte2bits = Util.byte2bits(c);
			String s1 = byte2bits.substring(0, 2);
			String s2 = byte2bits.substring(2, 4);
			String s3 = byte2bits.substring(4, 6);
			String s4 = byte2bits.substring(6, 8);
			ss.add(s1);
			ss.add(s2);
			ss.add(s3);
			ss.add(s4);
		}
		for (String string : ss) {
			if ("00".equals(string)) {
				d1++;
			}
			if ("01".equals(string)) {
				d2++;
			}
			if ("10".equals(string)) {
				d3++;
			}
			if ("11".equals(string)) {
				d4++;
			}
		}
		double d = d1 + d2 + d3 + d4;
		map.put("00", d1 / d);
		map.put("01", d2 / d);
		map.put("10", d3 / d);
		map.put("11", d4 / d);
		System.out.println(map);
	}

}
