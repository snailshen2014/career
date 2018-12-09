package com.bonc.busi.orderschedule.bo;

public class BlackWhiteUserList {
	private String USER_ID;
	private String USER_PHONE;
	private String FILTE_OPERATOR;
	private String FILTE_OPERATE_TIME;
	private String TENANT_ID;
	//FILTE_TYPE=01 blacklist，=02 white user list
	private String FILTE_TYPE;
	private String FILTE_DESC;
	
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uid) {
		this.USER_ID = uid;
	}
	public String getUSER_PHONE() {
		return USER_PHONE;
	}
	public void setUSER_PHONE(String phone) {
		this.USER_PHONE = phone;
	}
	public String getFILTE_OPERATOR() {
		return FILTE_OPERATOR;
	}
	public void setFILTE_OPERATOR(String opr) {
		this.FILTE_OPERATOR = opr;
	}
	public String getFILTE_OPERATE_TIME() {
		return FILTE_OPERATE_TIME;
	}
	public void setFILTE_OPERATE_TIME(String opr) {
		this.FILTE_OPERATE_TIME = opr;
	}
	public String getTENANT_ID() {
		return TENANT_ID;
	}
	public void setTENANT_ID(String tid) {
		this.TENANT_ID = tid;
	}
	public String getFILTE_TYPE() {
		return FILTE_TYPE;
	}
	public void setFILTE_TYPE(String ty) {
		this.FILTE_TYPE = ty;
	}
	public String getFILTE_DESC() {
		return FILTE_DESC;
	}
	public void setFILTE_DESC(String desc) {
		this.FILTE_DESC = desc;
	}
}
