package com.bonc.controller.interfaces;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.busi.interfaces.model.RespHeader;
import com.bonc.busi.send.model.sms.DxReq;
import com.bonc.busi.send.model.sms.DxResp;
import com.bonc.busi.send.service.DxSentService;
import com.bonc.utils.IContants;

@RestController
@RequestMapping("/interface/sms/")
public class SmsController {
	private static final Logger log = Logger.getLogger(SmsController.class);
	
	@Autowired
	private DxSentService dxSentService;
	
	/**
	 * 发送短信接口
	 * @param req
	 * @return
	 */
	@RequestMapping(value="sendsms")
	public Object sendsms(String req){
		log.info("短信发送接口请求参数——>"+req);
		DxReq request = JSON.parseObject(req,DxReq.class);
		RespHeader resp=new RespHeader();
		if(null==request.getTelPhone()||null==request.getSendContent()){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("必传参数不能为空！");
			return resp;
		}
		//默认是高级别信息发送
		request.setSendLev(4);
		DxResp respd = dxSentService.sendDx(request);
		if(respd.getFlag()){
			resp.setCode(IContants.CODE_SUCCESS);
			resp.setMsg("发送成功！");
		}else{
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("发送失败！");
		}
		return JSON.toJSONString(resp,SerializerFeature.WriteMapNullValue);
	}
}
