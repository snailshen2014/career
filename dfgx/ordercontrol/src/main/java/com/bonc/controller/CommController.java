package com.bonc.controller;
/*
 * @desc:常规控制功能
 * @author:zengdingyong
 * @date:2017-05-31
 */

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bonc.common.base.BDIJsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestHeader;


import com.bonc.common.base.JsonResult;
import com.bonc.busi.sys.service.SysFunction;
import com.bonc.busi.sys.entity.SysLog;
import com.bonc.utils.CommonUtil;

@RestController
@RequestMapping("/commoncontrol")
public class CommController {
	private final static Logger log = LoggerFactory.getLogger(CommController.class);
	@Autowired	private SysFunction	SysFunctionIns;
	

	@RequestMapping(value="/unlock")
	public String unLockOrderControl(HttpServletRequest req){
		// --- 调用解锁服务 （解锁单实例锁定标识）---
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-"+CommController.class.getName()+"-unLockOrderControl");
		SysLogIns.setTENANT_ID("SYS");
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到解锁单实例请求");
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysFunctionIns.saveSysLog(SysLogIns);
		JsonResult 	JsonResultIns = SysFunctionIns.unlockSystem(CommonUtil.getIpAddr(req));	
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
		SysLogIns.setBUSI_ITEM_2("解锁单实例结束");
		SysFunctionIns.saveSysLog(SysLogIns);
	    // --- 设置返回 ---
		if(JsonResultIns.getCode().equals("000000"))
			return "sucess";
		else
			return "解锁失败";
	}
	
	@RequestMapping(value="/startuserlabelasyn", method=RequestMethod.POST)
	public JsonResult startUserLabelAsyn(@RequestBody Map<String, Object> request){
			SysLog		SysLogIns = new SysLog();
			SysLogIns.setAPP_NAME("ORDERCONTROL-"+CommController.class.getName()+"-startUserLabelAsyn");
			SysLogIns.setTENANT_ID((String)request.get("TENANT_ID"));
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("收到用户资料同步启动请求");
			SysFunctionIns.saveSysLog(SysLogIns);
			// --- 调用service执行对应的功能 ---
			JsonResult 	JsonResultIns= SysFunctionIns.StartUserLabelAsyn((String)request.get("TENANT_ID"),'1');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
			SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
			SysLogIns.setBUSI_ITEM_2("用户资料同步调度结束");
			SysFunctionIns.saveSysLog(SysLogIns);
			return JsonResultIns;
		}
	/*
	 * 人工启动工单成功检查 
	 */
	@RequestMapping(value="/startordersucesscheck", method=RequestMethod.POST)
	public JsonResult startOrderSucessCheck(@RequestBody Map<String, Object> request){
			SysLog		SysLogIns = new SysLog();
			SysLogIns.setAPP_NAME("ORDERCONTROL-"+CommController.class.getName()+"-startOrderSucessCheck");
			SysLogIns.setTENANT_ID((String)request.get("TENANT_ID"));
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("收到工单成功检查启动请求");
			SysFunctionIns.saveSysLog(SysLogIns);
			// --- 调用service执行对应的功能 ---
			JsonResult 	JsonResultIns= SysFunctionIns.StartOrderSucessCheck((String)request.get("TENANT_ID"), '1');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
			SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
			SysLogIns.setBUSI_ITEM_2("工单成功检查调度结束");
			SysFunctionIns.saveSysLog(SysLogIns);
			return JsonResultIns;
		}

	/*
 	* 人工启动工单生成服务
 	*/
	@RequestMapping(value="/startGenOrder", method=RequestMethod.POST)
	public BDIJsonResult startGenOrder(@RequestBody Map<String, Object> request){
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-"+CommController.class.getName()+"-startGenOrder");
		SysLogIns.setTENANT_ID((String)request.get("TENANT_ID"));
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到生成工单启动请求");
		SysFunctionIns.saveSysLog(SysLogIns);
		// --- 调用service执行对应的功能 ---
		BDIJsonResult JsonResultIns=  SysFunctionIns.startGenOrder((String)request.get("TENANT_ID"), '1');
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getStatus());
		SysLogIns.setBUSI_ITEM_2("生成工单调度结束");
		SysFunctionIns.saveSysLog(SysLogIns);
		return JsonResultIns;
	}
	

	/*
	 * 人工启动黑白名单数据同步 
	 */
	@RequestMapping(value="/startblackandwhiteasyn", method=RequestMethod.POST)
	public JsonResult startBlackandWhiteAsyn(@RequestBody Map<String, Object> request){
			SysLog		SysLogIns = new SysLog();
			SysLogIns.setAPP_NAME("ORDERCONTROL-"+CommController.class.getName()+"-startblackandwhiteasyn");
			SysLogIns.setTENANT_ID((String)request.get("TENANT_ID"));
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("收到黑白名单数据同步启动请求");
			SysFunctionIns.saveSysLog(SysLogIns);
			// --- 调用service执行对应的功能 ---
			JsonResult 	JsonResultIns= SysFunctionIns.startBlackandWhiteAsyn((String)request.get("TENANT_ID"),'1');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
			SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
			SysLogIns.setBUSI_ITEM_2("黑白名单数据同步调度结束");
			SysFunctionIns.saveSysLog(SysLogIns);
			return JsonResultIns;
		}
	
}
