package com.bonc.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.busi.monitor.MonitorService;
import com.bonc.common.utils.ChannelEnum;

@Controller
@RequestMapping("/send")
public class ChannelController {
	
    @Resource
	MonitorService monitorService;
  
    @RequestMapping("/oneLevelChannel")
    @ResponseBody
    public String sendToOneLeveChannel( String activityInfo){
    	String result =monitorService.sendToChannel(activityInfo,ChannelEnum.YJQD.getCode());
    	return result;
    }
    
    @RequestMapping("/health")
    public String  checkHealth(){
   
    	return "{\"status\":\"UP\"}";
    }
    
    
}
