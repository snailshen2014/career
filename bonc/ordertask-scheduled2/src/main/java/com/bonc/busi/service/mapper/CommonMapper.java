package com.bonc.busi.service.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import com.bonc.busi.outer.model.PltActivityInfo;
import com.bonc.busi.service.entity.ActivityChannelExecute;
import com.bonc.busi.task.bo.ActivityFliteUsers;
import com.bonc.busi.task.bo.ActivitySucessInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;

public interface CommonMapper {
	
	// --- 查询某个租户对应的活动成功条件数据 （如果只有短信一个渠道，不提取)---
	@Select("SELECT a.REC_ID RecId,a.REC_ID ACTIVITY_SEQ_ID,a.ACTIVITY_ID ActivityId,b.SUCESSTYPE, "
	   		+ "b.SUCESSCONDITIONSQL SucessConSql,"
	   		+ "a.LAST_ORDER_CREATE_TIME  LastOrderCreateTime ,b.MATCHINGTYPE matchingType,b.SUCCESS_TYPE_CON_SQL "
	   		+ " FROM PLT_ACTIVITY_INFO a,PLT_ACTIVITY_SUCESS_CFG b "
	   		+ " WHERE DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d')>=DATE_SUB(CURDATE(),INTERVAL 1 DAY) AND a.REC_ID = b.ACTIVITY_SEQ_ID "
	   		+ "AND a.TENANT_ID=#{TenantId}  AND b.TENANT_ID=#{TenantId} ORDER BY a.REC_ID" )
	   public List<ActivitySucessInfo> getActivityForTenantId(@Param("TenantId")String TenantId);
	// --- 查询某个活动序列号对应的成功信息 ---
	@Select("SELECT a.REC_ID RecId,a.REC_ID ACTIVITY_SEQ_ID,b.SUCESSTYPE, "
	   		+ "b.SUCESSCONDITIONSQL SucessConSql,"
	   		+ "a.LAST_ORDER_CREATE_TIME  LastOrderCreateTime ,b.MATCHINGTYPE matchingType,b.SUCCESS_TYPE_CON_SQL "
	   		+ " FROM PLT_ACTIVITY_INFO a,PLT_ACTIVITY_SUCESS_CFG b "
	   		+ " WHERE a.REC_ID = b.ACTIVITY_SEQ_ID AND a.REC_ID = #{ActivitySeqId} AND b.ACTIVITY_SEQ_ID = #{ActivitySeqId} "
	   		+ "AND a.TENANT_ID=#{TenantId}  AND b.TENANT_ID=#{TenantId} ORDER BY a.REC_ID" )
	   public List<ActivitySucessInfo> getActivityForActivitySeqId(@Param("ActivitySeqId")int ActivitySeqId,
			   @Param("TenantId")String TenantId);
	   // --- 查询某个活动对应的产品列表 ---
	   @Select("SELECT PRODUCTCODE FROM PLT_ACTIVITY_PRODUCT_LIST "
	   		+ " WHERE ACTIVITY_SEQ_ID =#{ActivitySeqId} "
	   		+ " AND TENANT_ID=#{TenantId} ORDER BY PRODUCTCODE")
	   public	List<String> getProductListForActivity(@Param("ActivitySeqId")int ActivitySeqId,
			   @Param("TenantId")String TenantId);
	   // --- 根据活动序列号、渠道、租户编号查询分配的表 ---
	   @Select("SELECT   DISTINCT TABLE_NAME FROM  PLT_ORDER_TABLES_ASSIGN_RECORD_INFO WHERE TENANT_ID=#{TenantId}  AND "
	   		+ " ACTIVITY_SEQ_ID =#{ActivitySeqId}  ")
	    public String getTableNameByActivitySeqIdAndChannelId(@Param("TenantId")String TenantId,@Param("ActivitySeqId")int ActivitySeqId);
	   
   
	   
	//ScanTask
	@Select("SELECT TENANT_ID FROM TENANT_INFO WHERE STATE='1' ")
	public List<String> getValidTenantId();   
	
	@InsertProvider(type = ScanTaskOperation.class, method = "updateBeginStatus")
	public void updateBeginStatus(Map<String, Object> mapInfo);

//	@Update("UPDATE PLT_ACTIVITY_EXECUTE_LOG SET BEGIN_DATE = NOW() WHERE ACTIVITY_SEQ_ID = ${ACTIVITY_SEQ_ID} "
//			+"AND TENANT_ID = #{TENANT_ID} AND BUSI_CODE = '1016' AND CHANNEL_ID = #{CHANNEL_ID} AND BUSI_ITEM = #{ORDER_TABLE_NAME}")
//	public void updateBeginStatus(@Param("ACTIVITY_SEQ_ID") int ActivitySeqId, 
//			@Param("TENANT_ID") String TenantId,@Param("CHANNEL_ID") String ChannelId,@Param("ORDER_TABLE_NAME") String OrderTableName);

	@SelectProvider(type = ScanTaskOperation.class, method = "getPhoneCount")
	Map<String, Object> getPhoneCount(Map<String, Object> map);

	@Update("UPDATE PLT_ACTIVITY_EXECUTE_LOG SET PROCESS_STATUS = 1,END_DATE = NOW(),OPER_TIME = UNIX_TIMESTAMP(END_DATE)-UNIX_TIMESTAMP(BEGIN_DATE) "
	        +" WHERE ACTIVITY_SEQ_ID = ${ACTIVITY_SEQ_ID} AND TENANT_ID = #{TENANT_ID} AND CHANNEL_ID = #{CHANNEL_ID} AND BUSI_CODE = '1016'")
	public void updateEndStatus(@Param("ACTIVITY_SEQ_ID") int ActivitySeqId, @Param("TENANT_ID") String TenantId,
			@Param("CHANNEL_ID") String ChannelId);


	//blackandwhiteAsyn
	// --- 按照userid递增的顺序获取该租户下的数据 ---
	@Select("SELECT * FROM CLYX_ACTIVITY_FILTE_USERS WHERE TENANT_ID = #{TenantId} AND (USER_ID+0) >= #{minUserId} "
			+ "ORDER BY (USER_ID+0) ASC LIMIT ${LIMITNUM} ")
	public List<ActivityFliteUsers> getBlackandWhiteData(@Param("minUserId") int minUserId,
			@Param("TenantId") String TenantId,@Param("LIMITNUM") int limitNum);
	
	// --- 获取最小userId ---
	@Select("SELECT MIN(USER_ID) minUserId FROM CLYX_ACTIVITY_FILTE_USERS WHERE TENANT_ID = #{TenantId}")
	public HashMap<String, Object> getMinUserId(@Param("TenantId") String TenantId);
	
	// --- 向数据库插数据  --- 
	@InsertProvider(type = ScanTaskOperation.class, method = "insertIntoFilteTable")
	public void insertIntoFilteTable(HashMap<String, Object> map);
	
	
	
	
	//cleanOrder
	// --- 获取失效工单 ---
	@SelectProvider(type = ScanTaskOperation.class, method = "getInvalidActivitySeqId")
	public List<PltActivityInfo> getInvalidActivitySeqId(String tenantId);
	
	// --- 获取失效工单(活动状态) ---
	@Select("SELECT REC_ID,ACTIVITY_ID,RESERVE1 FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_STATUS = '2' AND TENANT_ID=#{tenantId} AND ((RESERVE1 IS NULL) OR (RESERVE1 <> '1'))")
	public List<PltActivityInfo> getInvalidActivitySeqIdByStatus(@Param("tenantId") String tenantId);
	
	// --- 获取失效工单(时间到期) ---
    @Select("SELECT REC_ID,ACTIVITY_ID FROM PLT_ACTIVITY_INFO WHERE ORDER_END_DATE < SYSDATE() AND ACTIVITY_STATUS IN (1,8,9) AND TENANT_ID=#{tenantId}")
	public List<PltActivityInfo> getInvalidActivitySeqIdByDate(@Param("tenantId") String tenantId);
    //UPDATE PLT_ACTIVITY_INFO SET RESERVE1 = '1' WHERE  REC_ID IN () 
    @Update("UPDATE PLT_ACTIVITY_INFO SET RESERVE1 = '1' WHERE  REC_ID IN (${recId}) AND TENANT_ID =#{tenantId}")
    public int updateRESERVE1ForActivity(@Param("recId")String recId,@Param("tenantId") String tenantId);
    /*//UPDATE PLT_ACTIVITY_INFO SET RESERVE1 = '1' WHERE  REC_ID IN () 
    @Update("UPDATE PLT_ACTIVITY_INFO SET RESERVE1 = '0' WHERE  REC_ID = #{recId} AND TENANT_ID =#{tenantId}")
    public void updateRESERVE1ForSMS(@Param("recId")Integer recId,@Param("tenantId") String tenantId);*/
	
	//--查询该活动下的各渠道工单表表名--
	@Select("SELECT CHANNEL_ID,TABLE_NAME FROM  PLT_ORDER_TABLES_ASSIGN_RECORD_INFO WHERE ACTIVITY_ID = #{activityId} AND ACTIVITY_SEQ_ID = #{activitySeqId}  AND TENANT_ID = #{tenantId} AND BUSI_TYPE = #{busiType}")
	public List<Map<String,String>> getAllInvalidOrderName(@Param("activityId") String activityId,@Param("activitySeqId") Integer activitySeqId,@Param("tenantId") String tenantId,@Param("busiType") Integer busiType);
	//-查询是否有执行完毕的短信渠道--
	@Select("SELECT COUNT(*) FROM  `plt_activity_channel_execute_interface` WHERE  STATUS = #{status} AND TENANT_ID = #{tenantId} AND ACTIVITY_ID= #{activityId}  AND CHANNEL_ID = #{channelId}  AND ACTIVITY_SEQ_ID = #{activitySeqId}")
	public int getChannelExecute(@Param("activityId") String activityId,@Param("activitySeqId") Integer activitySeqId,@Param("tenantId") String tenantId,@Param("status") String status,@Param("channelId") String channelId);
	/*//--失效活动的各个渠道号--
	@Select("SELECT  CHANNEL_ID FROM  plt_activity_channel_execute_interface WHERE ACTIVITY_SEQ_ID =  #{activitySeqId} AND ACTIVITY_ID = #{activityId} AND TENANT_ID = #{tenantId}")
	public List<String> getInvalidActivityChannels(@Param("activitySeqId") Integer activitySeqId,@Param("activityId") String activityId,@Param("tenantId") String tenantId);*/
	//--活动分配工单的PLT_ORDER_TABLES_ASSIGN_RECORD_INFO打上失效标识--
	@Update("UPDATE `PLT_ORDER_TABLES_ASSIGN_RECORD_INFO` SET BUSI_TYPE = 8 WHERE CHANNEL_ID IN(${channelListString}) AND  ACTIVITY_SEQ_ID = ${activitySeqId} AND ACTIVITY_ID = #{activityId} AND TENANT_ID = #{tenantId}")
	public int  updateAssignTableInfo(@Param("channelListString")String channelListString,@Param("activityId")String activityId,@Param("tenantId")String tenantId,@Param("activitySeqId")int activitySeqId);
	// --- 更新活动状态 ---
	@Update("${sql}")
    public void updateActvityInfoInvalid(@Param("sql")String sql);
	
	@Update("UPDATE PLT_ORDER_TABLES_ASSIGN_RECORD_INFO SET BUSI_TYPE = 8   WHERE TENANT_ID = #{tenantId} AND BUSI_TYPE = 0 AND ACTIVITY_SEQ_ID  IN (${invalidActivitys})")
    public int updateAllInvalidActivitysOrderName(@Param("invalidActivitys")String invalidActivitys,@Param("tenantId")String tenantId);
    //--系统问题出现的多余数据--
    @Select("SELECT DISTINCT  ACTIVITY_SEQ_ID AS activitySeqId,ACTIVITY_ID  AS activityId FROM  PLT_ORDER_TABLES_ASSIGN_RECORD_INFO  WHERE TENANT_ID = #{tenantId} AND BUSI_TYPE = 0 AND ACTIVITY_SEQ_ID NOT IN "
    +" (SELECT   REC_ID  FROM  plt_activity_info  WHERE TENANT_ID = #{tenantId})")
    public List<Map<String,Object>> getAllkilledActivitys(@Param("tenantId")String tenantId);
    @Update("UPDATE PLT_ORDER_TABLES_ASSIGN_RECORD_INFO SET BUSI_TYPE = 9   WHERE TENANT_ID = #{tenantId} AND BUSI_TYPE = 0 AND ACTIVITY_SEQ_ID  IN (${killedActivitys})")
    public int updateAllkilledActivitys(@Param("killedActivitys")String killedActivitys,@Param("tenantId")String tenantId);
    //--动态更新用于工单用户资料刷新的工单所属渠道以及对应的工单表字段映射--
    //--用于更新工单用户资料的渠道列表--
    @Select("SELECT DISTINCT CHANNEL_ID FROM `PLT_REFRESH_ORDER_USED_USERLABEL` WHERE TENANT_ID = #{tenantId}")
    public  List<String> getMappingChannel(@Param("tenantId") String tenantId);
    //--按照渠道号从映射表中查找工单表字段与客户标签表字段的映射关系--
    @Select("SELECT ORDER_COLUMN,USER_LABEL_COLUMN FROM `PLT_REFRESH_ORDER_USED_USERLABEL` WHERE CHANNEL_ID = #{channelId} AND TENANT_ID = #{tenantId}")
    public List<Map<String,String>> getChannelOrderMapping(@Param("channelId")String channelId,@Param("tenantId") String tenantId);
    
}
