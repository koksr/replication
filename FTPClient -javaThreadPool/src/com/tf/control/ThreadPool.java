package com.tf.control;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	private ThreadPoolExecutor executor;
	private static final ThreadPool single = new ThreadPool();
	public static ThreadPool getInstance() {
		return single;
	}

	/*
	 * 构造一个默认的线程池
	 */
	public ThreadPool() {
		executor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.DAYS, queue);
	}

	/*
	 * 构造一个自定义的线程池
	 */
	public ThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit) {
		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, unit, queue);
	}

	public void start(Thread write,Thread read){
		executor.execute(write);
		read.start();
	}

}