package com.bonc.busi.sys.mapper;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import com.bonc.busi.sys.entity.*;

public interface SysMapper {
	
	// --- 查询有效的租户编号 ---
    @Select("SELECT TENANT_ID FROM TENANT_INFO WHERE STATE='1' ")
	public List<String> getValidTenantId();
    // ---查询有效租户数量 ---
    @Select("SELECT COUNT(*) FROM TENANT_INFO WHERE STATE='1' ")
	public int getValidTenantNum();
    // ---提取系统变量表某个KEY对应的VALUE---
    @Select("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE CFG_KEY=#{CFG_KEY}")
    public String getSystemValueByKey(@Param("CFG_KEY")String conValue);
    // ---更新系统变量表的某个键值---
    @Update("UPDATE SYS_COMMON_CFG SET CFG_VALUE=#{CFG_VALUE} WHERE CFG_KEY=#{CFG_KEY}")
    public int updateSystemValueByKey(@Param("CFG_KEY")String colName,@Param("CFG_VALUE")String conValue);
    // ---添加信息到ORDER_CONTROL_LOG表中---
    @Insert("INSERT INTO ORDER_CONTROL_LOG(CONTROL_CODE,TENANT_ID,BUSI_TIME,"
    		+ "BUSI_RESULT,BUSI_MESSAGE) VALUES(#{CONTROL_CODE},#{TENANT_ID},"
    		+"#{BUSI_TIME},#{BUSI_RESULT},#{BUSI_MESSAGE})")
    public void insertOrderControlLog(OrderControlLog rec);
    // ---添加信息到SYS_LOG_月份表---
    @Insert("INSERT INTO SYS_LOG_${MONTHSTR}(TENANT_ID,LOG_TIME,APP_NAME,BUSI_ITEM_1,BUSI_ITEM_2,"
    		+ "BUSI_ITEM_3,BUSI_ITEM_4,BUSI_ITEM_5,LOG_MESSAGE) VALUES(#{TENANT_ID},#{LOG_TIME},"
    		+"#{APP_NAME},#{BUSI_ITEM_1},#{BUSI_ITEM_2},#{BUSI_ITEM_3},#{BUSI_ITEM_4},#{BUSI_ITEM_5},#{LOG_MESSAGE})")
    public int insertSysLogMonth(Map<String, Object> map);
    // ---提取SYS_MAP_INT表某个KEY对应的VALUE---
    @Select("SELECT VALUE FROM SYS_MAP_INT WHERE KEY=#{KEY}")
    public Integer getSysMapValueByKey(@Param("KEY")String colName);
    // ---SYS_MAP_INT表的value值＋1---
    @Update("UPDATE SYS_MAP_INT SET VALUE=VALUE+1 WHERE KEY=#{KEY} ")
    public void IncreaseSysMapInt(@Param("KEY")String colName);
    
	 // --- 插入通用日志表 ---
//    @Insert("INSERT INTO SYS_LOG  (TENANT_ID,LOG_TIME,APP_NAME,BUSI_ITEM_1,BUSI_ITEM_2,BUSI_ITEM_3,BUSI_ITEM_4,BUSI_ITEM_5,"
//    		+ "LOG_MESSAGE)   "
//    		+ "VALUES (#{TENANT_ID},#{LOG_TIME},#{APP_NAME},#{BUSI_ITEM_1},#{BUSI_ITEM_2},#{BUSI_ITEM_3},#{BUSI_ITEM_4},#{BUSI_ITEM_5}"
//    		+ ",#{LOG_MESSAGE}) ")
//    public void insertSysLog(SysLog  logData);

}
