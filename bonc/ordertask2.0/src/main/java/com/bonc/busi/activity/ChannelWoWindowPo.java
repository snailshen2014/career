package com.bonc.busi.activity;

public class ChannelWoWindowPo implements ChannelPo{
	private String channelId;
	private String channelWowindowContent;
	private String channelWowindowTitle;		//沃视窗 标题
	private String channelWowindowUrl;			//沃视窗 地址
	private String channelWowindowImgurl;		//沃视窗 图片地址
	private String channelWowindowImgsize;		//沃视窗 图片大小
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

	public String getChannelWowindowContent() {
		return channelWowindowContent;
	}
	public void setChannelWowindowContent(String channelWowindowContent) {
		this.channelWowindowContent = channelWowindowContent;
	}
	public String getChannelWowindowTitle() {
		return channelWowindowTitle;
	}
	public void setChannelWowindowTitle(String channelWowindowTitle) {
		this.channelWowindowTitle = channelWowindowTitle;
	}
	public String getChannelWowindowUrl() {
		return channelWowindowUrl;
	}
	public void setChannelWowindowUrl(String channelWowindowUrl) {
		this.channelWowindowUrl = channelWowindowUrl;
	}
	public String getChannelWowindowImgurl() {
		return channelWowindowImgurl;
	}
	public void setChannelWowindowImgurl(String channelWowindowImgurl) {
		this.channelWowindowImgurl = channelWowindowImgurl;
	}
	public String getChannelWowindowImgsize() {
		return channelWowindowImgsize;
	}
	public void setChannelWowindowImgsize(String channelWowindowImgsize) {
		this.channelWowindowImgsize = channelWowindowImgsize;
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
