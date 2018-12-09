package com.bonc.busi.statistic.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.statistic.mapper.RmoveUserMapper;
import com.bonc.busi.statistic.service.RemoveUserService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.SysVars;
import com.bonc.utils.IContants;
import com.bonc.utils.PhoneUtil;
@Service("removeUserService")
public class RemoveUserServiceImpl implements RemoveUserService{
private static final Logger log = Logger.getLogger(RemoveUserServiceImpl.class);
	
	@Autowired
	private RmoveUserMapper mapper;
	
	@Autowired
	private BusiTools  BusiTools;
	
	/**
	 * 维度修改为 ORG_PATH、ACTIVITY_SEQ_ID
	 * @param req
	 * @return
	 */
	private String getStatisticSql(HashMap<String, Object> req){
		String activity =  (null==req.get("activitySeqId")||"".equals("activitySeqId"))?"":(" AND ACTIVITY_SEQ_ID='"+req.get("activitySeqId")+"' ");
		String org =  (null==req.get("orgPath")||"".equals("orgPath"))?"":(" AND ORG_PATH LIKE '"+req.get("orgPath")+"%' ");
		return (" SELECT NOW() STATISTIC_DATE,TENANT_ID,ACTIVITY_SEQ_ID,ORG_PATH,WENDING_FLAG LOGIN_ID,RESERVE3 IS_EXE, "
				+ " SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',3),'/',-1) AREA_NO, " //客户经理归属地市
				+ " SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',4),'/',-1) CITY_ID, " //客户经理归属地市
				+ " COUNT(*) TOTAL_NUM, " //-- 有效总量
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
				+ " AND CHANNEL_STATUS NOT IN('401','402','403') AND ORDER_STATUS=5 AND CHANNEL_ID='"+req.get("channelId")+"' "
				+ " GROUP BY TENANT_ID,CHANNEL_ID,ACTIVITY_SEQ_ID,WENDING_FLAG,ORG_PATH,RESERVE3 ");
	}
	
	//插入PLT_ORDER_STATISTIC新的统计数据
	public String insertStatistic(HashMap<String, Object> req){
		String insert = "INSERT INTO PLT_ORDER_STATISTIC (STATISTIC_DATE,TENANT_ID,ACTIVITY_SEQ_ID,ORG_PATH,LOGIN_ID,IS_EXE,AREA_NO,CITY_ID,VALID_NUMS,VISIT_NUMS_TODAY,VISIT_NUMS_TOTAL,INTER_SUCCESS,VISITED_SUCCESS,VISITED_NO_SUCCESS,ITEM0, ITEM101,ITEM102,ITEM103,ITEM104,ITEM121,ITEM201,ITEM202,ITEM203,ITEM204,TYPE1,TYPE2,TYPE3,MARKER) ";
		req.put("MARKER", "1");
		insert = insert + getStatisticSql(req);
		return insert;
	}
	
	public void initload(){
		//判断上次扫描是否结束  SysVars.getTenantId()
		String strRunFlag = BusiTools.getValueFromGlobal("ODS.EXECUTE.FLAG."+SysVars.getTenantId());
		//判断23G转4G是否在执行
		String turnFlag = BusiTools.getValueFromGlobal("UPDATEUSERID.FLAG."+SysVars.getTenantId());
		System.out.println("判断上次扫描是否结束 "+strRunFlag);
		System.out.println("判断23G转4G是否在执行 "+turnFlag);
		if(strRunFlag != null){
			if(turnFlag != null && turnFlag.equals("true")){
				log.warn("---23G转4G操作正在处理---"+SysVars.getTenantId());
				return ;
			}
			if(strRunFlag.equals("true")){
				log.warn("---离网操作正在处理---"+SysVars.getTenantId());
				List<String> lastUseres = mapper.getLastUseres(SysVars.getTenantId());
				if(lastUseres.size() == 4){
					String time = lastUseres.get(3).toString();
					if(time != null && !"".equals(time)){
						String dateid= BusiTools.getValueFromGlobal("ODS.EXECUTE.DATEID."+SysVars.getTenantId());
						if(dateid != null && !"".equals(dateid)){
							int diffInt = 0;
							try {
								diffInt = differentDaysByMillisecond(dateid,time);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(diffInt>0){
								log.warn("---离网操作正在处理---以经超过1天，强制停止");
								 // --- 离网操作正在处理 ---
								BusiTools.setValueToGlobal("ODS.EXECUTE.FLAG." + SysVars.getTenantId(), "true");
								updateAll(time);
								updateOrder(time);
								//记录已经更新的离网账期
								BusiTools.setValueToGlobal("ODS.EXECUTE.DATEID."+SysVars.getTenantId(), time);
								System.out.println("");
								//离网操作结束
								BusiTools.setValueToGlobal("ODS.EXECUTE.FLAG."+SysVars.getTenantId(), "false");
								log.warn("---离网已经处理！");
								return ;
							}else{
								log.warn("---离网操作正在处理---不差账期，应该是还在进行处理操作，请等候");
								return ;
							}
						}else{
							log.warn("---离网操作正在处理---系统配置表没有账期");
							return ;
						}
					}else{
						log.warn("---离网操作正在处理---plt_user_off表没有第三个账期");
						return ;
					}
				}else{
					log.warn("---离网操作正在处理---当前是最新账期");
					return ;
				}
			}
		}
		System.out.println("开始提取账期");
		//获取ods_execute_log表procname 三号环境是-3四号环境是-4
		String procname = BusiTools.getValueFromGlobal("ODS_EXECUTE_LOG_PROCNAME");
		//获取离网日志表最新数据日期
		String odsMax = mapper.findOdsMax(procname);
		//获取系统当前时间
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMdd");
	    String retStrFormatNowDate = sdFormatter.format(nowTime);
		int diffInt = 0;
		try {
			diffInt = differentDaysByMillisecond(odsMax,retStrFormatNowDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//如果超过三天没有更新数据将执行最新日期数据
		if(diffInt > 3){
			//获取离网表最新数据日期
			String offMax = mapper.findOffMax(SysVars.getTenantId());
			if(odsMax != null &&offMax!= null && odsMax.equals(offMax)){
				//检测当前账期是否已经处理
				String dateid= BusiTools.getValueFromGlobal("ODS.EXECUTE.DATEID."+SysVars.getTenantId());
				if(dateid != null && odsMax.equals(dateid)){
					log.warn("---该账期离网操作已经处理---"+SysVars.getTenantId());
					return ;
				}
				 // --- 离网操作正在处理 ---
				BusiTools.setValueToGlobal("ODS.EXECUTE.FLAG." + SysVars.getTenantId(), "true");
				updateAll(odsMax);
				updateOrder(odsMax);
				//记录已经更新的离网账期
				BusiTools.setValueToGlobal("ODS.EXECUTE.DATEID."+SysVars.getTenantId(), odsMax);
				//离网操作结束
				BusiTools.setValueToGlobal("ODS.EXECUTE.FLAG."+SysVars.getTenantId(), "false");
			}
			
		}else{
			//查询ods_execute_log表是否更新
			String strMaxDate = mapper.findUp(procname);
			//查询用户离网表是否更新
			String changeTableDateId  = mapper.findMax(SysVars.getTenantId());
			if(strMaxDate != null && changeTableDateId != null &&  strMaxDate.equals(changeTableDateId)){
				//检测当前账期是否已经处理
				String dateid= BusiTools.getValueFromGlobal("ODS.EXECUTE.DATEID."+SysVars.getTenantId());
				if(dateid != null && strMaxDate.equals(dateid)){
					log.warn("---该账期离网操作已经处理---"+SysVars.getTenantId());
					return ;
				}
				 // --- 离网操作正在处理 ---
				BusiTools.setValueToGlobal("ODS.EXECUTE.FLAG." + SysVars.getTenantId(), "true");
				updateAll(strMaxDate);
				updateOrder(strMaxDate);
				//记录已经更新的离网账期
				BusiTools.setValueToGlobal("ODS.EXECUTE.DATEID."+SysVars.getTenantId(), strMaxDate);
				//离网操作结束
				BusiTools.setValueToGlobal("ODS.EXECUTE.FLAG."+SysVars.getTenantId(), "false");
			}
		}
		
	}
	
	public void updateAll(String dataId) {
		System.out.println("开始更新客户经理工单");
		HashMap<String, Object> dateInfo = mapper.findDateId(dataId,SysVars.getTenantId());
		if(null==dateInfo){
			return;
		}
		long min=1;
		long max=0;
		if(null!=dateInfo.get("min")&&null!=dateInfo.get("max")){
			min=Long.parseLong(dateInfo.get("min").toString());
			max=Long.parseLong(dateInfo.get("max").toString());
		}
		long sum = 0;
		while (min<=max) {

			Long tempMax = min+3000;
			if(tempMax >= max){
				tempMax = max;
				sum = sum+(max - min);
			}else{
				sum = sum+3000;
			}
			HashMap<String, Object> userInfo = new HashMap<String, Object>();
			//查询离网用户id
			userInfo.put("dateId", dataId);
			userInfo.put("TenantId", SysVars.getTenantId());
			userInfo.put("min", min);
			userInfo.put("tempMax", tempMax);
			List<String> userIdes = mapper.findUseId(userInfo);
			if(userIdes != null && userIdes.size()>0){
				String userid = "";
				for (int i=0;i<userIdes.size();i++) {
					if(i==userIdes.size()-1){
						userid+="'"+userIdes.get(i)+"'";
					}else{
						userid+="'"+userIdes.get(i)+"',";
					}
				}
				//查询活动批次号  租户id
				System.out.println("开始查询客户经理渠道查询批次信息！");
				List<HashMap<String, Object>> recIdes = mapper.findRecId(userid,IContants.YX_CHANNEL,SysVars.getTenantId());
				System.out.println("客户经理渠道查询完批次信息！");
				int m=1;
				if(recIdes != null && recIdes.size()>0){
					if(userIdes != null && userIdes.size()>0){
						//查询一个批次下,需要移除的工单信息
						for (HashMap<String, Object> hashMap : recIdes) {
							System.out.println("总批次数量"+recIdes.size());
							//判断该批次是否有效
							int recidNum = mapper.findActivity((int)hashMap.get("ACTIVITY_SEQ_ID"));
							if(recidNum>0){

							}else{
								continue;
							}
							//查询ORG_PATH
							List<String> orderInfoes = mapper.findOrderInfos((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid,IContants.YX_CHANNEL);
							//查询出要删除的工单总数与已接触数
							HashMap<String, Object> orderIndex = mapper.findOrderIndex((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid,IContants.YX_CHANNEL);
							//查询出不重复channelid
							//List<String> orderChannel = mapper.findOrderChannel((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid);
							//获取总工单数
							int num = Integer.parseInt(orderIndex.get("NUM").toString());
							if(orderIndex!=null && orderIndex.size()>0&& num>0 && orderInfoes != null && orderInfoes.size()>0 && !"null".equals(orderInfoes.get(0))){
								//移入历史表
								String month = PhoneUtil.routeMonth();
								String insertInto = "INSERT INTO PLT_ORDER_INFO_HIS_"+month+"  SELECT * FROM PLT_ORDER_INFO "
										+ "  WHERE  ACTIVITY_SEQ_ID ='"+(int)hashMap.get("ACTIVITY_SEQ_ID")+"' AND TENANT_ID='"
										+(String) hashMap.get("TENANT_ID") +"' AND USER_ID IN ("+userid+")";
								BusiTools.executeDdlOnMysql(insertInto,(String) hashMap.get("TENANT_ID"));
								
								String updateOrderStatus = "UPDATE PLT_ORDER_INFO_HIS_"+month+" SET ORDER_STATUS = '10',LAST_UPDATE_TIME = NOW(),INPUT_DATE = NOW() "
										+ "  WHERE  ACTIVITY_SEQ_ID ='"+(int)hashMap.get("ACTIVITY_SEQ_ID")+"' AND TENANT_ID='"
										+(String) hashMap.get("TENANT_ID") +"' AND USER_ID IN ("+userid+")";
								BusiTools.executeDdlOnMysql(updateOrderStatus,(String) hashMap.get("TENANT_ID"));
								
								//mapper.moveOrderHis((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid);废弃移入历史
								//删除工单表
								mapper.deleteOrder((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid);
								for(int i=0;i<orderInfoes.size();i++){
									//修改统计表数据
									HashMap<String, Object> req = new HashMap<String, Object>();
									req.put("channelId", IContants.YX_CHANNEL);
									req.put("CHANNEL_ID", IContants.YX_CHANNEL);
									req.put("tenantId", hashMap.get("TENANT_ID"));
									req.put("activitySeqId", hashMap.get("ACTIVITY_SEQ_ID"));
									req.put("orgPath", orderInfoes.get(i));
									//统计清除
									long start = System.currentTimeMillis();
									mapper.delOrgStatistic(req);
									long end = System.currentTimeMillis();
									log.info("活动统计清除总耗时——>>>>"+(end-start)/1000.0+"s");
									//重新统计一个批次的信息
									String insert = "INSERT INTO PLT_ORDER_STATISTIC (STATISTIC_DATE,TENANT_ID,ACTIVITY_SEQ_ID,ORG_PATH,LOGIN_ID,IS_EXE,AREA_NO,CITY_ID,VALID_NUMS,VISIT_NUMS_TODAY,VISIT_NUMS_TOTAL,INTER_SUCCESS,VISITED_SUCCESS,VISITED_NO_SUCCESS,ITEM0, ITEM101,ITEM102,ITEM103,ITEM104,ITEM121,ITEM201,ITEM202,ITEM203,ITEM204,TYPE1,TYPE2,TYPE3,MARKER) ";
									req.put("MARKER", "1");
									insert = insert+getStatisticSql(req);
									BusiTools.executeDdlOnMysql(insert,""+req.get("tenantId"));
									//
									HashMap<String, Object> orderHashMap = mapper.orderLimit(req);
									if(null==orderHashMap){
										return;
									}
									mapper.updateStatistic(orderHashMap);
									end = System.currentTimeMillis();
									log.info("活动统计总耗时——>>>>"+(end-start)/1000.0+"s");
								}

								//数据表修改完毕
								//修改批次统计表
								HashMap<String, Object> smsMap = new HashMap<String, Object>();
								smsMap.put("tenantId", hashMap.get("TENANT_ID"));
								smsMap.put("channelId",IContants.YX_CHANNEL);
								smsMap.put("ACTIVITY_SEQ_ID",hashMap.get("ACTIVITY_SEQ_ID"));
								//根据不同渠道查询出
								List<HashMap<String, Object>> findSmsNoSend = mapper.findSmsNoSend(smsMap);
								for (HashMap<String, Object> hashMap2 : findSmsNoSend) {
									//工单池中有效工单量
									int sendNum = (int) hashMap2.get("VALID_NUM");
									int nums = Integer.parseInt(orderIndex.get("NUM").toString());
									if(orderIndex.get("NUM")!=null && nums < sendNum){
										smsMap.put("VALID_NUM", sendNum-nums);
									}else{
										smsMap.put("VALID_NUM", 0);
									}
									//代表的是接触的工单数
									int sendSucNum =(int) hashMap2.get("SEND_SUC_NUM");
									int vistNum = Integer.parseInt(orderIndex.get("VISITED").toString());
									if(orderIndex.get("VISITED")!= null && sendSucNum > vistNum){
										smsMap.put("SEND_SUC_NUM", sendSucNum-vistNum);
									}else{
										smsMap.put("SEND_SUC_NUM", 0);
									}
									smsMap.put("tenantId", hashMap.get("TENANT_ID"));
									smsMap.put("channelId",IContants.YX_CHANNEL);
									smsMap.put("ACTIVITY_SEQ_ID",hashMap.get("ACTIVITY_SEQ_ID"));
									//更新数据
									mapper.updateSmsStatistic(smsMap);
								}

							}

							System.out.println("第m次"+m);
							m++;
							if(recIdes.size()==m){
								System.out.println("结束");
							}
						}
						System.out.println("真的结束了+");
					}
				}
			}
			System.out.println("本次离网用户ID范围最小："+min);
			System.out.println("本次离网用户ID范围最大："+tempMax);
			System.out.println("已查询数据条数："+sum);
			min = min+3000;
			if(tempMax >= max){
				System.out.println("真的结束了!"+sum);
				return ;
			}
		}
	}
	
	/*
	 *比较相差天数
	 */
	 public static int differentDaysByMillisecond(String date1,String date2) throws ParseException
	    {
		 Date date = new SimpleDateFormat("yyyyMMdd").parse(date1); 
		 
		 Date date3 = new SimpleDateFormat("yyyyMMdd").parse(date2); 
	        int days = (int) ((date3.getTime() - date.getTime()) / (1000*3600*24));
	        return days;
	    }

	 /**
	  * 弹窗渠道移历史
	  * @param dataId
	  */
	 public void updateOrder(String dataId) {
		 System.out.println("开始更新弹窗工单");
		 //查询离网用户id SysVars.getTenantId()
		 String tenantId = SysVars.getTenantId();
//		 String tenantId = "uni076";
		 HashMap<String, Object> dateInfo = mapper.findDateId(dataId,tenantId);
			if(null==dateInfo){
				return;
			}
			long min=1;
			long max=0;
			if(null!=dateInfo.get("min")&&null!=dateInfo.get("max")){
				min=Long.parseLong(dateInfo.get("min").toString());
				max=Long.parseLong(dateInfo.get("max").toString());
			}
			long sum = 0;
			while (min<=max) {
				Long tempMax = min+3000;
				if(tempMax >= max){
					tempMax = max;
					sum = sum+(max - min);
				}else{
					sum = sum+3000;
				}
				HashMap<String, Object> userInfo = new HashMap<String, Object>();
				userInfo.put("dateId", dataId);
				userInfo.put("TenantId", tenantId);
				userInfo.put("min", min);
				userInfo.put("tempMax", tempMax);
				//查询离网用户id
				List<String> userIdes = mapper.findUseId(userInfo);
				if(userIdes != null && userIdes.size()>0){
					String userid = "";
					for (int i=0;i<userIdes.size();i++) {
						if(i==userIdes.size()-1){
							userid+="'"+userIdes.get(i)+"'";
						}else{
							userid+="'"+userIdes.get(i)+"',";
						}
					}
					
					for(int x=0;x<10;x++) {
						String tableName = "PLT_ORDER_INFO_POPWIN_"+x;
											
						//查询活动批次号  租户id
		//				System.out.println(JSON.toJSONString(userid));
						List<HashMap<String, Object>> recIdes = mapper.findRecIdPopwin(userid,tenantId,tableName);
						int m=1;
						if(recIdes != null && recIdes.size()>0){
							if(userIdes != null && userIdes.size()>0){
								//查询一个批次下,需要移除的工单信息
								for (HashMap<String, Object> hashMap : recIdes) {
									System.out.println("总批次数量"+recIdes.size());
									//判断该批次是否有效
									int recidNum = mapper.findActivity((int)hashMap.get("ACTIVITY_SEQ_ID"));
									if(recidNum>0){
	
									}else{
										continue;
									}
									//查询出要删除的工单总数与已接触数
									Integer orderIndex = mapper.findPopwinIndex((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid,tableName);
									//获取总工单数
									int num = orderIndex.intValue();
									if( num>0){
										
										//获取当前月份
								        String month = PhoneUtil.routeMonth();
										//移入历史表
										String insertInto = "INSERT INTO PLT_ORDER_INFO_POPWIN_HIS_"+month+" SELECT * FROM  "+tableName
												+ "  WHERE  ACTIVITY_SEQ_ID ='"+(int)hashMap.get("ACTIVITY_SEQ_ID")+"' AND TENANT_ID='"
												+(String) hashMap.get("TENANT_ID") +"' AND USER_ID IN ("+userid+")";
										BusiTools.executeDdlOnMysql(insertInto,(String) hashMap.get("TENANT_ID"));
										
										String updateOrderStatus = "UPDATE PLT_ORDER_INFO_HIS_"+month+" SET ORDER_STATUS = '10',LAST_UPDATE_TIME = NOW(),INPUT_DATE = NOW() "
												+ "  WHERE  ACTIVITY_SEQ_ID ='"+(int)hashMap.get("ACTIVITY_SEQ_ID")+"' AND TENANT_ID='"
												+(String) hashMap.get("TENANT_ID") +"' AND USER_ID IN ("+userid+")";
										BusiTools.executeDdlOnMysql(updateOrderStatus,(String) hashMap.get("TENANT_ID"));
										
										//mapper.moveOrderHis((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid);废弃移入历史
										//删除工单表
										mapper.deleteOrderPopwin((int)hashMap.get("ACTIVITY_SEQ_ID"),(String) hashMap.get("TENANT_ID"),userid,tableName);
									}
	
									System.out.println("第m次"+m);
									m++;
//									if(recIdes.size()==m){
//										System.out.println(tableName+"结束");
//									}
								}
								System.out.println(tableName+"结束");
							}
						}
						
					}
				}
				System.out.println("本次离网用户ID范围最小："+min);
				System.out.println("本次离网用户ID范围最大："+tempMax);
				System.out.println("已查询数据条数："+sum);
				min = min+3000;
				if(tempMax >= max){
					System.out.println("弹窗离网---结束,处理数据"+sum);
					return ;
				}
			}
		}
	 /**
	  * 启动项目将离网状态设置为false
	  */
	 public void init(){
		 BusiTools.setValueToGlobal("ODS.EXECUTE.FLAG.uni076", "false");
	 }

}
