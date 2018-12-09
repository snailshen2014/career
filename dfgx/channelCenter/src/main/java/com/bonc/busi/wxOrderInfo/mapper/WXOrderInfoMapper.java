/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WXOrderInfoMapper.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.wxOrderInfo.mapper
 * @Description: WXOrderInfoMapper
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月19日 下午4:29:17
 * @version: V1.0  
 */

package com.bonc.busi.wxOrderInfo.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;
import com.bonc.busi.wxOrderInfo.po.WXOrderInfo;

/**
 * @ClassName: WXOrderInfoMapper
 * @Description: WXOrderInfoMapper
 * @author: LiJinfeng
 * @date: 2016年11月19日 下午4:29:17
 */

public interface WXOrderInfoMapper {
	
	/**
	 * @Title: findActivityList
	 * @Description: 查询所有符合条件的活动
	 * @return: List<WXActivityInfo>
	 * @param config
	 * @return
	 * @throws: 
	 */
	public List<WXActivityInfo> findActivityList(@Param("config")HashMap<String, Object> config,
			@Param("activityStatusList")List<Integer> activityStatusList);
	
	/**
	 * @Title: findTemplateByActivityId
	 * @Description: 根据活动ID查找TemplateId
	 * @return: String
	 * @param activityId
	 * @param webChatStatus
	 * @return
	 * @throws: 
	 */
	public String findTemplateIdByActivityId(@Param("config")HashMap<String, Object> config,
			@Param("activityId")String activityId, @Param("webChatStatus")String webChatStatus);
	
	/**
	 * @Title: findProductIdListByAvtivityId
	 * @Description: 获取活动对应的产品列表
	 * @return: List<String>
	 * @param wxOrderInfo
	 * @return
	 * @throws: 
	 */
	public List<String> findProductIdListByAvtivityId(@Param("wxActivityInfo")WXActivityInfo wxActivityInfo);
	
	
	/**
	 * @Title: findFieldList
	 * @Description: 查询变量变量名、字段名
	 * @return: List<HashMap>
	 * @param wxActivityInfo
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String,Object>> findFieldList(@Param("wxActivityInfo")WXActivityInfo wxActivityInfo);
	
	/**
	 * @Title: getOrdereRecIdList
	 * @Description: 获取微信工单的REC_ID列表
	 * @return: List<Integer>
	 * @param config
	 * @return
	 * @throws: 
	 */
	public List<Integer> getOrdereRecIdList(@Param("wxActivityInfo")WXActivityInfo wxActivityInfo,
			@Param("config")HashMap<String, Object> config);
	
	
	
	/**
	 * @Title: findWXOrderInfoListByChannelId
	 * @Description: 分页查询微信工单
	 * @return: List<WXOrderInfo>
	 * @param config
	 * @param orderRecIdList
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String,Object>> findWXOrderInfoListByChannelId(@Param("config")HashMap<String, Object> config,
			@Param("orderRecIdList")List<Integer> orderRecIdList,@Param("wxActivityInfo")WXActivityInfo wxActivityInfo,
			@Param("fieldList")List<HashMap<String,Object>> fieldList,
			@Param("activityStatusList")List<Integer> activityStatusList,
			@Param("mysqlFieldList")List<String> mysqlFieldList);
	
	
	/**
	 * @Title: updateWXChannelStatus
	 * @Description: 更新PLT_ORDER_INFO(工单表)中的渠道状态
	 * @return: void
	 * @param config
	 * @param orderRecId
	 * @param wxChannelStatus
	 * @throws: 
	 */
	public Integer updateWXChannelStatus(@Param("config")HashMap<String, Object> config,
			@Param("wxOrderInfoList")List<WXOrderInfo> wxOrderInfoList,
			@Param("wxChannelStatus") String wxChannelStatus);
	
	/**
	 * @Title: updateDXChannelStatus
	 * @Description: 更新PLT_ORDER_INFO_SMS(短信工单表)中的渠道状态 ====暂时不用
	 * @return: void
	 * @param channelStatus
	 * @param orderRecId
	 * @param tenantId
	 * @throws: 
	 */
	public Integer updateDXChannelStatus(@Param("config")HashMap<String, Object> config,
			@Param("wxOrderInfo")WXOrderInfo wxOrderInfo);
	
	
	/**
	 * @Title: findProductIdAndIsFollowPublicAndMBNetTypeByPhone
	 * @Description: 根据USER_ID查询用户的主产品套餐、piblicCode字段名称、用户移动网别
	 * @return: WXOrderInfo
	 * @param userId
	 * @param tenantId
	 * @param publicCode
	 * @return
	 * @throws: 
	 */
	public WXOrderInfo findProductIdAndIsFollowPublicAndMBNetTypeByPhone(@Param("userId")String userId,
			@Param("tenantId")String tenantId,@Param("publicCode")String publicCode);
	
	/**
	 * @Title: getUserOpenId
	 * @Description: 获取用户的OpenId
	 * @return: Boolean
	 * @param wxActivityInfo
	 * @param wxOrderInfo
	 * @return
	 * @throws: 
	 */
	public String getUserOpenId(@Param("wxActivityInfo")WXActivityInfo wxActivityInfo,
			@Param("wxOrderInfo")WXOrderInfo wxOrderInfo);
	
	/**
	 * @Title: insertWXOrderInfo
	 * @Description: 将组装好的微信工单入表
	 * @return: void
	 * @param wxOrderInfo
	 * @throws: 
	 */
	public Integer insertWXOrderInfo(@Param("wxOrderInfoList")List<WXOrderInfo> wxOrderInfoList);
	
	/**
	 * @Title: getCountStatistic
	 * @Description: 查看统计信息表中是否有此条记录
	 * @return: Integer
	 * @param config
	 * @param activitySeqId
	 * @return
	 * @throws: 
	 */
	public Integer getCountStatistic(@Param("config")HashMap<String, Object> config,
			@Param("activitySeqId")Integer activitySeqId);
	
	/**
	 * @Title: insertCountWXOrder
	 * @Description: 向统计信息表中插入数据
	 * @return: Integer
	 * @param config
	 * @param countWXOrder
	 * @return
	 * @throws: 
	 */
	public Integer insertCountWXOrder(@Param("config")HashMap<String, Object> config,
			@Param("countWXOrder")HashMap<String, Object> countWXOrder);
	
	/**
	 * @Title: updateCountWXOrder
	 * @Description: 更新统计信息表数据
	 * @return: Integer
	 * @param config
	 * @param countWXOrder
	 * @return
	 * @throws: 
	 */
	public Integer updateCountWXOrder(@Param("config")HashMap<String, Object> config,
			@Param("countWXOrder")HashMap<String, Object> countWXOrder);
	
	/**
	 * @Title: setActivityChannelStatus
	 * @Description: 设置活动渠道状态
	 * @return: Integer
	 * @param wxActivityInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Integer setActivityChannelStatus(@Param("wxActivityInfo")WXActivityInfo wxActivityInfo, 
			@Param("config")HashMap<String, Object> config);
	
	

	
}
