package com.bonc.busi.outer.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.busi.outer.model.PltActivityInfo;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.statistic.service.StatisticService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.Global;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.StringUtil;

@Service("OrderActivityService")
public class OrderActivityServiceImpl  implements OrderActivityService{
	
	@Autowired
	PltActivityInfoDao		PltActivityInfoDaoIns;
	@Autowired
	 private JdbcTemplate jdbcTemplate;
	@Autowired
	private BusiTools  BusiToolsIns;
	@Autowired	private StatisticService StatisticServiceIns;
	
	private final static Logger log = LoggerFactory.getLogger(OrderActivityServiceImpl.class);
	
	/*
	 * 更新活动状态
	 */
	public  JsonResult setActivityStatus(ActivityStatus reqdata){
		JsonResult	JsonResultIns = new JsonResult();
		String activityId = reqdata.getActivityId();
		String tenantId = reqdata.getTenant_id();
		// --- 测试SQL  ---
		/*
		StringBuilder   sb = new StringBuilder();
		sb.append(" SELECT * FROM PLT_ACTIVITY_INFO ");
		List<Map<String,Object>>   list  =(List<Map<String,Object>>)jdbcTemplate.queryForList(sb.toString());
		if(list != null){
			for(Map map:list){
				log.warn("map:"+map);
			}
		}
		else{
			log.warn("list is null");
		}
		*/
		
		
		// --- 查询活动是否存在 ---
		final List<PltActivityInfo>	listPltActivityInfo = 
	//	PltActivityInfo		PltActivityInfoIns = 
				PltActivityInfoDaoIns.retrievePltActivityInfoByActivityId(activityId,tenantId);//根据activity_id得到活动信息
		if(listPltActivityInfo == null || listPltActivityInfo.size() == 0){
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("活动不存在 !!!");
			return JsonResultIns;
		}
		// --- 获取最新批次 信息 ---
		PltActivityInfo	PltActivityInfoIns = listPltActivityInfo.get(listPltActivityInfo.size()-1);
		// --- 根据最新批次的活动状态判断该活动是否失效 ---
		int sqlStatus = PltActivityInfoIns.getACTIVITY_STATUS();
		// --- 输入的活动状态 ---
		int inActivityStatus =  Integer.parseInt(reqdata.getActivityStatus());
		// --- 根据最新批次状态判断活动是否失效 ---
		if(sqlStatus != inActivityStatus){
			if(sqlStatus == 2){  // ---已经失效了是否就不允许修改了?
				JsonResultIns.setCode("2");
				JsonResultIns.setMessage("活动已经失效 !!!");
				return JsonResultIns;
			}
			if(inActivityStatus == 2){   // --- 活动失效  ---
				// --- 更新活动状态和时间 ---
				PltActivityInfoDaoIns.expirePltActivityInfo(PltActivityInfoIns);
				final List<PltActivityInfo> activityInfo =  PltActivityInfoDaoIns.activityInfoByActivityId(activityId,tenantId);
				// --- 启动新线程去执行工单的更改（暂时先不执行 )  ----
			    ExecutorService pool = Global.getExecutorService();
				log.info(" 启动新线程执行");
				pool.execute(new Runnable() {
					public void run() {
						for(PltActivityInfo item:activityInfo){
							String tenantId = item.getTENANT_ID();
							int  activitySeqId = item.getREC_ID();
							Date orderEndTime = item.getORDER_END_DATE();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String orderEndDate = dateFormat.format(orderEndTime);
							log.info("----------orderEndDate----------"+orderEndDate);
							String month = orderEndDate.substring(orderEndDate.indexOf("-")+1, orderEndDate.lastIndexOf("-"));
							expireActivityHandle(tenantId,activitySeqId,month);
						}
					}
				}
			);
				
//				//通知各个渠道不进行工单执行
//				String		stopSmsUrl = BusiToolsIns.getValueFromGlobal("CHANNELSTOP.SMS");
//				HashMap<String, String> smsStop = new HashMap<String, String>();
//				smsStop.put("tenantId", reqdata.getTenant_id());
//				smsStop.put("activityId", reqdata.getActivityId());
//				
//				try {
//					String result = HttpUtil.sendPost(stopSmsUrl, JSON.toJSONString(smsStop));
//					@SuppressWarnings("unchecked")
//					HashMap<String, String> smsResp = JSON.parseObject(result,HashMap.class);
//					if("3".equals(smsResp.get("code"))){
//						log.warn(smsResp.get("msg"));
//					}
//				} catch (Exception e) {
//					log.error("活动失效短信渠道通知失败！");
//				}
			}
			else 	if( inActivityStatus == 9){   // --- 活动挂起  ---
				PltActivityInfoDaoIns.suspendActivity(PltActivityInfoIns);
			}
			else 	if( inActivityStatus == 8){   // --- 活动取消挂起  ---
				if(PltActivityInfoIns.getACTIVITY_STATUS() != 9){
					JsonResultIns.setCode("6");
					JsonResultIns.setMessage("活动未挂起 !!!");
					return JsonResultIns;
				}
				else{
					PltActivityInfoDaoIns.resumeActivity(PltActivityInfoIns);
				}
			}
			else{
				JsonResultIns.setCode("7");
				JsonResultIns.setMessage("传入的状态不支持 !!!");
				return JsonResultIns;
			}
		}
		log.warn("活动名称:"+PltActivityInfoIns.getACTIVITY_NAME());
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("update sucess");
		
		return JsonResultIns;
	}
	/*
	 * 启动线程转移工单
	 */
	//private  void  expireActivityHandle(List<PltActivityInfo>	listPltActivityInfo){
	private	 void  expireActivityHandle(String tenantId,int activitySeqId,String month){
		StringBuilder  sb = new StringBuilder();
		String iActivitySeqId = String.valueOf(activitySeqId);
		
			// --- 获取需要遍历的工单表 --- 
		    String	strTableName = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.TABLE");
		
		    // --- 获取sql --- 
			String	strMove = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.MOVE");
			String	strDelete = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.DELETE");
			String	strUpdate = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.UPDATE");
            // --- 替换租户id ---
			String	strTmpMove = strMove.replaceFirst("TTTTTNENAT_ID", tenantId);
			String	strTmpDelete = strDelete.replaceFirst("TTTTTNENAT_ID", tenantId);
			String	strTmpUpdate = strUpdate.replaceFirst("TTTTTNENAT_ID", tenantId);
			// --- 替换失效批次 ---
			String	strLastMove = strTmpMove.replaceFirst("AAAAACTIVITY_SEQ_ID",iActivitySeqId);
			String	strLastDelete = strTmpDelete.replaceFirst("AAAAACTIVITY_SEQ_ID",iActivitySeqId);
			String	strLastUpdate = strTmpUpdate.replaceFirst("AAAAACTIVITY_SEQ_ID",iActivitySeqId);
			
			String	strTableItem[] = strTableName.split(",");
			String  tableHisName = null;
			for(String tableName:strTableItem){
				// --- 由于弹窗工单表和历史表表名不一致，需要做特殊处理 ---
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
				sb.append(" AND TENANT_ID ='");
				sb.append(tenantId);
				sb.append("'");
				log.info("sql={}",sb.toString());
				Map<String,Object> mapResult = jdbcTemplate.queryForMap(sb.toString());
				if(mapResult.get("MAXID")==null||"null".equals(String.valueOf(mapResult.get("MAXID")))||"".equals(String.valueOf(mapResult.get("MAXID")))) continue;
//				if(mapResult == null ) continue;
	
				long  lMinRec = 0;
				long  lMaxRec =0;
				try{
					lMinRec = Long.valueOf(String.valueOf(mapResult.get("MINID")));
					lMaxRec = Long.valueOf(String.valueOf(mapResult.get("MAXID")));
				}catch(Exception e){  // --- 捕获空指针 ---
					e.printStackTrace();
					continue;
				}
				log.info("sql={},table ={},min rec ={},max rec={},={}",sb.toString(),tableName,lMinRec,lMaxRec,mapResult.toString());
				long  lBeginRec = 0L;
				long  lEndRec = 0L;
				long  lRound = (lMaxRec-lMinRec)/10000L;
				for(long i=0;i <= lRound ;++i){
					lBeginRec = lMinRec + 10000L*i;
					if(i == lRound){
						lEndRec = lMaxRec;
					}
					else{
						lEndRec = lBeginRec + 10000L-1L;
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
					log.info("更新工单表:{} ,工单序列号:{},数量:{}",tableName,iActivitySeqId,result);		
					sb.setLength(0);
					sb.append(strLocalMove);
					sb.append(" AND REC_ID >= ");
					sb.append(lBeginRec);
					sb.append(" AND REC_ID <= ");
					sb.append(lEndRec);
					log.info("工单移入历史sql = {}",sb.toString());
					result = jdbcTemplate.update(sb.toString());
					log.info("工单移入历史表:{} ,工单序列号:{},数量:{}",tableHisName,iActivitySeqId,result);		
					sb.setLength(0);
					sb.append(strLocalDelete);
					sb.append(" AND REC_ID >= ");
					sb.append(lBeginRec);
					sb.append(" AND REC_ID <= ");
					sb.append(lEndRec);
					log.info("删除工单sql = {}",sb.toString());
					result = jdbcTemplate.update(sb.toString());
					log.info("删除工单表数据:{} ,工单序列号:{},数量:{}",tableName,iActivitySeqId,result);		
				}
			
			}
			// --- 调用统计 ---
//			HashMap<String,String>   mapActivity = new HashMap<String,String>();
//			mapActivity.put("activityId",activityId);
//			mapActivity.put("tenantId", strTenantId);
//			StatisticServiceIns.invalidActivity(mapActivity);
			log.info("活动批次失效结束");
		
	}
	
	
	/**
	 * 查询活动订单
	 * 
	 */
	@Override
	public List<HashMap<String, Object>> queryActivityOrderInfo(HashMap<String, Object> req) {
		if(!StringUtil.validateStr(req.get("envType"))){
			req.put("envTypeAnd", " AND RESERVE1 = #{envType} ");
		}
		if(!StringUtil.validateStr(req.get("eventId"))){
			req.put("eventIdAnd", " AND RESERVE2 = #{eventId} ");	
		}
		if(!StringUtil.validateStr(req.get("channelId"))){
			req.put("channelIdAnd", " AND CHANNEL_ID = #{channelId} ");	
		}
		if(!StringUtil.validateStr(req.get("activityId"))){
			req.put("activitySeqId", " AND	ACTIVITY_SEQ_ID IN (SELECT IFNULL(REC_ID,0) FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = #{activityId}) ");	
		}
		
		return  PltActivityInfoDaoIns.selectActivityOrder(req);
	}
	
	
}
