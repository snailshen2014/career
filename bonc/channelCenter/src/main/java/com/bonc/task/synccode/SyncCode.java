/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: SyncCode.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.synccode
 * @Description: SyncCode
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月16日 下午4:00:27
 * @version: V1.0  
 */

package com.bonc.task.synccode;

/**
 * @ClassName: SyncCode
 * @Description: 码表同步接口
 * @author: LiJinfeng
 * @date: 2017年1月16日 下午4:00:27
 */
public interface SyncCode {
	
	/**
	 * @Title: syncCode
	 * @Description: 同步全部码表数据至内存
	 * @return: void
	 * @param tenantId
	 * @throws: 
	 */
	public void syncCode(String tenantId);
	
	/**
	 * @Title: syncCode
	 * @Description: 同步单个码表数据至内存
	 * @return: String
	 * @param tenantId
	 * @param fieldName
	 * @param fieldKey
	 * @return
	 * @throws: 
	 */
	public String syncCode(String tenantId,String fieldName,String fieldKey);
	
}
