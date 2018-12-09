/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WX_ORDER_INFO.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.domain
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月19日 下午4:16:29
 * @version: V1.0  
 */

package com.bonc.busi.wxOrderInfo.po;

import java.util.Date;

import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;

/**
 * @ClassName: WXOrderInfo
 * @Description: WXOrderInfo
 * @author: LiJinfeng
 * @date: 2016年11月21日 下午3:01:38
 */
public class WXOrderInfo {
	
	//对应WX_ORDER_INFO表中字段
	private Integer orderId;
	//用户手机号
	private String telInt;
	//工单生效时间、失效时间
	private Date startTime;
	private Date endTime;
	//用户表的索引用户ID
    private String userId;  
    //网别，需转化（判断预付费、已付费）
  	private Integer netType;
    
	
	
	//扩展字段
  	//微信话术
  	private String wxhsh;
	//公众号对应用户唯一标识
	private String openId;
	//用户对应的移动套餐ID
	private String productId;
    //活动信息
	private WXActivityInfo wxActivityInfo;
	//变量字段JSON串
	private String fieldInfo;
   

	public String getWxhsh() {
		return wxhsh;
	}
	public void setWxhsh(String wxhsh) {
		this.wxhsh = wxhsh;
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

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getTelInt() {
		return telInt;
	}

	public void setTelInt(String telInt) {
		this.telInt = telInt;
	}

	public Integer getNetType() {
		return netType;
	}

	public void setNetType(Integer netType) {
		this.netType = netType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public WXActivityInfo getWxActivityInfo() {
		return wxActivityInfo;
	}

	public void setWxActivityInfo(WXActivityInfo wxActivityInfo) {
		this.wxActivityInfo = wxActivityInfo;
	}

	public String getFieldInfo() {
		return fieldInfo;
	}

	public void setFieldInfo(String fieldInfo) {
		this.fieldInfo = fieldInfo;
	}
	
	@Override
	public String toString() {
		return "WXOrderInfo [orderId=" + orderId + ", telInt=" + telInt + ", startTime=" + startTime + ", endTime="
				+ endTime + ", userId=" + userId + ", netType=" + netType + ", wxhsh=" + wxhsh + ", openId=" + openId
				+ ", productId=" + productId + ", wxActivityInfo=" + wxActivityInfo + ", fieldInfo=" + fieldInfo + "]";
	}
    
	
	
	
  
}
