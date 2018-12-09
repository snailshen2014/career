package com.bonc.busi.orderschedule.bo;

import java.util.Date;

public class ParamMap {
	/***************增加记录到渠道工单列表中所用参数******************/
	private Integer 	REC_ID;
	private String CHANN_ID;
	private String TENANT_ID;
	public String getTENANT_ID() {
		return TENANT_ID;
	}
	public void setTENANT_ID(String tENANT_ID) {
		TENANT_ID = tENANT_ID;
	}
	public Integer getREC_ID() {
		return REC_ID;
	}
	public Date getINVALID_DATE() {
		return INVALID_DATE;
	}
	public void setINVALID_DATE(Date iNVALID_DATE) {
		INVALID_DATE = iNVALID_DATE;
	}
	public void setREC_ID(Integer rEC_ID) {
		REC_ID = rEC_ID;
	}
	public String getCHANN_ID() {
		return CHANN_ID;
	}
	public void setCHANN_ID(String cHANN_ID) {
		CHANN_ID = cHANN_ID;
	}
	/************删除重复工单所用参数*************************/
	private Integer ACTIVITY_SEQ_ID;
	private String USER_ID;
	private Integer ORDER_UPDATE_RULE;
	private Date INVALID_DATE;
	private String type;
	private Date LAST_ORDER_CREATE_TIME;
	private Integer ORDER_GEN_RULE;
	
	public Date getLAST_ORDER_CREATE_TIME() {
		return LAST_ORDER_CREATE_TIME;
	}
	public void setLAST_ORDER_CREATE_TIME(Date lAST_ORDER_CREATE_TIME) {
		LAST_ORDER_CREATE_TIME = lAST_ORDER_CREATE_TIME;
	}
	public Integer getORDER_GEN_RULE() {
		return ORDER_GEN_RULE;
	}
	public void setORDER_GEN_RULE(Integer oRDER_GEN_RULE) {
		ORDER_GEN_RULE = oRDER_GEN_RULE;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getACTIVITY_SEQ_ID() {
		return ACTIVITY_SEQ_ID;
	}
	public void setACTIVITY_SEQ_ID(Integer aCTIVITY_SEQ_ID) {
		ACTIVITY_SEQ_ID = aCTIVITY_SEQ_ID;
	}
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	public Integer getORDER_UPDATE_RULE() {
		return ORDER_UPDATE_RULE;
	}
	public void setORDER_UPDATE_RULE(Integer oRDER_UPDATE_RULE) {
		ORDER_UPDATE_RULE = oRDER_UPDATE_RULE;
	}
	/************根据接触频次过滤工单所用参数*************************/
	private String ORG_PATH;
	private String CONTACT_DATE;
	private String ACTIVITY_ID;
	private Date BEGIN_DATE ;
	public Date getBEGIN_DATE() {
		return BEGIN_DATE;
	}
	public void setBEGIN_DATE(Date bEGIN_DATE) {
		BEGIN_DATE = bEGIN_DATE;
	}
	public String getACTIVITY_ID() {
		return ACTIVITY_ID;
	}
	public void setACTIVITY_ID(String aCTIVITY_ID) {
		ACTIVITY_ID = aCTIVITY_ID;
	}
	public String getORG_PATH() {
		return ORG_PATH;
	}
	public void setORG_PATH(String oRG_PATH) {
		ORG_PATH = oRG_PATH;
	}
	public String getCONTACT_DATE() {
		return CONTACT_DATE;
	}
	public void setCONTACT_DATE(String cONTACT_DATE) {
		CONTACT_DATE = cONTACT_DATE;
	}
	
	
	
}
