package com.bonc.busi.outer.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * 短信工单Mapper
 * @author Administrator
 *
 */
public interface SmsOrderMapper {
	
	@Insert("INSERT INTO PLT_SMS_ORDER_MOVE_RECORD(ACTIVITY_ID,ACTIVITY_SEQ_ID,TENANT_ID,CHANNEL_ID,OPERATE_STATUS,BEGIN_TIME) "
			+ " VALUES(#{activityId},${activitySeqId},#{tenantId},#{channelId},#{status}, NOW())")
	void insertSmsOrderMoveRecordOrignStatus(@Param("activityId") String activityId, @Param("activitySeqId") int activitySeqId,
			@Param("tenantId") String tenantId,@Param("channelId") String channelId, @Param("status") String status);
	
	
    @Update("UPDATE PLT_SMS_ORDER_MOVE_RECORD SET OPERATE_STATUS=#{status},END_TIME=NOW() WHERE ACTIVITY_ID=#{activityId} "
    		+ " AND ACTIVITY_SEQ_ID=#{activitySeqId} AND CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId} AND ID=${maxId}")
	void updateSmsOrderMoveRecordOrignStatus(@Param("activityId") String activityId, @Param("activitySeqId") int activitySeqId,
			@Param("tenantId") String tenantId,@Param("channelId") String channelId, @Param("status") String status,@Param("maxId")int maxId);


    /**
     * 根据基本查询条件查询该批次的工单总数以及最大最小recId
     * @param activityId
     * @param activitySeqId
     * @param tenantId
     * @param channelId
     * @return
     */
    @Select("SELECT COUNT(1) as count,MAX(REC_ID) as maxRecId, MIN(REC_ID) as minRecId FROM ${hisTableName} WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=${activitySeqId}")
	Map<String, Object> queryCountAndRecId(@Param("hisTableName")String hisTableName,@Param("activitySeqId")int activitySeqId, @Param("tenantId")String tenantId,@Param("channelId")String channelId);


    @Update("UPDATE ${targetHisTable} SET INPUT_DATE=NOW(),INVALID_DATE=NOW() WHERE ACTIVITY_SEQ_ID=${activitySeqId}  AND REC_ID>=${lBeginRec} AND REC_ID<=${lEndRec} AND TENANT_ID=#{tenantId}")
	void updateTime(@Param("targetHisTable") String targetHisTable, @Param("activitySeqId")int activitySeqId, @Param("tenantId")String tenantId, @Param("lBeginRec")int lBeginRec, @Param("lEndRec")int lEndRec);


    //查询最大的Id
    @Select("SELECT MAX(ID) FROM PLT_SMS_ORDER_MOVE_RECORD  WHERE ACTIVITY_ID=#{activityId} AND ACTIVITY_SEQ_ID=${activitySeqId} AND TENANT_ID=#{tenantId}")
	int queryMaxId(@Param("activityId")String activityId, @Param("activitySeqId")int activitySeqId, @Param("tenantId")String tenantId, @Param("channelId")String channelId);

}
