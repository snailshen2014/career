/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WXProductInfo.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.wxProductInfo.po
 * @Description: WXProductInfo
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月22日 下午1:43:29
 * @version: V1.0  
 */

package com.bonc.busi.wxProductInfo.po;

/**
 * @ClassName: WXProductInfo
 * @Description: WXProductInfo
 * @author: LiJinfeng
 * @date: 2016年11月22日 下午1:43:29
 */
public class WXProductInfo {
	
	//WX_PRODUCT_INFO表对应的字段
	private Integer id;
	//产品分类 3=流量包,4=合约,5=套餐 
	private String productType;
	private Integer price;
	//SAP名称
	private String SAPName;
	//SAP编码
	private String SAPId;
	//01:4G假日流量包,02:4G日流量包,03:月流量包
	private Integer flowType;
	//01:bss(2,3G),02:cbss(4G)
	private String netType;
	//00:基本产品，01:附加产品
	private String extraProductType;
	//0:不能订购   1:能订购
	private Integer order_flag;
	//产品描述
	private String productDesc;
	private String tenantId;
	
	//扩展字段
	private String productId;
	private String elementId;
	private String elementName;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getFlowType() {
		return flowType;
	}
	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}	
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getExtraProductType() {
		return extraProductType;
	}
	public void setExtraProductType(String extraProductType) {
		this.extraProductType = extraProductType;
	}
	public Integer getOrder_flag() {
		return order_flag;
	}
	public void setOrder_flag(Integer order_flag) {
		this.order_flag = order_flag;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	public String getSAPName() {
		return SAPName;
	}
	public void setSAPName(String sAPName) {
		SAPName = sAPName;
	}
	public String getSAPId() {
		return SAPId;
	}
	public void setSAPId(String sAPId) {
		SAPId = sAPId;
	}
	public String getNetType() {
		return netType;
	}
	public void setNetType(String netType) {
		this.netType = netType;
	}
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	@Override
	public String toString() {
		return "WXProductInfo [id=" + id + ", productType=" + productType + ", price=" + price + ", SAPName=" + SAPName
				+ ", SAPId=" + SAPId + ", flowType=" + flowType + ", netType=" + netType + ", extraProductType="
				+ extraProductType + ", order_flag=" + order_flag + ", productDesc=" + productDesc + ", tenantId="
				+ tenantId + ", productId=" + productId + ", elementId=" + elementId + ", elementName=" + elementName
				+ "]";
	}
	
	
	
	

}
