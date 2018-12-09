package com.bonc.busi.interfaces.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.interfaces.model.alertwin.ActivityQueryReq;

public interface AlertWinMapper {

	@Select(" SELECT l.REC_ID recId,"
			+ " a.ACTIVITY_ID activityId,"
			+ " a.ACTIVITY_NAME activityName, "
			+ " a.ACTIVITY_THEMEID activityType,"
			+ " a.ACTIVITY_THEME activityTypeDesc,"
			+ " l.BEGIN_DATE beginDate,"
			+ " l.END_DATE endDate,"
			+ " l.PHONE_NUMBER phoneNum, "
			+ " a.GROUP_NAME groupName ,"
			+ " l.RESERVE5,"
			+ " l.USER_ID userId,"
			+ " l.MARKETING_WORDS strategyInfo,"
			+ " l.RESERVE5,"
			+ " w.TARGET groupDesc, "
			+ " a.STRATEGY_DESC policyDesc,"
			+ " w.SMS_WORDS smsDesc, "
			+ " w.CONTENT marketDesc" //进行话术变量替换的
			+ " FROM PLT_ORDER_INFO_POPWIN l,PLT_ACTIVITY_INFO a,PLT_ACTIVITY_CHANNEL_DETAIL w  "
			+ " WHERE l.TENANT_ID=#{tenantId} AND l.CHANNEL_ID=#{channelId} AND l.PHONE_NUMBER=#{phoneNum} "
			+ " AND l.ORDER_STATUS=5 AND l.CHANNEL_STATUS=0 AND a.TENANT_ID=#{tenantId} AND w.TENANT_ID=#{tenantId} "
			+ " AND a.ACTIVITY_STATUS IN ('1','8','9') AND (l.RESERVE1<w.NUMBERLIMIT OR l.RESERVE1 IS NULL ) "
			+ " AND a.REC_ID=l.ACTIVITY_SEQ_ID AND a.REC_ID=w.ACTIVITY_SEQ_ID AND l.CHANNEL_ID=w.CHANN_ID " )
	List<HashMap<String, Object>> findActivitys(ActivityQueryReq req);

	/**
	 * 查询同一主题下的接触历史
	 * @param req
	 * @return
	 */
	@Select("SELECT l.CONTACT_DATE contactDate,a.REC_ID activityId,a.ACTIVITY_NAME activityName, "
			+ " a.ACTIVITY_THEMEID activityType,a.ACTIVITY_THEME activityTypeDesc, l.CONTACT_TYPE contactType, "
			+ " l.CONTACT_CODE contactCode, l.CONTACT_CONTENT contactMsg,"
			+ " l.PHONE_NUMBER phoneNum,l.LOGIN_ID loginId, l.LOGIN_NAME loginName,l.TENANT_ID tenantId, "
			+ " l.EXT2 contactSmsDesc, l.EXT3 contactMarketDesc "
			+ " FROM PLT_ORDER_PROCESS_LOG_${month} l,PLT_ACTIVITY_INFO a WHERE "
			+ " l.TENANT_ID=#{tenantId} AND a.TENANT_ID=#{tenantId} AND a.ACTIVITY_THEMEID=#{activityType} "
			+ " AND a.REC_ID=l.ACTIVITY_SEQ_ID AND l.CHANNEL_ID=#{channelId} ")
	List<HashMap<String, Object>> findWinHistory(HashMap<String, Object> req);

	@Update("UPDATE PLT_ORDER_INFO_POPWIN SET RESERVE1=RESERVE1+1 WHERE TENANT_ID=#{tenantId} AND REC_ID IN (${recIds}) ")
	void updateOrders(HashMap<String, String> item);

	@Update("UPDATE PLT_ORDER_INFO_POPWIN SET RESERVE1=0 WHERE TENANT_ID=#{TENANT_ID} AND CHANNEL_ID=#{CHANNEL_ID} ")
	void updateLimitNum(Map<String, Object> map);

	@Insert("INSERT INTO PLT_ORDER_PROCESS_LOG_${month} (TENANT_ID,ORDER_REC_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,PHONE_NUMBER,USER_ID,CONTACT_CODE,CONTACT_DATE,LOGIN_ID,EXT5)"
			+ " VALUES(#{tenantId},#{recId},#{activitySeqId},#{channelId},#{phoneNum},#{userId},#{contactCode},#{alertTime},#{loginId},#{uuid})")
	void addAlertLog(HashMap<String, String> req);
	
	@Select("SELECT * FROM PLT_ORDER_INFO_POPWIN WHERE TENANT_ID=#{tenantId} AND REC_ID IN (${recIds}) ")
	List<HashMap<String, Object>> selectOrders(HashMap<String, String> item);

}
