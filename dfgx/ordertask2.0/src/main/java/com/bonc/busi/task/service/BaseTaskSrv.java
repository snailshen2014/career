package com.bonc.busi.task.service;

import com.bonc.busi.activity.SuccessStandardPo;

public interface BaseTaskSrv {
	/*
	 * 工单生成后过滤已经成功的工单
	 */
	public		boolean			orderFilterSucess(int  activitySeqId,SuccessStandardPo sucessCon,String tenantId);
	/*
	 * 测试数据库联接
	 */
	public		int		testDbConnection( String dbtype,String timeDur);
	/*
	 * 得到工单数量 
	 */
	public		int		getOrderNum();
	/*
	 * 测试MYCAT
	 */
	public		int		testMycat();


}
