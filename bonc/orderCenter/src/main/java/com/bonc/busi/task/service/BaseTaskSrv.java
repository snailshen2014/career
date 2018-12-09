package com.bonc.busi.task.service;

public interface BaseTaskSrv {

	public		void			orderCheck();

	/*
	 * 对工单做成功判断
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

}
