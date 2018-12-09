package com.bonc.busi.outer.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

import java.util.HashMap;

public class StatusProvider {

	public String getActivityCount(HashMap<String, Object> req){
		BEGIN();
		SELECT("COUNT(1) ");
		FROM("PLT_ACTIVITY_INFO a,PLT_ACTIVITY_PROCESS_LOG c ");
		WHERE("a.TENANT_ID=#{tenantId} ");
		WHERE("a.ACTIVITY_ID=#{activityId} ");
		WHERE("a.TENANT_ID=c.TENANT_ID ");
		WHERE("a.REC_ID=c.ACTIVITY_SEQ_ID ");
		WHERE("c.STATUS='0'");
		return SQL();
	}
	
	public String getActivityList(HashMap<String, Object> req){
		BEGIN();
		SELECT("a.ACTIVITY_ID, a.ACTIVITY_NAME,c.CHANNEL_ID,c.CHANNEL_ORDER_NUM,c.CHANNEL_FINISH_NUM,"
				+ " IFNULL(c.BEGIN_DATE,'--') BEGIN_DATE,IFNULL(c.END_DATE,'--') END_DATE");
		FROM("PLT_ACTIVITY_INFO a,PLT_ACTIVITY_PROCESS_LOG c ");
		WHERE("a.TENANT_ID=#{tenantId} ");
		WHERE("a.ACTIVITY_ID=#{activityId} ");
		WHERE("a.TENANT_ID=c.TENANT_ID ");
		WHERE("a.REC_ID=c.ACTIVITY_SEQ_ID ");
		WHERE("c.STATUS='0'");
		ORDER_BY("c.BEGIN_DATE DESC");
		ORDER_BY("c.CHANNEL_ID");
		String Limit = " LIMIT " + req.get("startNum")+","+req.get("pageSize");
		return SQL() + Limit;
	}
}
