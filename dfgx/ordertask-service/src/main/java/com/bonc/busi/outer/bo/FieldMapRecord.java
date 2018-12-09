package com.bonc.busi.outer.bo;

import java.io.Serializable;

/**
 * 字段之间映射对应的实体类
 * @author Administrator
 *
 */
public class FieldMapRecord implements Serializable {

	private static final long serialVersionUID = -2056737680175006817L;

	private int id;
	//租户Id
	private String tenantId;  
	
	//活动Id
	private String activityId;
	
	//批次Id
	private int activitySeqId;
	
	//渠道Id
	private String channelId;
	
	//与工单表字段做映射的字段的名称
	private String strategyFieldName;
	
	//工单表对应的字段的名称
	private String orderFieldName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public int getActivitySeqId() {
		return activitySeqId;
	}
	public void setActivitySeqId(int activitySeqId) {
		this.activitySeqId = activitySeqId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getStrategyFieldName() {
		return strategyFieldName;
	}
	public void setStrategyFieldName(String strategyFieldName) {
		this.strategyFieldName = strategyFieldName;
	}
	public String getOrderFieldName() {
		return orderFieldName;
	}
	public void setOrderFieldName(String orderFieldName) {
		this.orderFieldName = orderFieldName;
	}
	
}
