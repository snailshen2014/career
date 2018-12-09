package com.bonc.busi.service.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.metrics.stats.Total;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.mapper.CommonMapper;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.utils.HttpUtil;

@Service("CleanOrder")

public class CleanOrder {
	private final static Logger log= LoggerFactory.getLogger(BusiTools.class);
	@Autowired private BusiTools BusiToolsIns;
	@Autowired private JdbcTemplate jdbcTemplate;
	@Autowired private CommonMapper CommonMapperIns;
	
	public void handleActivityOrder(String tenantId,int activitySeqId,String orderName,String channelId,String activityId){
		
		StringBuilder sb = new StringBuilder();
		String strTenantId = tenantId;
		int	iActivitySeqId = activitySeqId;
		String iActivityId = activityId;
					
			String strMove = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.MOVE");
			String strDelete = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.DELETE");
			String strUpdate = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.UPDATE");
			
			String strTmpMove = strMove.replaceFirst("TTTTTENANT_ID", strTenantId);
			String strTmpDelete = strDelete.replaceFirst("TTTTTENANT_ID", strTenantId);
			String strTmpUpdate = strUpdate.replaceFirst("TTTTTENANT_ID", strTenantId);
			
			String strLastMove = strTmpMove.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(iActivitySeqId));
			String strLastDelete = strTmpDelete.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));
			String strLastUpdate = strTmpUpdate.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));
			
			String strFinalMove = strLastMove.replaceFirst("CCCCCHANNEL_ID",channelId);
			String strFinalDelete = strLastDelete.replaceFirst("CCCCCHANNEL_ID",channelId);
			String strFinalUpdate = strLastUpdate.replaceFirst("CCCCCHANNEL_ID",channelId);
			
			String strLocalUpdate = strFinalUpdate.replaceAll("TTTTTABLENAME", orderName);
			String strLocalMove = strFinalMove.replaceAll("TTTTTABLENAME", orderName);
			String strLocalDelete = strFinalDelete.replaceAll("TTTTTABLENAME", orderName);
			log.info("strLocalUpdate:"+strLocalUpdate+"strLocalMove:"+strLocalMove+"strLocalDelete"+strLocalDelete);

			sb.setLength(0);
			sb.append("SELECT COUNT(*) AS TOTAL, MAX(REC_ID) AS MAXID,MIN(REC_ID) AS MINID  FROM ");
			sb.append(orderName);
			sb.append(" WHERE ACTIVITY_SEQ_ID = ");
			sb.append(iActivitySeqId);
			sb.append("  AND TENANT_ID ='");
			sb.append(strTenantId);
			sb.append("'");
			sb.append("  AND CHANNEL_ID ='");
			sb.append(channelId);
			sb.append("'");
			
			Map<String,Object>  mapResult = jdbcTemplate.queryForMap(sb.toString());
			log.info("mapResult:"+mapResult);
			
			if((long)mapResult.get("TOTAL") == 0) return;
			
//			if(mapResult == null ) return;
			int  lMinRec = 0;
			int  lMaxRec = 0;
			long total = 0;
			try{
				lMinRec = (int)mapResult.get("MINID");
				lMaxRec = (int)mapResult.get("MAXID");
				total = (long)mapResult.get("TOTAL");
			}catch(Exception e){  // --- 捕获空指针 ---
				System.out.println(e.getMessage());
				return;
			}
			log.info("sql={},table ={},min rec ={},max rec={},Total={}",sb.toString(),orderName,lMinRec,lMaxRec,total);
			
			/*int  lBeginRec = 0;
			int  lEndRec = 0;
			int lRound = (lMaxRec-lMinRec)/10000;
			for(int  i=0;i <= lRound ;++i){
				lBeginRec = lMinRec + 10000*i;
				if(i == lRound){
					lEndRec = lMaxRec;
				}
				else{
					lEndRec = lBeginRec + 10000-1;
				}*/
			int num = Integer.parseInt(BusiToolsIns.getValueFromGlobal("ORDERFAILURE.CLEAN.ORDER.NUM"));
			
			int  lBeginRec = 0;
			int  lEndRec = 0;
			int lRound = (lMaxRec-lMinRec)/num;
			for(int i = 0;i <= lRound ;++i){
				lBeginRec = lMinRec + num * i;
				if(i == lRound){
					lEndRec = lMaxRec;
				}
				else{
					lEndRec = lBeginRec + num - 1;
				}
				log.info("round = {}",i);
				// --- 开始执行 ---
				sb.setLength(0);
				sb.append(strLocalUpdate);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("更新工单sql = {}",sb.toString());
				int result = jdbcTemplate.update(sb.toString());
				log.info("更新工单时间:{} ,工单序列号:{},数量:{}",orderName,iActivitySeqId,result);		
				sb.setLength(0);
				sb.append(strLocalMove);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("工单移入历史sql = {}",sb.toString());
				result = jdbcTemplate.update(sb.toString());
				log.info("工单移入历史:{} ,工单序列号:{},数量:{}",orderName,iActivitySeqId,result);		
				sb.setLength(0);
				sb.append(strLocalDelete);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("删除工单sql = {}",sb.toString());
				result = jdbcTemplate.update(sb.toString());
				log.info("删除工单:{} ,工单序列号:{},数量:{}",orderName,iActivitySeqId,result);		
			
		}
			
		// --- 调用更新工单数量接口 ---
		String updateOrderNum_url = SyscommoncfgDao.query("ORDERFAILURE.UPDATE.OREDER.NUM.URL");
		HashMap<String, Object> request = new HashMap<String, Object>();
		request.put("tenantId", strTenantId);
		request.put("tableName", orderName);
		request.put("delCount", total);
		request.put("busiType", "0");
		String send = HttpUtil.sendPost(updateOrderNum_url, JSON.toJSONString(request));
		log.info("调用更新工单数量返回结果：" + send);

	}


	
	public void deletePhoneIndex(String tenantId, Integer activitySeqId,String channelId, String activityId) {
		
		StringBuilder sb = new StringBuilder();
		String deleteIndex = BusiToolsIns.getValueFromGlobal("ORDERFAILURE.DELETE.PHONE.INDEX.SQL");
		//   /*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID'  */DELETE FROM TTTTTABLENAME WHERE ACTIVITY_SEQ_ID = AAAAACTIVITY_SEQ_ID AND CHANNEL_ID = 'CCCCCHANNEL_ID'
		for(int lastNum = 0;lastNum <= 9;lastNum++){
			
			String indexTableName = "PLT_ORDER_PHONE_INDEX_"+ lastNum;
			String strTmpDelete = deleteIndex.replaceFirst("TTTTTENANT_ID", tenantId);
			String strDelete = strTmpDelete.replaceFirst("TTTTTABLENAME", indexTableName);
			String strDeleteSql = strDelete.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(activitySeqId));
			String deleteSql = strDeleteSql.replaceFirst("CCCCCHANNEL_ID", channelId);
			
			sb.setLength(0);
			sb.append("SELECT COUNT(*) AS TOTAL, MAX(REC_ID) AS MAXID,MIN(REC_ID) AS MINID  FROM ");
			sb.append(indexTableName);
			sb.append(" WHERE ACTIVITY_SEQ_ID = ");
			sb.append(activitySeqId);
			sb.append("  AND TENANT_ID ='");
			sb.append(tenantId);
			sb.append("'");
			sb.append("  AND CHANNEL_ID ='");
			sb.append(channelId);
			sb.append("'");
			Map<String,Object>  mapResult = jdbcTemplate.queryForMap(sb.toString());
			log.info("mapResult:"+mapResult);
			if((long)mapResult.get("TOTAL") == 0) return;
			
			int  lMinRec = 0;
			int  lMaxRec = 0;
			long total = 0;
			try{
				lMinRec = (int)mapResult.get("MINID");
				lMaxRec = (int)mapResult.get("MAXID");
				total = (long)mapResult.get("TOTAL");
			}catch(Exception e){  // --- 捕获空指针 ---
				System.out.println(e.getMessage());
				return;
			}
			log.info("sql={},table ={},min rec ={},max rec={},Total={}",sb.toString(),indexTableName,lMinRec,lMaxRec,total);
			
			int num = Integer.parseInt(BusiToolsIns.getValueFromGlobal("ORDERFAILURE.DELETE.PHONE.INDEX.NUM"));
			
			int  lBeginRec = 0;
			int  lEndRec = 0;
			int lRound = (lMaxRec-lMinRec)/num;
			for(int i = 0;i <= lRound ;++i){
				lBeginRec = lMinRec + num * i;
				if(i == lRound){
					lEndRec = lMaxRec;
				}
				else{
					lEndRec = lBeginRec + num - 1;
				}
				log.info("{}该手机号索引表要循环执行{}次：",indexTableName,i);
				
				sb.setLength(0);
				sb.setLength(0);
				sb.append(deleteSql);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("删除手机号索引表数据sql = {}",sb.toString());
				int result = jdbcTemplate.update(sb.toString());
				log.info("手机号索引表{},活动序列号:{},数量:{}",indexTableName,activitySeqId,result);								
			}			
		}		
	}
}
