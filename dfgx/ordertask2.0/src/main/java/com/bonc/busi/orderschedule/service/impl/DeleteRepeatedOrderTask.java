package com.bonc.busi.orderschedule.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.mapper.OrderMapper;

/**
 * 删除每个渠道重复工单的任务，返回该渠道下删除的重复的工单数
 * @author Administrator
 *
 */
public class DeleteRepeatedOrderTask implements Callable<Integer> {
	
	private final static Logger LOG = LoggerFactory.getLogger(DeleteRepeatedOrderTask.class);

	private String activityid; 
	private Integer activitySeqId;
	private String channelId;
	private String tenantId;
	private OrderMapper ordermapper;
	
	public DeleteRepeatedOrderTask(OrderMapper ordermapper,String channelId,String tenantId,String activityid,Integer activitySeqId){
		this.ordermapper = ordermapper;
		this.channelId = channelId;
		this.tenantId = tenantId;
		this.activityid = activityid;
		this.activitySeqId = activitySeqId;
		
	}

	/**
	 * 删除渠道下重复的工单，并且返回删除的工单的数量
	 */
	@Override
	public Integer call() throws Exception {
		List<Map<String, Object>> repeatedTelPhoneList = ordermapper.queryRepeatedPhonePerChannel(channelId,
				tenantId);
		int deleteCountPerChannel = 0;
		if (repeatedTelPhoneList != null && repeatedTelPhoneList.size() != 0) {
			LOG.info(">>>>>>>>进入每个渠道删除重复工单的方法，渠道:" + channelId + ",重复的手机号：" + repeatedTelPhoneList);
			// 针对每个手机号进行删除操作
			for (Map<String, Object> map : repeatedTelPhoneList) {
				String telPhone = (String) map.get("PHONE_NUMBER"); // 手机号
				if (telPhone != null && !telPhone.trim().equals("")) { //手机号不为空才去重
					Integer repeatedCount = ((Long) map.get("REPEAT_COUNT")).intValue();
					int deleteCountPerPhone = repeatedCount - 1; // 重复的数量
					LOG.info("手机号码：" + telPhone + "有" + deleteCountPerPhone + "条重复的工单");
					// 保留重复工单里的RecId最小的工单
					int minRecId = ordermapper.queryMinRecId(telPhone, channelId, tenantId);
					ordermapper.deleteRepatedOrder(minRecId, telPhone, channelId, tenantId);
					LOG.info("Delete: 删除了手机号码：" + telPhone + " " + deleteCountPerPhone + "条重复的工单");
					deleteCountPerChannel += deleteCountPerPhone;
				}
			}
			ActivityProcessLog log = new ActivityProcessLog();
			log.setACTIVITY_ID(activityid);
			log.setACTIVITY_SEQ_ID(activitySeqId);
			log.setTENANT_ID(tenantId);
			log.setCHANNEL_ID(channelId);
			log.setREPEAT_FILTER_AMOUNT(deleteCountPerChannel); // 更新黑名单过滤工单的数量
			ordermapper.UpdateRepeateFilterCountToActivityProcessLog(log);
			LOG.info("<<<<<<<<<退出每个渠道删除重复工单的方法，渠道:" + channelId + "共删除重复的工单数：" + deleteCountPerChannel); 
		}
		return deleteCountPerChannel;
	}

}
