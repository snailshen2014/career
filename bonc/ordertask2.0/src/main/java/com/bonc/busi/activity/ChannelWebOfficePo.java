package com.bonc.busi.activity;

public class ChannelWebOfficePo implements ChannelPo{
	private String activityId;
	private String channelWebofficeTitle;		//标题
	private String channelWebofficeUrl;			//网厅地址
	private String channelWebofficeContent;		//网厅话术内容
	private String tenantId;
	private String channelId;	//渠道Id
	private String filterCondition;  //筛选条件
	private String filterConditionSql;   	//筛选条件对应sql
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

	public String getChannelWebofficeTitle() {
		return channelWebofficeTitle;
	}

	public void setChannelWebofficeTitle(String channelWebofficeTitle) {
		this.channelWebofficeTitle = channelWebofficeTitle;
	}

	public String getChannelWebofficeUrl() {
		return channelWebofficeUrl;
	}

	public void setChannelWebofficeUrl(String channelWebofficeUrl) {
		this.channelWebofficeUrl = channelWebofficeUrl;
	}

	public String getChannelWebofficeContent() {
		return channelWebofficeContent;
	}

	public void setChannelWebofficeContent(String channelWebofficeContent) {
		this.channelWebofficeContent = channelWebofficeContent;
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
