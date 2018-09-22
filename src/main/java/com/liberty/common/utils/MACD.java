package com.liberty.common.utils;

import java.util.List;

import com.liberty.system.model.Kline;

public class MACD {
	private int sp, lp, dp;

	public MACD() {
		this(12, 26, 9);
	}

	public MACD(int sp, int lp, int dp) {
		this.sp = sp;
		this.lp = lp;
		this.dp = dp;
	}

	public List<Kline> calMacd(List<Kline> klines, Kline lastKline) {
		if (lastKline == null) {
			for (int i = 0; i < klines.size(); i++) {
				if (i == 0) {
					klines.get(i).setEmaS(klines.get(i).getClose());
					klines.get(i).setEmaL(klines.get(i).getClose());
					klines.get(i).setDiff(0.0);
					klines.get(i).setDea(0.0);
					klines.get(i).setBar(0.0);
				} else {
					klines.get(i).setEmaS(klines.get(i - 1).getEmaS() * (sp - 1) / (sp + 1)
							+ klines.get(i).getClose() * 2 / (sp + 1));
					klines.get(i).setEmaL(klines.get(i - 1).getEmaL() * (lp - 1) / (lp + 1)
							+ klines.get(i).getClose() * 2 / (lp + 1));
					klines.get(i).setDiff(klines.get(i).getEmaS() - klines.get(i).getEmaL());
					klines.get(i).setDea(
							klines.get(i - 1).getDea() * (dp - 1) / (dp + 1) + klines.get(i).getDiff() * 2 / (dp + 1));
					klines.get(i).setBar(2 * (klines.get(i).getDiff() - klines.get(i).getDea()));
				}
			}
		} else {
			for (int i = 0; i < klines.size(); i++) {
				if (i == 0) {
					klines.get(i).setEmaS(
							lastKline.getEmaS() * (sp - 1) / (sp + 1) + klines.get(i).getClose() * 2 / (sp + 1));
					klines.get(i).setEmaL(
							lastKline.getEmaL() * (lp - 1) / (lp + 1) + klines.get(i).getClose() * 2 / (lp + 1));
					klines.get(i).setDiff(klines.get(i).getEmaS() - klines.get(i).getEmaL());
					klines.get(i)
							.setDea(lastKline.getDea() * (dp - 1) / (dp + 1) + klines.get(i).getDiff() * 2 / (dp + 1));
					klines.get(i).setBar(2 * (klines.get(i).getDiff() - klines.get(i).getDea()));
				} else {
					klines.get(i).setEmaS(klines.get(i - 1).getEmaS() * (sp - 1) / (sp + 1)
							+ klines.get(i).getClose() * 2 / (sp + 1));
					klines.get(i).setEmaL(klines.get(i - 1).getEmaL() * (lp - 1) / (lp + 1)
							+ klines.get(i).getClose() * 2 / (lp + 1));
					klines.get(i).setDiff(klines.get(i).getEmaS() - klines.get(i).getEmaL());
					klines.get(i).setDea(
							klines.get(i - 1).getDea() * (dp - 1) / (dp + 1) + klines.get(i).getDiff() * 2 / (dp + 1));
					klines.get(i).setBar(2 * (klines.get(i).getDiff() - klines.get(i).getDea()));
				}
			}
		}
		return klines;
	}
}
