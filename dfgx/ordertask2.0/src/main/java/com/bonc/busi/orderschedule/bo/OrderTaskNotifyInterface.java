package com.bonc.busi.orderschedule.bo;

import java.util.Date;

public class OrderTaskNotifyInterface {
	String ACTIVITY_ID;
	Integer ACTIVITY_SEQ_ID;
	String CHANNEL_ID;
	Integer TASK_TYPE;
	Integer STATUS;
	Date BEGIN_DATE;
	Date END_DATE;
	String ORDER_TABLE_NAME;
	Date TASK_GEN_DATE;
	Integer TASK_NUMBER;
	Integer TASK_PRIORITY;
	String TENANT_ID;
	
	public void setACTIVITY_ID(String id) {
		this.ACTIVITY_ID = id;
	}
	public String getACTIVITY_ID() {
		return this.ACTIVITY_ID;
	}
	public void setACTIVITY_SEQ_ID(Integer id) {
		this.ACTIVITY_SEQ_ID = id;
	}
	public Integer getACTIVITY_SEQ_ID() {
		return this.ACTIVITY_SEQ_ID;
	}
	public void setCHANNEL_ID(String id) {
		this.CHANNEL_ID = id;
	}
	public String getCHANNEL_ID() {
		return this.CHANNEL_ID;
	}
	public void setTASK_TYPE(Integer type) {
		this.TASK_TYPE = type;
	}
	public Integer getTASK_TYPE() {
		return this.TASK_TYPE;
	}
	public void setSTATUS(Integer status) {
		this.STATUS = status;
	}
	public Integer getSTATUS() {
		return this.STATUS;
	}
	public void setBEGIN_DATE(Date dt) {
		this.BEGIN_DATE = dt;
	}
	public Date getBEGIN_DATE() {
		return this.BEGIN_DATE;
	}
	public void setEND_DATE(Date dt) {
		this.END_DATE = dt;
	}
	public Date getEND_DATE() {
		return this.END_DATE;
	}
	public void setORDER_TABLE_NAME(String name) {
		this.ORDER_TABLE_NAME = name;
	}
	public String getORDER_TABLE_NAME() {
		return this.ORDER_TABLE_NAME;
	}
	public void setTASK_GEN_DATE(Date dt) {
		this.TASK_GEN_DATE = dt;
	}
	public Date getTASK_GEN_DATE() {
		return this.TASK_GEN_DATE;
	}
	public void setTASK_NUMBER(Integer num) {
		this.TASK_NUMBER = num;
	}
	public Integer getTASK_NUMBER() {
		return this.TASK_NUMBER;
	}
	public void setTASK_PRIORITY(Integer pri) {
		this.TASK_PRIORITY = pri;
	}
	public Integer getTASK_PRIORITY() {
		return this.TASK_PRIORITY;
	}
	public void setTENANT_ID(String id) {
		this.TENANT_ID = id;
	}
	public String getTENANT_ID() {
		return this.TENANT_ID;
	}
}
