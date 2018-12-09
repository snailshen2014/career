/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: SyncCodeController.java
 * @Prject: channelCenter
 * @Package: com.bonc.controller
 * @Description: SyncCodeController
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年3月1日 上午9:23:24
 * @version: V1.0  
 */

package com.bonc.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.task.InitSyncCode;

/**
 * @ClassName: SyncCodeController
 * @Description: SyncCodeController
 * @author: LiJinfeng
 * @date: 2017年3月1日 上午9:23:24
 */

@RequestMapping("/synccode")
@Controller
public class SyncCodeController {

	@Autowired
	private InitSyncCode initSyncCode;
	
	private static Log log = LogFactory.getLog(SyncCodeController.class);
	
	@RequestMapping("/start")
	@ResponseBody
	public String syncCode(){
		
		try {
			initSyncCode.startSyncCode();
		} catch (Exception e) {
			log.error("occurred exception when synchronize codes");
			return "error";
		}
		return "success";
		
	}
	
}
