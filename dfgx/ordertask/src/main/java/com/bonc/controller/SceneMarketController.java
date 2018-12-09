package com.bonc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ActivityProvPo;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.service.SceneService;
import com.bonc.common.base.JsonResult;

@RestController
@RequestMapping("/scenemarket")
@ConfigurationProperties(prefix = "kafka", ignoreUnknownFields = false)
public class SceneMarketController {
	private String topicName;
	
	
	public String getTopicName() {
		return topicName;
	}


	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	@Autowired
	private OrderActivityService OrderActivityServiceIns;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SceneService sceneService;
	
	@Autowired
	private BusiTools  AsynDataIns;
	
	private final static Logger log = LoggerFactory.getLogger(SceneMarketController.class);

	/*
	 * 设置活动状态
	 */
	// @RequestMapping(value="/setstatus")
	@RequestMapping(value = "/setstatus", method = RequestMethod.POST)
	public JsonResult setActivityStatus(@RequestBody ActivityStatus request) {
		log.warn("id=" + request.getActivityId());
		log.warn("status:" + request.getActivityStatus());
		// --- 调用service执行对应的功能 ---
		return OrderActivityServiceIns.setActivityStatus(request);
	}

	@RequestMapping(value = "/startActivity")
	public JsonResult startActivity(@RequestBody String request) {
		log.info("活动添加接口请求数据——>" + request);
		JsonResult JsonResultIns = new JsonResult();
		if (null == request) {
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("场景营销活动数据不能为空！——>>>>请求格式为：req=JSON串,注意特殊字符可能会URL转码开始生成");
			return JsonResultIns;
		}
		ActivityProvPo actjson = new ActivityProvPo();
		try {
			actjson = JSON.parseObject(request, ActivityProvPo.class);
		} catch (Exception e) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("错误的JSON请求数据！");
			return JsonResultIns;
		}
		//recordActivity为private，不可复用
		/*if (!(orderService.recordActivity(actjson))) {
			JsonResultIns.setCode("3");
			JsonResultIns.setMessage("该场景营销活动启动失败");
			return JsonResultIns;
		}*/
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("该场景营销活动启动成功");
		return JsonResultIns;
	}

	@RequestMapping(value = "/queryOrderInfo")
	public Object queryOrderInfo(@RequestBody String request) {
		log.info("工单查询查询接口请求参数——>" + request);
		JsonResult JsonResultIns = new JsonResult();
		if (null == request) {
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("场景营销请求参数不能为空！——>>>>请求格式为：req=JSON串,注意特殊字符可能会URL转码开始生成");
			return JsonResultIns;

		}
		OrderAndOrderSMS sceneOrder = new OrderAndOrderSMS();
		try {
			sceneOrder = JSON.parseObject(request, OrderAndOrderSMS.class);
		} catch (Exception e) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("错误的JSON请求参数！");
			return JsonResultIns;
		}
		// --参数判断，是否必传，是否选传--
		//--查询，Map-->json.toString(Map)--
		return JsonResultIns;
	}

//	@RequestMapping(value = "/addOrderInfo")
//	public void addOrderInfo() {
//		log.info("interface start scene get order!");
//		String isRun = AsynDataIns.getValueFromGlobal("SCENCE_CONSUMER_ISRUN");
//		if ("1".equals(isRun)) {
//			log.info("interface scene get order is running!");
//		}else{
//			try {
//				sceneService.consumer(topicName);
//				sceneService.dealfailsms();
//				log.info("interface get scene order success!");
//			} catch (Exception e) {
//				log.error("interface get scene order error!");
//			}finally{
//				log.info("interface get scene order end!");
//				AsynDataIns.setValueToGlobal("SCENCE_CONSUMER_ISRUN", "0");
//			}
//		}
//	}
	
	
//	@RequestMapping(value = "/testdealfailsms")
//	public void testdealfailsms() {
//		log.info("场景营销更新失败短信接口测试——");
//		sceneService.dealfailsms();
//	}
}
