package com.bonc.busi.task.base;
/*
 * @desc:库内关联查询优化服务 
 * @author: 曾定勇
 * @time:2016-12-16
 */
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

//import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.thread.ThreadBaseFunction;

@Service("InnerDbQuerySrv")
//@SuppressWarnings("unchecked")
public class InnerDbQuerySrv {
	private final static Logger log= LoggerFactory.getLogger(InnerDbQuerySrv.class);
//	@Autowired	private BusiTools  AsynDataIns;
//	@Autowired	private BaseMapper  TaskBaseMapperDao;
	@Autowired	 private JdbcTemplate jdbcTemplate;
	@Autowired	private InnerDbQueryFunc InnerDbQueryFunc;
	
	public		int		query(Map<String,Object> mapPara){
		Date		dateBegin = new Date();
		int		iThreadNum = 4;   // --- 暂定,可以从数据库配置 ---
		// --- 得到第一个SQL并执行 ---
		String		strFirstSql = (String)mapPara.get("FIRSTSQL");
		// --- 执行第一个SQL,得到最小值和最大值 ---
		log.info("FIRSTSQL:{}",strFirstSql);
		Map<String,Object> mapFirst = jdbcTemplate.queryForMap(strFirstSql);
		log.info("execute time:{}",new Date().getTime() - dateBegin.getTime());
		// --- 得到最小值 ---
		long		lMinId = (long)mapFirst.get("MINID");
		// --- 得到最大值 ---
		long		lMaxId = (long)mapFirst.get("MAXID");
		// --- 计算步长 ---
		long		lStep = (lMaxId-lMinId)/iThreadNum;
		int 		i=0;
		long		lBeginId = lMinId;
		long		lCurId = lMinId;
		// --- 得到线程池变量  ---
		ExecutorService pool = Global.getExecutorService();
		List<Map<String, Object>> listSqlResult = new  ArrayList<Map<String, Object>>();
		ThreadBaseFunction		ThreadBaseFunctionIns = InnerDbQueryFunc;
		StringBuilder		sb = new StringBuilder();
		log.info("before  thread time:{}",new Date().getTime() - dateBegin.getTime());
		for(i =1;i <= iThreadNum;++i){
			lBeginId = lCurId;
			if(i == iThreadNum){
				lCurId = lMaxId+1;
			}
			else{
				lCurId = lMinId + lStep * i;
			}
			
			sb.setLength(0);
			// --- 定义传入线程的变量 ---
			Map<String, Object>		mapThreadPara = new HashMap<String, Object>();
			listSqlResult.add(mapThreadPara);
			sb.append(mapPara.get("SECONDSQL"));
			sb.append(" AND ");
			sb.append(mapPara.get("KEYID"));
			sb.append(" >= ");
			sb.append(lBeginId);
			sb.append(" AND ");
			sb.append(mapPara.get("KEYID"));
			sb.append(" < ");
			sb.append(lCurId);
			mapThreadPara.put("SQL", sb.toString());
			Thread  ThreadIns = new InnerDbQueryThread(ThreadBaseFunctionIns,mapThreadPara);
			pool.execute(ThreadIns);		
		}
		log.info("after  thread time:{}",new Date().getTime() - dateBegin.getTime());
		int		iTotalNum = 0;
		int		iSleepSeconds = 0;
		int		iEndNum = iThreadNum;
		boolean		bSleep = false;
		List<Map<String, Object>> listSqlData = new  ArrayList<Map<String, Object>>();
		while(iEndNum > 0){  // --- 需要判断多少时间结束 ---
			// --- 业务判断 ---
			try{
				bSleep = true;
				if(bSleep){
					++iSleepSeconds;
					Thread.sleep(1000);  // --- 休息一秒钟 ---
				}
				if(iSleepSeconds == 600){  // --- 时间过长，退出 (10分钟） ---
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
				return -1;
			}
			for(i=0;i<listSqlResult.size() ;++i){
				if(listSqlResult.get(i).get("ENDFLAG") != null){
					iEndNum--;
					// --- 合并结果 ---
					List<Map<String, Object>> listTmp =(List<Map<String, Object>>)listSqlResult.get(i).get("DATA");
					if(listTmp != null && listTmp.size() > 0) {
						for(Map<String, Object> item:listTmp){
							listSqlData.add(item);
						}
					}
					bSleep = false;
					break;
				}			
			}// for 
			if(bSleep == false){  // --- 有数据发生 ---
				listSqlResult.remove(i);
			}
		}		
		mapPara.put("DATA", listSqlData);
		return 0;
	}

}
