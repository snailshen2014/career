package com.bonc.busi.activity;

import java.util.List;

/**
 * 短信渠道配置
 * @author ICE
 *
 */
public class MsmChannelPo {
	/**
	 * 渠道Id
	 */
	private String channelId;
	/**
	 * 发送级别
	 */
	private String sendLevel;
	
	/**
	 * 不发送时间
	 */
	private String noSendTime;
	/**
	 * 短信发送时段设置
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
	 * 短信模板内容
	 */
	private String smsContent;
	/**
	 * 自定义特殊筛选
	 */
	private List<ChannelSpecialFilterPo> ChannelSpecialFilterList;
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
	 * 工单下发规则
	 */
	private String orderIssuedRule;
	/**
	 * 产品id
	 */
	private String msmProductId;
	/**
	 * 产品编码
	 */
	private String msmProductCode;
	/**
	 * 短信选择成功标准已选产品
	 * @return
	 */
	private SuccessProductPo successProductPo;
	
	public String getMsmProductId() {
		return msmProductId;
	}

	public void setMsmProductId(String msmProductId) {
		this.msmProductId = msmProductId;
	}

	public String getMsmProductCode() {
		return msmProductCode;
	}

	public void setMsmProductCode(String msmProductCode) {
		this.msmProductCode = msmProductCode;
	}

	public SuccessProductPo getSuccessProductPo() {
		return successProductPo;
	}

	public void setSuccessProductPo(SuccessProductPo successProductPo) {
		this.successProductPo = successProductPo;
	}

	public String getOrderIssuedRule() {
		return orderIssuedRule;
	}

	public void setOrderIssuedRule(String orderIssuedRule) {
		this.orderIssuedRule = orderIssuedRule;
	}

	public String getMessageSendTime() {
		return messageSendTime;
	}

	public void setMessageSendTime(String messageSendTime) {
		this.messageSendTime = messageSendTime;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getFilterConditionSql() {
		return filterConditionSql;
	}

	public void setFilterConditionSql(String filterConditionSql) {
		this.filterConditionSql = filterConditionSql;
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

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
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
	private String tenantId;
	private String activityId;
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
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
