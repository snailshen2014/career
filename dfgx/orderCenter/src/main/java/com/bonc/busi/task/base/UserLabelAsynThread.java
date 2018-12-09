package com.bonc.busi.task.base;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bonc.common.thread.ThreadBaseFunction;


public class UserLabelAsynThread extends Thread{
	
	ThreadBaseFunction		ThreadBaseFunctionIns = null;
	Map<String, Object>	mapTenantInfo = null;
	
	public UserLabelAsynThread(ThreadBaseFunction func,Map<String, Object> mapTenantInfo){
		this.ThreadBaseFunctionIns = func;
		this.mapTenantInfo = mapTenantInfo;
	}

	@Override
	public	void run(){
		ThreadBaseFunctionIns.handleData(mapTenantInfo);
	}
}
