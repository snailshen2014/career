package com.bonc.busi.activity;
/**
 * 
 * @author zhaojianhong
 *玩转流量app渠道实体
 */
public class ChannelPlayFlow {
	private String activityId;//活动id
	private String tenantId;//租户id
	private String filterCondition; //筛选条件
	private String filterConditionSql; //筛选条件对应sql
	private String filterSqlCondition;//条件回显到用户群标识
	private String orderissuedRule;//下发规则
	private String playFlowHuashuContent;//营销话术
	private String channelId;
	private String touchLimitDayApp; //玩转流量app接触频次定义
	private String  isuniset; //是否统一设置：1、统一设置，0、自定义
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
	public String getFilterSqlCondition() {
		return filterSqlCondition;
	}
	public void setFilterSqlCondition(String filterSqlCondition) {
		this.filterSqlCondition = filterSqlCondition;
	}
	public String getOrderissuedRule() {
		return orderissuedRule;
	}
	public void setOrderissuedRule(String orderissuedRule) {
		this.orderissuedRule = orderissuedRule;
	}
	public String getPlayFlowHuashuContent() {
		return playFlowHuashuContent;
	}
	public void setPlayFlowHuashuContent(String playFlowHuashuContent) {
		this.playFlowHuashuContent = playFlowHuashuContent;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getTouchLimitDayApp() {
		return touchLimitDayApp;
	}
	public void setTouchLimitDayApp(String touchLimitDayApp) {
		this.touchLimitDayApp = touchLimitDayApp;
	}
	public String getIsuniset() {
		return isuniset;
	}
	public void setIsuniset(String isuniset) {
		this.isuniset = isuniset;
	}
	
	
	

}
