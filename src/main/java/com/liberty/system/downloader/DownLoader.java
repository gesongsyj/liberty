package com.liberty.system.downloader;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liberty.system.model.Currency;
import com.liberty.system.model.Kline;

public interface DownLoader {
	/**
	 * 下载传入日期以后的K线
	 * @param currency
	 * @param type
	 * @param method
	 * @param date
	 * @return
	 */
	List<Kline> downLoad(Currency currency, String type, String method, Date date);
	/**
	 * 根据size下载一定数量的k线
	 * @param currency
	 * @param type
	 * @param method
	 * @param size
	 * @return
	 */
	List<Kline> downLoad(Currency currency, String type, String method, int size);
	/**
	 * 实时的交易信息,打单信息,直接put到currency中
	 * @param currency
	 * @param type
	 * @param method
	 * @return
	 */
	void downLoadRealTimeData(Currency currency, String type, String method);
	
}
