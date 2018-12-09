package com.bonc.busi.outer.service;



import java.util.HashMap;
import java.util.List;

import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.common.base.JsonResult;

public interface OrderActivityService {
	
	/*
	 * 更新活动状态
	 */
	public  JsonResult setActivityStatus(ActivityStatus reqdata);
	
	public  List<HashMap<String, Object>> queryActivityOrderInfo(HashMap<String,Object> req); 


}
