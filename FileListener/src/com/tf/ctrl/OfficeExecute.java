package com.tf.ctrl;

import java.io.File;
import java.util.List;

import com.tf.handle.ImgConverter;
import com.tf.handle.Mediahandle;
import com.tf.handle.PDFConverter;
import com.tf.handle.XPSConverter;
import com.tf.model.Element;
import com.tf.model.ElementType;
import com.tf.util.Coder;
import com.tf.util.ElementUtil;
import com.tf.util.ThreadPool;
import com.tf.view.Listener;

public class OfficeExecute extends Thread {
	private File tempFile;
	private String name;
	private String creater;
	private String md5;

	/**
	 * 
	 * @param tempFile
	 * @param name
	 * @param creater
	 * @param md5
	 */
	public OfficeExecute(File tempFile, String name, String creater, String md5) {
		super();
		this.tempFile = tempFile;
		this.name = name;
		this.creater = creater;
		this.md5 = md5;
	}

	public void run() {
		Listener.area.append(ElementUtil.dateFormat.format(System
				.currentTimeMillis()) + "	开始处理"+name+"\r\n");
		File file = new File(System.getProperty("user.dir") + "\\processed\\"
				+ md5 + "." + ElementUtil.getFileSuffix(name));
		if (!file.exists()) {
			boolean ex = tempFile.renameTo(file);
			System.out.println(ex);
			if (!ex) {
				tempFile.delete();
			}
		} else {
			tempFile.delete();
		}
		String suffix = ElementUtil.getFileSuffix(name);
		File pdfFile = null;
		if (suffix.equals("pdf")) {
			pdfFile = file;
		} else {
			pdfFile = new PDFConverter(file).Convert();
		}
		/*
		 * File pdfLocation =new File(
		 * ElementUtil.getFilePrefix(pdfFile.getAbsolutePath()));
		 * if(!pdfLocation.exists()) try { pdfLocation.createNewFile(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		boolean res = true;
		List<File> imgs = ImgConverter.Convert(pdfFile);
		
		File thumFile = new File(file.getParent() + File.separator + "img"
				+ ElementUtil.getFilePrefix(file.getName()) + ".jpg");
		ElementUtil.CopyImg(imgs.get(0).getAbsolutePath(),
				thumFile.getAbsolutePath());
		for (File f : imgs) {
			if (!new Coder().encode(f.getAbsolutePath()))
				res = false;
		}
		
		File xpsFile = new XPSConverter(pdfFile).Convert();
		Element element = new Element();
		element.setType(ElementType.document.getValue());
		element.setFileName(name);
		element.setFileSize(String.valueOf(file.length()));
		boolean fileRes = false;
		if (res) {
			if (!suffix.equals("pdf")) {
				pdfFile.delete();
			}
			fileRes=new Coder().encode(xpsFile.getAbsolutePath(), imgs.get(0).getParent()
					+ ".xps");
			xpsFile.delete();
		}
		if(fileRes)
		Listener.area.append(ElementUtil.dateFormat.format(System
				.currentTimeMillis()) + "	"+name+"加密成功\r\n");
		file.delete();
		element.setFilePath("ftpFile" + File.separator + "processed"
				+ File.separator + imgs.get(0).getParentFile().getName() + ".xps");
		element.setMd5(md5);
		element.setuId(Integer.parseInt(creater));
		element.setIsDeleted(Short.parseShort("0"));
		element.setAuditStatus(new DBhandle().getAuditStatus());
		element.setUploadTime(new java.sql.Timestamp(System.currentTimeMillis()));
		element.setThumbnailUrl("ftpFile\\processed\\"+thumFile.getName());
		//element.setResolution(resolution);
		element =Mediahandle.getInfo(thumFile, element);
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
