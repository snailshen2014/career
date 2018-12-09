/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: ActivityDetailController.java
 * @Prject: orderCenter
 * @Package: com.bonc.controller.interfaces
 * @Description: 活动详情查询
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月14日 下午5:12:25
 * @version: V1.0  
 */

package com.bonc.controller.interfaces;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.interfaces.service.ActivityDetailService;
import com.bonc.utils.HttpUtil;

/**
 * @ClassName: ActivityDetailController
 * @Description: 活动详情查询
 * @author: LiJinfeng
 * @date: 2017年1月14日 下午5:12:25
 */
@RestController
@RequestMapping("/interface/activityDetail")
public class ActivityDetailController {
	
	@Autowired
	private ActivityDetailService activityDetailService;
	
	/**
	 * @Title: channelOrderCount
	 * @Description: 渠道工单生成数量查询接口
	 * @return: String
	 * @param activityId
	 * @return
	 * @throws: 
	 */
	@RequestMapping(value="/channelOrderCount",method={RequestMethod.POST})
	public String channelOrderCount(@RequestBody HashMap<String, Object> parameter){
		
		System.out.println(parameter);
		return activityDetailService.channelOrderCount(parameter);
		
	}
	
	/**
	 * @Title: activityLog
	 * @Description: 活动日志接口
	 * @return: String
	 * @param activityId
	 * @return
	 * @throws: 
	 */
	@RequestMapping(value ="/activityLog",method={RequestMethod.POST})
	public String activityLog(@RequestBody HashMap<String, Object> parameter){
		
		System.out.println(parameter);
		return activityDetailService.activityLog(parameter);
		
	}
	
	
	public static void main(String[] args) {
		
		/*HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("tenantId", "uni076");
		parameterMap.put("activityIdList", "2222,108975");
		String sendPost = HttpUtil.sendPost("http://127.0.0.1:17001/ordercenter/activityDetail/channelOrderCount",JSON.toJSONString(parameterMap));*/
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("tenantId", "uni076");
		parameterMap.put("activityIdList", "108457,108431,108572");
		parameterMap.put("startTime", "20170101000000");
		parameterMap.put("endTime", "20170115000000");
		parameterMap.put("activityStatusList", "0,1,2");
		parameterMap.put("orderBy", "desc");
		/*parameterMap.put("size", "1");*/
		String sendPost = HttpUtil.sendPost("http://127.0.0.1:17001/ordercenter/activityDetail/activityLog",JSON.toJSONString(parameterMap));
		System.out.println(sendPost);
		
	}
	

}
