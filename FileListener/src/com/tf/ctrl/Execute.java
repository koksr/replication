package com.tf.ctrl;

import java.io.File;
import java.io.IOException;

import com.tf.handle.Mediahandle;
import com.tf.model.Element;
import com.tf.model.ElementType;
import com.tf.util.ElementUtil;
import com.tf.util.Logs;
import com.tf.util.MD5Util;
import com.tf.util.ThreadPool;
import com.tf.view.Listener;

public class Execute extends Thread {
	private Long maxSize = 1024 * 1024 * 10L;
	private File tempFile;
	private String md5;
	private String name;
	private String creater;

	public Execute(File file, String md5, String name, String creater) {
		this.tempFile = file;
		this.md5 = md5;
		this.name = name;
		this.creater = creater;
	}

	public void run() {
		File file = new File(System.getProperty("user.dir") + "\\processed\\"
				+ md5 + "." + ElementUtil.getFileSuffix(name));
		if (!file.exists()) {
			boolean ex = tempFile.renameTo(file);
			if (!ex) {
				tempFile.delete();
			}
		} else {
			tempFile.delete();
		}
		Element element = new Element();
		element.setType(ElementUtil.getType(name));
		element.setFileName(name);
		if(element.getType() == ElementType.image.getValue()){
			ElementUtil.thumImg(file.getAbsolutePath());
			if (file.length() > this.maxSize) {
				file=ElementUtil.zipImageFile(file.getAbsolutePath(), this.maxSize);
				element.setDescription("由于文件过大已被压缩");
				try {
					md5=MD5Util.getFileMD5String(file);
				} catch (IOException e) {
					Logs.WriteLogs(e);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		element.setFileSize(String.valueOf(file.length()));
		element.setFilePath("ftpFile\\processed\\" + md5 + "."
				+ ElementUtil.getFileSuffix(name));
		element.setMd5(md5);
		// TODO url
		element.setuId(Integer.parseInt(creater));
		element.setIsDeleted(Short.parseShort("0"));
		element.setAuditStatus(new DBhandle().getAuditStatus());
		element.setUploadTime(new java.sql.Timestamp(System.currentTimeMillis()));
		element = Mediahandle.getInfo(file, element);
		switch (element.getType()) {
		case 4:
			element.setThumbnailUrl(Mediahandle.getGif(element, file));
			break;
		case 2:
			element.setResolution("300x220");
			element.setThumbnailUrl("ftpFile\\processed\\audio.png");
			break;
		case 6:
			if(element.getResolution()==null&&element.getTimeLength()==null){
				element.setResolution("300x220");
				element.setThumbnailUrl("ftpFile\\processed\\fail.png");
			}else{
				element.setThumbnailUrl(Mediahandle.getGif(element, file));
			}
			break;
		case 3:
			element.setThumbnailUrl("ftpFile\\processed\\" + "img"
					+ file.getName());
			break;
		case 7:
			element.setResolution("300x220");
			element.setThumbnailUrl("ftpFile\\processed\\fail.png");
			break;
		case 9:
			element.setResolution("300x220");
			element.setThumbnailUrl("ftpFile\\processed\\fail.png");
			break;
		}

		if (new com.tf.util.Coder().encode(file.getAbsolutePath())) {
			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis()) + "   " + name + "加密成功\r\n");
		} else {
			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis()) + "   " + name + "加密失败，正在尝试重新加密\r\n");
			if(new com.tf.util.Coder().encode(file.getAbsolutePath(),file.getParent()+File.separator+"en"+file.getName())){
				Listener.area.append(ElementUtil.dateFormat.format(System
						.currentTimeMillis()) + "   " + name + "加密成功\r\n");
				element.setFilePath("ftpFile\\processed\\en" + md5 + "."
				+ ElementUtil.getFileSuffix(name));
				file.delete();
			}
		}

		if (new DBhandle().Insert(element) == 1 && !tempFile.exists()) {
			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis()) + "	" + name + "已存入数据库\r\n");
			if (ThreadPool.fileSet.remove(tempFile)) {
				Listener.area.append(ElementUtil.dateFormat.format(System
						.currentTimeMillis()) + "	" + name + "处理完成\r\n");
			}
		}
	}
}
