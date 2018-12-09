package com.bonc.busi.service;


import com.bonc.common.base.JsonResult;

public interface ServiceControl {
	
	/*
	 * 用户资料同步启动
	 */
	public		boolean			userlabelAsyn(String tenant_id);
	/*
	 * 工单成功检查（事后检查）
	 */
	public		JsonResult		ordersucessCheck(String tenant_id);
	/*
	 * 工单成功检查（事前检查）
	 */
	public		JsonResult		ordersucessFilter(String TenantId,int ActivitySeqId,String ActivityId);
	/*
	 * 工单用户资料更新
	 */
	public		JsonResult		orderUserLabelUpdate(String TenantId,short updateType);
	
	/*
	 * 黑白名单数据同步
	 */	
	public      boolean         blackandwhiteAsyn(String TenantId);
	
	/*
	 * 场景营销拉去下行kafka入工单
	 */
	public      void         GenSenceOrder(String TenantId);
	/*
	 * 场景营销拉去下行kafka入工单
	 */
	public      void         dealfailsms(String TenantId);
	
	/*
	 * 弹窗渠道加手机号索引
	 */
	public	boolean	addPhoneIndex(String TenantId,String ChannelId,String OrderTableName,int ActivitySeqId, String ActivityId);
	
	/*
	 * 根据插入记录表更新工单成功数据
	 */
	public JsonResult productSaveForSuccess(String tenantId);
	
}
