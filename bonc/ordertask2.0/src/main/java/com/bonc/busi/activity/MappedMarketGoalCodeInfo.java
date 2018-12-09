package com.bonc.busi.activity;

/**
 * 
 * <p>Title: JEEAC - MappedMarketGoalCodeInfo </p>
 * 
 * <p>Description: 营销预期目标关联表(po) - clyx_dmarketing_goals </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

public class MappedMarketGoalCodeInfo {
	
	/**
	 * 营销id
	 */
	private String marketingId;
	
	/**
	 * 目标id
	 */
	private String goalId;

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
	

}
