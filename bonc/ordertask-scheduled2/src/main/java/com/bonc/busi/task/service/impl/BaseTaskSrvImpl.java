package com.bonc.busi.task.service.impl;
/*
 * @desc:基础任务实现
 * @author:曾定勇
 * @time:2016-11-26
 */

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.security.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;

import com.bonc.busi.task.service.impl.OrderSucessFunc;
import com.bonc.common.thread.PoolThread;
import com.bonc.common.thread.ThreadPoolManage;
import com.bonc.utils.HttpUtil;

import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import com.bonc.busi.task.base.*;
import com.bonc.busi.task.service.BaseTaskSrv;
import com.bonc.busi.task.mapper.*;
import com.bonc.busi.task.bo.*;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.common.thread.ThreadBaseFunction;
import com.bonc.busi.task.instance.OrderFilterSucess;
import com.bonc.busi.statistic.service.StatisticService;

@Service()
// @Transactional
public class BaseTaskSrvImpl implements BaseTaskSrv {

	private final static Logger log = LoggerFactory.getLogger(BaseTaskSrvImpl.class);

	@Autowired
	private StatisticService StatisticServiceIns;

	@Autowired
	private OrderSucessFunc OrderSucessFuncIns;
	// @Autowired
	// private OrderCheckSucessFunc OrderCheckSucessFuncIns;
	@Autowired
	private BusiTools BusiToolsIns;
	@Autowired
	private BaseMapper TaskBaseMapperDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	// @Autowired
	// private PltActivityInfoDao TaskActivityDao;

	// @Autowired private ParrelHandleFunc ParrelHandleFunc;
	@Autowired
	private InnerDbQuerySrv InnerDbQuerySrv;
	@Autowired
	private UpdateUserId UpdateUserIdIns;

	/*
	 * 工单生成后过滤已经成功的工单
	 */
	public boolean orderFilterSucess(int activitySeqId, SuccessStandardPo sucessCon, String tenantId) {
		// --- 定义2个线程，无数据退出 ---
		int iThreadNum = 2;
		String strTmp = BusiToolsIns.getValueFromGlobal("ORDERSUCESSCHECK.THREADSNUM");
		if (strTmp != null) {
			iThreadNum = Integer.parseInt(strTmp);
			if (iThreadNum < 2)
				iThreadNum = 2;
		}
		log.info("thread num:{}", strTmp);

		// --- 查询各个表当前活动下的工单量，并按工单量大小排序 ---
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("PLT_ORDER_INFO");
		tableNames.add("PLT_ORDER_INFO_SMS");
		tableNames.add("PLT_ORDER_INFO_WEIXIN");
		tableNames.add("PLT_ORDER_INFO_POPWIN");
		tableNames.add("PLT_ORDER_INFO_ONE");
		List<HashMap<String, Object>> countRecs = new ArrayList<HashMap<String, Object>>();
		for (String tableName : tableNames) {
			HashMap<String, Object> countRec = TaskBaseMapperDao.getCountForActivity(tableName, activitySeqId,
					tenantId);
			if (!"0".equals(countRec.get("num") + "")) {
				countRecs.add(countRec);
			}
		}

		for (int j = countRecs.size() - 1; j >= 0; j--) {
			for (int i = 0; i < j; i++) {
				if (Integer.parseInt(String.valueOf(countRecs.get(i).get("num"))) < Integer
						.parseInt(String.valueOf(countRecs.get(i + 1).get("num")))) {
					HashMap<String, Object> temp = countRecs.get(i);
					countRecs.set(i, countRecs.get(i + 1));
					countRecs.set(i + 1, temp);
				}
			}
		}
		log.info("----根据表工单量排序后----->" + countRecs);

		if (countRecs.size() <= 0) {
			log.info("无工单");
			return false;
		}

		// --- 执行第一张表 ---
		OrderFilterSucess OrderFilterSucessIns = new OrderFilterSucess();
		OrderFilterSucessIns.setActivitySeqid(activitySeqId);
		OrderFilterSucessIns.setTenantId(tenantId);
		List<HashMap<String, Object>> countRecFist = new ArrayList<HashMap<String, Object>>();
		countRecFist.add(countRecs.get(0));
		OrderFilterSucessIns.setTableOrder(countRecFist);
		OrderFilterSucessIns.setTable(countRecs);
		OrderFilterSucessIns.setSuccessStandardPo(sucessCon);
		ParallelManage ParallelManageIns = new ParallelManage(OrderFilterSucessIns, iThreadNum);
		ParallelManageIns.setMaxWaitSeconds(1200);
		ParallelManageIns.execute();

		// --- 执行其他表时 ---
		OrderFilterSucessIns = null;
		OrderFilterSucessIns = new OrderFilterSucess();
		OrderFilterSucessIns.setActivitySeqid(activitySeqId);
		OrderFilterSucessIns.setTenantId(tenantId);
		OrderFilterSucessIns.setTable(countRecs);
		List<HashMap<String, Object>> countRecOthers = new ArrayList<HashMap<String, Object>>();
		for (int x = 0; x < countRecs.size(); x++) {
			if (x != 0) {
				countRecOthers.add(countRecs.get(x));
			}
		}
		OrderFilterSucessIns.setTableOrder(countRecOthers);
		OrderFilterSucessIns.setSuccessStandardPo(sucessCon);
		ParallelManageIns = new ParallelManage(OrderFilterSucessIns, iThreadNum);
		ParallelManageIns.setMaxWaitSeconds(1200);
		if (countRecOthers.size() > 0) {
			ParallelManageIns.execute();
		}

		return true;
	}

	/*
	 * 测试MYCAT
	 */
	// @Transactional
	public int testMycat() {

		// --- 更新统计 ---
		StringBuilder sb = new StringBuilder();
		// --- 提取有效租户信息 ---
		List<Map<String, Object>> listTenantInfo = null; // --- 有效租户信息 ---
		listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if (listTenantInfo == null || listTenantInfo.size() == 0) { // ---
																	// 无有效租户纪录
																	// ---
			return 0;
		}
		for (Map<String, Object> item : listTenantInfo) {
			String strTenantId = (String) item.get("TENANT_ID");
			sb.setLength(0);
			sb.append("SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_STATUS !='2' ");
			sb.append(" AND TENANT_ID ='");
			sb.append(strTenantId);
			sb.append("'");
			long iActivitySeqId = 0;
			List<Map<String, Object>> listResult = jdbcTemplate.queryForList(sb.toString());
			for (Map<String, Object> rec : listResult) {
				iActivitySeqId = (Long) rec.get("REC_ID");
				log.info("seqid={}", iActivitySeqId);
				// --- begin to call statics ---
				// StatisticServiceIns.statisticBench(strTenantId,
				// String.valueOf(iActivitySeqId));
			}
		}
		/*
		 * try{
		 * 
		 * List<Map<String, Object>> listTenantInfo =
		 * BusiToolsIns.getValidTenantInfo(); if(listTenantInfo == null ||
		 * listTenantInfo.size() == 0){ log.warn("没有找到有效的租户信息"); return -1; }
		 * AsynUserLabelIns.handleDataDirect(listTenantInfo.get(0));
		 */

		// String strSqlData = "SELECT 'TTTTTENANT_ID' TENANT_ID, PROV_ID ,
		// AREA_NO AREA_ID, CITY_ID, USER_ID, "
		// + "DEVICE_NUMBER, REPLACE(REPLACE(CUST_NAME,'\\','/'),'\"','')
		// CUST_NAME, case when NUM_21 is null then '0' else
		// to_char(round(NUM_21*100)) end BALANCE_FEE, case when KD_BALANCE_FEE
		// is null then '0' else to_char(round(KD_BALANCE_FEE*100)) end
		// KD_BALANCE_FEE, BA_S_01 USER_STATUS, DATA_TYPE USER_TYPE,
		// KD_PRODUCT_ID MB_PACKAGE_ID, KD_PRODUCT_ID KD_PACKAGE_ID, NUM_24
		// MB_FIRST_OWE_MONTH, KD_CHR_2 KD_FIRST_OWE_MONTH, case when OWE_FEE>0
		// then '是' else '否' end OWE_FLAG, case when OWE_FEE is null then '0'
		// else to_char(round(OWE_FEE*100)) end MB_OWE_FEE, case when KD_NUM_2
		// is null then '0' else to_char(round(KD_NUM_2*100)) end KD_OWE_FEE,
		// BA_G_02 MB_OWE_MONTHS, case when ONLINE_DUR_LVL=0 or ONLINE_DUR_LVL
		// is null then LAST3_TOTAL_FEE when ONLINE_DUR_LVL >=3 then
		// ROUND(LAST3_TOTAL_FEE/3,2) when ONLINE_DUR_LVL <3 then
		// ROUND(LAST3_TOTAL_FEE/ONLINE_DUR_LVL,2) end MB_ARPU, W3_PROD_ID_1
		// ELECCHANNEL_FLAG, case when RENT_FEE > 96 then '是' else '否' end
		// HIGH_96_FLAG, case when RENT_FEE is null then '0' else
		// to_char(round(RENT_FEE*100)) end RENT_FEE, PR_R_01 MB_MIX_FLAG, case
		// WHEN CHR_46 is null then KD_CUST_TYPE end MB_CUST_TYPE,case when
		// ONLINE_DUR_LVL is null then 0 else ONLINE_DUR_LVL end MB_ONLINE_DUR,
		// JIAZHI_FLAG MB_VALUE_LEVEL, PR_B_05 MB_PRODUCT_TYPE, CH_C_03
		// MB_NETIN_CHANNEL, ACTIVITY_TYPE MB_AGREEMENT_TYPE,
		// REPLACE(REPLACE(CHR_14,'\','/'),'"','') MB_AGREEMENT_NAME, CHR_15
		// MB_AGREEMENT_BEGIN_TIME, EXP_DATE MB_AGREEMENT_END_TIME, NUM_1
		// MB_AGREEMENT_REST_MONTHS, REPLACE(REPLACE(ba_n_11,'\\','/'),'\"','')
		// CONTACT_TELEPHONE_NO, SERVICE_TYPE MB_NET_TYPE, case when LAST3_FLUX
		// is null then 0 when ONLINE_DUR_LVL=0 or ONLINE_DUR_LVL is null then
		// LAST3_FLUX when ONLINE_DUR_LVL>=3 then ROUND(LAST3_FLUX/3,0) when
		// ONLINE_DUR_LVL<3 then ROUND(LAST3_FLUX/ONLINE_DUR_LVL,0) end
		// MB_AVERAGE_FLOW, TE_N_02 MB_TERMINAL_BRAND, TE_N_04 MB_NET_MODE,
		// KD_DQ_MONTH KD_AGREEMENT_END_TIME, KD_IS_KDDQXQ
		// KD_CONTINUE_AGREEMENT_FLAG, KD_SERVICE_SUB_TYPE KD_SUB_PACKAGE_NAME,
		// KD_COMB_SUB_TYPE KD_MIX_TYPE, KD_COMB_START_DATE KD_MIX_BEGIN_TIME,
		// KD_MOBILE_NUMBER1 KD_MIX_MOBILE_NO, KD_COMB_FX_NUMBER KD_MIX_FIX_NO,
		// KD_BILLING_BANDWIDTH KD_CUR_RATE, case when KD_INNET_LENGTH is null
		// then 0 else KD_INNET_LENGTH end KD_NETIN_MONTHS ,
		// REPLACE(REPLACE(KD_MANAGER_NAME,'\','/'),'"','') KD_OWNER_AREA,
		// REPLACE(REPLACE(KD_ADDR_SIX_NAME,'\','/'),'"','') KD_ADDR_SIX_NAME,
		// CH_C_03 KD_CHANNEL_ID, KD_XQ_DATE KD_CONTINUE_AGREEMENT_TIME,
		// REPLACE(REPLACE(KD_MANAGER_NAME,'\','/'),'"','') KD_CUST_MANAGER,
		// IS_SIM MB_SIM_TYPE, TE_N_04 MB_CHG_MACHINE_FLAG, BA_S_04
		// REAL_NAME_FLAG, CHR_17 MB_ADDCARD_TYPE, TYPE_HYBL_CUST_TYPE
		// MB_4G_NET_FLAG,CHR_30,
		// PAY_MODE,PRODUCT_BASE_CLASS,ACTIVITY_TYPE,WENDING_FLAG,case when
		// ACCT_FEE is null then '0' else to_char(round(ACCT_FEE*100)) end
		// ACCT_FEE,CO_B_01,C_100,PRODUCT_CLASS FROM
		// UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE DATE_ID ='DDDDDATEID' AND PROV_ID
		// = 'PPPPPROV_ID' ";
		// BusiToolsIns.XcloudToMysql(sqlData, TenantId, MysqlTableName,
		// MysqlCols);
		// log.info("从行云出库begin");
		/// *
		// try{
		// jdbcTemplate.execute("/*!mycat:sql=SELECT max(date_id) DATE_ID FROM
		// DIM_KFPT_BAND_DATE */"
		// + "EXPORT select is_sim,USER_ID from UNICOM_D_MB_DS_ALL_LABEL_INFO
		// where DEVICE_NUMBER IN"
		// +"('15538337157','15538216073','13298177239','13253584580','13140144999')
		// "
		// + "ATTRIBUTE(LOCATION('HDFS:/files/prov/076/dataasyn/test.csv')
		// SEPARATOR('0x09'))");
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// */
		/*
		 * log.info("从行云出库end"); log.info("查询ORACLEbegin");
		 * jdbcTemplate.queryForList(
		 * "select count(*) from user_tool_weights_conditions");
		 * log.info("查询ORACLEend"); log.info("查询MYSQLbegin");
		 * jdbcTemplate.queryForList("select count(*) from TENANT_INFO");
		 * log.info("查询MYSQLend");
		 */
		/*
		 * 更新工单用户信息不需要了 UpdateOrderUserInfo UpdateOrderUserInfoIns = new
		 * UpdateOrderUserInfo(); ParallelManage ParallelManageIns = new
		 * ParallelManage(UpdateOrderUserInfoIns); ParallelManageIns.execute();
		 */
		// UpdateOrderUserInfoIns.handleData(null);
		/*
		 * log.info("当前时间：{}"+new Date()); // log.info("休息10秒"); //
		 * Thread.sleep(10000); jdbcTemplate.execute("TRUNCATE TABLE DD_MYSQL");
		 * // log.info("休息10秒"); // Thread.sleep(10000); List<Map<String,
		 * Object>> listSqlData = jdbcTemplate.queryForList(
		 * "SELECT * FROM DD_MYSQL"); //log.info("休息10秒"); //
		 * Thread.sleep(10000); if(listSqlData != null){ for(Map<String, Object>
		 * item:listSqlData){ log.info(item.toString()); } }
		 * jdbcTemplate.execute("INSERT INTO DD_MYSQL VALUES('13701091002')");
		 * // log.info("休息30秒"); // Thread.sleep(30000); listSqlData =
		 * jdbcTemplate.queryForList("SELECT * FROM DD_MYSQL"); if(listSqlData
		 * != null){ for(Map<String, Object> item:listSqlData){
		 * log.info(item.toString()); } } jdbcTemplate.execute(
		 * "INSERT INTO DD_MYSQL VALUES('13701091002')");
		 */
		// log.info("休息180秒");
		// Thread.sleep(180000);
		/*
		 * listSqlData = jdbcTemplate.queryForList("SELECT * FROM DD_MYSQL");
		 * if(listSqlData != null){ for(Map<String, Object> item:listSqlData){
		 * log.info(item.toString()); } } jdbcTemplate.execute(
		 * "INSERT INTO DD_MYSQL VALUES('13701091002')"); log.info("休息400秒");
		 * Thread.sleep(400000); listSqlData = jdbcTemplate.queryForList(
		 * "SELECT * FROM DD_MYSQL"); if(listSqlData != null){ for(Map<String,
		 * Object> item:listSqlData){ log.info(item.toString()); } }
		 */
		/*
		 * }catch(Exception e){ e.printStackTrace(); } log.info("结束:{}",new
		 * Date());
		 */
		return 0;
	}

	/*
	 * 得到工单数量
	 */
	// @Transactional
	public int getOrderNum() {
		String partFlag = BusiToolsIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");

		Date beginDate = new Date();
		Map<String, Object> mapPara = new HashMap<String, Object>();
		StringBuilder sbTmp = new StringBuilder();
		sbTmp.append("SELECT min(o.REC_ID) MINID,MAX(o.REC_ID) MAXID FROM PLT_ACTIVITY_INFO a,");
		sbTmp.append("PLT_ORDER_INFO o WHERE   1 = 1   AND a.TENANT_ID = 'uni076'    AND  ");
		sbTmp.append("o.TENANT_ID = 'uni076'   AND o.CHANNEL_ID = '5'   AND o.SERVICE_TYPE = '0'  ");
		sbTmp.append("  AND a.REC_ID = 61  AND o.ORDER_STATUS = '5'     AND o.ORG_PATH LIKE '/root%'  ");
		sbTmp.append("AND o.ACTIVITY_SEQ_ID = a.REC_ID    AND o.CONTACT_CODE NOT IN (101, 102, 103, 121) ");
		sbTmp.append("AND o.CHANNEL_STATUS = '0' ");
		mapPara.put("FIRSTSQL", sbTmp.toString());
		mapPara.put("KEYID", "o.REC_ID");
		sbTmp.setLength(0);
		sbTmp.append("SELECT COUNT(*) RECCOUNT FROM PLT_ACTIVITY_INFO a,PLT_USER_LABEL u,");
		sbTmp.append("PLT_ORDER_INFO o WHERE   1 = 1   AND a.TENANT_ID = 'uni076'    AND  ");
		sbTmp.append("o.TENANT_ID = 'uni076'   AND o.CHANNEL_ID = '5'   AND o.SERVICE_TYPE = '0'  ");
		sbTmp.append("  AND a.REC_ID = 61  AND o.ORDER_STATUS = '5'     AND o.ORG_PATH LIKE '/root%'  ");
		sbTmp.append("AND o.ACTIVITY_SEQ_ID = a.REC_ID    AND o.CONTACT_CODE NOT IN (101, 102, 103, 121) ");
		sbTmp.append("AND o.CHANNEL_STATUS = '0' ");
		sbTmp.append(" AND u.TENANT_ID = 'uni076' AND PARTITION_FLAG =" + partFlag + " AND o.USER_ID = u.USER_ID ");
		mapPara.put("SECONDSQL", sbTmp.toString());
		InnerDbQuerySrv.query(mapPara);
		List<Map<String, Object>> listSqlData = (List<Map<String, Object>>) mapPara.get("DATA");
		long lTotalNum = 0;
		for (Map<String, Object> item : listSqlData) {
			log.info(item.toString());
			lTotalNum += (long) item.get("RECCOUNT");
		}
		Date dateEnd = new Date();
		log.info("totalnum={},时间：{}", lTotalNum, dateEnd.getTime() - beginDate.getTime());

		return (int) lTotalNum;
	}

	public String getChannelID(String tableName) {

		SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		String OrderInsertTime; // -------获取当前月份，短信表入历史，判断短信发送完毕后 是几月就入几月的表----
		OrderInsertTime = sdfYm.format(new Date()).substring(5, 7);

		String channelId = null;
		if (tableName == "PLT_ORDER_INFO") {
			channelId = "('5')";
		} else if (tableName == "PLT_ORDER_INFO_WEIXIN") {
			channelId = "('11')";
		} else if (tableName == "PLT_ORDER_INFO_POPWIN") {
			channelId = "('81','82')";
		} else if (tableName == "PLT_ORDER_INFO_ONE") {
			channelId = "('1','2','9')";
		} else if (tableName.equalsIgnoreCase("PLT_ORDER_INFO_SMS_HIS_" + OrderInsertTime)) {
			channelId = "('7')";
		} else if(tableName == "PLT_ORDER_INFO_CALL"){
 			channelId = "('12')";
		} else {
			log.info("包含各渠道，不参与渠道");
		}
		return channelId;
	}

	// --- 工单检查（重新REVIEW）----
	// --- 每天22:30执行工单检查 ---
	 //@Scheduled(cron = "0 30 22 * * ?") 
	public void orderCheck() {
		// --- 定义一个开关 ---
		String switchPut = null;
		switchPut = BusiToolsIns.getValueFromGlobal("ORDERCHECK.SUCCESS.SWITCH");
		if ("1".equals(switchPut))
			return;

		// --- 定义2个线程，无数据退出 ---
		int iThreadNum = 2;
		String strTmp = BusiToolsIns.getValueFromGlobal("ORDERSUCESSCHECK.THREADSNUM");
		if (strTmp != null) {
			iThreadNum = Integer.parseInt(strTmp);
			if (iThreadNum < 2)
				iThreadNum = 2;
		}

		String tenantId = SysVars.getTenantId();
//		String tenantId = "uni076";
//		String provId = "076";
		List<Map<String, Object>> listTenantInfo = new ArrayList<Map<String,Object>>();
		Map<String, Object > tenantInfo = new HashMap<String, Object>();
		tenantInfo.put("TENANT_ID", tenantId);
		
		listTenantInfo.add(tenantInfo);
		
//		
//		// --- 提取有效租户信息 ---
//		List<Map<String, Object>> listTenantInfo = null; // --- 有效租户信息 ---
//		listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if (listTenantInfo == null || listTenantInfo.size() == 0) { // ---
																	// 无有效租户纪录
																	// ---
			return;
		}
//		String tenantId = (String) listTenantInfo.get(0).get("TENANT_ID");



		SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		String OrderInsertTime; // -------获取当前月份，短信表入历史，判断短信发送完毕后 是几月就入几月的表----
		OrderInsertTime = sdfYm.format(new Date()).substring(5, 7);

		// --- 准备好需要跑成功检查的表
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("PLT_ORDER_INFO");
		tableNames.add("PLT_ORDER_INFO_WEIXIN");
		tableNames.add("PLT_ORDER_INFO_POPWIN");
		tableNames.add("PLT_ORDER_INFO_ONE");
		tableNames.add("PLT_ORDER_INFO_SMS_HIS_" + OrderInsertTime);
		tableNames.add("PLT_ORDER_INFO_CALL");

		// 1.先查询出工单量最多的表，类似短信类型的表统计表中无记载，暂不参与排序
		List<HashMap<String, Object>> countRecs = new ArrayList<HashMap<String, Object>>();
		for (String tableName : tableNames) {
			String channelId = getChannelID(tableName);
			HashMap<String, Object> countRec = TaskBaseMapperDao.getRecsForActivity(tableName, tenantId, channelId);
			countRecs.add(countRec);
		}

		HashMap<String, Object> countRec = TaskBaseMapperDao.getRemainForActivity("PLT_ORDER_INFO_REMAIN", tenantId);
		countRecs.add(countRec);

		for (int j = countRecs.size() - 1; j >= 0; j--) {
			for (int i = 0; i < j; i++) {
				if (Integer.parseInt(String.valueOf(countRecs.get(i).get("num"))) < Integer
						.parseInt(String.valueOf(countRecs.get(i + 1).get("num")))) {
					HashMap<String, Object> temp = countRecs.get(i);
					countRecs.set(i, countRecs.get(i + 1));
					countRecs.set(i + 1, temp);
				}
			}
		}
		log.info("----根据表工单量排序后----->" + countRecs);

		OrderCheck OrderChcekIns = new OrderCheck();
		ParallelManage ParallelManageIns = new ParallelManage(OrderChcekIns, iThreadNum);
		for (HashMap<String, Object> tableOne : countRecs) {
			OrderChcekIns = null;
			OrderChcekIns = new OrderCheck();
			OrderChcekIns.setTableName(String.valueOf(tableOne.get("tableName")));
			OrderChcekIns.setTableList(countRecs);
			ParallelManageIns = null;
			ParallelManageIns = new ParallelManage(OrderChcekIns, iThreadNum);
			ParallelManageIns.execute();
		}

		// // --- 启动任务 ---
		// // --- 处理客户经理表 ---
		// OrderCheck OrderChcekIns = new OrderCheck();
		// OrderChcekIns.setTableName("PLT_ORDER_INFO");
		// ParallelManage ParallelManageIns = new
		// ParallelManage(OrderChcekIns,iThreadNum);
		// ParallelManageIns.execute();
		// // --- 处理弹窗表 ---
		// OrderChcekIns = null;
		// OrderChcekIns = new OrderCheck();
		// OrderChcekIns.setTableName("PLT_ORDER_INFO_POPWIN");
		// ParallelManageIns = null;
		// ParallelManageIns = new ParallelManage(OrderChcekIns,iThreadNum);
		// ParallelManageIns.execute();
		// // --- 处理微信工单表 ---
		// OrderChcekIns = null;
		// OrderChcekIns = new OrderCheck();
		// OrderChcekIns.setTableName("PLT_ORDER_INFO_WEIXIN");
		// ParallelManageIns = null;
		// ParallelManageIns = new ParallelManage(OrderChcekIns,iThreadNum);
		// ParallelManageIns.execute();
		// // --- 处理一级工单表 ---
		// OrderChcekIns = null;
		// OrderChcekIns = new OrderCheck();
		// OrderChcekIns.setTableName("PLT_ORDER_INFO_ONE");
		// ParallelManageIns = null;
		// ParallelManageIns = new ParallelManage(OrderChcekIns,iThreadNum);
		// ParallelManageIns.execute();

		// --- 处理场景营销工单表 ---
		OrderChcekIns = null;
		OrderChcekIns = new OrderCheck();
		OrderChcekIns.setTableName("PLT_ORDER_INFO_SCENEMARKET");
		OrderChcekIns.setTableList(countRecs);
		ParallelManageIns = null;
		ParallelManageIns = new ParallelManage(OrderChcekIns, iThreadNum);
		ParallelManageIns.execute();

		// --- 处理短信工单表，判断当前时间是否为每月的前三天 ，是否处理上月的---
		SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		Calendar last = Calendar.getInstance();
		last.setTime(new Date());
		last.add(Calendar.DAY_OF_MONTH, -3); // 设置为3天前
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.set(Calendar.DATE, 1);
		Date last1 = last.getTime(); // 得到3天前的时间
		Date now1 = now.getTime(); // 得到每月1号
		String OrderLastTime;
		OrderLastTime = sdfYmd.format(last1).substring(5, 7); // ---得到上月月份
		if (last1.getTime() < now1.getTime()) { // 判断但前时间的3天前是否小于每月1号
			OrderChcekIns = null;
			OrderChcekIns = new OrderCheck();
			OrderChcekIns.setTableName("PLT_ORDER_INFO_SMS_HIS_" + OrderLastTime);
			OrderChcekIns.setTableList(countRecs);
			ParallelManageIns = null;
			ParallelManageIns = new ParallelManage(OrderChcekIns, iThreadNum);
			ParallelManageIns.execute();
		}
		// --- 特殊处理工单历史表 ---
		OrderChcekIns = null;
		OrderChcekIns = new OrderCheck();
		OrderChcekIns.setTableName("PLT_ORDER_INFO_HIS");
		OrderChcekIns.setTableList(countRecs);
		ParallelManageIns = null;
		ParallelManageIns = new ParallelManage(OrderChcekIns, iThreadNum);
		ParallelManageIns.execute();

		// // --- 处理短信工单表 ---
		// OrderChcekIns = null;
		// OrderChcekIns = new OrderCheck();
		// OrderChcekIns.setTableName("PLT_ORDER_INFO_SMS_HIS_"+OrderInsertTime);
		// ParallelManageIns = null;
		// ParallelManageIns = new ParallelManage(OrderChcekIns,iThreadNum);
		// ParallelManageIns.execute();
		//
		//
		// // --- 处理留存工单表 ---
		// OrderChcekIns = null;
		// OrderChcekIns = new OrderCheck();
		// OrderChcekIns.setTableName("PLT_ORDER_INFO_REMAIN");
		// ParallelManageIns = null;
		// ParallelManageIns = new ParallelManage(OrderChcekIns,iThreadNum);
		// ParallelManageIns.execute();

		// --- 更新统计 ---
		StringBuilder sb = new StringBuilder();

		for (Map<String, Object> item : listTenantInfo) {
			String strTenantId = (String) item.get("TENANT_ID");
			sb.setLength(0);
			sb.append(
					"SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE DATE_FORMAT(ORDER_END_DATE,'%Y%m%d')>=DATE_SUB(CURDATE(),INTERVAL 1 DAY) ");
			sb.append(" AND TENANT_ID ='");
			sb.append(strTenantId);
			sb.append("'");
			long lActivitySeqId = 0;
			List<Map<String, Object>> listResult = jdbcTemplate.queryForList(sb.toString());
			for (Map<String, Object> rec : listResult) {
				lActivitySeqId = (Long) rec.get("REC_ID");
				log.info("seqid={}", lActivitySeqId);
				// --- begin to call statics ---
				StatisticServiceIns.statisticBench(strTenantId, String.valueOf(lActivitySeqId));
			}
		}
		
		// --- 得到最大当前最大账期时间 ---
//		String countDateId = TaskBaseMapperDao.getMaxDateId();
		String  monthTimeUrl  = BusiToolsIns.getValueFromGlobal("GET_MONTH_TIME_"+SysVars.getTenantId());
        String countDateId = HttpUtil.doGet(monthTimeUrl, null).split(",")[1];
		log.info("更新每天成功标准PLT_SUCCESS_PROCESS_LOG插入一条记录");
		TaskBaseMapperDao.insertRecord(tenantId, countDateId);

		// --- 成功检查跑完调用一下场景营销的接口，告知场景 ---
		String getSceUrl = BusiToolsIns.getValueFromGlobal("ORDERCHECK.SCENEMARKET.URL");
		HashMap<String, Object> req = new HashMap<String, Object>();
		req.put("", "");
		HttpUtil.doGet(getSceUrl, req);

	}

	// --- 每天4点执行工单检查（停用） ---
	// @Scheduled(cron = "0 0 4 * * ?")
	public void checkOrderSucess() {
		// --- 定义线程池运行需要的信息 ---
		// OrderSucessFunc OrderSucessFuncIns = new OrderSucessFunc();
		// --- 定义5个线程，无数据退出 ---
		int iThreadNum = 5;
		String strTmp = BusiToolsIns.getValueFromGlobal("ORDERSUCESSCHECK.THREADSNUM");
		if (strTmp != null) {
			iThreadNum = Integer.parseInt(strTmp);
			if (iThreadNum < 5)
				iThreadNum = 5;
		}
		ThreadPoolManage ThreadPoolManageIns = new ThreadPoolManage(iThreadNum, OrderSucessFuncIns);
		// ThreadPoolManage ThreadPoolManageIns = new
		// ThreadPoolManage(1,OrderSucessFuncIns);
		// --- 启动线程池 -----------
		ThreadPoolManageIns.start();
		// --- 是否有其它处理? ---
		return;
	}

	/*
	 * 同步行云上的用户标签数据
	 */
	// @Transactional
	// --- 每天1个小时执行一次用户数据同步 ---
	//@Scheduled(cron = "0 0 * * * ?")
	public void asynUserLabel() {
	
		
		log.info("-- asynUserLabel  BEGIN  ---");
			
		// --- 提取当前的有效租户数据，含省编号 ---
		String tenantId = SysVars.getTenantId();
		String provId = SysVars.getProvId();
		List<Map<String, Object>> listTenantInfo = new ArrayList<Map<String,Object>>();
		Map<String, Object > tenantInfo = new HashMap<String, Object>();
		tenantInfo.put("TENANT_ID", tenantId);
		tenantInfo.put("PROV_ID", provId);
		listTenantInfo.add(tenantInfo);
		/*List<Map<String, Object>> listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if (listTenantInfo == null || listTenantInfo.size() == 0) {
			log.warn("没有找到有效的租户信息");
			return;
		}*/
//		ExecutorService pool = Executors.newFixedThreadPool(listTenantInfo.size());
//		ThreadBaseFunction ThreadBaseFunctionIns = null;
//		ThreadBaseFunctionIns = AsynUserLabelIns;
//		for (Map<String, Object> item : listTenantInfo) {
//			Thread ThreadIns = new UserLabelAsynThread(ThreadBaseFunctionIns, item);
//			pool.execute(ThreadIns);
//		}
		// 关闭线程池
		//pool.shutdown();
		// --- 各线程执行各线程的，不用统一结束 ---
		log.info("-- asynUserLabel  END  ---");

		return;
	}

	/*
	 * 更新工单表中的用户标签数据 （可以在用户数据同步完后启动，如凌晨7点半）
	 */
	// --- 每天6点执行工单用户数据同步 ---
	// @Scheduled(cron = "0 0 6 * * ?")
	public void updateOrderUserLabel() {
		log.info("更新工单用户标签开始 ");
		Date dateBegin = new Date();
		StringBuilder sb = new StringBuilder();
		// --- 得到SQL ---
		String strMbSql = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSERLABEL.SQL.MB");
		String strKdSql = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSERLABEL.SQL.KD");
		String strDateId = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSERLABEL.DATEID");
		String strAllTableName[] = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.TABLE").split(",");

		int SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("02");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSPONSOR("UPDATEORDERUSER." + Thread.currentThread().getName());

		// --- 得到有效的租户编号 ----
		List<Map<String, Object>> listTenantInfo = BusiToolsIns.getValidTenantInfo();
		for (Map<String, Object> item : listTenantInfo) {
			String strTenantId = (String) item.get("TENANT_ID");
			log.info("工单用户信息,目前处理的租户编号:{}", strTenantId);
			String strCurMbSql = strMbSql.replaceFirst("TTTTTENANT_ID", strTenantId);
			String strCurKdSql = strKdSql.replaceFirst("TTTTTENANT_ID", strTenantId);
			String strLabelDateId = BusiToolsIns.getValueFromGlobal("ASYNUSER.XCLOUD.DATEID." + strTenantId);
			if (strLabelDateId.equalsIgnoreCase(strDateId)) {
				log.info("租户:{}不需要更新", strTenantId);
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("NO NEED RUN");
				PltCommonLogIns.setBUSI_DESC("租户:" + strTenantId + "不需要运行 !");
				BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
				continue;
			}
			// --- 根据租户编号得到活动序列号 ---
			sb.append("SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE TENANT_ID = ? ");
			sb.append(" AND ACTIVITY_STATUS in ('1','8','9') AND REC_ID IN (");
			sb.append(" SELECT  DISTINCT ACTIVITY_SEQ_ID  FROM PLT_ACTIVITY_CHANNEL_DETAIL ");
			sb.append("WHERE CHANN_ID = '5' AND TENANT_ID= ?)");
			List<Map<String, Object>> listActivitySeqId = jdbcTemplate.queryForList(sb.toString(),
					new Object[] { strTenantId, strTenantId },
					new int[] { java.sql.Types.VARCHAR, java.sql.Types.VARCHAR });
			for (Map<String, Object> mem : listActivitySeqId) {
				Date dateCurBegin = new Date();
				// --- 处理移动 ---
				String strExecMbSql = strCurMbSql.replaceFirst("AAAAACTIVITY_SEQ_ID", mem.get("REC_ID").toString());
				for (String rec : strAllTableName) {
					String strLocalExecMbSql = strExecMbSql.replaceAll("TTTTTABLENAME", rec);
					log.info("mbsql={}", strLocalExecMbSql);
					int updateMbNums = jdbcTemplate.update(strLocalExecMbSql);
					log.info("seq={},updateMbNums={}", mem.get("REC_ID"), updateMbNums);
				}
				// log.info("mbsql={}",strExecMbSql);
				// int updateMbNums =jdbcTemplate.update(strExecMbSql);
				// log.info("seq={},updateMbNums={}",mem.get("REC_ID"),updateMbNums);
				// --- 处理宽带 ---
				String strExecKdSql = strCurKdSql.replaceFirst("AAAAACTIVITY_SEQ_ID", mem.get("REC_ID").toString());
				for (String rec : strAllTableName) {
					String strLocalExecKdSql = strExecKdSql.replaceAll("TTTTTABLENAME", rec);
					int updateKdNums = jdbcTemplate.update(strLocalExecKdSql);
					log.info("seq={},updateKdNums={}", mem.get("REC_ID"), updateKdNums);
				}
				// log.info("kdsql={}",strExecKdSql);
				// int updateKdNums =jdbcTemplate.update(strExecKdSql);
				// log.info("seq={},updateKdNums={}",mem.get("REC_ID"),updateKdNums);
				Date dateCurEnd = new Date();
				PltCommonLogIns.setSTART_TIME(dateCurBegin);
				PltCommonLogIns.setEND_TIME(dateCurEnd);
				PltCommonLogIns.setBUSI_CODE("ONE ACTVIVITYSEQID END");
				PltCommonLogIns.setBUSI_ITEM_1(strTenantId);
				PltCommonLogIns.setBUSI_DESC(mem.get("REC_ID").toString());
				PltCommonLogIns.setDURATION((int) (dateCurEnd.getTime() - dateCurBegin.getTime()));
				BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
				// try{Thread.sleep(100000);}catch(Exception e){}
			}
			// --- 更新帐期标识 ---
			BusiToolsIns.setValueToGlobal("UPDATEORDERUSERLABEL.DATEID." + strTenantId, strLabelDateId);

		}
		Date dateEnd = new Date();
		log.info("更新工单用户标签结束 ,耗时:{}", dateEnd.getTime() - dateBegin.getTime());
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setEND_TIME(dateEnd);
		PltCommonLogIns.setBUSI_CODE("ALL ENDS");
		PltCommonLogIns.setBUSI_DESC("更新工单上的用户信息结束 !!!");
		PltCommonLogIns.setDURATION((int) (dateEnd.getTime() - dateBegin.getTime()));
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bonc.busi.task.service.BaseTaskSrv#testDbConnection()
	 */
	public int testDbConnection(String dbtype, String timeDur) {
		Date begin = new Date();
		Connection connection = null;
		Statement statement = null;
		try {
			// Class.forName(getValueFromGlobal("DS.MYSQL.DRIVER"));

			try {
				// 加载MySql的驱动类
				if ("mysql".equalsIgnoreCase(dbtype)) {
					Class.forName("com.mysql.jdbc.Driver");
				} else if ("oracle".equalsIgnoreCase(dbtype)) {
					Class.forName("oracle.jdbc.OracleDriver");
				}
			} catch (ClassNotFoundException e) {
				System.out.println("找不到驱动程序类 ，加载驱动失败！");
				e.printStackTrace();
				return -1;
			}

			if ("mysql".equalsIgnoreCase(dbtype)) {
				connection = DriverManager.getConnection("jdbc:mysql://10.162.2.119:31699/henan0?&useSSL=false",
						"orderrun", "CLYXV3_HN_123");
				statement = connection.createStatement();
				statement.execute("insert into dd values('1370109220')");
			} else if ("oracle".equalsIgnoreCase(dbtype)) {
				connection = DriverManager.getConnection("jdbc:oracle:thin:@//132.35.224.165:1521/dwtest", "clyxv3_hn",
						"CLYXV3_HN_123");
				statement = connection.createStatement();
				statement.execute("insert into dd values('1370109220')");
			}

			log.info(" before  sleep:dbtype:{},time:{}", dbtype, timeDur);
			Thread.sleep(Integer.parseInt(timeDur));
			log.info(" after  sleep:dbtype:{},time:{}", dbtype, timeDur);
			statement.execute("insert into dd values('1370109220')");
			log.info("  成功执行  !!!!!!!!!!!!!!!!!!!!!!!!!1");
		} catch (Exception e) {

			e.printStackTrace();
			return -1;
		} finally {
			if (statement != null) {
				try {
					statement.close();
					connection.close();
				} catch (Exception e) {
				}
			}
		}

		return 0;
	}

	// --- 更新工单表的userid ---
	//@Scheduled(cron = "0 0 0 * * ?")
	public void updateUserId() {
		log.info("-- updateUserId  BEGIN  ---");
		// --- 提取当前的有效租户数据，含省编号 ---
		List<Map<String, Object>> listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if (listTenantInfo == null || listTenantInfo.size() == 0) {
			log.warn("没有找到有效的租户信息");
			return;
		}
		// --- 一个租户一个线程 ---
		ExecutorService pool = Executors.newFixedThreadPool(listTenantInfo.size());
		ThreadBaseFunction ThreadBaseFunctionIns = null;
		ThreadBaseFunctionIns = UpdateUserIdIns;
		for (Map<String, Object> item : listTenantInfo) {
			Thread ThreadIns = new UpdateUserIdThread(ThreadBaseFunctionIns, item);
			pool.execute(ThreadIns);
		}
		// --- 关闭线程池 ---
		pool.shutdown();
		// --- 各线程执行各线程的，不用统一结束 ---
		log.info("-- updateUserId  END  ---");
		return;

	}
}
