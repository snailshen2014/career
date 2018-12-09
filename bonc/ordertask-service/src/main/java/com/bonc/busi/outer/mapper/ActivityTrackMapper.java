package com.bonc.busi.outer.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

public interface ActivityTrackMapper {
	
	
	@SelectProvider(type=ActivityTrackProvider.class,method="getActivitySet")
	List<HashMap<String, Object>> getActivitySet(HashMap<String, Object> req);
	
	//1.1
	@Select("SELECT A.ALL_VALID_COUNT+B.ALL_VALID_COUNT+B.REMAIN_COUNT ALL_VALID_COUNT,"
       +"B.ALL_CONTACT_COUNT ALL_CONTACT_COUNT,"
       +"B.ALL_UN_CONTACT_COUNT ALL_UN_CONTACT_COUNT,"	     
       +"A.ALL_UN_CONTACT_COUNT PAST_UN_CONTACT_COUNT,"
       +"A.ALL_CONTACT_COUNT ALL_UN_SUCCESS_COUNT,"
       +"B.ALL_SUCCESS_COUNT+A.ALL_SUCCESS_COUNT ALL_SUCCESS_COUNT,"
       + "B.REMAIN_COUNT+A.REMAIN_COUNT REMAIN_COUNT "     
	   +" FROM " 
		+"(SELECT IFNULL(SUM(d.VALID_NUM),0) ALL_VALID_COUNT,"
			+"IFNULL(SUM(s.SEND_SUC_NUM), 0) ALL_CONTACT_COUNT,"
			+"IFNULL(SUM(d.VALID_NUM) - SUM(s.SEND_SUC_NUM),0) ALL_UN_CONTACT_COUNT,"
			+"IFNULL(SUM(s.VISITED_SUCCESS), 0) ALL_SUCCESS_COUNT,"
			+"IFNULL(sum(d.RESERVE1),0) REMAIN_COUNT "
			+"FROM PLT_ORDER_STATISTIC_SEND s,PLT_ACTIVITY_INFO a,PLT_ORDER_DETAIL_COUNT d "
			+"WHERE a.ACTIVITY_ID = #{activityId}"
			+" AND a.REC_ID = s.ACTIVITY_SEQ_ID AND a.REC_ID = d.ACTIVITY_SEQ_ID "
			+" AND s.CHANNEL_ID = d.CHANNEL_ID "
			+" AND a.TENANT_ID = #{tenantId} AND s.TENANT_ID = #{tenantId} AND d.TENANT_ID = #{tenantId} "
			+" AND  a.ORDER_END_DATE < NOW()  ) A,"
		+" (SELECT IFNULL(SUM(d.VALID_NUM),0) ALL_VALID_COUNT,"
			+"IFNULL(SUM(s.SEND_SUC_NUM), 0) ALL_CONTACT_COUNT,"
			+"IFNULL(SUM(d.VALID_NUM) - SUM(s.SEND_SUC_NUM),0) ALL_UN_CONTACT_COUNT,"
			+"IFNULL(SUM(s.VISITED_SUCCESS), 0) ALL_SUCCESS_COUNT,"
			+"IFNULL(sum(d.RESERVE1),0) REMAIN_COUNT "
			+"FROM PLT_ORDER_STATISTIC_SEND s,PLT_ACTIVITY_INFO a,PLT_ORDER_DETAIL_COUNT d "
			+"WHERE a.ACTIVITY_ID = #{activityId}"
			+" AND a.REC_ID = s.ACTIVITY_SEQ_ID AND a.REC_ID = d.ACTIVITY_SEQ_ID "
			+" AND s.CHANNEL_ID = d.CHANNEL_ID "
			+" AND a.TENANT_ID = #{tenantId} AND s.TENANT_ID = #{tenantId} AND d.TENANT_ID = #{tenantId} "
			+" AND  a.ORDER_END_DATE >= NOW()  ) B ")
	HashMap<String, Object> getStatisticAll(HashMap<String, Object> req);
	

	//2.1
	@SelectProvider(type=ActivityTrackProvider.class,method="getChannelStatistic")
	List<HashMap<String, Object>> getChannelStatistic(HashMap<String, Object> req);
	
	
	//2.2
	@SelectProvider(type=ActivityTrackProvider.class,method="getUnValidNum")
	HashMap<String, Object> getUnValidNum(HashMap<String, Object> req);
	
	//2.3
	@SelectProvider(type=ActivityTrackProvider.class,method="getValidNum")
	HashMap<String, Object> getValidNum(HashMap<String, Object> req);
	
	
	
	//3.2
	@SelectProvider(type=ActivityTrackProvider.class,method="getOrderhistory")
	List<HashMap<String, Object>> getOrderhistory(HashMap<String, Object> req);
	
	//3.3
	@SelectProvider(type=ActivityTrackProvider.class,method="getUpadteCount")
	HashMap<String, Object> getUpadteCount(HashMap<String, Object> req);
	
	//3.3
	@SelectProvider(type=ActivityTrackProvider.class,method="getDeleteUpadteCount")
	HashMap<String, Object> getDeleteUpadteCount(HashMap<String, Object> req);
	
	
	//4.0 
//	@Select("SELECT DATE_FORMAT(ORDER_BEGIN_DATE,'%Y-%m-%d %h:%i:%s') ORDER_BEGIN_DATE FROM PLT_ACTIVITY_INFO WHERE REC_ID = #{activitySeqId}")
	
	@SelectProvider(type=ActivityTrackProvider.class,method="getOrderDealMonth")
	HashMap<String, Object> getOrderDealMonth(HashMap<String, Object> req);

	//4.1.1
	@SelectProvider(type=ActivityTrackProvider.class,method="getUpdateRecord")
	HashMap<String, Object> getUpdateRecord(HashMap<String, Object> req);

	//4.1.2
	@SelectProvider(type=ActivityTrackProvider.class,method="getLastOrderCount")
	HashMap<String, Object> getLastOrderCount(HashMap<String, Object> req);
	
	// 4.1.3
	@Select("SELECT REC_ID,TENANT_ID FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = "
			+ "(SELECT ACTIVITY_ID FROM PLT_ACTIVITY_INFO WHERE REC_ID = #{activitySeqId} AND TENANT_ID=#{tenantId}) "
			+ "AND TENANT_ID=#{tenantId} ORDER BY ORDER_BEGIN_DATE LIMIT 1")
	HashMap<String, Object> getFirstId(HashMap<String, Object> req);

	// 4.1.4
	@Select("SELECT SAVE_NUMBER FROM PLT_ACTIVITY_REMAIN_INFO WHERE ACTIVITY_SEQ_ID=#{REC_ID} AND TENANT_ID=#{TENANT_ID} AND CHANNEL_ID=#{channelId}")
	HashMap<String, Object> getReserveCount(HashMap<String, Object> req);

	//4.2
	@SelectProvider(type=ActivityTrackProvider.class,method="getUpdateHistoryRecord")
	HashMap<String, Object> getUpdateHistoryRecord(HashMap<String, Object> req);
	
	//4.3
	@SelectProvider(type=ActivityTrackProvider.class,method="getAllOrderCount")
	HashMap<String, Object> getAllOrderCount(HashMap<String, Object> req);
	

//	//4.3
//	@SelectProvider(type=ActivityTrackProvider.class,method="getDetailVaildCount")
//	HashMap<String, Object> getDetailVaildCount(HashMap<String, Object> req);
	
	@Select("SELECT a.ACTIVITY_ID,CONCAT('${orgParentPath}',SUBSTRING_INDEX(SUBSTRING_INDEX(s.ORG_PATH,'${orgParentPath}',-1),'/',2)) ORGPATH,"
			+"SUM(s.VALID_NUMS) NUM "
			+"FROM PLT_ACTIVITY_INFO a,PLT_ORDER_STATISTIC s "
			+"WHERE a.TENANT_ID = '${tenantId}' AND s.TENANT_ID = '${tenantId}' AND a.REC_ID=s.ACTIVITY_SEQ_ID AND ACTIVITY_ID IN (${activityIds}) AND "
			+"(${orgpath})"
			+" GROUP BY a.ACTIVITY_ID,CONCAT('${orgParentPath}',SUBSTRING_INDEX(SUBSTRING_INDEX(s.ORG_PATH,'${orgParentPath}',-1),'/',2))")
	List<HashMap<String,Object>> allocation(@Param("tenantId")String tenantId,@Param("activityIds")String activityIds,@Param("orgParentPath")String orgParentPath,@Param("orgpath")String orgpath);
		 
	
	

	
	
}
