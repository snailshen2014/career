package com.bonc.busi.task.base;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public    class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
@Override
public void onApplicationEvent(ContextRefreshedEvent event) {
//	if(Global.init() == false){
//			System.err.println("初始化失败 !!!");
//			System.exit(0);
//	}
//SourceRepository sourceRepository = event.getApplicationContext().getBean(SourceRepository.class);
//Source je =new Source("justice_eternal吧","http://tieba.baidu.com/f?kw=justice_eternal");
//sourceRepository.save(je);
}
}