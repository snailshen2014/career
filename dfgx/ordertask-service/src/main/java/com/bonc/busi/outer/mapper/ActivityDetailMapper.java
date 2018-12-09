/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: ActivityDetailMapper.java
 * @Prject: orderCenter
 * @Package: com.bonc.busi.interfaces.mapper
 * @Description: ActivityDetailMapper
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月14日 下午5:38:11
 * @version: V1.0  
 */

package com.bonc.busi.outer.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.junit.runners.Parameterized.Parameters;

/**
 * @ClassName: ActivityDetailMapper
 * @Description: ActivityDetailMapper
 * @author: LiJinfeng
 * @date: 2017年1月14日 下午5:38:11
 */
public interface ActivityDetailMapper {
	
	/**
	 * @Title: getChannelOrderCountByActivityId
	 * @Description: 获取渠道工单数量
	 * @return: List<HashMap<String,Object>>
	 * @param activityId
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	@Select("SELECT CHANNEL_ID channelId,ORI_AMOUNT-INOUT_FILTER_AMOUNT-COVERAGE_FILTER_AMOUNT-BLACK_FILTER_AMOUNT-RESERVE_FILTER_AMOUNT-TOUCH_FILTER_AMOUNT-SUCCESS_FILTER_AMOUNT-REPEAT_FILTER_AMOUNT channelOrderNum FROM PLT_ACTIVITY_PROCESS_LOG WHERE "
			+ " TENANT_ID = #{tenantId} AND ACTIVITY_SEQ_ID =${activitySeqId} AND ACTIVITY_ID=#{activityId}")
	public List<HashMap<String, Object>> getChannelOrderCountByActivityId(@Param("activityId")String activityId,
			@Param("tenantId")String tenantId,@Param("activitySeqId") int activitySeqId);

	/**
	 * @Title: getActivityLogListByActivityList
	 * @Description: 获取活动日志列表
	 * @return: List<HashMap<String,Object>>
	 * @param parameter
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String,Object>> getActivityLogListByActivityList(
			@Param("parameterMap")HashMap<String, Object> parameterMap);
	
	/**
	 * @Title: getActivityIdList
	 * @Description: 获取活动ID列表
	 * @return: List<String>
	 * @param parameter
	 * @return
	 * @throws: 
	 */
	public List<String> getActivityIdList(@Param("parameterMap")HashMap<String, Object> parameter);

}
