package com.bonc.busi.task.base;

import java.util.Date;
import java.util.Map;

import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.h2.util.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.thread.ThreadBaseFunction;
import com.bonc.xcloud.xserver.SessionService.Processor.login;

@Service("UpdateUserId")
public class UpdateUserId extends ThreadBaseFunction{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private final static Logger log= LoggerFactory.getLogger(UpdateUserId.class);
	
	@Autowired
	private BusiTools UpdateDataIns;
	@Autowired
	private BaseMapper  TaskBaseMapperDao;

	
	@Override
	public int handleData(Object data){
		// --- 从全局类中得到当前租户变量 ---
		Map<String, Object> mapTenantInfo = (Map<String, Object>)data;
		log.info((String)mapTenantInfo.get("TENANT_ID"));
		
		String TenantId = (String)mapTenantInfo.get("TENANT_ID");
		
		// --- 首先判断是否有进程正在处理 ---
		String strRunFlag = UpdateDataIns.getValueFromGlobal("UPDATEUSERID.FLAG."+(String)mapTenantInfo.get("TENANT_ID"));//配置1 获取当前进程状态
		if(strRunFlag != null){
			if(strRunFlag.equals("true")){
				log.warn("---租户：{}更新userid进程正在处理---",(String)mapTenantInfo.get("TENANT_ID"));
				return -1;
			}
		}
		strRunFlag = null;   // --- 释放字符串 ---
		int SerialId = UpdateDataIns.getSequence("COMMONLOG.SERIAL_ID");
        PltCommonLog PltCommonLogIns = new PltCommonLog();
        PltCommonLogIns.setLOG_TYPE("02");
        PltCommonLogIns.setSERIAL_ID(SerialId);
        PltCommonLogIns.setSPONSOR("UPDATEUSERID."+(String)mapTenantInfo.get("TENANT_ID") +"."+
		        Thread.currentThread().getName());
        
        // --- 获取账期 ---
        String strMaxDate = TaskBaseMapperDao.getMaxDate();//获取日志表的账期 每天更新        
        /*String strMaxDate = "20170424";*/
        String changeTableDateId = TaskBaseMapperDao.getChangeTableDateId(TenantId);//获取PLT_USER_CHANGE表中的DATE_ID
        
        log.info("最新账期为:"+ strMaxDate);
        log.info("最新数据的更新账期为:"+changeTableDateId);
        
        // --- 判断是否需要更新工单表的userid --- 		 
        if(strMaxDate == null || !strMaxDate.equals(changeTableDateId)){
        	log.info("工单表的userid已经更新或数据还没有准备好");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("WARN");
			PltCommonLogIns.setBUSI_DESC("工单表的userid已经更新或数据还没有准备好,strCurMaxDay="+changeTableDateId);
			UpdateDataIns.insertPltCommonLog(PltCommonLogIns);
			return -1;        	
        }
        
        // --- 设置更新工单表userid的进程正在处理 ---
        UpdateDataIns.setValueToGlobal("UPDATEUSERID.FLAG." + (String)mapTenantInfo.get("TENANT_ID"), "true");
       
        log.info("---租户：{}更新工单表的userid进程开始---",(String)mapTenantInfo.get("TENANT_ID"));
       
        // --- 更新工单表的userid ---
        String mysqlTableName = UpdateDataIns.getValueFromGlobal("UPDATEUSERID.TABLE");//配置2 获取需要更新的工单表
        String mysqlUpdateSql = UpdateDataIns.getValueFromGlobal("UPDATEUSERID.UPDATE");//配置3 获取sql语句
        String strUpdateSql = mysqlUpdateSql.replaceAll("TTTTTENANT_ID", 
				(String)mapTenantInfo.get("TENANT_ID"));
        
        String strTableItem[] = mysqlTableName.split(",");
		
		for(String rec:strTableItem){
			String realUpdateSqlData = strUpdateSql.replaceAll("TTTTTABLENAME", rec);
			jdbcTemplate.execute(realUpdateSqlData);
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("WARN");
			PltCommonLogIns.setBUSI_DESC("用户的userid更新完毕,strCurMaxDay="+strMaxDate);
			PltCommonLogIns.setBUSI_ITEM_1(realUpdateSqlData);
			UpdateDataIns.insertPltCommonLog(PltCommonLogIns);
		}
		log.info("工单表的userid更新完毕");
		
		// --- 更新进程标识
		UpdateDataIns.setValueToGlobal("UPDATEUSERID.FLAG."+(String)mapTenantInfo.get("TENANT_ID"), "false");
		log.info("---租户：{}更新工单表的userid进程结束---",(String)mapTenantInfo.get("TENANT_ID"));
		return 0;
	}
	
}
