package com.tf.handle;

import java.io.File;
import java.io.IOException;

import com.tf.init.XPSInit;
import com.tf.util.ElementUtil;

public class XPSConverter {

	private File sourceFile;
	private File destFile;

	public XPSConverter(File file) {
		this.sourceFile = file;
		this.destFile = new File(file.getParentFile().getAbsoluteFile()
				+ File.separator
				+ ElementUtil.getRealName(file.getAbsolutePath()) + ".xps");
	}

	public File Convert() {
		try {
			String cmd = XPSInit.getPath() +" -o "+destFile.getParent()+" --prefix " +ElementUtil.getFilePrefix(destFile.getName())+" "+sourceFile.getAbsolutePath();
			System.out.println(cmd);
			Runtime.getRuntime().exec("cmd /c " + cmd);
			while (!destFile.exists()) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return destFile;
	}

}
