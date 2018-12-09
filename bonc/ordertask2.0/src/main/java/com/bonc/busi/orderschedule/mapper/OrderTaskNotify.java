package com.bonc.busi.orderschedule.mapper;

import org.apache.ibatis.annotations.Insert;

import com.bonc.busi.orderschedule.bo.OrderTablesAssignRecord;
import com.bonc.busi.orderschedule.bo.OrderTaskNotifyInterface;

/**
 *  order task managerment
 * @author yanjunshen
 * @Date 2017-05-16 14:17
 */
public interface OrderTaskNotify {
	/**
	 * send a task to other module
	 * @param
	 * @return
	 */
	@Insert("insert into PLT_ORDER_TASK_NOTIFY_INTERFACE (ACTIVITY_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,STATUS,"
			+ "ORDER_TABLE_NAME,TASK_GEN_DATE,TASK_PRIORITY,TENANT_ID,TASK_TYPE)"
			+ " values (#{ACTIVITY_ID},#{ACTIVITY_SEQ_ID},#{CHANNEL_ID},#{STATUS},#{ORDER_TABLE_NAME},"
			+ "#{TASK_GEN_DATE},#{TASK_PRIORITY},#{TENANT_ID},#{TASK_TYPE}) ")
	public void recordActivityAssignedTableInfo(OrderTaskNotifyInterface record);
}
