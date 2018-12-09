package com.bonc.busi.orderschedule.service;

import java.util.List;

import com.bonc.busi.orderschedule.bo.BlackWhiteUserList;
import com.bonc.busi.orderschedule.bo.ParamMap;
import com.bonc.busi.orderschedule.redis.JodisProperties;

import redis.clients.jedis.Jedis;

public interface FilterOrderService {

	String isBlackWhiteUser(BlackWhiteUserList user);
	
	/**
	 * 黑名单过滤工单
	 * @param activityId     活动Id
	 * @param activitySEQID  活动批次
	 * @param tenantId       租户Id
	 * @param channelList    渠道列表
	 */
	void filterOrderWithBlackUser(String activityId,Integer activitySEQID, String tenantId, List<String> channelList);

	void filterOrderStatus(String actId,Integer activitySEQID, String tenantId, List<String> channelList);
	/**
	 * 有进有出过滤/覆盖过滤/接触过滤
	 * @param activitySEQID
	 * @param tenantId
	 * @param channelList
	 */
	//void filterOrderStatus(Integer activitySEQID, String tenantId, List<String> channelList);
	

	/**
	 *  reserve order  base on activity percent
	 *  @param actId: activity's id
	 *  @param actSeqId:activity's batch id 
	 *  @param tenantId:tenant id
	 *  @param channelList: activity channels gennerated  order 
	 */
	void reserveOrder(String actId,Integer actSeqId, String tenantId, List<String> channelList);

	/**
	 * 从临时表plt_order_info_temp里删除过滤的工单，并且把过滤的工单数记录到plt_activity_process_log表里
	 * @param activityid  活动ID
	 * @param activitySeqId 活动批次
	 * @param tenantId      租户ID
	 * @param enableChannel 渠道列表
	 * @param orderStatus   工单状态
	 */
	void delAndUpdateFilterCount(String activityid, Integer activitySeqId, String tenantId, List<String> enableChannel,String orderStatus);

	/**
	 * 从临时表里删除具有相同的手机号码的重复的工单
	 * @param activityid  活动Id
	 * @param activitySeqId 批次Id
	 * @param tenantId      租户Id
	 * @param enableChannel  渠道列表
	 */
	void delRepeatedOrder(String activityid, Integer activitySeqId, String tenantId, List<String> enableChannel);

}
