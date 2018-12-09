package com.bonc.busi.activity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StockMarketingPo implements Serializable {
	
	private String tenantId; //租户id
	private String tenantName;

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
