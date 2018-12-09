package com.bonc.busi.interfaces.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.bonc.busi.interfaces.mapper.StatusMapper;
import com.bonc.busi.interfaces.service.StatusService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;

@Service("statusService")
@ConfigurationProperties(prefix = "activity", ignoreUnknownFields = false)
public class StatusServiceImpl implements StatusService{

	@Autowired
	private StatusMapper mapper;
	
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public Integer getActivityStatus(HashMap<String, Object> req) {
		//先查询活动当前　在工单侧有没有，如果没有调用寒冰的接口获取活动状态
		HashMap<String, Object> status =  mapper.getActivityStatus(req);
		if(null==status){
			return 0;
		}else if("0".equals(status.get("ACTIVITY_STATUS")+"")){//工单生成中
			return 1;
		}else if(("1").equals(status.get("ACTIVITY_STATUS")+"")){//工单生成完成，更具工单生失效时间判断　工单的具体流程状态
			req.put("recId", status.get("REC_ID"));
			Integer orderStatus = mapper.getOrderStatus(req);
			if (null==orderStatus){
				return 1;
			}else{
				return orderStatus;
			}
		}else{
			return 0;
		}
	}

	/**
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getActivityRecord(HashMap<String, Object> req) {
		List<Object> list = new ArrayList<Object>();
		List<HashMap<String, Object>> listLog = mapper.getGenList(req);
		list.addAll(listLog);
		String result = null;
		try{
			result = HttpUtil.doGet(url+"activity/actChangeLog", req);
		}catch(Exception e){
			throw new BoncExpection(IContants.CODE_FAIL,"活动接口调用失败！");
		}
		List<HashMap> activityRecord = JSONArray.parseArray(result, HashMap.class);
		list.addAll(activityRecord);
		HashMap<String, Object> resp = new HashMap<String, Object>();
		
		resp.put("total", list.size());
		Integer end = Integer.parseInt(req.get("endNum")+"");
		HashMap<String, Object> channelMap = new HashMap<String, Object>();
		channelMap.put(IContants.YX_CHANNEL, "一线渠道");
		channelMap.put(IContants.DX_CHANNEL, "短信渠道");
		channelMap.put(IContants.WX_CHANNEL, "微信渠道");
		channelMap.put(IContants.TC_CHANNEL, "弹窗渠道");
		channelMap.put(IContants.TC_CHANNEL_1, "弹窗自有渠道");
		channelMap.put(IContants.TC_CHANNEL_2, "弹窗社会渠道");
		channelMap.put(IContants.ST_CHANNEL, "手厅");
		channelMap.put(IContants.WSC_CHANNEL, "沃视窗");
		channelMap.put(IContants.WT_CHANNEL, "网厅");
		
		List<Object> items = list.subList(Integer.parseInt(req.get("startNum")+""), end>list.size()?list.size():end);
		for(Object item:items){
			HashMap map = (HashMap)item;
			if(null!=map&&null!=map.get("channelId")){
				map.put("channelId", channelMap.get(map.get("channelId")));
			}
		}
		resp.put("items", items);
		return resp;
	}
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("123");
		list.add("456");
		list.add("789");
		list.add("111");
		list.add("222");
		list.subList(2, 3);
		for(String sre:list.subList(0, 5)){
			System.out.println(sre);
		} 
	}

	@Override
	public HashMap<String, Object> orderGenStatus(HashMap<String, Object> req) {
		List<HashMap<String, Object>> items = mapper.getActivityBench(req);
		HashMap<String, Object> resp = new HashMap<String, Object>();
		//1、 如果未同步活动则为未开始
		if(items.size()==0){
			resp.put("STATUS", -1);
		}else{
			//ACTIVITY_STATUS=0 生成中，ACTIVITY_STATUS=1 生成成功
			if("0".equals(items.get(0).get("ACTIVITY_STATUS")+"")){
				resp.put("STATUS", 0);
				resp.put("UPDATE_TIME", items.get(0).get("BEGIN_GEN_DATE"));
			}else{
				resp.put("STATUS", 1);
				resp.put("UPDATE_TIME", items.get(0).get("END_GEN_DATE"));
			}
		}
		return resp;
	}


}
