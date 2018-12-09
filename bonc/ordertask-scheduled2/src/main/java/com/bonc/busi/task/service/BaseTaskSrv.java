package com.bonc.busi.task.service;

import com.bonc.busi.activity.SuccessStandardPo;

public interface BaseTaskSrv {
	/*
	 * 工单生成后过滤已经成功的工单
	 */
	public		boolean			orderFilterSucess(int  activitySeqId,SuccessStandardPo sucessCon,String tenantId);
	
	/*
	 * 工单成功判断
	 */
	public		void			orderCheck();

	/*
	 * 对工单做成功判断（不用了）
	 */
	public		void		checkOrderSucess();
	/*
	 * 同步行云上的用户标签数据 
	 */
	public		void		asynUserLabel();
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
	/*
	 * 更新工单上的用户标签数据
	 */
	public		void			updateOrderUserLabel();

	/*
	 * 更新工单表的userid
	 */
	public void updateUserId();

}
