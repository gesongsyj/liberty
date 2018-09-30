package com.liberty.system.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyPicCompress {
	public static void main(String[] args) throws Exception {
//		compress("C:/111/pg.jpg");
		File file = new File("C:/111/pg.jpg");
		System.out.println(file.hashCode());
	}
	
	@SuppressWarnings("resource")
	public static void compress(String filePath) throws Exception{
		FileInputStream in = new FileInputStream(filePath);
		byte[] data=new byte[in.available()];
		in.read(data);
		in.close();
		System.out.println(data.length);
		byte[] deflateCompress = Compress.deflateCompress(data);
		
		System.out.println(deflateCompress.length);
	}
}
