package com.bonc.busi.activity;

public class ChannelHandOfficePo {
	private String activityId;
	private String channelId;
	private String channelHandofficeTitle;
	private String channelHandofficeUrl;
	private String channelHandofficeContent;
	private String filterCondition; // 筛选条件
	private String filterConditionSql; // 筛选条件对应sql
	private String tenantId;
	/**
	 * 工单下发规则
	 */
	private String orderIssuedRule;

	public String getOrderIssuedRule() {
		return orderIssuedRule;
	}

	public void setOrderIssuedRule(String orderIssuedRule) {
		this.orderIssuedRule = orderIssuedRule;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getFilterConditionSql() {
		return filterConditionSql;
	}

	public void setFilterConditionSql(String filterConditionSql) {
		this.filterConditionSql = filterConditionSql;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getChannelHandofficeTitle() {
		return channelHandofficeTitle;
	}

	public void setChannelHandofficeTitle(String channelHandofficeTitle) {
		this.channelHandofficeTitle = channelHandofficeTitle;
	}

	public String getChannelHandofficeUrl() {
		return channelHandofficeUrl;
	}

	public void setChannelHandofficeUrl(String channelHandofficeUrl) {
		this.channelHandofficeUrl = channelHandofficeUrl;
	}

	public String getChannelHandofficeContent() {
		return channelHandofficeContent;
	}

	public void setChannelHandofficeContent(String channelHandofficeContent) {
		this.channelHandofficeContent = channelHandofficeContent;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	//for ACTIVITY_SEQ_ID
	private Integer ACTIVITY_SEQ_ID;
	public void setACTIVITY_SEQ_ID(Integer id) {
		this.ACTIVITY_SEQ_ID = id;
	}
	public Integer getACTIVITY_SEQ_ID(){
		return this.ACTIVITY_SEQ_ID;
	}
}
