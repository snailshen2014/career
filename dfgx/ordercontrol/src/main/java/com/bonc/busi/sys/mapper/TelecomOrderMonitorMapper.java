package com.bonc.busi.sys.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/*
 * 工单监控
 */
public interface TelecomOrderMonitorMapper {

	/**
	 * 查询PLT_ACTIVITY_EXECUTE_LOG,倒排BEGIN_DATE,选取 NOW()-BEGIN_DATE<10的记录，如果存在表示这个活动Id就是正在跑的活动
	 * @param tenantId
	 * @return
	 */
	@Select("SELECT ACTIVITY_ID FROM (SELECT ACTIVITY_ID,BEGIN_DATE FROM PLT_ACTIVITY_EXECUTE_LOG"
			+ " WHERE BUSI_CODE NOT IN ('1004', '2002', '2004') AND TENANT_ID=#{tenantId} ORDER BY BEGIN_DATE DESC LIMIT 1) temp"
			+ " WHERE NOW()-BEGIN_DATE<10")
	public  String  queryRunningActivity(@Param("tenantId") String tenantId);

	/**
	 * 查询跑完的活动的最后更新时间及对应的批次
	 * @param actIds
	 * @param tenantId
	 * @return
	 */
	@Select("SELECT * FROM"
			+ " ("
			+ " SELECT  ACTIVITY_ID,REC_ID,LAST_ORDER_CREATE_TIME,TENANT_ID from PLT_ACTIVITY_INFO "
			+ " WHERE ACTIVITY_STATUS='1' "
			+ " AND LAST_ORDER_CREATE_TIME IS NOT NULL "
			+ " AND ACTIVITY_ID IN ${actIds}"
			+ " AND TENANT_ID=#{tenantId}"
			+ " ORDER BY LAST_ORDER_CREATE_TIME DESC"
			+ " ) S "
			+ " WHERE TENANT_ID=#{tenantId}"
			+ " GROUP BY ACTIVITY_ID")
	public List<Map<String, Object>> queryActivityInfoMap(@Param("actIds") String actIds, @Param("tenantId") String tenantId);

	/**
	 * 查询工单数
	 * @param tenantId
	 * @param activitySeqId
	 * @param activityId
	 * @return
	 */
	@Select(" SELECT SUM(ORI_AMOUNT-INOUT_FILTER_AMOUNT-COVERAGE_FILTER_AMOUNT-BLACK_FILTER_AMOUNT-RESERVE_FILTER_AMOUNT-TOUCH_FILTER_AMOUNT-SUCCESS_FILTER_AMOUNT-REPEAT_FILTER_AMOUNT)"
			+ " AS TOTAL"
			+ " FROM PLT_ACTIVITY_PROCESS_LOG"
			+ " WHERE ACTIVITY_SEQ_ID=${activitySeqId} AND ACTIVITY_ID=#{activityId} AND TENANT_ID=#{tenantId}")
	public int queryOrderCount(@Param("tenantId")String tenantId, @Param("activitySeqId")int activitySeqId, @Param("activityId")String activityId);
}
