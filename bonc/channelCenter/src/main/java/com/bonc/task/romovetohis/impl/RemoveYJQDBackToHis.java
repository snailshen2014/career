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

import com.bonc.busi.removetohis.mapper.RemoveToHisMapper;
import com.bonc.task.romovetohis.RemoveToHis;
import com.bonc.utils.TimeUtil;

/**
 * @ClassName: RemoveYJQDBackToHis
 * @Description: 一级渠道回执移除实现类
 * @author: LiJinfeng
 * @date: 2017年1月5日 下午8:01:11
 */
@Component("removeYJQDBackToHis")
public class RemoveYJQDBackToHis implements RemoveToHis {

	@Autowired
	private RemoveToHisMapper removeToHisMapper;
	
	private static Log log = LogFactory.getLog(RemoveYJQDBackToHis.class);
	/* (non Javadoc)
	 * @Title: removeToHis
	 * @Description: 一级渠道回执移除实现方法
	 * @param tenantId
	 * @see com.bonc.task.romovetohis.RemoveToHis#removeToHis(java.lang.String)
	 */
	@Override
	public void removeToHis(String tenantId) {
		
		//获取当前日期
		String date = TimeUtil.formatSystemTime("yyyyMMddHHmmss");
		//将一个月之前的一级渠道回执复制到历史表
		Integer insertYJQDBackHis = removeToHisMapper.insertYJQDBackHis(
				date,date,tenantId);
		if(insertYJQDBackHis == null || insertYJQDBackHis == 0 ){
			log.info("Time:"+date+" the number of table PLT_YJQD_CONTACT_INFO which need to remove to history is null");
			return;
		}
	    //删除原有的一级渠道回执
		@SuppressWarnings("unused")
		Integer deleteYJQDBack = removeToHisMapper.deleteYJQDBack(date,tenantId);
		/*if(!deleteYJQDBack.equals(insertYJQDBackHis)){
			log.error("Time:"+date+" occurred error when delete from table PLT_YJQD_CONTACT_INFO!");
			return;
		}*/
        log.info("Time:" +date+" remove to history from table PLT_YJQD_CONTACT_INFO is success");
        return;

	}

}
