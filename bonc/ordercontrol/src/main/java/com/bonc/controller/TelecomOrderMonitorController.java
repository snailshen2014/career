package com.bonc.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.sys.service.TelecomOrderMonitorService;
import com.bonc.common.base.JsonResult;

/**
 * 电信提的工单生成监控的需求：想要知道正在跑哪个活动？ 哪些活动正在等待跑？以及哪些活动以及跑完了(跑完的活动需要给出最近跑的时间以及工单数)
 * @author Administrator
 */
@Controller
@RequestMapping("/ordergenmonitor")
public class TelecomOrderMonitorController {
	
	@Autowired
	TelecomOrderMonitorService service;
	
	/**
	 * 
	 * @param tenantId 租户Id
	 * @return
	 */
	@RequestMapping("/{tenantId}")
	@ResponseBody
	public String monitor(@PathVariable("tenantId") String tenantId){
		JsonResult jsonResult = new JsonResult();
		Map<String, Object> dataMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(tenantId)){
			jsonResult.setCode("1");
			jsonResult.setMessage("租户Id不能为空");
			return JSON.toJSONString(jsonResult);
		}
		try{
		dataMap = service.queryOrderStateById(tenantId);
		}catch(Exception ex){
			jsonResult.setMessage(ex.getMessage());
			jsonResult.setCode("1");
			return JSON.toJSONString(jsonResult);
		}
		jsonResult.setData(dataMap);
		jsonResult.setCode("0");
		return JSON.toJSONString(jsonResult);
	}

}
