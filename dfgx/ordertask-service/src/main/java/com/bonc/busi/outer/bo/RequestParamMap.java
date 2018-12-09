package com.bonc.busi.outer.bo;

import java.io.Serializable;

/**
 * 封装请求的参数
 * @author Administrator
 *
 */
public class RequestParamMap implements Serializable{


	private static final long serialVersionUID = 2479665854979443004L;
	
	//活动Id
    private String activityId;
    
    private String activitySeqId;
	
	//租户Id
	private String tenantId;
	
	//电话号码
	private String phoneNumber;
	
	//用户ID
	private String userId;
	
	//渠道Id
	private String channelId;

	//工单表名
	private String tableName;
	
	//PLT_ORDER_TABLES_ASSIGN_RECORD_INFO BUSI_TYPE值(0/1/2/3)
	private int busiType;
	
	//查询条件(处理某些字段可传可不传的情况)
    private String whereSql;
    
    //由于多租户问题,通过myCatSql解决找不到数据表的问题
    private String myCatSql;
    
    private int pageSize;
    
    
    private int pageNum;
    
    //租户对应的SCHAMA名
    private String tableSchema;
    
    //字段描述
    private String comment;
    
    //有效的用户标签表名
    private String validTableName;
	
	
	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getBusiType() {
		return busiType;
	}

	public void setBusiType(int busiType) {
		this.busiType = busiType;
	}

	public String getWhereSql() {
		return whereSql;
	}

	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getActivitySeqId() {
		return activitySeqId;
	}

	public void setActivitySeqId(String activitySeqId) {
		this.activitySeqId = activitySeqId;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getMyCatSql() {
		return myCatSql;
	}

	public void setMyCatSql(String myCatSql) {
		this.myCatSql = myCatSql;
	}

	public String getTableSchema() {
		return tableSchema;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getValidTableName() {
		return validTableName;
	}

	public void setValidTableName(String validTableName) {
		this.validTableName = validTableName;
	}

	
    
}
