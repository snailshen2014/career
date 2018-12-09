package com.bonc.busi.track.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.fileupload.util.LimitedInputStream;
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

	//4.2
	@SelectProvider(type=ActivityTrackProvider.class,method="getUpdateHistoryRecord")
	HashMap<String, Object> getUpdateHistoryRecord(HashMap<String, Object> req);
	
	//4.3
	@SelectProvider(type=ActivityTrackProvider.class,method="getAllOrderCount")
	HashMap<String, Object> getAllOrderCount(HashMap<String, Object> req);
	

//	//4.3
//	@SelectProvider(type=ActivityTrackProvider.class,method="getDetailVaildCount")
//	HashMap<String, Object> getDetailVaildCount(HashMap<String, Object> req);
	
	
		 
	

	
	
}
