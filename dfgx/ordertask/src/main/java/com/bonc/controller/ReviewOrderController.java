package com.bonc.controller;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.orderschedule.service.ReviewOrderService;
import com.bonc.busi.task.base.BusiTools;

@RestController
@RequestMapping("/reviewOrder")
public class ReviewOrderController {
	@Autowired
	private OrderService order_service;
	@Autowired
	private ReviewOrderService reviewOrderService;
	@Autowired
	private FilterOrderService filterOrderService;
	
	@Autowired
	private BusiTools  AsynDataIns;
	private final static Logger log = LoggerFactory.getLogger(ReviewOrderController.class);
	@RequestMapping(value="/orderReviwew")
	public void reviewOrder(@Param("activityId")String activityId ,@Param("orderDate")String orderDate,@Param("tenantId")String tenantId){
		log.info("activityId="+activityId+", orderDate="+orderDate+", tenantId="+tenantId);
		long start = System.currentTimeMillis();
		String RunningStatus = AsynDataIns.getValueFromGlobal("ORDER_RUNNING");
		if(RunningStatus == null ){
			AsynDataIns.setValueToGlobal("ORDER_RUNNING", "1");
			
		}else {
			if(RunningStatus.equals("1")) {
				log.info("[OrderCenter] Activity is running...,waitting finished.");
				return;
			}else if(RunningStatus.equals("0")){
				//set running status
				AsynDataIns.setValueToGlobal("ORDER_RUNNING", "1");
			}
		}
		try {
			reviewOrderService.reviewActivityOrder(activityId, orderDate, tenantId);
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
//		AsynDataIns.setValueToGlobal("ORDER_RUNNING", "0");
		
		long end = System.currentTimeMillis();
		log.info("[OrderCenter] finished，time-consuming:" + (end-start)/1000.0+"s");
	}
	
}
