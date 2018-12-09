package com.bonc.busi.service.func;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bonc.common.thread.ThreadBaseFunction;


public class EntryThread extends Thread{
	
	ThreadBaseFunction		ThreadBaseFunctionIns = null;
	Map<String, Object>	mapParaInfo = null;
	
	public EntryThread(ThreadBaseFunction func,Map<String, Object> mapParaInfo){
		this.ThreadBaseFunctionIns = func;
		this.mapParaInfo = mapParaInfo;
	}

	@Override
	public	void run(){
		ThreadBaseFunctionIns.handleData(mapParaInfo);
	}
}
