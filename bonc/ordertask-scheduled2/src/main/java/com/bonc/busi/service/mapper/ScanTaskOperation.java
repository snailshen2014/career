package com.bonc.busi.service.mapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.bo.ActivityFliteUsers;
public class ScanTaskOperation {
	
	@Autowired private JdbcTemplate jdbcTemplate;
	private static final Logger logger = Logger.getLogger(ScanTaskOperation.class);
	
	// --- 像PLT_ACTIVITY_EXECUTE_LOG表中插入一条记录
	public String updateBeginStatus(Map<String, Object> map){
		String s = "'";
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("INSERT INTO PLT_ACTIVITY_EXECUTE_LOG ");
		sBuilder.append("(CHANNEL_ID,ACTIVITY_ID,ACTIVITY_SEQ_ID,TENANT_ID,BUSI_CODE,PROCESS_STATUS,BEGIN_DATE,BUSI_ITEM) VALUES (");
		sBuilder.append( s + map.get("channelId") + s + ",");
		sBuilder.append( s + map.get("activityId") + s + ",");
		sBuilder.append( map.get("activitySeqId") + ",");
		sBuilder.append( s + map.get("tenantId") + s + ",'1016',0,");
		sBuilder.append("NOW(),");
		sBuilder.append( s + map.get("orderTableName") + s + ")");
		return sBuilder.toString();	
	}

	// --- 通过活动序列号和表名查手机号  每次取10万---
	public String getPhoneCount(Map<String, Object> map){
		String s = "'";
		StringBuilder sBuilder = new StringBuilder();
//		sBuilder.append("SELECT PHONE_NUMBER FROM ");
		sBuilder.append("SELECT COUNT(*) AS TOTAL,MAX(REC_ID) AS MAXREC,MIN(REC_ID) AS MINREC FROM ");
		sBuilder.append(map.get("orderTableName").toString());
		sBuilder.append(" WHERE TENANT_ID = " + s + map.get("tenantId") + s);
		sBuilder.append(" AND CHANNEL_ID = " + s + map.get("channelId") + s);
		sBuilder.append(" AND ACTIVITY_SEQ_ID = " + map.get("activitySeqId"));
		logger.info("getValidPhoneNum sql:" + sBuilder);
		return sBuilder.toString();
	}
			
	// --- 黑白名单数据入库 --- 
	@SuppressWarnings("unchecked")
	public String insertIntoFilteTable(HashMap<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		StringBuilder builder = new StringBuilder();
		List<ActivityFliteUsers> blackandWhiteData = (List<ActivityFliteUsers>) map.get("blackandWhiteData");
		String newPartFlag = (String) map.get("newPartFlag");
		
		for (ActivityFliteUsers activityFliteUsers : blackandWhiteData) {
			builder.append("('").append(activityFliteUsers.getUSER_ID())
				   .append("','").append(activityFliteUsers.getUSER_PHONE())
			       .append("','").append(activityFliteUsers.getFILTE_TYPE())
			       .append("','").append(activityFliteUsers.getTENANT_ID())
			       .append("',").append(newPartFlag) 
			       .append(")").append(",");
		}
		String substring = builder.substring(0, builder.length()-1);
		// --- 开始执行 ---
		sb.setLength(0);
		sb.append("INSERT INTO PLT_ACTIVITY_FILTE_USERS ");
		sb.append(" (USER_ID,USER_PHONE,FILTE_TYPE,TENANT_ID,PARTITION_FLAG) VALUES ");
		sb.append(substring);
		sb.append(" ");
		return sb.toString();

	}	
	
	// --- 获取失效工单 ---
	public String getInvalidActivitySeqId(String tenantId) {
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ORDER_END_DATE < SYSDATE()");
		whereBuilder.append(" AND ACTIVITY_STATUS IN (1,8,9)");
		whereBuilder.append(" AND TENANT_ID= " + s + tenantId + s);
		whereBuilder.append(" OR DATE_FORMAT(END_DATE,'%Y-%m-%d') = DATE_FORMAT(SYSDATE(),'%Y-%m-%d')");
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT REC_ID,ACTIVITY_ID FROM PLT_ACTIVITY_INFO WHERE ");
        sb.append(whereBuilder);
		return sb.toString();
	}	
	
	
	
}

