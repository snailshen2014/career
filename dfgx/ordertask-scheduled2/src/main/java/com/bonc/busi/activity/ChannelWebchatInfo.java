/*
 * File name:          ChannelWebchatInfo.java
 * Copyright@DongShiPeng (China)
 * Editor:           JDK1.6.32
 */
package com.bonc.busi.activity;


/**
 * TODO: File comments
 * <p>
 * <p>
 * Author:          董世鹏
 * <p>
 * Date:           2016年11月17日
 * <p>
 * Time:           下午4:35:10
 * <p>
 * Director:        董世鹏
 * <p>
 * <p>
 */

@SuppressWarnings("serial")
public class ChannelWebchatInfo extends StockMarketingPo {
	private String activityId; //活动id
	private String channelId;  //渠道Id
	private String channelWebchatModelId; //渠道微信公众号模板id
	private String channelWebchatTitle; //微信公众号标题
	private String channelWebchatUrl; //微信公众号网址
	private String channelWebchatContent; //微信公众号话术
	private String channelWebchatImgurl; //微信公众号图片地址
	private String webchatType; //操作类型(修改/新增)
	private String filterCondition;  //筛选条件
	private String filterConditionSql;   	//筛选条件对应sql
	private WebChatMidActivityPo webChatMidActivityPo; //微信公共号信息
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

	public WebChatMidActivityPo getWebChatMidActivityPo() {
		return webChatMidActivityPo;
	}

	public void setWebChatMidActivityPo(WebChatMidActivityPo webChatMidActivityPo) {
		this.webChatMidActivityPo = webChatMidActivityPo;
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

	public String getChannelWebchatModelId() {
		return channelWebchatModelId;
	}

	public void setChannelWebchatModelId(String channelWebchatModelId) {
		this.channelWebchatModelId = channelWebchatModelId;
	}

	public String getChannelWebchatTitle() {
		return channelWebchatTitle;
	}

	public void setChannelWebchatTitle(String channelWebchatTitle) {
		this.channelWebchatTitle = channelWebchatTitle;
	}

	public String getChannelWebchatUrl() {
		return channelWebchatUrl;
	}

	public void setChannelWebchatUrl(String channelWebchatUrl) {
		this.channelWebchatUrl = channelWebchatUrl;
	}

	public String getChannelWebchatContent() {
		return channelWebchatContent;
	}

	public void setChannelWebchatContent(String channelWebchatContent) {
		this.channelWebchatContent = channelWebchatContent;
	}

	public String getWebchatType() {
		return webchatType;
	}

	public void setWebchatType(String webchatType) {
		this.webchatType = webchatType;
	}

	public String getChannelWebchatImgurl() {
		return channelWebchatImgurl;
	}

	public void setChannelWebchatImgurl(String channelWebchatImgurl) {
		this.channelWebchatImgurl = channelWebchatImgurl;
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
