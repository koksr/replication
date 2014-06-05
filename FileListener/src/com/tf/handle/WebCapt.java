package com.tf.handle;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import com.tf.init.IECaptInit;

public class WebCapt {
	public synchronized static File Capt(String URL, String dir, Long timeStamp)
			throws IOException, InterruptedException {
		int responseCode = 0;
		File file = null;
		HttpURLConnection conn;
		try {
			URL res = new URL(URL);
			conn = (HttpURLConnection) res.openConnection();
			conn.setConnectTimeout(60);
			responseCode = conn.getResponseCode();
		} catch (UnknownHostException e) {
			responseCode = 404;
		} catch (IOException e) {
			responseCode = 500;
		}
		if (responseCode != 200) {
			file = new File(dir + File.separator + "processed" + File.separator
					+ "fail.png");
		} else {

			file = new File(dir + File.separator + "processed" + File.separator
					+ timeStamp + ".jpg");
			String cmd = IECaptInit.getPath() + " --url=" + URL + " --out=\""
					+ file.getAbsolutePath() + "\"";
			// System.out.println(cmd);
			Runtime.getRuntime().exec("cmd /c " + cmd);
			while (!file.exists()) {
				Thread.sleep(5000);
			}
		}
		return file;
	}
}
