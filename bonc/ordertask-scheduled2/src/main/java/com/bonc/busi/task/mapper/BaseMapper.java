package com.bonc.busi.task.mapper;
/*
 * @desc:任务对应的MYBATIS的MAPPER，和任务模块相关的数据库操作基本在此
 * @author:曾定勇
 * @time:2016-11-26
 */

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import com.bonc.busi.outer.model.PltActivityInfo;
import com.bonc.busi.task.bo.*;

public interface BaseMapper {
	
		// --- 得到当前的帐期（某天）---
	   //@Select("SELECT  max(date_id) DATE_ID FROM DIM_KFPT_BAND_DATE ")
	   @Select("select   max_date from  ui_scheme_data_fubu")
	    public String getMaxDateId();
	   // --- 查询某个租户对应的活动成功条件数据 （如果只有短信一个渠道，不提取)---
	   @Select("SELECT a.REC_ID RecId,a.REC_ID ACTIVITY_SEQ_ID,b.SUCESSTYPE, "
	   		+ "b.SUCESSCONDITIONSQL SucessConSql,"
	   		+ "a.LAST_ORDER_CREATE_TIME  LastOrderCreateTime ,b.MATCHINGTYPE matchingType,b.SUCCESS_TYPE_CON_SQL "
	   		+ " FROM PLT_ACTIVITY_INFO a,PLT_ACTIVITY_SUCESS_CFG b "
	   		+ " WHERE DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d')>=DATE_SUB(CURDATE(),INTERVAL 1 DAY) AND a.REC_ID = b.ACTIVITY_SEQ_ID "
	   		+ "AND a.TENANT_ID=#{TenantId}  AND b.TENANT_ID=#{TenantId} AND a.REC_ID IN ("
	   		+ "SELECT  DISTINCT ACTIVITY_SEQ_ID  FROM PLT_ACTIVITY_CHANNEL_DETAIL "
	   		+ "WHERE  TENANT_ID=#{TenantId}) ORDER BY a.REC_ID" )
	   public List<ActivitySucessInfo> getActivityForTenantId(@Param("TenantId")String TenantId);
	   
       // --- 查询一个月内工单历史表活动
	   @Select("select a.REC_ID RecId,a.REC_ID ACTIVITY_SEQ_ID,b.SUCESSTYPE,"
			   + " b.SUCESSCONDITIONSQL SucessConSql,"
			   + " a.LAST_ORDER_CREATE_TIME  LastOrderCreateTime ,b.MATCHINGTYPE matchingType,b.SUCCESS_TYPE_CON_SQL" 
			   + " FROM PLT_ACTIVITY_INFO a, PLT_ACTIVITY_SUCESS_CFG b"
			   + " WHERE DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d') >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) AND a.REC_ID = b.ACTIVITY_SEQ_ID" 
			   + " AND a.TENANT_ID = #{TenantId} AND b.TENANT_ID = #{TenantId} AND a.ACTIVITY_STATUS = 2"
			   + " AND a.REC_ID IN(SELECT ACTIVITY_SEQ_ID FROM PLT_ACTIVITY_CHANNEL_DETAIL WHERE TENANT_ID = #{TenantId} AND CHANN_ID = 5)")
	   public List<ActivitySucessInfo> getOrderHisInfo(@Param("TenantId")String TenantId);
	   
	   
	   // --- 查询某个活动对应的产品列表 ---
	   @Select("SELECT PRODUCTCODE FROM PLT_ACTIVITY_PRODUCT_LIST "
	   		+ " WHERE ACTIVITY_SEQ_ID =#{ActivitySeqId} "
	   		+ " AND TENANT_ID=#{TenantId} ORDER BY PRODUCTCODE")
	   public	List<String> getProductListForActivity(@Param("ActivitySeqId")int ActivitySeqId,
			   @Param("TenantId")String TenantId);
	   // --- 根据租户，活动编号，起始纪录号  查询未确认工单 ---
	   @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO "
	   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
	   		+ " CHANNEL_STATUS  IN('0','2') "
		   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
		   		+ " ORDER BY REC_ID LIMIT 5000")
	   public	List<OrderCheckInfo> getOrderListForActivity(@Param("TenantId")String TenantId,
			   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	   // --- 提取微信表  ---
	   @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_WEIXIN "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
		   		+ " CHANNEL_STATUS  IN('0','2') "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 5000")
		   public	List<OrderCheckInfo> getWeiXinOrderListForActivity(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	   // --- 提取一级表  ---
	   @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_ONE "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
		   		+ " CHANNEL_STATUS  IN('0','2') "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 5000")
		   public	List<OrderCheckInfo> getOneOrderListForActivity(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	// --- 提取弹窗表  ---
	   @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_POPWIN "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
		   		+ " CHANNEL_STATUS  IN('0','2') "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 5000")
		   public	List<OrderCheckInfo> getPopwinOrderListForActivity(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	   
	// --- 提取短信历史表  ---
	   @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM ${tableName} "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
		   		+ " CHANNEL_STATUS  IN('0','2') "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 5000")
		   public	List<OrderCheckInfo> getSmsHisOrderListForActivity(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId,@Param("tableName")String tableName);
	   
	// --- 提取场景营销工单表  ---
	   @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_SCENEMARKET "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
		   		+ " CHANNEL_STATUS  IN('0','2') "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 5000")
		   public	List<OrderCheckInfo> getScenemarketOrderListForActivity(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	   
	// --- 提取留存工单表  ---
		@Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_REMAIN "
				+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
				+ " CHANNEL_STATUS  IN('0','2') "
				+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
				+ " ORDER BY REC_ID LIMIT 5000")
			public	List<OrderCheckInfo> getRemainOrderListForActivity(@Param("TenantId")String TenantId,
					@Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
		
	// --- 提取客户经理历史工单表  ---
		@Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_HIS "
				+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='5' AND "
				+ " CHANNEL_STATUS  IN('0','2') "
				+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
				+ " ORDER BY REC_ID LIMIT 5000")
			public	List<OrderCheckInfo> getOrderHisListForActivity(@Param("TenantId")String TenantId,
					@Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);		
		
	/*// --- 提取电话渠道工单表 ---
	@Select("SELECT REC_ID orderRecId,USER_ID userId FROM PLT_ORDER_INFO_CALL "
			+ "WHERE ACTIVITY_SEQ_ID = #{ActivitySeqId} AND ORDER_STATUS = '5' AND " + "CHANNEL_STATUS  IN('0','2') "
			+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  " + " ORDER BY REC_ID LIMIT 5000")
	public List<OrderCheckInfo> getCallOrderForAvtivity(@Param("TenantId") String TenantId,
			@Param("ActivitySeqId") int ActivitySeqId, @Param("orderRecId") long orderRecId);*/
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
	    // --- 修改通用日志表 ---
	    @Update("UPDATE  PLT_COMMON_LOG SET END_TIME = #{END_TIME},BUSI_DESC = #{BUSI_DESC},DURATION = #{DURATION},BUSI_CODE = #{BUSI_CODE},DEST_NUM = #{DEST_NUM}" 
+"WHERE LOG_TYPE = #{LOG_TYPE} AND TENANT_ID = #{TENANT_ID} AND BUSI_ITEM_6 = #{BUSI_ITEM_6} AND BUSI_ITEM_2 = #{BUSI_ITEM_2}AND SERIAL_ID = #{SERIAL_ID}")
	    public void updatePltCommonLog(PltCommonLog  logData);
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
	    
	    @Select("SELECT COUNT(*) COUNT FROM PLT_USER_LABEL WHERE TENANT_ID='uni076' AND "
	    		+ "USER_ID IN (${userIds})")
	    public	long	getCountForOrder(@Param("userIds") String userIds);
	    
	    // --- 提取需要判断是否成功的客户经理工单（工单生成后）
	    @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='0'  "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 10000")
		   public	List<OrderCheckInfo> getFrontOrderListForSucess(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	    // --- 提取需要判断是否成功的短信工单（工单生成后）
	    @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_SMS "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='0'  "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 10000")
		   public	List<OrderCheckInfo> getSmsOrderListForSucess(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	    // --- 提取需要判断是否成功的微信工单（工单生成后）
	    @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_WEIXIN "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='0'  "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 10000")
		   public	List<OrderCheckInfo> getWeixinOrderListForSucess(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	    // --- 提取需要判断是否成功的弹窗工单（工单生成后）
	    @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_POPWIN "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='0'  "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 10000")
		   public	List<OrderCheckInfo> getPopwinOrderListForSucess(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	    // --- 提取需要判断是否成功的一级工单（工单生成后）
	    @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_ONE "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='0'  "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 10000")
		   public	List<OrderCheckInfo> getOneOrderListForSucess(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	    
	    // --- 提取需要判断是否成功的留存工单（工单生成后）
	    @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_INFO_REMAIN "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='0'  "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 10000")
		   public	List<OrderCheckInfo> getRemainOrderListForSucess(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId);
	    
	    
	    
	    // --- 提取需要判断是否成功的工单（工单生成后）
	    @Select("SELECT REC_ID orderRecId,USER_ID userId  FROM ${tableName} "
		   		+ " WHERE   ACTIVITY_SEQ_ID =#{ActivitySeqId}  AND ORDER_STATUS ='0'  "
			   		+ " AND TENANT_ID=#{TenantId}  AND REC_ID  > #{orderRecId}  "
			   		+ " ORDER BY REC_ID LIMIT 10000")
		   public	List<OrderCheckInfo> getOrderListForSucess(@Param("TenantId")String TenantId,
				   @Param("ActivitySeqId")int ActivitySeqId,@Param("orderRecId")long orderRecId,@Param("tableName")String tableName);
	    

	 // --- 更新每天成功标准插入一条记录 ---
	    @Insert("INSERT INTO PLT_SUCCESS_PROCESS_LOG (PROCESS_TIME,TENANT_ID,DATE_ID) VALUES (NOW(),#{tenantId},#{countDateId}) ")
	    public void insertRecord(@Param("tenantId")String tenantId,@Param("countDateId")String countDateId);
	    
	    @Insert("SELECT COUNT(1) FROM PLT_SUCCESS_PROCESS_LOG WHERE TENANT_ID=#{tenantId} AND DATE_ID=#{countDateId} AND DATE(PROCESS_TIME)=CURDATE() ")
	    public Integer seletRecord(@Param("tenantId")String tenantId,@Param("countDateId")String countDateId);
	    
	    @Select("SELECT COUNT(1) num, #{tableName} tableName FROM ${tableName} WHERE ACTIVITY_SEQ_ID =#{ActivitySeqId} AND TENANT_ID=#{TenantId} ")
		public HashMap<String, Object> getCountForActivity(@Param("tableName")String tableName,@Param("ActivitySeqId")int ActivitySeqId,@Param("TenantId")String TenantId);
	    
	    @Select("SELECT IFNULL(SUM(d.VALID_NUM),0) num,#{tableName} tableName FROM PLT_ORDER_DETAIL_COUNT d,PLT_ACTIVITY_INFO a "
					+" WHERE d.ACTIVITY_SEQ_ID = a.REC_ID AND DATE_FORMAT(a.ORDER_END_DATE, '%Y%m%d') >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)"
					+" AND d.CHANNEL_ID IN ${channelId}  AND a.TENANT_ID=#{tenantId}  AND d.TENANT_ID=#{tenantId}"
					+" AND a.REC_ID IN (SELECT  DISTINCT ACTIVITY_SEQ_ID  FROM PLT_ACTIVITY_CHANNEL_DETAIL WHERE  TENANT_ID=#{tenantId}) ")
		public HashMap<String, Object> getRecsForActivity(@Param("tableName")String tableName, @Param("tenantId")String tenantId,@Param("channelId")String channelId);
	    
	    
	    @Select("SELECT IFNULL(SUM(d.RESERVE1),0) num,#{tableName} tableName FROM PLT_ORDER_DETAIL_COUNT d,PLT_ACTIVITY_INFO a " 
					+" WHERE d.ACTIVITY_SEQ_ID = a.REC_ID AND DATE_FORMAT(a.ORDER_END_DATE, '%Y%m%d') >= DATE_SUB(CURDATE(), INTERVAL 1 DAY)"
					+"  AND a.TENANT_ID=#{tenantId} AND d.TENANT_ID=#{tenantId} "
					+ " AND a.REC_ID IN (SELECT  DISTINCT ACTIVITY_SEQ_ID  FROM PLT_ACTIVITY_CHANNEL_DETAIL WHERE  TENANT_ID=#{tenantId} )")
	    public HashMap<String, Object> getRemainForActivity(@Param("tableName")String tableName, @Param("tenantId")String tenantId);
		
	    @Select("SELECT MAX(MONTH_ID) FROM ODS_EXECUTE_LOG WHERE PROCNAME='P_PLT_USER_CHANGE_HIVE2MYSQL' AND RESULT='SUCCESS'")
	    public String getMaxDate();
	    
//	    @Select("SELECT DATE_ID FROM PLT_USER_CHANGE WHERE TENANT_ID=#{TenantId} GROUP BY DATE_ID")
//		public String getChangeTableDateId(@Param("TenantId")String TenantId);
	    
	    @Select("SELECT MAX(DATE_ID) DATE_ID FROM PLT_USER_CHANGE WHERE TENANT_ID=#{TenantId}")
     	public String getChangeTableDateId(@Param("TenantId")String TenantId);
		
}
