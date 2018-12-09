package com.bonc.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.activityInfo.mapper.ActivityInfoMapper;
import com.bonc.busi.activityInfo.po.ActivityInfo;
import com.bonc.busi.channelStatus.mapper.ChannelStatusMapper;
import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.busi.monitor.MonitorService;
import com.bonc.busi.orderInfo.mapper.OrderInfoMapper;
import com.bonc.busi.sendData.mapper.SendDataMapper;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.utils.Constants;
import com.bonc.utils.PropertiesUtil;

/**
 * @ClassName: SendYJQDOrderTask
 * @Description: 启动定时任务，自动下发数据
 * @author: sky
 */
@Component
public class SendYJQDOrderTask {

	
	@Autowired
	MonitorService monitorService;
	    
	@Autowired
	OrderInfoMapper orderInfoMapper;
	
	@Autowired
	ActivityInfoMapper activityInfoMapper;
	
	@Autowired
	ChannelStatusMapper channelStatusMapper;
	
	@Autowired
	SendDataMapper sendDataMapper;
	
	@Autowired
	private GlobalCFGMapper globalCFGMapper;
	
	
	private static Log log = LogFactory.getLog(SendYJQDOrderTask.class);


	/**
     * @Title: startSendOrderTasks
     * @Description: 启动定时任务，自动下发数据
     * @return: void
     * @throws: 
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void startSendOrderTasks(){
 
    	List<String> tenantIdList = globalCFGMapper.getTenantIdList(Constants.TENANT_VALID_STATE);
		/*String[] tenantIdList = PropertiesUtil.getConfig(Constants.TENANT_ID_LIST).split(Constants.SEPARATOR);*/
		if (tenantIdList == null) {
			log.info("租户列表为空！=======================================》");
			return;
		}
    	/*String[] tenantIdList = PropertiesUtil.getConfig(
    			Constants.TENANT_ID_LIST).split(Constants.SEPARATOR);*/
    	for(String tenantId:tenantIdList){			
    		proccess(tenantId);
    	}
    }
    /**
     * 业务处理
     * @param tenantId
     */
	private void proccess(String tenantId) {
		//多账期 活动  下发控制
		Map<String,Integer> dealMonth = new HashMap<String,Integer>();
		//查询需要下发的活动 +账期   
		List<ActivityInfo> activityList = findNeedSendActivitys(tenantId);
		
		if(activityList != null && activityList.size() > 0){
			log.info("可下发一级渠道活动数："+activityList.size());
			//锁定活动数据
			lockActivitys(tenantId, activityList);
			//一个活动多个账期    取出活动对应的账期总数      
			for(ActivityInfo activity : activityList){
				String activityId = activity.getActivityId();
				if(dealMonth.containsKey(activityId)){
					int count = dealMonth.get(activityId) + 1;
					dealMonth.put(activityId, count);
				}else{
					dealMonth.put(activityId, 1);
				}
			}
			for(ActivityInfo activity : activityList){
			       //下发数据
					String result = send(activity);
					//更新状态 
					afterSend(activity,result,dealMonth);
					
			}
		//对下发失败的 活动处理 
		  failProcess(activityList,dealMonth);
		}else{
			log.info("无可下发的一级渠道相关活动！");
		}
	
		
	}
	
	/**
	 * 发送失败后的处理      （单次活动：再发送一次，如果还失败 就挂起  ）
	 * @param activityList 
	 * @param dealMonth 
	 */
	private void failProcess(List<ActivityInfo> activityList, Map<String, Integer> dealMonth) {
		
		for(java.util.Iterator<String> ite=dealMonth.keySet().iterator();ite.hasNext();){
			String activityId = ite.next();
			//未完成下发成功的活动     状态设定    如果原状态为  重试 则状态更新为挂起
			if(dealMonth.get(activityId) > 0){
				
				ActivityInfo activity = findActivityById(activityList,activityId);
	
				if( Constants.CHANNEL_STATUS_RESEND.equals(activity.getSendStatus())){
					updateChannelSendStatus(activity,Constants.CHANNEL_STATUS_HUNG);
				}else{
					updateChannelSendStatus(activity,Constants.CHANNEL_STATUS_RESEND);
				}
			}
		}
	}
    /**
     * 从活动列表中获取指定的活动
     * @param activityList
     * @param activityId
     * @return
     */
	private ActivityInfo findActivityById(List<ActivityInfo> activityList,String activityId) {
		for(ActivityInfo activity : activityList){
			if(activityId.equals(activity.getActivityId())){
				return activity;
			}else{
				continue;
			}
		}
		return null;
	}
	/**
	 * 数据下发后处理
	 * @param data
	 * @param result
	 * @param dealMonth 
	 */
	private void afterSend(ActivityInfo activity, String result, Map<String, Integer> dealMonth) {
		if(result != null){
			JSONObject  json = JSON.parseObject(result);
			//活动账期 处理成功
			if(json.getBoolean("success")){
				
				//updateOrderStatus(activity,Constants.CHANNEL_STATUS_SUCCESS);		
				
				String activityId = activity.getActivityId();		
				int count = dealMonth.get(activityId)-1;
				dealMonth.put(activityId, count);
				//所有账期都下发成功   则本次活动 数据下发成功  
				if(count == 0){
					updateChannelSendStatus(activity,Constants.CHANNEL_STATUS_SUCCESS);
					log.info("租户:"+activity.getTenantId()+"--活动："+activity.getActivityId()+"一级渠道数据下发完成！");
				}	
			}else{
				/*if(!"20001".equals(json.getString("errorcode")))
					deleteSendData(activity);*/
				log.info("一级渠道数据下发失败，租户："+activity.getTenantId()+"活动ID："+activity.getActivityId()+"--账期："+activity.getDealMonth());
			}
		}
	}
	
	/**
	 * 更新 渠道 下发状态
	 * @param data
	 * @param channelStatusSuccess
	 */
	private void updateChannelSendStatus(ActivityInfo activity, String status) {
		
		List<String> channelList = Arrays.asList( PropertiesUtil.getConfig(
				Constants.YJQD_CHANNEL_LIST).split(Constants.SEPARATOR));
		
		String tenantId = activity.getTenantId();
		List<Integer> idLsit=new ArrayList<Integer>();
		idLsit.add(activity.getRecId());
		channelStatusMapper.updateChannelSendStatus(idLsit, tenantId, 
				channelList, status);
	}
	
	/**
	 * 数据下发失败，清除下发数据表里的数据
	 * @param activity
	 */
	private void deleteSendData(ActivityInfo activity){
		String tenantId = activity.getTenantId();
		int activitySeqId = activity.getRecId();
		String dealMonth = activity.getDealMonth();
		//删除已入库的下发数据
		sendDataMapper.deleteSendDataByActivity(tenantId, activitySeqId, dealMonth);
	}
   /**
    * 更新工单channel_status
    * @param data
    * @param channelStatus
    */
	private void updateOrderStatus(ActivityInfo activity, String channelStatus) {
		
		/*List<String> channelList = Arrays.asList( PropertiesUtil.getConfig(
				Constants.YJQD_CHANNEL_LIST).split(Constants.SEPARATOR));*/	
		orderInfoMapper.updateChannelStatus(activity.getTenantId(),activity.getRecId(), 
				Constants.ORDER_STATUS_READY,activity.getDealMonth(),channelStatus);
	}

	/**
	 * 查询需要下发的活动  排除处理中的 
	 * @param tenantId
	 * @return
	 */
	private List<ActivityInfo> findNeedSendActivitys(String tenantId) {
		
			List<String> channelList = Arrays.asList( PropertiesUtil.getConfig(
					Constants.YJQD_CHANNEL_LIST).split(Constants.SEPARATOR));
	
			 List<ActivityInfo>  activitys = activityInfoMapper.findNeedSendActivityInfos(tenantId, 
					 Constants.ORDER_STATUS_READY,channelList);
			 return  activitys;
	
	}
	
	/**
	 * 锁定数据 
	 * @param tenantId
	 * @param activitys
	 */
	private void lockActivitys(String tenantId,List<ActivityInfo> activitys) {
		List<Integer> idLsit = new ArrayList<Integer>();
		for(ActivityInfo act : activitys){
			idLsit.add(act.getRecId());
		}
		List<String> channelList = Arrays.asList( PropertiesUtil.getConfig(
				Constants.YJQD_CHANNEL_LIST).split(Constants.SEPARATOR));
		channelStatusMapper.updateChannelSendStatus(idLsit, tenantId, 
				channelList, Constants.CHANNEL_STATUS_PROCCESS);
	}
	
    /**
     * 数据下发处理
     * @param data
     * @return
     */
	private String send(ActivityInfo activity) {
		
		String data = JSON.toJSONString(activity);
		String result =monitorService.sendToChannel(data,ChannelEnum.YJQD.getCode());
		return  result;
	}

}