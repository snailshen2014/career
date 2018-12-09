package com.bonc.controller.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.interfaces.model.RespHeader;
import com.bonc.busi.interfaces.service.StatusService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@RestController
@RequestMapping("/status/")
public class StatusController {

	private static final Logger log = Logger.getLogger(StatusController.class);
	
	@Autowired
	private StatusService statusService;
	
	@Autowired
	private CodeService codeService;
	
	private boolean validate(HashMap<String, Object> req,String pama){
		if(null==req.get(pama)||"".equals(req.get(pama))){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 必传参数:
	 * tenantId
	 * activityId
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value="activity",method={RequestMethod.POST})
	public Object getActivityStatus(@RequestBody HashMap<String, Object> req){
		log.info("活动状态查询接口请求参数——>>>>"+JSON.toJSONString(req));
		RespHeader resp = new RespHeader();
		if(validate(req,"tenantId")||validate(req,"activityId")){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("必传参数为空！");
		}
		return statusService.getActivityStatus(req);
	}
	
	/**
	 * 必传参数
	 * tenantId
	 * activityId
	 * startNum
	 * pageSize
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value="benchlist",method={RequestMethod.POST})
	public Object benchList(@RequestBody HashMap<String, Object> req){
		log.info("批次列表查询接口请求参数——>>>>"+JSON.toJSONString(req));
		RespHeader resp = new RespHeader();
		if(validate(req,"tenantId")||validate(req,"activityId")){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("必传参数为空！");
		}
		if(validate(req,"pageNum")||validate(req,"pageSize")){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("分页参数参数为空！");
		}
		RespHeader respHeader = new RespHeader();
		try{
			return statusService.getActivityRecord(req);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	
	/**
	 * 必传参数:
	 * tenantId
	 * activityId
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value="ordergenstatus",method={RequestMethod.POST})
	public Object ordergenstatus(@RequestBody HashMap<String, Object> req){
		log.info("活动状态查询接口请求参数——>>>>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		fields.add("activityId");
		try {
			StringUtil.validate(req, fields);
			resp = statusService.orderGenStatus(req);
		}catch (BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("code", e.getMessage());
		}
		return resp;
	}
}
