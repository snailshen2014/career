/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: InitSyncCode.java
 * @Prject: channelCenter
 * @Package: com.bonc.task
 * @Description: InitSyncCode
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月16日 下午3:48:12
 * @version: V1.0  
 */

package com.bonc.task;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.task.synccode.SyncCode;
import com.bonc.utils.Constants;

/**
 * @ClassName: InitSyncCode
 * @Description:同步码表数据
 * @author: LiJinfeng
 * @date: 2017年1月16日 下午3:48:12
 */
@Component
public class InitSyncCode {
	
	 @Autowired
	 private GlobalCFGMapper globalCFGMapper;
	 
	 @Autowired
	 private SyncCode syncCode;
	
	 private static Log log = LogFactory.getLog(InitSyncCode.class);
	
	 private Integer flag = 0;
	 
	 
	 /*@Scheduled(cron = "0 0 2 * * ?")*/
	 public void startSyncCode(){
		 
		if(flag == 1){
    		return;
    	}
    	flag = 1;
    	List<String> tenantIdList = globalCFGMapper.getTenantIdList(Constants.TENANT_VALID_STATE);
    	/*String[] tenantIdList = PropertiesUtil.getConfig(Constants.TENANT_ID_LIST).split(Constants.SEPARATOR);*/
    	if(tenantIdList == null){
    		log.info("valid tenantIdList is null！=======================================》");
    		flag = 0;
    		return;
    	}
    	for(String tenantId:tenantIdList){
    		
    		syncCode.syncCode(tenantId);
    		
    	}
    	flag = 0;
		 
	 }

}
