package com.bonc.controller.interfaces;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.busi.interfaces.model.RespHeader;
import com.bonc.busi.interfaces.model.alertwin.ActivityQueryReq;
import com.bonc.busi.interfaces.model.alertwin.ActivityQueryResp;
import com.bonc.busi.interfaces.model.frontline.ContactReq;
import com.bonc.busi.interfaces.service.AlertWinService;
import com.bonc.busi.interfaces.service.ContactService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;

@RestController
@RequestMapping("/channel/alertwin/")
public class AlertWinController {
	private static final Logger log = Logger.getLogger(AlertWinController.class);
	
	@Autowired
	private AlertWinService alertWinService;
	
	@Autowired
	private ContactService contactService;
	
	@RequestMapping(value="activityquery",method=RequestMethod.POST)
	public Object activityquery(@RequestBody String request){
//		System.out.println(request);
		RespHeader respCode = new RespHeader();
		String decode = null;
		try{
			decode = URLDecoder.decode(request, "UTF-8");
		}catch(Exception e){
			respCode.setCode(IContants.CODE_FAIL);
			respCode.setMsg("REQUEST_DECODE_FAIL");
			return respCode;
		}
		try {
			ActivityQueryReq req=JSON.parseObject(decode, ActivityQueryReq.class);
			if(null==req.getPhoneNum()||null==req.getTenantId()||null==req.getChannelId()){
				respCode.setCode(IContants.CODE_FAIL);
				respCode.setMsg("必传参数不能为空！");
				return respCode;
			}
			long start = System.currentTimeMillis();
//			log.info("弹窗服务查询接口请求参数——>"+JSON.toJSONString(req));
			ActivityQueryResp resp = alertWinService.activityQuery(req);
			long end = System.currentTimeMillis();
			log.info("弹窗服务调用总耗时——>"+(end-start)/1000.0+"s");
			return URLEncoder.encode(JSON.toJSONString(resp,SerializerFeature.WriteMapNullValue), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("URL转码失败");
			respCode.setCode(IContants.CODE_FAIL);
			respCode.setMsg("URL转码失败");
			return respCode;
		} catch(BoncExpection e){
			respCode.setCode(IContants.BUSI_ERROR_CODE);
			respCode.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respCode;
		} catch (Exception e) {
			respCode.setCode(IContants.SYSTEM_ERROR_CODE);
			respCode.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respCode;
		}
	}
	
	@RequestMapping("receipt")
	public Object receipt(@RequestBody String request){
		RespHeader resp = new RespHeader();
		String decode = null;
		try{
			decode = URLDecoder.decode(request, "UTF-8");
		}catch(Exception e){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("REQUEST_DECODE_FAIL");
			return resp;
		}
		
		ContactReq req=JSON.parseObject(decode, ContactReq.class);
		
		/**
		 * 设置营销话术和短信模板
		 */
		for(HashMap<String, Object> item:req.getPama()){
			item.put("ext2", item.get("contactSmsDesc"));
			item.put("ext3", item.get("contactMarketDesc"));
		}
		
		long start = System.currentTimeMillis();
		log.info("弹窗回执接口请求参数——>"+JSON.toJSONString(req));
		
		try {
			contactService.contact(req);
			resp.setCode(IContants.CODE_SUCCESS);
			resp.setMsg("回执成功！");
		}catch(BoncExpection e){
			resp.setCode(e.getCode());
			resp.setMsg(IContants.BUSI_ERROR_MSG+"——>"+e.getMsg());
		} catch (Exception e) {
			resp.setCode(IContants.SYSTEM_ERROR_CODE);
			resp.setMsg(IContants.SYSTEM_ERROR_MSG+"——>"+e.getMessage());
		}
		long end = System.currentTimeMillis();
		log.info("弹窗服务调用总耗时——>"+(end-start)/1000.0+"s");
		
		try {
			return URLEncoder.encode(JSON.toJSONString(resp,SerializerFeature.WriteMapNullValue), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("URL转码失败");
			RespHeader respCode = new RespHeader();
			respCode.setCode(IContants.CODE_FAIL);
			respCode.setMsg("RESPONSE_ENCODE_FAIL");
			return respCode;
		} 
	}
	
	@RequestMapping("alerttimes")
	public Object alerttimes(@RequestBody String request){
		RespHeader resp = new RespHeader();
		String decode = null;
		try{
			decode = URLDecoder.decode(request, "UTF-8");
		}catch(Exception e){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("REQUEST_DECODE_FAIL");
			return resp;
		}
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> req=JSON.parseObject(decode, HashMap.class);
		
		long start = System.currentTimeMillis();
		log.info("增加弹出次数请求参数——>"+JSON.toJSONString(req));
		
		try {
			alertWinService.alertTimes(req);
			resp.setCode(IContants.CODE_SUCCESS);
			resp.setMsg("增加弹出次数成功！");
		}catch(BoncExpection e){
			resp.setCode(IContants.BUSI_ERROR_CODE);
			resp.setMsg(IContants.BUSI_ERROR_MSG+"——>"+e.getMsg());
		} catch (Exception e) {
			resp.setCode(IContants.SYSTEM_ERROR_CODE);
			resp.setMsg(IContants.SYSTEM_ERROR_MSG+"——>"+e.getMessage());
		}
		long end = System.currentTimeMillis();
		log.info("弹窗服务调用总耗时——>"+(end-start)/1000.0+"s");
		
		try {
			return URLEncoder.encode(JSON.toJSONString(resp,SerializerFeature.WriteMapNullValue), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("URL转码失败");
			RespHeader respCode = new RespHeader();
			respCode.setCode(IContants.CODE_FAIL);
			respCode.setMsg("RESPONSE_ENCODE_FAIL");
			return respCode;
		} 
	}
}
