package com.bonc.service.func;

import com.bonc.common.thread.ThreadBaseFunction;

import java.util.Map;


public class EntryThread extends Thread{
	
	ThreadBaseFunction ThreadBaseFunctionIns = null;
	Map<String, Object>	mapParaInfo = null;
	
	public EntryThread(ThreadBaseFunction func, Map<String, Object> mapParaInfo){
		this.ThreadBaseFunctionIns = func;
		this.mapParaInfo = mapParaInfo;
	}

	@Override
	public	void run(){
		ThreadBaseFunctionIns.handleData(mapParaInfo);
	}
}
