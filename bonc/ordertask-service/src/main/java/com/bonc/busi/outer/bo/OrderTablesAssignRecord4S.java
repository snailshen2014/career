package com.bonc.busi.outer.bo;

import java.io.Serializable;
import java.util.Date;

public class OrderTablesAssignRecord4S implements Serializable{

	private static final long serialVersionUID = 1750499035622176653L;
	
	private Integer activitySeqId; 
	private String activityId;
	private String channelId;
	private String tableName;
	private Date   assignDate;
	private String tenantId;
	private Integer busiType;
	public Integer getActivitySeqId() {
		return activitySeqId;
	}
	public void setActivitySeqId(Integer activitySeqId) {
		this.activitySeqId = activitySeqId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Date getAssignDate() {
		return assignDate;
	}
	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public Integer getBusiType() {
		return busiType;
	}
	public void setBusiType(Integer busiType) {
		this.busiType = busiType;
	}

	
}
