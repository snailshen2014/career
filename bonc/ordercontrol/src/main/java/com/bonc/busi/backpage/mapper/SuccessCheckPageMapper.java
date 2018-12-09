package com.bonc.busi.backpage.mapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;


public interface SuccessCheckPageMapper {
//@Select("SELECT   * FROM `sys_log_${month}` WHERE BUSI_ITEM_5 = #{flag}  AND TENANT_ID = #{tenantId}  AND (   UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(LOG_TIME) ) <= 86400 / 12 LIMIT (#{currentPage},#{pageNum})")
//public List<HashMap> getCurrentSucesslog(@Param("month") String month,@Param("flag") String flag,@Param("tenantId") String tenantId,@Param("currentPage") int currentPage,@Param("pageNum") int pageNum);

	@Select("SELECT   * FROM `sys_log_${month}` WHERE BUSI_ITEM_5 = #{flag}  AND TENANT_ID = #{tenantId}  AND LOG_TIME>='2017-08-27 00:00:00' LIMIT ${currentPage},${pageNum}")
	public List<HashMap> getCurrentSucesslog(@Param("month") String month,@Param("flag") String flag,@Param("tenantId") String tenantId,@Param("date") String date,@Param("currentPage") int currentPage,@Param("pageNum") int pageNum);
	@Select("SELECT  COUNT(*) FROM `sys_log_${month}` WHERE BUSI_ITEM_5 = #{flag}  AND TENANT_ID = #{tenantId}  AND LOG_TIME>='2017-08-27 00:00:00'")
	public int getSuccessLogNum(@Param("month") String month,@Param("flag") String flag,@Param("tenantId") String tenantId,@Param("date") String date);
	
	
	@SelectProvider(type = SuccessFunc.class, method = "getActivitySuccessByPage")
	public List<HashMap<String,Object>> getActivitySuccessByPage(Map<String, Object> param);
	
	@SelectProvider(type = SuccessFunc.class, method = "getActivitySuccessNum")
	public int getActivitySuccessNum(Map<String, Object> param);
	
	@Select("SELECT  ID,date_format(LOG_TIME,'%Y-%m-%d %H:%i:%s') AS LOG_TIME,BUSI_ITEM_2,BUSI_ITEM_3,BUSI_ITEM_4,BUSI_ITEM_1,LOG_MESSAGE FROM `sys_log_${month}` WHERE BUSI_ITEM_5 = #{flag}  AND BUSI_ITEM_2 = #{activitySeqId} AND TENANT_ID = #{tenantId} ")
	public List<HashMap<String,Object>> getActivitysuccessDetail(Map<String, Object> param);
	@SelectProvider(type = SuccessFunc.class, method = "getDateId")
	public List<HashMap<String,Object>> getDateId(Map<String, Object> param);
	
	@Select("SELECT   a.BUSI_ITEM_4 AS DATEID,  date_format(TIMEDIFF(b.LOG_TIME,a.LOG_TIME),'%H:%i:%s') AS FOR_TIME, date_format(a.LOG_TIME,'%Y-%m-%d %H:%i:%s') AS BEGIN_TIME, a.LOG_MESSAGE AS BEGIN_MESSAGE, date_format(b.LOG_TIME,'%Y-%m-%d %H:%i:%s') AS END_TIME, b.LOG_MESSAGE AS END_MESSAGE  FROM `sys_log_${month}` a   LEFT JOIN `sys_log_${month}` b    ON a.BUSI_ITEM_4 = b.BUSI_ITEM_4 WHERE a.`LOG_MESSAGE` = '工单成功检查开始运行'  AND b.`LOG_MESSAGE` = '工单成功检查结束'   AND a.`BUSI_ITEM_5` = '01'   AND b.`BUSI_ITEM_5` = '01'  AND a.`TENANT_ID` = #{tenantId} AND b.`TENANT_ID` = #{tenantId} AND a.`BUSI_ITEM_4` =#{dateId}  AND b.`BUSI_ITEM_4` =#{dateId} ORDER BY a.`LOG_TIME` DESC  LIMIT 1")
	public HashMap<String,Object> getCheckOverview(Map<String, Object> param);
	
	//--检查日志是否出错--
	@Select("SELECT COUNT(*) FROM `sys_log_${month}` WHERE `BUSI_ITEM_1` = '工单成功检查出错' AND `BUSI_ITEM_4` = #{DATEID} AND `BUSI_ITEM_5` = '01'  AND LOG_TIME >= #{BEGIN_TIME} AND LOG_TIME <= #{END_TIME} ")
	public int getCheckOverviewError(Map<String, Object> param);
	
	@Select("SELECT a.BUSI_ITEM_4 AS DATEID, DATE_FORMAT(TIMEDIFF(b.LOG_TIME,a.LOG_TIME),'%H:%i:%s') AS FOR_TIME,DATE_FORMAT(a.LOG_TIME, '%Y-%m-%d %H:%i:%s') AS BEGIN_TIME,a.LOG_MESSAGE AS BEGIN_MESSAGE,DATE_FORMAT(b.LOG_TIME, '%Y-%m-%d %H:%i:%s') AS END_TIME,b.LOG_MESSAGE AS END_MESSAGE"
	+" FROM "+" (SELECT BUSI_ITEM_4,LOG_TIME,LOG_MESSAGE FROM `sys_log_${month}` WHERE `LOG_MESSAGE` = '工单成功检查开始运行' AND `BUSI_ITEM_5` = '01' AND BUSI_ITEM_4 =#{dateId} AND `TENANT_ID` = #{tenantId}) a "
			+" LEFT JOIN "
	+"(SELECT BUSI_ITEM_4,LOG_TIME,LOG_MESSAGE FROM `sys_log_${month}` WHERE BUSI_ITEM_4 =#{dateId} AND `BUSI_ITEM_5` = '01' AND `TENANT_ID` = #{tenantId} ORDER BY LOG_TIME DESC LIMIT 1)  b "
	+ " ON a.BUSI_ITEM_4 = b.BUSI_ITEM_4 LIMIT 1 ")
	public HashMap<String,Object> getCheckOverviewIng(Map<String, Object> param);
	@SelectProvider(type = SuccessFunc.class, method = "getDateIdNum")
	 public int  getDateIdNum(Map<String,Object> param);
	
	@Select("SELECT  ID,date_format(LOG_TIME,'%Y-%m-%d %H:%i:%s') AS LOG_TIME,BUSI_ITEM_2,BUSI_ITEM_3,BUSI_ITEM_4,BUSI_ITEM_1,LOG_MESSAGE FROM `sys_log_${month}` WHERE BUSI_ITEM_5 = #{flag}  AND BUSI_ITEM_4 = #{dateId}  AND LOG_TIME >=#{beginTime} AND LOG_TIME <=#{endTime} AND TENANT_ID = #{tenantId} ORDER BY  LOG_TIME DESC LIMIT ${currentPage},${pageNum}")
	public List<HashMap<String,Object>> getSuccessCheckDetail(Map<String, Object> param);
	@Select("SELECT  COUNT(*) FROM `sys_log_${month}`  WHERE BUSI_ITEM_5 = #{flag}  AND BUSI_ITEM_4 = #{dateId} AND LOG_TIME >=#{beginTime} AND LOG_TIME <=#{endTime}  AND TENANT_ID = #{tenantId} ")
	public int getSuccessCheckNum(Map<String, Object> param);


}
