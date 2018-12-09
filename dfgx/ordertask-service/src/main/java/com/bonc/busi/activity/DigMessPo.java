package com.bonc.busi.activity;

import java.io.File;

/**
 * 
 * <p>Title: JEEAC - CostInfo </p>
 * 
 * <p>Description: 上报成本信息(po) </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2016 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author lwx
 * @version 1.0.0
 */
public class DigMessPo {
	
	private String activityID; // 活动ID
	private byte[] uploadFile; // 成本上传文件
	private String uploadFileName; // 成本上传文件名称
	private String title1;//标题1
	private String content1;//内容1
	private String title2;//标题2
	private String content2;//内容2
	private String txt;//纯文本内容1
	private String txt2;//纯文本内容2
	private String fileIds;//附件id1
	private String fileIds2;//附件id2

	public String getTitle1() {
		return title1;
	}
	public void setTitle1(String title1) {
		this.title1 = title1;
	}
	public String getContent1() {
		return content1;
	}
	public void setContent1(String content1) {
		this.content1 = content1;
	}
	public String getTitle2() {
		return title2;
	}
	public void setTitle2(String title2) {
		this.title2 = title2;
	}
	public String getContent2() {
		return content2;
	}
	public void setContent2(String content2) {
		this.content2 = content2;
	}
	public String getActivityID() {
		return activityID;
	}
	public void setActivityID(String activityID) {
		this.activityID = activityID;
	}  
	public byte[] getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(byte[] uploadFile) {
		this.uploadFile = uploadFile;
	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	public String getTxt2() {
		return txt2;
	}
	public void setTxt2(String txt2) {
		this.txt2 = txt2;
	}
	public String getFileIds() {
		return fileIds;
	}
	public void setFileIds(String fileIds) {
		this.fileIds = fileIds;
	}
	public String getFileIds2() {
		return fileIds2;
	}
	public void setFileIds2(String fileIds2) {
		this.fileIds2 = fileIds2;
	}
	
}
