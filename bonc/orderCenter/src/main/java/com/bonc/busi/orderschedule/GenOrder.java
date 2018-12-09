package com.bonc.busi.orderschedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.orderschedule.service.impl.OrderServiceImpl;
import com.bonc.busi.task.base.AsynUserLabel;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.utils.IContants;


@Component
public class GenOrder {
	
	@Autowired
	private BusiTools  AsynDataIns;
	@Autowired
	private OrderService order_service;
	private final static Logger log= LoggerFactory.getLogger(GenOrder.class);
	//@Scheduled(fixedDelay = 5 * 60 * 1000)

	//@Scheduled(fixedRate = 5 * 60 * 1000)
	//@Scheduled(cron = "0 0/2 * * * ?")
    public void generateOrder() {
		List<Map<String, Object>> tenantIdsList = AsynDataIns.getValidTenantInfo();
		for(Map<String, Object> map : tenantIdsList){
			System.out.println("[OrderCenter] OrderCenter running...");
			long start = System.currentTimeMillis();
			String tenantId = (String)map.get("TENANT_ID");
			
			String ac_url = AsynDataIns.getValueFromGlobal("ACTIVITY_INFO");
			if (ac_url.equals("") || ac_url == null) {
				System.out.println("[OrderCenter] Get activity_url error..................!!!");
				continue;
			}
			
			//filter http retry request,and handel many times run
			String RunningStatus = AsynDataIns.getValueFromGlobal("ORDER_RUNNING");
			System.out.println("Running Status==" + RunningStatus);
			if(RunningStatus == null ){
				AsynDataIns.setValueToGlobal("ORDER_RUNNING", "1");
				
			}
			else {
				if(RunningStatus.equals("1")) {
					System.out.println("[OrderCenter] Activity is running...,waitting finished.");
					continue;
				}
				//set running status
				AsynDataIns.setValueToGlobal("ORDER_RUNNING", "1");
			}
			
			order_service.doActivity(ac_url,tenantId);
			//set activity finished status
			AsynDataIns.setValueToGlobal("ORDER_RUNNING", "0");
			
			long end = System.currentTimeMillis();
			System.out.println("[OrderCenter] finished，time-consuming:" + (end-start)/1000.0+"s" );
			
		}//end for tenant id
		
    }
	//@Scheduled(cron = "0 30 21 * * ?")
    public void cleanOrder() {
		List<Map<String, Object>> tenantIdsList = AsynDataIns.getValidTenantInfo();
		for(Map<String, Object> map : tenantIdsList){
			log.info("[OrderCenter] cleanOrder running...");
			long start = System.currentTimeMillis();
			String tenantId = (String)map.get("TENANT_ID");
			List<Integer> activitySeqIdList= order_service.getInvalidActivitySeqId(tenantId);
			for (Integer act_seq_id : activitySeqIdList) {
				log.info("[OrderCenter] cleanOrder begin,activity_seq_id:" + act_seq_id);
				PltActivityInfo activity = new PltActivityInfo();
				activity.setREC_ID(act_seq_id);
				activity.setTENANT_ID(tenantId);
				//update invalid order records
				order_service.updateInvalidOrderRecords(activity);
				//insert to his table
				order_service.moveInvalidOrderRecords(activity);
				//delete invalid order records
				order_service.deleteInvalidOrderRecords(activity);
				//update activity info status
				order_service.updateActvityInfoInvalid(activity);
				log.info("[OrderCenter] cleanOrder end,activity_seq_id:" + act_seq_id);
			}
			
			long end = System.currentTimeMillis();
			log.info("[OrderCenter] clean finished，time-consuming:"+ (end-start)/1000.0+"s");
			
			
		}//end for tenant id
		
    }
}
