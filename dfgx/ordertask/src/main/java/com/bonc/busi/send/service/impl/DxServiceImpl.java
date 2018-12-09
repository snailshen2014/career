package com.bonc.busi.send.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bonc.busi.send.mapper.SendMapper;
import com.bonc.busi.send.model.sms.DxReq;
import com.bonc.busi.send.model.sms.DxResp;
import com.bonc.busi.send.model.sms.SmsReq;
import com.bonc.busi.send.model.sms.SmsStatistics;
import com.bonc.busi.send.service.SentService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.h2.pojo.UserFlowInfoBean;
import com.bonc.h2.thread.UserFlowInfo;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Service("dxService")
@ConfigurationProperties(prefix = "channel.sms", ignoreUnknownFields = false)
public class DxServiceImpl implements SentService{
	private static final Logger logger = Logger.getLogger(DxServiceImpl.class);
	@Autowired
	private SendMapper sendMapper;

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 短信发送统计同步
	 */
	@Override
	public void synchSend(){
		//		logger.info("------进入短信统计刷新-----------------------");
		if(IContants.SMS_STATC_TASK.equals("1")){
			return;
		}
		IContants.SMS_STATC_TASK = "1";

		List<HashMap<String, Object>> tanantList = sendMapper.findTenant();
		HashMap<String, Object> tanantMap;
		if(tanantList!=null&&tanantList.size()>0){
			for(int j=0;j<tanantList.size();j++){
				tanantMap = tanantList.get(j);

				HashMap<String, Object> reqMap = new HashMap<String, Object>();
				reqMap.put("tenantId",tanantMap.get("TENANT_ID"));
				reqMap.put("channelId", IContants.DX_CHANNEL);
				reqMap.put("IS_FINISH", "0");
				try{
					List<HashMap<String, Object>> list = sendMapper.findSmsNoSend(reqMap);
					if(list!=null&&list.size()>0){
						//Integer count;
						HashMap<String, Object> map;
						SmsStatistics smss;
						for(int i=0;i<list.size();i++){
							map = list.get(i);
							//count = (Integer)map.get("SEND_ALL_COUNT");
							smss = new SmsStatistics();
							smss.setExternalId((String)map.get("EXTERNAL_ID"));
							smss.setSmsResource("1");
							smss.setTenantId(String.valueOf(map.get("TENANT_ID")));
							smss.setActivitySqlId(String.valueOf(map.get("ACTIVITY_SEQ_ID")));
							synchSMS(smss);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		IContants.SMS_STATC_TASK = "0";
	}
	/**
	 * 短信接口发送
	 * 维护一个活动MAP 循环往里面放活动的信息，每次从Map取 Map没有 从表里面取，
	 * 再做一个活动的Map存储 是否可以发送 只判断一次活动是否可发送
	 */
	@Override
	public void sent() {
		if(IContants.SMS_SEND_TASK.equals("1")){
			return;
		}

		IContants.SMS_SEND_TASK = "1";
		//一、取可发送的短信列表
		HashMap<String, Object> reqMap = new HashMap<String, Object>();

		List<HashMap<String, Object>> tanantList = sendMapper.findTenant();
		Long times = new Date().getTime();

		if(tanantList!=null&&tanantList.size()>0){
			HashMap<String, Object> tanantMap;
			List<HashMap<String, Object>> dxActivity;
			ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
			for(int j=0;j<tanantList.size();j++){
				tanantMap = tanantList.get(j);
				//查询有效的短信活动
				//logger.info("------进入短信发送-----------------"+url+"------");
				reqMap.put("tenantId", tanantMap.get("TENANT_ID"));
				reqMap.put("channelId", IContants.DX_CHANNEL);

				//TODO 取短信可以发送的活动
				reqMap.put("status","0");//未执行状态
				dxActivity = sendMapper.findActivityDetail(reqMap);

				if(dxActivity!=null&&dxActivity.size()>0){
					for(int i=0;i<dxActivity.size();i++){
						final HashMap<String, Object> activity = dxActivity.get(i);

						reqMap.put("activitySqlId",String.valueOf(activity.get("ACTIVITY_SEQ_ID")));
						reqMap.put("status","2");//预执行
						sendMapper.modifyDuanxinStatus(reqMap);

						singleThreadExecutor.execute(new Runnable(){  
							public void run() {  
								try {
									actSendSms(activity); 
								} catch (Exception e) {  
									e.printStackTrace();  
								}  
							}  
						});
					}
				}

			}
		}

		times = (new Date().getTime()-times)/1000;
		//		logger.info("------短信发送结束-------------"+times+"秒----------");
		IContants.SMS_SEND_TASK = "0";
	}

	public void actSendSms(HashMap<String, Object> activity){
		if(activity==null){
			return ;
		}
		try {
			//账期
			String tenantId =  String.valueOf(activity.get("TENANT_ID"));

			//活动批量号
			String activitySqlId = String.valueOf(activity.get("ACTIVITY_SEQ_ID"));

			//获取话术变量
			List<HashMap<String, Object>> fieldList = codeFiledList(tenantId);
			//字段对应码值
			HashMap<String, Object> codeMap = codeFieldMap(tenantId,fieldList);
			//需要查询跟话术有关的字段
			String userSqlStr = userSql(fieldList,"u");
			//获取短信网关
			String smsSetId = getSmsId(tenantId);
			//添加统计
			HashMap<String, Object> smss = addSmss(activity);
			String externalId = String.valueOf(smss.get("externalId"));

			HashMap<String, Object> reqMap = new HashMap<String, Object>();
			reqMap.put("tenantId",tenantId);
			reqMap.put("activitySqlId",activitySqlId);
			reqMap.put("orderStatus",IContants.ORDER_STATUS_GET);
			reqMap.put("userColum",userSqlStr);
			reqMap.put("channelId",IContants.DX_CHANNEL);

			List<HashMap<String, Object>> smsList;
			ArrayList<DxReq> reqsList;
			HashMap<String, Object> taskSms;
			DxReq dxbean;
			DxResp rebean;
			int i;
			String sendContent;
			int[] levArr = new int[5];
			levArr[0] = 4;
			levArr[1] = 3;
			levArr[2] = 2;
			levArr[3] = 1;
			Object objlev = activity.get("ACTIVITY_LEVEL");
			Integer lev = objlev!=null&&objlev.toString().length()>0?Integer.valueOf(objlev.toString()):3;
			lev =lev!=null?lev:3;

			while(true){
				reqMap.put("prepareSendStatus",IContants.CHANNEL_STATUS_PRE);
				reqMap.put("channelStatus",IContants.CHANNEL_STATUS_WAIT);
				sendMapper.updateDxOrderList(reqMap);

				smsList = sendMapper.findDxOrderList(reqMap);
				//如果没数据了 跳出循环
				if(smsList.size()==0){
					break;
				}
				reqsList = new ArrayList<DxReq>();
				for(i=0;i<smsList.size();i++){
					taskSms = smsList.get(i);
					dxbean = new DxReq();


					//短信发送内容
					sendContent = activity.get("SMS_WORDS")!=null?activity.get("SMS_WORDS").toString():"";

					dxbean.setSendContent(sendContent(fieldList,codeMap,taskSms,sendContent));
					dxbean.setSmsSetId(smsSetId);
					dxbean.setTelPhone(String.valueOf(taskSms.get("PHONE_NUMBER")));
					dxbean.setSendLev(levArr[lev]);
					dxbean.setExternalId(externalId);
					reqsList.add(dxbean);

					if(i>0&&i%99==0){
						//短信批量发送
						rebean = sendDx(reqsList);
						reqsList = new ArrayList<DxReq>();
					}
				}
				if(reqsList.size()>0){
					rebean = sendDx(reqsList);
					reqsList = new ArrayList<DxReq>();
				}
				sendMapper.modifyPlDxStatus(reqMap);
				//TODO 修改pl_order_info_sms 的CHANNEL_STATUS为404

			}
			reqMap.put("status","1");//已执行
			sendMapper.modifyDuanxinStatus(reqMap);
		}catch (Exception e) {
			e.printStackTrace();
			//发送失败重新命名成失败状态　
			activity.put("channelStatus", IContants.CHANNEL_STATUS_FAIL);
			sendMapper.modifyDxLose(activity);
		}

	}

	/**
	 * 短信批量发送 最多不超过100条
	 * @param reqs
	 * @return
	 */
	private DxResp sendDx(ArrayList<DxReq> reqs) {
		DxResp dxResp = new DxResp();

		String jsonString = JSON.toJSONString(reqs);
		String resp = HttpUtil.sendPost(url+IContants.sendlist, jsonString);
		JSONArray jsonArray = JSON.parseArray(resp);
		JSONObject jsonObject=(JSONObject) jsonArray.get(0);

		Integer returnflag = jsonObject.getInteger("returnflag");
		if(returnflag==0){
			dxResp.setFlag(true);
			dxResp.setMsg("发送成功！");
		}else{
			dxResp.setFlag(false);
			dxResp.setMsg("发送失败！");
		}
		return dxResp;
	}



	public void refleshSmss(HashMap<String, Object> smsMap){
		sendMapper.updateSmsStatistic(smsMap);
	}
	public void updateSmss(HashMap<String, Object> smsMap){
		sendMapper.updateSmsStatisticFlag(smsMap);
	}
	/**
	 * 添加工单汇总
	 * @param activity
	 * @return
	 */
	private HashMap<String, Object> addSmss(HashMap<String, Object> activity){
		HashMap<String, Object> smsMap = new HashMap<String, Object>();
		smsMap.put("activitySeqId",String.valueOf(activity.get("ACTIVITY_SEQ_ID")));
		smsMap.put("tenantId", String.valueOf(activity.get("TENANT_ID")));
		smsMap.put("channelId", IContants.DX_CHANNEL);

		List<HashMap<String, Object>> list = sendMapper.findSmsStatistic(smsMap);
		/**
		 * 如果存在，不新增，如果不存在则新增
		 */
		if(list!=null&&list.size()>0){
			HashMap<String, Object> countTask = list.get(0);

			smsMap.put("externalId", countTask.get("EXTERNAL_ID"));
			smsMap.put("sendAllCount", countTask.get("SEND_ALL_COUNT"));
			smsMap.put("sendAllNum", countTask.get("SEND_ALL_NUM"));
			return smsMap;
		}else{
			smsMap.put("orderStatus", IContants.ORDER_STATUS_GET);
			Long sendAllCount = sendMapper.selectCount(smsMap);

			smsMap.put("externalId", StringUtil.getUUID());
			smsMap.put("sendAllCount", sendAllCount);
			smsMap.put("sendAllNum", sendAllCount);
			sendMapper.addSmsStatistic(smsMap);
			return smsMap;
		}
	}
	/**
	 * 一个租户可能会有多个网关，但是一般就一个
	 * @param tanintId
	 * @return
	 */
	public String getSmsId(String tenantId){
		logger.info("获取当前租户ID的网关ID="+tenantId);
		String resp = null;
		try{
			resp = HttpUtil.doGet(url+IContants.tenantid+"/"+tenantId, null);
		}catch(Exception e){
			logger.error("系统异常-连接短信平台失败！");
			throw new BoncExpection(IContants.SYSTEM_ERROR_MSG,IContants.SYSTEM_ERROR_MSG+"——>连接短信平台失败！");
		}

		@SuppressWarnings("rawtypes")
		List<HashMap> json = JSON.parseArray(resp, HashMap.class);
		String falg = (String) json.get(0).get("returnflag");

		if("0".equals(falg)){
			String smssetid = json.get(0).get("smssetid").toString();
			List<String> smsIds = JSON.parseArray(smssetid, String.class);
			String smsSetId = "";
			if(smsIds!=null&&smsIds.size()>0){
				smsSetId = smsIds.get(0);
			}
			return smsSetId;
		}else{
			return null;
		}

	}
	/**
	 * 短信批量发送 最多不超过100条
	 * @param reqs
	 * @return
	 */
	private SmsReq sendDx(SmsStatistics sms){
		SmsReq bean = new SmsReq();
		String jsonString = JSON.toJSONString(sms);
		String resp = HttpUtil.sendPost(url+IContants.statistics, jsonString);

		try{
			//			logger.info("------------------------"+resp);
			JSONArray jsonArray = JSON.parseArray(resp);
			JSONObject jsonObject=(JSONObject) jsonArray.get(0);

			Integer returnflag = jsonObject.getInteger("returnflag");

			if(returnflag!=null&&returnflag==1){
				bean.setMsg("业务号不存在！");
				bean.setFlag(false);
			}else{
				bean.setFlag(true);
				bean.setSendAllNum(jsonObject.getLong("allSmsNum"));
				bean.setSendSucNum(jsonObject.getLong("sendSucNum"));
				bean.setErrNum(jsonObject.getLong("errNum"));

				bean.setSendErrNum(jsonObject.getLong("sendErrNum"));
				bean.setFormatErrNum(jsonObject.getLong("formatErrNum"));
				bean.setFormatSucNum(jsonObject.getLong("formatSucNum"));
			}
		}catch(Exception e){
			bean.setMsg("业务号不存在！");
			bean.setFlag(false);
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 短信统计同步
	 * @param smss
	 */
	private void synchSMS(SmsStatistics smss) {
		/*如果工单数为0的话直接将状态置为1*/
		HashMap<String, Object> smsCondition=new HashMap<String, Object>();
		smsCondition.put("tenantId", smss.getTenantId());
		smsCondition.put("channelId",IContants.DX_CHANNEL);
		smsCondition.put("IS_FINISH", "1");
		smsCondition.put("externalId", smss.getExternalId());
		smsCondition.put("activitySqlId", smss.getActivitySqlId());
		List<HashMap<String, Object>> statisticlist =sendMapper.findOneSmsStatistic(smsCondition);
		Integer sendAllNum=0;
		for(HashMap<String, Object> statistic:statisticlist)
		{
			if(null!=statistic)
			{
				if(0==(Integer)statistic.get("SEND_ALL_COUNT"))
				{
					//相当于无效的活动
					updateSmss(smsCondition);
					// System.out.println("----该活动没有任何的工单--");
				}
				sendAllNum=(Integer)statistic.get("SEND_ALL_NUM");

			}
		}
		judgeIsFinish(smss,sendAllNum);
	}
	/**
	 * 判断短信是否发送完成
	 * @param sendAllNum 
	 * */
	private void judgeIsFinish(SmsStatistics smss, Integer sendAllNum)
	{
		Long sendAllNumLong=Long.valueOf(sendAllNum);

		//短信失否发送完成 
		SmsReq sr = sendDx(smss);
		if(!sr.getFlag()){
			return;
		}
		if(sr!=null&&sr.getSendAllNum()!=null&&sr.getSendAllNum()>0&&sendAllNum>0){
			HashMap<String, Object> smsMap = new HashMap<String, Object>();
			smsMap.put("externalId", smss.getExternalId());
			smsMap.put("SEND_NUM", sr.getSendAllNum());
			smsMap.put("SEND_SUC_NUM", sr.getSendSucNum());
			smsMap.put("SEND_ERR_NUM", sr.getErrNum());
			smsMap.put("tenantId", smss.getTenantId());
			smsMap.put("channelId",IContants.DX_CHANNEL);
			refleshSmss(smsMap);
			if(sr.getSendSucNum()!=null&&sr.getSendAllNum().longValue()==(sr.getSendSucNum().longValue()+sr.getErrNum().longValue())
					&&sendAllNumLong.longValue()==sr.getSendAllNum().longValue()
					){
				smsMap.put("IS_FINISH", "1");
				smsMap.put("activitySqlId", smss.getActivitySqlId());
				updateSmss(smsMap);
				//更新错误的短信
				updateErrorSmss(smsMap,sr.getErrNum());
				sendMapper.smsToHistory(smsMap);//将发送完的短信工单移入历史表
				sendMapper.delSms(smsMap);//将发送完的短信工单删除
			}
		}
	}

	/**
	 *  分批从接口中获取错误的短信，然后更新到数据库 
	 * */
	private void updateErrorSmss(HashMap<String, Object> smsMap,Long totalErrorSmss){

		String externalId=(String) smsMap.get("externalId");

		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String sendTime = formatter.format(currentTime);

		//每次从接口查询的数量（错误数量不会太多）
		Long perSize=1000L;
		Long pageSize=totalErrorSmss/perSize;
		Long remain=totalErrorSmss% perSize;

		if(pageSize!=0)
		{
			//循环访问接口
			for(Long currentPage=0L;currentPage<pageSize;currentPage++){
				DxReq req=new DxReq();
				req.setExternalId(externalId);
				req.setStartNum(perSize*currentPage);
				req.setEndNum(perSize*(currentPage+1));
				req.setSendTime(sendTime);
				DxResp dxresp=	findError(req);

				//TODO 修改pl_order_info_sms 的CHANNEL_STATUS为404
				StringBuilder phoneStr = new StringBuilder();
				for(DxReq Singlereq:dxresp.getErrorList())
				{
					if(Singlereq.getTelPhone().trim().length()>0){
						if(phoneStr.length()>0){
							phoneStr.append(",");
						}
						phoneStr.append("'"+Singlereq.getTelPhone()+"'");
					}
				}

				//短信发送失败
				smsMap.put("channel_status", "404");
				smsMap.put("phoneStr", phoneStr.toString());
				sendMapper.modifyOrderInfoSmsStatus(smsMap);
			}
		}

		//剩余的不足perSize的
		if(remain!=0)
		{
			DxReq req=new DxReq();
			req.setExternalId(externalId);
			req.setStartNum(perSize*pageSize);
			req.setEndNum(totalErrorSmss+1);
			req.setSendTime(sendTime);
			DxResp dxresp=	findError(req);
			//TODO 修改pl_order_info_sms 的CHANNEL_STATUS为404
			StringBuilder phoneStr = new StringBuilder();
			for(DxReq Singlereq:dxresp.getErrorList())
			{
				if(Singlereq.getTelPhone().trim().length()>0){
					if(phoneStr.length()>0){
						phoneStr.append(",");
					}
					phoneStr.append("'"+Singlereq.getTelPhone()+"'");
				}
			}
			//短信发送失败
			smsMap.put("channel_status", "404");
			smsMap.put("phoneStr", phoneStr.toString());
			sendMapper.modifyOrderInfoSmsStatus(smsMap);
		}
	}

	/*public static void main(String[] args) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String sendTime = formatter.format(currentTime);
		DxReq req=new DxReq();
		req.setExternalId("test1234");
		req.setStartNum(0);
		req.setEndNum(100);
		req.setSendTime(sendTime);
		DxResp dxresp=	findError(req);
		System.out.println("_---------"+dxresp.getErrorList().get(0).getTelPhone());
		System.out.println("_---------"+dxresp.getErrorList().size());

	}*/

	/**
	 * 获取错误的短信信息
	 * @param reqs
	 * @return
	 */
	private  DxResp findError(DxReq reqs) {
		DxResp dxResp = new DxResp();
		String jsonString = JSON.toJSONString(reqs);
		String resp = HttpUtil.sendPost(url+IContants.findErrorSms, jsonString);
		JSONArray jsonArray = JSON.parseArray(resp);
		JSONObject jsonObject=(JSONObject) jsonArray.get(0);

		Integer returnflag = jsonObject.getInteger("returnflag");

		if(returnflag==0){
			dxResp.setFlag(true);
			dxResp.setMsg("收到错误短信信息");
			List<DxReq> errorList = JSON.parseObject(JSON.toJSONString(jsonObject.get("msg")), new TypeReference<List<DxReq>>() {});
			dxResp.setErrorList(errorList);
		}
		return dxResp;
	}
	/**
	 * 短信发送内容替换掉变量
	 * @param codelist
	 * @param sms
	 * @param sendContent
	 * @return
	 */
	private String sendContent(List<HashMap<String, Object>> codelist, HashMap<String, Object> codeListMap,HashMap<String, Object> sms,String sendContent){
		if(codelist==null||codelist.size()<1||sms==null||sendContent==null||sendContent.trim().length()<1){
			return sendContent;
		}

		HashMap<String, Object> codeMap;
		String fieldkey;
		String fieldName;
		String fieldTable;
		String fieldvalue;
		Object codeValue;
		DecimalFormat df = new DecimalFormat("######0.00");   

		for(int i=0;i<codelist.size();i++){
			codeMap = codelist.get(i);
			fieldkey = codeMap.get("FIELD_KEY")!=null?codeMap.get("FIELD_KEY").toString():"";
			fieldName = codeMap.get("FIELD_NAME")!=null?codeMap.get("FIELD_NAME").toString():"";
			fieldTable = codeMap.get("TABLE_NAME")!=null?codeMap.get("TABLE_NAME").toString():"";
			if(fieldName.trim().length()<1||fieldkey.trim().length()<1){
				continue;
			}
			fieldvalue = sms.get(fieldName)!=null?sms.get(fieldName).toString():"";

			if(sendContent.indexOf(fieldkey)!=-1){
				codeValue = codeListMap.get(fieldTable+"_"+fieldvalue);
				fieldvalue = codeValue!=null?codeValue.toString():fieldvalue;
				if(fieldvalue.trim().length()>0&&fieldName.equalsIgnoreCase("ACCT_FEE")){
					fieldvalue = df.format(Integer.valueOf(fieldvalue)/100.0);
				}

				sendContent = sendContent.replace(fieldkey,fieldvalue);
			}
		}
		//实时信息变量替换，实时变量不做码表替换
		if(sendContent.contains("${总流量}")||sendContent.contains("${已使用流量}")||sendContent.contains("${剩余流量}")){
			try {
				UserFlowInfo ufi=new UserFlowInfo("13213153720","201612","02","G");
				UserFlowInfoBean ufib=ufi.getUserFlowInfo();
				DecimalFormat FOMART = 	new DecimalFormat("#.##");
				sendContent = sendContent.replace("${总流量}", ufib.getTotal_last_flow())
						.replace("${已使用流量}",FOMART.format(ufib.getBefore_last_flow()/1024.0))
						.replace("${剩余流量}",FOMART.format(ufib.getPackage_last_flow()/1024.0));
			} catch (Exception e) {
				logger.error("流量信息获取失败！");
				e.printStackTrace();
			}
		}
		return sendContent;
	}
	/**
	 * 话术变量字段名称转换
	 * @param tenantId
	 * @return
	 */
	private List<HashMap<String, Object>> codeFiledList(String tenantId){
		HashMap<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("tenantId", tenantId);
		reqMap.put("TABLE_NAME", "'TALK_VARIABLE'");
		List<HashMap<String, Object>> fieldList = sendMapper.findTalkList(reqMap);
		HashMap<String, Object> fieldMap;
		String fieldName;
		for(int i=0;i<fieldList.size();i++){
			fieldMap = fieldList.get(i);
			fieldName = fieldMap.get("FIELD_VALUE")!=null?fieldMap.get("FIELD_VALUE").toString():"";
			
			if(fieldName.equalsIgnoreCase("SERVICE_TYPE_DESC")){
				fieldList.get(i).put("FIELD_NAME", "USER_TYPE");
			}else if(fieldName.equalsIgnoreCase("AREA_NO")){
				fieldList.get(i).put("FIELD_NAME","AREA_ID");
			}else if(fieldName.equalsIgnoreCase("AGREEMENT_TYPE")){
				fieldList.get(i).put("FIELD_NAME","MB_AGREEMENT_TYPE");
			}else if(fieldName.equalsIgnoreCase("AGREEMENT_EXPIRE_TIME")){
				fieldList.get(i).put("FIELD_NAME","MB_AGREEMENT_END_TIME");
				fieldList.get(i).put("FIELD_NAME_KD","KD_AGREEMENT_END_TIME");
			}else if(fieldName.equalsIgnoreCase("CHNL_TYPE4")){
				fieldList.get(i).put("FIELD_NAME","ELECCHANNEL_FLAG");
			}else{
				fieldList.get(i).put("FIELD_NAME",fieldName);
			}
		}
		return fieldList;
	}
	/**
	 * 获取话术变量码表
	 * @param tenantId
	 * @param fieldList
	 * @return
	 */
	private HashMap<String, Object> codeFieldMap(String tenantId,List<HashMap<String, Object>> fieldList){
		HashMap<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("tenantId", tenantId);
		HashMap<String, Object> fieldMap;
		String fieldName;
		String fieldNameKd;
		StringBuilder fieldStr = new StringBuilder();
		for(int i=0;i<fieldList.size();i++){
			fieldMap = fieldList.get(i);
			fieldName = fieldMap.get("FIELD_NAME")!=null?fieldMap.get("FIELD_NAME").toString():"";
			fieldNameKd = fieldMap.get("FIELD_NAME_KD")!=null?fieldMap.get("FIELD_NAME_KD").toString():"";

			if(fieldName.trim().length()>0){
				if(fieldStr.length()>0){
					fieldStr.append(",");
				}
				fieldStr.append("'"+fieldName+"'");
			}
			if(fieldNameKd.trim().length()>0){
				if(fieldStr.length()>0){
					fieldStr.append(",");
				}
				fieldStr.append("'"+fieldNameKd+"'");
			}

		}
		reqMap.put("TABLE_NAME",fieldStr.toString());
		List<HashMap<String, Object>> codeList = sendMapper.findTalkList(reqMap);

		HashMap<String, Object> codeMap;
		String fieldKey;
		String fieldValue;
		HashMap<String, Object> code = new HashMap<String, Object>();
		for(int i=0;i<codeList.size();i++){
			codeMap = codeList.get(i);
			fieldName = codeMap.get("FIELD_NAME")!=null?codeMap.get("FIELD_NAME").toString():"";
			fieldKey = codeMap.get("FIELD_KEY")!=null?codeMap.get("FIELD_KEY").toString():"";
			fieldValue = codeMap.get("FIELD_VALUE")!=null?codeMap.get("FIELD_VALUE").toString():"";

			code.put(fieldName+"_"+fieldKey, fieldValue);
		}
		return code;
	}
	/**
	 * 将话术变量组合成查询字段
	 * @param codelist 变量集合
	 * @param pre 前缀
	 * @return
	 */
	private String userSql(List<HashMap<String, Object>> fieldlist,String pre){
		StringBuilder columSql = new StringBuilder();
		HashMap<String, Object> codeMap;
		String fieldName;
		String fieldNameKd;
		for(int i=0;i<fieldlist.size();i++){
			codeMap = fieldlist.get(i);
			fieldName = codeMap.get("FIELD_NAME")!=null?codeMap.get("FIELD_NAME").toString():"";
			fieldNameKd = codeMap.get("FIELD_NAME_KD")!=null?codeMap.get("FIELD_NAME_KD").toString():"";
			if(fieldName.trim().length()>0){
				columSql.append(",");
				if(pre!=null&&pre.trim().length()>0){
					columSql.append(pre);
					columSql.append(".");
				}
				columSql.append(fieldName);
			}
			if(fieldNameKd.trim().length()>0){
				columSql.append(",");
				if(pre!=null&&pre.trim().length()>0){
					columSql.append(pre);
					columSql.append(".");
				}
				columSql.append(fieldNameKd);
			}
		}
		return columSql.toString();
	}

	@Override
	public void sentFile() {
		// TODO Auto-generated method stub

	}
}
