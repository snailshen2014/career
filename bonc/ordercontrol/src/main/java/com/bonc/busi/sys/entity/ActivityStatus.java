package com.bonc.busi.sys.entity;

/**
 * 
 * <p>Title: BONC - 工单中心 </p>
 * 
 * <p>Description: 活动状态类（用于活动状态变时接受通知） </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author zengdingyong
 * @version 1.0.0
 */

public class ActivityStatus {
	// --- 活动编号 ---
	private String activityId;
	// --- 活动状态 ---
	private String activityStatus;
	// --- 省编号 --
	private String  tenant_id;
	
	/*
	 *   get 和 set 操作
	 */
	public String getActivityId() {
		return activityId;
	}
	public String getActivityStatus() {
		return activityStatus;
	}
	public String getTenant_id() {
		return tenant_id;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public void setTenant_Id(String tenant_id) {
		this.tenant_id = tenant_id;
	}
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

}
