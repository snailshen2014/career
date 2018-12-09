package com.bonc.busi.orderschedule.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.orderschedule.mapper.NoticeMapper;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.ParallelFunc;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.utils.HttpUtil;

public class NoticesSend implements Runnable{

	private final static Logger 		logger= LoggerFactory.getLogger(NoticesSend.class);
	private	JdbcTemplate 			JdbcTemplateIns = SpringUtil.getBean(JdbcTemplate.class);
	private	BusiTools					AsynDataIns = SpringUtil.getBean(BusiTools.class);
	private	NoticeMapper				noticeMapper = SpringUtil.getBean(NoticeMapper.class);
	private	PltCommonLog		PltCommonLogIns = new PltCommonLog();  // --- 日志变量 ---
	private	Date							dateBegin = null;    // --- 开始时间  ---
	private	Date							dateCur = null;    // --- 当前时间  ---
	
	private HashMap<String, Object> activityMap = new HashMap<String, Object>();
	
	//set loginid 进来
	private List<String> orderInfoes = new ArrayList<String>();
	
	public List<String> getOrderInfoes() {
		return orderInfoes;
	}

	public void setOrderInfoes(List<String> orderInfoes) {
		this.orderInfoes = orderInfoes;
	}

	public HashMap<String, Object> getActivityMap() {
		return activityMap;
	}
	
	public void setActivityMap(HashMap<String, Object> activityMap) {
		this.activityMap = activityMap;
	}
	
	
	@Override
	public void run() {
		try{
			logger.info("发送短信前："+ new Date());
			send();
			logger.info("发送短信后："+ new Date());
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("发送短信："+ e.getMessage());
		}
	}
	
	public void send(){ 
		// TODO Auto-generated method stub
		//获取模板信息SHORT_MESSAGE_TEMPLATE_INFO
		String templateInfo = AsynDataIns.getValueFromGlobal("SHORT_MESSAGE_TEMPLATE_INFO");
		if(templateInfo == null || templateInfo.equals("")){
			logger.info("模板信息错误:"+templateInfo);
			return ;
		}
		templateInfo = templateInfo.replace("activityName", activityMap.get("activityName")+"");
		//2.获取每日最大发送次数
		int num = getNum();
		if(num == 0){
			logger.info("渠道数据接口dataMap的每日发送次数为0");
			return ;
		}
		//单条发送组装数据
		for (int i = 0; i < orderInfoes.size(); i++) {
			//发送短信使用
			activityMap.put("contactNum", 1);
			activityMap.put("loginId", orderInfoes.get(i));
			//获取电话号码使用
			HashMap<String, Object> getMap = new HashMap<String, Object>();
			getMap.put("p", "loginId");
			getMap.put("v", orderInfoes.get(i));
			getMap.put("tenant_id", activityMap.get("tenantId"));
			//判断loginid当日是否达到最大发送次数
			int maxSend=0;
			try{
				Integer sendNum = noticeMapper.maxSend(activityMap);
				if(sendNum != null){
					maxSend = sendNum.intValue();
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("日志表没有该员工发送短信记录"+orderInfoes.get(i) + e.getMessage());
			}
			if(maxSend >= num){
				continue;
			}
			//4.调用门户接口获取电话号码DOOR_SECURITY_REST
			String mobile = "";
			mobile = getMobile(getMap);
			if(mobile.equals("")){
				logger.info("获取门户接口电话号码失败");
				continue;
			}
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("telPhone", mobile);
			requestMap.put("sendContent", templateInfo);
			//5.发送短信
			sendMessage(requestMap,activityMap);
		}
	}
	/**
	 * 渠道获取最大每日发送短信数量
	 */
	private Integer getNum(){
		int num = 0;
		String channelInfoUrl = AsynDataIns.getValueFromGlobal("XW_ACTIVITY_CHANNWL_DEFAUL");
		if(channelInfoUrl == null || channelInfoUrl.equals("")){
			logger.info("渠道数据接口url地址错误:"+channelInfoUrl);
			return 0;
		}
		String channelInfo = HttpUtil.doGet(channelInfoUrl, activityMap);
		if(channelInfo == null || channelInfo.equals("") || channelInfo.equals("ERROR")){
			logger.info("渠道数据接口:"+channelInfo);
			return 0;
		}
		HashMap<String, Object> dataMap= JSONObject.parseObject(channelInfo,HashMap.class);
		logger.info("渠道数据接口dataMap的大小:"+dataMap.size());
		if(dataMap != null && dataMap.size()>0){
			/**--要去掉--**/
//			dataMap.put("managerSmsLimit", "10");
			if(dataMap.get("managerSmsLimit") != null && !dataMap.get("managerSmsLimit").toString().equals("")){
				num =  Integer.parseInt(dataMap.get("managerSmsLimit").toString());
				logger.info("渠道数据接口dataMap的每日发送次数:"+num);
			}
		}
		return num;
	}
	
	/**
	 * 获取login的电话号码
	 * @return
	 */
	public String getMobile(HashMap<String, Object> getMap){
		String mobile = "";
		String doorUrl = AsynDataIns.getValueFromGlobal("DOOR_SECURITY_REST");
		if(doorUrl == null || doorUrl.equals("")){
			logger.info("获取门户url地址错误:"+doorUrl);
			return mobile;
		}
		String doorInfo = HttpUtil.doGet(doorUrl, getMap);
		if(doorInfo == null || doorInfo.equals("") || doorInfo.equals("ERROR")){
			logger.info("获取门户接口:"+doorInfo);
			return mobile;
		}
		List<Map<String, Object>> doorList= JSONObject.parseObject(doorInfo,List.class);
		logger.info("获取门户接口doorMap的大小:"+doorList.size());
		if(doorList != null && doorList.size()>0){
			Map<String, Object> doorMap = (Map<String, Object>) doorList.get(0);
			logger.info("获取门户接口doorMap的大小:"+doorMap.size());
			if(doorMap != null && doorMap.size()>0){
				if(doorMap.get("mobile") != null && !doorMap.get("mobile").toString().equals("")){
					mobile =  doorMap.get("mobile").toString();
					logger.info("获取门户接口电话号码:"+mobile);
				}
			}else{
				logger.info("获取门户接口接口错误doorMap:" + doorMap);
			}
		}else{
			logger.info("获取门户接口接口错误");
		}
		return mobile;
	}
	/**
	 * 发送短信
	 * @param request
	 */
	public void sendMessage(HashMap<String,Object> requestMap,HashMap<String,Object> insertMap){
		logger.info("发送短信参数："+JSON.toJSONString(requestMap));
		logger.info("记录发送短信参数："+JSON.toJSONString(insertMap));
		String messageSendUrl = AsynDataIns.getValueFromGlobal("SHORT_MESSAGE_SEND_HENAN");
		if(messageSendUrl == null || messageSendUrl.equals("")){
			logger.info("获取发送短信url地址错误:"+messageSendUrl);
			return;
		}
		String messageSendInfo = HttpUtil.doGet(messageSendUrl, requestMap);
		if(messageSendInfo == null || messageSendInfo.equals("") || messageSendInfo.equals("ERROR")){
			logger.info("获取发送短信接口:"+messageSendInfo);
			return;
		}
		if(messageSendInfo.equals("1")){
			int result = noticeMapper.updateLog(insertMap);
			if(result == 0){
				noticeMapper.insertLog(insertMap);
			}
			logger.info("发送短信成功!");	
		}else if(messageSendInfo.equals("0")){
			logger.info("发送短信号码为空!");	
		}else if(messageSendInfo.equals("2")){
			logger.info("发送短信内容为空!");	
		}else if(messageSendInfo.equals("3")){
			logger.info("发送短信系统异常!");	
		}else{
			logger.info("发送短信接口异常!");	
		}
	}
	
}
