package com.bonc.busi.outer.service;

/**
 * 短信工单服务
 * @author Administrator
 *
 */
public interface SmsOrderService {
	
	/**
	 * 对接发短信那边： 发完短信后，短信那边把短信工单移入到按月建立hisTableName表中，这个service完成
	 * 把hisTableName中的历史短信工单移入到按手机尾号拆分的短信历史表中
	 * @param activityId
	 * @param activitySeqId
	 * @param tenantId
	 * @param channelId
	 * @param hisTableName
	 */
	public void moveSmsOrderToHis(String activityId,int activitySeqId,String tenantId,
			                          String channelId,String hisTableName);

}
