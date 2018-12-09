package com.bonc.busi.entity;

import java.util.Date;

public class ActivityPo {
	
	/**
	 * 登录人姓名
	 */
	private String userName;

	/**
	 * 活动Id
	 */
	private String activityId;
	
	/**
	 * 活动名称
	 */
	private String activityName;
	
	/**
	 * 活动描述
	 */
	private String activityDesc;
	
	/**
	 * 政策id
	 */
	private String policyId;
	
	/**
	 * 政策ids 用来区分前一个 政策id
	 * @lwx
	 */
	private String policyIds;
	
	/**
	 * 创建时间
	 */
	private Date createDate;
	
	/**
	 * 创建时间
	 */
	private Date createDate2;

	/**
	 * 创建人id
	 */
	private String createId;
	
	/**
	 * 创建人名称
	 */
	private String createName;
	
	/**
	 * 省份id
	 */
	private String provId;
	
	/**
	 * 省份名称
	 */
	private String provName;
	
	/**
	 * 地市id
	 */
	private String cityId; 
	
	/**
	 * 权限省分id 用来控制省分用户活动列表查看权限
	 */
	private String provIds; 
	
	/**
	 * 权限省分id3 用来控制地市用户活动列表查看权限
	 */
	private String provId3; 

	/**
	 * 权限地市id 用来控制地市用户活动列表查看权限
	 */
	private String cityIds; 
	
	/**
	 * 开始时间
	 */
	private Date startDate;
	
	/**
	 * 结束时间
	 */
	private Date endDate;
	/**
	 * 活动状态
	 */
	private String status;
	
	/**
	 * 用户数
	 */
	private String userGroupNum;
	
	/**
	 * 是否省份执行 1-是 0-否
	 */
	private String isProvExecute;
	
	/**
	 * 用户群id
	 */
	private String userGroupId;
	
	/**
	 * 用户群名称
	 */
	private String userGroupName;
	/**
	 * 用户群多账期标识
	 */
	private String userGroupPhotocondation;
	/**
	 * 流程id
	 */
	private String flowId;
	
	/**
	 * 流程id
	 */
	private String flowTypeId;
	
	/**
	 * 父活动id
	 */
	private String parentId;
	
	/**
	 * 活动配置指导意见
	 */
	private String suggestionContent;
	/**
	 * 活动选择产品指导意见
	 */
	private String productAdvice;
	
	/**
	 * 活动周期性 1：一次性 2：周期性 
	 */
	private String cycleInfo;
	
	/**
	 * 立方体id
	 * 作为下发使用字段
	 */
	private String cubeId;
	
	/**
	 * 活动状态 1：暂存 2：提交,3:不通过
	 */
	private String state;
	
	/**
	 * 是否是子活动1-是 0-否
	 */
	private String isSubActivity;
	
	/**
	 * 子活动是否需要集团渠道审批 1或者0
	 */
	private String isJtChannel;
	
	/**
	 * 数据下发账期
	 */
	private String dataSendingDate;
	
	/**
	 * 线下拆分子活动名称
	 */
	private String activityChildName;
	
	/**
	 * 线下拆分子活动说明
	 */
	private String activityChildRemark;
	
	/**
	 * 线下拆分子活动预算
	 */
	private String budget;
	
	/**
	 * 线下拆分子活动类型
	 */
	private String splitType;
	
	/**
	 * 活动创建归属地Id
	 */
	private String areaId;
	
	/**
	 * 活动创建归属地名称
	 */
	private String areaName;


	/**
	 * 
	 * @return
	 */
	private String activityIds;
	
	public String getActivityIds() {
		return activityIds;
	}

	public void setActivityIds(String activityIds) {
		this.activityIds = activityIds;
	}

	public String getBudget() {
		return budget;
	}

	public void setBudget(String budget) {
		this.budget = budget;
	}

	public String getActivityChildName() {
		return activityChildName;
	}

	public void setActivityChildName(String activityChildName) {
		this.activityChildName = activityChildName;
	}

	public String getActivityChildRemark() {
		return activityChildRemark;
	}

	public void setActivityChildRemark(String activityChildRemark) {
		this.activityChildRemark = activityChildRemark;
	}

	public String getIsJtChannel() {
		return isJtChannel;
	}

	public void setIsJtChannel(String isJtChannel) {
		this.isJtChannel = isJtChannel;
	}

	public String getIsSubActivity() {
		return isSubActivity;
	}

	public void setIsSubActivity(String isSubActivity) {
		this.isSubActivity = isSubActivity;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCubeId() {
		return cubeId;
	}

	public void setCubeId(String cubeId) {
		this.cubeId = cubeId;
	}

	public String getCycleInfo() {
		return cycleInfo;
	}

	public void setCycleInfo(String cycleInfo) {
		this.cycleInfo = cycleInfo;
	}

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}
	
	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserGroupNum() {
		return userGroupNum;
	}

	public void setUserGroupNum(String userGroupNum) {
		this.userGroupNum = userGroupNum;
	}

	public String getSuggestionContent() {
		return suggestionContent;
	}

	public void setSuggestionContent(String suggestionContent) {
		this.suggestionContent = suggestionContent;
	}
    
	public String getProductAdvice() {
		return productAdvice;
	}

	public void setProductAdvice(String productAdvice) {
		this.productAdvice = productAdvice;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityDesc() {
		return activityDesc;
	}

	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getIsProvExecute() {
		return isProvExecute;
	}

	public void setIsProvExecute(String isProvExecute) {
		this.isProvExecute = isProvExecute;
	}

	public String getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getFlowTypeId() {
		return flowTypeId;
	}

	public void setFlowTypeId(String flowTypeId) {
		this.flowTypeId = flowTypeId;
	}
	
	public String getCityIds() {
		return cityIds;
	}

	public void setCityIds(String cityIds) {
		this.cityIds = cityIds;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDataSendingDate() {
		return dataSendingDate;
	}

	public void setDataSendingDate(String dataSendingDate) {
		this.dataSendingDate = dataSendingDate;
	}

	public String getPolicyIds() {
		return policyIds;
	}

	public void setPolicyIds(String policyIds) {
		this.policyIds = policyIds;
	}
	

	public String getProvIds() {
		return provIds;
	}

	public void setProvIds(String provIds) {
		this.provIds = provIds;
	}
	
	public String getProvId3() {
		return provId3;
	}

	public void setProvId3(String provId3) {
		this.provId3 = provId3;
	}
	

	public Date getCreateDate2() {
		return createDate2;
	}

	public void setCreateDate2(Date createDate2) {
		this.createDate2 = createDate2;
	}
	
	public String getSplitType() {
		return splitType;
	}

	public void setSplitType(String splitType) {
		this.splitType = splitType;
	}
     

	public String getUserGroupPhotocondation() {
		return userGroupPhotocondation;
	}

	public void setUserGroupPhotocondation(String userGroupPhotocondation) {
		this.userGroupPhotocondation = userGroupPhotocondation;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
   
	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}
