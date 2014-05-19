package com.tf.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tf.ctrl.Execute;
import com.tf.ctrl.ExecuteFail;
import com.tf.ctrl.OfficeExecute;
import com.tf.ctrl.WebExecute;
import com.tf.model.ElementType;
import com.tf.view.Listener;

public class ThreadPool extends Thread {
	private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	private ThreadPoolExecutor executor;
	private static final ThreadPool single = new ThreadPool();
	public static boolean runStatus = false;
	public static Set<File> fileSet= new HashSet<File>();
	public static ThreadPool getInstance() {
		return single;
	}

	public ThreadPool() {
		executor = new ThreadPoolExecutor(3, 10, 1000, TimeUnit.DAYS, queue);
	}

	public ThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit) {
		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, unit, queue);
	}

	public void run() {
		while(runStatus){
			File[] files = new File(System.getProperty("user.dir")).listFiles();
			for (File f : files) {
				if (!f.isDirectory() && (f.getName().indexOf("&_@") != -1)
						&& (f.getName().indexOf("&_^") != -1)
						&& (!f.getName().endsWith(".temp~"))){
					if(fileSet.add(f)){
						String md5 = f.getName().split("&_\\u005E")[0];
						String name = f.getName().split("&_\\u005E")[1].split("&_@")[0];
						String creater = f.getName().split("&_\\u005E")[1].split("&_@")[1];
						Listener.area.append(ElementUtil.dateFormat.format(System
								.currentTimeMillis()) + "	开始处理" + name + "\r\n");
						if(md5.equals("saveHtml")){
							this.put(new WebExecute(name,creater,f));
						}else{
							if(ElementUtil.checkMd5(f, md5)){
								if(ElementUtil.getType(name)==ElementType.document.getValue())
									this.put(new OfficeExecute(f, name, creater, md5));
								else
								this.put(new Execute(f, md5, name, creater));
							}else{
								this.put(new ExecuteFail(name, creater, f));
							}
						}
					}else{
						continue;
					}
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Logs.WriteLogs(e);
				Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");
			}
			
		}

	}

	public void put(Thread thread) {
		executor.execute(thread);
	}

	public void shutdown() {
		runStatus = false;
		executor.shutdownNow();
	
	}

}