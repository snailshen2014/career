package com.bonc.busi.task.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.task.base.ParallelFunc;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.ScenePowerStatus;
import com.bonc.busi.task.mapper.SceneMapper;
import com.bonc.utils.DateUtil.DateFomart;

public class ScenePowerAnalyse extends ParallelFunc {
	private final static Logger logger = LoggerFactory.getLogger(ScenePowerAnalyse.class);
	private static HashMap<String, String> activity_Name_cache = new HashMap<String,String>(); 
	private final String TENANT_ID = "uni076";
	private SceneMapper sceneMapper = SpringUtil.getBean(SceneMapper.class);
	private Integer lastBatchId;

	@Override
	public Object get() {
		while (true) {
			try {
//				Thread.sleep(30 * 1000);
				Thread.sleep(600 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Integer batch = sceneMapper.getInsertFinishedFirstBatch(TENANT_ID);
			if (this.lastBatchId == batch ) {
				continue;
			}
			this.lastBatchId = batch;
			// TODO Auto-generated method stub
			return batch;
		}
	}

	@Override
	public int handle(Object data) {
		Integer batchId = (Integer) data;
		long start = System.currentTimeMillis();
		ScenePowerStatus scenePowerStatus = new ScenePowerStatus();
		scenePowerStatus.setBatchId(batchId);
		scenePowerStatus.setTenantId(TENANT_ID);
		scenePowerStatus.setStatus("2");
		sceneMapper.updateScenePowerStatus(scenePowerStatus);
		HashMap<String,Object> resultMap = dealAnalyse(batchId);
		// 2.更新状态和数值
		long end  = System.currentTimeMillis();
		scenePowerStatus.setEndDate(new Date());
		scenePowerStatus.setResultData(JSON.toJSONString(resultMap.get("resultIdNumsMap")));
		scenePowerStatus.setConsumeTimeDetail(JSON.toJSONString(resultMap.get("resultActIdTimeMap")));
		scenePowerStatus.setStatus("3");
		scenePowerStatus.setConsumeTime((end-start)/1000 + "s");
		sceneMapper.updateScenePowerStatus(scenePowerStatus);
		logger.info("[Ordertask-ScenePower] ----- dealAnalyse success : batchId ==" +batchId + "     消耗时间为 ： "+ (end-start)/1000 +"s");
		return 0;
	}

	private HashMap<String,Object> dealAnalyse(Integer batchId) {
		HashMap<String, Object> result = new HashMap<String,Object>();
		// 1.查询一个月中有效的活动批次 
		List<HashMap<String, Integer>> activityMaps =  sceneMapper.getActivityProcessMap(TENANT_ID);
		
		HashMap<String, Object> tableMap = initDbTableMap();
		HashMap<String, Integer> seqIdNumsMap = new HashMap<String, Integer>();
		HashMap<String, Integer> seqIdOrderNumsMap = new HashMap<String, Integer>();
		HashMap<String, Integer> seqIdComsumeTimeMap = new HashMap<String, Integer>();
		HashMap<String, Object> resultActIdTimeMap = new HashMap<String, Object>();

//		for (String table : tableList) {
//			for (HashMap<String, Integer> activityMap : activityMaps) {
//				String activitySeqId = String.valueOf(activityMap.get("ACTIVITY_SEQ_ID"));
//				Integer seqNums = sceneMapper.getCountFromSeqId(table, activitySeqId,TENANT_ID,batchId);
//				if (null != seqIdNumsMap.get(activitySeqId)) {
//					seqIdNumsMap.put(activitySeqId, Integer.valueOf(seqIdNumsMap.get(activitySeqId)) + seqNums);
//				} else {
//					seqIdNumsMap.put(activitySeqId, seqNums);
//				}
//			}
//		}
 		for (HashMap<String, Integer> activityMap : activityMaps) {
			String activitySeqId = String.valueOf(activityMap.get("ACTIVITY_SEQ_ID"));
			//查询活动批次中有的渠道id集合
			List<String> channelIdList = sceneMapper.queryChannelIdBySeqId(activitySeqId,TENANT_ID);
			//查询活动各渠道数据总量
			Integer channelOrderNum =  sceneMapper.queryOrderNumBySeqId(activitySeqId,TENANT_ID);
			//记录各渠道处理开始时间
			long channelBeginTime = System.currentTimeMillis();
			for(String channelId : channelIdList){
				//单独处理一下短信的历史表
				if ("7".equals(channelId)) {
					Calendar cal = Calendar.getInstance();
					int month=cal.get(Calendar.MONTH)+1;
					String monthNum  = String.format("%02d",month); // 1->01 10->10
					String table = "PLT_ORDER_INFO_SMS_HIS_"+monthNum;
					Integer seqNums = sceneMapper.getCountFromSeqId(table, activitySeqId,TENANT_ID,batchId);
					if (null != seqIdNumsMap.get(activitySeqId)) {
						seqIdNumsMap.put(activitySeqId, Integer.valueOf(seqIdNumsMap.get(activitySeqId)) + seqNums);
					} else {
						seqIdNumsMap.put(activitySeqId, seqNums);
					}
				}
				String table = (String) tableMap.get(channelId);
				Integer seqNums = sceneMapper.getCountFromSeqId(table, activitySeqId,TENANT_ID,batchId);
				if (null != seqIdNumsMap.get(activitySeqId)) {
					seqIdNumsMap.put(activitySeqId, Integer.valueOf(seqIdNumsMap.get(activitySeqId)) + seqNums);
				} else {
					seqIdNumsMap.put(activitySeqId, seqNums);
				}
			}
			//记录各渠道处理结束时间
			long channelEndTime = System.currentTimeMillis();
			Integer channelConsumeTime = Integer.parseInt(String.valueOf(channelEndTime - channelBeginTime));
			seqIdComsumeTimeMap.put(activitySeqId, channelConsumeTime);
			seqIdOrderNumsMap.put(activitySeqId, channelOrderNum);
			logger.info("活动批次id"+activityMap+"--------产生工单数为:"+channelOrderNum + "--------耗时为(毫秒):" + channelConsumeTime );
		}
 		//拼接活动对应map 和返回结果
		HashMap<String, Integer> resultIdNumsMap = jointResultMap(activityMaps, seqIdNumsMap);
		resultIdNumsMap = addActivityName(resultIdNumsMap);
		//拼接活动对应map 和各渠道统计结果
		HashMap<String, Integer> resultOrderNumsMap = jointResultMap(activityMaps, seqIdOrderNumsMap);
		//拼接活动对应map 和各渠道耗时
		HashMap<String, Integer> resultComsumeTimeMap = jointResultMap(activityMaps, seqIdComsumeTimeMap);
//		logger.info("各个活动工单数综合为---"+resultOrderNumsMap);
//		logger.info("各个活动消耗时间为---"+resultComsumeTimeMap);
		//拼接两个map key值相同的value
		resultActIdTimeMap = joinValuesBySameKey(resultOrderNumsMap,resultComsumeTimeMap);
		logger.info("各个活动统计结果为---"+resultActIdTimeMap);
		result.put("resultIdNumsMap", resultIdNumsMap);
		result.put("resultActIdTimeMap", resultActIdTimeMap);
		return result;

	}

	

	
	/*
	 * // 初始化渠道id和对应表map关系
	 */
	private HashMap<String, Object>  initDbTableMap() {
		HashMap<String, Object> tableMap = new HashMap<String, Object>();
		tableMap.put("1", "PLT_ORDER_INFO_ONE");
		tableMap.put("2", "PLT_ORDER_INFO_ONE");
		tableMap.put("9", "PLT_ORDER_INFO_ONE");
		tableMap.put("5", "PLT_ORDER_INFO");
		tableMap.put("11", "PLT_ORDER_INFO_WEIXIN");
		tableMap.put("8", "PLT_ORDER_INFO_POPWIN");
		tableMap.put("81", "PLT_ORDER_INFO_POPWIN");
		tableMap.put("82", "PLT_ORDER_INFO_POPWIN");
		tableMap.put("83", "PLT_ORDER_INFO_POPWIN");
		tableMap.put("7", "PLT_ORDER_INFO_SMS");
		tableMap.put("14", "PLT_ORDER_INFO_CALL");
		tableMap.put("13", "PLT_ORDER_INFO_SMALLWO");
		tableMap.put("19", "PLT_ORDER_INFO_PLAY");
		
		
		return tableMap;
	}

	// 根据出库listmap 和 计数map 得出最后统计结果
	private static HashMap<String, Integer> jointResultMap(List<HashMap<String, Integer>> activityMaps,
			HashMap<String, Integer> seqIdNumsMap) {
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
		HashMap<String, Set<Integer>> tempMap = new HashMap<String, Set<Integer>>();
		Set<Integer> set = new HashSet<Integer>();
		for (HashMap<String, Integer> activityMap : activityMaps) {
			String activityId = activityMap.get("ACTIVITY_ID")+"";
			Integer seqId =  Integer.parseInt(String.valueOf(activityMap.get("ACTIVITY_SEQ_ID")));
			if (null != tempMap.get(activityId)) {
				set.add(seqId);
			} else {
				set = new HashSet<Integer>();
				set.add(seqId);
			}
			tempMap.put(activityId, set);
		}
		for (Entry<String, Set<Integer>> entry : tempMap.entrySet()) {
			Integer num = 0;
			String activityId = entry.getKey();
			Set<Integer> seqSet = entry.getValue();
			for (Integer seqId : seqSet) {
				for(Entry<String, Integer> seqIdNums : seqIdNumsMap.entrySet()){
					if (seqIdNums.getKey().equals(seqId+"")) {
						num += seqIdNums.getValue();
					}
				}
			}
			resultMap.put(activityId, num);
//			System.out.println("key= " + activityId + " and value= " + num);
		}

		return resultMap;
	}

	private HashMap<String, Integer> addActivityName(HashMap<String, Integer> map) {
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
		//把map中的key值拼接上活动名称
		for(String activityId : map.keySet()){
			String activityName = activity_Name_cache.get(activityId);
			if (null == activityName) {
				activityName = sceneMapper.getActivityName(TENANT_ID,activityId);
				activity_Name_cache.put(activityId, activityName);
			}
			resultMap.put(activityName +"!#" +activityId, map.get(activityId));
		}
		return resultMap;
	}
	
	
	private HashMap<String, Object> joinValuesBySameKey(HashMap<String, Integer> resultOrderNumsMap,
			HashMap<String, Integer> resultComsumeTimeMap) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		//把map中的相同key值拼接上value
		for(String activityId : resultOrderNumsMap.keySet()){
			resultMap.put(activityId , resultOrderNumsMap.get(activityId)+"#"+resultComsumeTimeMap.get(activityId));
		}
		return resultMap;
	}

	
//	public static void main(String[] args) {
//		List<HashMap<String, Integer>> list = new ArrayList<>();
//		HashMap<String, Integer> map = new HashMap<String, Integer>();
//		map.put("ACTIVITY_ID", 116944);
//		map.put("ACTIVITY_SEQ_ID", 580032);
//		list.add(map);
//		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
//		map2.put("ACTIVITY_ID", 116944);
//		map2.put("ACTIVITY_SEQ_ID", 680032);
//		list.add(map2);
//		HashMap<String, Integer> map3 = new HashMap<String, Integer>();
//		map3.put("ACTIVITY_ID", 216944);
//		map3.put("ACTIVITY_SEQ_ID", 780032);
//		list.add(map3);
//
//		HashMap<String, Integer> seqIdNumsMap = new HashMap<String, Integer>();
//		seqIdNumsMap.put("580032", 1600);
//		seqIdNumsMap.put("680032", 1800);
//		seqIdNumsMap.put("780032", 1000);
//		HashMap<String, Integer> jointResultMap = ScenePowerAnalyse.jointResultMap(list, seqIdNumsMap);
//		System.out.println(jointResultMap);
//	}
	
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		int month=cal.get(Calendar.MONTH)+1;
		String monthNum  = String.format("%02d",month); // 1->01 10->10
		System.out.println(monthNum);
	}

}
