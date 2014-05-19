package com.tf.handle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.tf.init.FfmpegInit;
import com.tf.model.Element;
import com.tf.model.ElementType;
import com.tf.util.ElementUtil;
import com.tf.util.Logs;
import com.tf.view.Listener;

public class Mediahandle {

	/**
	 * 
	 * @param file
	 * @param element
	 * @return element.timeLength
	 * @return element.resolution
	 */
	@SuppressWarnings("unused")
	public static Element getInfo(File file, Element element) {
		String result = null;
		List<String> commend = new java.util.ArrayList<String>();
		commend.add("\"" + FfmpegInit.getPath() + "\"");
		commend.add("-i");
		commend.add("\"" + file.getAbsolutePath() + "\"");
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			// 1. start
			BufferedReader buf = null;
			String line = null;
			// read the standard output
			buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while ((line = buf.readLine()) != null) {
				sb.append(line);
				continue;
			}
			int ret = p.waitFor();
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
				element.setResolution("300x300");
			} else {
				Listener.area.append(ElementUtil.dateFormat.format(System
						.currentTimeMillis())
						+ "	"
						+ element.getFileName()
						+ "信息读取成功\r\n");
			}
		}
		return element;
	}

	/**
	 * 
	 * @param element
	 * @param file
	 * @return 生成缩略图并返回缩略图相对路径
	 */
	/*
	 * public static String getThumbnail(Element element, File file) { String
	 * thumName = null; if (element.getType() == ElementType.video.getValue() ||
	 * element.getType() == ElementType.flash.getValue()) { File temp = new
	 * File(file.getParentFile().getAbsolutePath() + "\\" + new Date().getTime()
	 * + ".bat"); FileWriter fw = null; thumName =
	 * file.getParentFile().getAbsolutePath() + "\\img" +
	 * ElementUtil.getFilePrefix(file.getName()) + ".jpg"; try { fw = new
	 * FileWriter(temp); String command =
	 * "D:\r\n cd D:\\ffmpeg\\bin\\\r\nffmpeg.exe -i \"" +
	 * file.getAbsolutePath() + "\" -ss 5 -vframes 1 -r 1 -ac 1 -ab 2 -s " +
	 * element.getResolution() + " -f  image2 \"" + thumName + "\"\r\n del \"" +
	 * temp.getAbsolutePath() + "\""; fw.write(command);
	 * 
	 * 
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * Logs.WriteLogs(e); Listener.area.append("异常警告\r\n" + e.getMessage() +
	 * "\r\n"); System.exit(0); } finally { if (fw != null) { try { fw.close();
	 * } catch (IOException e) { Logs.WriteLogs(e);
	 * Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");
	 * System.exit(0); } } } try { Desktop.getDesktop().open(temp); } catch
	 * (IOException e) { Logs.WriteLogs(e); Listener.area.append("异常警告\r\n" +
	 * e.getMessage() + "\r\n"); }
	 * 
	 * }
	 * 
	 * if (!new File(thumName).exists()) {
	 * 
	 * try { Thread.sleep(3000); } catch (InterruptedException e) {
	 * Logs.WriteLogs(e); // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * } Listener.area.append(ElementUtil.dateFormat.format(System
	 * .currentTimeMillis()) + "	" + element.getFileName() + "缩略图生成成功\r\n");
	 * return "ftpFile\\processed\\img" +
	 * ElementUtil.getFilePrefix(file.getName()) + ".jpg"; }
	 */
	public static String getGif(Element element, File file) {
		if (!java.util.regex.Pattern.matches("\\d+x\\d+",
				(CharSequence) element.getResolution())) {
			element.setResolution("300x220");
			return "ftpFile\\processed\\fail.png";
		}
		String thumName = null;
		if (element.getType() == ElementType.video.getValue()
				|| element.getType() == ElementType.flash.getValue()) {
			thumName = file.getParent() + "\\img"
					+ ElementUtil.getFilePrefix(file.getName()) + ".gif";
			try {
				float width = Float.parseFloat(element.getResolution().split("x")[0]);
				float height = Float.parseFloat(element.getResolution()
						.split("x")[1]);
				float arg = 0;
				if (height >= width) {
					arg = 300 / height;
					height = 300;
					width = width * arg;
				} else {
					arg = 300 / width;
					width = 300;
					height = arg * height;
				}
				String res = (int)Math.floor(width) + "x" + (int)Math.floor(height);
				String cmd = FfmpegInit.getPath() + " -i \""
						+ file.getAbsolutePath()
						+ "\"  -vframes 500 -y -f gif -s " + res + " \""
						+ thumName + "\"";
				Runtime.getRuntime().exec("cmd /c " + cmd);
				// p.waitFor();
				int time = 0;
				while (!new File(thumName).exists()) {
					Thread.sleep(5000);
					time++;
					if (time == 16) {
						return getThumbnail(element, file);
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logs.WriteLogs(e);
				Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");

			}

			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis())
					+ "	"
					+ element.getFileName()
					+ "缩略图生成成功\r\n");
			return "ftpFile\\processed\\img"
					+ ElementUtil.getFilePrefix(file.getName()) + ".gif";

		}
		return "ftpFile\\processed\\fail.png";
	}

	public static String getThumbnail(Element element, File file) {
		if (!java.util.regex.Pattern.matches("\\d+x\\d+",
				(CharSequence) element.getResolution())) {
			element.setResolution("300x220");
			return "ftpFile\\processed\\fail.png";
		}
		String thumName = null;
		if (element.getType() == ElementType.video.getValue()
				|| element.getType() == ElementType.flash.getValue()) {
			thumName = file.getParent() + "\\img"
					+ ElementUtil.getFilePrefix(file.getName()) + ".jpg";
			try {
				String cmd = FfmpegInit.getPath() + " -i \""
						+ file.getAbsolutePath()
						+ "\" -ss 5 -vframes 1 -r 1 -ac 1 -ab 2 -s "
						+ element.getResolution() + " -f  image2 \"" + thumName
						+ "\"";
				Runtime.getRuntime().exec("cmd /c " + cmd);
				// p.waitFor();
				while (!new File(thumName).exists()) {
					Thread.sleep(5000);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logs.WriteLogs(e);
				Listener.area.append("异常警告\r\n" + e.getMessage() + "\r\n");

			}

			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis())
					+ "	"
					+ element.getFileName()
					+ "缩略图生成成功\r\n");
			return "ftpFile\\processed\\img"
					+ ElementUtil.getFilePrefix(file.getName()) + ".jpg";

		}
		return "ftpFile\\processed\\fail.png";
	}

}
