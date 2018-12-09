/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: JDBCUtil.java
 * @Prject: channelCenter
 * @Package: com.bonc.utils
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月16日 上午11:01:11
 * @version: V1.0  
 */

package com.bonc.utils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.entity.PltCommonLog;
import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;

/**
 * @ClassName: BusiTools
 * @Description: 常用工具类
 * @author: LiJinfeng
 * @date: 2016年12月16日 上午11:01:11
 */
@Component
public class BusiTools {
	
	@Autowired
    private GlobalCFGMapper globalCFGMapper;
	
	private static Log log = LogFactory.getLog(BusiTools.class);
	
	public String getGlobalValue(String strCfgKey){
		
		return globalCFGMapper.getGlobalCFG(strCfgKey);
		
	}
	
	public void insertCommonLog(String logMessage,String logLevel,PltCommonLog pltCommonLog,String... fields){
		
        //释放pltCommonLog的字段
		pltCommonLog.cleanFields();
		//设置pltCommonLog的字段
		pltCommonLog.setFields(logMessage,logLevel,fields);
		//插入通用日志表
		globalCFGMapper.insertCommonLog(pltCommonLog);
		try {
			//打印日志信息
			@SuppressWarnings("rawtypes")
			Class clazz = log.getClass();
			@SuppressWarnings("unchecked")
			Method logType = clazz.getMethod(logLevel,new Class[] { Object.class });
			Object[] arguments = new Object[] { logMessage };
			logType.invoke(log,arguments);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("occured exception when print log");
		}
		//释放字符串
		logMessage = null;
		logLevel = null;
		fields = null;
		
	}
	
	@Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRES_NEW)
	public synchronized	int getSequence(String  sequenceName){
		// --- 开启事物定义相关数据 -------------------------------------
		//TransactionDefinition def =  new DefaultTransactionDefinition();
		//TransactionStatus status =  tm.getTransaction(def);
		// --- 得到当前值  ---
		 Map<String, Object> mapResult = globalCFGMapper.getSequenceInfo(sequenceName);
		 if(mapResult == null){
			 return -1;
		 }
		 int		iCur_Value = (Integer)mapResult.get("CUR_VALUE");
		 int		iMax_Value = (Integer)mapResult.get("MAX_VALUE");
		 int		iInit_Value = (Integer)mapResult.get("INIT_VALUE");
		 if(iCur_Value == -1  || ( iCur_Value >= iMax_Value && iMax_Value != -1)){
			 // --- 更新初始值 ---
			 globalCFGMapper.initSequence(sequenceName);
			 return iInit_Value;
		 }
		
		// int		iCur_Value = (Integer)mapResult.get("CUR_VALUE");
		int  sequenceId =-1;
		globalCFGMapper.updateSequence(sequenceName);
		sequenceId = Integer.parseInt(String.valueOf(globalCFGMapper.getSequenceInfo(sequenceName).get("CUR_VALUE")));
		//tm.commit(status);
		//try{
		//	Thread.sleep(50000);
		//}catch(Exception e){
			
		//}
		return sequenceId;
	}
	
	public Boolean LoadDataToMysql(String sql,String tenantId){
		
		Boolean result = true;
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName(getGlobalValue("DS.MYSQL.DRIVER"));
			connection = DriverManager.getConnection(getGlobalValue("DS.MYSQL.URL."+tenantId),
					getGlobalValue("DS.MYSQL.USER."+tenantId),
					getGlobalValue("DS.MYSQL.PASSWORD."+tenantId));
			statement = connection.prepareStatement(sql);
			statement.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}finally{
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {}
			}
			if(connection!=null){
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		return result;	
		
	}

}
