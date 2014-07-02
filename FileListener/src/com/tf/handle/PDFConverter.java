package com.tf.handle;

import java.io.File;
import java.io.IOException;

import com.tf.init.PDFInit;
import com.tf.util.ElementUtil;

public class PDFConverter {

	private File sourceFile;
	private File destFile;

	public PDFConverter(File file) {
		this.sourceFile = file;
		this.destFile = new File(file.getParentFile().getAbsoluteFile()
				+ File.separator
				+ ElementUtil.getRealName(file.getAbsolutePath()) + ".pdf");
	}

	public File Convert() {
		try {
			String cmd = PDFInit.getPath() + " /hidden " + sourceFile.getAbsolutePath()
					+ " " + destFile.getAbsolutePath();
			//System.out.println(cmd);
			Runtime.getRuntime().exec("cmd /c " + cmd);
			while (!destFile.exists()) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile;
	}

}
