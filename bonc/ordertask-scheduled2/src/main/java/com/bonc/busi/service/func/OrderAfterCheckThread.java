package com.bonc.busi.service.func;
/*
 * @desc:以新线程方式启动工单事后成功检查
 * @author:zengdingyong
 * @time:2017-06-07
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.service.mapper.CommonMapper;
//import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.utils.HttpUtil;

public class OrderAfterCheckThread extends Thread {
	private final static Logger log = LoggerFactory.getLogger(OrderAfterCheckThread.class);
	// @Autowired private CommonMapper CommonMapperIns;
	// private BusiTools BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private SysFunction SysFunctionIns = SpringUtil.getBean(SysFunction.class);
	private CommonMapper CommonMapperIns = SpringUtil.getBean(CommonMapper.class);

	private Map<String, Object> Para = null;

	public OrderAfterCheckThread() {

	}

	public OrderAfterCheckThread(Map<String, Object> para) {
		this.Para = para;
	}

	@Override
	public void run() {

		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-" + OrderAfterCheckThread.class + "-run");
		String TenantId = (String) Para.get("TENANT_ID");
		log.info("tenantid={}", TenantId);
		SysLogIns.setTENANT_ID(TenantId);
		String curRunFlag = SyscommoncfgDao.query("ORDERCHECK.SUCESS.RUNFLG." + TenantId);
		if (curRunFlag.equals("TRUE")) {
			log.info("当前有成功检查在运行");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("当前有成功检查在运行");
			SyslogDao.insert(SysLogIns);
			return;
		}
		try {

			// --- 查询当前帐期（是否电信） ---
			String providerType = SyscommoncfgDao.query("SERVICE_PROVIDER_TYPE");
			String curMothDay = null;
			if (providerType != null && providerType.trim().equals("1")) {
				// --电信--
				String month_date_url = SyscommoncfgDao.query("GET_MONTH_TIME");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("1", "1");
				params.put("tenant_id", TenantId);
				Map<String, Object> requestMap = new HashMap<String, Object>();
				// 电信需要放入req参数中，否则服务无法收到该请求参数
				requestMap.put("req", JSON.toJSONString(params));
				String sendPost = HttpUtil.doGet(month_date_url, requestMap);
				Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
				curMothDay = (String) resultMap.get("MAX_DATE");

			} else {
				curMothDay = SysFunctionIns.getCurMothDay(TenantId);

			}
			if (curMothDay == null) {
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("查询当前帐期为空");
				SyslogDao.insert(SysLogIns);
				return;
			}
			// --- 更新运行标识 ---
			SyscommoncfgDao.update("ORDERCHECK.SUCESS.RUNFLG." + TenantId, "TRUE");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("OrderAfterCheckThread");
			SysLogIns.setBUSI_ITEM_4(curMothDay);
			SysLogIns.setBUSI_ITEM_5("01");
			SysLogIns.setLOG_MESSAGE("工单成功检查开始运行");
			SyslogDao.insert(SysLogIns);
			// --- 定义4个线程 ---
			int iThreadNum = 4;
			String strTmp = SyscommoncfgDao.query("ORDERSUCESSCHECK.THREADSNUM");
			if (strTmp != null) {
				iThreadNum = Integer.parseInt(strTmp);
				if (iThreadNum < 4)
					iThreadNum = 4;
			}
			// --- 查询租户对应的有效活动序列号 ---
			List<ActivitySucessInfo> listActivitySucessInfo = new ArrayList<ActivitySucessInfo>();
			listActivitySucessInfo = CommonMapperIns.getActivityForTenantId(TenantId);
			// --- 一个活动一个活动的处理 ---
			for (ActivitySucessInfo ActivitySucessInfoIns : listActivitySucessInfo) {
				// --- 查询产品列表 ---
				List<String> listProductInfo = CommonMapperIns
						.getProductListForActivity(ActivitySucessInfoIns.getACTIVITY_SEQ_ID(), TenantId);

				// --- 调用工单检查 ---
				OrderSucessCheck OrderSucessCheckIns = new OrderSucessCheck();
				// --- 设置相应的运行参数 ---
				// OrderSucessCheckIns.setTableName(TableName);
				OrderSucessCheckIns.setProductInfo(listProductInfo);
				OrderSucessCheckIns.setActivitySeqId(ActivitySucessInfoIns.getACTIVITY_SEQ_ID());
				OrderSucessCheckIns.setTenantId(TenantId);
				OrderSucessCheckIns.setActivityId(ActivitySucessInfoIns.getActivityId());
				OrderSucessCheckIns.setMaxMonthDay(curMothDay);
				OrderSucessCheckIns.setActivitySucessInfo(ActivitySucessInfoIns);
				OrderSucessCheckIns.setCheckType((short) 0); // --- 事后检查 ---
				ParallelManageThread ParallelManageIns = new ParallelManageThread(OrderSucessCheckIns, iThreadNum);
				ParallelManageIns.execute();

				// --- 调用统计接口 ---
				if (callStatic(TenantId, ActivitySucessInfoIns.getACTIVITY_SEQ_ID(),
						ActivitySucessInfoIns.getActivityId()) == false) {
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setBUSI_ITEM_1("callStatic");
					SysLogIns.setLOG_MESSAGE("调用统计接口出错,不再继续");
					SyslogDao.insert(SysLogIns);
				}
				// --- 判断帐期是否变化 ---
				// --- 查询当前帐期 ---

				String newMothDay = null;
				if (providerType != null && providerType.trim().equals("1")) {
					// --电信--
					String month_date_url = SyscommoncfgDao.query("GET_MONTH_TIME");
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("1", "1");
					params.put("tenant_id", TenantId);
					Map<String, Object> requestMap = new HashMap<String, Object>();
					// 电信需要放入req参数中，否则服务无法收到该请求参数
					requestMap.put("req", JSON.toJSONString(params));
					String sendPost = HttpUtil.doGet(month_date_url, requestMap);
					Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
					newMothDay = (String) resultMap.get("MAX_DATE");

				} else {
					newMothDay = SysFunctionIns.getCurMothDay(TenantId);

				}

				if (newMothDay.equals(curMothDay))
					continue;
				else { // --- 帐期发生了变化,不再继续 ---
					log.info("帐期变化");
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("帐期发生了变化,不再继续");
					SysLogIns.setBUSI_ITEM_1(curMothDay);
					SysLogIns.setBUSI_ITEM_2(newMothDay);
					SyslogDao.insert(SysLogIns);
					break;
				}
			} // --- for ---
				// --- 更新成功的帐期
				// ----------------------------------------------------------------------------

			SyscommoncfgDao.update("ORDERCHECK.SUCCESS.XCLOUD.DATEID." + TenantId, curMothDay);

		} catch (Exception e) {
			e.printStackTrace();
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("工单成功检查出错");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
			SyslogDao.insert(SysLogIns);
		} finally {
			SyscommoncfgDao.update("ORDERCHECK.SUCESS.RUNFLG." + TenantId, "FALSE");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("OrderAfterCheckThread");
			SysLogIns.setLOG_MESSAGE("工单成功检查结束");
			SyslogDao.insert(SysLogIns);
		}
	}// --- run ---
	/*
	 * 调用统计接口
	 */

	private boolean callStatic(String TenantId, int ActivitySeqId, String ActivityId) {
		SysLog SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-" + OrderAfterCheckThread.class + "-callStatic");
		SysLogIns.setTENANT_ID(TenantId);
		try {
			// --- 调用服务，启动同步 ---
			Map<String, Object> mapPara = new HashMap<String, Object>();
			HttpHeaders headers = new HttpHeaders();
			// MediaType type =
			// MediaType.parseMediaType("application/x-www-form-urlencoded;
			// charset=UTF-8");
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			mapPara.put("activityId", ActivityId);
			mapPara.put("tenantId", TenantId);
			mapPara.put("activitySeqId", Integer.toString(ActivitySeqId));
			HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
			RestTemplate restTemplate = new RestTemplate();
			String serviceUrl = SyscommoncfgDao.query("ORDERUSERLABELUPDATE.STATISTIC.URL");
			Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("工单成功检查调用统计接口");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		Map<String, Object> mapPara = new HashMap<String, Object>();
		HttpHeaders headers = new HttpHeaders();
		// MediaType type =
		// MediaType.parseMediaType("application/x-www-form-urlencoded;
		// charset=UTF-8");
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		mapPara.put("activityId", "");
		mapPara.put("tenantId", "uni081");
		mapPara.put("activitySeqId", Integer.toString(11111));
		HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(mapPara, headers);
		RestTemplate restTemplate = new RestTemplate();
		String serviceUrl = SyscommoncfgDao.query("ORDERUSERLABELUPDATE.STATISTIC.URL");
		Map<String, Object> result = restTemplate.postForObject(serviceUrl, formEntity, Map.class);
	}
}
