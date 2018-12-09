package com.bonc.busi.task.bo;

import java.io.Serializable;

/**
 * 短信工单移历史的请求参数封装类
 * @author Administrator
 *
 */
public class SmsOrderToHisTableRequest implements Serializable {

	private static final long serialVersionUID = -2189625247208130909L;

	private String activityId;
	
	private int activitySeqId;
	
	private String tenantId;
	
	private String channelId;
	
	private String hisTableName;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public int getActivitySeqId() {
		return activitySeqId;
	}

	public void setActivitySeqId(int activitySeqId) {
		this.activitySeqId = activitySeqId;
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

	public String getHisTableName() {
		return hisTableName;
	}

	public void setHisTableName(String hisTableName) {
		this.hisTableName = hisTableName;
	}
}
