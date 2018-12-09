package com.bonc.busi.orderschedule.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;


public interface NoticeMapper {
	
	/**
	 * 获取loginid
	 * @param map
	 * @return
	 */
	@Select("SELECT DISTINCT(LOGIN_ID) FROM PLT_ORDER_STATISTIC WHERE TENANT_ID=#{tenantId} "
			+ " AND ACTIVITY_SEQ_ID=#{activitySeqId} AND LOGIN_ID IS NOT NULL AND LOGIN_ID <> ''")
	List<String> sendNotice(HashMap<String,Object> map);
	
	@Select("SELECT CONTACT_NUM FROM PLT_ORDER_INFO_LOGINID_LOG WHERE TENANT_ID=#{tenantId} "
			+ " AND LOGIN_ID=#{loginId} AND TO_DAYS(LAST_UPDATE_TIME) = TO_DAYS(NOW())")
	Integer maxSend(HashMap<String,Object> map);
	
	@Insert(" INSERT INTO PLT_ORDER_INFO_LOGINID_LOG(TENANT_ID,LOGIN_ID,CONTACT_NUM,LAST_UPDATE_TIME) VALUES "
			+ " (#{tenantId},#{loginId},#{contactNum},NOW())")
	void insertLog(Map<String,Object> map);
	
	@Update(" UPDATE PLT_ORDER_INFO_LOGINID_LOG SET CONTACT_NUM=CONTACT_NUM+1,LAST_UPDATE_TIME=NOW() "
			+ " WHERE TENANT_ID=#{tenantId} AND LOGIN_ID=#{loginId} AND TO_DAYS(LAST_UPDATE_TIME) = TO_DAYS(NOW())")
	Integer updateLog(Map<String,Object> map);
}
