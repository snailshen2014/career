/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WXProductInfoMapper.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.wxProductInfo.mapper
 * @Description: WXProductInfoMapper
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月22日 下午1:44:02
 * @version: V1.0  
 */

package com.bonc.busi.wxProductInfo.mapper;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.wxProductInfo.po.WXProductInfo;

/**
 * @ClassName: WXProductInfoMapper
 * @Description: WXProductInfoMapper
 * @author: LiJinfeng
 * @date: 2016年11月22日 下午1:44:02
 */
public interface WXProductInfoMapper {
	

	/**
	 * @Title: findProductByProductId
	 * @Description: 从远程产品表中查询产品信息
	 * @return: WXProductInfo
	 * @param productId
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public WXProductInfo findProductByProductId(@Param("elementId")String elementId,
			@Param("tenantId")String tenantId);
	
	/**
	 * @Title: findWXProductInfoByProductId
	 * @Description: 查看本地产品表中是否有指定产品
	 * @return: WXProductInfo
	 * @param orderProductId
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public Integer findWXProductInfoByProductId(@Param("SAPId")String SAPId,
			@Param("tenantId")String tenantId);
	
	/**
	 * @Title: updateWXProductInfoByProductId
	 * @Description: 插入本地产品表
	 * @return: void
	 * @param wxProductInfo
	 * @param tenantId
	 * @throws: 
	 */
	public Integer insertWXProductInfo(@Param("productInfo")WXProductInfo productInfo);
	
	/**
	 * @Title: updateWXProductInfoByProductId
	 * @Description: 更新本地产品表
	 * @return: void
	 * @param wxProductInfo
	 * @param tenantId
	 * @throws: 
	 */
	public Integer updateWXProductInfoByProductId(@Param("productInfo")WXProductInfo productInfo);
	
}
