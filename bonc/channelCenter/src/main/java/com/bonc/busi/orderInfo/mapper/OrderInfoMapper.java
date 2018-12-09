/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ActivityConfigInfoMapper.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.activityConfigInfo.mapper
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月18日 上午11:08:17
 * @version: V1.0  
 */

package com.bonc.busi.orderInfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.activityInfo.po.ActivityInfo;

/**
 * @ClassName: OrderInfoMapper
 * @Description: TODO
 * @author: sky
 */
public interface OrderInfoMapper {
	/**
	 * updateChannelStatus
	 * 更新订单状态
	 * @param activitySeqId
	 * @param ordStatus
	 * @param channelList
	 * @param channelStatus
	 * @return
	 */
	public void updateChannelStatus(@Param("tenantId")String tenantId,@Param("activitySeqId")Integer activitySeqId,@Param("ordStatus")String ordStatus,
			@Param("dealMonth")String dealMonth,@Param("channelStatus")String channelStatus);
	
	
	/**
	 * 
	 * @param userIdList
	 */
	public Integer updateChannelStatusBatch(@Param("tenantId")String tenantId,@Param("activitySeqId")Integer activitySeqId,@Param("ordStatus")String ordStatus,
			@Param("dealMonth")String dealMonth,@Param("channelStatus")String channelStatus,@Param("userIdList")List<String> userIdList);
	
	

}
