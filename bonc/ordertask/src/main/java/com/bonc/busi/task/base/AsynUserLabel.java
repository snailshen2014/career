package com.bonc.busi.task.base;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.orderschedule.utils.ServiceAlert;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.thread.ThreadBaseFunction;
import com.bonc.common.base.JsonResult;

@Service("AsynUserLabel")
//@Transactional
public class AsynUserLabel extends ThreadBaseFunction{
	@Autowired
	private BusiTools  AsynDataIns;
	@Autowired
	private BaseMapper  TaskBaseMapperDao;
	@Autowired
	ServiceAlert serviceAlert;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private final static Logger log= LoggerFactory.getLogger(AsynUserLabel.class);
	//private	Map<String, Object>  mapGlobalTenantInfo = null;
	
	//public	void setTenantInfo(Map<String, Object> mapTenantInfo){
	//	this.mapGlobalTenantInfo = mapTenantInfo;
	//}
	//@Override
	public	int handleDataDirect(Object data){
		// --- 从全局类中得到当前租户变量 ---
		Map<String,Object>  mapTenantInfo = (Map<String,Object>)data;
		
		// --- 首先判断是否已经有进程在处理 ---
		String		strRunFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.RUN.FLAG."+(String)mapTenantInfo.get("TENANT_ID"));
		if(strRunFlag != null){
			if(strRunFlag.equals("TRUE")){
				log.warn("--- 租户：{} 同步进程正在运行 ---",(String)mapTenantInfo.get("TENANT_ID"));
				return -1;
			}
		}
		strRunFlag = null;   // --- 释放字符串 ---
		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog		PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("00");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSPONSOR("USERLABELASYN."+(String)mapTenantInfo.get("TENANT_ID") +"."+
				Thread.currentThread().getName());
		// --- 判断行云的数据是否准备好 ---
		// --- 提取行云的数据 ---
		String		dbDateId = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.DATEID."+
									(String)mapTenantInfo.get("TENANT_ID"));
		String		strCurMonthDay = TaskBaseMapperDao.getMaxDateId();					
		if(strCurMonthDay == null || strCurMonthDay.equals(dbDateId)){
			log.info("数据已经倒过了或没有准备好");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("WARN");
			PltCommonLogIns.setBUSI_DESC("数据已经倒过了或没有准备好,strCurMonthDay="+strCurMonthDay);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		dbDateId = null;
		// --- 设置同步进程正在处理 ---
		AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+(String)mapTenantInfo.get("TENANT_ID"), "TRUE");
					


		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("同步用户标签开始,租户:"+(String)mapTenantInfo.get("TENANT_ID"));
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
					
		// --- 提取SQL语句 ---
		String		xcloudSqlData = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.SQLDIRECT");
		// --- 提取SQL语句 ---
		String		mysqlSqlCols = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.COLS");
		String		mysqlSqlTableName = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.TABLENAME");
		String   	sqlPre = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.SQLPRE");
		String		strIndexSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.CREATEINDEX_1");
		String		strDropTableSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.DROPTABLE");
		String		strCreateTableSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.CREATETABLE");

		// --- 替换最大帐期 ----
		String	realXcloudSqlData = xcloudSqlData.replaceFirst("DDDDDATEID", strCurMonthDay);
		// --- 替换租户编号 ---
		realXcloudSqlData = realXcloudSqlData.replaceFirst("TTTTTENANT_ID", 
				(String)mapTenantInfo.get("TENANT_ID"));
		// --- 替换省编号 ---
		realXcloudSqlData = realXcloudSqlData.replaceFirst("PPPPPROV_ID", 
				(String)mapTenantInfo.get("PROV_ID"));
		log.info("sql="+realXcloudSqlData);
		// --- 替换结束 ---
		
		// --- 入库前建表 ------------------------------------------------------------------------------------------------
		String	execSql = strDropTableSql.replaceFirst("TTTTTNENAT_ID", (String)mapTenantInfo.get("TENANT_ID"));
		// --- drop table ---
		log.info("sql:"+execSql);
		jdbcTemplate.execute(execSql);
		// --- create table ---
		execSql = null;
		execSql = strCreateTableSql.replaceFirst("TTTTTNENAT_ID", (String)mapTenantInfo.get("TENANT_ID"));
		log.info("sql:"+execSql);
		jdbcTemplate.execute(execSql);
		// ----------------------------------------------------------------------------------------------------------------
		CrossDbSynData CrossDbSynDataIns  = new CrossDbSynData(
				AsynDataIns.getValueFromGlobal("DS.XCLOUD.DRIVER"),
				AsynDataIns.getValueFromGlobal("DS.XCLOUD.URL"),
				AsynDataIns.getValueFromGlobal("DS.XCLOUD.USER"),
				AsynDataIns.getValueFromGlobal("DS.XCLOUD.PASSWORD"),
				AsynDataIns.getValueFromGlobal("DS.MYSQL.DRIVER"),
				AsynDataIns.getValueFromGlobal("DS.MYSQL.URL."+(String)mapTenantInfo.get("TENANT_ID")),
				AsynDataIns.getValueFromGlobal("DS.MYSQL.USER."+(String)mapTenantInfo.get("TENANT_ID")),
				AsynDataIns.getValueFromGlobal("DS.MYSQL.PASSWORD."+(String)mapTenantInfo.get("TENANT_ID")),
				realXcloudSqlData,mysqlSqlTableName, mysqlSqlCols
				);
		CrossDbSynDataIns.setCommitRecs(Integer.parseInt(AsynDataIns.getValueFromGlobal("CROSSDBSYNDATA.COMMITRECS")));
		CrossDbSynDataIns.setBatchRecs(Integer.parseInt(AsynDataIns.getValueFromGlobal("CROSSDBSYNDATA.BATCHRECS")));
		ParallelManage ParallelManageIns = new ParallelManage(CrossDbSynDataIns,
				Integer.parseInt(AsynDataIns.getValueFromGlobal("CROSSDBSYNDATA.THREADNUMS")));
		if(ParallelManageIns.execute() != 0){
			// --- 设置同步进程处理结束 ---
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+(String)mapTenantInfo.get("TENANT_ID"), 
									"FALSE");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("SELECTANDINSERT");
			PltCommonLogIns.setBUSI_DESC("向MYSQL倒入数据失败");
			PltCommonLogIns.setBUSI_ITEM_1("多线程执行从行云到MYSQL出错 !!!");
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		// ----------------------------------------------------------------------------------------------------------------
		
		// --- 建索引，改名 -----------------------------------------------------------------------------------------------
		// --- 建索引  ---
		String sqlLocalPre = sqlPre.replaceFirst("TTTTTNENAT_ID", (String)mapTenantInfo.get("TENANT_ID"));
		//execSql = null;
		//execSql = strIndexSql.replaceFirst("TTTTTNENAT_ID", (String)mapTenantInfo.get("TENANT_ID"));
		// --- CREATE INDEX ---
		log.info("创建索引:{}",strIndexSql);
		if(AsynDataIns.executeDdlOnMysql(strIndexSql,  (String)mapTenantInfo.get("TENANT_ID")) == false){
			log.warn(" 创建索引失败:{}",strIndexSql);
			// --- 设置同步进程处理结束 ---
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+(String)mapTenantInfo.get("TENANT_ID"), 
						"FALSE");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("CREATEINDEXFAIL");
			PltCommonLogIns.setBUSI_DESC("建索引失败");
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		//log.info("sql:"+execSql);
		//jdbcTemplate.execute(execSql);
		// --- 如果有第二个索引是此建 ---
		// --- 结束建第二个索引 ---
		// --- 删除BAK表 ---
		
		execSql = null;
		execSql = sqlLocalPre + "DROP TABLE IF EXISTS  PLT_USER_LABEL_BAK";
		log.info("sql:"+execSql);
		jdbcTemplate.execute(execSql);
		/*
		// --- 将当前表RENAME ---
		execSql = null;
		execSql = sqlLocalPre + "RENAME TABLE PLT_USER_LABEL TO PLT_USER_LABEL_BAK";
		log.info("sql:"+execSql);
		jdbcTemplate.execute(execSql);
		// --- 将临时表更改为正式表 ---
		execSql = null;
		execSql = sqlLocalPre + "RENAME TABLE PLT_USER_LABEL_TMP TO PLT_USER_LABEL";
		log.info("sql:"+execSql);
		jdbcTemplate.execute(execSql);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("MYSQLDBHANDLEEND");
		PltCommonLogIns.setBUSI_DESC("MYSQL数据库处理（改表，建索引等）结束");
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		*/
		// ----------------------------------------------------------------------------------------------------------------
		

		// --- 事后处理 ----------------------------------------------------------------------------------------------------
		// --- 删除本地文件 ---
	//	AsynDataIns.deleteFile(mysqlFileName);
		
		// --- 更新最大帐期字段  ----
		if(strCurMonthDay != null)
			AsynDataIns.setValueToGlobal("ASYNUSER.XCLOUD.DATEID."+(String)mapTenantInfo.get("TENANT_ID"), 
					strCurMonthDay);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_DESC("从行云同步用户标签表结束");
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		
		// --- 设置同步进程处理结束 ---
		AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+(String)mapTenantInfo.get("TENANT_ID"), 
				"FALSE");
		
		// ----------------------------------------------------------------------------------------------------------------
		
		log.info("--- 同步用户标签数据成功结束  ---");
		
		
		return 0;
	}
	
	
	@Override
	public	int handleData(Object data){
		// --- 从全局类中得到当前租户变量 ---
		Map<String,Object>  mapTenantInfo = (Map<String,Object>)data;
		String tenantId = (String)mapTenantInfo.get("TENANT_ID");
		String ProvId = (String)mapTenantInfo.get("PROV_ID");
		// --- 首先判断是否已经有进程在处理 ---
		String		strRunFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.RUN.FLAG."+tenantId);
		if(strRunFlag != null){
			if(strRunFlag.equals("TRUE")){
				log.warn("--- 租户：{} 同步进程正在运行 ---",tenantId);
				return -1;
			}
		}
		strRunFlag = null;   // --- 释放字符串 ---
		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog		PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("00");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSPONSOR("USERLABELASYN."+tenantId +"."+Thread.currentThread().getName());
		// --- 判断行云的数据是否准备好 ---
		// --- 提取数据库存放的账期 ---
		String		dbDateId = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.DATEID."+tenantId);
		// --- 获取需要同步的最新账期 ---
		String		strCurMonthDay = TaskBaseMapperDao.getMaxDateId();		
		// --- 比较两个账期判断是否需要同步数据 ---
		if(strCurMonthDay == null || strCurMonthDay.equals(dbDateId)){
			log.info("数据已经倒过了或没有准备好");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("WARN");
			PltCommonLogIns.setBUSI_DESC("数据已经倒过了或没有准备好,strCurMonthDay="+strCurMonthDay);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		dbDateId = null;
		int		iStep = 0;
		try{
		// --- 设置同步进程正在处理 ---
		AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+tenantId, "TRUE");
	
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("同步用户标签开始,租户:"+tenantId);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
					
		int  seq = AsynDataIns.getSequence("USERLABEL.FILEID");
		//int seq = 150;
		log.info("seq = "+seq);
					
		// --- 判断新账期下行云是否有数据 ---
		String selectCountSql = "/*!mycat:sql=select * FROM XCLOUD_TTTTTENANT_ID*/"
				+ "SELECT COUNT(*) total FROM UNICOM_D_MB_DS_ALL_LABEL_INFO "
				+ "WHERE DATE_ID='DDDDDATE_ID' AND PROV_ID = 'PPPPPROV_ID'";
		String countSql = selectCountSql.replaceFirst("TTTTTENANT_ID", tenantId);
		String selectSql = countSql.replaceFirst("DDDDDATE_ID", strCurMonthDay);
		String sql = selectSql.replaceFirst("PPPPPROV_ID", ProvId);
		long total = (long) jdbcTemplate.queryForMap(sql).get("total");
		log.info("total:" + total);
		if(total==0){
			log.info("该账期下的数据还没有准备好，strCurMonthDay="+strCurMonthDay);
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("WARN");
			PltCommonLogIns.setBUSI_DESC("该账期下的数据还没有准备好,strCurMonthDay="+strCurMonthDay);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			serviceAlert.smsAlert(strCurMonthDay +"该账期下的用户资料同步数据还没有准备好，行云的数据量是0",tenantId,"000010");
			//===============================================调用接口   信息：strCurMonthDay 该账期下数据还没有准备好，行云数据是0
			return -1;
		}
		
		// --- 提取所有的配置数据  ---
		
		// --- 提取FTP配置数据 ---
		String		FtpSrvIp = AsynDataIns.getValueFromGlobal("HDFSSRV.IP");
		String		FtpUser = AsynDataIns.getValueFromGlobal("HDFSSRV.USER");
		String		FtpPassowd = AsynDataIns.getValueFromGlobal("HDFSSRV.PASSWORD");
		String		FtpPort = AsynDataIns.getValueFromGlobal("HDFSSRV.PORT");
		// --- 结束对FTP配置数据的提取 ---

		// --- 文件名后缀 ---
		String		fileSuffix = AsynDataIns.getValueFromGlobal("ASYNUSER.FILE.SUFFIX");
		// --- 提取从行云导出数据SQL语句 ---
		String		xcloudSqlData = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.SQLDATA");
		// --- 远程文件路径 ---
		String		xcloudFileName = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.FILENAME") + seq + fileSuffix;
		// --- 提取向mysql导入数据SQL语句 ---
		String		mysqlSqlData = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.SQLDATA");
		// --- 本地文件路径 ---
		String		mysqlFileName = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.FILENAME") + seq + fileSuffix;
//		String   	sqlPre = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.SQLPRE");
//      String		strIndexSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.CREATEINDEX_1");
//		String		strDropTableSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.DROPTABLE");
//		String		strCreateTableSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.CREATETABLE");	
		
		// --- 获取有效的分区标识 ---
		String      oldPartFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
		String		newPartFlag = "0".equals(oldPartFlag)?"1":"0";
		
		// --- 删除无效的分区数据SQL语句 ---
		String      truncatePartSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.PARTITION_TRUNCATE");	
		/*
		 *  --- 从行云出库 ------------------------------------------------------------------------------------------------
		 */
		// --- 替换SQL中的特定词 ---
		// --- 替换文件名 ---
		String			realXcloudSqlData = xcloudSqlData.replaceFirst("FFFFFILENAME", xcloudFileName);
		// --- 替换最大帐期 ---
		realXcloudSqlData = realXcloudSqlData.replaceFirst("DDDDDATEID", strCurMonthDay);
		// --- 替换租户编号 ---
		realXcloudSqlData = realXcloudSqlData.replaceFirst("TTTTTENANT_ID",tenantId);
		// --- 替换省编号 ---
		realXcloudSqlData = realXcloudSqlData.replaceFirst("PPPPPROV_ID",ProvId);
		
		// --- 替换分区标识 ---
		realXcloudSqlData = realXcloudSqlData.replaceFirst("PPPPPARTITION_FLAG",newPartFlag);
		
		log.info("sql="+realXcloudSqlData);
		// --- 替换结束 ---
		log.info("--- 开始从行云出数据,时间:"+new Date() +"  ---");
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("GETDATAFROMXCLOUDSTART");
		PltCommonLogIns.setBUSI_DESC("从行云获取数据开始");
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		JsonResult  JsonResultIns = AsynDataIns.execDdlOnXcloud(realXcloudSqlData);
		if("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false){   
			log.error("行云出库失败");
			// --- 设置同步进程处理结束 ---
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+tenantId,"FALSE");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("GETDATAFROMXCLOUDFAIL");
			PltCommonLogIns.setBUSI_DESC("从行云获取数据失败");
			PltCommonLogIns.setBUSI_ITEM_1(JsonResultIns.getMessage());
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			PltCommonLogIns.setBUSI_ITEM_1(null);
			serviceAlert.smsAlert(strCurMonthDay +"该账期下的用户资料同步从行云获取数据失败",tenantId,"000011");
			//===============================================调用接口   信息：从行云获取数据失败（待定）
			return -1;
		}
		iStep = 1;
		log.info("--- 结束行云出数据,时间:"+new Date() +"  ---");
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("GETDATAFROMXCLOUDEND");
		PltCommonLogIns.setBUSI_DESC("从行云获取数据结束");
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		// --- 结束行云出库 ----------------------------------------------------------------------------------------------
		
		// --- 从FTP服务器取回用户数据文件 -----------------------------------------------------------------------------
		// --- 去掉行云文件名中的HDFS前缀  ---
		if(xcloudFileName.contains("HDFS")){
			xcloudFileName = xcloudFileName.replaceFirst("HDFS:", "");
		}
		log.info("remotefile:"+xcloudFileName + " localfile:"+mysqlFileName);
		log.info("从FTP 服务器提取文件,time:{}",new Date());
		
		int	iPort = 21;
		if(FtpPort != null){
			iPort = Integer.parseInt(FtpPort);
		}
		String	strFtpResult = FtpTools.downloadXcloudFile(FtpSrvIp,FtpUser,FtpPassowd,iPort,xcloudFileName,mysqlFileName,true);
		if("000000".equalsIgnoreCase(strFtpResult) == false){
			log.warn("从FTP 服务器取文件失败:{} !!!",strFtpResult);
			// --- 设置同步进程处理结束 ---
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+tenantId,"FALSE");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("GETDATAFROMFTPFAIL");
			PltCommonLogIns.setBUSI_DESC("从FTP获取数据失败");
			PltCommonLogIns.setBUSI_ITEM_1(strFtpResult);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			serviceAlert.smsAlert(strCurMonthDay +"该账期下的用户资料同步从ftp获取数据失败",tenantId,"000012");
			//===============================================调用接口   信息：从ftp获取数据失败
			return -1;
		}		
		iStep = 2;
		log.info("结束从FTP 服务器提取文件,time:{} ",new Date());
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("GETDATAFROMFTPEND");
		PltCommonLogIns.setBUSI_DESC("从FTP服务器获取数据结束");
		PltCommonLogIns.setBUSI_ITEM_1(FtpSrvIp);
		PltCommonLogIns.setBUSI_ITEM_2(FtpUser);
		PltCommonLogIns.setBUSI_ITEM_3(FtpPassowd);
		PltCommonLogIns.setBUSI_ITEM_4(xcloudFileName);
		PltCommonLogIns.setBUSI_ITEM_5(mysqlFileName);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		PltCommonLogIns.setBUSI_ITEM_1(null);
		PltCommonLogIns.setBUSI_ITEM_2(null);
		PltCommonLogIns.setBUSI_ITEM_3(null);
		PltCommonLogIns.setBUSI_ITEM_4(null);
		PltCommonLogIns.setBUSI_ITEM_5(null);
		// --- 结束从FTP服务器取回用户数据文件 -------------------------------------------------------------------------
		
		// --- 入库前建表 ------------------------------------------------------------------------------------------------
		/*String	execSql = strDropTableSql.replaceFirst("TTTTTNENAT_ID", (String)mapTenantInfo.get("TENANT_ID"));
		// --- drop table ---
		log.info("sql:"+execSql);
		jdbcTemplate.execute(execSql);
		// --- create table ---
		execSql = null;
		execSql = strCreateTableSql.replaceFirst("TTTTTNENAT_ID", (String)mapTenantInfo.get("TENANT_ID"));
		log.info("sql:"+execSql);
		jdbcTemplate.execute(execSql);*/
		// ----------------------------------------------------------------------------------------------------------------
		
		/*// --- 向mysql 倒入数据前先清空分区 --- 
		String clearNewPart = clearPartSql.replaceFirst("TTTTTENANT_ID", 
				(String)mapTenantInfo.get("TENANT_ID"));
		clearNewPart = clearNewPart.replaceFirst("PPPPPARTITION_FLAG", newPartFlag);
		jdbcTemplate.execute(clearNewPart);*/
/*		//---删除分区---
		String deletePart = deletePartSql.replaceFirst("TTTTTENANT_ID", 
				(String)mapTenantInfo.get("TENANT_ID"));
		deletePart = deletePart.replaceFirst("PPPPPART","p" + newPartFlag);
		jdbcTemplate.execute(deletePart);
		
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("DELETEPART");
		PltCommonLogIns.setBUSI_DESC("删除分区");
		PltCommonLogIns.setBUSI_ITEM_1(deletePart);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);*/
		
		
	/*	//---添加新的分区---
		String createPart = createPartSql.replaceFirst("TTTTTENANT_ID", 
				(String)mapTenantInfo.get("TENANT_ID"));
		createPart = createPart.replaceFirst("PPPPPARTITION_FLAG",newPartFlag);	
		createPart = createPart.replaceFirst("PPPPPART","p" + newPartFlag);
		jdbcTemplate.execute(createPart);
		
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("CREATEPART");
		PltCommonLogIns.setBUSI_DESC("添加新的分区");
		PltCommonLogIns.setBUSI_ITEM_1(createPart);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);*/
		
		//---删除分区数据---
		String truncatePart = truncatePartSql.replaceFirst("TTTTTENANT_ID",tenantId);
		truncatePart = truncatePart.replaceFirst("PPPPPART","p" + newPartFlag);
		jdbcTemplate.execute(truncatePart);
				
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("TRUNCATEPART");
		PltCommonLogIns.setBUSI_DESC("删除分区数据");			
		PltCommonLogIns.setBUSI_ITEM_1(truncatePart);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);				
				
		// --- 入库MYCAT -----------------------------------------------------------------------------------------------
		// --- 将文件入MYSQL库 ---
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("LOADDATAINMYCATBEGIN");
		PltCommonLogIns.setBUSI_DESC("向MYCAT倒入数据开始");
		PltCommonLogIns.setBUSI_ITEM_1(mysqlFileName);
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		PltCommonLogIns.setBUSI_ITEM_1(null);
		log.info(" MYSQL 倒数据开始: "+new Date());
		String		strExecSql = null;
		strExecSql = mysqlSqlData.replaceFirst("FFFFFILENAME", mysqlFileName);
		
		 if(batchInMycat(mysqlFileName,seq,mysqlSqlData,tenantId) != 0){
		 	log.error("入MYSQL失败");
			// --- 设置同步进程处理结束 ---
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+tenantId,"FALSE");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("LOADDATAINMYSQLFAIL");
			PltCommonLogIns.setBUSI_DESC("向MYSQL倒入数据失败");
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			serviceAlert.smsAlert(strCurMonthDay +"该账期下的用户资料同步向数据库导入数据失败",tenantId,"000013");
			//===============================================调用接口   信息：向数据库导入数据失败
			return -1;
		 }

		strExecSql = null;	
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("LOADDATAINMYSQLEND");
		PltCommonLogIns.setBUSI_DESC("向MYSQL倒入数据结束");
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		AsynDataIns.setValueToGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION", newPartFlag);
		
		// ----------------------------------------------------------------------------------------------------------------
		// --- 事后处理 ---
		// --- 删除本地文件 ---
		AsynDataIns.deleteFile(mysqlFileName);
		
		// --- 更新最大帐期字段  ----
		if(strCurMonthDay != null)
			AsynDataIns.setValueToGlobal("ASYNUSER.XCLOUD.DATEID."+tenantId,strCurMonthDay);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_DESC("从行云同步用户标签表结束");
		AsynDataIns.insertPltCommonLog(PltCommonLogIns);		
		log.info("--- 同步用户标签数据成功结束  ---");
		}catch(Exception e){
			// --- 根据已经执行的步骤处理 ---
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXECUTE ERROR");
			PltCommonLogIns.setBUSI_DESC("执行过程中出错");
			PltCommonLogIns.setBUSI_ITEM_1(e.getMessage());
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			if(e.getCause() instanceof  SQLException){
				log.info("sql error happend:{}",e.getMessage());
				throw e;
			}
			log.info("return -1");
			return -1;
		}finally{
			log.info(" 在finally 中更新状态");
			// --- 设置状态为结束，而非一直在执行 ---
			// --- 设置同步进程处理结束 ---
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+tenantId,"FALSE");
		}
		return 0;
	}
	/*
	 * 分批入库
	 */
	private	int	batchInMycat(String strOrigFileName,int iSeq,String strMysqlLoadSql,String TenantId){
		try{
			int  i = 0;
			int	iTotalNum = 0;
			boolean  bChange = false;
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(strOrigFileName),
					"UTF-8"));
			// --- 得到本地文件名 ---
			String	strPath = FtpTools.getPath(strOrigFileName);
			String	LocalLastFileName = "user_label_tmp_"+iSeq+".csv";
			String	strTmpFileName = null;
			if(strPath != null){
				strTmpFileName = strPath +"/" +LocalLastFileName;
			}
			else
				strTmpFileName = LocalLastFileName;
			LocalLastFileName = null;
			strPath = null;
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(strTmpFileName,false), 
			    		"UTF-8");   
			String  sqlData = strMysqlLoadSql.replaceFirst("FFFFFILENAME", strTmpFileName);
			while(1 > 0){   // --- 纪录数过多，不利于入库，10万入库一次 ---
				String line = br.readLine();		
				if(line != null){
					++iTotalNum;
					osw.write(line);
					osw.write('\n');
					++i;
					if(i >= 100000){ 
						// --- 入库 ---
						try{
							i = 0;
							osw.flush();
							osw.close();
							// --- 入库 ---
							//log.info("当前纪录数：{},sql:{}",iTotalNum,sqlData);
							//log.info(" 10万纪录入库,filename:"+strTmpFileName +"  time:"+ new Date());
							Date  begin = new Date();
							//String  sqlData = mysqlSqlData.replaceFirst("FFFFFILENAME", LocalLastFileName);
							if(AsynDataIns.loadDataInMysql(sqlData,TenantId) == false){
								log.error("入MYSQL失败");
								return -1;
							}
							Date end = new Date();
							log.info("当前纪录数据：{},filename:{}, 耗时:{}  ms", iTotalNum,strTmpFileName ,
									end.getTime()-begin.getTime());
							// --- 重新打开流 ---
							osw = new OutputStreamWriter(new FileOutputStream(strTmpFileName,false), "UTF-8");   
							/*
							if(bChange){
							  log.info(" test string1" + AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.SQLPRE"+iTotalNum));
							  bChange = false;
							}
							else{
								  bChange = true;
								log.info(" test string2" + AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.CREATEINDEX_1"+ iTotalNum));
							}
							*/
						}catch(Exception e){
							e.printStackTrace();
							br.close();
							return -1;
						}
					}
				}
				else{
					// --- 入库 ---
					log.info("纪录数："+i);
					try{
						br.close();
						osw.flush();
						osw.close();
						if(i > 0){
							if(AsynDataIns.loadDataInMysql(sqlData,TenantId) == false){
								log.error("入MYSQL失败");
								return -1;
							}
							log.info("记录:{} 入库",i);
						}		
					}catch(Exception e){
						return -1;
					}
					break;
				}	// --- else ---		
			}	// --- while ---	
			// --- 删除临时文件 --------------
			AsynDataIns.deleteFile(strTmpFileName);
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
