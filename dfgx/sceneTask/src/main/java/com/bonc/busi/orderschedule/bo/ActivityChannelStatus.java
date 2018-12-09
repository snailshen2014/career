package com.bonc.busi.orderschedule.bo;

public class ActivityChannelStatus {

	private Integer ACTIVITY_SEQ_ID;
	private String ACTIVITY_ID;
	private String TENANT_ID;
	private String CHANNEL_ID;
	private String STATUS;
	
	public void setACTIVITY_SEQ_ID(Integer id) {
		this.ACTIVITY_SEQ_ID = id;
	}
	public Integer getACTIVITY_SEQ_ID() {
		return ACTIVITY_SEQ_ID;
	}
	public void setACTIVITY_ID(String id) {
		this.ACTIVITY_ID = id;
	}
	public String getACTIVITY_ID() {
		return ACTIVITY_ID;
	}
	public void setTENANT_ID(String id) {
		this.TENANT_ID = id;
	}
	public String getTENANT_ID() {
		return TENANT_ID;
	}
	public void setCHANNEL_ID(String id) {
		this.CHANNEL_ID = id;
	}
	public String getCHANNEL_ID() {
		return CHANNEL_ID;
	}
	public void setSTATUS(String s) {
		this.STATUS = s;
	}
	public String getSTATUS() {
		return STATUS;
	}
	
}
