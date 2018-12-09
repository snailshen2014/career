package com.bonc.busi.task.base;
/*
 * @desc:针对高耗时的SQL做优化
 * @author:曾定勇
 * @time:2016-12-14
 */

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.common.thread.ThreadBaseFunction;

public class InnerDbQueryThread extends Thread{
	private final static Logger log= LoggerFactory.getLogger(InnerDbQueryThread.class);
	ThreadBaseFunction		ThreadBaseFunctionIns = null;
	Map<String, Object>	mapData = null;
	
	public InnerDbQueryThread(ThreadBaseFunction func,Map<String, Object> mapData){
		this.ThreadBaseFunctionIns = func;
		this.mapData = mapData;
	}

	@Override
	public	void run(){
		ThreadBaseFunctionIns.handleData(mapData);
	}

}
