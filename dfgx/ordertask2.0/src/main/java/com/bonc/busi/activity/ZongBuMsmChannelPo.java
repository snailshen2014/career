package com.bonc.busi.activity;

import java.util.*;

/**
 * 集团短信
 * @author ICE
 *
 */
public class ZongBuMsmChannelPo implements ChannelPo {
	private String channelId;//渠道id
	private String activityId;

	private String provId;
	private String tenantId;
	private String orderIssuedRule;
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getOrderIssuedRule() {
		return orderIssuedRule;
	}

	public void setOrderIssuedRule(String orderIssuedRule) {
		this.orderIssuedRule = orderIssuedRule;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	private String filterConditionSql;

	public String getFilterConditionSql() {
		return filterConditionSql;
	}

	public void setFilterConditionSql(String filterConditionSql) {
		this.filterConditionSql = filterConditionSql;
	}

	/**
	 * 筛选条件
	 */
	private String filterCondition;
	private String filterSqlCondition;
	/**
	 * 发送级别
	 */
	private String sendLevel;

	/**
	 * 不发送时间
	 */
	private String noSendTime;
	/**
	 * 短信发送时段：1、中午（9：00-12：00），2、下午（14：30-18:00），（1,2）中午下午都发送
	 */
	private String messageSendTime;
	/**
	 * 开始时间
	 */
	private String sendStartTime;

	/**
	 * 结束时间
	 */
	private String sendEndTime;
	/**
	 * 周期内发送次数
	 */
	private String cycleTimes;

	/**
	 * 发送多次的时间间隔
	 */
	private String intervalHours;
	/**
	 * 是否特殊筛选
	 */
	private String isSpecialFilter;

	/**
	 * 自定义特殊筛选
	 */
	private List<ChannelSpecialFilterPo> ChannelSpecialFilterList;
	/**
	 * 短信内容
	 */
	private String smsContent;

	private String msmStartDay;

	private String msmStartTime;

	private String msmEndDay;

	private String msmEndTime;
	/**
	 * 是否统一设置：1、统一设置，0、自定义
	 */
	private String isUniSet;
	/**
	 * 接触频次
	 */
	private String touchLimitDay;

	/**
	 * 选择订购产品id
	 */
	private String orderProductId;
	/**
	 * 选择订购产品名称
	 */
	private String orderProductName;
	/**
	 * 短信营销类型
	 */
	private String marketingType;
	/**
	 * 短信营销类型名称
	 */
	private String marketingName;
	/**
	 * 高级模式扩展规则
	 */
	private List<ChannelSpecialFilterPo> smschannelSpecialFilterList;

	public String getOrderProductId() {
		return orderProductId;
	}

	public void setOrderProductId(String orderProductId) {
		this.orderProductId = orderProductId;
	}

	public String getFilterSqlCondition() {
		return filterSqlCondition;
	}

	public void setFilterSqlCondition(String filterSqlCondition) {
		this.filterSqlCondition = filterSqlCondition;
	}

	public String getMessageSendTime() {
		return messageSendTime;
	}

	public void setMessageSendTime(String messageSendTime) {
		this.messageSendTime = messageSendTime;
	}

	public String getTouchLimitDay() {
		return touchLimitDay;
	}

	public void setTouchLimitDay(String touchLimitDay) {
		this.touchLimitDay = touchLimitDay;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getSendLevel() {
		return sendLevel;
	}

	public void setSendLevel(String sendLevel) {
		this.sendLevel = sendLevel;
	}

	public String getNoSendTime() {
		return noSendTime;
	}

	public void setNoSendTime(String noSendTime) {
		this.noSendTime = noSendTime;
	}

	public String getSendStartTime() {
		return sendStartTime;
	}

	public void setSendStartTime(String sendStartTime) {
		this.sendStartTime = sendStartTime;
	}

	public String getSendEndTime() {
		return sendEndTime;
	}

	public void setSendEndTime(String sendEndTime) {
		this.sendEndTime = sendEndTime;
	}

	public String getCycleTimes() {
		return cycleTimes;
	}

	public void setCycleTimes(String cycleTimes) {
		this.cycleTimes = cycleTimes;
	}

	public String getIntervalHours() {
		return intervalHours;
	}

	public void setIntervalHours(String intervalHours) {
		this.intervalHours = intervalHours;
	}

	public String getIsSpecialFilter() {
		return isSpecialFilter;
	}

	public void setIsSpecialFilter(String isSpecialFilter) {
		this.isSpecialFilter = isSpecialFilter;
	}

	public List<ChannelSpecialFilterPo> getChannelSpecialFilterList() {
		return ChannelSpecialFilterList;
	}

	public void setChannelSpecialFilterList(List<ChannelSpecialFilterPo> channelSpecialFilterList) {
		ChannelSpecialFilterList = channelSpecialFilterList;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public String getMsmStartDay() {
		return msmStartDay;
	}

	public void setMsmStartDay(String msmStartDay) {
		this.msmStartDay = msmStartDay;
	}

	public String getMsmStartTime() {
		return msmStartTime;
	}

	public void setMsmStartTime(String msmStartTime) {
		this.msmStartTime = msmStartTime;
	}

	public String getMsmEndDay() {
		return msmEndDay;
	}

	public void setMsmEndDay(String msmEndDay) {
		this.msmEndDay = msmEndDay;
	}

	public String getMsmEndTime() {
		return msmEndTime;
	}

	public void setMsmEndTime(String msmEndTime) {
		this.msmEndTime = msmEndTime;
	}

	public String getIsUniSet() {
		return isUniSet;
	}

	public void setIsUniSet(String isUniSet) {
		this.isUniSet = isUniSet;
	}

	public String getOrderProductName() {
		return orderProductName;
	}

	public void setOrderProductName(String orderProductName) {
		this.orderProductName = orderProductName;
	}

	public String getMarketingType() {
		return marketingType;
	}

	public void setMarketingType(String marketingType) {
		this.marketingType = marketingType;
	}

	public String getMarketingName() {
		return marketingName;
	}

	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}

	public List<ChannelSpecialFilterPo> getSmschannelSpecialFilterList() {
		return smschannelSpecialFilterList;
	}

	public void setSmschannelSpecialFilterList(List<ChannelSpecialFilterPo> smschannelSpecialFilterList) {
		this.smschannelSpecialFilterList = smschannelSpecialFilterList;
	}

}
