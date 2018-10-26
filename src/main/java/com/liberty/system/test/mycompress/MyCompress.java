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
		double d1 = 0, d2 = 0, d3 = 0, d4 = 0,d5 = 0, d6 = 0, d7 = 0, d8 = 0,d9 = 0, d10 = 0, d11 = 0, d12 = 0,d13 = 0, d14 = 0, d15 = 0, d16 = 0;
		for (byte c : bytes) {
			String byte2bits = Util.byte2bits(c);
			String s1 = byte2bits.substring(0, 4);
			String s2 = byte2bits.substring(4, 8);
			ss.add(s1);
			ss.add(s2);
		}
		for (String string : ss) {
			if ("0000".equals(string)) {
				d1++;
			}
			if ("0001".equals(string)) {
				d2++;
			}
			if ("0010".equals(string)) {
				d3++;
			}
			if ("0011".equals(string)) {
				d4++;
			}
			if ("0100".equals(string)) {
				d5++;
			}
			if ("0101".equals(string)) {
				d6++;
			}
			if ("0110".equals(string)) {
				d7++;
			}
			if ("0111".equals(string)) {
				d8++;
			}
			if ("1000".equals(string)) {
				d9++;
			}
			if ("1001".equals(string)) {
				d10++;
			}
			if ("1010".equals(string)) {
				d11++;
			}
			if ("1011".equals(string)) {
				d12++;
			}
			if ("1100".equals(string)) {
				d13++;
			}
			if ("1101".equals(string)) {
				d14++;
			}
			if ("1110".equals(string)) {
				d15++;
			}
			if ("1111".equals(string)) {
				d16++;
			}
		}
		double d = d1 + d2 + d3 + d4+d5+d6+d7+d8+d9+d10+d11+d12+d13+d14+d15+d16;
		map.put("0000", d1 / d);
		map.put("0001", d2 / d);
		map.put("0010", d3 / d);
		map.put("0011", d4 / d);
		map.put("0100", d5 / d);
		map.put("0101", d6 / d);
		map.put("0110", d7 / d);
		map.put("0111", d8 / d);
		map.put("1000", d9 / d);
		map.put("1001", d10 / d);
		map.put("1010", d11 / d);
		map.put("1011", d12 / d);
		map.put("1100", d13 / d);
		map.put("1101", d14 / d);
		map.put("1110", d15 / d);
		map.put("1111", d16 / d);
		System.out.println(map);
		
	}

}
