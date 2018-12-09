package com.bonc.busi.orderschedule.service;

import java.util.List;

import com.bonc.busi.orderschedule.bo.BlackWhiteUserList;
import com.bonc.busi.orderschedule.bo.ParamMap;
import com.bonc.busi.orderschedule.utils.JodisProperties;

import redis.clients.jedis.Jedis;

public interface FilterOrderService {
	// 删除重复工单
	void updateRepetitiveOrder(Integer activitySEQId, String tenantId, String channelid);

	/**
	 * 过滤工单，包括过滤黑白名单，和删除重复工单
	 */
	String isBlackWhiteUser(BlackWhiteUserList user);

	void filterOrderStatus(Integer activitySEQID, String tenantId, List<String> channelList);
	
	
	/**
	 * 根据接触频次过滤工单
	 */
	void updateOrderByTouchLimitDay(Integer activitySEQId, String tenantId, String channelid);

	/**
	 * 过滤黑名单
	 * @param tenant_id
	 * @param activity_seq_id
	 * @param isFilterSMS
	 */
	void filterBlackUser(String tenant_id, Integer activity_seq_id, String channelid);

	/**
	 * 过滤白名单
	 * @param tenant_id
	 * @param activity_seq_id
	 * @param isFilterSMS
	 */
	void filterWhiteUser(String tenant_id, Integer activity_seq_id, String channelid);

	/**
	 * 过滤order_info表黑名单，并移入历史表， 删除原工单
	 *@param paramMap
	 * @param jedis
	 */
	void filterAndHisOrderInfoBlack(ParamMap paramMap, Jedis jedis);

	/**
	 * 过滤order_info表白名单，并移入历史表， 删除原工单
 *@param paramMap
	 * @param jedis
	 */
	void filterAndHisOrderInfoWhite(ParamMap paramMap, Jedis jedis);

	/**
	 * 过滤order_info_sms表黑名单，并移入历史表， 删除原短信工单
	  *@param paramMap
	 * @param jedis
	 */
	void filterAndHisOrderSmsBlack(ParamMap paramMap, Jedis jedis);

	/**
	 * 过滤order_info_sms表白名单，并移入历史表， 删除原短信工单
	 *@param paramMap
	 * @param jedis
	 */
	void filterAndHisOrderSmsWhite(ParamMap paramMap, Jedis jedis);

	/**
	 * jodis的配置信息
	 * 
	 * @return
	 */
	JodisProperties getJodisProperties();
	void filterRuleOrder(Integer activitySEQId, String tenantId, String channelid);
	/**
	 *  reserve order  base on activity percent
	 *  @param actId: activity's id
	 *  @param actSeqId:activity's batch id 
	 *  @param tenantId:tenant id
	 *  @param channelList: activity channels gennerated  order 
	 */
	void reserveOrder(String actId,Integer actSeqId, String tenantId, List<String> channelList);

}
