package com.tf.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class Logs {
	public static void WriteLogs(Exception exception) {
		File file = new File(System.getProperty("user.dir")
				+ "\\logs\\"
				+ new SimpleDateFormat("yyyy-MM-dd").format(System
						.currentTimeMillis()) + ".txt");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter fw;
		try {
			fw = new FileWriter(file, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("--------------------"
					+ (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
							.format(System.currentTimeMillis()))
					+ "--------------------");
			exception.printStackTrace(pw);
			pw.close();
			fw.close();
		} catch (IOException e) {
			Logs.WriteLogs(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
