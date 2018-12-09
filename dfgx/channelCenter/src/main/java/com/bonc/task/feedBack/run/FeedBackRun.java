package com.bonc.task.feedBack.run;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bonc.common.utils.ChannelEnum;
import com.bonc.task.feedBack.processer.FeedBackProcesser;



/**
 * @ClassName: FeedBackTask
 * @Description: 定时任务线程
 * @author: LiJinfeng
 * @date: 2016年12月3日 下午3:04:53
 */
@Component("feedBackRun")
@Scope("prototype")
public class FeedBackRun extends Thread{
	
	/* (non Javadoc)
	 * @Title: run
	 * @Description: 定时任务线程
	 * @see java.lang.Thread#run()
	 */
	@Resource(name="wxFeedBackProcesser")
	private FeedBackProcesser wxFeedBackProcesser;
	
	@Resource(name="stFeedBackProcesser")
	private FeedBackProcesser stFeedBackProcesser;

	@Resource(name="wtFeedBackProcesser")
	private FeedBackProcesser wtFeedBackProcesser;
	
	@Resource(name="wscFeedBackProcesser")
	private FeedBackProcesser wscFeedBackProcesser;
	
	private String channelId;
	
	private String tenantId;
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}


	@Override
	public void run() {
		       
		if(channelId.equals(ChannelEnum.WX.getCode())){
			wxFeedBackProcesser.FeedBackProcess(tenantId,channelId);
		}
		else if(channelId.equals(ChannelEnum.ST.getCode())){
			stFeedBackProcesser.FeedBackProcess(tenantId,channelId);
		}
		else if(channelId.equals(ChannelEnum.WT.getCode())){
			wtFeedBackProcesser.FeedBackProcess(tenantId,channelId);	
	    }
		else{
			wscFeedBackProcesser.FeedBackProcess(tenantId,channelId);
		}
		
	}
}
