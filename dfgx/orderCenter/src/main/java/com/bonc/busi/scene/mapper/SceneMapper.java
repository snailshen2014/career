package com.bonc.busi.scene.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Select;

public interface SceneMapper {
	
	@Select(" SELECT COUNT(DISTINCT PHONE_NUMBER) userNum, " //用户数
			+ " COUNT(REC_ID) orderNum, "	//工单数
			+ " COUNT(DISTINCT IF(CONTACT_CODE='0',PHONE_NUMBER,NULL)) sendUserNum," //发送用户数
			+ " COUNT(IF(CONTACT_CODE='0',REC_ID,NULL)) sendOrderNum," //发送工单数
			+ " COUNT(DISTINCT IF(CHANNEL_STATUS='3' AND CONTACT_CODE='0',PHONE_NUMBER,NULL)) successUserNum, "//
			+ " COUNT(IF(CHANNEL_STATUS='3' AND CONTACT_CODE='0',REC_ID,NULL)) succesOrderNum " //成功工单数
			+ " FROM PLT_ORDER_INFO_SCENEMARKET WHERE TENANT_ID=#{tenantId} "
			+ " AND ACTIVITY_SEQ_ID IN (${recSql}) ${contactDateStartSql} ${contactDateEndSql} ")
	HashMap<String, Object> querySuccessNum(HashMap<String, Object> req);

	@Select("SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_ID=#{activityId} AND ACTIVITY_STATUS<>2")
	List<Object> queryActivitySeq(HashMap<String, Object> req);

}
