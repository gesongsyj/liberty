package com.liberty.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ShellUtil {
	public static void callBat(String locationCmd) {
		try {
			Process child = Runtime.getRuntime().exec("cmd.exe /C start " + locationCmd);
			InputStream in = child.getInputStream();
			int c;
			while ((c = in.read()) != -1) {
			}
			in.close();
			try {
				child.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void callSh(String command, String outPrinter) {
		try {
//			String command = "/export/home/xlg/solarischk.sh";
//			String outPrinter="/export/home/zjg/show.txt";
			Runtime rt = Runtime.getRuntime();
			Process pcs = rt.exec(command);
			PrintWriter outWriter = new PrintWriter(new File(outPrinter));
			BufferedReader br = new BufferedReader(new InputStreamReader(pcs.getInputStream()));
			String line = new String();
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				outWriter.write(line);
			}
			pcs.waitFor();
			br.close();
			outWriter.flush();
			outWriter.close();
			int ret = pcs.exitValue();
			System.out.println(ret);
			System.out.println("执行完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		callBat("C:\\Users\\Administrator\\Desktop\\test.bat");
	}
}
