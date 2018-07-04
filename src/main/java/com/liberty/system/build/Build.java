package com.liberty.system.build;

import java.util.Date;

import com.generator.MyGenerator.MyGenerator;

public class Build {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		MyGenerator generator = new MyGenerator("com.liberty.system", "jfinal.properties", true);
		generator.setUsername("username");
		generator.setMappingKitPackageName("com.liberty.common.jfinal");
		generator.generate();
		
	}
}
