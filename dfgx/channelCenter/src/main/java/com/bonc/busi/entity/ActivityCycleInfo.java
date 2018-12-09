package com.bonc.busi.entity;

import java.util.Date;

public class ActivityCycleInfo {
	/**
	 * 活动id
	 */
	private String activityId;
	
	/**
	 * 定期模式（0按天，1按周，2按月）
	 */
	private String cycleMode;
	
	/**
	 * 每隔XX的单位数，（1按周，2按月必填）
	 */
	private String everyUnit; 
	
	/**
	 * 第X天（1按周时候不能超过7，2按月时候不能超过31，如果配置31，月份不够的取最后一天）
	 */
	private String dayNumber;
	
	/**
	 * 重复范围开始时间
	 */
	private Date scopeStartDay;
	
	/**
	 * 重复范围结束时间
	 */
	private Date scopeEndDay;
	
	/**
	 * 是否结束日期1:无结束 2：有结束
	 */
	private String isEndDate;
	
	/***
	 * @author liyang
	 * 2016年4月14日
	 * 
	 * 周期性活动下发的重试次数
	 */
	private String retryTime;
	
	public String getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(String retryTime) {
		this.retryTime = retryTime;
	}

	public String getIsEndDate() {
		return isEndDate;
	}

	public void setIsEndDate(String isEndDate) {
		this.isEndDate = isEndDate;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getCycleMode() {
		return cycleMode;
	}

	public void setCycleMode(String cycleMode) {
		this.cycleMode = cycleMode;
	}

	public String getEveryUnit() {
		return everyUnit;
	}

	public void setEveryUnit(String everyUnit) {
		this.everyUnit = everyUnit;
	}

	public String getDayNumber() {
		return dayNumber;
	}

	public void setDayNumber(String dayNumber) {
		this.dayNumber = dayNumber;
	}

	public Date getScopeStartDay() {
		return scopeStartDay;
	}

	public void setScopeStartDay(Date scopeStartDay) {
		this.scopeStartDay = scopeStartDay;
	}

	public Date getScopeEndDay() {
		return scopeEndDay;
	}

	public void setScopeEndDay(Date scopeEndDay) {
		this.scopeEndDay = scopeEndDay;
	}
}
