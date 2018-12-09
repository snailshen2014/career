/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ActivityRun.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.sendWXOrder.impl
 * @Description: ActivityRun
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月20日 上午11:04:38
 * @version: V1.0  
 */

package com.bonc.task.sendWXOrder.impl;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;

/**
 * @ClassName: ActivityRun
 * @Description: ActivityRun
 * @author: LiJinfeng
 * @date: 2016年12月20日 上午11:04:38
 */
@Component("activityRun")
@Scope("prototype")
public class ActivityRun extends Thread{
	
	@Resource(name="sendWXOrderByActivity")
	private SendWXOrderByActivity sendWXOrderByActivity;

	private WXActivityInfo wxActivityInfo;
	
	private String channelId;
	
	public WXActivityInfo getWxActivityInfo() {
		return wxActivityInfo;
	}


	public void setWxActivityInfo(WXActivityInfo wxActivityInfo) {
		this.wxActivityInfo = wxActivityInfo;
	}

	public String getChannelId() {
		return channelId;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	@Override
	public void run() {
		
		sendWXOrderByActivity.orderProcess(wxActivityInfo,channelId);
		
	}
	
	

}
