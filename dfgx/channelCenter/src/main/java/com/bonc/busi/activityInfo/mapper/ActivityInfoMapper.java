package com.bonc.busi.activityInfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.activityInfo.po.ActivityInfo;

/**
 * @ClassName: ActivityInfoMapper
 * @Description: TODO
 * @author: sky
 */
public interface ActivityInfoMapper {
	
	/**
	 * findNeedSendActivityInfos
	 * 查询需要下发的活动
	 * @param tenantId
	 * @param ordStatus
	 * @param channelList
	 * @return
	 */
	public List<ActivityInfo> findNeedSendActivityInfos(@Param("tenantId")String tenantId,@Param("ordStatus")String ordStatus,
			@Param("channelList")List<String> channelList);
	
	/**
	 * findSendCountByActivityId
	 * 查询周期性活动已下发计数
	 * @param activityId
	 * @param tenantId
	 * @return
	 */
	public int findSendCountByActivityId(@Param("activityId")String activityId,@Param("tenantId")String tenantId);
	/**
	 * findSendCountByParentActivityId
	 * 查询子活动已下发计数
	 * @param parentActivityId
	 * @param tenantId
	 * @return
	 */
	public int findSendCountByParentActivityId(@Param("parentActivityId")String parentActivityId,@Param("tenantId")String tenantId);

}

