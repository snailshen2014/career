package com.bonc.busi.outer.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import com.bonc.busi.outer.mapper.SmsOrderMapper;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;

/**
 * 把历史短信工单从按月的表中移入到按月按手机尾号编排的历史表中
 * @author Administrator
 *
 */
public class SmsOrderMoveToHisTask implements Runnable {
	
	private final static Logger 		log= LoggerFactory.getLogger(SmsOrderMoveToHisTask.class);
	
	SmsOrderMapper mapper;
	
	private String activityId;
	
	private int activitySeqId;
	
	private String tenantId;
	
	private String channelId;
	
	//按月建立的短信历史表
	private String hisTableName;
	
	//表对表方式移工单的模板sql:SYS_COMMON_CFG里的ACTIVITYSTATUS.MOVE
	private String moveOrderTemplate;
	
	//从表中删除工单的模板sql:SYS_COMMON_CFG里的ACTIVITYSTATUS.DELETE
	private String deletOrderTemplate;
	
	private JdbcTemplate jdbcTemplate;
	
	private BusiTools BusiToolsIns;

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

	public String getHisTableName() {
		return hisTableName;
	}

	public void setHisTableName(String hisTableName) {
		this.hisTableName = hisTableName;
	}
	
	public SmsOrderMapper getMapper() {
		return mapper;
	}

	public void setMapper(SmsOrderMapper mapper) {
		this.mapper = mapper;
	}
	
	public String getMoveOrderTemplate() {
		return moveOrderTemplate;
	}

	public void setMoveOrderTemplate(String moveOrderTemplate) {
		this.moveOrderTemplate = moveOrderTemplate;
	}
	
	public String getDeletOrderTemplate() {
		return deletOrderTemplate;
	}

	public void setDeletOrderTemplate(String deletOrderTemplate) {
		this.deletOrderTemplate = deletOrderTemplate;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private BaseMapper baseMapper;
	
	public BaseMapper getBaseMapper() {
		return baseMapper;
	}

	public void setBaseMapper(BaseMapper baseMapper) {
		this.baseMapper = baseMapper;
	}

	public BusiTools getBusiToolsIns() {
		return BusiToolsIns;
	}

	public void setBusiToolsIns(BusiTools busiToolsIns) {
		BusiToolsIns = busiToolsIns;
	}

	@Override
	public void run() {
		log.info("======================开始同步短信历史工单到行云:{}--------->:XLD_ORDER_INFO_SMS_HIS", hisTableName); 
	    if(!uploadHisSmsOrderToXcloud(activitySeqId, tenantId, hisTableName)){
	    	log.info("=========================同步短信历史工单到行云出错了，终止了下面的步骤");
	    	//return;
	    	//10秒后再重试一次
	    	try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	if(!uploadHisSmsOrderToXcloud(activitySeqId, tenantId, hisTableName)){
	    		log.info("=========================同步短信历史工单到行云出错了，已重试了1次，终止了下面的步骤");
	    		return;
	    	}
	    }	 
		log.info("======================同步短信历史工单到行云结束:{}--------->:XLD_ORDER_INFO_SMS_HIS", hisTableName);
		log.info("============= 移工单操作开始");
		//updateMoveSmsOrderStatus();
		long beginTime = System.currentTimeMillis();
		int batchCount = 100000; //一次移动10万条
		Map<String,Object> countAndRecIdInfo = mapper.queryCountAndRecId(hisTableName,activitySeqId,tenantId,channelId);
	    int smsOrderCount = ((Long) countAndRecIdInfo.get("count")).intValue();
	    if(smsOrderCount ==0){  //如果没有待移动的工单
	    	updateMoveSmsOrderStatus();
	    	log.info("批次: {}, 没有短信历史工单,不需要做移动操作，直接返回",activitySeqId);
	    	return;
	    } 
	    int maxRecId = ((Long) countAndRecIdInfo.get("maxRecId")).intValue();
	    int minRecId = ((Long) countAndRecIdInfo.get("minRecId")).intValue();
	    log.info("批次："+ activitySeqId + ", 共有"+ smsOrderCount + "工单 , 最大RecId:" +maxRecId + ",最小RecId:" + minRecId);
	    String replaceTenantId = moveOrderTemplate.replaceFirst("TTTTTNENAT_ID", tenantId);
	    String replaceSeqId = replaceTenantId.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(activitySeqId));
	    String replaceTargetTable = replaceSeqId.replaceFirst("TTTTTABLENAME_HIS", "TARGET_TABLE");
	    String replaceSourceTableName = replaceTargetTable.replaceAll("TTTTTABLENAME", hisTableName);
	    
	    String deleteReplaceTenantId = deletOrderTemplate.replaceFirst("TTTTTNENAT_ID", tenantId);
	    String deleteReplaceSeqId = deleteReplaceTenantId.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(activitySeqId));
	    String deleteReplaceSouceTableName = deleteReplaceSeqId.replaceFirst("TTTTTABLENAME", hisTableName);
	    
	    int round = (maxRecId-minRecId)/batchCount;
	    int lBeginRec = 0;
	    int lEndRec = 0;
	    for(int i = 0 ; i<=round; ++i){   //批量移动，每次最多移动batchCount条
	    	lBeginRec = minRecId + batchCount*i;
	    	if(i==round){
	    		lEndRec = maxRecId;
	    	}else{
	    		lEndRec = lBeginRec + batchCount-1;
	    	}
	    	StringBuffer sbuffer = new StringBuffer();
	    	for(int phoneNumberLastDigit=0; phoneNumberLastDigit<10;phoneNumberLastDigit++){
	    	try{
	    		log.info("-----------------------------批量开始从按月分配的临时表中移动工单,lBeginRec={},lEndRec={}",lBeginRec,lEndRec);
	    		sbuffer.setLength(0);
	    		String targetHisTable = hisTableName+String.valueOf(phoneNumberLastDigit);  //根据按月拆分的表名得到按月按手机尾号拆分的表名PLT_ORDER_INFO_SMS_HIS_110
	    		String replaceTargetOrderTable = replaceSourceTableName.replaceFirst("TARGET_TABLE", targetHisTable);
	    		String appendPhoneNumberSelectCondition = replaceTargetOrderTable+"  AND PHONE_NUMBER LIKE '%LAST_NUMBER'";
	    		String moveOrderSql = appendPhoneNumberSelectCondition.replaceFirst("LAST_NUMBER", String.valueOf(phoneNumberLastDigit));
	    		sbuffer.append(moveOrderSql);
	    		sbuffer.append(" AND REC_ID >= ");
				sbuffer.append(lBeginRec);
				sbuffer.append(" AND REC_ID <= ");
				sbuffer.append(lEndRec);
                //目标sql: /*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */INSERT INTO PLT_ORDER_INFO_SMS_HIS_110 SELECT * FROM PLT_ORDER_INFO_SMS_HIS_11 WHERE ACTIVITY_SEQ_ID = 450043 
	    		//        AND CHANNEL_ID = '7' AND PHONE_NUMBER LIKE '%0' AND REC_ID >=10001 AND REC_ID <=9999
	    		log.info("moveOrderSql={}", sbuffer.toString());
	    		int moveCount = jdbcTemplate.update(sbuffer.toString());
	    		log.info("-----------------------------批量结束从按月分配的临时表中移动工单,lBeginRec={},lEndRec={},批量移动了{}",lBeginRec,lEndRec,moveCount);
	    		//更新INPUT_DATE 和 INVALID_DAT 为 now();
	    		mapper.updateTime(targetHisTable,activitySeqId,tenantId,lBeginRec,lEndRec);
	    		log.info("更新INPUT_DATE INVALID_DAT时间完成,表名:{},批次：{},lBeginRec:{},lEndRec:{}",targetHisTable,activitySeqId,lBeginRec,lEndRec);
	    		log.info("-----------------------------批量开始从按月分配的临时表中删除工单,lBeginRec={},lEndRec={}",lBeginRec,lEndRec);
	    		sbuffer.setLength(0);
	    		appendPhoneNumberSelectCondition= deleteReplaceSouceTableName + "  AND PHONE_NUMBER LIKE '%LAST_NUMBER'";
	    		String deletOrderSql = appendPhoneNumberSelectCondition.replaceFirst("LAST_NUMBER", String.valueOf(phoneNumberLastDigit));
	    		sbuffer.append(deletOrderSql);
	    		sbuffer.append(" AND REC_ID >= ");
				sbuffer.append(lBeginRec);
				sbuffer.append(" AND REC_ID <= ");
				sbuffer.append(lEndRec);
				log.info("deleteOrderSql={}", sbuffer.toString());
				int deleteCount = jdbcTemplate.update(sbuffer.toString());
	    		log.info("-----------------------------批量结束从按月分配的临时表中删除工单,lBeginRec={},lEndRec={},批量删除了{}",lBeginRec,lEndRec,deleteCount);
	    	 }catch(Exception ex){
					ex.printStackTrace();
					continue;
				}
	    	}
	    }
	    long endTime = System.currentTimeMillis();
	    float usedTime = (endTime-beginTime)/1000;
	    log.info("批次："+ activitySeqId+"移历史完成,耗时:" + usedTime + "秒");
	    updateMoveSmsOrderStatus();
	}
	
	/**
	 * 同步短信历史工单信息到行云DbLink下的XLD_ORDER_INFO_SMS_HIS表，由于速度很快，不需要分批同步
	 * @param activitySeqId  同步的短信历史工单的批次
	 * @param tenantId       租户Id
	 * @param tableName      源表
	 * @return true:成功    false:出错异常
	 */
	boolean  uploadHisSmsOrderToXcloud(int activitySeqId,String tenantId,String tableName){
		boolean success = true; //是否成功
		Connection connection = null;
		Statement statement =null;
		String driverName = baseMapper.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
		String url = baseMapper.getValueFromSysCommCfg("DS.XCLOUD.URL");
		String username =  baseMapper.getValueFromSysCommCfg("DS.XCLOUD.USER");
		String password = baseMapper.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
		String order_xcloud_suffix = baseMapper.getValueFromSysCommCfg("ORDER_XCLOUD_SUFFIX."+tenantId);
		if(StringUtils.isBlank(order_xcloud_suffix)){
			log.error("======== 缺少ORDER_XCLOUD_SUFFIX配置,请配置");
			return false;
		}
		try{	
			Class.forName(driverName);
			connection = DriverManager.getConnection(url , 
					username, 
					password);		
			long begin = System.currentTimeMillis();
			String templateSql = "insert into XLD_ORDER_INFO_SMS_HIS "
					+    "{Select REC_ID,CONTACT_DATE,PHONE_NUMBER,ACTIVITY_SEQ_ID,TENANT_ID,CHANNEL_ID,ORDER_STATUS,CONTACT_CODE "
					+    " FROM TTTTTABLENAME WHERE TENANT_ID='TTTTTNENAT_ID' AND ACTIVITY_SEQ_ID = AAAAACTIVITY_SEQ_ID}@ORDER_XCLOUD_SUFFIX";
			String replaceTenantIdSql = templateSql.replaceFirst("TTTTTNENAT_ID", tenantId);
			String replaceSourceTableName  = replaceTenantIdSql.replaceFirst("TTTTTABLENAME", tableName);
			String replaceOrderXCloudSuffix = replaceSourceTableName.replaceFirst("ORDER_XCLOUD_SUFFIX", order_xcloud_suffix);
			String sql = replaceOrderXCloudSuffix.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(activitySeqId));
			statement = connection.createStatement();
			statement.execute(sql);
			System.out.println("====================upload sucess");
			System.out.println("========== 耗时："+(System.currentTimeMillis()-begin)/1000);
			return success;
		   }catch(Exception ex){
			    ex.printStackTrace();
			    PltCommonLog PltCommonLogIns = new PltCommonLog();
			    PltCommonLogIns.setLOG_TYPE("203");
				PltCommonLogIns.setSERIAL_ID(1122);
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setSPONSOR(tableName);
				PltCommonLogIns.setBUSI_CODE("203");
				String error = ex.getMessage();
				if(error!=null && error.length()>1000){
					error = error.substring(0, 998);
				}
				PltCommonLogIns.setBUSI_DESC("203");
				PltCommonLogIns.setBUSI_ITEM_1(error);
				BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			    success = false;
		   }finally{
			   try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  }
		return success;
	}
	
	void updateMoveSmsOrderStatus(){
		//对于周期性活动，更新时需要更新最新的记录
		int maxId = mapper.queryMaxId(activityId,activitySeqId,tenantId,channelId);
		mapper.updateSmsOrderMoveRecordOrignStatus(activityId, activitySeqId, tenantId, channelId, "1",maxId);
	}

}
