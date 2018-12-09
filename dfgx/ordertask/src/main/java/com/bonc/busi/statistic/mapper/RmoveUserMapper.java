package com.bonc.busi.statistic.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface RmoveUserMapper{

	@Update("DELETE FROM PLT_ORDER_STATISTIC WHERE TENANT_ID=#{tenantId}  "
			+ " AND ACTIVITY_SEQ_ID=#{activitySeqId} AND ORG_PATH LIKE '${orgPath}%' ")
	void delOrgStatistic(HashMap<String, Object> req);

	/**
	 * 任意获取一条工单
	 * @param string
	 * @return
	 */
	@Select("SELECT o.* FROM PLT_ORDER_INFO o WHERE o.TENANT_ID=#{tenantId} AND o.ACTIVITY_SEQ_ID=#{activitySeqId} AND o.CHANNEL_ID='5' LIMIT 1")
	HashMap<String, Object> orderLimit(Map<String, Object> map);

	
	@Update("UPDATE PLT_ORDER_STATISTIC SET SERVICE_TYPE=#{SERVICE_TYPE},BEGIN_DATE=#{BEGIN_DATE},END_DATE=#{END_DATE} WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} " )
	void updateStatistic(HashMap<String, Object> orderHashMap);
	
	/**
	 * 查询活动批次号
	 * @return
	 */
	@Select("SELECT DISTINCT ACTIVITY_SEQ_ID, TENANT_ID FROM PLT_ORDER_INFO WHERE USER_ID IN(${userid}) AND CHANNEL_ID=#{channelId} AND ORDER_STATUS='5' AND TENANT_ID = #{tenantId}")
	List<HashMap<String, Object>> findRecId(@Param("userid")String userid,@Param("channelId")String channelId,@Param("tenantId")String tenantId);
	
	
	/**
	 * 查询工单信息
	 */
	@Select("SELECT DISTINCT ORG_PATH "
	 +" FROM PLT_ORDER_INFO WHERE  ACTIVITY_SEQ_ID=#{ActivitySeqId} AND TENANT_ID=#{TenantId} AND CHANNEL_ID=#{channelId} AND USER_ID IN(${useId})")
	List<String> findOrderInfos(@Param("ActivitySeqId") int ActivitySeqId,@Param("TenantId")String TenantId,@Param("useId")String useId,@Param("channelId")String channelId);
	/**
	 * 查询工单渠道id
	 */
	@Select("SELECT DISTINCT CHANNEL_ID "
			+" FROM PLT_ORDER_INFO WHERE  ACTIVITY_SEQ_ID=#{ActivitySeqId} AND TENANT_ID=#{TenantId} AND USER_ID IN(${useId})")
	List<String> findOrderChannel(@Param("ActivitySeqId") int ActivitySeqId,@Param("TenantId")String TenantId,@Param("useId")String useId);
	/**
	 * 查询工单接触结果信息
	 */
	@Select("SELECT COUNT(*) NUM, COUNT(CONTACT_CODE IN ('101', '102', '103', '104', '121')) VISITED "
			+" FROM PLT_ORDER_INFO WHERE  ACTIVITY_SEQ_ID=#{ActivitySeqId} AND TENANT_ID=#{TenantId} AND USER_ID IN(${useId}) AND CHANNEL_ID=#{channelId} AND ORDER_STATUS='5'")
	HashMap<String, Object> findOrderIndex(@Param("ActivitySeqId") int ActivitySeqId,@Param("TenantId")String TenantId,@Param("useId")String useId,@Param("channelId")String channelId);
	
	/**
	 * 修改离网用户统计send表
	 * @param sms
	 */
	@Update("UPDATE PLT_ORDER_STATISTIC_SEND SET VALID_NUM=#{VALID_NUM},SEND_SUC_NUM=#{SEND_SUC_NUM} WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID=5 AND ACTIVITY_SEQ_ID =#{ACTIVITY_SEQ_ID}")
	public void updateSmsStatistic(HashMap<String, Object> sms);
	/**
	 * 查询发送统计send表
	 * @param sms
	 */
	@Select("SELECT * FROM PLT_ORDER_STATISTIC_SEND WHERE TENANT_ID=#{tenantId} and CHANNEL_ID=#{channelId} and ACTIVITY_SEQ_ID =#{ACTIVITY_SEQ_ID} ")
	public List<HashMap<String, Object>> findSmsNoSend(HashMap<String, Object> sms);
	/**
	 * 查询离网用户id
	 * @return
	 */
	@Select("SELECT USER_ID FROM PLT_USER_OFF WHERE DATE_ID=#{dateId} AND TENANT_ID=#{TenantId} AND REC_ID >= #{min} AND REC_ID <= #{tempMax}")
	List<String> findUseId(HashMap<String, Object> userInfo);
	/**
	 * 查询离网用户id是否还在用户表
	 * @return
	 */
	@Select("SELECT USER_ID FROM PLT_USER_LABEL WHERE USER_ID IN (${useid}) AND TENANT_ID=#{TenantId} AND PARTITION_FLAG='1'")
	List<String> findUseLab(@Param("useid")String useid,@Param("TenantId")String TenantId);
	
	// --- 执行批处理,将工单移入历史 ---
    @Insert("INSERT INTO PLT_ORDER_INFO_HIS SELECT * FROM PLT_ORDER_INFO "  +
      "  WHERE  ACTIVITY_SEQ_ID =${ActivitySeqId} AND TENANT_ID=#{TenantId} AND USER_ID IN (${useId})")
    int moveOrderHis(@Param("ActivitySeqId") int ActivitySeqId,
    		@Param("TenantId")String TenantId,@Param("useId")String useId);
    
    // --- 删除工单（待执行) ---
 // --- 执行批处理,将工单移入历史 ---
    @Delete("DELETE FROM PLT_ORDER_INFO "  +
      "  WHERE  ACTIVITY_SEQ_ID =#{ActivitySeqId} AND TENANT_ID=#{TenantId} AND USER_ID IN (${useId})")
    public int deleteOrder(@Param("ActivitySeqId") int ActivitySeqId,
    		@Param("TenantId")String TenantId,@Param("useId")String useId);
    
    /**
     * 查询用户离网表是否更新
     * @return
     */
    @Select("SELECT MAX(DATE_ID) DATE_ID  FROM PLT_USER_OFF WHERE TENANT_ID=#{TenantId} AND DATE_ID NOT IN(SELECT MAX(DATE_ID) DATE_ID  FROM PLT_USER_OFF WHERE TENANT_ID=#{TenantId}); ")
    public String  findMax(@Param("TenantId")String TenantId);
    
    /**
     * 查询用户离网表是否更新
     * @return
     */
    @Select("SELECT MAX(DATE_ID) DATE_ID  FROM PLT_USER_OFF WHERE TENANT_ID=#{TenantId}; ")
    public String  findOffMax(@Param("TenantId")String TenantId);
    /**
    /**
     * 查询ods_execute_log表是否更新
     * @return
     */
    @Select("SELECT MAX(MONTH_ID) FROM ODS_EXECUTE_LOG A WHERE A.USERNAME='OPDN1_076' AND A.PROCNAME = #{procname} AND RESULT='SUCCESS' AND MONTH_ID NOT IN(SELECT MAX(MONTH_ID) FROM ODS_EXECUTE_LOG A WHERE A.USERNAME='OPDN1_076' AND A.PROCNAME = #{procname} AND RESULT='SUCCESS'); ")
    public String  findUp(@Param("procname")String procname);
    /**
     * 查询ods_execute_log表最新数据
     * @return
     */
    @Select("SELECT MAX(MONTH_ID) FROM ODS_EXECUTE_LOG A WHERE A.USERNAME='OPDN1_076' AND A.PROCNAME = #{procname} AND RESULT='SUCCESS'; ")
    public String  findOdsMax(@Param("procname")String procname);
    /**
     * 查询有效批次id数
     * @return
     */
    @Select("SELECT COUNT(*) FROM PLT_ACTIVITY_INFO WHERE REC_ID=#{ActivitySeqId} AND ACTIVITY_STATUS<>'2'")
    int findActivity(@Param("ActivitySeqId") int ActivitySeqId);
    
    /**
     * 获取离网账期最新4个
     * @param ActivitySeqId
     * @return
     */
    @Select("SELECT DISTINCT(DATE_ID) FROM PLT_USER_OFF WHERE TENANT_ID = #{tenantId} GROUP BY DATE_ID ORDER BY DATE_ID DESC limit 4;")
    List<String> getLastUseres(@Param("tenantId")String tenantId);
    
    /**
	 * 弹窗查询活动批次号
	 * @return
	 */
	@Select("SELECT DISTINCT ACTIVITY_SEQ_ID, TENANT_ID FROM ${tableName} WHERE USER_ID IN(${userid}) AND ORDER_STATUS='5' AND TENANT_ID = #{tenantId}")
	List<HashMap<String, Object>> findRecIdPopwin(@Param("userid")String userid,@Param("tenantId")String tenantId,@Param("tableName")String tableName);
	
	/**
	 * 弹窗查询工单接触结果信息
	 */
	@Select("SELECT COUNT(*) NUM "
			+" FROM ${tableName} WHERE  ACTIVITY_SEQ_ID=#{ActivitySeqId} AND TENANT_ID=#{TenantId} AND USER_ID IN(${useId}) AND ORDER_STATUS='5'")
	Integer findPopwinIndex(@Param("ActivitySeqId") int ActivitySeqId,@Param("TenantId")String TenantId,@Param("useId")String useId,@Param("tableName")String tableName);
	
	/**
	 * 弹窗渠道删除工单
	 * @param ActivitySeqId
	 * @param TenantId
	 * @param useId
	 * @return
	 */
	 @Delete("DELETE FROM ${tableName} "  +
		      "  WHERE  ACTIVITY_SEQ_ID =#{ActivitySeqId} AND TENANT_ID=#{TenantId} AND USER_ID IN (${useId})")
		    public int deleteOrderPopwin(@Param("ActivitySeqId") int ActivitySeqId,
		    		@Param("TenantId")String TenantId,@Param("useId")String useId,@Param("tableName")String tableName);
	 
	 
	 /**
	 * 查询离网用户最大最小id
	 * @return
	 */
	@Select("SELECT MAX(REC_ID) max,MIN(REC_ID) min FROM PLT_USER_OFF WHERE DATE_ID=#{dateId} AND TENANT_ID=#{TenantId}")
	HashMap<String, Object> findDateId(@Param("dateId")String dateId,@Param("TenantId")String TenantId);
	
}
