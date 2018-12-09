package com.bonc.busi.service.dao;
/*
 * @desc:PLT_ORDER_INFO_XX的相关操作
 * @author:zengdingyong
 * @time:2017-06-05
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;


import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.OrderCheckInfo;

import kafka.log.Log;

public class PltorderinfoDao {
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	private final static Logger log= LoggerFactory.getLogger(PltorderinfoDao.class);
	
	/*
	 * 提取事后检查工单
	 */
	public		static		List<OrderCheckInfo>     getOrderListForCheck(int ActivitySeqId,String TenantId,long RecId){
		StringBuilder			sb = new StringBuilder();
		sb.append("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_CHECK_SUCESS_MEM ");
//		sb.append(" WHERE   ORDER_STATUS ='5' AND CHANNEL_STATUS  IN('0','2') ");
		sb.append(" WHERE  TENANT_ID=?   AND REC_ID  > ?  ");
		sb.append(" ORDER BY REC_ID LIMIT 10000");
		RowMapper<OrderCheckInfo > rm = BeanPropertyRowMapper.newInstance(OrderCheckInfo .class);
		List<OrderCheckInfo>			listOrderInfo = jdbcTemplate.query(sb.toString(),
				new Object[]{TenantId,RecId},
				new int[]{java.sql.Types.VARCHAR,java.sql.Types.BIGINT},
				rm
				);
		return listOrderInfo;
	}
	/*
	 * 提取事前过滤工单
	 */
	public		static		List<OrderCheckInfo>     getOrderListForFilter(int ActivitySeqId,String TenantId,long RecId){
		StringBuilder			sb = new StringBuilder();
		sb.append("SELECT REC_ID orderRecId,USER_ID userId  FROM PLT_ORDER_CHECK_FILTER_MEM  ");
		sb.append(" WHERE TENANT_ID=?   AND REC_ID  > ?  ");
		sb.append(" ORDER BY REC_ID LIMIT 10000");
		log.info("sql={}",sb.toString());

	//	List<OrderCheckInfo>			listOrderInfo = jdbcTemplate.queryForList(sb.toString(),
	
//		List<OrderCheckInfo>			listOrderInfo = jdbcTemplate.query(sb.toString(),
//				new Object[]{ActivitySeqId,TenantId,RecId,ChannelId},
//				new int[]{java.sql.Types.INTEGER,java.sql.Types.VARCHAR,java.sql.Types.BIGINT,java.sql.Types.VARCHAR},
//				OrderCheckInfo.class
//				);
		RowMapper<OrderCheckInfo > rm = BeanPropertyRowMapper.newInstance(OrderCheckInfo .class);
		List<OrderCheckInfo>			listOrderInfo = jdbcTemplate.query(sb.toString(),
				new Object[]{TenantId,RecId},
				new int[]{java.sql.Types.VARCHAR,java.sql.Types.BIGINT},
				rm
				);
		return listOrderInfo;
	}
	/*
	 * 更新事后检查工单
	 */
	public		static		int			updateOrderForCheck(List<String> TableName,int ActivitySeqId,String TenantId,String users,String MaxMonthDay){
		StringBuilder			sb = new StringBuilder();
		int		count =0;
		
		
		for(String  item:TableName){
			sb.setLength(0);
			sb.append("UPDATE ");
			sb.append(item);
			sb.append("  SET  SUCCESS_STATUS = 1 ,SUCCESS_UPDATE_TIME = now() ,SUCCESS_DATEID = ");
			sb.append(MaxMonthDay);
			sb.append(" WHERE TENANT_ID='");
			sb.append(TenantId);
			sb.append("'");
			sb.append("  AND ACTIVITY_SEQ_ID =");
			sb.append(ActivitySeqId);
			sb.append("  AND USER_ID IN (");
			sb.append(users);
			sb.append(")");
			
			count  +=   jdbcTemplate.update(sb.toString());
		}
		
		return count;		
	}
	/*
	 * 更新事前过滤工单
	 */
	public		static		int			updateOrderForFilter(String TableName,int ActivitySeqId,String TenantId,String users,String MaxMonthDay){
		StringBuilder			sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(TableName);
		sb.append("  SET  ORDER_STATUS = '6' ");
		sb.append(" WHERE TENANT_ID='");
		sb.append(TenantId);
		sb.append("'");
		sb.append("  AND ACTIVITY_SEQ_ID =");
		sb.append(ActivitySeqId);
		// --- 一个活动序列号的所有工单在一个表中，更新时不用限制渠道，则所有成功工单都更新 ----
//		sb.append("  AND CHANNEL_ID = ");
//		sb.append(ChannelId);
		sb.append("  AND USER_ID IN (");
		sb.append(users);
		sb.append(")");
		log.info("update sql ={}",sb.toString());
		int		count=jdbcTemplate.update(sb.toString());
		
		return count;		
	}
//--更新接触过滤工单的成功状态--
	public		static		int			updateContactOrderForSuccess(String TableName,int ActivitySeqId,String TenantId,String MaxMonthDay,int num){
		StringBuilder			sb = new StringBuilder();
		//--SELECT COUNT(1) FROM `plt_order_info_15` WHERE CONTACT_RESULT = '1'  AND  TENANT_ID = 'uni097'; -- AND ACTIVITY_SEQ_ID = --
		sb.append("UPDATE ");
		sb.append(TableName);
		sb.append("  SET  SUCCESS_STATUS = 1 ,SUCCESS_UPDATE_TIME = now() ,SUCCESS_DATEID = ");
		sb.append(MaxMonthDay);
		sb.append(" WHERE TENANT_ID='");
		sb.append(TenantId);
		sb.append("'");
		sb.append("  AND ACTIVITY_SEQ_ID =");
		sb.append(ActivitySeqId);
		sb.append("  AND SUCCESS_STATUS <> 1");
		sb.append(" AND CONTACT_RESULT = '1'");
		if(num>10000){
			sb.append(" LIMIT 10000");
		}
		
		log.info("select count sql ={}",sb.toString());
		int		count=jdbcTemplate.update(sb.toString());
		return count;		
	}
	
}
