package com.bonc.busi.outer.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.bonc.busi.outer.bo.RequestParamMap;

public interface StatusMapper {

	@Select("SELECT ACTIVITY_STATUS,REC_ID,ACTIVITY_NAME FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} "
			+ " AND ACTIVITY_ID=#{activityId} ORDER BY REC_ID DESC LIMIT 1")
	public HashMap<String, Object> getActivityStatus(HashMap<String, Object> req);

	@SelectProvider(type =StatusProvider.class,method="getActivityCount")
	public Integer getActivityCount(HashMap<String, Object> req);

	@SelectProvider(type =StatusProvider.class,method="getActivityList")
	public List<HashMap<String, Object>> getActivityList(HashMap<String, Object> req);

	@Select("SELECT IF(NOW()<END_DATE,'2','3') STATUS FROM PLT_ORDER_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{recId} LIMIT 1")
	public Integer getOrderStatus(HashMap<String, Object> req);

	@Select(" SELECT l.operater,l.channelId,l.orderNum,l.statusDesc,l.operateTime,l.status,s.SEND_SUC_NUM finishNum FROM ("
			+ " SELECT '系统' operater,l.CHANNEL_ID channelId,l.CHANNEL_ORDER_NUM orderNum,l.TENANT_ID,l.ACTIVITY_SEQ_ID,l.BEGIN_DATE,"
			+ " CASE WHEN l.`STATUS`=1 THEN '工单生成失败' WHEN (l.`STATUS`=0 AND (l.END_DATE IS NULL OR l.ORDER_END_DATE IS NULL)) THEN '工单生成中' WHEN (l.`STATUS`=0 AND l.END_DATE IS NOT NULL AND l.ORDER_END_DATE>NOW()) THEN '工单执行中' WHEN (l.`STATUS`=0 AND l.END_DATE IS NOT NULL AND l.ORDER_END_DATE<=NOW()) THEN '工单已完成' END AS `statusDesc`, "
			+ " CASE WHEN l.`STATUS`=1 THEN DATE_FORMAT(l.BEGIN_DATE,'%Y-%m-%d %H:%i:%s') WHEN (l.`STATUS`=0 AND (l.END_DATE IS NULL OR l.ORDER_END_DATE IS NULL)) THEN DATE_FORMAT(l.BEGIN_DATE,'%Y-%m-%d %H:%i:%s') WHEN (l.`STATUS`=0 AND l.END_DATE IS NOT NULL AND l.ORDER_END_DATE>NOW()) THEN DATE_FORMAT(l.END_DATE,'%Y-%m-%d %H:%i:%s') WHEN (l.`STATUS`=0 AND l.END_DATE IS NOT NULL AND l.ORDER_END_DATE<=NOW()) THEN DATE_FORMAT(l.ORDER_END_DATE,'%Y-%m-%d %H:%i:%s') END operateTime , "
			+ " CASE WHEN l.`STATUS`=1 THEN 1 WHEN (l.`STATUS`=0 AND (l.END_DATE IS NULL OR l.ORDER_END_DATE IS NULL)) THEN 2 WHEN (l.`STATUS`=0 AND l.END_DATE IS NOT NULL AND l.ORDER_END_DATE>NOW()) THEN 3 WHEN (l.`STATUS`=0 AND l.END_DATE IS NOT NULL AND l.ORDER_END_DATE<=NOW()) THEN 4 END AS `status`  "
			+ " FROM PLT_ACTIVITY_PROCESS_LOG l WHERE l.TENANT_ID=#{tenantId} AND l.ACTIVITY_ID=#{activityId} AND l.STATUS='0') l LEFT JOIN PLT_ORDER_STATISTIC_SEND s ON l.TENANT_ID=s.TENANT_ID AND l.ACTIVITY_SEQ_ID=s.ACTIVITY_SEQ_ID AND l.channelId=s.CHANNEL_ID ORDER BY l.ACTIVITY_SEQ_ID DESC,l.BEGIN_DATE DESC ")
	public List<HashMap<String, Object>> getGenList(HashMap<String, Object> req);

	@Select("SELECT *,DATE_FORMAT(ORDER_BEGIN_DATE,'%Y-%m-%d %H:%i:%s') BEGIN_GEN_DATE,DATE_FORMAT(LAST_ORDER_CREATE_TIME,'%Y-%m-%d %H:%i:%s') END_GEN_DATE FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_ID=#{activityId} ORDER BY LAST_ORDER_CREATE_TIME DESC")
	public List<HashMap<String, Object>> getActivityBench(RequestParamMap req);
	
}
