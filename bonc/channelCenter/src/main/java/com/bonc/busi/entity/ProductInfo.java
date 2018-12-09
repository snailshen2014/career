package com.bonc.busi.entity;

import java.util.Date;

/**
 * 
 * <p>Title: JEEAC - ProductInfoPo </p>
 * 
 * <p>Description: 产品(po) </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */
public class ProductInfo {
	/**
	 *  id
	 */
	private String productId;
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 产品编码
	 */
	private String productCode;
	/**
	 * 产品描述
	 */
	private String productDesc;
	/**
	 * 产品分类id
	 */
	private String productClassId;
	/**
	 * 注册时间
	 */
	private Date registrationTime;
	/**
	 * 启用时间
	 */
	private String useDate;
	/**
	 * 失效时间
	 */
	private String invalidDate;
	/**
	 * 注册人
	 */
	private String registrationPerson;
	/**
	 * 是否有效
	 */
	private String isValid;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 省分id
	 */
	private String provId;
	
	private String classId;
	
	private String isValidDesc;
	/**
	 * 账期
	 */
	private String accPer;
	
	private String partId;
	
	public String getPartId() {
		return partId;
	}

	public void setPartId(String partId) {
		this.partId = partId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getProductClassId() {
		return productClassId;
	}

	public void setProductClassId(String productClassId) {
		this.productClassId = productClassId;
	}

	public Date getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getRegistrationPerson() {
		return registrationPerson;
	}

	public void setRegistrationPerson(String registrationPerson) {
		this.registrationPerson = registrationPerson;
	}

	public String getIsValid() {
		return isValid;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getUseDate() {
		return useDate;
	}

	public void setUseDate(String useDate) {
		this.useDate = useDate;
	}

	public String getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(String invalidDate) {
		this.invalidDate = invalidDate;
	}
	public String getAccPer() {
		return accPer;
	}
	public void setAccPer(String accPer) {
		this.accPer = accPer;
	}
	
}
