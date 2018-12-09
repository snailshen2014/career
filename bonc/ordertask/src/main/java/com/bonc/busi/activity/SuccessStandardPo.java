package com.bonc.busi.activity;

import java.util.Date;
import java.util.List;




/**
 * 成功标准PO
 * @author ICE
 *
 */
@SuppressWarnings("serial")
public class SuccessStandardPo extends StockMarketingPo {

	/**
	 * 成功标准ID
	 */
	private String successId;
	/**
	 * 成功类型：换卡/换机/办理流量包/合约续约/换套餐/实名登记/宽带续约/办理副卡/4G登网/承诺抵消
	 */
	private String successType;
	private String successTypeId;
	private String successTypeCondition;
	private String successTypeConditionSql;
	/**
	 * 成功名称
	 */
	private String successName;
	/**
	 * 匹配类型:1=全部匹配；2=精确匹配
	 */
	private String matchingType;
	private String matchingTypeId;
	/**
	 * 成功产品
	 */
	private List<SuccessProductPo> successProductList;
	private String successProductIds;

	/**
	 * 成功条件：flow>300
	 */
	private String successCondition;
	/**
	 * 成功奖励描述
	 */
	private String successReward;
	/**
	 * 成功积分
	 */
	private String successPoint;
	private String orgId; //组织机构id
	private String successConditionSQL; //成功条件生成sql
	private String orgpath; //组织机构路径
	private String operateUser; //操作执行人
	private Date operateTime; //操作时间
	
	//虚拟产品成功标准标识   1:是     0：否
	private String isHaveRenewProduct;
	
	public String getSuccessTypeCondition() {
		return successTypeCondition;
	}

	public void setSuccessTypeCondition(String successTypeCondition) {
		this.successTypeCondition = successTypeCondition;
	}

	public String getSuccessTypeConditionSql() {
		return successTypeConditionSql;
	}

	public void setSuccessTypeConditionSql(String successTypeConditionSql) {
		this.successTypeConditionSql = successTypeConditionSql;
	}

	public String getSuccessProductIds() {
		return successProductIds;
	}

	public void setSuccessProductIds(String successProductIds) {
		this.successProductIds = successProductIds;
	}

	public String getMatchingTypeId() {
		return matchingTypeId;
	}

	public void setMatchingTypeId(String matchingTypeId) {
		this.matchingTypeId = matchingTypeId;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getSuccessTypeId() {
		return successTypeId;
	}

	public void setSuccessTypeId(String successTypeId) {
		this.successTypeId = successTypeId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getSuccessConditionSQL() {
		return successConditionSQL;
	}

	public void setSuccessConditionSQL(String successConditionSQL) {
		this.successConditionSQL = successConditionSQL;
	}

	public String getOrgpath() {
		return orgpath;
	}

	public void setOrgpath(String orgpath) {
		this.orgpath = orgpath;
	}

	public String getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}

	public String getSuccessId() {
		return successId;
	}

	public void setSuccessId(String successId) {
		this.successId = successId;
	}

	public String getSuccessType() {
		return successType;
	}

	public void setSuccessType(String successType) {
		this.successType = successType;
	}

	public String getSuccessName() {
		return successName;
	}

	public void setSuccessName(String successName) {
		this.successName = successName;
	}

	public String getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(String matchingType) {
		this.matchingType = matchingType;
	}

	public List<SuccessProductPo> getSuccessProductList() {
		return successProductList;
	}

	public void setSuccessProductList(List<SuccessProductPo> successProductList) {
		this.successProductList = successProductList;
	}

	public String getSuccessCondition() {
		return successCondition;
	}

	public void setSuccessCondition(String successCondition) {
		this.successCondition = successCondition;
	}

	public String getSuccessReward() {
		return successReward;
	}

	public void setSuccessReward(String successReward) {
		this.successReward = successReward;
	}

	public String getSuccessPoint() {
		return successPoint;
	}

	public void setSuccessPoint(String successPoint) {
		this.successPoint = successPoint;
	}
	//add by shenyj for tenant_id,activity_id
	private String activityId; //活动id
	private String tenantId; //租户id
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	private String activity_seq_id;
	public String getActivity_seq_id() {
		return this.activity_seq_id;
	}

	public void setActivity_seq_id(String id) {
		this.activity_seq_id = id;
	}

	public String getIsHaveRenewProduct() {
		return isHaveRenewProduct;
	}

	public void setIsHaveRenewProduct(String isHaveRenewProduct) {
		this.isHaveRenewProduct = isHaveRenewProduct;
	}
	
}
