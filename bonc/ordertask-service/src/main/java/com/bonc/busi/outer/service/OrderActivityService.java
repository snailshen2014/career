package com.bonc.busi.outer.service;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.busi.outer.bo.ActivityChannelExecute;
import com.bonc.busi.outer.bo.ActivityProcessLog;
import com.bonc.busi.outer.bo.OrderTableUsingInfo;
import com.bonc.busi.outer.bo.OrderTablesAssignRecord4S;
import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.common.base.JsonResult;

public interface OrderActivityService {

	/*
	 * 更新活动状态 （场景营销移植）
	 */
	public  JsonResult setActivityStatus(ActivityStatus reqdata);



	/**
	 * 根据活动的Id 租户Id获取活动的有效批次
	 * @param activityId 活动的Id
	 * @return  活动的有效批次
	 */
	public List<Integer> getActivitySEQIdsById(String activityId,String tenantId);
	
	/**
	 * 根据活动的Id 租户Id获取活动的无效批次
	 * @param activityId 活动的Id
	 * @return  活动的有效批次
	 */
	public List<Integer> getActivityInvalidSEQIdsById(String activityId,String tenantId);

	/**
	 * 根据活动的Id 租户Id获取活动最新的有效批次
	 * @param activityId 活动的Id
	 * @return 活动最新的有效批次
	 */
	public Integer getLatestActivitySEQIdById(String activityId,String tenantId);

	/**
	 * 根据活动id、租户id、批次查询工单的各类数量
	 * @param processLog
	 */
	public List<Map<String,Integer>> getOrderCount(ActivityProcessLog processLog);

	/**
	 * 获取工单的表名
	 * @param orderTable 封装了请求参数的OrderTablesAssignRecord4S对象
	 * @return
	 */
	public String getOrderTableName(OrderTablesAssignRecord4S orderTable);

	/**
	 * 根据活动Id 活动批次 获取生成工单的渠道列表
	 * @param activityId   活动Id
	 * @param activitySeqId  活动批次
	 * @return  生成工单的渠道列表
	 */
	public List<String> getOrderChannelListByActivityIdAndSeqId(String activityId, int activitySeqId,String tenantId);

	/**
	 * 根据手机号、渠道获取工单表名
	 * @param paramMap 封装的请求参数
	 * @return 工单表名
	 */
	public List<String> getOrderTableNamesByPhoneAndChannelId(RequestParamMap paramMap);

	/**
	 * 根据活动和渠道查询工单表列表
	 * @param paramMap
	 * @return
	 */
	public List<String> getOrderTableListByActivityAndChannel(RequestParamMap paramMap);

	/**
	 * 根据渠到获取表列表
	 * @param paramMap
	 * @return
	 */
	public List<String> getOrderTableListByChannel(RequestParamMap paramMap);
	
	/**
	 * 判断指定的活动的批次是否失效    失效时返回true
	 * @param activityId
	 * @param activitySeqId
	 * @return
	 */
	public boolean isActivityInvalid(String activityId, int activitySeqId,String tenantId);

	/**
	 * 查询PLT_ORDER_TABLE_COLUMN_MAP_INFO表生成列映射map(宽表字段到工单表字段的映射)
	 * @param param
	 * @return
	 */
	public Map<String, String> getOrderTableColumnMap(RequestParamMap param);

	/**
	 * 根据手机号码查询工单表名
	 * @param param
	 * @return
	 */
	public List<String> getOrderTableNameByPhoneNumber(RequestParamMap param);

	/**
	 * 根据活动、批次查询PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中可执行的渠道列表
	 * @param param
	 * @return
	 */
	public List<String> getExecuteableChannelList(RequestParamMap param);
	
	public  List<HashMap<String, Object>> queryActivityOrderInfo(HashMap<String,Object> req);


    /**
     * 根据渠道查询可执行的批次
     * @param executeInterface
     * @retur
     */
	public List<Integer> getExecuteableActivityInfo(ActivityChannelExecute executeInterface);


    /**
     * 更新PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中记录的状态
     * @param executeInterface
     */
	public void updateExecuteInterfaceStatus(ActivityChannelExecute executeInterface);


    /**
     * 工单表删除数据时更新PLT_ORDER_TABLES_USING_INFO表的的使用信息
     * @param usingInfo  含有删除的数量及工单表名称
     */
	public void updateOrderTableUsingInfo(OrderTableUsingInfo usingInfo);


    /**
     * 查询 PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE_HIS，查询里面是否有指定的渠道的批次，如果有，说明该渠道的该批次已执行完毕
     * @param channelExecute
     * @return
     */
	public int getChannelFinishedCount(ActivityChannelExecute channelExecute);


    /**
     * 根据活动Id查询工单生成的步骤的名称
     * @param param
     * @return
     */
	public List getOrderGenerateStepName(RequestParamMap param);


	/**
	 * 更新工单表的使用信息： plt_order_tables_using_info
	 * 遍历所有的表，查询表中实际的工单数，把改数更新到plt_order_tables_using_info对应的行中
	 * @param tenantId  租户Id
	 */
	public void synOrderTableUsingCount(String tenantId);

	HashMap<String, Object> updateActivitySuccess(RequestParamMap req);
}
