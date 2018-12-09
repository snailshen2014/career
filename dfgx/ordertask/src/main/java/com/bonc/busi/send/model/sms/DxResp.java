package com.bonc.busi.send.model.sms;

import java.util.List;

public class DxResp {

	private Boolean flag;
	private String msg;
	private List<DxReq> errorList;

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<DxReq> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<DxReq> errorList) {
		this.errorList = errorList;
	}
}
