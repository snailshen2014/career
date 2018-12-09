package com.bonc.busi.orderschedule.service.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ActivityProvPo;
import com.bonc.busi.activity.ChannelGroupPopupPo;
import com.bonc.busi.activity.ChannelHandOfficePo;
import com.bonc.busi.activity.ChannelSmallWoPo;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.ChannelWebOfficePo;
import com.bonc.busi.activity.ChannelWebchatInfo;
import com.bonc.busi.activity.ChannelWoWindowPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.activity.MsmChannelPo;
import com.bonc.busi.activity.SuccessProductPo;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.activity.TelePhoneChannelPo;
import com.bonc.busi.orderschedule.bo.ActivityChannelStatus;
import com.bonc.busi.orderschedule.bo.ActivityDailySummary;
import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.bo.Order;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.bo.ResourceRsp;
import com.bonc.busi.orderschedule.bo.SpecialFilter;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.orderschedule.service.ReviewOrderService;
import com.bonc.busi.orderschedule.utils.OrderFileMannager;
import com.bonc.busi.statistic.service.StatisticService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.task.service.BaseTaskSrv;
import com.bonc.common.base.JsonResult;
import com.bonc.common.datasource.TargetDataSource;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;

@Service("reviewOrderService")
@EnableAutoConfiguration
// @ConfigurationProperties(prefix = "xcloud", ignoreUnknownFields = false)

public class ReviewOrderServiceImpl implements ReviewOrderService {
	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);
	private static final String selectField = "b.TENANT_ID," + "a.USER_ID," + "a.DEVICE_NUMBER," + "b.ORGPATH,"
			+ "0 as ORDER_STATUS," + "b.MONTH_ID as DEAL_MONTH," + "a.DATA_TYPE as SERVICE_TYPE ," + "b.CITYID,"
			+ "b.AREAID ," + "c.AREAID as USER_ORG_ID," + "c.ORGPATH as USER_PATH";

	private static final String ORDER_COLUMLIST = " (ACTIVITY_SEQ_ID,BATCH_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID"
			+ ",USER_ID,PHONE_NUMBER,ORG_PATH,ORDER_STATUS,DEAL_MONTH,SERVICE_TYPE,CITYID,AREAID"
			+ ",USER_ORG_ID,USER_PATH,BEGIN_DATE,END_DATE,LAST_UPDATE_TIME,INPUT_DATE,INVALID_DATE,";

	// private String remotepath;
	// private String localpath;
	// private String server;
	// private String passwd;
	// private String user;
	private String filename;

	// private boolean isGenSmsOrder;
	// private boolean isGenOtherChannelOrder;
	private List<String> finishedOrderChannelList;
	private String accessResultCode = "0";

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

	// for count order
	@Autowired
	private StatisticService statisticService;

	@Autowired
	private FilterOrderService filterService;

	@Autowired
	private OrderMapper ordermapper;

	@Autowired
	private BusiTools AsynDataIns;

	@Autowired
	private BaseMapper TaskBaseMapperDao;

	@Autowired
	private BaseTaskSrv baseTask;

	private void setActivitySeqId(PltActivityInfo act) {

		this.activitySeqId = ordermapper.getActivityRecid(act);
	}

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

	/*
	 * public String getRemotepath() { return remotepath; }
	 * 
	 * public void setRemotepath(String path) { this.remotepath = path; }
	 * 
	 * public String getLocalpath() { return localpath; }
	 * 
	 * public void setLocalpath(String path) { this.localpath = path; }
	 * 
	 * public String getServer() { return server; }
	 * 
	 * public void setServer(String sr) { this.server = sr; }
	 * 
	 * public String getUser() { return user; }
	 * 
	 * public void setUser(String usr) { this.user = usr; }
	 * 
	 * public String getPasswd() { return passwd; }
	 * 
	 * public void setPasswd(String pd) { this.passwd = pd; }
	 */

	private static String sendHttpRequest(String activity_url) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000);
		requestFactory.setReadTimeout(1000);
		System.out.println(activity_url);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String result = restTemplate.getForObject("https://www.baidu.com", String.class);

		// ActivityProvPo act = restTemplate.getForObject(activity_url,
		// ActivityProvPo.class);
		// System.out.println("result===" + result);
		// int dex = result.indexOf("</html>");
		// System.out.println("result===" + result.substring(dex +7,
		// result.length()));
		// String rtn = result.substring(dex +7, result.length());
		// rtn = rtn.trim();
		return "";

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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new java.sql.Timestamp(parsed.getTime());

	}

	private boolean recordActivityInfo(ActivityProvPo act, String orderDate) {
		// 3.commitActivityInfo
		// set activitySeqId
		this.activitySeqId = AsynDataIns.getActivitySeqId();
		if (this.activitySeqId == -1) {
			logger.error("[OrderCenter] get activity sequence id error.");
			return false;
		}

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
		ac_obj.setLAST_ORDER_CREATE_TIME(getReviewOrderDate(orderDate));
		// 预留百分比
		if (act.getObligateOrder() != null)
			ac_obj.setREMAIN_PERCENT(Integer.parseInt(act.getObligateOrder()));
		commitActivityInfo(ac_obj);
		return true;
	}

	private void commitChannelInfo(ActivityProvPo act) {
		// 渠道-本地弹窗 8
		if (act.getChannelGroupPopupPoList() != null) {
			for (ChannelGroupPopupPo po : act.getChannelGroupPopupPoList()) {
				if (po.getChannelId() != null) {
					po.setActivity_Id(act.getActivityId());
					po.setTenant_id(act.getTenantId());
					ordermapper.InsertGroupPop(po);
				}
			}
		}

		// channelHandOfficePo 1-手厅
		if (act.getChannelHandOfficePo() != null) {
			ChannelHandOfficePo hand = act.getChannelHandOfficePo();
			if (hand.getChannelId() != null) {
				hand.setActivityId(act.getActivityId());
				hand.setTenantId(act.getTenantId());
				ordermapper.InsertChannelHandOffice(hand);
			}
		}
		// channelWebOfficePo 2-网厅
		if (act.getChannelWebOfficePo() != null) {
			ChannelWebOfficePo web = act.getChannelWebOfficePo();
			if (web.getChannelId() != null) {
				web.setActivityId(act.getActivityId());
				web.setTenantId(act.getTenantId());
				ordermapper.InsertChannelWebOffice(web);
			}
		}
		// channelWebchatInfo 11-微信
		if (act.getChannelWebchatInfo() != null) {
			ChannelWebchatInfo wechat = act.getChannelWebchatInfo();
			if (wechat.getChannelId() != null) {
				wechat.setActivityId(act.getActivityId());
				wechat.setTenantId(act.getTenantId());
				ordermapper.InsertChannelWebchatInfo(wechat);
			}
		}
		// channelWoWindowPo 9-活视窗
		if (act.getChannelWoWindowPo() != null) {
			ChannelWoWindowPo wo = act.getChannelWoWindowPo();
			wo.setTenantId(act.getTenantId());
			wo.setActivityId(act.getActivityId());
			if (wo.getChannelId() != null)
				ordermapper.InsertChannelWebWoWindow(wo);
		}
		// frontlineChannelPo 5
		if (act.getFrontlineChannelPo() != null) {
			FrontlineChannelPo front = act.getFrontlineChannelPo();
			front.setTenantId(act.getTenantId());
			front.setActivityId(act.getActivityId());
			if (front.getChannelId() != null)
				ordermapper.InsertChannelFrontline(front);
		}
		// msmChannelPo 7-短信
		if (act.getMsmChannelPo() != null) {
			MsmChannelPo msm = act.getMsmChannelPo();
			msm.setTenantId(act.getTenantId());
			msm.setActivityId(act.getActivityId());
			if (msm.getChannelId() != null) {
				ordermapper.InsertChannelMsm(msm);
			}
		}

	}

	private void commitSuccessInfo(ActivityProvPo act) {
		SuccessStandardPo success = act.getSuccessStandardPo();
		if (success != null) {
			String activity_seq_id = getActivitySeqId().toString();
			success.setTenantId(act.getTenantId());
			success.setActivityId(act.getActivityId());
			success.setActivity_seq_id(activity_seq_id);
			success.setIsHaveRenewProduct(act.getSuccessStandardPo().getIsHaveRenewProduct() != null
					? act.getSuccessStandardPo().getIsHaveRenewProduct() : "0");
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

	private void filterOrderInfo(ActivityProvPo act) {
		// black user delete
		if (act.getIsDeleteBlackUser().equals("1")) {

		}
		// white user delete
		if (act.getIsDeleteWhiteUser().equals("1")) {

		}

		// 是否同一活动分类用户剔除
		if (act.getIsDeleteSameType().equals("1")) {

		}
		// 是否同一活动成功标准类型用户剔除

		if (act.getIsDeleteSameSuccess().equals("1")) {

		}
		// "orderUpdateRule": "工单更新规则 1、有进有出，2、覆盖",
		if (act.getOrderUpdateRule().equals("1")) {

		}
		if (act.getOrderUpdateRule().equals("2")) {

		}
		// 接触过滤
		// 4.pass black white list users
		// BlackWhiteUserList usr = new BlackWhiteUserList();
		// usr.setUSER_ID("196");
		// usr.setFILTE_TYPE("01");

		// System.out.println(ordermapper.isBlackWhiteUser(usr));
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
		PltCommonLog logdb = new PltCommonLog();
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("55");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("ORDER_GEN");
		logdb.setBUSI_CODE("GET_USER_GROUP_INFO_BEGIN");
		logdb.setBUSI_ITEM_1(userGroupId);
		logdb.setBUSI_ITEM_3(targetid);
		logdb.setBUSI_ITEM_4(act.getActivityId());
		logdb.setBUSI_ITEM_5(this.activitySeqId.toString());

		AsynDataIns.insertPltCommonLog(logdb);
		// get usergroup condition sql
		String con = getUserGroupInfo(userGroupId);
		if (con == null) {
			logger.error("[OrderCenter] Can not get user group info,user gourp sql null.");
			return null;
		}
		// log
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("GET_USER_GROUP_INFO_END");
		logdb.setBUSI_ITEM_10(con);
		AsynDataIns.insertPltCommonLog(logdb);

		// multi and one orderissuleruler using same api
		// String xCloudUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_DIVIDE");
		String xCloudUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_MULT_DIVIDE");
		// 参数格式：{ rule_type_id:规则ID,rule_sql:查询条件, rule_type_sort:[{id: 字段对应关系ID
		// }]}

		// String jsonStr = "{rule_type_id:'4',tenant_id:'uni076'}";
		String resJson = "{rule_type_id:'" + orderIssuedRule + "',";
		resJson += "ruleOrgPath:'";
		if (targetid.equals("5")) {
			resJson += act.getFrontlineChannelPo().getRuleOrgPath();
		}
		if (targetid.equals("14")) { // 电话渠道,需要把规则使用范围传给资源划配
			resJson += act.getChannelTelePhone().getRuleOrgPath();
		}

		resJson += "',";
		resJson += "rule_sql:\"";
		if (con == null)
			resJson += " ";
		else
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
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RESOURCE_ASSIGN_BEGIN");
		logdb.setBUSI_ITEM_1(resJson);
		AsynDataIns.insertPltCommonLog(logdb);

		// call resource assign api
		String resourceRep = requestResource(xCloudUrl, resJson);
		logger.info("[OrderCenter] resource's respond=" + resourceRep);

		// log
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RESOURCE_ASSIGN_END");
		logdb.setBUSI_ITEM_1(resourceRep);
		AsynDataIns.insertPltCommonLog(logdb);

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
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RESOURCE_GET_STATUS_BEGIN");
		logdb.setBUSI_ITEM_1(orderIssuedRule);
		logdb.setBUSI_ITEM_2(statusUrl);
		AsynDataIns.insertPltCommonLog(logdb);

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
					// TODO Auto-generated catch block
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
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RESOURCE_GET_STATUS_END");
		AsynDataIns.insertPltCommonLog(logdb);
		return resourceTable;

	}

	private static String requestResource(String url, String json) {
		Map<String, String> map = null;
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return buf.toString();
	}

	private static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
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

	private boolean loadDataToMysql(String channel_id, String tenant_id, String localpath, String actId) {
		String loadSql = "LOAD DATA   local INFILE  '";
		loadSql += localpath;
		loadSql += getFileName();
		loadSql += ".csv";
		// loadSql += "order_tmp_20161119202037.csv";
		loadSql += "'";
		// loadSql += " replace into table ";
		loadSql += " IGNORE into table ";
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
		logger.info("[OrderCenter] Load data to mysql begin,sql=" + loadSql + "|time:" + getSysDateTime());
		// ordermapper.loadDataToMysql(loadSql);
		// load data by mysql
		if (AsynDataIns.loadDataInMysql(loadSql, tenant_id)) {
			logger.info("[OrderCenter] Load data finished status ok,time:" + getSysDateTime());
			// delete local file
			AsynDataIns.deleteFile(localpath + getFileName() + ".csv");
			return true;
		} else {
			logger.info("[OrderCenter] Load data finished status error,time:" + getSysDateTime());
			// delete local file
			AsynDataIns.deleteFile(localpath + getFileName() + ".csv");
			return false;
		}

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
			else if (channel_id.equals("5"))// 5: frontline
				loadSql += " PLT_ORDER_INFO fields  terminated by \'|\' ";
			else if (channel_id.equals("1") || channel_id.equals("2") || channel_id.equals("9"))
				loadSql += " PLT_ORDER_INFO_ONE fields  terminated by \'|\' ";
			else if (channel_id.indexOf("8") != -1)
				loadSql += " PLT_ORDER_INFO_POPWIN fields  terminated by \'|\' ";
			else if (channel_id.equals("11"))
				loadSql += " PLT_ORDER_INFO_WEIXIN fields  terminated by \'|\' ";
			else if (channel_id.equals("14")) // 电话渠道
				loadSql += " PLT_ORDER_INFO_CALL fields  terminated by \'|\' ";
			else if (channel_id.equals("13")) {// 小沃渠道
				loadSql += " PLT_ORDER_INFO_SMALLWO fields  terminated by \'|\' ";
			} else {
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
	 * count order begin_date ,end_date
	 * 
	 * @param activityCycle
	 *            数据更新周期 1、月，2、日，3、一次性",
	 * @param orderCycle
	 * @return xcloud date sql
	 */
	private String genOrderLifeDate(String activityCycle, String orderCycle) {
		int flag = Integer.parseInt(activityCycle);
		String sql = null;
		// record order begin date
		this.orderBeginDate = getCurrentTime("yyyy-MM-dd HH:mm:ss");
		// for count date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String resultEndDate = null;

		switch (flag) {
		case 1:// one month one time
			sql = " ,SYSDATE as BEGIN_DATE ,ADD_MONTHS(SYSDATE,";
			sql += orderCycle;
			sql += ") as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			// count end date
			calendar.add(Calendar.MONTH, Integer.parseInt(orderCycle));
			Date date = calendar.getTime();
			resultEndDate = dateFormat.format(date);

			break;
		case 2:// day
			sql = ",SYSDATE as BEGIN_DATE,SYSDATE + ";
			sql += orderCycle;
			sql += " as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";

			// count end date
			calendar.add(Calendar.DATE, Integer.parseInt(orderCycle));
			Date date2 = calendar.getTime();
			resultEndDate = dateFormat.format(date2);

			break;
		case 3:// only one time
			sql = ",SYSDATE  as BEGIN_DATE, \'2099:12:31 23:59:59\' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			resultEndDate = "2099-12-31 23:59:59";
			break;
		default:
			break;
		}

		// record order end date
		this.orderEndDate = resultEndDate;
		return sql;
	}

	/**
	 * count order begin_date ,end_date refer to orderIsConsultEndDate
	 * 
	 * @param activityCycle
	 *            数据更新周期 1、月，2、日，3、一次性",
	 * @param orderCycle
	 * @return xcloud date sql
	 */
	private String genOrderLifeDateByActivity(String activityCycle, String orderCycle, String endDate) {
		int flag = Integer.parseInt(activityCycle);
		// for count date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();

		// record order begin date
		this.orderBeginDate = getCurrentTime("yyyy-MM-dd HH:mm:ss");

		Date activityEndDate = null;
		// add hh mm ss
		String endDateAfter = endDate;
		try {

			// yyyy-mm-dd
			if (endDateAfter.trim().length() == 10) {
				endDateAfter += " 00:00:00";
			} else {
				logger.error("[OrderCenter] actiivty endDate format error.!");
				return null;
			}
			activityEndDate = dateFormat.parse(endDateAfter);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("[OrderCenter] dateFormat activity enddate error.");
			return null;
		}
		String resultEndDate = null;
		String sql = null;
		switch (flag) {
		case 1:// one month one time
			calendar.add(Calendar.MONTH, Integer.parseInt(orderCycle));
			Date date = calendar.getTime();
			// set smaller date
			if (date.before(activityEndDate)) {
				// set date
				resultEndDate = dateFormat.format(date);
			} else {
				// set activityEndDate
				resultEndDate = endDateAfter;
			}
			sql = " ,SYSDATE as BEGIN_DATE ,'";
			sql += resultEndDate;
			sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			break;
		case 2:// day
			calendar.add(Calendar.DATE, Integer.parseInt(orderCycle));
			Date date2 = calendar.getTime();
			// set smaller date
			if (date2.before(activityEndDate)) {
				// set date
				resultEndDate = dateFormat.format(date2);
			} else {
				// set activityEndDate
				resultEndDate = endDateAfter;
			}
			sql = ",SYSDATE as BEGIN_DATE,'";
			sql += resultEndDate;
			sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			break;
		case 3:// only one time
			sql = ",SYSDATE  as BEGIN_DATE, \'2099:12:31 23:59:59\' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			// set activityEndDate
			resultEndDate = "2099-12-31 23:59:59";
			break;
		default:
			break;
		}

		// record order end date
		this.orderEndDate = resultEndDate;
		return sql;
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
			String endDate,String orderDate) {
		int flag = Integer.parseInt(activityCycle);
		// for count date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();

		// record order begin date
		this.orderBeginDate = getReviewOrderDateTime(orderDate);

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
				// TODO Auto-generated catch block
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
				sql += "' as END_DATE , ";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
				// set end date
				resultEndDate = tmpEndDate;
			} else {
				// refer to activity enddate
				if (refer != null && refer.equals("1")) {
					sql = ",'";
					sql += this.orderBeginDate;
					sql += "'  as BEGIN_DATE, '";
					sql += endDateAfter;
					//sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
					sql += "' as END_DATE , ";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
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
					//sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
					sql += "' as END_DATE , ";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
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
				//sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				sql += "' as END_DATE , ";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
				// set end date
				resultEndDate = tmpEndDate2;
			} else {
				// refer to activity enddate
				if (refer != null && refer.equals("1")) {
					sql = ",'";
					sql += this.orderBeginDate;
					sql += "'  as BEGIN_DATE, '";
					sql += endDateAfter;
					//sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
					sql += "' as END_DATE , ";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
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
					//sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
					sql += "' as END_DATE , ";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
					sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
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
				//sql += "' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				sql += "' as END_DATE , ";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
				// set activityEndDate
				resultEndDate = endDateAfter;
			} else {
				sql = ",'";
				sql += this.orderBeginDate;
				sql += "'  as BEGIN_DATE, ";
				//sql += "'2099:12:31 23:59:59' as END_DATE ,""SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
				sql += "'2099:12:31 23:59:59' as END_DATE ,";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INPUT_DATE,";
				sql += "'"+getReviewOrderDateTime(orderDate)+"'  as INVALID_DATE";
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
			String org_range, String startDate, String endDate, String remotePath, String orderIsConsultEndDate,
			String productId, String productName, String orderDate,String tenantId) {
		// get max date id
		// String sql = "(SELECT max(date_id) FROM DIM_KFPT_BAND_DATE)";
		//String maxDateId = TaskBaseMapperDao.getMaxDateId();
		//--获取相近账期--
		//String maxDateId = getReviewDateId(orderDate, tenantId);(暂时保留)
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

		runSQL += genOrderLifeReferActivityDate(activityCycle, orderCycle, orderIsConsultEndDate, endDate,orderDate);
		runSQL += ", '"+getReviewOrderDateTime(orderDate)+"' as LAST_UPDATE_TIME, ";

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

		runSQL += " FROM  UNICOM_D_MB_DS_ALL_LABEL_INFO"+getReviewUserLabel(orderDate)+" a ";
		runSQL += " , ";
		runSQL += tabname;
		runSQL += " b ";
		runSQL += " ,wxwl_client_assign c ";
		runSQL += "where b.clientcode = a.USER_ID ";
		runSQL += " and b.clientcode = c.clientcode ";
		//依靠宽表本身卡主账期，则不需要预测的账期
		/*runSQL += " and a.DATE_ID ='";
		runSQL += maxDateId;
		runSQL += "'";*/
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

		String replaceProductIdRunSQL = runSQL;
		// 由于没有剩余的列,暂且使用a.PRODUCT_CLASS列存储productId
		if (productId != null && !productId.equals("")) {
			if (productId.indexOf("case") != -1) {
				replaceProductIdRunSQL = runSQL.replace("a.PRODUCT_CLASS", productId);
			} else {
				replaceProductIdRunSQL = runSQL.replace("a.PRODUCT_CLASS", "\'" + productId + "\'");
			}
		}
		String replaceProductNameRunSQL = replaceProductIdRunSQL;
		if (productName != null && !productName.equals("")) {
			if (productName.indexOf("case") != -1) {
				replaceProductNameRunSQL = replaceProductIdRunSQL.replace(
						"( CASE WHEN a.KD_INNET_LENGTH IS NULL THEN 0 ELSE a.KD_INNET_LENGTH END )", productName);
			} else {
				replaceProductNameRunSQL = replaceProductIdRunSQL.replace(
						"( CASE WHEN a.KD_INNET_LENGTH IS NULL THEN 0 ELSE a.KD_INNET_LENGTH END )",
						"\'" + productName + "\'");
			}
		}

		return replaceProductNameRunSQL;
	}

	/**
	 * xcloud datasource test
	 * 
	 * @param
	 * @return
	 */
	@Override
	@TargetDataSource(name = "xcloud")
	public void TestXcloud() {
		ordermapper.TestXcloud();
	}

	/**
	 * oracle datasource test
	 * 
	 * @param
	 * @return
	 */
	@Override
	@TargetDataSource(name = "oracle")
	public void TestOracle() {
		List<String> ts = ordermapper.TestOracle();
		for (String s : ts) {
			System.out.println(s);
		}
	}

	/**
	 * request activity info
	 * 
	 * @param url
	 * @return json string
	 */
	@Override
	public String getActivityInfo(String activity_url) {
		return "";
	}

	/**
	 * business deal function
	 * 
	 * @param url
	 * @return
	 */
	@Override
	public void doActivity(String activity_url, String tenantId) {

		// set order generate status
		// this.isGenSmsOrder = false;
		// this.isGenOtherChannelOrder = false;
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

		// call api to get activity list
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tenantId", tenantId);
		String rsp = HttpUtil.doGet(activity_url, map);
		logger.info("[OrderCenter] Activity list Info:" + rsp);

		// log
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("55");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR("doActivity");
		PltCommonLogIns.setBUSI_CODE("Get Activity info.");
		if (rsp != null && !rsp.equals("") && !rsp.equals("ERROR")) {
			String tmp = rsp;
			if (tmp.length() > 6000)
				tmp = tmp.substring(0, 6000);
			PltCommonLogIns.setBUSI_ITEM_1(tmp);
		} else
			PltCommonLogIns.setBUSI_ITEM_1("[OrderCenter] Activity respond error.");

		PltCommonLogIns.setBUSI_ITEM_2(activity_url);

		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		if (rsp == null || rsp.equals("") || rsp.equals("ERROR")) {
			logger.error("[OrderCenter] Activity respond error..................!!!");
			return;
		}
		List<String> act_list = JSON.parseArray(rsp, String.class);
		for (int i = 0; i < act_list.size(); i++) {
			logger.info("[OrderCenter] Activity id:" + act_list.get(i) + " begin running...");
			// log
			PltCommonLogIns.setBUSI_ITEM_2("");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("Activity Begin");
			PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

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
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("Activity Begin");
			PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
			if (respond.length() > 8000) {
				String tmp = respond.substring(0, 8000);
				PltCommonLogIns.setBUSI_ITEM_10(tmp);
			} else
				PltCommonLogIns.setBUSI_ITEM_10(respond);

			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

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
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("Activity Begin");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				PltCommonLogIns.setBUSI_ITEM_10("activity status!=9 ,no need run.");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				continue;
			}
			if (isActivityNeedRun(act_id, tenant_id, Integer.parseInt(flag), activityEndDate) == -1) {
				logger.info("[OrderCenter] Activity id=" + act_id + " no need run.");
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("Activity Begin");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				PltCommonLogIns.setBUSI_ITEM_10("no need run.");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				continue;
			}

			// init list
			this.finishedOrderChannelList = new ArrayList<String>();

			// record activity info
			/*
			 * if (!recordActivityInfo(actjson)) { logger.error(
			 * "[OrderCenter] Activity id=" + act_id +
			 * " get activity sequence error."); continue; }
			 */

			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("RECORD_ACT_INFO");
			PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
			// activity_id
			PltCommonLogIns.setBUSI_ITEM_4(act_id);
			// activity_seq_id
			PltCommonLogIns.setBUSI_ITEM_5(this.activitySeqId.toString());
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

			// generate all channels order
			String ogr_range = actjson.getOrgRange();
			// genAllChannelOrderInfo(actjson, ogr_range,
			// PltCommonLogIns,orderDate);

			// update activity status
			if (this.finishedOrderChannelList.size() > 0) {
				// log
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("FILTER_ORDER_BEGIN");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);

				// order filter call api
				filterService.filterOrderStatus(this.getActivitySeqId(), tenantId, this.finishedOrderChannelList);

				// call api for filter success standard
				String filterSuccessFlag = AsynDataIns.getValueFromGlobal("FILTER_SUCCESS");
				if (filterSuccessFlag != null && filterSuccessFlag.equals("1"))
					baseTask.orderFilterSucess(this.getActivitySeqId(), actjson.getSuccessStandardPo(), tenant_id);

				// call api for reserve order
				filterService.reserveOrder(act_id, this.getActivitySeqId(), tenantId, this.finishedOrderChannelList);

				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("FILTER_ORDER_END");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);

				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("UPDATE_ACT_STATUS,COMMIT_SUCCESS_INFO");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				updateActivityStatus(actjson);
				// sync success info
				commitSuccessInfo(actjson);

				// log
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("FINISHED,ok");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);

				HashMap<String, Object> activityMap = new HashMap<String, Object>();
				activityMap.put("tenantId", tenant_id);
				activityMap.put("activitySeqId", getActivitySeqId());
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("COUNT_BEGIN");
				PltCommonLogIns.setBUSI_ITEM_10("");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);

				// call api
				try {
					statisticService.incrStatistic(activityMap);
					PltCommonLogIns.setSTART_TIME(new Date());
					PltCommonLogIns.setBUSI_CODE("COUNT_END");
					PltCommonLogIns.setBUSI_ITEM_2("Call statistic ok.");
					AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("[OrderCenter]" + e.getMessage());
					PltCommonLogIns.setSTART_TIME(new Date());
					PltCommonLogIns.setBUSI_CODE("COUNT_END");
					PltCommonLogIns.setBUSI_ITEM_1("count exception");
					AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				}
				logger.info("[OrderCenter] Activity id:" + act_list.get(i) + " finished, status ok.");
			} else {
				if ("0".equals(this.accessResultCode)) {
					logger.info(
							"[OrderCenter] Activity id:" + act_list.get(i) + " finished,but No Access Channel Run!");
					PltCommonLogIns.setSTART_TIME(new Date());
					PltCommonLogIns.setBUSI_CODE("FINISHED,NoChannelAccess.");
					PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
					AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				} else {
					PltActivityInfo ac_obj = new PltActivityInfo();
					ac_obj.setREC_ID(getActivitySeqId());
					ac_obj.setTENANT_ID(actjson.getTenantId());
					ordermapper.cleanActivityInfo(ac_obj);
					System.out.println("[OrderCenter] Activity id:" + act_list.get(i) + " finished,status:error!");
					// log
					PltCommonLogIns.setSTART_TIME(new Date());
					PltCommonLogIns.setBUSI_CODE("FINISHED,error.");
					PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
					PltCommonLogIns.setBUSI_ITEM_10("clean activity info");
					AsynDataIns.insertPltCommonLog(PltCommonLogIns);
					logger.info("[OrderCenter] Activity id:" + act_list.get(i) + " finished, status error.");
				}
			}

			// only deal one activity one time,for activity Priority
			break;

		} // end for activity

	}

	/**
	 * insert activity table in mysql
	 * 
	 * @param activity
	 * @return 0:success;-1 error
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
	 * create order records on xcloud
	 * 
	 * @param sql
	 * @return 0:success;-1 error
	 */
	@Override
	public void genOrderRec(String sql) throws java.sql.SQLException {
		ordermapper.genOrderRec(sql);
	}

	/**
	 * insert activity summmary table in mysql
	 * 
	 * @param activity
	 * @return 0:success;-1 error
	 */
	@Override
	public Integer commitActivitySummmaryInfo(ActivityDailySummary summary) {
		return 1;
	}

	private boolean updateWechatStatus(ActivityProvPo act) {
		if (act.getChannelWebchatInfo() == null) {
			return false;
		}
		if (act.getChannelWebchatInfo().getWebChatMidActivityPo() != null) {
			String state = act.getChannelWebchatInfo().getWebChatMidActivityPo().getState();
			// orginal wechat state
			PltActivityChannelDetail detail = new PltActivityChannelDetail();
			detail.setACTIVITY_ID(act.getActivityId());
			detail.setTENANT_ID(act.getTenantId());
			String oldState = ordermapper.getWechatStatus(detail);
			if (oldState == null)
				return false;
			if (!state.equals(oldState)) {
				// update detail
				detail.setWECHAT_STATUS(state);
				// update all one activity chatweb status,can not confirm which
				// cycle activity
				ordermapper.updateWechatStatus(detail);
				return true;
			}
			return false;
		}
		return false;
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
	private void syncProcessLog(ActivityProvPo act, String channelId, int type, int status, String orderDate) {
		// log activity process log
		ActivityProcessLog processLog = new ActivityProcessLog();
		processLog.setACTIVITY_ID(act.getActivityId());
		processLog.setTENANT_ID(act.getTenantId());
		processLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
		processLog.setCHANNEL_ID(channelId);
		processLog.setSTATUS(status);
		if (type == 0) {
			processLog.setBEGIN_DATE(getReviewOrderDate(orderDate));
			ordermapper.InsertActivityProcessLog(processLog);
		}
		if (type == 1) {
			processLog.setEND_DATE(getReviewOrderDate(orderDate));
			// processLog.setCHANNEL_ORDER_NUM(ordermapper.getOrderNumByChannelId(processLog));
			processLog.setCHANNEL_ORDER_NUM(OrderFileMannager.getOrderNumber().toString());
			ordermapper.UpdateActivityProcessLog(processLog);
		}
	}

	private void genAllChannelOrderInfo(ActivityProvPo act, String org_range, PltCommonLog log, String orderDate) {
		// log
		log.setSTART_TIME(new Date());
		log.setBUSI_CODE("ORDER_GEN_START");
		log.setBUSI_ITEM_1(act.getActivityId());

		// insert status
		ActivityChannelStatus status = new ActivityChannelStatus();
		status.setACTIVITY_ID(act.getActivityId());
		status.setACTIVITY_SEQ_ID(getActivitySeqId());
		status.setSTATUS("0");
		status.setTENANT_ID(act.getTenantId());

		// call api to get activity enableChannel
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("tenantId", act.getTenantId());
		param.put("activityId", act.getActivityId());
		String enableChannel_url = AsynDataIns.getValueFromGlobal("ORDER_CHECK_ENABLECHANNEL");
		String enableChannel_result = HttpUtil.doGet(enableChannel_url, param);
		if ("ERROR".equals(enableChannel_result)) {
			logger.error("call api to get activity enableChannel error");
			this.accessResultCode = "2";
			return;
		}
		HashMap<String, Object> jsonResult = (HashMap<String, Object>) JSON.parseObject(enableChannel_result,
				Map.class);
		// 因前端活动追踪有问题，先取消渠道审批日志
		// log.setSTART_TIME(new Date());
		// log.setBUSI_CODE("ORDER_ENABLECHANNEL_LIST");
		// log.setBUSI_ITEM_3(enableChannel_result);
		// AsynDataIns.insertPltCommonLog(log);
		// for channel list ,generate every channel's order info
		// 渠道-本地弹窗 8
		if (act.getChannelGroupPopupPoList() != null && isInAccessList(jsonResult, IContants.TC_CHANNEL)) {
			for (ChannelGroupPopupPo po : act.getChannelGroupPopupPoList()) {
				if (po.getChannelId() != null) {
					String orderIssuedRule = po.getOrderIssuedRule();
					String tmpTable = createTempOrderInfo(act, orderIssuedRule, po.getChannelId());
					if (tmpTable != null && orderIssuedRule != null) {
						String subChannelId = po.getChannelId();
						subChannelId += po.getBusinessHall();
						// record process log
						syncProcessLog(act, subChannelId, 0, 0, orderDate);

						// modified by shenyj at 2017-03-08 14:29 for adding
						// special filter
						String recomendInfo, smsTemplate;
						recomendInfo = smsTemplate = "";

						String productId, productName;
						productId = productName = "";

						String specialFilterListString = null;
						if (po.getChannelSpecialList() != null) {
							recomendInfo = getSpecialFilterMap(po.getChannelSpecialList(), 0);
							smsTemplate = getSpecialFilterMap(po.getChannelSpecialList(), 1);
							productId = getSpecialFilterMap(po.getChannelSpecialList(), 2);
							productName = getSpecialFilterMap(po.getChannelSpecialList(), 3);
							specialFilterListString = JSON.toJSONString(po.getChannelSpecialList());
						}
						if (genOrderinfoByChannelId(act, subChannelId, po.getFilterConditionSql(), tmpTable,
								recomendInfo, smsTemplate, org_range, productId, productName, orderDate)) {
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
							if (specialFilterListString != null) {
								if (specialFilterListString.length() > 4999) {
									specialFilterListString = specialFilterListString.substring(0, 4500);
								}
								detail.setCHANNEL_SPECIALFILTER_LIST(specialFilterListString);
							}
							/**
							 * 弹窗添加SmsSendWords短信话术字段对应到detail表中的sms_words
							 * 
							 * @author 王明新
							 */
							detail.setSMS_WORDS(po.getSmsSendWords());

							ordermapper.insertChannelDetailInfo(detail);

							// insert channel status
							status.setCHANNEL_ID(subChannelId);
							// ordermapper.InsertActivityChannelStatus(status);

							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_END");
							log.setBUSI_ITEM_3(subChannelId);
							AsynDataIns.insertPltCommonLog(log);

							// record process log
							syncProcessLog(act, subChannelId, 1, 0, orderDate);
						}
					} else {
						logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + po.getChannelId()
								+ " temp table null or orderIssuedRule null.");

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_ERROR");
						log.setBUSI_ITEM_3(po.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
						// record process log
						syncProcessLog(act, po.getChannelId(), 1, 1, orderDate);
					}
				}
			}
		}
		// channelHandOfficePo 1-手厅
		if (act.getChannelHandOfficePo() != null && isInAccessList(jsonResult, IContants.ST_CHANNEL)) {
			ChannelHandOfficePo hand = act.getChannelHandOfficePo();
			if (hand.getChannelId() != null) {
				String orderIssuedRule = hand.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, hand.getChannelId());

				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, hand.getChannelId(), 0, 0, orderDate);

					if (genOrderinfoByChannelId(act, hand.getChannelId(), hand.getFilterConditionSql(), tmpTable, "",
							"", org_range, "", "", orderDate)) {
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

						// insert channel status
						status.setCHANNEL_ID(hand.getChannelId());
						// ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_3(hand.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
						// update order number
						// record process log
						syncProcessLog(act, hand.getChannelId(), 1, 0, orderDate);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_3(hand.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					// record process log
					syncProcessLog(act, hand.getChannelId(), 1, 1, orderDate);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + hand.getChannelId()
							+ " temp table null or orderIssuedRule null.");
				}
			}
		}
		// channelWebOfficePo 2-网厅
		if (act.getChannelWebOfficePo() != null && isInAccessList(jsonResult, IContants.WT_CHANNEL)) {
			ChannelWebOfficePo web = act.getChannelWebOfficePo();
			if (web.getChannelId() != null) {
				String orderIssuedRule = web.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, web.getChannelId());
				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, web.getChannelId(), 0, 0, orderDate);
					if (genOrderinfoByChannelId(act, web.getChannelId(), web.getFilterConditionSql(), tmpTable, "", "",
							org_range, "", "", orderDate)) {
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

						// insert channel status
						status.setCHANNEL_ID(web.getChannelId());
						// ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_ITEM_3(web.getChannelId());
						log.setBUSI_CODE("ORDER_GEN_END");
						AsynDataIns.insertPltCommonLog(log);

						// record process log
						syncProcessLog(act, web.getChannelId(), 1, 0, orderDate);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_3(web.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + web.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, web.getChannelId(), 1, 1, orderDate);
				}

			}
		}
		// channelWebchatInfo 11-微信
		if (act.getChannelWebchatInfo() != null && isInAccessList(jsonResult, IContants.WX_CHANNEL)) {
			ChannelWebchatInfo wechat = act.getChannelWebchatInfo();
			if (wechat.getChannelId() != null) {
				String orderIssuedRule = wechat.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, wechat.getChannelId());

				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, wechat.getChannelId(), 0, 0, orderDate);

					if (genOrderinfoByChannelId(act, wechat.getChannelId(), wechat.getFilterConditionSql(), tmpTable,
							wechat.getChannelWebchatContent(), "", org_range, "", "", orderDate)) {
						// this.isGenOtherChannelOrder = true;
						this.finishedOrderChannelList.add(wechat.getChannelId());
						// sync wechat channel info
						wechat.setActivityId(act.getActivityId());
						wechat.setTenantId(act.getTenantId());
						wechat.setACTIVITY_SEQ_ID(getActivitySeqId());
						ordermapper.InsertChannelWebchatInfo(wechat);

						// insert channel status
						status.setCHANNEL_ID(wechat.getChannelId());
						// ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_ITEM_3(wechat.getChannelId());
						log.setBUSI_CODE("ORDER_GEN_END");
						AsynDataIns.insertPltCommonLog(log);
						// record process log
						syncProcessLog(act, wechat.getChannelId(), 1, 0, orderDate);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_3(wechat.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + wechat.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, wechat.getChannelId(), 1, 1, orderDate);
				}
			}
		}
		// channelWoWindowPo 9-活视窗
		if (act.getChannelWoWindowPo() != null && isInAccessList(jsonResult, IContants.WSC_CHANNEL)) {
			ChannelWoWindowPo wo = act.getChannelWoWindowPo();
			if (wo.getChannelId() != null) {
				String orderIssuedRule = wo.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, wo.getChannelId());

				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, wo.getChannelId(), 0, 0, orderDate);
					if (genOrderinfoByChannelId(act, wo.getChannelId(), wo.getFilterConditionSql(), tmpTable, "", "",
							org_range, "", "", orderDate)) {
						// this.isGenOtherChannelOrder = true;
						this.finishedOrderChannelList.add(wo.getChannelId());
						// sync wowind channel info
						wo.setTenantId(act.getTenantId());
						wo.setActivityId(act.getActivityId());
						wo.setACTIVITY_SEQ_ID(getActivitySeqId());
						if (wo.getChannelId() != null)
							ordermapper.InsertChannelWebWoWindow(wo);

						// insert channel status
						status.setCHANNEL_ID(wo.getChannelId());
						// ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_3(wo.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
						// record process log
						syncProcessLog(act, wo.getChannelId(), 1, 0, orderDate);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_3(wo.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + wo.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, wo.getChannelId(), 1, 1, orderDate);
				}
			}
		}

		// 小沃渠道 - 13
		if (act.getChannelSmallWoPo() != null && isInAccessList(jsonResult, IContants.SMALL_WSC_CHANNEL)) {
			ChannelSmallWoPo smallWoPo = act.getChannelSmallWoPo();
			if (smallWoPo.getChannelId() != null) {
				String orderIssuedRule = smallWoPo.getOrderissuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, smallWoPo.getChannelId());
				if (tmpTable != null && orderIssuedRule != null) {

					syncProcessLog(act, smallWoPo.getChannelId(), 0, 0, orderDate);

					if (genOrderinfoByChannelId(act, smallWoPo.getChannelId(), smallWoPo.getFilterConditionSql(),
							tmpTable, "", "", org_range, "", "", orderDate)) {

						this.finishedOrderChannelList.add(smallWoPo.getChannelId());
						// 记录渠道详细信息
						PltActivityChannelDetail detail = new PltActivityChannelDetail();
						detail.setCHANN_ID(smallWoPo.getChannelId());
						detail.setTENANT_ID(act.getTenantId()); // 租户id
						detail.setACTIVITY_ID(act.getActivityId()); // 活动id
						detail.setACTIVITY_SEQ_ID(getActivitySeqId()); // 活动流水号
						detail.setFILTER_CON(smallWoPo.getFilterCondition()); // 筛选条件
						detail.setFILTER_SQL(smallWoPo.getFilterConditionSql()); // 筛选条件sql
						detail.setORDERISSUEDRULE(smallWoPo.getOrderissuedRule());// 下发规则
						detail.setSMS_WORDS(smallWoPo.getChannaelSmallWoContent());// 话术信息
						detail.setMARKET_WORDS(smallWoPo.getMarketingTarget()); // 营销目标
						ordermapper.insertChannelDetailInfo(detail);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_3(smallWoPo.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
						// record process log
						syncProcessLog(act, smallWoPo.getChannelId(), 1, 0, orderDate);

					}
				} else {
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_3(smallWoPo.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + smallWoPo.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, smallWoPo.getChannelId(), 1, 1, orderDate);
				}
			}
		}

		// TelePhoneChannelPo 14-电话渠道
		if (act.getChannelTelePhone() != null && isInAccessList(jsonResult, IContants.Tel_CHANNEL)) {
			// 获得电话渠道信息对象
			TelePhoneChannelPo tel = act.getChannelTelePhone();
			if (tel.getChannelId() != null) {
				String orderIssuedRule = tel.getOrderissuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, tel.getChannelId());
				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, tel.getChannelId(), 0, 0, orderDate);

					String specialFilterListString = null;
					List<ChannelSpecialFilterPo> telchannelSpecialFilters = null;
					String recomendInfo, smsTemplate;
					recomendInfo = smsTemplate = "";

					String productId, productName;
					productId = productName = "";

					if (tel.getTelchannelSpecialFilterList() != null) {
						telchannelSpecialFilters = tel.getTelchannelSpecialFilterList();
						recomendInfo = getSpecialFilterMap(telchannelSpecialFilters, 0);
						smsTemplate = getSpecialFilterMap(telchannelSpecialFilters, 1);
						productId = getSpecialFilterMap(telchannelSpecialFilters, 2);
						productName = getSpecialFilterMap(telchannelSpecialFilters, 3);
						specialFilterListString = JSON.toJSONString(telchannelSpecialFilters);
					}
					if (genOrderinfoByChannelId(act, tel.getChannelId(), tel.getFilterConditionSql(), tmpTable,
							recomendInfo, smsTemplate, org_range, productId, productName, orderDate)) {
						// this.isGenOtherChannelOrder = true;
						this.finishedOrderChannelList.add(tel.getChannelId());

						PltActivityChannelDetail detail = new PltActivityChannelDetail();
						detail.setCHANN_ID(tel.getChannelId()); // 渠道id
						detail.setRESERVE1(tel.getCompanyId()); // 渠道选择的外呼公司id
						detail.setMARKET_WORDS(tel.getTelephoneHuashuContent()); // 话术内容
						detail.setTENANT_ID(act.getTenantId()); // 租户id
						detail.setACTIVITY_ID(act.getActivityId()); // 活动id
						detail.setACTIVITY_SEQ_ID(getActivitySeqId()); // 活动流水号
						detail.setFILTER_CON(tel.getFilterCondition()); // 筛选条件
						detail.setFILTER_SQL(tel.getFilterConditionSql()); // 筛选条件sql
						detail.setCONTENT(JSON.toJSONString(tel.getCompanyInfoList()));// 公司列表
						if (specialFilterListString != null) {
							if (specialFilterListString.length() > 4999) {
								specialFilterListString = specialFilterListString.substring(0, 4500);
							}
							detail.setCHANNEL_SPECIALFILTER_LIST(specialFilterListString);
						}
						status.setCHANNEL_ID(tel.getChannelId());
						// ordermapper.InsertActivityChannelStatus(status);
						ordermapper.insertChannelDetailInfo(detail);
						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_3(tel.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
						// record process log
						syncProcessLog(act, tel.getChannelId(), 1, 0, orderDate);
					}

				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_3(tel.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + tel.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// record process log
					syncProcessLog(act, tel.getChannelId(), 1, 1, orderDate);
				}
			}
		}

		// frontlineChannelPo 5
		if (act.getFrontlineChannelPo() != null && isInAccessList(jsonResult, IContants.YX_CHANNEL)) {
			FrontlineChannelPo front = act.getFrontlineChannelPo();

			if (front.getChannelId() != null && front.getOrderIssuedRule() != null) {
				String orderIssuedRule = front.getOrderIssuedRule();
				String tmpTable = createTempOrderInfo(act, orderIssuedRule, front.getChannelId());
				if (tmpTable != null) {
					// record process log
					syncProcessLog(act, front.getChannelId(), 0, 0, orderDate);
					// specialFilter List
					if (front.getChannelSpecialFilterList() != null) {
						String recommendInfo, smsTemplate;
						recommendInfo = smsTemplate = "";
						String productId, productName;
						productId = productName = "";
						recommendInfo = getSpecialFilterMap(front.getChannelSpecialFilterList(), 0);
						smsTemplate = getSpecialFilterMap(front.getChannelSpecialFilterList(), 1);
						productId = getSpecialFilterMap(front.getChannelSpecialFilterList(), 2);
						productName = getSpecialFilterMap(front.getChannelSpecialFilterList(), 3);
						if (genOrderinfoByChannelId(act, front.getChannelId(), front.getFilterConditionSql(), tmpTable,
								recommendInfo, smsTemplate, org_range, productId, productName, orderDate)) {
							// this.isGenOtherChannelOrder = true;
							this.finishedOrderChannelList.add(front.getChannelId());
							// sync front line channel detail info

							front.setTenantId(act.getTenantId());
							front.setActivityId(act.getActivityId());
							front.setACTIVITY_SEQ_ID(getActivitySeqId());
							ordermapper.InsertChannelFrontline(front);

							// insert channel status
							status.setCHANNEL_ID(front.getChannelId());
							// ordermapper.InsertActivityChannelStatus(status);

							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_END");
							if (recommendInfo.length() > 2000) {
								recommendInfo = recommendInfo.substring(0, 1999);
							}
							log.setBUSI_ITEM_2(recommendInfo);
							log.setBUSI_ITEM_3(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 0, orderDate);
						} else {
							logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + front.getChannelId()
									+ " temp table null or orderIssuedRule null.");
							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_ERROR");
							if (recommendInfo.length() > 2000) {
								recommendInfo = recommendInfo.substring(0, 1999);
							}
							log.setBUSI_ITEM_3(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);

							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 1, orderDate);
						} // end if else gen order ok

					} else { // no specialFilter List
						if (genOrderinfoByChannelId(act, front.getChannelId(), front.getFilterConditionSql(), tmpTable,
								"", "", org_range, "", "", orderDate)) {
							// this.isGenOtherChannelOrder = true;
							this.finishedOrderChannelList.add(front.getChannelId());
							// sync front line channel info
							front.setTenantId(act.getTenantId());
							front.setActivityId(act.getActivityId());
							front.setACTIVITY_SEQ_ID(getActivitySeqId());
							ordermapper.InsertChannelFrontline(front);
							// insert channel status
							status.setCHANNEL_ID(front.getChannelId());
							// ordermapper.InsertActivityChannelStatus(status);
							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_END");
							log.setBUSI_ITEM_3(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 0, orderDate);

						} else {
							logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + front.getChannelId()
									+ " temp table null or orderIssuedRule null.");
							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_ERROR");
							log.setBUSI_ITEM_3(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);

							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 1, orderDate);
						} // end if else gen order ok
					} // end no specialfilter list
				} // end temptable

			} // end channel id not null,orderissuerule not null

		} // end front line

		// msmChannelPo 7-短信
		/*if (act.getMsmChannelPo() != null && isInAccessList(jsonResult, IContants.DX_CHANNEL)) {
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
					syncProcessLog(act, msm.getChannelId(), 0, 0, orderDate);

					String recommendInfo, smsTemplate;
					recommendInfo = smsTemplate = "";
					String productId, productName;
					productId = productName = "";
					String specialFilterListString = null;
					if (msm.getChannelSpecialFilterList() != null) {
						recommendInfo = getSpecialFilterMap(msm.getChannelSpecialFilterList(), 0);
						smsTemplate = getSpecialFilterMap(msm.getChannelSpecialFilterList(), 1);
						productId = getSpecialFilterMap(msm.getChannelSpecialFilterList(), 2);
						productName = getSpecialFilterMap(msm.getChannelSpecialFilterList(), 3);
						specialFilterListString = JSON.toJSONString(msm.getChannelSpecialFilterList());
					}
					if (genOrderinfoByChannelId(act, msm.getChannelId(), msm.getFilterConditionSql(), tmpTable, "", "",
							org_range, productId, productName, orderDate)) {
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
						if (specialFilterListString != null) {
							if (specialFilterListString.length() > 4999) {
								specialFilterListString = specialFilterListString.substring(0, 4500);
							}
							detail.setCHANNEL_SPECIALFILTER_LIST(specialFilterListString);
						}
						*//**
						 * 短信中的添加3个字段，其中channelpo产品编码对应detail表中的PRODUCT_LIST
						 * 
						 * @author 王明新
						 *//*
						detail.setPRODUCT_LIST(msm.getMsmProductCode());
						*//**
						 * 短信中的保存字段reserve2为短信端口号
						 * 
						 * @author 马擎泽
						 *//*
						detail.setRESERVE2(act.getSmsUsePort());
						*//**
						 * 短信中 1、短信免打扰编码：messNodisturbCode 对应detail表中
						 * MESS_NODISTURB_CODE 2、短信订购编码：messOrderCode 对应detail表中
						 * MESS_ORDER_CODE 3、短信有效时间：messEffectiveTime 对应detail表中
						 * MESS_EFFECTIVE_TIME 4、订购失败回复语：orderFailureReply
						 * 对应detail表中 ORDER_FAILURE_REPLY
						 * 5、订购超时回复语：orderOvertimeReply 对应detail表中
						 * ORDER_OVERTIMEREPLY_REPLY
						 * 
						 * @author 马擎泽
						 *//*
						if (msm.getMessNodisturbCode() != null) {
							detail.setMessNodisturbCode(msm.getMessNodisturbCode());
						}
						if (msm.getMessOrderCode() != null) {
							detail.setMessOrderCode(msm.getMessOrderCode());
						}
						if (msm.getMessEffectiveTime() != null) {
							detail.setMessEffectiveTime(msm.getMessEffectiveTime());
						}
						if (msm.getOrderFailureReply() != null) {
							detail.setOrderFailureReply(msm.getOrderFailureReply());
						}
						if (msm.getOrderOvertimeReply() != null) {
							detail.setOrderOvertimeReply(msm.getOrderOvertimeReply());
						}

						ordermapper.insertChannelDetailInfo(detail);

						// insert channel status
						status.setCHANNEL_ID(msm.getChannelId());
						// ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_3(msm.getChannelId());
						AsynDataIns.insertPltCommonLog(log);

						// record process log
						syncProcessLog(act, msm.getChannelId(), 1, 0, orderDate);
					}
				} else {
					logger.error("[OrderCenter] genOrderinfoByChannelId channelId:" + msm.getChannelId()
							+ " temp table null or orderIssuedRule null.");
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_3(msm.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					// record process log
					syncProcessLog(act, msm.getChannelId(), 1, 1, orderDate);
				}
			}
		}*/

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
			String recommenedInfo, String smsTemplate, String org_range, String productId, String productName,
			String orderDate) {

		logger.info("[OrderCenter] genOrderinfoByChannelId:" + channel_id + "|" + sql + "|" + tmpTable + "|"
				+ recommenedInfo + "|" + org_range);
		// log
		PltCommonLog logdb = new PltCommonLog();
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("55");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("REVIEW_ORDER_GEN");
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

		if (productId != null && !productId.equals("")) {
			if (productId.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1)
				productId = productId.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
		}
		if (productName != null && !productName.equals("")) {
			if (productName.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1)
				productName = productName.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
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
				act.getStartDate(), act.getEndDate(), ftpRemote, act.getOrderIsConsultEndDate(), productId, productName,
				orderDate,act.getTenantId());
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
						// TODO Auto-generated catch block
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
				Integer filterBlackUserFlag = ordermapper.getFilterBlackUserFlag(ACTIVITY_SEQ_ID, act.getTenantId());

				List<String> fileList = OrderFileMannager.splitFile(origFileName, this.splitNum,
						(filterBlackUserFlag != null && filterBlackUserFlag == 1) ? true : false);
				AsynDataIns.deleteFile(origFileName);
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
			if (type == 2) {
				filter.setProductId(p.getProductId());
			}
			if (type == 3) {
				filter.setProductName(p.getProductName());
			}
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
			if (type == 2) {
				caseWhenSql.append(filterMap.get("0").getProductId());
			}
			if (type == 3) {
				caseWhenSql.append(filterMap.get("0").getProductName());
			}
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
				if (type == 2)
					caseWhenSql.append(entry.getValue().getProductId());
				if (type == 3)
					caseWhenSql.append(entry.getValue().getProductName());
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
			if (type == 2)
				caseWhenSql.append(filterMap.get("0").getProductId());
			if (type == 3)
				caseWhenSql.append(filterMap.get("0").getProductName());
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
	 * judge activity is in Accesslist
	 * 
	 * @param jsonResult
	 * @param channel
	 * @return true or false
	 */
	private Boolean isInAccessList(HashMap<String, Object> jsonResult, String channel) {

		if (null == jsonResult || "".equals(jsonResult)) {
			return false;
		}
		this.accessResultCode = (String) jsonResult.get("resultCode");
		List<String> accessResultList = (List<String>) jsonResult.get("result");
		if ("0".equals(this.accessResultCode)) {
			return false;
		} else if ("1".equals(this.accessResultCode)) {
			if (accessResultList.contains(channel)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean getActivity(String activityId, String orderDate, String tenantId) {
		boolean activityFlag = false;
		// --获取活动报文--
		String subUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_INFO_DETAIL");
		if (subUrl.equals("") || subUrl == null) {
			logger.error("[OrderCenter] Get suburl error..................!!!");
			return activityFlag;
		}
		HashMap<String, String> submap = new HashMap<String, String>();
		submap.put("activityId", activityId);
		submap.put("tenantId", tenantId);
		String respond = HttpRequest(subUrl, submap);
		if (respond.equals("ERROR")) {
			logger.error("[OrderCenter] HttpRequest for call activity detail api error.");
			return activityFlag;
		}
		logger.info("[OrderCenter] Activity detail info:" + respond);
		// log
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("Activity Begin");
		PltCommonLogIns.setBUSI_ITEM_1(activityId);
		if (respond.length() > 8000) {
			String tmp = respond.substring(0, 8000);
			PltCommonLogIns.setBUSI_ITEM_10(tmp);
		} else
			PltCommonLogIns.setBUSI_ITEM_10(respond);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);

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
			return activityFlag;
		}

		// 8、停⽤用,9、启⽤用,10、未执⾏行,11、暂停,12、暂存,13、审批中",
		// only 9 can run
		String activityState = actjson.getState();
		if (!activityState.equals("9")) {
			logger.info("[OrderCenter] Activity id=" + act_id + ":activity status!=9 no need run.");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("Activity Begin");
			PltCommonLogIns.setBUSI_ITEM_1(activityId);
			PltCommonLogIns.setBUSI_ITEM_10("activity status!=9 ,no need run.");
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			return activityFlag;
		}
		// --补漏操作，该活动需要重跑,即不需要判断--
		/*
		 * if (isActivityNeedRun(act_id, tenant_id, Integer.parseInt(flag),
		 * activityEndDate) == -1) { logger.info("[OrderCenter] Activity id=" +
		 * act_id + " no need run."); PltCommonLogIns.setSTART_TIME(new Date());
		 * PltCommonLogIns.setBUSI_CODE("Activity Begin");
		 * PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
		 * PltCommonLogIns.setBUSI_ITEM_10("no need run.");
		 * AsynDataIns.insertPltCommonLog(PltCommonLogIns); continue; }
		 */
		// record activity info
		if (!recordActivityInfo(actjson, orderDate)) {
			logger.error("[OrderCenter] Activity id=" + act_id + " get activity sequence error.");
			activityFlag = true;

		}

		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("RECORD_ACT_INFO");
		PltCommonLogIns.setBUSI_ITEM_1(activityId);
		// activity_id
		PltCommonLogIns.setBUSI_ITEM_4(act_id);
		// activity_seq_id
		PltCommonLogIns.setBUSI_ITEM_5(this.activitySeqId.toString());
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		return activityFlag;
	}

	@Override
	public void reviewActivityOrder(String activityId, String orderDate, String tenantId) {
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
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");// --得到序列号--
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("55");
		PltCommonLogIns.setSPONSOR("reviewActivity");
		logger.info("[OrderCenter] Activity id:" + activityId + " begin running...");
		// log
		PltCommonLogIns.setBUSI_ITEM_2("");
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("Activity Begin");
		PltCommonLogIns.setBUSI_ITEM_1(activityId);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		String subUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_INFO_DETAIL");
		if (subUrl.equals("") || subUrl == null) {
			logger.error("[OrderCenter] Get suburl error..................!!!");
			return;
		}
		HashMap<String, String> submap = new HashMap<String, String>();
		submap.put("activityId", activityId);
		submap.put("tenantId", tenantId);
		String respond = HttpRequest(subUrl, submap);
		if (null==respond || respond.equals("")) {
			logger.error("[OrderCenter] HttpRequest for call activity detail api null.");
			return;
		}
		if (respond.equals("ERROR")) {
			logger.error("[OrderCenter] HttpRequest for call activity detail api error.");
			return;
		}
		logger.info("[OrderCenter] Activity detail info:" + respond);
		// log

		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("Activity Begin");
		PltCommonLogIns.setBUSI_ITEM_1(activityId);
		if (respond.length() > 8000) {
			String tmp = respond.substring(0, 8000);
			PltCommonLogIns.setBUSI_ITEM_10(tmp);
		} else
			PltCommonLogIns.setBUSI_ITEM_10(respond);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);

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
			return;
		}

		// 8、停⽤用,9、启⽤用,10、未执⾏行,11、暂停,12、暂存,13、审批中",
		// only 9 can run
		String activityState = actjson.getState();
		if (!activityState.equals("9")) {
			logger.info("[OrderCenter] Activity id=" + act_id + ":activity status!=9 no need run.");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("Activity Begin");
			PltCommonLogIns.setBUSI_ITEM_1(activityId);
			PltCommonLogIns.setBUSI_ITEM_10("activity status!=9 ,no need run.");
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			return;
		}
		// --补漏操作，该活动需要重跑,即不需要判断--
		/*
		 * if (isActivityNeedRun(act_id, tenant_id, Integer.parseInt(flag),
		 * activityEndDate) == -1) { logger.info("[OrderCenter] Activity id=" +
		 * act_id + " no need run."); PltCommonLogIns.setSTART_TIME(new Date());
		 * PltCommonLogIns.setBUSI_CODE("Activity Begin");
		 * PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
		 * PltCommonLogIns.setBUSI_ITEM_10("no need run.");
		 * AsynDataIns.insertPltCommonLog(PltCommonLogIns); continue; }
		 */
		// record activity info
		if (!recordActivityInfo(actjson, orderDate)) {
			logger.error("[OrderCenter] Activity id=" + act_id + " get activity sequence error.");
			return;
		}

		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("RECORD_ACT_INFO");
		PltCommonLogIns.setBUSI_ITEM_1(activityId);
		// activity_id
		PltCommonLogIns.setBUSI_ITEM_4(act_id);
		// activity_seq_id
		PltCommonLogIns.setBUSI_ITEM_5(this.activitySeqId.toString());
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);

		// init list
		this.finishedOrderChannelList = new ArrayList<String>();
		// generate all channels order
		String ogr_range = actjson.getOrgRange();
		genAllChannelOrderInfo(actjson, ogr_range, PltCommonLogIns, orderDate);

		// update activity status
		if (this.finishedOrderChannelList.size() > 0) {
			// log
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("FILTER_ORDER_BEGIN");
			PltCommonLogIns.setBUSI_ITEM_1(activityId);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

			// order filter call api
			filterService.filterOrderStatus(this.getActivitySeqId(), tenantId, this.finishedOrderChannelList);

			// call api for filter success standard
			String filterSuccessFlag = AsynDataIns.getValueFromGlobal("FILTER_SUCCESS");
			if (filterSuccessFlag != null && filterSuccessFlag.equals("1"))
				baseTask.orderFilterSucess(this.getActivitySeqId(), actjson.getSuccessStandardPo(), tenant_id);

			// call api for reserve order
			filterService.reserveOrder(act_id, this.getActivitySeqId(), tenantId, this.finishedOrderChannelList);

			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("FILTER_ORDER_END");
			PltCommonLogIns.setBUSI_ITEM_1(activityId);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("UPDATE_ACT_STATUS,COMMIT_SUCCESS_INFO");
			PltCommonLogIns.setBUSI_ITEM_1(activityId);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			updateActivityStatus(actjson);
			// sync success info
			commitSuccessInfo(actjson);

			// log
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("FINISHED,ok");
			PltCommonLogIns.setBUSI_ITEM_1(activityId);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

			HashMap<String, Object> activityMap = new HashMap<String, Object>();
			activityMap.put("tenantId", tenant_id);
			activityMap.put("activitySeqId", getActivitySeqId());
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("COUNT_BEGIN");
			PltCommonLogIns.setBUSI_ITEM_10("");
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

			// call api
			try {
				statisticService.incrStatistic(activityMap);
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("COUNT_END");
				PltCommonLogIns.setBUSI_ITEM_2("Call statistic ok.");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[OrderCenter]" + e.getMessage());
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("COUNT_END");
				PltCommonLogIns.setBUSI_ITEM_1("count exception");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			}
			logger.info("[OrderCenter] Activity id:" + activityId + " finished, status ok.");
		} else {
			if ("0".equals(this.accessResultCode)) {
				logger.info("[OrderCenter] Activity id:" + activityId + " finished,but No Access Channel Run!");
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("FINISHED,NoChannelAccess.");
				PltCommonLogIns.setBUSI_ITEM_1(activityId);
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			} else {
				PltActivityInfo ac_obj = new PltActivityInfo();
				ac_obj.setREC_ID(getActivitySeqId());
				ac_obj.setTENANT_ID(actjson.getTenantId());
				ordermapper.cleanActivityInfo(ac_obj);
				System.out.println("[OrderCenter] Activity id:" + activityId + " finished,status:error!");
				// log
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("FINISHED,error.");
				PltCommonLogIns.setBUSI_ITEM_1(activityId);
				PltCommonLogIns.setBUSI_ITEM_10("clean activity info");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				logger.info("[OrderCenter] Activity id:" + activityId + " finished, status error.");
			}
		}

	}
// --重跑的日期，加上当前的时分秒，组成完整的时间----
	private Timestamp getReviewOrderDate(String datetime) {
		SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
		Locale locale = new Locale("zh", "CN");
		Date parsed = null;
		// --重跑的日期，加上当前的时分秒，组成完整的时间--
		try {
			parsed = f1.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd ");
		String date = f2.format(parsed);
		Date now = new Date();
		DateFormat medium2 = DateFormat.getTimeInstance( DateFormat.MEDIUM, locale);
		DateFormat df3 = DateFormat.getTimeInstance();// 只显示出时分秒
		String time = medium2.format(now);
		String dateTime = date + time;
		SimpleDateFormat f3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			parsed = f3.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new java.sql.Timestamp(parsed.getTime());

	}
	// --重跑的日期，加上当前的时分秒，组成完整的时间----
		private String getReviewOrderDateTime(String datetime) {
			SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
			Locale locale = new Locale("zh", "CN");
			Date parsed = null;
			// --重跑的日期，加上当前的时分秒，组成完整的时间--
			try {
				parsed = f1.parse(datetime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd ");
			String date = f2.format(parsed);
			Date now = new Date();
			DateFormat medium2 = DateFormat.getTimeInstance( DateFormat.MEDIUM, locale);
			DateFormat df3 = DateFormat.getTimeInstance();// 只显示出时分秒
			String time = medium2.format(now);
			String dateTime = date + time;
			
			return dateTime;

		}
//--重跑活动，获取当日接近账期--
	public String getReviewDateId(String orderDate,String tenantId) {
		String dateId = null;
		SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
		Date parsed = null;
		try {
			parsed = f1.parse(orderDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parsed);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);// 让日期加1
		SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd");
		String dateTime = f2.format(calendar.getTime());
		dateId = ordermapper.getReviewDateId(dateTime, tenantId);//从表中获取
		if(null == dateId || dateId.equals("")){
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 3);//--预测账期为当前日期的前两天--
			dateId = f1.format(calendar.getTime());
		}
		
		return dateId;
	}
	public String getReviewUserLabel(String orderDate){
		String userLabelIndex = null;
		String maxDateId = TaskBaseMapperDao.getMaxDateId();
		SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
		Date reviewDate = null;
		Date maxDate = null;
		try {
			reviewDate = f1.parse(orderDate);
			maxDate = f1.parse(maxDateId);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//重跑的日期小于等于最大账期，则说明重跑的活动的账期小于最大账期，则不能在当前的宽表里面获取数据，则宽表数据在宽表历史表里
		if(reviewDate.getTime()<=maxDate.getTime()){
			userLabelIndex = orderDate.substring(6);
		}
		
		return userLabelIndex;
	}
}
