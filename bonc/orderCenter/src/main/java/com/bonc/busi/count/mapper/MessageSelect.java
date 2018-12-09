package com.bonc.busi.count.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;
import com.bonc.busi.count.model.SendCount;

public class MessageSelect {
	private StringBuilder getDivideWhere(SendCount sendCount){
		StringBuilder whereBuilder = new StringBuilder(" 1=1 ");
		whereBuilder.append(" AND s.TENANT_ID=#{tenant_id} ");
    	whereBuilder.append(" AND s.channel_id='7'");
    	whereBuilder.append(" AND s.is_finish=#{is_finish}");
    	
    	if(null!=sendCount.getActivity_name()&&!"".equals(sendCount.getActivity_name())){
    		whereBuilder.append(" AND c.ACTIVITY_NAME like '%").append(sendCount.getActivity_name()).append("%'");
    	}
    	
    	if(null!=sendCount.getActivity_seq_id()&&!"".equals(sendCount.getActivity_seq_id())){
    		whereBuilder.append(" AND s.activity_seq_id=#{activity_seq_id} ");
    	}
    	whereBuilder.append(" AND c.REC_ID = s.ACTIVITY_SEQ_ID");
    	return whereBuilder;
	}
	

	
	public String activityCount(SendCount sendCount){
    	BEGIN();
    	String sql =" external_id 'external_id',"
    			+ "send_date 'send_date',"
    			+ "activity_name 'activity_name',"
    			+ "activity_seq_id 'activity_seq_id',"
    			+ "send_all_count 'send_all_count',"
    			+ "valid_num 'valid_num',"
    			+ "send_all_num 'send_all_num',"
    			+ "valid_num-send_all_num 'send_no_num',"
    			+ "send_num 'send_num',"
    			+ "send_suc_num 'send_suc_num',"
    			+ "send_err_num 'send_err_num'";
    	SELECT(sql);
        FROM(" PLT_ORDER_STATISTIC_SEND s,PLT_ACTIVITY_INFO c " );
        WHERE( getDivideWhere(sendCount).toString());
        ORDER_BY(" send_date desc");
        //设置分页参数
        return SQL()+"  limit #{startnum},#{endnum}";
	}
	
	public String activityCountByCondition(SendCount sendCount){
    	BEGIN();
    	SELECT("count(*)");
        FROM(" PLT_ORDER_STATISTIC_SEND s,PLT_ACTIVITY_INFO c " );
        WHERE( getDivideWhere(sendCount).toString());
       
        //设置分页参数
        return SQL();
	}

}
