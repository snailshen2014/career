package com.bonc.busi.orderschedule.bo;

public class WhiteBlackFilterUser {
	private String  USER_ID;
	private String USER_PHONE;
	private String FILTE_TYPE;
	
	public String getUSER_ID() {
		return this.USER_ID;
	}
	public void setUSER_ID(String id) {
		this.USER_ID = id;
	}
	public String getUSER_PHONE() {
		return this.USER_PHONE;
	}
	public void setUSER_PHONE(String phone) {
		this.USER_PHONE = phone;
	}
	public String getFILTE_TYPE() {
		return this.FILTE_TYPE;
	}
	public void setFILTE_TYPE(String type) {
		this.FILTE_TYPE = type;
	}
}
