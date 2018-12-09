/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: WXOrderRun.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.sendWXOrder.impl
 * @Description: 生成微信工单线程
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月11日 下午5:04:25
 * @version: V1.0  
 */

package com.bonc.task.sendWXOrder.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;
import com.bonc.busi.wxOrderInfo.po.WXOrderInfo;
import com.bonc.busi.wxOrderInfo.service.WXOrderInfoService;

/**
 * @ClassName: WXOrderRun
 * @Description: 生成微信工单线程
 * @author: LiJinfeng
 * @date: 2017年1月11日 下午5:04:25
 */

public class WXOrderRun implements Callable<List<WXOrderInfo>>{

	/* (non Javadoc)
	 * @Title: call
	 * @Description: 生成微信工单线程
	 * @return
	 * @throws Exception
	 * @see java.util.concurrent.Callable#call()
	 */
	
	private List<WXOrderInfo> resultList = new ArrayList<WXOrderInfo>();
	
	private List<HashMap<String, Object>> wxOrderInfoMapList;
	
	private WXOrderInfoService wxOrderInfoService;
	
	private HashMap<String, Object> config;
	
	private WXActivityInfo wxActivityInfo;
	
	private static Log log = LogFactory.getLog(WXOrderRun.class);
	
	public WXOrderRun(List<HashMap<String, Object>> wxOrderInfoMapList,WXOrderInfoService wxOrderInfoService,
			HashMap<String, Object> config,WXActivityInfo wxActivityInfo){
		
		this.wxOrderInfoMapList = wxOrderInfoMapList;
		this.wxOrderInfoService = wxOrderInfoService;
		this.config = config;
		this.wxActivityInfo = wxActivityInfo;	
			
	}
	
	@Override
	public List<WXOrderInfo> call() throws Exception {
		
		log.info("ActivityId:"+wxActivityInfo.getActivityId() + 
				" this is a thread for sending order=========================》");
		for(HashMap<String, Object> wxOrderInfoMap:wxOrderInfoMapList){	
		    
			//存放分解完之后的fieldInfo和wxOrderInfo
			WXOrderInfo wxOrderInfo = new WXOrderInfo();
			//判断wxOrderInfo的必输字段是否为空
			Boolean wxOrderInfoFieldIsEmpty = wxOrderInfoService.wxOrderInfoFieldIsEmpty
					(wxOrderInfoMap, config, wxActivityInfo);
			if(!wxOrderInfoFieldIsEmpty){				
				log.error("ActivityId:"+wxActivityInfo.getActivityId()+ " weChat order:" + 
						wxOrderInfo.toString()+" dead field is null!");
				continue;
			}
			
			Boolean analyzeWXOrderInfoMap = wxOrderInfoService.analyzeWXOrderInfoMap
					(wxOrderInfoMap, config, wxActivityInfo, wxOrderInfo);
			if(!analyzeWXOrderInfoMap){
				continue;
			}
			wxOrderInfo.setWxActivityInfo(wxActivityInfo);	
			/*System.out.println(wxOrderInfo);*/
			//将符合条件微信工单信息放到List集合中
			resultList.add(wxOrderInfo);
		}
		return resultList;
		
	}

}
