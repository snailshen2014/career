package com.bonc.busi.activity;

import java.util.Date;

public class MappedChannelInfoPici {
	private String id; //id
	private String activityId; //活动id
	private String mappedChannelId; //配置渠道id
	private int piciNum; //批次号
	private Date startTime;//开始时段
	private Date endTime; //结束时段
	private int userNumber;//用户数
	
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMappedChannelId() {
		return mappedChannelId;
	}
	public void setMappedChannelId(String mappedChannelId) {
		this.mappedChannelId = mappedChannelId;
	}
	public int getPiciNum() {
		return piciNum;
	}
	public void setPiciNum(int piciNum) {
		this.piciNum = piciNum;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public int getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}
	
}
