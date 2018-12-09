package com.bonc.busi.outer.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bonc.busi.outer.mapper.ActivityTrackMapper;
import com.bonc.busi.outer.service.ActivityTrackService;


@Service("ActivityTrackService")
public class ActivityTrackServiceImpl implements ActivityTrackService{

	@Autowired
	private ActivityTrackMapper activityTrackMapper;
	
	private static final Logger log = Logger.getLogger(ActivityTrackServiceImpl.class);
	//1
	
	@Override
	public Object getActvityStatistic(HashMap<String, Object> req) {
		
		HashMap<String, Object> item = new HashMap<String,Object>();
		
		//1.1查询当前活动下工单信息总量
		long start = System.currentTimeMillis();
		item = activityTrackMapper.getStatisticAll(req);
		long end = System.currentTimeMillis();
		log.info("actvitystatistic--->工单信息总量sql-->耗时"+(end-start)/1000.0+"s");
		
		log.info("actvitystatistic--->返回参数——>"+item);
		return item;
	}

	//2
	@Override
	public Object getChannelStatistic(HashMap<String, Object> req) {
		
		//分渠道总计
		long start = System.currentTimeMillis();
		List<HashMap<String, Object>> items = activityTrackMapper.getChannelStatistic(req);
		long end = System.currentTimeMillis();
		log.info("channelstatistic--->各渠道下总共单量和留存数sql-->耗时"+(end-start)/1000.0+"s");
		
		for(HashMap<String, Object> item:items){
			req.put("channelId", item.get("CHANNEL_ID"));
			
		    start = System.currentTimeMillis();
			//查询无效
			HashMap<String, Object> unValidNum = activityTrackMapper.getUnValidNum(req);			
			//查询有效
			HashMap<String, Object> validNum = activityTrackMapper.getValidNum(req);
			end = System.currentTimeMillis();
			log.info("channelstatistic--->各渠道下无效，有效下工单接触总量sql-->耗时"+(end-start)/1000.0+"s");
			
			item.putAll(unValidNum);
			item.putAll(validNum);
		}
		
		log.info("channelstatistic--->返回参数——>"+items);
		return items;
	}

	//3 更新历史
	@Override
	public Object getOrderhistory(HashMap<String, Object> req) {
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		List<HashMap<String, Object>> items = activityTrackMapper.getOrderhistory(req);
		long end = System.currentTimeMillis();
		log.info("orderhistory--->更新历史sql-->耗时"+(end-start)/1000.0+"s");
		
		for(HashMap<String, Object> item:items){
			 
			// 工单起始更新    数量
			if("1".equals(item.get("ORDER_MARK"))){
				
				//  查询更新后数量     工单新增
				// ---------------- 此批次未失效    更新数量则为当前批次新增数量加上以前未失效批次有效工单数量 ----------------			    
			    	start = System.currentTimeMillis();
			    	HashMap<String, Object> upadteCount = activityTrackMapper.getUpadteCount(item);	
			    	end = System.currentTimeMillis();
					log.info("orderhistory--->工单新增，更新后数量sql-->耗时"+(end-start)/1000.0+"s");
					if(upadteCount!=null){
						item.put("VALID_NUM", upadteCount.get("VALID_NUM"));
					}
					
			}else{
				//查询更新后数量     工单剔除
				start = System.currentTimeMillis();
				HashMap<String, Object> upadteCount = activityTrackMapper.getDeleteUpadteCount(item);
				end = System.currentTimeMillis();
				log.info("orderhistory--->工单剔除，更新后数量sql-->耗时"+(end-start)/1000.0+"s");
				if(upadteCount!=null){
					item.put("VALID_NUM", Integer.parseInt(String.valueOf(upadteCount.get("VALID_NUM")))-Integer.parseInt(String.valueOf(item.get("UPDATE_NUM"))));
				}
			}
			
				
		}			
		resp.put("total", items.size());
		resp.put("items",items);
		
		log.info("orderhistory--->返回参数——>"+resp);
		return resp;
	}

	@Override
	public Object getUpdateRecord(HashMap<String, Object> req) {
		
		//得到活动的开始时间和结束时间
		HashMap<String, Object> orderDealMonth =  activityTrackMapper.getOrderDealMonth(req);
		
		HashMap<String, Object> item = new HashMap<String, Object>();

		//活动为有效，则需要看上期工单是否有剩余
		if(req.get("orderDate").equals(orderDealMonth.get("ORDER_DATE"))){
			
			//4.1
			long start = System.currentTimeMillis();
			item = activityTrackMapper.getUpdateRecord(req);
			long end = System.currentTimeMillis();
			log.info("updaterecord--->工单增减情况记录sql-->耗时"+(end-start)/1000.0+"s");
			
			//4.2   上期剩余工单数
			start = System.currentTimeMillis();
			HashMap<String, Object> lastOrderCount = activityTrackMapper.getLastOrderCount(req);
			end = System.currentTimeMillis();
			log.info("updaterecord--->工单上期剩余工单数sql-->耗时"+(end-start)/1000.0+"s");
			
			if(lastOrderCount!=null){
				item.put("LAST_COUNT", lastOrderCount.get("UN_CONTACT_COUNT"));
				item.put("VALID_NUM", Integer.parseInt(String.valueOf(lastOrderCount.get("UN_CONTACT_COUNT")))+Integer.parseInt(String.valueOf(item.get("VALID_NUM"))));
			}else {
				item.put("LAST_COUNT", 0);
			}
			if(null == item.get("TARGET_USER")){  //本期目标客户
				item.put("TARGET_USER",0);
			}
			if(null == item.get("RANGE_COUNT")){  //适用范围过滤
				item.put("RANGE_COUNT",0);
			}
			if(null == item.get("CHANNEL_COUNT")){  //渠道协同过滤
				item.put("CHANNEL_COUNT",0);
			}
			if(null == item.get("CHANNEL_SELECT_COUNT")){  //渠道筛选
				item.put("CHANNEL_SELECT_COUNT",0);
			}
			if(null == item.get("DISTINCT_COUNT")){  //剔除重复工单
				item.put("DISTINCT_COUNT",0);
			}
			item.put("ORDER_MARK","1");
			//获取第一个批次号
			HashMap<String, Object> firstId = activityTrackMapper.getFirstId(req);
			String channelId = (String) req.get("channelId");
			//获取留存数
			firstId.put("channelId", channelId);
			HashMap<String, Object> reserveCount = activityTrackMapper.getReserveCount(firstId);
			
			if(String.valueOf(req.get("activitySeqId")).equals(String.valueOf(firstId.get("REC_ID")))){
				item.put("FILTER4_COUNT", 0); //留存过滤数	
			}
			if(reserveCount != null) {
			  item.put("RESERVE1", Integer.parseInt(String.valueOf(reserveCount.get("SAVE_NUMBER")))); //留存工单数
			}
		}else {
			long start = System.currentTimeMillis();
			item = activityTrackMapper.getUpdateHistoryRecord(req);
			HashMap<String, Object> allOrderCount = activityTrackMapper.getAllOrderCount(req);
			long end = System.currentTimeMillis();
			log.info("updaterecord--->超期剔除sql-->耗时"+(end-start)/1000.0+"s");
			//  剔除前的工单总量
			if(allOrderCount!=null){
				item.put("VALID_NUM", Integer.parseInt(String.valueOf(allOrderCount.get("VALID_NUM")))+Integer.parseInt(String.valueOf(item.get("VALID_NUM"))));

			}

			item.put("ORDER_MARK","0");
		}
		
		log.info("updaterecord--->返回参数——>"+item);
		return item;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<HashMap<String,Object>> allocation(HashMap<String, Object> dataMap) {
		List<HashMap> request = (List<HashMap>) dataMap.get("items");
		//List<HashMap> request = JSONObject.parseArray(dataMap.get("items").toString(), HashMap.class);
		//接收参数信息，并整合信息
		String activityIds = "";//活动id
		String orgpath = "";//活动组织路径
		String orgParentPath = dataMap.get("orgParentPath").toString();//活动组织路径
		String tenantId = dataMap.get("tenantId").toString();//活动组织路径
		int i=1;
		for (HashMap hashMap : request) {
			String[] activityArray = hashMap.get("activityIds").toString().split(",");
			if(i==request.size()){
				for(int j=0;j<activityArray.length;j++){
					if(j==activityArray.length-1){
						activityIds+= "'"+activityArray[j]+"'";
					}else{
						activityIds+= "'"+activityArray[j]+"'"+",";
					}
				}
				orgpath+="s.ORG_PATH LIKE "+ "'"+hashMap.get("orgpath")+"%'";
			}else{
				for(int j=0;j<activityArray.length;j++){
					activityIds+= "'"+activityArray[j]+"'"+",";
				}
				orgpath+="s.ORG_PATH LIKE "+ "'"+hashMap.get("orgpath")+"%'"+" OR ";
				i++;
			}
		}
		List<HashMap<String,Object>> results = activityTrackMapper.allocation(tenantId,activityIds,orgParentPath,orgpath);
		//整理返回数据
		//统计orgerPath并分组
		List<Object> orgerPath = new ArrayList<Object>();
		//最终返回的集合
		List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> hashMap : results) {
			List<HashMap<String, Object>> activityIdToNum = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> activityIdToNumMap = new HashMap<String, Object>();
			HashMap<String, Object> cityMap = new HashMap<String, Object>();
			if(!orgerPath.contains(hashMap.get("ORGPATH"))){
				orgerPath.add(hashMap.get("ORGPATH"));
				activityIdToNumMap.put("activityId", hashMap.get("ACTIVITY_ID"));
				activityIdToNumMap.put("NUM", hashMap.get("NUM"));
				activityIdToNum.add(activityIdToNumMap);
				cityMap.put("orgid",hashMap.get("ORGPATH").toString().substring(hashMap.get("ORGPATH").toString().lastIndexOf("/")+1));
				cityMap.put("orgerPath",hashMap.get("ORGPATH"));
				cityMap.put("activityIdToNum",activityIdToNum);
				resultList.add(cityMap);
			}else{
				List<HashMap<String,Object>> activityIdToNumes = null;
				for(int k=0;k<resultList.size();k++){
					if(resultList.get(k).get("orgerPath") != null && !resultList.get(k).get("orgerPath").toString().equals("") &&resultList.get(k).get("orgerPath").equals(hashMap.get("ORGPATH"))){
						Object activityIdToNums =resultList.get(k).get("activityIdToNum");
						activityIdToNumes =(List<HashMap<String, Object>>) activityIdToNums;
						activityIdToNumMap.put("activityId", hashMap.get("ACTIVITY_ID"));
						activityIdToNumMap.put("NUM", hashMap.get("NUM"));
						activityIdToNumes.add(activityIdToNumMap);
					}
				}
			}
		}
		//System.out.println(resultList.size());
		return resultList;
		
	}

	
}
