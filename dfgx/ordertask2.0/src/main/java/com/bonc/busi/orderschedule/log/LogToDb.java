package com.bonc.busi.orderschedule.log;

import com.bonc.busi.orderschedule.bo.PltActivityExecuteLog;
import com.bonc.busi.orderschedule.mapper.ActivityExecuteLogDao;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;



/**
 * write log to db 
 * @author yanjunshen
 * @Time 2017-04-12 15:38
 */

public class LogToDb {
	//log db
	private final static BaseMapper BASE_MAPPER = (BaseMapper) SpringUtil.getApplicationContext().getBean("baseMapper");
	
	//log type
	private static final String logType = "55";
	
	/**
	 * write log to db table
	 * @param id
	 * @param sponsor
	 * @param busiCode
	 * @param items
	 */
	public static void writeLog(Integer id,
								String sponsor,
								String busiCode,
								Map<Integer,String> items) {
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE(logType);
		PltCommonLogIns.setSERIAL_ID(id);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR(sponsor);
		PltCommonLogIns.setBUSI_CODE(busiCode);
		String methodPrefix = "BUSI_ITEM_";
		for (Entry<Integer,String> entry : items.entrySet()) {
			Method m = null;
			try {
				String methodName = "set" + methodPrefix + entry.getKey();
				m = PltCommonLogIns.getClass().getMethod(methodName,String.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			try {
				m.invoke(PltCommonLogIns, entry.getValue());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		BASE_MAPPER.insertPltCommonLog(PltCommonLogIns);
		
	}
	/**
	 * record activity running log
	 * @param exeLog
	 * @param type 0:insert ;1:update
	 */
	public static void recordActivityExecuteLog(final PltActivityExecuteLog exeLog, int type) {
		if (0 == type) {
			ActivityExecuteLogDao.insert(exeLog);
		} else if (1 == type) {
			ActivityExecuteLogDao.updateStatus(exeLog);
		} else {
			System.out.println("Error activity execute log type");
		}
	}
	/**
	 * get  activity running status 
	 * @param exeLog
	 */
	public static Integer getActivityExecuteLogStatus(final PltActivityExecuteLog exeLog) {
		return ActivityExecuteLogDao.getStatus(exeLog);
	}
}
