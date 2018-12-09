package com.bonc.busi.scene.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.scene.bo.ScenePowerInfo;
import com.bonc.busi.scene.bo.ScenePowerStatus;

public interface SceneMapper {
	
	/*
	 * 查询场景营销成功总数
	 */
	@Select(" SELECT COUNT(DISTINCT PHONE_NUMBER) userNum, " //用户数
			+ " COUNT(REC_ID) orderNum, "	//工单数
			+ " COUNT(DISTINCT IF(CONTACT_CODE='0',PHONE_NUMBER,NULL)) sendUserNum," //发送用户数
			+ " COUNT(IF(CONTACT_CODE='0',REC_ID,NULL)) sendOrderNum," //发送工单数
			+ " COUNT(DISTINCT IF(SUCCESS_STATUS='1' AND CONTACT_CODE='0',PHONE_NUMBER,NULL)) successUserNum, "//
			+ " COUNT(IF(SUCCESS_STATUS='1' AND CONTACT_CODE='0',REC_ID,NULL)) succesOrderNum " //成功工单数
			+ " FROM ${tableName} WHERE TENANT_ID=#{tenantId} "
			+ " AND ACTIVITY_SEQ_ID IN (${recSql}) ${contactDateStartSql} ${contactDateEndSql} ")
	HashMap<String, Object> querySuccessNum(HashMap<String, Object> req);
	
	/*
	 * 获取活动批次号
	 */
	@Select("SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_ID=#{activityId} AND ACTIVITY_STATUS<>2")
	List<Object> queryActivitySeq(HashMap<String, Object> req);

	/*
	 * 获取场景营销批次号（场景营销一个活动一个批次）
	 */
	@Select("SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_ID=#{activityId} AND ACTIVITY_STATUS<>2 limit 1")
	Integer querySceneActivitySeq(HashMap<String, Object> req);


	/**
	 * 场景能力插入初始化状态数据
	 * @param scenePowerStatus
	 */
	@Insert("INSERT INTO SCENCE_POWER_STATE(BATCH_ID,BEGIN_DATE,END_DATA,STATE,TENANT_ID) VALUES (#{batchId},#{beginDate},#{endDate},#{status},#{tenantId})")
	Integer addScenePowerStatus(ScenePowerStatus scenePowerStatus);
	
	/**
	 * 更新场景能力状态数据
	 * @param scenePowerStatus
	 */
	@Update("UPDATE SCENCE_POWER_STATE SET STATE = #{status},END_DATA = #{endDate} WHERE TENANT_ID = #{tenantId} AND BATCH_ID = #{batchId}")
	void updateScenePowerStates(ScenePowerStatus scenePowerStatus);
	
	/**
	 * 根据场景能力批次号查询
	 * @param batchId
	 * @param tenantId
	 * @return 批次号和结果值
	 */
	@Select("SELECT BATCH_ID,RESULT_NUM,STATE FROM SCENCE_POWER_STATE WHERE TENANT_ID = #{tenantId} AND BATCH_ID = #{batchId}")
	HashMap<String, Object> queryScencePowerStatus(@Param("batchId") String batchId, @Param("tenantId") String tenantId);
	
	/**
	 * 批量插入场景能力手机号基础数据
	 */
	@Insert("INSERT INTO SCENCE_POWER_INFO (BATCH_ID,PHONE_NUMBER,TENANT_ID) VALUES ${valueSQL} ")
	Integer addScenePowerInfo(@Param("valueSQL") String valueSQL);
	
	/**
	 * 查询场景能力之前是否存在批次号
	 * @return
	 */
	@Select("SELECT BATCH_ID FROM SCENCE_POWER_INFO WHERE TENANT_ID = #{tenantId} AND BATCH_ID = #{batchId}")
	Integer queryIsExistBatchId(@Param("batchId") Integer batchId, @Param("tenantId") String tenantId);
	
	
	/*
	 * 删除之前有的批次号记录
	 */
	@Delete("DELETE FROM SCENCE_POWER_INFO WHERE TENANT_ID = 'uni076' AND BATCH_ID = '35'")
	void delOldScenePowerStatus(@Param("batchId") Integer batchId, @Param("tenantId") String tenantId);
 
}
