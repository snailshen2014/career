/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ActivityConfigInfo.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.activityInfo.po
 * @Description: TODO
 * @Company: BONC
 * @author: sky  
 * @version: V1.0  
 */

package com.bonc.busi.activityInfo.po;

/**
 * @ClassName: ActivityInfo
 * @Description: TODO
 * @author: sky
 */
public class ActivityInfo {
	
	private int recId;
	
	private String activityId;
		
	private String tenantId;
	
	private String dealMonth;

	private String sendStatus;
	
	private String activityType;
	
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getDealMonth() {
		return dealMonth;
	}

	public void setDealMonth(String dealMonth) {
		this.dealMonth = dealMonth;
	}

	@Override
	public String toString() {
		return "ActivityInfo [ activityId=" + activityId + ", dealMonth=" + dealMonth
				+ ", tenantId=" + tenantId + "]";
	}

	public int getRecId() {
		return recId;
	}

	public void setRecId(int recId) {
		this.recId = recId;
	}

	public String getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	
	
	
	
	

}
