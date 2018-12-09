package com.bonc.busi.outer.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

public interface TrackMapper {
	
	@SelectProvider(type=TrackProvider.class,method="getActivitySet")
	public List<HashMap<String, Object>> getActivitySet(HashMap<String, Object> req);

	@SelectProvider(type=TrackProvider.class,method="countContactTrack")
	public Long countContactTrack(HashMap<String, Object> req);
	
	@SelectProvider(type=TrackProvider.class,method="listContactTrack")
	public List<HashMap<String, Object>> listContactTrack(HashMap<String, Object> req);

	@SelectProvider(type=TrackProvider.class,method="getDxActivitySet")
	public List<HashMap<String, Object>> getDxActivitySet(HashMap<String, Object> req);

//	@Select(" SELECT a.ACTIVITY_ID,s.CHANNEL_ID,s.TENANT_ID,"
//			+ " SUM(s.ALL_COUNT)+SUM(s.VALID_NUM) ALL_COUNT," //初始工单数
////			+ " SUM(s.SEND_SUC_NUM) CONTACT_COUNT," //已完成工单数
//			+ " SUM(s.ALL_COUNT) FILTER_COUNT, " // 过滤掉的工单数
//			+ " SUM(s.FILTER2_COUNT) BLACK_FILTER_COUNT,"// 黑名单过滤数
//			+ " SUM(s.FILTER3_COUNT) SUCCESS_FILTER_COUNT,"// 成功过滤数
//			+ " SUM(s.FILTER0_COUNT)+SUM(s.FILTER1_COUNT) RULE_FILTER_COUNT,"// 规则过滤数
//			+ " SUM(s.VALID_NUM) VALID_COUNT"// 有效工单数
////			+ " SUM(s.VALID_NUM)-SUM(s.SEND_SUC_NUM) UN_CONTACT_COUNT " //未接触工单数
//			+ " FROM PLT_ORDER_DETAIL_COUNT s,PLT_ACTIVITY_INFO a WHERE "
//			+ " s.TENANT_ID=#{tenantId} AND a.TENANT_ID=#{tenantId} AND a.ACTIVITY_ID=#{activityId}" 
//			+ " AND a.REC_ID=s.ACTIVITY_SEQ_ID GROUP BY s.CHANNEL_ID ")
	
//	@Select(" SELECT a.ACTIVITY_ID,s.CHANNEL_ID,"
// 			+ " SUM(s.SEND_ALL_COUNT)+SUM(s.BLACK_COUNT)+SUM(s.RULE_COUNT) ALL_COUNT," //初始工单数
// 			+ " SUM(s.SEND_SUC_NUM) CONTACT_COUNT," //已完成工单数
// 			+ " SUM(s.SEND_ALL_COUNT)-SUM(s.VALID_NUM)+SUM(s.BLACK_COUNT)+SUM(s.RULE_COUNT) FILTER_COUNT, " // 过滤掉的工单数
// 			+ " SUM(s.BLACK_COUNT) BLACK_FILTER_COUNT,"// 黑名单过滤数
// 			+ " SUM(s.SEND_ALL_COUNT)-SUM(s.VALID_NUM) SUCCESS_FILTER_COUNT,"// 成功过滤数
// 			+ " SUM(s.RULE_COUNT) RULE_FILTER_COUNT,"// 规则过滤数
// 			+ " SUM(s.VALID_NUM) VALID_COUNT,"// 有效工单数
// 			+ " SUM(s.VALID_NUM)-SUM(s.SEND_SUC_NUM) UN_CONTACT_COUNT " //未接触工单数
// 			+ " FROM PLT_ACTIVITY_INFO a,PLT_ORDER_STATISTIC_SEND s WHERE a.TENANT_ID=#{tenantId} "
// 			+ " AND s.TENANT_ID=#{tenantId} AND a.ACTIVITY_ID=#{activityId} AND a.REC_ID=s.ACTIVITY_SEQ_ID GROUP BY s.CHANNEL_ID ")
	
	
	
	@SelectProvider(type=TrackProvider.class,method="getChannelNums")
	public List<HashMap<String, Object>> getChannelNums(HashMap<String, Object> req);
	 
	@Select(" SELECT s.CHANNEL_ID,"
			+ " SUM(s.SEND_SUC_NUM) CONTACT_COUNT," //已完成工单数
			+ " ${VALID_COUNT}-SUM(s.SEND_SUC_NUM) UN_CONTACT_COUNT " //未接触工单数
			+ " FROM PLT_ACTIVITY_INFO a,PLT_ORDER_STATISTIC_SEND s WHERE a.TENANT_ID=#{TENANT_ID} "
			+ " AND s.TENANT_ID=#{TENANT_ID} AND a.ACTIVITY_ID=#{ACTIVITY_ID} AND a.REC_ID=s.ACTIVITY_SEQ_ID AND CHANNEL_ID=#{CHANNEL_ID} ")
	public HashMap<String, Object> getContactNums(HashMap<String, Object> req);
	 
//	@Select("SELECT COUNT(1) FROM PLT_ORDER_DETAIL_COUNT s,PLT_ACTIVITY_INFO a "
//			+ " WHERE s.TENANT_ID=#{tenantId} AND a.TENANT_ID=#{tenantId} AND "
//			+ " a.ACTIVITY_ID=#{activityId} AND a.REC_ID=s.ACTIVITY_SEQ_ID "
//			+ " AND s.CHANNEL_ID=#{channelId} ")
	
	@SelectProvider(type=TrackProvider.class,method="countupdatehistory")
	public Integer countupdatehistory(HashMap<String, Object> req);
	
//	@Select("SELECT '新增工单' UPDATE_TYPE,s.VALID_NUM UPDATE_NUM,s.CHANNEL_ID,s.ACTIVITY_SEQ_ID, "
//			+ " a.ORDER_BEGIN_DATE UPDATE_DATE,a.ORDER_BEGIN_DATE VALIDED_DATE,a.ACTIVITY_STATUS "
//			+ " FROM PLT_ORDER_DETAIL_COUNT s,PLT_ACTIVITY_INFO a "
//			+ " WHERE s.TENANT_ID=#{tenantId} AND a.TENANT_ID=#{tenantId} AND "
//			+ " a.ACTIVITY_ID=#{activityId} AND a.REC_ID=s.ACTIVITY_SEQ_ID "
//			+ " AND s.CHANNEL_ID=#{channelId} LIMIT ${limit}")
	
	@SelectProvider(type=TrackProvider.class,method="updatehistory")
	public List<HashMap<String, Object>> updatehistory(HashMap<String, Object> req);

	@Select("SELECT * FROM PLT_ACTIVITY_CHANNEL_STATUS WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activitySeqId}")
	public HashMap<String, Object> getDxOrderStatus(HashMap<String, Object> req);

	@Select("SELECT ACTIVITY_STATUS,DATE_FORMAT(ORDER_BEGIN_DATE,'%Y%m') SEND_MONTH FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND REC_ID=#{activitySeqId} ")
	public HashMap<String, Object> getRecStatus(HashMap<String, Object> req);

	@SelectProvider(type=TrackProvider.class,method="countOrderRecord")
	public Long countOrderRecord(HashMap<String, Object> req);

	@SelectProvider(type=TrackProvider.class,method="listOrderRecord")
	public List<HashMap<String, Object>> listOrderRecord(HashMap<String, Object> req);

	@Select("SELECT DATE_FORMAT(a.LAST_ORDER_CREATE_TIME,'%Y-%m-%d %H:%i:%s') LAST_ORDER_CREATE_TIME,a.* FROM PLT_ACTIVITY_INFO a WHERE a.TENANT_ID=#{tenantId} AND a.ACTIVITY_ID=#{activityId} ORDER BY a.LAST_ORDER_CREATE_TIME DESC")
	public List<HashMap<String, Object>> findActivitySeqs(HashMap<String, Object> req);

	@Select("SELECT * FROM PLT_ORDER_STATISTIC_SEND WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activitySeqId} AND CHANNEL_ID=#{channelId} ")
	public List<HashMap<String, Object>> countDetails(HashMap<String, Object> req);

//	@Select("SELECT COUNT(1) FROM PLT_ORDER_INFO_BLACK WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID IN ${recIds} AND CHANNEL_ID=#{channelId} AND PHONE_NUMBER LIKE '${phoneNumber}%'")
	
	@SelectProvider(type=TrackProvider.class,method="countBlack")
	public Integer countBlack(HashMap<String, Object> req);

	
//	@Select("SELECT PROV_ID,AREA_NO,CITYID,CHANNEL_ID,PHONE_NUMBER,DATE_FORMAT(BEGIN_DATE,'%Y-%m-%d %H:%i:%s') ORDER_BEGIN_DATE FROM PLT_ORDER_INFO_BLACK WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID IN ${recIds} AND CHANNEL_ID=#{channelId} AND PHONE_NUMBER LIKE '${phoneNumber}%' LIMIT ${pageStart},${pageSize}")
	
	@SelectProvider(type=TrackProvider.class,method="listBlack")
	public List<HashMap<String, Object>> listBlack(HashMap<String, Object> req);

//	@Select("SELECT COUNT(1) FROM ${tableName} WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID IN ${recIds} AND ORDER_STATUS NOT IN (6,5) AND PHONE_NUMBER LIKE '${phoneNumber}%'")
	
	@SelectProvider(type=TrackProvider.class,method="countRuleFilter")
	public Integer countRuleFilter(HashMap<String, Object> req);

//	@Select("SELECT PROV_ID,AREA_NO,CITYID,CHANNEL_ID,PHONE_NUMBER,DATE_FORMAT(BEGIN_DATE,'%Y-%m-%d %H:%i:%s') ORDER_BEGIN_DATE FROM ${tableName} WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID IN ${recIds} AND ORDER_STATUS NOT IN (6,5) AND PHONE_NUMBER LIKE '${phoneNumber}%' LIMIT ${pageStart},${pageSize}")
	
	@SelectProvider(type=TrackProvider.class,method="getFilterList")
	public List<HashMap<String, Object>> getFilterList(HashMap<String, Object> req);

	@Select("SELECT REC_ID,ACTIVITY_STATUS,ORDER_BEGIN_DATE UPDATE_DATE,ORDER_END_DATE VALIDED_DATE FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_ID=#{activityId} AND ACTIVITY_STATUS<>0 ")
	public List<HashMap<String, Object>> getActivitySeq(HashMap<String, Object> req);

}
