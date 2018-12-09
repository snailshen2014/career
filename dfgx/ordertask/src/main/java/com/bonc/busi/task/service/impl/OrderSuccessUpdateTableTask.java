package com.bonc.busi.task.service.impl;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class OrderSuccessUpdateTableTask implements Runnable {
	
	private final static Logger log = LoggerFactory.getLogger(OrderSuccessUpdateTableTask.class);
	
	CountDownLatch countDownLatch;
	
	String tablName;
	
	String userIdStr;
	
	String tenantId;
	
	int activitySeqId;
	
	JdbcTemplate JdbcTemplateIns;
	
	String fileName;

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}
	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

	public String getTablName() {
		return tablName;
	}

	public void setTablName(String tablName) {
		this.tablName = tablName;
	}
    
	public String getUserIdStr() {
		return userIdStr;
	}
	public void setUserIdStr(String userIdStr) {
		this.userIdStr = userIdStr;
	}
	
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public void run() {
      try{
    	  StringBuilder sbb = new StringBuilder();
			sbb.append("UPDATE "+ tablName);
			sbb.append(" SET  ORDER_STATUS = '6' ");
			sbb.append(" WHERE TENANT_ID='");
			sbb.append(tenantId);
			sbb.append("'");
			sbb.append("  AND ACTIVITY_SEQ_ID =");
			sbb.append(activitySeqId);
			sbb.append("  AND USER_ID IN (");
			sbb.append(userIdStr);
			sbb.append(")");
			sbb.append(" AND ORDER_STATUS = '0' ");
			int result = JdbcTemplateIns.update(sbb.toString());
			sbb.setLength(0);
			log.info("更新 " + tablName + "表,工单过 滤成功,activityseqid={},更新数量={},拆分的小文件名:{}",activitySeqId,result,fileName);
      }catch(Exception ex){
    	  ex.printStackTrace();
      }finally{
    	  countDownLatch.countDown();
      }
	}

}
