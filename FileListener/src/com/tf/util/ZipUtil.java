package com.tf.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	
	public static void zipFile(String inputFileName,String zipFileName)
			throws Exception {
		System.out.println("开始压缩文件");
		Date date = new Date();
		ZipOutputStream out = new ZipOutputStream(
				new FileOutputStream(zipFileName));
//		out.setEncoding("GBK");
		File inputFile = new File(inputFileName);
		zipIt(out, inputFile, "", true);
		out.close();
		System.out.println("压缩完成，用时"+(new Date().getTime()-date.getTime())+"毫秒");
	}
	
	
	private static void zipIt(ZipOutputStream out, File f,
			String base, boolean first) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			if (first) {
				first = false;
			} else {
				base = base + "\\";
			}
			for (int i = 0; i < fl.length; i++) {
				zipIt(out, fl[i], base + fl[i].getName(), first);
			}
		} else {
			if (first) {
				base = f.getName();
			}
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}

	public static void unZipFile(String unZipFileName, String unZipPath)
			throws Exception {
		System.out.println("开始解压缩文件");
		Date date = new Date();
		ZipFile zipFile = new ZipFile(
				unZipFileName);
		unZipFileByOpache(zipFile, unZipPath);
		System.out.println("解压缩完成，用时"+(new Date().getTime()-date.getTime())+"毫秒");
	}
	
	
	@SuppressWarnings("rawtypes")
	private static void unZipFileByOpache(ZipFile zipFile,
			String unZipRoot) throws Exception, IOException {
		Enumeration e = zipFile.entries();
		ZipEntry zipEntry;
		while (e.hasMoreElements()) {
			zipEntry = (ZipEntry) e.nextElement();
			InputStream fis = zipFile.getInputStream(zipEntry);
			if (zipEntry.isDirectory()) {
			} else {
				File file = new File(unZipRoot + File.separator
						+ zipEntry.getName());
				File parentFile = file.getParentFile();
				parentFile.mkdirs();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int len;
				while ((len = fis.read(b, 0, b.length)) != -1) {
					fos.write(b, 0, len);
				}
				fos.close();
				fis.close();
			}
		}
	}


}