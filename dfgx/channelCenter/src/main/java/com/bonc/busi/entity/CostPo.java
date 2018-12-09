package com.bonc.busi.entity;

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
public class CostPo {
	
	private String activityID; // 活动ID
	private byte[] uploadFile; // 成本上传文件
	private String uploadFileName; // 成本上传文件名称
	private String costRule; // 成本计算规则
	private double budget; // 预算
	

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
	public String getCostRule() {
		return costRule;
	}
	public void setCostRule(String costRule) {
		this.costRule = costRule;
	}
	public double getBudget() {
		return budget;
	}
	public void setBudget(double budget) {
		this.budget = budget;
	}
	
}
