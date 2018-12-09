package com.bonc.busi.send.model.sms;

import com.bonc.busi.interfaces.model.ReqHeader;

//短信发送表
public class DxReq extends ReqHeader{
	
	//短信主键
	private String id;
	//短信配置ID
	private String smsSetId;
	//短信业务ID
	private String externalId;
	//手机号码
	private String telPhone;
	//发送内容
	private String sendContent;
	//紧急等级(等级越高 优先级越高)
	private Integer sendLev;
	
	//开始时间（yyyy-MM-dd HH:mm）
	private String startTimeStr;
	
	//有效时间(分钟)
	private Integer timeValue;
	
	//事件类型
	private String eventType;
	
	//渠道类型
	private String channelType;
	
	//错误代码
	private String errorCode;

	//查询错误短信的起始位置（分页）
	private Long startNum;
	
	//查询错误短信的终止位置（分页）
	private Long endNum;
	
	//发送日期
	private String sendTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSmsSetId() {
		return smsSetId;
	}

	public void setSmsSetId(String smsSetId) {
		this.smsSetId = smsSetId;
	}


	public String getTelPhone() {
		return telPhone;
	}

	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}

	public String getSendContent() {
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public Integer getSendLev() {
		return sendLev;
	}

	public void setSendLev(Integer sendLev) {
		this.sendLev = sendLev;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public Integer getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Integer timeValue) {
		this.timeValue = timeValue;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}


	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	@Override
	public String toString() {
		return "DxReq [id=" + id + ", smsSetId=" + smsSetId + ", externalId=" + externalId + ", telPhone=" + telPhone
				+ ", sendContent=" + sendContent + ", sendLev=" + sendLev + ", startTimeStr=" + startTimeStr
				+ ", timeValue=" + timeValue + ", eventType=" + eventType + ", channelType=" + channelType
				+ ", errorCode=" + errorCode + ", startNum=" + startNum + ", endNum=" + endNum + ", sendTime="
				+ sendTime + "]";
	}

	public Long getStartNum() {
		return startNum;
	}

	public void setStartNum(Long startNum) {
		this.startNum = startNum;
	}

	public Long getEndNum() {
		return endNum;
	}

	public void setEndNum(Long endNum) {
		this.endNum = endNum;
	}

	
}
