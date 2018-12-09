package com.bonc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.count.model.SendCount;
import com.bonc.busi.count.service.MessageSendService;

@RestController
@RequestMapping("/messageSend")
public class MessageSendController {
	

	@Autowired
	private MessageSendService messageSendService;
	@RequestMapping(value={"/count"},method = RequestMethod.GET)
	public List<SendCount> count(String tenant_id,String is_finish,String startnum,String endnum){
		SendCount sendCount= new SendCount();
		sendCount.setTenant_id(tenant_id);
		sendCount.setIs_finish( Integer.parseInt(is_finish));
		if(startnum!=null){
			sendCount.setStartnum(Integer.parseInt(startnum));
		}else {
			sendCount.setStartnum(0);
		} 
		if(endnum!=null){
			sendCount.setEndnum(Integer.parseInt(endnum));
		}else{
			sendCount.setEndnum(1000);
		}
		List<SendCount> list  = messageSendService.findCountList(sendCount);
		return list;
	}
	@RequestMapping(value="/counttotal")
	public String  counttotal(String tenant_id,String is_finish,String startnum,String endnum){
		SendCount sendCount= new SendCount();
		sendCount.setTenant_id(tenant_id);
		sendCount.setIs_finish( Integer.parseInt(is_finish));
		String total = messageSendService.findCountTotal(sendCount);
		return total;
	}
}

