package com.bonc.busi.statistic.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface StatisticActivityMapper {

	@Update("DELETE FROM PLT_ORDER_STATISTIC WHERE TENANT_ID=#{tenantId}  "
			+ " AND ACTIVITY_SEQ_ID=#{activitySeqId} AND ORG_PATH LIKE '${orgPath}%' ")
	void delOrgStatistic(HashMap<String, Object> req);

	/**
	 * 任意获取一条工单
	 * @param string
	 * @return
	 */
	@Select("SELECT o.* FROM PLT_ORDER_INFO o WHERE o.TENANT_ID=#{tenantId} AND o.ACTIVITY_SEQ_ID=#{activitySeqId} AND o.CHANNEL_ID='5' LIMIT 1")
	HashMap<String, Object> orderLimit(Map<String, Object> map);

	@Update("UPDATE PLT_ORDER_STATISTIC SET SERVICE_TYPE=#{SERVICE_TYPE},BEGIN_DATE=#{BEGIN_DATE},END_DATE=#{END_DATE} WHERE TENANT_ID=#{TENANT_ID} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} " )
	void updateStatistic(HashMap<String, Object> orderHashMap);

}
