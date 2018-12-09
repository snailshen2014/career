package com.bonc.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.sys.dao.SyscommcfgDao;
import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.mapper.SysMapper;
import com.bonc.busi.sys.service.OrderFailureFunction;
import com.bonc.busi.sys.service.SysFunction;
import com.bonc.common.base.JsonResult;
import com.bonc.task.OrderFailureTask;

@RestController
@RequestMapping("/orderfailurecontroller")
public class OrderFailureController {
	private final static Logger log = LoggerFactory.getLogger(OrderFailureController.class);

	@Autowired		private			SysMapper  SysMapperIns;
	@Autowired		private 			SysFunction	SysFunctionIns;
	@Autowired		private 			OrderFailureFunction	orderFailureFunctionIns;
	//--人工启动工单失效--
	@RequestMapping(value="/startorderfailure", method=RequestMethod.POST)
	public JsonResult startorderfailure(@RequestBody Map<String, Object> request){
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-"+CommController.class.getName()+"-startOrderFailure");
		SysLogIns.setTENANT_ID((String)request.get("TENANT_ID"));
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到工单失效检查启动请求");
		SysFunctionIns.saveSysLog(SysLogIns);
		// --- 调用service执行对应的功能 ---
		JsonResult 	JsonResultIns= orderFailureFunctionIns.startOrderFailureByStatus((String)request.get("TENANT_ID"), '1');
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
		SysLogIns.setBUSI_ITEM_2("工单失效检查调度结束");
		SysFunctionIns.saveSysLog(SysLogIns);
		return JsonResultIns;
		}
}
