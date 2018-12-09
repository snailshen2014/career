package com.bonc.busi.activity;

public class ChannelOtherPo implements ChannelPo{
	private String channelId;//渠道id
	private String activityId;//活动id
	private String tenantId;//租户id
	private String filterCondition;//筛选条件
	private String filterConditionSql;//筛选条件对应sql
	private String exportModelName;//导出模板
	private String filterSqlCondition;//条件回显到用户群标示
	private String orderIssuedRule;  //工单下发规则
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
	public String getExportModelName() {
		return exportModelName;
	}
	public void setExportModelName(String exportModelName) {
		this.exportModelName = exportModelName;
	}
	public String getFilterSqlCondition() {
		return filterSqlCondition;
	}
	public void setFilterSqlCondition(String filterSqlCondition) {
		this.filterSqlCondition = filterSqlCondition;
	}
	public String getOrderIssuedRule() {
		return orderIssuedRule;
	}
	public void setOrderIssuedRule(String orderIssuedRule) {
		this.orderIssuedRule = orderIssuedRule;
	}
}
