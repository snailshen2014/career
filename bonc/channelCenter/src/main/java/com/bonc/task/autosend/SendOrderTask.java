package com.bonc.task.autosend;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.monitor.MonitorService;
import com.bonc.busi.orderInfo.mapper.OrderInfoMapper;
import com.bonc.utils.Constants;
import com.bonc.utils.PropertiesUtil;

@Component("sendOrderTask")
@Scope("prototype")
public class SendOrderTask<T> extends Thread {


    @Resource
	MonitorService monitorService;
    
	private BlockingQueue<T> queue;
	
	private String channelId;
	
	@Autowired
	OrderInfoMapper orderInfoMapper;
	

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public void setQueue(BlockingQueue<T> queue){
		this.queue = queue;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if(queue.isEmpty()){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				String data =beforeSend();
				String result = send(data);
				afterSend(data,result);
			}
		}
	}

	private void afterSend(String data, String result) {
		if(result != null){
			JSONObject  json = JSON.parseObject(result);
			if(json.getBoolean("success")){
				refreshOrder(data,Constants.CHANNEL_STATUS_SUCCESS);
			}
		}
		
	}

	private void refreshOrder(String data, String channelStatus) {
		JSONObject  json = JSON.parseObject(data);
	/*	List<String> channelList = Arrays.asList( PropertiesUtil.getConfig(
				Constants.YJQD_CHANNEL_LIST).split(Constants.SEPARATOR));*/
		
		orderInfoMapper.updateChannelStatus(json.getString("tenantId"),json.getInteger("activityRecId"), 
				Constants.ORDER_STATUS_READY,json.getString("dealMonth"),channelStatus);
	}

	private String beforeSend() {
		try {
			return JSON.toJSONString(queue.take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String send(String data) {
		String result =monitorService.sendToChannel(data,channelId);
		return  result;
	}

}
