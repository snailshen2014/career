package com.bonc.busi.activity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 电话渠道实体类
 * @author Administrator
 *
 */
public class TelePhoneChannelPo implements Serializable,ChannelPo{
	
	private static final long serialVersionUID = 6589270698349490019L;

	//渠道选择的外呼公司id
	private String companyId;
	
	//规则使用范围
	private String ruleOrgPath;
	
	//筛选条件
	private String filterCondition;
	
	//下发规则
	private String orderissuedRule;
	
	//话术内容
	private String telephoneHuashuContent;
	
	private List<ChannelSpecialFilterPo> telchannelSpecialFilterList;
	
	//条件回显到用户群标识
	private String filterSqlCondition;
	
	//筛选条件sql
	private String filterConditionSql; 
	
	//渠道id(14)
	private String channelId;
	
	private List<Map<String,Object>> companyInfoList;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getRuleOrgPath() {
		return ruleOrgPath;
	}

	public void setRuleOrgPath(String ruleOrgPath) {
		this.ruleOrgPath = ruleOrgPath;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getOrderissuedRule() {
		return orderissuedRule;
	}

	public void setOrderissuedRule(String orderissuedRule) {
		this.orderissuedRule = orderissuedRule;
	}

	public String getTelephoneHuashuContent() {
		return telephoneHuashuContent;
	}

	public void setTelephoneHuashuContent(String telephoneHuashuContent) {
		this.telephoneHuashuContent = telephoneHuashuContent;
	}

	public List<ChannelSpecialFilterPo> getTelchannelSpecialFilterList() {
		return telchannelSpecialFilterList;
	}

	public void setTelchannelSpecialFilterList(
			List<ChannelSpecialFilterPo> telchannelSpecialFilterList) {
		this.telchannelSpecialFilterList = telchannelSpecialFilterList;
	}

	public String getFilterSqlCondition() {
		return filterSqlCondition;
	}

	public void setFilterSqlCondition(String filterSqlCondition) {
		this.filterSqlCondition = filterSqlCondition;
	}

	@Override
	public String getOrderIssuedRule() {
		return getOrderissuedRule();
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

	public List<Map<String, Object>> getCompanyInfoList() {
		return companyInfoList;
	}

	public void setCompanyInfoList(
			List<Map<String, Object>> companyInfoList) {
		this.companyInfoList = companyInfoList;
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
