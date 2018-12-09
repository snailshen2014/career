package com.bonc.busi.sys.service;

import com.bonc.common.base.JsonResult;

public interface OrderFailureFunction {
	public		JsonResult		startOrderFailureByStatus(String TENANT_ID,char flag);
}
