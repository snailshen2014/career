package com.bonc.busi.service.func;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.dao.SysrunningcfgDao;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.service.mapper.CommonMapper;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.bo.ActivityFliteUsers;
import com.bonc.controller.ServiceController;

@Service("AsynBlackandWhite")
public class AsynBlackandWhite{

	@Autowired
	private BusiTools AsynDataIns;
	private final static Logger log= LoggerFactory.getLogger(AsynBlackandWhite.class);
	@Autowired  private JdbcTemplate jdbcTemplate;
	@Autowired	private CommonMapper CommonMapperIns;
	@Autowired	private SysFunction  SysFunctionIns;
	

public int handleData(String tenantId){
		
		SysLog	SysLogIns = new SysLog();
		
		int limitNum =Integer.parseInt(AsynDataIns.getValueFromGlobal("ASYNBLACKANDWHITE.INTOTABLE"));//每次插入数据量
		// --- 获取有效的分区标识 ---
		String      oldPartFlag = SysrunningcfgDao.query("ASYNBLACKANDWHITE.EFFECTIVE_PARTITION."+tenantId);//获取分区标识
		String		newPartFlag = "0".equals(oldPartFlag)?"1":"0";

		// --- 获取删除无效的分区数据sql ---
		String      truncateDataSql = AsynDataIns.getValueFromGlobal("ASYNBLACKANDWHITE.TRUNCATEDATASQL");
		//  /*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = TTTTTENANT_ID*/ALTER TABLE PLT_ACTIVITY_FILTE_USERS TRUNCATE PARTITION PPPPPART	
		// --- 替换特殊字段 ---
		String      truncateSql = truncateDataSql.replaceFirst("TTTTTENANT_ID",tenantId);
		truncateSql = truncateDataSql.replaceFirst("PPPPPART","p"+ newPartFlag);
		log.info(" Truncate Part Data sql = " + truncateSql);
		
		// --- 获取最小的userid ---
		HashMap<String, Object> minUserIdMap = CommonMapperIns.getMinUserId(tenantId);
		if(minUserIdMap == null || minUserIdMap.get("minUserId") == null){
			return -1;
		}
		// --- 删除无效的分区数据 ---
		jdbcTemplate.execute(truncateSql);
		
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AsynBlackandWhite");
		SysLogIns.setTENANT_ID(tenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("删除无效的分区数据p"+ newPartFlag + " sql:" + truncateSql);
		SysFunctionIns.saveSysLog(SysLogIns);
		
		int minUserId = Integer.parseInt(minUserIdMap.get("minUserId").toString());
		
		while(1>0){
			List<ActivityFliteUsers> blackandWhiteData = CommonMapperIns.getBlackandWhiteData(minUserId,tenantId, limitNum);
			if(blackandWhiteData == null || blackandWhiteData.isEmpty()){
				SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AsynBlackandWhite");
				SysLogIns.setTENANT_ID(tenantId);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("没有数据或者已经获取到全部数据");
				SysFunctionIns.saveSysLog(SysLogIns);
				log.info(" ====== 没有数据或者已经获取到全部数据  ====== ");
				break;
			}

			HashMap<String, Object> map = new HashMap<String,Object>();
			
			map.put("newPartFlag", newPartFlag);
			map.put("blackandWhiteData", blackandWhiteData);
			CommonMapperIns.insertIntoFilteTable(map);
			
			SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ServiceController.class.getName()+"-AsynBlackandWhite");
			SysLogIns.setTENANT_ID(tenantId);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("黑白名单正在向mysql表同步数据");
			SysFunctionIns.saveSysLog(SysLogIns);
			minUserId = Integer.parseInt(blackandWhiteData.get(blackandWhiteData.size()-1).getUSER_ID()) + 1;
		}
		SysrunningcfgDao.update("ASYNBLACKANDWHITE.EFFECTIVE_PARTITION."+tenantId,newPartFlag);
		return 0;
	}
}
		
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
