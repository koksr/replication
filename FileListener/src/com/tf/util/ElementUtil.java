package com.tf.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.tf.model.ElementType;

public class ElementUtil {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd  HH:mm:ss");

	/**
	 * 
	 * @param str
	 * @param strs
	 * @return
	 */
	public static boolean in(String str, String[] strs) {
		for (String s : strs) {
			if (str.toLowerCase().equals(s))
				return true;
		}
		return false;
	}

	/**
	 * @param fileName
	 * @return 扩展名
	 */
	public static String getFileSuffix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(splitIndex + 1);
	}

	/**
	 * @param fileName
	 * @return 真实文件名
	 */
	public static String getFilePrefix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, splitIndex);
	}

	public static String getRealName(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		} else {
			return getFilePrefix(file.getName());
		}
	}

	public static int getType(String name) {
		String[] videoType = { "mkv", "ts", "m2ts", "mts", "tp", "trp", "wmv",
				"Ifo", "iso", "dat", "avi", "asf", "mp4", "mov", "rm", "rmvb",
				"divx", "xvid", "flv", "f4v", "mpg", "vob" };
		String[] audioType = { "mp3", "wma", "wav", "ogg", "aac", "lpcm",
				"flac", "ac3" };
		String[] flashType = { "swf" };
		String[] imageType = { "jpg", "bmp", "png", "jpeg", "gif" };
		String[] docType = { "xlsx", "docx", "pptx", "doc", "ppt", "xls", "pdf" };
		if (in(getFileSuffix(name), videoType)) {
			return ElementType.video.getValue();
		} else if (in(getFileSuffix(name), audioType)) {
			return ElementType.audio.getValue();
		} else if (in(getFileSuffix(name), imageType)) {
			return ElementType.image.getValue();
		} else if (in(getFileSuffix(name), flashType)) {
			return ElementType.flash.getValue();
		} else if (in(getFileSuffix(name), docType)) {
			return ElementType.document.getValue();
		} else {
			return ElementType.other.getValue();
		}

	}

	public static boolean checkMd5(File file, String md5) {
		try {
			return MD5Util.getFileMD5String(file).equals(md5);
		} catch (IOException e) {
			Logs.WriteLogs(e);
			e.printStackTrace();
			return false;
		}
	}

	public static void CopyImg(String imgPath) {
		File source = new File(imgPath);
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(source.getParentFile().getAbsolutePath()
					+ "\\img" + source.getName());
			byte[] buffer = new byte[1024];
			while (true) {
				int size = in.read(buffer);
				out.write(buffer);
				if (size == -1) {
					out.flush();
					out.close();
					in.close();
					return;
				}
			}
		} catch (Exception e) {
			Logs.WriteLogs(e);
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
			} catch (IOException e) {
				Logs.WriteLogs(e);
				e.printStackTrace();
			}
		}
	}

	public static void CopyImg(String sourcePath, String destPath) {
		File source = new File(sourcePath);
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(destPath);
			byte[] buffer = new byte[1024];
			while (true) {
				int size = in.read(buffer);
				out.write(buffer);
				if (size == -1) {
					out.flush();
					out.close();
					in.close();
					return;
				}
			}
		} catch (Exception e) {
			Logs.WriteLogs(e);
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
			} catch (IOException e) {
				Logs.WriteLogs(e);
				e.printStackTrace();
			}
		}
	}

	public static void thumImg(String imgPath) {
		File source = new File(imgPath);
		Image srcFile = null;
		try {
			srcFile = ImageIO.read(source);
		} catch (IOException e) {
			e.printStackTrace();
			Logs.WriteLogs(e);
		}
		int width = srcFile.getWidth(null);
		int height = srcFile.getHeight(null);
		float arg = 0;
		if (height >= width) {
			arg = (float) (300.00000000 / height);
			height = 300;
			width = new Float(width * arg).intValue();
		} else {

			arg = (float) (300.00000000 / width);
			width = 300;
			height = new Float(arg * height).intValue();
		}
		BufferedImage tag = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
		// String filePrex = oldFile.getName().substring(0,
		// oldFile.getName().indexOf('.'));
		// newImage = filePrex + smallIcon
		// + oldFile.getName().substring(filePrex.length());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(source.getParentFile().getAbsolutePath()
					+ "\\img" + source.getName());
		} catch (FileNotFoundException e) {
			Logs.WriteLogs(e);
			e.printStackTrace();
		}
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
		jep.setQuality(1, true);
		try {
			encoder.encode(tag, jep);
		} catch (ImageFormatException e) {
			Logs.WriteLogs(e);
			e.printStackTrace();
		} catch (IOException e) {
			Logs.WriteLogs(e);
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				Logs.WriteLogs(e);
				e.printStackTrace();
			}
		}
	}

	public static File zipImageFile(String path, long maxSize) {
		return zipImageFile(path, maxSize, 1.5);
	}

	/**
	 * 
	 * @param path
	 * @param maxSize
	 * @param scale
	 *            压缩比
	 * @return 压缩后的文件
	 */
	public static File zipImageFile(String path, long maxSize, double scale) {
		File newFile = new File(path);
		int w = 0;
		int h = 0;
		if (!newFile.exists()) {
			return null;
		}
		File oldFile = new File(newFile.getAbsolutePath() + ".temp");
		newFile.renameTo(oldFile);
		try {
			Image srcFile = ImageIO.read(oldFile);
			w = (int) (srcFile.getWidth(null) / scale);
			h = (int) (srcFile.getHeight(null) / scale);
			BufferedImage tag = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(srcFile, 0, 0, w, h, null);
			// String filePrex = oldFile.getName().substring(0,
			// oldFile.getName().indexOf('.'));
			// newImage = filePrex + smallIcon
			// + oldFile.getName().substring(filePrex.length());
			FileOutputStream out = new FileOutputStream(newFile);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
			jep.setQuality(1, true);
			encoder.encode(tag, jep);
			out.close();
			oldFile.delete();
		} catch (java.lang.OutOfMemoryError err) {
			zipImageFile(path, maxSize, scale + 0.5);
		} catch (FileNotFoundException e) {
			Logs.WriteLogs(e);
			e.printStackTrace();
		} catch (IOException e) {
			Logs.WriteLogs(e);
			e.printStackTrace();
		}
		if (newFile.length() > maxSize) {
			zipImageFile(path, maxSize, scale + 0.5);
		}
		return newFile;

	}

	public static void createDirs() {
		List<String> list = new ArrayList<String>(Arrays.asList(
				"playlistXmlFile", "processed", "programXmlFile", "screen",
				"scrollingnewsXmlFile", "templateXmlFile", "weatherXmlFile",
				"logs"));
		for (String str : list) {
			File file = new File(System.getProperty("user.dir") + "\\" + str);
			if (!file.exists()) {
				file.mkdir();
			}
		}
	}

	public static boolean renameTO(File srcFile, File destFile)
			throws IOException {
		if (!destFile.getParentFile().exists())
			destFile.getParentFile().mkdirs();

		FileUtils.copyFile(srcFile, destFile);

		if (destFile.exists()) {
			return true;
		} else {
			return false;
		}

	}

	public static void main(String[] args) {
		float a = (float) (300.0000000000 / 2984);
		System.out.println(a);
		thumImg("F:\\baidu.jpeg");
	}
}
