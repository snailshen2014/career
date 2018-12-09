package com.bonc.busi.interfaces.model.alertwin;

import com.bonc.busi.interfaces.model.ReqHeader;

public class ActivityQueryReq extends ReqHeader {

	private String subChannel;
	private String phoneNum;
	private String accPeriod;

	public String getSubChannel() {
		return subChannel;
	}

	public void setSubChannel(String subChannel) {
		this.subChannel = subChannel;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getAccPeriod() {
		return accPeriod;
	}

	public void setAccPeriod(String accPeriod) {
		this.accPeriod = accPeriod;
	}

}
