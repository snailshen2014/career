/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: RemoveToHisMapper.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.removetohis.mapper
 * @Description: RemoveToHisMapper
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月5日 下午8:37:52
 * @version: V1.0  
 */

package com.bonc.busi.removetohis.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @ClassName: RemoveToHisMapper
 * @Description: RemoveToHisMapper
 * @author: LiJinfeng
 * @date: 2017年1月5日 下午8:37:52
 */
public interface RemoveToHisMapper {
	
	
	/**
	 * @Title: insertWXOrderHis
	 * @Description: 将微信工单移入历史表
	 * @return: Integer
	 * @param inputTime
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public Integer insertWXOrderHis(@Param("inputTime")String inputTime,@Param("tenantId")String tenantId);
	
	/**
	 * @Title: deleteWXOrder
	 * @Description: 删除微信工单
	 * @return: Integer
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public Integer deleteWXOrder(@Param("tenantId")String tenantId);
	
	/**
	 * @Title: insertWXBackHis
	 * @Description: 将微信回执移入历史表
	 * @return: Integer
	 * @param inputTime
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public Integer insertWXBackHis(@Param("inputTime")String inputTime,@Param("tenantId")String tenantId);
	
	/**
	 * @Title: deleteWXBack
	 * @Description: 删除微信回执
	 * @return: Integer
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public Integer deleteWXBack(@Param("tenantId")String tenantId);
	
    /**
     * @Title: insertYJQDBackHis
     * @Description: 将一级渠道回执移入历史表
     * @return: Integer
     * @param inputTime
     * @param date
     * @param tenantId
     * @return
     * @throws: 
     */
    public Integer insertYJQDBackHis(@Param("inputTime")String inputTime,
    		@Param("date")String date,@Param("tenantId")String tenantId);
	
	/**
	 * @Title: deleteYJQDBack
	 * @Description: 删除一级渠道回执
	 * @return: Integer
	 * @param date
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public Integer deleteYJQDBack(@Param("date")String date,@Param("tenantId")String tenantId);

}
