package com.bonc.busi.send.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.send.bo.PltChannelOrderList;
import com.bonc.busi.send.mapper.SendMapper;
import com.bonc.busi.send.model.QueryRange;
import com.bonc.busi.send.model.sms.DxReq;
import com.bonc.busi.send.model.sms.DxResp;
import com.bonc.busi.send.service.DxSentService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.ConfigUtil;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;

@Service("dxSentService")
@ConfigurationProperties(prefix = "channel.sms", ignoreUnknownFields = false)
public class DxSentServiceImpl implements DxSentService {
	private static final Logger logger = Logger.getLogger(DxSentServiceImpl.class);
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Autowired
	private SendMapper sendMapper;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Override
	public DxResp sendDx(DxReq req) {
		logger.info("发送单条短信请求参数="+JSON.toJSONString(req));
		DxResp dxResp = new DxResp();
		
		//1、先查询网关ID
		List<String> tenantId = null;
		try{
			tenantId = getSmsId(req.getTenantId());
		}catch(BoncExpection e){
			logger.error(e.getMsg());
			dxResp.setFlag(false);
			dxResp.setMsg(e.getMsg());
			return dxResp;
		}
		
		if(null == tenantId || tenantId.size()==0){
			logger.error("业务异常——>>租户"+req.getTenantId()+"为查询到可用的网关ID");
			dxResp.setFlag(false);
			dxResp.setMsg("租户没有可用的网关ID");
			return dxResp;
		}else{
			//2、发送短信
			req.setSmsSetId(tenantId.get(0));
			
			String jsonString = JSON.toJSONString(req);
			logger.info(jsonString);
			String resp = HttpUtil.sendPost(url+IContants.sendsingle, jsonString);
			logger.info("发送单条短信响应参数="+resp);
			JSONArray jsonArray = JSON.parseArray(resp);
			JSONObject jsonObject=(JSONObject) jsonArray.get(0);
			
			Integer returnflag = jsonObject.getInteger("returnflag");
			if(returnflag==0){
				dxResp.setFlag(true);
				dxResp.setMsg("发送成功！");
			}else{
				dxResp.setFlag(false);
				dxResp.setMsg("发送失败！");
				DxReq error = JSON.parseObject(jsonObject.get("error").toString(),DxReq.class);
				List<DxReq> reqs = new ArrayList<DxReq>();
				reqs.add(error);
				dxResp.setErrorList(reqs);
			}
			return dxResp;
		}
	}

	@Override
	public DxResp sendDx(ArrayList<DxReq> reqs) {
		logger.info("开发发送短信"+reqs.size()+"条");
		DxResp dxResp = new DxResp();
		
		String jsonString = JSON.toJSONString(reqs);
		logger.info(jsonString);
		
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
		logger.info("发送短信"+reqs.size()+"条，发送结果"+JSON.toJSONString(dxResp));
		return dxResp;
	}
	
	/**
	 * 一个租户可能会有多个网关，但是一般就一个
	 * @param tanintId
	 * @return
	 */
	@Override
	public List<String> getSmsId(String tenantId){
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
			return smsIds;
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) {
//		List<String> smsIdStrings = new DxSentServiceImpl().getSmsId("uni076");
//		for(String string : smsIdStrings){
//			System.out.println(string);
//		}
		ArrayList<DxReq> list = new ArrayList<DxReq>();
		DxReq req = new DxReq();
		req.setSendContent("你好！");
		req.setTelPhone("15515780616");
		req.setSmsSetId("3037145623");
		list.add(req);
		
		DxReq req1 = new DxReq();
		req1.setSendContent("你好！");
		req1.setSmsSetId("3037145623");
		req1.setTelPhone("15515780616");
		req1.setSendLev(4);
		list.add(req1);
//		List<String> resList = new DxSentServiceImpl().getSmsId("uni076"); 
		DxResp repString = new DxSentServiceImpl().sendDx(list);
		System.out.println(JSON.toJSONString(repString));
	}

	
	/**
	 * 集成了短信批量发送的业务
	 */
	@Override
	public void sendDx(String provId) {
		//1|获取网关ID
		List<String> smsIdList=getSmsId(provId);
		if(null!=smsIdList&&smsIdList.size()>0){
			//网关并不会太多一般只有一个
			String smsId=smsIdList.get(0);
			//1、下发短信渠道
			QueryRange range = new QueryRange();
			range.setProvId(provId);
			range.setChannelId(IContants.DX_CHANNEL);
			range.setOrderStatus("0");
			
			//2、设置批次发送条数
			range.setSize(ConfigUtil.getIntProperty("sms.size"));
			while (true) {
				ArrayList<PltChannelOrderList> orderInfos = sendMapper.findOrderPage(range);
				int max=0,min=0;
				//查询出的工单没了
				if(orderInfos.size()>0){
					max=orderInfos.get(0).getID();
					min=orderInfos.get(0).getID();
					ArrayList<DxReq> dxReqs = new ArrayList<DxReq>();
					for(PltChannelOrderList req : orderInfos){
						if(max<req.getID()){
							max=req.getID();
						}
						if(min>req.getID()){
							min=req.getID();
						}
						
						DxReq dxReq = new DxReq();
						dxReq.setSendContent(req.getORDER_CONTENT());
						dxReq.setTelPhone(req.getPHONE_NUMBER());
						dxReq.setSmsSetId(smsId);
						dxReqs.add(dxReq);
					}
					//发送短信
					int i=1;
					String orderState = "1";
					//循环
					while (i++<=3) {
						try{
							sendDx(dxReqs);
							break;
						}catch(Exception e){
							orderState = "4";
							logger.info("短信发送失败第"+i+"次");
						}
					}
					range.setOrderStatus(orderState);
					range.setStart(min);
					range.setEnd(max);
					sendMapper.updateDxOrder(range);
				}else {
					break;
				}
			}
		}
	}
	
}
