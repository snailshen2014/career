package com.bonc.busi.outer.mapper;

import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.outer.bo.ActivityChannelExecute;
import com.bonc.busi.outer.bo.ActivityProcessLog;
import com.bonc.busi.outer.bo.OrderTableUsingInfo;
import com.bonc.busi.outer.bo.OrderTablesAssignRecord4S;
import com.bonc.busi.outer.bo.PltActivityInfo;
import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.bo.UserLabel;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PltActivityInfoDao {
	
    
    //根据活动Id 租户ID查询活动的有效批次
    @Select("/*!mycat:db_type=slave*/SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = #{activityId} AND TENANT_ID =#{tenantId} AND ACTIVITY_STATUS IN(1,8,9)")
    public List<Integer> getActivitySEQIdsById(@Param("activityId") String activityId,@Param("tenantId") String tenantId);
    
    //根据活动Id 租户ID查询活动的无效批次
    @Select("/*!mycat:db_type=slave*/SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = #{activityId} AND TENANT_ID =#{tenantId} AND ACTIVITY_STATUS NOT IN(1,8,9)")
    public List<Integer> getActivityInvalidSEQIdsById(@Param("activityId") String activityId,@Param("tenantId") String tenantId);
    
    //根据活动Id查询活动的最新有效批次
    @Select("/*!mycat:db_type=slave*/SELECT MAX(REC_ID) FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = #{activityId} AND TENANT_ID =#{tenantId} AND ACTIVITY_STATUS=1")
    public Integer getLatestActivitySEQId(@Param("activityId") String activityId,@Param("tenantId") String tenantId);

    //根据活动Id、租户Id、活动批次查询工单的各类数量
    @Select("/*!mycat:db_type=slave*/SELECT ACTIVITY_ID,ACTIVITY_SEQ_ID,TENANT_ID,CHANNEL_ID,ORI_AMOUNT,INOUT_FILTER_AMOUNT,"
    		+ "COVERAGE_FILTER_AMOUNT,BLACK_FILTER_AMOUNT,RESERVE_FILTER_AMOUNT,TOUCH_FILTER_AMOUNT,SUCCESS_FILTER_AMOUNT,REPEAT_FILTER_AMOUNT"
    		+ " FROM PLT_ACTIVITY_PROCESS_LOG  WHERE ACTIVITY_ID=#{activityId} "
    		+ "AND TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activitySeqId} ${whereSql}")
	public List<Map<String,Integer>> getOrderCount(ActivityProcessLog processLog);


    //根据条件查询获取工单表名
    @Select("/*!mycat:db_type=slave*/SELECT DISTINCT(TABLE_NAME) FROM PLT_ORDER_TABLES_ASSIGN_RECORD_INFO WHERE "
    		+ "   ACTIVITY_SEQ_ID=#{activitySeqId} AND CHANNEL_ID=#{channelId} "
    		+ "AND TENANT_ID=#{tenantId} AND BUSI_TYPE=#{busiType}")
	public String getOrderTableName(OrderTablesAssignRecord4S orderTable);

    /**
     * 根据活动Id 活动批次获取生成工单的渠道列表
     * @param activityId
     * @param activitySeqId
     * @return
     */
    @Select("/*!mycat:db_type=slave*/SELECT CHANNEL_ID FROM PLT_ACTIVITY_PROCESS_LOG WHERE ACTIVITY_ID = #{activityId} AND "
    		+ "  ACTIVITY_SEQ_ID = #{activitySeqId} AND TENANT_ID = #{tenantId}")
	public List<String> getOrderChannelListByActivityIdAndSeqId(@Param("activityId") String activityId, @Param("activitySeqId") int activitySeqId,
			@Param("tenantId") String tenantId);

    /**
     * 根据渠道Id查询所有的工单比表名
     * @param paramMap
     * @return
     */
    @Select("/*!mycat:db_type=slave*/SELECT DISTINCT TABLE_NAME FROM PLT_ORDER_TABLES_ASSIGN_RECORD_INFO WHERE CHANNEL_ID = #{channelId} AND TENANT_ID = #{tenantId} AND BUSI_TYPE=#{busiType}")
	public List<String> getAllOrderTableName(RequestParamMap paramMap);

    /**
     * 从工单表中查询满足查询条件的工单数
     * @param paramMap 查询条件
     * @return
     */
    @Select("/*!mycat:db_type=slave*/SELECT COUNT(1) FROM ${tableName} WHERE (PHONE_NUMBER=#{phoneNumber} or USER_ID=#{userId}) AND"
    		+ "  CHANNEL_ID = #{channelId} AND TENANT_ID = #{tenantId}")
	public int selectOrderCountFromTableByCondition(RequestParamMap paramMap);

    // --- 场景营销 工单查询---
    @Select("SELECT PHONE_NUMBER,DATE_FORMAT(CONTACT_DATE,'%Y-%m-%d %H:%i:%s') CONTACT_DATE "
            + " FROM ${orderTableName}  "
            + " WHERE  PHONE_NUMBER = #{phoneNum} "
            + " AND TENANT_ID = #{tenantId} "
            + " AND BEGIN_DATE>=#{contactDateStart} "
            + " AND BEGIN_DATE<=#{contactDateEnd} "
            + " AND CHANNEL_STATUS = '3' "
            + " ${envTypeAnd} ${eventIdAnd} ${channelIdAnd} ${activitySeqIdAnd} "

    )
    public List<HashMap<String,Object>> selectActivityOrder(HashMap<String, Object> req);


    //--- 场景营销根据activityId查
    @Select("SELECT REC_ID FROM plt_activity_info WHERE ACTIVITY_ID = #{activityId}")
    public List<HashMap<String, Object>> selectActivityId(@Param("activityId") Object object);

    // --- 活动挂起  ---
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS = 9,ORI_STATE=11 , BEFORE_SUSPEND_STATUS = #{ACTIVITY_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void suspendActivity(PltActivityInfo data);

    // --- 活动恢复 ---
    @Update("update PLT_ACTIVITY_INFO set ORI_STATE=9 ,ACTIVITY_STATUS= #{BEFORE_SUSPEND_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void resumeActivity(PltActivityInfo data);

    // --- 失效活动 ---
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS=2, ORI_STATE=8 , END_DATE = now()  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void expirePltActivityInfo(PltActivityInfo data);

    @Select("select * from PLT_ACTIVITY_INFO where ACTIVITY_ID=#{ActivityId} and TENANT_ID=#{TenantId} ORDER BY LAST_ORDER_CREATE_TIME")
    public List<PltActivityInfo> retrievePltActivityInfoByActivityId(@Param("ActivityId")String ActivityId,
                                                                     @Param("TenantId")String TenantId);
    
    //根据活动批次和租户Id查询活动Id
    @Select("/*!mycat:db_type=slave*/SELECT ACTIVITY_ID FROM PLT_ACTIVITY_INFO WHERE REC_ID=#{recId}  AND TENANT_ID=#{tenantId}")
    public String getActivityIdBySeqIdAndTenantId(@Param("recId") int recId, @Param("tenantId") String tenantId);

    //根据活动和渠道查询表列表
    @Select("/*!mycat:db_type=slave*/SELECT DISTINCT TABLE_NAME FROM PLT_ORDER_TABLES_ASSIGN_RECORD_INFO WHERE ACTIVITY_ID = #{activityId}  AND CHANNEL_ID = #{channelId} AND TENANT_ID = #{tenantId}  ${whereSql}")
	public List<String> getOrderTableListByActivityAndChannel(RequestParamMap paramMap);

    //根据渠到获取表列表
    @Select("/*!mycat:db_type=slave*/select DISTINCT table_name from PLT_ORDER_TABLES_ASSIGN_RECORD_INFO where CHANNEL_ID = #{channelId} and TENANT_ID =#{tenantId}  ${whereSql}")
	public List<String> getOrderTableListByChannel(RequestParamMap paramMap);

    //判断活动是否失效
    @Select("/*!mycat:db_type=slave*/select ACTIVITY_STATUS from PLT_ACTIVITY_INFO where ACTIVITY_ID=#{activityId} and REC_ID=#{recId} and TENANT_ID =#{tenantId}")
	public int isActivityInvalid(@Param("activityId") String activityId, @Param("recId") int recId,@Param("tenantId") String tenantId);

    //查询PLT_ORDER_TABLE_COLUMN_MAP_INFO获取字段映射
    @Select("select SOURCE_TABLE_COLUMN,SOURCE_TABLE_ALIAS,ORDER_COLUMN from PLT_ORDER_TABLE_COLUMN_MAP_INFO"
    		+ "  where TENANT_ID=#{tenantId} and IN_USE=1 and (column_type IN (0,2)) ")
	public List<Map<String, String>> getOrderTableColumnMap(RequestParamMap param);

    //根据手机号码查询工单表名
    @Select("/*!mycat:db_type=slave*/select DISTINCT ORDER_TABLE_NAME FROM ${tableName} where PHONE_NUMBER=#{phoneNumber} and  CHANNEL_ID=#{channelId} and "
    		+ "  TENANT_ID=#{tenantId}")
	public List<String> getOrderTableNameByPhoneNumber(RequestParamMap param);

    //根据活动、批次查询PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中可执行的渠道列表
    @Select("/*!mycat:db_type=slave*/SELECT CHANNEL_ID FROM PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE WHERE ACTIVITY_ID=#{activityId} AND  "
    		+ " ACTIVITY_SEQ_ID=#{activitySeqId} AND TENANT_ID=#{tenantId} AND STATUS='1' ")
	public List<String> getExecuteableChannelList(RequestParamMap param);
    
    //获取所有的租户
    @Select("SELECT TENANT_ID FROM TENANT_INFO WHERE STATE='1' ")
	public List<String> getValidTenantId();

    //PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中查询指定租户下STATUS=2的记录
    @Select("/*!mycat:db_type=slave*/select REC_ID as recId,ACTIVITY_SEQ_ID as activitySeqId ,ACTIVITY_ID as activityId , TENANT_ID as tenantId,CHANNEL_ID as channelId ,STATUS,GEN_DATE as genDate from PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE "
    		+ " where STATUS='2' and TENANT_ID=#{tenantId}")
	public List<ActivityChannelExecute> getChannelExecuteList(@Param("tenantId") String tenantId);

	//PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中移除STATUS=2的记录到PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE_HIS
	@Insert("insert into PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE_HIS(ACTIVITY_SEQ_ID, ACTIVITY_ID, TENANT_ID, CHANNEL_ID, STATUS,GEN_DATE,HIS_DATE) values  "
			+ " (#{activitySeqId}, #{activityId},#{tenantId},#{channelId}, #{status},#{genDate},now())")
	public void removeChannelToHis(ActivityChannelExecute temp);

	//PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中删除记录
	@Delete("delete from PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE where ACTIVITY_ID=#{activityId} and ACTIVITY_SEQ_ID=#{activitySeqId} and "
			+ " TENANT_ID=#{tenantId} and CHANNEL_ID=#{channelId} and STATUS=#{status} ") 
	public void deleteChannel(ActivityChannelExecute temp);

	//根据渠道查询可执行的活动和批次
	@Select("/*!mycat:db_type=slave*/select REC_ID as recId,ACTIVITY_SEQ_ID as activitySeqId ,ACTIVITY_ID as activityId , TENANT_ID as tenantId,CHANNEL_ID as channelId ,STATUS,GEN_DATE as genDate from PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE "
    		+ " where STATUS=#{touchCode} and TENANT_ID=#{tenantId} and CHANNEL_ID=#{channelId}")
	public List<ActivityChannelExecute> getExecuteableActivityInfo(ActivityChannelExecute executeInterface);

	//PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中记录的状态
	@Update("update PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE set STATUS=#{touchCode} where TENANT_ID=#{tenantId} "
			+ "  and ACTIVITY_SEQ_ID=#{activitySeqId} and CHANNEL_ID=#{channelId}")
	public void updateExecuteInterfaceStatus(ActivityChannelExecute executeInterface);

	//查询工单表当前已使用的数量
	@Select("/*!mycat:db_type=slave*/select CURRENT_AMOUNT from PLT_ORDER_TABLES_USING_INFO where TABLE_NAME=#{tableName} and TENANT_ID=#{tenantId} and USING_BUSI_TYPE=#{busiType}")
	public int getUsedCount(OrderTableUsingInfo usingInfo);

	//更新工单表使用数量
	@Update("update PLT_ORDER_TABLES_USING_INFO set CURRENT_AMOUNT=#{cucrrentCount},AMOUNT_LAST_UPDATE_TIME=now() where TABLE_NAME=#{tableName} and TENANT_ID=#{tenantId} and USING_BUSI_TYPE=#{busiType}")
	public void updateOrderTableUsingInfo(OrderTableUsingInfo usingInfo);

	//更新工单表使用状态
	@Update("update PLT_ORDER_TABLES_USING_INFO set USING_STATUS=#{usingStatus},AMOUNT_LAST_UPDATE_TIME=now() where TABLE_NAME=#{tableName} and TENANT_ID=#{tenantId} and USING_BUSI_TYPE=#{busiType}")

	public void updateOrderTableUsingStatus(OrderTableUsingInfo usingInfo); 
	
	//查询指定的渠道的批次是否执行完毕，如果执行完毕，在PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE_HIS会有记录
	@Select("/*!mycat:db_type=slave*/select count(1) from PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE_HIS where TENANT_ID=#{tenantId} and CHANNEL_ID=#{channelId} and ACTIVITY_SEQ_ID=#{activitySeqId} and STATUS='2'")
	public int getChannelFinishedCount(ActivityChannelExecute channelExecute);

	//查询批次id
    @Select("SELECT IFNULL(REC_ID,0) FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = #{activityId} AND TENANT_ID=#{tenantId} limit 1")
    Integer selectActivitySeqid(HashMap<String, Object> req);

    //查询某个活动最新的批次
    @Select("/*!mycat:db_type=slave*/SELECT REC_ID from PLT_ACTIVITY_INFO where ACTIVITY_ID=#{activityId} and TENANT_ID=#{tenantId} "
    		+ "   ORDER BY LAST_ORDER_CREATE_TIME")
	public List<Integer> getLatestActivitySeqId(RequestParamMap param);

    //查询工单生成的步骤Map集合： 查询PLT_ACTIVITY_EXECUTE_LOG和PLT_ACTIVITY_EXECUTE_BUSICODE_DEF
    @Select("${myCatSql} "
    		+"  SELECT e.CHANNEL_ID,e.ACTIVITY_ID,e.ACTIVITY_SEQ_ID,e.TENANT_ID, b.DESC as log_info,e.BUSI_ITEM as BUSI_ITEM_1,OPER_TIME,"
    		+ " date_format(BEGIN_DATE,'%Y-%m-%d %H:%i:%s') as START_TIME, "
    		+ " date_format(BEGIN_DATE,'%Y-%m-%d') as log_date,date_format(BEGIN_DATE,'%T') as log_time,"
    		+ " date_format(END_DATE,'%Y-%m-%d %H:%i:%s') as END_DATE"
          + " FROM  PLT_ACTIVITY_EXECUTE_LOG e,PLT_ACTIVITY_EXECUTE_BUSICODE_DEF b"
          + " WHERE e.ACTIVITY_ID=#{activityId} AND e.ACTIVITY_SEQ_ID=#{activitySeqId} AND e.BUSI_CODE = b.BUSI_CODE"
          + " AND ACTIVITY_SEQ_ID in (SELECT ACTIVITY_SEQ_ID FROM PLT_ACTIVITY_EXECUTE_LOG  GROUP BY ACTIVITY_SEQ_ID HAVING COUNT(ACTIVITY_SEQ_ID)>2)"
          + " AND e.TENANT_ID =#{tenantId} AND b.TENANT_ID =#{tenantId} GROUP BY e.REC_ID DESC")
	public List<Map<String, Object>> getOrderGenStepName(RequestParamMap param);

    //查询plt_order_tables_using_info获取所有的工单表名
    @Select("/*!mycat:db_type=slave*/SELECT TABLE_NAME FROM PLT_ORDER_TABLES_USING_INFO WHERE TENANT_ID=#{tenantId}")
	public List<String> getOrderTableNameByUsingInfo(@Param("tenantId") String tenantId);

    //查询工单表中实际的工单的数量
    @Select("/*!mycat:db_type=slave*/SELECT COUNT(1) FROM ${tableName} WHERE TENANT_ID=#{tenantId}")
	public int getOrderCountInOrderTable(@Param("tableName") String tableName,@Param("tenantId") String tenantId);

    //更新plt_order_tables_using_info中工单表的实际使用数量
    @Update("UPDATE PLT_ORDER_TABLES_USING_INFO SET CURRENT_AMOUNT=#{count} WHERE TABLE_NAME=#{tableName} AND TENANT_ID=#{tenantId}")
	public void updateOrderTableUsingCount(@Param("tableName")String tableName,@Param("count")int orderCount, @Param("tenantId")String tenantId);

    
    
    //根据租户ID查询对应的租户库名：clyx_app_gd_hlj,clyx_app_gd_hainan,sichuan
    @Select("SELECT FIELD_VALUE FROM PLT_STATIC_CODE WHERE TABLE_NAME='INFORMATION_SCHEMA_COLUMNS' AND FIELD_KEY='SCHEMA_NAME' AND TENANT_ID = #{tenantId}")
    public String getSchemaName(@Param("tenantId")String tenantId);
    //查询用户标签表的字段全部返回
    @Select("${myCatSql}"
    		+ "SELECT DISTINCT COLUMN_NAME AS columnName,CHARACTER_MAXIMUM_LENGTH AS length,COLUMN_COMMENT AS comment,DATA_TYPE AS dataType FROM information_schema.COLUMNS"
    		+ " WHERE TABLE_NAME = #{validTableName} AND TABLE_SCHEMA = #{tableSchema}")
	public List<Map<String, Object>> getUserLabel(RequestParamMap param);

    //模糊查询用户标签表的字段
    @Select("${myCatSql}"
    		+ "SELECT DISTINCT COLUMN_NAME AS columnName,CHARACTER_MAXIMUM_LENGTH AS length,COLUMN_COMMENT AS comment,DATA_TYPE AS dataType FROM information_schema.COLUMNS"
    		+ " WHERE TABLE_NAME IN ('PLT_USER_LABEL_0','PLT_USER_LABEL_1') AND TABLE_SCHEMA = #{tableSchema} "
    		+ "AND COLUMN_COMMENT LIKE '%${comment}%' ")
	public List<Map<String, Object>> getUserLabelIndistinct(RequestParamMap param);
    
    //查询有效的分区数据标识
    @Select("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE CFG_KEY = #{validFlagKey} ")
	public String getValidFlag(String validFlagKey);
    
    //查询PLT_DEMAND_SYN_USERLABEL表中是否存在指定的租户的标签信息
    @Select("/*!mycat:db_type=slave*/SELECT COUNT(1) FROM PLT_DEMAND_SYN_USERLABEL WHERE COLUMN_NAME=#{columnName} AND TENANT_ID=#{tenantId}")
    public int queryIsExistTenantUserLable(@Param("tenantId") String tenantId,@Param("columnName") String columnName);

    //保存需要的用户标签信息到PLT_DEMAND_SYN_USERLABEL
    @Insert("INSERT INTO PLT_DEMAND_SYN_USERLABEL(TENANT_ID,COLUMN_NAME,DATA_TYPE,LENGTH,LABEL_COMMENT) "
    		+ " VALUES(#{tenantId},#{userLabel.xCloudColumn},#{userLabel.dataType},${userLabel.length},#{userLabel.columnDesc})")
	public void saveDemandUserLabel(@Param("tenantId") String tenantId, @Param("userLabel") UserLabel userLabel);

    //更新PLT_DEMAND_SYN_USERLABEL中用户标签的信息
    @Update("UPDATE PLT_DEMAND_SYN_USERLABEL SET DATA_TYPE=#{userLabel.dataType},LENGTH=${userLabel.length},LABEL_COMMENT=#{userLabel.columnDesc}"
    		+ " WHERE COLUMN_NAME=#{userLabel.xCloudColumn} AND TENANT_ID=#{tenantId}")
	public void updateDemandUserLabel(@Param("tenantId") String tenantId, @Param("userLabel") UserLabel userLabel);

    //检查表里是否已经存在这个渠道的标签了
    @Select("/*!mycat:db_type=slave*/SELECT COUNT(1) FROM PLT_REFRESH_ORDER_USED_USERLABEL WHERE CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId}")
	public int checkLabelIsAreadyExsit(@Param("tenantId")String tenantId, @Param("channelId")String channelId);

    //删除表中指定渠道下的用户标签
    @Delete("DELETE FROM PLT_REFRESH_ORDER_USED_USERLABEL WHERE CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId}")
	public void deleteAreadyExistLabel(@Param("tenantId")String tenantId, @Param("channelId")String channelId);

    //查询PLT_ORDER_CHANNEL_COLUMN_MAPPING中与userLable对应的工单表的字段名称、标签描述信息以及标签是否在
    @Select("/*!mycat:db_type=slave*/SELECT ORDER_COLUMN,COLUMN_DESC,IS_USE FROM PLT_ORDER_CHANNEL_COLUMN_MAPPING WHERE CHANNEL_ID=#{channelId} AND XCLOUD_COLUMN=#{userLable} AND IS_USE=1 AND TENANT_ID=#{tenantId}")
	public Map<String, String> queryOrderColumnNameFromMapping(@Param("channelId")String channelId, @Param("userLable")String userLable,@Param("tenantId") String tenantId);

    //PLT_ORDER_CHANNEL_COLUMN_MAPPING中插入需要刷新的用户标签的记录
    @Insert("INSERT INTO PLT_REFRESH_ORDER_USED_USERLABEL(TENANT_ID,CHANNEL_ID,ORDER_COLUMN,USER_LABEL_COLUMN,LABEL_DESC) "
    		+ " VALUES(#{tenantId},#{channelId},#{orderColumn},#{userLable},#{colunDesc})")
	public void insertOrderUsedLabel(@Param("tenantId")String tenantId, @Param("channelId")String channelId, @Param("orderColumn")String orderColumn, @Param("userLable")String userLable,
			@Param("colunDesc")String colunDesc);

    //插入一条sys_common_cfg配置
    @Insert("INSERT INTO SYS_COMMON_CFG(CFG_KEY,CFG_VALUE,NOTE) VALUES (#{cfgKey},#{cfgValue},#{note})")
	public void insertSysCommonCfg(@Param("cfgKey") String cfgKey, @Param("cfgValue") String cfgValue, @Param("note") String note);

    //更新sys_common_cfg配置项的值
    @Update("UPDATE SYS_COMMON_CFG SET CFG_VALUE = #{cfgValue} WHERE CFG_KEY=#{cfgKey}")
	public void updateSysCommonCfg(@Param("cfgKey") String cfgKey, @Param("cfgValue")String cfgValue);


    //根据活动更新
    @Update("UPDATE PLT_ACTIVITY_SUCESS_CFG SET MATCHINGTYPE =#{matchingType} ,SUCESSCONDITIONSQL =#{successConditionSQL}," +
            " SUCESSNAME=#{successName},SUCESSPOINTS=#{successPoint},SUCESSREWARD=#{successReward}" +
            ",SUCESSTYPE=#{successType},SUCCESS_TYPE_CON_SQL= #{successTypeConditionSql},SUCESSCONDITIONE=#{successCondition} " +
            " WHERE ACTIVITY_ID =#{activityId} AND TENANT_ID =#{tenantId} ")
    void updateSuccessStandardPo(JSONObject successStandardPo);

    //根据批次删成功产品
    @Delete("DELETE FROM PLT_ACTIVITY_PRODUCT_LIST WHERE TENANT_ID =#{tenantId} AND ACTIVITY_SEQ_ID =#{seqId} ")
    void deleteSuccessProductBySeqId(@Param("seqId") Integer seqId,@Param("tenantId")  String tenantId);

    //插入成功标准产品
    @Insert("INSERT INTO PLT_ACTIVITY_PRODUCT_LIST(TENANT_ID,ACTIVITY_ID,PRODUCTCODE,PRODUCTNAME,PRODUCTDES,ISVALID,PRODUCTDISTRICT,ACTIVITY_SEQ_ID)" +
            " VALUES (#{tenantId},#{activityId},#{productCode},#{productName},#{productDes},'1',#{productDistrict},#{activitySeqId})")
    void insertSuccessProduct(JSONObject successProduct);
}
