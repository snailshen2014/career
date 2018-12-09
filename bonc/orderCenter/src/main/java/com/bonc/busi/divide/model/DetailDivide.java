package com.bonc.busi.divide.model;

import com.bonc.busi.interfaces.model.ReqHeader;

public class DetailDivide extends ReqHeader{
	
	private String cityId;
	private String cityName;
	private String acceptPath;
	private Integer divideNum;
	private String curOrderId;
	private String acceptPathDesc;
	private String arups;

	public String getArups() {
		return arups;
	}

	public void setArups(String arups) {
		this.arups = arups;
	}

	public String getAcceptPathDesc() {
		return acceptPathDesc;
	}

	public void setAcceptPathDesc(String acceptPathDesc) {
		this.acceptPathDesc = acceptPathDesc;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAcceptPath() {
		return acceptPath;
	}

	public void setAcceptPath(String acceptPath) {
		this.acceptPath = acceptPath;
	}

	public Integer getDivideNum() {
		return divideNum;
	}

	public void setDivideNum(Integer divideNum) {
		this.divideNum = divideNum;
	}

	public String getCurOrderId() {
		return curOrderId;
	}

	public void setCurOrderId(String curOrderId) {
		this.curOrderId = curOrderId;
	}


}
