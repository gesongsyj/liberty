package com.liberty.system.test.mycompress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Config {
	// 文件路径
	private String filePath;
	// 文件字节的哈夫曼编码映射
	private HashMap<Byte, String> hfmCodeMap;

	/**
	 * 构造方法
	 * 
	 * @param filePath
	 *            文件路径
	 * @param hfmCodeMap
	 *            文件字节的哈夫曼编码映射
	 */
	public Config(String filePath, HashMap<Byte, String> hfmCodeMap) {
		super();
		this.filePath = filePath;
		this.hfmCodeMap = hfmCodeMap;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public HashMap<Byte, String> getHfmCodeMap() {
		return hfmCodeMap;
	}

	public void setHfmCodeMap(HashMap<Byte, String> hfmCodeMap) {
		this.hfmCodeMap = hfmCodeMap;
	}

	@Override
	public String toString() {
		return "FileConfig [filePath=" + filePath + ", hfmCodeMap=" + hfmCodeMap + "]";
	}
	
	/** 
	 * 根据指定的文件统计该文件中每个字节出现的次数，保存到一个HashMap对象中 
	 *  
	 * @param f 
	 *            要统计的文件 
	 * @return 保存次数的HashMap 
	 */  
	public static HashMap<Byte, Integer> countByte(File f) {  
	    // 判断文件是否存在  
	    if (!f.exists()) {  
	        // 不存在，直接返回null  
	        return null;  
	    }  
	    // 执行到这表示文件存在  
	    HashMap<Byte, Integer> byteCountMap = new HashMap<>();  
	    FileInputStream fis = null;  
	    try {  
	        // 创建文件输入流  
	        fis = new FileInputStream(f);  
	        // 保存每次读取的字节  
	        byte[] buf = new byte[1024];  
	        int size = 0;  
	        // 每次读取1024个字节  
	        while ((size = fis.read(buf)) != -1) {  
	            // 循环每次读到的真正字节  
	            for (int i = 0; i < size; i++) {  
	                // 获取缓冲区的字节  
	                byte b = buf[i];  
	                // 如果map中包含了这个字节，则取出对应的值，自增一次  
	                if (byteCountMap.containsKey(b)) {  
	                    // 获得原值  
	                    int old = byteCountMap.get(b);  
	                    // 先自增后入  
	                    byteCountMap.put(b, ++old);  
	                } else {  
	                    // map中不包含这个字节，则直接放入，且出现次数为1  
	                    byteCountMap.put(b, 1);  
	                }  
	            }  
	        }  
	    } catch (FileNotFoundException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    } finally {  
	        if (fis != null) {  
	            try {  
	                fis.close();  
	            } catch (IOException e) {  
	                // TODO Auto-generated catch block  
	                fis = null;  
	            }  
	        }  
	    }  
	    return byteCountMap;  
	} 
}
