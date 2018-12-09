package com.bonc.busi.activity;

public class DataSendingConfigPo {
	/**
	 * 字段ID
	 */
	private String id;
	/**
	 * 字段名称
	 */
	private String fieldName;
	
	/**
	 * 字段类型
	 */
	private String fieldType;
	
	/**
	 * 字段长度
	 */
	private String fieldLength;
	
	/**
	 * 字段描述
	 */
	private String fieldDesc;
	/**
	 * 字段别名
	 */
	private String fieldAlias;
	
	/**
	 * 是否必选字段（1是 0否）
	 */
	private String isMust;
	
	/**
	 * 是否默认下发字段（1是 0否）
	 */
	private String isDefault;
	
	/**
	 * 是否可扩展字段（1是 0否）
	 */
	private String isExtend;
	/**
	 * 排序序号
	 */
	private String fieldOrd;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}

	public String getFieldDesc() {
		return fieldDesc;
	}

	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	public void setFieldAlias(String fieldAlias) {
		this.fieldAlias = fieldAlias;
	}

	public String getIsMust() {
		return isMust;
	}

	public void setIsMust(String isMust) {
		this.isMust = isMust;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getIsExtend() {
		return isExtend;
	}

	public void setIsExtend(String isExtend) {
		this.isExtend = isExtend;
	}

	public String getFieldOrd() {
		return fieldOrd;
	}

	public void setFieldOrd(String fieldOrd) {
		this.fieldOrd = fieldOrd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
