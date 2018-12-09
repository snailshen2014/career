package com.bonc.busi.orderschedule.bo;

import java.util.Date;

public class PltActvityRemainInfo {
	String CHANNEL_ID;
	String ACTIVITY_ID;
	Integer ACTIVITY_SEQ_ID;
	String TENANT_ID;
	Integer SAVE_NUMBER;
	Date SAVE_TIME;
	
	public String getCHANNEL_ID() {
		return CHANNEL_ID;
	}
	public void setCHANNEL_ID(String id) {
		this.CHANNEL_ID = id;
	}
	public String getACTIVITY_ID() {
		return ACTIVITY_ID;
	}
	public void setACTIVITY_ID(String id) {
		this.ACTIVITY_ID = id;
	}
	public Integer ACTIVITY_SEQ_ID() {
		return ACTIVITY_SEQ_ID;
	}
	public void setACTIVITY_SEQ_ID(Integer id) {
		this.ACTIVITY_SEQ_ID = id;
	}
	public String getTENANT_ID() {
		return TENANT_ID;
	}
	public void setTENANT_ID(String id) {
		this.TENANT_ID = id;
	}
	public Integer getSAVE_NUMBER() {
		return SAVE_NUMBER;
	}
	public void setSAVE_NUMBER(Integer num) {
		this.SAVE_NUMBER = num;
	}
	public Date getSAVE_TIME() {
        return SAVE_TIME;
    }

    public void setSAVE_TIMEE(Date dd) {
        this.SAVE_TIME = dd;
    }
}
