package com.bonc.busi.task.service.impl;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class OrderCheckUpdateTableTask implements Runnable {
	
	private final static Logger log = LoggerFactory.getLogger(OrderCheckUpdateTableTask.class);
	
	String tableName;
	
	String TENANT_ID;
	
	int activitySeqId;
	
	JdbcTemplate JdbcTemplateIns;
	
	String curDateId;

	String userIdStr;
	
	String fileName;
	
	CountDownLatch cdl;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTENANT_ID() {
		return TENANT_ID;
	}
	public void setTENANT_ID(String tENANT_ID) {
		TENANT_ID = tENANT_ID;
	}
	
	public int getActivitySeqId() {
		return activitySeqId;
	}
	public void setActivitySeqId(int activitySeqId) {
		this.activitySeqId = activitySeqId;
	}
	
	public JdbcTemplate getJdbcTemplateIns() {
		return JdbcTemplateIns;
	}
	public void setJdbcTemplateIns(JdbcTemplate jdbcTemplateIns) {
		JdbcTemplateIns = jdbcTemplateIns;
	}
	
	public String getCurDateId() {
		return curDateId;
	}
	public void setCurDateId(String curDateId) {
		this.curDateId = curDateId;
	}
	
	public String getUserIdStr() {
		return userIdStr;
	}
	public void setUserIdStr(String userIdStr) {
		this.userIdStr = userIdStr;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public CountDownLatch getCdl() {
		return cdl;
	}
	public void setCdl(CountDownLatch cdl) {
		this.cdl = cdl;
	}
	@Override
	public void run() {
        try{
		StringBuilder sbb = new StringBuilder();
		sbb.append("UPDATE " + tableName
				+ " SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "
				+ curDateId + " ");
		sbb.append(" WHERE TENANT_ID='");
		sbb.append(TENANT_ID);
		sbb.append("'");
		sbb.append("  AND ACTIVITY_SEQ_ID =");
		sbb.append(activitySeqId);
		sbb.append("  AND USER_ID IN (");
		sbb.append(userIdStr);
		sbb.append(")");
		sbb.append(" AND ORDER_STATUS ='5' AND CHANNEL_STATUS IN('0','2')");
		int result = JdbcTemplateIns.update(sbb.toString());
		log.info("更新 " + tableName + "表,事后工单成功检查成功,activityseqid={},更新数量={},更新使用的文件名:{}",activitySeqId,result,fileName);
        }catch(Exception ex){
        	ex.printStackTrace();
        }finally{
        	cdl.countDown();
        }
	}

}
