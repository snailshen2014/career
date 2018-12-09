package com.bonc.busi.send.model.sms;

public class SmsStatistics {
	private String externalId;
	private String smsResource;
	private String craeteTime;
	private String tenantId;
	private String activitySqlId;
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getSmsResource() {
		return smsResource;
	}
	public void setSmsResource(String smsResource) {
		this.smsResource = smsResource;
	}
	public String getCraeteTime() {
		return craeteTime;
	}
	public void setCraeteTime(String craeteTime) {
		this.craeteTime = craeteTime;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getActivitySqlId() {
		return activitySqlId;
	}
	public void setActivitySqlId(String activitySqlId) {
		this.activitySqlId = activitySqlId;
	}
}
