package com.bonc.busi.count.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Select;
import com.bonc.busi.count.model.SendCount;

public interface MessageSendMapper {
	@Select("select "
			+ "activity_name 'activity_name',"
			+ " s.activity_seq_id 'activity_seq_id',"
			+ " send_all_count 'send_all_count',"
			+ "send_all_num 'send_all_num ' ,"
			+ "send_num 'send_num',"
			+ " send_suc_num 'send_suc_num',"
			+ " send_err_num 'send_err_num'"
			+ "  from PLT_ORDER_STATISTIC_SEND s"
			+ "  inner join PLT_ACTIVITY_INFO  c on c.REC_ID = s.ACTIVITY_SEQ_ID"
			+ " WHERE s.tenant_id=#{tenant_id} and s.channel_id='7' and s.is_finish=#{is_finish} limit #{startnum},#{endnum}")
	public List<SendCount> findCountList(SendCount sendCount);
	
	@Select("select "
			+" count(*)"
			+ "  from PLT_ORDER_STATISTIC_SEND s"
			+ "  inner join PLT_ACTIVITY_INFO  c on c.REC_ID = s.ACTIVITY_SEQ_ID"
			+ " WHERE s.tenant_id=#{tenant_id} and s.channel_id='7' and s.is_finish=#{is_finish} ")
	public String findCountTotal(SendCount sendCount);

}
