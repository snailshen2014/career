package com.bonc.busi.orderschedule.service;

import com.bonc.busi.activity.SuccessStandardPo;

/**
 * 工单成功过滤、成功检查处理类
 * @author Administrator
 *
 */
public interface OrderSuccessService {
	
	/*
	 * 由于河南活动工单量太大的话，原来的成功过滤比较慢,改写成orderFilterSucessV2实现
	 */
	public		boolean			orderFilterSucessV2(int  activitySeqId,SuccessStandardPo sucessCon,String tenantId);

	/**
	 * 同步工单表数据到行云PLT_ORDER_INFO_FOR_FILTER表中
	 * @param activitySeqId
	 * @param tenantId
	 * @param tableName
	 * @return
	 */
	boolean  uploadOrderToXcloud(int activitySeqId,String tenantId,String tableName);
	
	/**
	 * 删除行云PLT_ORDER_INFO_FOR_FILTER表中的工单
	 * @param activitySeqId
	 * @param tenantId
	 * @param tableName
	 * @return
	 */
	boolean  deleteOderInXcloud();

}
