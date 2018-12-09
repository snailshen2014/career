package com.bonc.busi.backpage.bo;

import java.io.Serializable;

/**
 * 活动对应的实体类
 * @author Administrator
 *
 */
public class ActivityPo implements Serializable{
	private static final long serialVersionUID = -6388893463688085645L;
	
	/**
	 * 活动Id
	 */
	private String activityId;
	
	/*
	 * 活动名称
	 */
	private String activityName;
	
	/**
	 * 活动工单的生成状态：  1(生成成功)   0(生成失败）   2(生成中)
	 */
	private String orderStatus;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
}
