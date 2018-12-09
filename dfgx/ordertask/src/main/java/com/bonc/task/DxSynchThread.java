package com.bonc.task;

import com.bonc.busi.send.service.SentService;

public class DxSynchThread extends Thread{
	private SentService dxService;
	public DxSynchThread(SentService dxService){
		this.dxService = dxService;
	}
    public void run() {
    	dxService.synchSend();
	}
	public SentService getDxService() {
		return dxService;
	}
	public void setDxService(SentService dxService) {
		this.dxService = dxService;
	}
}
