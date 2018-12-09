package com.bonc.busi.send.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.send.mapper.YJSentMapper;
import com.bonc.busi.send.service.YjService;
import com.bonc.utils.HttpUtil;

@Service("yjService")
@ConfigurationProperties(prefix = "channel.yj", ignoreUnknownFields = false)
public class YjServiceImpl implements YjService {
	private static final Logger log = Logger.getLogger(DxSentServiceImpl.class);

	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Autowired
	private YJSentMapper yjSendMapper;

	@Override
	public void sent() {
		log.info("<<<<——开始下发一级渠道活动——>>>>");
		HashMap<String, Object> reqMap = new HashMap<String, Object>();
		//查询有效的短信活动
		reqMap.put("tenantId", "uni076");
		//查询一级渠道有效活动列表
		List<HashMap<String, Object>> yjActivity = yjSendMapper.findActivityDetail(reqMap);
		for (HashMap<String, Object> activity:yjActivity) {
			reqMap.put("ACTIVITY_SEQ_ID", activity.get("REC_ID"));
			try {
				//查询以及渠道批次工单数据量　
				HashMap<String, Object> yjReq = new HashMap<String, Object>();
				yjReq.put("activityId", activity.get("ACTIVITY_ID"));
				yjReq.put("recId", activity.get("REC_ID"));
				yjReq.put("tenantId", activity.get("TENANT_ID"));
				yjReq.put("activityType", activity.get("ACTIVITY_TYPE"));
				//TODO 确定一级渠道的账期是
				yjReq.put("dealMonth", "");
				String result = HttpUtil.sendPost(url,JSON.toJSONString(yjReq));
				JSONObject obj = JSONObject.parseObject(result);
				log.info("一级渠道活动工单调用结果——>" + result);
				String success = obj.getString("result");
				if(Boolean.parseBoolean(success)){
					reqMap.put("channelStatus", "1");
					reqMap.put("status", 1);
					yjSendMapper.modifyChannelStatus(reqMap);
					yjSendMapper.modifyChannelStatis(reqMap);
				}else{
					reqMap.put("channelStatus", "5");
					reqMap.put("status", 2);
					yjSendMapper.modifyChannelStatus(reqMap);
					yjSendMapper.modifyChannelStatis(reqMap);
				}
			} catch (Exception e) {
				log.error("下发一级渠道接口调用失败！");
				reqMap.put("channelStatus", "5");
				reqMap.put("status", 3);
				yjSendMapper.modifyChannelStatus(reqMap);
				yjSendMapper.modifyChannelStatis(reqMap);
			}
		}
	}
}
