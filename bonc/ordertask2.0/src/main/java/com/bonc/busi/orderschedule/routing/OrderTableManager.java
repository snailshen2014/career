package com.bonc.busi.orderschedule.routing;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bonc.busi.orderschedule.bo.OrderTablesAssignRecord;
import com.bonc.busi.orderschedule.mapper.OrderTableRouting;
import com.bonc.busi.task.base.SpringUtil;
/**
 * handle exception when table assigning
 * @author yanjunshen
 *
 */
class TableManagerException extends Exception {
	public TableManagerException() {}
	public TableManagerException(String msg) {
		super(msg);
	}
}

/**
 * manager order table for assigning,resizing etc.
 * attendtion:this class no thread-safe
 * @author yanjunshen
 * @time 2017-05-05 16:14
 */

public class OrderTableManager {
	/*
	 * LOG handel
	 */
	private final static Logger LOG = LoggerFactory.getLogger(OrderTableManager.class);
	
	/*
	 * order table routing mapper 
	 */
	private  final static OrderTableRouting TABLEROUTING_MAPPER = (OrderTableRouting) SpringUtil.getApplicationContext().getBean("orderTableRouting");
	
	/**
	 *  out interface,get assigned table name
	 * @param activityId
	 * @param tenantId
	 * @param channelId
	 * @param busiType 0:get orders table,1:get order filtering tables,2:reserve orders using
	 * @param rows
	 * @param activity_seq_id
	 * @return assigned table name
	 */
	public static String getAssignedTable(String activityId,String tenantId,String channelId,int busiType,int rows,int actSeqId) {
		//According to the activity id get table before use
		String tname = TABLEROUTING_MAPPER.getActivityAssignedTable(activityId, channelId, busiType,tenantId);
		if(tname == null || tname.equals("")) {
			try {
				tname = getUsableTalbe(busiType,rows,tenantId);
			} catch (TableManagerException e) {
				e.printStackTrace();
				return "";
			}
			setTableUsingInfo(tname,0,busiType,rows,tenantId,UsingTableUpdateType.STATUS_CAPACITY);
		} else {
			//table using amount beyond limit
			if (!judgeTableIsUsable(tname, tenantId, busiType, rows)) {
				try {
					tname = getUsableTalbe(busiType,rows,tenantId);
				} catch (TableManagerException e) {
					e.printStackTrace();
					return "";
				}
			}
			setTableUsingInfo(tname,0,busiType,rows,tenantId,UsingTableUpdateType.ONLY_CAPACITY);
		}
		//insert using info
		OrderTablesAssignRecord rec = new OrderTablesAssignRecord();
		rec.setACTIVITY_ID(activityId);
		rec.setACTIVITY_SEQ_ID(actSeqId);
		rec.setASSIGN_DATE(new Date());
		rec.setCHANNEL_ID(channelId);
		rec.setTABLE_NAME(tname);
		rec.setTENANT_ID(tenantId);
		rec.setBUSI_TYPE(busiType);
		recordActivityUsingTableInfo(rec);
		return tname;
		
	}

	/**
	 * judge table is usable 
	 * 
	 * @param tableName
	 * @param tenantId
	 * @param busiType
	 * @param rows
	 * @return true,false
	 */
	private static boolean judgeTableIsUsable(String tableName, String tenantId, int busiType,int rows) {
		//get table current  capacity
		Integer currentCap = TABLEROUTING_MAPPER.getTableUsingNumber(tableName, busiType, tenantId);
		Integer maxCap = TABLEROUTING_MAPPER.getTableTotalLimit(tableName, busiType, tenantId);
		if (currentCap == null || maxCap == null) {
			LOG.error("No configure table using info.");
			return false;
		}
		return currentCap + rows < maxCap;
	}
	/**
	 * judge table is usable 
	 *
	 * @param busiType
	 * @param tenantId
	 * @return table name
	 */
	private static String getNewTable(int busiType,String tenantId) throws TableManagerException{
		//assign new table 
		String tname = "";
		
		Integer currentAssignedIndex = TABLEROUTING_MAPPER.getCurrentAssignedTableNumber(busiType, tenantId);
		if (currentAssignedIndex == null) {
			LOG.error("No config Table Routing.");
			throw new TableManagerException(Thread.currentThread().getStackTrace()[1].getMethodName() + " ,No configuration base data ,Table Routing");
		}
		Integer maxCapacity = TABLEROUTING_MAPPER.getMaxAssignedTableNumber(busiType, tenantId);
		if (currentAssignedIndex.compareTo(maxCapacity) < 0 ) {
			tname = TABLEROUTING_MAPPER.getRoutingTable(busiType,tenantId);
			currentAssignedIndex += 1;
			TABLEROUTING_MAPPER.setTableRoutingCurrentIndex(busiType, tenantId);
			return tname;
		} else {
			LOG.info("All Tables already assigned,so get max remain capacity table from old tables.");
			return "";
		}
		
	}

	/**
	 * get assigned table name
	 * 
	 * @param busiType
	 * @param rows
	 * @param tenantId
	 * @return  table name
	 */
	private static String getUsableTalbe(int busiType, int rows,String tenantId) throws TableManagerException{
		String tname = "";
		// assign new table
		try {
			tname = getNewTable(busiType, tenantId);
		} catch (TableManagerException e) {
			//no configuration base data
			throw new TableManagerException(e.getMessage());
		}
		
		if (!tname.equals("")) {
			if (!judgeTableIsUsable(tname, tenantId, busiType, rows)) {
				LOG.error("New table capacity too small,or can not configure table using info.");
				throw new TableManagerException("New table capacity too small,or can not configure table using info.");
			}
		} else {
			//no new table,all tables already assigned,get max remain capacity table form using table
			tname = TABLEROUTING_MAPPER.getMaxRemainCapacityTable(busiType, tenantId);
			if (!judgeTableIsUsable(tname, tenantId, busiType, rows)) {
				LOG.error("System no usable table,resize.");
				throw new TableManagerException("System no usable table,please resize manually.");
			}
		}
		return tname;
	}

	/**
	 * update table using info
	 * @param tname
	 * @param operType ,update CURRENT_AMOUNT,0 add,1 subtraction
	 * @param busiType
	 * @param rows
	 * @param tenantId
	 * @param updateType
	 * @return
	 */
	private static void setTableUsingInfo(String tname, int operType, int busiType, int rows, String tenantId,UsingTableUpdateType updateType) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("TYPE", operType);
		param.put("NUMBER", rows);
		param.put("TABLE_NAME", tname);
		param.put("BUSI_TYPE", busiType);
		param.put("TENANT_ID", tenantId);
		// update table using info
		if (UsingTableUpdateType.STATUS_CAPACITY == updateType) {//new table
			TABLEROUTING_MAPPER.setTableUsingStatus(1, tname, busiType, tenantId);
			TABLEROUTING_MAPPER.setTableCapacity(param);
		}
		else if (UsingTableUpdateType.ONLY_CAPACITY == updateType) {//old table
			TABLEROUTING_MAPPER.setTableCapacity(param);
		} else {
			LOG.error("setTableUsingInfo error udpate type.");
		}
	}
	/**
	 *  get assigned table name
	 * @param OrderTablesAssignRecord
	 * @return 
	 */
	private static void recordActivityUsingTableInfo(OrderTablesAssignRecord rec) {
		TABLEROUTING_MAPPER.recordActivityAssignedTableInfo(rec);
		
	}
	/**
	 * update table using info, outside interface
	 * @param tname
	 * @param operType ,update CURRENT_AMOUNT,0 add,1 subtraction
	 * @param busiType
	 * @param rows
	 * @param tenantId
	 * @param updateType 0:status & capacity;1:only capacity
	 * @return
	 */
	public static void updateTableUsingInfo(String tname, int operType, int busiType, int rows, String tenantId,int updateType) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("TYPE", operType);
		param.put("NUMBER", rows);
		param.put("TABLE_NAME", tname);
		param.put("BUSI_TYPE", busiType);
		param.put("TENANT_ID", tenantId);
		UsingTableUpdateType type ;
		if (0 == updateType)
			type = UsingTableUpdateType.STATUS_CAPACITY;
		else if (1 == updateType)
			type = UsingTableUpdateType.ONLY_CAPACITY;
		else {
			LOG.error("Error update type");
			return;
		}
		// update table using info
		if (UsingTableUpdateType.STATUS_CAPACITY == type) {//new table
			TABLEROUTING_MAPPER.setTableUsingStatus(1, tname, busiType, tenantId);
			TABLEROUTING_MAPPER.setTableCapacity(param);
		}
		else if (UsingTableUpdateType.ONLY_CAPACITY == type) {//old table
			TABLEROUTING_MAPPER.setTableCapacity(param);
		} else {
			LOG.error("setTableUsingInfo error udpate type.");
		}
	}
	
	/**
	 * delete order table assign record info
	 */
	public static void deleteOrderTableAssignRecord(String activityId,Integer actSeqId,String channelId,String tname,String tenantId,Integer busiType){
		        //delete using info
				OrderTablesAssignRecord rec = new OrderTablesAssignRecord();
				rec.setACTIVITY_ID(activityId);
				rec.setACTIVITY_SEQ_ID(actSeqId);
				rec.setCHANNEL_ID(channelId);
				rec.setTABLE_NAME(tname);
				rec.setTENANT_ID(tenantId);
				rec.setBUSI_TYPE(busiType);
				TABLEROUTING_MAPPER.deleteActivityAssignedTableInfo(rec);
	    }
}
/**
 * update table using
 * @author yanjunshen
 *
 */
 enum UsingTableUpdateType {
	STATUS_CAPACITY,ONLY_CAPACITY
 }
