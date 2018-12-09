package com.bonc.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.bonc.busi.send.service.SentService;


/**
 * 
 * @author 高阳
 * @date 2016/11/16
 * @memo 定时任务 每天下发工单任务
 */
@Component
@Configurable
@EnableScheduling
public class DownSendOrder {

	@Autowired
	private SentService dxService;
	
	@Autowired
	private SentService yjService;
	
	/**
     * 工单下发
     * 
     * 每天1:00,开始执行任务
     * 1、查询活动列表  活动要求：（1）生失效时间包含今天，（2）且状态是有效状态的活动
     * 2、一活动为单位，批量向各个渠道发送任务，任务要求：（1）工单状态必须是未回执，（2）工单必须是未成功的[没有回执也可能成功]，（3）工单状态必须是有效
     * 3、直接根据不同渠道的对接方式，想各个渠道组装发送数据
     */
//    @Scheduled(cron = "0/10 * *  * * * ")
    public void dxSent(){
    	dxService.sent();
    }
    
    public void yjSent(){
    	yjService.sent();
    }
}
