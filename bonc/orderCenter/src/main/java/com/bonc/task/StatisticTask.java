package com.bonc.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.interfaces.service.AlertWinService;
import com.bonc.busi.statistic.service.StatisticService;

@Component
@Configurable
@EnableScheduling
public class StatisticTask {
	
	@Autowired
	private StatisticService statisticService;
	
	@Autowired
	private AlertWinService alertWinService;
	
	@Autowired
	private CodeService codeService;
	
//	@Scheduled(cron = "0 30 6 * * ?")
//	public void dxSent(){
//		statisticService.statisticActivity();
//	}
//		
//	@Scheduled(cron = "0 0/4 * * * ?")
//	public void statiscOrderDate(){
//		statisticService.orderDate();
//	}
	
	//@Scheduled(cron = "0 10 0 * * ?")
//	public void updateLimitNum(){
//		alertWinService.updateLimitNum();
//	}
	
	//@Scheduled(cron = "0 10 0 * * ?")
//	public void deleteSame(){
//		codeService.deleteSame();
//	}
}
