package com.bonc.busi.assemble.impl;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.activityInfo.mapper.ActivityInfoMapper;
import com.bonc.busi.activityXMLInfo.mapper.ActivityXMLInfoMapper;
import com.bonc.busi.activityXMLInfo.po.ActivityXMLInfo;
import com.bonc.busi.assemble.DataAssembler;
import com.bonc.busi.entity.ActivityBo;
import com.bonc.busi.entity.ActivityCycleInfo;
import com.bonc.busi.entity.ChannelInfo;
import com.bonc.busi.entity.CostPo;
import com.bonc.busi.entity.ProductInfo;
import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.busi.orderInfo.mapper.OrderInfoMapper;
import com.bonc.busi.sendData.mapper.SendDataMapper;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.common.utils.FreemarkerUtil;
import com.bonc.common.utils.WebServiceUtils;
import com.bonc.utils.Constants;
import com.bonc.utils.DateUtil;
import com.bonc.utils.PropertiesUtil;
import com.bonc.utils.TimeUtil;

/**
 * 一级渠道数据组装实现类
 * 
 * @author sky
 *
 */
@Component(value = "yJQDDataAssembler")
public class YJQDDataAssembler implements DataAssembler {

	protected Logger log4j = Logger.getLogger(getClass());
	
	@Autowired
	ActivityXMLInfoMapper activityXMLInfoMapper;
	
	@Autowired
	private GlobalCFGMapper globalCFGMapper;
	
	@Autowired
	ActivityInfoMapper activityInfoMapper;
	
	@Autowired
	SendDataMapper sendDataMapper;
	
	@Autowired
	OrderInfoMapper orderInfoMapper;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static String TEMPLATE_PATH = "com/bonc/busi/template";
	private static String TEMPLATE_NAME = "activityConfig-template.ftl";

	@Override
	public Object assembleData(Object object) throws Exception {

		Map<String, Object> data = (Map<String, Object>) object;
		ActivityBo bo = new ActivityBo();
		// 组装ActivityBo 对象
		assmbleActivityBo(data, bo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bo", bo);
		
		//状态字段   一次性 为0  周期性  如果已下发一次 
		if (bo.getPo().getCycleInfo() != null && 
				"1".equals(bo.getPo().getCycleInfo())) {
			map.put("status", "0");
		} else {
			int count = activityInfoMapper.findSendCountByActivityId(data.get("activityId").toString(),
					data.get("tenantId").toString());
			if(count > 1){
				map.put("status", "1");
			}else{
				map.put("status", "0");
			}	
		}
		//父活动状态
		if(data.get("parentActivity") != null){
			int sendCount = activityInfoMapper.findSendCountByParentActivityId(data.get("parentActivity").toString(),
					data.get("tenantId").toString());	
			if(sendCount > 1)
				map.put("parentStatus", "1");
			else
				map.put("parentStatus", "0");
		}
		
		// 按模板生成xml
		saveConfig(data, map);
		
		assmbleSendData(data);
		data.put("assembleData", "OK");
		return data;
	}

	/**
	 * 组装下发数据
	 * @param data
	 * @throws Exception 
	 */
	private void assmbleSendData(Map<String, Object> data){
		int startIndex = 0; 
		int pageSize = Integer.parseInt(PropertiesUtil.getConfig("yjqd.insert.size"));
		
		int activitySeqId = Integer.valueOf(data.get("recId").toString());
		String tenantId = data.get("tenantId").toString();
		String dealMonth = data.get("dealMonth").toString();
	
		createSendData(tenantId,activitySeqId,dealMonth,startIndex,pageSize);
	}
	
	/**
	 * 创建下发数据
	 * @param tenantId
	 * @param activitySeqId
	 * @param dealMonth
	 * @param startIndex
	 * @param pageSize
	 * @throws Exception 
	 */
	private void createSendData(String tenantId,int activitySeqId,
			String dealMonth,int startIndex,int pageSize){
		long start = System.currentTimeMillis();
		int partitionFlag = Integer.parseInt(globalCFGMapper.getGlobalCFG("ASYNUSER.MYSQL.EFFECTIVE_PARTITION"));
		List<Map> sendDataList = sendDataMapper.findOrderOneSendDataByActivity(tenantId,activitySeqId, 
				dealMonth,startIndex,pageSize,partitionFlag);
		long end = System.currentTimeMillis();
		log4j.info("查询"+sendDataList.size()+"条数据耗时："+(end-start)/1000.0+"s");
		if(sendDataList != null && sendDataList.size() > 0){
			insertSendData(sendDataList,tenantId,activitySeqId,dealMonth);
			sendDataList = null;
			createSendData(tenantId,activitySeqId,dealMonth,startIndex,pageSize);
		}else{
			return;
		}
	}
	/**
	 * 插入数据
	 * @param list
	 * @param data
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	private void insertSendData(List<Map> list,String  tenantId,int activitySeqId,
			String dealMonth) {
		String[] column = new String[]{"SUBS_INSTANCE_ID","DEVICE_NUMBER","SERVICE_TYPE","ACTIVITY_ID","USERGROUP_ID",
				"PROV_ID","AREA_ID","PAY_MODE","PRODUCT_BASE_CLASS","ACTIVITY_TYPE","EXP_DATE","ACCT_FEE",
				"WENDING_FLAG","CHNL_TYPE3","TENANT_ID","ACTIVITY_SEQ_ID","DEAL_MONTH","STATUS"};
		StringBuilder stringBuilder =  new StringBuilder();
		List<String> userIds =  new ArrayList<String>();
		String createDate = DateUtil.CurrentDate.currentDateFomart(DateUtil.DateFomart.DATETIME);
		long start = System.currentTimeMillis();
		
		stringBuilder.append("INSERT INTO YJQD_SEND_DATA(");
		String columnStr = "";
		for(int i = 0; i < column.length; i++){
			columnStr += column[i]+",";
		}
		stringBuilder.append(columnStr +"CREATE_DATE");
		stringBuilder.append(" ) VALUES ");
		for(Map map: list){
			userIds.add(map.get("SUBS_INSTANCE_ID")+"");
			stringBuilder.append("(");
			for(int i = 0; i < column.length; i++){
				if("ACCT_FEE".equals(column[i]) || "ACTIVITY_SEQ_ID".equals(column[i])){
					stringBuilder.append(map.get(column[i])+",");
				}else{
					stringBuilder.append("'"+map.get(column[i])+"',");
				}
				
			}
			stringBuilder.append(createDate);
			stringBuilder.append("),");
		}
		
		String sql = stringBuilder.substring(0, stringBuilder.length()-1);	
		
		jdbcTemplate.execute(sql);
		sql = null;
		stringBuilder = null;
		
		orderInfoMapper.updateChannelStatusBatch(tenantId,activitySeqId, 
			Constants.ORDER_STATUS_READY,dealMonth,Constants.CHANNEL_STATUS_SUCCESS,userIds);
		
		userIds = null;
		long end = System.currentTimeMillis();
		log4j.info("本次插入"+list.size()+"条数据耗时："+(end-start)/1000.0+"s");
	}

	
		
		
	
	
	/**
	 * 保存config xml
	 * 
	 * @param data
	 * @param map
	 * @throws Exception
	 */
	private void saveConfig(Map<String, Object> data, Map<String, Object> map) throws Exception {

		StringWriter sw = new StringWriter();
		boolean success = FreemarkerUtil.print(TEMPLATE_PATH, TEMPLATE_NAME, map, sw);
		if (success) {
			String activityId = data.get("activityId").toString();
			String tenantId = data.get("tenantId").toString();
			String dealMonth = data.get("dealMonth").toString();
			int activitySeqId = Integer.valueOf(data.get("recId").toString());
			ActivityXMLInfo activityXMLInfo = activityXMLInfoMapper
					.findActivityXMLInfo(activityId,tenantId,activitySeqId,dealMonth);
			//有，更新
			if (activityXMLInfo != null) {
				activityXMLInfo.setConfigInfo(sw.toString());
				activityXMLInfoMapper.updateActivityXMLInfo(activityXMLInfo);
			} 
			//没有，添加
			else {
				activityXMLInfo = new ActivityXMLInfo();
				activityXMLInfo.setConfigInfo(sw.toString());
				activityXMLInfo.setActivityId(activityId);
				activityXMLInfo.setTenantId(tenantId);
				activityXMLInfo.setActivitySeqId(activitySeqId);
				activityXMLInfo.setDealMonth(dealMonth);
				activityXMLInfoMapper.insertActivityXMLInfo(activityXMLInfo);
			}
		}
		sw.close();
	}

	/**
	 * 
	 * @param data
	 * @param bo
	 * @throws Exception 
	 */
	private void assmbleActivityBo(Map<String, Object> data, ActivityBo bo)
			throws Exception {

		assmbleActivity(data, bo);
		// 组装渠道数据 网厅、手厅、沃视窗
		assmbleChannel(data, bo);
		// 组装产品数据
		assmbleProduct(data, bo);
		//
		assmbleCost(data, bo);

	}

	/**
	 * 组装活动 数据
	 * 
	 * @param data
	 * @param bo
	 * @throws Exception 
	 */
	private void assmbleActivity(Map<String, Object> data, ActivityBo bo) throws Exception {
		
		bo.getPo().setProvId(data.get("provId") != null ? data.get("provId").toString() : "-1");
		//默认  cityId 设置为-1
		bo.getPo().setCityId("-1");
		//拆分 默认设置为0
		bo.getPo().setSplitType("0");
		/**
		 * 活动ID改为唯一标识ID  周期性活动 
		 */
		bo.getPo().setActivityId(data.get("recId") != null ? data.get("recId").toString() : "");
		//bo.getPo().setActivityId(data.get("activityId") != null ? data.get("activityId").toString() : "");
		bo.getPo().setActivityName(data.get("activityName") != null ? data.get("activityName").toString() : "");
		
		Date start = new Date();
		Date end = new Date();
		if(data.get("startDate") != null){
			if(data.get("startDate").getClass().isAssignableFrom(Map.class)){
				Map startDate = (Map) data.get("startDate");
				if (startDate.get("time") != null) {
					start = new Date(Long.parseLong(startDate.get("time").toString()));
				}
			}else if(data.get("startDate").getClass().isAssignableFrom(String.class)){
				String startDate = (String)data.get("startDate");
				if (!"".equals(startDate) ) {
					start = TimeUtil.String2Date(startDate,DateUtil.DateFomart.EN_DATE);
				}
			}
		}
		
		if(data.get("endDate") != null){
			if(data.get("endDate").getClass().isAssignableFrom(Map.class)){
				Map endDate = (Map) data.get("endDate");
				if (endDate.get("time") != null) {
					end = new Date(Long.parseLong(endDate.get("time").toString()));
				}
			}else if(data.get("endDate").getClass().isAssignableFrom(String.class)){
				String endDate = (String)data.get("endDate");
				if (!"".equals(endDate)) {
					end = TimeUtil.String2Date(endDate,DateUtil.DateFomart.EN_DATE);
				}
			}
			
		}	
		
		bo.getPo().setStartDate(start);
		bo.getPo().setEndDate(end);
		
		if(data.get("parentActivity") != null ){
			//父活动信息
			assmbleParentPo(data, bo);	
			//parentPo.activityId 存在   才是地市活动
			bo.getPo().setCityId(data.get("createOrgId") != null ? data.get("createOrgId").toString() : "");	
		}
		//根据  orgRange 组装  USE_RANGE
		String  orgRange = data.get("orgRange") != null ? data.get("orgRange").toString() : "";
		bo.setProvIds(assmbleRangeFromPath(orgRange));

		bo.getPo().setUserGroupId(data.get("userGroupId") != null ? data.get("userGroupId").toString() : "");
		bo.getPo().setUserGroupName(data.get("userGroupName") != null ? data.get("userGroupName").toString() : "");
		String  activityType = data.get("activityType")!=null ?data.get("activityType").toString():"";
		if( "3".equals(activityType)){
			bo.getPo().setCycleInfo("1");
		}else if(!"".equals(activityType)){
			bo.getPo().setCycleInfo("2");
		}
		//账期
		bo.getPo().setDataSendingDate(data.get("dealMonth") != null ? data.get("dealMonth").toString() : "");
		//循环信息
		assmbleCycInfo(data, bo, start, end);
	}
    /**
     * 组装  活动循环信息
     * @param data
     * @param bo
     * @param start
     * @param end
     */
	private void assmbleCycInfo(Map<String, Object> data, ActivityBo bo, Date start, Date end) {
		ActivityCycleInfo cyc = new ActivityCycleInfo();
		cyc.setCycleMode(data.get("activityType")!=null ?data.get("activityType").toString():"");
		cyc.setScopeStartDay(start);
		cyc.setScopeEndDay(end);
		bo.setActivityCycleInfo(cyc);
	}
   /**
    * 组装父活动信息
    * @param data
    * @param bo
 * @throws Exception 
    */
	private void assmbleParentPo(Map<String, Object> data, ActivityBo bo) throws Exception {

		String serviceUrl = PropertiesUtil.getWebService("activityRequest.webService.url");
		serviceUrl += "?activityId=" +data.get("parentActivity") + "&tenantId=" + data.get("tenantId");
		String accept = "application/json";
		int timeOut = 5000;
		String res = WebServiceUtils.HttpWebServiceInvoke(serviceUrl, accept, timeOut,"utf-8");
		if (res != null) {
			try {
				JSON.parseObject(res);
			} catch (Exception e) {
				throw new Exception("获取父活动信息失败！parentActivityId:"+data.get("parentActivity") +"info:" + res);
			}
			
			JSONObject json =JSON.parseObject(res);
			
			Date parentStart = new Date();
			Date parentEnd = new Date();
			
			if(json.get("startDate") != null){
				if(json.get("startDate").getClass().isAssignableFrom(Map.class)){
					Map startDate = (Map) json.get("startDate");
					if (startDate.get("time") != null) {
						parentStart = new Date(Long.parseLong(startDate.get("time").toString()));
					}
				}else if(json.get("startDate").getClass().isAssignableFrom(String.class)){
					String startDate = (String)json.get("startDate");
					if (!"".equals(startDate) ) {
						parentStart = TimeUtil.String2Date(startDate,DateUtil.DateFomart.EN_DATE);
					}
				}
			}
			
			if(json.get("endDate") != null){
				if(json.get("endDate").getClass().isAssignableFrom(Map.class)){
					Map endDate = (Map) json.get("endDate");
					if (endDate.get("time") != null) {
						parentEnd = new Date(Long.parseLong(endDate.get("time").toString()));
					}
				}else if(json.get("endDate").getClass().isAssignableFrom(String.class)){
					String endDate = (String)json.get("endDate");
					if (!"".equals(endDate)) {
						parentEnd = TimeUtil.String2Date(endDate,DateUtil.DateFomart.EN_DATE);
					}
				}
			}	
			bo.getParentPo().setActivityId(data.get("parentActivity").toString());
			bo.getParentPo().setProvId(json.get("provId")!= null ? json.get("provId").toString() : "");	
			bo.getParentPo().setActivityName(json.getString("activityName") != null ? 
					json.getString("activityName").toString() : "");
			
			bo.getParentPo().setStartDate(parentStart);
			bo.getParentPo().setEndDate(parentEnd);
			bo.getParentPo().setSplitType("0");
			
			bo.setParentProvIds(assmbleRangeFromPath(json.getString("orgRange")));
		}
		
	}

	/**
	 * 从 range Path 取最后一级 组装USE_RANGE
	 * @param orgRange
	 * @return
	 */
	private String assmbleRangeFromPath(String orgRange) {
		String provIds = "";
		if(!"".equals(orgRange)){
			String[] ranges = orgRange.split(",");
			for(String s : ranges){
				if(!"".equals(s))
					provIds += s.substring(s.lastIndexOf("/") + 1) + ",";
			}
		}
		return provIds.substring(0,provIds.length() - 1);
	}

	/**
	 * 组装 cost
	 * 
	 * @param data
	 * @param bo
	 */
	private void assmbleCost(Map<String, Object> data, ActivityBo bo) {
		CostPo c = new CostPo();
		bo.setCostInfo(c);
	}

	/**
	 * 组装产品信息
	 * 
	 * @param data
	 * @param bo
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void assmbleProduct(Map<String, Object> data, ActivityBo bo)
			throws IllegalAccessException, InvocationTargetException {

		Map successStandardPo = (Map) data.get("successStandardPo");
		if (successStandardPo != null) {
			List<Map> productList = (List<Map>) successStandardPo.get("successProductList");
			if (productList != null) {
				List<ProductInfo> productInfoList = new ArrayList<ProductInfo>();
				for (Map product : productList) {
					ProductInfo p = new ProductInfo();
					p.setProductCode(product.get("productCode") != null ? product.get("productCode").toString() : "");
					p.setProductName(product.get("productName") != null ? product.get("productName").toString() : "");
					productInfoList.add(p);
				}
				bo.setProductList(productInfoList);
			}
		}
	}

	/**
	 * 组装渠道信息
	 * 
	 * @param data
	 * @param bo
	 */
	private void assmbleChannel(Map<String, Object> data, ActivityBo bo) {

		List<ChannelInfo> channelList = new ArrayList<ChannelInfo>();
		Map channelHandOfficePo = (Map) data.get("channelHandOfficePo");
		Map channelWebOfficePo = (Map) data.get("channelWebOfficePo");
		Map channelWoWindowPo = (Map) data.get("channelWoWindowPo");

		if (channelHandOfficePo != null) {
			ChannelInfo handOffice = new ChannelInfo();
			String channelId = channelHandOfficePo.get("channelId")!=null?
					channelHandOfficePo.get("channelId").toString():ChannelEnum.ST.getCode();
			handOffice.setChannelId(channelId);
			handOffice.setChannelShortName(ChannelEnum.getName(channelId));
			handOffice.setChannelCode("");
			handOffice.setUrl(channelHandOfficePo.get("channelHandofficeUrl") != null ? channelHandOfficePo.get("channelHandofficeUrl").toString() : "");
			handOffice.setTitle(
					channelHandOfficePo.get("channelHandofficeTitle") != null ? channelHandOfficePo.get("channelHandofficeTitle").toString() : "");
			handOffice.setHuashuContent(
					channelHandOfficePo.get("channelHandofficeContent") != null ? channelHandOfficePo.get("channelHandofficeContent").toString() : "");
			handOffice.setTargetId(
					channelHandOfficePo.get("targetId") != null ? channelHandOfficePo.get("targetId").toString() : "");
			channelList.add(handOffice);
		}

		if (channelWebOfficePo != null) {
			ChannelInfo webOffice = new ChannelInfo();
			String channelId = channelWebOfficePo.get("channelId")!=null?
					channelWebOfficePo.get("channelId").toString():ChannelEnum.WT.getCode();
			webOffice.setChannelId(channelId);
			webOffice.setChannelShortName(ChannelEnum.getName(channelId));
			webOffice.setChannelCode("");
			webOffice.setUrl(channelWebOfficePo.get("channelWebofficeUrl") != null ? channelWebOfficePo.get("channelWebofficeUrl").toString() : "");
			webOffice.setTitle(
					channelWebOfficePo.get("channelWebofficeTitle") != null ? channelWebOfficePo.get("channelWebofficeTitle").toString() : "");
			webOffice.setHuashuContent(
					channelWebOfficePo.get("channelWebofficeContent") != null ? channelWebOfficePo.get("channelWebofficeContent").toString() : "");
			webOffice.setTargetId(
					channelWebOfficePo.get("targetId") != null ? channelWebOfficePo.get("targetId").toString() : "");
			channelList.add(webOffice);
		}

		if (channelWoWindowPo != null) {
			ChannelInfo woWindow = new ChannelInfo();
			String channelId = channelWoWindowPo.get("channelId")!=null?
					channelWoWindowPo.get("channelId").toString():ChannelEnum.WSC.getCode();
			woWindow.setChannelId(channelId);
			woWindow.setChannelShortName(ChannelEnum.getName(channelId));
			woWindow.setChannelCode("");
			woWindow.setUrl(channelWoWindowPo.get("channelWowindowUrl") != null ? channelWoWindowPo.get("channelWowindowUrl").toString() : "");
			woWindow.setTitle(channelWoWindowPo.get("channelWowindowTitle") != null ? channelWoWindowPo.get("channelWowindowTitle").toString() : "");
			woWindow.setHuashuContent(
					channelWoWindowPo.get("channelWowindowContent") != null ? channelWoWindowPo.get("channelWowindowContent").toString() : "");
			woWindow.setImgUrl(
					channelWoWindowPo.get("channelWowindowImgurl") != null ? channelWoWindowPo.get("channelWowindowImgurl").toString() : "");
			woWindow.setImgSize(
					channelWoWindowPo.get("channelWowindowImgsize") != null ? channelWoWindowPo.get("channelWowindowImgsize").toString() : "");
			
			woWindow.setTargetId(
					channelWoWindowPo.get("targetId") != null ? channelWoWindowPo.get("targetId").toString() : "");
			channelList.add(woWindow);
		}

		bo.setChannelInfoList(channelList);
	}

	@Override
	public Boolean supports(String channelId) {

		return ChannelEnum.YJQD.getCode().equals(channelId);
	}
	
}