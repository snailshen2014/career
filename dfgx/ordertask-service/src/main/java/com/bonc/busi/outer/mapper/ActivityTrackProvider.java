package com.bonc.busi.outer.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.GROUP_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.util.HashMap;

import oracle.net.aso.a;


public class ActivityTrackProvider {
	
	
	public String getActivitySet(HashMap<String, Object> req){
		BEGIN();
		SELECT(" REC_ID,ACTIVITY_STATUS ");
		FROM("PLT_ACTIVITY_INFO a ");
		WHERE(" a.ACTIVITY_ID =#{activityId} "); 
		WHERE(" a.TENANT_ID=#{tenantId} ");	
		
		return SQL(); 
	}


	
	public String getChannelStatistic(HashMap<String, Object> req){
		
		BEGIN();
		SELECT(" IFNULL(SUM(VALID_NUM), 0) + IFNULL(SUM(s.RESERVE1), 0) ALL_COUNT,"
				+ "IFNULL(SUM(s.RESERVE1),0) REMAIN_COUNT,"
				+ "s.CHANNEL_ID,s.TENANT_ID,a.ACTIVITY_ID "); 
		
		FROM("PLT_ORDER_DETAIL_COUNT s,PLT_ACTIVITY_INFO a");
		
		WHERE(" a.ACTIVITY_ID =#{activityId} "); 		
		WHERE(" s.ACTIVITY_SEQ_ID = a.REC_ID ");
		WHERE(" s.TENANT_ID=#{tenantId} ");	
		WHERE(" a.TENANT_ID=#{tenantId} ");
		
		GROUP_BY("s.CHANNEL_ID");
		
		return SQL(); 
	}



	public String getOrderhistory(HashMap<String, Object> req){
		
		String LIMIT = "LIMIT "+((Integer)req.get("pageNum")-1)*(Integer)req.get("pageSize")+","+req.get("pageSize");
		//String LIMIT = "LIMIT "+(Integer.parseInt((String)req.get("pageNum"))-1)*(Integer.parseInt((String)req.get("pageSize")))+","+req.get("pageSize");
		
		return ("SELECT CHANNEL_ID,a.ACTIVITY_ID,ACTIVITY_STATUS,ACTIVITY_SEQ_ID,a.TENANT_ID,'1' ORDER_MARK,DATE_FORMAT(a.ORDER_BEGIN_DATE,'%Y-%m-%d %H:%i:%s') UPDATE_TIME,"
				+ "DATE_FORMAT(a.ORDER_BEGIN_DATE,'%Y%m%d %H%i%s') UPDATE_DATE,DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d %H:%i:%s') ORDER_END_DATE,"
				+ "DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d %H:%i:%s') ORDER_BEGIN_DATE,"
				+ "'新增工单' REASON,ORI_AMOUNT-INOUT_FILTER_AMOUNT-COVERAGE_FILTER_AMOUNT-BLACK_FILTER_AMOUNT-RESERVE_FILTER_AMOUNT-TOUCH_FILTER_AMOUNT-SUCCESS_FILTER_AMOUNT-REPEAT_FILTER_AMOUNT UPDATE_NUM,"
				+ " ORI_AMOUNT-INOUT_FILTER_AMOUNT-COVERAGE_FILTER_AMOUNT-BLACK_FILTER_AMOUNT-RESERVE_FILTER_AMOUNT-TOUCH_FILTER_AMOUNT-SUCCESS_FILTER_AMOUNT-REPEAT_FILTER_AMOUNT VALID_NUM "
				+"FROM PLT_ACTIVITY_INFO a,PLT_ACTIVITY_PROCESS_LOG s "
				+" WHERE a.ACTIVITY_ID = '"+req.get("activityId")+"'"
				+ " AND a.REC_ID = s.ACTIVITY_SEQ_ID "
				+ " AND s.CHANNEL_ID = '"+req.get("channelId")+"'"
				+ " AND a.TENANT_ID = '"+req.get("tenantId")+"'"
				+ " AND s.TENANT_ID = '"+req.get("tenantId")+"'" 
			+" UNION "
			   +"SELECT CHANNEL_ID,a.ACTIVITY_ID,ACTIVITY_STATUS,ACTIVITY_SEQ_ID,a.TENANT_ID,'0' ORDER_MARK,DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d %H:%i:%s') UPDATE_TIME,"
			   + "DATE_FORMAT(a.ORDER_END_DATE,'%Y%m%d %H%i%s') UPDATE_DATE,DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d %H:%i:%s') ORDER_END_DATE,"
			   + "DATE_FORMAT(a.ORDER_BEGIN_DATE,'%Y-%m-%d %H:%i:%s') ORDER_BEGIN_DATE,"
			   + "'到期剔除' REASON,ORI_AMOUNT-INOUT_FILTER_AMOUNT-COVERAGE_FILTER_AMOUNT-BLACK_FILTER_AMOUNT-RESERVE_FILTER_AMOUNT-TOUCH_FILTER_AMOUNT-SUCCESS_FILTER_AMOUNT-REPEAT_FILTER_AMOUNT UPDATE_NUM,"
			   + "  0 VALID_NUM "
				+"FROM PLT_ACTIVITY_INFO a, PLT_ACTIVITY_PROCESS_LOG s "
				+"WHERE a.ACTIVITY_ID = '"+req.get("activityId")+"'"
				+ " AND a.REC_ID = s.ACTIVITY_SEQ_ID"
				+ " AND s.CHANNEL_ID = '"+req.get("channelId")+"'"
				+ " AND a.TENANT_ID = '"+req.get("tenantId")+"'"
				+ " AND s.TENANT_ID = '"+req.get("tenantId")+"'"
				+ " AND a.ORDER_END_DATE <= NOW()"
			+"ORDER BY UPDATE_DATE ASC ")+LIMIT; 
	} 
	
	public String getUpadteCount(HashMap<String, Object> req){
		
		StringBuilder sql = new StringBuilder();
		sql.append(" a.ACTIVITY_ID =#{ACTIVITY_ID} ");
		sql.append(" AND s.ACTIVITY_SEQ_ID = a.REC_ID ");
		sql.append(" AND a.ORDER_BEGIN_DATE <= #{UPDATE_TIME} ");
		sql.append(" AND a.ORDER_END_DATE >= #{UPDATE_TIME} ");
//		sql.append(" AND a.ACTIVITY_STATUS <> '2' ");
//		sql.append(" AND a.ORDER_END_DATE >= NOW() ");
		sql.append(" AND s.CHANNEL_ID=#{CHANNEL_ID} ");
		sql.append(" AND a.TENANT_ID = #{TENANT_ID} ");
		sql.append(" AND s.TENANT_ID = #{TENANT_ID} ");	
		
		BEGIN();
		SELECT("  IFNULL(SUM(ORI_AMOUNT-INOUT_FILTER_AMOUNT-COVERAGE_FILTER_AMOUNT-BLACK_FILTER_AMOUNT-RESERVE_FILTER_AMOUNT-TOUCH_FILTER_AMOUNT-SUCCESS_FILTER_AMOUNT-REPEAT_FILTER_AMOUNT),0) VALID_NUM ");
		FROM(" PLT_ACTIVITY_INFO a,PLT_ACTIVITY_PROCESS_LOG s ");
		WHERE(sql.toString());
		
		return SQL();
	}
	
	public String getDeleteUpadteCount(HashMap<String, Object> req){
			
			StringBuilder sql = new StringBuilder();
			sql.append(" a.ACTIVITY_ID =#{ACTIVITY_ID} ");
			sql.append(" AND s.ACTIVITY_SEQ_ID = a.REC_ID ");
//			sql.append(" AND a.ORDER_BEGIN_DATE >= #{ORDER_BEGIN_DATE} ");
			sql.append(" AND a.ORDER_BEGIN_DATE <= #{UPDATE_TIME} ");
			sql.append(" AND a.ORDER_END_DATE >= #{UPDATE_TIME} ");
	//		sql.append(" AND a.ACTIVITY_STATUS <> '2' ");
	//		sql.append(" AND a.ORDER_END_DATE >= NOW() ");
			sql.append(" AND s.CHANNEL_ID=#{CHANNEL_ID} ");
			sql.append(" AND a.TENANT_ID = #{TENANT_ID} ");
			sql.append(" AND s.TENANT_ID = #{TENANT_ID} ");	
			
			BEGIN();
			SELECT("  IFNULL((ORI_AMOUNT-INOUT_FILTER_AMOUNT-COVERAGE_FILTER_AMOUNT-BLACK_FILTER_AMOUNT-RESERVE_FILTER_AMOUNT-TOUCH_FILTER_AMOUNT-SUCCESS_FILTER_AMOUNT-REPEAT_FILTER_AMOUNT),0) VALID_NUM ");
			FROM(" PLT_ACTIVITY_INFO a,PLT_ACTIVITY_PROCESS_LOG s ");
			WHERE(sql.toString());
			
			return SQL();
		}
	
	
	
	public String getOrderDealMonth(HashMap<String, Object> req){
		
		BEGIN();
		
		SELECT(" *,DATE_FORMAT(ORDER_BEGIN_DATE,'%Y%m%d %H%i%s')  ORDER_DATE ");
		
		FROM(" PLT_ACTIVITY_INFO ");
			
		WHERE(" REC_ID = #{activitySeqId}  ");
		
		WHERE(" TENANT_ID=#{tenantId} ");
		
		return SQL();
		
	}
	
	
	public String getUpdateRecord(HashMap<String, Object> req){
		
		BEGIN();
		//适用范围过滤        //上期剩余工单     //渠道协同过滤     //渠道筛选    //剔除重复工单
		
		SELECT(" a.ORDER_BEGIN_DATE UPDATE_DATE,a.ORDER_BEGIN_DATE VALIDED_DATE,a.ACTIVITY_STATUS, "
				
				+ " s.ORI_AMOUNT TARGET_USER, " // 用户数
				+ " s.BLACK_FILTER_AMOUNT BLACK_FILTER_COUNT,"// 免打扰过滤数
				+ " s.SUCCESS_FILTER_AMOUNT SUCCESS_FILTER_COUNT,"// 已成功过滤数
//				+ " SUM(s.VALID_NUM)-SUM(s.FILTER3_COUNT) UN_CONTACT_COUNT "//未接触工单数
//				+ " SUM(s.FILTER0_COUNT)+SUM(s.FILTER1_COUNT) RULE_FILTER_COUNT,"// 规则过滤数
				+ " s.RESERVE_FILTER_AMOUNT FILTER4_COUNT," //留存过滤数
				+ " s.INOUT_FILTER_AMOUNT INOUT_FILTER_AMOUNT," //有进有出过滤数
				+ " s.COVERAGE_FILTER_AMOUNT COVERAGE_FILTER_AMOUNT," //覆盖过滤数
				+ " s.TOUCH_FILTER_AMOUNT TOUCH_FILTER_AMOUNT," //接触过滤数
				+ " 0 as  RESERVE1," //留存工单
				+ " (s.REPEAT_FILTER_AMOUNT) as DISTINCT_COUNT,"
				+ " (s.ORI_AMOUNT-s.INOUT_FILTER_AMOUNT-s.COVERAGE_FILTER_AMOUNT-s.BLACK_FILTER_AMOUNT-s.RESERVE_FILTER_AMOUNT-s.TOUCH_FILTER_AMOUNT-s.SUCCESS_FILTER_AMOUNT-s.REPEAT_FILTER_AMOUNT) VALID_NUM ");//本期有效工单
				
		
		FROM(" PLT_ACTIVITY_PROCESS_LOG s,PLT_ACTIVITY_INFO a ");
		WHERE(" s.TENANT_ID=#{tenantId} ");
		WHERE(" a.TENANT_ID=#{tenantId} ");
		WHERE(" a.REC_ID = s.ACTIVITY_SEQ_ID ");	
		WHERE(" s.ACTIVITY_SEQ_ID = #{activitySeqId}");
		WHERE(" s.CHANNEL_ID=#{channelId} ");

		
		return SQL(); 
	}
	
	public String getLastOrderCount(HashMap<String, Object> req){
			
			BEGIN();
			//上期剩余工单    
			SELECT(" SUM(s.ORI_AMOUNT-s.INOUT_FILTER_AMOUNT-s.COVERAGE_FILTER_AMOUNT-s.BLACK_FILTER_AMOUNT-s.RESERVE_FILTER_AMOUNT-s.TOUCH_FILTER_AMOUNT-s.SUCCESS_FILTER_AMOUNT-s.REPEAT_FILTER_AMOUNT) UN_CONTACT_COUNT ");		
					
			FROM(" PLT_ACTIVITY_PROCESS_LOG s,PLT_ACTIVITY_INFO a,(SELECT ACTIVITY_ID,ORDER_BEGIN_DATE FROM PLT_ACTIVITY_INFO WHERE REC_ID = #{activitySeqId} AND TENANT_ID=#{tenantId}) r");
			
			WHERE(" r.ACTIVITY_ID = a.ACTIVITY_ID ");
			WHERE(" a.REC_ID = s.ACTIVITY_SEQ_ID ");
			WHERE(" a.ORDER_BEGIN_DATE < r.ORDER_BEGIN_DATE ");
			WHERE(" a.ORDER_END_DATE >= r.ORDER_BEGIN_DATE ");
//			WHERE(" a.ACTIVITY_STATUS <> '2' ");
//			WHERE(" a.ORDER_END_DATE >= NOW() ");
			WHERE(" s.CHANNEL_ID=#{channelId} ");
			WHERE(" s.TENANT_ID=#{tenantId} ");
			WHERE(" a.TENANT_ID=#{tenantId} ");

			return SQL(); 
		}
	
		public String getUpdateHistoryRecord(HashMap<String, Object> req){
			
			BEGIN();
			SELECT(" IFNULL(s.ORI_AMOUNT-s.INOUT_FILTER_AMOUNT-s.COVERAGE_FILTER_AMOUNT-s.BLACK_FILTER_AMOUNT-s.RESERVE_FILTER_AMOUNT-s.TOUCH_FILTER_AMOUNT-s.SUCCESS_FILTER_AMOUNT-s.REPEAT_FILTER_AMOUNT, 0) VALID_NUM,IFNULL(d.CONTACT_NUM, 0) SUCCESS_COUNT,"
					+ "IFNULL(s.ORI_AMOUNT-s.INOUT_FILTER_AMOUNT-s.COVERAGE_FILTER_AMOUNT-s.BLACK_FILTER_AMOUNT-s.RESERVE_FILTER_AMOUNT-s.TOUCH_FILTER_AMOUNT-s.SUCCESS_FILTER_AMOUNT-s.REPEAT_FILTER_AMOUNT, 0) - IFNULL(d.CONTACT_NUM, 0) UN_CONTACT_COUNT  ");		
					
			FROM(" PLT_BENCH_STATISTIC d , PLT_ACTIVITY_PROCESS_LOG s");
			WHERE(" d.ACTIVITY_SEQ_ID = #{activitySeqId} ");
			WHERE(" d.TENANT_ID=#{tenantId} ");
			WHERE(" d.CHANNEL_ID=#{channelId} ");
			WHERE(" d.ACTIVITY_SEQ_ID = s.ACTIVITY_SEQ_ID ");
			WHERE(" d.TENANT_ID=s.TENANT_ID ");
			WHERE(" d.CHANNEL_ID=s.CHANNEL_ID ");
			return SQL(); 
		}
		
		public String getAllOrderCount(HashMap<String, Object> req){
				
				BEGIN();
				//上期剩余工单    
				SELECT(" IFNULL(SUM(s.ORI_AMOUNT-s.INOUT_FILTER_AMOUNT-s.COVERAGE_FILTER_AMOUNT-s.BLACK_FILTER_AMOUNT-s.RESERVE_FILTER_AMOUNT-s.TOUCH_FILTER_AMOUNT-s.SUCCESS_FILTER_AMOUNT-s.REPEAT_FILTER_AMOUNT),0) VALID_NUM ");		
						
				FROM(" PLT_ACTIVITY_PROCESS_LOG s,PLT_ACTIVITY_INFO a,(SELECT ACTIVITY_ID,a.ORDER_END_DATE,a.ORDER_BEGIN_DATE FROM PLT_ACTIVITY_INFO WHERE REC_ID = #{activitySeqId} AND TENANT_ID=#{tenantId}) r");
				
				WHERE(" r.ACTIVITY_ID = a.ACTIVITY_ID ");
				WHERE(" a.REC_ID = s.ACTIVITY_SEQ_ID ");
				WHERE(" a.ORDER_BEGIN_DATE < r.ORDER_END_DATE ");
				WHERE(" a.ORDER_BEGIN_DATE > r.ORDER_BEGIN_DATE ");
				WHERE(" a.ORDER_END_DATE >= r.ORDER_END_DATE ");
				WHERE(" s.CHANNEL_ID=#{channelId} ");
				WHERE(" s.TENANT_ID=#{tenantId} ");
				WHERE(" a.TENANT_ID=#{tenantId} ");
	
				return SQL(); 
			}		
		
//		public String getDetailVaildCount(HashMap<String, Object> req){
//			
//			BEGIN();
//			SELECT(" IFNULL(s.VALID_NUM,0) VALID_NUM ");		
//					
//			FROM(" PLT_ORDER_DETAIL_COUNT s ");
//			WHERE(" s.TENANT_ID=#{tenantId} ");
//			WHERE(" s.ACTIVITY_SEQ_ID = #{activitySeqId} ");
//			WHERE(" s.CHANNEL_ID=#{channelId} ");
//
//			return SQL(); 
//		}	
		
		public String getUnValidNum(HashMap<String, Object> req){
			
			BEGIN();
			SELECT(" IFNULL(SUM(s.SEND_SUC_NUM),0) VISITED_SUCCESS, "
					+ " IFNULL(SUM(d.VALID_NUM) - SUM(s.SEND_SUC_NUM) , 0)  PAST_UN_CONTACT_COUNT");
			FROM(" PLT_ORDER_DETAIL_COUNT d,PLT_ORDER_STATISTIC_SEND s,PLT_ACTIVITY_INFO a  ");
			WHERE(getWhereSql(req));
//			WHERE("  a.ACTIVITY_STATUS = '2' ");
			WHERE(" a.ORDER_END_DATE < NOW()");
			return SQL(); 
		}	
		

		public String getValidNum(HashMap<String, Object> req){
			
			BEGIN();
			SELECT(" IFNULL(SUM(s.SEND_SUC_NUM),0) CONTACT_COUNT, "
					+ "IFNULL(SUM(d.VALID_NUM),0) VALID_NUM,"
					+ " IFNULL(SUM(d.VALID_NUM) - SUM(s.SEND_SUC_NUM) , 0)  ALL_UN_CONTACT_COUNT");
			FROM(" PLT_ORDER_DETAIL_COUNT d,PLT_ORDER_STATISTIC_SEND s,PLT_ACTIVITY_INFO a  ");
			WHERE(getWhereSql(req));
//			WHERE(" a.ACTIVITY_STATUS <> '2' ");
			WHERE(" a.ORDER_END_DATE >= NOW()");

			return SQL(); 
		}	
		
		public  String getWhereSql(HashMap<String, Object> req){
			
			StringBuilder where = new StringBuilder();
			where.append("a.ACTIVITY_ID = #{activityId}" );
			where.append(" AND a.REC_ID = s.ACTIVITY_SEQ_ID" );
			where.append(" AND a.REC_ID = d.ACTIVITY_SEQ_ID " );
			where.append(" AND d.CHANNEL_ID = s.CHANNEL_ID" );
			where.append(" AND d.CHANNEL_ID = #{channelId}" );
			where.append(" AND a.TENANT_ID = #{tenantId}" );
			where.append(" AND s.TENANT_ID =  #{tenantId} " );
			where.append(" AND d.TENANT_ID = #{tenantId}" );
			return where.toString();
		}
}
