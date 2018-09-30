package com.liberty.system.test.mycompress;

public class Util {
	/**
	 * 将字符串转成二进制字节的方法
	 * 
	 * @param bString
	 *            待转换的字符串
	 * @return 二进制字节
	 */
	private static byte bit2byte(String bString) {
		byte result = 0;
		for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
			result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
		}
		return result;
	}

	/**
	 * 将二字节转成二进制的01字符串
	 * 
	 * @param b
	 *            待转换的字节
	 * @return 01字符串
	 */
	public static String byte2bits(byte b) {
		int z = b;
		z |= 256;
		String str = Integer.toBinaryString(z);
		int len = str.length();
		return str.substring(len - 8, len);
	}
	
	public static void main(String[] args) {
		byte bit2byte = bit2byte("10000000");
		byte bit2byte2 = bit2byte("00000001");
		byte bit2byte3 = bit2byte("00111111");
		System.out.println(bit2byte);
		System.out.println(bit2byte2);
		System.out.println(bit2byte3);
		
		String byte2bits = byte2bits((byte)(-128));
		System.out.println(byte2bits);
	}
}
