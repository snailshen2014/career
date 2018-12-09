package com.bonc.task;

import java.util.Properties;

import kafka.consumer.ConsumerConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.service.SceneService;
import com.bonc.busi.task.service.impl.SceneServiceImpl;

@Component
@EnableScheduling
public class SceneTask {
	private static final Logger log = Logger.getLogger(SceneServiceImpl.class);

	@Autowired
	private BusiTools  AsynDataIns;

	@Bean
	private ConsumerConfig getKafkaCfg(){
		Properties props = new Properties();
		props.put("zookeeper.connect", AsynDataIns.getValueFromGlobal("KAFKA_ZKP_CONNECT"));
		props.put("group.id", AsynDataIns.getValueFromGlobal("KAFKA_GROUP_ID"));
		props.put("zookeeper.session.timeout.ms", AsynDataIns.getValueFromGlobal("KAFKA_ZKP_SESSON_TIME"));
		props.put("zookeeper.sync.time.ms", AsynDataIns.getValueFromGlobal("KAFKA_ZKP_SYNC_TIME"));
		props.put("auto.commit.interval.ms", AsynDataIns.getValueFromGlobal("KAFKA_INTERV_TIME"));
		props.put("auto.offset.reset", AsynDataIns.getValueFromGlobal("KAFKA_OFFSET_RESET"));
		props.put("serializer.class", AsynDataIns.getValueFromGlobal("KAFKA_SERIALIZER_CLASS"));
		//测试
//		props.put("zookeeper.connect","172.16.11.167:2191,172.16.11.167:2192,172.16.11.167:2193");
//		props.put("group.id", "test2");
//		props.put("auto.commit.interval.ms", "1000");
//		props.put("zookeeper.session.timeout.ms","10000");
//		props.put("zookeeper.sync.time.ms", "200");
//		props.put("auto.offset.reset", "smallest");
//		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("consumer.timeout.ms", "20000");
		return new ConsumerConfig(props);
	}
	
	@Autowired
	private SceneService sceneService;
	
	//定时检查处理错误短信数据
	@Scheduled(cron = "0 0 * * * ?")
	public void consumer(){
		try {
			sceneService.dealfailsms();
		} catch (Exception e) {
			log.error("dealfailsms --- error!");
		}
	}
		
}
