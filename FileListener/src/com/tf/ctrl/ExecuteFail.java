package com.tf.ctrl;

import java.io.File;

import com.tf.model.Element;
import com.tf.model.ElementType;
import com.tf.util.ElementUtil;
import com.tf.util.ThreadPool;
import com.tf.view.Listener;

public class ExecuteFail extends Thread {
	private String name;
	private String creater;
	private File f;
	
	
	
	public ExecuteFail(String name, String creater, File f) {
		super();
		this.name = name;
		this.creater = creater;
		this.f = f;
	}



	public void run(){
		Element element = new Element();
		Listener.area.append(ElementUtil.dateFormat.format(System.currentTimeMillis()) + "	"
				+ name + "md5值校验失败\r\n");
		element.setFileName(name);
		element.setType(ElementType.other.getValue());
		element.setuId(Integer.parseInt(creater));
		element.setIsDeleted(Short.parseShort("0"));
		element.setUploadTime(new java.sql.Timestamp(
				System.currentTimeMillis()));
		element.setDescription("文件md5校验失败，上传文件破损，请重新上传");
		Listener.area.append(ElementUtil.dateFormat.format(System.currentTimeMillis()) + "	"
				+ name + "已存入数据库\r\n");
		
		if(new DBhandle().fail(element)==1&&f.delete()){
			ThreadPool.fileSet.remove(f);
		}
		Listener.area.append(ElementUtil.dateFormat.format(System.currentTimeMillis()) + "	"
				+ name + "已被删除，请重新上传\r\n");
	
	}
}
