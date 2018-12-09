/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: InitRemoveToHis.java
 * @Prject: channelCenter
 * @Package: com.bonc.task
 * @Description: InitRemoveToHis
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月5日 下午6:01:59
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
import com.bonc.task.romovetohis.RemoveToHis;
import com.bonc.utils.Constants;

/**
 * @ClassName: InitRemoveToHis
 * @Description: 定时移除一个月之前的微信工单表、微信回执表、一级渠道回执表
 * @author: LiJinfeng
 * @date: 2017年1月5日 下午6:01:59
 */
@Component
public class InitRemoveToHis {
	
	@Autowired
	private RemoveToHis removeWXOrderToHis;
	
	@Autowired
	private RemoveToHis removeWXBackToHis;
	
	@Autowired
	private RemoveToHis removeYJQDBackToHis;
	
	@Autowired
	private RemoveToHis removeYJQDSendDataToHis;
	
    private static Log log = LogFactory.getLog(InitRemoveToHis.class);
	
    @Autowired
	private GlobalCFGMapper globalCFGMapper;
    
	private Integer flag = 0;

	
	/**
	 * @Title: startRemoveToHis
	 * @Description: 定时移除一个月之前的微信工单表、微信回执表、一级渠道回执表
	 * @return: void
	 * @throws: 
	 */
	@Scheduled(cron="0 0 2 * * ?")
	public void startRemoveToHis(){
		
		try {
			if (flag == 1) {
				return;
			}
			flag = 1;
			List<String> tenantIdList = globalCFGMapper.getTenantIdList(Constants.TENANT_VALID_STATE);
			/*String[] tenantIdList = PropertiesUtil.getConfig(Constants.TENANT_ID_LIST).split(Constants.SEPARATOR);*/
			if (tenantIdList == null) {
				log.info("valid tenantIdList is null！=======================================》");
				flag = 0;
				return;
			}
			for (String tenantId : tenantIdList) {

				log.info("TenantId:" + tenantId + " begin remove table WX_ORDER_INFO!"
						+ "=======================================》");
				removeWXOrderToHis.removeToHis(tenantId);
				log.info("TenantId:" + tenantId + " begin remove table WX_BACK_INFO!"
						+ "=======================================》");
				removeWXBackToHis.removeToHis(tenantId);
				log.info("TenantId:" + tenantId + " begin remove table PLT_YJQD_CONTACT_INFO!"
						+ "=======================================》");
				removeYJQDBackToHis.removeToHis(tenantId);
				log.info("TenantId:" + tenantId + " begin remove table YJQD_SEND_DATA!"
						+ "=======================================》");
				removeYJQDSendDataToHis.removeToHis(tenantId);
			}
			flag = 0;
		} catch (Exception e) {
			log.error("occurred exception when remove to history！=======================================》");
			e.printStackTrace();
			flag = 0;
		}
		
	}
	
}
