package com.bonc.busi.filter.impl;

import java.util.Map;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.filter.DataFilter;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.common.utils.WebServiceUtils;
import com.bonc.utils.PropertiesUtil;

/**
 * 一级渠道数据过滤 实现类
 * 
 * @author sky
 *
 */
@Component(value = "yJQDDataFilter")
public class YJQDDataFilter implements DataFilter {

	@Override
	public Object filterData(Object object) throws Exception {

		Map<String, Object> dataMap = (Map<String, Object>) object;
		String serviceUrl = PropertiesUtil.getWebService("activityRequest.webService.url");
		if (dataMap != null)
			serviceUrl += "?activityId=" + dataMap.get("activityId") + "&tenantId=" + dataMap.get("tenantId");
		String accept = "application/json";
		int timeOut = 5000;
		String res = WebServiceUtils.HttpWebServiceInvoke(serviceUrl, accept, timeOut,"utf-8");
		if (res != null) {
			try {
				JSON.parseObject(res);
			} catch (Exception e) {
				throw new Exception("获取活动信息失败！info:" + res);
			}

			dataMap.putAll((Map<String, Object>) JSON.parse(res));
		}
		return dataMap;
	}

	@Override
	public Boolean supports(String channelId) {

		return ChannelEnum.YJQD.getCode().equals(channelId);
	}
}