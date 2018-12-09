package com.bonc.busi.interfaces.service.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.interfaces.mapper.TalkMapper;
import com.bonc.busi.interfaces.service.TalkService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.varsion.monitor.TalkSync;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.DateUtil;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.JsonUtil;
import com.bonc.utils.StringUtil;

@Service("talkService")
public class TalkServiceImpl implements TalkService{

	private static final Logger log = Logger.getLogger(TalkServiceImpl.class);
	
	private static String flowUrl  = null;
	
	private static String packageUrl = null;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private TalkMapper talkMapper;
	
	@Autowired
	private BusiTools  AsynDataIns;
	
	/**
	 * must parameter tenantId and talkWord
	 * 
	 * tenantId
	 * talkWord
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public String exchangeTalkVal(HashMap<String, String> talk) {
		String tenantId = talk.get("tenantId");
		if(StringUtil.validateStr(tenantId)){
			log.error("tenantId must not empty!");
			return talk.get("talkWork");
		}
		
		String partFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
		talk.put("partFlag", partFlag);
		// put user query fields from Talk monitor
		talk.put("extInfo", TalkSync.getExtInfo(tenantId));
		
		HashMap<String, Object> userInfo = null;
		// userId not null, first query by userId
		if(!StringUtil.validateStr(talk.get("userId"))){
			userInfo = talkMapper.getUserInfo(talk);
			
		}
		// userInfo is null but phoneNumber not null ,query user by phoneNumber, dataType is confirm KD OR MB
		if(null==userInfo&&!StringUtil.validateStr(talk.get("phoneNumber"))&&!StringUtil.validateStr(talk.get("dataType"))){
			userInfo = talkMapper.getUserInfoByPhone(talk);
		}
		
		// not find this user,throw EXP jump out
		if(null==userInfo){
			throw new BoncExpection(IContants.CODE_FAIL,"not find userInfo");
		}
		
		// get where field is codeTable 
		HashMap<String, String> codeTable = CodeUtil.getCodeMap(tenantId, IContants.PLT_USER_LABEL);
		String content = talk.get("talkWork");
		HashMap<String, CodeReq> codes=CodeUtil.getValue(tenantId, TalkSync.getTalkName());
		for(String key:codes.keySet()){
			// get MYSQL field name ,judge is code,if true replace code,else straight replace
			String fieldName=codes.get(key).getFieldValue();
			
			//if contain this variable ,replace it ; 
			if(content.contains(key)){
				if(null==userInfo.get(fieldName)){
					content.replace(fieldName, "");
				}
				if(codeTable.containsKey(fieldName)){
					content = content.replace(key,CodeUtil.getValue(tenantId, codeTable.get(fieldName), userInfo.get(fieldName)+""));
				}else{
					content = content.replace(key, userInfo.get(fieldName)+"");
				}
			}
		}
		
		HashMap<String, Object> req = new HashMap<String, Object>();
		
		//if content package variable ,through interface getRealFlow replace it 
		if(content.contains("${包名称}")){
			try {
				// set request parameter request interface
				setUserInfo(req, userInfo);
				
				if(packageUrl==null){
					packageUrl = AsynDataIns.getValueFromGlobal("XBUILDERORACLE_REALPRODUCT");
//					packageUrl="http://10.245.2.222/xbuilderoracle/restful/h2service/getRealUserProduct";
					log.info("get getRealUserProduct url="+packageUrl);
				}
				
				String result = HttpUtil.doPost(packageUrl, req);
				HashMap map = JSON.parseObject(result,HashMap.class);
				if("0000".equals(map.get("msgcode"))&&null!=map.get("result")){
					content = content.replace("${包名称}",map.get("result")+"");
				}else{
					content = content.replace("${包名称}", "未查询出结果！");
				}
			} catch (Exception e) {
				log.error("getRealUserProduct fail !");
				content = content.replace("${包名称}", "获取实时数据失败！");
				e.printStackTrace();
			}
		}
		
		//if content flow variable ,through interface getRealFlow replace it 
		if(content.contains("${总流量}")||content.contains("${已使用流量}")||content.contains("${剩余流量}")){
			try {
				if(flowUrl==null){
					flowUrl = AsynDataIns.getValueFromGlobal("XBUILDERORACLE_REALFLOW");
					//test URL
//					flowUrl="http://10.245.2.222/xbuilderoracle/restful/h2service/getRealFlow";
					log.info("get getRealFlow url="+flowUrl);
				}
				
				// if not contains ${包名称} ,need reset userInfo 
				if(!content.contains("${包名称}")){
					setUserInfo(req, userInfo);
				}
				
				// set search data ,because 
				req.put("searchDate", DateUtil.CurrentDate.currentDateFomart(DateUtil.DateFomart.MONTH));
				
				String result = HttpUtil.doPost(flowUrl, req);
				Map map = JsonUtil.getJson(result, Map.class);
				if("0000".equals(map.get("msgcode"))){
					//resolve the result 
					DecimalFormat FOMART = new DecimalFormat("#.##");
					Double resulttotal = Double.parseDouble(map.get("resulttotal")+"");
					Double resultremain = Double.parseDouble(map.get("resultremain")+"");
					Double all = resultremain+resulttotal;
					content = content.replace("${已使用流量}", FOMART.format(resulttotal)+"MB")
							.replace("${剩余流量}",FOMART.format(resultremain)+"MB")
							.replace("${总流量}",FOMART.format(all)+"MB");
				}else{
					content = content.replace("${总流量}", "未查询出结果！")
							.replace("${已使用流量}","未查询出结果！")
							.replace("${剩余流量}","未查询出结果！");
				}
			} catch (Exception e) {
				content = content.replace("${总流量}", "获取实时数据失败！")
						.replace("${已使用流量}","获取实时数据失败！")
						.replace("${剩余流量}","获取实时数据失败！");
				log.error("getRealFlow fail!");
				e.printStackTrace();
			}
		}
		
		return content;
	}
	
	private void setUserInfo(HashMap<String, Object> req,HashMap<String, Object> userInfo){
		//set phoneNumber
		if(null!=userInfo.get("DEVICE_NUMBER")){
			req.put("phoneNum", userInfo.get("DEVICE_NUMBER"));
		}else {
			throw new BoncExpection(IContants.BUSI_ERROR_CODE, "DEVICE_NUMBER field not exist!");
		}
		//set serviceType
		Object SERVICE_TYPE = userInfo.get("MB_NET_TYPE");
		if(null==SERVICE_TYPE){
			throw new BoncExpection(IContants.BUSI_ERROR_CODE, "MB_NET_TYPE field not exist");
		}else{
			if((SERVICE_TYPE+"").startsWith("40")){
				req.put("serviceType", "T");
			}else{
				req.put("serviceType", "G");
			}
		}
	}

	 
	/**
	 * userId and phoneNumber,dataType not all null
	 * because phoneNumber can confirm a user,so need dataType ensure one user
	 * phoneNumber 
	 * dateType MB OR KD
	 * userId 
	 */
	@Override
	public HashMap<String, String> exchangeTalkVals(List<HashMap<String, String>> req) {
		HashMap<String, String> resp = new HashMap<String, String>();
		try{
			for(HashMap<String, String> item: req){
				String result = exchangeTalkVal(item);
				if(!StringUtil.validateStr(item.get("phoneNumber"))){
					resp.put(item.get("phoneNumber"), result);
				}
				if(!StringUtil.validateStr(item.get("userId"))){
					resp.put(item.get("userId"), result);
				}
			}
			resp.put("code", IContants.CODE_SUCCESS);
			resp.put("msg", IContants.MSG_SUCCESS);
		}catch (BoncExpection e){
			resp.put("code", e.getCode());
			resp.put("msg", e.getMsg());
		}catch (Exception e) {
			log.error(e.getMessage());
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", IContants.SYSTEM_ERROR_MSG);
		}
		return resp;
	}

}
