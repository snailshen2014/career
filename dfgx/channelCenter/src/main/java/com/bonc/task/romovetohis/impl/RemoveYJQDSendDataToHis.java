/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: RemoveYJQDBackToHis.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.romovetohis.impl
 * @Description: 一级渠道回执移除实现类
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月5日 下午8:01:11
 * @version: V1.0  
 */

package com.bonc.task.romovetohis.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.sendData.mapper.SendDataMapper;
import com.bonc.task.romovetohis.RemoveToHis;
import com.bonc.utils.TimeUtil;

/**
 * @ClassName: RemoveYJQDBackToHis
 * @Description: 一级渠道回执移除实现类
 * @author: LiJinfeng
 * @date: 2017年1月5日 下午8:01:11
 */
@Component("removeYJQDSendDataToHis")
public class RemoveYJQDSendDataToHis implements RemoveToHis {

	@Autowired
	private SendDataMapper sendDataMapper;
	
	private static Log log = LogFactory.getLog(RemoveYJQDSendDataToHis.class);
	/* (non Javadoc)
	 * @Title: removeToHis
	 * @Description: 一级渠道回执移除实现方法
	 * @param tenantId
	 * @see com.bonc.task.romovetohis.RemoveToHis#removeToHis(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeToHis(String tenantId) {
		
		//获取当前日期
		String date = TimeUtil.formatSystemTime("yyyyMMddHHmmss");
		//将一个月之前的一级渠道回执复制到历史表
		Integer insertCount = sendDataMapper.insertSendDataHis(date, tenantId);
		if(insertCount == null || insertCount == 0 ){
			log.info("Time:"+date+" the number of table YJQD_SEND_DATA which need to remove to history is null");
			return;
		}
	    //删除原有的一级渠道回执
		sendDataMapper.deleteSendData(tenantId);
	
        log.info("Time:" +date+" remove to history from table YJQD_SEND_DATA is success");
        return;

	}

}
