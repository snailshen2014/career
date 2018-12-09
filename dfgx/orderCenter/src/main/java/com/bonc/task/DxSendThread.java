package com.bonc.task;

import com.bonc.busi.send.service.SentService;

/**
 * 短信发送
 * @author llb
 *
 */
public class DxSendThread extends Thread{
	private SentService dxService;
	public DxSendThread(SentService dxService){
		this.dxService = dxService;
	}
    public void run() {
    	dxService.sent();
	}
	public SentService getDxService() {
		return dxService;
	}
	public void setDxService(SentService dxService) {
		this.dxService = dxService;
	}
	
}
