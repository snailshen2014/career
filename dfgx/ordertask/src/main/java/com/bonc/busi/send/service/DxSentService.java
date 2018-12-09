package com.bonc.busi.send.service;

import java.util.ArrayList;
import java.util.List;

import com.bonc.busi.send.model.sms.DxReq;
import com.bonc.busi.send.model.sms.DxResp;

public interface DxSentService {

	/**
	 * 发送短信数据
	 * @param req
	 * @return
	 */
	DxResp sendDx(DxReq req);
	
	/**
	 * 批量发送短信
	 * @param reqs
	 * @return
	 */
	DxResp sendDx(ArrayList<DxReq> reqs);
	
	/**
	 * 发送某个省份的短信消息
	 * @param provId
	 */
	void sendDx(String provId);
	
	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	public List<String> getSmsId(String tenantId);
}
