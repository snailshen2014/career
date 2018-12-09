package com.bonc.busi.orderschedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import java.sql.SQLException;

import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.orderschedule.service.impl.OrderServiceImpl;
import com.bonc.busi.task.base.AsynUserLabel;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.OverdueOrderMoveHis;
import com.bonc.utils.IContants;
import com.bonc.utils.PhoneUtil;


@Component
public class GenOrder {
	
	@Autowired
	private BusiTools  AsynDataIns;
	@Autowired
	private OrderService order_service;
	@Autowired
	private OverdueOrderMoveHis OverdueOrderMoveHisIns;
	
	
	
	private final static Logger log= LoggerFactory.getLogger(GenOrder.class);
	//@Scheduled(fixedDelay = 5 * 60 * 1000)

	//@Scheduled(fixedRate = 5 * 60 * 1000)
	@Scheduled(cron = "0 0/7 * * * ?")
    public void generateOrder() {
		List<Map<String, Object>> tenantIdsList = AsynDataIns.getValidTenantInfo();
		for(Map<String, Object> map : tenantIdsList){
			
			long start = System.currentTimeMillis();
			String tenantId = (String)map.get("TENANT_ID");
			log.info("[OrderCenter] OrderCenter running...,tenantId:" +  tenantId);
			String ac_url = AsynDataIns.getValueFromGlobal("ACTIVITY_INFO");
			if (ac_url == null || ac_url.equals("") ) {
				log.error("[OrderCenter] Get activity_url error...,tenantId:" + tenantId);
				continue;
			}
			
			//filter http retry request,and handel many times run
			String RunningStatus = AsynDataIns.getValueFromGlobal("ORDER_RUNNING");
			log.info("Running Status==" + RunningStatus);
			if(RunningStatus == null ){
				AsynDataIns.setValueToGlobal("ORDER_RUNNING", "1");
				
			}
			else {
				if(RunningStatus.equals("1")) {
					log.info("[OrderCenter] Activity is running...,waitting finished.");
					//get beforeHour time
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.HOUR, -1);
				    Date beforeHour = calendar.getTime();
				    
				    //get last log time
				    String lastRunningTime = order_service.getLastLogTime();
				    if (lastRunningTime == null)
				    	continue;
				    Date lastRunningDate = null;
				    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    try {
				    	lastRunningDate = dateFormat.parse(lastRunningTime);
						log.info("[OrderCenter] last running time:" + dateFormat.format(lastRunningDate));
						log.info("[OrderCenter] beforeHour time:" + dateFormat.format(beforeHour));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				    if(beforeHour.after(lastRunningDate)) {
				    	log.error("[OrderCenter] one hour log no flush ,exists problem,running flag update to 0.");
				    	AsynDataIns.setValueToGlobal("ORDER_RUNNING", "0");
				    }
					continue;
				}
				//set running status
				AsynDataIns.setValueToGlobal("ORDER_RUNNING", "1");
			}
			//catch sql exception
			try {
				order_service.doActivity(ac_url,tenantId);
			
			}catch(Exception e){
			  
				if(e.getCause() instanceof  SQLException){
					log.error("[OrderCentr] sql error happend:{}",e.getMessage());
					throw e;
				}else{
					 e.printStackTrace();
				}
			
			}finally{
				log.info("[OrderCenter] no catch  exception,finnally update running status.");
				AsynDataIns.setValueToGlobal("ORDER_RUNNING", "0");
			}
			//set activity finished status
//			AsynDataIns.setValueToGlobal("ORDER_RUNNING", "0");
			
			long end = System.currentTimeMillis();
			log.info("[OrderCenter] finished，time-consuming:" + (end-start)/1000.0+"s");
		}//end for tenant id
		
    }
	
	@Scheduled(cron = "0 30 20 * * ?")
    public void cleanOrder() {
		List<Map<String, Object>> tenantIdsList = AsynDataIns.getValidTenantInfo();
		for(Map<String, Object> map : tenantIdsList){		
			log.info("[OrderCenter] cleanOrder running...");
			String tenantId = (String)map.get("TENANT_ID");
			long start = System.currentTimeMillis();
			
            // --- 获取当前时间月份  （移入当前时间月份的历史表）---
			String month = PhoneUtil.routeMonth();
			
			// --- 获取失效批次 ---
			List<PltActivityInfo> actList= order_service.getInvalidActivitySeqId(tenantId);
			for (PltActivityInfo act : actList) {
				int activitySeqId =  act.getREC_ID();
				log.info("[OrderCenter] cleanOrder begin,activity_seq_id:" + activitySeqId + ",month:" + month);
			
				//批量移历史操作
				OverdueOrderMoveHisIns.expireActivityHandle(tenantId,activitySeqId,month);
				
				//更新活动状态为2
				act.setTENANT_ID(tenantId);
				order_service.updateActvityInfoInvalid(act);
				log.info("[OrderCenter] cleanOrder end,activity_seq_id:" + activitySeqId);
			}
			
			long end = System.currentTimeMillis();
			log.info("[OrderCenter] clean finished，time-consuming:"+ (end-start)/1000.0+"s");
			
			
		}//end for tenant id
		
    }
}
