package com.bonc.busi.entity;

public class MappedRelatedLabelInfo {
	/**
	 * 渠道偏好标签
	 */
	private String labelId;
	/**
	 * 活动id
	 */
	private String activityId;
	/**
	 * 用户数
	 */
	private String userCount;
	/**
	 * 省分id
	 */
	private String provId;
	/**
	 * 地市id
	 */
	private String cityId;
	
	/**
	 * 用户信息
	 */
	private byte[] users;
	
	private String userInfo;
	
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	public byte[] getUsers() {
		return users;
	}
	public void setUsers(byte[] users) {
		this.users = users;
	}
	public String getProvId() {
		return provId;
	}
	public void setProvId(String provId) {
		this.provId = provId;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getLabelId() {
		return labelId;
	}
	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getUserCount() {
		return userCount;
	}
	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}
}
