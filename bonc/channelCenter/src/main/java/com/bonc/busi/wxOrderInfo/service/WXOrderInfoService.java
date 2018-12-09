/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WXOrderInfoService.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.wxOrderInfo.service
 * @Description: 微信工单服务接口
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月10日 下午9:26:35
 * @version: V1.0  
 */

package com.bonc.busi.wxOrderInfo.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.entity.PageBean;
import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;
import com.bonc.busi.wxOrderInfo.po.WXOrderInfo;
import com.bonc.busi.wxProductInfo.po.WXProductInfo;


/**
 * @ClassName: WXOrderInfoService
 * @Description: 微信工单服务接口
 * @author: LiJinfeng
 * @date: 2016年12月11日 下午5:09:16
 */
public interface WXOrderInfoService {
	
	/**
	 * @Title: getConfig
	 * @Description: 设置所需常量
	 * @return: HashMap<String,Object>
	 * @param wxChannelId
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public HashMap<String, Object> getConfig(String wxChannelId,String tenantId);
	
	/**
	 * @Title: insertLog
	 * @Description: 记录日志
	 * @return: void
	 * @param wxActivityInfo
	 * @param logMessage
	 * @throws: 
	 */
	public void insertLog(WXActivityInfo wxActivityInfo,String logMessage);
	
	/**
	 * @Title: findActivityList
	 * @Description: 查询所有符合条件的活动
	 * @return: List<WXActivityInfo>
	 * @param config
	 * @return
	 * @throws: 
	 */
	public List<WXActivityInfo> findActivityList(@Param("config")HashMap<String, Object> config);
	
	/**
	 * @Title: wxActivityInfoFieldIsEmpty
	 * @Description: 校验wxActivityInfo的各字段是否为空
	 * @return: Boolean
	 * @param wxActivityInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean wxActivityInfoFieldIsEmpty(WXActivityInfo wxActivityInfo, HashMap<String, Object> config);
	
	/**
	 * @Title: getOrdereRecIdList
	 * @Description: 获取微信工单的REC_ID列表
	 * @return: List<Integer>
	 * @param config
	 * @return
	 * @throws: 
	 */
	public List<Integer> getOrdereRecIdList(WXActivityInfo wxActivityInfo,HashMap<String, Object> config);
	
	/**
	 * @Title: webChatInfoFormat
	 * @Description: 校验微信公众号信息
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean webChatInfoFormat(WXActivityInfo wxActivityInfo,HashMap<String, Object> config);
	
	/**
	 * @Title: findFieldList
	 * @Description: 查找模板对应的字段名、变量名
	 * @return: Boolean
	 * @param wxActivityInfo
	 * @return
	 * @throws: 
	 */
	public Boolean findFieldList(WXActivityInfo wxActivityInfo,HashMap<String, Object> config);
	
	/**
	 * @Title: productIdListIsEmpty
	 * @Description: 判断活动对应的产品列表是否为空
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean productIdListIsEmpty(WXActivityInfo wxActivityInfo, HashMap<String, Object> config);	
	
	/**
	 * @Title: productIsExistInRomote
	 * @Description: 判断产品在远程产品表中是否存在
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean productIsExistInRomote(WXActivityInfo wxActivityInfo, HashMap<String, Object> config);
	
	/**
	 * @Title: productFieldIsEmpty
	 * @Description: 判断产品列表各字段是否为空
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean productFieldIsEmpty(WXActivityInfo wxActivityInfo, HashMap<String, Object> config);
	
	/**
	 * @Title: flowTypeIsEquals
	 * @Description: 判断多个产品是否为同一类型
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	/*public Boolean flowTypeIsEquals(WXActivityInfo wxActivityInfo, HashMap<String, Object> config);*/
	
	/**
	 * @Title: updateWXProductInfo
	 * @Description: 更新本地产品表
	 * @return: void
	 * @param wxOrderInfo
	 * @param config
	 * @throws: 
	 */
	public Boolean updateWXProductInfo(List<WXProductInfo> wxProductInfoList, HashMap<String, Object> config);

	/**
	 * @Title: findWXOrderInfoListByChannelId
	 * @Description: 分页查询微信工单
	 * @return: List<WXOrderInfo>
	 * @param config
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String, Object>> findWXOrderInfoListByChannelId(HashMap<String, Object> config,
			WXActivityInfo wxActivityInfo);
	
	/**
	 * @Title: getCurrentPartitionFlag
	 * @Description: 获取当前用户有效分区标识
	 * @return: Boolean
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean getCurrentPartitionFlag(HashMap<String, Object> config);
	
	/**
	 * @Title: wxOrderInfoFieldIsEmpty
	 * @Description: 校验wxOrderInfo的各字段是否为空
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean wxOrderInfoFieldIsEmpty(HashMap<String,Object> wxOrderInfoMap,HashMap<String, Object> config,
			WXActivityInfo wxActivityInfo);	
	
	/**
	 * @Title: analyzeWXOrderInfoMap
	 * @Description: 分解wxOrderInfoMap的键值对，生成wxOrderInfo和fieldInfo
	 * @return: Boolean
	 * @param wxOrderInfoMap
	 * @param config
	 * @param wxOrderInfo
	 * @param fieldInfo
	 * @return
	 * @throws: 
	 */
	public Boolean analyzeWXOrderInfoMap(HashMap<String,Object> wxOrderInfoMap, HashMap<String, Object> config,
    		WXActivityInfo wxActivityInfo,WXOrderInfo wxOrderInfo);
	
	/**
	 * @Title: getTalkVarList
	 * @Description: 动态获取话术变量列表
	 * @return: Boolean
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean getTalkVarList(HashMap<String, Object> config);
	
	
	/**
	 * @Title: setProductId
	 * @Description: 设置ProductIds
	 * @return: void
	 * @param wxActivityInfo
	 * @throws: 
	 */
	public Boolean setProductIds(WXActivityInfo wxActivityInfo,HashMap<String, Object> config);
	
	/**
	 * @Title: updateChannelStatus
	 * @Description: 更改工单状态
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	/*public Boolean updateChannelStatus(WXOrderInfo wxOrderInfo, HashMap<String, Object> config);*/
	
	/**
	 * @Title: insertWXOrderInfo
	 * @Description: 将组装好的微信工单入表
	 * @return: Boolean
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean insertWXOrderInfo(List<WXOrderInfo> wxOrderInfoList, 
			HashMap<String, Object> config);
	
	/**
	 * @Title: countWXOrder
	 * @Description: 统计微信工单下发信息
	 * @return: void
	 * @param pageBean
	 * @param resultListSize
	 * @param isSuccess
	 * @throws: 
	 */
	public void countWXOrder(HashMap<String, Object> config,WXActivityInfo wxActivityInfo,Integer ordereRecIdListSize,
			PageBean pageBean,Integer resultListSize,Boolean isSuccess);
	
	/**
	 * @Title: setActivityChannelStatus
	 * @Description: 设置活动渠道状态
	 * @return: Boolean
	 * @param wxActivityInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	public Boolean setActivityChannelStatus(WXActivityInfo wxActivityInfo, HashMap<String, Object> config);
}
