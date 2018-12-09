package com.bonc.busi.send.model;

public class ResJson {

	// 返回状态码
	private String stateCode;
	// 返回提示信息
	private String stateMessage;

	public ResJson(String stateCode, String stateMessage) {
		super();
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
}
