package com.bonc.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.statistic.service.RemoveUserService;


@Component
@Configurable
@EnableScheduling
public class RemoveUserTask {
	@Autowired
	private RemoveUserService removeUserService;
	
	@Scheduled(cron = "0 0 * * * ?")
	public void removeOrders() {
		removeUserService.initload();
	}
}
