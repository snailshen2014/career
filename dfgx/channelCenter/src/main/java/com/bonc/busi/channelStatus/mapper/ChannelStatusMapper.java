package com.bonc.busi.channelStatus.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.activityInfo.po.ActivityInfo;

/**
 * @ClassName: ChannelStatusMapper
 * @Description: TODO
 * @author: sky
 */
public interface ChannelStatusMapper {
	
	/**
	 * updateChannelSendStatus
	 * 更新渠道下发状态
	 * @param activityId
	 * @param channelList
	 * @return
	 */
	public void updateChannelSendStatus(@Param("activitySeqIdList")List<Integer> activitySeqIdList,
			@Param("tenantId") String tenantId,@Param("channelList")List<String> channelList,@Param("status") String status);
	
	

}
