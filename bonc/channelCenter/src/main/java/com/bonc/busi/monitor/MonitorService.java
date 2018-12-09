package com.bonc.busi.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.assemble.DataAssembler;
import com.bonc.busi.filter.DataFilter;
import com.bonc.busi.invoke.ChannelInvoker;
import com.bonc.busi.proccess.Proccesser;
import com.bonc.busi.resolve.DataResolver;

/**
 * 主服务类 分发数据到渠道并返回
 * 
 * @author sky
 *
 */
@Component
public class MonitorService {
	
	Logger loger = Logger.getLogger(MonitorService.class);
	@Resource
	DataResolver orderDataResolver;
	@Resource
	Proccesser proccesser;
	@Resource
	DataFilter wXDataFilter;
	@Resource
	DataAssembler wXDataAssembler;
	@Resource
	ChannelInvoker wXChannelInvoker;
	@Resource
	DataFilter dXDataFilter;
	@Resource
	DataAssembler dXDataAssembler;
	@Resource
	ChannelInvoker dXChannelInvoker;
	@Resource
	DataFilter wTDataFilter;
	@Resource
	DataAssembler wTDataAssembler;
	@Resource
	ChannelInvoker wTChannelInvoker;
	@Resource
	DataFilter sTDataFilter;
	@Resource
	DataAssembler sTDataAssembler;
	@Resource
	ChannelInvoker sTChannelInvoker;
	@Resource
	DataFilter wSCDataFilter;
	@Resource
	DataAssembler wSCDataAssembler;
	@Resource
	ChannelInvoker wSCChannelInvoker;
	@Resource
	DataFilter yJQDDataFilter;
	@Resource
	DataAssembler yJQDDataAssembler;
	@Resource
	ChannelInvoker yJQDChannelInvoker;

	/**
	 * 下发到渠道
	 * 
	 * @return
	 */
	public String sendToChannel(String src, String channelCode) {

		
		Map<String, Object> map = new HashMap<String, Object>();
		String result = "";
		Map<String, Object> resMap = new HashMap<String, Object>();
		try {
			if(src == null || "".equals(src))  throw new Exception("请求参数为空！");
			//将请求参数封装到dataMap中
 			Map<String, Object> dataMap = orderDataResolver.resolveData(src);
 			//在dataMap中添加省份ID
			if (dataMap.get("tenantId") != null) {
				String tenantId = dataMap.get("tenantId").toString();
				dataMap.put("provId", tenantId.substring("uni".length()));
			}
		    //将dataMap以渠道ID为键放入map中
			map.put(channelCode, dataMap);

			if (map != null) {
				for (String key : map.keySet()) {
					switch (key) {
					case "1":
						result = proccesser.proccess(key, sTDataFilter, sTDataAssembler, sTChannelInvoker,
								map.get(key));
						break;
					case "2":
						result = proccesser.proccess(key, wTDataFilter, wTDataAssembler, wTChannelInvoker,
								map.get(key));
						break;
					case "3":
						result = proccesser.proccess(key, dXDataFilter, dXDataAssembler, dXChannelInvoker,
								map.get(key));
						break;
					case "4":
						result = proccesser.proccess(key, wXDataFilter, wXDataAssembler, wXChannelInvoker,
								map.get(key));
						break;					
					case "9":
						result = proccesser.proccess(key, wSCDataFilter, wSCDataAssembler, wSCChannelInvoker,
								map.get(key));
						break;
					case "999":
						result = proccesser.proccess(key, yJQDDataFilter, yJQDDataAssembler, yJQDChannelInvoker,
								map.get(key));
						break;
					default:
						break;
					}

				}
			}
		} catch (Exception e) {
			resMap.put("success", false);
			resMap.put("message", "数据下发失败!");
			resMap.put("error", e.getMessage());
			if("OK".equals(((Map)map.get(channelCode)).get("assembleData"))){
				resMap.put("errorcode", "20001");
			}else{
				resMap.put("errorcode", "10001");
			}
			result = JSON.toJSONString(resMap);
			e.printStackTrace();
		}
		loger.info(result);
		return result;
	}

	/**
	 * 根据用户+渠道 获取活动
	 * 
	 * @return
	 */
	public String getActiveList() {
		// TODO: implement
		return null;
	}
}