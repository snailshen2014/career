package com.bonc.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.service.OrderFailureFunction;
import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.CommonUtil;

@RestController
@RequestMapping("/failure")
public class OrderFailureController {
	private final static Logger log = LoggerFactory.getLogger(OrderFailureController.class);
	@Autowired
	private OrderFailureFunction OrderFailureFunctionIns;
	@Autowired
	private SysFunction SysFunctionIns;

	@RequestMapping(value="/orderFailure/{TenantId}", method=RequestMethod.GET)
	public JsonResult orderFailureByStatus(@PathVariable  String TenantId,HttpServletRequest req){
		log.info("tenantid={}",TenantId);
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setBUSI_ITEM_5("09");
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderFailureController.class.getName()+"-orderFailure");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到工单失效检查请求");
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysFunctionIns.saveSysLog(SysLogIns);
		JsonResult	JsonResultIns = OrderFailureFunctionIns.orderFailureByStatus(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
		SysLogIns.setBUSI_ITEM_2("工单失效移入历史流程结束");
		SysFunctionIns.saveSysLog(SysLogIns);
		return JsonResultIns;
	}
}
