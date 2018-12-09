package com.bonc.busi.outer.bo;

import java.io.Serializable;
import java.util.List;

/**
 * 渠道需要同步哪些用户标签的请求体
 * @author Administrator
 *
 */
public class StoreDemandLabelRequest implements Serializable {

	private static final long serialVersionUID = -2914492153154542943L;

	//租户Id
	private String tenantId;
	
	//标签集合
	private List<UserLabel>  columns;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public List<UserLabel> getColumns() {
		return columns;
	}

	public void setColumns(List<UserLabel> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "StoreDemandLabelRequest [tenantId=" + tenantId + ", columns=" + columns + "]";
	}
}
