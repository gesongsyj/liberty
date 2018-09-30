package com.liberty.system.test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageCompressUtil {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		dealImage("C:/111/pg.jpg", "C:/111/pg_new2.jpg");

		long end = System.currentTimeMillis();
		System.out.println("总计耗时:" + (end - start) + "秒");
	}

	/**
	 * 根据宽高编辑图片
	 * 
	 * @param outPath
	 *            输出文件路径
	 * @param width
	 *            输出文件宽
	 * @param height
	 *            输出文件高
	 * @throws Exception
	 */

	public static void dealImage(String filePath, String outPath) throws Exception {
		// 读取本地文件：
		InputStream is = new FileInputStream(filePath);
		// 判断图片大小 0---500k 进行4倍压缩 500----1024k 进行6倍压缩 1024以上进行8倍压缩
		File picture = new File(filePath);
		int cutMultiple = 2;
		if (picture.exists()) {
			// int picsize =Integer.parseInt(new
			// DecimalFormat("0").format(picture.length()/1024.0));//四舍五入
			try {
				int picsize = (int) (picture.length() / 1024.0);// 非四舍五入
				if (picsize <= 256) {
					cutMultiple = 2;
				}
				if (picsize <= 512 && picsize > 256) {
					cutMultiple = 4;
				} else if (picsize > 512 && picsize <= 1024) {
					cutMultiple = 6;
				} else if (picsize > 1024) {
					cutMultiple = 8;
				}
			} catch (Exception e) {// 假容错处理
				cutMultiple = 2;
			}
		}

		Image image = ImageIO.read(is);
		float tagsize = 200;
		int old_w = image.getWidth(null);
		int old_h = image.getHeight(null);
		int tempsize;
		BufferedImage tag = new BufferedImage(old_w / cutMultiple, old_h / cutMultiple, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(image, 0, 0, old_w / cutMultiple, old_h / cutMultiple, null);
		FileOutputStream newimage = new FileOutputStream(outPath);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
		encoder.encode(tag);
		newimage.close();

	}
}