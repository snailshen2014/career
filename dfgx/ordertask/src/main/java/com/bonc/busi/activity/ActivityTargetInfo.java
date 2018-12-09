package com.bonc.busi.activity;


/**
 * 
 * <p>Title: JEEAC - ActivityTargetInfo </p>
 * 
 * <p>Description: 活动营销目标信息(po) - clyx_marketing_goals </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class ActivityTargetInfo extends StockMarketingPo {
	
	/**
	 * 活动id
	 */
	private String activityId;
	
	/**
	 * 营销id
	 */
	private String marketingId;
	
	/**
	 * 预期目标id
	 */
	private String goalId;
	
	/**
	 * 数值
	 */
	private Integer number;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getMarketingId() {
		return marketingId;
	}

	public void setMarketingId(String marketingId) {
		this.marketingId = marketingId;
	}

	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
	
}
