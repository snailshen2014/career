package com.bonc.busi.orderschedule;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.orderschedule.bo.OrderTablesAssignRecord;
import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.orderschedule.log.LogToDb;
import com.bonc.busi.orderschedule.mapping.SqlMapping;
import com.bonc.busi.orderschedule.mapping.ConstantValue;
import com.bonc.busi.orderschedule.routing.OrderTableManager;

@Component
public class TestSyncData {
	
//    @Scheduled(cron = "0 0/1 * * * ?")
    public void testTableRouting() {
    	/*
    	//test log
    	HashMap<Integer,String> params = new HashMap<Integer,String>();
		params.put(1, "uuuuuuu");
		params.put(3, "333");
		params.put(4, "1111333");
		params.put(5, "10003000");
		LogToDb.writeLog(121121, "ORDER_GEN", "GET_USER_GROUP_INFO_BEGIN", params);
		
		System.out.println("Begin get table name.");
		
		String activityId = "1111";
		String tenantId = "uni076";
		String channelId = "7";
		//1
		int busiType = 1;
		int rows = 50000;
		String name = OrderTableManager.getAssignedTable(activityId, tenantId, channelId, busiType, rows);
		System.out.println("Get table name=" + name);
		//test 
		OrderTablesAssignRecord rec = new OrderTablesAssignRecord();
		rec.setACTIVITY_ID("1111");
		rec.setACTIVITY_SEQ_ID(33333);
		rec.setASSIGN_DATE(new Date());
		rec.setCHANNEL_ID("7");
		rec.setTABLE_NAME(name);
		rec.setTENANT_ID("uni076");
		rec.setBUSI_TYPE(1);
		OrderTableManager.recordActivityUsingTableInfo(rec);
		
		//test setTableUsingInfo
		
		OrderTableManager.updateTableUsingInfo("PLT_ORDER_INFO_1", 1, 0, 2000000, "uni076", 1);
	*/
    	SqlMapping sqlMap = new SqlMapping("uni076");
    	//need set value handel for counting elements
    	//columns
    	ConstantValue value = new ConstantValue();
    	value.setValue(1);
    	//ACTIVITY_SEQ_ID
    	sqlMap.setElementValue(1, value, 0);
    	//CHANNEL_ID
    	sqlMap.setElementValue(2, value,0);
    	//ORDER_STATUS
    	sqlMap.setElementValue(19, value,0);
    	//CHANNEL_STATUS
    	sqlMap.setElementValue(20, value,0);
    	//BEGIN_DATE
    	ConstantValue value2 = new ConstantValue();
    	value2.setValue(new Date());
    	sqlMap.setElementValue(24, value2,0);
    	//END_DATE
    	sqlMap.setElementValue(25, value2,0);
    	
    	//table
    	//${AssignResult}
    	ConstantValue value3 = new ConstantValue();
    	value3.setValue("temp_20170525");
    	sqlMap.setElementValue(2, value3,1);
    	
    	//conditions
    	ConstantValue value4 = new ConstantValue();
    	value4.setValue("20170524");
    	sqlMap.setElementValue(3, value4,2);
    	
    	//5 ,6 need judge by business feature
    	ConstantValue value5 = new ConstantValue();
    	value5.setValue("orgRange");
    	sqlMap.setElementValue(4, value5,2);
    	
    	ConstantValue value6 = new ConstantValue();
    	value6.setValue("1 = 1 ");
    	sqlMap.setElementValue(5, value6,2);
    	sqlMap.closeElement(5, 2);
    	
    	System.out.println(sqlMap.toColumns());
    	System.out.println(sqlMap.toSelect());
	}	
}

