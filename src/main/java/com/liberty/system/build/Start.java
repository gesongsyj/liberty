package com.liberty.system.build;

import com.jfinal.core.JFinal;

public class Start {
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/liberty",5);// 启动
	}
}
