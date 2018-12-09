package com.bonc.busi.task.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.task.bo.ScenePowerStatus;

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

    
    @Update("UPDATE PLT_ORDER_INFO_SCENEMARKET SET CONTACT_CODE = '1' WHERE PHONE_NUMBER = #{telphone} AND RESERVE3 = #{uniqueId} ")
	public void updateFailSms(@Param("telphone") String telphone, @Param("uniqueId") String uniqueId);
    
    /**
     * 根据开始时间和状态为1 获取一条数据
     * @param TENANT_ID
     * @return
     */
    @Select("SELECT BATCH_ID FROM SCENCE_POWER_STATE  WHERE TENANT_ID = #{tenantId} AND STATE = '1' ORDER BY BEGIN_DATE LIMIT 1")
	public Integer getInsertFinishedFirstBatch(@Param("tenantId") String tenantId);

    
    /*
     * 查询进程表找到id对应活动id和活动批次号
     */
//     @Select("SELECT DISTINCT(ACTIVITY_ID) ,ACTIVITY_SEQ_ID FROM PLT_ACTIVITY_PROCESS_LOG "
//    		+ " WHERE DATE_FORMAT( BEGIN_DATE, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m') AND TENANT_ID = #{tenantId} ")
    @Select("SELECT ACTIVITY_ID,rec_id AS ACTIVITY_SEQ_ID FROM PLT_ACTIVITY_INFO  WHERE "
    		+ " DATE_FORMAT( LAST_ORDER_CREATE_TIME, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m') AND activity_status = '1' AND TENANT_ID = #{tenantId} ")
	public List<HashMap<String, Integer>> getActivityProcessMap(@Param("tenantId") String tenantId);
	
	/*
	 * 根据表名批次号查询成功工单数
	 */
//	@Select("SELECT COUNT(*) FROM ${table} a, SCENCE_POWER_INFO b "
//			+ " WHERE a.activity_seq_id =${activitySeqId} AND a.channel_status = '3' "
//			+ " AND a.PHONE_NUMBER = b.PHONE_NUMBER AND a.TENANT_ID = #{tenantId} AND b.TENANT_ID = #{tenantId} AND b.BATCH_ID = #{batchId}")
	@Select("SELECT  COUNT(*) FROM ${table} WHERE ACTIVITY_SEQ_ID = ${activitySeqId} AND CHANNEL_STATUS='3'"
			+ " AND TENANT_ID = #{tenantId}  AND PHONE_NUMBER IN ( "
			+ "SELECT phone_number FROM  SCENCE_POWER_INFO WHERE batch_id= #{batchId} AND TENANT_ID = #{tenantId} )")
	public Integer getCountFromSeqId( @Param("table") String table,@Param("activitySeqId") String activitySeqId, @Param("tenantId") String tenantId , @Param("batchId") Integer batchId);

	/**
	 * 更新场景能力状态数据
	 * @param scenePowerStatus
	 */
	@Update("UPDATE SCENCE_POWER_STATE SET STATE = #{status},END_DATA = #{endDate}, RESULT_NUM=#{resultData} ,CONSUME_TIME=#{consumeTime},CONSUME_TIME_DETAIL=#{consumeTimeDetail} WHERE TENANT_ID = #{tenantId} AND BATCH_ID = #{batchId}")
	public void updateScenePowerStatus(ScenePowerStatus scenePowerStatus);

	
	/*
	 * 根据活动id查询活动名称
	 */
	@Select("SELECT ACTIVITY_NAME from PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = #{activityId} AND TENANT_ID=#{tenantId} LIMIT 1")
	public String getActivityName( @Param("tenantId") String tenantId ,@Param("activityId") String activityId);

	/**
	 * 根据批次查询这个批次生成工单的所有渠道
	 * @param activitySeqId
	 * @param tENANT_ID
	 */
	@Select("SELECT CHANNEL_ID FROM PLT_ACTIVITY_PROCESS_LOG WHERE ACTIVITY_SEQ_ID =#{activitySeqId} AND TENANT_ID=#{tenantId}")
	public List<String> queryChannelIdBySeqId(@Param("activitySeqId") String activitySeqId, @Param("tenantId") String tenantId);

	
	@Select("SELECT sum(CHANNEL_ORDER_NUM) FROM PLT_ACTIVITY_PROCESS_LOG WHERE ACTIVITY_SEQ_ID =#{activitySeqId} AND TENANT_ID=#{tenantId}")
	public Integer queryOrderNumBySeqId(@Param("activitySeqId") String activitySeqId, @Param("tenantId") String tenantId);

	@Update("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */ ${substring}")
	void executeScene(@Param("substring") String substring );

}
