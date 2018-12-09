package com.bonc.busi.sys.service.impl;


import com.alibaba.fastjson.JSON;
import com.bonc.busi.sys.dao.SyscommcfgDao;
import com.bonc.busi.sys.dao.SyslogDao;
import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.mapper.SysMapper;
import com.bonc.busi.sys.service.SysFunction;
import com.bonc.common.base.BDIJsonResult;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("SysFunctionImpl")
public class SysFunctionImpl implements SysFunction {

	// --- 定义日志变量 ---
	private final static Logger log = LoggerFactory.getLogger(SysFunctionImpl.class);

	@Autowired
	private SysMapper sysMapper;

	@Autowired SyscommcfgDao SyscommcfgDao;

	/*
	 * 异常信息入库
	 */
	public void saveExceptioneMessage(Exception e, SysLog logIns) {
		try {
			// --- 异常信息转换 ---
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String message = sw.toString();
			if (message.length() < 8190)
				logIns.setLOG_MESSAGE(message);
			else
				logIns.setLOG_MESSAGE(message.substring(0, 8189));

			// --- 入库 ---
			SyslogDao.insert(logIns);
		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	/*
	 * 查询当前帐期
	 */
	public String getCurMothDay(String tenant_id) {
		String strCurMonthDay = null;
		try {
			String monthTimeUrl = SyscommcfgDao.query("GET_MONTH_TIME");
			if (monthTimeUrl == null) {
				log.warn("GET_MONTH_TIME:没有设置");
				return null;
			}
			String cubeId = SyscommcfgDao.query("GET_CUBE_ID");
			HashMap<String, Object> reqMap = new HashMap<String, Object>();
	        reqMap.put("tenantId", tenant_id);
	        reqMap.put("cubeId", cubeId);
			
			strCurMonthDay = HttpUtil.doGet(monthTimeUrl, reqMap);
			if (strCurMonthDay == null || strCurMonthDay.length() == 0) {
				log.warn("获取当前帐期时间失败");
				return null;
			}
			strCurMonthDay = strCurMonthDay.split(",")[1];
			if (strCurMonthDay == null || strCurMonthDay.length() == 0) {
				log.warn("租户:" + tenant_id + " 获取帐期失败,strCurMonthDay=" + strCurMonthDay);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			strCurMonthDay = null;
			SysLog SysLogIns = new SysLog();
			SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-getCurMothDay");
			SysLogIns.setTENANT_ID(tenant_id);
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return strCurMonthDay;
	}

	/*
	 * 启动工单成功标准检查
	 * modeFlag:0-定时任务启动，1-手工启动
	 */
	@SuppressWarnings("unchecked")
	public JsonResult StartOrderSucessCheck(String TENANT_ID, char modeFalg) {
		JsonResult JsonResultIns = new JsonResult();
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-StartOrderSucessCheck");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null) {
				JsonResultIns.setCode("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			if (modeFalg == '0') {  // --- 如果启动方式是定时任务启动，则已经经过了检查 ---

			} else {

			}
			// --- 调用接口 ---
			String serviceUrl = SyscommcfgDao.query("ORDERCHECK.SUCESS.SERVICE.URL");
			if (serviceUrl == null) {
				JsonResultIns.setCode("000005");
				JsonResultIns.setMessage("参数:ORDERCHECK.SUCESS.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ORDERCHECK.SUCESS.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用服务，启动同步 ---
//			HttpHeaders headers = new HttpHeaders();
			//		MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			//MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			//		headers.setContentType(type);
			//headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			Map<String, String> mapPara = new HashMap<String, String>();
			mapPara.put("TENANT_ID", TENANT_ID);
//			HttpEntity<Map<String,String>> formEntity = new HttpEntity<Map<String,String>>(mapPara, headers);
			RestTemplate restTemplate = new RestTemplate();
			Map<String, Object> result = restTemplate.getForObject(serviceUrl, Map.class, mapPara);
			//Map<String,Object>		result  = restTemplate.getForObject("http://127.0.0.1:17001/ordertask-scheduled2/service/ordersucesscheck/{TENANT_ID}", 
			//		Map.class,
			//		mapPara);			
			String code = (String) result.get("code");
			if (code.equals("000000")) {
				log.info("调度成功");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单成功标准检查远程调用成功");
				SysLogIns.setBUSI_ITEM_1("true");
				SyslogDao.insert(SysLogIns);
			} else {
				log.info("调度失败,message=" + result.get("message"));
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单成功标准检查远程调用失败:" + result.get("message"));
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
			}
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("正常启动");
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setBUSI_ITEM_2("工单成功标准检查调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}

	/*
	 * 启动同步资料
	 * flag 启动方式 ：0-自动启动，1-人工调用,2-远程服务启动
	 */
	@SuppressWarnings("unchecked")
	public JsonResult StartUserLabelAsyn(String TENANT_ID, char flag) {
		JsonResult JsonResultIns = new JsonResult();
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-StartUserLabelAsyn");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null) {
				JsonResultIns.setCode("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 判断租户是否在同步资料 ---
			String asynFlag = SyscommcfgDao.query("ASYNUSER.RUN.FLAG." + TENANT_ID);
			if (asynFlag == null) {
				JsonResultIns.setCode("000003");
				JsonResultIns.setMessage("参数:ASYNUSER.RUN.FLAG." + TENANT_ID + " 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNUSER.RUN.FLAG." + TENANT_ID + " 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			if (asynFlag.equals("TRUE")) {
				JsonResultIns.setCode("000004");
				JsonResultIns.setMessage("用户同步正在运行,不能启动");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("用户同步正在运行,不能启动");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用接口同步资料 ---
			String serviceUrl = SyscommcfgDao.query("ASYNUSER.SERVICE.URL");
			if (serviceUrl == null) {
				JsonResultIns.setCode("000005");
				JsonResultIns.setMessage("参数:ASYNUSER.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNUSER.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用服务，启动同步 ---
			HttpHeaders headers = new HttpHeaders();
			//MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			Map<String, Object> mapPara = new HashMap<String, Object>();
			mapPara.put("TENANT_ID", TENANT_ID);
			HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
			RestTemplate restTemplate = new RestTemplate();
			Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
			//Map		result = restTemplate.postForObject("http://127.0.0.1:17001//ordertask-scheduled2/service/asynuserlabel", formEntity, Map.class);

			String code = (String) result.get("code");
			if (code.equals("000000")) {
				log.info("调度成功");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE((String) result.get("message"));
				SysLogIns.setBUSI_ITEM_1("sucess");
				SysLogIns.setBUSI_ITEM_2("用户资料同步调用远程成功");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000000");
				JsonResultIns.setMessage("正常启动");
			} else {
				log.info("调度失败,message=" + result.get("message"));
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("用户资料同步调用远程失败:" + result.get("message"));
				SysLogIns.setBUSI_ITEM_1("fail");
				SysLogIns.setBUSI_ITEM_2("用户资料同步调用远程失败");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000023");
				JsonResultIns.setMessage("用户资料同步调用远程失败:" + result.get("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setBUSI_ITEM_2("用户资料同步调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}
	@Override
	public		JsonResult			StartOrderUserLabelUpdate(String TENANT_ID,String updateType){
		JsonResult JsonResultIns = new JsonResult();
		// --- 开启事物定义相关数据 -------------------------------------
		SysLog SysLogIns = new SysLog();
		SysLogIns.setBUSI_ITEM_5("050");
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-StartOrderUserLabelUpdate");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null ) {
				JsonResultIns.setCode("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			
			// --- 调用接口同步资料 ---
			String serviceUrl = SyscommcfgDao.query("ORDER.USERLABEL.UPDATE.SERVICE.URL");
			if (serviceUrl == null) {
				JsonResultIns.setCode("000005");
				JsonResultIns.setMessage("参数:ORDER.USERLABEL.UPDATE.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ORDER.USERLABEL.UPDATE.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用服务，启动同步 ---
			HttpHeaders headers = new HttpHeaders();
			//MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			Map<String, Object> mapPara = new HashMap<String, Object>();
			mapPara.put("TENANT_ID", TENANT_ID);
			mapPara.put("UPDATE_TYPE", updateType);
			HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
			RestTemplate restTemplate = new RestTemplate();
			Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
			//Map		result = restTemplate.postForObject("http://127.0.0.1:17001//ordertask-scheduled2/service/asynuserlabel", formEntity, Map.class);

			String code = (String) result.get("code");
			if (code.equals("000000")) {
				log.info("工单用户资料刷新调度成功");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE((String) result.get("message"));
				SysLogIns.setBUSI_ITEM_1("000000");
				SysLogIns.setBUSI_ITEM_2("工单用户资料刷新调用远程成功");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000000");
				JsonResultIns.setMessage("工单用户资料刷新正常启动");
			} else {
				log.info("工单用户资料刷新调度失败,message=" + result.get("message"));
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单用户资料刷新调用远程失败:" + result.get("message"));
				SysLogIns.setBUSI_ITEM_1("fail");
				SysLogIns.setBUSI_ITEM_2("工单用户资料刷新调用远程失败");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000023");
				JsonResultIns.setMessage("工单用户资料刷新调用远程失败:" + result.get("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setBUSI_ITEM_2("工单用户资料刷新调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}
	/*
	 * 启动生成工单
	 * flag 启动方式 ：0-自动启动，1-人工调用,2-远程服务启动
	 */
	@Override
	public BDIJsonResult startGenOrder(String TENANT_ID, char flag) {
		boolean bFlag = true;
		BDIJsonResult JsonResultIns = new BDIJsonResult();
		// --- 开启事物定义相关数据 -------------------------------------
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-startGenOrder");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null) {
				JsonResultIns.setStatus("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 判断租户是否在生成工单 ---
			String orderRunFlag = SyscommcfgDao.query("ORDER_RUNNING_" + TENANT_ID);
			if (orderRunFlag == null) {
				JsonResultIns.setStatus("000003");
				JsonResultIns.setMessage("参数:ORDER_RUNNING_" + TENANT_ID + " 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ORDER_RUNNING_" + TENANT_ID + " 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			if (orderRunFlag.equals("1")) {
				JsonResultIns.setStatus("000004");
				JsonResultIns.setMessage("工单正在生成,不能启动");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单正在生成,不能启动");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用接口生成工单 ---
			String serviceUrl = SyscommcfgDao.query("GENORDER.SERVICE.URL");
			if (serviceUrl == null) {
				JsonResultIns.setStatus("000005");
				JsonResultIns.setMessage("参数:GENORDER.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:GENORDER.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用接口活动列表 ---
			String activityListUrl = SyscommcfgDao.query("ACTIVITY_INFO");
			if (activityListUrl == null) {
				JsonResultIns.setStatus("000006");
				JsonResultIns.setMessage("参数:ACTIVITY_INFO 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ACTIVITY_INFO 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用活动列表 ---
			RestTemplate restTemplate = new RestTemplate();
			String activityString = restTemplate.getForObject(activityListUrl + "?tenantId=" + TENANT_ID, String.class);
			if (activityString == null || activityString.equals("")) {
				JsonResultIns.setStatus("000007");
				JsonResultIns.setMessage("活动列表为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("活动列表为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			List<String> activityList = JSON.parseArray(activityString, String.class);
			// --- 调用服务，启动同步 ---

			HttpHeaders headers = new HttpHeaders();
			//MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			Map<String, Object> mapPara = new HashMap<String, Object>();
			mapPara.put("TENANT_ID", TENANT_ID);
			mapPara.put("ActivityIdList", activityList);
			HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
			Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
			//Map		result = restTemplate.postForObject("http://127.0.0.1:17001//ordertask-scheduled2/service/asynuserlabel", formEntity, Map.class);
			String code = (String) result.get("code");
			if (code.equals("000000")) {
				log.info("调度成功");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE(activityList.toString());
				SysLogIns.setBUSI_ITEM_1("sucess");
				SysLogIns.setBUSI_ITEM_2("工单生成调用远程成功");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setStatus("1");  // 因为和电信版本合并 状态需要从000000 改为1
				JsonResultIns.setMessage("正常启动");
			} else {
				log.info("调度失败,message=" + result.get("message"));
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单生成调用远程失败:" + result.get("message"));
				SysLogIns.setBUSI_ITEM_1("fail");
				SysLogIns.setBUSI_ITEM_2("工单生成调用远程失败");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setStatus("000023");
				JsonResultIns.setMessage("工单生成调用远程失败:" + result.get("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setStatus("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setBUSI_ITEM_2("工单生成调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}

	/*
	 * 启动场景营销生成工单
	 * flag 启动方式 ：0-自动启动，1-人工调用,2-远程服务启动
	 */
	@Override
	public void startSceneGenOrder(String TENANT_ID, char flag) {
		// --- 开启事物定义相关数据 -------------------------------------
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-startSceneGenOrder");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null) {
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
			}
			// --- 调用接口生成工单 ---
			String serviceUrl = SyscommcfgDao.query("GENORDER.SCENESERVICE.URL");
			if (serviceUrl == null) {
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:GENORDER.SCENESERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return;
			}
			// --- 调用服务，启动同步 ---
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(serviceUrl+"?TenantId="+TENANT_ID,String.class);
		} catch (Exception e) {
			e.printStackTrace();
			SysLogIns.setBUSI_ITEM_2("场景营销工单生成调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
	}

	/*
	 * 启动生成工单
	 * flag 启动方式 ：0-自动启动，1-人工调用,2-远程服务启动
	 */
	@Override
	public BDIJsonResult startTelecomGenOrder(String TENANT_ID, List<String> ActivityListStr, char flag) {
		boolean bFlag = true;
		BDIJsonResult JsonResultIns = new BDIJsonResult();
		// --- 开启事物定义相关数据 -------------------------------------
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-startGenOrder");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null) {
				JsonResultIns.setStatus("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				JsonResultIns.setData("");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 判断租户是否在生成工单 ---
			String orderRunFlag = SyscommcfgDao.query("ORDER_RUNNING_" + TENANT_ID);
			if (orderRunFlag == null) {
				JsonResultIns.setStatus("000003");
				JsonResultIns.setMessage("参数:ORDER_RUNNING_" + TENANT_ID + " 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				JsonResultIns.setData("");
				SysLogIns.setLOG_MESSAGE("参数:ORDER_RUNNING_" + TENANT_ID + " 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			if (orderRunFlag.equals("1")) {
				JsonResultIns.setStatus("1");
				JsonResultIns.setMessage("工单正在生成,不能启动");
				JsonResultIns.setData("");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单正在生成,不能启动");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用接口生成工单 ---
			String serviceUrl = SyscommcfgDao.query("GENORDER.SERVICE.URL");
			if (serviceUrl == null) {
				JsonResultIns.setStatus("000005");
				JsonResultIns.setMessage("参数:GENORDER.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				JsonResultIns.setData("");
				SysLogIns.setLOG_MESSAGE("参数:GENORDER.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用服务，启动同步 ---
			HttpHeaders headers = new HttpHeaders();
			//MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			Map<String, Object> mapPara = new HashMap<String, Object>();
			mapPara.put("TENANT_ID", TENANT_ID);
			mapPara.put("ActivityIdList", ActivityListStr);
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
			Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
			//Map		result = restTemplate.postForObject("http://127.0.0.1:17001//ordertask-scheduled2/service/asynuserlabel", formEntity, Map.class);
			String code = (String) result.get("code");
			if (code.equals("000000")) {
				log.info("调度成功");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE((String) result.get("message"));
				SysLogIns.setBUSI_ITEM_1("sucess");
				SysLogIns.setBUSI_ITEM_2("工单生成调用远程成功");
				JsonResultIns.setData("");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setStatus("1");  // 因为和电信版本合并 状态需要从000000 改为1
				JsonResultIns.setMessage("正常启动");
			} else {
				log.info("调度失败,message=" + result.get("message"));
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单生成调用远程失败:" + result.get("message"));
				SysLogIns.setBUSI_ITEM_1("fail");
				SysLogIns.setBUSI_ITEM_2("工单生成调用远程失败");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setStatus("000023");
				JsonResultIns.setMessage("工单生成调用远程失败:" + result.get("message"));
				JsonResultIns.setData("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setStatus("000001");
			JsonResultIns.setMessage(e.getMessage());
			JsonResultIns.setData("");
			SysLogIns.setBUSI_ITEM_2("工单生成调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}

	/*
	 * 解锁系统
	 */
	//@Transactional
	public JsonResult unlockSystem(String cltIP) {
		JsonResult JsonResultIns = new JsonResult();
		// --- 开启事物定义相关数据 -------------------------------------
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-unlockSystem");
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setBUSI_ITEM_1(cltIP);
		try {
			SyscommcfgDao.update("ORDERCONTROLCENTERFLAG", "TRUE");
			log.info("unlock the system");
			// --- 调用日志纪录服务 ---
			SysLogIns.setLOG_MESSAGE("系统解锁成功");
			SyslogDao.insert(SysLogIns);
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}

	/*
	 * 单实例控制
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)  // --- 新起一个事物，不管以前是否有事物  ---
	public boolean singleInstaceControl(String dbcheckFlag, String dbstartupFlag, String colName) {
		// --- 查询是否需要判断单实例  ---
		/*String checkFlag = jdbcTemplate.queryForObject("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE `CFG_KEY`= ?",
				new Object[]{dbcheckFlag},
				new int[]{java.sql.Types.VARCHAR},
				String.class
		);*/
		String checkFlag = sysMapper.getSystemValueByKey(dbcheckFlag);
		if (checkFlag.equals("FALSE")) {
			// --- 系统已经启动 ---
			log.info("不需要判断单实例 !!!");
			return true;
		}

		// --- 查询系统启动标识是否被设置 ---
		/*String startupFlag = jdbcTemplate.queryForObject("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE `CFG_KEY`= ?",
				new Object[]{dbstartupFlag},
				new int[]{java.sql.Types.VARCHAR},
				String.class
		);*/
		String startupFlag = sysMapper.getSystemValueByKey(dbstartupFlag);
		// --- 判断系统启动标识是否是TRUE  ---
		if (startupFlag.equals("TRUE")) {
			// --- 系统已经启动 ---
			log.warn("已有其它实例启动 !!!");
			return false;
		}
		// --- 得到当前值  ---
		/*int curValue = jdbcTemplate.queryForObject("SELECT VALUE FROM SYS_MAP_INT WHERE `KEY`= ?",
				new Object[]{colName},
				new int[]{java.sql.Types.VARCHAR},
				Integer.class
		);*/
		int curValue = sysMapper.getSysMapValueByKey(colName);
		log.info("当前值是:" + curValue);
		// --- 更新库中的值，设置库中的值加一 ---
		/*jdbcTemplate.update("UPDATE SYS_MAP_INT SET VALUE=VALUE+1 WHERE `KEY`= ?",
				new Object[]{colName},
				new int[]{java.sql.Types.VARCHAR}
		);*/
		sysMapper.IncreaseSysMapInt(colName);
		// --- 查询更新后的值是否是加一的  ---
		/*int newValue = jdbcTemplate.queryForObject("SELECT VALUE FROM SYS_MAP_INT WHERE `KEY`= ?",
				new Object[]{colName},
				new int[]{java.sql.Types.VARCHAR},
				Integer.class
		);*/
		int newValue = sysMapper.getSysMapValueByKey(colName);
		log.info("新值是:" + newValue);
		if (newValue != curValue + 1) {   // --- 没有抢到，返回失败
			log.warn("已有其它实例启动");
			return false;
		}
		// --- 再次查询系统启动标识是否被设置 ---
		startupFlag = null;
		/*startupFlag = jdbcTemplate.queryForObject("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE `CFG_KEY`= ?",
				new Object[]{dbstartupFlag},
				new int[]{java.sql.Types.VARCHAR},
				String.class
		);*/
		startupFlag = sysMapper.getSystemValueByKey(dbstartupFlag);
		// --- 判断系统启动标识是否是TRUE  ---
		if (startupFlag.equals("TRUE")) {
			// --- 系统已经启动 ---
			log.warn("已有其它实例启动 !!!");
			return false;
		}
		// --- 更新系统启动标识为TRUE  ---
		/*jdbcTemplate.update("UPDATE SYS_COMMON_CFG SET CFG_VALUE='TRUE' WHERE `CFG_KEY`= ?",
				new Object[]{dbstartupFlag},
				new int[]{java.sql.Types.VARCHAR}
		);*/
		sysMapper.updateSystemValueByKey(colName, "true");
		log.info("单实例判断结束,系统可以启动");
		return true;
	}

	/*
	 * 纪录系统日志
	 */
	//@Transactional
	public boolean saveSysLog(final SysLog log) {
		boolean bFlag = true;
		// --- 开启事物定义相关数据 -------------------------------------
		try {
			// --- 判断时间是否设置 --
			if (log.getLOG_TIME() == null) return false;
			// --- 根据时间获取月份 ---
			SyslogDao.insert(log);
			return bFlag;
		} catch (Exception e) {
			e.printStackTrace();
			bFlag = false;
			return bFlag;
		}
	}

	/*
	 * 启动黑白名单数据同步
	 */
	@Override
	@SuppressWarnings("unchecked")
	public JsonResult startBlackandWhiteAsyn(String TENANT_ID, char flag) {
		JsonResult JsonResultIns = new JsonResult();
		// --- 开启事物定义相关数据 -------------------------------------
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-startBlackandWhiteAsyn");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null) {
				JsonResultIns.setCode("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 判断租户是否在同步资料 ---
			String asynFlag = SyscommcfgDao.query("ASYNBLACKANDWHITE.RUN.FLAG." + TENANT_ID);
			if (asynFlag == null) {
				JsonResultIns.setCode("000003");
				JsonResultIns.setMessage("参数:ASYNBLACKANDWHITE.RUN.FLAG." + TENANT_ID + " 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNBLACKANDWHITE.RUN.FLAG." + TENANT_ID + " 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			if (asynFlag.equals("TRUE")) {
				JsonResultIns.setCode("000004");
				JsonResultIns.setMessage("黑白名单数据同步正在运行,不能启动");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("黑白名单数据同步正在运行,不能启动");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用接口同步黑白名单数据 ---
			String serviceUrl = SyscommcfgDao.query("ASYNBLACKANDWHITE.SERVICE.URL");
			if (serviceUrl == null) {
				JsonResultIns.setCode("000005");
				JsonResultIns.setMessage("参数:ASYNBLACKANDWHITE.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNBLACKANDWHITE.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用服务，启动黑白名单数据同步 ---
			HttpHeaders headers = new HttpHeaders();
			//MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			Map<String, Object> mapPara = new HashMap<String, Object>();
			mapPara.put("TENANT_ID", TENANT_ID);
			HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
			RestTemplate restTemplate = new RestTemplate();
			Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
			//Map result = restTemplate.postForObject("http://127.0.0.1:17001//ordertask-scheduled2/service/asynblackandwhite", formEntity, Map.class);
			String code = (String) result.get("code");
			if (code.equals("000000")) {
				log.info("调度成功");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE((String) result.get("message"));
				SysLogIns.setBUSI_ITEM_1("sucess");
				SysLogIns.setBUSI_ITEM_2("黑白名单数据同步调用远程成功");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000000");
				JsonResultIns.setMessage("正常启动");
			} else {
				log.info("调度失败,message=" + result.get("message"));
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("黑白名单数据同步调用远程失败:" + result.get("message"));
				SysLogIns.setBUSI_ITEM_1("fail");
				SysLogIns.setBUSI_ITEM_2("黑白名单数据同步调用远程失败");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000023");
				JsonResultIns.setMessage("黑白名单数据同步调用远程失败:" + result.get("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setBUSI_ITEM_2("黑白名单数据同步调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}
	
	/*
	 * 启动受理成功
	 */
	@Override
	@SuppressWarnings("unchecked")
	public JsonResult startProductSaveSuccess(String TENANT_ID, char flag) {
		JsonResult JsonResultIns = new JsonResult();
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERCONTROL-" + SysFunctionImpl.class + "-startProductSaveSuccess");
		SysLogIns.setTENANT_ID(TENANT_ID);
		try {
			// --- 判断参数 ---
			if (TENANT_ID == null) {
				JsonResultIns.setCode("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 判断租户是否执行受理成功 ---
			String asynFlag = SyscommcfgDao.query("ASYNPRODUCTSAVE.RUN.FLAG." + TENANT_ID);
			if (asynFlag == null) {
				JsonResultIns.setCode("000003");
				JsonResultIns.setMessage("参数:ASYNPRODUCTSAVE.RUN.FLAG." + TENANT_ID + " 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNPRODUCTSAVE.RUN.FLAG." + TENANT_ID + " 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
//				return JsonResultIns;
			}
			if (!asynFlag.equals("FALSE")) {
				JsonResultIns.setCode("000004");
				JsonResultIns.setMessage("受理成功正在运行,不能启动");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("受理成功正在运行,不能启动");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
//				return JsonResultIns;
			}
			// --- 调用接口执行受理成功 ---
			String serviceUrl = SyscommcfgDao.query("ASYNPRODUCTSAVE.SERVICE.URL");
			if (serviceUrl == null) {
				JsonResultIns.setCode("000005");
				JsonResultIns.setMessage("参数:ASYNPRODUCTSAVE.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNPRODUCTSAVE.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			// --- 调用服务，启动受理成功 ---
			HttpHeaders headers = new HttpHeaders();
			//MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			Map<String, Object> mapPara = new HashMap<String, Object>();
			mapPara.put("TENANT_ID", TENANT_ID);
			HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
			RestTemplate restTemplate = new RestTemplate();
			Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
			//Map result = restTemplate.postForObject("http://127.0.0.1:17001//ordertask-scheduled2/service/asynblackandwhite", formEntity, Map.class);
			String code = (String) result.get("code");
			if (code.equals("000000")) {
				log.info("调度成功");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE((String) result.get("message")+TENANT_ID);
				SysLogIns.setBUSI_ITEM_1("sucess");
				SysLogIns.setBUSI_ITEM_2("调用受理成功成功");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000000");
				JsonResultIns.setMessage("正常启动");
			} else {
				log.info("调度失败,message=" + result.get("message"));
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("调用受理成功远程失败:" + result.get("message")+TENANT_ID);
				SysLogIns.setBUSI_ITEM_1("fail");
				SysLogIns.setBUSI_ITEM_2("调用受理成功远程失败");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000023");
				JsonResultIns.setMessage("调用受理成功远程失败:" + result.get("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage()+TENANT_ID);
			SysLogIns.setBUSI_ITEM_2("调用受理成功调度异常");
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;		
	}

}
