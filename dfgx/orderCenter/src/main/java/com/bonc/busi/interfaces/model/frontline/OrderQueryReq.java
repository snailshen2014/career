package com.bonc.busi.interfaces.model.frontline;

import java.util.HashMap;
import java.util.List;

import com.bonc.busi.interfaces.model.ReqHeader;

public class OrderQueryReq extends ReqHeader {

	private String activityId; // 活动ID
	private HashMap<String, String> pama;
	private Integer pageSize; // 每页条数
	private Integer pageNum; // 页数
	private String serviceType; // 活动类型、移动还是宽带
	private String todayTime; // 当前日期(可能为空)点击今日回访时传

	private String isVaild;//判断工单是否有效
	private String sort;	//排序字段
	private Integer order;//排序 DESC
	
	private String sql;
	
	private List<String> queryFields; 
	
	private String activitySeqs;
	
	private String partFlag;	//用户分区改造
	private String loginId;	//执行操作改造
	
	//执行查询操作改造
	private String roleType;
	//优化查询 count list 改造
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPartFlag() {
		return partFlag;
	}

	public void setPartFlag(String partFlag) {
		this.partFlag = partFlag;
	}

	public String getActivitySeqs() {
		return activitySeqs;
	}

	public void setActivitySeqs(String activitySeqs) {
		this.activitySeqs = activitySeqs;
	}

	public List<String> getQueryFields() {
		return queryFields;
	}

	public void setQueryFields(List<String> queryFields) {
		this.queryFields = queryFields;
	}

	public String getIsVaild() {
		return isVaild;
	}

	public void setIsVaild(String isVaild) {
		this.isVaild = isVaild;
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	

	public HashMap<String, String> getPama() {
		return pama;
	}

	public void setPama(HashMap<String, String> pama) {
		this.pama = pama;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}


	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}


	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getTodayTime() {
		return todayTime;
	}

	public void setTodayTime(String todayTime) {
		this.todayTime = todayTime;
	}

}
