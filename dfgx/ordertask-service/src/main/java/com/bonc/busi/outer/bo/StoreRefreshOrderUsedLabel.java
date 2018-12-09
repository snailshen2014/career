package com.bonc.busi.outer.bo;

import java.io.Serializable;
import java.util.List;

/**
 * 刷新工单数据使用的用户标签的请求bean
 * @author Administrator
 *
 */
public class StoreRefreshOrderUsedLabel implements Serializable{

	private static final long serialVersionUID = -7065409276602970365L;

	//租户Id
	private String tenantId;
	
	//渠道Id
	private String channelId;
	
	//用户标签集合
	private List<String> columns;
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
}
