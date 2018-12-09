package com.bonc.busi.activity;

import java.util.Date;
import java.util.List;
import java.util.Map;




/**
 * 
 * <p>Title: JEEAC - ActivityProvPo </p>
 * 
 * <p>Description: 日常维系活动(po) - clyx_activity_daily </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class ActivityProvPo extends StockMarketingPo {
	
	/**
	 * 活动Id
	 */
	private String activityId;
	/**
	 * 活动区分：1、执行活动，2、下发活动
	 */
	private String activityDivision;
	/**
	 * 活动名称
	 */
	private String activityName;
	
	/**
	 * 活动说明
	 */
	private String activityDesc;
	
	/**
	 * 联系电话
	 */
	private String telephone;
	/**
	 * 活动主题
	 */
	private String activityTheme;
	/**
	 * 活动主题Id
	 */
	private String activityThemeCode;
	/**
	 * 活动主题列表，用于新增编辑页面加载
	 */
	private List<Map<String,String>> activityThemeList;
	/**
	 * 创建人名称
	 */
	private String createName;
	/**
	 * 创建时间
	 */
	private String createDate;
	
	/**
	 * 开始时间
	 */
	private String startDate;
	
	/**
	 * 结束时间
	 */
	private String endDate;
	
	
	/**
	 * 组织级别
	 */
	private String orgLevel;
	/**
	 * 组织机构适用范围
	 */
	private String orgRange;
	/**
	 * 用户群id
	 */
	private String userGroupId;
	/**
	 * 用户群名称
	 */
	private String userGroupName;
	/**
	 * 用户群筛选条件
	 */
	private String userGroupFilterCondition;
	/**
	 * 营销目标id
	 */
	private String targetId;
	
	/**
	 * 场景id
	 */
	private String sceneId;	
	/**
	 * 是否临近营业厅
	 */
	private String isNearHall;
	/**
	 * 状态
	 */
	private String state;
	
	/**
	 * 是否选择营销时机
	 */
	/*ysc 8-2 临时试验 地市和省份*/
	/**
	 * 权限省分id 用来控制省分用户活动列表查看权限
	 */
    private String provIds; 
	/**
	 * 地市id
	 */
	private String cityId; 
	/**
	 * 省份id
	 */
	private String provId;
	/**
	 * 总部活动
	 */
	private String parentId;
	/**
	 * 活动类型Id
	 */
	private String activityTypeId;
	/**
	 * 活动类型
	 */
	private String activityType;
	/**
	 * 紧急程度
	 */
	private String urgencyLevel;
	private String isSaleTime;
	
	/**
	 * 落地用户群名称
	 */
	private String userGroupTable;
	
	private String execFlag;
	/**
	 * 政策id
	 */
	private String policyId;
	/**
	 * 是否剔除黑名单
	 */
	private String isDeleteBlackUser;
	/**
	 * 是否剔除黑名单
	 */
	private String isDeleteWhiteUser;
	
	/**
	 * 活动周期
	 */
	private String activityCycle;
	/**
	 * 工单周期
	 */
	private String orderCycle;
	
	/**
	 * 工单更新规则
	 */
	private String orderUpdateRule;
	
	/**
	 * 接触限制，多少天一次
	 */
	private String touchLimitDay;
	
	/**
	 * 是否同一活动分类用户剔除
	 */
	private String isDeleteSameType;
	/**
	 * 是否同一活动成功标准类型用户剔除
	 */
	private String isDeleteSameSuccess;
	/**
	 * 策略名称
	 */
	private String strategyName;
	/**
	 * 策略分类
	 */
	private String strategyType;
	/**
	 * 策略描述
	 */
	private String strategyDesc;
		
	/**
	 * 一线执行渠道
	 */
	private FrontlineChannelPo frontlineChannelPo;
	
	private List<?> activityPiciList;
	
	
	/**
	 * weixin qvdao
	 */
	private ChannelWebchatInfo channelWebchatInfo;
	/**
	 * 短信执行渠道
	 */
	private MsmChannelPo MsmChannelPo;
	/**
	 * 成功标准信息
	 */
	private SuccessStandardPo successStandardPo;
	/**
	 * 沃视窗
	 */
	private ChannelWoWindowPo channelWoWindowPo; 
	/**
	 * 
	 * 网厅信息
	 */
	private ChannelWebOfficePo ChannelWebOfficePo;
	/**
	 * 
	 * 手厅信息
	 */
	private ChannelHandOfficePo channelHandOfficePo;
	
	private MsmChannelPo msmChannelPo;
	
	private ChannelSpecialFilterPo channelSpecialFilterPo;
	/**
	 * 自定义特殊筛选
	 */
	private List<ChannelSpecialFilterPo> ChannelSpecialFilterList;
	
	/**
	 * 限制用户处理方式:1、发送工单，0、不发送工单
	 */
	private String isSendOrder;
	/**
	 * 短信微信互斥发送规则:1、各自重复发送，0、互斥发送
	 */
	private String selfSendChannelRule;
	/**
	 * 电子渠道互斥发送规则:1、各自展示，0、展示其中一个(暂不支持)
	 */
	private String eChannelShowRule;
	/**
	 * 客户经理与弹窗互斥发送规则:1、各自执行，0、互斥执行
	 */
	private String otherChannelExeRule;
	/**
	 * 总部关联活动Id
	 */
	private String parentActivity;
	/**
	 * 总部关联活动名称
	 */
	private String parentActivityName;
	
	/**
	 * 总部关联活动开始日期
	 */
	private String parentActivityStartDate;
	/**
	 * 总部关联活动结束日期
	 */
	private String parentActivityEndDate;
	/**
	 * 总部关联活动省份Id
	 */
	private String parentProvId;
	/**
	 * 创建人组织机构Id
	 * @return
	 */
	private String createOrgId;
	/**
	 * 创建人组织机构名称
	 */
	private String createOrgName;
	/**
	 * 创建人组织机构路径
	 * @return
	 */
	private String createOrgPath;
	private String orgIds;
	/**
	 * 工单是否参照活动结束时间：1、是，0、否
	 */
	private String orderIsConsultEndDate;
	/*
	 * 预留工单百分比
	 */
	private String obligateOrder;
	/*
	 * 短信使用端口号
	 */
	private String smsUsePort;
	
	
	
	public String getSmsUsePort() {
		return smsUsePort;
	}

	public void setSmsUsePort(String smsUsePort) {
		this.smsUsePort = smsUsePort;
	}

	public String getObligateOrder() {
		return obligateOrder;
	}

	public void setObligateOrder(String obligateOrder) {
		this.obligateOrder = obligateOrder;
	}

	public String getOrderIsConsultEndDate() {
		return orderIsConsultEndDate;
	}

	public void setOrderIsConsultEndDate(String orderIsConsultEndDate) {
		this.orderIsConsultEndDate = orderIsConsultEndDate;
	}

	public String getOrgIds() {
		return orgIds;
	}

	public void setOrgIds(String orgIds) {
		this.orgIds = orgIds;
	}

	public String getCreateOrgId() {
		return createOrgId;
	}

	public void setCreateOrgId(String createOrgId) {
		this.createOrgId = createOrgId;
	}

	public String getCreateOrgPath() {
		return createOrgPath;
	}

	public void setCreateOrgPath(String createOrgPath) {
		this.createOrgPath = createOrgPath;
	}

	public String getParentActivityName() {
		return parentActivityName;
	}

	public void setParentActivityName(String parentActivityName) {
		this.parentActivityName = parentActivityName;
	}

	public String getParentActivityStartDate() {
		return parentActivityStartDate;
	}

	public void setParentActivityStartDate(String parentActivityStartDate) {
		this.parentActivityStartDate = parentActivityStartDate;
	}

	public String getUserGroupFilterCondition() {
		return userGroupFilterCondition;
	}

	public void setUserGroupFilterCondition(String userGroupFilterCondition) {
		this.userGroupFilterCondition = userGroupFilterCondition;
	}

	public String getCreateOrgName() {
		return createOrgName;
	}

	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}

	public String getParentActivityEndDate() {
		return parentActivityEndDate;
	}

	public void setParentActivityEndDate(String parentActivityEndDate) {
		this.parentActivityEndDate = parentActivityEndDate;
	}

	public String getParentProvId() {
		return parentProvId;
	}

	public void setParentProvId(String parentProvId) {
		this.parentProvId = parentProvId;
	}

	public List<ChannelSpecialFilterPo> getChannelSpecialFilterList() {
		return ChannelSpecialFilterList;
	}

	public void setChannelSpecialFilterList(
			List<ChannelSpecialFilterPo> channelSpecialFilterList) {
		ChannelSpecialFilterList = channelSpecialFilterList;
	}

	public String getParentActivity() {
		return parentActivity;
	}

	public void setParentActivity(String parentActivity) {
		this.parentActivity = parentActivity;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getSelfSendChannelRule() {
		return selfSendChannelRule;
	}

	public void setSelfSendChannelRule(String selfSendChannelRule) {
		this.selfSendChannelRule = selfSendChannelRule;
	}

	public String geteChannelShowRule() {
		return eChannelShowRule;
	}

	public void seteChannelShowRule(String eChannelShowRule) {
		this.eChannelShowRule = eChannelShowRule;
	}

	public String getOtherChannelExeRule() {
		return otherChannelExeRule;
	}

	public void setOtherChannelExeRule(String otherChannelExeRule) {
		this.otherChannelExeRule = otherChannelExeRule;
	}

	public String getIsSendOrder() {
		return isSendOrder;
	}

	public void setIsSendOrder(String isSendOrder) {
		this.isSendOrder = isSendOrder;
	}

	


	public String getActivityTheme() {
		return activityTheme;
	}

	public void setActivityTheme(String activityTheme) {
		this.activityTheme = activityTheme;
	}

	public String getActivityThemeCode() {
		return activityThemeCode;
	}

	public void setActivityThemeCode(String activityThemeCode) {
		this.activityThemeCode = activityThemeCode;
	}

	public List<Map<String, String>> getActivityThemeList() {
		return activityThemeList;
	}

	public void setActivityThemeList(List<Map<String, String>> activityThemeList) {
		this.activityThemeList = activityThemeList;
	}




	/**
	 * 
	 * 集团短信批次信息
	 */
	private ChannelJtdxPiciPo  channelJtdxPiciPo ;
	
	public ChannelJtdxPiciPo getChannelJtdxPiciPo() {
		return channelJtdxPiciPo;
	}

	public void setChannelJtdxPiciPo(ChannelJtdxPiciPo channelJtdxPiciPo) {
		this.channelJtdxPiciPo = channelJtdxPiciPo;
	}
	
	public ChannelWebOfficePo getChannelWebOfficePo() {
		return ChannelWebOfficePo;
	}

	public void setChannelWebOfficePo(ChannelWebOfficePo channelWebOfficePo) {
		ChannelWebOfficePo = channelWebOfficePo;
	}

	public ChannelHandOfficePo getChannelHandOfficePo() {
		return channelHandOfficePo;
	}

	public void setChannelHandOfficePo(ChannelHandOfficePo channelHandOfficePo) {
		this.channelHandOfficePo = channelHandOfficePo;
	}

	public ChannelWoWindowPo getChannelWoWindowPo() {
		return channelWoWindowPo;
	}

	public void setChannelWoWindowPo(ChannelWoWindowPo channelWoWindowPo) {
		this.channelWoWindowPo = channelWoWindowPo;
	}


	private String activityFunction;
	/*
	 * 成功标准
	 */
	private String successId;
	

	public String getSuccessId() {
		return successId;
	}

	public void setSuccessId(String successId) {
		this.successId = successId;
	}
	
	

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getUrgencyLevel() {
		return urgencyLevel;
	}

	public void setUrgencyLevel(String urgencyLevel) {
		this.urgencyLevel = urgencyLevel;
	}

	public String getActivityTypeId() {
		return activityTypeId;
	}

	public void setActivityTypeId(String activityTypeId) {
		this.activityTypeId = activityTypeId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getProvIds() {
		return provIds;
	}

	public void setProvIds(String provIds) {
		this.provIds = provIds;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	
	public String activityDailyId;
	
	public String activityUnderName;
	
	private ChannelGroupPopupPo channelGroupPopupPo;
	private List<ChannelGroupPopupPo> channelGroupPopupPoList;
	
	public List<ChannelGroupPopupPo> getChannelGroupPopupPoList() {
		return channelGroupPopupPoList;
	}

	public void setChannelGroupPopupPoList(
			List<ChannelGroupPopupPo> channelGroupPopupPoList) {
		this.channelGroupPopupPoList = channelGroupPopupPoList;
	}

	public ChannelGroupPopupPo getChannelGroupPopupPo() {
		return channelGroupPopupPo;
	}

	public void setChannelGroupPopupPo(ChannelGroupPopupPo channelGroupPopupPo) {
		this.channelGroupPopupPo = channelGroupPopupPo;
	}

	public String getActivityDivision() {
		return activityDivision;
	}

	public void setActivityDivision(String activityDivision) {
		this.activityDivision = activityDivision;
	}

	public String getActivityUnderName() {
		return activityUnderName;
	}

	public void setActivityUnderName(String activityUnderName) {
		this.activityUnderName = activityUnderName;
	}

	public String getActivityDailyId() {
		return activityDailyId;
	}

	public void setActivityDailyId(String activityDailyId) {
		this.activityDailyId = activityDailyId;
	}

	
	public String getExecFlag() {
		return execFlag;
	}

	public void setExecFlag(String execFlag) {
		this.execFlag = execFlag;
	}

	public String getUserGroupTable() {
		return userGroupTable;
	}

	public void setUserGroupTable(String userGroupTable) {
		this.userGroupTable = userGroupTable;
	}

	public String getIsSaleTime() {
		return isSaleTime;
	}

	public void setIsSaleTime(String isSaleTime) {
		this.isSaleTime = isSaleTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
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

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getSceneId() {
		return sceneId;
	}

	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}

	public String getIsNearHall() {
		return isNearHall;
	}

	public void setIsNearHall(String isNearHall) {
		this.isNearHall = isNearHall;
	}

	public String getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}

   

	public String getIsDeleteBlackUser() {
		return isDeleteBlackUser;
	}

	public void setIsDeleteBlackUser(String isDeleteBlackUser) {
		this.isDeleteBlackUser = isDeleteBlackUser;
	}

	public String getIsDeleteWhiteUser() {
		return isDeleteWhiteUser;
	}

	public void setIsDeleteWhiteUser(String isDeleteWhiteUser) {
		this.isDeleteWhiteUser = isDeleteWhiteUser;
	}

	public String getActivityCycle() {
		return activityCycle;
	}

	public void setActivityCycle(String activityCycle) {
		this.activityCycle = activityCycle;
	}

	public String getOrderCycle() {
		return orderCycle;
	}

	public void setOrderCycle(String orderCycle) {
		this.orderCycle = orderCycle;
	}

	public String getOrderUpdateRule() {
		return orderUpdateRule;
	}

	public void setOrderUpdateRule(String orderUpdateRule) {
		this.orderUpdateRule = orderUpdateRule;
	}

	public String getTouchLimitDay() {
		return touchLimitDay;
	}

	public void setTouchLimitDay(String touchLimitDay) {
		this.touchLimitDay = touchLimitDay;
	}

	public String getIsDeleteSameType() {
		return isDeleteSameType;
	}

	public void setIsDeleteSameType(String isDeleteSameType) {
		this.isDeleteSameType = isDeleteSameType;
	}

	public String getIsDeleteSameSuccess() {
		return isDeleteSameSuccess;
	}

	public void setIsDeleteSameSuccess(String isDeleteSameSuccess) {
		this.isDeleteSameSuccess = isDeleteSameSuccess;
	}

	public String getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(String strategyType) {
		this.strategyType = strategyType;
	}

	public String getStrategyDesc() {
		return strategyDesc;
	}

	public void setStrategyDesc(String strategyDesc) {
		this.strategyDesc = strategyDesc;
	}

	public FrontlineChannelPo getFrontlineChannelPo() {
		return frontlineChannelPo;
	}

	public void setFrontlineChannelPo(FrontlineChannelPo frontlineChannelPo) {
		this.frontlineChannelPo = frontlineChannelPo;
	}

	public MsmChannelPo getMsmChannelPo() {
		return MsmChannelPo;
	}

	public void setMsmChannelPo(MsmChannelPo msmChannelPo) {
		MsmChannelPo = msmChannelPo;
	}

	public String getActivityDesc() {
		return activityDesc;
	}

	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}

	public String getOrgRange() {
		return orgRange;
	}

	public void setOrgRange(String orgRange) {
		this.orgRange = orgRange;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public SuccessStandardPo getSuccessStandardPo() {
		return successStandardPo;
	}

	public void setSuccessStandardPo(SuccessStandardPo successStandardPo) {
		this.successStandardPo = successStandardPo;
	}

	public String getActivityFunction() {
		return activityFunction;
	}

	public void setActivityFunction(String activityFunction) {
		this.activityFunction = activityFunction;
	}

	public ChannelWebchatInfo getChannelWebchatInfo() {
		return channelWebchatInfo;
	}

	public void setChannelWebchatInfo(ChannelWebchatInfo channelWebchatInfo) {
		this.channelWebchatInfo = channelWebchatInfo;
	}
	
	   private String channelCheck;

	    public String getChannelCheck() {
		    return channelCheck;
	    }

	    public void setChannelCheck(String channelCheck) {
		    this.channelCheck = channelCheck;
	}

		public ChannelSpecialFilterPo getChannelSpecialFilterPo() {
			return channelSpecialFilterPo;
		}

		public void setChannelSpecialFilterPo(ChannelSpecialFilterPo channelSpecialFilterPo) {
			this.channelSpecialFilterPo = channelSpecialFilterPo;
		}

		public List<?> getActivityPiciList() {
			return activityPiciList;
		}

		public void setActivityPiciList(List<?> activityPiciList) {
			this.activityPiciList = activityPiciList;
		}

}
