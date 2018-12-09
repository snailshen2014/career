/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: GlobalCFGMapper.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.globalCFG.mapper
 * @Description: GlobalCFGMapper
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月16日 上午11:15:32
 * @version: V1.0  
 */

package com.bonc.busi.globalCFG.mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.entity.PltCommonLog;
import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;

/**
 * @ClassName: GlobalCFGMapper
 * @Description: GlobalCFGMapper
 * @author: LiJinfeng
 * @date: 2016年12月16日 上午11:15:32
 */
public interface GlobalCFGMapper {
	
	// --- 查询系统变量表 --
    @Select("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE CFG_KEY = #{strCfgKey}")
  	public String getGlobalCFG(@Param("strCfgKey") String strCfgKey);
    
    // --- 设置变量系统 --
    @Update("UPDATE SYS_COMMON_CFG SET CFG_VALUE = #{strCfgValue} WHERE CFG_KEY = #{strCfgKey}")
    public void setGlobalCFG(@Param("strCfgKey") String strCfgKey,@Param("strCfgValue") String strCfgValue);
    
    // --- 查询租户列表 --
    @Select("SELECT TENANT_ID FROM TENANT_INFO WHERE STATE = #{state}")
  	public List<String> getTenantIdList(@Param("state") String state);
    
    // --- 查询日志 --
    @Select("SELECT COUNT(REC_ID) FROM PLT_WX_LOG WHERE ACTIVITY_SEQ_ID = #{activitySeqId}")
    public Integer getCountLogByActivitySeqID(@Param("activitySeqId")Integer activitySeqId);
    
    // ----插入日志 ----
    @Insert("INSERT INTO PLT_WX_LOG(ACTIVITY_ID,ACTIVITY_SEQ_ID,TENANT_ID,LOG_MESSAGE,LAST_INPUT_TIME,LAST_INPUT_DATE,"
    		+ "FIRST_INPUT_TIME,FIRST_INPUT_DATE) "
    		+ "VALUES(#{wxActivityInfo.activityId},#{wxActivityInfo.recId},#{wxActivityInfo.tenantId},#{logMessage},"
    		+ "#{inputTime},#{inputDate},#{inputTime},#{inputDate})")
  	public Integer insertLog(@Param("wxActivityInfo") WXActivityInfo wxActivityInfo,
  			@Param("logMessage")String logMessage,@Param("inputTime")Date inputTime,
  			@Param("inputDate")String inputDate);
    
    // ----更新日志 ----
    @Update("UPDATE PLT_WX_LOG SET LOG_MESSAGE = #{logMessage},LAST_INPUT_TIME = #{inputTime},"
    		+ "LAST_INPUT_DATE = #{inputDate} WHERE ACTIVITY_SEQ_ID = #{wxActivityInfo.recId}")
  	public Integer updateLogByActivitySeqID(@Param("wxActivityInfo") WXActivityInfo wxActivityInfo,
  			@Param("logMessage")String logMessage,@Param("inputTime")Date inputTime,
  			@Param("inputDate")String inputDate);
    
    // ----查询序列号 ----
    @Select("SELECT INIT_VALUE,CUR_VALUE,STEP,MAX_VALUE FROM SYS_SEQUENCE WHERE  SEQUENCE_NAME = #{sequenceName}")
    public HashMap<String,Object> getSequenceInfo(@Param("sequenceName")String sequenceName);
    
    // ----初始序列号 ----
    @Select("UPDATE SYS_SEQUENCE SET CUR_VALUE = INIT_VALUE WHERE SEQUENCE_NAME = #{sequenceName}")
    public HashMap<String,Object> initSequence(@Param("sequenceName")String sequenceName);
    
    // ----更新序列号 ----
    @Select("update SYS_SEQUENCE SET CUR_VALUE = CUR_VALUE + STEP where SEQUENCE_NAME = #{sequenceName}")
    public HashMap<String,Object> updateSequence(@Param("sequenceName")String sequenceName);
    
    // ----插入统一日志 ----
    @Insert("INSERT INTO PLT_COMMON_LOG(LOG_TYPE,SERIAL_ID,SPONSOR,START_TIME,BUSI_CODE,TENANT_ID,YYMMDD,END_TIME,"
    		+ "DURATION,DEST_NUM,BUSI_DESC,BUSI_NUM_1,BUSI_NUM_2,BUSI_TIME_1,BUSI_TIME_2,BUSI_MONEY_1,"
    		+ "BUSI_MONEY_2,BUSI_ITEM_1,BUSI_ITEM_2,BUSI_ITEM_3,BUSI_ITEM_4,BUSI_ITEM_5,BUSI_ITEM_6,BUSI_ITEM_7,"
    		+ "BUSI_ITEM_8,BUSI_ITEM_9,BUSI_ITEM_10,BUSI_ITEM_11,BUSI_ITEM_12,BUSI_ITEM_13,BUSI_ITEM_14,BUSI_ITEM_15)  "
    		+ "VALUES (#{LOG_TYPE},#{SERIAL_ID},#{SPONSOR},#{START_TIME},#{BUSI_CODE},#{TENANT_ID},#{YYMMDD},#{END_TIME}"
    		+ ",#{DURATION},#{DEST_NUM},#{BUSI_DESC},#{BUSI_NUM_1},#{BUSI_NUM_2},#{BUSI_TIME_1},#{BUSI_TIME_2},"
    		+ "#{BUSI_MONEY_1},#{BUSI_MONEY_2},#{BUSI_ITEM_1},#{BUSI_ITEM_2},#{BUSI_ITEM_3},#{BUSI_ITEM_4},#{BUSI_ITEM_5},"
    		+ "#{BUSI_ITEM_6},#{BUSI_ITEM_7},#{BUSI_ITEM_8},#{BUSI_ITEM_9},#{BUSI_ITEM_10},#{BUSI_ITEM_11},#{BUSI_ITEM_12},"
    		+ "#{BUSI_ITEM_13},#{BUSI_ITEM_14},#{BUSI_ITEM_15}) ")
    public Integer insertCommonLog(PltCommonLog  logData);
    

}
