package com.bonc.busi.interfaces.model;

public class RespHeader {

	private String code;
	private String msg;

	public RespHeader() {
		super();
	}

	public RespHeader(String code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
