package com.bonc.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.task.base.BusiTools;

@RestController
@RequestMapping("/testOrder")
public class TestOrder {
	
	@Autowired
	private OrderService order_service;
	@Autowired
	private FilterOrderService filterOrderService;
	
	@Autowired
	private BusiTools  AsynDataIns;
	private final static Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	@RequestMapping(value="/ordertest")
	public void  test(int activitySeqId){
		/*
		 * http://clyxys.yz.local:8080/activityInter/activity/info?activityId=100000&tenantId=uni076
		http://clyxys.yz.local:8080/activityInter/activity/activityDoc
		http://clyxys.yz.local:8080/activityInter/activity/tempActIds?tenantId=uni076
		 */
	//	System.out.println("OrderCenter running...");
		//String ac_url = "http://clyxys.bonc.yz/activityInter/activity/tempActIds";
		//String ac_url = "http://clyxys.yz.local:8080/activityInter/activity/tempActIds";
		//String ac_url = "http://activityinter:8080/activityInter/activity/tempActIds";
	//	String ac_url = AsynDataIns.getValueFromGlobal("ACTIVITY_INFO");
	//	order_service.doActivity(ac_url,"uni076");
        //String id = order_service.isBlackWhiteUser("153", "01");
        //String id2 = order_service.isBlackWhiteUser("153", "02");
        
        //order_service.TestXcloud();
       // order_service.TestOracle();
		//return JsonResultIns;
		
		long start = System.currentTimeMillis();
		List<String> channelList = new ArrayList<String>();
		channelList.add("5");
		filterOrderService.filterRuleOrder(activitySeqId, "uni076", "5");
		long end = System.currentTimeMillis();
		log.info("[OrderCenter] filterRuleOrder  resetactivity finished，time-consuming:"+ (end-start)/1000.0+"s");
	}
	
	@RequestMapping(value="/cleaninvalidorder")
	public void  cleaninvalidorder(){
		
		
		
	}
	
	@RequestMapping(value="/resetactivity")
	public void  resetActivity(@RequestParam(value="id") String id){
		List<Map<String, Object>> tenantIdsList = AsynDataIns.getValidTenantInfo();
		for(Map<String, Object> map : tenantIdsList){
			log.info("[OrderCenter] resetactivity running...");
			long start = System.currentTimeMillis();
			String tenantId = (String)map.get("TENANT_ID");
			PltActivityInfo activity = new PltActivityInfo();
			activity.setACTIVITY_ID(id);;
			activity.setTENANT_ID(tenantId);
			Integer actvity_seq_id = order_service.getLastActivityRecId(activity);
			
			long end = System.currentTimeMillis();
			log.info("[OrderCenter] resetactivity finished，time-consuming:"+ (end-start)/1000.0+"s");
			
			
		}//end for tenant id
		
	}
	
}
