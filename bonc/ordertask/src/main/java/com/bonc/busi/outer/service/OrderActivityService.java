package com.bonc.busi.outer.service;



import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.common.base.JsonResult;

public interface OrderActivityService {
	
	/*
	 * 更新活动状态
	 */
	public  JsonResult setActivityStatus(ActivityStatus reqdata);


}
