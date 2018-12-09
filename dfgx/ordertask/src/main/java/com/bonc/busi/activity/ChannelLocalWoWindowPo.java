package com.bonc.busi.activity;

import java.util.List;

public class ChannelLocalWoWindowPo {
	private String channelId;
	private String channelLocalWowindowContent;
	private String channelLocalWowindowTitle;		//本地沃视窗 标题
	private String channelLocalWowindowUrl;			//本地沃视窗 地址
	private String channelLocalWowindowImgurl;		//本地沃视窗 图片地址
	private String channelLocalWowindowImgsize;		//本地沃视窗 图片大小
	private String filterCondition;  //筛选条件
	private String filterConditionSql;   	//筛选条件对应sql
	/**
	 * 自定义特殊筛选
	 */
	private List<ChannelSpecialFilterPo> woWindowchannelSpecialFilterList;
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
	public String getChannelLocalWowindowContent() {
		return channelLocalWowindowContent;
	}
	public void setChannelLocalWowindowContent(String channelLocalWowindowContent) {
		this.channelLocalWowindowContent = channelLocalWowindowContent;
	}
	public String getChannelLocalWowindowTitle() {
		return channelLocalWowindowTitle;
	}
	public void setChannelLocalWowindowTitle(String channelLocalWowindowTitle) {
		this.channelLocalWowindowTitle = channelLocalWowindowTitle;
	}
	public String getChannelLocalWowindowUrl() {
		return channelLocalWowindowUrl;
	}
	public void setChannelLocalWowindowUrl(String channelLocalWowindowUrl) {
		this.channelLocalWowindowUrl = channelLocalWowindowUrl;
	}
	public String getChannelLocalWowindowImgurl() {
		return channelLocalWowindowImgurl;
	}
	public void setChannelLocalWowindowImgurl(String channelLocalWowindowImgurl) {
		this.channelLocalWowindowImgurl = channelLocalWowindowImgurl;
	}
	public String getChannelLocalWowindowImgsize() {
		return channelLocalWowindowImgsize;
	}
	public void setChannelLocalWowindowImgsize(String channelLocalWowindowImgsize) {
		this.channelLocalWowindowImgsize = channelLocalWowindowImgsize;
	}
	
	public List<ChannelSpecialFilterPo> getWoWindowchannelSpecialFilterList() {
		return woWindowchannelSpecialFilterList;
	}
	public void setWoWindowchannelSpecialFilterList(List<ChannelSpecialFilterPo> woWindowchannelSpecialFilterList) {
		this.woWindowchannelSpecialFilterList = woWindowchannelSpecialFilterList;
	}
	
}
