/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ProvFTPInfo.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.provFTPInfo.mapper
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月18日 下午3:01:20
 * @version: V1.0  
 */

package com.bonc.busi.ftpInfo.mapper;

import org.apache.ibatis.annotations.Param;
import com.bonc.busi.ftpInfo.po.FTPInfo;

/**
 * @ClassName: FTPInfoMapper
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年11月18日 下午3:01:20
 */
public interface FTPInfoMapper {
	
    /**
     * @Title: findFTPInfoByProvId
     * @Description: 根据PROV_ID查询FTP服务器配置信息
     * @return: FTPInfo
     * @param provId
     * @param tenantId
     * @return
     * @throws: 
     */
    public FTPInfo findFTPInfoByProvId(@Param("provId")String provId,@Param("tenantId")String tenantId);
	
	/**
	 * @Title: insertFTPInfo
	 * @Description: 向YJQD_FTP_INFO表中插入数据
	 * @return: void
	 * @param ftpInfo
	 * @throws: 
	 */
	public void insertFTPInfo(@Param("ftpInfo")FTPInfo ftpInfo);
	
	

}
