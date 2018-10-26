package com.liberty.system.test;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Queue<T> {
	private int waitemptysize = 0;
	private int waitfullsize = 0;
	private Object[] elements;
	private Lock lock = new ReentrantLock();
	// 队列是否为空
	private Condition notempty = lock.newCondition();
	// 队列是否已满
	private Condition notfull = lock.newCondition();
	// 队列长度,插入元素下标,删除元素下标
	private int length = 0, addIndex = 0, removeIndex = 0;

	public Queue(int size) {
		elements = new Object[size];
	}

	// 插入元素,队列已满则等待
	public void add(T object) throws InterruptedException {
		lock.lock();
		try {
			while (length == elements.length) {
				waitfullsize++;
				System.err.println("队列已满,等待.......线程名:"+Thread.currentThread().getName());
				System.out.println("waitfullsize:" + waitfullsize);
				notfull.await();// 执行await()方法后进入等待队列,执行signal()方法后回到同步队列,从这里继续往后执行
				waitfullsize--;
				System.out.println("队列继续执行....线程名:"+Thread.currentThread().getName());
			}
			elements[addIndex] = object;
			System.out.println("线程执行....线程名:"+Thread.currentThread().getName());
			System.out.println("addIndex:" + addIndex);
			if (addIndex++ == elements.length) {
				addIndex = 0;
			}
			length++;
			notempty.signal();
		} finally {
			lock.unlock();
		}
	}

	// 删除队列,队列为空则等待
	public T remove() throws InterruptedException {
		lock.lock();
		try {
			while (0 == length) {
				waitemptysize++;
				System.out.println("队列为空,等待......线程名:"+Thread.currentThread().getName());
				System.out.println("waitemptysize:" + waitemptysize);
				notempty.await();// 执行await()方法后进入等待队列,执行signal()方法后回到同步队列,从这里继续往后执行
				System.out.println("队列继续执行....线程名:"+Thread.currentThread().getName());
				waitemptysize--;
			}
			Object element = elements[removeIndex];
			System.out.println("线程执行....线程名:"+Thread.currentThread().getName());
			System.out.println("removeIndex:" + removeIndex);
			if (removeIndex++ == elements.length) {
				removeIndex = 0;
			}
			length--;
			notfull.signal();
			return (T) element;
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Vector<Thread> threads = new Vector<Thread>(100);
		final Queue<Integer> queue = new Queue<Integer>(100);
		for (int i = 0; i < 10; i++) {
			Thread ithread = new Thread(new Runnable() {
				@Override
				public void run() {
					int j = (int) (1 + Math.random() * (10 - 1 + 1));
					if (j / 2 != 0) {
						try {
							queue.remove();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							queue.add(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			});
			ithread.setName(String.valueOf(i));
			threads.add(ithread);
			ithread.start();
		}
		for (int i = 10; i < 20; i++) {
			Thread ithread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						queue.add(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			});
			ithread.setName(String.valueOf(i));
			threads.add(ithread);
			ithread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
	}
}
