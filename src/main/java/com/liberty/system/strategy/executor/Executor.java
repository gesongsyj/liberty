package com.liberty.system.strategy.executor;

import java.util.List;
import java.util.Vector;

import com.liberty.system.model.Currency;

public interface Executor {
	Vector<Currency> execute(String code);
}
