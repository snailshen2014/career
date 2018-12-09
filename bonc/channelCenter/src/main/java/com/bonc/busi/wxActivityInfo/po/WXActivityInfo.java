/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WXActivityInfo.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.wxActivityInfo.po
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月14日 上午10:43:50
 * @version: V1.0  
 */

package com.bonc.busi.wxActivityInfo.po;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.bonc.busi.wxProductInfo.po.WXProductInfo;

/**
 * @ClassName: WXActivityInfo
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年12月14日 上午10:43:50
 */
public class WXActivityInfo {
	
	private Integer recId;
	private String activityId;
	private String activityName;
	private Date startTime;
	private Date endTime;
	private String webChatInfo;
	private String isMutex;
	private String tenantId;
	
	//产品相关系列ID
	private String productId1;
	private String elementId1;
	private String orderProductId1;
	private String productId2;
	private String elementId2;
	private String orderProductId2;
	private String productId3;
	private String elementId3;
	private String orderProductId3;
	
	//扩展字段
	//ActivityId对应的ProductIdList
	private List<String> productIdList;
	//公众号ID
	private String publicId;
	//用户是否关注公众号字段名
	private String publicCode;
	//微信模板ID，从微信公众号JSON中获取
	private String templateId;
	//有无产品标识：0没有，1有
	private Integer productFlag;
  	//远程产品信息
  	private List<WXProductInfo> productInfoList = new ArrayList<WXProductInfo>();
  	//模板对应变量名、字段名列表
  	private List<HashMap<String,Object>> fieldList = new ArrayList<HashMap<String,Object>>();
    //模板变量名、字段名映射
  	private HashMap<String,String> fieldMap = new HashMap<String, String>();
  	
	public Integer getRecId() {
		return recId;
	}
	public void setRecId(Integer recId) {
		this.recId = recId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public String getWebChatInfo() {
		return webChatInfo;
	}
	public void setWebChatInfo(String webChatInfo) {
		this.webChatInfo = webChatInfo;
	}
	public String getIsMutex() {
		return isMutex;
	}
	public void setIsMutex(String isMutex) {
		this.isMutex = isMutex;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public List<String> getProductIdList() {
		return productIdList;
	}
	public void setProductIdList(List<String> productIdList) {
		this.productIdList = productIdList;
	}
	public String getPublicId() {
		return publicId;
	}
	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public Integer getProductFlag() {
		return productFlag;
	}
	public void setProductFlag(Integer productFlag) {
		this.productFlag = productFlag;
	}
	public List<WXProductInfo> getProductInfoList() {
		return productInfoList;
	}
	public void setProductInfoList(List<WXProductInfo> productInfoList) {
		this.productInfoList = productInfoList;
	}
	public String getPublicCode() {
		return publicCode;
	}
	public void setPublicCode(String publicCode) {
		this.publicCode = publicCode;
	}
	public String getProductId1() {
		return productId1;
	}

	public void setProductId1(String productId1) {
		this.productId1 = productId1;
	}

	public String getElementId1() {
		return elementId1;
	}

	public void setElementId1(String elementId1) {
		this.elementId1 = elementId1;
	}

	public String getOrderProductId1() {
		return orderProductId1;
	}

	public void setOrderProductId1(String orderProductId1) {
		this.orderProductId1 = orderProductId1;
	}

	public String getProductId2() {
		return productId2;
	}

	public void setProductId2(String productId2) {
		this.productId2 = productId2;
	}

	public String getElementId2() {
		return elementId2;
	}

	public void setElementId2(String elementId2) {
		this.elementId2 = elementId2;
	}
	
	public String getOrderProductId2() {
		return orderProductId2;
	}

	public void setOrderProductId2(String orderProductId2) {
		this.orderProductId2 = orderProductId2;
	}

	public String getProductId3() {
		return productId3;
	}

	public void setProductId3(String productId3) {
		this.productId3 = productId3;
	}

	public String getElementId3() {
		return elementId3;
	}

	public void setElementId3(String elementId3) {
		this.elementId3 = elementId3;
	}

	public String getOrderProductId3() {
		return orderProductId3;
	}

	public void setOrderProductId3(String orderProductId3) {
		this.orderProductId3 = orderProductId3;
	}

	public List<HashMap<String, Object>> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<HashMap<String, Object>> fieldList) {
		this.fieldList = fieldList;
	}
	
	public HashMap<String, String> getFieldMap() {
		return fieldMap;
	}
	public void setFieldMap(HashMap<String, String> fieldMap) {
		this.fieldMap = fieldMap;
	}	
	
	
	@Override
	public String toString() {
		return "WXActivityInfo [recId=" + recId + ", activityId=" + activityId + ", activityName=" + activityName
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", webChatInfo=" + webChatInfo + ", isMutex="
				+ isMutex + ", tenantId=" + tenantId + ", productId1=" + productId1 + ", elementId1=" + elementId1
				+ ", orderProductId1=" + orderProductId1 + ", productId2=" + productId2 + ", elementId2=" + elementId2
				+ ", orderProductId2=" + orderProductId2 + ", productId3=" + productId3 + ", elementId3=" + elementId3
				+ ", orderProductId3=" + orderProductId3 + ", productIdList=" + productIdList + ", publicId=" + publicId
				+ ", publicCode=" + publicCode + ", templateId=" + templateId + ", productFlag=" + productFlag
				+ ", productInfoList=" + productInfoList + ", fieldList=" + fieldList + ", fieldMap=" + fieldMap + "]";
	}
	
	/**
	 * @Title: setProductId
	 * @Description: 判断具体选择哪个setProductId方法
	 * @return: void
	 * @param productId
	 * @param index
	 * @throws: 
	 */
	public void setProductId(String productId,int index){
		switch (index) {
		case 1:
			setProductId1(productId);
			break;
		case 2:
			setProductId2(productId);
			break;
		case 3:
			setProductId3(productId);
			break;
		default:
			break;
		}
	}
	
    /**
     * @Title: setElementId
     * @Description: 判断具体选择哪个setElementId方法
     * @return: void
     * @param elementId
     * @param index
     * @throws: 
     */
    public void setElementId(String elementId,int index){
    	switch (index) {
		case 1:
			setElementId1(elementId);
			break;
		case 2:
			setElementId2(elementId);
			break;
		case 3:
			setElementId3(elementId);
			break;
		default:
			break;
		}
	}
    
    /**
     * @Title: setOrderProductId
     * @Description: 判断具体选择哪个setOrderProductId方法
     * @return: void
     * @param orderProductId
     * @param index
     * @throws SecurityException 
     * @throws  
     * @throws: 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void setOrderProductId(List<WXProductInfo> productInfoList) throws Exception{
    	
    	Class clazz = this.getClass();
		String methodName = null;
		Method method = null;
		Object[] arguments = null;
		int i = 1;
		for (WXProductInfo productInfo : productInfoList) {
			//设置productId
			methodName = "setProductId" + String.valueOf(i);
			method = clazz.getMethod(methodName, new Class[] { String.class });
			arguments = new Object[] { productInfo.getProductId() };
			method.invoke(this, arguments);
			//设置elementId
			methodName = "setElementId" + String.valueOf(i);
			method = clazz.getMethod(methodName, new Class[] { String.class });
			arguments = new Object[] { productInfo.getElementId() };
			method.invoke(this, arguments);
			//设置orderProductId
			methodName = "setOrderProductId" + String.valueOf(i);
			method = clazz.getMethod(methodName, new Class[] { String.class });
			arguments = new Object[] { productInfo.getSAPId() };
			method.invoke(this, arguments);
			i++;
		} 
    	
	}
	
	

}
