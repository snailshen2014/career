package com.bonc.busi.task.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.task.instance.ScenePowerAnalyse;

public class ScenePowerInit implements Runnable {

	private final static Logger logger =  LoggerFactory.getLogger(ScenePowerInit.class);
	
	@Override
	public void run() {
		try{
			Thread.sleep(10*1000L);
			logger.error("begin ScenePowerTask ");
			ScenePowerAnalyse scenePowerAnalyse = new ScenePowerAnalyse();
			scenePowerAnalyse.setNoDataQuitFlag(false);
			ParallelManage parallelManage = new ParallelManage(scenePowerAnalyse,2);
			parallelManage.execute();
			logger.error("begin ScenePowerTask success");
			
		}catch(Exception e){
			logger.error("begin ScenePowerTask error!!!!!" + e.getMessage());
		}
		
	}


}
