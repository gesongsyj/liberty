package com.liberty.common.utils;

public class CountUtil {

  int count = 0;
  int limit = 0;

  public CountUtil(int limit) {
    count = 0;
    this.limit = limit;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void plusCount() {
    this.count += 1;
  }

  public boolean isOffline() {
    return this.count > limit;
  }

}
