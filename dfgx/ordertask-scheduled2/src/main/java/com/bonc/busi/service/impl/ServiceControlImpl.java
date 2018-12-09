package com.bonc.busi.service.impl;
/*
 * @desc:服务管控
 * @author:zengdingyong
 * @time:2017-06-01
 */

import java.util.*;

import javax.swing.Spring;

import com.bonc.busi.task.base.*;
import com.bonc.busi.task.instance.SceneGenOrder;
import com.bonc.busi.task.service.SceneService;

import kafka.consumer.ConsumerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.busi.outer.model.PltActivityInfo;
import com.bonc.busi.service.ServiceControl;
import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.dao.PltActivityExecuteLogDao;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.dao.SysrunningcfgDao;
import com.bonc.busi.service.dao.TenantinfoDao;
import com.bonc.busi.service.entity.ActivityChannelExecute;
import com.bonc.busi.service.entity.PltActivityExecuteLog;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.service.func.AsynBlackandWhite;
import com.bonc.busi.service.func.AsynUserLabel;
import com.bonc.busi.service.func.CleanOrder;
import com.bonc.busi.service.func.EntryThread;
import com.bonc.busi.service.func.OrderAfterCheckThread;
import com.bonc.busi.service.func.OrderSucessCheck;
import com.bonc.busi.service.func.OrderUserlabelUpdate;
import com.bonc.busi.service.func.ParallelManageThread;
import com.bonc.busi.service.func.ProductSaveForSuccessThread;
import com.bonc.busi.service.mapper.CommonMapper;
import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.common.base.JsonResult;
import com.bonc.controller.ServiceController;
import com.bonc.utils.HttpUtil;


@Service()
public class ServiceControlImpl implements ServiceControl {
	private final static Logger log= LoggerFactory.getLogger(ServiceControlImpl.class);
	@Autowired	private AsynUserLabel AsynUserLabelIns;
	@Autowired	private SysFunction  SysFunctionIns;
	@Autowired	private CommonMapper CommonMapperIns;
	@Autowired  private AsynBlackandWhite AsynBlackandWhitelIns;
    @Autowired  private  BusiTools AsynDataIns;
    @Autowired  private SceneService sceneService;
    @Autowired  private AddPhoneIndex AddPhoneIndexIns; 
    
    @Bean
    private ConsumerConfig getKafkaCfg(){
        Properties props = new Properties();
        props.put("zookeeper.connect", AsynDataIns.getValueFromGlobal("KAFKA_ZKP_CONNECT"));
        props.put("group.id", AsynDataIns.getValueFromGlobal("KAFKA_GROUP_ID"));
        props.put("zookeeper.session.timeout.ms", AsynDataIns.getValueFromGlobal("KAFKA_ZKP_SESSON_TIME"));
        props.put("zookeeper.sync.time.ms", AsynDataIns.getValueFromGlobal("KAFKA_ZKP_SYNC_TIME"));
        props.put("auto.commit.interval.ms", AsynDataIns.getValueFromGlobal("KAFKA_INTERV_TIME"));
        props.put("auto.offset.reset", AsynDataIns.getValueFromGlobal("KAFKA_OFFSET_RESET"));
        props.put("serializer.class", AsynDataIns.getValueFromGlobal("KAFKA_SERIALIZER_CLASS"));
        return new ConsumerConfig(props);
    }


    /*
	 * 工单用户资料更新
	 */
	public		JsonResult		orderUserLabelUpdate(String TenantId,short updateType){
		JsonResult		JsonResultIns = new JsonResult();
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setBUSI_ITEM_5("05");
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceControlImpl.class+"-orderUserLabelUpdate");
		// --- 查询当前帐期（是否电信） ---
		String providerType = SyscommoncfgDao.query("SERVICE_PROVIDER_TYPE");
		String		strCurMonthDay = null;
		if(providerType!=null && providerType.trim().equals("1")){
			//--电信--
			String month_date_url = SyscommoncfgDao.query("GET_MONTH_TIME");
			Map<String, Object> params  = new HashMap<String, Object>();
			params.put("1", "1");
			params.put("tenant_id",TenantId);
			Map<String,Object> requestMap = new HashMap<String,Object>();
			//电信需要放入req参数中，否则服务无法收到该请求参数
			requestMap.put("req", JSON.toJSONString(params));
			String sendPost = HttpUtil.doGet(month_date_url, requestMap);
			Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
			strCurMonthDay = (String) resultMap.get("MAX_DATE");
		}else{
			strCurMonthDay = SysFunctionIns.getCurMothDay(TenantId);
						
		}
		// --- 判断参数 ---
		if (TenantId == null || strCurMonthDay == null ||strCurMonthDay.length()==0) {
			JsonResultIns.setCode("000002");
			JsonResultIns.setMessage("参数TENANT_ID:"+TenantId+",strCurMonthDay:"+strCurMonthDay);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("参数TENANT_ID:"+TenantId+",strCurMonthDay:"+strCurMonthDay);
			SysLogIns.setBUSI_ITEM_1("fail");
			SyslogDao.insert(SysLogIns);
			return JsonResultIns;
		}
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_4(strCurMonthDay);
		// --- 查询当前是否有任务运行 ---
			String runFlag = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.RUNFLAG."+TenantId);
			if(StringUtils.isNotNull(runFlag) == false){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数 :ORDER.USERLABEL.UPDATE.RUNFLAG."+TenantId+" 设置有误 !!!");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000102");
				JsonResultIns.setMessage("参数 :ORDER.USERLABEL.UPDATE.RUNFLAG."+TenantId+" 设置有误 !!!");
				return JsonResultIns;
			}
			if(runFlag.equals("FALSE") == false){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("当前租户:"+TenantId+" 有正在运行的工单用户资料更新任务");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000022");
				JsonResultIns.setMessage("当前租户:"+TenantId+" 有正在运行的工单用户资料更新任务");
				return JsonResultIns;
			}
			//--得到渠道id和处理SQL的集合的列表--
			List<Map<String,String>> orderUserLabelUpdateSqlList = new ArrayList<Map<String,String>>();
			orderUserLabelUpdateSqlList = getorderUserLabelUpdateSql(TenantId,strCurMonthDay,updateType);
			if(orderUserLabelUpdateSqlList == null || orderUserLabelUpdateSqlList.size() == 0){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("当前租户:"+TenantId+" 没有可用的渠道和映射字段");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000033");
				JsonResultIns.setMessage("当前租户:"+TenantId+" 没有可用的渠道和映射字段");
				return JsonResultIns;
			}
			SysLogIns.setLOG_TIME(new Date());
			if(orderUserLabelUpdateSqlList.toString().length()>1024){
				
			}else{
				SysLogIns.setLOG_MESSAGE("当前租户:"+TenantId+",orderUserLabelUpdateSqlList:"+orderUserLabelUpdateSqlList.toString());
			}
		
			SyslogDao.insert(SysLogIns);
			// --- 定义4个线程 ---
			int 	iThreadNum = 4;
			String strTmp = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.THREADSNUM");
			if (strTmp != null) {
				iThreadNum = Integer.parseInt(strTmp);
				if (iThreadNum < 4)
					iThreadNum = 4;
			}
			
			try{
				for(Map<String,String> orderUserLabelUpdateSql:orderUserLabelUpdateSqlList){	
					// --- 执行用户资料更新 ---
					//--OrderUserlabelUpdate 中新增    属性：渠道id，  属性：渠道id对应的处理SQL--
					OrderUserlabelUpdate		OrderUserlabelUpdateIns = new OrderUserlabelUpdate();
					OrderUserlabelUpdateIns.setTenantId(TenantId);
					OrderUserlabelUpdateIns.setStrCurMonthDay(strCurMonthDay);
					OrderUserlabelUpdateIns.setChannelId(orderUserLabelUpdateSql.get("channelId"));//--渠道--
					OrderUserlabelUpdateIns.setUpdateSql(orderUserLabelUpdateSql.get("orderUpdateSql"));//--处理SQL--
					OrderUserlabelUpdateIns.setUpdateType(updateType);//--判断是否是修改字段更新还是账期更新处理的标识--
					ParallelManageThread ParallelManageIns = new ParallelManageThread(OrderUserlabelUpdateIns, iThreadNum);
					Global.getExecutorService().execute(ParallelManageIns);
				}
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("工单用户资料更新启动");
					SyslogDao.insert(SysLogIns);
					// --- 成功返回 ---
					JsonResultIns.setCode("000000");
					JsonResultIns.setMessage("sucess");
			}catch(Exception e){
					e.printStackTrace();
					JsonResultIns.setCode("000001");
					JsonResultIns.setMessage(e.getMessage());
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setBUSI_ITEM_1("工单用户资料更新异常");
					SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
					
			}
			return JsonResultIns;	
		
			/*// --- 执行用户资料更新 ---
			OrderUserlabelUpdate		OrderUserlabelUpdateIns = new OrderUserlabelUpdate();
			OrderUserlabelUpdateIns.setTenantId(TenantId);
			ParallelManageThread ParallelManageIns = new ParallelManageThread(OrderUserlabelUpdateIns, iThreadNum);
			Global.getExecutorService().execute(ParallelManageIns);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("工单用户资料更新启动");
			SyslogDao.insert(SysLogIns);
			// --- 成功返回 ---
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
		}catch(Exception e){
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("工单用户资料更新异常");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;*/
	}
	
	/*
	 * 工单成功过滤（事前检查）
	 */
	public		JsonResult		ordersucessFilter(String TenantId,int ActivitySeqId,String ActivityId){
		JsonResult		JsonResultIns = new JsonResult();
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceControlImpl.class+"-ordersucessFilter");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_5("91");
		SysLogIns.setBUSI_ITEM_2(String.valueOf(ActivitySeqId));
		try{		
			// --- 查询当前是否有任务运行 ---
			String runFlag = SyscommoncfgDao.query("ORDERCHECK.FILTER.RUNFLG."+TenantId);
			if(StringUtils.isNotNull(runFlag) == false){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("参数 :ORDERCHECK.FILTER.RUNFLG."+TenantId+" 设置有误 !!!");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000102");
				JsonResultIns.setMessage("参数 :ORDERCHECK.FILTER.RUNFLG."+TenantId+" 设置有误 !!!");
				return JsonResultIns;
			}
			if(runFlag.equals("FALSE") == false){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("当前租户:"+TenantId+" 有正在运行的工单成功过滤任务");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000022");
				JsonResultIns.setMessage("当前租户:"+TenantId+" 有正在运行的工单成功过滤任务");
				return JsonResultIns;
			}
			
			// --- 查询当前帐期（是否电信） ---
			String providerType = SyscommoncfgDao.query("SERVICE_PROVIDER_TYPE");
			String		curMothDay = null;
			if(providerType!=null && providerType.trim().equals("1")){
				//--电信--
				String month_date_url = SyscommoncfgDao.query("GET_MONTH_TIME");
				Map<String, Object> params  = new HashMap<String, Object>();
				params.put("1", "1");
				params.put("tenant_id",TenantId);
				Map<String,Object> requestMap = new HashMap<String,Object>();
				//电信需要放入req参数中，否则服务无法收到该请求参数
				requestMap.put("req", JSON.toJSONString(params));
				String sendPost = HttpUtil.doGet(month_date_url, requestMap);
				Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
				curMothDay = (String) resultMap.get("MAX_DATE");
				
				
			}else{
				curMothDay = SysFunctionIns.getCurMothDay(TenantId);
				
			}
			if(curMothDay == null){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("当前租户:"+TenantId+" 提取帐期时间失败");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000013");
				JsonResultIns.setMessage("当前租户:"+TenantId+" 提取帐期时间失败");
				return JsonResultIns;
			}	
			
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
			listActivitySucessInfo = CommonMapperIns.getActivityForActivitySeqId(ActivitySeqId, TenantId);
			if(listActivitySucessInfo == null || listActivitySucessInfo.size() == 0){
				JsonResultIns.setCode("000020");
				JsonResultIns.setMessage("没有找到对应的活动或其成功信息");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("没有找到对应的活动或其成功信息");
				SyslogDao.insert(SysLogIns);
				// --- 纪录工单运行表 ---
				PltActivityExecuteLog	PltActivityExecuteLogIns = new PltActivityExecuteLog();
				PltActivityExecuteLogIns.setCHANNEL_ID("0");
				PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
				PltActivityExecuteLogIns.setTENANT_ID(TenantId);
				PltActivityExecuteLogIns.setBUSI_CODE(1011);
				PltActivityExecuteLogIns.setBEGIN_DATE(new Date());
				PltActivityExecuteLogIns.setPROCESS_STATUS(1);
				PltActivityExecuteLogIns.setACTIVITY_ID(ActivityId);
				PltActivityExecuteLogIns.setEND_DATE(new Date());
			PltActivityExecuteLogDao.insert(PltActivityExecuteLogIns);
				return JsonResultIns;
			}
			if(listActivitySucessInfo.size() > 1){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("找到了多条活动或成功纪录");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000030");
				JsonResultIns.setMessage("找到了多条活动或成功纪录");
				return JsonResultIns;
			}
			// --- 查询产品列表 ---
			List<String>   listProductInfo = CommonMapperIns.getProductListForActivity(ActivitySeqId,TenantId);
			
			// --- 调用工单检查 ---
			OrderSucessCheck		OrderSucessCheckIns = new OrderSucessCheck();
			// --- 设置相应的运行参数 ---
			OrderSucessCheckIns.setTableName(SyscommoncfgDao.query("ORDERCHECK.FILTER.TABLENAME"));
				//OrderSucessCheckIns.setChannelId(channel_id);
			OrderSucessCheckIns.setProductInfo(listProductInfo);
			OrderSucessCheckIns.setActivitySeqId(ActivitySeqId);
			OrderSucessCheckIns.setActivityId(ActivityId);
			OrderSucessCheckIns.setTenantId(TenantId);
			OrderSucessCheckIns.setMaxMonthDay(curMothDay);
			OrderSucessCheckIns.setActivitySucessInfo(listActivitySucessInfo.get(0));
			OrderSucessCheckIns.setCheckType((short)1);  // --- 事前过滤 ---
			ParallelManageThread ParallelManageIns = new ParallelManageThread(OrderSucessCheckIns, iThreadNum);
		 	Global.getExecutorService().execute(ParallelManageIns);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("工单成功过滤启动");
			SyslogDao.insert(SysLogIns);
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
		}catch(Exception e){
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("工单成功过滤异常");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}
	
	/*
	 * 工单成功检查（事后检查）
	 */
	public		JsonResult		ordersucessCheck(String tenant_id){
		JsonResult		JsonResultIns = new JsonResult();
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceControlImpl.class+"-ordersucessCheck");
		SysLogIns.setTENANT_ID(tenant_id);
		try{
			// ---  加锁检查标识 ---
			String		curCheckFlag = SyscommoncfgDao.query("ORDERCHECK.SUCESS.RUNFLG."+tenant_id);
			if(curCheckFlag == null){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("ORDERCHECK.SUCESS.RUNFLG."+tenant_id+" 没有设置或有多条设置");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000010");
				JsonResultIns.setMessage("ORDERCHECK.SUCESS.RUNFLG."+tenant_id+" 没有设置或有多条设置");
				return JsonResultIns;
			}
			if("TRUE".equals(curCheckFlag)){
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("当前租户:"+tenant_id+" 正在运行工单成功检查");
				SyslogDao.insert(SysLogIns);
				JsonResultIns.setCode("000011");
				JsonResultIns.setMessage("当前租户:"+tenant_id+" 正在运行工单成功检查");
				return JsonResultIns;
			}
			
			// --- 启动一个新线程执行 ---
			Map<String, Object > tenantInfo = new HashMap<String, Object>();
			tenantInfo.put("TENANT_ID", tenant_id);
			OrderAfterCheckThread	OrderAfterCheckThreadIns = new OrderAfterCheckThread(tenantInfo);
			Global.getExecutorService().execute(OrderAfterCheckThreadIns);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("工单成功检查启动");
			SyslogDao.insert(SysLogIns);
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
		}catch(Exception e){
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage());
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("工单成功检查异常");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
		}finally{

		}	
		return JsonResultIns;
	}
	
	/*
	 * 用户资料同步启动
	 */
	public		boolean			userlabelAsyn(String tenant_id){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceControlImpl.class+"-userlabelAsyn");
		SysLogIns.setTENANT_ID(tenant_id);
		try{
			// --- 先查询出省编号 ---
			String	provId = TenantinfoDao.queryPROV_ID(tenant_id);
			if(provId == null){
				log.error("根据租户编号没有取到省编号:PROV_ID,TENANT_ID="+tenant_id);
				return false;
			}
			Map<String, Object > tenantInfo = new HashMap<String, Object>();
			tenantInfo.put("TENANT_ID", tenant_id);
			tenantInfo.put("PROV_ID", provId);
			Thread ThreadIns = new EntryThread(AsynUserLabelIns, tenantInfo);
			// --- 执行 ---
			log.info("--- begin to execute userlabelasyn ---");
			Global.getExecutorService().execute(ThreadIns);
			log.info("--- return ---");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("用户资料同步启动");
			SyslogDao.insert(SysLogIns);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("用户资料同步异常");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
			return false;
		}
	}
	


	/*
	 * 黑白名单数据同步
	 */	
	public      boolean         blackandwhiteAsyn(String tenant_id){
		SysLog	SysLogIns = new SysLog();
		try {
			// 判断是否正在执行任务   true为正在执行
			boolean runFlag = Boolean.parseBoolean(SysrunningcfgDao.query("ASYNBLACKANDWHITE.RUN.FLAG."+tenant_id));
			if (runFlag) {
				log.warn("--- 当前租户：{} 正在进行黑白数据同步 ---", tenant_id);
				return true;
			}
			log.info("------ 黑白名单数据同步开始 ------");
			long start = System.currentTimeMillis();
			SysrunningcfgDao.update("ASYNBLACKANDWHITE.RUN.FLAG."+tenant_id, "TRUE");

			SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AsynBlackandWhite");
			SysLogIns.setTENANT_ID(tenant_id);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("黑白名单数据同步开始");
			SysFunctionIns.saveSysLog(SysLogIns);
			AsynBlackandWhitelIns.handleData(tenant_id);
			
			long end = System.currentTimeMillis();
			log.info("--- 黑白名单数据同步结束 ,time-consuming:" + (end - start) / 1000.0 + "s ---");
			SysrunningcfgDao.update("ASYNBLACKANDWHITE.RUN.FLAG."+tenant_id, "FALSE");
		} catch (Exception e) {
			e.printStackTrace();
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("黑白名单数据同步异常");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
			SysrunningcfgDao.update("ASYNBLACKANDWHITE.RUN.FLAG."+tenant_id, "FALSE");
			return false;
		}
		return true;
	}

	@Override
	public void GenSenceOrder(final String TenantId) {
		try {
			log.info("begin start kafka task!");
			new Thread(new Runnable() {
				@Override
				public void run() {
					SceneGenOrder sceneGenOrder = new SceneGenOrder();
					sceneGenOrder.setMaxQueueSize(10000);
					sceneGenOrder.setTenantId(TenantId);
					ParallelManage ParallelManageIns = new ParallelManage(sceneGenOrder,1,60);
					ParallelManageIns.execute();
				}
			}).start();
			log.info("load kafka success!");
			log.info("begin dealFailSms");
			sceneService.dealfailsms(TenantId);
			log.info("end dealFailSms");
		} catch (Exception e) {
			log.info("load kafka error!"+e.getMessage());
		}
	}

	@Override
	public void dealfailsms(String TenantId) {
		sceneService.dealfailsms(TenantId);
	}


	/*
	 * 弹窗渠道加手机号索引
	 */
	public boolean addPhoneIndex(String TenantId,String ChannelId,String OrderTableName,int ActivitySeqId,String ActivityId) {
		
		SysLog			SysLogIns = new SysLog();
		try {
			 Map<String, Object> map = new HashMap<String, Object>();
		      map.put("tenantId", TenantId);
		      map.put("activitySeqId", ActivitySeqId);
		      map.put("channelId", ChannelId);
		      map.put("orderTableName", OrderTableName);
		      map.put("activityId", ActivityId);

		      Thread ThreadIns = new EntryThread(AddPhoneIndexIns,map);
		      // --- 执行 ---
		      log.info("------ 准备执行渠道加手机号索引  ------");
		      Global.getExecutorService().execute(ThreadIns);
		      SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AddPhoneIndex");
		      SysLogIns.setTENANT_ID(TenantId);
		      SysLogIns.setLOG_TIME(new Date());
		      SysLogIns.setLOG_MESSAGE("渠道加手机号索引开始执行");
		      SyslogDao.insert(SysLogIns);
		      return true;
		      }catch(Exception e){
		      e.printStackTrace();
		      SysLogIns.setLOG_TIME(new Date());
		      SysLogIns.setBUSI_ITEM_1("渠道加手机号索引任务执行异常");
		      SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
		      return false;
		    }
		  }
		
//		try {
//			 //int limitNum = Integer.parseInt(SyscommoncfgDao.query("TASK.EXECUTE.INTOTABLE")!=null?SyscommoncfgDao.query("TASK.EXECUTE.INTOTABLE"):"10000");
//			int limitNum = 1000;
//			log.info("------ 开始执行{}渠道加手机号索引任务 ------", ChannelId);
//
//			CommonMapperIns.updateBeginStatus(ActivitySeqId, TenantId,
//					ChannelId, OrderTableName);
//
//			long start = System.currentTimeMillis();
//
//			HashMap<String, Object> minRecIdMap = CommonMapperIns.getMinRecId(
//					OrderTableName, ActivitySeqId, TenantId, ChannelId);
//			// if(minRecIdMap == null || minRecIdMap.get("minRecId") == null){
//			// log.info("------ 没有工单可执行 ------");
//			// return true;
//			//
//			// }
//			// int minRecId =
//			// Integer.parseInt(minRecIdMap.get("minRecId").toString());
//			// while(1>0){
//			Map<String, Object> map = new HashMap<String, Object>();
//			// map.put("minRecId",minRecId);
//			// map.put("limitNum",limitNum);
//			map.put("tenantId", TenantId);
//			map.put("activitySeqId", ActivitySeqId);
//			map.put("channelId", ChannelId);
//			map.put("orderTableName", OrderTableName);
//
//			List<HashMap<String, Object>> orderInfoList = CommonMapperIns
//					.getValidPhoneNum(map);
//
//			// if(orderInfoList == null || orderInfoList.isEmpty()){
//			// log.info("------ 已经获取到全部信息 ------");
//			// break;
//			// }
//			Map<String, List<String>> groupPhoneNum = new HashMap<String, List<String>>();
//
//			for (HashMap<String, Object> orderInfo : orderInfoList) {
//
//				String phoneNum = orderInfo.get("PHONE_NUMBER") + "";
//				String tailPhoneNum = phoneNum.substring(phoneNum.length() - 1,
//						phoneNum.length());
//
//				List<String> groupPhoneNumList;
//
//				if (groupPhoneNum.get(tailPhoneNum) == null) {
//					groupPhoneNumList = new ArrayList<String>();
//					groupPhoneNum.put(tailPhoneNum, groupPhoneNumList);
//				} else {
//					groupPhoneNumList = groupPhoneNum.get(tailPhoneNum);
//				}
//				groupPhoneNumList.add(phoneNum);
//			}
//
//			// entry存放的是 key为手机尾号 value为该尾号下的所有手机号
//			for (Map.Entry<String, List<String>> entry : groupPhoneNum
//					.entrySet()) {
//
//				String lastNum = entry.getKey();
//				List<String> phoneNumList = entry.getValue();
//      
//				String tableName = "PLT_ORDER_PHONE_INDEX_" + lastNum;
//                
//				Map<String, Object> mapp = new HashMap<String, Object>();
//				//mapp.put("phoneNumList", phoneNumList);
//				mapp.put("indexTableName", tableName);
//				mapp.put("channelId", ChannelId);
//				mapp.put("activitySeqId", ActivitySeqId);
//				mapp.put("orderTableName", OrderTableName);
//				mapp.put("tenantId", TenantId);
//				if(phoneNumList.size() <= limitNum){ //如果集合数量小于批量,一次全部执行
//					mapp.put("phoneNumList", phoneNumList);
//					try {
//    					CommonMapperIns.insertIntoPhoneNum(mapp);
//    				} catch (Exception e) {
//    					e.printStackTrace();
//    				}
//				} else {
//					List<String> batchPhoneNumList = new ArrayList<String>();
//					int phoneNumSize = phoneNumList.size(); // 手机号数量
//					int cycleCount = (phoneNumSize / limitNum == 0) ? (phoneNumSize / limitNum)
//							: ((phoneNumSize / limitNum) + 1); // 循环的次数
//					for (int i = 0; i < cycleCount; i++) {
//						if ((cycleCount - 1) != i) {
//							batchPhoneNumList = phoneNumList.subList(i * limitNum, (i + 1) * limitNum);
//						} else {
//							batchPhoneNumList = phoneNumList.subList(i * limitNum, phoneNumSize);
//						}
//						mapp.put("phoneNumList", batchPhoneNumList);
//						try {
//							CommonMapperIns.insertIntoPhoneNum(mapp);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//			CommonMapperIns.updateEndStatus(ActivitySeqId, TenantId, ChannelId);
//			long end = System.currentTimeMillis();
//			log.info("------ 渠道加手机号索引任务结束,time-consuming:" + (end - start)
//					/ 1000.0 + "s ------");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
	
	/*
	 * 根据插入记录表更新工单成功数据   受理成功
	 */
	@Override
	public JsonResult productSaveForSuccess(String tenant_id) {
		
		JsonResult		JsonResultIns = new JsonResult();
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceControlImpl.class+"-productSaveForSuccess");
		SysLogIns.setTENANT_ID(tenant_id);
		try {
			// 判断是否正在执行任务   true为正在执行
			String curCheckFlag = SyscommoncfgDao.query("ASYNPRODUCTSAVE.RUN.FLAG."+tenant_id);

			if("TRUE".equals(curCheckFlag)){
				JsonResultIns.setCode("0000");
				JsonResultIns.setMessage("当前租户:"+tenant_id+" 有正在运行的受理成功任务");
//				return JsonResultIns;
			}

			Map<String, Object > tenantInfo = new HashMap<String, Object>();
			tenantInfo.put("TENANT_ID", tenant_id);
			ProductSaveForSuccessThread ProductSaveForSuccessThreadIns = new ProductSaveForSuccessThread(tenantInfo);
			Global.getExecutorService().execute(ProductSaveForSuccessThreadIns);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("受理成功启动."+tenant_id);
			SyslogDao.insert(SysLogIns);
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
		} catch(Exception e){
			e.printStackTrace();
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.getMessage()+"."+tenant_id);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("受理成功异常");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
		}
		return JsonResultIns;
	}
	public List<Map<String,String>> getorderUserLabelUpdateSql(String tenantId,String strCurMonthDay,short updateType){
		SysLog			SysLogIns = new SysLog();
			SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-getorderUserLabelUpdateSql");
			SysLogIns.setTENANT_ID(tenantId);
	      
		//--更新工单用户资料的渠道列表--
		List<String> channelList = new ArrayList<String>();
		channelList = CommonMapperIns.getMappingChannel(tenantId);
		if(channelList==null ||channelList.size() ==0 ) {
			SysLogIns.setLOG_TIME(new Date());
		      SysLogIns.setLOG_MESSAGE("无法找到可用渠道");
		      SyslogDao.insert(SysLogIns);
			return null;
		}
		
		List<Map<String,String>> orderUpdateSqlList = new ArrayList<Map<String,String>>();
		//--按照渠道号从映射表中查找工单表字段与客户标签表字段的映射关系--
		for(String channelId:channelList){
			Map<String,String> orderUpdate = new HashMap<String,String>();
			//--通过映射表的查询，得到一个工单表字段与客户标签表字段的映射数组--
		List<Map<String,String>> columns = 	CommonMapperIns.getChannelOrderMapping(channelId,tenantId);
		if(columns==null ||columns.size() ==0 ) continue;
		
		StringBuilder  orderUpdateSql = new StringBuilder();
		orderUpdateSql.append("/*!mycat:sql=select * FROM PLT_ACTIVITY_INFO WHERE TENANT_ID = 'TTTTTENANT_ID'*/ ");//--mycat路由--
		orderUpdateSql.append(" UPDATE TABLEAAAAA a,TABLEBBBBB b SET ");//--update语句头--
		//--set的字段--
		for(int i = 0;i<columns.size();++i){
			//if(i > 0) orderUpdateSql.append(",");
			orderUpdateSql.append("a."+columns.get(i).get("ORDER_COLUMN"));//a.getKey = b.getValue
					orderUpdateSql.append(" = ");
			orderUpdateSql.append("b."+columns.get(i).get("USER_LABEL_COLUMN"));
			orderUpdateSql.append(",");
		}
		orderUpdateSql.append("a.USERDATA_DEAL_MONTH = '"+strCurMonthDay+"' ");
		//--update语句尾--
		orderUpdateSql.append(" WHERE a.USER_ID = b.USER_ID AND a.REC_ID >= MINID  AND a.REC_ID < MAXID  AND a.CHANNEL_ID = '"+channelId+"' ");
		
		if(updateType == (short)0){
			//--ORDER_DEAL_MONTH != StrCurMonthDay--只更新旧账期下的工单数据(账期更新)
			orderUpdateSql.append(" AND a.ORDER_DEAL_MONTH <> '"+strCurMonthDay+"' ");
		}else if (updateType == (short)1){
			//--更新所有的工单数据(修改更新的字段)--
		}
		
		orderUpdate.put("channelId", channelId);
		orderUpdate.put("orderUpdateSql", orderUpdateSql.toString());
		
		orderUpdateSqlList.add(orderUpdate);
		}
		log.info("updateUserLabelSql："+orderUpdateSqlList.toString());
		if(orderUpdateSqlList == null || orderUpdateSqlList.size() == 0){
		      SysLogIns.setLOG_TIME(new Date());
		      SysLogIns.setLOG_MESSAGE("无法找到可用映射字段");
		      SyslogDao.insert(SysLogIns);
		}
		return orderUpdateSqlList;
	}
}
