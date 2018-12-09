package com.bonc.busi.task.base;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.task.mapper.BaseMapper;
@Service("OverdueOrderMoveHis")
public class OverdueOrderMoveHis {
	
	private final static Logger log= LoggerFactory.getLogger(BusiTools.class);
	@Autowired
	private BaseMapper   TaskBaseMapperIns;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	/*
	 * 从全局变量中得到参数
	 */
	public		String		getValueFromGlobal(String strKey){
		return TaskBaseMapperIns.getValueFromSysCommCfg(strKey);
	}
	/*
	 * 设置全局变量，同时更新数据库
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public	 boolean	setValueToGlobal(String strKey,String strValue){
		TaskBaseMapperIns.updateSysCommonCfg(strKey, strValue);
		return true;
	}
	/*
	 * 数据交换 
	 */
	/*
	 * 失效工单，和ORDERCENTER中的逻辑保持一致
	 */
	public	void  expireActivityHandle(String tenantId,int activitySeqId,String month){

		StringBuilder  sb = new StringBuilder();		
		String iActivitySeqId = String.valueOf(activitySeqId);
		// --- 获取移历史sql并替换 ---
		String	strMove = getValueFromGlobal("ACTIVITYSTATUS.MOVE");
		String  strTmpMove = strMove.replaceFirst("TTTTTNENAT_ID", tenantId);
		String  strLastMove = strTmpMove.replaceFirst("AAAAACTIVITY_SEQ_ID", iActivitySeqId);
		
		// --- 获取删除工单表失效批次工单 sql并替换---
		String	strDelete = getValueFromGlobal("ACTIVITYSTATUS.DELETE");
		String  strTmpDelete = strDelete.replaceFirst("TTTTTNENAT_ID", tenantId);
		String  strLastDelete = strTmpDelete.replaceFirst("AAAAACTIVITY_SEQ_ID",iActivitySeqId);
		
		// --- 获取更新工单表数据sql并替换 --- 
		String  strUpdate = getValueFromGlobal("ACTIVITYSTATUS.UPDATE");
		String  strTmpUpdate = strUpdate.replaceFirst("TTTTTNENAT_ID", tenantId);
		String  strLastUpdate = strTmpUpdate.replaceFirst("AAAAACTIVITY_SEQ_ID",iActivitySeqId);
	    // --- 获取需要遍历的工单表 ---
		String  strTableName = getValueFromGlobal("ACTIVITYSTATUS.TABLE");

	
		String  strTableItem[] = strTableName.split(",");
		String  tableHisName = null;
		for(String tableName:strTableItem){
			// --- 由于弹窗工单表和历史表不一致，需要做特殊处理 ---
			if(tableName.contains("PLT_ORDER_INFO_POPWIN")){
				tableHisName = "PLT_ORDER_INFO_POPWIN_HIS_"+ month;//PLT_ORDER_INFO_POPWIN_1  PLT_ORDER_INFO_POPWIN_HIS_01
				
			}else{
				tableHisName = tableName + "_HIS_" + month;//PLT_ORDER_INFO  PLT_ORDER_INFO_HIS_01
				
			}
			// --- 替换表名 ---
			String	strLocalUpdate = strLastUpdate.replaceAll("TTTTTABLENAME", tableName);
			String	strLocalDelete = strLastDelete.replaceAll("TTTTTABLENAME", tableName);
			
			String  move = strLastMove.replace("TTTTTABLENAME_HIS", tableHisName);
			String  strLocalMove = move.replace("TTTTTABLENAME", tableName);

			sb.setLength(0);
			sb.append("SELECT MAX(REC_ID) AS MAXID,MIN(REC_ID) AS MINID  FROM ");
			sb.append(tableName);
			sb.append(" WHERE ACTIVITY_SEQ_ID = ");
			sb.append(iActivitySeqId);
			sb.append("  AND TENANT_ID ='");
			sb.append(tenantId);
			sb.append("'");
			Map<String,Object>  mapResult = jdbcTemplate.queryForMap(sb.toString());
			log.info("mapResult================="+mapResult.toString());
			if(mapResult == null||mapResult.get("MAXID")==null||"null".equals(String.valueOf(mapResult.get("MAXID")))||"".equals(String.valueOf(mapResult.get("MAXID")))) continue;
			long lMinRec = 0;
			long lMaxRec =0;
			try{
				lMinRec = Long.valueOf(String.valueOf(mapResult.get("MINID")));
				lMaxRec = Long.valueOf(String.valueOf(mapResult.get("MAXID")));
			}catch(Exception e){  // --- 捕获空指针 ---
				e.printStackTrace();
				continue;
			}
			log.info("sql:{},工单表名:{},批次:{},MinRec:{},MaxRec:{}",sb.toString(),tableName,iActivitySeqId,lMinRec,lMaxRec);
//			log.info("sql={},table ={},min rec ={},max rec={},={}",sb.toString(),tableName,lMinRec,lMaxRec,mapResult.toString());
			long lBeginRec = 0L;
			long lEndRec = 0L;
			long lRound = (lMaxRec-lMinRec)/10000L;
			for(long i=0;i <= lRound ;++i){
				lBeginRec = lMinRec + 10000L*i;
				if(i == lRound){
					lEndRec = lMaxRec;
				}
				else{
					lEndRec = lBeginRec + 10000L-1L;
				}
//				log.info("round = {}",i);
				// --- 开始执行 ---
				sb.setLength(0);
				sb.append(strLocalUpdate);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("updateOrderSql={},lBeginRec={},lEndRec={}",sb.toString(),lBeginRec,lEndRec);
				int result = jdbcTemplate.update(sb.toString());
				log.info("----------更新的工单表:{},批次:{},数量:{}",tableName,iActivitySeqId,result);		
				sb.setLength(0);
				sb.append(strLocalMove);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("moveOrderToHisSql={},lBeginRec={},lEndRec={}",sb.toString(),lBeginRec,lEndRec);
				result = jdbcTemplate.update(sb.toString());
				log.info("----------工单移入的历史表:{},批次:{},数量:{}",tableHisName,iActivitySeqId,result);		
				sb.setLength(0);
				sb.append(strLocalDelete);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("deleteOrderSql={},lBeginRec={},lEndRec={}",sb.toString(),lBeginRec,lEndRec);
				result = jdbcTemplate.update(sb.toString());
				log.info("----------删除工单的工单表:{},批次:{},数量:{}",tableName,iActivitySeqId,result);		
			}
		}
			// --- 调用统计 ---
//			HashMap<String,String>   mapActivity = new HashMap<String,String>();
//			mapActivity.put("activityId",activityId);
//			mapActivity.put("tenantId", strTenantId);
			//StatisticServiceIns.invalidActivity(mapActivity);
		log.info("活动批次失效结束");	
	}
}
