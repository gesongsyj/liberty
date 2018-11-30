package com.liberty.system.downloader;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;

public interface DownLoader {
	List<Kline> downLoad(Currency currency, String type, String method, Date date);
}
