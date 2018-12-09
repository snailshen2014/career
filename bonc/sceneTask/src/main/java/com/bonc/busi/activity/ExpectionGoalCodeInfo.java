package com.bonc.busi.activity;


/**
 * 
 * <p>Title: JEEAC - ExpectionGoalCodeInfo </p>
 * 
 * <p>Description: 预期目标码表(po) - clyx_marketing_goal </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class ExpectionGoalCodeInfo extends StockMarketingPo {
	
	/**
	 * 预期目标id
	 */
	private String goalId;
	
	/**
	 * 预期目标名称
	 */
	private String goalName;
	
	/**
	 * 排序
	 */
	private String ord;

	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public String getGoalName() {
		return goalName;
	}

	public void setGoalName(String goalName) {
		this.goalName = goalName;
	}

	public String getOrd() {
		return ord;
	}

	public void setOrd(String ord) {
		this.ord = ord;
	}
	
}
