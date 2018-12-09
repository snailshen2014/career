package com.bonc.busi.task.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.orderschedule.utils.OrderFileMannagerForOrderCheck;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.base.Global;
import com.bonc.busi.task.base.StringUtils;
import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.busi.task.bo.OrderCheckInfo;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.task.service.OrderCheckService;
import com.bonc.common.base.JsonResult;

/**
 * 事后成功检查实现类
 * @author Administrator
 *
 */
@Service
public class OrderCheckServiceImpl implements OrderCheckService {
	
	private final static Logger log = LoggerFactory.getLogger(OrderCheckServiceImpl.class);
	
	@Autowired
	private BusiTools BusiToolsIns;
	
	@Autowired
	private BaseMapper BaseMapperIns;
	
	private Date dateBegin = null; // --- 开始时间 ---
	private PltCommonLog PltCommonLogIns = new PltCommonLog(); // --- 日志变量 ---
	// --- 定义当前租户变量 ---
	private String curTenantId = null;
	private int SerialId = 0; // --- 日志序列号 ---
	// --- 定义当前帐期变量 ---
	private String curDateId = null;
	// --- 定义保存租户成功条件的变量 ---
	private Map<String, List<ActivitySucessInfo>> mapActivitySucessInfo = new HashMap<String, List<ActivitySucessInfo>>();
	// --- 定义活动对应的产品列表 ---
	private Map<String, List<String>> mapActivityProduct = new HashMap<String, List<String>>();
	
	private String dealOrderTableName;
	
	// get ftp info from table
	String FtpSrvIp = null;
	String FtpUser = null;
	String FtpPassowd = null;
	String FtpPort = null;
	String ftpRemote = null;
	String ftpLocal = null;
	
    private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Autowired
	private	JdbcTemplate JdbcTemplateIns;
	
	Connection connection = null;//全局的连接

	/**
	 * 同步工单表中的工单到行云DbLink下的PLT_ORDER_INFO_FOR_ORDERCHECK表，由于速度很快，不需要分批同步
	 * @param tenantId       租户Id
	 * @param tableName      源表
	 * @return true:成功    false:出错异常
	 */
	@Override
	public boolean uploadOrderToXcloud(String tenantId, String tableName) {
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
			//1 先清空PLT_ORDER_INFO_FOR_ORDERCHECK里的数据
			String deleteOrderInXcloud = "truncate  table  PLT_ORDER_INFO_FOR_ORDERCHECK";
			statement = connection.createStatement();
			statement.execute(deleteOrderInXcloud);
			log.info("====================delete PLT_ORDER_INFO_FOR_ORDERCHECK sucess,工单表:{}",tableName);
			
			//2 上传数据到PLT_ORDER_INFO_FOR_ORDERCHECK
			long begin = System.currentTimeMillis();
			String templateSql = "insert into PLT_ORDER_INFO_FOR_ORDERCHECK "
					+    "{Select REC_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,TENANT_ID,USER_ID,ORDER_STATUS,CHANNEL_STATUS "
					+    " FROM TTTTTABLENAME WHERE TENANT_ID='TTTTTNENAT_ID' AND ORDER_STATUS ='5' AND CHANNEL_STATUS IN('0','2')}@ORDER_XCLOUD_SUFFIX";
			String replaceTenantIdSql = templateSql.replaceFirst("TTTTTNENAT_ID", tenantId);
			String replaceSourceTableName  = replaceTenantIdSql.replaceFirst("TTTTTABLENAME", tableName);
			String replaceOrderXcloudSuffix = replaceSourceTableName.replaceFirst("ORDER_XCLOUD_SUFFIX", order_xcloud_suffix);
			statement.execute(replaceOrderXcloudSuffix);
			log.info("====================upload sucess,工单表:{}",tableName);
			System.out.println("========== 耗时："+(System.currentTimeMillis()-begin)/1000);
			return success;
		   }catch(Exception ex){
			    ex.printStackTrace();
			    PltCommonLogIns.setLOG_TYPE("202");
				PltCommonLogIns.setSERIAL_ID(1122);
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setSPONSOR(tableName);
				PltCommonLogIns.setBUSI_CODE("202");
				String error = ex.getMessage();
				if(error!=null && error.length()>1000){
					error = error.substring(0, 998);
				}
				PltCommonLogIns.setBUSI_DESC("202");
				PltCommonLogIns.setBUSI_ITEM_1(error);
				BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
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
	 * 同步工单表的某个批次的工单到行云DbLink下的PLT_ORDER_INFO_FOR_ORDERCHECK表，由于速度很快，不需要分批同步
	 * @param tenantId       租户Id
	 * @param tableName      源表
	 * @param activitySeqId  上传的批次
	 * @return true:成功    false:出错异常
	 */
	public boolean uploadOrderToXcloudV2(String tenantId, String tableName,int activitySeqId) {
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
			//1 先清空PLT_ORDER_INFO_FOR_ORDERCHECK里的数据
			String deleteOrderInXcloud = "truncate  table  PLT_ORDER_INFO_FOR_ORDERCHECK";
			statement = connection.createStatement();
			statement.execute(deleteOrderInXcloud);
			log.info("====================delete PLT_ORDER_INFO_FOR_ORDERCHECK sucess,工单表:{}",tableName);
			
			//2 上传数据到PLT_ORDER_INFO_FOR_ORDERCHECK
			long begin = System.currentTimeMillis();
			String templateSql = "insert into PLT_ORDER_INFO_FOR_ORDERCHECK "
					+    "{Select REC_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,TENANT_ID,USER_ID,ORDER_STATUS,CHANNEL_STATUS "
					+    " FROM TTTTTABLENAME WHERE TENANT_ID='TTTTTNENAT_ID' AND ACTIVITY_SEQ_ID=TTTTTACTIVITY_SEQ_ID AND ORDER_STATUS ='5' AND CHANNEL_STATUS IN('0','2')}@ORDER_XCLOUD_SUFFIX";
			String replaceTenantIdSql = templateSql.replaceFirst("TTTTTNENAT_ID", tenantId);
			String replaceSourceTableName  = replaceTenantIdSql.replaceFirst("TTTTTABLENAME", tableName);
			String replaceOrderXcloudSuffix = replaceSourceTableName.replaceFirst("ORDER_XCLOUD_SUFFIX", order_xcloud_suffix);
			String replaceActivitySeqId = replaceOrderXcloudSuffix.replaceFirst("TTTTTACTIVITY_SEQ_ID", String.valueOf(activitySeqId));
			statement.execute(replaceActivitySeqId);
			log.info("====================upload sucess,工单表:{},批次:{}",tableName,activitySeqId);
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
	 * 删除行云PLT_ORDER_INFO_FOR_ORDERCHECK中的工单数据
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
			String deleteOrderInXcloud = "truncate  table  PLT_ORDER_INFO_FOR_ORDERCHECK";
			statement = connection.createStatement();
			statement.execute(deleteOrderInXcloud);
			System.out.println("====================truncate PLT_ORDER_INFO_FOR_ORDERCHECK sucess");
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

	@Override
	public void orderCheck() {
		if(begin()!=0){
			log.info("==== 事后成功检查不满足执行条件,直接返回");
			return;
		}
		
		// get ftp info from table
		FtpSrvIp = BusiToolsIns.getValueFromGlobal("HDFSSRV.IP");
		FtpUser = BusiToolsIns.getValueFromGlobal("HDFSSRV.USER");
		FtpPassowd = BusiToolsIns.getValueFromGlobal("HDFSSRV.PASSWORD");
		FtpPort = BusiToolsIns.getValueFromGlobal("HDFSSRV.PORT");
		ftpRemote = BusiToolsIns.getValueFromGlobal("ORDER_REMOTEPATH");
		ftpLocal = BusiToolsIns.getValueFromGlobal("ORDER_LOCALPATH");
		
		List<Map<String, Object>> listTenantInfo = null; // --- 有效租户信息 ---
		listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if (listTenantInfo == null || listTenantInfo.size() == 0) { // ---
			// 无有效租户纪录
			// ---
          return;
        }
		
		String tenantId = (String) listTenantInfo.get(0).get("TENANT_ID");
		
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		String currentMonth = date.format(new Date()).substring(5, 7); //获取当前月份
		
		Calendar currentTime = Calendar.getInstance();
		currentTime.setTime(new Date());
		currentTime.add(Calendar.MONTH, -1);// 
		String lastMonth = date.format(currentTime.getTime()).substring(5, 7); //上月月份
		
		// --- 准备好需要跑成功检查的表
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("PLT_ORDER_INFO_SCENEMARKET");
		tableNames.add("PLT_ORDER_INFO");
		tableNames.add("PLT_ORDER_INFO_WEIXIN");
		// tableNames.add("PLT_ORDER_INFO_POPWIN");
		tableNames.add("PLT_ORDER_INFO_ONE");
		// tableNames.add("PLT_ORDER_INFO_SMS_HIS_" + OrderInsertTime);
		tableNames.add("PLT_ORDER_INFO_CALL");
		tableNames.add("PLT_ORDER_INFO_SMALLWO");
		tableNames.add("PLT_ORDER_INFO_PLAY");
		// --PLT_ORDER_INFO_NEWWSC channelID：20--
		tableNames.add("PLT_ORDER_INFO_REMAIN");
		tableNames.add("PLT_ORDER_INFO_NEWWSC");
		tableNames.add("PLT_ORDER_INFO_HIS_" + currentMonth);
		// 添加2个月的短信历史表
		for (int phoneLastDigit = 0; phoneLastDigit < 10; phoneLastDigit++) {
			String tableName = "PLT_ORDER_INFO_SMS_HIS_" + lastMonth + phoneLastDigit;
			tableNames.add(tableName);
		}
		for (int phoneLastDigit = 0; phoneLastDigit < 10; phoneLastDigit++) {
			String tableName = "PLT_ORDER_INFO_SMS_HIS_" + currentMonth + phoneLastDigit;
			tableNames.add(tableName);
		}
		for (int i = 0; i < 10; i++) {
			tableNames.add("PLT_ORDER_INFO_POPWIN_" + String.valueOf(i));// 弹窗渠道工单表按手机尾号拆成了10张表
		}
		log.info(" ======= 事后成功检查，需要跑的成功检查的表: " + tableNames);
		initConnection();
		/*
		 * 依次处理tableNames中的表
		 * 1、 表中的数据上传到行云PLT_ORDER_INFO_FOR_ORDERCHECK中
		 * 2、 在行云上执行成功检查，获取结果
		 * 3、 下载获取结果
		 * 4、 根据结果更新工单中的数据
		 */
		for (Map<String, Object> tenantIdMap : listTenantInfo) {
			String TENANT_ID = (String) tenantIdMap.get("TENANT_ID");
			List<Integer> activitySeqIdList = new ArrayList<Integer>();
			 for (String tableName : tableNames) {
				// 上传tableName中的工单数据到行云PLT_ORDER_INFO_FOR_ORDERCHECK
				if (!uploadOrderToXcloud(TENANT_ID, tableName)) { // 如果上传数据失败了，继续下一个表的处理
					log.error("====== 事后成功检查,表：{}工单数据上传行云PLT_ORDER_INFO_FOR_ORDERCHECK出错了", tableName);
					//continue;
					//停10秒后再重试一次
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!uploadOrderToXcloud(TENANT_ID, tableName)){
						log.error("====== 事后成功检查,表：{}工单数据上传行云PLT_ORDER_INFO_FOR_ORDERCHECK出错了,已经重试1次了", tableName);
						continue;
					}
				}
				dealOrderTableName = tableName;
				activitySeqIdList = BaseMapperIns.queryAllSeqIds(tableName,TENANT_ID);
				log.info("表:{}中获取的批次:{}",tableName,activitySeqIdList);
				if(activitySeqIdList!=null && activitySeqIdList.size()==0){
					log.info("表{}中没有批次,处理下一张表",tableName);
					continue;
				}
				// 每个租户下需要做成功检查的活动
				List<ActivitySucessInfo> listTmp = new ArrayList<ActivitySucessInfo>();
				if (tableName.contains("PLT_ORDER_INFO_HIS")) {
					listTmp = BaseMapperIns.getOrderHisInfo(TENANT_ID);
				} else {
					listTmp = BaseMapperIns.getActivityForTenantId(TENANT_ID);
				}
				log.info("需要处理的批次:" + listTmp);
				// --- 根据每个活动提取活动对应的产品列表 ---
				if (listTmp != null && listTmp.size() > 0) {
//					mapActivitySucessInfo.put(TENANT_ID, listTmp);
//					for (ActivitySucessInfo item : listTmp) {
//						List<String> listTemp = BaseMapperIns.getProductListForActivity(item.getACTIVITY_SEQ_ID(),
//								TENANT_ID);
//						if (listTemp != null && listTemp.size() > 0) {
//							mapActivityProduct.put(TENANT_ID + "-" + item.getACTIVITY_SEQ_ID(), listTemp);
//						}
//					}					
					//处理每一个批次： 组装在行云上执行的事后工单成功检查的sql
					for (ActivitySucessInfo activitySucessInfo : listTmp) {
						log.info("正在处理的表:{},处理的批次:{}",tableName,activitySucessInfo.getACTIVITY_SEQ_ID());
						//按表跑成功检查时，需要判断当前表里有没有activitySucessInfo.getACTIVITY_SEQ_ID()批次，如果没有的话直接下一批次
						if(activitySeqIdList!=null && !activitySeqIdList.contains(activitySucessInfo.getACTIVITY_SEQ_ID())){
							log.info("表{}中没有批次:{},处理下一批次",tableName,activitySucessInfo.getACTIVITY_SEQ_ID());
							continue;
						}
						List<String> productList = BaseMapperIns.getProductListForActivity(activitySucessInfo.getACTIVITY_SEQ_ID(),
								TENANT_ID);
						if (productList != null && productList.size() > 0) {
							mapActivityProduct.put(TENANT_ID + "-" + activitySucessInfo.getACTIVITY_SEQ_ID(), productList);
						}
						genFileName();//生成csv文件的名称
						String orderCheckSql = getSqlForSucessType(TENANT_ID, activitySucessInfo.getACTIVITY_SEQ_ID(), activitySucessInfo); 
						if(orderCheckSql == null){
							log.info("表{},批次:{},没有成功标准，处理下一个批次",tableName,activitySucessInfo.getACTIVITY_SEQ_ID());
							continue;
						}
						if(!isExsistSuccessOrder(orderCheckSql)){  //没有成功的数,不执行下面的操作了
							log.info("表：{},批次:{},没有成功数,执行下一批次",tableName,activitySucessInfo.getACTIVITY_SEQ_ID());
							continue;
						}
						String xcloudSql = "export "+ orderCheckSql;
						xcloudSql += " ATTRIBUTE(LOCATION('";
						xcloudSql += ftpRemote;
						xcloudSql += getFileName();
						xcloudSql += ".csv')";
						xcloudSql += " SEPARATOR('|'))";
						if(xcloudSql !=null){
						   log.info("------在行云上执行的事后成功检查的sql内容:{},表：{},批次：{} " + xcloudSql,tableName,activitySucessInfo.getACTIVITY_SEQ_ID());
						    // 2、执行，获取结果
							JsonResult JsonResultIns = BusiToolsIns.execDdlOnXcloud(xcloudSql);
							if ("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false) {
								log.error("[OrderTask] 事后成功过滤sql on xcloud 执行失败");
							}else {
								log.error("[OrderTask] 事后成功检查sql on xcloud 执行成功,下一步要从FTP服务器上下载数据文件");
								//3 下载结果 download order file
								if (downLoadFile(FtpSrvIp, FtpUser, FtpPassowd, FtpPort, ftpRemote, ftpLocal) == 0) {
									log.info("Down load 事后成功检查file" + ftpRemote + getFileName() + ".csv" + " ok.");
									// split order file and load data
									String origFileName = ftpLocal + getFileName() + ".csv";
									List<String> fileList = OrderFileMannagerForOrderCheck.splitFile(origFileName, 100000, false);
									BusiToolsIns.deleteFile(origFileName);
									//4  更新MySql工单表的数据
									int perSeqIdSuccessCount = 0; //每一个批次成功的数量
									for (String fileName : fileList) {
										try {	
											List<String> userIdList = FileUtils.readLines(new File(fileName));// 活动到了成功的USER_ID
											if (userIdList != null && userIdList.size() > 0) {
												String userIdStr = generateUserIds(userIdList);
												StringBuilder sbb = new StringBuilder();
												sbb.append("UPDATE " + tableName
														+ " SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "
														+ curDateId + " ");
												sbb.append(" WHERE TENANT_ID='");
												sbb.append(TENANT_ID);
												sbb.append("'");
												sbb.append("  AND ACTIVITY_SEQ_ID =");
												sbb.append(activitySucessInfo.getACTIVITY_SEQ_ID());
												sbb.append("  AND USER_ID IN (");
												sbb.append(userIdStr);
												sbb.append(")");
												sbb.append(" AND ORDER_STATUS ='5' AND CHANNEL_STATUS IN('0','2')");
												int result = JdbcTemplateIns.update(sbb.toString());
												log.info("更新 " + tableName + "表,事后工单成功检查成功,activityseqid={},更新数量={},更新使用的文件名:{}",activitySucessInfo.getACTIVITY_SEQ_ID(),result,fileName);
												perSeqIdSuccessCount += result;
												sbb.setLength(0);		
											}
											BusiToolsIns.deleteFile(fileName);
										} catch (IOException e) {
											log.error("xxxxxxxxx 事后成功过滤在读取拆分的数据文件时报错了. 表：{},批次：{} xxxx ",tableName,activitySucessInfo.getACTIVITY_SEQ_ID());
											BusiToolsIns.deleteFile(fileName);
											e.printStackTrace();
										}

									} // end for
									log.info("更新 " + tableName + "表,事后工单成功检查成功,activityseqid={},更新数量={}",activitySucessInfo.getACTIVITY_SEQ_ID(),perSeqIdSuccessCount);
								    if(perSeqIdSuccessCount>0){
									PltCommonLogIns.setLOG_TYPE("200");
									PltCommonLogIns.setSERIAL_ID(SerialId);
									PltCommonLogIns.setSTART_TIME(dateBegin);
									PltCommonLogIns.setSPONSOR(tableName);
									PltCommonLogIns.setBUSI_CODE(activitySucessInfo.getACTIVITY_SEQ_ID()+"");
									PltCommonLogIns.setBUSI_DESC("事后成功检查成功的数量");
									PltCommonLogIns.setBUSI_ITEM_1(perSeqIdSuccessCount+"");
									BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
								   }
								} // download ok
								else {
									log.error("Down load 事后成功检查file" + ftpRemote + getFileName() + ".csv" + " 出错了.");
								}

							}
						}
					}
				}
			 }

		}
		closeConnection();
	}
	
	/**
	 * 初始化connection
	 */
	private void initConnection() {
		String driverName = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
		String url = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.URL");
		String username =  BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.USER");
		String password = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
		try{Class.forName(driverName);
		connection = DriverManager.getConnection(url,username,password);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 关闭连接
	 */
	private void closeConnection(){
		if(connection != null){
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 在行云上查询的符合成功标准的全量用户集小于500万的,可使用该方法的处理逻辑，否则，使用上面的orderCheck方法的处理逻辑(查询出的复合成功标准的
	 * 全量用户集太大的话在Mysql上更新较慢)
	 */
//	@Override
//	public void orderCheck() {
//		if(begin()!=0){
//			log.info("==== 事后成功检查不满足执行条件,直接返回");
//			return;
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
//		List<Map<String, Object>> listTenantInfo = null; // --- 有效租户信息 ---
//		listTenantInfo = BusiToolsIns.getValidTenantInfo();
//		if (listTenantInfo == null || listTenantInfo.size() == 0) { // ---
//			// 无有效租户纪录
//			// ---
//          return;
//        }
//		
//		String tenantId = (String) listTenantInfo.get(0).get("TENANT_ID");
//		
//		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
//		String currentMonth = date.format(new Date()).substring(5, 7); //获取当前月份
//		
//		Calendar currentTime = Calendar.getInstance();
//		currentTime.setTime(new Date());
//		currentTime.add(Calendar.MONTH, -1);// 
//		String lastMonth = date.format(currentTime.getTime()).substring(5, 7); //上月月份
//		ExecutorService service = Global.getExecutorService();
//	    initConnection();
//		// --- 准备好需要跑成功检查的表
//		/*
//		 * 1、 在行云上执行成功检查，获取结果
//		 * 2、 下载获取结果
//		 * 3、 根据结果更新工单中的数据
//		 */
//		for (Map<String, Object> tenantIdMap : listTenantInfo) {
//			String TENANT_ID = (String) tenantIdMap.get("TENANT_ID");
//				// 每个租户下需要做成功检查的活动
//				List<ActivitySucessInfo> listTmp = new ArrayList<ActivitySucessInfo>();
//				listTmp = BaseMapperIns.getActivityForTenantId(TENANT_ID);
//				// --- 根据每个活动提取活动对应的产品列表 ---
//				if (listTmp != null && listTmp.size() > 0) {				
//					//处理每一个批次： 组装在行云上执行的事后工单成功检查的sql
//					for (ActivitySucessInfo activitySucessInfo : listTmp) {
//						List<String> productList = BaseMapperIns.getProductListForActivity(activitySucessInfo.getACTIVITY_SEQ_ID(),
//								TENANT_ID);
//						if (productList != null && productList.size() > 0) {
//							mapActivityProduct.put(TENANT_ID + "-" + activitySucessInfo.getACTIVITY_SEQ_ID(), productList);
//						}
//						List<String> channelIdList = BaseMapperIns.queryChannelIdsBySeqId(activitySucessInfo.getACTIVITY_SEQ_ID(),TENANT_ID);
//						log.info("批次：{}下的渠道有:{}",activitySucessInfo.getACTIVITY_SEQ_ID(),channelIdList);
//						//该批次下跑完成功检查需要更新的工单表
//						List<String> orderTableNames = getOrderTableNamesByChannelId(channelIdList);
//						log.info("批次：{}需要跑成功标准的表有:{}",activitySucessInfo.getACTIVITY_SEQ_ID(),orderTableNames);
//						genFileName();//生成csv文件的名称
//						//全量查询方式对应的sql
//						String orderCheckSql = getSqlForSucessTypeV2(TENANT_ID, activitySucessInfo.getACTIVITY_SEQ_ID(), activitySucessInfo); 
//						if(orderCheckSql == null){
//							continue;
//						}
//						//先查询符合该批次成功标准的全量用户的数量,如果数量为0执行下一个批次，如果数量大于500万,把工单表的数据同步到行云上,查询出成功的用户后再更新工单表
//						int successCount = getSuccessCount(orderCheckSql);
//						log.info("批次：{},符合成功标准的全量用户有:{}",activitySucessInfo.getACTIVITY_SEQ_ID(),successCount);
//						if(successCount == 0){
//							log.info("批次：{},没有符合成功标准的全量用户,执行下一个批次",activitySucessInfo.getACTIVITY_SEQ_ID());
//							continue;
//						}
//					    if(successCount <5000000){  //如果符合成功标准的全量用户集数量小于500万,继续往下执行
//						String xcloudSql = "export "+ orderCheckSql;
//						xcloudSql += " ATTRIBUTE(LOCATION('";
//						xcloudSql += ftpRemote;
//						xcloudSql += getFileName();
//						xcloudSql += ".csv')";
//						xcloudSql += " SEPARATOR('|'))";
//						if(xcloudSql !=null){
//						   log.info("------在行云上执行的事后成功检查的sql内容:{},批次：{} ", xcloudSql,activitySucessInfo.getACTIVITY_SEQ_ID());
//						    // 1、执行，获取结果
//							JsonResult JsonResultIns = BusiToolsIns.execDdlOnXcloud(xcloudSql);
//							if ("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false) {
//								log.error("[OrderTask] 事后成功过滤sql on xcloud 执行失败");
//							}else {
//								log.error("[OrderTask] 事后成功检查sql on xcloud 执行成功,下一步要从FTP服务器上下载数据文件");
//								//2 下载结果 download order file
//								if (downLoadFile(FtpSrvIp, FtpUser, FtpPassowd, FtpPort, ftpRemote, ftpLocal) == 0) {
//									log.info("Down load 事后成功检查file" + ftpRemote + getFileName() + ".csv" + " ok.");
//									// split order file and load data
//									String origFileName = ftpLocal + getFileName() + ".csv";
//									List<String> fileList = OrderFileMannagerForOrderCheck.splitFile(origFileName, 100000, false);
//									BusiToolsIns.deleteFile(origFileName);
//									//3  更新MySql工单表的数据
//									for (String fileName : fileList) {
//										try {
//											List<String> userIdList = FileUtils.readLines(new File(fileName));// 活动到了成功的USER_ID
//											if (userIdList != null && userIdList.size() > 0) {
//												String userIdStr = generateUserIds(userIdList);
//												//控制下面线程的执行顺序，子任务必须全部执行完才能继续往下执行
//												CountDownLatch  cdl = new CountDownLatch(orderTableNames.size());
//											 for (String tableName : orderTableNames) {
//												OrderCheckUpdateTableTask task = new OrderCheckUpdateTableTask();
//												task.setTableName(tableName);
//												task.setTENANT_ID(TENANT_ID);
//												task.setCurDateId(curDateId);
//												task.setActivitySeqId(activitySucessInfo.getACTIVITY_SEQ_ID());
//												task.setJdbcTemplateIns(JdbcTemplateIns);
//												task.setFileName(fileName);
//												task.setUserIdStr(userIdStr);
//												task.setCdl(cdl);
//												service.execute(task);
////												StringBuilder sbb = new StringBuilder();
////												sbb.append("UPDATE " + tableName
////														+ " SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "
////														+ curDateId + " ");
////												sbb.append(" WHERE TENANT_ID='");
////												sbb.append(TENANT_ID);
////												sbb.append("'");
////												sbb.append("  AND ACTIVITY_SEQ_ID =");
////												sbb.append(activitySucessInfo.getACTIVITY_SEQ_ID());
////												sbb.append("  AND USER_ID IN (");
////												sbb.append(userIdStr);
////												sbb.append(")");
////												sbb.append(" AND ORDER_STATUS ='5' AND CHANNEL_STATUS IN('0','2')");
////												int result = JdbcTemplateIns.update(sbb.toString());
////												log.info("更新 " + tableName + "表,事后工单成功检查成功,activityseqid={},更新数量={},更新使用的文件名:{}",activitySucessInfo.getACTIVITY_SEQ_ID(),result,fileName);
////												sbb.setLength(0);	
//											}
//											 try {
//												cdl.await();
//											} catch (InterruptedException e) {
//												e.printStackTrace();
//											}
//										  }
//											BusiToolsIns.deleteFile(fileName);
//										} catch (IOException e) {
//											log.error("xxxxxxxxx 事后成功过滤在读取拆分的数据文件时报错了. 批次：{} xxxx ",activitySucessInfo.getACTIVITY_SEQ_ID());
//											BusiToolsIns.deleteFile(fileName);
//											e.printStackTrace();
//										}
//
//									} // end for
//								} // download ok
//								else {
//									log.error("Down load 事后成功检查file" + ftpRemote + getFileName() + ".csv" + " 出错了.");
//								}
//							}
//						 }
//					   }else{
//						   log.info("批次：{},符合成功标准的全量用户超过500w了,使用上传工单到行云表的方式",activitySucessInfo.getACTIVITY_SEQ_ID());
//						    for(String tableName : orderTableNames){
//						    	// 上传tableName中的工单数据到行云PLT_ORDER_INFO_FOR_ORDERCHECK
//								if (!uploadOrderToXcloudV2(TENANT_ID, tableName,activitySucessInfo.getACTIVITY_SEQ_ID())) { // 如果上传数据失败了，继续下一个表的处理
//									log.error("====== 事后成功检查,表：{},批次:{},工单数据上传行云PLT_ORDER_INFO_FOR_ORDERCHECK出错了", tableName,activitySucessInfo.getACTIVITY_SEQ_ID());
//									continue;
//								}
//								genFileName();//生成csv文件的名称
//								//此处要使用getSqlForSucessType方法:非全量方式
//								orderCheckSql = getSqlForSucessType(TENANT_ID, activitySucessInfo.getACTIVITY_SEQ_ID(), activitySucessInfo); 
//								if(orderCheckSql == null){
//									continue;
//								}
//								if(!isExsistSuccessOrder(orderCheckSql)){  //没有成功的数,不执行下面的操作了
//									continue;
//								}
//								String xcloudSql = "export "+ orderCheckSql;
//								xcloudSql += " ATTRIBUTE(LOCATION('";
//								xcloudSql += ftpRemote;
//								xcloudSql += getFileName();
//								xcloudSql += ".csv')";
//								xcloudSql += " SEPARATOR('|'))";
//								executeXcloudSqlAndUpdateMySqlOrder(xcloudSql,activitySucessInfo.getACTIVITY_SEQ_ID(),tableName,TENANT_ID);
//						    }
//					   }
//					}
//				}
//		    }
//		
//		//单独处理 "PLT_ORDER_INFO_HIS_" + currentMonth 表
//		for (Map<String, Object> tenantIdMap : listTenantInfo) {
//			String TENANT_ID = (String) tenantIdMap.get("TENANT_ID");
//			    String tableName =  "PLT_ORDER_INFO_HIS_" + currentMonth;
//				// 每个租户下需要做成功检查的活动
//				List<ActivitySucessInfo> listTmp = new ArrayList<ActivitySucessInfo>();
//				listTmp = BaseMapperIns.getOrderHisInfo(TENANT_ID);
//				// --- 根据每个活动提取活动对应的产品列表 ---
//				if (listTmp != null && listTmp.size() > 0) {				
//					//处理每一个批次： 组装在行云上执行的事后工单成功检查的sql
//					for (ActivitySucessInfo activitySucessInfo : listTmp) {
//						List<String> productList = BaseMapperIns.getProductListForActivity(activitySucessInfo.getACTIVITY_SEQ_ID(),
//								TENANT_ID);
//						if (productList != null && productList.size() > 0) {
//							mapActivityProduct.put(TENANT_ID + "-" + activitySucessInfo.getACTIVITY_SEQ_ID(), productList);
//						}
//						genFileName();//生成csv文件的名称
//						String orderCheckSql = getSqlForSucessTypeV2(TENANT_ID, activitySucessInfo.getACTIVITY_SEQ_ID(), activitySucessInfo); 
//						if(orderCheckSql == null){
//							continue;
//						}
//						if(!isExsistSuccessOrder(orderCheckSql)){  //没有成功的数,不执行下面的操作了
//							continue;
//						}
//						String xcloudSql = "export "+ orderCheckSql;
//						xcloudSql += " ATTRIBUTE(LOCATION('";
//						xcloudSql += ftpRemote;
//						xcloudSql += getFileName();
//						xcloudSql += ".csv')";
//						xcloudSql += " SEPARATOR('|'))";
//						if(xcloudSql !=null){
//						executeXcloudSqlAndUpdateMySqlOrder(xcloudSql,activitySucessInfo.getACTIVITY_SEQ_ID(),tableName,TENANT_ID); 
//						}
//					}
//				}
//		    }
//		closeConnection();
//	}
	
	/**
	 * 执行行云sql,下载结果，并更新Mysql的工单表状态
	 */
	void executeXcloudSqlAndUpdateMySqlOrder(String xcloudSql,int activitySeqId,String tableName,String TENANT_ID){
		log.info("------在行云上执行的事后成功检查的sql内容:{},批次：{} ", xcloudSql,activitySeqId);
	    // 1、执行，获取结果
		JsonResult JsonResultIns = BusiToolsIns.execDdlOnXcloud(xcloudSql);
		if ("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false) {
			log.error("[OrderTask] 事后成功过滤sql on xcloud 执行失败");
		}else {
			log.error("[OrderTask] 事后成功检查sql on xcloud 执行成功,下一步要从FTP服务器上下载数据文件");
			//2 下载结果 download order file
			if (downLoadFile(FtpSrvIp, FtpUser, FtpPassowd, FtpPort, ftpRemote, ftpLocal) == 0) {
				log.info("Down load 事后成功检查file" + ftpRemote + getFileName() + ".csv" + " ok.");
				// split order file and load data
				String origFileName = ftpLocal + getFileName() + ".csv";
				List<String> fileList = OrderFileMannagerForOrderCheck.splitFile(origFileName, 100000, false);
				BusiToolsIns.deleteFile(origFileName);
				//3  更新MySql工单表的数据
				for (String fileName : fileList) {
					try {	
						List<String> userIdList = FileUtils.readLines(new File(fileName));// 活动到了成功的USER_ID
						if (userIdList != null && userIdList.size() > 0) {
							String userIdStr = generateUserIds(userIdList);
							StringBuilder sbb = new StringBuilder();
							sbb.append("UPDATE " + tableName
									+ " SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "
									+ curDateId + " ");
							sbb.append(" WHERE TENANT_ID='");
							sbb.append(TENANT_ID);
							sbb.append("'");
							sbb.append("  AND ACTIVITY_SEQ_ID =");
							sbb.append(activitySeqId);
							sbb.append("  AND USER_ID IN (");
							sbb.append(userIdStr);
							sbb.append(")");
							sbb.append(" AND ORDER_STATUS ='5' AND CHANNEL_STATUS IN('0','2')");
							int result = JdbcTemplateIns.update(sbb.toString());
							log.info("更新 " + tableName + "表,事后工单成功检查成功,activityseqid={},更新数量={},更新使用的文件名:{}",activitySeqId,result,fileName);
							sbb.setLength(0);	
					  }
						BusiToolsIns.deleteFile(fileName);
					} catch (IOException e) {
						log.error("xxxxxxxxx 事后成功过滤在读取拆分的数据文件时报错了. 批次：{} xxxx ",activitySeqId);
						BusiToolsIns.deleteFile(fileName);
						e.printStackTrace();
					}

				} // end for
			} // download ok
			else {
				log.error("Down load 事后成功检查file" + ftpRemote + getFileName() + ".csv" + " 出错了.");
			}
		}
	}
	
	/**
	 * 根据渠道集合查询需要更新的工单表(包含历史表)
	 * @param channelIdList
	 * @return
	 */
	private List<String> getOrderTableNamesByChannelId(List<String> channelIdList) {
		
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		String currentMonth = date.format(new Date()).substring(5, 7); //获取当前月份
		
		Calendar currentTime = Calendar.getInstance();
		currentTime.setTime(new Date());
		currentTime.add(Calendar.MONTH, -1);// 
		String lastMonth = date.format(currentTime.getTime()).substring(5, 7); //上月月份
		
		List<String> tables = new ArrayList<String>();
		tables.add("PLT_ORDER_INFO_SCENEMARKET");//场景营销
		tables.add("PLT_ORDER_INFO_REMAIN");     //留存表
		for(String channelId: channelIdList){
			if(channelId.equals("5")){
				tables.add("PLT_ORDER_INFO");
			}
			if(channelId.equals("11")){
				tables.add("PLT_ORDER_INFO_WEIXIN");
			}
			if(channelId.equals("1")||channelId.equals("2")||channelId.equals("9")){
				if(!tables.contains("PLT_ORDER_INFO_ONE")){
					tables.add("PLT_ORDER_INFO_ONE");
				}
			}
			if(channelId.equals("14")){
				tables.add("PLT_ORDER_INFO_CALL");
			}
			if(channelId.equals("13")){
				tables.add("PLT_ORDER_INFO_SMALLWO");
			}
			if(channelId.equals("19")){
				tables.add("PLT_ORDER_INFO_PLAY");
			}
			if(channelId.equals("20")){
				tables.add("PLT_ORDER_INFO_NEWWSC");
			}
			if(channelId.indexOf("8") != -1){
				for (int i = 0; i < 10; i++) {
					tables.add("PLT_ORDER_INFO_POPWIN_" + String.valueOf(i));// 弹窗渠道工单表按手机尾号拆成了10张表
				}
			}
			if(channelId.equals("7")){
				// 添加2个月的短信历史表			
				for (int phoneLastDigit = 0; phoneLastDigit < 10; phoneLastDigit++) {
					String tableName = "PLT_ORDER_INFO_SMS_HIS_" + lastMonth + phoneLastDigit;
					tables.add(tableName);
				}
				for (int phoneLastDigit = 0; phoneLastDigit < 10; phoneLastDigit++) {
					String tableName = "PLT_ORDER_INFO_SMS_HIS_" + currentMonth + phoneLastDigit;
					tables.add(tableName);
				}
			}
		}
		return tables;
	}
	/**
	 * 在行云上执行事后成功检查的sql,如果有成功的数,返回ture,如果成功的数是0则返回false
	 * @param sql
	 * @return
	 */
	boolean isExsistSuccessOrder(String sql){
		boolean exsit = true;	
		Statement statement =null;
		try{
			if(connection!=null && connection.isClosed()){  //如果连接断了，重新创建一个连接
				log.info("isExsistSuccessOrder 方法里的connection连接已关闭，重新创建连接...");
				String driverName = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
				String url = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.URL");
				String username =  BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.USER");
				String password = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
				Class.forName(driverName);
				connection = DriverManager.getConnection(url , 
						username, 
						password);
			}		
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			if(rs.next()){
				exsit = true;
				return true;
			}else{
				exsit = false;
				return false;
			}
		   }catch(Exception ex){
			   ex.printStackTrace();
			   exsit = false;
		   }finally{
			   try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  }
		
		return exsit;
	}
	
	/**
	 * 查询满足成功标注的用户的数量
	 * @param sql
	 * @return
	 */
	int getSuccessCount(String sql){
		int count = 0;
		Statement statement =null;
		try{	
			if(connection!=null && connection.isClosed()){  //如果连接断了，重新创建一个连接
				log.info("isExsistSuccessOrder 方法里的connection连接已关闭，重新创建连接...");
				String driverName = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
				String url = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.URL");
				String username =  BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.USER");
				String password = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
				Class.forName(driverName);
				connection = DriverManager.getConnection(url , 
						username, 
						password);
			}	
			statement = connection.createStatement();
			String querySuccessCountSql = " SELECT COUNT(1) AS SUCCESS_COUNT FROM (" + sql+ ")";
			ResultSet rs = statement.executeQuery(querySuccessCountSql);
			while(rs.next()){
				count = rs.getInt("SUCCESS_COUNT");
			}
			}catch(Exception ex){
			   ex.printStackTrace();
		   }finally{
			   try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  }
		return count;
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
	
	public void genFileName() {
		String name = "ordersuccesscheck_tmp_";
		Format format = new SimpleDateFormat("yyyyMMddHHmmss");
		name += format.format(new Date());
		this.fileName = name;
	}
	
	private String getSqlForSucessType(String tenant_id, int activity_seq_id, ActivitySucessInfo activitySucessInfo) {
		//--log--
		PltCommonLog logdb = new PltCommonLog();
		logdb.setLOG_TYPE("01");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("orderCheck");
		logdb.setBUSI_ITEM_2("" + activity_seq_id);
		logdb.setBUSI_CODE("getSqlForSucessType");
		// --- 得到活动成功信息 ---
		ActivitySucessInfo ActivitySucessInfoIns = activitySucessInfo;
		if (ActivitySucessInfoIns == null){
			logdb.setBUSI_DESC("该活动成功标准检查无成功标准ActivitySucessInfo");
			BusiToolsIns.insertPltCommonLog(logdb);
			return null;
		}

		if (StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false) {
			logdb.setBUSI_DESC("该活动成功标准检查无成功类型SucessType");
			BusiToolsIns.insertPltCommonLog(logdb);
			return null;
		}
		// if(ActivitySucessInfoIns.getSucessType() == null) return null;
		boolean bProduct = false; // --- 是否成功标准中有产品 ---
		StringBuilder sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		String OrderCreateTime;
		if (ActivitySucessInfoIns.getLastOrderCreateTime() != null) {
			OrderCreateTime = sdfYmd.format(ActivitySucessInfoIns.getLastOrderCreateTime());
		} else {
			OrderCreateTime = sdfYm.format(new Date()) + "-01";
		}


		String isHaveRenewProduct = ActivitySucessInfoIns.getIsHaveRenewProduct();
		if(isHaveRenewProduct ==null){
			isHaveRenewProduct = "0";
		} 
		if (StringUtils.isNotNull(ActivitySucessInfoIns.getMatchingType())) {
			if (ActivitySucessInfoIns.getMatchingType().equals("2")
					&& isHaveRenewProduct.equals("0")) { // ---
																					// 精确匹配产品
																					// ---
				// --- 提取活动成功产品列表 ---
				List<String> listProductCode = mapActivityProduct
						.get(tenant_id + "-" + ActivitySucessInfoIns.getACTIVITY_SEQ_ID());
				bProduct = true;
				int i = 0;
				if (listProductCode != null && listProductCode.size() > 0) { // ---
																				// 得到SQL
																				// ---

					sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					for (i = 0; i < listProductCode.size(); ++i) {
						if (i > 0)
							sbProduct.append(",");
						sbProduct.append("'");
						sbProduct.append(listProductCode.get(i));
						sbProduct.append("'");
					}
					sbProduct.append(")");
					sbProduct.append(" AND ACCEPTED_DATE > '");
					sbProduct.append(OrderCreateTime);
					sbProduct.append("'  ");
				} else {
					log.info("精准匹配产品时，无产品编码");
					logdb.setBUSI_CODE("getSqlForSucessType,精准匹配产品时，无产品编码");
					BusiToolsIns.insertPltCommonLog(logdb);
					return null;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		String strAddSql = null;
		String strTmp = null;
		if (StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())) {
			strAddSql = ActivitySucessInfoIns.getSucessConSql().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		if (StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())) {
			strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		String successType = ActivitySucessInfoIns.getSucessType();
		// --add--
		/*************** 20171225 事后成功检查改造,被注释的代码块 **********************/
//		StringBuilder userIdForCheck = new StringBuilder();// --MySQL表中的USERID--
//		for (int i = 0; i < listOrderCheckInfo.size(); ++i) {
//			if (i > 0)
//				userIdForCheck.append(",");
//			userIdForCheck.append("'");
//			userIdForCheck.append(listOrderCheckInfo.get(i).getUserId());
//			userIdForCheck.append("'");
//		}
		/*************** 20171225 事后成功检查改造,被注释的代码块 **********************/
		String  userIdForCheck = "SELECT USER_ID FROM PLT_ORDER_INFO_FOR_ORDERCHECK WHERE ACTIVITY_SEQ_ID="+activity_seq_id;
		// --add end--
		// 精准营销时
		if (bProduct) {
			if (strTmp != null) {
				sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
				sbProduct.append("(" + strTmp + ")");
				sbProduct.append(" AND ");
				sbProduct.append(" DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'");
				/**********  20171227 改用全量的方式 *****************/
				sbProduct.append(" AND USER_ID IN (");
				sbProduct.append(userIdForCheck);
				sbProduct.append(")");
				/**********  20171227 改用全量的方式 *****************/ 
				if (strAddSql != null) {
					sbProduct.append(" AND " + "(" + strAddSql + ")");
				}
				sbProduct.append(" ) ");
			} else { // --- 成功标准类型中，无成功标准 如3,4,5
				if (strAddSql != null) {
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(" DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'");
					/**********  20171227 改用全量的方式 *****************/
					sbProduct.append(" AND USER_ID IN (");
					sbProduct.append(userIdForCheck);
					sbProduct.append(")");
					/**********  20171227 改用全量的方式 *****************/
					sbProduct.append(" AND " + "(" + strAddSql + ")");
					sbProduct.append(" ) ");
					//sbProduct.append(" ) ");   //经确认，此右括号有问题
				}
			}
			log.info("精准营销：");

		} else {
			// 无精准营销但有成功标准
			if (strTmp != null) {
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
				sb.append("(" + strTmp + ")");
				sb.append(" AND DATE_ID =  '");
				sb.append(curDateId);
				sb.append("'");
				/**********  20171227 改用全量的方式 *****************/
				sb.append(" AND USER_ID IN (");
				sb.append(userIdForCheck);
				sb.append(")");
				/**********  20171227 改用全量的方式 *****************/
				if (strAddSql != null) {
					sb.append(" AND " + "(" + strAddSql + ")");
				}

			} else {
				if (strAddSql != null) {// --- 无成功标准 ---
					if (successType.equals("3") || successType.equals("4") || successType.equals("5")) { // ---
																											// 成功标准类型在3,4,5之间时
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
						sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
						sb.append("(" + strAddSql + ")");
						sb.append(" AND DATE_ID = '");
						sb.append(curDateId);
						sb.append("' ");
						/**********  20171227 改用全量的方式 *****************/
						sb.append(" AND USER_ID IN (");
						sb.append(userIdForCheck);
						sb.append(")");
						/**********  20171227 改用全量的方式 *****************/
						sb.append(")");
					} else {
						sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
						sb.append("(" + strAddSql + ")");
						sb.append(" AND DATE_ID = '");
						sb.append(curDateId);
						sb.append("' ");
						/**********  20171227 改用全量的方式 *****************/
						sb.append(" AND USER_ID IN (");
						sb.append(userIdForCheck);
						sb.append(")");
						/**********  20171227 改用全量的方式 *****************/

					}
				} else { // --- 全没有时 ---
					log.info("此产品不存在或者没有成功标准可以判断");
					logdb.setBUSI_CODE("getSqlForSucessType,成功标准条件附加条件为空，此产品不存在或者没有成功标准可以判断");
					BusiToolsIns.insertPltCommonLog(logdb);
					return null;
				}
			}
		}

		log.info("工单检查sql=" + sb.toString() + sbProduct.toString() + ", 成功类型=" + successType);

		// 一条成功标准sql插入一条日志
		logdb.setBUSI_DESC("成功标准检查SusccessSql");

		String tmp = sb.toString()+sbProduct.toString();
		if(tmp.length()>1024){
			String tmp1 = tmp.substring(0, 1024);
			logdb.setBUSI_ITEM_1(tmp1);
		}else {
			logdb.setBUSI_ITEM_1(tmp);
		}
		logdb.setBUSI_CODE("getSqlForSucessType,成功类型="+successType+"精确匹配："+bProduct);
		BusiToolsIns.insertPltCommonLog(logdb);

		if (bProduct) {
			return sbProduct.toString();
		} else {
			return sb.toString();
		}
	}
	
	/**
	 * 行云上全量跑成功标准的查询USER_ID的方法： 与getSqlForSucessType不同的地方在是否有userIdForCheck条件，本方法没有。
	 * @param tenant_id
	 * @param activity_seq_id
	 * @param activitySucessInfo
	 * @return
	 */
	private String getSqlForSucessTypeV2(String tenant_id, int activity_seq_id, ActivitySucessInfo activitySucessInfo) {
		//--log--
		PltCommonLog logdb = new PltCommonLog();
		logdb.setLOG_TYPE("01");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("orderCheck");
		logdb.setBUSI_ITEM_2("" + activity_seq_id);
		logdb.setBUSI_CODE("getSqlForSucessType");
		// --- 得到活动成功信息 ---
		ActivitySucessInfo ActivitySucessInfoIns = activitySucessInfo;
		if (ActivitySucessInfoIns == null){
			logdb.setBUSI_DESC("该活动成功标准检查无成功标准ActivitySucessInfo");
			BusiToolsIns.insertPltCommonLog(logdb);
			return null;
		}

		if (StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false) {
			logdb.setBUSI_DESC("该活动成功标准检查无成功类型SucessType");
			BusiToolsIns.insertPltCommonLog(logdb);
			return null;
		}
		// if(ActivitySucessInfoIns.getSucessType() == null) return null;
		boolean bProduct = false; // --- 是否成功标准中有产品 ---
		StringBuilder sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		String OrderCreateTime;
		if (ActivitySucessInfoIns.getLastOrderCreateTime() != null) {
			OrderCreateTime = sdfYmd.format(ActivitySucessInfoIns.getLastOrderCreateTime());
		} else {
			OrderCreateTime = sdfYm.format(new Date()) + "-01";
		}


		String isHaveRenewProduct = ActivitySucessInfoIns.getIsHaveRenewProduct();
		if(isHaveRenewProduct ==null){
			isHaveRenewProduct = "0";
		} 
		if (StringUtils.isNotNull(ActivitySucessInfoIns.getMatchingType())) {
			if (ActivitySucessInfoIns.getMatchingType().equals("2")
					&& isHaveRenewProduct.equals("0")) { // ---
																					// 精确匹配产品
																					// ---
				// --- 提取活动成功产品列表 ---
				List<String> listProductCode = mapActivityProduct
						.get(tenant_id + "-" + ActivitySucessInfoIns.getACTIVITY_SEQ_ID());
				bProduct = true;
				int i = 0;
				if (listProductCode != null && listProductCode.size() > 0) { // ---
																				// 得到SQL
																				// ---

					sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					for (i = 0; i < listProductCode.size(); ++i) {
						if (i > 0)
							sbProduct.append(",");
						sbProduct.append("'");
						sbProduct.append(listProductCode.get(i));
						sbProduct.append("'");
					}
					sbProduct.append(")");
					sbProduct.append(" AND ACCEPTED_DATE > '");
					sbProduct.append(OrderCreateTime);
					sbProduct.append("'  ");
				} else {
					log.info("精准匹配产品时，无产品编码");
					logdb.setBUSI_CODE("getSqlForSucessType,精准匹配产品时，无产品编码");
					BusiToolsIns.insertPltCommonLog(logdb);
					return null;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		String strAddSql = null;
		String strTmp = null;
		if (StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())) {
			strAddSql = ActivitySucessInfoIns.getSucessConSql().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		if (StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())) {
			strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		String successType = ActivitySucessInfoIns.getSucessType();
		// --add--
		/*************** 20171225 事后成功检查改造,被注释的代码块 **********************/
//		StringBuilder userIdForCheck = new StringBuilder();// --MySQL表中的USERID--
//		for (int i = 0; i < listOrderCheckInfo.size(); ++i) {
//			if (i > 0)
//				userIdForCheck.append(",");
//			userIdForCheck.append("'");
//			userIdForCheck.append(listOrderCheckInfo.get(i).getUserId());
//			userIdForCheck.append("'");
//		}
		/*************** 20171225 事后成功检查改造,被注释的代码块 **********************/
		//String  userIdForCheck = "SELECT USER_ID FROM PLT_ORDER_INFO_FOR_ORDERCHECK WHERE ACTIVITY_SEQ_ID="+activity_seq_id;
		// --add end--
		// 精准营销时
		if (bProduct) {
			if (strTmp != null) {
				sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
				sbProduct.append("(" + strTmp + ")");
				sbProduct.append(" AND ");
				sbProduct.append(" DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'");
				/**********  20171227 改用全量的方式 *****************/
				//sbProduct.append(" AND USER_ID IN (");
				//sbProduct.append(userIdForCheck);
				//sbProduct.append(")");
				/**********  20171227 改用全量的方式 *****************/ 
				if (strAddSql != null) {
					sbProduct.append(" AND " + "(" + strAddSql + ")");
				}
				sbProduct.append(" ) ");
			} else { // --- 成功标准类型中，无成功标准 如3,4,5
				if (strAddSql != null) {
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(" DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'");
					/**********  20171227 改用全量的方式 *****************/
					//sbProduct.append(" AND USER_ID IN (");
					//sbProduct.append(userIdForCheck);
					//sbProduct.append(")");
					/**********  20171227 改用全量的方式 *****************/
					sbProduct.append(" AND " + "(" + strAddSql + ")");
					sbProduct.append(" ) ");
					sbProduct.append(" ) ");
				}
			}
			log.info("精准营销：");

		} else {
			// 无精准营销但有成功标准
			if (strTmp != null) {
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
				sb.append("(" + strTmp + ")");
				sb.append(" AND DATE_ID =  '");
				sb.append(curDateId);
				sb.append("'");
				/**********  20171227 改用全量的方式 *****************/
				//sb.append(" AND USER_ID IN (");
				//sb.append(userIdForCheck);
				//sb.append(")");
				/**********  20171227 改用全量的方式 *****************/
				if (strAddSql != null) {
					sb.append(" AND " + "(" + strAddSql + ")");
				}

			} else {
				if (strAddSql != null) {// --- 无成功标准 ---
					if (successType.equals("3") || successType.equals("4") || successType.equals("5")) { // ---
																											// 成功标准类型在3,4,5之间时
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
						sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
						sb.append("(" + strAddSql + ")");
						sb.append(" AND DATE_ID = '");
						sb.append(curDateId);
						sb.append("' ");
						/**********  20171227 改用全量的方式 *****************/
						//sb.append(" AND USER_ID IN (");
						//sb.append(userIdForCheck);
						//sb.append(")");
						/**********  20171227 改用全量的方式 *****************/
						sb.append(")");
					} else {
						sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
						sb.append("(" + strAddSql + ")");
						sb.append(" AND DATE_ID = '");
						sb.append(curDateId);
						sb.append("' ");
						/**********  20171227 改用全量的方式 *****************/
						//sb.append(" AND USER_ID IN (");
						//sb.append(userIdForCheck);
						//sb.append(")");
						/**********  20171227 改用全量的方式 *****************/

					}
				} else { // --- 全没有时 ---
					log.info("此产品不存在或者没有成功标准可以判断");
					logdb.setBUSI_CODE("getSqlForSucessType,成功标准条件附加条件为空，此产品不存在或者没有成功标准可以判断");
					BusiToolsIns.insertPltCommonLog(logdb);
					return null;
				}
			}
		}

		log.info("工单检查sql=" + sb.toString() + sbProduct.toString() + ", 成功类型=" + successType);

		// 一条成功标准sql插入一条日志
		logdb.setBUSI_DESC("成功标准检查SusccessSql");

		String tmp = sb.toString()+sbProduct.toString();
		if(tmp.length()>1024){
			String tmp1 = tmp.substring(0, 1024);
			logdb.setBUSI_ITEM_1(tmp1);
		}else {
			logdb.setBUSI_ITEM_1(tmp);
		}
		logdb.setBUSI_CODE("getSqlForSucessType,成功类型="+successType+"精确匹配："+bProduct);
		BusiToolsIns.insertPltCommonLog(logdb);

		if (bProduct) {
			return sbProduct.toString();
		} else {
			return sb.toString();
		}
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
			log.error("Down load 事后成功检查的数据文件出错" + ftpRtn);
		return downRtn;
	}
	
	public int begin() {
		// --- 纪录开始时间 ---
		dateBegin = new Date();
		// --- 提取有效租户信息 ---
		List<Map<String, Object>> listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if (listTenantInfo == null || listTenantInfo.size() == 0) { // ---
			// 无有效租户纪录
			// ---
			log.error("--- 无有效租户纪录---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，无有效租户纪录");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		// --- 设置当前租户编号 ---
		curTenantId = (String) listTenantInfo.get(0).get("TENANT_ID");
		// --- 得到序列号 ---
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		// --- 纪录日志 ---
		PltCommonLogIns.setLOG_TYPE("01");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setSPONSOR("ORDERCHECK");
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("工单检查开始");
		PltCommonLogIns.setBUSI_ITEM_1(curTenantId);
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		PltCommonLogIns.setBUSI_ITEM_10(null);
		// --- 得到最大帐期 ---
		curDateId = BaseMapperIns.getMaxDateId();
		if (curDateId == null) {
			log.warn("--- 取当前帐期失败,得到的是空值 ---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，curDateid为空");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		return 0;
	}

}
