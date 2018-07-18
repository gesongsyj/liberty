package com.liberty.system.service;

import java.util.List;
import java.util.Map;

import com.liberty.system.model.Kline;

public interface DownLoader {
	
	public List<Kline> downLoad(String code, String type, String method, Kline lastKline);
}
