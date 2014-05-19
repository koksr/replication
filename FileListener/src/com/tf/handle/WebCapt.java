package com.tf.handle;

import java.io.File;
import java.io.IOException;

import com.tf.init.IECaptInit;

public class WebCapt {
	public synchronized static File Capt(String URL, String dir, Long timeStamp)
			throws IOException, InterruptedException {
		File file = new File(dir + File.separator+"processed"+File.separator + timeStamp + ".jpg");
		String cmd = IECaptInit.getPath() + " --url=" + URL + " --out=\""
				+ file.getAbsolutePath() + "\"";
		// System.out.println(cmd);
		Runtime.getRuntime().exec("cmd /c " + cmd);
		while (!file.exists()) {
			Thread.sleep(5000);
		}
		return file;
	}
}
