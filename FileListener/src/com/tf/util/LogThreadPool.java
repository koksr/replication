package com.tf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.tf.ctrl.DBhandle;
import com.tf.view.Listener;

public class LogThreadPool extends Thread {
	private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	private ThreadPoolExecutor executor;
	private static final LogThreadPool single = new LogThreadPool();
	public static boolean runStatus = false;
	public static Set<File> fileSet = new HashSet<File>();

	public static LogThreadPool getInstance() {
		return single;
	}

	public LogThreadPool() {
		executor = new ThreadPoolExecutor(3, 10, 1000, TimeUnit.DAYS, queue);
	}

	public LogThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit) {
		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, unit, queue);
	}

	public void run() {
		while (true) {
			File[] files = new File(System.getProperty("user.dir")
					+ File.separator + "statistics").listFiles();
			for (File f : files) {
				int res = 0 ;
				String time = f.getName().split("@")[0];
				String mac = f.getName().split("@")[1].substring(0, f.getName()
						.split("@")[1].lastIndexOf("."));
				if (checkTime(time)) {
					InputStreamReader input = null;
					BufferedReader bufferedReader = null;
					try {
						input = new InputStreamReader(new FileInputStream(f),
								"unicode");
					} catch (FileNotFoundException e) {
						Logs.WriteLogs(e);
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						Logs.WriteLogs(e);
						e.printStackTrace();
					}
					bufferedReader = new BufferedReader(input);
					String sql = null;
				
					try {
						while ((sql = bufferedReader.readLine()) != null) {
//							Logs.WriteLogs("sql:--");
//							Logs.WriteLogs(sql);
						res = 	new DBhandle().writeLogs(unicodeToUtf8(sql), mac);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							input.close();
							bufferedReader.close();
						} catch (IOException e) {
							Logs.WriteLogs(e);
							e.printStackTrace();
						}
					}
					try {
						if(res!=-5){FileUtils.moveToDirectory(f,
								new File(System.getProperty("user.dir")
										+ File.separator + "statistics_old"),
								true);}
						// boolean flag =ElementUtil.renameTO(f, new
						// File(System.getProperty("user.dir")+File.separator+"statistics_old"+File.separator+f.getName()));
						// System.out.println(flag);
					} catch (org.apache.commons.io.FileExistsException e) {
						Logs.WriteLogs(e);
						f.delete();
					} catch (IOException e) {
						Logs.WriteLogs(e);
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(1000 * 60 * 60 * 4);
			} catch (InterruptedException e) {
				Logs.WriteLogs(e);
				Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");
			}

		}

	}

	public void put(Thread thread) {
		executor.execute(thread);
	}

	public boolean checkTime(String time) {
		Long timeStamp = 0L;
		try {
			timeStamp = new SimpleDateFormat("yyyy-MM-dd").parse(time)
					.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (System.currentTimeMillis() - timeStamp) > (1000 * 60 * 60 * 24);
	}

	public static String utf8ToUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				// 英文及数字等
				sb.append(myBuffer[i]);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				// 全角半角字符
				int j = (int) myBuffer[i] - 65248;
				sb.append((char) j);
			} else {
				// 汉字
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\u" + hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	public static void main(String[] args) {
		LogThreadPool.getInstance().start();
	}
}
