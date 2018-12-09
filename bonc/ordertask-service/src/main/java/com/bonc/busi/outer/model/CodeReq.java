package com.bonc.busi.outer.model;

public class CodeReq {

	private String tenantId; // 租户标识
	private String fieldName;// 码表字段
	private String fieldKey;// 码表key值
	private String fieldValue;// 码表值
	private String loadDate;// 最新更新时间

	
	public CodeReq() {
		super();
	}

	public CodeReq(String tenantId, String fieldName, String fieldKey) {
		super();
		this.tenantId = tenantId;
		this.fieldName = fieldName;
		this.fieldKey = fieldKey;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldKey() {
		return fieldKey;
	}

	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}

}
