package com.bonc.busi.backpage.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuccessFunc {
    private final static Logger log = LoggerFactory.getLogger(SuccessFunc.class);

   // @Select("SELECT e.ACTIVITY_ID, e.ACTIVITY_SEQ_ID, b.`DESC`,e.OPER_TIME, e.BEGIN_DATE, e.END_DATE FROM PLT_ACTIVITY_EXECUTE_LOG e, PLT_ACTIVITY_EXECUTE_BUSICODE_DEF b WHERE e.BUSI_CODE = b.BUSI_CODE  AND (e.`BUSI_CODE`='1011' OR e.`BUSI_CODE`='2005')ORDER BY e.`BEGIN_DATE` DESC")
    public String getActivitySuccessByPage(HashMap<String,Object> param){
    	
    	String select = " e.ACTIVITY_ID, e.ACTIVITY_SEQ_ID, b.`DESC`,e.OPER_TIME, date_format(e.BEGIN_DATE,'%Y-%m-%d %H:%i:%s') as BEGIN_DATE, date_format(e.END_DATE,'%Y-%m-%d %H:%i:%s') as END_DATE ";
    	String from = "PLT_ACTIVITY_EXECUTE_LOG e, PLT_ACTIVITY_EXECUTE_BUSICODE_DEF b";
    	StringBuilder whereBuilder = new StringBuilder();
    	whereBuilder.append(" e.BUSI_CODE = b.BUSI_CODE  AND (e.`BUSI_CODE`='1011' OR e.`BUSI_CODE`='2005' )");
    	if(param.get("activityId")!=null&&!param.get("activityId").equals("")){
    		whereBuilder.append(" AND (e.`ACTIVITY_ID` = '"+param.get("activityId")+"'");
    		whereBuilder.append(" OR e.`ACTIVITY_SEQ_ID` = '"+param.get("activityId")+"')");
    	}else{
    		whereBuilder.append(" AND e.`ACTIVITY_SEQ_ID` IN (SELECT REC_ID FROM `plt_activity_info` WHERE  ACTIVITY_STATUS != 2)");
    	}
    	whereBuilder.append(" AND e.TENANT_ID='" + param.get("tenantId")+"'");
    	String limitSql = " LIMIT "+param.get("currentPage")+","+param.get("pageNum");
    	BEGIN();
		SELECT(select);
		FROM(from);
		WHERE(whereBuilder.toString());
		ORDER_BY(" e.`BEGIN_DATE` DESC ");
		String sql =SQL()+limitSql;
		log.info("--getActivitySuccessByPage:"+sql);
		return sql;
    }
   // @Select("SELECT e.ACTIVITY_ID, e.ACTIVITY_SEQ_ID, b.`DESC`,e.OPER_TIME, e.BEGIN_DATE, e.END_DATE FROM PLT_ACTIVITY_EXECUTE_LOG e, PLT_ACTIVITY_EXECUTE_BUSICODE_DEF b WHERE e.BUSI_CODE = b.BUSI_CODE  AND (e.`BUSI_CODE`='1011' OR e.`BUSI_CODE`='2005')ORDER BY e.`BEGIN_DATE` DESC")
    public String getActivitySuccessNum(Map<String,Object> param){
    	
    	String select = "COUNT(*) ";
    	String from = "PLT_ACTIVITY_EXECUTE_LOG e, PLT_ACTIVITY_EXECUTE_BUSICODE_DEF b";
    	StringBuilder whereBuilder = new StringBuilder();
    	whereBuilder.append(" e.BUSI_CODE = b.BUSI_CODE  AND (e.`BUSI_CODE`='1011' OR e.`BUSI_CODE`='2005') ");
    	if(param.get("activityId")!=null&&!param.get("activityId").equals("")){
    		whereBuilder.append(" AND (e.`ACTIVITY_ID` = '"+param.get("activityId")+"'");
    		whereBuilder.append(" OR e.`ACTIVITY_SEQ_ID` = '"+param.get("activityId")+"')");
    	}else{
    		whereBuilder.append(" AND e.`ACTIVITY_SEQ_ID` IN (SELECT REC_ID FROM `plt_activity_info` )");
    	}
    	whereBuilder.append(" AND e.TENANT_ID='" + param.get("tenantId")+"'");
    	
    	BEGIN();
		SELECT(select);
		FROM(from);
		WHERE(whereBuilder.toString());
		
		String sql = SQL();
		log.info("--getActivitySuccessNum:"+SQL());
		
		return sql;
    }
  public String getDateId(Map<String,Object> param){
    	
    	String select = " BUSI_ITEM_3 AS DATEID,date_format(START_TIME,'%Y%m%d') AS START_TIME ";
    	String from = " plt_common_log ";
    	String groupBysql = " GROUP BY BUSI_ITEM_3  ORDER BY BUSI_ITEM_3 DESC ";
    	String limitSql = " LIMIT "+param.get("currentPage")+","+param.get("pageNum");
    	StringBuilder whereBuilder = new StringBuilder();
    	whereBuilder.append(" LOG_TYPE = '01'   AND BUSI_ITEM_4 = '"+param.get("tenantId")+"' ");
    	if(param.get("dateId")!=null&&!param.get("dateId").equals("")){
    		whereBuilder.append(" AND BUSI_ITEM_3 LIKE '%"+param.get("dateId")+"%'");
    	}
    	
    	BEGIN();
		SELECT(select);
		FROM(from);
		WHERE(whereBuilder.toString());
		
		String sql = SQL()+groupBysql+limitSql;
		log.info("--getDateId:"+sql);
		
		return sql;
    }
  public String getDateIdNum(Map<String,Object> param){
  	
  	String select = " COUNT(DISTINCT (BUSI_ITEM_3)) ";
  	String from = " plt_common_log ";
  
  	StringBuilder whereBuilder = new StringBuilder();
  	whereBuilder.append(" LOG_TYPE = '01'   AND BUSI_ITEM_4 = '"+param.get("tenantId")+"' ");
  	if(param.get("dateId")!=null&&!param.get("dateId").equals("")){
  		whereBuilder.append(" AND BUSI_ITEM_3 = '"+param.get("dateId")+"'");
  	}
  	
  	BEGIN();
		SELECT(select);
		FROM(from);
		WHERE(whereBuilder.toString());
		
		String sql = SQL();
		log.info("--getDateIdNum:"+sql);
		
		return sql;
  }
}
