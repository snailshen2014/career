package com.bonc.busi.task.mapper;
/*
 * @desc:任务对应的MYBATIS的MAPPER，和任务模块相关的数据库操作基本在此
 * @author:曾定勇
 * @time:2016-11-26
 */

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import com.bonc.busi.outer.model.PltActivityInfo;
import com.bonc.busi.task.bo.*;

public interface BaseMapper {
	
		// --- 得到当前的帐期（某天）---
	   @Select("SELECT  max(date_id) DATE_ID FROM DIM_KFPT_BAND_DATE ")
	    public String getMaxDateId();
	   // --- 查询某个租户对应的活动成功条件数据 （如果只有短信一个渠道，不提取)---
	   @Select("SELECT a.REC_ID RecId,a.REC_ID ACTIVITY_SEQ_ID,b.SUCESSTYPE,"
	   		+ "b.SUCESSCONDITIONSQL SucessConSql,"
	   		+ "a.LAST_ORDER_CREATE_TIME  LastOrderCreateTime ,b.MATCHINGTYPE matchingType"
	   		+ " FROM PLT_ACTIVITY_INFO a,PLT_ACTIVITY_SUCESS_CFG b "
	   		+ " WHERE a.ACTIVITY_STATUS in ('1','8','9') AND a.REC_ID = b.ACTIVITY_SEQ_ID "
	   		+ "AND a.TENANT_ID=#{TenantId}  AND b.TENANT_ID=#{TenantId} AND a.REC_ID IN ("
	   		+ "SELECT  DISTINCT ACTIVITY_SEQ_ID  FROM PLT_ACTIVITY_CHANNEL_DETAIL "
	   		+ "WHERE CHANN_ID != '7' AND TENANT_ID=#{TenantId}) ORDER BY a.REC_ID" )
	   public List<ActivitySucessInfo> getActivityForTenantId(@Param("TenantId")String TenantId);
	   // --- 查询某个活动对应的产品列表 ---
	   @Select("SELECT PRODUCTCODE FROM PLT_ACTIVITY_PRODUCT_LIST "
	   		+ " WHERE ACTIVITY_SEQ_ID =#{ActivitySeqId} "
	   		+ "AND ISVALID = '1' AND TENANT_ID=#{TenantId} ORDER BY PRODUCTCODE")
	   public	List<String> getProductListForActivity(@Param("ActivitySeqId")int ActivitySeqId,
			   @Param("TenantId")String TenantId);
	   // --- 根据租户，活动编号，起始纪录号  查询未确认工单 ---
	   @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO "
	   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
	   		+ " CHANNEL_STATUS  IN('0','2') "
		   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
		   		+ " ORDER BY REC_ID LIMIT 2000")
	   public	List<OrderCheckInfo> getOrderListForActivity(@Param("TenantId")String TenantId,
			   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")int orderRecId);
	   /*
	   // --- 查询出需要同步用户信息的工单 ---
	   @Select("SELECT REC_ID ,USER_ID,SERVICE_TYPE  FROM PLT_ORDER_INFO "
		   		+ " WHERE    ORDER_STATUS ='5' AND CHANNEL_STATUS  IN('0','2')"
		   		+ " AND CHANNEL_ID = '5' "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 2000")
		   public	List<Integer> getOrderUserForUpdate(@Param("TenantId")String TenantId,
				   @Param("orderRecId")int orderRecId);
	   /*
	   // --- 更新工单表上的用户信息 ---
	   @Update("UPDATE PLT_ORDER_INFO SET "
	   		+ "WHERE TENANT_ID=#{TenantId}  AND REC_ID  = #{orderRecId}")
	   public	void		updateOrderUserInfo(@Param("TenantId")String TenantId,
			   @Param("orderRecId")int orderRecId );
			   */
	   
	   // --- 更新序列表 ---
	    @Update("update SYS_SEQUENCE SET CUR_VALUE = CUR_VALUE + STEP "
	            + " where SEQUENCE_NAME=#{SequceName}")
	    public void updateSequence(@Param("SequceName") String SequceName);
	    // --- 得到当前的帐期（某天）---
	    @Select("SELECT  CUR_VALUE  FROM SYS_SEQUENCE WHERE  SEQUENCE_NAME=#{SequceName}")
		public int getSequenceValue(@Param("SequceName") String SequceName);
	    // --- 查询系统配置表 ---
	    @Select("SELECT  CFG_KEY,CFG_VALUE  FROM SYS_COMMON_CFG ")
		public List<SysCommonCfg> getAllSysCommonCfg();
	    // --- 查询有效的租户编号 ---
	    @Select("SELECT TENANT_ID FROM TENANT_INFO WHERE STATE='1' ")
		public List<String> getValidTenantId();
	    // ---查询有效租户数量 ---
	    @Select("SELECT COUNT(*) FROM TENANT_INFO WHERE STATE='1' ")
		public int getValidTenantNum();
	    // --- 更新SYS_COMMON_CFG ---
	    @Update("update SYS_COMMON_CFG SET CFG_VALUE = #{strValue} "
	            + " where CFG_KEY=#{strKey}")
	    public void updateSysCommonCfg(@Param("strKey") String strKey,@Param("strValue") String strValue);
	    // --- 插入通用日志表 ---
	    @Insert("INSERT INTO PLT_COMMON_LOG(LOG_TYPE,SERIAL_ID,SPONSOR,START_TIME,BUSI_CODE,TENANT_ID,YYMMDD,END_TIME,"
	    		+ "DURATION,DEST_NUM,BUSI_DESC,BUSI_NUM_1,BUSI_NUM_2,BUSI_TIME_1,BUSI_TIME_2,BUSI_MONEY_1,"
	    		+ "BUSI_MONEY_2,BUSI_ITEM_1,BUSI_ITEM_2,BUSI_ITEM_3,BUSI_ITEM_4,BUSI_ITEM_5,BUSI_ITEM_6,BUSI_ITEM_7,"
	    		+ "BUSI_ITEM_8,BUSI_ITEM_9,BUSI_ITEM_10,BUSI_ITEM_11,BUSI_ITEM_12,BUSI_ITEM_13,BUSI_ITEM_14,BUSI_ITEM_15)  "
	    		+ "VALUES (#{LOG_TYPE},#{SERIAL_ID},#{SPONSOR},#{START_TIME},#{BUSI_CODE},#{TENANT_ID},#{YYMMDD},#{END_TIME}"
	    		+ ",#{DURATION},#{DEST_NUM},#{BUSI_DESC},#{BUSI_NUM_1},#{BUSI_NUM_2},#{BUSI_TIME_1},#{BUSI_TIME_2},"
	    		+ "#{BUSI_MONEY_1},#{BUSI_MONEY_2},#{BUSI_ITEM_1},#{BUSI_ITEM_2},#{BUSI_ITEM_3},#{BUSI_ITEM_4},#{BUSI_ITEM_5},"
	    		+ "#{BUSI_ITEM_6},#{BUSI_ITEM_7},#{BUSI_ITEM_8},#{BUSI_ITEM_9},#{BUSI_ITEM_10},#{BUSI_ITEM_11},#{BUSI_ITEM_12},"
	    		+ "#{BUSI_ITEM_13},#{BUSI_ITEM_14},#{BUSI_ITEM_15}) ")
	    public void insertPltCommonLog(PltCommonLog  logData);
	    // --- 查询系统变量表 --
	    @Select("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE CFG_KEY = #{strCfgKey}")
	  	public String getValueFromSysCommCfg(@Param("strCfgKey") String strCfgKey);
	    // --- 更新系统变量 ---
	    @Update("update SYS_COMMON_CFG SET CFG_VALUE = #{strCfgValue} "
	            + " where CFG_KEY=#{strCfgKey}")
	    public void  updateValueToSysCommCfg(@Param("strCfgKey") String strCfgKey,
	    		@Param("strCfgValue") String strCfgValue);
	    
	    // --- 得到用户编号 ---
	    @Select("SELECT  o.USER_ID  USER_ID FROM PLT_ACTIVITY_INFO a,  PLT_ORDER_INFO o IGNORE INDEX (IDX_ORG_PATH)"
	    		+ " WHERE 1=1 AND a.TENANT_ID = 'uni076'  AND o.TENANT_ID = 'uni076'  AND o.CHANNEL_ID = '5' "
	    		+ " AND o.SERVICE_TYPE = '0'  AND a.REC_ID = 61 AND o.ORDER_STATUS = '5'  "
	    		+ " AND o.ORG_PATH LIKE '/root%'  AND o.ACTIVITY_SEQ_ID = a.REC_ID  "
	    		+ " AND    o.CONTACT_CODE NOT IN (${orderStatus}) "
	    		+ " AND o.CHANNEL_STATUS = '0' ")
	    public	List<String> getUserIdFromOrder(@Param("orderStatus") String orderStatus);
	    
	    @Select("SELECT COUNT(*) COUNT FROM PLT_USER_LABEL WHERE TENANT_ID='uni076' AND PARTITION_FLAG=#{partFlag} AND "
	    		+ "USER_ID IN (${userIds})")
	    public	long	getCountForOrder(@Param("userIds") String userIds,@Param("partFlag") String partFlag);
	    
	    

}
