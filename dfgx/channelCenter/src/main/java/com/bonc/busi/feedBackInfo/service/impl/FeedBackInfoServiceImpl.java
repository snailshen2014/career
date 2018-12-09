/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: yjqdBackInfoServiceImpl.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.yjqdBackInfo.service.impl
 * @Description: 回执处理服务接口实现类
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月10日 下午1:31:28
 * @version: V1.0  
 */

package com.bonc.busi.feedBackInfo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.entity.PageBean;
import com.bonc.busi.feedBackInfo.mapper.FeedBackInfoMapper;
import com.bonc.busi.feedBackInfo.service.FeedBackInfoService;
import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.utils.Constants;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.MapUtil;
import com.bonc.utils.PropertiesUtil;
import com.bonc.utils.TimeUtil;

/**
 * @ClassName: yjqdBackInfoServiceImpl
 * @Description: 回执处理服务接口实现类
 * @author: LiJinfeng
 * @date: 2016年12月10日 下午1:31:28
 */
@Service
@Scope("prototype")
@Transactional
public class FeedBackInfoServiceImpl implements FeedBackInfoService{
	
	@Autowired
    private FeedBackInfoMapper feedBackInfoMapper;
	
	@Autowired
	private GlobalCFGMapper globalCFGMapper;
	
	@Autowired
	private PageBean pageBean;
	
	private static Log log = LogFactory.getLog(FeedBackInfoServiceImpl.class);
	
	/* (non Javadoc)
	 * @Title: getConfig
	 * @Description: 设置所需常量
	 * @param tenantId
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#getConfig(java.lang.String)
	 */
	public HashMap<String, Object> getConfig(String tenantId,String channelId){
		
		//请求参数map
		HashMap<String, Object> config = new HashMap<String, Object>();
		
		//设置provId
		config.put("provId",tenantId.substring(Constants.TENANT_ID_PREFIX.length()));
		//设置表名
		config.put("tableName",Constants.TABLE_NAME_PREFIX+tenantId.substring(Constants.TENANT_ID_PREFIX.length()));
		//设置字段名
		config.put("stField",Constants.TABLE_FIELD_ST);
		config.put("wtField",Constants.TABLE_FIELD_WT);
		config.put("wscField",Constants.TABLE_FIELD_WSC);
		/*config.put("stTableName", Constants.TABLE_NAME_PREFIX + Constants.TABLE_NAME_ST + 
				tenantId.substring(Constants.TENANT_ID_PREFIX.length()));
		config.put("wtTableName", Constants.TABLE_NAME_PREFIX + Constants.TABLE_NAME_WT + 
				tenantId.substring(Constants.TENANT_ID_PREFIX.length()));
		config.put("wscTableName", Constants.TABLE_NAME_PREFIX + Constants.TABLE_NAME_WSC + 
				tenantId.substring(Constants.TENANT_ID_PREFIX.length()));*/
		//设置脚本生成账期
	    /*config.put("accountTime",TimeUtil.getStartTimeOfYesterday(Constants.YJQD_HZ_DATE_FORMAT));*/
		/*config.put("monthId",TimeUtil.getStartTimeOfYesterday(
				Constants.YJQD_HZ_DATE_FORMAT).substring(0, 6));
		config.put("dayId",TimeUtil.getStartTimeOfYesterday(
				Constants.YJQD_HZ_DATE_FORMAT).subSequence(6, 8));*/
		//设置字段成功标志
		config.put("successFlag",Constants.YJQD_BACKINFO_SUCCESS);
		config.put("falseFlag",Constants.YJQD_BACKINFO_FALSE);
		//设置pageSize
		config.put("pageSize", Integer.parseInt(PropertiesUtil.getConfig(Constants.HZ_PAGESIZE)));
		//设置TENANT_ID
		config.put("TENANT_ID", tenantId);
		//设置CHANNEL_ID
		config.put("CHANNEL_ID", channelId);
		//设置发送字段列表
		config.put("sendPrivateFieldList", PropertiesUtil.getConfig(Constants.FEEDBACK_SEND_PRIVATE_FIELDLIST).
				split(Constants.SEPARATOR));
		config.put("sendPublicFieldList", PropertiesUtil.getConfig(Constants.FEEDBACK_SEND_PUBLIC_FIELDLIST).
				split(Constants.SEPARATOR));
		//设置一级渠道字段前缀
		config.put("yjqdPrefix", Constants.LOCAL_YJQD_PREFIX);
		//设置公共字段前缀
		config.put("publicPrefix", Constants.LOCAL_PUBLIC_PREFIX);
		//设置一级渠道时间后缀
		/*config.put("yjqdDateSuffix", Constants.YJQD_HZ_DATE_ADD);*/
		//设置回执处理URL
		/*config.put("url", PropertiesUtil.getConfig(Constants.FEEDBACK_PROCESS_URL));*/
		config.put("url", globalCFGMapper.getGlobalCFG("ORDERCENTER_FEEDBACK"));
		
		//设置微信起始时间
		config.put("startTime",TimeUtil.getStartTimeOfYesterday(Constants.WX_HZ_DATE_FORMAT_AFTER));
		//设置微信结束时间
		config.put("endTime",TimeUtil.getStartTimeOfTaday(Constants.WX_HZ_DATE_FORMAT_AFTER));
		//设置微信渠道字段前缀
		config.put("wxPrefix", Constants.LOCAL_WX_PREFIX);
		config.put("wxHZDateFormatBefore", Constants.WX_HZ_DATE_FORMAT_BEFORE);
		config.put("wxHZDateFormatAfter", Constants.WX_HZ_DATE_FORMAT_AFTER);
		return config;
	}
	
	/* (non Javadoc)
	 * @Title: getYJQDPageBeanByProvIdAndAccountTime
	 * @Description: 查询一级渠道单个渠道回执数据总量
	 * @param request
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#getYJQDPageBeanByProvIdAndAccountTime(java.util.HashMap)
	 */
	@Override
	public PageBean getYJQDPageBeanByProvIdAndAccountTime(HashMap<String, Object> request) {
		
		/*log.info("getSTPageBeanByProvIdAndAccountTime的请求参数"+request.toString());*/
		//设置当前页
		Integer currentPage = 1;
		//获取回执总条数
		List<Integer> idList = feedBackInfoMapper.findYJQDCountByProvIdAndAccountTime(request);		
		pageBean.setCurrentPage(currentPage);
		pageBean.setTotal(idList.size());
		pageBean.setPageSize(Integer.parseInt(String.valueOf(request.get("pageSize"))));
		//设置idList
		request.put("idList", idList);
		/*log.info("getSTPageBeanByProvIdAndAccountTime的响应参数"+pageBean.toString());*/
		return pageBean;

	}
	
	/* (non Javadoc)
	 * @Title: findSTFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询手厅回执数据
	 * @param request
	 * @return
	 * @see com.bonc.busi.yjqdBackInfo.service.YJQDBackInfoService#findSTFeedBackByProvIdAndAccountTime(java.util.HashMap)
	 */
	@Override
	public List<HashMap<String, Object>> findSTFeedBackByProvIdAndAccountTime(HashMap<String, Object> request,
			List<Integer> subIdList) {
		
		/*log.info("findSTFeedBackByProvIdAndAccountTime的请求参数"+request.toString());*/
		List<HashMap<String, Object>> stFeedBackInfoList = 
				feedBackInfoMapper.findSTFeedBackByProvIdAndAccountTime(request,subIdList);
		/*log.info("findSTFeedBackByProvIdAndAccountTime的响应参数"+stFeedBackInfoList.toString());*/
		if(stFeedBackInfoList == null || stFeedBackInfoList.isEmpty() || stFeedBackInfoList.size()<1){
			return null;
		}
		return stFeedBackInfoList;
		
	}
	
	/* (non Javadoc)
	 * @Title: findWTFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询网厅回执数据
	 * @param request
	 * @return
	 * @see com.bonc.busi.yjqdBackInfo.service.YJQDBackInfoService#findWTFeedBackByProvIdAndAccountTime(java.util.HashMap)
	 */
	@Override
	public List<HashMap<String, Object>> findWTFeedBackByProvIdAndAccountTime(HashMap<String, Object> request,
			List<Integer> subIdList) {
		
		/*log.info("findWTFeedBackByProvIdAndAccountTime的请求参数"+request.toString());*/
		
		List<HashMap<String, Object>> wtFeedBackInfoList = 
				feedBackInfoMapper.findWTFeedBackByProvIdAndAccountTime(request,subIdList);
		/*log.info("findWTFeedBackByProvIdAndAccountTime的响应参数"+wtFeedBackInfoList.toString());*/
		if(wtFeedBackInfoList == null || wtFeedBackInfoList.isEmpty() || wtFeedBackInfoList.size()<1){
			return null;
		}
		return wtFeedBackInfoList;

	}
	
	/* (non Javadoc)
	 * @Title: findWSCTFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询沃视窗回执数据
	 * @param request
	 * @return
	 * @see com.bonc.busi.yjqdBackInfo.service.YJQDBackInfoService#findWSCTFeedBackByProvIdAndAccountTime(java.util.HashMap)
	 */
	@Override
	public List<HashMap<String, Object>> findWSCFeedBackByProvIdAndAccountTime(HashMap<String, Object> request,
			List<Integer> subIdList) {
		
		/*log.info("findWSCTFeedBackByProvIdAndAccountTime的请求参数"+request.toString());*/
		List<HashMap<String, Object>> wscFeedBackInfoList = 
				feedBackInfoMapper.findWSCFeedBackByProvIdAndAccountTime(request,subIdList);
		/*log.info("findWSCTFeedBackByProvIdAndAccountTime的响应参数"+wscFeedBackInfoList.toString());*/
		if(wscFeedBackInfoList == null || wscFeedBackInfoList.isEmpty() || wscFeedBackInfoList.size()<1){
			return null;
		}
		return wscFeedBackInfoList;

	}
	
	/* (non Javadoc)
	 * @Title: yjqdFeedBackFilter
	 * @Description: 一级渠道回执数据过滤
	 * @param yjqdFeedBackInfo
	 * @param request
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#yjqdFeedBackFilter(java.util.HashMap, java.util.HashMap)
	 */
	public HashMap<String, Object> yjqdFeedBackFilter(HashMap<String, Object> yjqdFeedBackInfo, 
			HashMap<String, Object> request){
		
		/*log.info("yjqdFeedBackFilter的请求参数"+yjqdFeedBackInfo.toString());*/
		//查看map是否为空或其中是否有null值
		if(!MapUtil.allFieldsIsNotNull(yjqdFeedBackInfo)){
			/*log.error("回执字段不完整=========================》");*/
			return null;
		}
		//判断下发字段是否完整
		String[] sendPrivateFieldList = (String[]) request.get("sendPrivateFieldList");
		for(String sendPrivateField:sendPrivateFieldList){
			if(yjqdFeedBackInfo.get(PropertiesUtil.getConfig(request.get("yjqdPrefix")+sendPrivateField)) == null){
				return null;
			}
		}
		/*log.info("yjqdFeedBackFilter的响应参数"+yjqdFeedBackInfo.toString());*/
		return yjqdFeedBackInfo;
		
	}
	
	
	/* (non Javadoc)
	 * @Title: yjqdTranslator
	 * @Description: 一级渠道字段名称转换
	 * @param yjqdFeedBack
	 * @param request
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#yjqdTranslator(java.util.HashMap, java.util.HashMap)
	 */
	public void yjqdTranslator(HashMap<String, Object> yjqdFeedBackInfo, HashMap<String, Object> request){
		
		//对map中回执状态进行更改
		yjqdFeedBackInfo.put("CONTACT_CODE",
				PropertiesUtil.getConfig(String.valueOf(yjqdFeedBackInfo.get("CONTACT_CODE"))));
		/*yjqdFeedBackInfo.put("ACCT_DATE", yjqdFeedBackInfo.get("ACCT_DATE")+String.valueOf(request.get("yjqdDateSuffix")));*/
		//map中键值对名字替换
		String[] sendPrivateFieldList = (String[]) request.get("sendPrivateFieldList");
		for(String sendPrivateField:sendPrivateFieldList){
			yjqdFeedBackInfo.put(sendPrivateField,String.valueOf(
					yjqdFeedBackInfo.get(PropertiesUtil.getConfig(request.get("yjqdPrefix")+sendPrivateField))));
			yjqdFeedBackInfo.remove(PropertiesUtil.getConfig(request.get("yjqdPrefix")+sendPrivateField));
		}
		
	}
	
	/* (non Javadoc)
	 * @Title: updateYJQDBack
	 * @Description: 更新一级渠道回执状态
	 * @param yjqdFeedBack
	 * @param request
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#updateYJQDBack(java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public Boolean updateYJQDBack(List<Integer> resultIdList, HashMap<String, Object> request) {
	
		Boolean result = false;
		Integer count = feedBackInfoMapper.updateYJQDBack(resultIdList,request);
		if(count != resultIdList.size()){
			return result;
		}
		result = true;
		return result;
		
	}
	
	/* (non Javadoc)
	 * @Title: getWXPageBeanByProvIdAndAccountTime
	 * @Description: 获得微信回执数据总量
	 * @param request
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#getWXPageBeanByProvIdAndAccountTime(java.util.HashMap)
	 */
	@Override
	public PageBean getWXPageBeanByProvIdAndAccountTime(HashMap<String, Object> request) {
		
		/*log.info("getWXPageBeanByProvIdAndAccountTime的请求参数"+request.toString());*/
		//设置当前页
		Integer currentPage = 1;
		//获取回执id列表
		List<Integer> idList = feedBackInfoMapper.findWXCountByTenantIdAndTouchTime(request);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Integer> newIdList = new ArrayList(new TreeSet(idList)); 
		pageBean.setCurrentPage(currentPage);
		pageBean.setTotal(idList.size());
		pageBean.setPageSize(Integer.parseInt(String.valueOf(request.get("pageSize"))));
		//设置idList
		request.put("idList", newIdList);
		/*log.info("getWXPageBeanByProvIdAndAccountTime的响应参数"+pageBean.toString());*/
		return pageBean;

	}

	/* (non Javadoc)
	 * @Title: findWXFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询微信回执数据
	 * @param request
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#findWXFeedBackByProvIdAndAccountTime(java.util.HashMap)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> findWXFeedBackByProvIdAndAccountTime(HashMap<String, Object> request) {
		
		/*log.info("findWXFeedBackByProvIdAndAccountTime的请求参数"+request.toString());*/
		List<HashMap<String, Object>> wxFeedBackInfoList = 
				feedBackInfoMapper.findWXFeedBackByTenantIdAndTouchTime(request,(List<Integer>) request.get("subIdList"));
		/*log.info("findWXFeedBackByProvIdAndAccountTime的响应参数"+wxFeedBackInfoList.toString());*/
		if(wxFeedBackInfoList == null || wxFeedBackInfoList.isEmpty() || wxFeedBackInfoList.size()<1){
			return null;
		}
		return wxFeedBackInfoList;
		
	}
	
	/* (non Javadoc)
	 * @Title: wxFeedBackFilter
	 * @Description: 微信回执数据过滤
	 * @param wxFeedBackInfo
	 * @param request
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#wxFeedBackFilter(java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public HashMap<String, Object> wxFeedBackFilter(HashMap<String, Object> wxFeedBackInfo,
			HashMap<String, Object> request) {
		
		/*log.info("wxFeedBackFilter的请求参数"+wxFeedBackInfo.toString());*/
		//查看map是否为空或其中是否有null值
		if(!MapUtil.allFieldsIsNotNull(wxFeedBackInfo)){
			/*log.error("回执字段不完整=========================》");*/
			return null;
		}
		//判断下发字段是否完整
		String[] sendPrivateFieldList = (String[]) request.get("sendPrivateFieldList");
		for(String sendPrivateField:sendPrivateFieldList){
			if(wxFeedBackInfo.get(PropertiesUtil.getConfig(request.get("wxPrefix")+sendPrivateField)) == null){
				return null;
			}
		}
		
		/*log.info("wxFeedBackFilter的响应参数"+wxFeedBackInfo.toString());*/
		return wxFeedBackInfo;

	}

	/* (non Javadoc)
	 * @Title: wxTranslator
	 * @Description: 微信渠道字段名称转换
	 * @param wxFeedBack
	 * @param request
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#wxTranslator(java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public void wxTranslator(HashMap<String, Object> wxFeedBackInfo, HashMap<String, Object> request) {
		/*System.out.println(wxFeedBackInfo.get("back_time").getClass().getName());*/
		//对map中回执时间键值对进行更改
		wxFeedBackInfo.put("back_time",TimeUtil.string2String(String.valueOf(request.get("wxHZDateFormatBefore")),
				String.valueOf(wxFeedBackInfo.get("back_time")),String.valueOf(request.get("wxHZDateFormatAfter"))));
		//私有字段名称转码
		String[] sendPrivateFieldList = (String[]) request.get("sendPrivateFieldList");
		for(String sendPrivateField:sendPrivateFieldList){
			wxFeedBackInfo.put(sendPrivateField,String.valueOf(
					wxFeedBackInfo.get(PropertiesUtil.getConfig(request.get("wxPrefix")+sendPrivateField))));
			wxFeedBackInfo.remove(PropertiesUtil.getConfig(request.get("wxPrefix")+sendPrivateField));
		}
		
	}
	
	/* (non Javadoc)
	 * @Title: updateWXBack
	 * @Description: 更新微信回执状态
	 * @param wxFeedBack
	 * @param request
	 * @return
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#updateWXBack(java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public Boolean updateWXBack(List<Integer> resultIdList, HashMap<String, Object> request) {
		
		Boolean result = false;
		Integer count = feedBackInfoMapper.updateWXBack(resultIdList,request);
		if(count != resultIdList.size()){
			log.error("weChat set status error!");
			return result;
		}
		result = true;
		return result;
		
	}
	
	/* (non Javadoc)
	 * @Title: sendFeedBackList
	 * @Description: 发送回执数据
	 * @param resultList
	 * @param request
	 * @see com.bonc.busi.feedBackInfo.service.FeedBackInfoService#sendFeedBackList(java.util.List, java.util.HashMap)
	 */
	@SuppressWarnings("unchecked")
	public void sendFeedBackList(List<HashMap<String, Object>> pama,HashMap<String, Object> request){
	
		try {
			HashMap<String, Object> req= new HashMap<String, Object>();
			req.put("pama", pama);
			req.put("TENANT_ID",request.get("TENANT_ID"));
			req.put("CHANNEL_ID",request.get("CHANNEL_ID"));
			//公共字段名称转码
			String[] sendPublicFieldList = (String[]) request.get("sendPublicFieldList");
			for(String sendPublicField:sendPublicFieldList){
				req.put(sendPublicField,String.valueOf(
						req.get(PropertiesUtil.getConfig(request.get("publicPrefix")+sendPublicField))));
				req.remove(PropertiesUtil.getConfig(request.get("publicPrefix")+sendPublicField));
			}
			
			/*log.error("本次发送参数为"+JSON.toJSONString(req));*/
			//调用数据处理接口
			String result = HttpUtil.sendPost(String.valueOf(request.get("url")),JSON.toJSONString(req));
			HashMap<String,String> resultMap = JSON.parseObject(result, HashMap.class);
			if(!(Constants.RESULT_CODE_SUCCESS).equals(resultMap.get(Constants.RESULT_CODE))){
				log.warn("Channel："+request.get("CHANNEL_ID")+" page "+pageBean.getCurrentPage()+
						" occurred error when calling contact interface，detail info："
			             +resultMap.get(Constants.RESULT_CODE)+":"+resultMap.get(Constants.RESULT_MESSAGE));
				return;
			}
			log.info("Channel："+request.get("CHANNEL_ID")+" page "+pageBean.getCurrentPage()+
					" success when calling contact interface，detail info："
		             +resultMap.get(Constants.RESULT_CODE)+":"+resultMap.get(Constants.RESULT_MESSAGE));
			return;
	    } catch (Exception e) {
			log.error("Channel：" + request.get("CHANNEL_ID")+" page "+pageBean.getCurrentPage()+
					"occurred error when calling contact interface");			
		}
		
	}
	
	/* (non Javadoc)
	 * @Title: findSTCountByProvIdAndAccountTime
	 * @Description: 获得手厅回执数据总量
	 * @param provId
	 * @param accountTime
	 * @return
	 * @see com.bonc.busi.yjqdBackInfo.service.yjqdBackInfoService#findSTCountByProvIdAndAccountTime(java.lang.String, java.lang.String)
	 */
	/*@Override
	public PageBean getSTPageBeanByProvIdAndAccountTime(HashMap<String, Object> request) {
		log.info("getSTPageBeanByProvIdAndAccountTime的请求参数"+request.toString());
		//设置表名
		request.put("tableName", request.get("stTableName"));
		//设置当前页
		Integer currentPage = 1;
		//获取回执总条数
		Integer count = feedBackInfoMapper.findSTCountByProvIdAndAccountTime(request);		
		pageBean.setCurrentPage(currentPage);
		pageBean.setTotal(count);
		pageBean.setPageSize(Integer.parseInt(String.valueOf(request.get("pageSize"))));
		log.info("getSTPageBeanByProvIdAndAccountTime的响应参数"+pageBean.toString());
		return pageBean;
	}*/

	

	/* (non Javadoc)
	 * @Title: getWTPageBeanByProvIdAndAccountTime
	 * @Description: 获得网厅回执数据总量
	 * @param request
	 * @return
	 * @see com.bonc.busi.yjqdBackInfo.service.YJQDBackInfoService#getWTPageBeanByProvIdAndAccountTime(java.util.HashMap)
	 */
	/*@Override
	public PageBean getWTPageBeanByProvIdAndAccountTime(HashMap<String, Object> request) {
		
		log.info("getWTPageBeanByProvIdAndAccountTime的请求参数"+request.toString());
		//设置表名
		request.put("tableName", request.get("wtTableName"));
		//设置当前页
		Integer currentPage = 1;
		//获取回执总条数
		Integer count = feedBackInfoMapper.findWTCountByProvIdAndAccountTime(request);		
		pageBean.setCurrentPage(currentPage);
		pageBean.setTotal(count);
		pageBean.setPageSize(Integer.parseInt(String.valueOf(request.get("pageSize"))));
		log.info("getWTPageBeanByProvIdAndAccountTime的响应参数"+pageBean.toString());
		return pageBean;
		
	}*/

	

	/* (non Javadoc)
	 * @Title: getWSCTPageBeanByProvIdAndAccountTime
	 * @Description: 获得沃视窗回执数据总量
	 * @param request
	 * @return
	 * @see com.bonc.busi.yjqdBackInfo.service.YJQDBackInfoService#getWSCTPageBeanByProvIdAndAccountTime(java.util.HashMap)
	 */
	/*@Override
	public PageBean getWSCPageBeanByProvIdAndAccountTime(HashMap<String, Object> request) {
		
		log.info("getWSCPageBeanByProvIdAndAccountTime的请求参数"+request.toString());
		//设置表名
		request.put("tableName", request.get("wscTableName"));
		//设置当前页
		Integer currentPage = 1;
		//获取回执总条数
		Integer count = feedBackInfoMapper.findWSCCountByProvIdAndAccountTime(request);		
		pageBean.setCurrentPage(currentPage);
		pageBean.setTotal(count);
		pageBean.setPageSize(Integer.parseInt(String.valueOf(request.get("pageSize"))));
		log.info("getWSCPageBeanByProvIdAndAccountTime的响应参数"+pageBean.toString());
		return pageBean;
		
	}*/

}
