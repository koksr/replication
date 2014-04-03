package com.tf.model;

import java.io.File;

public class FileData {
	private File file;
	private Integer row;
	private String md5;
	private int createrID;
	private int status;
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public int getCreaterID() {
		return createrID;
	}
	public void setCreaterID(int createrID) {
		this.createrID = createrID;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	
}
