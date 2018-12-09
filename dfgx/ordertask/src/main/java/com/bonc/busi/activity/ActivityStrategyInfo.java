package com.bonc.busi.activity;





/**
 * 
 * <p>Title: JEEAC - ActivityStrategyInfo </p>
 * 
 * <p>Description: 活动策略信息(po) - clyx_activity_strategy </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class ActivityStrategyInfo extends StockMarketingPo {
	
	/**
	 * 活动id
	 */
	private String activityId;
	
	/**
	 * 策略id
	 */
	private String strategyId;
	
	/**
	 * 用户数
	 */
	private String num;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(String strategyId) {
		this.strategyId = strategyId;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}
	
}
