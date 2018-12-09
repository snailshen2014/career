package com.bonc.busi.backpage.mapper;

import com.bonc.busi.backpage.bo.CreateTenantBo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MQZ on 2017/8/17.
 */
public interface BackPageMapper {

    /*
        获取工单表占有信息
     */
    @Select("SELECT TENANT_ID,TABLE_NAME,TOTAL_LIMIT AS MAX_NUM, CURRENT_AMOUNT AS  USED_NUM, " +
            "ROUND(CURRENT_AMOUNT/TOTAL_LIMIT * 100,1) AS USED_RATE from  " +
            "PLT_ORDER_TABLES_USING_INFO where TENANT_ID = #{tenantId} ORDER BY USED_NUM DESC")
    public List<HashMap> getUsedTableList(@Param("tenantId") String tenantId);

    /*
        获取当前有效租户信息
     */
    @Select("SELECT TENANT_ID,TENANT_NAME,PROV_ID FROM TENANT_INFO WHERE STATE='1'")
    List<Map<String,Object>> getValidTenantInfo();

    /*
        获取SysCfg信息
     */
    @Select("SELECT * FROM  SYS_COMMON_CFG")
    List<HashMap> getSysCfg();
    
    @Select("SELECT CFG_VALUE from SYS_COMMON_CFG WHERE CFG_KEY=#{cfgKey}")
    String getCfgValue(@Param("cfgKey") String cfgKey);

    /*
        delete cfgconfg
     */
    @Select("DELETE FROM SYS_COMMON_CFG WHERE CFG_KEY = #{key}")
    void delCfgRow(@Param("key") String key);

    @Select("SELECT count(1) FROM  SYS_COMMON_CFG WHERE CFG_KEY = #{key}")
    Integer getSysCfgByKey(String key);

    @Update("UPDATE SYS_COMMON_CFG SET CFG_VALUE = #{CFG_VALUE} , NOTE = #{NOTE} WHERE CFG_KEY = #{CFG_KEY}")
    void updateCfg(Map cfgMap);

    @Insert("INSERT INTO SYS_COMMON_CFG VALUES(#{CFG_KEY},#{CFG_VALUE} , #{NOTE})")
    void insertCfg(Map cfgMap);

    //查询活动的批次： 查询最新的一个批次和活动名称
    @Select("SELECT REC_ID, ACTIVITY_NAME FROM PLT_ACTIVITY_INFO "
    		+ " WHERE ACTIVITY_ID=#{activityId} AND TENANT_ID=#{tenantId} ORDER BY LAST_ORDER_CREATE_TIME DESC LIMIT 1 ")
	public Map<String, Object> selectActivityLatestSeqIdAndName(@Param("tenantId") String tenantId, @Param("activityId")String activityId);

    //根据活动批次查询该批次的busi_code,以此判断该批次下工单的生成状态
    @Select("SELECT BUSI_CODE FROM PLT_ACTIVITY_EXECUTE_LOG WHERE ACTIVITY_ID=#{activityId} "
    		+ " AND  ACTIVITY_SEQ_ID =#{activitySeqId} AND TENANT_ID=#{tenantId} ")
	public List<Integer> selectBusiCode(@Param("activityId") String activityId, @Param("activitySeqId")Integer activitySeqId, @Param("tenantId")String tenantId);

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
	public List<Map<String, Object>> selectActivityOrderGenerateSteps(Map<String, Object> requestMap);

    //根据条件删除PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE里的信息
    @Delete("DELETE FROM ${tableName} WHERE ACTIVITY_ID=#{activityId} AND ACTIVITY_SEQ_ID=#{activitySeqId} AND TENANT_ID=#{tenantId} AND STATUS='1' ")
	public void deleteExecuteInterface(Map<String, Object> delMap);

    //根据条件删除活动表里的信息
    @Delete("DELETE FROM ${tableName} WHERE ACTIVITY_ID=#{activityId} AND REC_ID=#{activitySeqId} AND TENANT_ID=#{tenantId}")
	public void deleteActivityInfo(Map<String, Object> delMap);



    // 查询plt_order_table_column_map_info
    @Select("SELECT * FROM PLT_ORDER_TABLE_COLUMN_MAP_INFO WHERE TENANT_ID=#{tenantId}")
    List<HashMap> XSqlSelect (@Param("tenantId") String tenantId);
    // plt_order_data_source_tables_def
    @Select("SELECT * FROM PLT_ORDER_DATA_SOURCE_TABLES_DEF WHERE TENANT_ID=#{tenantId}")
    List<HashMap> XSqlTable(@Param("tenantId") String tenantId);
    // plt_order_data_condition
    @Select("SELECT * FROM PLT_ORDER_DATA_CONDITION WHERE TENANT_ID=#{tenantId}")
    List<HashMap> XSqlWhere(@Param("tenantId") String tenantId);
    @Select(" ${mycatSql} DELETE FROM PLT_ORDER_TABLE_COLUMN_MAP_INFO WHERE ORDER_COLUMN_SEQ = #{key}")
    void delSelectRow(@Param("key") String key ,@Param("mycatSql") String mycatSql);
    @Select("${mycatSql} DELETE FROM PLT_ORDER_DATA_SOURCE_TABLES_DEF WHERE TABLE_SEQ = #{key}")
    void delTableRow(@Param("key") String key,@Param("mycatSql") String mycatSql);
    @Select("${mycatSql} DELETE FROM PLT_ORDER_DATA_CONDITION WHERE CON_SEQ = #{key}")
    void delWhereRow(@Param("key") String key,@Param("mycatSql") String mycatSql);
    @Select("SELECT COUNT(1) FROM  PLT_ORDER_TABLE_COLUMN_MAP_INFO where ORDER_COLUMN_SEQ = #{seq} ")
    Integer getSelectByKey(@Param("seq") String order_column_seq);
    @Select("SELECT COUNT(1) FROM  PLT_ORDER_DATA_SOURCE_TABLES_DEF where TABLE_SEQ = #{seq} ")
    Integer getTableByKey(@Param("seq") String table_seq);
    @Select("SELECT COUNT(1) FROM  PLT_ORDER_DATA_CONDITION where CON_SEQ = #{seq} ")
    Integer getWhereByKey(@Param("seq") String con_seq);
    @Update("${mycatSql} " +
            "UPDATE PLT_ORDER_TABLE_COLUMN_MAP_INFO SET ORDER_COLUMN = #{ORDER_COLUMN} ," +
            "ORDER_COLUMN_DES =#{ORDER_COLUMN_DES}, SOURCE_TABLE_ALIAS=#{SOURCE_TABLE_ALIAS}," +
            "SOURCE_TABLE_COLUMN=#{SOURCE_TABLE_COLUMN},SQL_BLOCK=#{SQL_BLOCK},IN_USE=#{IN_USE}," +
            "COLUMN_TYPE=#{COLUMN_TYPE} where TENANT_ID=#{TENANT_ID} AND ORDER_COLUMN_SEQ = #{ORDER_COLUMN_SEQ}")
    void updateSelect(Map map);
    @Update("${mycatSql} " +
            "UPDATE PLT_ORDER_DATA_SOURCE_TABLES_DEF SET SOURCE_TABLE=#{SOURCE_TABLE},TABLE_ALIAS=#{TABLE_ALIAS}," +
            "TABLE_TYPE=#{TABLE_TYPE} WHERE TENANT_ID=#{TENANT_ID} AND TABLE_SEQ=#{TABLE_SEQ}")
    void updateTable(Map map);
    @Update("${mycatSql} " +
            "UPDATE PLT_ORDER_DATA_CONDITION SET CON_SQL=#{CON_SQL},CON_TYPE=#{CON_TYPE},CON_ADD=#{CON_ADD}" +
            " WHERE TENANT_ID=#{TENANT_ID} AND CON_SEQ=#{CON_SEQ}")
    void updateWhere(Map map);

    @Insert("${mycatSql}   " +
            " INSERT INTO PLT_ORDER_TABLE_COLUMN_MAP_INFO(ORDER_COLUMN_SEQ,ORDER_COLUMN,ORDER_COLUMN_DES,TENANT_ID,SOURCE_TABLE_ALIAS,SOURCE_TABLE_COLUMN,SQL_BLOCK,IN_USE,COLUMN_TYPE) VALUES " +
            "( #{ORDER_COLUMN_SEQ} , #{ORDER_COLUMN} , #{ORDER_COLUMN_DES} , " +
            "#{TENANT_ID} , #{SOURCE_TABLE_ALIAS} , #{SOURCE_TABLE_COLUMN} , #{SQL_BLOCK} , #{IN_USE} , #{COLUMN_TYPE} )")
    void insertSelect(Map map);
    @Insert("${mycatSql} " +
            " INSERT INTO PLT_ORDER_DATA_SOURCE_TABLES_DEF(SOURCE_TABLE,TABLE_SEQ,TABLE_ALIAS,TENANT_ID,TABLE_TYPE) VALUES ( #{SOURCE_TABLE} , #{TABLE_SEQ} , #{TABLE_ALIAS} , " +
            "#{TENANT_ID} , #{TABLE_TYPE} )")
    void insertTable(Map map);
    @Insert(" ${mycatSql} " +
            " INSERT INTO PLT_ORDER_DATA_CONDITION(CON_SEQ,CON_SQL,TENANT_ID,CON_TYPE,CON_ADD) VALUES ( #{CON_SEQ} , #{CON_SQL} , #{TENANT_ID} , " +
            "#{CON_TYPE} , #{CON_ADD})")
    void insertWhere(Map map);

    //对工单表的容量进行扩容
    @Update("UPDATE PLT_ORDER_TABLES_USING_INFO SET TOTAL_LIMIT = TOTAL_LIMIT + 5000000 where TENANT_ID=#{tenantId} ")
	public void addTableCapacity(@Param("tenantId") String tenantId);

    @Insert("INSERT INTO TENANT_INFO (TENANT_ID,TENANT_NAME,PROV_ID,STATE) VALUES (#{tenantId},#{tenantName},#{provId},'1')")
    void addTenantRecord(CreateTenantBo cfg);

    //根据条件查询活动表中活动的数量
    @Select("SELECT  COUNT(1)  FROM  PLT_ACTIVITY_INFO  WHERE DATE_FORMAT(LAST_ORDER_CREATE_TIME,'%Y-%m-%d')  = #{date}  and  TENANT_ID = #{tenantId} and ACTIVITY_STATUS='0' ")
	public int queryFailedActivityNum(@Param("date") String date,@Param("tenantId") String tenantId);
    
  //根据条件查询活动表中活动的数量
    @Select("SELECT  COUNT(1)  FROM  PLT_ACTIVITY_INFO  WHERE DATE_FORMAT(LAST_ORDER_CREATE_TIME,'%Y-%m-%d')  = #{date}  and  TENANT_ID = #{tenantId}")
	public int queryTotalActivityNum(@Param("date") String date,@Param("tenantId") String tenantId);
    
    //更新逻辑节点
    @Update("update SYS_COMMON_CFG set CFG_VALUE=CONCAT(CFG_VALUE,#{nodename}) where CFG_KEY='SYS_LOGIC_NODE_NAMES'")
    public void addnodename(@Param("nodename") String nodename);
    
    
   //查询所有的表名
    @Select("${mycatSql}"
    		+"select table_name from information_schema.`TABLES` WHERE TABLE_SCHEMA= #{mysqlschema}")
    public List<String> tablename(@Param("mycatSql") String mycatSql,@Param("mysqlschema") String mysqlschema);
    
    
    @Update("update SYS_COMMON_CFG set CFG_VALUE=CONCAT(CFG_VALUE,#{nodename}) where CFG_KEY='SYS_CHANNEL_NODE_NAMES'")
	public void addCnodename(@Param("nodename")String nodename);
}
