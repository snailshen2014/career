package com.bonc.busi.task.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.task.bo.ScenePowerInfo;
import com.bonc.busi.task.bo.ScenePowerStatus;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.SceneMapper;
import com.bonc.busi.task.service.SceneService;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.DateUtil.CurrentDate;
import com.bonc.utils.DateUtil.DateFomart;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Service("sceneService")
public class SceneServiceImpl implements SceneService{
	private static final Logger logger = Logger.getLogger(SceneServiceImpl.class);

	private final int TRANSACTION_TIMES = 100000;
	
	@Autowired
	private SceneMapper mapper;
	
//	@Autowired
//	private FilterOrderService filterOrderService;
	
	@Autowired
	private BusiTools  AsynDataIns;
	
	@SuppressWarnings("unchecked")
	@Override
	public void dealfailsms() {
//		filterOrderService.getJodisProperties();
//		JodisPoolConfiguration jpc = new JodisPoolConfiguration();
//		Jedis jedis = jpc.createJedisPool().getResource();
//		String value = jedis.get("FailSms"+CurrentDate.currentDateFomart(DateFomart.EN_DATE));
		
		//log
		logger.info("begin --- dealfailsms --- task ");
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("70");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSPONSOR("dealfailsms");
		PltCommonLogIns.setBUSI_CODE("dealfailsms_index");
		
		int index = 1;	//默认值
		int  step = 500; //每次数量
		
		//获取index
		String fail_sms = AsynDataIns.getValueFromGlobal("DEAL_FAIL_SMS_INDEX");
		String[] args = fail_sms.split("\\|");
		SimpleDateFormat sdf = new SimpleDateFormat(DateFomart.EN_DATE);  
		Calendar cal = Calendar.getInstance();
		try {
			//如果获取数据和now不是同一天，则循环处理之前的数据，防止服务停滞几天或者遗漏11点后的数据。
			cal.setTime(sdf.parse(args[0]));
			while(!isSameDay(cal.getTime(),new Date())){
				String index1 = AsynDataIns.getValueFromGlobal("DEAL_FAIL_SMS_INDEX").split("\\|")[1]; //获取上次的index
				String queryTime = new SimpleDateFormat(DateFomart.EN_DATE).format(cal.getTime()); //转换格式为yyyy-MM-dd
				index1 = callAndUpdate(Integer.parseInt(index1), step ,queryTime); 
				cal.add(Calendar.DATE, +1);
				AsynDataIns.setValueToGlobal("DEAL_FAIL_SMS_INDEX", new SimpleDateFormat(DateFomart.EN_DATE).format(cal.getTime())+ "|1");
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_ITEM_1( index1);
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			}
			
		} catch (ParseException e) {
			logger.info("----dealfailsms-----时间转换错误----检查DEAL_FAIL_SMS_INDEX配置 ----");
			e.printStackTrace();
		} 
		//正常处理当天数据
		String today = CurrentDate.currentDateFomart(DateFomart.EN_DATE);
		index = Integer.parseInt(AsynDataIns.getValueFromGlobal("DEAL_FAIL_SMS_INDEX").split("\\|")[1]);
		String reIndex = callAndUpdate(index, step , today);
		AsynDataIns.setValueToGlobal("DEAL_FAIL_SMS_INDEX",today + "|" + reIndex);
		
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_ITEM_1( reIndex);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		logger.info("end --- dealfailsms --- task ");

//		if (jedis != null) {
// 	        jedis.close();
// 	    }
	}
	
	/**
	 * 
	 * @param index  游标位置
	 * @param step		循环每次数量
	 * @param queryTime		调用接口查询时间 时间格式为 yyyy-MM-dd
	 * @return
	 */
	private String callAndUpdate(int index ,int step , String  queryTime){
//		String url  = "http://10.245.2.222:8080/smsinterface/sms/cjyx/failsmslist";
		String url = AsynDataIns.getValueFromGlobal("SMSINTERFACE_FAILSMSLIST");
		while(1 > 0){
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("sendTime", queryTime);
			param.put("startNum", index);
			param.put("endNum", index + step);
			String result = HttpUtil.sendPost(url, JSON.toJSONString(param));
			if (null == result || result.contains("error")) {
				logger.info("------  response is null or have error ----" + result);
				break;
			}
//			Map<String, Object> rmap = JSON.parseObject( result,new TypeReference<Map<String, Object>>(){} );
			//update error sms state
			HashMap<String, List<Map<String, String>>> reqMap = null;
			try {
				reqMap = (HashMap<String, List<Map<String, String>>>) JSON.parseObject(result, Map.class);
				for (int i = 0; i < reqMap.get("msg").size(); i++) {
					String telphone = reqMap.get("msg").get(i).get("telphone");
					String uniqueId = reqMap.get("msg").get(i).get("uniqueId");
					mapper.updateFailSms( telphone , uniqueId);
				}
			} catch (Exception e) {
				logger.info("------------resultConvertError  or   updateError------------" );
				return "0";
			}
			index = index + reqMap.get("msg").size() ;
			if (step > reqMap.get("msg").size()) {
				break;
			}
		}
		
		return Integer.toString(index);
	}
	
	
	
	/**
	 * 判断某一时间是否在一个区间内
	 * 
	 * @param sourceTime  间区间,半闭合,如[00:00-01:00)
	 * @param curTime  需要判断的时间 如10:00
	 * @throws IllegalArgumentException
	 */
	public  boolean isInTime(String sourceTime, String curTime) {
	    if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }
	    if (curTime == null || !curTime.contains(":")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
	    }
	    String[] args = sourceTime.split("-");
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    try {
	        long now = sdf.parse(curTime).getTime();
	        long start = sdf.parse(args[0]).getTime();
	        long end = sdf.parse(args[1]).getTime();
	        if (args[1].equals("00:00")) {
	            args[1] = "24:00";
	        }
	        if (end < start) {
	            if (now >= end && now < start) {
	                return false;
	            } else {
	                return true;
	            }
	        } 
	        else {
	            if (now >= start && now < end) {
	                return true;
	            } else {
	                return false;
	            }
	        }
	    } catch (ParseException e) {
	        e.printStackTrace();
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }

	}
	
	//判断是同一天
	public  boolean isSameDay(Date date1, Date date2) {  
	    Calendar calDateA = Calendar.getInstance();  
	    calDateA.setTime(date1);  
	    Calendar calDateB = Calendar.getInstance();  
	    calDateB.setTime(date2);  
	    return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)  
	            && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)  
	            && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB  
	                    .get(Calendar.DAY_OF_MONTH);  
	}  
	
	@Override
	public HashMap<String, Object> querySuccessNum(HashMap<String, Object> req) {
		// 渠道id
		String channelId = (String) req.get("channelId");
		// 先查询活动信息
		List<Object> recIds = mapper.queryActivitySeq(req);
		if (recIds.size() <= 0) {
			throw new BoncExpection(IContants.CODE_FAIL, " activity not exists! ");
		}

		String recSql = "";
		for (int i = 0, max = recIds.size() - 1; i <= max; i++) {
			recSql = (recSql + recIds.get(i) + (i == max ? "" : ","));
		}
		req.put("recSql", recSql);

		if (!StringUtil.validateStr(req.get("contactDateStart"))) {
			req.put("contactDateStartSql", " AND BEGIN_DATE>=#{contactDateStart} ");
		}
		if (!StringUtil.validateStr(req.get("contactDateEnd"))) {
			req.put("contactDateEndSql", " AND BEGIN_DATE<=#{contactDateEnd} ");
		}

		List<HashMap<String, Object>> resp = mapper.querySuccessNum(req);
		if(null!=resp && !resp.isEmpty()){
			for (HashMap<String, Object> numMap : resp){
				String  numChannel = (String) numMap.get("CHANNEL_ID");
				if(numChannel.equals(channelId)){
					return numMap;
				}
			}
		}
		return new HashMap<>();
	}

	@Override
	public List<HashMap<String, Object>> queryHandleNum(HashMap<String, Object> req) {
		// 先查询活动信息
		List<Object> recIds = mapper.queryActivitySeq(req);
		if (recIds.size() <= 0) {
			throw new BoncExpection(IContants.CODE_FAIL, " activity not exists! ");
		}
		String recSql = "";
		for (int i = 0, max = recIds.size() - 1; i <= max; i++) {
			recSql = (recSql + recIds.get(i) + (i == max ? "" : ","));
		}
		req.put("recSql", recSql);
		req.put("contactDateEndSql", " AND BEGIN_DATE<=#{contactDateEnd} ");
		List<HashMap<String, Object>> resp = mapper.queryHandleNum(req);
		return resp;
	}

	@Override
	public JsonResult addSenceRecordBatch(HashMap<Object, Object> request) {
		JsonResult result = new JsonResult();
		result.setCode("0");
		result.setMessage("insert sence record success");
		List<String> phoneNums;
		try {
			phoneNums = (List<String>) request.get("phoneNums");
		} catch (Exception e) {
			result.setCode("3");
			result.setMessage("参数信息有误" + e.getMessage());
			return result;
		}
		// 1.获取批次号 插入一条状态信息 状态码为0
		ScenePowerStatus scenePowerStatus = new ScenePowerStatus();
		scenePowerStatus.setBeginDate(new Date());
		int batchId = AsynDataIns.getSequence("SCENCE_BATCH_ID");
		String tenantId = (String) request.get("tenantId");
		scenePowerStatus.setBatchId(batchId);
		scenePowerStatus.setEndDate(new Date());
		scenePowerStatus.setStatus("0");
		scenePowerStatus.setTenantId(tenantId);
		// 1.1 如果批次号重复则清除之前的批次号数据再插入新的数据
		Integer oldBatchId = mapper.queryIsExistBatchId(batchId, tenantId);
		if (null != oldBatchId || !"".equals(oldBatchId)) {
			mapper.delOldScenePowerStatus(batchId, tenantId);
		}

		mapper.addScenePowerStatus(scenePowerStatus);

		// 2.批量数据入库
		ScenePowerInfo scenePowerInfo = new ScenePowerInfo();
		scenePowerInfo.setBatchId(batchId);
		scenePowerInfo.setTenantId((String) request.get("tenantId"));
		scenePowerInfo.setPhoneNumber(phoneNums);
		Integer insertPhoneNums = insertBatchRecord(scenePowerInfo);
		result.setMessage("insertPhoneNums :" + insertPhoneNums);
		result.setData(batchId);
		// 3.入库完成更新状态库 状态码为1
		scenePowerStatus.setEndDate(new Date());
		scenePowerStatus.setStatus("1");
		mapper.updateScenePowerStates(scenePowerStatus);
		// 4.异步处理扫表查询 进行统计 启动多线程处理 (改在ordertask中进行)
		// AsynDealAnalyse();

		// 5.返回插入完成信息

		return result;
	}
	/**
	 * 批量插入手机号
	 * 
	 * @param scenePowerInfo
	 * @return 返回成功数量
	 */
	private Integer insertBatchRecord(ScenePowerInfo scenePowerInfo) {
		Integer countResult = 0;
		List<String> phoneNums = scenePowerInfo.getPhoneNumber();
		// 拆分手机号，50000为一次

		for (int i = 0; i < ((phoneNums.size() % TRANSACTION_TIMES != 0) ? phoneNums.size() / TRANSACTION_TIMES + 1
				: phoneNums.size() / TRANSACTION_TIMES); i++) {
			// 拼接手机号
			StringBuilder sbb = new StringBuilder();
			String valueSql = "";
			for (int j = i * TRANSACTION_TIMES; j < i * TRANSACTION_TIMES + TRANSACTION_TIMES
					&& j < phoneNums.size(); j++) {
				sbb.append("(");
				sbb.append("'").append(scenePowerInfo.getBatchId()).append("',");
				sbb.append("'").append(String.valueOf(phoneNums.get(j))).append("',");
				sbb.append("'").append(scenePowerInfo.getTenantId().toString()).append("'),");
				valueSql = sbb.substring(0, sbb.lastIndexOf(","));
				valueSql += ")";
			}
			Integer insertPhoneNums = mapper.addScenePowerInfo(valueSql.substring(0, valueSql.length() - 1));
			countResult += insertPhoneNums;
			sbb.setLength(0);
		}
		return countResult;
	}

	@Override
	public JsonResult queryScencePowerStatus(HashMap<Object, String> request) {
		JsonResult result = new JsonResult();
		result.setCode("0");
		result.setMessage("query sence record success");
		if (null == request.get("batchId")) {
			result.setCode("-1");
			result.setMessage("参数信息有误");
			return result;
		}
		// 1.根据批次号查询对应状态码
		HashMap<String, Object> statusAndNums = mapper.queryScencePowerStatus(String.valueOf(request.get("batchId")),
				(String) request.get("tenantId"));
		if (null == statusAndNums) {
			result.setMessage("查询结果为空");
			return result;
		}
		// 2.1 状态码为3 返回处理完成，同时count数值
		// 2.2 状态码为2 返回正在处理
		if ("3".equals(statusAndNums.get("STATE"))) {
			result.setCode("3");
			result.setMessage("处理完成");
			result.setData(statusAndNums.get("RESULT_NUM"));
		} else if ("2".equals(statusAndNums.get("STATE"))) {
			result.setCode("2");
			result.setMessage("处理中");
		} else if ("1".equals(statusAndNums.get("STATE"))) {
			result.setCode("1");
			result.setMessage("处理中");
		} else {
			result.setCode("0");
			result.setMessage("未开始");
		}
		return result;
	}
}
