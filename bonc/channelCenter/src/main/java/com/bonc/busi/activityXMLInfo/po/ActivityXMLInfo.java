/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ActivityConfigInfo.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.activityConfigInfo.po
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月18日 上午11:08:48
 * @version: V1.0  
 */

package com.bonc.busi.activityXMLInfo.po;

/**
 * @ClassName: ActivityXMLInfo
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年11月18日 上午11:08:48
 */
public class ActivityXMLInfo {
	
	private Integer id;
	
	private String activityId;
	
	private String configInfo;
	
	private String tenantId;
	
	private Integer activitySeqId;
	
	private String dealMonth;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	public String getConfigInfo() {
		return configInfo;
	}

	public void setConfigInfo(String configInfo) {
		this.configInfo = configInfo;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public String toString() {
		return "ActivityConfigInfo [id=" + id + ", activityId=" + activityId + ", configInfo=" + configInfo
				+ ", tenantId=" + tenantId + ", activitySeqId=" + activitySeqId +", dealMonth=" + dealMonth +"]";
	}

	public Integer getActivitySeqId() {
		return activitySeqId;
	}

	public void setActivitySeqId(Integer activitySeqId) {
		this.activitySeqId = activitySeqId;
	}

	public String getDealMonth() {
		return dealMonth;
	}

	public void setDealMonth(String dealMonth) {
		this.dealMonth = dealMonth;
	}

	
	
	
	
	

}
