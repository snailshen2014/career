/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: ScheduleConfig.java
 * @Prject: channelCenter
 * @Package: com.bonc.task
 * @Description: ScheduleConfig
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月4日 上午10:45:18
 * @version: V1.0  
 */

package com.bonc.task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @ClassName: ScheduleConfig
 * @Description: 定时器配置(实现多个task并行执行)
 * @author: LiJinfeng
 * @date: 2017年1月4日 上午10:45:18
 */
@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {

	/* (non Javadoc)
	 * @Title: configureTasks
	 * @Description: ScheduleConfig
	 * @param arg0
	 * @see org.springframework.scheduling.annotation.SchedulingConfigurer#configureTasks(org.springframework.scheduling.config.ScheduledTaskRegistrar)
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		
		taskRegistrar.setScheduler(taskExecutor());
		
	}
 
    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(4);
    }

}
