package com.bonc.busi.task.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SceneMapper {

    @Insert(" INSERT INTO PLT_ORDER_INFO_SCENEMARKET ("
    		+ " ACTIVITY_SEQ_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID,PHONE_NUMBER,BEGIN_DATE,"
    		+ " CONTACT_TYPE,CONTACT_CODE,AREAID,CITYID,RESERVE1,RESERVE2,ORDER_STATUS,USER_ID,RESERVE3,RESERVE4 ) "
    		+ " VALUES (#{activitySeqId},#{channelType},#{sendContent},#{tenantId},#{telPhone},"
    		+ " #{downKafkaTime},#{contactType},'0',#{areaId},#{cityId},#{RESERVE1},#{RESERVE2},'5',#{userId},#{uniqueId},NOW() )")
	public	void addSceneOrderInfo(HashMap<String, String> order);
    
    
    @Insert(" INSERT INTO PLT_ORDER_INFO_SCENEMARKET_HIS ("
    		+ " ACTIVITY_SEQ_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID,PHONE_NUMBER,BEGIN_DATE,"
    		+ " CONTACT_TYPE,CONTACT_CODE,AREAID,CITYID,RESERVE1,RESERVE2,ORDER_STATUS,USER_ID,RESERVE3,RESERVE4 ) "
    		+ " VALUES (#{activitySeqId},#{channelType},#{sendContent},#{tenantId},#{telPhone},"
    		+ " #{downKafkaTime},#{contactType},'0',#{areaId},#{cityId},#{RESERVE1},#{RESERVE2},'5',#{userId},#{uniqueId},NOW() )")
    public void addSceneOrderInfoHis(HashMap<String, String> order);
    
    
    //包括失效的活动
    @Select("SELECT * FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID=#{activityId} AND TENANT_ID=#{tenantId} LIMIT 1 ")
    public HashMap<String, Object> getActivityInfo(@Param("tenantId") String tenantId, @Param("activityId") String activityId);

    
    //根据手机号获取用户iD  仅限移动用户
    @Select("SELECT USER_ID FROM PLT_USER_LABEL WHERE DEVICE_NUMBER = #{telPhone} AND USER_TYPE = '0' AND PARTITION_FLAG = #{partFlag} AND TENANT_ID=#{tenantId}")
	public String getUserId(@Param("telPhone") String telPhone, @Param("partFlag") String partFlag, @Param("tenantId") String tenantId);

    
    @Update("UPDATE PLT_ORDER_INFO_${k} SET CONTACT_CODE = '1' WHERE PHONE_NUMBER = #{telphone} AND BUSINESS_RESERVE3 = #{uniqueId} ")
	public void updateFailSms(@Param("telphone") String telphone, @Param("uniqueId") String uniqueId, @Param("k") int k);

	@Update("${mycatSql} ${substring}")
    void executeScene(@Param("substring") String substring,@Param("mycatSql") String mycatSql);

	@Select(" SELECT TABLE_NAME FROM PLT_ORDER_TABLES_ASSIGN_RECORD_INFO " +
			"WHERE ACTIVITY_SEQ_ID=#{activitySeqId} AND CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId} AND BUSI_TYPE=#{busiType} limit 1")
	String queryOrderTable(HashMap<String, Object> params);
}
