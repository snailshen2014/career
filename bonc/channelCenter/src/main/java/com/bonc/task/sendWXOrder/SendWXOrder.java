/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: SendWXOrder.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.sendWXOrder
 * @Description: 微信工单下发接口
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月11日 上午1:17:14
 * @version: V1.0  
 */

package com.bonc.task.sendWXOrder;

/**
 * @ClassName: SendWXOrder
 * @Description: 微信工单下发接口
 * @author: LiJinfeng
 * @date: 2016年12月11日 上午1:17:14
 */
public interface SendWXOrder {

	/**
	 * @Title: WXChannelProcess
	 * @Description: 微信工单下发处理查询
	 * @return: void
	 * @param wxChannelId
	 * @param tenantId
	 * @throws: 
	 */
	public void activityProcess(String wxChannelId,String tenantId); 
	
}
