package com.bonc.busi.statistic.service.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.statistic.mapper.StatisticActivityMapper;
import com.bonc.busi.statistic.service.StatisticService;
import com.bonc.busi.task.base.BusiTools;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService{

	private static final Logger log = Logger.getLogger(StatisticServiceImpl.class);
	
	@Autowired
	private StatisticActivityMapper mapper;
	
	@Autowired
	private BusiTools  BusiTools;
	
	/**
	 * 维度修改为 ORG_PATH、ACTIVITY_SEQ_ID
	 * @param req
	 * @return
	 */
	private String getStatisticSql(HashMap<String, Object> req){
		String activity = " AND ACTIVITY_SEQ_ID='"+req.get("activitySeqId")+"' ";
		String org = " AND ORG_PATH LIKE '"+req.get("orgPath")+"%' ";
		return (" SELECT NOW() STATISTIC_DATE,TENANT_ID,ACTIVITY_SEQ_ID,ORG_PATH,WENDING_FLAG LOGIN_ID,RESERVE3 IS_EXE,"
				+ " SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',3),'/',-1) AREA_NO," //客户经理归属地市
				+ " SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',4),'/',-1) CITY_ID," //客户经理归属地市
				+ " COUNT(*) TOTAL_NUM," //-- 有效总量
				+ " COUNT(IF(CONTACT_CODE IN ('101', '102', '103', '104', '121') AND DATE(CONTACT_DATE)=CURDATE(),TRUE,NULL)) VISIT_NUMS_TODAY," //-- 今日回访量
				+ " COUNT(IF(CONTACT_CODE IN ('101', '102', '103', '104', '121'), TRUE, NULL)) VISIT_NUMS_TOTAL," // -- 总回访量
				+ " COUNT(IF(CHANNEL_STATUS='3', TRUE, NULL)) INTER_SUCCESS," // -- 总成功量
				+ " COUNT(IF((CONTACT_CODE IN ('101', '102', '103', '104', '121') AND CHANNEL_STATUS='3'), TRUE, NULL)) VISITED_SUCCESS," //-- 已回访成功量
				+ " COUNT(IF((CONTACT_CODE IN ('101', '102', '103', '104', '121') AND CHANNEL_STATUS<>'3'), TRUE, NULL)) VISITED_NO_SUCCESS,"//已回访未成功
				+ " IFNULL(COUNT(IF(CONTACT_CODE='0' OR CONTACT_CODE IS NULL ,TRUE,NULL)),0) ITEM0,"//未接触量
				+ " IFNULL(COUNT(IF(CONTACT_CODE='101',TRUE,NULL)),0) ITEM101,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='102',TRUE,NULL)),0) ITEM102,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='103',TRUE,NULL)),0) ITEM103,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='104',TRUE,NULL)),0) ITEM104,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='121',TRUE,NULL)),0) ITEM121,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='201',TRUE,NULL)),0) ITEM201,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='202',TRUE,NULL)),0) ITEM202,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='203',TRUE,NULL)),0) ITEM203,"
				+ " IFNULL(COUNT(IF(CONTACT_CODE='204',TRUE,NULL)),0) ITEM204,"
				+ " IFNULL(COUNT(IF(CONTACT_TYPE='1',TRUE,NULL)),0) TYPE1,"
				+ " IFNULL(COUNT(IF(CONTACT_TYPE='2',TRUE,NULL)),0) TYPE2,"
				+ " IFNULL(COUNT(IF(CONTACT_TYPE='3',TRUE,NULL)),0) TYPE3, '"+req.get("MARKER")+"' MARKER "
				+ " FROM PLT_ORDER_INFO WHERE TENANT_ID='"+req.get("tenantId")+"' " + activity + org
				+ " AND CHANNEL_STATUS NOT IN('401','402','403') AND ORDER_STATUS=5 AND CHANNEL_ID='5' "
				+ " GROUP BY TENANT_ID,CHANNEL_ID,ACTIVITY_SEQ_ID,ORG_PATH,WENDING_FLAG,RESERVE3 ");
	}

	@Override
	public void reStatisticOrg(HashMap<String, Object> req) {
		long start = System.currentTimeMillis();
		mapper.delOrgStatistic(req);
		long end = System.currentTimeMillis();
		log.info("活动统计清除总耗时——>>>>"+(end-start)/1000.0+"s");
		
		String insert = "INSERT INTO PLT_ORDER_STATISTIC (STATISTIC_DATE,TENANT_ID,ACTIVITY_SEQ_ID,ORG_PATH,LOGIN_ID,IS_EXE,AREA_NO,CITY_ID,VALID_NUMS,VISIT_NUMS_TODAY,VISIT_NUMS_TOTAL,INTER_SUCCESS,VISITED_SUCCESS,VISITED_NO_SUCCESS,ITEM0, ITEM101,ITEM102,ITEM103,ITEM104,ITEM121,ITEM201,ITEM202,ITEM203,ITEM204,TYPE1,TYPE2,TYPE3,MARKER) ";
		req.put("MARKER", "1");
		insert = insert+getStatisticSql(req);
		BusiTools.executeDdlOnMysql(insert,""+req.get("tenantId"));
		
		HashMap<String, Object> orderHashMap = mapper.orderLimit(req);
		if(null==orderHashMap){
			return;
		}
		mapper.updateStatistic(orderHashMap);
		end = System.currentTimeMillis();
		log.info("活动统计总耗时——>>>>"+(end-start)/1000.0+"s");
	}
}
