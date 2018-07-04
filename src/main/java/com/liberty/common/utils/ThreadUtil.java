package com.liberty.common.utils;

public class ThreadUtil {

  /**
   * 通过线程组获得线程
   *
   * @param threadId 线程ID
   * @return 对应的线程
   */
  public static Thread findThread(long threadId) {
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    while(group != null) {
      Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
      int count = group.enumerate(threads, true);
      for(int i = 0; i < count; i++) {
        if(threadId == threads[i].getId()) {
          return threads[i];
        }
      }
      group = group.getParent();
    }
    return null;
  }

  /**
   * 通过线程组获得线程
   *
   * @param threadName 线程名称
   * @return 对应的线程
   */
  public static Thread findThread(String threadName) {
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    while(group != null) {
      Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
      int count = group.enumerate(threads, true);
      for(int i = 0; i < count; i++) {
        if(threadName.equals(threads[i].getName())) {
          return threads[i];
        }
      }
      group = group.getParent();
    }
    return null;
  }

}
