package com.bonc.busi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.model.PltActivityInfo;
import com.bonc.busi.service.OrderFailureFunction;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.service.func.CleanOrder;
import com.bonc.busi.service.mapper.CommonMapper;
import com.bonc.busi.task.base.StringUtils;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.HttpUtil;

@Service
public class OrderFailureFunctionImpl implements OrderFailureFunction {
	private final static Logger log = LoggerFactory.getLogger(OrderFailureFunctionImpl.class);

	@Autowired
	private CommonMapper CommonMapperIns;
	@Autowired
	private CleanOrder CleanOrderIns;

	@Override
	public JsonResult orderFailureByStatus(String TenantId) {
		JsonResult JsonResultIns = new JsonResult();
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-" + OrderFailureFunctionImpl.class + "-orderFailureByStatus");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_5("09");
		// --- 查询当前是否有任务运行 ---
		String runFlag = SyscommoncfgDao.query("ORDERFAILURE.RUNNING.FLAG." + TenantId);
		log.info("runFlag=" + runFlag);
		log.info("ORDERFAILURE.RUNNING.FLAG." + TenantId);
		if (StringUtils.isNotNull(runFlag) == false) {
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("参数 :ORDERFAILURE.RUNNING.FLAG." + TenantId + " 设置有误 !!!");
			SyslogDao.insert(SysLogIns);
			JsonResultIns.setCode("000102");
			JsonResultIns.setMessage("参数 :ORDERFAILURE.RUNNING.FLAG." + TenantId + " 设置有误 !!!");
			return JsonResultIns;
		}
		if (runFlag.equals("FALSE") == false) {
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("当前租户:" + TenantId + " 有正在运行的工单失效任务");
			SyslogDao.insert(SysLogIns);
			JsonResultIns.setCode("000022");
			JsonResultIns.setMessage("当前租户:" + TenantId + " 有正在运行的工单失效任务");
			return JsonResultIns;
		}

		// --- 时间到期的活动 ---
		List<PltActivityInfo> timeoutActivityList = new ArrayList<PltActivityInfo>();
		timeoutActivityList = CommonMapperIns.getInvalidActivitySeqIdByDate(TenantId);

		if (timeoutActivityList.size() != 0) {
			String timeoutActivitySql = null;
			timeoutActivitySql = getupdateStatusSql(timeoutActivityList, TenantId);
			CommonMapperIns.updateActvityInfoInvalid(timeoutActivitySql);
		}

		// --activityStatus==2--
		List<PltActivityInfo> invalidActivitys = new ArrayList<PltActivityInfo>();
		invalidActivitys = CommonMapperIns.getInvalidActivitySeqIdByStatus(TenantId);
		// --系统错误出现的活动批次--
		List<Map<String, Object>> killedActivitys = new ArrayList<Map<String, Object>>();
		killedActivitys = CommonMapperIns.getAllkilledActivitys(TenantId);
		// -是否有失效活动-
		if ((invalidActivitys == null || invalidActivitys.size() == 0)
				&& (killedActivitys == null || killedActivitys.size() == 0)) {
			log.info("无失效活动，流程结束");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("当前租户:" + TenantId + "工单失效流程结束， 无失效活动");
			SyslogDao.insert(SysLogIns);
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("当前租户:" + TenantId + "工单失效流程结束， 无失效活动");
			return JsonResultIns;
		}
		int openCount = SyscommoncfgDao.update("ORDERFAILURE.RUNNING.FLAG." + TenantId, "TRUE");
		if (openCount == 1) {
			log.info("工单失效流程开始");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("当前租户:" + TenantId + " 工单失效流程开始");
			SyslogDao.insert(SysLogIns);
		}
		// --活动失效-->工单失效--
		handleInvalidActivityOrders(invalidActivitys, TenantId, SysLogIns);

		// --系统问题-->多余工单--

		handleKilledActivityOrders(killedActivitys, TenantId, SysLogIns);
		
		int closeCount = SyscommoncfgDao.update("ORDERFAILURE.RUNNING.FLAG." + TenantId, "FALSE");
		if (closeCount == 1) {
			log.info("工单失效流程结束");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("当前租户:" + TenantId + " 工单失效流程结束");
			SyslogDao.insert(SysLogIns);
		}
		JsonResultIns.setCode("000000");
		JsonResultIns.setMessage("当前租户:" + TenantId + " 工单失效流程结束");
		return JsonResultIns;
	}

	public String getupdateStatusSql(List<PltActivityInfo> actList, String tenantId) {
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		StringBuilder sbb = new StringBuilder();
		System.out.println("timeoutActivityList=" + actList.get(0).getACTIVITY_ID() + actList.get(0).getREC_ID());
		for (PltActivityInfo act : actList) {
			sbb.append(String.valueOf(act.getREC_ID()) + ",");
		}

		whereBuilder.append("REC_ID IN (" + sbb.substring(0, sbb.length() - 1).toString() + ")");
		whereBuilder.append(" AND  TENANT_ID= " + s + tenantId + s);
		whereBuilder.append(" AND ACTIVITY_STATUS <> '2' ");
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE PLT_ACTIVITY_INFO SET ACTIVITY_STATUS = '2',ORDER_END_DATE = NOW() WHERE ");
		sb.append(whereBuilder);
		return sb.toString();
	}

	// --无用的工单移入历史--
	public void handleOrders(List<Map<String, String>> handledOrderName, String TenantId, Integer activitySeqId,
			String activityId) {
		// 工单失效移入历史
		for (Map<String, String> channelOrder : handledOrderName) {
			String channelId = channelOrder.get("CHANNEL_ID");
			String orderName = channelOrder.get("TABLE_NAME");
			// 处理
			CleanOrderIns.handleActivityOrder(TenantId, activitySeqId, orderName, channelId, activityId);

			String channels = SyscommoncfgDao.query("TASK.EXECUTE.CHANNEL"); // 8,81,82,83,D,d,5
			String channel[] = channels.split(",");
			List<String> channelList = Arrays.asList(channel);

			if (!channelList.contains(channelId)) {
				log.info("------ 该渠道在手机号索引表里没有数据  ------");
			}

			CleanOrderIns.deletePhoneIndex(TenantId, activitySeqId, channelId, activityId);

		}
		// --- 获取失效批次调用渠道接口 ---
		String invalidHandle_url = SyscommoncfgDao.query("COCHANNEL_INVALIDHANDLE_URL");
		HashMap<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("tenantId", TenantId);
		reqMap.put("activityId", activityId);
		reqMap.put("activitySeqId", activitySeqId);
		String sendPost = HttpUtil.sendPost(invalidHandle_url, JSON.toJSONString(reqMap));
		log.info(sendPost);

	}
	// --活动失效-->工单失效--

	public void handleInvalidActivityOrders(List<PltActivityInfo> invalidActivitys,String TenantId,SysLog SysLogIns) {
		if (invalidActivitys == null || invalidActivitys.size() == 0) {
			
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("活动失效-->工单失效:当前租户:" + TenantId + " 无失效活动");
			SyslogDao.insert(SysLogIns);
			log.info("--------活动失效-->工单失效:无失效活动，流程结束--------");
			return;
		}
		
		
		// // 失效活动下所有工单表
		for (PltActivityInfo invalidActivity : invalidActivitys) {
			
			String activityId = invalidActivity.getACTIVITY_ID();
			Integer activitySeqId = invalidActivity.getREC_ID();
			log.info("-----活动失效-->工单失效:一个活动流程开始,活动批次："+activitySeqId+"------");
			// 失效活动下所有工单表
			List<Map<String, String>> allInvalidOrderName = new ArrayList<Map<String, String>>();
			allInvalidOrderName = CommonMapperIns.getAllInvalidOrderName(activityId, activitySeqId, TenantId, 0);
			

			log.info("-----活动失效-->工单失效:活动批次：  "+activitySeqId+"  获取的工单表名：" + allInvalidOrderName);
			if (allInvalidOrderName == null || allInvalidOrderName.size() == 0) {
				log.info("-----活动失效-->工单失效:该失效活动:活动批次：  "+activitySeqId+" 下无工单，该活动流程结束！-------");
				continue;
			}

			/*for (int i = 0; i < allInvalidOrderName.size(); i++) {
				Map<String, String> channelOrder = allInvalidOrderName.get(i);

				// --短信渠道--
				if (channelOrder.get("CHANNEL_ID").trim().equals("7")) {
					// 有短信渠道
					// --执行完毕的短信渠道--
					int smsCount = CommonMapperIns.getChannelExecute(activityId, activitySeqId, TenantId, "1",
							channelOrder.get("CHANNEL_ID"));

					if (smsCount != 0) {
						// 有短信渠道而无执行完毕的短信，去除短信渠道
						allInvalidOrderName.remove(i);
						CommonMapperIns.updateRESERVE1ForSMS(activitySeqId, TenantId);
					}
				}
			}
			if (allInvalidOrderName == null || allInvalidOrderName.size() == 0) {
				log.info("-----活动失效-->工单失效:该失效活动下无工单，该活动流程结束！------");
				continue;
			}*/
			
			// 工单失效移入历史
			handleOrders(allInvalidOrderName, TenantId, activitySeqId, activityId);
		
			/*
			 * for(Map<String, String> channelOrder : allInvalidOrderName){
			 * String channelId = channelOrder.get("CHANNEL_ID"); String
			 * orderName = channelOrder.get("TABLE_NAME"); //处理
			 * CleanOrderIns.handleActivityOrder(TenantId,activitySeqId,
			 * orderName,channelId,activityId);
			 * 
			 * String channels = SyscommoncfgDao.query("TASK.EXECUTE.CHANNEL");
			 * //8,81,82,83,D,d,5 String channel[] = channels.split(",");
			 * List<String> channelList = Arrays.asList(channel);
			 * 
			 * if(!channelList.contains(channelId)){ log.info(
			 * "------ 该渠道在手机号索引表里没有数据  ------"); }
			 * 
			 * CleanOrderIns.deletePhoneIndex(TenantId,activitySeqId,channelId,
			 * activityId);
			 * 
			 * } // --- 获取失效批次调用渠道接口 --- String invalidHandle_url =
			 * SyscommoncfgDao.query("COCHANNEL_INVALIDHANDLE_URL");
			 * HashMap<String, Object> reqMap = new HashMap<String, Object>();
			 * reqMap.put("tenantId", TenantId); reqMap.put("activityId",
			 * activityId); reqMap.put("activitySeqId", activitySeqId); String
			 * sendPost = HttpUtil.sendPost(invalidHandle_url,
			 * JSON.toJSONString(reqMap)); log.info(sendPost);
			 */
			log.info("-----活动失效-->工单失效:一个活动:活动批次：  "+activitySeqId+" 流程结束---------");
		}
		// 更新所有的RESERVE1 = 1
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < invalidActivitys.size(); i++) {
			sb.append(invalidActivitys.get(i).getREC_ID());
			sb.append(",");
		}
		String activityList = sb.substring(0, sb.length() - 1).toString();
		int  invalidActivitysCount  = CommonMapperIns.updateRESERVE1ForActivity(activityList, TenantId);
		//--为处理后的数据打上标识--
		int  count = CommonMapperIns.updateAllInvalidActivitysOrderName(activityList, TenantId);
		log.info("-----活动失效-->工单失效:所有失效活动,活动数量："+invalidActivitysCount+" 流程结束---------");
	}

	// --系统问题-->多余工单--
	public void handleKilledActivityOrders(List<Map<String, Object>> killedActivitys,String TenantId,SysLog SysLogIns) {
		// --系统错误导致的多余工单--
		if (killedActivitys == null || killedActivitys.size() == 0) {
			log.info(" --系统问题-->多余工单--无失效活动，流程结束-----");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("--系统问题-->多余工单--当前租户:" + TenantId + " 无失效活动");
			SyslogDao.insert(SysLogIns);
			return;
		}
		StringBuilder killedActivitySeqIds = new StringBuilder();
		
				for (Map<String, Object> killedActivity : killedActivitys) {
					
					String activityId = (String) killedActivity.get("activityId");
					Integer activitySeqId = (Integer) killedActivity.get("activitySeqId");
					log.info("-----------系统问题-->多余工单--一次活动:活动批次：  "+activitySeqId+" 流程开始---------");
					// 失效活动下所有工单表
					List<Map<String, String>> allKilledOrderName = new ArrayList<Map<String, String>>();
					allKilledOrderName = CommonMapperIns.getAllInvalidOrderName(activityId, activitySeqId, TenantId, 0);
					// 重跑活动后遗留下的工单数据工单表

					log.info("该活动获取的工单表名：" + allKilledOrderName);
					if (allKilledOrderName == null || allKilledOrderName.size() == 0) {
						log.info("--系统问题-->多余工单--该失效活动:活动批次：  "+activitySeqId+" 下无工单，该活动流程结束！");
						continue;
					}

					handleOrders(allKilledOrderName, TenantId, activitySeqId, activityId);

					log.info("-------系统问题-->多余工单--一个活动:活动批次：  "+activitySeqId+" 流程结束---------");
					killedActivitySeqIds.append(String.valueOf(activitySeqId));
					killedActivitySeqIds.append(",");
				}
				//--为处理后的数据打上标识--
				log.info("-------系统问题-->多余工单--所有活动活动:活动批次：  "+killedActivitySeqIds.substring(0, killedActivitySeqIds.length()-1)+" 流程结束---------");
				int  count = CommonMapperIns.updateAllkilledActivitys(killedActivitySeqIds.substring(0, killedActivitySeqIds.length()-1), TenantId);
				
	
	}

}
