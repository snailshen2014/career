package com.bonc.busi.send.model.sms;

public class SmsFileResp {

	private String TENANT_ID;
	private String ACTIVITY_ID;
	private String TEL_PHONE;
	private String CONTENT;

	public String getTENANT_ID() {
		return TENANT_ID;
	}

	public void setTENANT_ID(String tENANT_ID) {
		TENANT_ID = tENANT_ID;
	}

	public String getACTIVITY_ID() {
		return ACTIVITY_ID;
	}

	public void setACTIVITY_ID(String aCTIVITY_ID) {
		ACTIVITY_ID = aCTIVITY_ID;
	}

	public String getTEL_PHONE() {
		return TEL_PHONE;
	}

	public void setTEL_PHONE(String tEL_PHONE) {
		TEL_PHONE = tEL_PHONE;
	}

	public String getCONTENT() {
		return CONTENT;
	}

	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}

}
