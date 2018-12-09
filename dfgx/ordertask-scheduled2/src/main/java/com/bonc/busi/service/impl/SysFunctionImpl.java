package com.bonc.busi.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.utils.HttpUtil;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.dao.SyscommoncfgDao;

@Service()
public class SysFunctionImpl implements SysFunction{
	
	// --- 定义日志变量 ---
		private final static Logger log = LoggerFactory.getLogger(SysFunctionImpl.class);
	
	@Autowired	 private JdbcTemplate jdbcTemplate;
	// --- 定义事物管理的变量 -------------------
	@Autowired	private DataSourceTransactionManager tm;
	
	/*
	 * 查询当前帐期
	 */
	public		String			getCurMothDay(String tenant_id){
		String strCurMonthDay =null;
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setTENANT_ID(tenant_id);
		String cubeId = SyscommoncfgDao.query("GET_CUBE_ID");
		
		SysLogIns.setAPP_NAME("ORDERCONTROL-"+SysFunctionImpl.class+"-getCurMothDay");
		try{
		String  monthTimeUrl  = SyscommoncfgDao.query("GET_MONTH_TIME");
		if(monthTimeUrl == null){
			log.warn("GET_MONTH_TIME:没有设置");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("GET_MONTH_TIME:没有设置");
			SyslogDao.insert(SysLogIns);
			return null;
		}
		HashMap<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("tenantId", tenant_id);
        reqMap.put("cubeId", cubeId);
		strCurMonthDay = HttpUtil.doGet(monthTimeUrl,reqMap);
		
		if(strCurMonthDay == null || strCurMonthDay.length() == 0){
			log.warn("获取当前帐期时间失败");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("获取当前帐期时间失败");
			SyslogDao.insert(SysLogIns);
			return null;
		}
		log.info("strCurMonthDay={}",strCurMonthDay);
		strCurMonthDay = strCurMonthDay.substring(strCurMonthDay.indexOf(",") + 1, strCurMonthDay.length());
//		strCurMonthDay = strCurMonthDay.split(",")[1];
		if(strCurMonthDay == null || strCurMonthDay.length() == 0){
			log.warn("租户:"+tenant_id+" 获取帐期失败,strCurMonthDay="+strCurMonthDay);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("租户:"+tenant_id+" 获取帐期失败,strCurMonthDay="+strCurMonthDay);
			SyslogDao.insert(SysLogIns);
			return null;

			
		}
		}catch(Exception e){
			e.printStackTrace();
			SysLogIns.setLOG_TIME(new Date());
			saveExceptioneMessage(e,SysLogIns);
		}
		return strCurMonthDay;
	}
	
	/*
	 * 解锁成功检查标识
	 */
	public		int			unlockOderSucessFlag(String tenant_id){
		// --- 开启事物定义相关数据 -------------------------------------
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = tm.getTransaction(def);
		 try{
			 int count = SyscommoncfgDao.update("ORDERSUCESSCHECK.RUNFLAG."+tenant_id, "FALSE");
			 tm.commit(status);
			return count;
		 }catch(Exception e){
			tm.rollback(status);
			return 0;
		}		
	}
	
	/*
	 * 加锁成功检查标识
	 */
	public		int			lockOderSucessFlag(String tenant_id){
		// --- 开启事物定义相关数据 -------------------------------------
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = tm.getTransaction(def);
		 try{
			 int count = SyscommoncfgDao.update("ORDERSUCESSCHECK.RUNFLAG."+tenant_id, "TRUE");
			 tm.commit(status);
			return count;
		 }catch(Exception e){
			tm.rollback(status);
			return 0;
		}
	}
	
	/*
	 * 纪录系统日志
	 */
	 @Transactional
	public		boolean			saveSysLog(final SysLog log){
		 boolean		bFlag = true;
		// --- 开启事物定义相关数据 -------------------------------------
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = tm.getTransaction(def);
		 try{
			 // --- 判断时间是否设置 --
			 if(log.getLOG_TIME() == null)  return false;
			// --- 根据时间获取月份 ---
			SyslogDao.insert(log);
			tm.commit(status);
			return bFlag;
		 }catch(Exception e){
			 	tm.rollback(status);
				bFlag = false;
				return bFlag;
		}
	}
	 /*
		 * 异常信息入库
		 */
		public				void			saveExceptioneMessage(Exception e ,SysLog logIns){
			// --- 开启事物定义相关数据 -------------------------------------
			TransactionDefinition def = new DefaultTransactionDefinition();
			TransactionStatus status = tm.getTransaction(def);
			try{
				// --- 异常信息转换 ---
				StringWriter sw = new StringWriter();  
				PrintWriter pw = new PrintWriter(sw);  
				e.printStackTrace(pw);  
				String  message = sw.toString();
				if(message.length() < 8190)
				logIns.setLOG_MESSAGE(message);
				else 
					logIns.setLOG_MESSAGE(message.substring(0, 8189));
				// --- 入库 ---
				SyslogDao.insert(logIns);
				tm.commit(status);
				sw = null;
				pw = null;			
			}catch(Exception f){
				tm.rollback(status);
				f.printStackTrace();
			}
		}

}
