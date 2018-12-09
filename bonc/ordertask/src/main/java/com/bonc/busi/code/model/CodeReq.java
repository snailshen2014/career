package com.bonc.busi.code.model;

public class CodeReq {

	private String tenantId; //租户标识
	private String fieldName;//码表字段
	private String fieldKey;// 码表key值
	private String fieldValue;//码表值
	
	private String fieldType;//字段类型 用户标签表码表 为 USER
	private String type; 	// 码表类型
	private String table;
	
	public CodeReq() {
		super();
	}

	public CodeReq(String tenantId, String fieldName) {
		super();
		this.tenantId = tenantId;
		this.fieldName = fieldName;
	}

	public CodeReq(String tenantId, String fieldName, String fieldKey) {
		super();
		this.tenantId = tenantId;
		this.fieldName = fieldName;
		this.fieldKey = fieldKey;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
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

}
