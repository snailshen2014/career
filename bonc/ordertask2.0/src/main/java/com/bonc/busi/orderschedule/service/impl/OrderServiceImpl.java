package com.bonc.busi.orderschedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.*;
import com.bonc.busi.orderschedule.bo.*;
import com.bonc.busi.orderschedule.files.OrderFileMannager;
import com.bonc.busi.orderschedule.log.LogToDb;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.task.service.BaseTaskSrv;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.HttpUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Service("orderService")
@EnableAutoConfiguration
// @ConfigurationProperties(prefix = "xcloud", ignoreUnknownFields = false)

/*
 * @Desc: order producer service,the entry of the scheduled task
 * 
 * @Author: shenyanjun@bonc.com.cn
 * 
 * @Time: 2017-04-12 for productization add
 */


public class OrderServiceImpl implements OrderService {
	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);
	private static final String selectField = "b.TENANT_ID," + "a.USER_ID," + "a.DEVICE_NUMBER," + "b.ORGPATH,"
			+ "0 as ORDER_STATUS," + "b.MONTH_ID as DEAL_MONTH," + "a.DATA_TYPE as SERVICE_TYPE ," + "b.CITYID,"
			+ "b.AREAID ," + "c.AREAID as USER_ORG_ID," + "c.ORGPATH as USER_PATH";

	private static final String ORDER_COLUMLIST = " (ACTIVITY_SEQ_ID,BATCH_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID"
			+ ",USER_ID,PHONE_NUMBER,ORG_PATH,ORDER_STATUS,DEAL_MONTH,SERVICE_TYPE,CITYID,AREAID"
			+ ",USER_ORG_ID,USER_PATH,BEGIN_DATE,END_DATE,LAST_UPDATE_TIME,INPUT_DATE,INVALID_DATE,";

	//order file name
	private String filename;
	//the channel list that has been generated orders
	private List<String> finishedOrderChannelList;

	// PLT_ACTIVITY_INFO:REC_ID
	private Integer activitySeqId;

	private String userLabelSql;
	private String userLabelColumn;

	// for record order begin_date,end_date
	private String orderBeginDate;
	private String orderEndDate;

	// split number
	private int splitNum;

	// for judging user label status
	private String userLabelStatusSql;
	// 1:open;0:close
	private String judgeFlag;

	// statistis service
	//filter order service
	@Autowired
	private FilterOrderService filterService;
	//mapper bean
	@Autowired
	private OrderMapper ordermapper;

	@Autowired
	private BusiTools AsynDataIns;

	@Autowired
	private BaseMapper TaskBaseMapperDao;
	//filter orders for success 
	@Autowired
	private BaseTaskSrv baseTask;

	private Integer getActivitySeqId() {
		return this.activitySeqId;
	}

	public String getFileName() {
		return filename;
	}

	public String getSysDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	public String getCurrentTime(String formater) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(formater);// 可以方便地修改日期格式
		return dateFormat.format(now);
	}

	public void genFileName() {
		String name = "order_tmp_";
		Format format = new SimpleDateFormat("yyyyMMddHHmmss");
		name += format.format(new Date());
		this.filename = name;
	}

	private String HttpRequest(String url, HashMap<String, String> param) {
		HashMap<String, Object> httpParam = new HashMap<String, Object>();
		for (Entry<String, String> entry : param.entrySet()) {
			httpParam.put(entry.getKey(), entry.getValue());
		}

		return HttpUtil.doGet(url, httpParam);

	}

	private Timestamp getDateTime(String datetime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date parsed = null;
		try {
			parsed = format.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new java.sql.Timestamp(parsed.getTime());

	}

	private Timestamp getDate(String datetime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date parsed = null;
		try {
			parsed = format.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new java.sql.Timestamp(parsed.getTime());

	}

	/**
	 * insert activity info
	 * 
	 * @param act
	 */
	private void recordActivityInfo(ActivityProvPo act) {
		PltActivityInfo ac_obj = new PltActivityInfo();
		ac_obj.setREC_ID(this.activitySeqId);
		ac_obj.setACTIVITY_ID(act.getActivityId());
		ac_obj.setACTIVITY_DIVISION(act.getActivityDivision());
		ac_obj.setACTIVITY_NAME(act.getActivityName());

		if (act.getActivityTheme() != null)
			ac_obj.setACTIVITY_THEME(act.getActivityTheme());

		ac_obj.setACTIVITY_THEMEID(act.getActivityThemeCode());
		//  "activityType":"活动类型：1、周期性（按月），2、周期性（按日），3、一次性"
		ac_obj.setACTIVITY_TYPE(act.getActivityType());
		// 活动开始日期"
		if (act.getStartDate() != null)
			ac_obj.setBEGIN_DATE(getDate(act.getStartDate()));

		if (act.getEndDate() != null)
			ac_obj.setEND_DATE(getDate(act.getEndDate()));
		//   "state":"活动状态：8、停用，9、启用，10、未执行，11、暂停,12、暂存，13、审批中"
		ac_obj.setORI_STATE(act.getState());
		ac_obj.setCREATE_NAME(act.getCreateName());

		if (act.getCreateDate() != null)
			ac_obj.setCREATE_DATE(getDate(act.getCreateDate()));
		ac_obj.setORG_RANGE(act.getOrgRange());
		ac_obj.setTENANT_ID(act.getTenantId());
		// 初始状态设置0，工单生成后改为1
		ac_obj.setACTIVITY_STATUS(0);
		ac_obj.setACTIVITY_DESC(act.getActivityDesc());
		ac_obj.setGROUP_ID(act.getUserGroupId());
		ac_obj.setGROUP_NAME(act.getUserGroupName());
		// "urgencyLevel":"优先级：1、高，2、中，3、低",
		ac_obj.setACTIVITY_LEVEL(act.getUrgencyLevel());
		// "parentActivity":"关联总部活动Id"
		ac_obj.setPARENT_ACTIVITY(act.getParentActivity());
		// "policyId":"所属政策Id"
		ac_obj.setPOLICY_ID(act.getPolicyId());
		// 数据更新周期 1、月，2、日，3、一次性",
		if (act.getActivityType() != null)
			ac_obj.setORDER_GEN_RULE(Integer.parseInt(act.getActivityType()));
		// 工单周期
		if (act.getOrderCycle() != null)
			ac_obj.setORDER_LIFE_CYCLE(Integer.parseInt(act.getOrderCycle()));
		// "工单更新规则 1、有进有出，2、覆盖",
		if (act.getOrderUpdateRule() != null)
			ac_obj.setORDER_UPDATE_RULE(Integer.parseInt(act.getOrderUpdateRule()));
		// 是否剔除黑名单 1、是，0、否",
		if (act.getIsDeleteBlackUser() != null)
			ac_obj.setFILTER_BLACKUSERLIST(Integer.parseInt(act.getIsDeleteBlackUser()));
		// 是否剔除白名单 1、是，0、否",
		if (act.getIsDeleteWhiteUser() != null)
			ac_obj.setFILTER_WHITEUSERLIST(Integer.parseInt(act.getIsDeleteWhiteUser()));

		// 是否同一活动分类用户剔除
		if (act.getIsDeleteSameType() != null)
			ac_obj.setDELETE_ACTIVITY_USER(Integer.parseInt(act.getIsDeleteSameType()));
		// 是否同一活动成功标准类型用户剔除",
		if (act.getIsDeleteSameSuccess() != null)
			ac_obj.setDELETE_SUCCESSRULE_USER(Integer.parseInt(act.getIsDeleteSameSuccess()));
		// 仅针对处于接触频次限制的目标客户：1、发送工单，0、不发送工单 (先不考虑字段)
		ac_obj.setIS_SENDORDER(act.getIsSendOrder());
		// "orgLevel":"活动行政级别:1、集团，2、省级，3、市级，4、其他"
		ac_obj.setORG_LEVEL(act.getOrgLevel());
		// :"客户经理与弹窗互斥发送规则：1、各自执行，0、互斥执行"
		ac_obj.setOTHER_CHANNEL_EXERULE(act.getOtherChannelExeRule());
		// :"短信微信互斥发送规则：1、各自重复发送，0、互斥发送
		ac_obj.setSELF_SEND_CHANNEL_RULE(act.getSelfSendChannelRule());
		//  "strategyDesc":"策略描述",
		ac_obj.setSTRATEGY_DESC(act.getStrategyDesc());
		// "电子渠道互斥发送规则：1、各自展示，0、展示其中一个
		ac_obj.setECHANNEL_SHOW_RULE(act.geteChannelShowRule());
		ac_obj.setPARENT_ACTIVITY_NAME(act.getParentActivityName());
		ac_obj.setPARENT_ACTIVITY_STARTDATE(act.getParentActivityStartDate());
		ac_obj.setPARENT_ACTIVITY_ENDDATE(act.getParentActivityEndDate());
		ac_obj.setPARENT_PROVID(act.getParentProvId());
		ac_obj.setCREATOR_ORGID(act.getCreateOrgId());
		ac_obj.setCREATOR_ORG_PATH(act.getCreateOrgPath());
		ac_obj.setUSERGROUP_FILTERCON(act.getUserGroupFilterCondition());
		ac_obj.setLAST_ORDER_CREATE_TIME(getDateTime(getCurrentTime("yyyy-MM-dd HH:mm:ss")));
		// 预留百分比
		if (act.getObligateOrder() != null)
			ac_obj.setREMAIN_PERCENT(Integer.parseInt(act.getObligateOrder()));
		commitActivityInfo(ac_obj);

	}

	private void commitSuccessInfo(ActivityProvPo act) {
		SuccessStandardPo success = act.getSuccessStandardPo();
		if (success != null) {
			String activity_seq_id = getActivitySeqId().toString();
			success.setTenantId(act.getTenantId());
			success.setActivityId(act.getActivityId());
			success.setActivity_seq_id(activity_seq_id);
			ordermapper.InsertSuccessStandardPo(success);
			List<SuccessProductPo> list = success.getSuccessProductList();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					SuccessProductPo p = list.get(i);
					p.setActivityId(act.getActivityId());
					p.setTenantId(act.getTenantId());
					p.setActivity_seq_id(activity_seq_id);
					ordermapper.InsertProduct(p);
				}
			}
		}

	}

	/**
	 * 
	 * @param activity
	 *            object
	 * @param orderIssuedRule
	 * @return order temp table
	 */
	private String createTempOrderInfo(ActivityProvPo act, String orderIssuedRule, String targetid) {

		String userGroupId = act.getUserGroupId();
		// String rule_id = actjson.getr
		// log
		logger.info("[OrderCenter] Call oracle interface get usergroup info,id:" + userGroupId);
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		
		HashMap<Integer,String> params = new HashMap<Integer,String>();
		params.put(1, userGroupId);
		params.put(3, targetid);
		params.put(4, act.getActivityId());
		params.put(5, this.activitySeqId.toString());
		LogToDb.writeLog(SerialId, "ORDER_GEN", "GET_USER_GROUP_INFO_BEGIN", params);
		
		// get usergroup condition sql
		String con = getUserGroupInfo(userGroupId);
		if (con == null) {
			logger.error("[OrderCenter] Can not get user group info,user gourp sql null.");
			return null;
		}
		// log
		params.put(10, con);
		LogToDb.writeLog(SerialId, "ORDER_GEN", "GET_USER_GROUP_INFO_END", params);
		
		// multi and one orderissuleruler using same api
		// String xCloudUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_DIVIDE");
		String xCloudUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_MULT_DIVIDE");
		// 参数格式：{ rule_type_id:规则ID,rule_sql:查询条件, rule_type_sort:[{id: 字段对应关系ID
		// }]}

		// String jsonStr = "{rule_type_id:'4',tenant_id:'uni076'}";
		String resJson = "{rule_type_id:'" + orderIssuedRule + "',";
		resJson += "ruleOrgPath:'";
		if (targetid.equals("5"))
			resJson += act.getFrontlineChannelPo().getRuleOrgPath();

		resJson += "',";
		resJson += "rule_sql:\"";
		resJson += con;
		resJson += "\",tenant_id:'";
		resJson += act.getTenantId();
		resJson += "',target_id:'";
		resJson += targetid;
		resJson += "',business_id:'";
		resJson += act.getActivityId();
		resJson += "'}";

		logger.info("[OrderCenter] Call resource interface request json:" + resJson);
		// log
		params.put(1, resJson);
		LogToDb.writeLog(SerialId, "ORDER_GEN", "RESOURCE_ASSIGN_BEGIN", params);
		// call resource assign api
		String resourceRep = requestResource(xCloudUrl, resJson);
		logger.info("[OrderCenter] resource's respond=" + resourceRep);

		// log
		params.put(1, resourceRep);
		LogToDb.writeLog(SerialId, "ORDER_GEN", "RESOURCE_ASSIGN_END", params);
		// parse resourece respond ResourceRespond
		ResourceRsp res = new ResourceRsp();
		String resourceTable = null;
		String draw_business_id = null;
		if (resourceRep != null && !resourceRep.equals("null") && resourceRep.length() > 0) {
			res = JSON.parseObject(resourceRep, ResourceRsp.class);
			resourceTable = res.getTemp_table();
			draw_business_id = res.getDraw_business_id();

		} else
			return resourceTable;

		String statusUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_DIVIDE_STATUS");
		// log
		params.put(1, orderIssuedRule);
		params.put(2, statusUrl);
		LogToDb.writeLog(SerialId, "ORDER_GEN", "RESOURCE_GET_STATUS_BEGIN", params);
		xCloudUrl = statusUrl;
		// multi orderissuerule
		int delay = 0;
		while (true) {
			String multiRequest = "{\"";
			multiRequest += "draw_business_id\":";
			multiRequest += "\"";
			multiRequest += draw_business_id;
			multiRequest += "\"}";
			// call get resource assign status api
			String rspStatus = requestResource(xCloudUrl, multiRequest);
			logger.info("[OrderCenter] Multi ruler rsp:" + rspStatus);
			if (rspStatus.indexOf("3") != -1)
				break;
			else
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
			logger.info("[OrderCenter] Call GetStatus() running...");
			// only wait max time 20 minute
			if (delay++ >= 120) {
				logger.error("[OrderCenter] get Ressource Status timeout.");
				return null;
			}
		}
		// log
		LogToDb.writeLog(SerialId, "ORDER_GEN", "RESOURCE_GET_STATUS_END", params);
		return resourceTable;

	}

	private static String requestResource(String url, String json) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		StringBuffer buf = new StringBuffer();
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json; charset=utf-8");

			StringEntity inputEntity = new StringEntity(json, "utf-8");
			inputEntity.setContentType("application/json");
			inputEntity.setContentEncoding("utf-8");
			httpPost.setEntity(inputEntity);

			httpResponse = httpClient.execute(httpPost);
			// System.out.println("RequestResource httpResponse =" +
			// httpResponse);
			System.out.println("[OrderCenter] RequestResource httpResponse status: =" + httpResponse.getStatusLine());
			String output = "";
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader((httpResponse.getEntity().getContent())));

			while ((output = bufferedReader.readLine()) != null) {
				buf.append(output);
			}
			// System.out.println(buf);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[OrderCenter] RequestResource cat IOException:" + httpResponse.getStatusLine());

		}

		finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buf.toString();
	}

	private int downLoadFile(String ip, String user, String passwd, String port, String remote, String local) {
		String absRemote = remote;
		absRemote = absRemote.replaceFirst("HDFS:", "");

		int downRtn = -1;
		String ftpRtn = FtpTools.downloadXcloudFile(ip, user, passwd, Integer.parseInt(port),
				absRemote + getFileName() + ".csv", local + getFileName() + ".csv", true);
		if ("000000".equals(ftpRtn))
			downRtn = 0;
		else
			logger.error("Down load xcloudFile error" + ftpRtn);
		return downRtn;

	}

	private boolean loadDataToMysqlByFileName(String channel_id, String tenant_id, String fileName, String actId) {
		String loadSql = "LOAD DATA   local INFILE  '";
		loadSql += fileName;
		// loadSql += "order_tmp_20161119202037.csv";
		loadSql += "'";
		// loadSql += " replace into table ";
		loadSql += " IGNORE into table ";
		// black file
		if (fileName.indexOf("black") != -1) {
			loadSql += " PLT_ORDER_INFO_BLACK fields  terminated by \'|\' ";
		} else {
			// sms
			if (channel_id.equals("7"))
				loadSql += " PLT_ORDER_INFO_SMS fields  terminated by \'|\' ";
			else if (channel_id.equals("5"))// frontline
				loadSql += " PLT_ORDER_INFO fields  terminated by \'|\' ";
			else if (channel_id.equals("1") || channel_id.equals("2") || channel_id.equals("9"))
				loadSql += " PLT_ORDER_INFO_ONE fields  terminated by \'|\' ";
			else if (channel_id.indexOf("8") != -1)
				loadSql += " PLT_ORDER_INFO_POPWIN fields  terminated by \'|\' ";
			else if (channel_id.equals("11"))
				loadSql += " PLT_ORDER_INFO_WEIXIN fields  terminated by \'|\' ";
			else {
				logger.error("channel id error!" + channel_id);
				return false;
			}
		}
		loadSql += " LINES TERMINATED BY \'\\n\' ";
		loadSql += ORDER_COLUMLIST;
		// user label column
		loadSql += this.userLabelColumn;
		// log
		PltCommonLog logdb = new PltCommonLog();
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("55");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("ORDER_GEN");
		logdb.setBUSI_CODE("LOADMYSQL");
		logdb.setBUSI_ITEM_1(loadSql);
		logdb.setBUSI_ITEM_4(actId);
		logdb.setBUSI_ITEM_5(this.activitySeqId.toString());
		AsynDataIns.insertPltCommonLog(logdb);

		// load data by mysql
		if (AsynDataIns.loadDataInMysql(loadSql, tenant_id)) {
			logger.info("[OrderCenter] Load data file:" + fileName + " finished status ok.");
			// delete local file
			AsynDataIns.deleteFile(fileName);
			return true;
		} else {
			logger.error("[OrderCenter] Load data file:" + fileName + " finished status error.");
			// delete local file
			AsynDataIns.deleteFile(fileName);
			return false;
		}

	}

	/**
	 * update activity_status=1,and order_status = 5 ,when order records all be
	 * created
	 * 
	 * @param act
	 */
	private void updateActivityStatus(ActivityProvPo act) {
		PltActivityInfo ac_obj = new PltActivityInfo();
		ac_obj.setACTIVITY_ID(act.getActivityId());
		ac_obj.setTENANT_ID(act.getTenantId());
		ac_obj.setLAST_ORDER_CREATE_TIME(getDateTime(getCurrentTime("yyyy-MM-dd HH:mm:ss")));
		// set order_begin_date,order_end_date
		if (this.orderBeginDate != null)
			ac_obj.setORDER_BEGIN_DATE(getDateTime(this.orderBeginDate));
		if (this.orderEndDate != null)
			ac_obj.setORDER_END_DATE(getDateTime(this.orderEndDate));
		// get activity rec_id
		Integer ActRecId = getActivitySeqId();
		for (String channelid : this.finishedOrderChannelList) {
			logger.info("[OrderCenter] Update order status begin:" + channelid + " seqid:" + ActRecId);

			// avoid big transction
			ActivityProcessLog processLog = new ActivityProcessLog();
			processLog.setTENANT_ID(act.getTenantId());
			processLog.setACTIVITY_SEQ_ID(ActRecId);
			processLog.setCHANNEL_ID(channelid);
			// get order number contains (success filter records)
			// String orderSum = ordermapper.getOrderNumByChannelId(processLog);
			String orderSum = OrderFileMannager.getOrderNumber().toString();
			int iOrderSum = Integer.parseInt(orderSum);
			logger.info("[OrderCenter] Update order status 5 :" + channelid + " seqid:" + ActRecId + " OrderSum:"
					+ iOrderSum + " ,update times:" + (iOrderSum / 100000 + 1));
			// update 10w record every time +1 for last records
			for (int i = 0; i < iOrderSum / 100000 + 1; ++i) {
				Order orderinfo = new Order();
				orderinfo.setACTIVITY_SEQ_ID(ActRecId);
				orderinfo.setTENANT_ID(act.getTenantId());
				orderinfo.setCHANNEL_ID(channelid);
				// update number use "RETRY_TIMES" fields
				orderinfo.setRETRY_TIMES("100000");
				ordermapper.updateOrderFinishedStatus(orderinfo);
			}

			logger.info("[OrderCenter] Update order status end:" + channelid + " seqid:" + ActRecId);
		}
		// when all channel order order_status=5, set activity_status =1
		ac_obj.setREC_ID(ActRecId);
		ordermapper.updateActivityStatus(ac_obj);

		// if (this.isGenOtherChannelOrder)
		// ordermapper.updateOrderFinishedStatus(ac_obj);

		// if (this.isGenSmsOrder)
		// ordermapper.updateOrderSmsFinishedStatus(ac_obj);

	}

	/**
	 * judge is activity need running by activityCycle
	 * 
	 * @param activity
	 *            id
	 * @param tenant_id
	 * @param flag:activity
	 *            cycle
	 * @param activityEndDate:activity
	 *            end date
	 * @return -1:no run,0:run
	 */
	private int isActivityNeedRun(String actid, String tenant_id, int flag, String activityEndDate) {
		// 1、月，2、日，3、一次性",
		PltActivityInfo act = new PltActivityInfo();
		act.setACTIVITY_ID(actid);
		act.setTENANT_ID(tenant_id);
		String create_time = ordermapper.isActivityRun(act);
		// new activity
		if (create_time == null) {
			return 0;
		}
		// when activity expire ,the activity can not run
		if (activityEndDate != null) {
			String now = getCurrentTime("yyyy-MM-dd");
			if (activityEndDate.compareTo(now) < 0) {
				return -1;
			}
		}

		int rtn = -1;
		switch (flag) {
		case 1:// one month one time
			String actmonth = create_time.substring(0, 6);
			String nowmonth = getCurrentTime("yyyyMM");
			if (!actmonth.equals(nowmonth) && isExistsActivityList(actid, tenant_id))
				rtn = 0;
			break;
		case 2:// day
			String actday = create_time.substring(0, 8);
			String nowday = getCurrentTime("yyyyMMdd");
			if (!actday.equals(nowday) && isExistsActivityList(actid, tenant_id))
				rtn = 0;
			break;
		case 3:// only one time
			rtn = -1;
			break;
		default:
			return -1;
		}

		System.out.println("[OrderCenter] Activity id=" + actid + ",teantid=" + tenant_id + "activityType=" + flag
				+ ",return=" + rtn);
		return rtn;
	}

	/**
	 * count order begin_date ,end_date refer to orderIsConsultEndDate
	 * 
	 * @param activityCycle
	 *            数据更新周期 1、月，2、日，3、一次性",
	 * @param orderCycle
	 * @param refer
	 *            1:refer to activity enddate,else no refer to
	 * @param endDate
	 *            ,activity end date.
	 * @return xcloud date sql
	 */
	private String genOrderLifeReferActivityDate(String activityCycle, String orderCycle, String refer,
			String endDate) {
		int flag = Integer.parseInt(activityCycle);
		// for count date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();

		// record order begin date
		this.orderBeginDate = getCurrentTime("yyyy-MM-dd HH:mm:ss");

		Date activityEndDate = null;
		String endDateAfter = null;
		if (endDate != null) {
			// add hh mm ss
			endDateAfter = endDate;
			try {
				// yyyy-mm-dd
				if (endDateAfter.trim().length() == 10) {
					endDateAfter += " 23:59:59";
				} else {
					logger.error("[OrderCenter] actiivty endDate format error.!");
					return null;
				}
				activityEndDate = dateFormat.parse(endDateAfter);
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error("[OrderCenter] dateFormat activity enddate error.");
				return null;
			}
		}

		String resultEndDate = null;
		String sql = null;
		switch (flag) {
		case 1:// one month one time
			calendar.add(Calendar.MONTH, Integer.parseInt(orderCycle) - 1);
			Date date = calendar.getTime();
			// yyyy-MM-dd
			String tmpEndDate = this.getLastDayOfMonth(date);
			tmpEndDate += " 23:59:59";
			if (endDate == null) {
				sql = ",'";
				sql += this.orderBeginDate;
				sql += "'  as BEGIN_DATE, '";
				sql += tmpEndDate;
				sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				// set end date
				resultEndDate = tmpEndDate;
			} else {
				// refer to activity enddate
				if (refer != null && refer.equals("1")) {
					sql = ",'";
					sql += this.orderBeginDate;
					sql += "'  as BEGIN_DATE, '";
					sql += endDateAfter;
					sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
					// set end date
					resultEndDate = endDateAfter;
				} else {
					// set smaller date
					if (date.before(activityEndDate)) {
						// set date
						resultEndDate = tmpEndDate;
					} else {
						// set activityEndDate
						resultEndDate = endDateAfter;
					}
					sql = " ,'";
					sql += this.orderBeginDate;
					sql += "' as BEGIN_DATE ,'";
					sql += resultEndDate;
					sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				}
			}
			break;
		case 2:// day
			calendar.add(Calendar.DATE, Integer.parseInt(orderCycle) - 1);
			Date date2 = calendar.getTime();
			SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
			String tmpEndDate2 = dateFormat3.format(date2);
			tmpEndDate2 += " 23:59:59";

			if (endDate == null) {
				sql = ",'";
				sql += this.orderBeginDate;
				sql += "'  as BEGIN_DATE, '";
				sql += tmpEndDate2;
				sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				// set end date
				resultEndDate = tmpEndDate2;
			} else {
				// refer to activity enddate
				if (refer != null && refer.equals("1")) {
					sql = ",'";
					sql += this.orderBeginDate;
					sql += "'  as BEGIN_DATE, '";
					sql += endDateAfter;
					sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
					// set end date
					resultEndDate = endDateAfter;
				} else {
					// set smaller date
					if (date2.before(activityEndDate)) {
						// set date
						resultEndDate = tmpEndDate2;
					} else {
						// set activityEndDate
						resultEndDate = endDateAfter;
					}
					sql = " ,'";
					sql += this.orderBeginDate;
					sql += "' as BEGIN_DATE ,'";
					sql += resultEndDate;
					sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				}
			}
			break;
		case 3:// only one time
			if (endDate != null) {
				sql = ",'";
				sql += this.orderBeginDate;
				sql += "'  as BEGIN_DATE, ";
				sql += "'";
				sql += endDateAfter;
				sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				// set activityEndDate
				resultEndDate = endDateAfter;
			} else {
				sql = ",'";
				sql += this.orderBeginDate;
				sql += "'  as BEGIN_DATE, ";
				sql += "'2099:12:31 23:59:59' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				// set activityEndDate
				resultEndDate = "2099-12-31 23:59:59";
			}

			break;
		default:
			break;
		}

		// record order end date
		this.orderEndDate = resultEndDate;
		return sql;
	}

	private String genXCloudSql(String tabname, String activityid, int seq, String activityCycle, String orderCycle,
			String channel_id, String sqlcon, String recommenedInfo, String smsTemplate, String batchid,
			String org_range, String startDate, String endDate, String remotePath, String orderIsConsultEndDate) {
		// get max date id
		// String sql = "(SELECT max(date_id) FROM DIM_KFPT_BAND_DATE)";
		String maxDateId = TaskBaseMapperDao.getMaxDateId();
		// gen filename
		genFileName();
		// String runSQL = "/*!mycat:sql=select * FROM DIM_BOOLEAN */ export
		// select ";
		String runSQL = "export select ";
		runSQL += Integer.toString(seq);
		runSQL += " as ACTIVITY_SEQ_ID,";
		runSQL += "\'";
		runSQL += batchid;
		runSQL += "\' as BATCH_ID,";
		runSQL += "\'";
		runSQL += channel_id;
		runSQL += "\' as CHANNEL_ID,";

		if (recommenedInfo != null && recommenedInfo.indexOf("case") != -1)
			runSQL += recommenedInfo;
		else {
			runSQL += "\'";
			runSQL += recommenedInfo;
			runSQL += "\'";
		}
		runSQL += " as MARKETING_WORDS,";

		runSQL += selectField;

		runSQL += genOrderLifeReferActivityDate(activityCycle, orderCycle, orderIsConsultEndDate, endDate);
		runSQL += ",SYSDATE as LAST_UPDATE_TIME, ";

		// user label sql
		runSQL += this.userLabelSql;

		// for reserve5 column
		runSQL += ",";
		// sms template info
		if (smsTemplate != null && smsTemplate.indexOf("case") != -1)
			runSQL += smsTemplate;
		else {
			runSQL += "\'";
			runSQL += smsTemplate;
			runSQL += "\'";
		}
		runSQL += " as RESERVE5 ";

		runSQL += " FROM  UNICOM_D_MB_DS_ALL_LABEL_INFO a ";
		runSQL += " , ";
		runSQL += tabname;
		runSQL += " b ";
		runSQL += " ,wxwl_client_assign c ";
		runSQL += "where b.clientcode = a.USER_ID ";
		runSQL += " and b.clientcode = c.clientcode ";
		runSQL += " and a.DATE_ID ='";
		runSQL += maxDateId;
		runSQL += "'";
		// runSQL += sql;
		runSQL += " and ";
		runSQL += getOrgRangeSQL(org_range);
		if (sqlcon != null) {
			runSQL += " and ";
			runSQL += sqlcon;
		}
		runSQL += " ATTRIBUTE(LOCATION('";
		runSQL += remotePath;
		runSQL += getFileName();
		runSQL += ".csv')";
		runSQL += " SEPARATOR('|'))";

		return runSQL;
	}

	/**
	 * generate orders by activity 
	 * @param url
	 * @param tenant id
	 * @return
	 */
	@Override
	public void doActivity(String activity_url, String tenantId) {
		//reset variable
		initOrderCondition();
		
		// call api to get activity list
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tenantId", tenantId);
		String rsp = HttpUtil.doGet(activity_url, map);
		

		// log
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		HashMap<Integer,String> params = new HashMap<Integer,String>();
		String tmp = rsp;
		if (!rsp.equals("ERROR")) {
			if (tmp.length() > 6000)
				tmp = tmp.substring(0, 6000);
			logger.info("[OrderCenter] Activity list Info:" + rsp);
		} else {
			logger.error("[OrderCenter] Activity service[" + activity_url + "]" + rsp);
			return;
		}
		//log
		params.put(1, tmp);
		params.put(2, activity_url);
		LogToDb.writeLog(SerialId, "doActivity", "Get Activity info.", params);

		List<String> act_list = JSON.parseArray(rsp, String.class);
		for (int i = 0; i < act_list.size(); i++) {
			logger.info("[OrderCenter] Activity id:" + act_list.get(i) + " begin running...");
			// log
			params.put(2, "");
			params.put(1, act_list.get(i));
			LogToDb.writeLog(SerialId, "doActivity", "Activity Begin", params);
			// call get activity detail api
			String act = act_list.get(i);
			String subUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_INFO_DETAIL");
			if (subUrl.equals("") || subUrl == null) {
				logger.error("[OrderCenter] Get suburl error..................!!!");
				continue;
			}
			HashMap<String, String> submap = new HashMap<String, String>();
			submap.put("activityId", act);
			submap.put("tenantId", tenantId);
			String respond = HttpRequest(subUrl, submap);
			if (respond.equals("ERROR")) {
				logger.error("[OrderCenter] HttpRequest for call activity detail api error.");
				continue;
			}
			logger.info("[OrderCenter] Activity detail info:" + respond);
			// log
			String tmp2;
			if (respond.length() > 8000) {
				tmp2 = respond.substring(0, 8000);
			} else {
				tmp2 = respond;
			}
			params.put(2, "");
			params.put(10, tmp2);
			LogToDb.writeLog(SerialId, "doActivity", "Activity Begin", params);
			// 2.parse res to object
			ActivityProvPo actjson = JSON.parseObject(respond, ActivityProvPo.class);

			// judge run activity is running?
			// "活动类型：1、周期性（按月），2、周期性（按日），3、一次性",
			String flag = actjson.getActivityType();
			String act_id = actjson.getActivityId();
			String tenant_id = actjson.getTenantId();
			String activityEndDate = actjson.getEndDate();

			if (flag == null || act_id == null || tenant_id == null) {
				logger.error("[OrderCenter] activityType,ActivityId,tenantId param error!");
				continue;
			}

			// 8、停⽤用,9、启⽤用,10、未执⾏行,11、暂停,12、暂存,13、审批中",
			// only 9 can run
			String activityState = actjson.getState();
			if (!activityState.equals("9")) {
				logger.info("[OrderCenter] Activity id=" + act_id + ":activity status!=9 no need run.");
				params.put(10, "activity status!=9 ,no need run.");
				LogToDb.writeLog(SerialId, "doActivity", "Activity Begin", params);
				continue;
			}
			if (isActivityNeedRun(act_id, tenant_id, Integer.parseInt(flag), activityEndDate) == -1) {
				logger.info("[OrderCenter] Activity id=" + act_id + " no need run.");
				params.put(10, "no need run.");
				LogToDb.writeLog(SerialId, "doActivity", "Activity Begin", params);
				continue;
			}

			// init list
			this.finishedOrderChannelList = new ArrayList<String>();
			
			// set activitySeqId
			this.activitySeqId = AsynDataIns.getActivitySeqId();
			if (this.activitySeqId == -1) {
				logger.error("[OrderCenter] Activity id=" + act_id + ",get activity sequence id error.");
				continue;
			} 
			// record activity info
			recordActivityInfo(actjson);
			//log
			params.put(4, act_id);
			params.put(5, this.activitySeqId.toString());
			LogToDb.writeLog(SerialId, "doActivity", "RECORD_ACT_INFO", params);
			// generate all channels order
			String ogr_range = actjson.getOrgRange();
			genAllChannelOrderInfo(actjson, ogr_range);

			// update activity status
			if (this.finishedOrderChannelList.size() > 0) {
				// log
				params.put(1, act_list.get(i));
				LogToDb.writeLog(SerialId, "doActivity", "FILTER_ORDER_BEGIN", params);
				// order filter call api
				filterService.filterOrderStatus(act_id,this.getActivitySeqId(), tenantId, this.finishedOrderChannelList);

				// call api for filter success standard
				String filterSuccessFlag = AsynDataIns.getValueFromGlobal("FILTER_SUCCESS");
				if (filterSuccessFlag != null && filterSuccessFlag.equals("1"))
					baseTask.orderFilterSucess(this.getActivitySeqId(), actjson.getSuccessStandardPo(), tenant_id);

				// call api for reserve order
				filterService.reserveOrder(act_id, this.getActivitySeqId(), tenantId, this.finishedOrderChannelList);
				params.put(1, act_list.get(i));
				LogToDb.writeLog(SerialId, "doActivity", "FILTER_ORDER_END", params);
				
				params.put(1, act_list.get(i));
				LogToDb.writeLog(SerialId, "doActivity", "UPDATE_ACT_STATUS,COMMIT_SUCCESS_INFO", params);
				updateActivityStatus(actjson);
				// sync success info
				commitSuccessInfo(actjson);

				// log
				params.put(1, act_list.get(i));
				LogToDb.writeLog(SerialId, "doActivity", "FINISHED,ok", params);
				HashMap<String, Object> activityMap = new HashMap<String, Object>();
				activityMap.put("tenantId", tenant_id);
				activityMap.put("activitySeqId", getActivitySeqId());
				params.put(1, act_list.get(i));
				params.put(10, "");
				LogToDb.writeLog(SerialId, "doActivity", "COUNT_BEGIN", params);
				// call api
				try {
//					statisticService.incrStatistic(activityMap);
					params.put(1, act_list.get(i));
					params.put(2, "Call statistic ok.");
					LogToDb.writeLog(SerialId, "doActivity", "COUNT_END", params);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("[OrderCenter]" + e.getMessage());
					params.put(1, "count exception");
					params.put(2, "Call statistic ok.");
					LogToDb.writeLog(SerialId, "doActivity", "COUNT_END", params);
				}
				logger.info("[OrderCenter] Activity id:" + act_list.get(i) + " finished, status ok.");
			} else {
				PltActivityInfo ac_obj = new PltActivityInfo();
				ac_obj.setREC_ID(getActivitySeqId());
				ac_obj.setTENANT_ID(actjson.getTenantId());
				ordermapper.cleanActivityInfo(ac_obj);
				System.out.println("[OrderCenter] Activity id:" + act_list.get(i) + " finished,status:error!");
				// log
				params.put(1, act_list.get(i));
				params.put(10, "clean activity info");
				LogToDb.writeLog(SerialId, "doActivity", "FINISHED,error.", params);
				logger.info("[OrderCenter] Activity id:" + act_list.get(i) + " finished, status error.");
			}

			// only deal one activity one time,for activity Priority
			break;

		} // end for activity

	}

	/**
	 * insert activity table in mysql
	 * 
	 * @param activity
	 * @return 
	 */
	@Override
	public void commitActivityInfo(PltActivityInfo activity) {
		ordermapper.InsertActivityInfo(activity);
	}

	/**
	 * get user group infomation from oracle by user group id.
	 * 
	 * @param user
	 *            group id
	 * @return 0:success;-1 error
	 */
	@Override
	public String getUserGroupInfo(String id) {
		/*
		 * Statement stmt = null; Connection conn = null; ResultSet rs = null;
		 * try { String oracleUser =
		 * AsynDataIns.getValueFromGlobal("USERGROUP_USER"); String oraclePasswd
		 * = AsynDataIns.getValueFromGlobal("USERGROUP_PASSWD"); String jdbc =
		 * AsynDataIns.getValueFromGlobal("USERGROUP_JDBC");
		 * 
		 * Class.forName("oracle.jdbc.driver.OracleDriver"); //conn =
		 * DriverManager.getConnection(
		 * "jdbc:oracle:thin:@//132.35.224.165:1521/dwtest", "usertool_hn", //
		 * "USERTOOL_HN_123"); conn = DriverManager.getConnection(jdbc,
		 * oracleUser,oraclePasswd);
		 * 
		 * stmt = conn.createStatement(); // String sql =
		 * "insert into ddd values('18522829266')"; String sql =
		 * "SELECT to_char(a.SQL) as SQL FROM user_tool_weights_conditions a WHERE ( a.CI_WA_ID = "
		 * ; sql += "'"; sql += id; sql += "'"; sql +=
		 * " and a.CI_WA_MAX_MONTH_ID = ( select max(b.CI_WA_MAX_MONTH_ID) from user_tool_weights_conditions b where a.CI_WA_ID = b.CI_WA_ID) and rownum<2)"
		 * ; stmt = conn.createStatement(); rs = stmt.executeQuery(sql); while
		 * (rs.next()) { return rs.getString("SQL"); }
		 * 
		 * } catch (ClassNotFoundException e) { e.printStackTrace(); } catch
		 * (SQLException e) { e.printStackTrace(); } return null;
		 */
		return ordermapper.getUserGroupInfo(id);
	}


	/**
	 * record processlog,one channel one activity
	 * 
	 * @param act
	 *            json string
	 * @param channelId
	 * @param type
	 *            0:insert,1:update
	 * @param status
	 *            0:success;1 error
	 */
	private void syncProcessLog(ActivityProvPo act, String channelId, int type, int status) {
		// log activity process log
		ActivityProcessLog processLog = new ActivityProcessLog();
		processLog.setACTIVITY_ID(act.getActivityId());
		processLog.setTENANT_ID(act.getTenantId());
		processLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
		processLog.setCHANNEL_ID(channelId);
		processLog.setSTATUS(status);
		if (type == 0) {
			processLog.setORDER_BEGIN_DATE(getDateTime(getCurrentTime("yyyy-MM-dd HH:mm:ss")));
			ordermapper.InsertActivityProcessLog(processLog);
		}
		if (type == 1) {
			processLog.setORDER_END_DATE(getDateTime(getCurrentTime("yyyy-MM-dd HH:mm:ss")));
			// processLog.setCHANNEL_ORDER_NUM(ordermapper.getOrderNumByChannelId(processLog));
			processLog.setORI_AMOUNT(OrderFileMannager.getOrderNumber());
			ordermapper.UpdateActivityProcessLog(processLog);
		}
	}

	/**
	 * generate all channels orders
	 * 
	 * @param act
	 * @param org_range
	 */
	private void genAllChannelOrderInfo(ActivityProvPo act, String org_range) {
		// log
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		HashMap<Integer,String> params = new HashMap<Integer,String>();
		params.put(1, act.getActivityId());
		LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_START", params);
		

		// for channel list ,generate every channel's order info
		// 渠道-本地弹窗 8
		if (act.getChannelGroupPopupPoList() != null) {
			for (ChannelGroupPopupPo po : act.getChannelGroupPopupPoList()) {
				if (po.getChannelId() != null) {
					String orderIssuedRule = po.getOrderIssuedRule();
					String tmpTable = createTempOrderInfo(act, orderIssuedRule, po.getChannelId());
					if (tmpTable != null && orderIssuedRule != null) {
						String subChannelId = po.getChannelId();
						subChannelId += po.getBusinessHall();
						// record process log
						syncProcessLog(act, subChannelId, 0, 0);

						// modified by shenyj at 2017-03-08 14:29 for adding
						// special filter
						String recomendInfo, smsTemplate;
						recomendInfo = smsTemplate = "";

						String specialFilterListString = null;
						if (po.getChannelSpecialList() != null) {
							recomendInfo = getSpecialFilterMap(po.getChannelSpecialList(), 0);
							smsTemplate = getSpecialFilterMap(po.getChannelSpecialList(), 1);
							specialFilterListString = JSON.toJSONString(po.getChannelSpecialList());
						}
						if (genOrderinfoByChannelId(act, subChannelId, po.getFilterConditionSql(), tmpTable,
								recomendInfo, smsTemplate, org_range)) {
							// this.isGenOtherChannelOrder = true;
							this.finishedOrderChannelList.add(subChannelId);
							// insert channel detail
							PltActivityChannelDetail detail = new PltActivityChannelDetail();
							detail.setBUSINESS_HALL_ID(po.getBusinessHall());
							detail.setBUSINESS_HALL_NAME(po.getBusinessHallName());
							detail.setCHANN_ID(subChannelId);
							detail.setTENANT_ID(act.getTenantId());
							detail.setACTIVITY_ID(act.getActivityId());
							detail.setCONTENT(po.getContent());
							detail.setFILTER_CON(po.getFilterCondition());
							detail.setACTIVITY_SEQ_ID(getActivitySeqId());
							detail.setFILTER_SQL(po.getFilterConditionSql());
							detail.setNUMBERLIMIT(po.getNumberLimit());
							detail.setTARGET(po.getTarget());

							// added by shenyj for special filter
							if (specialFilterListString != null)
								detail.setCHANNEL_SPECIALFILTER_LIST(specialFilterListString);
							/**
							 * 弹窗添加SmsSendWords短信话术字段对应到detail表中的sms_words
							 * 
							 * @author 王明新
							 */
							detail.setSMS_WORDS(po.getSmsSendWords());

							ordermapper.insertChannelDetailInfo(detail);


							// log
							params.put(3, subChannelId);
							LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
							
							// record process log
							syncProcessLog(act, subChannelId, 1, 0);
						}
					} else {
						logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + po.getChannelId()
								+ " temp table null or orderIssuedRule null.");

						// log
						params.put(3, po.getChannelId());
						LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
						// record process log
						syncProcessLog(act, po.getChannelId(), 1, 1);
					}
				}
			}
		}
		// channelHandOfficePo 1-手厅
		if (act.getChannelHandOfficePo() != null) {
			ChannelHandOfficePo hand = act.getChannelHandOfficePo();
			if (hand.getChannelId() != null) {
				String orderIssuedRule = hand.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, hand.getChannelId());

				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, hand.getChannelId(), 0, 0);

					if (genOrderinfoByChannelId(act, hand.getChannelId(), hand.getFilterConditionSql(), tmpTable, "",
							"", org_range)) {
						// this.isGenOtherChannelOrder = true;
						this.finishedOrderChannelList.add(hand.getChannelId());
						// sync detail info
						PltActivityChannelDetail detail = new PltActivityChannelDetail();
						detail.setCONTENT(hand.getChannelHandofficeContent());
						detail.setTITLE(hand.getChannelHandofficeTitle());
						detail.setURL(hand.getChannelHandofficeUrl());
						detail.setCHANN_ID(hand.getChannelId());
						detail.setTENANT_ID(act.getTenantId());
						detail.setACTIVITY_ID(act.getActivityId());
						detail.setFILTER_CON(hand.getFilterCondition());
						detail.setACTIVITY_SEQ_ID(getActivitySeqId());
						detail.setFILTER_SQL(hand.getFilterConditionSql());
						ordermapper.insertChannelDetailInfo(detail);

						// log
						params.put(3,hand.getChannelId());
						LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
						// update order number
						// record process log
						syncProcessLog(act, hand.getChannelId(), 1, 0);
					}
				} else {
					// log
					params.put(3,hand.getChannelId());
					LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
					// record process log
					syncProcessLog(act, hand.getChannelId(), 1, 1);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + hand.getChannelId()
							+ " temp table null or orderIssuedRule null.");
				}
			}
		}
		// channelWebOfficePo 2-网厅
		if (act.getChannelWebOfficePo() != null) {
			ChannelWebOfficePo web = act.getChannelWebOfficePo();
			if (web.getChannelId() != null) {
				String orderIssuedRule = web.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, web.getChannelId());
				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, web.getChannelId(), 0, 0);
					if (genOrderinfoByChannelId(act, web.getChannelId(), web.getFilterConditionSql(), tmpTable, "", "",
							org_range)) {
						// this.isGenOtherChannelOrder = true;
						this.finishedOrderChannelList.add(web.getChannelId());
						// sync detail info
						PltActivityChannelDetail detail = new PltActivityChannelDetail();
						detail.setCONTENT(web.getChannelWebofficeContent());
						detail.setTITLE(web.getChannelWebofficeTitle());
						detail.setURL(web.getChannelWebofficeUrl());
						detail.setCHANN_ID(web.getChannelId());
						detail.setTENANT_ID(act.getTenantId());
						detail.setACTIVITY_ID(act.getActivityId());
						detail.setFILTER_CON(web.getFilterCondition());
						detail.setACTIVITY_SEQ_ID(getActivitySeqId());
						detail.setFILTER_SQL(web.getFilterConditionSql());
						ordermapper.insertChannelDetailInfo(detail);

						// log
						params.put(3,web.getChannelId());
						LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
						// record process log
						syncProcessLog(act, web.getChannelId(), 1, 0);
					}
				} else {
					// log
					params.put(3,web.getChannelId());
					LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + web.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, web.getChannelId(), 1, 1);
				}

			}
		}
		// channelWebchatInfo 11-微信
		if (act.getChannelWebchatInfo() != null) {
			ChannelWebchatInfo wechat = act.getChannelWebchatInfo();
			if (wechat.getChannelId() != null) {
				String orderIssuedRule = wechat.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, wechat.getChannelId());

				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, wechat.getChannelId(), 0, 0);

					if (genOrderinfoByChannelId(act, wechat.getChannelId(), wechat.getFilterConditionSql(), tmpTable,
							wechat.getChannelWebchatContent(), "", org_range)) {
						// this.isGenOtherChannelOrder = true;
						this.finishedOrderChannelList.add(wechat.getChannelId());
						// sync wechat channel info
						wechat.setActivityId(act.getActivityId());
						wechat.setTenantId(act.getTenantId());
						wechat.setACTIVITY_SEQ_ID(getActivitySeqId());
						ordermapper.InsertChannelWebchatInfo(wechat);

						// log
						params.put(3,wechat.getChannelId());
						LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
						// record process log
						syncProcessLog(act, wechat.getChannelId(), 1, 0);
					}
				} else {
					// log
					params.put(3,wechat.getChannelId());
					LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + wechat.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, wechat.getChannelId(), 1, 1);
				}
			}
		}
		// channelWoWindowPo 9-活视窗
		if (act.getChannelWoWindowPo() != null) {
			ChannelWoWindowPo wo = act.getChannelWoWindowPo();
			if (wo.getChannelId() != null) {
				String orderIssuedRule = wo.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, wo.getChannelId());

				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, wo.getChannelId(), 0, 0);
					if (genOrderinfoByChannelId(act, wo.getChannelId(), wo.getFilterConditionSql(), tmpTable, "", "",
							org_range)) {
						// this.isGenOtherChannelOrder = true;
						this.finishedOrderChannelList.add(wo.getChannelId());
						// sync wowind channel info
						wo.setTenantId(act.getTenantId());
						wo.setActivityId(act.getActivityId());
						wo.setACTIVITY_SEQ_ID(getActivitySeqId());
						if (wo.getChannelId() != null)
							ordermapper.InsertChannelWebWoWindow(wo);

						// log
						params.put(3,wo.getChannelId());
						LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
						// record process log
						syncProcessLog(act, wo.getChannelId(), 1, 0);
					}
				} else {
					// log
					params.put(3,wo.getChannelId());
					LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + wo.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, wo.getChannelId(), 1, 1);
				}
			}
		}

		// frontlineChannelPo 5
		if (act.getFrontlineChannelPo() != null) {
			FrontlineChannelPo front = act.getFrontlineChannelPo();

			if (front.getChannelId() != null && front.getOrderIssuedRule() != null) {
				String orderIssuedRule = front.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, front.getChannelId());
				if (tmpTable != null) {
					// record process log
					syncProcessLog(act, front.getChannelId(), 0, 0);
					// specialFilter List
					if (front.getChannelSpecialFilterList() != null) {
						String recommendInfo, smsTemplate;
						recommendInfo = smsTemplate = "";
						recommendInfo = getSpecialFilterMap(front.getChannelSpecialFilterList(), 0);
						smsTemplate = getSpecialFilterMap(front.getChannelSpecialFilterList(), 1);
						if (genOrderinfoByChannelId(act, front.getChannelId(), front.getFilterConditionSql(), tmpTable,
								recommendInfo, smsTemplate, org_range)) {
							// this.isGenOtherChannelOrder = true;
							this.finishedOrderChannelList.add(front.getChannelId());
							// sync front line channel detail info

							front.setTenantId(act.getTenantId());
							front.setActivityId(act.getActivityId());
							front.setACTIVITY_SEQ_ID(getActivitySeqId());
							ordermapper.InsertChannelFrontline(front);

							// log
							params.put(3,front.getChannelId());
							LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 0);
						} else {
							logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + front.getChannelId()
									+ " temp table null or orderIssuedRule null.");
							// log
							params.put(2,recommendInfo);
							params.put(3,front.getChannelId());
							LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 1);
						} // end if else gen order ok

					} else { // no specialFilter List
						if (genOrderinfoByChannelId(act, front.getChannelId(), front.getFilterConditionSql(), tmpTable,
								"", "", org_range)) {
							// this.isGenOtherChannelOrder = true;
							this.finishedOrderChannelList.add(front.getChannelId());
							// sync front line channel info
							front.setTenantId(act.getTenantId());
							front.setActivityId(act.getActivityId());
							front.setACTIVITY_SEQ_ID(getActivitySeqId());
							ordermapper.InsertChannelFrontline(front);
							// log
							params.put(3,front.getChannelId());
							LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 0);

						} else {
							logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + front.getChannelId()
									+ " temp table null or orderIssuedRule null.");
							// log
							params.put(3,front.getChannelId());
							LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 1);
						} // end if else gen order ok
					} // end no specialfilter list
				} // end temptable

			} // end channel id not null,orderissuerule not null

		} // end front line

		// msmChannelPo 7-短信
		if (act.getMsmChannelPo() != null) {
			MsmChannelPo msm = act.getMsmChannelPo();
			if (msm.getChannelId() != null) {
				String orderIssuedRule = msm.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, msm.getChannelId());
				if (tmpTable == null) {
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + msm.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					return;
				}
				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, msm.getChannelId(), 0, 0);
					if (genOrderinfoByChannelId(act, msm.getChannelId(), msm.getFilterConditionSql(), tmpTable, "", "",
							org_range)) {
						// this.isGenSmsOrder = true;
						this.finishedOrderChannelList.add(msm.getChannelId());
						// sync sms channel detail info
						PltActivityChannelDetail detail = new PltActivityChannelDetail();
						detail.setCHANN_ID(msm.getChannelId());
						if (msm.getCycleTimes() != null)
							detail.setTIMES(Integer.parseInt(msm.getCycleTimes()));
						detail.setFILTER_CON(msm.getFilterCondition());
						detail.setACTIVITY_SEQ_ID(getActivitySeqId());
						detail.setFILTER_SQL(msm.getFilterConditionSql());
						if (msm.getIntervalHours() != null)
							detail.setINTERVAL_HOUR(Integer.parseInt(msm.getIntervalHours()));
						detail.setNOSEND_TIME(msm.getNoSendTime());
						detail.setEND_TIME(msm.getSendEndTime());
						detail.setSTART_TIME(msm.getSendStartTime());
						detail.setSEND_LEVEL(msm.getSendLevel());
						detail.setTENANT_ID(act.getTenantId());
						detail.setACTIVITY_ID(act.getActivityId());
						detail.setTOUCHLIMITDAY(msm.getTouchLimitDay());
						detail.setCONTENT(msm.getSmsContent());
						detail.setRESERVE1(msm.getMessageSendTime());
						/**
						 * 短信中的添加3个字段，其中channelpo产品编码对应detail表中的PRODUCT_LIST
						 * 
						 * @author 王明新
						 */
						detail.setPRODUCT_LIST(msm.getMsmProductCode());
						ordermapper.insertChannelDetailInfo(detail);

						// log
						params.put(3,msm.getChannelId());
						LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_END", params);
						// record process log
						syncProcessLog(act, msm.getChannelId(), 1, 0);
					}
				} else {
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + msm.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// log
					params.put(3,msm.getChannelId());
					LogToDb.writeLog(SerialId, "doActivity", "ORDER_GEN_ERROR", params);
					// record process log
					syncProcessLog(act, msm.getChannelId(), 1, 1);
				}
			}
		}

	}

	private String getOrgRangeSQL(String org_range) {
		/// root/770/,/root/HAB/,/root/763/,/root/766/,
		String orgpath = org_range;
		String[] path_list = orgpath.split(",");
		String tmp = "( ";
		String tmp2 = "c.ORGPATH like '%";
		String sql = "";
		for (String path : path_list) {
			if (path != null && path.length() != 0) {
				sql += tmp2;
				sql += path;
				sql += "%' ";

				sql += " or ";
			}
		}

		int pos = sql.lastIndexOf("or");
		sql = sql.substring(0, pos);
		sql += ")";
		return tmp + sql;
	}

	/*
	 * generate order sql ,file and load file to mysql
	 * 
	 * @param: ActivityProvPo, activity detail json string
	 * 
	 * @param: channel_id
	 * 
	 * @param:sql ,channel special filter sql
	 * 
	 * @param:tmpTable, resource assign result table
	 * 
	 * @param:recommenedInfo channel special recommend info
	 * 
	 * @param: sms template info
	 * 
	 * @param:org_range
	 * 
	 * @return :boolean
	 */
	private boolean genOrderinfoByChannelId(ActivityProvPo act, String channel_id, String sql, String tmpTable,
			String recommenedInfo, String smsTemplate, String org_range) {

		logger.info("[OrderCenter] genOrderinfoByChannelId:" + channel_id + "|" + sql + "|" + tmpTable + "|"
				+ recommenedInfo + "|" + org_range);
		// log
		PltCommonLog logdb = new PltCommonLog();
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("55");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("ORDER_GEN");
		logdb.setBUSI_CODE("PREPARE_XCLOUD_SQL_BEGIN");
		logdb.setBUSI_ITEM_4(act.getActivityId());
		logdb.setBUSI_ITEM_5(this.activitySeqId.toString());
		AsynDataIns.insertPltCommonLog(logdb);

		boolean bRtn = false;

		PltActivityInfo acttmp = new PltActivityInfo();
		acttmp.setACTIVITY_ID(act.getActivityId());
		acttmp.setTENANT_ID(act.getTenantId());

		// get activity rec_id
		Integer ACTIVITY_SEQ_ID = getActivitySeqId();
		int batchid = AsynDataIns.getSequence("PLT_ORDER_INFO.BATCH_ID");
		// judge orderCycle
		String orderCycle = act.getOrderCycle();
		String activityType = act.getActivityType();
		if (orderCycle == null || activityType == null) {
			logger.error("[OrderCenter] orderCycle or activityType null ,error.");
			return false;
		}

		// special filter condition (UNICOM_D_MB_DS_ALL_LABEL_INFO)
		String sqlCondition = sql;
		if (sqlCondition != null) {
			if (sqlCondition.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1)
				sqlCondition = sqlCondition.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
		}
		if (recommenedInfo != null && !recommenedInfo.equals("")) {
			if (recommenedInfo.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1)
				recommenedInfo = recommenedInfo.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
		}
		if (smsTemplate != null && !smsTemplate.equals("")) {
			if (smsTemplate.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1)
				smsTemplate = smsTemplate.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
		}
		// get ftp info from table
		String FtpSrvIp = AsynDataIns.getValueFromGlobal("HDFSSRV.IP");
		String FtpUser = AsynDataIns.getValueFromGlobal("HDFSSRV.USER");
		String FtpPassowd = AsynDataIns.getValueFromGlobal("HDFSSRV.PASSWORD");
		String FtpPort = AsynDataIns.getValueFromGlobal("HDFSSRV.PORT");
		String ftpRemote = AsynDataIns.getValueFromGlobal("ORDER_REMOTEPATH");
		String ftpLocal = AsynDataIns.getValueFromGlobal("ORDER_LOCALPATH");

		String xCloudSql = genXCloudSql(tmpTable, "", ACTIVITY_SEQ_ID, act.getActivityType(),
				// act.getOrderCycle(),
				orderCycle, // for filter null value
				channel_id, sqlCondition, recommenedInfo, smsTemplate, Integer.toString(batchid), org_range,
				act.getStartDate(), act.getEndDate(), ftpRemote, act.getOrderIsConsultEndDate());
		if (xCloudSql == null) {
			logger.error("[OrderCenter] genXCloudSql error!");
			logdb.setBUSI_ITEM_1("genXCloudSql error");
			AsynDataIns.insertPltCommonLog(logdb);
			return false;
		}
		if (xCloudSql.length() > 6000) {
			String tmp = xCloudSql.substring(0, 6000);
			logdb.setBUSI_ITEM_1(tmp);
		} else {
			logdb.setSTART_TIME(new Date());
			logdb.setBUSI_CODE("PREPARE_XCLOUD_SQL_END");
			logdb.setBUSI_ITEM_1(xCloudSql);
		}
		AsynDataIns.insertPltCommonLog(logdb);

		// log
		logger.info("[OrderCenter] Create order list,runsql:" + xCloudSql);
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RUN_XCLOUD_SQL_BEGIN");
		AsynDataIns.insertPltCommonLog(logdb);

		// judge user label table status when data changing
		if (this.judgeFlag != null && this.judgeFlag.trim().equals("1") && this.userLabelStatusSql != null) {
			String statusSql = this.userLabelStatusSql;
			String dateId = TaskBaseMapperDao.getMaxDateId();
			if (statusSql.indexOf("DATEID") != -1)
				statusSql = statusSql.replace("DATEID", dateId);
			// wait until UserLabel data finished,waitting 20 minute
			int iWait = 0;
			while (iWait++ < 20) {
				String status = this.ordermapper.getUserLabelDataStatus(statusSql);
				logger.info("[OrderCenter] judge user label table status:" + status + " ,sql:" + statusSql);
				if (status != null && status.trim().equals("1")) {
					logger.info("[OrderCenter] UserLabel table data changing,wait 1 minutes");
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else
					break;
			}
		}

		JsonResult JsonResultIns = AsynDataIns.execDdlOnXcloud(xCloudSql);
		if ("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false) {

			logdb.setBUSI_CODE("RUN_XCLOUD_SQL_ERROR");
			logdb.setBUSI_DESC("export from xcloud error.");
			if (JsonResultIns.getMessage().length() > 6000)
				logdb.setBUSI_ITEM_1(JsonResultIns.getMessage().substring(0, 6000));
			else
				logdb.setBUSI_ITEM_1(JsonResultIns.getMessage());

			AsynDataIns.insertPltCommonLog(logdb);
			logdb.setBUSI_ITEM_1("");
			logger.error("[OrderCenter] Generate order file error on xcloud.");

		} else {
			logdb.setSTART_TIME(new Date());
			logdb.setBUSI_CODE("RUN_XCLOUD_SQL_END");
			AsynDataIns.insertPltCommonLog(logdb);
			bRtn = true;

			// download order file
			if (downLoadFile(FtpSrvIp, FtpUser, FtpPassowd, FtpPort, ftpRemote, ftpLocal) == 0) {
				logdb.setSTART_TIME(new Date());
				logdb.setBUSI_CODE("DOWNLOAD");
				logdb.setBUSI_ITEM_1("Down load file" + ftpRemote + getFileName() + ".csv" + " ok.");
				AsynDataIns.insertPltCommonLog(logdb);

				// split order file and load data
				String origFileName = ftpLocal + getFileName() + ".csv";
				Integer filterBlackUserFlag = ordermapper.getFilterBlackUserFlag(ACTIVITY_SEQ_ID, act.getTargetId());

				List<String> fileList = OrderFileMannager.splitFile(origFileName, this.splitNum,
						(filterBlackUserFlag != null && filterBlackUserFlag == 1) ? true : false);
				for (String fileName : fileList) {
					// if (loadDataToMysql(channel_id,
					// act.getTenantId(),ftpLocal)) {
					if (loadDataToMysqlByFileName(channel_id, act.getTenantId(), fileName, act.getActivityId())) {
						bRtn = true;
						logdb.setSTART_TIME(new Date());
						logdb.setBUSI_CODE("LOAD_MYSQL");
						logdb.setBUSI_ITEM_1("load data mysql ok");
						logdb.setBUSI_ITEM_2(fileName);
						AsynDataIns.insertPltCommonLog(logdb);
					} else {
						bRtn = false;
						logdb.setSTART_TIME(new Date());
						logdb.setBUSI_CODE("LOAD_MYSQL");
						logdb.setBUSI_ITEM_1("load data mysql error.");
						logdb.setBUSI_ITEM_2(fileName);
						AsynDataIns.insertPltCommonLog(logdb);
					}
				} // end for
			} // download ok
			else {
				bRtn = false;
				// log
				logdb.setBUSI_ITEM_1("Down load file" + ftpRemote + getFileName() + ".csv" + " error.");
				logger.error("Down load file" + ftpRemote + getFileName() + ".csv" + " error.");
				AsynDataIns.insertPltCommonLog(logdb);
			}

		} // end export xcloud ok

		logger.info("[OrderCenter] genOrderinfoByChannelId finished:" + channel_id + "|" + sql + "|" + tmpTable + "|"
				+ recommenedInfo + "|" + org_range);
		return bRtn;

	}

	/*
	 * get case when sql
	 * 
	 * @param: List<ChannelSpecialFilterPo>
	 * 
	 * @param: int 0:get recommend sql;1:get msm template sql
	 * 
	 * @return: sql
	 */
	private String getSpecialFilterMap(List<ChannelSpecialFilterPo> specialFilterList, int type) {
		// init tree map
		Map<String, SpecialFilter> filterMap = new TreeMap<String, SpecialFilter>();
		for (int i = 0; i < specialFilterList.size(); i++) {
			ChannelSpecialFilterPo p = specialFilterList.get(i);
			SpecialFilter filter = new SpecialFilter();
			filter.setSql(p.getFilterConditionSql());
			if (type == 0)
				filter.setRecommend(p.getRecommenedInfo());
			if (type == 1)
				filter.setSmsTemplate(p.getMsmTemplate());
			filterMap.put(p.getOrd(), filter);
		}

		// generate case sql
		StringBuilder caseWhenSql = new StringBuilder();
		String tempCon = "";

		if (filterMap.size() == 1) {
			// no condition sql so return recommend info and sms template
			if (type == 0)
				caseWhenSql.append(filterMap.get("0").getRecommend());
			if (type == 1)
				caseWhenSql.append(filterMap.get("0").getSmsTemplate());
		} else {
			int i = 0;
			int j = 0;
			String and = " and ";
			caseWhenSql.append("(case");
			for (Map.Entry<String, SpecialFilter> entry : filterMap.entrySet()) {
				if (++i == 1)
					continue;
				// caseWhenSql.append
				caseWhenSql.append(" when " + tempCon);
				if (!tempCon.equals(""))
					caseWhenSql.append(and);
				caseWhenSql.append(entry.getValue().getSql());
				caseWhenSql.append(" then ");
				// join value
				caseWhenSql.append("'");
				if (type == 0)
					caseWhenSql.append(entry.getValue().getRecommend());
				if (type == 1)
					caseWhenSql.append(entry.getValue().getSmsTemplate());
				caseWhenSql.append("'");

				tempCon += getReverseCondition(entry.getValue().getSql(), ++j);

			}
			// join else sql
			caseWhenSql.append(" else ");
			caseWhenSql.append("'");
			if (type == 0)
				caseWhenSql.append(filterMap.get("0").getRecommend());
			if (type == 1)
				caseWhenSql.append(filterMap.get("0").getSmsTemplate());
			caseWhenSql.append("'");
			caseWhenSql.append(" end) ");

		}
		return caseWhenSql.toString();

	}

	private String getReverseCondition(String con, int num) {

		String temp = null;
		if (con.indexOf("=") != -1)
			temp = con.replace("=", " != ");
		if (con.indexOf("!=") != -1)
			temp = con.replace("!=", " = ");
		if (con.indexOf("<") != -1)
			temp = con.replace("<", " > ");
		if (con.indexOf(">") != -1)
			temp = con.replace(">", " < ");

		if (num > 1)
			return " and " + temp;
		else

			return temp;

	}

	/*
	 * get invalid order activity_seq_id from plt_order_info table
	 * 
	 * @param tenantId
	 * 
	 * @return List<Integer>
	 */
	public List<PltActivityInfo> getInvalidActivitySeqId(String tenantId) {
		return ordermapper.getInvalidActivitySeqId(tenantId);
	}
	/*
	 * move invalid order records to his table
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public void moveInvalidOrderRecords(PltActivityInfo act) {
		ordermapper.moveInvalidOrderRecords(act);
	}
	/*
	 * delete invalid order records from plt_order_info
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public void deleteInvalidOrderRecords(PltActivityInfo act) {
		ordermapper.deleteInvalidActivitySeqId(act);
	}
	/*
	 * update invalid order records,set
	 * invalid_date=sysdate,input_date=sysdate,order_status='6'
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public void updateInvalidOrderRecords(PltActivityInfo act) {
		ordermapper.updateInvalidOrderRecords(act);

	}
	/*
	 * update activity_info table set activity_status='2' when order recores
	 * invalid
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public void updateActvityInfoInvalid(PltActivityInfo act) {
		ordermapper.updateActvityInfoInvalid(act);
	}

	/*
	 * get the last finished activity rec_id by activity_id
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */
	public Integer getLastActivityRecId(PltActivityInfo act) {
		return ordermapper.getActivityRecid(act);
	}
	/*
	 * get last log record time
	 * 
	 * @param
	 * 
	 * @return max time
	 */

	public String getLastLogTime() {
		return ordermapper.getLastLogTime();
	}

	/*
	 * get last day time of the month from special day
	 * 
	 * @param special day Date
	 * 
	 * @return last day time
	 */
	public String getLastDayOfMonth(Date oneday) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar ca = Calendar.getInstance();
		ca.setTime(oneday);
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return format.format(ca.getTime());

	}

	/**
	 * judge activity is exists activity list just now
	 * 
	 * @param activity
	 *            id
	 * @param tenant_id
	 * @return true or false
	 */
	private boolean isExistsActivityList(String actid, String tenant_id) {
		String ac_url = AsynDataIns.getValueFromGlobal("ACTIVITY_INFO");
		if (ac_url == null || ac_url.equals("")) {
			logger.error("[OrderCenter] isExistsActivityList Get activity_url error...");
			return false;
		}
		// call api to get activity list
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tenantId", tenant_id);
		String rsp = HttpUtil.doGet(ac_url, map);
		if (rsp == null || rsp.equals("")) {
			logger.error("[OrderCenter] isExistsActivityList, Activity respond error..................!!!");
			return false;
		}

		List<String> act_list = JSON.parseArray(rsp, String.class);
		return act_list.contains(actid);

	}

	/**
	 * init condition variable
	 * 
	 * @param
	 * @return
	 */
	private void initOrderCondition() {
		this.activitySeqId = 0;
		this.userLabelSql = AsynDataIns.getValueFromGlobal("USER_LABEL_SQL");
		this.userLabelColumn = AsynDataIns.getValueFromGlobal("USER_LABEL_COLUMN");
		this.orderBeginDate = null;
		this.orderEndDate = null;
		String splitNum = AsynDataIns.getValueFromGlobal("ORDER_SPLIT_NUM");
		if (splitNum != null)
			this.splitNum = Integer.parseInt(splitNum);
		else
			this.splitNum = 50000;// default value

		// user label table change status sql
		this.userLabelStatusSql = AsynDataIns.getValueFromGlobal("USER_LABEL_STATUS_SQL");
		this.judgeFlag = AsynDataIns.getValueFromGlobal("USER_LABEL_STATUS_JUDGE");

		logger.info("[OrderCenter] userLabelSql=" + this.userLabelSql + " userLabelColumn=" + this.userLabelColumn);
		logger.info("[OrderCenter] userLabelStatusSql=" + this.userLabelStatusSql + " judgeFlag=" + this.judgeFlag);
	}
}
