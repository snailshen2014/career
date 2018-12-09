package com.bonc.busi.service;

import com.bonc.common.base.JsonResult;

public interface OrderFailureFunction {
	/*
	 * 工单失效
	 */
	public		JsonResult		orderFailureByStatus(String tenant_id);
}
