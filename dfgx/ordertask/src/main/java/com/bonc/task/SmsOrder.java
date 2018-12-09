package com.bonc.task;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.bonc.busi.send.service.SentService;
/**
 * 短信发送和短信同步
 * @author llb
 *
 */
@Component
@Configurable
@EnableScheduling
public class SmsOrder {
	@Autowired
	private SentService dxService;
	
	/**
	 * 短信批量发送
	 */
//	@Scheduled(cron = "0/60 * * * * ?")
	public void dxSent(){
		dxService.sent();
	}
	
	/**
	 * 短信统计同步
	 */
//	@Scheduled(cron = "0/10 * * * * ?")
	public void synchSend(){
		dxService.synchSend();
	}
}
