package com.liberty.system.test.callback;

public class Wang implements CallBack {
	private Li li;
	
	public Wang(Li li) {
		super();
		this.li = li;
	}

	@Override
	public void solve(String result) {
		System.out.println("小李告诉小王的答案是:" + result);
	}

	public void askQuestion(String q) {
		System.out.println("问题是:" + q);
		new Thread(new Runnable() {
			public void run() {
				li.exe(Wang.this, q);
			}
		}).start();;
		play();
		
	}
	
	public void play(){
		System.out.println("我去逛街了!");
	}
}
