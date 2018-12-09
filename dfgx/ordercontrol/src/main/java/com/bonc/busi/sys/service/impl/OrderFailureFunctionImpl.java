package com.bonc.busi.sys.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;

import com.bonc.busi.sys.dao.SyscommcfgDao;
import com.bonc.busi.sys.dao.SyslogDao;
import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.service.OrderFailureFunction;
import com.bonc.common.base.JsonResult;
@Service("OrderFailureFunctionImpl")
public class OrderFailureFunctionImpl implements OrderFailureFunction{
	// --- 定义日志变量 ---
		private final static Logger log = LoggerFactory.getLogger(OrderFailureFunctionImpl.class);
		
	
	
		
		@Autowired	private SyscommcfgDao SyscommcfgDao;

	

		
	@Override	
	@SuppressWarnings("unchecked")
	public JsonResult startOrderFailureByStatus(String TENANT_ID, char flag) {
		
		JsonResult	JsonResultIns = new JsonResult();
		SysLog			SysLogIns = new SysLog();
		
		SysLogIns.setAPP_NAME("ORDERCONTROL-"+SysFunctionImpl.class.getName()+"-startOrderFailureByStatus");
		SysLogIns.setTENANT_ID(TENANT_ID);
		SysLogIns.setBUSI_ITEM_5("09");
	
			// --- 判断参数 ---
			if(TENANT_ID == null){
				JsonResultIns.setCode("000002");
				JsonResultIns.setMessage("参数TENANT_ID为空");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数TENANT_ID为空");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
			
			// --- 调用接口 ---
			String		serviceUrl = SyscommcfgDao.query("ORDERFAILURE.SERVICE.URL");
			if(serviceUrl == null){
				JsonResultIns.setCode("000005");
				JsonResultIns.setMessage("参数:ORDERFAILURE.SERVICE.URL 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ORDERFAILURE.SERVICE.URL 没有设置");
				SysLogIns.setBUSI_ITEM_1("fail");
				SyslogDao.insert(SysLogIns);
				return JsonResultIns;
			}
		try{
			Map<String,String>		mapPara = new HashMap<String,String>();
			mapPara.put("TENANT_ID", TENANT_ID);

			RestTemplate restTemplate = new RestTemplate();
			Map<String,Object>		result  = restTemplate.getForObject(serviceUrl, Map.class, mapPara);
					
			//String		code = (String)result.get("code");
			log.info("调度成功，tenant_id:"+TENANT_ID);
				
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("正常启动");
		
		}catch(Exception e){
			e.printStackTrace();
			log.info("调度失败,tenant_id:"+TENANT_ID);
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setBUSI_ITEM_2("工单失效调度异常");
			SysLogIns.setLOG_TIME(new Date());
			SyslogDao.insert(SysLogIns);
		}
		return JsonResultIns;
	}

}
