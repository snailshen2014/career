//package com.bonc.common.thread;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Component;
//
//import com.bonc.properties.ThreadPoolProperties;
//
//@Component
//public class ThreadPool {
//
//	@Autowired
//	private ThreadPoolProperties threadPoolProperties;
//	
//	@Bean
//    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
//        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();   
//        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
//        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
//        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
//        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
//        return threadPoolTaskExecutor;
//    }
//}
