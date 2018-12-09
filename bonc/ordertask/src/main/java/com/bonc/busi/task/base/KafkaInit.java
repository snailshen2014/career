package com.bonc.busi.task.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.task.instance.SceneGenOrder;

public class KafkaInit implements Runnable{
	
	private final static Logger log= LoggerFactory.getLogger(UpdateOrderUserInfo.class);
	
	@Override
	public void run() {
		try {
			Thread.sleep(60*1000l);
			log.info("begin start kafka task!");
			SceneGenOrder sceneGenOrder = new SceneGenOrder();
	 		sceneGenOrder.setNoDataQuitFlag(false);
	 		sceneGenOrder.setMaxQueueSize(10000);
	 		ParallelManage ParallelManageIns = new ParallelManage(sceneGenOrder,2);
	 		ParallelManageIns.execute();
	 		log.info("load kafka success!");
		} catch (Exception e) {
			log.info("load kafka error!"+e.getMessage());
		}
		
	}

}
