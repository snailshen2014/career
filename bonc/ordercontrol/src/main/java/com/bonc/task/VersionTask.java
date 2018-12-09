package com.bonc.task;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.sys.dao.SyscommcfgDao;
import com.bonc.busi.sys.mapper.SysMapper;
import com.bonc.busi.sys.service.SysFunction;
import com.bonc.busi.sys.dao.SyscommcfgDao;
import com.bonc.utils.HttpUtil;
import com.bonc.busi.sys.entity.SysLog;

@Component
@EnableScheduling
public class VersionTask {
	
	private final static Logger log = LoggerFactory.getLogger(VersionTask.class);
	@Autowired		private			SysMapper  SysMapperIns;
	@Autowired		private 			SysFunction	SysFunctionIns;
	@Autowired 		private  		SyscommcfgDao SyscommcfgDao;

	
	/**
	 * 
	 */
	//@Scheduled(cron = "0/5 * * * * ?")
	/*
	 * 用户同步检查，每10分钟检查一次
	 */
    @Scheduled(cron = "0 0/10 * * * ?")
	//@Scheduled(cron = "0 0 * * * ?")
	//@Scheduled(cron = "0 0/2 * * * ?")
	public		void			userlabelAsynScan(){
		List<String>		listTenantId = SysMapperIns.getValidTenantId();
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME(VersionTask.class.getName()+"-userlabelAsynScan");
		
		for(String item:listTenantId){
			SysLogIns.setTENANT_ID(item);
			SysLogIns.setBUSI_ITEM_1("用户资料同步定时检查");
			// --- 检查帐期是否有变化 ---
			String		dbDateId = SyscommcfgDao.query("ASYNUSER.XCLOUD.DATEID."+item);
			if(dbDateId == null){
				log.warn("ASYNUSER.XCLOUD.DATEID."+item+ "  没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("ASYNUSER.XCLOUD.DATEID."+item+ "  没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			// --- 提取帐期 ---
			String flag = SyscommcfgDao.query("SERVICE_PROVIDER_TYPE");
			String strCurMonthDay;
			if (flag!=null && flag.equals("1")) {
				String strDXCurMonthDayUrl = SyscommcfgDao.query("GET_MONTH_TIME");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("1", "1");
				params.put("tenant_id", item);
				Map<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("req", JSON.toJSONString(params));
				String sendPost = HttpUtil.doGet(strDXCurMonthDayUrl, requestMap);
				Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
				strCurMonthDay = (String) resultMap.get("MAX_DATE");
			}else{
//				strCurMonthDay = SysFunctionIns.getCurMothDay(TenantId);
				strCurMonthDay = SysFunctionIns.getCurMothDay(item);
			}

			if(strCurMonthDay == null || strCurMonthDay.length() == 0){
				log.warn("获取当前帐期时间失败");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("获取当前帐期时间失败");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}		
			log.info("cur day="+strCurMonthDay);
			if(strCurMonthDay == null || strCurMonthDay.equals(dbDateId) || strCurMonthDay.length() == 0){
				log.info("租户:"+item+" 数据已经倒过了或没有准备好,strCurMonthDay="+strCurMonthDay);
				continue;
			}
			
			// --- 检查当前是否在同步 ---
			String		asynFlag = SyscommcfgDao.query("ASYNUSER.RUN.FLAG."+item);
			if(asynFlag == null){
				log.warn("参数:ASYNUSER.RUN.FLAG."+item+" 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNUSER.RUN.FLAG."+item+" 没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if(asynFlag.equals("TRUE")){
				log.info("用户同步正在运行 ,tenant_id:"+item);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("用户同步正在运行");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			// --- 启动同步 ---
			SysFunctionIns.StartUserLabelAsyn(item,'0');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("启动用户资料同步");
			SysFunctionIns.saveSysLog(SysLogIns);
			
			
		}//  --- for ---	
	}
	/*
	 * 工单用户资料刷新检查，每10分钟检查一次
	 */
    @Scheduled(cron = "0 0/10 * * * ?")
   // @Scheduled(cron = "0 30/60 * * * ?")
    public		void			orderUserlabelUpdateScan(){
		List<String>		listTenantId = SysMapperIns.getValidTenantId();
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setBUSI_ITEM_5("050");
		SysLogIns.setAPP_NAME(VersionTask.class.getName()+"-orderUserlabelUpdateScan");
		
		for(String item:listTenantId){
			String updateType = null;
			SysLogIns.setTENANT_ID(item);
			SysLogIns.setBUSI_ITEM_1("工单用户资料刷新定时检查");
			// --- 检查帐期是否有变化 ---
			String		dbDateId = SyscommcfgDao.query("ORDER.USERLABEL.UPDATE.XCLOUD.DATEID."+item);
			if(dbDateId == null){
				log.warn("ORDER.USERLABEL.UPDATE.XCLOUD.DATEID."+item+ "  没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("ORDER.USERLABEL.UPDATE.XCLOUD.DATEID."+item+ "  没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			// --- 提取帐期 ---
			String flag = SyscommcfgDao.query("SERVICE_PROVIDER_TYPE");
			String strCurMonthDay;
			if (flag!=null && flag.equals("1")) {
				String strDXCurMonthDayUrl = SyscommcfgDao.query("GET_MONTH_TIME");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("1", "1");
				params.put("tenant_id", item);
				Map<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("req", JSON.toJSONString(params));
				String sendPost = HttpUtil.doGet(strDXCurMonthDayUrl, requestMap);
				Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
				strCurMonthDay = (String) resultMap.get("MAX_DATE");
			}else{			
				strCurMonthDay = SysFunctionIns.getCurMothDay(item);
			}

			if(strCurMonthDay == null || strCurMonthDay.length() == 0){
				log.warn("获取当前帐期时间失败");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("获取当前帐期时间失败");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}		
			log.info("cur day="+strCurMonthDay);
			
			//--标签字段发生变化，因调用接口时执行过程正在进行，而延迟，判断标签是否变化的标识--
			String		   asynOrderLabelUpdateFlag = SyscommcfgDao.query("ORDER.USERLABEL.UPDATE.LABELUPDATE.FLAG."+item);
			
			if(strCurMonthDay.equals(dbDateId)){
				log.info("租户:"+item+" 工单用户资料刷新--数据已经刷新过了或没有准备好,strCurMonthDay="+strCurMonthDay);
				if(asynOrderLabelUpdateFlag == null || asynOrderLabelUpdateFlag.length() == 0 || asynOrderLabelUpdateFlag.equals("FALSE")){
					log.info("租户:"+item+" 工单用户资料刷新--标签刷新过了或没有准备好,strCurMonthDay="+strCurMonthDay);
					continue;
				}else if ( asynOrderLabelUpdateFlag.equals("TRUE")){
					log.info("租户:"+item+" 工单用户资料刷新--标签刷新,strCurMonthDay="+strCurMonthDay);
					updateType = "1";
				}
				
			}else{
				log.info("租户:"+item+" 工单用户资料刷新--数据刷新,strCurMonthDay="+strCurMonthDay);
				updateType = "0";
			}
			//--检查当前是否在同步--
			String		asynFlag = SyscommcfgDao.query("ASYNUSER.RUN.FLAG."+item);
			if(asynFlag == null){
				log.warn("参数:ASYNUSER.RUN.FLAG."+item+" 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNUSER.RUN.FLAG."+item+" 没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if(asynFlag.equals("TRUE")){
				log.info("用户同步正在运行 ,请等待,tenant_id:"+item);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("用户同步正在运行,请等待");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			//--若没有在同步，检查同步的数据当前是否是最新账期下的数据--
			String		asynDateId = SyscommcfgDao.query("ASYNUSER.XCLOUD.DATEID."+item);
			if(asynDateId == null){
				log.warn("ASYNUSER.XCLOUD.DATEID."+item+ "  没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("ASYNUSER.XCLOUD.DATEID."+item+ "  没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if( !(strCurMonthDay.equals(asynDateId)) ){
				log.info("租户:"+item+"用户资料同步尚未开始,请等待,asynDateId="+asynDateId+",strCurMonthDay="+strCurMonthDay);
				continue;
			}
			// --- 检查当前是否在刷新 ---
			String		orderUpdateFlag = SyscommcfgDao.query("ORDER.USERLABEL.UPDATE.RUNFLAG."+item);
			if(orderUpdateFlag == null){
				log.warn("参数:ORDER.USERLABEL.UPDATE.RUNFLAG."+item+" 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ORDER.USERLABEL.UPDATE.RUNFLAG."+item+" 没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if(orderUpdateFlag.equals("TRUE")){
				log.info("工单用户资料刷新正在运行 ,tenant_id:"+item);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单用户资料刷新正在运行");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			// --- 启动用户资料刷新 ---
			SysFunctionIns.StartOrderUserLabelUpdate(item,updateType);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("启动工单用户资料刷新");
			SysFunctionIns.saveSysLog(SysLogIns);
			
			
		}//  --- for ---	
	}
    @Scheduled(cron = "0 0/10 * * * ?")
	public		void			orderSucessCheckScan(){
		List<String>		listTenantId = SysMapperIns.getValidTenantId();
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME(VersionTask.class.getName());
		
		for(String item:listTenantId){
			SysLogIns.setTENANT_ID(item);
			SysLogIns.setBUSI_ITEM_1("工单事后成功检查");
			// --- 检查帐期是否有变化 ---
			String		dbDateId = SyscommcfgDao.query("ORDERCHECK.SUCCESS.XCLOUD.DATEID."+item);
			if(dbDateId == null){
				log.warn("ORDERCHECK.SUCCESS.XCLOUD.DATEID."+item+ "  没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("ORDERCHECK.SUCCESS.XCLOUD.DATEID."+item+ "  没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			// --- 提取帐期 ---
			String flag = SyscommcfgDao.query("SERVICE_PROVIDER_TYPE");
			String strCurMonthDay;
			if (flag != null && flag.equals("1")) {
				String strDXCurMonthDayUrl = SyscommcfgDao.query("GET_MONTH_TIME");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("1", "1");
				params.put("tenant_id", item);
				Map<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("req", JSON.toJSONString(params));
				String sendPost = HttpUtil.doGet(strDXCurMonthDayUrl, requestMap);
				Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
				strCurMonthDay = (String) resultMap.get("MAX_DATE");
			} else {
				strCurMonthDay = SysFunctionIns.getCurMothDay(item);
			}
						
//			String strCurMonthDay = SysFunctionIns.getCurMothDay(item);
			if(strCurMonthDay == null || strCurMonthDay.length() == 0){
				log.warn("工单事后成功:获取当前帐期时间失败");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("获取当前帐期时间失败");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}		
			log.info("cur day= "+strCurMonthDay+" ,dbDateId = "+dbDateId);
			
			if(strCurMonthDay == null || strCurMonthDay.equals(dbDateId) || strCurMonthDay.length() == 0){
				log.info("租户:"+item+" 勿须运行成功标准检查,strCurMonthDay="+strCurMonthDay);
				continue;
			}
			// --- 检查当前是否正在运行 ---
			String		asynFlag = SyscommcfgDao.query("ORDERCHECK.SUCESS.RUNFLG."+item);
			if(asynFlag == null){
				log.warn("参数:ORDERCHECK.SUCESS.RUNFLG."+item+" 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ORDERCHECK.SUCESS.RUNFLG."+item+" 没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if(asynFlag.equals("FALSE") == false){
				log.info("工单成功标准检查正在运行 ,tenant_id:"+item);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("工单成功标准检查正在运行");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			// --- 启动同步 ---
			SysFunctionIns.StartOrderSucessCheck(item, '0');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("启动工单成功标准检查");
			SysFunctionIns.saveSysLog(SysLogIns);
			
		}	// --- for ---
	}// --- 方法结束  ---

	@Scheduled(cron = "0 0/7 * * * ?")
	public		void			genOrderScan(){
		SysLog SysLogIns = new SysLog();
		try {
			//判断是否是电信服务 电信通过BDI调用不走定时任务
			String type = SyscommcfgDao.query("SERVICE_PROVIDER_TYPE");
			if ("1".equals(type)) {
				return;
			}
			List<String> listTenantId = SysMapperIns.getValidTenantId();
			SysLogIns.setAPP_NAME(VersionTask.class.getName() + "-genOrderScan");

			for (String item : listTenantId) {
				SysLogIns.setTENANT_ID(item);
				SysLogIns.setBUSI_ITEM_1("工单生成同步定时检查");
				// --- 检查当前是否在同步 ---
				String orderRunFlag = SyscommcfgDao.query("ORDER_RUNNING_" + item);
				if (orderRunFlag == null) {
					log.warn("参数:ORDER_RUNNING_" + item + " 没有设置");
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("参数:ORDER_RUNNING_" + item + " 没有设置");
					SysFunctionIns.saveSysLog(SysLogIns);
					continue;
				}
				if (orderRunFlag.equals("1")) {
					log.info("该租户工单生成正在运行 ,tenant_id:" + item);
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("生成工单正在运行");
					SysFunctionIns.saveSysLog(SysLogIns);
					continue;
				}
				// --- 启动同步 ---
				SysFunctionIns.startGenOrder(item, '0');
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("启动工单生成");
				SysFunctionIns.saveSysLog(SysLogIns);
			}//  --- for ---
		}catch (Exception e){
			log.info("数据库连接错误:{}",e);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("生成工单数据库连接错误");
			SysFunctionIns.saveSysLog(SysLogIns);
		}
	}
//	@Scheduled(cron = "0 0 */4 * * ?")
	public		void			genSceneOrderScan(){
		List<String>		listTenantId = SysMapperIns.getValidTenantId();
		SysLog		SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME(VersionTask.class.getName()+"-genSceneOrderScan");

		for(String item:listTenantId){
			SysLogIns.setTENANT_ID(item);
			SysLogIns.setBUSI_ITEM_1("场景营销工单生成同步定时检查");
			// --- 启动同步 ---
			SysFunctionIns.startSceneGenOrder(item,'0');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("启动场景营销工单生成");
			SysFunctionIns.saveSysLog(SysLogIns);
		}//  --- for ---
	}
	// --- 每天凌晨同步黑白名单数据  ---
	@Scheduled(cron = "0 0 0 * * ?")
	public		void	blackandWhiteAsynScan(){
		List<String>	listTenantId = SysMapperIns.getValidTenantId();
		SysLog	SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME(VersionTask.class.getName()+"-blackandWhiteAsynScan");
		for(String item:listTenantId){
			SysLogIns.setTENANT_ID(item);
			SysLogIns.setBUSI_ITEM_1("黑白名单数据同步定时检查");
			// --- 检查当前是否在同步 ---
			String orderRunFlag = SyscommcfgDao.query("ASYNBLACKANDWHITE.RUN.FLAG." + item);
			if (orderRunFlag == null) {
				log.warn("参数:ASYNBLACKANDWHITE.RUN.FLAG." + item + " 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNBLACKANDWHITE.RUN.FLAG." + item + " 没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if(orderRunFlag.equals("FALSE") == false){
				log.info("黑白名单数据同步正在运行 ,tenant_id:"+item);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("黑白名单数据同步正在运行");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			// --- 启动同步 ---
			SysFunctionIns.startBlackandWhiteAsyn(item,'0');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("启动黑白名单数据同步");
			SysFunctionIns.saveSysLog(SysLogIns);
		}
			
		}
	
	// --- 处理受理成功  ---
    @Scheduled(cron = "0 0 0/1 * * ?")
	public void productSaveSuccess(){
		List<String>	listTenantId = SysMapperIns.getValidTenantId();
		SysLog	SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME(VersionTask.class.getName()+"-productSaveSuccess");
		
		for(String item:listTenantId){
			SysLogIns.setTENANT_ID(item);
			SysLogIns.setBUSI_ITEM_1("受理成功----定时检查");
			// --- 检查当前是否在运行 ---
			String orderRunFlag = SyscommcfgDao.query("ASYNPRODUCTSAVE.RUN.FLAG." + item);
			if (orderRunFlag == null) {
				log.warn("参数:ASYNPRODUCTSAVE.RUN.FLAG." + item + " 没有设置");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数:ASYNPRODUCTSAVE.RUN.FLAG." + item + " 没有设置");
				SysFunctionIns.saveSysLog(SysLogIns);
				continue;
			}
			if(!orderRunFlag.equals("FALSE")){
				log.info("受理成功----正在运行 ,tenant_id:"+item);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("受理成功----正在运行");
				SysFunctionIns.saveSysLog(SysLogIns);
//				continue;
			}
			
			// --- 启动受理成功 ---
			SysFunctionIns.startProductSaveSuccess(item,'0');
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("启动受理成功"+item);
			SysFunctionIns.saveSysLog(SysLogIns);
			
		}
		
	}
	

}
	
	

