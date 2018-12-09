package com.bonc.busi.outer.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import java.text.SimpleDateFormat;  

import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.common.base.JsonResult;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.busi.outer.model.PltActivityInfo;
import com.bonc.busi.task.base.BusiTools;
import	com.bonc.busi.task.base.Global;

@Service("OrderActivityService")
public class OrderActivityServiceImpl  implements OrderActivityService{
	
	@Autowired
	PltActivityInfoDao		PltActivityInfoDaoIns;
	@Autowired
	 private JdbcTemplate jdbcTemplate;
	@Autowired
	private BusiTools  BusiToolsIns;
	
	private final static Logger log = LoggerFactory.getLogger(OrderActivityServiceImpl.class);
	
	/*
	 * 更新活动状态
	 */
	public  JsonResult setActivityStatus(ActivityStatus reqdata){
		JsonResult	JsonResultIns = new JsonResult();
		// --- 测试SQL  ---
		/*
		StringBuilder   sb = new StringBuilder();
		sb.append(" SELECT * FROM PLT_ACTIVITY_INFO ");
		List<Map<String,Object>>   list  =(List<Map<String,Object>>)jdbcTemplate.queryForList(sb.toString());
		if(list != null){
			for(Map map:list){
				log.warn("map:"+map);
			}
		}
		else{
			log.warn("list is null");
		}
		*/
		
		
		// --- 查询活动是否存在 ---
		final List<PltActivityInfo>		listPltActivityInfo = 
	//	PltActivityInfo		PltActivityInfoIns = 
				PltActivityInfoDaoIns.retrievePltActivityInfoByActivityId(reqdata.getActivityId(),
						reqdata.getTenant_id());
		if(listPltActivityInfo == null || listPltActivityInfo.size() == 0){
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("活动不存在 !!!");
			return JsonResultIns;
		}
		PltActivityInfo	PltActivityInfoIns = listPltActivityInfo.get(0);
		
		int     inActivityStatus =  Integer.parseInt(reqdata.getActivityStatus());
		// --- 判断输入 的活动状态和库中的状态是否相等 ---
		if(PltActivityInfoIns.getACTIVITY_STATUS() != inActivityStatus){
			if(PltActivityInfoIns.getACTIVITY_STATUS()  == 2){  // ---已经失效了是否就不允许修改了?
				JsonResultIns.setCode("2");
				JsonResultIns.setMessage("活动已经失效 !!!");
				return JsonResultIns;
			}
			if(inActivityStatus == 2){   // --- 活动失效  ---
				PltActivityInfoIns.setACTIVITY_STATUS(inActivityStatus);
				PltActivityInfoIns.setEND_DATE(new Date());
				PltActivityInfoDaoIns.expirePltActivityInfo(PltActivityInfoIns);
				// --- 启动新线程去执行工单的更改（暂时先不执行 )  ----
				ExecutorService pool = Global.getExecutorService();
				log.info(" 启动新线程执行");
				pool.execute(new Runnable() {
						public void run() {
							expireActivityHandle(listPltActivityInfo);
						}
					}
				);
			}
			else 	if( inActivityStatus == 9){   // --- 活动挂起  ---
				PltActivityInfoDaoIns.suspendActivity(PltActivityInfoIns);
			}
			else 	if( inActivityStatus == 8){   // --- 活动取消挂起  ---
				if(PltActivityInfoIns.getACTIVITY_STATUS() != 9){
					JsonResultIns.setCode("6");
					JsonResultIns.setMessage("活动未挂起 !!!");
					return JsonResultIns;
				}
				else{
					PltActivityInfoDaoIns.resumeActivity(PltActivityInfoIns);
				}
			}
			else{
				JsonResultIns.setCode("7");
				JsonResultIns.setMessage("传入的状态不支持 !!!");
				return JsonResultIns;
			}
		}
		log.warn("活动名称:"+PltActivityInfoIns.getACTIVITY_NAME());
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("update sucess");
		
		return JsonResultIns;
	}
	/*
	 * 启动线程转移工单
	 */
	private	void		expireActivityHandle(List<PltActivityInfo>		listPltActivityInfo){
		for(PltActivityInfo  item:listPltActivityInfo){
			String		strTenantId = item.getTENANT_ID();
			int			iActivitySeqId = item.getREC_ID();
			// --- 将工单移入历史  ---
			//int	result = PltActivityInfoDaoIns.moveOrderHis(iActivitySeqId,strTenantId);
		
			String		strMove = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.MOVE");
			String		strDelete = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.DELETE");
			String		strUpdate = BusiToolsIns.getValueFromGlobal("ACTIVITYSTATUS.UPDATE");
			String		strTmpMove = strMove.replaceFirst("TTTTTENANT_ID", strTenantId);
			String		strTmpDelete = strDelete.replaceFirst("TTTTTENANT_ID", strTenantId);
			String		strTmpUpdate = strUpdate.replaceFirst("TTTTTENANT_ID", strTenantId);
			String		strLastMove = strTmpMove.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(iActivitySeqId));
			String		strLastDelete = strTmpDelete.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));
			String		strLastUpdate = strTmpUpdate.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));
			
			log.info("更新工单时间:{}",strLastUpdate);
			// --- 更新历史工单上的时间 ---
			int result = jdbcTemplate.update(strLastUpdate);
			log.info("更新工单时间 ,工单序列号:{},数量:{}",iActivitySeqId,result);			
			log.info("移入工单 :{}",strLastMove);
			result = jdbcTemplate.update(strLastMove);
			log.info("工单移入历史 ,工单序列号:{},数量:{}",iActivitySeqId,result);
			// --- 删除工单  ---
			log.info("删除工单 :{}",strLastDelete);
			result = jdbcTemplate.update(strLastDelete);
			log.info("删除工单,工单序列号:{},数量:{}",iActivitySeqId,result);
			// --- 更新统计（先不执行） ---
		}
		
	}
}
