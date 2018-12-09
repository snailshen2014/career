package com.bonc.busi.orderschedule.bo;

import java.util.Date;

public class OrderTablesAssignRecord {
	private Integer ACTIVITY_SEQ_ID;
	private String ACTIVITY_ID;
	private String CHANNEL_ID;
	private String TABLE_NAME;
	private Date ASSIGN_DATE;
	private String TENANT_ID;
	private Integer BUSI_TYPE;
	public Integer getACTIVITY_SEQ_ID() {
		return this.ACTIVITY_SEQ_ID;
	}
	public void setACTIVITY_SEQ_ID(Integer id) {
		this.ACTIVITY_SEQ_ID = id;
	}
	
	public String getACTIVITY_ID() {
		return this.ACTIVITY_ID;
	}
	public void setACTIVITY_ID(String id) {
		this.ACTIVITY_ID = id;
	}
	public String getCHANNEL_ID() {
		return this.CHANNEL_ID;
	}
	public void setCHANNEL_ID(String id) {
		this.CHANNEL_ID = id;
	}
	public String getTABLE_NAME() {
		return this.TABLE_NAME;
	}
	public void setTABLE_NAME(String name) {
		this.TABLE_NAME = name;
	}
	public Date getASSIGN_DATE() {
		return this.ASSIGN_DATE;
	}
	public void setASSIGN_DATE(Date dt) {
		this.ASSIGN_DATE = dt;
	}
	public String getTENANT_ID() {
		return this.TENANT_ID;
	}
	public void setTENANT_ID(String id) {
		this.TENANT_ID = id;
	}
	public Integer getBUSI_TYPE() {
		return this.BUSI_TYPE;
	}
	public void setBUSI_TYPE(Integer type) {
		this.BUSI_TYPE = type;
	}
}
