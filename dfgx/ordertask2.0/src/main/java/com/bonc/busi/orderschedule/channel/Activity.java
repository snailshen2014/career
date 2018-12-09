package com.bonc.busi.orderschedule.channel;

/**
 * define activity's common attributes
 * 
 * @author yanjunshen
 *
 */
public final class Activity {
	private int activitySeqId;
	private String channelId;
	private String tenantId;
	private String orderBeginDate;
	private String orderEndDate;
	private String supChannelId;
	
	public void setActivitySeqId(int id) {
		this.activitySeqId = id;
	}
	public int getActivitySeqId() {
		return this.activitySeqId;
	}
	public void setChannelId(String id) {
		this.channelId = id;
	}
	public String getChannelId() {
		return this.channelId;
	}
	public void setTenantId(String id) {
		this.tenantId = id;
	}
	public String getTenantId() {
		return this.tenantId;
	}
	public void setOrderBeginDate(String dt) {
		this.orderBeginDate = dt;
	}
	public String getOrderBeginDate() {
		return this.orderBeginDate;
	}
	public void setOrderEndDate(String dt) {
		this.orderEndDate = dt;
	}
	public String getOrderEndDate() {
		return this.orderEndDate;
	}

	public String getSupChannelId() {
		return supChannelId;
	}

	public void setSupChannelId(String supChannelId) {
		this.supChannelId = supChannelId;
	}
}
