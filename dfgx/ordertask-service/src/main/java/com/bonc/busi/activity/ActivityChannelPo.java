package com.bonc.busi.activity;


public class ActivityChannelPo {
	/**
	 * id
	 */
	private String id;
	

	/**
	 * code_id
	 */
	private String channelId;
	
	/**
	 * 名称 code_desc
	 */
	private String channelName;
	
	/**
	 * 助销Id
	 */
	private String activityId;
	
	/**
	 * 是否生效 (1生效,0无效)
	 */
	private String isValidate;
	
	/**
	 * 弹窗推送次数
	 */
	private Integer pushTime;
	
	/**
	 * 目标
	 */
	private String targetName;
	
	/**
	 * 话术
	 */
	private String huashuDescription;
	


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}


	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getIsValidate() {
		return isValidate;
	}

	public void setIsValidate(String isValidate) {
		this.isValidate = isValidate;
	}

	public Integer getPushTime() {
		return pushTime;
	}

	public void setPushTime(Integer pushTime) {
		this.pushTime = pushTime;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getHuashuDescription() {
		return huashuDescription;
	}

	public void setHuashuDescription(String huashuDescription) {
		this.huashuDescription = huashuDescription;
	}
}
