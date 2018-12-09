package com.bonc.busi.statistic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.statistic.mapper.StatisticActivityMapper;
import com.bonc.busi.statistic.service.StatisticService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.DateUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService{

	private static final Logger log = Logger.getLogger(StatisticServiceImpl.class);
	
	@Autowired
	private StatisticActivityMapper mapper;
	
	@Autowired
	private BusiTools  BusiTools;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private BusiTools  AsynDataIns;
	
	/**
	 * 维度修改为 ORG_PATH、ACTIVITY_SEQ_ID
	 * @param req
	 * @return
	 */
	private String getStatisticSql(HashMap<String, Object> req){
		String activity =  (null==req.get("activitySeqId")||"".equals("activitySeqId"))?"":(" AND ACTIVITY_SEQ_ID='"+req.get("activitySeqId")+"' ");
		String org =  (null==req.get("orgPath")||"".equals("orgPath"))?"":(" AND ORG_PATH LIKE '"+req.get("orgPath")+"%' ");
		return (" SELECT NOW() STATISTIC_DATE,TENANT_ID,ACTIVITY_SEQ_ID,ORG_PATH,WENDING_FLAG LOGIN_ID,RESERVE3 IS_EXE, "
				+ " SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',3),'/',-1) AREA_NO, " //客户经理归属地市
				+ " SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',4),'/',-1) CITY_ID, " //客户经理归属地市
				+ " COUNT(*) TOTAL_NUM, " //-- 有效总量
				+ " COUNT(IF(CONTACT_CODE IN ('101', '102', '103', '104', '121') AND DATE(CONTACT_DATE)=CURDATE(),TRUE,NULL)) VISIT_NUMS_TODAY," //-- 今日回访量
				+ " COUNT(IF(CONTACT_CODE IN ('101', '102', '103', '104', '121'), TRUE, NULL)) VISIT_NUMS_TOTAL," // -- 总回访量
				+ " COUNT(IF(CHANNEL_STATUS='3', TRUE, NULL)) INTER_SUCCESS," // -- 总成功量
				+ " COUNT(IF((CONTACT_CODE IN ('101', '102', '103', '104', '121') AND CHANNEL_STATUS='3'), TRUE, NULL)) VISITED_SUCCESS," //-- 已回访成功量
				+ " COUNT(IF((CONTACT_CODE IN ('101', '102', '103', '104', '121') AND CHANNEL_STATUS<>'3'), TRUE, NULL)) VISITED_NO_SUCCESS,"//已回访未成功
				+ " IFNULL(COUNT(IF(CONTACT_CODE='0' OR CONTACT_CODE IS NULL ,TRUE,NULL)),0) ITEM0,"//未接触量
				+ " IFNULL(COUNT(IF(CONTACT_CODE='101',TRUE,NULL)),0) ITEM101,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='102',TRUE,NULL)),0) ITEM102,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='103',TRUE,NULL)),0) ITEM103,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='104',TRUE,NULL)),0) ITEM104,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='121',TRUE,NULL)),0) ITEM121,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='201',TRUE,NULL)),0) ITEM201,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='202',TRUE,NULL)),0) ITEM202,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='203',TRUE,NULL)),0) ITEM203,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='204',TRUE,NULL)),0) ITEM204,"
				+ " IFNULL(COUNT(IF(CONTACT_TYPE='1',TRUE,NULL)),0) TYPE1,"
				+ " IFNULL(COUNT(IF(CONTACT_TYPE='2',TRUE,NULL)),0) TYPE2,"
				+ " IFNULL(COUNT(IF(CONTACT_TYPE='3',TRUE,NULL)),0) TYPE3, '"+req.get("MARKER")+"' MARKER "
				+ " FROM PLT_ORDER_INFO WHERE TENANT_ID='"+req.get("tenantId")+"' " + activity + org
				+ " AND CHANNEL_STATUS NOT IN('401','402','403') AND ORDER_STATUS=5 AND CHANNEL_ID='"+req.get("channelId")+"' "
				+ " GROUP BY TENANT_ID,CHANNEL_ID,ACTIVITY_SEQ_ID,WENDING_FLAG,ORG_PATH,RESERVE3 ");
	}
	
	public String insertStatistic(HashMap<String, Object> req){
		String insert = "INSERT INTO PLT_ORDER_STATISTIC (STATISTIC_DATE,TENANT_ID,ACTIVITY_SEQ_ID,ORG_PATH,LOGIN_ID,IS_EXE,AREA_NO,CITY_ID,VALID_NUMS,VISIT_NUMS_TODAY,VISIT_NUMS_TOTAL,INTER_SUCCESS,VISITED_SUCCESS,VISITED_NO_SUCCESS,ITEM0, ITEM101,ITEM102,ITEM103,ITEM104,ITEM121,ITEM201,ITEM202,ITEM203,ITEM204,TYPE1,TYPE2,TYPE3,MARKER) ";
		req.put("MARKER", "1");
		insert = insert + getStatisticSql(req);
		return insert;
	}

	@Override
	public void statisticBench(String tenantId,String activitySeqId) {
		HashMap<String, Object> req = new HashMap<String, Object>();
		req.put("TENANT_ID", tenantId);
		req.put("ACTIVITY_SEQ_ID", activitySeqId);
		
		PltCommonLog		logdb = new PltCommonLog();
		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("13");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSPONSOR("STATISITC");
		logdb.setBUSI_CODE("SUCCESS_STATISTIC_START");
		logdb.setBUSI_ITEM_5("批次号"+activitySeqId);
		logdb.setDEST_NUM(Integer.parseInt(activitySeqId));
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_DESC("成功后开始统计！");
		AsynDataIns.insertPltCommonLog(logdb);
		
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_DESC("成功后统计结束！");
		logdb.setBUSI_CODE("SUCCESS_STATISTIC_END");
		
		//有效的 批次渠道
		List<HashMap<String,Object>> recIds = mapper.getActivityRecList(req);
		HashMap<String, String> orderTable = codeService.getValue(tenantId, IContants.CHANNEL_ORDER_TABLE);
		//统计一个批次，一个渠道 完成工单总数，工单成功数，干预成功数，个别渠道的剩余工单量
		for(HashMap<String, Object> recId:recIds){
			try{
				logdb.setBUSI_ITEM_4(recId.get("CHANNEL_ID")+"");
				logdb.setBUSI_ITEM_3(orderTable.get(recId.get("CHANNEL_ID")));
				req.putAll(recId);
				
				//短信渠道统计成功的工单数
				req.put("TABLE_NAME", orderTable.get(recId.get("CHANNEL_ID")));
				
				HashMap<String, Object> statistic = new HashMap<String, Object>();
				
				if(IContants.DX_CHANNEL.equals(recId.get("CHANNEL_ID"))||IContants.TC_CHANNEL_1.equals(recId.get("CHANNEL_ID"))||IContants.TC_CHANNEL_2.equals(recId.get("CHANNEL_ID"))) {
					
					int VALID_NUM = 0;    //干预成功量
					int FINISH_NUM = 0;      //成功量
					int VISITED_SUCCESS = 0;    //干预成功量
					int TOTAL_SUCCESS = 0;      //成功量
					
					for(int i=0;i<10;i++) {
						
						if(IContants.DX_CHANNEL.equals(recId.get("CHANNEL_ID"))){
							req.put("TABLE_NAME", orderTable.get(recId.get("CHANNEL_ID"))+"_HIS_"+recId.get("MONTH")+i);
						}else if(IContants.TC_CHANNEL_1.equals(recId.get("CHANNEL_ID"))||IContants.TC_CHANNEL_2.equals(recId.get("CHANNEL_ID"))){
							req.put("TABLE_NAME", orderTable.get(recId.get("CHANNEL_ID"))+"_"+i);
						}
						
						HashMap<String, Object> statisticOne = mapper.getRecStatistic(req);
						statistic.putAll(statisticOne);
						VALID_NUM += ((Long)statisticOne.get("VALID_NUM")).intValue();
						FINISH_NUM += ((Long)statisticOne.get("FINISH_NUM")).intValue();	
						VISITED_SUCCESS += ((Long)statisticOne.get("VISITED_SUCCESS")).intValue();
						TOTAL_SUCCESS += ((Long)statisticOne.get("TOTAL_SUCCESS")).intValue();
						statistic.put("VALID_NUM",VALID_NUM);
						statistic.put("FINISH_NUM",FINISH_NUM);
						statistic.put("VISITED_SUCCESS",VISITED_SUCCESS);
						statistic.put("TOTAL_SUCCESS",TOTAL_SUCCESS);
					}
				}else {
					statistic = mapper.getRecStatistic(req);
				}
				
				
				if(null==statistic){
					log.warn("批次统计为空！");
					continue;
				}
				Integer num = mapper.updateRecStatistic(statistic);
				if(IContants.YX_CHANNEL.contains(""+recId.get("CHANNEL_ID"))){
					req.put("activitySeqId", activitySeqId);
					req.put("tenantId", tenantId);
					managerStatisitc(req);
				}
				log.info("批统计信息更新——>>>>"+JSON.toJSONString(statistic)+"更新结果： num="+num);
				logdb.setBUSI_ITEM_10("1");
			}catch(Exception e){
				e.printStackTrace();
				logdb.setBUSI_ITEM_10("0");
				log.error("渠道批次统计失败!"+JSON.toJSONString(recId));
			}finally{
				logdb.setEND_TIME(new Date());
				logdb.setBUSI_ITEM_1((logdb.getEND_TIME().getTime()-logdb.getSTART_TIME().getTime())/1000.0+"s");
				AsynDataIns.insertPltCommonLog(logdb);
			}
		}
	}

	private void initLoginId(HashMap<String, Object> map) {
		Integer maxRecId = mapper.getOrgRecMax(map);
		if(null==maxRecId||maxRecId<=0){
			return;
		}
		
		StringBuilder sql = new StringBuilder("UPDATE PLT_ORDER_INFO o,PLT_ORG_LOGIN_INFO l ");
		sql.append("SET o.WENDING_FLAG=l.LOGIN_ID,o.RESERVE3=1 ");
		sql.append("WHERE o.TENANT_ID='").append(map.get("tenantId")).append("' AND l.TENANT_ID='").append(map.get("tenantId")).append("' AND o.ACTIVITY_SEQ_ID=").append(map.get("activitySeqId")).append(" AND l.ORG_PATH=o.ORG_PATH AND (o.WENDING_FLAG IS NULL OR o.WENDING_FLAG ='' ) ");
		
		int i=0;
		while (i<=maxRecId) {
			String limit = " AND l.REC_ID>"+(i)+" AND l.REC_ID<="+(i+=500);
			AsynDataIns.executeDdlOnMysql(sql.toString()+limit, map.get("tenantId")+"");
		}
	}
	
	/**
	 * 跑这个增量活动统计的时候需要注意的是，跑完活动统计之后会收集批次生失效时间，这个短息渠道存在风险，
	 * 
	 * 需要注意的是尽量保证　只有新建活动的时候才跑改信息
	 */
	@Override
	public void incrStatistic(HashMap<String, Object> map) {
		
		initLoginId(map);
		
		PltCommonLog		logdb = new PltCommonLog();
		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("12");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("STATISITC");
		logdb.setBUSI_CODE("INRC_STATISITC_START");
		logdb.setBUSI_ITEM_5("批次号"+map.get("activitySeqId"));
		logdb.setDEST_NUM(Integer.parseInt(map.get("activitySeqId")+""));
		logdb.setBUSI_DESC("工单开始统计！");
		AsynDataIns.insertPltCommonLog(logdb);
		
		logdb.setBUSI_CODE("INRC_STATISITC_END");
		logdb.setBUSI_DESC("工单统计结束！");
		
		//初始设置MAP 使用信息
		map.put("TENANT_ID", map.get("tenantId"));
		map.put("ACTIVITY_SEQ_ID", map.get("activitySeqId"));
		
		//查询该批次所生成的渠道列表
		List<HashMap<String, Object>> list = mapper.orderDate(map);
		
		//查询该批次的活动信息
		HashMap<String, Object> activityInfo = null;
		activityInfo = mapper.getActivityInfo(map);
		if(null==activityInfo){
			log.error("该批次的活动不存在！");
			throw new BoncExpection(IContants.BUSI_ERROR_CODE,"not find this activity by ACTIVITY_SEQ_ID="+map.get("ACTIVITY_SEQ_ID"));
		}
		if("2".equals(activityInfo.get("ACTIVITY_STATUS"))){
			log.error("该批次的活动已经失效！");
			throw new BoncExpection(IContants.BUSI_ERROR_CODE,"activity status is 2");
		}
		
		//判断工单生成规则，如果是覆盖的化统计当前活动有效批次的陪覆盖掉的工单数
		List<HashMap<String, Object>> activitySeqInfo = new ArrayList<HashMap<String,Object>>();
		String ORDER_UPDATE_RULE = activityInfo.get("ORDER_UPDATE_RULE")+"";
		if("2".equals(ORDER_UPDATE_RULE)){
			activitySeqInfo=mapper.getActivitySeqs(activityInfo);
		}
		
		//查询各个渠道所在的工单表名称
		HashMap<String, String> orderTable = codeService.getValue(map.get("TENANT_ID")+"", IContants.CHANNEL_ORDER_TABLE);
		
		long start = 0;
		long end = 0;
		
		for(HashMap<String, Object> order:list){
			HashMap<String, Float> timeMap = new HashMap<String, Float>();
			try{
				order.put("tableName", orderTable.get(order.get("CHANNEL_ID")));
				
				//根据批次的声失效时间同步PROCESS_LOG表
				activityInfo.put("PROC_LOG_ID", order.get("REC_ID"));
				if(null==order.get("ORDER_BEGIN_DATE")&&null!=activityInfo.get("ORDER_BEGIN_DATE")){
					mapper.updateChannelLog(activityInfo);
				}
				
				//查询免打扰名单过滤数量
				start = System.currentTimeMillis();
				Integer BLACK_NUM = statisticBlackRec(order);
				end = System.currentTimeMillis();
				order.put("BLACK_NUM", BLACK_NUM);
				
				timeMap.put("1", (end-start)/1000f);
				
				//根据工单生成的月份判断短信渠道和其他渠道规则过滤工单所在的月表
				order.put("month","_" + DateUtil.getCurMonth());
				Integer CONTACT_FILTER_NUM = 0;
				Integer RULE_NUM = 0;
				Integer REMAIN_NUM = 0;
				if(IContants.DX_CHANNEL.equals(order.get("CHANNEL_ID"))) {
					
					for(int i=0;i<10;i++) {					
						order.put("month","_" + DateUtil.getCurMonth() + i);
						//查询接触过滤工单数
						order.put("ORDER_STATUS", 2);
						start = System.currentTimeMillis();
						CONTACT_FILTER_NUM += statisticRuleRec(order);
						end = System.currentTimeMillis();
						
						timeMap.put("2", (end-start)/1000f);
						order.put("CONTACT_FILTER_NUM", CONTACT_FILTER_NUM);
						
						
						//查询有进有出规则删除工单数，SEND表中的工单状态包含 规则过滤和接触过滤
						order.put("ORDER_STATUS", 3);
						start = System.currentTimeMillis();
						RULE_NUM += statisticRuleRec(order);
						end = System.currentTimeMillis();
						
						timeMap.put("3", (end-start)/1000f);
						order.put("RULE_NUM", RULE_NUM+CONTACT_FILTER_NUM);
						
						//查询有进有出规则删除工单数，SEND表中的工单状态包含 规则过滤和接触过滤
						order.put("ORDER_STATUS", 7);
						start = System.currentTimeMillis();
						REMAIN_NUM += statisticRuleRec(order);
						end = System.currentTimeMillis();						
						timeMap.put("4", (end-start)/1000f);
						order.put("REMAIN_NUM", REMAIN_NUM);
					}
				}else {
					//查询接触过滤工单数
					order.put("ORDER_STATUS", 2);
					start = System.currentTimeMillis();
					CONTACT_FILTER_NUM = statisticRuleRec(order);
					end = System.currentTimeMillis();
					
					timeMap.put("2", (end-start)/1000f);
					order.put("CONTACT_FILTER_NUM", CONTACT_FILTER_NUM);
					
					
					//查询有进有出规则删除工单数，SEND表中的工单状态包含 规则过滤和接触过滤
					order.put("ORDER_STATUS", 3);
					start = System.currentTimeMillis();
					RULE_NUM = statisticRuleRec(order);
					end = System.currentTimeMillis();
					
					timeMap.put("3", (end-start)/1000f);
					order.put("RULE_NUM", RULE_NUM+CONTACT_FILTER_NUM);
					
					//查询有进有出规则删除工单数，SEND表中的工单状态包含 规则过滤和接触过滤
					order.put("ORDER_STATUS", 7);
					start = System.currentTimeMillis();
					REMAIN_NUM = statisticRuleRec(order);
					end = System.currentTimeMillis();
					
					timeMap.put("4", (end-start)/1000f);
					order.put("REMAIN_NUM", REMAIN_NUM);
				}
				
				
				//查询一下有效工单数
				order.put("ORDER_STATUS", 6);
				start = System.currentTimeMillis();
				Integer SUCCESS_NUM = mapper.statisticRec(order);
				end = System.currentTimeMillis();
				
				timeMap.put("5", (end-start)/1000f);
				order.put("SUCCESS_NUM", SUCCESS_NUM);
				
				//查询一下有效工单数
				order.put("ORDER_STATUS", 5);
				start = System.currentTimeMillis();
				Integer VALID_NUM = mapper.statisticRec(order);
				end = System.currentTimeMillis();
				
				timeMap.put("6", (end-start)/1000f);
				order.put("VALID_NUM", VALID_NUM);
				
				String UUID = StringUtil.getUUID();
				order.put("UUID", UUID);
				
				try {
					mapper.addActivityDeqStatistic(order);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("渠道统计接口插入失败!");
				}
				
				//统计留存数
				start = System.currentTimeMillis();
				Integer REMAIN_NUM_COUNT = mapper.statisticRemainNum(order);
				end = System.currentTimeMillis();
				
				timeMap.put("7", (end-start)/1000f);
				order.put("REMAIN_NUM_COUNT", REMAIN_NUM_COUNT);
				
				
				//拼加原始工单数
				Integer ALL_COUNT = RULE_NUM+CONTACT_FILTER_NUM+SUCCESS_NUM+BLACK_NUM+REMAIN_NUM;
				order.put("ALL_COUNT", ALL_COUNT);
				//COUNT_DETAIL 表RULE_NUM 只包含RULE_NUM
				order.put("RULE_NUM", RULE_NUM);
				
				
				List<HashMap<String, Object>> activityCount = mapper.findActivityCount(order);
				if(null==activityCount||activityCount.size()==0){
					order.put("ACTIVITY_THEMEID", activityInfo.get("ACTIVITY_THEMEID"));
					mapper.addActivityCount(order);
				}
				
				//查询覆盖规则删除工单数,有效工单数，并对对应的批次统计信息进行更新
				start = System.currentTimeMillis();
				for(HashMap<String, Object> activitySeq:activitySeqInfo){
					activitySeq.put("ORDER_STATUS", 3);
					activitySeq.put("ACTIVITY_SEQ_ID", activitySeq.get("REC_ID"));
					activitySeq.put("CHANNEL_ID", order.get("CHANNEL_ID"));
					activitySeq.put("tableName", order.get("tableName"));
					activitySeq.put("month", order.get("month"));
					
					activitySeq.put("RULE_NUM", statisticRuleRec(activitySeq));
					
					//查询一下有效工单数
					activitySeq.put("ORDER_STATUS", 5);
					activitySeq.put("VALID_NUM", mapper.statisticRec(activitySeq));
					mapper.updateRuleNum(activitySeq);
					mapper.updateActivityCount(activitySeq);
					mapper.updateActivityFilterAllCount(activitySeq);
				}
				end = System.currentTimeMillis();
				timeMap.put("8", (end-start)/1000f);
				
				logdb.setBUSI_ITEM_7("0");
				logdb.setEND_TIME(new Date());
				logdb.setBUSI_ITEM_1(order.get("CHANNEL_ID")+"");
				logdb.setBUSI_ITEM_2(orderTable.get(order.get("CHANNEL_ID")));
				
				if(IContants.YX_CHANNEL.equals(order.get("CHANNEL_ID"))){
					start = System.currentTimeMillis();
					managerStatisitc(map);
					
					//重新统计覆盖规则客户经理批次信息
					for(HashMap<String, Object> activitySeq:activitySeqInfo){
						map.put("activitySeqId", activitySeq.get("REC_ID"));
						managerStatisitc(map);
					}
					end = System.currentTimeMillis();
					timeMap.put("8", (end-start)/1000f);
				}
				
				//渠道状态准备好之后 插入标识
				order.put("STATUS", 0);
				mapper.addActivityChannelStatus(order);
				
				logdb.setBUSI_ITEM_10(JSON.toJSONString(timeMap));
				AsynDataIns.insertPltCommonLog(logdb);
			}catch (Exception e){
				e.printStackTrace();
				logdb.setEND_TIME(new Date());
				logdb.setBUSI_ITEM_11(e.getMessage().substring(200));
				logdb.setBUSI_ITEM_7("1");
				logdb.setBUSI_DESC("活动增量统计异常！");
				logdb.setBUSI_ITEM_10(JSON.toJSONString(timeMap));
				AsynDataIns.insertPltCommonLog(logdb);
			}
		}
	}

	public Integer statisticBlackRec(HashMap<String, Object> order) {
		return mapper.statisticBlackRec(order);
	}

	public Integer statisticRuleRec(HashMap<String, Object> order) {
		return mapper.statisticRuleRec(order);
	}

	private void managerStatisitc(HashMap<String, Object> req) {
		PltCommonLog		logdb = new PltCommonLog();
		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("11");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("STATISITC");
		logdb.setBUSI_CODE("INRC_STATISITC");
		logdb.setBUSI_ITEM_1(IContants.YX_CHANNEL);
		logdb.setBUSI_ITEM_2(IContants.PLT_ORDER_INFO);
		logdb.setBUSI_ITEM_5("批次号"+req.get("activitySeqId"));
		logdb.setBUSI_DESC("活动初始化统计开始"+req.get("activitySeqId"));
		logdb.setDEST_NUM(Integer.parseInt(req.get("activitySeqId")+""));
		try {
			long start = System.currentTimeMillis();
			req.put("channelId", IContants.YX_CHANNEL);
			req.put("CHANNEL_ID", IContants.YX_CHANNEL);
			req.put("TENANT_ID", req.get("tenantId"));
			req.put("ACTIVITY_SEQ_ID", req.get("activitySeqId"));
			mapper.delIncrStatistic(req);
			long end = System.currentTimeMillis();
			log.info("活动统计清除总耗时——>>>>"+(end-start)/1000.0+"s");
			start = System.currentTimeMillis();
			
			// 工单统计之前 把为"" 的loginId 设置成为NULL 如果是NULL认为工单每到人
			mapper.updateEmptyLoginId(req);
			
			BusiTools.executeDdlOnMysql(insertStatistic(req),""+req.get("tenantId"));
			
			req.put("tableName", IContants.PLT_ORDER_INFO);
			HashMap<String, Object> orderHashMap = mapper.orderLimit(req);
			if(null!=orderHashMap){
				mapper.updateStatistic(orderHashMap);
			}
			//批次统计
			end = System.currentTimeMillis();
			log.info("活动统计总耗时——>>>>"+(end-start)/1000.0+"s");
			logdb.setBUSI_ITEM_10("0");
			logdb.setEND_TIME(new Date());
			AsynDataIns.insertPltCommonLog(logdb);
		} catch (Exception e) {
			e.printStackTrace();
			logdb.setBUSI_ITEM_10("1");
			AsynDataIns.insertPltCommonLog(logdb);
		}
	}

	@Override
	public void backStatisitic() {
		List<Map<String, Object>> tenantIdsList = BusiTools.getValidTenantInfo();
		long first = System.currentTimeMillis();
		//遍历租户
		for(Map<String, Object> map : tenantIdsList){
			//初始化今日回访量
			mapper.dayUpdate(map);
			String SQL = "INSERT INTO `PLT_ORDER_STATISTIC_HIS` SELECT * FROM PLT_ORDER_STATISTIC WHERE TENANT_ID='"+map.get("TENANT_ID")+"' ";
			BusiTools.executeDdlOnMysql(SQL,""+map.get("TENANT_ID"));
			mapper.dayVisitedInit(map);
		}
		long end = System.currentTimeMillis();
		log.info("当天统计结束总耗时——>>>>"+ (end-first)/1000.0 +"s");
		
	}

	@Override
	public void statisticConNum() {

		List<Map<String, Object>> listTenantInfo = BusiTools.getValidTenantInfo();

		long start = System.currentTimeMillis();
		// --- 设置当前租户编号 ---
		String curTenantId = (String)listTenantInfo.get(0).get("TENANT_ID");

		//获取最大id，和最小id
		HashMap<String, Object> orders = mapper.getOrders(curTenantId);

		if(null!=orders&&orders.size()>0){

			//获取minId和maxId之间不同渠道的所有活动
			List<HashMap<String, Object>> listActivity = mapper.getListActivity(orders);

			//更新到统计表中
			for(HashMap<String, Object> curActivity:listActivity){

				mapper.updateStatisSend(curActivity);

			}

			//清除临时表
			mapper.deleteOrders(orders);

			long end = System.currentTimeMillis();
			log.info("statisticConNum lose——>>>>"+ (end-start)/1000.0 +"s");
		}

	}

	public static void main(String[] args) {
		HashMap<String, Object> orders = new HashMap<String, Object>();
		orders.put("1", 1);
		orders.put("VISITED_SUCCESS",2);
		orders.put("TOTAL_SUCCESS",3);
		
		HashMap<String, Object> ss = new HashMap<String, Object>();
		ss.putAll(orders);
		ss.put("VISITED_SUCCESS",0);
		ss.put("TOTAL_SUCCESS",0);
		
		System.out.println(ss);
	}

}
