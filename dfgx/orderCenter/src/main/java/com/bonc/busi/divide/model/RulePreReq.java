package com.bonc.busi.divide.model;

import java.util.HashMap;

import com.bonc.busi.interfaces.model.ReqHeader;

public class RulePreReq extends ReqHeader {

	//-1 收入归属，-2 平均分 ， -3 arpu平均分，>0 每人分多少
	private Integer ruleType;
	//接收者 MAP key path ,value isExe;isExe=1  path=orgPath,loginId
	private HashMap<String, String> receiveOrgs;
	private String activityId; // 活动批次号
	
	//工单归属者Map key path,value isExe,  isExe=1  path=orgPath,loginId
	private HashMap<String, String> orderOrgs;
	
	private String orderOrgSql;

	public String getOrderOrgSql() {
		return orderOrgSql;
	}

	public void setOrderOrgSql(String orderOrgSql) {
		this.orderOrgSql = orderOrgSql;
	}

	public HashMap<String, String> getOrderOrgs() {
		return orderOrgs;
	}

	public void setOrderOrgs(HashMap<String, String> orderOrgs) {
		this.orderOrgs = orderOrgs;
	}

	public Integer getRuleType() {
		return ruleType;
	}

	public void setRuleType(Integer ruleType) {
		this.ruleType = ruleType;
	}

	public HashMap<String, String> getReceiveOrgs() {
		return receiveOrgs;
	}

	public void setReceiveOrgs(HashMap<String, String> receiveOrgs) {
		this.receiveOrgs = receiveOrgs;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	
}
