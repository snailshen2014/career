/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: FeedBackProcess.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.feedBack.task
 * @Description: 回执处理接口
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月10日 下午1:13:46
 * @version: V1.0  
 */

package com.bonc.task.feedBack.processer;

/**
 * @ClassName: FeedBackProcesser
 * @Description: 回执处理接口
 * @author: LiJinfeng
 * @date: 2016年12月10日 下午1:13:46
 */
public interface FeedBackProcesser {

	/**
	 * @Title: FeedBackProcess
	 * @Description: 回执处理方法
	 * @return: void
	 * @param tenantId
	 * @throws: 
	 */
	public void FeedBackProcess(String tenantId,String channelId);
	
}
