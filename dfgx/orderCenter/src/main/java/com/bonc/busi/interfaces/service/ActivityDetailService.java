/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: ActivityDetailService.java
 * @Prject: orderCenter
 * @Package: com.bonc.busi.interfaces.service
 * @Description: ActivityDetailService
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月14日 下午5:18:41
 * @version: V1.0  
 */

package com.bonc.busi.interfaces.service;

import java.util.HashMap;

/**
 * @ClassName: ActivityDetailService
 * @Description: 活动详情服务接口
 * @author: LiJinfeng
 * @date: 2017年1月14日 下午5:18:41
 */
public interface ActivityDetailService {
	
	/**
	 * @Title: channelOrderCount
	 * @Description: 渠道工单生成数量查询服务接口
	 * @return: String
	 * @param activityId
	 * @return
	 * @throws: 
	 */
	public String channelOrderCount(HashMap<String, Object> parameter);
	
	/**
	 * @Title: activityLog
	 * @Description: 活动日志服务接口
	 * @return: String
	 * @param parameter
	 * @return
	 * @throws: 
	 */
	public String activityLog(HashMap<String, Object> parameter);
	
	

}
