package com.liberty.system.test.callback;

public class Test {
	public static void main(String[] args) {
		Li li = new Li();
		Wang wang = new Wang(li);
		wang.askQuestion("1+1=?");
	}
}
