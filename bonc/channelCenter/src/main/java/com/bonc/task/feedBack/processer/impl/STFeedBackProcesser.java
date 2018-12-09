/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: STFeedBackProcesser.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.feedBack.process
 * @Description: 手厅回执处理实现类
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月10日 下午1:16:08
 * @version: V1.0  
 */

package com.bonc.task.feedBack.processer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bonc.busi.entity.PageBean;
import com.bonc.busi.feedBackInfo.service.FeedBackInfoService;
import com.bonc.task.feedBack.processer.FeedBackProcesser;

/**
 * @ClassName: STFeedBackProcesser
 * @Description: 手厅回执处理实现类
 * @author: LiJinfeng
 * @date: 2016年12月10日 下午1:16:08
 */

@Component("stFeedBackProcesser")
public class STFeedBackProcesser implements FeedBackProcesser{
	
	@Autowired
    private FeedBackInfoService stBackInfoService;
	
	private static Log log = LogFactory.getLog(STFeedBackProcesser.class);

	/* (non Javadoc)
	 * @Title: FeedBackProcess
	 * @Description: 手厅回执处理实现方法
	 * @param tenantId
	 * @see com.bonc.task.feedBack.processer.FeedBackProcesser#FeedBackProcess(java.lang.String)
	 */
	@Override
	public void FeedBackProcess(String tenantId,String channelId) {
		
		log.info("TenantId:" + tenantId + " begin processing feedback of handoffice");
		
		//获取配置参数
		HashMap<String, Object> config = stBackInfoService.getConfig(tenantId,channelId);
		//设置字段名
		config.put("fieldName", config.get("stField"));
		//获取分页对象
		PageBean pageBean = stBackInfoService.getYJQDPageBeanByProvIdAndAccountTime(config);
		//获取id的list集合
		@SuppressWarnings("unchecked")
		List<Integer> idList = (List<Integer>) config.get("idList");
		//判断手厅回执信息是否为0
		if(pageBean.getTotal() < 1){
			log.info("TenantId:" + tenantId +" the feedback of handoffice is null");
			return;
		}
		
		while(pageBean.getCurrentPage() <= pageBean.getTotalPage()){
			//本次查询的id子集合
			List<Integer> subIdList = idList.subList(pageBean.getStartPage(), 
					pageBean.getEndPage());
			//存放符合条件的回执信息
			List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
			//存放符合条件的回执ID列表
			List<Integer> resultIdList = new ArrayList<Integer>();
			
			//获取回执数据
			List<HashMap<String, Object>> stFeedBackInfoList = stBackInfoService.
					findSTFeedBackByProvIdAndAccountTime(config,subIdList);
			if(stFeedBackInfoList == null){
				log.info("TenantId:" + tenantId +" handoffice page "+pageBean.getCurrentPage()+" feedback is null");
				pageBean.setCurrentPage(pageBean.getCurrentPage()+1);
				continue;
			}
		    //遍历处理回执数据
			for(HashMap<String, Object> stFeedBackInfo:stFeedBackInfoList){	
				
				//筛选回执数据
				HashMap<String, Object> result = stBackInfoService.yjqdFeedBackFilter(stFeedBackInfo,config);
				if(result == null){
					log.warn("TenantId:" + tenantId + " handoffice feedback " +
							stFeedBackInfo.toString()+" dead field is null");
					continue;
				}			
				//resultList键值对修改
				stBackInfoService.yjqdTranslator(result, config);
				//添加到结果集
				resultList.add(result);
				//删除多余的ID属性
				resultIdList.add(Integer.parseInt(String.valueOf(result.get("id"))));
				result.remove("id");
			}
			if(resultList.size()<1){
				log.info("TenantId:" + tenantId + " handoffice page "+pageBean.getCurrentPage()+
						" feedback is null after filter");
				pageBean.setCurrentPage(pageBean.getCurrentPage()+1);
				continue;
			}
			//批量更新回执数据状态
			Boolean updateYJQDBack = stBackInfoService.updateYJQDBack(resultIdList, config);
			if(!updateYJQDBack){
				log.error("TenantId:" + tenantId + " handoffice page "+pageBean.getCurrentPage()+
						" update status error");
				continue;
			}
			//调用回执处理接口
			stBackInfoService.sendFeedBackList(resultList,config);
			//进入下一页
			pageBean.setCurrentPage(pageBean.getCurrentPage()+1);
		}		
		
	}

}
