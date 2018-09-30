package com.liberty.system.test;

import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

public class PicCompress_Thumbnails {
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		picCompress_scale("C:/111/pg.jpg", "C:/111/pg_new1.jpg", 0.5f);
//		picCompress_size("C:/111/pg.jpg", "C:/111/pg_new.jpg", 200, 300);

		long end = System.currentTimeMillis();
		System.out.println("总计耗时:" + (end - start) + "秒");
	}
	
	/**
	 * 指定大小缩放
	 * @param sourceFilePath 源文件路径
	 * @param targetFilePath 生成文件路径
	 * @param width 宽
	 * @param height 高
	 * @throws IOException
	 */
	public static void picCompress_size(String sourceFilePath,String targetFilePath,int width,int height) throws IOException{
		Thumbnails.of(sourceFilePath).size(width, height).toFile(targetFilePath);
	}
	
	/**
	 * 按照比例进行缩放
	 * @param sourceFilePath 源文件路径
	 * @param targetFilePath 生成文件路径
	 * @param scale 比例
	 * @throws IOException
	 */
	public static void picCompress_scale(String sourceFilePath,String targetFilePath,float scale) throws IOException{
		Thumbnails.of(sourceFilePath).scale(scale).toFile(targetFilePath);
	}
}
