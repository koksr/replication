package com.tf.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class SourceUtil {
	private static final String config = "DBConf.xml";
	private static final String audioImg = "audio.png";
	private static final String failImg = "fail.png";
	private static final String plugins = "plugins.zip";
	private static final String pauseImg = "pause.png";
	private void SourceFile(String fileName) {
		File f  = new File(System.getProperty("user.dir") + "\\" + fileName);
		if(f.exists()){
			return;
		}
		InputStream is = this.getClass().getResourceAsStream("/"+fileName);
		// FileInputStream fis = (FileInputStream) this.getClass()
		// .getResourceAsStream("/"+fileName);
		// ImageIO.read(is);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = is.read(buffer)) != -1) {
				fos.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(fos!=null){
					fos.flush();
					fos.close();
				}
				if(is!=null){
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void SourceImg(String fileName) {
		InputStream in = null;
		FileOutputStream out = null;
		try {
			File f = new File(System.getProperty("user.dir") + "\\processed\\"
					+ fileName);
			if(f.exists()){
				return;
			}
			in = this.getClass().getResourceAsStream("/" + fileName);
			Image srcFile = ImageIO.read(in);
			BufferedImage tag = new BufferedImage(srcFile.getWidth(null),
					srcFile.getHeight(null), BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(srcFile, 0, 0, srcFile.getWidth(null),
					srcFile.getHeight(null), null);
			out = new FileOutputStream(f);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
			jep.setQuality(1, true);
			encoder.encode(tag, jep);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out!=null){
					out.flush();
					out.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public static void getSource() {
		new SourceUtil().SourceImg(SourceUtil.pauseImg);
		new SourceUtil().SourceFile(SourceUtil.config);
		new SourceUtil().SourceImg(SourceUtil.audioImg);
		new SourceUtil().SourceImg(SourceUtil.failImg);
		new SourceUtil().SourceFile(SourceUtil.plugins);
		File zipFile = new File(System.getProperty("user.dir") + File.separator + SourceUtil.plugins);
		File dirFile = new File(System.getProperty("user.dir") + File.separator + ElementUtil.getFilePrefix(SourceUtil.plugins));
		if(!dirFile.exists()){
			try {
				ZipUtil.unZipFile(zipFile.getAbsolutePath(), dirFile.getParent());
				if(dirFile.listFiles().length==5){
					XmlUtil.saveOrUpdate("plugins", dirFile.getAbsolutePath()+File.separator+"bin");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
