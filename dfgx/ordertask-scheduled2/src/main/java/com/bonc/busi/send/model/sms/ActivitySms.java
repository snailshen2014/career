package com.bonc.busi.send.model.sms;

import com.bonc.busi.activity.MsmChannelPo;

public class ActivitySms extends MsmChannelPo {

	/**
	 * 活动ID
	 */
	private String activityId;
	/**
	 * 根据规则判断该活动是否可发送
	 */
	private Boolean sendEnable;
	
	/**
	 * 活动的文件名称
	 */
	private String fileName;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Boolean getSendEnable() {
		return sendEnable;
	}

	public void setSendEnable(Boolean sendEnable) {
		this.sendEnable = sendEnable;
	}

}
