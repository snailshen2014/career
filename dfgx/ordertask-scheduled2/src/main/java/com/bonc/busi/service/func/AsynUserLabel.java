package com.bonc.busi.service.func;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.busi.task.base.SftpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
//import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.dao.SysrunningcfgDao;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.service.mapper.DynamicAsynMapper;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.ConcatSqlOperation;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.base.ParallelManage;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.common.base.JsonResult;
import com.bonc.common.thread.ThreadBaseFunction;
import com.bonc.utils.HttpUtil;

@Service("AsynUserLabel")
//@Transactional
public class AsynUserLabel extends ThreadBaseFunction{
	@Autowired  private BusiTools  AsynDataIns;
	@Autowired	private JdbcTemplate jdbcTemplate;
	private final static Logger log= LoggerFactory.getLogger(AsynUserLabel.class);
	
	@Autowired	private SysFunction  SysFunctionIns;
	@Autowired  private DynamicAsynMapper DynamicAsynMapperIns;
	
	@SuppressWarnings("unchecked")
	@Override
	public	int handleData(Object data){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+AsynUserLabel.class+"-handleData");
		
		// --- 从全局类中得到当前租户变量 ---
		Map<String,Object>  mapTenantInfo = (Map<String,Object>)data;
		String		TenantId = (String)mapTenantInfo.get("TENANT_ID");
		String		ProvId = (String)mapTenantInfo.get("PROV_ID");
		
		// --- 首先判断是否已经有进程在处理 ---
		//		String		strRunFlag = SysrunningcfgDao.query("ASYNUSER.RUN.FLAG."+TenantId);
		String		strRunFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.RUN.FLAG."+TenantId);
		if(strRunFlag != null){
			if(strRunFlag.equals("TRUE")){
				log.warn("--- 租户：{} 同步进程正在运行 ---",TenantId);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("用户资料同步进程正在运行");
				SysLogIns.setBUSI_ITEM_5("00");
				SyslogDao.insert(SysLogIns);
				return -1;
			}
		}		
		strRunFlag = null;   // --- 释放字符串 ---
		
		// --- 提取帐期 ---
		String flag = AsynDataIns.getValueFromGlobal("SERVICE_PROVIDER_TYPE");	
		String strCurMonthDay;
		String totalSql;
		String xcloudTableName;//宽表名
		String asynMonthDate;//表示账期的字段名
		if (flag!=null && flag.equals("1")) {
			String strDXCurMonthDayUrl = AsynDataIns.getValueFromGlobal("GET_MONTH_TIME");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("1", "1");
			params.put("tenant_id", TenantId);
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("req", JSON.toJSONString(params));
			String sendPost = HttpUtil.doGet(strDXCurMonthDayUrl, requestMap);
			Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
			strCurMonthDay = (String) resultMap.get("MAX_DATE");
//			strCurMonthDay = "20171022";
			xcloudTableName = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUDTABLENAME."+TenantId); // 获取电信宽表名
			asynMonthDate = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUDDATEID."+TenantId); //获取电信账期字段名
		}else{
			// --- 联通多租户宽表名和账期字段名一致 --- 
			strCurMonthDay = SysFunctionIns.getCurMothDay(TenantId);
			xcloudTableName = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUDTABLENAME") ;
			asynMonthDate = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUDDATEID"); 
			}	  
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setBUSI_ITEM_4(strCurMonthDay);
		SysLogIns.setBUSI_ITEM_5("00");
		SysLogIns.setLOG_MESSAGE("同步用户标签数据开始运行");
		SyslogDao.insert(SysLogIns);
		
		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog		PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setTENANT_ID(TenantId);
		PltCommonLogIns.setLOG_TYPE("00");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setBUSI_ITEM_6(strCurMonthDay);
		PltCommonLogIns.setSPONSOR("USERLABELASYN."+TenantId +"."+
				Thread.currentThread().getName());
	
		// --- 判断行云的数据是否准备好 ---

		String		dbDateId = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.DATEID."+TenantId);
//		String		dbDateId = SysrunningcfgDao.query("ASYNUSER.XCLOUD.DATEID."+TenantId);
//		String		dbDateId = "20170723";
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("提取数据库中当前已经同步的帐期");
		SysLogIns.setBUSI_ITEM_1(dbDateId);
		SyslogDao.insert(SysLogIns);

		// --- 判断该账期下行云是否有新数据 ---
		totalSql = AsynDataIns.getValueFromGlobal("GET_XCLOUD_DATATOTAL_SQL");
		// --- 替换 ---
		String tenantSql = totalSql.replaceFirst("TTTTTENANT_ID", TenantId);
		String dateIdSql = tenantSql.replaceFirst("DDDDDATE_ID", strCurMonthDay);
		String provIdsql = dateIdSql.replaceFirst("PPPPPROV_ID", ProvId);
		String xcloudTableNameSql = provIdsql.replaceFirst("XCLOUD_TABLENAME", xcloudTableName);
		String sql = xcloudTableNameSql.replaceFirst("XLOUD_DATEID",asynMonthDate);
		
		log.info("判断行云在该账期内是否有数据sql:" + sql);
		long total = (long) jdbcTemplate.queryForMap(sql).get("total");
		log.info("total:" + total);
		if(total==0){
			log.info("该账期下的数据还没有准备好，strCurMonthDay="+strCurMonthDay);
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("WARN");
			PltCommonLogIns.setBUSI_DESC("该账期下的数据还没有准备好,strCurMonthDay="+strCurMonthDay);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
			
		}
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("提取当前最新帐期");
		SysLogIns.setBUSI_ITEM_1(strCurMonthDay);
		SyslogDao.insert(SysLogIns);
//		if(strCurMonthDay == null || strCurMonthDay.equals(dbDateId)){
//			log.info("数据已经倒过了或没有准备好");
//			PltCommonLogIns.setSTART_TIME(new Date());
//			PltCommonLogIns.setBUSI_CODE("WARN");
//			PltCommonLogIns.setBUSI_DESC("数据已经倒过了或没有准备好,strCurMonthDay="+strCurMonthDay);
//			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
//			return -1;
//		}
		if(strCurMonthDay.equals(dbDateId)){
			log.info("有新字段需要进行同步。");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_DESC("有新字段需要进行同步");
			PltCommonLogIns.setBUSI_CODE("NEWFIELDSNEEDASYN");
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
		}
//		dbDateId = null;
		int		iStep = 0;
		String		mysqlFileName = null;
		int     seq = 0;
		try{
			// --- 设置同步进程正在处理 ---
//			SysrunningcfgDao.update("ASYNUSER.RUN.FLAG."+TenantId, "TRUE");
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+TenantId, "TRUE");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("设置数据库中的运行标识为TRUE");
			SysLogIns.setBUSI_ITEM_1("ASYNUSER.RUN.FLAG."+TenantId);
			SyslogDao.insert(SysLogIns);

			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("BEGIN");
			PltCommonLogIns.setBUSI_DESC("同步用户标签开始,租户:"+TenantId);
//			PltCommonLogIns.setBUSI_ITEM_6(strCurMonthDay);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);

			seq = AsynDataIns.getSequence("USERLABEL.FILEID");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("seq = "+seq);
			SyslogDao.insert(SysLogIns);
					
					
			// --- 提取所有的配置数据  ---
		
			// --- 提取FTP配置数据 ---
			String		FtpSrvIp = AsynDataIns.getValueFromGlobal("HDFSSRV.IP."+TenantId);
			String		FtpUser = AsynDataIns.getValueFromGlobal("HDFSSRV.USER."+TenantId);
			String		FtpPassowd = AsynDataIns.getValueFromGlobal("HDFSSRV.PASSWORD."+TenantId);
			String		FtpPort = AsynDataIns.getValueFromGlobal("HDFSSRV.PORT."+TenantId);
			// --- 结束对FTP配置数据的提取 ---

			// --- 文件名后缀 ---
			String		fileSuffix = AsynDataIns.getValueFromGlobal("ASYNUSER.FILE.SUFFIX");
			// --- 提取SQL语句 ---
//			String		xcloudSql = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.SQLDATA."+TenantId);
//			String		xcloudSqlData = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.SQLDATA");
			String		xcloudFileName = AsynDataIns.getValueFromGlobal("ASYNUSER.XCLOUD.FILENAME") +
					TenantId + "_" + seq + fileSuffix;
			// --- 提取SQL语句 ---
//			String		mysqlSqlData = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.SQLDATA."+TenantId);
//			String		mysqlSqlData = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.SQLDATA");
			mysqlFileName = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.FILENAME")+
					TenantId + "_" + seq + fileSuffix;

			
			// --- 获取有效的分区标识 ---
//			String      oldPartFlag = SysrunningcfgDao.query("ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+TenantId);
			String      oldPartFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+TenantId);
			String		newPartFlag = "0".equals(oldPartFlag)?"1":"0";
			
			String		strDropTableSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.DROPTABLE");
//			String		strCreateTableSql = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.CREATETABLE."+TenantId);		
//			String xcloudSqlData ="/*!mycat:sql=select * FROM XCLOUD_TTTTTENANT_ID*/" + xcloudSql;
			
			// --- 读取PLT_DEMAND_SYN_USERLABEL表内需要同步的最新字段 ---
			List<Map<String,Object>> columnsList = DynamicAsynMapperIns.getColumnsList(TenantId);
			System.out.println("columnsList:" + columnsList);
			if(columnsList.size() == 0){
				log.info("PLT_DEMAND_SYN_USERLABEL表数据为空！！！");
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("WARN");
				PltCommonLogIns.setBUSI_DESC("PLT_DEMAND_SYN_USERLABEL表数据为空!!!");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("PLT_DEMAND_SYN_USERLABEL表数据为空!!!");
				SyslogDao.insert(SysLogIns);
				return -1;
			}
			
			
			// --- 拼接从行云导出数据的sql语句  ---
			ConcatSqlOperation operation = new ConcatSqlOperation();
		    String exportSql = operation.getExportSql(TenantId,columnsList,xcloudTableName,asynMonthDate);
			log.info("从行云导出数据语句exportSql：" + exportSql);
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXPORTSQL");
			PltCommonLogIns.setBUSI_DESC("从行云导出数据SQL");
			PltCommonLogIns.setBUSI_ITEM_1(exportSql);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("从行云导出数据SQL");
			SysLogIns.setLOG_MESSAGE(exportSql);	
			SyslogDao.insert(SysLogIns);
			
			// --- 拼接向数据库导入数据的sql语句  ---
			String loadSql = operation.getLoadSql(columnsList);
			log.info("向MySQL导入语句loadSql：" + loadSql);
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("LOADSQL");
			PltCommonLogIns.setBUSI_DESC("向MySQL导入数据SQL");
			PltCommonLogIns.setBUSI_ITEM_1(loadSql);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("向MySQL导入数据SQL");
			SysLogIns.setLOG_MESSAGE(loadSql);	
			SyslogDao.insert(SysLogIns);
			
			// --- 拼接建表的sql语句  ---
			String createSql = operation.getCreateSql(columnsList);
			log.info("在MySQL建表语句createSql：" + createSql);
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("CREATESQL");
			PltCommonLogIns.setBUSI_DESC("在MySQL建表语句SQL");
			PltCommonLogIns.setBUSI_ITEM_1(createSql);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("在MySQL建表语句SQL");
			SysLogIns.setLOG_MESSAGE(createSql);	
			SyslogDao.insert(SysLogIns);
			/*		
			 *  --- 从行云出库 ------------------------------------------------------------------------------------------------
		     */
			// --- 替换SQL中的特定词 ---
			// --- 替换文件名 ---
			String			realExportSql = exportSql.replaceFirst("FFFFFILENAME", xcloudFileName);
			// --- 替换最大帐期 ----
			realExportSql = realExportSql.replaceFirst("DDDDDATEID", strCurMonthDay);
			// --- 替换租户编号 ---
			realExportSql = realExportSql.replaceAll("TTTTTENANT_ID", TenantId);
			// --- 替换省编号 ---
			realExportSql = realExportSql.replaceFirst("PPPPPROV_ID", ProvId);
			
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("行云开始出库,SQL= "+realExportSql);
			SyslogDao.insert(SysLogIns);
			
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("GETDATAFROMXCLOUDSTART");
			PltCommonLogIns.setBUSI_DESC("从行云获取数据开始");
			PltCommonLogIns.setBUSI_ITEM_1(realExportSql);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			
			jdbcTemplate.execute(realExportSql);
			log.info("替换后的导出语句："+ realExportSql);
			
//			JsonResult  JsonResultIns = AsynDataIns.execDdlOnXcloud(realXcloudSqlData,TenantId);
//			JsonResult  JsonResultIns = AsynDataIns.execDdlOnXcloud(realExportSql,TenantId);
//			if("000000".equalsIgnoreCase(JsonResultIns.getCode()) == false){   
//				log.error("行云出库失败");
//				// --- 设置同步进程处理结束 ---
//				AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+TenantId, 
//								"FALSE");
//				PltCommonLogIns.setSTART_TIME(new Date());
//				PltCommonLogIns.setBUSI_CODE("GETDATAFROMXCLOUDFAIL");
//				PltCommonLogIns.setBUSI_DESC("从行云获取数据失败");
//				PltCommonLogIns.setBUSI_ITEM_1(JsonResultIns.getMessage());
//				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
//				PltCommonLogIns.setBUSI_ITEM_1(null);
//				return -1;
//			}
//			iStep = 1;
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("GETDATAFROMXCLOUDEND");
			PltCommonLogIns.setBUSI_DESC("从行云获取数据结束");
			PltCommonLogIns.setBUSI_ITEM_1(String.valueOf(total));
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			
			
			// --- 结束行云出库 ----------------------------------------------------------------------------------------------

			// --- 从FTP服务器取回用户数据文件 -----------------------------------------------------------------------------
			// --- 去掉行云文件名中的HDFS前缀  ---
			if(xcloudFileName.contains("HDFS")){
				xcloudFileName = xcloudFileName.replaceFirst("HDFS:", "");
			}
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("remotefile:"+xcloudFileName + " localfile:"+mysqlFileName);
			SysLogIns.setBUSI_ITEM_1("从FTP 服务器提取文件");
			SyslogDao.insert(SysLogIns);
		
			int		iPort = 21;
			if(FtpPort != null){
				iPort = Integer.parseInt(FtpPort);
			}
			String transType = AsynDataIns.getValueFromGlobal("ORDER.TRANS.TYPE."+TenantId);
			String strFtpResult;
			if ("SFTP".equals(transType)){
				SftpUtils sftpUtils = new SftpUtils();
				strFtpResult = sftpUtils.downloadXcloudFile(FtpSrvIp, FtpUser, FtpPassowd,
						iPort,xcloudFileName, mysqlFileName,true);
			}else {
				strFtpResult = FtpTools.downloadXcloudFile(FtpSrvIp, FtpUser, FtpPassowd,
						iPort,xcloudFileName, mysqlFileName,true);
			}
			if("000000".equalsIgnoreCase(strFtpResult)  == false){
				log.warn("从FTP 服务器取文件失败:{} !!!",strFtpResult);
				// --- 设置同步进程处理结束 ---
//				SysrunningcfgDao.update("ASYNUSER.RUN.FLAG."+TenantId,"FALSE");
				SyscommoncfgDao.update("ASYNUSER.RUN.FLAG."+TenantId,"FALSE");
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("GETDATAFROMFTPFAIL");
				PltCommonLogIns.setBUSI_DESC("从FTP获取数据失败");
				PltCommonLogIns.setBUSI_ITEM_1(strFtpResult);
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				return -1;
			}		
			iStep = 2;
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
			// --- DROP掉旧表 ----
			strDropTableSql  = strDropTableSql.replaceFirst("TTTTTENANT_ID",TenantId);
			strDropTableSql = strDropTableSql.replaceFirst("PPPPPART",newPartFlag);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("删除旧表,strDropTableSql="+strDropTableSql);
			SyslogDao.insert(SysLogIns);
			jdbcTemplate.execute(strDropTableSql);
			// --- 建新表  ---
			createSql = createSql.replaceFirst("TTTTTENANT_ID",TenantId);
			createSql = createSql.replaceFirst("PPPPPART",newPartFlag);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("建新表,strCreateTableSql="+createSql);
			SyslogDao.insert(SysLogIns);
			jdbcTemplate.execute(createSql);
		
			// --- 入库MYCAT -----------------------------------------------------------------------------------------------
			// --- 将文件入MYSQL库 ---
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("LOADDATAINMYCATBEGIN");
			PltCommonLogIns.setBUSI_DESC("向MYSQL倒入数据开始，有效用户标签表为：PLT_USER_LABEL_" + newPartFlag);
			PltCommonLogIns.setBUSI_ITEM_1(mysqlFileName);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			PltCommonLogIns.setBUSI_ITEM_1(null);
//			String		strExecSql = null;
//			strExecSql = mysqlSqlData.replaceFirst("FFFFFILENAME", mysqlFileName);
//			strExecSql = strExecSql.replaceFirst("PPPPPART",newPartFlag);
		
			if(batchInMycat(mysqlFileName,seq,loadSql,TenantId,newPartFlag,strCurMonthDay) != 0){
				log.error("入MYSQL失败");
				// --- 设置同步进程处理结束 ---
//				SysrunningcfgDao.update("ASYNUSER.RUN.FLAG."+TenantId, "FALSE");
				SyscommoncfgDao.update("ASYNUSER.RUN.FLAG."+TenantId,"FALSE");
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("LOADDATAINMYSQLFAIL");
				PltCommonLogIns.setBUSI_DESC("向MYSQL倒入数据失败");
				AsynDataIns.insertPltCommonLog(PltCommonLogIns);
				return -1;
			}
		 
		/*
		log.info("mysql load sql:"+strExecSql);
		if(AsynDataIns.loadDataInMycat(strExecSql) == false){
			log.error("入MYCAT失败");
			// --- 设置同步进程处理结束 ---
			AsynDataIns.setValueToGlobal("ASYNUSER.RUN.FLAG."+(String)mapTenantInfo.get("TENANT_ID"), 
					"FALSE");
			return -1;
		}
		*/
		//strExecSql = null;
		
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("LOADDATAINMYSQLEND");
			PltCommonLogIns.setBUSI_DESC("向MYSQL倒入数据结束");
			PltCommonLogIns.setBUSI_ITEM_1(loadSql);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
//			SysrunningcfgDao.update("ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+TenantId, newPartFlag);
			SyscommoncfgDao.update("ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+TenantId, newPartFlag);
		
			// ----------------------------------------------------------------------------------------------------------------
	
			// --- 事后处理 ------------------------------------------------------------------------------------------------------
			// --- 删除本地文件 ---
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE(" 删除本地文件");
			SyslogDao.insert(SysLogIns);
			AsynDataIns.deleteFile(mysqlFileName);
			// --- 账期没有发生变化，更新字段标识位 ---
		    if(strCurMonthDay.equals(dbDateId)){
//		    	SysrunningcfgDao.update("ASYNUSER.DYNAMICLABELUPDATE.FLAG."+TenantId, "FALSE");
		    	SyscommoncfgDao.update("ASYNUSER.DYNAMICLABELUPDATE.FLAG."+TenantId, "FALSE");
		    	                       
		    }
			// --- 更新最大帐期字段  ----
			if(strCurMonthDay != null)
//				SysrunningcfgDao.update("ASYNUSER.XCLOUD.DATEID."+TenantId, strCurMonthDay);
			    SyscommoncfgDao.update("ASYNUSER.XCLOUD.DATEID."+TenantId, strCurMonthDay);
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("END");
			PltCommonLogIns.setBUSI_DESC("同步用户标签表结束");
			PltCommonLogIns.setBUSI_ITEM_1(null);
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			// ----------------------------------------------------------------------------------------------------------------

//			SysLogIns.setLOG_TIME(new Date());
//			SysLogIns.setLOG_MESSAGE("启动资料更新线程");
//			SyslogDao.insert(SysLogIns);
//			// --- 启动资料更新线程  ---
//			callOrderUserlabelUpdate(TenantId,strCurMonthDay);
			
			// -------------------------------------------------------------------------------------------------------------------
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("同步用户标签数据成功结束");
			SyslogDao.insert(SysLogIns);
		}catch(Exception e){
			// --- 根据已经执行的步骤处理 ---
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXECUTE ERROR");
			PltCommonLogIns.setBUSI_DESC("执行过程中出错");
			PltCommonLogIns.setBUSI_ITEM_1(e.getMessage());
			AsynDataIns.insertPltCommonLog(PltCommonLogIns);
			e.printStackTrace();
			if(e.getCause() instanceof  SQLException){
				log.info("sql error happend:{}",e.getMessage());
				throw e;
			}
			
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("用户资料同步异常");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
			
			return -1;
		} finally {

			String LocalPath = FtpTools.getPath(mysqlFileName);
			
			log.info("==========" + LocalPath);
			File filePath = new File(LocalPath); 
			File[] files = filePath.listFiles();
			log.info("本地文件："+ Arrays.asList(files).toString());
			
			String localFileName = mysqlFileName.substring(mysqlFileName.lastIndexOf("/") + 1);
			boolean file = AsynDataIns.judgeFileExist(LocalPath, localFileName);
			if (file == true) {
				AsynDataIns.deleteFile(mysqlFileName);
			}
			log.info(" 在finally 中更新状态");
			// --- 设置状态为结束，而非一直在执行 ---
			// --- 设置同步进程处理结束 ---
			SyscommoncfgDao.update("ASYNUSER.RUN.FLAG."+TenantId, "FALSE");
		}
		return 0;
	}

	/*
	 * 调用工单用户资料更新
	 */
	private		void				callOrderUserlabelUpdate(String TenantId, String strCurMonthDay){
		// --- 定义4个线程 ---
		int 	iThreadNum = 4;
		String strTmp = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.THREADSNUM");
		if (strTmp != null) {
			iThreadNum = Integer.parseInt(strTmp);
			if (iThreadNum < 4)
				iThreadNum = 4;
		}
		// --- 执行用户资料更新 ---
		OrderUserlabelUpdate		OrderUserlabelUpdateIns = new OrderUserlabelUpdate();
		OrderUserlabelUpdateIns.setTenantId(TenantId);
		OrderUserlabelUpdateIns.setStrCurMonthDay(strCurMonthDay);
		ParallelManage ParallelManageIns = new ParallelManage(OrderUserlabelUpdateIns, iThreadNum);
		ParallelManageIns.execute();
	}
	/*
	 * 分批入库
	 */
	private	int	batchInMycat(String strOrigFileName,int iSeq,String strMysqlLoadSql,
			String TenantId,String  newPartFlag,String strCurMonthDay ){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+AsynUserLabel.class+"-batchInMycat");
		SysLogIns.setTENANT_ID(TenantId);
		try{
			int num = Integer.parseInt(AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.INTODBNUM"));
			// --- 提取FTP配置数据 ---
//			String		FtpSrvIp = AsynDataIns.getValueFromGlobal("HDFSSRV.IP."+TenantId);
//			String		FtpUser = AsynDataIns.getValueFromGlobal("HDFSSRV.USER."+TenantId);
//			String		FtpPassowd = AsynDataIns.getValueFromGlobal("HDFSSRV.PASSWORD."+TenantId);
//			int		FtpPort = Integer.parseInt(AsynDataIns.getValueFromGlobal("HDFSSRV.PORT."+TenantId));
	
			
//			int num = 100000;
			int  i = 0;
			int	iTotalNum = 0;
//			boolean  bChange = false;
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(strOrigFileName),
					"UTF-8"));
			// --- 得到本地文件名 ---
			String	strPath = FtpTools.getPath(strOrigFileName);
			String	LocalLastFileName = "user_label_temp_"+TenantId+"_"+iSeq+".csv";
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
			sqlData  = sqlData.replaceFirst("PPPPPART",newPartFlag);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_4(strCurMonthDay);
			SysLogIns.setBUSI_ITEM_5("00");
			SysLogIns.setLOG_MESSAGE(" sqlData = "+sqlData);
			SyslogDao.insert(SysLogIns);
			while(1 > 0){   // --- 纪录数过多，不利于入库，100万入库一次 ---
				String line = br.readLine();		
				if(line != null){
					++iTotalNum;
					osw.write(line);
//					System.out.println(line.toString());
					osw.write('\n');
					++i;
					if(i >= num){ 
						
						// --- 入库 ---
						try{
							i = 0;
							osw.flush();
							osw.close();
							
							// --- 入库 ---
							//log.info("当前纪录数：{},sql:{}",iTotalNum,sqlData);
							//log.info(" 10万纪录入库,filename:"+strTmpFileName +"  time:"+ new Date());
							Date  begin = new Date();
							
//							int	strFtpResult = FtpTools.upload(FtpSrvIp, FtpUser, FtpPassowd,FtpPort,
//									xcloudFileName, strTmpFileName);
//						
//							return strFtpResult;
							//String  sqlData = mysqlSqlData.replaceFirst("FFFFFILENAME", LocalLastFileName);
							
/*							jdbcTemplate.execute(sqlData);*/	
							
							
							
							if(AsynDataIns.loadDataInMysql(sqlData,TenantId) == false){
								log.error("入MYSQL失败");
								SysLogIns.setLOG_TIME(new Date());
								SysLogIns.setLOG_MESSAGE(" 批量和库,入MYSQL失败");
								SyslogDao.insert(SysLogIns);
								return -1;
							}
							Date end = new Date();
							log.info("当前纪录数据：{},filename:{}, 耗时:{}  ms", iTotalNum,strTmpFileName ,
									end.getTime()-begin.getTime());
							SysLogIns.setLOG_TIME(new Date());
							SysLogIns.setLOG_MESSAGE("当前纪录数据:"+iTotalNum+" filename:"+strTmpFileName+" 耗时:"+(end.getTime()-begin.getTime()));
							SyslogDao.insert(SysLogIns);
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
							SysLogIns.setLOG_TIME(new Date());
							SysLogIns.setBUSI_ITEM_1("用户资料入库异常");
							SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
							return -1;
						}
					}
//					else{
//						osw.write('\n');
//					}
					
				}
				else{
					// --- 入库 ---
					log.info("纪录数："+i);
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("纪录数:"+i);
					SyslogDao.insert(SysLogIns);
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
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("用户资料入库异常2");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
			return -1;
		}
		return 0;
	}
}
