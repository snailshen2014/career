package com.bonc.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.varsion.VersionService;

@Component
@EnableScheduling
public class VersionTask {

	@Autowired
	private VersionService version;
	
	/**
	 * 
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	public void scan(){
		version.scan();
	}
}
