package com.bonc.busi.task.service;
/**
 * 由于河南工单量较大,orderCheck方式效率慢，现修改为在行云上跑成功检查，然后根据成功检查结果更新工单表
 * 方法：依次把每个需要跑成功检查的表同步到行云表上，然后在行云上跑成功检查，执行完成功检查sql后，把结果从
 * FTP上下载成功检查数据文件到本地，然后数据文件更新工单表
 */
public interface OrderCheckService {

	/**
	 * 同步工单表数据到行云PLT_ORDER_INFO_FOR_FILTER表中
	 * @param tenantId
	 * @param tableName
	 * @return
	 */
	boolean  uploadOrderToXcloud(String tenantId,String tableName);
	
	/**
	 * 删除行云PLT_ORDER_INFO_FOR_FILTER表中的工单
	 * @param activitySeqId
	 * @param tenantId
	 * @param tableName
	 * @return
	 */
	boolean  deleteOderInXcloud();
	
	void orderCheck();
}
