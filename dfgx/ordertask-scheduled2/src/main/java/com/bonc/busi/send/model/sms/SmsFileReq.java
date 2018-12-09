package com.bonc.busi.send.model.sms;

public class SmsFileReq {

	private String tenantId;
	private String tableName;
	private String channelId;
	private Integer size;

	public SmsFileReq() {
		super();
	}


	public SmsFileReq(String tenantId, String tableName, String channelId,
			Integer size) {
		super();
		this.tenantId = tenantId;
		this.tableName = tableName;
		this.channelId = channelId;
		this.size = size;
	}


	public String getChannelId() {
		return channelId;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}
