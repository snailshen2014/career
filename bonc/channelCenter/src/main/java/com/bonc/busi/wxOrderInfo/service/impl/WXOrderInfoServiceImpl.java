/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: WXOrderInfoServiceImpl.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.wxOrderInfo.service.impl
 * @Description: 微信工单服务接口实现类
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月10日 下午9:27:21
 * @version: V1.0  
 */

package com.bonc.busi.wxOrderInfo.service.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.entity.PageBean;
import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;
import com.bonc.busi.wxOrderInfo.mapper.WXOrderInfoMapper;
import com.bonc.busi.wxOrderInfo.po.WXOrderInfo;
import com.bonc.busi.wxOrderInfo.service.WXOrderInfoService;
import com.bonc.busi.wxProductInfo.mapper.WXProductInfoMapper;
import com.bonc.busi.wxProductInfo.po.WXProductInfo;
import com.bonc.common.datasource.TargetDataSource;
import com.bonc.task.synccode.SyncCode;
import com.bonc.utils.BusiTools;
import com.bonc.utils.Constants;
import com.bonc.utils.FieldUtil;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.MapUtil;
import com.bonc.utils.PropertiesUtil;
import com.bonc.utils.TimeUtil;

/**
 * @ClassName: WXOrderInfoServiceImpl
 * @Description: 微信工单服务接口实现类
 * @author: LiJinfeng
 * @date: 2016年12月10日 下午9:27:21
 */
/**
 * @ClassName: WXOrderInfoServiceImpl
 * @Description: 微信工单服务接口实现类
 * @author: LiJinfeng
 * @date: 2017年2月21日 下午5:20:04
 */
@Service("wxOrderInfoService")
public class WXOrderInfoServiceImpl implements WXOrderInfoService{
	
	@Autowired
    private WXOrderInfoMapper wxOrderInfoMapper;
	
	@Autowired
    private WXProductInfoMapper wxProductInfoMapper;
	
	@Autowired
    private SyncCode syncCode;
	
	@Autowired
	private GlobalCFGMapper globalCFGMapper;
	
	@Autowired
	private BusiTools busiTools;
	
	@Autowired
	private PageBean pageBean;
	
	private static Log log = LogFactory.getLog(WXOrderInfoServiceImpl.class);

	/* (non Javadoc)
	 * @Title: getConfig
	 * @Description: 设置所需常量
	 * @param wxChannelId
	 * @param tenantId
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#getConfig(java.lang.String, java.lang.String)
	 */
	public HashMap<String, Object> getConfig(String wxChannelId,String tenantId){
		//请求参数map
		HashMap<String, Object> config = new HashMap<String, Object>();
		
		config.put("tenantId", tenantId);
		config.put("channelId", wxChannelId);
		config.put("orderStatusReady",Constants.ORDER_STATUS_READY);
		config.put("channelStatusReady",Constants.CHANNEL_STATUS_READY);
		config.put("pageSize", PropertiesUtil.getConfig(Constants.WX_SENT_PAGESIZE));
		config.put("insertSize", Integer.parseInt(PropertiesUtil.getConfig(Constants.WX_INSERT_SIZE)));
		config.put("wxOrderInfoFieldList", Arrays.asList(
				busiTools.getGlobalValue(Constants.WX_ORDER_INFO_FIELDS).split(Constants.SEPARATOR)));
		config.put("wxChannelStatusSendFail",Constants.WX_CHANNEL_STATUS_SEND_FAIL);
		config.put("wxChannelStatusSendSuccess",Constants.WX_CHANNEL_STATUS_SEND_SUCCESS);
		/*config.put("dxChannelStatusMutex",Constants.DX_CHANNEL_STATUS_MUTEX);*/
		config.put("publicId",Constants.WX_PUBLIC_PUBLICID);
		config.put("publicCode", Constants.WX_PUBLIC_PUBLICCODE);
		config.put("weChatStatus", Constants.WX_PUBLIC_STATUS);
		config.put("wxProductInfoFieldBssList",Arrays.asList(
				busiTools.getGlobalValue(Constants.WX_PRODUCT_INFO_FIELDS_BSS).split(Constants.SEPARATOR)));
		config.put("wxProductInfoFieldCbssList",Arrays.asList(
				busiTools.getGlobalValue(Constants.WX_PRODUCT_INFO_FIELDS_CBSS).split(Constants.SEPARATOR)));
		config.put("wxPublicFollow",Constants.WX_PUBLIC_FOLLOW);
		config.put("sysTimeFormat",Constants.SYS_TIME_FORMAT);
		
		config.put("netTypeTwPost",Constants.NET_TYPE_TW_POST);
		config.put("netTypeTwPre",Constants.NET_TYPE_TW_PRE);
		config.put("netTypeThPost",Constants.NET_TYPE_TH_POST);
		config.put("netTypeThPre",Constants.NET_TYPE_TH_PRE);
		config.put("netTypeFoAll",Constants.NET_TYPE_FO_ALL);
		
		config.put("netTypeTw",Constants.NET_TYPE_TW);
		config.put("netTypeTh",Constants.NET_TYPE_TH);
		config.put("netTypeFo",Constants.NET_TYPE_FO);
		config.put("netTypePost",Constants.NET_TYPE_POST);
		config.put("netTypeZPre", Constants.NET_TYPE_Z_PRE);
		config.put("netTypePre",Constants.NET_TYPE_PRE);
		
		config.put("activityNoProduct",Constants.ACTIVITY_NO_PRODUCT);
		config.put("activityYesProduct",Constants.ACTIVITY_YES_PRODUCT);
		config.put("activityChannelReadyStatus",Constants.ACTIVITY_CHANNEL_READY_STATUS);
		config.put("activityChannelSuccessStatus",Constants.ACTIVITY_CHANNEL_SUCCESS_STATUS);
		config.put("wxActivityInfoFieldList",Arrays.asList(
				busiTools.getGlobalValue(Constants.WX_ACTIVITY_INFO_FIELDS).split(Constants.SEPARATOR)));
		config.put("activityStatusList",Constants.ACTIVITY_STATUS_LIST);
		
		config.put("mysqlDateFormat", Constants.WX_HZ_DATE_FORMAT_AFTER);
		config.put("keyList",Arrays.asList(String.valueOf(PropertiesUtil.getConfig("fieldList.field.list")).
				split(Constants.SEPARATOR)));
		config.put("fieldConstant", Constants.FIELD_CONSTANT);
		config.put("fieldOI",Constants.FIELD_OI);
		config.put("fieldUL",Constants.FIELD_UL);
		
		config.put("remark", Constants.WX_REMARK);
		
		//加载转化字段列表
		config.put("realFlowList",Arrays.asList(
				busiTools.getGlobalValue(Constants.REALFLOW_VARIABLE).split(Constants.SEPARATOR)));
		config.put("realProductList",Arrays.asList(
				busiTools.getGlobalValue(Constants.REALPRODUCT_VARIABLE).split(Constants.SEPARATOR)));
		/*config.put("remarkList",Arrays.asList(
				busiTools.getGlobalValue(Constants.REMARK_VAR).split(Constants.SEPARATOR)));*/
		config.put("feeList",Arrays.asList(
				busiTools.getGlobalValue(Constants.FEE_VAR).split(Constants.SEPARATOR)));
		config.put("stringDateDateList",Arrays.asList(
				busiTools.getGlobalValue(Constants.STRING_DATE_DATE_VARIABLE).split(Constants.SEPARATOR)));
		config.put("dateList",Arrays.asList(
				busiTools.getGlobalValue(Constants.DATE_VAR).split(Constants.SEPARATOR)));
		config.put("stringMonthDateList",Arrays.asList(
				busiTools.getGlobalValue(Constants.STRING_MONTH_DATE_VARIABLE).split(Constants.SEPARATOR)));
		config.put("stringSecondDateList",Arrays.asList(
				busiTools.getGlobalValue(Constants.STRING_SECOND_DATE_VARIABLE).split(Constants.SEPARATOR)));
		config.put("percentList",Arrays.asList(
				busiTools.getGlobalValue(Constants.PERCENT_VARIABLE).split(Constants.SEPARATOR)));
		config.put("valueList",Arrays.asList(
				busiTools.getGlobalValue(Constants.VALUE_VAR).split(Constants.SEPARATOR)));
		//加载产品字段映射JSON字符串
		config.put("productFieldJson",busiTools.getGlobalValue(Constants.PRODUCT_FIELD_JSON));
		//加载环境标识变量
		config.put("environmentFlag", busiTools.getGlobalValue(Constants.ENVIRONMENT_FLAG));
		//加载话术字段映射列表,分两个地方使用，且第一次使用要进行修改
		/*List<HashMap<String, Object>> remarkCodeTable = 
				codeTableMapper.getCodeListByFieldName(tenantId,Constants.TALK_VAR);
		config.put("remarkCodeTable", remarkCodeTable);
		List<HashMap<String, Object>> remarkCodeList = 
				codeTableMapper.getCodeListByFieldName(tenantId,Constants.TALK_VAR);
		config.put("remarkCodeList", remarkCodeList);*/
		//加载实时接口URL
		config.put("realFlowUrl",globalCFGMapper.getGlobalCFG(Constants.XBUILDERORACLE_REALFLOW));
		config.put("realProductUrl",globalCFGMapper.getGlobalCFG(Constants.XBUILDERORACLE_REALPRODUCT));
		//加载话术变量接口URL
		config.put("actHuaShu",globalCFGMapper.getGlobalCFG(Constants.ACTIVITYINTER_ACTHUASHU));
		//加载产品接口URL
		config.put("productUrl",globalCFGMapper.getGlobalCFG(Constants.ACTIVITYINTER_PRODUCTINFO));
		return config;
		
	}
	
	/* (non Javadoc)
	 * @Title: insertLog
	 * @Description: 记录日志
	 * @param wxActivityInfo
	 * @param logMessage
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#insertLog(com.bonc.busi.wxActivityInfo.po.WXActivityInfo, java.lang.String)
	 */
	public void insertLog(WXActivityInfo wxActivityInfo,String logMessage){
		
		Integer count = globalCFGMapper.getCountLogByActivitySeqID(wxActivityInfo.getRecId());
		if(count != 0){
			//更新日志
			globalCFGMapper.updateLogByActivitySeqID(wxActivityInfo, logMessage, new Date(), 
					TimeUtil.formatSystemTime(Constants.YJQD_HZ_DATE_FORMAT));
		}
		else{
			//插入日志
			globalCFGMapper.insertLog(wxActivityInfo, logMessage, new Date(), 
					TimeUtil.formatSystemTime(Constants.YJQD_HZ_DATE_FORMAT));
		}
	
	}
	
	/* (non Javadoc)
	 * @Title: findActivityList
	 * @Description: 查找活动列表
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#findActivityList(java.util.HashMap)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<WXActivityInfo> findActivityList(HashMap<String, Object> config) {
		
		long start = System.currentTimeMillis();
		List<WXActivityInfo> findActivityList = wxOrderInfoMapper.findActivityList(config,
				(List<Integer>) config.get("activityStatusList"));
		long end = System.currentTimeMillis();
		log.info("time of finding activityList："+(end-start)/1000.0+"s");
		
		if(findActivityList == null || findActivityList.isEmpty() || findActivityList.size()<1){
			return null;
		}
		return findActivityList;
		
	}

	/* (non Javadoc)
	 * @Title: wxActivityInfoFieldIsEmpty
	 * @Description: 校验wxActivityInfo的各字段是否为空
	 * @param wxActivityInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#wxActivityInfoFieldIsEmpty(com.bonc.busi.wxActivityInfo.po.WXActivityInfo, java.util.HashMap)
	 */
	@SuppressWarnings("unchecked")
    public Boolean wxActivityInfoFieldIsEmpty(WXActivityInfo wxActivityInfo, HashMap<String, Object> config) {
		
		Boolean result = false;
		result = FieldUtil.someFieldIsNotEmpty(wxActivityInfo,(List<String>) config.get("wxActivityInfoFieldList"));
		return result;
		
	}
	
	/* (non Javadoc)
	 * @Title: webChatInfoFormat
	 * @Description: 校验微信公众号信息
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#webChatInfoFormat(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	@Override
	public Boolean webChatInfoFormat(WXActivityInfo wxActivityInfo, HashMap<String, Object> config) {
		
		Boolean result = false;
		String webChatInfo = wxActivityInfo.getWebChatInfo();
		@SuppressWarnings("unchecked")
		HashMap<String,Object> webChatInfoMap = JSON.parseObject(webChatInfo, HashMap.class);
		if(webChatInfoMap == null || webChatInfoMap.isEmpty() || webChatInfoMap.size()<1){			
			return result;
		}
		if(!webChatInfoMap.containsKey(config.get("publicId")) || 
				!webChatInfoMap.containsKey(config.get("publicCode")) ){	
			return result;
		}
		String publicId = String.valueOf(webChatInfoMap.get(config.get("publicId")));
		String publicCode = String.valueOf(webChatInfoMap.get(config.get("publicCode")));
		if(StringUtils.isBlank(publicId) || StringUtils.isBlank(publicCode)){	
			return result;
		}
		wxActivityInfo.setPublicId(publicId);
		wxActivityInfo.setPublicCode(publicCode);
		String templateId = wxOrderInfoMapper.findTemplateIdByActivityId(config,wxActivityInfo.getActivityId(),
				String.valueOf(config.get("weChatStatus")));
		if(StringUtils.isBlank(templateId)){
			return result;
		}
		wxActivityInfo.setTemplateId(templateId);
		result = true;
		return result;
		
	}
	
	/* (non Javadoc)
	 * @Title: findFieldList
	 * @Description: 获取模板对应的变量名，字段名
	 * @param wxActivityInfo
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#findFieldList(com.bonc.busi.wxActivityInfo.po.WXActivityInfo)
	 */
	@Override
	public Boolean findFieldList(WXActivityInfo wxActivityInfo,HashMap<String, Object> config){
		
		Boolean result = false;
		List<HashMap<String, Object>> fieldList = wxOrderInfoMapper.findFieldList(wxActivityInfo);
		if(fieldList == null || fieldList.isEmpty() || fieldList.size() < 1){
			return result;
		}
		//映射TEM_VAR_CONTENT与TEM_VAR_VALUE
		HashMap<String, String> content2Value = new HashMap<String, String>();
		//获取列名
		@SuppressWarnings("unchecked")
		List<String> keyList =  (List<String>) config.get("keyList");
		for(HashMap<String,Object> field:fieldList){
			
			boolean someFieldsIsNotNull = MapUtil.someFieldsIsNotNull(field, keyList);
			if(!someFieldsIsNotNull){
				return result;
			}	
			
			String dataSourceType = String.valueOf(field.get(keyList.get(2)));
			String fieldValue = String.valueOf(field.get(keyList.get(1)));
			String fieldContent = String.valueOf(field.get(keyList.get(0)));
			//若变量来源为常量
			if(dataSourceType.equals(String.valueOf(config.get("fieldConstant")))){
				field.put("sqlField","'"+fieldValue+"'");
			}
			else if(dataSourceType.equals(String.valueOf(config.get("fieldOI")))){
				field.put("sqlField","oi."+fieldValue);
				content2Value.put(fieldContent, fieldValue);
			}
			else if(dataSourceType.equals(String.valueOf(config.get("fieldUL")))){
				field.put("sqlField","ul."+fieldValue);
				content2Value.put(fieldContent, fieldValue);
			}
			else{
				return result;
			}
			
		}
		wxActivityInfo.setFieldList(fieldList);
		wxActivityInfo.setFieldMap(content2Value);
		result = true;
		return result;
		
		
	}
	
	/* (non Javadoc)
	 * @Title: productIdListIsEmpty
	 * @Description: 判断活动对应的产品列表是否为空
	 * @param wxOrderInfo
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#productIdListIsEmpty(com.bonc.busi.wxOrderInfo.po.WXOrderInfo)
	 */
	@Override
	public Boolean productIdListIsEmpty(WXActivityInfo wxActivityInfo, HashMap<String, Object> config) {
		
		Boolean result = false;
		//获取活动对应的产品ID列表
		List<String> productIdList = wxOrderInfoMapper.findProductIdListByAvtivityId(wxActivityInfo);		
		//判断产品列表是否为空
		if(productIdList == null || productIdList.isEmpty() || productIdList.size()<1){
			//若活动关联的产品为空，则不关联产品ID
			wxActivityInfo.setProductFlag((Integer) config.get("activityNoProduct"));	
			return result;
		}
		//若有，则关联产品ID
		wxActivityInfo.setProductFlag((Integer) config.get("activityYesProduct"));	
		wxActivityInfo.setProductIdList(productIdList);
		result = true;
		return result;
		
	}
	
	
	/* (non Javadoc)
	 * @Title: productIsExistInRomote
	 * @Description: 判断产品在远程产品表中是否存在
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#productIsExistInRomote(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	@Override
	public Boolean productIsExistInRomote(WXActivityInfo wxActivityInfo, HashMap<String, Object> config) {
		
		Boolean result = false;
		//获取远程产品信息
		Boolean productInfoListFromRemote = false;
		try {
			productInfoListFromRemote = getProductInfoListFromRemote(wxActivityInfo, config);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ActivityId:"+ wxActivityInfo.getActivityId() +
					" occurred exception when calling interface of productInfo!");
			return result;
		}
		if(!productInfoListFromRemote){		
			return result;		
		}
		/*for(String elementId:wxActivityInfo.getProductIdList()){
			
			WXProductInfo productInfo = wxProductInfoMapper.findProductByProductId
					(elementId,String.valueOf(config.get("tenantId")));
			if(productInfo == null){
				continue;
			}
			productInfo.setTenantId(String.valueOf(config.get("tenantId")));
			wxActivityInfo.getProductInfoList().add(productInfo);
		}*/
		//若所有产品都找不到
		if(wxActivityInfo.getProductInfoList().size() < 1){
			return result;
		}
		result = true;
		return result;
		
	}
	
	/**
	 * @Title: getProductInfoListFromRemote
	 * @Description: 调接口获取活动对应产品信息列表
	 * @return: Boolean
	 * @param wxActivityInfo
	 * @param config
	 * @return
	 * @throws: 
	 */
	private Boolean getProductInfoListFromRemote(WXActivityInfo wxActivityInfo,
			HashMap<String, Object> config){
		
		Boolean flag = false;
		String url = (String) config.get("productUrl");
		/*String url = "http://clyxys.yz.local:8080/activityInter/activity/productInfo";*/
		//设置参数
    	String tanantId = (String) config.get("tenantId");
    	HashMap<String, Object> parameter = new HashMap<String,Object>();
    	parameter.put("tenantId", tanantId);
    	//设置elementIds
    	StringBuffer elementIds = new StringBuffer();
    	for(String elementId:wxActivityInfo.getProductIdList()){
    		
    		elementIds.append(elementId);
    		elementIds.append(",");
    		
    	}
    	elementIds.deleteCharAt(elementIds.length()-1);
    	parameter.put("productCode", elementIds);
    	log.info("ActivityId:"+ wxActivityInfo.getActivityId() +
    			" the parameter of calling interface of productInfo："+parameter.toString());
    	//获取产品信息
    	String result = HttpUtil.doGet(url, parameter);
    	log.info("ActivityId:"+ wxActivityInfo.getActivityId() +
    			" the result of calling interface of productInfo："+result);
    	@SuppressWarnings("rawtypes")
		List<HashMap> resultMapList = JSON.parseArray(result, HashMap.class);
    	if(resultMapList == null || resultMapList.isEmpty()){
    		log.info("ActivityId:"+ wxActivityInfo.getActivityId() + 
    				"the result of calling interface of productInfo is null！");
    		return flag;
    	}
    	//List<Map>的key值转化
    	String productField = config.get("productFieldJson").toString();
    	@SuppressWarnings("unchecked")
		HashMap<String,String> keyMap = JSON.parseObject(productField, HashMap.class);
    	MapUtil.convertMapKey(resultMapList, keyMap);
    	//将map转化为bean
    	for(@SuppressWarnings("rawtypes") HashMap resultMap:resultMapList){
    		WXProductInfo wxProductInfo = new WXProductInfo();
    		MapUtil.hashMap2Bean(resultMap,wxProductInfo);
    		log.info("ActivityId:"+ wxActivityInfo.getActivityId() + " product detail info:"
    				+wxProductInfo.toString());
    		wxActivityInfo.getProductInfoList().add(wxProductInfo);
    	}
    	flag = true;
		return flag;
		
	}
	
	/* (non Javadoc)
	 * @Title: productFieldIsEmpty
	 * @Description: 判断产品列表各字段是否为空
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#productFieldIsEmpty(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	@SuppressWarnings("unchecked")
	public Boolean productFieldIsEmpty(WXActivityInfo wxActivityInfo, HashMap<String, Object> config){
		Boolean result = false;
		//判断产品对象的相应字段是否为空
		Iterator<WXProductInfo> iterator = wxActivityInfo.getProductInfoList().iterator();
		while(iterator.hasNext()){
			WXProductInfo productInfo = iterator.next();
			//需区分bss与cbss
			//bss
			if(productInfo.getNetType() != null && Constants.NET_TYPE_BSS.equals(productInfo.getNetType())){
				result = FieldUtil.someFieldIsNotEmpty(productInfo,
						(List<String>) config.get("wxProductInfoFieldBssList"));
			}
			//cbss
			if(productInfo.getNetType() != null && Constants.NET_TYPE_CBSS.equals(productInfo.getNetType())){
				result = FieldUtil.someFieldIsNotEmpty(productInfo,
						(List<String>) config.get("wxProductInfoFieldCbssList"));
			}
			//若不符合，移除
			if(!result){	
				iterator.remove();
				/*return result;*/
			}
			//若符合
			else{
				//若为BSS,补全订购编码
				if(Constants.NET_TYPE_BSS.equals(productInfo.getNetType())){
					productInfo.setSAPId(Constants.BSS_PREFIX+productInfo.getElementId());
					productInfo.setSAPName(productInfo.getElementName());
					/*productInfo.setSAPId(Constants.BSS_PREFIX+productInfo.getSAPId());*/
				}
			}
		}
		//若所有产品都字段不完整
		if(wxActivityInfo.getProductInfoList().size()<1){
			return result;
		}
		result = true;
		return result;
		
	}
	
	/* (non Javadoc)
	 * @Title: flowTypeIsEquals
	 * @Description: 判断多个产品是否为同一类型
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#flowTypeIsEquals(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	/*@Override
	public Boolean flowTypeIsEquals(WXActivityInfo wxActivityInfo, HashMap<String, Object> config) {
		
		Boolean result = false;
		List<WXProductInfo> productInfoList = wxActivityInfo.getProductInfoList();
		int flowType = productInfoList.get(0).getFlowType();
		//依次获取productId，判断是否为同一类型
		for(WXProductInfo productInfo:productInfoList){
			int flowType1 = productInfo.getFlowType();
			if(flowType != flowType1){
				return result;
			}
		}
		result = true;
		return result;
		
	}*/
	
	/* (non Javadoc)
	 * @Title: updateWXProductInfo
	 * @Description: 批量更新本地产品码表
	 * @param wxOrderInfo
	 * @param config
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#updateWXProductInfo(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	@Transactional
	public Boolean updateWXProductInfo(List<WXProductInfo> productInfoList, HashMap<String, Object> config){
		
		Boolean result = false;
		for(WXProductInfo productInfo:productInfoList){
			Integer count = 0;
			Integer productCount = wxProductInfoMapper.findWXProductInfoByProductId
					(productInfo.getSAPId(),String.valueOf(config.get("tenantId")));
			//本地产品表无此产品，插入
			if(productCount == 0 ){
				count = wxProductInfoMapper.insertWXProductInfo(productInfo);			
			}
			//本地产品表有此产品，更新
			else{
				count = wxProductInfoMapper.updateWXProductInfoByProductId(productInfo);	
			}
			if(count != 1){
				return result;
			}
		}
		result = true;
		return result;
		
	}
	
	/* (non Javadoc)
	 * @Title: setProductId
	 * @Description: 设置活动关联产品的相关ID
	 * @param wxActivityInfo
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#setProductId(com.bonc.busi.wxActivityInfo.po.WXActivityInfo)
	 */
	@Override
	public Boolean setProductIds(WXActivityInfo wxActivityInfo,HashMap<String, Object> config) {
		
		Boolean result = false;
		//获取前三个产品的产品信息
		if(wxActivityInfo.getProductInfoList().size()>3){
			wxActivityInfo.setProductInfoList(wxActivityInfo.getProductInfoList().subList(0, 3));
		}
		try {
			wxActivityInfo.setOrderProductId(wxActivityInfo.getProductInfoList());
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		result = true;
		return result;
		
	}

	/* (non Javadoc)
	 * @Title: getOrdereRecIdList
	 * @Description: 获取所有微信工单的REC_ID列表
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#getOrdereRecIdList(java.util.HashMap)
	 */
	
	@Override
	@TargetDataSource(name = "mysqlslaveuni076")
	public List<Integer> getOrdereRecIdList(WXActivityInfo wxActivityInfo,HashMap<String, Object> config) {
		
		//设置当前页
		Integer currentPage = 1;
		//获取回执总条数
		long start = System.currentTimeMillis();
		List<Integer> ordereRecIdList = wxOrderInfoMapper.getOrdereRecIdList(wxActivityInfo,config);	
		long end = System.currentTimeMillis();
		log.info("ActivityId:"+wxActivityInfo.getActivityId()+" time of select weChat order："+(end-start)/1000.0+"s,"
				+ "number:"+ordereRecIdList.size());
		//设置pageBean相关参数
		pageBean.setCurrentPage(currentPage);
		pageBean.setTotal(ordereRecIdList.size());
		pageBean.setPageSize(Integer.parseInt(String.valueOf(config.get("pageSize"))));
		//将pageBean放入config中
		config.put("pageBean", pageBean);
		return ordereRecIdList;

	}

	/* (non Javadoc)
	 * @Title: findWXOrderInfoDataByChannelId
	 * @Description: 分页查询微信工单
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#findWXOrderInfoDataByChannelId(java.util.HashMap)
	 */
	@Override
	@SuppressWarnings("unchecked")
	@TargetDataSource(name = "mysqlslaveuni076")
	public List<HashMap<String, Object>> findWXOrderInfoListByChannelId(HashMap<String, Object> config,
			WXActivityInfo wxActivityInfo) {
		
		/*log.info("findWXOrderInfoDataByChannelId的请求参数"+config.toString());*/
		//设置起始页
		config.put("startPage", pageBean.getStartPage());
		//设置每页大小
		config.put("pageSize", pageBean.getPageSize());
		List<HashMap<String, Object>> wxOrderInfoMapList = new ArrayList<HashMap<String, Object>>();
		//获取话术变量对应用户标签表字段
		List<String> mysqlFieldList = (List<String>) config.get("mysqlFieldList");
		//分页查询微信工单
		long start = System.currentTimeMillis();
		try {
			wxOrderInfoMapList = wxOrderInfoMapper.findWXOrderInfoListByChannelId(config,
					(List<Integer>) config.get("ordereRecIdList"), wxActivityInfo, wxActivityInfo.getFieldList(),
					(List<Integer>) config.get("activityStatusList"), mysqlFieldList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ActivityId:"+wxActivityInfo.getActivityId()+" occurred exception when select weChat order!");
		}
		long end = System.currentTimeMillis();
		log.info("ActivityId:"+wxActivityInfo.getActivityId()+" page "+pageBean.getCurrentPage()+
				" time of select weChat order："+(end-start)/1000.0+"s,number："+wxOrderInfoMapList.size());
		/*log.info("findWXOrderInfoDataByChannelId的响应参数"+wxOrderInfoList.toString());*/
		if(wxOrderInfoMapList == null || wxOrderInfoMapList.isEmpty() || wxOrderInfoMapList.size()<1){
			return null;
		}
		return wxOrderInfoMapList;
		
	}
	
	public Boolean getCurrentPartitionFlag(HashMap<String, Object> config){
		
		Boolean result = false;
		String partitionFlag = globalCFGMapper.getGlobalCFG("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
		if(StringUtils.isBlank(partitionFlag)){
			log.info("occurred error when obtain partition flag");
		    return result; 
		}
		config.put("partitionFlag",Integer.parseInt(partitionFlag));
		result = true;
		return result;
		
	}

	/* (non Javadoc)
	 * @Title: WXOrderInfoFieldIsEmpty
	 * @Description: 校验wxOrderInfo的各字段是否为空
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#WXOrderInfoFieldIsEmpty(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Boolean wxOrderInfoFieldIsEmpty(HashMap<String,Object> wxOrderInfoMap, HashMap<String, Object> config,
			WXActivityInfo wxActivityInfo) {
		
		Boolean result = false;
		List<String> wxOrderInfoMapFieldList = (List<String>) config.get("wxOrderInfoFieldList");
		wxOrderInfoMapFieldList = new ArrayList<String>(wxOrderInfoMapFieldList);
		/*for(HashMap<String, Object> field:wxActivityInfo.getFieldList()){		
			wxOrderInfoMapFieldList.add(String.valueOf(field.get(((List<String>)config.get("keyList")).get(0))));		
		}*/
		//不验证生效时间、失效时间
		wxOrderInfoMapFieldList.remove("startTime");
		wxOrderInfoMapFieldList.remove("endTime");
		result = MapUtil.someFieldsIsNotNull(wxOrderInfoMap, wxOrderInfoMapFieldList);
		return result;
		
	}
	
	
    
    /* (non Javadoc)
     * @Title: analyzeWXOrderInfoMap
     * @Description: 分解wxOrderInfoMap的键值对，生成wxOrderInfo和fieldInfo
     * @param wxOrderInfoMap
     * @param config
     * @param wxActivityInfo
     * @param wxOrderInfo
     * @return
     * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#analyzeWXOrderInfoMap(java.util.HashMap, java.util.HashMap, com.bonc.busi.wxActivityInfo.po.WXActivityInfo, com.bonc.busi.wxOrderInfo.po.WXOrderInfo)
     */
    @Override
    public Boolean analyzeWXOrderInfoMap(HashMap<String,Object> wxOrderInfoMap, HashMap<String, Object> config,
    		WXActivityInfo wxActivityInfo,WXOrderInfo wxOrderInfo){
    	
    	Boolean result = false;
    	HashMap<String,Object> wxOrderMap = new HashMap<String,Object>();
    	@SuppressWarnings("unchecked")
		List<String> wxOrderInfoFieldList = (List<String>) config.get("wxOrderInfoFieldList");
    	//筛选属于WXOrderInfo对象的字段放到wxOrderMap
    	for(String wxOrderInfoField:wxOrderInfoFieldList){	    		
    		if(wxOrderInfoMap.containsKey(wxOrderInfoField)){
    			wxOrderMap.put(wxOrderInfoField,wxOrderInfoMap.get(wxOrderInfoField));
    			wxOrderInfoMap.remove(wxOrderInfoField);
    			
    		}	
    	}
    	//移除多余的productId字段
    	if(wxOrderInfoMap.containsKey("productId")){
			wxOrderMap.put("productId",wxOrderInfoMap.get("productId"));
			wxOrderInfoMap.remove("productId");
			
		}	
    	//微信话术转码
    	String remark = String.valueOf(wxOrderInfoMap.get(String.valueOf(config.get("remark"))));
    	@SuppressWarnings("unchecked")
		List<HashMap<String, String>> allTalkVarList = (List<HashMap<String, String>>) config.get("allTalkVarList");
		//存放本次实时接口请求字段列表
		List<String> realList = null;
		//存放本次实时接口请求URL
		String realUrl = null;
		//存放本次实时接口名字
		String realName = null;
		//存放本次所有实时变量
		HashMap<String, Object> allRealValue  = new HashMap<String, Object>();
		for(HashMap<String, String> remarkMap:allTalkVarList){
			if(!remark.contains(remarkMap.get(Constants.FIELD_KEY))){
				//删除话术字段
				wxOrderInfoMap.remove(remarkMap.get(Constants.FIELD_VALUE));
				continue;
			}
			//实时变量替换
			//话术中实时流量接口请求字段
			@SuppressWarnings("unchecked")
			List<String> realFlowList=(List<String>) config.get("realFlowList");
			if(realFlowList.contains(remarkMap.get(Constants.FIELD_VALUE))){
				realList = realFlowList;
				realUrl = String.valueOf(config.get("realFlowUrl"));
				realName = Constants.XBUILDERORACLE_REALFLOW;
			}
			//话术中实时套餐接口请求字段
			@SuppressWarnings("unchecked")
			List<String> realProductList=(List<String>) config.get("realProductList");
			if(realProductList.contains(remarkMap.get(Constants.FIELD_VALUE))){
				realList = realProductList;
				realUrl = String.valueOf(config.get("realProductUrl"));
				realName = Constants.XBUILDERORACLE_REALPRODUCT;
			}
			if(realList != null){
				//存放本次请求实时变量
				HashMap<String, Object> realValue = null;
				if(allRealValue.get(remarkMap.get(Constants.FIELD_VALUE)) == null){
					//获取实时流量数据
					realValue = getRealValue(String.valueOf(wxOrderMap.get("telInt")),
							Integer.parseInt(String.valueOf(wxOrderMap.get("netType"))),
							realUrl,realName);
					if(realValue == null){
						log.warn("ActivityId:"+wxActivityInfo.getActivityId()+
								" the result of calling real value is null！");
						//常量替换
					    remark = remark.replace(remarkMap.get(Constants.FIELD_KEY),Constants.NULL_PROMPT);
					    realList = null;
					    continue;
					}
					else{
						allRealValue.putAll(realValue);
					}
				}
				remark = remark.replace(remarkMap.get(Constants.FIELD_KEY),
						String.valueOf(allRealValue.get(remarkMap.get(Constants.FIELD_VALUE))));
				realList = null;
				continue;
			}
			//获取话术变量值
			String fieldValue = String.valueOf(wxOrderInfoMap.get(remarkMap.get(Constants.FIELD_VALUE)));
			if(StringUtils.isBlank(fieldValue) || "null".equals(fieldValue)){
				//常量替换
				remark = remark.replace(remarkMap.get(Constants.FIELD_KEY),Constants.NULL_PROMPT);
				wxOrderInfoMap.remove(remarkMap.get(Constants.FIELD_VALUE));
				continue;
			}
			//话术中需转码的字段
			@SuppressWarnings("unchecked")
			List<String> valueList = (List<String>) config.get("valueList");
			if(valueList.contains(remarkMap.get(Constants.FIELD_VALUE))){
				fieldValue = syncCode.syncCode(String.valueOf(config.get("tenantId")), 
						remarkMap.get(Constants.FIELD_VALUE), fieldValue);
			}
			//话术中涉及金额和额度的字段
			/*List<String> feeList = (List<String>) config.get("feeList");
			if(feeList.contains(remarkMap.get(Constants.FIELD_VALUE))){
				Double fee = Double.parseDouble(fieldValue)/100.0;
				fieldValue = String.valueOf(fee);
			}*/
			//日时间变量格式转化
			/*List<String> stringDateDateList = (List<String>) config.get("stringDateDateList");
			if(stringDateDateList.contains(remarkMap.get(Constants.FIELD_VALUE))){
				
				String date = TimeUtil.string2String("yyyyMMdd", fieldValue, "yyyy年MM月dd日");
				fieldValue = String.valueOf(date);
			}*/
			//月时间变量格式转化，话术中暂不涉及月时间格式变量
			/*List<String> stringMonthDateList=Arrays.asList(
					busiTools.getGlobalValue(Constants.STRING_MONTH_DATE_VARIABLE).split(Constants.SEPARATOR));
			if(stringMonthDateList.contains(remarkMap.get(Constants.FIELD_VALUE))){
				
				String date = TimeUtil.string2String("yyyyMM", fieldValue, "yyyy年MM月");
				fieldValue = String.valueOf(date);
			}*/
			//话术替换
			if(StringUtils.isBlank(fieldValue) || "null".equals(fieldValue)){
				//常量替换
				remark = remark.replace(remarkMap.get(Constants.FIELD_KEY),Constants.NULL_PROMPT);
			}
			else{
				remark = remark.replace(remarkMap.get(Constants.FIELD_KEY),fieldValue);
			}
			//删除话术字段
			wxOrderInfoMap.remove(remarkMap.get(Constants.FIELD_VALUE));
		}	
    	//筛选出微信话术字段放入
		/*wxActivityInfo.setWxhsh(remark);*/
		wxOrderInfo.setWxhsh(remark);
		wxOrderInfoMap.put("remark", remark);
    	/*wxOrderInfoMap.remove(String.valueOf(config.get("remark")));*/
    	//将wxOrderMap转化为WXOrderInfo对象
    	Boolean hashMap2Bean = MapUtil.hashMap2Bean(wxOrderMap, wxOrderInfo);
    	if(!hashMap2Bean){
    		return result;
    	}
    	//拼接fieldInfo的json字符串
    	HashMap<String,Object> fieldInfoMap = new HashMap<String,Object>();
    	Set<Entry<String, Object>> entrySet = wxOrderInfoMap.entrySet();
		for(Entry<String, Object> entry:entrySet){
			HashMap<String,Object> valueInfoMap = new HashMap<String,Object>();
			valueInfoMap.put(Constants.COLOR_KEY, Constants.COLOR_VALUE);
			//工单表中dateTime时间变量格式转化
			@SuppressWarnings("unchecked")
			List<String> dateList = (List<String>) config.get("dateList");
			if(dateList.contains(wxActivityInfo.getFieldMap().get(entry.getKey()))){
				Date date = (Date)entry.getValue();
				String date2String = TimeUtil.date2String(date, Constants.REMARK_ORDER_DATE_FORMAT);
				entry.setValue(date2String);
			}
			//秒时间变量格式转化
			@SuppressWarnings("unchecked")
			List<String> stringSecondDateList = (List<String>) config.get("stringSecondDateList");
			if(stringSecondDateList.contains(wxActivityInfo.getFieldMap().get(entry.getKey()))){
				String value = String.valueOf(entry.getValue());
				String date = TimeUtil.string2String("yyyyMMddHHmmss", value, "yyyy年MM月dd日HH时mm分ss秒");
				entry.setValue(date);
			}
			//日时间变量格式转化
			@SuppressWarnings("unchecked")
			List<String> stringDateDateList = (List<String>) config.get("stringDateDateList");
			if(stringDateDateList.contains(wxActivityInfo.getFieldMap().get(entry.getKey()))){
				
				String value = String.valueOf(entry.getValue());
				String date = TimeUtil.string2String("yyyyMMdd", value, "yyyy年MM月dd日");
				entry.setValue(date);
			}
			//月时间变量格式转化
			@SuppressWarnings("unchecked")
			List<String> stringMonthDateList = (List<String>) config.get("stringMonthDateList");
			if(stringMonthDateList.contains(wxActivityInfo.getFieldMap().get(entry.getKey()))){
				
				String value = String.valueOf(entry.getValue());
				String date = TimeUtil.string2String("yyyyMM", value, "yyyy年MM月");
				entry.setValue(date);
			}
			//金额字段转换
			@SuppressWarnings("unchecked")
			List<String> feeList = (List<String>) config.get("feeList");
			if(feeList.contains(wxActivityInfo.getFieldMap().get(entry.getKey()))){
				Double fee = Double.parseDouble(String.valueOf(entry.getValue()))/100.0;
				entry.setValue(String.valueOf(fee));
			}
			//百分比字段转码
			@SuppressWarnings("unchecked")
			List<String> percentList = (List<String>) config.get("percentList");
			if(percentList.contains(wxActivityInfo.getFieldMap().get(entry.getKey()))){
				String percent = String.valueOf(entry.getValue())+"%";
				entry.setValue(String.valueOf(percent));
			}
	        //变量字段转码
			@SuppressWarnings("unchecked")
			List<String> valueList = (List<String>) config.get("valueList");
			if(valueList.contains(wxActivityInfo.getFieldMap().get(entry.getKey()))){
				String value = syncCode.syncCode(String.valueOf(config.get("tenantId")), 
						wxActivityInfo.getFieldMap().get(entry.getKey()), String.valueOf(entry.getValue()));
				entry.setValue(value);
			}	
			if(null == entry.getValue() || StringUtils.isBlank(String.valueOf(entry.getValue())) || 
					"null".equals(String.valueOf(entry.getValue()))){
				valueInfoMap.put(Constants.VALUE_VALUE, Constants.NULL_PROMPT);
			}
			else{
				valueInfoMap.put(Constants.VALUE_VALUE, entry.getValue());
			}
			fieldInfoMap.put(entry.getKey(), valueInfoMap);
		}
		String fieldInfo = JSON.toJSONString(fieldInfoMap);
		/*if(StringUtils.isBlank(fieldInfo)){
			return result;
		}*/
		wxOrderInfo.setFieldInfo(fieldInfo);
    	result = true;
    	return result;
    	
    }
    
    /**
     * @Title: getInterfaceValue
     * @Description: 获取实时变量
     * @return: HashMap<String,String>
     * @param phoneNumber
     * @param netType
     * @return
     * @throws: 
     */
    private HashMap<String,Object> getRealValue(String phoneNumber,Integer netType,String realUrl,String realName){
    	
    	//准备请求参数
    	HashMap<String,Object> pamaMap = new HashMap<String,Object>();
    	pamaMap.put("phoneNum", phoneNumber);
    	if(netType == 50){
    		pamaMap.put("serviceType", "T");
    	}
    	else{
    		pamaMap.put("serviceType", "G");
    	}
    	pamaMap.put("searchDate", TimeUtil.formatSystemTime("yyyyMM"));
    	HashMap<String,Object> resultMap = new HashMap<String,Object>();
        //请求流量实时接口
		try {
			/*long start = System.currentTimeMillis();*/
			String result = HttpUtil.doPost(realUrl,pamaMap);
			/*long end = System.currentTimeMillis();
			log.info(phoneNumber+":"+realName+"实时接口耗时："+(end-start)/1000.0+"s");*/
			
			if(StringUtils.isBlank(result)){
				log.warn(phoneNumber+":"+realName+"the result of calling real value interface is null！");
				return null;
			}
			/*System.out.println(result.toString());*/
			resultMap = JSON.parseObject(result, HashMap.class);
			if(resultMap == null || resultMap.isEmpty() || resultMap.size()<1){
				log.warn(phoneNumber+":"+realName+"the resultMap of calling real value interface is null！");
				return null;
			}
			if("0001".equals(resultMap.get("msgcode"))){
				log.info(phoneNumber+":"+realName+"the msgCode of calling real value interface is error!");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(phoneNumber+":"+realName+"occurred exception when calling real value interface！");
			return null;
		}	
    	//流量接口字段名称转化
		if(Constants.XBUILDERORACLE_REALFLOW.equals(realName)){
			resultMap.put("allDinnerGprs", 
					Float.parseFloat(String.valueOf(resultMap.get("resultremain")))+
	    			Float.parseFloat(String.valueOf(resultMap.get("resulttotal")))+
	    			Constants.FLOW_UNIT);			
	    	resultMap.put("freeGprs", String.valueOf(resultMap.get("resultremain"))+Constants.FLOW_UNIT); 			
	    	resultMap.put("allGprs", String.valueOf(resultMap.get("resulttotal"))+Constants.FLOW_UNIT);
		}
    	//套餐接口字段名称转化
    	if(Constants.XBUILDERORACLE_REALPRODUCT.equals(realName)){
    		if("null".equals(String.valueOf(resultMap.get("result")))
    				||"".equals(String.valueOf(resultMap.get("result")))){
    			resultMap.put("dinnerDesc", Constants.NULL_PROMPT);
    		}
    		else{
    			resultMap.put("dinnerDesc", String.valueOf(resultMap.get("result")));
    		}	
    	}
    	return resultMap;
    	
    }
    
    
    /**
     * @Title: getTalkVarList
     * @Description: 获取话术变量列表
     * @return: Boolean
     * @param config
     * @return
     * @throws: 
     */
    @SuppressWarnings("unchecked")
	public Boolean getTalkVarList(HashMap<String, Object> config){
    	
    	Boolean flag = false;
    	String url = (String) config.get("actHuaShu");
    	String tanantId = (String) config.get("tenantId");
    	HashMap<String, Object> pama = new HashMap<String,Object>();
    	pama.put("tenantId", tanantId);
    	HashMap<String, Object> resultMap = new HashMap<String, Object>();
    	try {
			String result = HttpUtil.doGet(url, pama);
			if (StringUtils.isBlank(result)) {
				log.warn(tanantId+":the result of calling talk value interface is null！");
				return flag;
			}
			resultMap = JSON.parseObject(result, HashMap.class);
			if (resultMap == null || resultMap.isEmpty() || resultMap.size() < 1) {
				log.warn(tanantId+":the resultMap of calling talk value interface is null!");
				return flag;
			}
			if ("0".equals(resultMap.get("resultCode"))) {
				log.info(tanantId+":the resultCode of calling talk value interface is error！");
				return flag;
			} 
		} catch (Exception e) {
			e.printStackTrace();
			log.error(tanantId+":occurred exception when calling talk value interface！");
			return flag;
		}
    	String talkVarString = resultMap.get("result").toString();
    	if(StringUtils.isBlank(talkVarString)){
    		log.warn(tanantId+":the talkVar of calling talk value interface is null！");
    		return flag;
    	}
    	@SuppressWarnings("rawtypes")
		List<HashMap> talkVarList = JSON.parseArray(talkVarString, HashMap.class);
		if (talkVarList == null || talkVarList.isEmpty() || talkVarList.size() < 1) {
			log.warn(tanantId+":the talkVarList of calling talk value interface is null！");
			return flag;
		}
		List<HashMap<String,String>> allTalkVarList = new ArrayList<HashMap<String,String>>();
		List<String> mysqlFieldList = new ArrayList<String>();
		for(HashMap<String,String> talkVar:talkVarList){
			HashMap<String,String> talkVarMap = new HashMap<String,String>();
			talkVarMap.put("fieldKey", talkVar.get("content").toString());
			if("01".equals(talkVar.get("varType"))){
				if(-1 != talkVar.get("mysqlName").lastIndexOf(" ")){
					/*talkVarMap.put("fieldKey", talkVar.get("content").toString());*/
					talkVarMap.put("fieldValue", talkVar.get("mysqlName").replace("u.", "").split(" ")[1]);
					
				}
				else{
					/*talkVarMap.put("fieldKey", talkVar.get("content").toString());*/
					talkVarMap.put("fieldValue", talkVar.get("mysqlName").replace("u.", ""));
				}
				/*allTalkVarList.add(talkVarMap);*/
				mysqlFieldList.add(talkVar.get("mysqlName").replace("u.", "ul."));			
			}
			else if("02".equals(talkVar.get("varType"))){
				talkVarMap.put("fieldValue", talkVar.get("realName"));
			}
			else{
				log.info(tanantId+":not support this varType from talk value interface！");
			}
			allTalkVarList.add(talkVarMap);
			/*talkVarMap.clear();*/
		}
		//将组装好的两个list放入config中
		config.put("mysqlFieldList", mysqlFieldList);
		config.put("allTalkVarList", allTalkVarList);
		flag = true;
        return flag;
    }
	
	/* (non Javadoc)
	 * @Title: updateChannelStatus
	 * @Description: 更改工单状态
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#updateChannelStatus(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	/*@Transactional
	public Boolean updateChannelStatus(WXOrderInfo wxOrderInfo, HashMap<String, Object> config){
		
		//通用同步修改方法，两方修改顺序要一致
		Boolean result = false;
		//修改工单下发状态，并返回受影响行数(其实mybatis更新是返回的是matched值，而不是changed的值)
		Integer wxCount = wxOrderInfoMapper.updateWXChannelStatus(config, wxOrderInfo.getOrderId(),
				String.valueOf(config.get("wxChannelStatusSendSuccess")));
	    if(wxCount != 1){
	    	return result;
	    }
		//短信互斥处理代码
		Integer dxCount = wxOrderInfoMapper.updateDXChannelStatus(config, wxOrderInfo);
		if(wxCount != dxCount){
			 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			 return result;
		}
		if(wxCount != 1){
			return result;
		}
		result = true;
		return result;
		
	}*/
	
	
	
	/* (non Javadoc)
	 * @Title: insertWXOrderInfo
	 * @Description: 将组装好的微信工单入表
	 * @param wxOrderInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#insertWXOrderInfo(com.bonc.busi.wxOrderInfo.po.WXOrderInfo, java.util.HashMap)
	 */
	@Override
	@Transactional
	public Boolean insertWXOrderInfo(List<WXOrderInfo> wxOrderInfoList, 
			HashMap<String, Object> config){
		
		Boolean result = false; 
		//批量更新工单状态
		@SuppressWarnings("unused")
		Integer wxCount = wxOrderInfoMapper.updateWXChannelStatus(config, wxOrderInfoList,
				String.valueOf(config.get("wxChannelStatusSendSuccess")));
		/*if(wxCount == 0){
			log.error("更新工单状态出错");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return result;
		}*/
		StringBuilder stringBuilder =  new StringBuilder();
		long start = System.currentTimeMillis();
		/*Integer count = wxOrderInfoMapper.insertWXOrderInfo(wxOrderInfoList);*/
		stringBuilder.append("INSERT INTO WX_ORDER_INFO(order_id,activity_id,activity_seq_id,tel_int,net_type,wxhsh,"
				+ "template_id,activity_name,product_flag,product_id1,element_id1,"
				+ "orderproduct_id1,product_id2,element_id2,"
				+ "orderproduct_id2,product_id3,element_id3,orderproduct_id3,"
				+ "order_start_time,order_end_time,activity_start_time,activity_end_time,"
				+ "TENANT_ID,public_id,open_id,field_info,environment_flag) VALUES ");
		for(WXOrderInfo wxOrderInfo:wxOrderInfoList){
			
			stringBuilder.append("(");
			stringBuilder.append(wxOrderInfo.getOrderId()+",");
			stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getActivityId()+"',");
			stringBuilder.append(wxOrderInfo.getWxActivityInfo().getRecId()+",");
			stringBuilder.append("'"+wxOrderInfo.getTelInt()+"',");
			stringBuilder.append(wxOrderInfo.getNetType()+",");
			if(wxOrderInfo.getWxhsh() == null || "null".equals(wxOrderInfo.getWxhsh())){
				stringBuilder.append(null+",");
			}
			else{
				stringBuilder.append("'"+wxOrderInfo.getWxhsh()+"',");	
			}
			stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getTemplateId()+"',");
			stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getActivityName()+"',");
			stringBuilder.append(wxOrderInfo.getWxActivityInfo().getProductFlag()+",");
			List<WXProductInfo> productInfoList = wxOrderInfo.getWxActivityInfo().getProductInfoList();
			if(productInfoList.size() >=1){
				if(productInfoList.get(0).getFlowType() ==  Constants.FLOW_TYPE_DAY){
					stringBuilder.append("'"+wxOrderInfo.getProductId()+"',");
				}
				else{
					stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getProductId1()+"',");
				}
				stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getElementId1()+"',");
				stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getOrderProductId1()+"',");
			}
			else{
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getProductId1()+",");
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getElementId1()+",");
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getOrderProductId1()+",");
			}
			
			if(productInfoList.size() >=2){
				if(productInfoList.get(1).getFlowType() ==  Constants.FLOW_TYPE_DAY){
					stringBuilder.append("'"+wxOrderInfo.getProductId()+"',");
				}
				else{
					stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getProductId2()+"',");
				}
				stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getElementId2()+"',");
				stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getOrderProductId2()+"',");
			}
			else{
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getProductId2()+",");
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getElementId2()+",");
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getOrderProductId2()+",");
			}
			
			
			if(productInfoList.size() >=3){
				if(productInfoList.get(2).getFlowType() ==  Constants.FLOW_TYPE_DAY){
					stringBuilder.append("'"+wxOrderInfo.getProductId()+"',");
				}
				else{
					stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getProductId3()+"',");
				}
				stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getElementId3()+"',");
				stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getOrderProductId3()+"',");
			}
			else{
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getProductId3()+",");
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getElementId3()+",");
				stringBuilder.append(wxOrderInfo.getWxActivityInfo().getOrderProductId3()+",");
			}
			
			stringBuilder.append(TimeUtil.date2String(wxOrderInfo.getStartTime(), 
					String.valueOf(config.get("mysqlDateFormat")))+",");
			stringBuilder.append(TimeUtil.date2String(wxOrderInfo.getEndTime(), 
					String.valueOf(config.get("mysqlDateFormat")))+",");
			stringBuilder.append(TimeUtil.date2String(wxOrderInfo.getWxActivityInfo().getStartTime(),
					String.valueOf(config.get("mysqlDateFormat")))+",");
			stringBuilder.append(TimeUtil.date2String(wxOrderInfo.getWxActivityInfo().getEndTime(),
					String.valueOf(config.get("mysqlDateFormat")))+",");
			stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getTenantId()+"',");
			stringBuilder.append("'"+wxOrderInfo.getWxActivityInfo().getPublicId()+"',");
			stringBuilder.append("'"+wxOrderInfo.getOpenId()+"',");
			stringBuilder.append("'"+wxOrderInfo.getFieldInfo()+"',");
			stringBuilder.append("'"+config.get("environmentFlag")+"'");
			stringBuilder.append("),");
		}
		String sql = stringBuilder.substring(0, stringBuilder.length()-1);
		/*log.error(sql);*/
		Boolean loadDataToMysql = busiTools.LoadDataToMysql(sql, String.valueOf(config.get("tenantId")));
		long end = System.currentTimeMillis();
		if(!loadDataToMysql){
			log.error("occurred error when insert weChat order");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return result;
		}
		log.info("time of insert "+wxOrderInfoList.size()+" piece of data："+(end-start)/1000.0+"s");
		/*if(count != wxOrderInfoList.size()){
			return result;
		}*/
		result = true;
		return result;
		
	}
	
	
	/* (non Javadoc)
	 * @Title: countWXOrder
	 * @Description: 统计微信工单下发信息
	 * @param wxActivityInfo
	 * @param ordereRecIdListSize
	 * @param pageBean
	 * @param resultListSize
	 * @param isSuccess
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#countWXOrder(com.bonc.busi.wxActivityInfo.po.WXActivityInfo, java.lang.Integer, com.bonc.busi.entity.PageBean, java.lang.Integer, java.lang.Boolean)
	 */
	@Override
	public void countWXOrder(HashMap<String, Object> config,WXActivityInfo wxActivityInfo, 
			Integer ordereRecIdListSize, PageBean pageBean,Integer resultListSize, Boolean isSuccess) {
		
		HashMap<String, Object> countWXOrder = new HashMap<String, Object>();
		countWXOrder.put("activitySeqId",wxActivityInfo.getRecId());
		countWXOrder.put("ordereRecIdListSize",ordereRecIdListSize);
		countWXOrder.put("resultListSize",resultListSize);
		if(isSuccess){
			countWXOrder.put("sucNum", resultListSize);
		}
		else{
			countWXOrder.put("errNum", resultListSize);
		}
		countWXOrder.put("sendTime",new Date());
		if(pageBean.getCurrentPage() == 1){
			//只有一页，既为起始页也为结束页
			if(pageBean.getTotalPage() == 1){
				countWXOrder.put("isFinish",1);
				//获取该活动的统计信息条数
				Integer countStatistic = wxOrderInfoMapper.getCountStatistic(config,wxActivityInfo.getRecId());
				//若有，更新
				if(countStatistic != 0){
					wxOrderInfoMapper.updateCountWXOrder(config, countWXOrder);
				}
				//若无，插入
				else{
					wxOrderInfoMapper.insertCountWXOrder(config,countWXOrder);
				}			
			}
			//有多页，当前为第一页
			else{
				countWXOrder.put("isFinish",0);
				//获取该活动的统计信息条数
				Integer countStatistic = wxOrderInfoMapper.getCountStatistic(config,wxActivityInfo.getRecId());
				//若有，更新
				if(countStatistic != 0){
					wxOrderInfoMapper.updateCountWXOrder(config, countWXOrder);
				}
				//若无，插入
				else{
					wxOrderInfoMapper.insertCountWXOrder(config,countWXOrder);
				}			
			}
		}
		else{
			//有多页，当前为最后一页
			if(pageBean.getCurrentPage() == pageBean.getTotalPage()){
				countWXOrder.put("isFinish",1);
				wxOrderInfoMapper.updateCountWXOrder(config,countWXOrder);
			}
			//有多页，当前为中间某一页
			else{
				countWXOrder.put("isFinish",0);
				wxOrderInfoMapper.updateCountWXOrder(config,countWXOrder);
			}
		}
		
	}


	/* (non Javadoc)
	 * @Title: setActivityChannelStatus
	 * @Description: 设置活动渠道状态
	 * @param wxActivityInfo
	 * @param config
	 * @return
	 * @see com.bonc.busi.wxOrderInfo.service.WXOrderInfoService#setActivityChannelStatus(com.bonc.busi.wxActivityInfo.po.WXActivityInfo, java.util.HashMap)
	 */
	@Override
	public Boolean setActivityChannelStatus(WXActivityInfo wxActivityInfo, HashMap<String, Object> config) {

		Boolean result = false;
		Integer count = wxOrderInfoMapper.setActivityChannelStatus(wxActivityInfo,config);
		if(count != 1){  
			return result;
		}
		result = true;
		return result;
		
	}
	
	
    
	public static void main(String[] args) {
    	
    	WXOrderInfoServiceImpl wxOrderInfoServiceImpl = new WXOrderInfoServiceImpl();
    	HashMap<String, Object> interfaceValue = wxOrderInfoServiceImpl.getRealValue("18637106868",50,
    			"http://clyxys.yz.local:8080/xbuilderoracle/restful/h2service/getRealFlow","XBUILDERORACLE_REALFLOW");
 		System.out.println(interfaceValue);
     
     }
	
}
