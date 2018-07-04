package com.liberty.common.utils.excel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportTxt {


	/**
	 * @Description: 按行读取文本文件，返回字符串列表 
	 * @date 2016年7月15日
	 */
	public static List<String> ReadFile(String fileName)
	{
		List<String> list=new ArrayList<String>();

		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			while((str = br.readLine()) != null) {
				list.add(str);
			}
			br.close();
			reader.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
}