package com.bonc.busi.task.bo;

import java.util.Date;

public class ScenePowerStatus {
	private int batchId;
	private Date beginDate;
	private Date endDate;
	private String status;
	private String tenantId;
	private String resultData;
	private String consumeTime;
	private String consumeTimeDetail;
	
	
	
	
	
	public String getConsumeTimeDetail() {
		return consumeTimeDetail;
	}
	public void setConsumeTimeDetail(String consumeTimeDetail) {
		this.consumeTimeDetail = consumeTimeDetail;
	}
	public String getConsumeTime() {
		return consumeTime;
	}
	public void setConsumeTime(String consumeTime) {
		this.consumeTime = consumeTime;
	}
	public String getResultData() {
		return resultData;
	}
	public void setResultData(String resultData) {
		this.resultData = resultData;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
