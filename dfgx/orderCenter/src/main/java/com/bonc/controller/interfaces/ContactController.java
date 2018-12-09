package com.bonc.controller.interfaces;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.interfaces.model.RespHeader;
import com.bonc.busi.interfaces.model.frontline.ContactReq;
import com.bonc.busi.interfaces.service.ContactService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;

@RestController
@RequestMapping("/interface/channel/")
public class ContactController {
	private static final Logger log=Logger.getLogger(ContactController.class);

	@Autowired
	private ContactService contactService;
	
	/**
	 * 回执接口
	 * @param req
	 * @return
	 */
	@RequestMapping(value="caontact",method={RequestMethod.POST})
	public Object contact(@RequestBody ContactReq req){
		log.info("回执接触接口请求参数——>"+JSON.toJSONString(req));
		long start = System.currentTimeMillis();
		RespHeader resp = new RespHeader();
		try {
			contactService.contact(req);
			resp.setCode(IContants.CODE_SUCCESS);
			resp.setMsg("回执成功！");
		}catch(BoncExpection e){
			resp.setCode(IContants.BUSI_ERROR_CODE);
			resp.setMsg(IContants.BUSI_ERROR_MSG+"——>"+e.getMsg());
		} catch (Exception e) {
			resp.setCode(IContants.SYSTEM_ERROR_CODE);
			resp.setMsg(IContants.SYSTEM_ERROR_MSG+"——>"+e.getMessage());
		}
		long end = System.currentTimeMillis();
		log.info("回执接口操作总耗时——>"+(end-start)/1000.0+"s");
		return resp;
	}
}
