package com.bonc.busi.outer.bo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 字段映射的请求封装类
 * @author Administrator
 *
 */
public class FiledMapRequest implements Serializable {

	private static final long serialVersionUID = -3741987299667698970L;
	
	//租户Id
	private String tenantId;
	
	//活动Id
	private String activityId;
	
	//批次Id
	private int    activitySeqId;
	
	//渠道Id
	private String  channelId;

	//请求字段映射的类型：   0(策略细分字段与工单的映射)  1(用户标签与工单字段的映射)
	private String type;
	
	//请求映射的字段名称的集合
	private String[] fields;
	
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

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "FiledMapRequest [tenantId=" + tenantId + ", activityId=" + activityId + ", activitySeqId="
				+ activitySeqId + ", channelId=" + channelId + ", type=" + type + ", fields=" + Arrays.toString(fields)
				+ "]";
	}
}
