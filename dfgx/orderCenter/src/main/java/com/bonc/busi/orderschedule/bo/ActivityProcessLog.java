package com.bonc.busi.orderschedule.bo;

import java.util.Date;

public class ActivityProcessLog {

	private String ACTIVITY_ID;
	private String TENANT_ID;
	private String CHANNEL_ID;
	private String CHANNEL_ORDER_NUM;
	private int STATUS;
	private Date  BEGIN_DATE;
	private Date END_DATE;
	Integer ACTIVITY_SEQ_ID;
	
	public void setACTIVITY_ID(String id){
		this.ACTIVITY_ID = id;
	}
	public String getACTIVITY_ID(){
		return this.ACTIVITY_ID;
	}
	public void setTENANT_ID(String id){
		this.TENANT_ID = id;
	}
	public String getTENANT_ID(){
		return this.TENANT_ID;
	}
	public void setCHANNEL_ID(String id){
		this.CHANNEL_ID = id;
	}
	public String getCHANNEL_ID(){
		return this.CHANNEL_ID;
	}
	public void setCHANNEL_ORDER_NUM(String id){
		this.CHANNEL_ORDER_NUM = id;
	}
	public String getCHANNEL_ORDER_NUM(){
		return this.CHANNEL_ORDER_NUM;
	}
	public void setSTATUS(int ss){
		this.STATUS = ss;
	}
	public int getSTATUS(){
		return this.STATUS;
	}
	public void setBEGIN_DATE(Date ss){
		this.BEGIN_DATE = ss;
	}
	public Date getBEGIN_DATE(){
		return this.BEGIN_DATE;
	}
	public void setEND_DATE(Date ss){
		this.END_DATE = ss;
	}
	public Date getEND_DATE(){
		return this.END_DATE;
	}
	public void setACTIVITY_SEQ_ID(Integer ss){
		this.ACTIVITY_SEQ_ID = ss;
	}
	public Integer getACTIVITY_SEQ_ID(){
		return this.ACTIVITY_SEQ_ID;
	}
}
