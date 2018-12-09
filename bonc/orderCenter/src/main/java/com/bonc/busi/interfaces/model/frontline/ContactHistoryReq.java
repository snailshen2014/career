package com.bonc.busi.interfaces.model.frontline;

import com.bonc.busi.interfaces.model.ReqHeader;

public class ContactHistoryReq extends ReqHeader {

	private String userId; // 用户唯一标识
	private String activityId; // 活动的标识
	private String recId; // 任务的唯一标识
	private String sql;
	private String contactDate;
	private String contactChannel;
	
	
	public String getContactChannel() {
		return contactChannel;
	}

	public void setContactChannel(String contactChannel) {
		this.contactChannel = contactChannel;
	}

	public String getContactDate() {
		return contactDate;
	}

	public void setContactDate(String contactDate) {
		this.contactDate = contactDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getRecId() {
		return recId;
	}

	public void setRecId(String recId) {
		this.recId = recId;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
