package com.bonc.busi.activity;

import java.io.Serializable;

/**
 * 小沃渠道
 * @author Administrator
 *
 */
public class ChannelSmallWoPo implements Serializable {

	private static final long serialVersionUID = -5573447586587626422L;
	
	//渠道Id
	private String channelId;
	
	//筛选条件
	private String filterCondition;
	
	//筛选条件对应Sql
	private String filterConditionSql;
	
	//下发规则
	private String orderissuedRule;
	
	//小沃渠道话术信息
	private String channaelSmallWoContent;
	
	//营销目标
	private String marketingTarget;

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

	public String getOrderissuedRule() {
		return orderissuedRule;
	}

	public void setOrderissuedRule(String orderissuedRule) {
		this.orderissuedRule = orderissuedRule;
	}

	public String getChannaelSmallWoContent() {
		return channaelSmallWoContent;
	}

	public void setChannaelSmallWoContent(String channaelSmallWoContent) {
		this.channaelSmallWoContent = channaelSmallWoContent;
	}

	public String getMarketingTarget() {
		return marketingTarget;
	}

	public void setMarketingTarget(String marketingTarget) {
		this.marketingTarget = marketingTarget;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
