package com.bonc.task.autosend;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bonc.busi.activityInfo.mapper.ActivityInfoMapper;
import com.bonc.busi.activityInfo.po.ActivityInfo;
import com.bonc.busi.entity.PageBean;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.utils.Constants;
import com.bonc.utils.PropertiesUtil;
import com.github.pagehelper.PageHelper;

@Component("prepareTask")
@Scope("prototype")
public class PrepareTask<T> extends Thread {

	@Autowired
	ActivityInfoMapper activityInfoMapper;
   
	private BlockingQueue<T> queue;
	
	private String channelId;
	
	private String tenantId;

	private PageBean pageBean;

	public PageBean getPageBean() {
		return pageBean;
	}

	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getChannelId() {
		return channelId;
	}
	
	public void setQueue(BlockingQueue<T> queue){
		this.queue = queue;
	}
	
	@Override
	public void run() {
		while(true){
			if(queue.isEmpty()){
				List<T> dataList = queryData(tenantId,channelId);
				if(dataList != null && dataList.size() > 0) {
					for(T t : dataList){
						try {
							queue.put(t);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}else{
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private List<T> queryData(String tenantId, String channelId) {
		if(ChannelEnum.YJQD.getCode().equals(channelId)){
			List<String> channelList = Arrays.asList( PropertiesUtil.getConfig(
					Constants.YJQD_CHANNEL_LIST).split(Constants.SEPARATOR));
			PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());  
			
			 List<ActivityInfo>  activitys = activityInfoMapper.findNeedSendActivityInfos(tenantId, 
					 Constants.ORDER_STATUS_READY,channelList);
			 return (List<T>) activitys;
		}else if(ChannelEnum.WSC.getCode().equals(channelId)){
			
		}
		return null;
	
	}


}
