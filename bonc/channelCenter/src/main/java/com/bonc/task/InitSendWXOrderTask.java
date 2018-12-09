/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: InitSendWXOrderTask.java
 * @Prject: channelCenter
 * @Package: com.bonc.task
 * @Description: 下发微信工单任务，循环执行
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月6日 下午10:43:17
 * @version: V1.0  
 */

package com.bonc.task;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.task.sendWXOrder.SendWXOrder;
import com.bonc.utils.ApplicationContextUtil;
import com.bonc.utils.Constants;

/**
 * @ClassName: InitSendWXOrderTask
 * @Description: 下发微信工单任务，定时执行
 * @author: LiJinfeng
 * @date: 2016年12月6日 下午10:43:17
 */
@Component
public class InitSendWXOrderTask{

	/* (non Javadoc)
	 * @Title: onApplicationEvent
	 * @Description: 下发微信工单任务，循环执行
	 * @param arg0
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	private Log log = LogFactory.getLog(InitSendWXOrderTask.class);
	
	@Autowired
	private GlobalCFGMapper globalCFGMapper;
	//程序内部控制定时任务
	private Integer flag = 0;
	
	@Scheduled(cron = "0 * * * * ?")
	public void startSendWXOrderTask() {
		
		try {
			/*Integer flag = Integer.parseInt(globalCFGMapper.getGlobalCFG("CHANNEL_RUNNING"));*/
			if (flag == 1) {
				return;
			}
			/*globalCFGMapper.setGlobalCFG("CHANNEL_RUNNING","1");*/
			flag = 1;
			List<String> tenantIdList = globalCFGMapper.getTenantIdList(Constants.TENANT_VALID_STATE);
			/*String[] tenantIdList = PropertiesUtil.getConfig(Constants.TENANT_ID_LIST).split(Constants.SEPARATOR);*/
			if (tenantIdList == null) {
				log.info("TenantIdList is null！=======================================》");
				flag = 0;
				return;
			}
			for (String tenantId : tenantIdList) {
				log.info("TenantId:" + tenantId + " Channel:11 begin sending order============================》");
				SendWXOrder sendWXOrder = ApplicationContextUtil.getBean("sendWXOrderImpl", SendWXOrder.class);
				sendWXOrder.activityProcess(ChannelEnum.WX.getCode(), tenantId);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				log.error(this.toString() + ":thread occurred exception when sleeping!");
				e.printStackTrace();
			}
			/*globalCFGMapper.setGlobalCFG("CHANNEL_RUNNING","0");*/
			flag = 0;
		} catch (Exception e) {
			log.error("Channel:11 occurred exception when sending order！=======================================》");
			e.printStackTrace();
			flag = 0;
		}
			
	}

}
