package com.bonc.busi.task.bo;

import java.util.Date;
import java.util.List;

public class ScenePowerInfo {
	private int batchId;
	private String tenantId;
	private List<String> phoneNumber;
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public List<String> getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(List<String> phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
}
