package com.bonc.busi.interfaces.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.interfaces.mapper.AlertWinMapper;
import com.bonc.busi.interfaces.model.alertwin.ActivityQueryReq;
import com.bonc.busi.interfaces.model.alertwin.ActivityQueryResp;
import com.bonc.busi.interfaces.service.AlertWinService;
import com.bonc.busi.interfaces.service.TalkService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.common.datasource.TargetDataSource;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.DateUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Transactional
@Service("alertWinService")
public class AlertWinServiceImpl implements AlertWinService{
	
	private static final Logger log = Logger.getLogger(AlertWinServiceImpl.class);
	
	@Autowired
	private AlertWinMapper mapper;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private BusiTools  BusiTools;
	
	@Autowired
	private TalkService talkService;
	
	
	@TargetDataSource(name="mysqlslaveuni076")
	public List<HashMap<String, Object>> findActivitys(ActivityQueryReq req){
		return mapper.findActivitys(req);
	}
	
	@Override
	@Transactional
	public ActivityQueryResp activityQuery(ActivityQueryReq req) {
		ActivityQueryResp resp = new ActivityQueryResp();
		HashMap<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("tenantId", req.getTenantId());
		if("c".equals(req.getSubChannel())){
			req.setChannelId("81");
		}else if("d".equals(req.getSubChannel())){
			req.setChannelId("82");
		}else{
			throw new BoncExpection(IContants.CODE_FAIL, "暂时不支持该子渠道！");
		}
		reqMap.put("channelId", req.getChannelId());
		resp.setSourceCode("clyx");
		long start = System.currentTimeMillis();
		List<HashMap<String, Object>> items = findActivitys(req);
		long end = System.currentTimeMillis();
		log.info("弹窗服务活动查询sql耗时——>"+(end-start)/1000.0+"s");
		start = System.currentTimeMillis();
		if(items!=null&&items.size()>0){
			for(HashMap<String, Object> item:items){
				if(null!=item.get("RESERVE5")&&!"".equals(item.get("RESERVE5"))){
					item.put("smsDesc", item.get("RESERVE5"));
				}
				
				//判断是否发送短信标识,0 不发送短信，1 发送短信
				if(null==item.get("smsDesc")||"".equals(item.get("smsDesc"))){
					item.put("sendFlag", 0);
				}else{
					item.put("sendFlag", 1);
				}
				
				HashMap<String, String> talk = new HashMap<String, String>();
				talk.put("tenantId", req.getTenantId());
				talk.put("userId", item.get("userId")+"");
				talk.put("talkWork", item.get("marketDesc")+"");
				String marketDesc = talkService.exchangeTalkVal(talk);
				item.put("marketDesc", marketDesc);
				item.put("tenantId", req.getTenantId());
				
//				//日弹出次数设置
//				if(null==item.get("RESERVE1")||"".equals(item.get("RESERVE1"))){
//				        item.put("RESERVE1", "1");
//				}else{
//				        item.put("RESERVE1", Integer.parseInt(item.get("RESERVE1")+"")+1);
//				}
//				mapper.updateOrders(item);

				reqMap.put("recId", item.get("recId"));
				List<HashMap<String, Object>> historyList = new ArrayList<HashMap<String,Object>>();
				try {
					List<String> monthList = DateUtil.getMonthList(3);
					for(String monthStr:monthList){
						reqMap.put("month", monthStr.substring(4,6));
						historyList.addAll(findWinHistory(reqMap));
					}
				} catch (ParseException e) {
					log.error("查询近三个月的月份错误！");
				}
				item.put("contactList", historyList);
			}
		}
		end = System.currentTimeMillis();
		log.info("弹窗服务活动接触历史查询sql耗时——>"+(end-start)/1000.0+"s");
		resp.setItems(items);
		return resp;
	}

	@TargetDataSource(name="mysqlslaveuni076")
	private List<HashMap<String, Object>> findWinHistory(HashMap<String, Object> reqMap) {
		return mapper.findWinHistory(reqMap);
	}

	@Override
	public void updateLimitNum() {
		List<Map<String, Object>> tenantIds = BusiTools.getValidTenantInfo();
		for(Map<String, Object> map:tenantIds){
			map.put("CHANNEL_ID", IContants.TC_CHANNEL_1);
			mapper.updateLimitNum(map);
			map.put("CHANNEL_ID", IContants.TC_CHANNEL_2);
			mapper.updateLimitNum(map);
		}
	}

	@Override
	public void alertTimes(HashMap<String, String> req) {

		if(StringUtil.validateStr(req.get("recIds"))){
			throw new BoncExpection(IContants.CODE_FAIL,"recId is empty!");
		}
		if(StringUtil.validateStr(req.get("tenantId"))){
			throw new BoncExpection(IContants.CODE_FAIL,"tenantId is empty!");
		}
		if("c".equals(req.get("subChannel"))){
			req.put("channelId", "81");
		}else if("d".equals(req.get("subChannel"))){
			req.put("channelId", "82");
		}else{
			throw new BoncExpection(IContants.CODE_FAIL,"error subChannel "+req.get("subChannel"));
		}
		
		try{
			mapper.updateOrders(req);
			
			List<HashMap<String, Object>> items = mapper.selectOrders(req);
			req.put("month", req.get("alertTime").substring(5,7));
			req.put("uuid", StringUtil.getUUID());
			for(HashMap<String, Object> recId:items){
				req.put("recId", recId.get("REC_ID")+"");
				req.put("activitySeqId", recId.get("ACTIVITY_SEQ_ID")+"");
				req.put("phoneNum", recId.get("PHONE_NUMBER")+"");
				req.put("channelId", recId.get("CHANNEL_ID")+"");
				req.put("userId", recId.get("USER_ID")+"");
				mapper.addAlertLog(req);
			}
		}catch(Exception e){
			log.error(e.getMessage());
			throw new BoncExpection(IContants.SYSTEM_ERROR_CODE,IContants.SYSTEM_ERROR_MSG);
		}
	}

}
