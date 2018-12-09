package com.bonc.busi.entity;

public class ResJson {
	
    private Integer orderId;
	//返回状态码
	private String stateCode;
	//返回提示信息
	private String stateMessage;
	
	public ResJson(String stateCode, String stateMessage) {
		super();
		this.stateCode = stateCode;
		this.stateMessage = stateMessage;
	}
	
	public ResJson(Integer orderId, String stateCode, String stateMessage) {
		super();
		this.orderId = orderId;
		this.stateCode = stateCode;
		this.stateMessage = stateMessage;
	}

	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getStateMessage() {
		return stateMessage;
	}
	public void setStateMessage(String stateMessage) {
		this.stateMessage = stateMessage;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "ResJson [orderId=" + orderId + ", stateCode=" + stateCode + ", stateMessage=" + stateMessage + "]";
	}
	
	
	
	
}
