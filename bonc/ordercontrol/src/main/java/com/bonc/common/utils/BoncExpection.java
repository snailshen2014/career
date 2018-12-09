package com.bonc.common.utils;

public class BoncExpection extends RuntimeException {
	private static final long serialVersionUID = 0;

	private String code;
	private String msg;

	public BoncExpection() {
		super();
	}

	public BoncExpection(String code, String msg) {
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
