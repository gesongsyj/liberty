package com.liberty.system.test.callback;

public class Li {
	public void exe(CallBack callback,String q){
		//模拟耗时
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("小王的问题是:"+q);
		String result="答案是2";
		callback.solve(result);
	}
}
