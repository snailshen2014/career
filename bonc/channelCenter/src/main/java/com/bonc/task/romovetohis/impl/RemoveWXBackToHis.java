/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: RemoveWXBackToHis.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.romovetohis.impl
 * @Description: 微信回执移除实现类
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月5日 下午7:59:40
 * @version: V1.0  
 */

package com.bonc.task.romovetohis.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bonc.busi.removetohis.mapper.RemoveToHisMapper;
import com.bonc.task.romovetohis.RemoveToHis;
import com.bonc.utils.TimeUtil;

/**
 * @ClassName: RemoveWXBackToHis
 * @Description: 微信回执移除实现类
 * @author: LiJinfeng
 * @date: 2017年1月5日 下午7:59:40
 */
@Component("removeWXBackToHis")
public class RemoveWXBackToHis implements RemoveToHis {

	@Autowired
	private RemoveToHisMapper removeToHisMapper;
	
	private static Log log = LogFactory.getLog(RemoveWXBackToHis.class);
	/* (non Javadoc)
	 * @Title: removeToHis
	 * @Description: 微信回执移除实现方法
	 * @param tenantId
	 * @see com.bonc.task.romovetohis.RemoveToHis#removeToHis(java.lang.String)
	 */
	@Override
	public void removeToHis(String tenantId) {
		
		//获取当前日期
		String date = TimeUtil.formatSystemTime("yyyyMMddHHmmss");
		//将一个月之前的微信回执复制到历史表
		Integer insertWXBackHis = removeToHisMapper.insertWXBackHis(
				date,tenantId);
		if(insertWXBackHis == null || insertWXBackHis == 0 ){
			log.info("Time:"+date+" the number of table WX_BACK_INFO which need to remove to history is null");
			return;
		}
	    //删除原有的微信回执
		@SuppressWarnings("unused")
		Integer deleteWXBack = removeToHisMapper.deleteWXBack(tenantId);
		/*if(!deleteWXBack.equals(insertWXBackHis) ){
			log.error("Time:"+date+" occurred error when delete from table WX_BACK_INFO!");
			return;
 		}*/
		log.info("Time:" +date+" remove to history from table WX_BACK_INFO is success");
        return;
        
	}

}
