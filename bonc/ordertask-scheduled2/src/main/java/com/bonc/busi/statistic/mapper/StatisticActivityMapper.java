package com.bonc.busi.statistic.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface StatisticActivityMapper {

	@Update("DELETE FROM PLT_ORDER_STATISTIC WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activitySeqId} ")
	void delIncrStatistic(HashMap<String, Object> incrActivity);

	@Update("DELETE FROM PLT_ORDER_STATISTIC WHERE TENANT_ID=#{tenantId} "
			+ " AND MARKER='1' AND ACTIVITY_SEQ_ID=#{activitySeqId} AND ORG_PATH LIKE #{delOrgPath} ")
	void delOrgStatistic(HashMap<String, Object> req);

	/**
	 * 查询批次小有效的渠道
	 * @param map
	 * @return
	 */
	@Select("SELECT * FROM PLT_ACTIVITY_PROCESS_LOG WHERE TENANT_ID=#{TENANT_ID} AND `STATUS`=0 AND CHANNEL_ID IS NOT NULL AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID}")
	List<HashMap<String, Object>> orderDate(Map<String, Object> map);

	/**
	 * 任意获取一条工单
	 * @param string
	 * @return
	 */
	@Select("SELECT DATE_FORMAT(o.BEGIN_DATE,'%Y%m%d') DEAL_MONTH,o.* FROM ${tableName} o WHERE o.TENANT_ID=#{TENANT_ID} AND o.ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND o.CHANNEL_ID=#{CHANNEL_ID} LIMIT 1")
	HashMap<String, Object> orderLimit(Map<String, Object> map);

	@Update("UPDATE PLT_ACTIVITY_PROCESS_LOG SET ORDER_BEGIN_DATE=#{ORDER_BEGIN_DATE},ORDER_END_DATE=#{ORDER_END_DATE} WHERE TENANT_ID=#{TENANT_ID} AND REC_ID=#{PROC_LOG_ID} ")
	void updateChannelLog(HashMap<String, Object> orderHashMap);

	@Insert("INSERT INTO PLT_ORDER_STATISTIC_SEND (TENANT_ID,EXTERNAL_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,SEND_ALL_COUNT,SEND_ALL_NUM,VALID_NUM,BLACK_COUNT,RULE_COUNT) "
			+ " VALUES(#{TENANT_ID},#{UUID},#{ACTIVITY_SEQ_ID},#{CHANNEL_ID},#{CHANNEL_ORDER_NUM},#{VALID_NUM},#{VALID_NUM},#{BLACK_NUM},#{RULE_NUM})")
	void addActivityDeqStatistic(HashMap<String, Object> order);

	@Update("UPDATE PLT_ORDER_STATISTIC SET SERVICE_TYPE=#{SERVICE_TYPE},DEAL_MONTH=#{DEAL_MONTH},BEGIN_DATE=#{BEGIN_DATE},END_DATE=#{END_DATE} "
			+ " WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} " )
	void updateStatistic(HashMap<String, Object> orderHashMap);

	/**
	 * 关联活动表 和 批次日志表 获取工单生成成功，且当前有效的批次,短信的批次不进行处理，因为短信的不跑成功标准
	 * @param req
	 * @return
	 */
	@Select("SELECT ACTIVITY_SEQ_ID,CHANNEL_ID,RIGHT(CONCAT('0',MONTH(ORDER_BEGIN_DATE)),2) MONTH FROM PLT_ACTIVITY_PROCESS_LOG WHERE TENANT_ID=#{TENANT_ID} "
			+ " AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND STATUS='0'")
	List<HashMap<String,Object>> getActivityRecList(HashMap<String, Object> req);

	@Select(" SELECT '${TENANT_ID}' TENANT_ID,"
			+ " '${CHANNEL_ID}' CHANNEL_ID,"
			+ " '${ACTIVITY_SEQ_ID}' ACTIVITY_SEQ_ID,"
			+ " COUNT(*) VALID_NUM,"	//有效工单量
			+ " COUNT(IF(s.CONTACT_RESULT='1',TRUE,NULL)) FINISH_NUM,"  //已完成工单量
			+ " COUNT(IF(s.CHANNEL_STATUS=3 AND s.CONTACT_RESULT='1',TRUE,NULL)) VISITED_SUCCESS," //干预成功量
			+ " COUNT(IF(s.CHANNEL_STATUS=3,TRUE,NULL)) TOTAL_SUCCESS"	//成功量
			+ " FROM ( "
			//子查询表结构 START
			+ " SELECT CASE WHEN CHANNEL_ID='5' THEN IF(CONTACT_CODE IN ('101','102','103','104','121'),'1','0') "
			+ " WHEN CHANNEL_ID IN ('81','82') THEN IF(CONTACT_CODE IN ('101','121'),'1','0') "
			+ " WHEN CHANNEL_ID='11' THEN IF(CONTACT_CODE IN ('1101','1102','1100'),'1','0')"
			+ " WHEN CHANNEL_ID IN ('2','1','9') THEN IF(CONTACT_CODE IN ('0201','0202'),'1','0') END CONTACT_RESULT, "
			+ " CHANNEL_STATUS "
			+ " FROM ${TABLE_NAME} "
			+ " WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND ORDER_STATUS='5' "
			+ " AND CHANNEL_STATUS NOT IN ('401','402','403') AND CHANNEL_ID=#{CHANNEL_ID} "
			//子查询表结构 END
			+ " ) s ")
	HashMap<String, Object> getRecStatistic(HashMap<String, Object> req);

	@Update("UPDATE PLT_ORDER_STATISTIC_SEND SET VISITED_SUCCESS=#{VISITED_SUCCESS},TOTAL_SUCCESS=#{TOTAL_SUCCESS} "
			+ " WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID}")
	Integer updateRecStatistic(HashMap<String, Object> statistic);

	@Update("UPDATE PLT_ORDER_STATISTIC SET STATISTIC_DATE=NOW() WHERE TENANT_ID=#{TENANT_ID}")
	void dayUpdate(Map<String, Object> map);

	@Select("SELECT * FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{TENANT_ID} AND REC_ID=#{ACTIVITY_SEQ_ID} ")
	HashMap<String, Object> getActivityInfo(HashMap<String, Object> order);

	@Select("SELECT COUNT(1) FROM ${tableName} WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID} AND ORDER_STATUS=#{ORDER_STATUS} AND CHANNEL_STATUS NOT IN (401,402,403) ")
	Integer statisticRec(HashMap<String, Object> order);

	@Update("UPDATE PLT_ORDER_STATISTIC SET VISIT_NUMS_TODAY=0 WHERE TENANT_ID=#{TENANT_ID}")
	void dayVisitedInit(Map<String, Object> map);

	@Select("SELECT COUNT(1) FROM PLT_ORDER_INFO_BLACK WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID} ")
	Integer statisticBlackRec(HashMap<String, Object> order);

	/**
	 * 查询规则过滤的工单数
	 */
	@Select("SELECT COUNT(1) FROM ${tableName}_HIS${month} WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID} AND ORDER_STATUS=#{ORDER_STATUS}")
	Integer statisticRuleRec(HashMap<String, Object> order);

	@Select("SELECT * FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_ID=#{ACTIVITY_ID} AND ACTIVITY_STATUS<>2")
	List<HashMap<String, Object>> getActivitySeqs(HashMap<String, Object> activityInfo);

	@Update("UPDATE PLT_ORDER_STATISTIC_SEND SET RULE_COUNT=#{RULE_NUM},VALID_NUM=#{VALID_NUM} WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID}")
	void updateRuleNum(HashMap<String, Object> activitySeq);

	@Select("SELECT * FROM PLT_ORDER_DETAIL_COUNT WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID}")
	List<HashMap<String, Object>> findActivityCount(HashMap<String, Object> order);

	@Insert("INSERT INTO PLT_ORDER_DETAIL_COUNT (TENANT_ID,ACTIVITY_THEMEID,ACTIVITY_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,ALL_COUNT,FILTER0_COUNT,FILTER1_COUNT,FILTER2_COUNT,FILTER3_COUNT,FILTER4_COUNT,VALID_NUM,RESERVE1) "
			+ " VALUES(#{TENANT_ID},#{ACTIVITY_THEMEID},#{ACTIVITY_ID},#{ACTIVITY_SEQ_ID},#{CHANNEL_ID},#{ALL_COUNT},#{RULE_NUM},#{CONTACT_FILTER_NUM},#{BLACK_NUM},#{SUCCESS_NUM},#{REMAIN_NUM},#{VALID_NUM},#{REMAIN_NUM_COUNT}) ")
	void addActivityCount(HashMap<String, Object> order);

	@Update("UPDATE PLT_ORDER_DETAIL_COUNT SET FILTER0_COUNT=#{RULE_NUM},VALID_NUM=#{VALID_NUM} WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID}")
	void updateActivityCount(HashMap<String, Object> activitySeq);
	
	@Insert("INSERT INTO PLT_ACTIVITY_CHANNEL_STATUS (TENANT_ID,ACTIVITY_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,STATUS) "
			+ " VALUES(#{TENANT_ID},#{ACTIVITY_ID},#{ACTIVITY_SEQ_ID},#{CHANNEL_ID},#{STATUS}) ")
	void addActivityChannelStatus(HashMap<String, Object> order);
	
	@Update("UPDATE PLT_ORDER_DETAIL_COUNT SET ALL_COUNT=FILTER0_COUNT+FILTER1_COUNT+FILTER3_COUNT+FILTER2_COUNT WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID}")
	void updateActivityFilterAllCount(HashMap<String, Object> activitySeq);

	
	@Select("SELECT COUNT(1) FROM PLT_ORDER_INFO_REMAIN WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND CHANNEL_ID=#{CHANNEL_ID} ")
	Integer statisticRemainNum(HashMap<String, Object> order);

	@Select("SELECT MAX(REC_ID) FROM PLT_ORG_LOGIN_INFO WHERE TENANT_ID=#{tenantId}")
	Integer getOrgRecMax(HashMap<String, Object> map);
}
