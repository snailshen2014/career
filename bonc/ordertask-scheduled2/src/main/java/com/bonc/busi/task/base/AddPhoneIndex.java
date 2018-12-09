package com.bonc.busi.task.base;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.service.dao.PltActivityExecuteLogDao;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.entity.PltActivityExecuteLog;
import com.bonc.busi.service.mapper.CommonMapper;
import com.bonc.common.thread.ThreadBaseFunction;

@Service("AddPhoneIndex")
public class AddPhoneIndex extends ThreadBaseFunction {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private CommonMapper CommonMapperIns;

	private final static Logger log = LoggerFactory.getLogger(AddPhoneIndex.class);

	@SuppressWarnings("unchecked")
	@Override
	public int handleData(Object data) {
		
		String TenantId = "";
		int ActivitySeqId = 0;
		String ChannelId = "";
		String OrderTableName = "";
		String ActivityId ="";
		long start = System.currentTimeMillis();
		try {
			int limitNum = Integer.parseInt(SyscommoncfgDao.query("TASK.EXECUTE.INTOTABLE")); // 每次操作数据的数量
			Map<String, Object> mapInfo = (Map<String, Object>) data;
			TenantId = (String) mapInfo.get("tenantId");
			ActivitySeqId = (int) mapInfo.get("activitySeqId");
			ChannelId = (String) mapInfo.get("channelId");
			OrderTableName = (String) mapInfo.get("orderTableName");
			ActivityId = (String)mapInfo.get("activityId");
			log.info("------ 开始执行{}渠道加手机号索引  ------", ChannelId);
			
			// --- 向PLT_ACTIVITY_EXECUTE_LOG表中插入一条记录 ---
			CommonMapperIns.updateBeginStatus(mapInfo);
			
			// --- 查询需要执行的手机号的数量和最大最小的recid便于计算循环次数 ---
			Map<String, Object> orderInfoList = CommonMapperIns.getPhoneCount(mapInfo);//需要执行的总数量和最大最小的recId
			long  minRec = 0;
			long  maxRec = 0;
			long phoneNumCount = 0;
			try{
				minRec = Long.parseLong(orderInfoList.get("MINREC").toString());
				maxRec = Long.parseLong(orderInfoList.get("MAXREC").toString());
				phoneNumCount = (long)orderInfoList.get("TOTAL");
				
			}catch(Exception e){  // --- 捕获空指针 ---
				System.out.println(e.getMessage());
				return -1;
			}
			log.info("minRec:" + minRec + "    maxRec:" + maxRec + "    total:" + phoneNumCount);
			if (phoneNumCount == 0) {
				log.info("该渠道下没有可操作的手机号:" + ChannelId);
				return 0;
			}
			long beginRec = 0;
			long endRec = 0;
			long round = (maxRec - minRec) / limitNum;   //计算需要循环执行多少次
			for (int i = 0; i <= round; ++i) {
				beginRec = minRec + limitNum * i;
				if (i == round) {
					endRec = maxRec;
				} else {
					endRec = beginRec + limitNum - 1;
				}
				log.info("  正在向索引表插入数据..." );
		        // --- 循环0-9手机尾号 拼接手机号索引表 插入数据 ---
				for (int phoneLastNumber = 0; phoneLastNumber < 10; phoneLastNumber++) {
					String tableName = "PLT_ORDER_PHONE_INDEX_" + phoneLastNumber;
					String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '"+ TenantId + "'*/"
							+ "INSERT INTO "+ tableName	+ "(CHANNEL_ID,ACTIVITY_SEQ_ID,PHONE_NUMBER,TENANT_ID,ORDER_TABLE_NAME)"
							+ " SELECT CHANNEL_ID,ACTIVITY_SEQ_ID,PHONE_NUMBER,TENANT_ID,'"+ OrderTableName + "' FROM "+ OrderTableName
							+ " WHERE ACTIVITY_SEQ_ID = " + ActivitySeqId
							+ " AND CHANNEL_ID = '" + ChannelId + "'"
							+ " AND TENANT_ID = '" + TenantId + "'"
							+ " AND REC_ID >= " + beginRec
							+ " AND REC_ID <= " + endRec
							+ " AND PHONE_NUMBER LIKE '%"+ phoneLastNumber + "'";
					jdbcTemplate.update(sql);
				}		
			}
			// --- 将手机尾号是字母的手机号插入到0表 ---
			String SQL = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '"+ TenantId + "'*/ "
					+ "INSERT INTO PLT_ORDER_PHONE_INDEX_0 (CHANNEL_ID,ACTIVITY_SEQ_ID,PHONE_NUMBER,TENANT_ID,ORDER_TABLE_NAME)"
					+ " SELECT CHANNEL_ID,ACTIVITY_SEQ_ID,PHONE_NUMBER,TENANT_ID,'"+ OrderTableName + "' FROM "+ OrderTableName
					+ " WHERE ACTIVITY_SEQ_ID = " + ActivitySeqId
					+ " AND CHANNEL_ID = " + ChannelId
					+ " AND TENANT_ID = '" + TenantId + "'"
					+ " AND PHONE_NUMBER REGEXP '[a-zA-Z]$' ";
			jdbcTemplate.update(SQL);
			
			// --- 更新状态PROCESS_STATUS=1 ---
//			CommonMapperIns.updateEndStatus(ActivitySeqId, TenantId, ChannelId);
//			long end = System.currentTimeMillis();
//			log.info("------ 渠道加手机号索引任务结束,time-consuming:" + (end-start)/1000.0 + "s ------");

		} catch (Exception e) {
			e.printStackTrace();
			log.info("渠道加手机号索引任务执行报错！！！");
		} finally {	
			// --- 更新状态PROCESS_STATUS=1 ---
			CommonMapperIns.updateEndStatus(ActivitySeqId, TenantId, ChannelId);
			long end = System.currentTimeMillis();
			log.info("------ 渠道加手机号索引任务结束,time-consuming:" + (end-start)/1000.0 + "s ------");
		}
		return 0;
	}
}
