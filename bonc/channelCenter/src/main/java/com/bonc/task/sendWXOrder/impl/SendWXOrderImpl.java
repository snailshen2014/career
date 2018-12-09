/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: SendWXOrderImpl.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.sendWXOrder.impl
 * @Description: 微信工单下发接口实现类
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月11日 上午1:18:07
 * @version: V1.0  
 */

package com.bonc.task.sendWXOrder.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;
import com.bonc.busi.wxOrderInfo.service.WXOrderInfoService;
import com.bonc.task.sendWXOrder.SendWXOrder;
import com.bonc.task.synccode.SyncCode;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.Constants;

/**
 * @ClassName: SendWXOrderImpl
 * @Description: 微信工单下发接口实现类
 * @author: LiJinfeng
 * @date: 2016年12月11日 上午1:18:07
 */

@Component("sendWXOrderImpl")
public class SendWXOrderImpl implements SendWXOrder{
	
	@Autowired
	private WXOrderInfoService wxOrderInfoService;
	
	@Autowired
	private SyncCode syncCode;
	
	@Resource(name="sendWXOrderByActivity")
	private SendWXOrderByActivity sendWXOrderByActivity;
	
	/*@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;*/

	private static Log log = LogFactory.getLog(SendWXOrderImpl.class);
	
	/* (non Javadoc)
	 * @Title: WXChannelProces
	 * @Description: 微信工单下发处理查询
	 * @param src
	 * @return
	 * @see com.bonc.busi.service.WXChannelService#WXChannelProces(java.lang.String)
	 */
	@Override
	public void activityProcess(String wxChannelId,String tenantId) {
		/*System.out.println(threadPoolTaskExecutor.toString());*/
		
		//加载常量
		HashMap<String, Object> config = new HashMap<String,Object>();
		config.put("tenantId", tenantId);
		config.put("channelId", wxChannelId);
		config.put("activityChannelReadyStatus",Constants.ACTIVITY_CHANNEL_READY_STATUS);
		config.put("activityStatusList",Constants.ACTIVITY_STATUS_LIST);
		//获取符合条件的活动列表
		List<WXActivityInfo> findActivityList = wxOrderInfoService.findActivityList(config);
		if(findActivityList == null || findActivityList.isEmpty() || findActivityList.size() == 0){
			log.info("no activity need to send order!");
			return;
		}
		String activityIdList = new String();
		for(WXActivityInfo wxActivityInfo:findActivityList){
			activityIdList = activityIdList + wxActivityInfo.getActivityId() + ",";
		}
		log.info("the list of activity who need to send order："+activityIdList);
		log.info("load code table begin");
		CodeUtil.clearTable();
		syncCode.syncCode(tenantId);
		log.info("load code table end");
		/*log.info("thread start sleeping,wait the master-slave synchronization of mysql");
        long sleepTime = Long.parseLong(globalCFGMapper.getGlobalCFG("SLEEP_TIME"));
	    try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			log.error(this.toString()+":thread occurred exception when sleeping!");
		}
	    log.info("sleep end");*/
		for(WXActivityInfo wxActivityInfo:findActivityList){
			
			//启动线程，处理活动工单
			/*ActivityRun activityRun = ApplicationContextUtil.getBean("activityRun", ActivityRun.class);
			activityRun.setChannelId(wxChannelId);
			activityRun.setWxActivityInfo(wxActivityInfo);
        	threadPoolTaskExecutor.execute(activityRun);*/	
			sendWXOrderByActivity.orderProcess(wxActivityInfo,wxChannelId);
    
		}
		/*//判断所有线程是否执行完毕
		while(true){
    		try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				log.error(this.toString()+"线程休眠失败！");
				e.printStackTrace();
			}
            log.error("发送微信工单当前活动线程数："+threadPoolTaskExecutor.getActiveCount());
    		if(threadPoolTaskExecutor.getActiveCount() == 0){
    			return;
    		}
    	}*/
		
	}

}
