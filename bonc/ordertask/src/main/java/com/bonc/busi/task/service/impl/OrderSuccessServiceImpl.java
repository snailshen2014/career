package com.bonc.busi.task.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.activity.SuccessProductPo;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.orderschedule.service.OrderSuccessService;
import com.bonc.busi.orderschedule.utils.OrderFileMannager;
import com.bonc.busi.orderschedule.utils.OrderFileMannagerForOrderSuccessFilter;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.base.Global;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.base.StringUtils;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.instance.OrderFilterSucess;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.base.JsonResult;

@Service
public class OrderSuccessServiceImpl implements OrderSuccessService {
	
	private final static Logger log = LoggerFactory.getLogger(OrderSuccessServiceImpl.class);
	
	@Autowired
	private BusiTools BusiToolsIns;
	
	@Autowired
	private BaseMapper BaseMapperIns;
	
	private	Date dateBegin = null;
	private	Date dateCur = null;
	private	int  SerialId =0;   // --- 日志序列号 ---
	private	PltCommonLog PltCommonLogIns = new PltCommonLog();  // --- 日志变量 ---
	// --- 定义活动序列号 ---
	private	int	m_iActivitySeqId = -1;
	// --- 定义租户编号  ---
	private	String m_strTenantId = null;
	// --- 定义当前帐期变量 ---
	private	String curDateId = null;
	private	String m_strSucessSql = null;
	// --- 定义成功条件的PO ---
	public SuccessStandardPo m_cssSuccessStandardPo= null;
	
	public String tableName;
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	private String fileName;
	

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	//private	JdbcTemplate JdbcTemplateIns = SpringUtil.getBean(JdbcTemplate.class);
	@Autowired
	private	JdbcTemplate JdbcTemplateIns;

	// get ftp info from table
	String FtpSrvIp = null;
	String FtpUser = null;
	String FtpPassowd =null;
	String FtpPort = null;
	String ftpRemote = null;
	String ftpLocal = null;
	
	/*
	 *由于河南活动工单量很大的话,orderFilterSucess方式的成功过滤方式很慢,采用如下的方式：在行云上对需要过滤的工单表进行成功过滤
	 *采取的方式：把工单数据同步到行云，在行云上执行成功过滤,行云上成功过滤完成后把数据下载到本地，然后更新工单表数据
	 */
//	public boolean orderFilterSucessV(int activitySeqId, SuccessStandardPo sucessCon, String tenantId) {
//		// --判断是否存在成功标准--
//		PltCommonLog logdb = new PltCommonLog();
//		logdb.setLOG_TYPE("21");
//		if (sucessCon == null) {
//
//			logdb.setSTART_TIME(new Date());
//			logdb.setSPONSOR("成功标准过滤结束");
//			logdb.setBUSI_CODE("end");
//			logdb.setBUSI_DESC("无成功标准SuccessStandardPo");
//			BusiToolsIns.insertPltCommonLog(logdb);
//			return false;
//		}
//
//		// get ftp info from table
//		FtpSrvIp = BusiToolsIns.getValueFromGlobal("HDFSSRV.IP");
//		FtpUser = BusiToolsIns.getValueFromGlobal("HDFSSRV.USER");
//		FtpPassowd = BusiToolsIns.getValueFromGlobal("HDFSSRV.PASSWORD");
//		FtpPort = BusiToolsIns.getValueFromGlobal("HDFSSRV.PORT");
//		ftpRemote = BusiToolsIns.getValueFromGlobal("ORDER_REMOTEPATH");
//		ftpLocal = BusiToolsIns.getValueFromGlobal("ORDER_LOCALPATH");
//
//		// --- 查询各个表当前活动下的工单量，并按工单量大小排序 ---
//		List<String> tableNames = new ArrayList<String>();
//		List<String> needSuccessFilterTables = new ArrayList<String>();
//		tableNames.add("PLT_ORDER_INFO");
//		tableNames.add("PLT_ORDER_INFO_SMS");
//		tableNames.add("PLT_ORDER_INFO_WEIXIN");
//		tableNames.add("PLT_ORDER_INFO_POPWIN");
//		tableNames.add("PLT_ORDER_INFO_ONE");
//		tableNames.add("PLT_ORDER_INFO_CALL");// 电话渠道
//		tableNames.add("PLT_ORDER_INFO_SMALLWO");// 小沃渠道
//		tableNames.add("PLT_ORDER_INFO_PLAY");// 玩转流量APP渠道
//		// --PLT_ORDER_INFO_NEWWSC channelID：20--
//		tableNames.add("PLT_ORDER_INFO_NEWWSC");
//		for (String tableName : tableNames) {
//			HashMap<String, Object> countRec = BaseMapperIns.getCountForActivity(tableName, activitySeqId, tenantId);
//			if (!"0".equals(countRec.get("num") + "")) {
//				needSuccessFilterTables.add(tableName);
//			}
//		}
//
//		m_cssSuccessStandardPo = sucessCon;
//		m_iActivitySeqId = activitySeqId;
//		m_strTenantId = tenantId;
//		if (begin() != 0) { // 条件不满足，直接返回
//			log.info("不满足成功过滤的条件，直接返回,批次：{}", activitySeqId);
//			return true;
//		}
//		// m_strSucessSql真正要执行的行云sql已经在begin方法里拼装好了
//		for (String table : needSuccessFilterTables) { // 遍历处理每一张表，如果处理中报错了，程序不做处理，需要手工重跑活动
//			// 1、 上传工单数据到行云表中PLT_ORDER_INFO_FOR_FILTER
//			boolean uploadResult = uploadOrderToXcloud(activitySeqId, tenantId, table);
//			if(!uploadResult){
//				log.error(" ======= 事前成功过滤同步工单数据到行云出错了,终止下面的执行");
//				continue;
//			}
//			if (m_strSucessSql != null) { // 去行云上执行sql
//				if (m_strSucessSql.length() > 1000) {
//					log.info("------在行云上执行的成功过滤的sql部分内容: " + m_strSucessSql.substring(0, 999));
//				}
//				// 2、执行，获取结果
//				JsonResult JsonResultIns = BusiToolsIns.execDdlOnXcloud(m_strSucessSql);
//				if ("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false) {
//					log.error("[OrderTask] 事前成功过滤sql on xcloud 执行失败");
//				} else {
//					log.error("[OrderTask] 事前成功过滤sql on xcloud 执行成功,下一步要从FTP服务器上下载数据文件");
//					//3 下载结果 download order file
//					if (downLoadFile(FtpSrvIp, FtpUser, FtpPassowd, FtpPort, ftpRemote, ftpLocal) == 0) {
//						log.info("Down load 事前成功过滤file" + ftpRemote + getFileName() + ".csv" + " ok.");
//						// split order file and load data
//						String origFileName = ftpLocal + getFileName() + ".csv";
//						List<String> fileList = OrderFileMannager.splitFile(origFileName, 100000, false);
//						BusiToolsIns.deleteFile(origFileName);
//						//4  更新MySql工单表的数据
//						for (String fileName : fileList) {
//							try {
//								List<String> userIdList = FileUtils.readLines(new File(fileName));// 活动到了成功的USER_ID
//								if (userIdList != null && userIdList.size() > 0) {
//									String userIdStr = generateUserIds(userIdList);
//									StringBuilder sbb = new StringBuilder();
//									sbb.append("UPDATE "+ table);
//									sbb.append(" SET  ORDER_STATUS = '6' ");
//									sbb.append(" WHERE TENANT_ID='");
//									sbb.append(m_strTenantId);
//									sbb.append("'");
//									sbb.append("  AND ACTIVITY_SEQ_ID =");
//									sbb.append(m_iActivitySeqId);
//									sbb.append("  AND USER_ID IN (");
//									sbb.append(userIdStr);
//									sbb.append(")");
//									int result = JdbcTemplateIns.update(sbb.toString());
//									sbb.setLength(0);
//									log.info("更新 " + table + "表,工单过 滤成功,activityseqid={},更新数量={}",m_iActivitySeqId,result);
//								}
//								BusiToolsIns.deleteFile(fileName);
//							} catch (IOException e) {
//								log.error("xxxxxxxxx 事前成功过滤在读取拆分的数据文件时报错了. xxxx  ");
//								BusiToolsIns.deleteFile(fileName);
//								e.printStackTrace();
//							}
//
//						} // end for
//					} // download ok
//					else {
//						log.error("Down load 事前成功过滤file" + ftpRemote + getFileName() + ".csv" + " 出错了.");
//					}
//
//				}
//			}
//		}
//		return true;
//	}
	
	
	/*
	 *由于河南活动工单量很大的话,orderFilterSucess方式的成功过滤方式很慢,采用如下的方式：在行云上对需要过滤的工单表进行成功过滤
	 *采取的方式：在行云上执行成功过滤,行云上成功过滤完成后把数据下载到本地，然后更新工单表数据
	 */
	@Override
	public boolean orderFilterSucessV2(int activitySeqId, SuccessStandardPo sucessCon, String tenantId) {
		// --判断是否存在成功标准--
		PltCommonLog logdb = new PltCommonLog();
		logdb.setLOG_TYPE("21");
		if (sucessCon == null) {

			logdb.setSTART_TIME(new Date());
			logdb.setSPONSOR("成功标准过滤结束");
			logdb.setBUSI_CODE("end");
			logdb.setBUSI_DESC("无成功标准SuccessStandardPo");
			BusiToolsIns.insertPltCommonLog(logdb);
			return false;
		}

		// get ftp info from table
		FtpSrvIp = BusiToolsIns.getValueFromGlobal("HDFSSRV.IP");
		FtpUser = BusiToolsIns.getValueFromGlobal("HDFSSRV.USER");
		FtpPassowd = BusiToolsIns.getValueFromGlobal("HDFSSRV.PASSWORD");
		FtpPort = BusiToolsIns.getValueFromGlobal("HDFSSRV.PORT");
		ftpRemote = BusiToolsIns.getValueFromGlobal("ORDER_REMOTEPATH");
		ftpLocal = BusiToolsIns.getValueFromGlobal("ORDER_LOCALPATH");

		// --- 查询各个表当前活动下的工单量，并按工单量大小排序 ---
		List<String> tableNames = new ArrayList<String>();
		List<String> needSuccessFilterTables = new ArrayList<String>();
		tableNames.add("PLT_ORDER_INFO");
		tableNames.add("PLT_ORDER_INFO_SMS");
		tableNames.add("PLT_ORDER_INFO_WEIXIN");
		tableNames.add("PLT_ORDER_INFO_POPWIN");
		tableNames.add("PLT_ORDER_INFO_ONE");
		tableNames.add("PLT_ORDER_INFO_CALL");// 电话渠道
		tableNames.add("PLT_ORDER_INFO_SMALLWO");// 小沃渠道
		tableNames.add("PLT_ORDER_INFO_PLAY");// 玩转流量APP渠道
		// --PLT_ORDER_INFO_NEWWSC channelID：20--
		tableNames.add("PLT_ORDER_INFO_NEWWSC");
		for (String tableName : tableNames) {
			HashMap<String, Object> countRec = BaseMapperIns.getCountForActivity(tableName, activitySeqId, tenantId);
			if (!"0".equals(countRec.get("num") + "")) {
				needSuccessFilterTables.add(tableName);
			}
		}

		if(needSuccessFilterTables.size() == 0){
			return true;
		}
		ExecutorService service = Global.getExecutorService();
		m_cssSuccessStandardPo = sucessCon;
		m_iActivitySeqId = activitySeqId;
		m_strTenantId = tenantId;
		if (begin() != 0) { // 条件不满足，直接返回
			log.info("不满足成功过滤的条件，直接返回,批次：{}", activitySeqId);
			return true;
		}
		   // m_strSucessSql真正要执行的行云sql已经在begin方法里拼装好了
			if (m_strSucessSql != null) { // 去行云上执行sql
				log.info("------在行云上执行的成功过滤的sql部分内容: " + m_strSucessSql);
				// 1、执行，获取结果
				JsonResult JsonResultIns = BusiToolsIns.execDdlOnXcloud(m_strSucessSql);
				if ("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false) {
					log.error("[OrderTask] 事前成功过滤sql on xcloud 执行失败");
				} else {
					log.error("[OrderTask] 事前成功过滤sql on xcloud 执行成功,下一步要从FTP服务器上下载数据文件");
					//2 下载结果 download order file
					if (downLoadFile(FtpSrvIp, FtpUser, FtpPassowd, FtpPort, ftpRemote, ftpLocal) == 0) {
						log.info("Down load 事前成功过滤file" + ftpRemote + getFileName() + ".csv" + " ok.");
						// split order file and load data
						String origFileName = ftpLocal + getFileName() + ".csv";
						List<String> fileList = OrderFileMannagerForOrderSuccessFilter.splitFile(origFileName, 100000, false);
						BusiToolsIns.deleteFile(origFileName);
						//4  更新MySql工单表的数据
						for (String fileName : fileList) {
							try {
								List<String> userIdList = FileUtils.readLines(new File(fileName));// 活动到了成功的USER_ID
								if (userIdList != null && userIdList.size() > 0) {
									//控制下面线程的执行顺序，子任务必须全部执行完才能继续往下执行
									CountDownLatch  cdl = new CountDownLatch(needSuccessFilterTables.size());
								  for(String table : needSuccessFilterTables) { //遍历处理每一张表,更新工单
									String userIdStr = generateUserIds(userIdList);
									OrderSuccessUpdateTableTask task = new OrderSuccessUpdateTableTask();
									task.setTablName(table);
									task.setTenantId(m_strTenantId);
									task.setActivitySeqId(m_iActivitySeqId);
									task.setUserIdStr(userIdStr);
									task.setJdbcTemplateIns(JdbcTemplateIns);
									task.setCountDownLatch(cdl);
									task.setFileName(fileName);
									service.execute(task);
//									StringBuilder sbb = new StringBuilder();
//									sbb.append("UPDATE "+ table);
//									sbb.append(" SET  ORDER_STATUS = '6' ");
//									sbb.append(" WHERE TENANT_ID='");
//									sbb.append(m_strTenantId);
//									sbb.append("'");
//									sbb.append("  AND ACTIVITY_SEQ_ID =");
//									sbb.append(m_iActivitySeqId);
//									sbb.append("  AND USER_ID IN (");
//									sbb.append(userIdStr);
//									sbb.append(")");
//									sbb.append(" AND ORDER_STATUS = '0' ");
//									int result = JdbcTemplateIns.update(sbb.toString());
//									sbb.setLength(0);
//									log.info("更新 " + table + "表,工单过 滤成功,activityseqid={},更新数量={},拆分的小文件名:{}",m_iActivitySeqId,result,fileName);
								  }
								  try {
									cdl.await();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								}
								BusiToolsIns.deleteFile(fileName);
							} catch (IOException e) {
								log.error("xxxxxxxxx 事前成功过滤在读取拆分的数据文件时报错了. xxxx  ");
								BusiToolsIns.deleteFile(fileName);
								e.printStackTrace();
							}

						} // end for
					} // download ok
					else {
						log.error("Down load 事前成功过滤file" + ftpRemote + getFileName() + ".csv" + " 出错了.");
					}

				}
			}
		
		return true;
	}
	
	
	/**
	 * 组装成  111,112,113的形式
	 * @param listUserId
	 * @return
	 */
	  public String generateUserIds(List<String> listUserId){
		  StringBuilder	sb = new StringBuilder();
		  for(int i =0;i < listUserId.size();++i){
				if(i != 0) sb.append(",");
				sb.append("'");
				sb.append(listUserId.get(i));
				sb.append("'");
			}
		  return sb.toString();
	  }
	
	/**
	 * FTP服务器上下载成功过滤的数据文件
	 * @param ip
	 * @param user
	 * @param passwd
	 * @param port
	 * @param remote
	 * @param local
	 * @return
	 */
	private int downLoadFile(String ip, String user, String passwd, String port, String remote, String local) {
		String absRemote = remote;
		absRemote = absRemote.replaceFirst("HDFS:", "");
		int downRtn = -1;
		String ftpRtn = FtpTools.downloadXcloudFile(ip, user, passwd, Integer.parseInt(port),
				absRemote + getFileName() + ".csv", local + getFileName() + ".csv", true);
		if ("000000".equals(ftpRtn))
			downRtn = 0;
		else
			log.error("Down load 事前成功过滤的数据文件出错" + ftpRtn);
		return downRtn;
	}

	/**
	 * 同步短信历史工单信息到行云DbLink下的XLD_ORDER_INFO_SMS_HIS表，由于速度很快，不需要分批同步
	 * @param activitySeqId  同步的短信历史工单的批次
	 * @param tenantId       租户Id
	 * @param tableName      源表
	 * @return true:成功    false:出错异常
	 */
	@Override
	public boolean  uploadOrderToXcloud(int activitySeqId,String tenantId,String tableName){
		boolean success = true; //是否成功
		Connection connection = null;
		Statement statement =null;
		String driverName = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
		String url = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.URL");
		String username =  BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.USER");
		String password = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
		String order_xcloud_suffix = BaseMapperIns.getValueFromSysCommCfg("ORDER_XCLOUD_SUFFIX."+tenantId);
		if(org.apache.commons.lang.StringUtils.isBlank(order_xcloud_suffix)){
			log.error("======== 缺少ORDER_XCLOUD_SUFFIX配置,请配置");
			return false;
		}
		try{	
			Class.forName(driverName);
			connection = DriverManager.getConnection(url , 
					username, 
					password);
			//1 先清空PLT_ORDER_INFO_FOR_FILTER里的数据
			String deleteOrderInXcloud = "truncate  table  PLT_ORDER_INFO_FOR_FILTER";
			statement = connection.createStatement();
			statement.execute(deleteOrderInXcloud);
			log.info("====================delete PLT_ORDER_INFO_FOR_FILTER sucess,批次：{},工单表:{}",activitySeqId,tableName);
			
			//2 上传数据到PLT_ORDER_INFO_FOR_FILTER
			long begin = System.currentTimeMillis();
			String templateSql = "insert into PLT_ORDER_INFO_FOR_FILTER "
					+    "{Select REC_ID,ACTIVITY_SEQ_ID,BATCH_ID,CHANNEL_ID,TENANT_ID,USER_ID,ORDER_STATUS,CHANNEL_STATUS "
					+    " FROM TTTTTABLENAME WHERE TENANT_ID='TTTTTNENAT_ID' AND ACTIVITY_SEQ_ID = AAAAACTIVITY_SEQ_ID AND ORDER_STATUS='0'}@ORDER_XCLOUD_SUFFIX";
			String replaceTenantIdSql = templateSql.replaceFirst("TTTTTNENAT_ID", tenantId);
			String replaceSourceTableName  = replaceTenantIdSql.replaceFirst("TTTTTABLENAME", tableName);
			String replaceOrderXcloudSuffix = replaceSourceTableName.replaceFirst("ORDER_XCLOUD_SUFFIX", order_xcloud_suffix);
			String sql = replaceOrderXcloudSuffix.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(activitySeqId));
			statement.execute(sql);
			log.info("====================upload sucess,,批次：{},工单表:{}",activitySeqId,tableName);
			System.out.println("========== 耗时："+(System.currentTimeMillis()-begin)/1000);
			return success;
		   }catch(Exception ex){
			   ex.printStackTrace();
			   success = false;
		   }finally{
			   try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  }
		return success;
	}

	/**
	 * 删除行云PLT_ORDER_INFO_FOR_FILTER中的工单数据
	 */
	@Override
	public boolean deleteOderInXcloud() {
		boolean success = true; //是否成功
		Connection connection = null;
		Statement statement =null;
		String driverName = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
		String url = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.URL");
		String username =  BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.USER");
		String password = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
		try{	
			Class.forName(driverName);
			connection = DriverManager.getConnection(url , 
					username, 
					password);		
			long begin = System.currentTimeMillis();
			String deleteOrderInXcloud = "truncate  table  PLT_ORDER_INFO_FOR_FILTER";
			statement = connection.createStatement();
			statement.execute(deleteOrderInXcloud);
			System.out.println("====================delete PLT_ORDER_INFO_FOR_FILTER sucess");
			System.out.println("========== 耗时："+(System.currentTimeMillis()-begin)/1000);
			return success;
		   }catch(Exception ex){
			   ex.printStackTrace();
			   success = false;
		   }finally{
			   try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  }
		return success;
	}
	
	public	int	begin(){
		// --- 纪录开始时间 ---
		dateBegin = new Date();
		dateCur = new Date();
		// --- 得到序列号 ---
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		// --- 纪录日志 ---
		PltCommonLogIns.setLOG_TYPE("21");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setSPONSOR("工单事前成功过滤");
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("工单成功过滤检查开始");
		PltCommonLogIns.setDEST_NUM(m_iActivitySeqId);
		PltCommonLogIns.setBUSI_ITEM_4(m_strTenantId);
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		// --- 得到最大帐期 ---
		curDateId = BaseMapperIns.getMaxDateId();
		if(curDateId == null){
			log.warn("--- 取当前帐期失败,得到的是空值 ---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，curDateid为空");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		m_strSucessSql = getSqlForSucessType();
		if(m_strSucessSql == null){
			log.warn("--- 取成功条件SQL得到的是空值 ---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，成功条件SQL为空");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		
		genFileName();
		StringBuilder sb = new StringBuilder();
		sb.append(m_strSucessSql);
		//判断括号数量相不相同
		String sbString = sb.toString();
		int x = 0;
		int y = 0;
		for(int j = 0 ;j < sbString.length();j++){
			if(sbString.substring(j, j+1).equals("(")||sbString.substring(j, j+1).equals("[")){
				x++;
			}
			if(sbString.substring(j, j+1).equals(")")||sbString.substring(j, j+1).equals("]")){
				y++;
			}
		}

		// --- 加入用户id ---
		/*********************  20171227 改用全量用户跑的方式  注释掉选择USER_ID的步骤  *******************/
		//sb.append(" AND USER_ID IN (");
        //sb.append(" SELECT USER_ID FROM PLT_ORDER_INFO_FOR_FILTER");
		//sb.append(")");
		/*********************  20171227 改用全量用户跑的方式  *******************/
		if(x==y){
			log.info("左右括号数量相同均为"+x);
		}else {
			sb.append(")");
			log.info("左右括号数量不相同，左括号为"+x+"右括号为"+y);
		}
		m_strSucessSql = sb.toString();
		m_strSucessSql = "export "+ m_strSucessSql;
		m_strSucessSql += " ATTRIBUTE(LOCATION('";
		m_strSucessSql += ftpRemote;
		m_strSucessSql += getFileName();
		m_strSucessSql += ".csv')";
		m_strSucessSql += " SEPARATOR('|'))";
		return 0;
	}
	
	public void genFileName() {
		String name = "ordersuccess_tmp_";
		Format format = new SimpleDateFormat("yyyyMMddHHmmss");
		name += format.format(new Date());
		this.fileName = name;
	}
	
	private	String		getSqlForSucessType(){
		if(m_cssSuccessStandardPo == null)  return null;
		if(StringUtils.isNotNull(m_cssSuccessStandardPo.getSuccessType()) == false){
			return null;
		}
		boolean			bProduct = false;    // --- 是否成功标准中有产品 ---
		StringBuilder		sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		//SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String		OrderCreateTime ;
		OrderCreateTime = sdfYm.format(new Date()) +"-01";     // --- 固定从1号开始判断是否办理了产品 ---

//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  //1
//		Calendar lastDate = Calendar.getInstance();
//		lastDate.set(Calendar.DATE,1);//设为当前月的1 号
//		lastDate.add(Calendar.MONTH,-1);//减一个月，变为下月的1 号
//		//lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天
//		String		OrderCreateTime=sdf.format(lastDate.getTime());//1

		String			strMatchingType = m_cssSuccessStandardPo.getMatchingType();
		String          isHaveRenewProduct = m_cssSuccessStandardPo.getIsHaveRenewProduct();
		if(isHaveRenewProduct ==null){
			isHaveRenewProduct = "0";
		}
		if(StringUtils.isNotNull(strMatchingType)){
			if(strMatchingType.equals("2")&&isHaveRenewProduct.equals("0")){  // --- 精确匹配产品  ---
				// --- 提取活动成功产品列表 ---
				List<SuccessProductPo> listProduct = m_cssSuccessStandardPo.getSuccessProductList();
				bProduct = true;
				int		i = 0;
				if(listProduct != null && listProduct.size() > 0){   // --- 得到SQL  ---

					sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					for(i = 0;i < listProduct.size();++i){
						if(i > 0)  sbProduct.append(",");
						sbProduct.append("'");
						sbProduct.append(listProduct.get(i).getProductCode());
						sbProduct.append("'");
					}
					sbProduct.append(")");
					sbProduct.append(" AND ACCEPTED_DATE > '");
					sbProduct.append(OrderCreateTime);
					sbProduct.append("'  ");
				}else {
					log.info("精准匹配产品时，无产品编码");
					return null;
				}
			}
		}
		StringBuilder		sb = new StringBuilder();
		String				strAddSql = null;
		String				strTmp = null;
		if(StringUtils.isNotNull(m_cssSuccessStandardPo.getSuccessConditionSQL())){
			strAddSql = m_cssSuccessStandardPo.getSuccessConditionSQL().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		if(StringUtils.isNotNull(m_cssSuccessStandardPo.getSuccessTypeConditionSql())){
			strTmp = m_cssSuccessStandardPo.getSuccessTypeConditionSql().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		String successType = m_cssSuccessStandardPo.getSuccessType();
		//精准营销时
		if(bProduct){
			if(strTmp!=null){
				sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
				sbProduct.append("("+strTmp+")");
				sbProduct.append(" AND ");
				sbProduct.append(" DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'");
				if(strAddSql!=null){
					sbProduct.append(" AND " + "("+strAddSql+")" );
				}
			}else{                               //--- 成功标准类型中，无成功标准    如3,4,5
				if(strAddSql!=null){
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(" DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'");
					sbProduct.append(" AND " + "("+strAddSql+")"   );
				}
			}
			log.info("精准营销：");

		}else{
			//无精准营销但有成功标准
			if(strTmp!=null){
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
				sb.append("("+strTmp+")");
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'");
				if(strAddSql!=null){
					sb.append(" AND " + "("+strAddSql+")"   );
				}

			}else{                                  // --- 无成功标准  ---
				if(successType.equals("3")||successType.equals("4")||successType.equals("5")){       //--- 成功标准类型在3,4,5之间时
					sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='");
					if (successType.equals("3")) {
						sb.append("03");
					}
					if (successType.equals("4")) {
						sb.append("02");
					}
					if (successType.equals("5")) {
						sb.append("01");
					}
					sb.append("' AND ACCEPTED_DATE > '");
					sb.append(OrderCreateTime);
					sb.append("' AND USER_ID IN ( ");
				}
				if(strAddSql!=null){
					sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
					sb.append("("+strAddSql+")" );
					sb.append(" AND DATE_ID = '");
					sb.append(curDateId);
					sb.append("' ");

				}else {                                 // --- 全没有时  ---
					log.info("此产品不存在或者没有成功标准可以判断");
					return null;
				}
			}
		}

		log.info("工单检查sql="+sb.toString()+sbProduct.toString()+"), 成功类型="+successType);

		//一条成功标准sql插入一条日志
		PltCommonLog		logdb = new PltCommonLog();
//		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("221");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("工单过滤sql");
		logdb.setBUSI_CODE("getSqlForSucessType,成功类型="+successType);
		logdb.setBUSI_DESC("SuccessSql,select未加USER_ID");
		logdb.setBUSI_ITEM_1(sb.toString()+sbProduct.toString());
		logdb.setBUSI_ITEM_2(""+m_iActivitySeqId);
		logdb.setBUSI_ITEM_3(tableName);
		BusiToolsIns.insertPltCommonLog(logdb);
		if(bProduct){
			return sbProduct.toString();
		}else {
			return sb.toString();
		}
	}

}
