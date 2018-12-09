package com.bonc.busi.send.model.sms;

public class SmsReq {
	private Long sendAllNum;
	private Long sendNum;
	private Long formatErrNum;
	private Long formatSucNum;
	private Long sendSucNum;
	private Long sendErrNum;
	private Long errNum;
	private Boolean flag;
	private String msg;
	public Long getSendAllNum() {
		return sendAllNum;
	}
	public void setSendAllNum(Long sendAllNum) {
		this.sendAllNum = sendAllNum;
	}
	public Long getSendNum() {
		return sendNum;
	}
	public void setSendNum(Long sendNum) {
		this.sendNum = sendNum;
	}
	public Long getFormatErrNum() {
		return formatErrNum;
	}
	public void setFormatErrNum(Long formatErrNum) {
		this.formatErrNum = formatErrNum;
	}
	public Long getSendSucNum() {
		return sendSucNum;
	}
	public void setSendSucNum(Long sendSucNum) {
		this.sendSucNum = sendSucNum;
	}
	public Long getSendErrNum() {
		return sendErrNum;
	}
	public void setSendErrNum(Long sendErrNum) {
		this.sendErrNum = sendErrNum;
	}
	public Long getErrNum() {
		return errNum;
	}
	public void setErrNum(Long errNum) {
		this.errNum = errNum;
	}
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
	public Long getFormatSucNum() {
		return formatSucNum;
	}
	public void setFormatSucNum(Long formatSucNum) {
		this.formatSucNum = formatSucNum;
	}
	
}
