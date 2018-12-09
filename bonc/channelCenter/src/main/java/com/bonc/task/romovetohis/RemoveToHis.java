/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: RemoveToHis.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.romovetohis
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月5日 下午7:43:10
 * @version: V1.0  
 */

package com.bonc.task.romovetohis;

/**
 * @ClassName: RemoveToHis
 * @Description: 移除到历史表接口
 * @author: LiJinfeng
 * @date: 2017年1月5日 下午7:43:10
 */
public interface RemoveToHis {
	
	/**
	 * @Title: removeToHis
	 * @Description: 移除到历史表接口
	 * @return: void
	 * @param tenantId
	 * @throws: 
	 */
	public void removeToHis(String tenantId);

}
