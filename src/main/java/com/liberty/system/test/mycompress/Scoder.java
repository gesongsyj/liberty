package com.liberty.system.test.mycompress;

public class Scoder {
	private static String[] hs = new String[] { "00", "010", "0110", "01110", "011110", "0111110", "0111111" };

	public static String getCode(int i){
		if(i<7){
			return hs[i];
		}else if(i<71){
			byte a=(byte)(-128 | (i-7)); 
			return Util.byte2bits(a);
		}else{
			return "10111111"+Util.byte2bits((byte)(i-71));
		}
	}
	public static void main(String[] args) {
		System.out.println(getCode(2));
		System.out.println(getCode(7));
		System.out.println(getCode(8));
		System.out.println(getCode(71));
		System.out.println(getCode(72));
	}
}
