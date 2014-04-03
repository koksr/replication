package com.tf.util;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.tf.model.Element;
import com.tf.model.ElementType;
import com.tf.view.Listener;

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

	/**
	 * 
	 * @param element
	 * @param file
	 * @return 生成缩略图并返回缩略图相对路径
	 */
	public static String getThumbnail(Element element, File file) {
		String thumName = null;
		if (element.getType() == ElementType.video.getValue()
				|| element.getType() == ElementType.flash.getValue()) {
			File temp = new File(file.getParentFile().getAbsolutePath() + "\\"
					+ new Date().getTime() + ".bat");
			FileWriter fw = null;
			thumName = file.getParentFile().getAbsolutePath() + "\\img"
					+ getFilePrefix(file.getName()) + ".jpg";
			try {
				fw = new FileWriter(temp);
				String command = "D:\r\n cd D:\\ffmpeg\\bin\\\r\nffmpeg.exe -i \""
						+ file.getAbsolutePath()
						+ "\" -ss 5 -vframes 1 -r 1 -ac 1 -ab 2 -s "
						+ element.getResolution()
						+ " -f  image2 \""
						+ thumName
						+ "\"\r\n del \"" + temp.getAbsolutePath() + "\"";
				fw.write(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logs.WriteLogs(e);
				Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");
				System.exit(0);
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						Logs.WriteLogs(e);
						Listener.area.append("异常警告\r\n" + e.getMessage()
								+ "\r\n");
						System.exit(0);
					}
				}
			}
			try {
				Desktop.getDesktop().open(temp);
			} catch (IOException e) {
				Logs.WriteLogs(e);
				Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");
			}

		}

		if (!new File(thumName).exists()) {

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Logs.WriteLogs(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		Listener.area.append(dateFormat.format(System.currentTimeMillis())
				+ "	" + element.getFileName() + "缩略图生成成功\r\n");
		return "ftpFile\\processed\\img" + getFilePrefix(file.getName())
				+ ".jpg";
	}

	/**
	 * 
	 * @param file
	 * @param element
	 * @return element
	 */
	public static Element getInfo(File file, Element element) {
		String result = null;
		List<String> commend = new java.util.ArrayList<String>();

		commend.add("D:\\ffmpeg\\bin\\ffmpeg");// 鍙互璁剧疆鐜鍙橀噺浠庤�鐪佸幓杩欒
		commend.add("ffmpeg");
		commend.add("-i");
		commend.add(file.getAbsolutePath());
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			builder.redirectErrorStream(true);
			Process p = builder.start();

			// 1. start
			BufferedReader buf = null; // 淇濆瓨ffmpeg鐨勮緭鍑虹粨鏋滄祦
			String line = null;
			// read the standard output

			buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

			StringBuffer sb = new StringBuffer();
			while ((line = buf.readLine()) != null) {
				sb.append(line);
				continue;
			}
			int ret = p.waitFor();// 杩欓噷绾跨▼闃诲锛屽皢绛夊緟澶栭儴杞崲杩涚▼杩愯鎴愬姛杩愯缁撴潫鍚庯紝鎵嶅線涓嬫墽琛�
			// 1. end
			result = sb.toString();
		} catch (Exception e) {
			Logs.WriteLogs(e);
			Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");

		}
		PatternCompiler compiler = new Perl5Compiler();
		try {
			String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
			String regexVideo = "Video: (.*?), (.*?), (.*?)[,\\s]";
			String regexAudio = "Audio: (\\w*), (\\d*) Hz";

			Pattern patternDuration = compiler.compile(regexDuration,
					Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcherDuration = new Perl5Matcher();
			if (matcherDuration.contains(result, patternDuration)) {
				MatchResult re = matcherDuration.getMatch();
				// 1鎾斁鏃堕棿2寮�鏃堕 棿3鐮佺巼
				element.setTimeLength(re.group(1));
			}

			Pattern patternVideo = compiler.compile(regexVideo,
					Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcherVideo = new Perl5Matcher();

			if (matcherVideo.contains(result, patternVideo)) {
				MatchResult re = matcherVideo.getMatch();
				// 1缂栫爜鏍煎紡2瑙嗛鏍煎紡3鍒嗚鲸鐜�
				element.setResolution(re.group(3));
			}

			Pattern patternAudio = compiler.compile(regexAudio,
					Perl5Compiler.CASE_INSENSITIVE_MASK);
			PatternMatcher matcherAudio = new Perl5Matcher();

			if (matcherAudio.contains(result, patternAudio)) {
				MatchResult re = matcherAudio.getMatch();
				// TODO element.setType()
			}

		} catch (MalformedPatternException e) {
			Logs.WriteLogs(e);
			Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");
		}
		int type = element.getType();
		if (type == ElementType.video.getValue()
				|| type == ElementType.image.getValue()
				|| type == ElementType.html.getValue()) {
			if (element.getResolution() == null) {
				getInfo(file, element);
			} else {
				Listener.area.append(dateFormat.format(System
						.currentTimeMillis())
						+ "	"
						+ element.getFileName()
						+ "信息读取成功\r\n");
			}
		}
		return element;
	}

	public static int getType(String name) {
		String[] videoType = { "mkv", "ts", "m2ts", "mts", "tp", "trp", "wmv",
				"Ifo", "iso", "dat", "avi", "asf", "mp4", "mov", "rm", "rmvb",
				"divx", "xvid","flv","f4v" };
		String[] audioType = { "mp3", "wma", "wav", "ogg", "aac", "lpcm",
				"flac", "ac3" };
		String[] flashType = { "swf" };
		String[] imageType = { "jpg", "bmp", "png", "jpeg", "gif" };
		String[] docType = { "xlsx", "docx", "pptx", "doc", "ppt", "xls",
				"txt", "pdf" };
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

	public static File zipImageFile(String path, long maxSize) {
		return zipImageFile(path, maxSize, 1.5);
	}

	/**
	 * 等比例压缩图片文件<br>
	 * 先保存原文件，再压缩、上传
	 * 
	 * @param oldFile
	 *            要进行压缩的文件
	 * @param newFile
	 *            新文件
	 * @param width
	 *            宽度 //设置宽度时（高度传入0，等比例缩放）
	 * @param height
	 *            高度 //设置高度时（宽度传入0，等比例缩放）
	 * @param quality
	 *            质量
	 * @return 返回压缩后的文件的全路径
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
}
