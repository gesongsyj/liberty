package com.liberty.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class PathUtil {

	/**
	 * 逐行读取文件，将满足条件的内容进行替换
	 * @param path js文件完整路径
	 * @param key  替换时 搜索关键字
	 * @param word 替换内容
   */
	public static void setURL(String path, String key, String word) {
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String line = "";

			String content = null;
			while ((content = br.readLine()) != null) {  //每次读一行
				line = line + content + "\r\n";
				if (content.contains(key)) {
					line = line.replaceAll(content, word);
				}
			}

			FileWriter fw = new FileWriter(path);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(line);   //把所有的文件写入原文件中
			bw.close();
			fw.close();

			fr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
