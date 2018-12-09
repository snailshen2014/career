package com.bonc.busi.sendData.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.activityInfo.po.ActivityInfo;

/**
 * @ClassName: ActivityInfoMapper
 * @Description: TODO
 * @author: sky
 */
public interface SendDataMapper {
	
	/**
	 * findOrderOneSendDataByActivity
	 * 根据活动信息  查询一级渠道工单 下发数据
	 * @param tenantId
	 * @param activitySeqId
	 * @param dealMonth
	 * @return
	 */
	public List<Map> findOrderOneSendDataByActivity(@Param("tenantId") String tenantId,
			@Param("activitySeqId") int activitySeqId,@Param("dealMonth") String dealMonth,
			@Param("startIndex") int startIndex,@Param("pageSize") int pageSize,
			@Param("partitionFlag") int partitionFlag);
	
	/**
	 * deleteSendDataByActivity
	 * 删除下发数据
	 * @param tenantId
	 * @param activitySeqId
	 * @param dealMonth
	 * @param status
	 */
	public Integer deleteSendDataByActivity(@Param("tenantId") String tenantId,
			@Param("activitySeqId") int activitySeqId,@Param("dealMonth") String dealMonth);
	
	/**
	 * insertSendDataHis
	 * 下发数据移入历史表   7天前
	 * @param inputTime
	 * @param tenantId
	 * @return
	 */
	public Integer insertSendDataHis(@Param("inputTime")String inputTime,@Param("tenantId")String tenantId);
	
	/**
	 * deleteSendData
	 * 删除下发数据   7天前
	 * @param tenantId
	 * @return
	 */
	public Integer deleteSendData(@Param("tenantId")String tenantId);
}

