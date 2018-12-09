package com.bonc.busi.orderschedule.service.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ActivityProvPo;
import com.bonc.busi.activity.ChannelGroupPopupPo;
import com.bonc.busi.activity.ChannelHandOfficePo;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.ChannelWebOfficePo;
import com.bonc.busi.activity.ChannelWebchatInfo;
import com.bonc.busi.activity.ChannelWoWindowPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.activity.MsmChannelPo;
import com.bonc.busi.activity.SuccessProductPo;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.orderschedule.bo.ActivityChannelStatus;
import com.bonc.busi.orderschedule.bo.ActivityDailySummary;
import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.bo.ResourceRsp;
import com.bonc.busi.orderschedule.bo.SpecialFilter;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.statistic.service.StatisticService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.base.JsonResult;
import com.bonc.common.datasource.TargetDataSource;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;

@Service("orderService")
@EnableAutoConfiguration
@ConfigurationProperties(prefix = "xcloud", ignoreUnknownFields = false)

public class OrderServiceImpl implements OrderService {
	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);
	private static final String selectField = "b.TENANT_ID," + "a.USER_ID," + "a.DEVICE_NUMBER," + "b.ORGPATH,"
			+ "0 as ORDER_STATUS," + "b.MONTH_ID as DEAL_MONTH," + "a.DATA_TYPE as SERVICE_TYPE ," + "b.CITYID,"
			+ "b.AREAID ," + "c.AREAID as USER_ORG_ID," + "c.ORGPATH as USER_PATH";

	private static final String ORDER_COLUMLIST = " (ACTIVITY_SEQ_ID,BATCH_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID"
			+ ",USER_ID,PHONE_NUMBER,ORG_PATH,ORDER_STATUS,DEAL_MONTH,SERVICE_TYPE,CITYID,AREAID"
			+ ",USER_ORG_ID,USER_PATH,BEGIN_DATE,END_DATE,LAST_UPDATE_TIME,INPUT_DATE,INVALID_DATE,";

	private String remotepath;
	private String localpath;
	private String server;
	private String passwd;
	private String user;
	private String filename;

	private boolean isGenSmsOrder;
	private boolean isGenOtherChannelOrder;
	// PLT_ACTIVITY_INFO:REC_ID
	private Integer activitySeqId;

	private String userLabelSql;
	private String userLabelColumn;
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

	public String getRemotepath() {
		return remotepath;
	}

	public void setRemotepath(String path) {
		this.remotepath = path;
	}

	public String getLocalpath() {
		return localpath;
	}

	public void setLocalpath(String path) {
		this.localpath = path;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String sr) {
		this.server = sr;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String usr) {
		this.user = usr;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String pd) {
		this.passwd = pd;
	}

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

	private static Timestamp getDate(String datetime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date parsed = null;
		try {
			parsed = format.parse(datetime);
			Calendar c = Calendar.getInstance();
			c.setTime(parsed);
			c.add(Calendar.DAY_OF_MONTH, 1);// 场景营销过期+1天
			parsed = c.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new java.sql.Timestamp(parsed.getTime());

	}

	// 场景营销活动
	private boolean recordActivityInfo(ActivityProvPo act) {
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
		// ac_obj.setACTIVITY_DIVISION(act.getActivityDivision());
		ac_obj.setACTIVITY_NAME(act.getActivityName());
		// ac_obj.setACTIVITY_THEME(act.getActivityTheme());
		// ac_obj.setACTIVITY_THEMEID(act.getActivityThemeCode());
		/*
		 * //  "activityType":"活动类型：1、周期性（按月），2、周期性（按日），3、一次性"
		 * ac_obj.setACTIVITY_TYPE(act.getActivityType());
		 */
		// 活动开始日期,不能为空"
		if (act.getStartDate() != null) {
			ac_obj.setBEGIN_DATE(getDate(act.getStartDate()));
		} else {
			throw new BoncExpection(IContants.CODE_FAIL, "StartDate is empty");
		}

		if (act.getEndDate() != null) {
			ac_obj.setEND_DATE(getDate(act.getEndDate()));
		} else {
			throw new BoncExpection(IContants.CODE_FAIL, "EndDate is empty");
		}
		//   "state":"活动状态：8、停用，9、启用，10、未执行，11、暂停,12、暂存，13、审批中"
		ac_obj.setORI_STATE(act.getState());
		ac_obj.setCREATE_NAME(act.getCreateName());

		if (act.getCreateDate() != null)
			ac_obj.setCREATE_DATE(getDate(act.getCreateDate()));
		ac_obj.setORG_RANGE(act.getOrgRange());
		ac_obj.setTENANT_ID(act.getTenantId());
		// 初始状态设置0，工单生成后改为1
		ac_obj.setACTIVITY_STATUS(1);
		ac_obj.setACTIVITY_DESC(act.getActivityDesc());
		// ac_obj.setGROUP_ID(act.getUserGroupId());
		ac_obj.setGROUP_NAME(act.getUserGroupName());
		// "urgencyLevel":"优先级：1、高，2、中，3、低",
		// ac_obj.setACTIVITY_LEVEL(act.getUrgencyLevel());
		// "parentActivity":"关联总部活动Id"
		// ac_obj.setPARENT_ACTIVITY(act.getParentActivity());
		// "policyId":"所属政策Id"
		// ac_obj.setPOLICY_ID(act.getPolicyId());
		// 数据更新周期 1、月，2、日，3、一次性",
		// if (act.getActivityType() != null)
		// ac_obj.setORDER_GEN_RULE(Integer.parseInt(act.getActivityType()));
		// 工单周期
		// if (act.getOrderCycle() != null)
		// ac_obj.setORDER_LIFE_CYCLE(Integer.parseInt(act.getOrderCycle()));
		// "工单更新规则 1、有进有出，2、覆盖",
		// if (act.getOrderUpdateRule() != null)
		// ac_obj.setORDER_UPDATE_RULE(Integer.parseInt(act.getOrderUpdateRule()));
		// 是否剔除黑名单 1、是，0、否",
		// if (act.getIsDeleteBlackUser() != null)
		// ac_obj.setFILTER_BLACKUSERLIST(Integer.parseInt(act.getIsDeleteBlackUser()));
		// 是否剔除白名单 1、是，0、否",
		// if (act.getIsDeleteWhiteUser() != null)
		// ac_obj.setFILTER_WHITEUSERLIST(Integer.parseInt(act.getIsDeleteWhiteUser()));

		// 是否同一活动分类用户剔除
		// if (act.getIsDeleteSameType() != null)
		// ac_obj.setDELETE_ACTIVITY_USER(Integer.parseInt(act.getIsDeleteSameType()));
		// 是否同一活动成功标准类型用户剔除",
		// if (act.getIsDeleteSameSuccess() != null)
		// ac_obj.setDELETE_SUCCESSRULE_USER(Integer.parseInt(act.getIsDeleteSameSuccess()));
		// 仅针对处于接触频次限制的目标客户：1、发送工单，0、不发送工单 (先不考虑字段)
		// ac_obj.setIS_SENDORDER(act.getIsSendOrder());
		// "orgLevel":"活动行政级别:1、集团，2、省级，3、市级，4、其他"
		ac_obj.setORG_LEVEL(act.getOrgLevel());
		// :"客户经理与弹窗互斥发送规则：1、各自执行，0、互斥执行"
		// ac_obj.setOTHER_CHANNEL_EXERULE(act.getOtherChannelExeRule());
		// :"短信微信互斥发送规则：1、各自重复发送，0、互斥发送
		// ac_obj.setSELF_SEND_CHANNEL_RULE(act.getSelfSendChannelRule());
		//  "strategyDesc":"策略描述",
		// ac_obj.setSTRATEGY_DESC(act.getStrategyDesc());
		// "电子渠道互斥发送规则：1、各自展示，0、展示其中一个
		// ac_obj.setECHANNEL_SHOW_RULE(act.geteChannelShowRule());
		// ac_obj.setPARENT_ACTIVITY_NAME(act.getParentActivityName());
		// ac_obj.setPARENT_ACTIVITY_STARTDATE(act.getParentActivityStartDate());
		// ac_obj.setPARENT_ACTIVITY_ENDDATE(act.getParentActivityEndDate());
		// ac_obj.setPARENT_PROVID(act.getParentProvId());
		ac_obj.setCREATOR_ORGID(act.getCreateOrgId());
		ac_obj.setCREATOR_ORG_PATH(act.getCreateOrgPath());
		// ac_obj.setUSERGROUP_FILTERCON(act.getUserGroupFilterCondition());
		// ac_obj.setLAST_ORDER_CREATE_TIME(getDateTime(getCurrentTime("yyyy-MM-dd
		// HH:mm:ss")));
		ac_obj.setACTIVITY_SOURCE("1");
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
		System.out.println("[OrderCenter] Call oracle interface get usergroup info,id:" + userGroupId);
		PltCommonLog logdb = new PltCommonLog();
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("55");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("ORDER_GEN");
		logdb.setBUSI_CODE("GET_USER_GROUP_INFO");
		logdb.setBUSI_ITEM_1(userGroupId);
		AsynDataIns.insertPltCommonLog(logdb);

		String con = getUserGroupInfo(userGroupId);
		if (con == null) {
			System.out.println("[OrderCenter] Can not get user group info.");
			return null;
		}
		// log
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("GET_USER_GROUP_INFO");
		logdb.setBUSI_ITEM_10(con);
		AsynDataIns.insertPltCommonLog(logdb);

		// multi and one orderissuleruler using same api
		// String xCloudUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_DIVIDE");
		String xCloudUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_MULT_DIVIDE");
		// 参数格式：{ rule_type_id:规则ID,rule_sql:查询条件, rule_type_sort:[{id: 字段对应关系ID
		// }]}

		// String jsonStr = "{rule_type_id:'4',tenant_id:'uni076'}";
		String resJson = "{rule_type_id:'" + orderIssuedRule + "',";
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

		System.out.println("[OrderCenter] Call resource interface request json:" + resJson);
		// log
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RESOURCE_ASSIGN");
		logdb.setBUSI_ITEM_1(resJson);
		AsynDataIns.insertPltCommonLog(logdb);

		// call resource assign api
		String resourceRep = requestResource(xCloudUrl, resJson);
		System.out.println("[OrderCenter] resource's respond=" + resourceRep);

		// log
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RESOURCE_ASSIGN");
		logdb.setBUSI_ITEM_1(resourceRep);
		AsynDataIns.insertPltCommonLog(logdb);

		// parse resourece respond ResourceRespond
		ResourceRsp res = new ResourceRsp();
		String resourceTable = null;
		String draw_business_id = null;
		if (!resourceRep.equals("null") && resourceRep != null && resourceRep.length() > 0) {
			res = JSON.parseObject(resourceRep, ResourceRsp.class);
			resourceTable = res.getTemp_table();
			draw_business_id = res.getDraw_business_id();

		} else
			return resourceTable;

		String statusUrl = AsynDataIns.getValueFromGlobal("ACTIVITY_DIVIDE_STATUS");
		// log
		logdb.setSTART_TIME(new Date());
		logdb.setBUSI_CODE("RESOURCE_ASSIGN_MULTI");
		logdb.setBUSI_ITEM_1(orderIssuedRule);
		logdb.setBUSI_ITEM_2(statusUrl);
		logdb.setBUSI_ITEM_3(draw_business_id);
		AsynDataIns.insertPltCommonLog(logdb);

		xCloudUrl = statusUrl;
		// multi orderissuerule
		while (true) {
			String multiRequest = "{\"";
			multiRequest += "draw_business_id\":";
			multiRequest += "\"";
			multiRequest += draw_business_id;
			multiRequest += "\"}";
			// call get resource assign status api
			String rspStatus = requestResource(xCloudUrl, multiRequest);
			System.out.println("[OrderCenter] Multi ruler rsp:" + rspStatus);
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
			System.out.println("[OrderCenter] Call GetStatus() running...");
		}
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

	private int downLoadFile() {
		/*
		 * RemoteServerInfo ftp_server = new RemoteServerInfo();
		 * ftp_server.setIp_address(getServer());
		 * ftp_server.setAccount(getUser());
		 * ftp_server.setPassword(getPasswd());
		 * ftp_server.setFile_path(getRemotepath());
		 * System.out.println(getLocalpath());
		 * ftp_server.setLocal_store_path(getLocalpath());
		 * ftp_server.setOrder_file(getFileName() + ".csv"); FileDownLoadThread
		 * download = new FileDownLoadThread(ftp_server); download.start();
		 */
		String FtpSrvIp = AsynDataIns.getValueFromGlobal("HDFSSRV.IP");
		String FtpUser = AsynDataIns.getValueFromGlobal("HDFSSRV.USER");
		String FtpPassowd = AsynDataIns.getValueFromGlobal("HDFSSRV.PASSWORD");
		String FtpPort = AsynDataIns.getValueFromGlobal("HDFSSRV.PORT");
		System.out.println("[OrderCenter]:" + FtpSrvIp + "|" + FtpUser + "|" + FtpPassowd + "|" + FtpPort);
		String remote = getRemotepath();
		remote = remote.replaceFirst("HDFS:", "");

		int downRtn = -1;
		int i = 0;
		while (i++ < 3) {
			int itmp = FtpTools.download(FtpSrvIp, FtpUser, FtpPassowd, Integer.parseInt(FtpPort),
					remote + getFileName() + ".csv", getLocalpath() + getFileName() + ".csv", true);
			if (itmp == 0) {
				// set return value
				downRtn = 0;
				break;
			} else {
				try {
					Thread.sleep(3000);
					System.out.println("[OrderCenter] down file error,retry.");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} // end while
		return downRtn;

	}

	private boolean loadDataToMysql(String channel_id, String tenant_id) {
		String loadSql = "LOAD DATA   local INFILE  '";
		loadSql += getLocalpath();
		loadSql += getFileName();
		loadSql += ".csv";
		// loadSql += "order_tmp_20161119202037.csv";
		loadSql += "'";
		// loadSql += " replace into table ";
		loadSql += " IGNORE into table ";
		// sms
		if (channel_id.equals("7"))
			loadSql += " PLT_ORDER_INFO_SMS fields  terminated by \'|\' ";
		else
			loadSql += " PLT_ORDER_INFO fields  terminated by \'|\' ";
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
		AsynDataIns.insertPltCommonLog(logdb);
		System.out.println("[OrderCenter] Load data to mysql begin,sql=" + loadSql + "|time:" + getSysDateTime());
		// ordermapper.loadDataToMysql(loadSql);
		// load data by mysql
		if (AsynDataIns.loadDataInMysql(loadSql, tenant_id)) {
			System.out.println("[OrderCenter] Load data finished status ok,time:" + getSysDateTime());
			return true;
		} else
			return false;

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

		// get activity rec_id
		Integer ActRecId = getActivitySeqId();
		ac_obj.setREC_ID(ActRecId);
		ordermapper.updateActivityStatus(ac_obj);

		if (this.isGenOtherChannelOrder)
			ordermapper.updateOrderFinishedStatus(ac_obj);

		if (this.isGenSmsOrder)
			ordermapper.updateOrderSmsFinishedStatus(ac_obj);

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
			if (!actmonth.equals(nowmonth))
				rtn = 0;
			break;
		case 2:// day
			String actday = create_time.substring(0, 8);
			String nowday = getCurrentTime("yyyyMMdd");
			if (!actday.equals(nowday))
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

	private String parseXCloudSQL(String sql, String activityid, int seq) {
		// gen filename
		genFileName();
		String runSQL = "/*!mycat:sql=select * FROM DIM_BOOLEAN */ export select ";
		runSQL += Integer.toString(seq);
		runSQL += " as ACTIVITY_SEQ_ID,";
		runSQL += "concat('";
		runSQL += activityid;
		runSQL += "'";
		runSQL += ", a.subs_instance_id ) as order_id,";
		runSQL += selectField + " FROM ";
		// pasee table alias
		int index = sql.indexOf("from");
		if (index == -1)
			index = sql.indexOf("FROM");
		String rsql = sql.substring(4 + index, sql.length());
		rsql = rsql.trim();

		// find alias table
		String alias = null;
		int blankIndex = rsql.indexOf(" ");
		String sqltab = rsql.substring(0, blankIndex);
		sqltab = sqltab.trim();

		int whereIndex = rsql.indexOf("where");
		if (whereIndex == -1)
			whereIndex = rsql.indexOf("WHERE");
		String wSQL = rsql.substring(whereIndex, rsql.length());

		if (whereIndex != -1) {
			alias = rsql.substring(blankIndex + 1, whereIndex);
			alias = alias.trim();
		}
		System.out.println(sqltab);
		System.out.println(alias);
		System.out.println(wSQL);

		wSQL = wSQL.replace(alias, "a");

		runSQL += sqltab;
		runSQL += "  a ";
		runSQL += "inner join WXWL_CLIENT_ASSIGN assign on assign.clientcode=a.subs_instance_id ";
		runSQL += wSQL;
		runSQL += " ATTRIBUTE(LOCATION('";
		runSQL += getRemotepath();
		runSQL += getFileName();
		runSQL += ".csv')";
		runSQL += " SEPARATOR('|'));";
		System.out.println(runSQL);

		return runSQL.replace("201606", "201608");
		// return runSQL;
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
		switch (flag) {
		case 1:// one month one time
			sql = " ,SYSDATE as BEGIN_DATE ,ADD_MONTHS(SYSDATE,";
			sql += orderCycle;
			sql += ") as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			break;
		case 2:// day
			sql = ",SYSDATE as BEGIN_DATE,SYSDATE + ";
			sql += orderCycle;
			sql += " as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			break;
		case 3:// only one time
			sql = ",SYSDATE  as BEGIN_DATE, \'2099:12:31 23:59:59\' as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			break;
		default:
			break;
		}
		System.out.println("genOrderLifeDate's SQL:" + sql);
		return sql;
	}

	private String genXCloudSql(String tabname, String activityid, int seq, String activityCycle, String orderCycle,
			String channel_id, String sqlcon, String recommenedInfo, String batchid, String org_range, String startDate,
			String endDate) {
		// get max date id
		String sql = "(SELECT  max(date_id) FROM DIM_KFPT_BAND_DATE)";
		// String maxDateId = ordermapper.getMaxDataIdFromXcloud(sql);
		// System.out.println("xcloud max data id:" + maxDateId);
		// may error
		// String maxDateId = TaskBaseMapperDao.getMaxDateId();
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
		if (recommenedInfo.indexOf("case") != -1)
			runSQL += recommenedInfo;
		else {
			runSQL += "\'";
			runSQL += recommenedInfo;
			runSQL += "\'";
		}
		runSQL += " as MARKETING_WORDS,";

		runSQL += selectField;

		// special order begin_date,end_date .-1 set activity startdate,endDate
		if (orderCycle.equals("-1")) {
			String sDate = startDate;
			String eDate = endDate;
			if (sDate == null)
				sDate = " SYSDATE ";
			else {
				String tmp = "'";
				String tmp2 = tmp;
				tmp2 += sDate;
				tmp2 += tmp;
				sDate = tmp2;
			}
			if (eDate == null)
				eDate = " \'2099:12:31 23:59:59\'";
			else {
				String tmp = "'";
				String tmp2 = tmp;
				tmp2 += eDate;
				tmp2 += tmp;
				eDate = tmp2;
			}

			String life = "," + sDate + " as BEGIN_DATE, " + eDate
					+ " as END_DATE ,SYSDATE as INPUT_DATE,SYSDATE as INVALID_DATE";
			runSQL += life;
		} else
			runSQL += genOrderLifeDate(activityCycle, orderCycle);

		runSQL += ",SYSDATE as LAST_UPDATE_TIME, ";

		// user label sql
		runSQL += this.userLabelSql;

		runSQL += " FROM  UNICOM_D_MB_DS_ALL_LABEL_INFO a ";
		runSQL += " , ";
		runSQL += tabname;
		runSQL += " b ";
		runSQL += " ,wxwl_client_assign c ";
		runSQL += "where b.clientcode = a.USER_ID ";
		runSQL += " and b.clientcode = c.clientcode ";
		runSQL += " and a.DATE_ID in";
		// runSQL += "\'" ;
		// runSQL += maxDateId;
		// runSQL += "\'";
		runSQL += sql;
		runSQL += " and ";
		runSQL += getOrgRangeSQL(org_range);
		// runSQL += " and (c.ORGPATH "
		// runSQL += " and a.DATE_ID = (select max(date_id) from
		// DIM_KFPT_BAND_DATE)";
		// runSQL += " and a.DATE_ID = ";
		// runSQL += "\'" ;
		// runSQL += maxDateId;
		// runSQL += "\'";
		if (sqlcon != null) {
			runSQL += " and ";
			runSQL += sqlcon;
		}
		runSQL += " ATTRIBUTE(LOCATION('";
		runSQL += getRemotepath();
		runSQL += getFileName();
		runSQL += ".csv')";
		runSQL += " SEPARATOR('|'))";

		return runSQL;
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
		this.isGenSmsOrder = false;
		this.isGenOtherChannelOrder = false;
		this.activitySeqId = 0;
		this.userLabelSql = AsynDataIns.getValueFromGlobal("USER_LABEL_SQL");
		this.userLabelColumn = AsynDataIns.getValueFromGlobal("USER_LABEL_COLUMN");
		;
		System.out.println("[OrderCenter] userLabelSql=" + this.userLabelSql);
		System.out.println("[OrderCenter] userLabelColumn=" + this.userLabelColumn);

		// call api to get activity list
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tenantId", tenantId);
		String rsp = HttpUtil.doGet(activity_url, map);

		System.out.println("[OrderCenter] Activity Info:" + rsp);
		// log
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("55");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR("doActivity");
		PltCommonLogIns.setBUSI_CODE("Get Activity info.");
		if (!rsp.equals("") && rsp != null && !rsp.equals("ERROR"))
			PltCommonLogIns.setBUSI_ITEM_1(rsp);
		else
			PltCommonLogIns.setBUSI_ITEM_1("[OrderCenter] Activity respond error.");

		PltCommonLogIns.setBUSI_ITEM_2(activity_url);

		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		if (rsp.equals("") || rsp == null || rsp.equals("ERROR")) {
			System.out.println("[OrderCenter] Activity respond error..................!!!");
			return;
		}

		List<String> act_list = JSON.parseArray(rsp, String.class);
		for (int i = 0; i < act_list.size(); i++) {
			System.out.println("[OrderCenter] Activity id:" + act_list.get(i) + " begin running...");
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
				System.out.println("[OrderCenter] Get suburl error..................!!!");
				continue;
			}
			HashMap<String, String> submap = new HashMap<String, String>();
			submap.put("activityId", act);
			submap.put("tenantId", "uni076");
			String respond = HttpRequest(subUrl, submap);
			if (respond.equals("ERROR")) {
				System.out.println("[OrderCenter] ERROR ERROR ERROR fo call activity detail api");
				continue;
			}

			System.out.println("[OrderCenter] Activity detail info:" + respond);
			// log
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("Activity Begin");
			PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
			PltCommonLogIns.setBUSI_ITEM_10(respond);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

			// 2.parse res to object
			ActivityProvPo actjson = JSON.parseObject(respond, ActivityProvPo.class);
			/*
			 * wechat channel program handel if (updateWechatStatus(actjson)) {
			 * PltCommonLogIns.setSTART_TIME(new Date());
			 * PltCommonLogIns.setBUSI_CODE("Activity Begin" );
			 * PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
			 * PltCommonLogIns.setBUSI_ITEM_10("Update WECHAT_STATUS.");
			 * AsynDataIns.insertPltCommonLog(PltCommonLogIns); }
			 */

			// judge run activity is running?
			// "活动类型：1、周期性（按月），2、周期性（按日），3、一次性",
			String flag = actjson.getActivityType();
			String act_id = actjson.getActivityId();
			String tenant_id = actjson.getTenantId();
			// 8、停⽤用,9、启⽤用,10、未执⾏行,11、暂停,12、暂存,13、审批中",
			// only 9 can run
			String activityState = actjson.getState();
			if (!activityState.equals("9")) {
				System.out.println("[OrderCenter] Activity id=" + act_id + ":activity status!=9 no need run.");
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("Activity Begin");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				PltCommonLogIns.setBUSI_ITEM_10("activity status!=9 ,no need run.");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				continue;
			}
			/*
			 * if (isActivityNeedRun(act_id,tenant_id,Integer.parseInt(flag)) ==
			 * -1) { System.out.println("[OrderCenter] Activity id=" +act_id +
			 * " no need run."); PltCommonLogIns.setSTART_TIME(new Date());
			 * PltCommonLogIns.setBUSI_CODE("Activity Begin" );
			 * PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
			 * PltCommonLogIns.setBUSI_ITEM_10("no need run.");
			 * AsynDataIns.insertPltCommonLog(PltCommonLogIns); continue; }
			 */
			System.out.println("[OrderCenter] Activity id=" + act_id + "  begin.");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("RECORD_ACT_INFO");
			PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			// record activity info
			recordActivityInfo(actjson);

			// get REC_ID from PLT_ACTIVITY_INFO table
			PltActivityInfo acttmp = new PltActivityInfo();
			acttmp.setACTIVITY_ID(act_id);
			acttmp.setTENANT_ID(tenant_id);
			setActivitySeqId(acttmp);
			PltCommonLogIns.setBUSI_ITEM_2(this.activitySeqId.toString());

			// sync channel info,--mofied by shenyj for moving
			// GenAllChannelOrderInfo method
			// commitChannelInfo(actjson);

			// generate all channels order
			String ogr_range = actjson.getOrgRange();
			genAllChannelOrderInfo(actjson, ogr_range, PltCommonLogIns);

			// update activity status
			if (this.isGenOtherChannelOrder || this.isGenSmsOrder) {

				// log
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("FILTER_ORDER_BEGIN");
				PltCommonLogIns.setBUSI_ITEM_1(act_list.get(i));
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);

				// order filter call api
				filterService.filterOrderStatus(this.getActivitySeqId(), tenantId, this.isGenSmsOrder);

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
				System.out.println("[OrderCenter] Activity id:" + act_list.get(i) + " finished,status:ok");

				// put item to acitivity map for counting
				// new activity map

				HashMap<String, Object> activityMap = new HashMap<String, Object>();
				activityMap.put("tenantId", tenant_id);
				activityMap.put("activitySeqId", getActivitySeqId());
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("COUNT_BGEIN");
				PltCommonLogIns.setBUSI_ITEM_1("Begin");
				PltCommonLogIns.setBUSI_ITEM_10("");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);

				// call api
				try {
					// statisticService.incrStatistic(activityMap);
					PltCommonLogIns.setSTART_TIME(new Date());
					PltCommonLogIns.setBUSI_CODE("COUNT_END");
					PltCommonLogIns.setBUSI_ITEM_1("END");
					PltCommonLogIns.setBUSI_ITEM_2("Call statistic ok.");
					AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("[OrderCenter]" + e.getMessage());
					PltCommonLogIns.setSTART_TIME(new Date());
					PltCommonLogIns.setBUSI_CODE("COUNT_END");
					PltCommonLogIns.setBUSI_ITEM_1("END");
					PltCommonLogIns.setBUSI_ITEM_2("Call statistic exception");
					AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				}

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
			}

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
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@//132.35.224.165:1521/dwtest", "usertool_hn",
					"USERTOOL_HN_123");

			stmt = conn.createStatement();
			// String sql = "insert into ddd values('18522829266')";
			String sql = "SELECT to_char(a.SQL) as SQL FROM user_tool_weights_conditions a WHERE ( a.CI_WA_ID = ";
			sql += "'";
			sql += id;
			sql += "'";
			sql += " and a.CI_WA_MAX_MONTH_ID = ( select max(b.CI_WA_MAX_MONTH_ID) from user_tool_weights_conditions b where a.CI_WA_ID = b.CI_WA_ID) and rownum<2)";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				return rs.getString("SQL");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		// return ordermapper.getUserGroupInfo(id);
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
	private void syncProcessLog(ActivityProvPo act, String channelId, int type, int status) {
		// log activity process log
		ActivityProcessLog processLog = new ActivityProcessLog();
		processLog.setACTIVITY_ID(act.getActivityId());
		processLog.setTENANT_ID(act.getTenantId());
		processLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
		processLog.setCHANNEL_ID(channelId);
		processLog.setSTATUS(status);
		if (type == 0) {
			processLog.setBEGIN_DATE(getDateTime(getCurrentTime("yyyy-MM-dd HH:mm:ss")));
			ordermapper.InsertActivityProcessLog(processLog);
		}
		if (type == 1) {
			processLog.setEND_DATE(getDateTime(getCurrentTime("yyyy-MM-dd HH:mm:ss")));
			processLog.setCHANNEL_ORDER_NUM(ordermapper.getOrderNumByChannelId(processLog));
			ordermapper.UpdateActivityProcessLog(processLog);
		}
	}

	private void genAllChannelOrderInfo(ActivityProvPo act, String org_range, PltCommonLog log) {
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

						if (genOrderinfoByChannelId(act, subChannelId, po.getFilterConditionSql(), tmpTable, "",
								org_range)) {
							this.isGenOtherChannelOrder = true;
							// insert channel detail
							po.setActivity_Id(act.getActivityId());
							po.setTenant_id(act.getTenantId());
							po.setACTIVITY_SEQ_ID(getActivitySeqId());
							// reset channel id
							po.setChannelId(subChannelId);
							ordermapper.InsertGroupPop(po);

							// insert channel status
							status.setCHANNEL_ID(subChannelId);
							ordermapper.InsertActivityChannelStatus(status);

							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_END");
							log.setBUSI_ITEM_10(subChannelId);
							AsynDataIns.insertPltCommonLog(log);

							// record process log
							syncProcessLog(act, subChannelId, 1, 0);
						}
					} else {
						System.out.println(
								"[OrderCenter] Order info temp table null or orderIssuedRule null,generate order faile.");
						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_ERROR");
						log.setBUSI_ITEM_10(po.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
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
							org_range)) {
						this.isGenOtherChannelOrder = true;
						// sync detail info
						hand.setActivityId(act.getActivityId());
						hand.setTenantId(act.getTenantId());
						hand.setACTIVITY_SEQ_ID(getActivitySeqId());
						ordermapper.InsertChannelHandOffice(hand);

						// insert channel status
						status.setCHANNEL_ID(hand.getChannelId());
						ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_10(hand.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
						// update order number
						// record process log
						syncProcessLog(act, hand.getChannelId(), 1, 0);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_10(hand.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					// record process log
					syncProcessLog(act, hand.getChannelId(), 1, 1);
					System.out.println("[OrderCenter] Order info temp table null or rule null,generate order faile.");
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
					if (genOrderinfoByChannelId(act, web.getChannelId(), web.getFilterConditionSql(), tmpTable, "",
							org_range)) {
						this.isGenOtherChannelOrder = true;
						// sync detail info
						web.setActivityId(act.getActivityId());
						web.setTenantId(act.getTenantId());
						web.setACTIVITY_SEQ_ID(getActivitySeqId());
						ordermapper.InsertChannelWebOffice(web);

						// insert channel status
						status.setCHANNEL_ID(web.getChannelId());
						ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_ITEM_10(web.getChannelId());
						log.setBUSI_CODE("ORDER_GEN_END");
						AsynDataIns.insertPltCommonLog(log);

						// record process log
						syncProcessLog(act, web.getChannelId(), 1, 0);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_10(web.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					System.out.println("[OrderCenter] Order info temp table null or rule null,generate order faile.");
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
							wechat.getChannelWebchatContent(), org_range)) {
						this.isGenOtherChannelOrder = true;
						// sync wechat channel info
						wechat.setActivityId(act.getActivityId());
						wechat.setTenantId(act.getTenantId());
						wechat.setACTIVITY_SEQ_ID(getActivitySeqId());
						ordermapper.InsertChannelWebchatInfo(wechat);

						// insert channel status
						status.setCHANNEL_ID(wechat.getChannelId());
						ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_ITEM_10(wechat.getChannelId());
						log.setBUSI_CODE("ORDER_GEN_END");
						AsynDataIns.insertPltCommonLog(log);
						// record process log
						syncProcessLog(act, wechat.getChannelId(), 1, 0);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_10(wechat.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					System.out.println("[OrderCenter] Order info temp table null or rule null,generate order faile.");
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
					if (genOrderinfoByChannelId(act, wo.getChannelId(), wo.getFilterConditionSql(), tmpTable, "",
							org_range)) {
						this.isGenOtherChannelOrder = true;
						// sync wowind channel info
						wo.setTenantId(act.getTenantId());
						wo.setActivityId(act.getActivityId());
						wo.setACTIVITY_SEQ_ID(getActivitySeqId());
						if (wo.getChannelId() != null)
							ordermapper.InsertChannelWebWoWindow(wo);

						// insert channel status
						status.setCHANNEL_ID(wo.getChannelId());
						ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_10(wo.getChannelId());
						AsynDataIns.insertPltCommonLog(log);
						// record process log
						syncProcessLog(act, wo.getChannelId(), 1, 0);
					}
				} else {
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_10(wo.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
					System.out.println("[OrderCenter] Order info temp table null or rule null,generate order faile.");
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
						String caseWhenSql = getSpecialFilterMap(front.getChannelSpecialFilterList());
						if (genOrderinfoByChannelId(act, front.getChannelId(), front.getFilterConditionSql(), tmpTable,
								caseWhenSql, org_range)) {
							this.isGenOtherChannelOrder = true;
							// sync front line channel detail info
							front.setTenantId(act.getTenantId());
							front.setActivityId(act.getActivityId());
							front.setACTIVITY_SEQ_ID(getActivitySeqId());
							ordermapper.InsertChannelFrontline(front);
							// insert channel status
							status.setCHANNEL_ID(front.getChannelId());
							ordermapper.InsertActivityChannelStatus(status);

							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_END");
							log.setBUSI_ITEM_2(caseWhenSql);
							log.setBUSI_ITEM_10(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 0);
						} else {
							System.out.println(
									"[OrderCenter] Order info temp table null or rule null,generate order faile.");
							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_ERROR");
							log.setBUSI_ITEM_2(caseWhenSql);
							log.setBUSI_ITEM_10(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);

							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 1);
						} // end if else gen order ok

					} else { // no specialFilter List
						if (genOrderinfoByChannelId(act, front.getChannelId(), front.getFilterConditionSql(), tmpTable,
								"", org_range)) {
							this.isGenOtherChannelOrder = true;
							// sync front line channel info
							front.setTenantId(act.getTenantId());
							front.setActivityId(act.getActivityId());
							front.setACTIVITY_SEQ_ID(getActivitySeqId());
							ordermapper.InsertChannelFrontline(front);
							// insert channel status
							status.setCHANNEL_ID(front.getChannelId());
							ordermapper.InsertActivityChannelStatus(status);
							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_END");
							log.setBUSI_ITEM_10(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);
							// record process log
							syncProcessLog(act, front.getChannelId(), 1, 0);

						} else {
							System.out.println(
									"[OrderCenter] Order info temp table null or rule null,generate order faile.");
							// log
							log.setSTART_TIME(new Date());
							log.setBUSI_CODE("ORDER_GEN_ERROR");
							log.setBUSI_ITEM_10(front.getChannelId());
							AsynDataIns.insertPltCommonLog(log);

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
					System.out.println("[OrderCenter] Order info temp table null,generate order faile.");

					return;
				}
				if (tmpTable != null && orderIssuedRule != null) {
					// record process log
					syncProcessLog(act, msm.getChannelId(), 0, 0);
					if (genOrderinfoByChannelId(act, msm.getChannelId(), msm.getFilterConditionSql(), tmpTable, "",
							org_range)) {
						this.isGenSmsOrder = true;
						// sync sms channel detail info
						msm.setTenantId(act.getTenantId());
						msm.setActivityId(act.getActivityId());
						msm.setACTIVITY_SEQ_ID(getActivitySeqId());
						ordermapper.InsertChannelMsm(msm);
						// insert channel status
						status.setCHANNEL_ID(msm.getChannelId());
						ordermapper.InsertActivityChannelStatus(status);

						// log
						log.setSTART_TIME(new Date());
						log.setBUSI_CODE("ORDER_GEN_END");
						log.setBUSI_ITEM_10(msm.getChannelId());
						AsynDataIns.insertPltCommonLog(log);

						// record process log
						syncProcessLog(act, msm.getChannelId(), 1, 0);
					}
				} else {
					System.out.println("[OrderCenter] Order info temp table null or rule null,generate order faile.");
					// log
					log.setSTART_TIME(new Date());
					log.setBUSI_CODE("ORDER_GEN_ERROR");
					log.setBUSI_ITEM_10(msm.getChannelId());
					AsynDataIns.insertPltCommonLog(log);
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

	private boolean genOrderinfoByChannelId(ActivityProvPo act, String channel_id, String sql, String tmpTable,
			String recommenedInfo, String org_range) {
		System.out.println("[OrderCenter] genOrderinfoByChannelId:" + channel_id + "|" + sql + "|" + tmpTable + "|"
				+ recommenedInfo + "|" + org_range);
		// log
		PltCommonLog logdb = new PltCommonLog();
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("55");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("ORDER_GEN");
		logdb.setBUSI_CODE("PREPARE_XCLOUD_SQL");

		boolean bRtn = false;

		PltActivityInfo acttmp = new PltActivityInfo();
		acttmp.setACTIVITY_ID(act.getActivityId());
		acttmp.setTENANT_ID(act.getTenantId());

		// get activity rec_id
		Integer ACTIVITY_SEQ_ID = getActivitySeqId();
		int batchid = AsynDataIns.getSequence("PLT_ORDER_INFO.BATCH_ID");
		// judge orderCycle
		String orderCycle = act.getOrderCycle();
		if (orderCycle == null)
			orderCycle = "1";

		// special filter condition (UNICOM_D_MB_DS_ALL_LABEL_INFO)
		String sqlCondition = sql;
		if (sqlCondition != null) {
			if (sqlCondition.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1)
				sqlCondition = sqlCondition.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
		}
		if (recommenedInfo != null) {
			if (recommenedInfo.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1)
				recommenedInfo = recommenedInfo.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
		}

		String xCloudSql = genXCloudSql(tmpTable, "", ACTIVITY_SEQ_ID, act.getActivityType(),
				// act.getOrderCycle(),
				orderCycle, // for filter null value
				channel_id, sqlCondition, recommenedInfo, Integer.toString(batchid), org_range, act.getStartDate(),
				act.getEndDate());

		logdb.setBUSI_ITEM_1(xCloudSql);
		AsynDataIns.insertPltCommonLog(logdb);

		System.out.println("[OrderCenter] Create order list,runsql:" + xCloudSql);

		// export xcloud data by calling api
		/*
		 * if ( !AsynDataIns.getDataFromXcloud(xCloudSql)) { System.out.println(
		 * "[OrderCenter] Generate order file error.");
		 * 
		 * }
		 */
		JsonResult JsonResultIns = AsynDataIns.execDdlOnXcloud(xCloudSql);
		if ("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false) {

			logdb.setBUSI_CODE("RUN_XCLOUD_SQL_ERROR");
			logdb.setBUSI_DESC("export from xcloud error.");
			logdb.setBUSI_ITEM_1(JsonResultIns.getMessage());
			AsynDataIns.insertPltCommonLog(logdb);
			logdb.setBUSI_ITEM_1(null);

			System.out.println("[OrderCenter] Generate order file error.");
		} else {
			logdb.setSTART_TIME(new Date());
			logdb.setBUSI_CODE("RUN_XCLOUD_SQL_OK");
			AsynDataIns.insertPltCommonLog(logdb);
			bRtn = true;

			String remote = getRemotepath();
			remote = remote.replaceFirst("HDFS:", "");

			// download order file
			if (downLoadFile() == 0) {
				logdb.setSTART_TIME(new Date());
				logdb.setBUSI_CODE("DOWNLOAD");
				logdb.setBUSI_ITEM_1("Down load file" + remote + getFileName() + ".csv" + " ok.");
				AsynDataIns.insertPltCommonLog(logdb);
				if (loadDataToMysql(channel_id, act.getTenantId())) {
					bRtn = true;
					logdb.setSTART_TIME(new Date());
					logdb.setBUSI_CODE("LOAD_MYSQL");
					logdb.setBUSI_ITEM_1("load data mysql ok");
					AsynDataIns.insertPltCommonLog(logdb);
				} else {
					bRtn = false;
					logdb.setSTART_TIME(new Date());
					logdb.setBUSI_CODE("LOAD_MYSQL");
					logdb.setBUSI_ITEM_1("load data mysql error.");
					AsynDataIns.insertPltCommonLog(logdb);
				}
			} // download ok
			else {
				bRtn = false;
				// log
				logdb.setBUSI_ITEM_1("Down load file" + remote + getFileName() + ".csv" + " error.");
				AsynDataIns.insertPltCommonLog(logdb);
			}

		} // end export xcloud ok

		System.out.println("[OrderCenter] genOrderinfoByChannelId finished:" + channel_id + "|" + sql + "|" + tmpTable
				+ "|" + recommenedInfo + "|" + org_range);
		return bRtn;

	}

	private String getSpecialFilterMap(List<ChannelSpecialFilterPo> specialFilterList) {
		// init tree map
		Map<String, SpecialFilter> filterMap = new TreeMap<String, SpecialFilter>();
		for (int i = 0; i < specialFilterList.size(); i++) {
			ChannelSpecialFilterPo p = specialFilterList.get(i);
			SpecialFilter filter = new SpecialFilter();
			filter.setSql(p.getFilterConditionSql());
			filter.setRecommend(p.getRecommenedInfo());
			filterMap.put(p.getOrd(), filter);
		}
		// generate case sql
		StringBuilder caseWhenSql = new StringBuilder();
		String tempCon = "";
		if (filterMap.size() == 1) {
			/*
			 * caseWhenSql.append("( case when " + filterMap.get("0").getSql());
			 * caseWhenSql.append(" then " + "'" +
			 * filterMap.get("0").getRecommend() + "'" + " end)  ") ;
			 */
			// no condition sql so return recommend info
			caseWhenSql.append(filterMap.get("0").getRecommend());
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
				caseWhenSql.append(" then " + "'" + entry.getValue().getRecommend() + "' ");

				tempCon += getReverseCondition(entry.getValue().getSql(), ++j);

			}
			caseWhenSql.append(" else " + "'" + filterMap.get("0").getRecommend() + "'");
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
	public List<Integer> getInvalidActivitySeqId(String tenantId) {
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

	// 场景营销活动
	public boolean addActivityChannelDetail(ActivityProvPo actjson) {
		boolean flag = false;
		PltActivityChannelDetail detail = new PltActivityChannelDetail();
		detail.setCHANN_ID("7");
		detail.setTENANT_ID(actjson.getTenantId());
		detail.setACTIVITY_SEQ_ID(getActivitySeqId());
		ordermapper.insertChannelDetailInfo(detail);
		flag = true;
		return flag;
	}

	// 场景营销活动
	@Override
	public JsonResult startSceneMarketActivity(String response) {
		JsonResult JsonResultIns = new JsonResult();
		ActivityProvPo actjson = new ActivityProvPo();
		try {
			actjson = JSON.parseObject(response, ActivityProvPo.class);
		} catch (Exception e) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("错误的JSON请求数据！");
			return JsonResultIns;
		}

		// judge run activity is running?
		String act_id = actjson.getActivityId();
		String tenant_id = actjson.getTenantId();

		if (act_id == null || tenant_id == null) {
			logger.error("[OrderCenter] activityType,ActivityId,tenantId param error!");
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("参数ActivityId或TenantId为空");
			return JsonResultIns;
		}
		// record activity info
		if (!recordActivityInfo(actjson)) {
			logger.error("[OrderCenter] Activity id=" + act_id + " get activity sequence error.");
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("活动启用失败");
			return JsonResultIns;
		}
		// sync success info
		try {
			commitSuccessInfo(actjson);
		} catch (Exception e) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("活动成功标准，产品列表入库失败");
		}
		// record activity info
		if (!addActivityChannelDetail(actjson)) {
			logger.error("[OrderCenter] Activity id=" + act_id + " get activityChannelDetail sequence error.");
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("活动明细表录入失败");
			return JsonResultIns;
		}

		JsonResultIns.setCode("1");
		JsonResultIns.setMessage("活动启用成功");
		return JsonResultIns;

	}
}
