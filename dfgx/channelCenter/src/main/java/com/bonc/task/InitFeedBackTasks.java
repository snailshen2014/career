package com.bonc.task;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.task.feedBack.processer.FeedBackProcesser;
import com.bonc.utils.Constants;

/**
 * @ClassName: InitFeedBackTasks
 * @Description: 采集回执信息定时任务，处理微信与一级渠道的工单回执
 * @author: LiJinfeng
 * @date: 2016年12月3日 下午3:00:03
 */
@Component
public class InitFeedBackTasks {

	/*@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;*/
	
	@Resource(name="wxFeedBackProcesser")
	private FeedBackProcesser wxFeedBackProcesser;
	
	@Resource(name="stFeedBackProcesser")
	private FeedBackProcesser stFeedBackProcesser;

	@Resource(name="wtFeedBackProcesser")
	private FeedBackProcesser wtFeedBackProcesser;
	
	@Resource(name="wscFeedBackProcesser")
	private FeedBackProcesser wscFeedBackProcesser;
	
	private static Log log = LogFactory.getLog(InitFeedBackTasks.class);
	
	@Autowired
	private GlobalCFGMapper globalCFGMapper;
	
	private Integer flag = 0;

    /**
     * @Title: startWXTasks
     * @Description: 采集回执信息定时任务，处理微信与一级渠道的工单回执
     * @return: void
     * @throws: 
     */
	@Scheduled(cron = "0 0/5 * * * ?")
    public void startFeedBackTasks(){
    	
    	try {
			if (flag == 1) {
				return;
			}
			flag = 1;
			/*List<String> tenantIdList = null;*/
			List<String> tenantIdList = globalCFGMapper.getTenantIdList(Constants.TENANT_VALID_STATE);
			if (tenantIdList == null) {
				log.info("valid tenantIdList is null！=======================================》");
				flag = 0;
				return;
			}
			/*String[] tenantIdList = PropertiesUtil.getConfig(Constants.TENANT_ID_LIST).split(Constants.SEPARATOR);*/
			for (String tenantId : tenantIdList) {

				log.info("TenantId:" + tenantId + " begin processing "
						+ "feedback of weChat and level one===================================》");
				//微信回执处理
				wxFeedBackProcesser.FeedBackProcess(tenantId, ChannelEnum.WX.getCode());
				//手厅回执处理
				stFeedBackProcesser.FeedBackProcess(tenantId, ChannelEnum.ST.getCode());
				//网厅回执处理
				wtFeedBackProcesser.FeedBackProcess(tenantId, ChannelEnum.WT.getCode());
				//沃视窗回执处理
				wscFeedBackProcesser.FeedBackProcess(tenantId, ChannelEnum.WSC.getCode());

				log.info("TenantId:" + tenantId + " process feedback end===================================》");
				/*FeedBackRun wxFeedBackRun = ApplicationContextUtil.getBean("feedBackRun", FeedBackRun.class);
				wxFeedBackRun.setChannelId(ChannelEnum.WX.getCode());
				wxFeedBackRun.setTenantId(tenantId);
				threadPoolTaskExecutor.execute(wxFeedBackRun);
				
				FeedBackRun stFeedBackRun = ApplicationContextUtil.getBean("feedBackRun", FeedBackRun.class);
				stFeedBackRun.setChannelId(ChannelEnum.ST.getCode());
				stFeedBackRun.setTenantId(tenantId);
				threadPoolTaskExecutor.execute(stFeedBackRun);
				
				FeedBackRun wtFeedBackRun = ApplicationContextUtil.getBean("feedBackRun", FeedBackRun.class);
				wtFeedBackRun.setChannelId(ChannelEnum.WT.getCode());
				wtFeedBackRun.setTenantId(tenantId);
				threadPoolTaskExecutor.execute(wtFeedBackRun);
				
				FeedBackRun wscFeedBackRun = ApplicationContextUtil.getBean("feedBackRun", FeedBackRun.class);
				wscFeedBackRun.setChannelId(ChannelEnum.WSC.getCode());
				wscFeedBackRun.setTenantId(tenantId);
				threadPoolTaskExecutor.execute(wscFeedBackRun);
				
				//判断是否所有线程执行完毕
				while(true){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						log.error(this.toString()+"线程休眠异常！");
						e.printStackTrace();
					}
					if(threadPoolTaskExecutor.getActiveCount() == 0){
						break;
					}
				}*/
				/*threadPoolTaskExecutor.shutdown();*/
			}
			flag = 0;
		} catch (Exception e) {
			log.error("occurred exception when processing feedback！=======================================》");
			e.printStackTrace();
			flag = 0;
		}
    	
    }
    
}