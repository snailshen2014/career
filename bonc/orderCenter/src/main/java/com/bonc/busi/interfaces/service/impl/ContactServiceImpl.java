package com.bonc.busi.interfaces.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.interfaces.mapper.ContactMapper;
import com.bonc.busi.interfaces.model.frontline.ContactReq;
import com.bonc.busi.interfaces.service.ContactService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.common.datasource.TargetDataSource;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.DateUtil;
import com.bonc.utils.DateUtil.DateFomart;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Service("contactService")
public class ContactServiceImpl implements ContactService{

	private static final Logger log = Logger.getLogger(ContactServiceImpl.class);
	
	@Autowired
	private ContactMapper contactMapper;
	
	private static String url = null;
	
	@Autowired
	private BusiTools  AsynDataIns;
	
	@Override
	@Transactional
	public void contact(ContactReq req) {
		List<HashMap<String, Object>> maps = req.getPama();
		//TODO 缓存批量活动的列表信息
		HashMap<String, HashMap<String, Object>> activityMap = new HashMap<String, HashMap<String, Object>>();
		List<HashMap<String, String>> errorList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> orderTable = CodeUtil.getCodeMap(req.getTenantId(), IContants.CHANNEL_ORDER_TABLE);
		String orderTableName = orderTable.get(req.getChannelId());
		if(null==orderTableName||"".equals(orderTableName)){
			throw new BoncExpection(IContants.CODE_FAIL,"暂不支持改渠道回执!");
		}
		for(HashMap<String, Object> map:maps){
			try{
				//设置用到的全局参数
				map.put("tenantId", req.getTenantId());
				map.put("orgPath", req.getOrgPath());
				//注：　设置表名，不同渠道回执操作对应的表名不同
				map.put("tableName", orderTableName);
				//如果传参中没有有效的接触时间，设为未本应用内接触时间
				if(StringUtil.validateStr(map.get("contactDate"))){
					map.put("contactDate", DateUtil.CurrentDate.currentDateFomart(DateUtil.DateFomart.EN_DATETIME));
				}
				
				//一、先查询工单信息　
				HashMap<String, Object> orderInfo = null;
				if(IContants.YX_CHANNEL.equals(req.getChannelId())){
					orderInfo = contactMapper.findOrderInfo(map);
				}else{
					orderInfo = findOrderInfo(map);
				}
				
				//工单不存在
				if(null==orderInfo){
					throw new BoncExpection(IContants.CODE_FAIL,"order not exit!");
				}
				
				//工单已经执行
				if(IContants.TC_CHANNEL.equals(req.getChannelId())){
					if("2".equals(orderInfo.get("CHANNEL_STATUS"))){
						throw new BoncExpection("000002","order is exed!");
					}
				}
				
				//工单已失效
				map.put("channelId", orderInfo.get("CHANNEL_ID"));
				//TODO 工单不失效，且工单不成功
				if(null==orderInfo.get("CHANNEL_STATUS")||((String)orderInfo.get("CHANNEL_STATUS")).startsWith("4")||"3".equals(orderInfo.get("CHANNEL_STATUS"))){
					//TODO 记录日志
					log.error("工单已经失效，不可进行回执操作！"+map.get("recId"));
					throw new BoncExpection(IContants.CODE_FAIL,"order is invalid!");
				}
				
				//获取用户资料
				map.put("USER_ID", orderInfo.get("USER_ID"));
				map.put("PHONE_NUMBER", orderInfo.get("PHONE_NUMBER"));
				map.put("serviceType", orderInfo.get("SERVICE_TYPE"));
				//三、修改工单接触信息
				//(三.1)互斥性规则获取
				map.put("ACTIVITY_SEQ_ID", orderInfo.get("ACTIVITY_SEQ_ID"));
				HashMap<String, Object> activityInfo = null;
				if(null!=activityMap.get(map.get("tenantId")+"."+map.get("ACTIVITY_SEQ_ID"))){
					activityInfo = activityMap.get(map.get("tenantId")+"."+map.get("ACTIVITY_SEQ_ID"));
				}else{
					//将对应的活动信息放到缓存当中
					activityInfo = contactMapper.findActivity(map);
					
					//(三.2)互斥性规则标识
					if(null==activityInfo){
						log.error("该工单对应的活动信息不存在！");
						throw new BoncExpection(IContants.CODE_FAIL,"activity not exit!");
					}
					if("2".equals(activityInfo.get("ACTIVITY_STATUS"))){
						log.error("该工单对应的活动信息已经失效！");
						throw new BoncExpection(IContants.CODE_FAIL,"activity is invalid!");
					}
					activityMap.put(map.get("tenantId")+"."+map.get("ACTIVITY_SEQ_ID"), activityInfo);
				}
				
				map.put("activityName", activityInfo.get("ACTIVITY_NAME"));
				map.put("activityId", activityInfo.get("ACTIVITY_ID"));
				
				
				//获取执行结果
				if(contactSuccess(map)){
					map.put("CHANNEL_STATUS", "2");
					map.put("contactResult", "1");
				}else{
					map.put("CHANNEL_STATUS", "0");
					map.put("contactResult", "0");
				}
				//获取上次的接触结果，
				map.put("LAST_CHANNEL_STATUS", orderInfo.get("CHANNEL_STATUS"));
				
				
				//提取执行者信息     (将执行者的LOGIN_ID添加进去,弹窗渠道定义为loginId)
				if(IContants.TC_CHANNEL.equals(req.getChannelId())){
					map.put("exeId", map.get("loginId"));
				}
				//客户经理渠道定义执行者为 归属的PATH + LOGIN_ID
				if(IContants.YX_CHANNEL.equals(req.getChannelId())){
					map.put("exeId", map.get("orgPath"));
				}
				
				//更新工单状态	(、乐观锁模式 修改工单回执信息，如果CHANNEL_STATUS标识已经设置为非0 标识  则不对其进行后续处理)
				Integer contactFlag = contactMapper.updateOrderInfo(map);
				if(contactFlag==0){
					throw new BoncExpection("000002","order is exed!");
				}
				
				//修改统计信息	(修改客户经理信息统计表)
				if(IContants.YX_CHANNEL.equals(map.get("channelId"))){
					modifyStatistic(orderInfo,map);
				}
				
				//生成接触流水
				map.put("ext5", StringUtil.getUUID());
				
				//插入回执历史	
				insertContact(map);
				
				//更新批次统计信息
				contactMapper.updateActivityProc(map);
				
				//插入渠道协同接口
				contactRecord(map,req);
			}catch(BoncExpection e){
				HashMap<String, String> error = new HashMap<String, String>();
				error.put("code", e.getCode());
				error.put("recId", map.get("recId")+"");
				error.put("activityName", map.get("activityName")+"");
				errorList.add(error);
			}catch(Exception e){
				e.printStackTrace();
				log.info("工单回执操作失败！——>>>>" + map.get("recId"));
				HashMap<String, String> error = new HashMap<String, String>();
				error.put("code", IContants.BUSI_ERROR_CODE);
				error.put("recId", map.get("recId")+"");
				error.put("activityName", map.get("activityName")+"");
				errorList.add(error);
			}
		}
		if(errorList.size()>0){
			throw new BoncExpection(errorList.get(0).get("code"),JSON.toJSONString(errorList));
		}
	}

	/**
	 * 向渠道协同里面添加接触记录
	 * @param map
	 * @param contactReq
	 */
	private void contactRecord(HashMap<String, Object> map,ContactReq contactReq) {
		HashMap<String, Object> contactRecord = new HashMap<String, Object>();
		contactRecord.put("tenantId", contactReq.getTenantId());
		contactRecord.put("channelId", map.get("channelId"));
		contactRecord.put("source", "ordercenter");
		contactRecord.put("activityId", map.get("activityId"));
		contactRecord.put("phoneNumber", map.get("PHONE_NUMBER"));
		try {
			contactRecord.put("contactDate", DateUtil.FomartDate.toFormat(map.get("contactDate")+"", DateFomart.EN_DATETIME, DateFomart.DATETIME));
		} catch (ParseException e) {
			throw new BoncExpection(IContants.CODE_FAIL,"contactDate fomart error!"+map.get("contactDate"));
		}
		contactRecord.put("contactResult", map.get("contactResult"));
		contactRecord.put("contactContent", map.get("contactMsg"));	
		contactRecord.put("contactCode", map.get("contactCode"));
		contactRecord.put("activityName", map.get("activityName"));
		contactRecord.put("loginName", map.get("loginName"));
		contactRecord.put("uniqueId", map.get("ext5"));
		
		List<HashMap<String, Object>> req = new ArrayList<HashMap<String,Object>>();
		req.add(contactRecord);
		
		if(null==url){
			url = AsynDataIns.getValueFromGlobal("CHANNEL_COORD_ADD_URL");
		}
		log.info(" start add channelchoord record!" + HttpUtil.sendPost(url, JSONObject.toJSONString(req)));
	}

	@TargetDataSource(name="mysqlslaveuni076")
	private HashMap<String, Object> findActivity(HashMap<String, Object> map) {
		return contactMapper.findActivity(map);
	}

	@TargetDataSource(name="mysqlslaveuni076")
	private HashMap<String, Object> findOrderInfo(HashMap<String, Object> map) {
		return contactMapper.findOrderInfo(map);
	}

	//根据当前时间计算出　月表
	private void insertContact(HashMap<String, Object> map) {
		String logTable = "PLT_ORDER_PROCESS_LOG_"+DateUtil.getCurMonth();
		map.put("logTable", logTable);
		contactMapper.insertContact(map);
	}

	private boolean contactSuccess(HashMap<String, Object> map) {
		String contactCode = map.get("contactCode")+"";
		log.info("工单回执状态——>>>>"+contactCode);
		switch (map.get("channelId")+"") {
		case IContants.YX_CHANNEL:
			if("101".equals(contactCode)||"102".equals(contactCode)||"103".equals(contactCode)||"104".equals(contactCode)||"121".equals(contactCode)){
				log.info("工单回执成功!");
				return true;
			}
			return false;
		case IContants.TC_CHANNEL_1:
			if("101".equals(contactCode)||"121".equals(contactCode)){
				log.info("工单回执成功!");
				return true;
			}else if("201".equals(contactCode)){
				return false;
			}else{
				throw new BoncExpection(IContants.CODE_FAIL,"error contactCode!"+contactCode);
			}
		case IContants.TC_CHANNEL_2:
			if("101".equals(contactCode)||"121".equals(contactCode)){
				log.info("工单回执成功!");
				return true;
			}else if("201".equals(contactCode)){
				return false;
			}else{
				throw new BoncExpection(IContants.CODE_FAIL,"error contactCode!"+contactCode);
			}
		case IContants.WX_CHANNEL:
			return true;
		case IContants.WT_CHANNEL:
			if("0201".equals(contactCode)||"0202".equals(contactCode)){
				return true;
			}else if("0203".equals(contactCode)){
				return false;
			}else{
				throw new BoncExpection(IContants.CODE_FAIL,"error contactCode!"+contactCode);
			}
		case IContants.ST_CHANNEL:
			if("0201".equals(contactCode)||"0202".equals(contactCode)){
				return true;
			}else if("0203".equals(contactCode)){
				return false;
			}else{
				throw new BoncExpection(IContants.CODE_FAIL,"error contactCode!"+contactCode);
			}
		case IContants.WSC_CHANNEL:
			if("0201".equals(contactCode)||"0202".equals(contactCode)){
				return true;
			}else if("0203".equals(contactCode)){
				return false;
			}else{
				throw new BoncExpection(IContants.CODE_FAIL,"error contactCode!"+contactCode);
			}
		default:
			throw new BoncExpection(IContants.CODE_FAIL,"error channelId!");
		}
	
	}

	/**
	 * 修改工单对应的统计信息
	 * @param map
	 */ 
	private void modifyStatistic(HashMap<String, Object> orderInfo,HashMap<String, Object> map) {
		HashMap<String, Object> statistic = new HashMap<String, Object>();
		statistic.put("LAST_CHANNEL_STATUS", orderInfo.get("CHANNEL_STATUS"));
		if("0".equals(orderInfo.get("CONTACT_CODE"))){
			statistic.put("lastCode", "ITEM"+orderInfo.get("CONTACT_CODE"));
			statistic.put("isFirst", true);
		}else{
			statistic.put("isFirst", false);
			if("1".equals(orderInfo.get("CONTACT_TYPE"))){
				statistic.put("lastType", "TYPE1");
			}else if("2".equals(orderInfo.get("CONTACT_TYPE"))){
				statistic.put("lastType", "TYPE2");
			}else if("3".equals(orderInfo.get("CONTACT_TYPE"))){
				statistic.put("lastType", "TYPE3");
			}
			statistic.put("lastCode", "ITEM"+orderInfo.get("CONTACT_CODE"));
		}
		statistic.put("thisType", "TYPE"+map.get("contactType"));
		statistic.put("thisCode", "ITEM"+map.get("contactCode"));
		statistic.put("tenantId", map.get("tenantId"));
		statistic.put("orderDate", map.get("orderDate"));
		statistic.put("orgPath", map.get("orgPath"));
		statistic.put("activityId", map.get("ACTIVITY_SEQ_ID"));
		statistic.put("THIS_CHANNEL_STATUS", map.get("CHANNEL_STATUS"));
		statistic.put("orderOrgPath", orderInfo.get("ORG_PATH"));//工单归属的组织机构路径
		statistic.put("orderLoginId", orderInfo.get("WENDING_FLAG"));//工单归属的LOGIN_ID
		statistic.put("areaNo", orderInfo.get("AREA_NO"));//地市
		statistic.put("todayContact", orderInfo.get("TODAY_CONTACT"));
		contactMapper.contactStatistc(statistic);
	}
}