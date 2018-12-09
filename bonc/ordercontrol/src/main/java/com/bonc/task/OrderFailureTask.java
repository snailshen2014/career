package com.bonc.task;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.sys.dao.SyscommcfgDao;
import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.mapper.SysMapper;
import com.bonc.busi.sys.service.OrderFailureFunction;
import com.bonc.busi.sys.service.SysFunction;
import com.bonc.common.base.JsonResult;
@Component
@EnableScheduling
public class OrderFailureTask {
	private final static Logger log = LoggerFactory.getLogger(OrderFailureTask.class);
	@Autowired		private			SysMapper  SysMapperIns;
	@Autowired		private 			SysFunction	SysFunctionIns;
	@Autowired		private 			OrderFailureFunction	orderFailureFunctionIns;
	@Autowired		private 			SyscommcfgDao	SyscommcfgDao;

//@Scheduled(cron = "0 0/10 * * * ?")
@Scheduled(cron = "0 30 20 * * ?")
 //@Scheduled(cron = "0 0 * * * ?")

	public		void			orderFilterByStatus(){
		List<String>		listTenantId = SysMapperIns.getValidTenantId();
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME(OrderFailureTask.class.getName()+"orderFilterByStatus");
		SysLogIns.setBUSI_ITEM_5("09");
		for(String item:listTenantId){
			SysLogIns.setTENANT_ID(item);
			SysLogIns.setBUSI_ITEM_1("活动状态判断工单失效");
			// --- 检查是工单失效是否正在进行 ---
			String		runningFlag = SyscommcfgDao.query("ORDERFAILURE.RUNNING.FLAG."+item);
			if(runningFlag == null){
				log.warn("参数:ORDERFAILURE.RUNNING.FLAG."+item+ "  没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ORDERFAILURE.RUNNING.FLAG."+item+ "  没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if(runningFlag.equals("FALSE") == false){
				log.info("根据活动状态进行工单失效正在运行 ,tenant_id:"+item);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("根据活动状态进行工单失效正在运行");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}	

			// --- 启动工单失效 ---
			JsonResult jsonResults = orderFailureFunctionIns.startOrderFailureByStatus(item, '0');
			SysLogIns.setLOG_TIME(new Date());
			String		code = jsonResults.getCode();
			if(code.equals("000000")){
				SysLogIns.setLOG_MESSAGE("工单失效移入历史正常启动");
			}else{
				SysLogIns.setLOG_MESSAGE("工单失效移入历史启动异常");
			}
			
			SysFunctionIns.saveSysLog(SysLogIns);
			
		}	// --- for ---
	}// --- 方法结束  ---

}
