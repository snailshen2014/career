/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: ActivityDetailServiceImpl.java
 * @Prject: orderCenter
 * @Package: com.bonc.busi.interfaces.service.impl
 * @Description: ActivityDetailServiceImpl
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月14日 下午5:18:58
 * @version: V1.0  
 */

package com.bonc.busi.interfaces.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.interfaces.mapper.ActivityDetailMapper;
import com.bonc.busi.interfaces.service.ActivityDetailService;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.utils.MapUtil;

/**
 * @ClassName: ActivityDetailServiceImpl
 * @Description: 活动详情服务类
 * @author: LiJinfeng
 * @date: 2017年1月14日 下午5:18:58
 */
@Service
@Transactional
public class ActivityDetailServiceImpl implements ActivityDetailService{
	
	@Autowired
	private ActivityDetailMapper activityDetailMapper;
	
	@Autowired
	private BaseMapper baseMapper;
	
	private HashMap<String,Object> config = new HashMap<String,Object>();
	
	private static final Logger log = Logger.getLogger(ActivityDetailServiceImpl.class);


	/* (non Javadoc)
	 * @Title: channelOrderCount
	 * @Description: 渠道工单生成数量查询服务实现方法
	 * @param activityId
	 * @return
	 * @see com.bonc.busi.interfaces.service.ActivityDetailService#channelOrderCount(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String channelOrderCount(HashMap<String, Object> parameter) {
		
		getGlobalConfig();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		//验证参数
	    Boolean validateParameter = validateParameter(parameter,
	    		(List<String>) config.get("channelOrderCountList"),resultMap);
	    if(!validateParameter){
	    	return JSON.toJSONString(resultMap);
	    }
	    
	    //参数字段转化
	    parameter.put("activityIdList", Arrays.asList(String.valueOf(parameter.get("activityIdList")).split(",")));
	    //存放resultList
	    List<HashMap<String, Object>> activityChannelOrderCountList = new  ArrayList<HashMap<String, Object>>();
		//获取结果
	    for(String activityId:(List<String>)parameter.get("activityIdList")){
	    	
	    	List<HashMap<String, Object>> channelOrderCount = activityDetailMapper.getChannelOrderCountByActivityId(
	    			activityId,String.valueOf(parameter.get("tenantId")));
	    	Boolean validateResultList = validateResultList(channelOrderCount, resultMap);
	  		if(validateResultList){
	  			HashMap<String, Object> activityChannelOrderCount = new HashMap<String, Object>();
	  			activityChannelOrderCount.put("activityId",activityId);
	  			activityChannelOrderCount.put("channelOrderCount",channelOrderCount);
	  			activityChannelOrderCountList.add(activityChannelOrderCount);
	  		}
	  		
	    }
		//验证结果
		Boolean validateResultList = validateResultList(activityChannelOrderCountList, resultMap);
  		if(!validateResultList){
  			return JSON.toJSONString(resultMap);
  		}
  		
  		//组装结果
		setCodeAndMsg("0000", "查询成功", resultMap);
		resultMap.put("tenantId", parameter.get("tenantId"));
		/*resultMap.put("activityId", parameter.get("activityId"));*/
		resultMap.put("resultList", activityChannelOrderCountList);
		System.out.println(JSON.toJSONString(resultMap));
		return JSON.toJSONString(resultMap);
		
	}

	/* (non Javadoc)
	 * @Title: activityLog
	 * @Description: 活动日志服务实现方法
	 * @param parameter
	 * @return
	 * @see com.bonc.busi.interfaces.service.ActivityDetailService#activityLog(java.lang.String)
	 */
	@Override
	public String activityLog(HashMap<String, Object> parameter) {
		
		getGlobalConfig();
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		//验证参数
	    Boolean validateParameter = validateParameter(parameter,
	    		(List<String>) config.get("activityLogList"),resultMap);
	    if(!validateParameter){
	    	return JSON.toJSONString(resultMap);
	    }
	    
	    //参数字段转化
	    parameter.put("activityIdList", Arrays.asList(String.valueOf(parameter.get("activityIdList")).split(",")));
	    if(parameter.get("activityStatusList")!=null){
	    	parameter.put("activityStatusList", Arrays.asList(String.valueOf(parameter.get("activityStatusList")).split(",")));
	    }
	    if(parameter.get("size")!=null){
	    	parameter.put("size", Integer.parseInt(String.valueOf(parameter.get("size"))));
	    }
	    else{
	    	parameter.put("size", ((List<String>)parameter.get("activityIdList")).size());
	    }
	    System.out.println(parameter.get("size"));
	    //获取结果
  	    List<HashMap<String, Object>> activityLogListByActivityList = 
  	    		activityDetailMapper.getActivityLogListByActivityList(parameter);	
  		//拼接'活动工单未生成'的记录
  	    if(parameter.get("activityStatus") == null || 
  	    		Arrays.asList(String.valueOf(parameter.get("activityStatus")).split(",")).contains("0")){
  	    	//获取查询结果的activityId列表
  	    	List<String> activityIdList = activityDetailMapper.getActivityIdList(parameter);
  	    	//获取参数activityId列表
  	    	List<String> activityIdListAll = (List<String>) parameter.get("activityIdList");
  	    	for(String activityId:activityIdListAll){
  	  			if(!activityIdList.contains(activityId)){
  					HashMap<String, Object> result = new HashMap<String, Object>();
  					result.put("activityId", activityId);
  					result.put("activityStatus", "0");
  					result.put("activityStatusDesc", "活动工单未生成");
  					activityLogListByActivityList.add(result);
  	  			}
  	  		}
  	    }
  		//验证结果
  		Boolean validateResultList = validateResultList(activityLogListByActivityList, resultMap);
  		if(!validateResultList){
  			return JSON.toJSONString(resultMap);
  		}
  		//组装结果
  		setCodeAndMsg("0000", "查询成功", resultMap);
  		resultMap.put("tenantId", parameter.get("activityId"));
  		resultMap.put("resultList", activityLogListByActivityList);
  		System.out.println(JSON.toJSONString(resultMap));
  		return JSON.toJSONString(resultMap);
  		
	}
	
	/**
	 * @Title: getGlobalConfig
	 * @Description: 获取全局参数
	 * @return: void
	 * @throws: 
	 */
	private void getGlobalConfig(){

		//channelOrderCount接口的必传参数列表
		List<String> channelOrderCountList = 
				Arrays.asList(baseMapper.getValueFromSysCommCfg("CHANNELORDERCOUNTLIST").split(","));
		config.put("channelOrderCountList", channelOrderCountList);
		//activityLog接口的必传参数列表
		List<String> activityLogList = 
				Arrays.asList(baseMapper.getValueFromSysCommCfg("ACTIVITYLOGLIST").split(","));
		config.put("activityLogList", activityLogList);

	}
	
	/**
	 * @Title: setCodeAndMsg
	 * @Description: 设置结果编码与结果描述
	 * @return: void
	 * @param code
	 * @param msg
	 * @param resultMap
	 * @throws: 
	 */
	private void setCodeAndMsg(String code,String msg,HashMap<String, Object> resultMap){
		
		resultMap.put("code", code);
		resultMap.put("msg", msg);
		
	}
	
	/**
	 * @Title: validateParameter
	 * @Description: 参数验证
	 * @return: Boolean
	 * @param parameter
	 * @param resultMap
	 * @return
	 * @throws: 
	 */
	public Boolean validateParameter(HashMap<String, Object> parameter,List<String> validList,
			HashMap<String, Object> resultMap){
		
		Boolean result = false;
		//验证参数是否为空
		if(parameter == null || parameter.isEmpty()){
			setCodeAndMsg("0001", "参数为空", resultMap);
			log.warn(resultMap.get("code")+":"+resultMap.get("msg"));
			return result;
		}
		//验证参数必输字段是否为空
		boolean someFieldsIsNotNull = MapUtil.someFieldsIsNotNull(
				parameter,validList);
		if(!someFieldsIsNotNull){
			setCodeAndMsg("0002", "必传参数为空", resultMap);
			log.warn(resultMap.get("code")+":"+resultMap.get("msg"));
			return result;
		}
		result = true;
		return result;
		
	}
	
	/**
	 * @Title: validateResultList
	 * @Description: 验证返回结果
	 * @return: Boolean
	 * @param resultList
	 * @param resultMap
	 * @return
	 * @throws: 
	 */
	private Boolean validateResultList(List<HashMap<String, Object>> resultList,HashMap<String, Object> resultMap){
		
		//验证结果是否为空
		Boolean result = false;
		if(resultList == null || resultList.isEmpty()){
  			setCodeAndMsg("0003", "查询结果为空", resultMap);
  			log.info(resultMap.get("code")+":"+resultMap.get("msg"));
  			return result;
  		}
		result = true;
		return result;
		
	}

}
