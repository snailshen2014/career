package com.bonc.busi.service.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE对应的实体bean
 * @author Administrator
 *
 */
public class ActivityChannelExecute implements Serializable{

	private static final long serialVersionUID = 1853396138983664705L;

	private int recId;
	
	//活动批次
	private int activitySeqId;
	
	//活动Id
	private String activityId;
	
	//租户Id
	private String tenantId;
	
	//渠道Id
	private String channelId;
	
	//状态
	private String status;
	
	//时间
	private Date genDate;

	public int getRecId() {
		return recId;
	}

	public void setRecId(int recId) {
		this.recId = recId;
	}

	public int getActivitySeqId() {
		return activitySeqId;
	}

	public void setActivitySeqId(int activitySeqId) {
		this.activitySeqId = activitySeqId;
	}

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

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getGenDate() {
		return genDate;
	}

	public void setGenDate(Date genDate) {
		this.genDate = genDate;
	}
	
}
