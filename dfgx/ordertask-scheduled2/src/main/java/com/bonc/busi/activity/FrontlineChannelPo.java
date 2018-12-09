package com.bonc.busi.activity;

import java.util.List;

/**
 * 一线渠道
 * 
 * @author ICE
 *
 */
public class FrontlineChannelPo {
	/**
	 * 渠道Id
	 */
	private String channelId;

	/**
	 * 营销话术
	 */
	private String marketingWords;

	/**
	 * 短信话术
	 */
	private String smsWords;

	/**
	 * 工单下发规则
	 */
	private String orderIssuedRule;

	/**
	 * 工单下发级别：1=末级管理这；2=执行者
	 */
	private String orderIssuedLevel;

	/**
	 * 是否特殊筛选
	 */
	private String isSpecialFilter;
	/**
	 * 筛选类型：1=自定义；2=模型（标签）
	 */
	private String specialType;

	/**
	 * 自定义特殊筛选
	 */
	private List<ChannelSpecialFilterPo> ChannelSpecialFilterList;

	/**
	 * 模型筛选标签
	 */
	private String label;
	/**
	 * 是否发送短信：1、发送，0、不发送
	 */
	private String isSendSMS;
	/**
	 * 是否统一设置：1、统一设置，0、自定义
	 */
	private String isUniSet;
	/**
	 * 接触频次
	 */
	private String touchLimitDay;
	/**
	 * 筛选条件
	 */
	private String filterCondition;
	/**
	 * 筛选条件对应Sql
	 */
	private String filterConditionSql;
	/**
	 * 下发规则对应组织机构id
	 */
	private String ruleOrgId;
	/**
	 * 下发规则对应组织机构路径
	 */
	private String ruleOrgPath;

	public String getRuleOrgId() {
		return ruleOrgId;
	}

	public void setRuleOrgId(String ruleOrgId) {
		this.ruleOrgId = ruleOrgId;
	}

	public String getRuleOrgPath() {
		return ruleOrgPath;
	}

	public void setRuleOrgPath(String ruleOrgPath) {
		this.ruleOrgPath = ruleOrgPath;
	}

	public String getFilterConditionSql() {
		return filterConditionSql;
	}

	public void setFilterConditionSql(String filterConditionSql) {
		this.filterConditionSql = filterConditionSql;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getTouchLimitDay() {
		return touchLimitDay;
	}

	public void setTouchLimitDay(String touchLimitDay) {
		this.touchLimitDay = touchLimitDay;
	}

	public String getIsUniSet() {
		return isUniSet;
	}

	public void setIsUniSet(String isUniSet) {
		this.isUniSet = isUniSet;
	}

	public String getIsSendSMS() {
		return isSendSMS;
	}

	public void setIsSendSMS(String isSendSMS) {
		this.isSendSMS = isSendSMS;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getMarketingWords() {
		return marketingWords;
	}

	public void setMarketingWords(String marketingWords) {
		this.marketingWords = marketingWords;
	}

	public String getSmsWords() {
		return smsWords;
	}

	public void setSmsWords(String smsWords) {
		this.smsWords = smsWords;
	}

	public String getOrderIssuedRule() {
		return orderIssuedRule;
	}

	public void setOrderIssuedRule(String orderIssuedRule) {
		this.orderIssuedRule = orderIssuedRule;
	}

	public String getOrderIssuedLevel() {
		return orderIssuedLevel;
	}

	public void setOrderIssuedLevel(String orderIssuedLevel) {
		this.orderIssuedLevel = orderIssuedLevel;
	}

	public String getIsSpecialFilter() {
		return isSpecialFilter;
	}

	public void setIsSpecialFilter(String isSpecialFilter) {
		this.isSpecialFilter = isSpecialFilter;
	}

	public String getSpecialType() {
		return specialType;
	}

	public void setSpecialType(String specialType) {
		this.specialType = specialType;
	}

	public List<ChannelSpecialFilterPo> getChannelSpecialFilterList() {
		return ChannelSpecialFilterList;
	}

	public void setChannelSpecialFilterList(List<ChannelSpecialFilterPo> channelSpecialFilterList) {
		ChannelSpecialFilterList = channelSpecialFilterList;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	// add by shenyj for tenant_id,activity_id
	private String activityId; // 活动id
	private String tenantId; // 租户id

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	// for ACTIVITY_SEQ_ID
	private Integer ACTIVITY_SEQ_ID;

	public void setACTIVITY_SEQ_ID(Integer id) {
		this.ACTIVITY_SEQ_ID = id;
	}

	public Integer getACTIVITY_SEQ_ID() {
		return this.ACTIVITY_SEQ_ID;
	}
}
