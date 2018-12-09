package com.bonc.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
	
//	@Scheduled(cron = "0 0 6 * * ?")
	public void statisticActivity(){
//		statisticService.statisticActivity();
	}
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void statisticBench(){
		statisticService.backStatisitic();
	}
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void updateLimitNum(){
		alertWinService.updateLimitNum();
	}
	
	@Scheduled(cron = "0 10 0 * * ?")
	public void deleteSame(){
		codeService.deleteSame();
	}
	
//	@Scheduled(cron = "0 0 7-9 * * ?")
	public void vaildStatistic(){
//		statisticService.vaildStatistic();
	}

	//统计 接触成功次数，然后更新PLT_ORDER_STATISTIC_SEND 表    1/5min
	@Scheduled(cron = "0 0/5 * * * ?")
	public void statisticConNum(){
		statisticService.statisticConNum();
	}

}
