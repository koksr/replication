package com.tf.model;

import java.sql.Timestamp;


public class Element {
	private Integer TId;
	private Integer uId;
	private String fileName;
	private String fileSize;
	private String filePath;
	private Integer type;
	private String resolution;
	private String timeLength;
	private String thumbnailUrl;
	private Integer auditStatus;
	private String md5;
	private String description;
	private Timestamp uploadTime;
	private Timestamp updateTime;
	private Short isDeleted;
	
	
	
	public Integer getuId() {
		return uId;
	}
	public void setuId(Integer uId) {
		this.uId = uId;
	}
	public Integer getTId() {
		return TId;
	}
	public void setTId(Integer tId) {
		TId = tId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getTimeLength() {
		return timeLength;
	}
	public void setTimeLength(String timeLength) {
		this.timeLength = timeLength;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Timestamp uploadTime) {
		this.uploadTime = uploadTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public Short getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Short isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
}
