package com.bonc.busi.send.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface YJSentMapper {

	@Select("SELECT DISTINCT a.REC_ID, a.ACTIVITY_ID,a.ACTIVITY_TYPE,a.TENANT_ID FROM PLT_ACTIVITY_INFO a,PLT_ORDER_STATISTIC_SEND d WHERE a.TENANT_ID=#{TENANT_ID} AND a.TENANT_ID=d.TENANT_ID "
			+ " AND a.ACTIVITY_STATUS IN ('1','8','9') AND d.CHANNEL_ID IN ('1','2','9') AND d.IS_FINISH=0 ")
	public List<HashMap<String, Object>> findActivityDetail(HashMap<String, Object> req);

	/**
	 * 修改其他渠道的活动为失效
	 * @param reqMap
	 */
	@Update("UPDATE PLT_ORDER_INFO SET CHANNEL_STATUS=#{channelStatus} WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID IN ('1','2','9') AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} ") 
	public void modifyChannelStatus(HashMap<String, Object> reqMap);

	@Update("UPDATE PLT_ORDER_STATISTIC_SEND SET IS_FINISH=#{status} WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID IN ('1','2','9') AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} ")
	public void modifyChannelStatis(HashMap<String, Object> reqMap);
}
