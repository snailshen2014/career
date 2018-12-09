package com.bonc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.send.service.SentService;

@RestController
@RequestMapping("/scheduled/")
public class ScheduledController {
	
	@Autowired
	private SentService dxService;
	
	@RequestMapping("channel")
	public String scheduledChannel(){
		return "index";
	}
	
	@RequestMapping("smsSend")
	public String smsSend(){
		dxService.sent();
		return "index";
	}
}
