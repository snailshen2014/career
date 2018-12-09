package com.bonc.controller;
/*
 * @desc:服务调度
 * @author:zengdingyong
 * @time:2017-06-01
 */

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.service.ServiceControl;
import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.dao.PltActivityExecuteLogDao;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.entity.PltActivityExecuteLog;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.CommonUtil;

@RestController
@RequestMapping("/service")
public class ServiceController {
	private final static Logger log = LoggerFactory.getLogger(ServiceController.class);
	@Autowired	private ServiceControl ServiceControlIns;
	@Autowired	private SysFunction		SysFunctionIns;
	
	/*
	 * 工单用户资料更新
	 */
	@RequestMapping(value="/orderuserupdate", method=RequestMethod.POST)
	public JsonResult OrderUserUpdate(@RequestBody Map<String, Object> request,HttpServletRequest req){
		log.info("TENANT_ID="+request.get("TENANT_ID") + ",UPDATE_TYPE=" + request.get("UPDATE_TYPE"));
		String TenantId = (String)request.get("TENANT_ID");
		String updateTypeTemp = (String)request.get("UPDATE_TYPE"); 
		short updateType = Short.parseShort(updateTypeTemp);
		
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-OrderUserUpdate");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到工单用户资料更新请求");
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysFunctionIns.saveSysLog(SysLogIns);
		JsonResult	JsonResultIns = ServiceControlIns.orderUserLabelUpdate(TenantId,updateType);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
		SysLogIns.setBUSI_ITEM_2("工单用户资料更新结束");
		SysFunctionIns.saveSysLog(SysLogIns);
		return JsonResultIns;
	}
	
	/*
	 * 用户资料同步
	 */
	@RequestMapping(value="/asynuserlabel", method=RequestMethod.POST)
	public JsonResult AsynUserLabel(@RequestBody Map<String, Object> request,HttpServletRequest req){
		log.info("TENANT_ID="+request.get("TENANT_ID"));
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AsynUserLabel");
		SysLogIns.setTENANT_ID((String)request.get("TENANT_ID"));
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到用户资料同步请求");
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysFunctionIns.saveSysLog(SysLogIns);
		boolean  flag = ServiceControlIns.userlabelAsyn((String)request.get("TENANT_ID"));
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("用户资料同步结束");
		JsonResult	JsonResultIns = new JsonResult();
		if(flag){
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
			SysLogIns.setBUSI_ITEM_1("sucess");
		}
		else{
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage("failed");
			SysLogIns.setBUSI_ITEM_1("failed");
		}
		SysFunctionIns.saveSysLog(SysLogIns);
		return JsonResultIns;
	}
	/*
	 * 事前工单过滤服务
	 */
	@RequestMapping(value="/ordersucessfilter", method=RequestMethod.GET)
	public String OrderSucessFilter(@RequestParam("TenantId") String TenantId,@RequestParam("ActivityId") String ActivityId,
			@RequestParam("ActivitySeqId") int ActivitySeqId,HttpServletRequest req){
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-OrderSucessFilter");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到事前工单过滤请求");
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysLogIns.setBUSI_ITEM_2(String.valueOf(ActivitySeqId));
		SysLogIns.setBUSI_ITEM_5("91");
		SysFunctionIns.saveSysLog(SysLogIns);
		JsonResult	JsonResultIns = ServiceControlIns.ordersucessFilter(TenantId, ActivitySeqId,ActivityId);
		SysLogIns.setLOG_TIME(new Date());
		//SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
		//SysLogIns.setBUSI_ITEM_2("事前工单过滤结束");
		SysLogIns.setBUSI_ITEM_3(JsonResultIns.getMessage());
		SysLogIns.setLOG_MESSAGE("事前工单过滤结束");
		SysFunctionIns.saveSysLog(SysLogIns);
		if(JsonResultIns.getCode().equals("000000"))
			return "true";
		else
			return "false";
	}
	/*
	 * 事后工单检查
	 */
	@RequestMapping(value="/ordersucesscheck/{TenantId}", method=RequestMethod.GET)
	public JsonResult OrderSucessCheck(@PathVariable  String TenantId,HttpServletRequest req){
		log.info("tenantid={}",TenantId);
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-OrderSucessCheck");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到事后工单检查请求");
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysFunctionIns.saveSysLog(SysLogIns);
		JsonResult	JsonResultIns = ServiceControlIns.ordersucessCheck(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
		SysLogIns.setBUSI_ITEM_2("事后工单检查结束");
		SysFunctionIns.saveSysLog(SysLogIns);
		return JsonResultIns;
	}
	
	/*
	 * 黑白名单数据同步
	 */
	@RequestMapping(value="/asynblackandwhite", method=RequestMethod.POST)
	public JsonResult AsynBlackandWhite(@RequestBody Map<String, Object> request,HttpServletRequest req){
		log.info("TENANT_ID="+request.get("TENANT_ID"));
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AsynBlackandWhite");
		SysLogIns.setTENANT_ID((String)request.get("TENANT_ID"));
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到黑白名单数据同步请求,租户id为:"+request.get("TENANT_ID"));
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysFunctionIns.saveSysLog(SysLogIns);		
		boolean  flag = ServiceControlIns.blackandwhiteAsyn((String)request.get("TENANT_ID"));
		JsonResult	JsonResultIns = new JsonResult();
		if(flag){
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
		}
		else{
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage("failed");
		}
		return JsonResultIns;
	}

	/*
 * 场景营销拉去下行kafka入工单
 */
	@RequestMapping(value="/GenSenceOrder")
	public JsonResult GenSenceOrder(String TenantId){
		ServiceControlIns.GenSenceOrder(TenantId);
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}

	/*
	* 场景营销拉去下行kafka入工单
	*/
	@RequestMapping(value="/dealfailsms")
	public JsonResult dealfailsms(String TenantId){
		ServiceControlIns.dealfailsms(TenantId);
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	/*
	 * 弹窗渠道加手机号索引
	 */
	@RequestMapping(value="/popwinaddindex", method=RequestMethod.GET)
	public String AddPhoneIndex(@RequestParam("TenantId") String TenantId,@RequestParam("OrderTableName") String OrderTableName,
								 @RequestParam("ActivitySeqId") int ActivitySeqId,@RequestParam("ActivityId") String ActivityId,
								 @RequestParam("ChannelId") String ChannelId,HttpServletRequest req){
		log.info("------ 当前执行:"+ OrderTableName +"   "+ChannelId);
		boolean result = false;
		
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AddPhoneIndex");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到渠道加手机号索引请求");
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysLogIns.setBUSI_ITEM_2("渠道加手机号索引开始");
		SysLogIns.setBUSI_ITEM_3(ChannelId);

		SysFunctionIns.saveSysLog(SysLogIns);
		
		PltActivityExecuteLog	PltActivityExecuteLogIns = new PltActivityExecuteLog();
		
		String channels = SyscommoncfgDao.query("TASK.EXECUTE.CHANNEL");    //8,81,82,83,D,d,5
		
		log.info("------ 数据库配置渠道号 channels------" + channels);
		String channel[] = channels.split(",");
		List<String> channelList = Arrays.asList(channel);

		if(!channelList.contains(ChannelId)){
			log.info("------ 渠道号channelList ------" + channelList);
			log.info("------ 该渠道{}不需要添加手机号索引  ------" + ChannelId);
			PltActivityExecuteLogIns.setCHANNEL_ID(ChannelId);
			PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
			PltActivityExecuteLogIns.setTENANT_ID(TenantId);
			PltActivityExecuteLogIns.setBUSI_CODE(1016);
			PltActivityExecuteLogIns.setBEGIN_DATE(new Date());
			PltActivityExecuteLogIns.setPROCESS_STATUS(1);
			PltActivityExecuteLogIns.setACTIVITY_ID(ActivityId);
			PltActivityExecuteLogDao.insert(PltActivityExecuteLogIns);
			return String.valueOf(result);
		}
		
		PltActivityExecuteLogIns.setCHANNEL_ID(ChannelId);
		PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
		PltActivityExecuteLogIns.setTENANT_ID(TenantId);
		PltActivityExecuteLogIns.setBUSI_CODE(1016);
		PltActivityExecuteLogIns.setBEGIN_DATE(new Date());
		PltActivityExecuteLogIns.setPROCESS_STATUS(0);
		PltActivityExecuteLogIns.setACTIVITY_ID(ActivityId);
		PltActivityExecuteLogDao.insert(PltActivityExecuteLogIns);	
			
		result = ServiceControlIns.addPhoneIndex(TenantId,ChannelId,OrderTableName,ActivitySeqId,ActivityId);
		
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setBUSI_ITEM_2("渠道加手机号索引结束");
		SysFunctionIns.saveSysLog(SysLogIns);
		return String.valueOf(result);
		
	}
	
	/*
	 *  受理成功       根据插入记录表更新工单成功数据
	 */
	@RequestMapping(value="/ProductSaveForSuccess",method=RequestMethod.POST)
	public JsonResult ProductSaveForSuccess(@RequestBody Map<String, Object> request,HttpServletRequest req){
		log.info("TENANT_ID="+request.get("TENANT_ID"));
		SysLog		SysLogIns = new SysLog();
		String TenantId = String.valueOf(request.get("TENANT_ID"));
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-ProductSaveForSuccess");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("收到受理成功请求."+TenantId);
		SysLogIns.setBUSI_ITEM_1(CommonUtil.getIpAddr(req));
		SysFunctionIns.saveSysLog(SysLogIns);
		JsonResult	JsonResultIns = ServiceControlIns.productSaveForSuccess(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage()+"."+TenantId);
		SysLogIns.setBUSI_ITEM_1(JsonResultIns.getCode());
		SysLogIns.setBUSI_ITEM_2("受理成功请求结束");
		SysFunctionIns.saveSysLog(SysLogIns);

		return JsonResultIns;
	}

	
}
