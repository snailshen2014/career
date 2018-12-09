package com.bonc.busi.outer.bo;

import java.io.Serializable;

/**
 * 工单表使用记录信息
 * @author Administrator
 *
 */
public class OrderTableUsingInfo implements Serializable{

	private static final long serialVersionUID = -5353712695393496659L;
 
	//租户ID
	private String tenantId;
	
	//表名
	private String tableName;
	
	//删除的数量
	private int delCount;
	
	//业务类型
	private int busiType;
	
	//使用状态
	private int usingStatus;
	
	//当前使用量
	private int cucrrentCount;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getDelCount() {
		return delCount;
	}

	public void setDelCount(int delCount) {
		this.delCount = delCount;
	}

	public int getBusiType() {
		return busiType;
	}

	public void setBusiType(int busiType) {
		this.busiType = busiType;
	}

	public int getUsingStatus() {
		return usingStatus;
	}

	public void setUsingStatus(int usingStatus) {
		this.usingStatus = usingStatus;
	}

	public int getCucrrentCount() {
		return cucrrentCount;
	}

	public void setCucrrentCount(int cucrrentCount) {
		this.cucrrentCount = cucrrentCount;
	}
	
}
