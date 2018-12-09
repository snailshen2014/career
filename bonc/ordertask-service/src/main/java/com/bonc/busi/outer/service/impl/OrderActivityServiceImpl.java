package com.bonc.busi.outer.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.config.SystemCommonConfigManager;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.outer.bo.ActivityChannelExecute;
import com.bonc.busi.outer.bo.ActivityProcessLog;
import com.bonc.busi.outer.bo.OrderTableUsingInfo;
import com.bonc.busi.outer.bo.OrderTablesAssignRecord4S;
import com.bonc.busi.outer.bo.PltActivityInfo;
import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.StringUtil;

@Service("OrderActivityService")
public class OrderActivityServiceImpl  implements OrderActivityService{
	private final static Logger log = LoggerFactory.getLogger(OrderActivityServiceImpl.class);

	@Autowired
	PltActivityInfoDao		PltActivityInfoDaoIns;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private BusiTools BusiToolsIns;
	
	@Override
	public List<Integer> getActivitySEQIdsById(String activityId,String tenantId) {
		return PltActivityInfoDaoIns.getActivitySEQIdsById(activityId,tenantId);
	}
	
	@Override
	public Integer getLatestActivitySEQIdById(String activityId,String tenantId) {
		return PltActivityInfoDaoIns.getLatestActivitySEQId(activityId,tenantId);
	}
	
	@Override
	public List<Map<String,Integer>> getOrderCount(ActivityProcessLog processLog) {
		return PltActivityInfoDaoIns.getOrderCount(processLog);
	}
	
	
	@Override
	public String getOrderTableName(OrderTablesAssignRecord4S orderTable) {
		return PltActivityInfoDaoIns.getOrderTableName(orderTable);
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.bonc.busi.outer.service.OrderActivityService#getOrderChannelListByActivityIdAndSeqId(java.lang.String, int)
	 */
	@Override
	public List<String> getOrderChannelListByActivityIdAndSeqId(String activityId, int activitySeqId,String tenantId) {
		return PltActivityInfoDaoIns.getOrderChannelListByActivityIdAndSeqId(activityId,activitySeqId,tenantId);
	}

	/**
	 * 根据活动Id 租户Id查询所有的无效的活动批次
	 */
	@Override
	public List<Integer> getActivityInvalidSEQIdsById(String activityId, String tenantId) {
		return PltActivityInfoDaoIns.getActivityInvalidSEQIdsById(activityId, tenantId);
	}

	/**
	 * 根据手机号、渠道查询工单表名
	 */
	@Override
	public List<String> getOrderTableNamesByPhoneAndChannelId(RequestParamMap paramMap) {
		List<String> tableNameList = new ArrayList<String>();
		//String channelId = paramMap.getChannelId();
		//根据渠道Id从PLT_ORDER_TABLES_ASSIGN_RECORD_INFO查询目前所有的工单表名
		paramMap.setBusiType(0);
		//由于渠道8有子渠道81 82  83，通过 路由表查询时只根据8进行查询
//		if(paramMap.getChannelId().indexOf('8')!= -1) {
//			paramMap.setChannelId(String.valueOf(paramMap.getChannelId().charAt(0)));
//		}
		List<String> tables = PltActivityInfoDaoIns.getAllOrderTableName(paramMap);
		//遍历指定渠道下的所有工单，如果某个工单表中有满足查询条件的工单，就把这个工单表名添加到集合里
		//paramMap.setChannelId(channelId);
		for(String tableName : tables) {
			if(tableName != null) {
			paramMap.setTableName(tableName);
			int count = PltActivityInfoDaoIns.selectOrderCountFromTableByCondition(paramMap);
			if(count !=0) {
				tableNameList.add(tableName);
			}
		  }
		}
		return tableNameList;
	}


	/*
	 * 更新活动状态
	 */
	@Override
	public JsonResult setActivityStatus(ActivityStatus reqdata){
		JsonResult	JsonResultIns = new JsonResult();

		// --- 查询活动是否存在 ---
		final List<PltActivityInfo>		listPltActivityInfo =
				//	PltActivityInfo		PltActivityInfoIns =
				PltActivityInfoDaoIns.retrievePltActivityInfoByActivityId(reqdata.getActivityId(),
						reqdata.getTenant_id());
		if(listPltActivityInfo == null || listPltActivityInfo.size() == 0){
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("活动不存在 !!!");
			return JsonResultIns;
		}
		// --- 改为取最新的活动状态 ----
		PltActivityInfo	PltActivityInfoIns = listPltActivityInfo.get(listPltActivityInfo.size()-1);

		int     inActivityStatus =  Integer.parseInt(reqdata.getActivityStatus());
		// --- 判断输入 的活动状态和库中的状态是否相等 ---
		if(PltActivityInfoIns.getACTIVITY_STATUS() != inActivityStatus){
			if(PltActivityInfoIns.getACTIVITY_STATUS()  == 2){  // ---已经失效了是否就不允许修改了?
				JsonResultIns.setCode("2");
				JsonResultIns.setMessage("活动已经失效 !!!");
				return JsonResultIns;
			}
			if(inActivityStatus == 2){   // --- 活动失效  ---
				PltActivityInfoIns.setACTIVITY_STATUS(inActivityStatus);
				PltActivityInfoIns.setEND_DATE(new Date());
				PltActivityInfoDaoIns.expirePltActivityInfo(PltActivityInfoIns);
//				// --- 启动新线程去执行工单的更改（暂时先不执行 )  ----
//				ExecutorService pool = Global.getExecutorService();
//				log.info(" 启动新线程执行");
//				pool.execute(new Runnable() {
//								 public void run() {
//									 for(PltActivityInfo item:listPltActivityInfo){
//										 expireActivityHandle(item.getTENANT_ID(),item.getREC_ID(),item.getACTIVITY_ID());
//									 }
//								 }
//							 }
//				);

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
	//private	void		expireActivityHandle(List<PltActivityInfo>		listPltActivityInfo){
	private	void		expireActivityHandle(String tenantId,int  activitySeqId,String activityId){
		StringBuilder			sb = new StringBuilder();
		String		strTenantId =tenantId;
		int			iActivitySeqId =activitySeqId;
		// --- 表拆分，一分四，修改 ---

		String		strMove = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.MOVE");
		String		strDelete = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.DELETE");
		String		strUpdate = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.UPDATE");
		String		strTableName = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.TABLE");
		String		strTmpMove = strMove.replaceFirst("TTTTTNENAT_ID", strTenantId);
		String		strTmpDelete = strDelete.replaceFirst("TTTTTNENAT_ID", strTenantId);
		String		strTmpUpdate = strUpdate.replaceFirst("TTTTTNENAT_ID", strTenantId);
		String		strLastMove = strTmpMove.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(iActivitySeqId));
		String		strLastDelete = strTmpDelete.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));
		String		strLastUpdate = strTmpUpdate.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));

		String		strTableItem[] = strTableName.split(",");

		for(String rec:strTableItem){
			String		strLocalUpdate = strLastUpdate.replaceAll("TTTTTABLENAME", rec);
			String		strLocalMove = strLastMove.replaceAll("TTTTTABLENAME", rec);
			String		strLocalDelete = strLastDelete.replaceAll("TTTTTABLENAME", rec);

			sb.setLength(0);
			sb.append("SELECT MAX(REC_ID) AS MAXID,MIN(REC_ID) AS MINID  FROM ");
			sb.append(rec);
			sb.append(" WHERE ACTIVITY_SEQ_ID = ");
			sb.append(iActivitySeqId);
			sb.append("  AND TENANT_ID ='");
			sb.append(tenantId);
			sb.append("'");
			log.info("sql={}",sb.toString());
			Map<String,Object>  mapResult = jdbcTemplate.queryForMap(sb.toString());
			if(mapResult == null ) continue;

			long			lMinRec = 0;
			long			lMaxRec =0;
			try{
				lMinRec = (Long)mapResult.get("MINID");
				lMaxRec = (Long)mapResult.get("MAXID");
			}catch(Exception e){  // --- 捕获空指针 ---
				continue;
			}
			log.info("sql={},table ={},min rec ={},max rec={},={}",sb.toString(),rec,lMinRec,lMaxRec,mapResult.toString());
			long			lBeginRec = 0L;
			long			lEndRec = 0L;
			long			lRound = (lMaxRec-lMinRec)/10000L;
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
				log.info("更新工单时间:{} ,工单序列号:{},数量:{}",rec,iActivitySeqId,result);
				sb.setLength(0);
				sb.append(strLocalMove);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("工单移入历史sql = {}",sb.toString());
				result = jdbcTemplate.update(sb.toString());
				log.info("工单移入历史:{} ,工单序列号:{},数量:{}",rec,iActivitySeqId,result);
				sb.setLength(0);
				sb.append(strLocalDelete);
				sb.append(" AND REC_ID >= ");
				sb.append(lBeginRec);
				sb.append(" AND REC_ID <= ");
				sb.append(lEndRec);
				log.info("删除工单sql = {}",sb.toString());
				result = jdbcTemplate.update(sb.toString());
				log.info("删除工单:{} ,工单序列号:{},数量:{}",rec,iActivitySeqId,result);
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
		List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		if(!StringUtil.validateStr(req.get("envType"))){
			req.put("envTypeAnd", " AND BUSINESS_RESERVE1 = #{envType} ");
		}
		if(!StringUtil.validateStr(req.get("eventId"))){
			req.put("eventIdAnd", " AND BUSINESS_RESERVE2 = #{eventId} ");
		}
		if(!StringUtil.validateStr(req.get("channelId"))){
			req.put("channelIdAnd", " AND CHANNEL_ID = #{channelId} ");
		}

		//2.0获取表名
		Integer activitySeqId =  PltActivityInfoDaoIns.selectActivitySeqid(req);
		OrderTablesAssignRecord4S orderTablesAssignRecord4S = new OrderTablesAssignRecord4S();
		orderTablesAssignRecord4S.setActivityId((String) req.get("activityId"));
		orderTablesAssignRecord4S.setChannelId((String) req.get("channelId"));
		orderTablesAssignRecord4S.setActivitySeqId(activitySeqId);
		orderTablesAssignRecord4S.setTenantId((String) req.get("tenantId"));
		orderTablesAssignRecord4S.setBusiType(3);
		String orderTableName = PltActivityInfoDaoIns.getOrderTableName(orderTablesAssignRecord4S);
		if (null == orderTableName){
//			log.info("--------------场景营销路由表名为null----------------");
//			BoncExpection boncExpection = new BoncExpection();
//			boncExpection.setMsg("路由表名为null");
//			throw boncExpection;
			return  resultList;
		}
		if(!StringUtil.validateStr(req.get("activityId"))){
			req.put("activitySeqIdAnd", " AND	ACTIVITY_SEQ_ID = "+ activitySeqId );
		}
		req.put("orderTableName",orderTableName);
		resultList = PltActivityInfoDaoIns.selectActivityOrder(req);
		return  resultList;
	}

	/**
	 * 根据活动和渠道获取表列表
	 */
	@Override
	public List<String> getOrderTableListByActivityAndChannel(RequestParamMap paramMap) {
		return PltActivityInfoDaoIns.getOrderTableListByActivityAndChannel(paramMap);
	}

	/**
	 * 根据渠到获取表列表
	 */
	@Override
	public List<String> getOrderTableListByChannel(RequestParamMap paramMap) {
		return PltActivityInfoDaoIns.getOrderTableListByChannel(paramMap);
	}

	/**
	 * 查询活动是否失效
	 */
	@Override
	public boolean isActivityInvalid(String activityId, int activitySeqId,String tenantId) {
		return PltActivityInfoDaoIns.isActivityInvalid(activityId, activitySeqId,tenantId) == 2;
	}

	/**
	 * 获取字段映射
	 */
	@Override
	public Map<String,String> getOrderTableColumnMap(RequestParamMap param) {
		Map<String, String>  columnMap = new HashMap<String,String>();
		 List<Map<String,String>> list = PltActivityInfoDaoIns.getOrderTableColumnMap(param);
		 for(Map<String,String> map : list) {
			 columnMap.put(map.get("SOURCE_TABLE_COLUMN"), map.get("ORDER_COLUMN"));
		 }
		 return columnMap;
	}

	/**
	 * 根据手机号码、渠道查询工单所在的工单表名
	 */
	@Override
	public List<String> getOrderTableNameByPhoneNumber(RequestParamMap param) {
		List<String> tableNameList = new ArrayList<String>();
		String phoneIndexTable = "PLT_ORDER_PHONE_INDEX_0";
		String phoneNumber = param.getPhoneNumber();
		//获取手机号的最后一位
		char lastChar = phoneNumber.charAt(phoneNumber.length()-1);
		//在这个索引表里查询手机号所在的工单
		if(Character.isDigit(lastChar)){
			phoneIndexTable = "PLT_ORDER_PHONE_INDEX_" + lastChar;
		}else{
			phoneIndexTable = "PLT_ORDER_PHONE_INDEX_0";
		}
		param.setTableName(phoneIndexTable);
		List<String> tables = PltActivityInfoDaoIns.getOrderTableNameByPhoneNumber(param);
		return tables;
	}

	/**
	 * 根据活动、批次查询PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中可执行的渠道列表
	 */
	@Override
	public List<String> getExecuteableChannelList(RequestParamMap param) {
		return PltActivityInfoDaoIns.getExecuteableChannelList(param);
	}

	/**
	 * 根据渠道查询可以执行的批次
	 */
	@Override
	public List<Integer> getExecuteableActivityInfo(ActivityChannelExecute executeInterface) {
		List<ActivityChannelExecute> executeAbleList = new ArrayList<ActivityChannelExecute>();
		executeAbleList =PltActivityInfoDaoIns.getExecuteableActivityInfo(executeInterface);
		List<Integer> list = new ArrayList<Integer>();
		for(ActivityChannelExecute exe : executeAbleList) {
			list.add(exe.getActivitySeqId());
		} 
		return list;
	}

	/**
	 * PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中记录的状态
	 */
	@Override
	public void updateExecuteInterfaceStatus(ActivityChannelExecute executeInterface) {
		String tenantId = executeInterface.getTenantId();
		//更新状态
		PltActivityInfoDaoIns.updateExecuteInterfaceStatus(executeInterface);
		//查询状态为2的记录，移入历史表
		List<ActivityChannelExecute> list = PltActivityInfoDaoIns.getChannelExecuteList(tenantId);
		for(ActivityChannelExecute temp : list) {
				PltActivityInfoDaoIns.removeChannelToHis(temp);
				PltActivityInfoDaoIns.deleteChannel(temp);
			}
		}

	/**
	 * 工单表删除数据时更新PLT_ORDER_TABLES_USING_INFO表的的使用信息
	 */
	@Override
	public void updateOrderTableUsingInfo(OrderTableUsingInfo usingInfo) {
		//查询工单表当前的使用量
		int currentCount = PltActivityInfoDaoIns.getUsedCount(usingInfo);
		int delCount = usingInfo.getDelCount();
		if(delCount < currentCount) {  //删除的数量小于工单表的当前的使用量
			currentCount = currentCount-delCount;
		}else{
			currentCount=0;           //删除的数量大于工单表的当前的使用量
		}
		usingInfo.setCucrrentCount(currentCount);
		PltActivityInfoDaoIns.updateOrderTableUsingInfo(usingInfo);
		if(currentCount == 0) {
			usingInfo.setUsingStatus(0);
			PltActivityInfoDaoIns.updateOrderTableUsingStatus(usingInfo);
		}
	}

	/**
	 *  查询 PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE_HIS，查询里面是否有指定的渠道的批次，如果有，说明该渠道的该批次已执行完毕
	 */
	@Override
	public int getChannelFinishedCount(ActivityChannelExecute channelExecute) {
		return PltActivityInfoDaoIns.getChannelFinishedCount(channelExecute);
	}

	/**
	 * 根据活动Id查询工单生成的步骤的名称
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List getOrderGenerateStepName(RequestParamMap param) {
		List allActivitySeqIdSteplist = new ArrayList();
		// 查询工单的所有批次
		List<Integer> activitySeqIds = PltActivityInfoDaoIns.getLatestActivitySeqId(param);
		// 查询工单生成步骤名称
		if (activitySeqIds.size() != 0) {
			for (Integer activitySeqId : activitySeqIds) {
				param.setActivitySeqId(String.valueOf(activitySeqId));
				List<Map<String, Object>> stepMap = PltActivityInfoDaoIns.getOrderGenStepName(param);
				if (stepMap != null && stepMap.size() != 0) {
					allActivitySeqIdSteplist.add(stepMap);
				}
			}
		}
		return allActivitySeqIdSteplist;
	}

	/**
	 * 更新工单表的使用信息： plt_order_tables_using_info
	 * 遍历所有的表，查询表中实际的工单数，把改数更新到plt_order_tables_using_info对应的行中
	 */
	@Override
	public void synOrderTableUsingCount(String tenantId) {
		//通过查询plt_order_tables_using_info表获取所有的工单表名
		List<String> orderTableNameList = PltActivityInfoDaoIns.getOrderTableNameByUsingInfo(tenantId);
		//遍历每一张工单表，查询当前表中工单的数量，更新plt_order_tables_using_info记录
		for(String tableName : orderTableNameList){
			int orderCount = PltActivityInfoDaoIns.getOrderCountInOrderTable(tableName,tenantId);
			PltActivityInfoDaoIns.updateOrderTableUsingCount(tableName,orderCount,tenantId);
		}
	}

	/**
	 * 更新成功标准
	 * @param req
	 * @return
	 */
	@Override
	public HashMap<String, Object> updateActivitySuccess(RequestParamMap req) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("code", IContants.CODE_SUCCESS);
		resp.put("msg",IContants.MSG_SUCCESS);
		String activityId = req.getActivityId();
		String tenantId = req.getTenantId();
		JSONObject actJson = getActivityDetail(req);
		JSONObject successStandardPo = actJson.getJSONObject("successStandardPo");
		if (successStandardPo != null && !successStandardPo.isEmpty()) {
			successStandardPo.put("activityId", activityId);
			successStandardPo.put("tenantId", tenantId);
			PltActivityInfoDaoIns.updateSuccessStandardPo(successStandardPo);
			JSONArray successProductList = successStandardPo.getJSONArray("successProductList");
			if (successProductList != null && !successProductList.isEmpty()) {
				List<Integer> activityInvalidSEQIdsById = PltActivityInfoDaoIns.getActivityInvalidSEQIdsById(activityId, tenantId);
				for(Integer seqid : activityInvalidSEQIdsById){
					PltActivityInfoDaoIns.deleteSuccessProductBySeqId(seqid,tenantId);
					for (Object p : successProductList) {
						JSONObject successProduct = (JSONObject) p;
						successProduct.put("activityId", activityId);
						successProduct.put("tenantId", tenantId);
						successProduct.put("activitySeqId", seqid);
						PltActivityInfoDaoIns.insertSuccessProduct(successProduct);
					}
				}

			}
		}else {
			resp.put("msg","该活动详情未获取到成功标准");
		}
		return resp;
	}

	private JSONObject getActivityDetail(RequestParamMap req) {
		String activityId = req.getActivityId();
		String tenantId = req.getTenantId();
		String activityDetailUrl = SystemCommonConfigManager.getSysCommonCfgValue("ACTIVITY_INFO_DETAIL");
		String activityDetailInfo = "";
		String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
		if ("1".equals(type)){
			HashMap<String, String> submap = new HashMap<String, String>();
			//由于电信组使用的框架导致在调用活动详情服务接口时必须把参数按照调用的服务接口参数再封装一次
			Map<String,Object> activityDetailRequestMap = new HashMap<String,Object>();
			submap.put("activity_id", activityId);
			submap.put("tenant_id", tenantId);
			activityDetailRequestMap.put("req", JSON.toJSON(submap));
			activityDetailInfo = HttpUtil.doPost(activityDetailUrl, activityDetailRequestMap);
		}else {
			HashMap<String, Object> reqMap = new HashMap<String, Object>();
			reqMap.put("tenantId", tenantId);
			reqMap.put("activityId", activityId);
			activityDetailInfo = HttpUtil.doGet(activityDetailUrl, reqMap);
		}
		if (activityDetailInfo == null || activityDetailInfo.equals("") || activityDetailInfo.equals("ERROR")) {
			log.error("[OrderCenter] getActivityDetail respond error..................!!!");
		}
		return JSON.parseObject(activityDetailInfo);
	}
}
