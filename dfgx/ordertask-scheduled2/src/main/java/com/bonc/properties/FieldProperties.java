package com.bonc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "field.pamas", ignoreUnknownFields = false)
public class FieldProperties {

	private String telPhone;
	private String activeName;
	private String accFee;// 余额
	private String custType;// 客户类型
	private String subsStatus;// 客户状态
	private String activeId;// 客户状态
	private String proEndTime;// 协议到期时间
	private String packageFee;// 套餐金额 >96视为高套餐
	private String isRonghe;// 融合（用于判断是否融合）
	private String likeEco;// 是否电子渠道偏好
	private String proType;// 合约类型
	private String endTime;// 工单结束时间
	private String contactResult;// 接触结果
	private String isSuccess;// 是否办理
	private String startTime;// 工单产生月份
	
	public String getTelPhone() {
		return telPhone;
	}

	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}

	public String getActiveId() {
		return activeId;
	}

	public void setActiveId(String activeId) {
		this.activeId = activeId;
	}

	public String getActiveName() {
		return activeName;
	}

	public void setActiveName(String activeName) {
		this.activeName = activeName;
	}

	public String getAccFee() {
		return accFee;
	}

	public void setAccFee(String accFee) {
		this.accFee = accFee;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getSubsStatus() {
		return subsStatus;
	}

	public void setSubsStatus(String subsStatus) {
		this.subsStatus = subsStatus;
	}

	public String getProEndTime() {
		return proEndTime;
	}

	public void setProEndTime(String proEndTime) {
		this.proEndTime = proEndTime;
	}

	public String getPackageFee() {
		return packageFee;
	}

	public void setPackageFee(String packageFee) {
		this.packageFee = packageFee;
	}

	public String getIsRonghe() {
		return isRonghe;
	}

	public void setIsRonghe(String isRonghe) {
		this.isRonghe = isRonghe;
	}

	public String getLikeEco() {
		return likeEco;
	}

	public void setLikeEco(String likeEco) {
		this.likeEco = likeEco;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getContactResult() {
		return contactResult;
	}

	public void setContactResult(String contactResult) {
		this.contactResult = contactResult;
	}

	public String getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

}
