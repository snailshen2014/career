/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ActivityConfigInfoMapper.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.activityConfigInfo.mapper
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月18日 上午11:08:17
 * @version: V1.0  
 */

package com.bonc.busi.activityXMLInfo.mapper;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.activityXMLInfo.po.ActivityXMLInfo;

/**
 * @ClassName: ActivityXMLInfoMapper
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年11月18日 上午11:08:17
 */
public interface ActivityXMLInfoMapper {
	
	
	 
	/**
	 * @Title: insertActivityXMLInfo
	 * @Description: 向YJQD_ACTIVITY_XML_INFO表中插入数据
	 * @return: void
	 * @param activityXMLInfo
	 * @throws: 
	 */
	public void insertActivityXMLInfo(@Param("activityXMLInfo")ActivityXMLInfo activityXMLInfo);
	
	/**
	 * @Title: updateActivityXMLInfo
	 * @Description: 更新YJQD_ACTIVITY_XML_INFO表中的xml数据
	 * @return: void
	 * @param activityXMLInfo
	 * @throws: 
	 */
	public void updateActivityXMLInfo(@Param("activityXMLInfo")ActivityXMLInfo activityXMLInfo);
	
	/**
	 * @Title: findActivityXMLInfo
	 * @Description: 根据activitySeqId、dealMonth和tenantId查询YJQD_ACTIVITY_XML_INFO表中数据
	 * @return: ActivityXMLInfo
	 * @param activityId
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public ActivityXMLInfo findActivityXMLInfo(@Param("activityId")String activityId,@Param("tenantId")String tenantId,
				@Param("activitySeqId")int activitySeqId,@Param("dealMonth")String dealMonth);
	

}
