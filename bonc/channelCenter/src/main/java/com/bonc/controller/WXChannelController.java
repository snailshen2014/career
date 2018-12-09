/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WXChannelController.java
 * @Prject: channelCenter
 * @Package: com.bonc.controller.send
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月19日 下午2:03:57
 * @version: V1.0  
 */

package com.bonc.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.busi.entity.PageBean;
import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;
import com.bonc.busi.wxOrderInfo.mapper.WXOrderInfoMapper;
import com.bonc.busi.wxOrderInfo.po.WXOrderInfo;
import com.bonc.busi.wxOrderInfo.service.WXOrderInfoService;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.task.sendWXOrder.SendWXOrder;

/**
 * @ClassName: WXChannelController
 * @Description: 微信渠道对外暴露接口
 * @author: LiJinfeng
 * @date: 2016年11月19日 下午2:03:57
 */

@Controller
@Scope("prototype")
@RequestMapping("/weChatChannel")
public class WXChannelController {	
	
	@Autowired
	SendWXOrder sendWXOrder;
	
	@Autowired
	WXOrderInfoMapper wxOrderMapper;
	
	@Autowired
	WXOrderInfoService wxOrderInfoService;
	
	/**
	 * @Title: WXChannelSend
	 * @Description: 微信渠道数据下发接口
	 * @return: String
	 * @param src
	 * @return
	 * @throws: 
	 */
	@RequestMapping("/sendOrder")
	@ResponseBody

	public void WXChannelSend(String tenantId){
		
		sendWXOrder.activityProcess(ChannelEnum.WX.getCode(),tenantId);
		
	}
	
	

}
