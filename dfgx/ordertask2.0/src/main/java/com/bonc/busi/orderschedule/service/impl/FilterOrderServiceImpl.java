package com.bonc.busi.orderschedule.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import com.bonc.busi.orderschedule.GenOrderIns;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.bo.BlackWhiteUserList;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.orderschedule.bo.ParamMap;
import com.bonc.busi.orderschedule.bo.PltActivityExecuteLog;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.bo.PltActvityRemainInfo;
import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.orderschedule.files.OrderFileMannager;
import com.bonc.busi.orderschedule.log.LogToDb;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.redis.JodisPoolConfiguration;
import com.bonc.busi.orderschedule.redis.JodisProperties;
import com.bonc.busi.orderschedule.routing.OrderTableManager;
import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.mapper.BaseMapper;

import redis.clients.jedis.Jedis;

@Service("filterOrderService")
@EnableAutoConfiguration
// @ConfigurationProperties(prefix = "xcloud", ignoreUnknownFields = false)
public class FilterOrderServiceImpl implements FilterOrderService {
	private final static Logger LOG = LoggerFactory.getLogger(FilterOrderServiceImpl.class);

	@Autowired
	private OrderMapper ordermapper;
	
	@Autowired
	private ApiManage  apiManage;
	
	@Autowired
	private BaseMapper baseMapper;

	
	// 过滤重复工单
	public String getSysDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	private Timestamp getDateTime(String datetime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date parsed = null;
		try {
			parsed = format.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new java.sql.Timestamp(parsed.getTime());

	}

	// 有进有出(工单表)

	// 有进有出(短信工单表)

	// 覆盖（工单表）

	@Override
	public String isBlackWhiteUser(BlackWhiteUserList user) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@//132.35.224.165:1521/dwtest", "usertool_hn",
					"USERTOOL_HN_123");

			stmt = conn.createStatement();
			// String sql = "insert into ddd values('18522829266')";
			String sql = "SELECT USER_ID as USERID FROM CLYX_ACTIVITY_FILTE_USERS a WHERE ( a.USER_ID = ";
			sql += "'";
			sql += user.getUSER_ID();
			sql += "'";
			sql += " and a.FILTE_TYPE = ";
			sql += "'";
			sql += user.getFILTE_TYPE();
			sql += "'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				return rs.getString("USERID");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

		// return ordermapper.isBlackWhiteUser(user);
	}

	/**
	 * 过滤工单，包括过滤黑白名单，和删除重复工单
	 */
	@Override
	public void filterOrderStatus(String actId,Integer activitySEQID, String tenantId, List<String> channelList) {
		PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
		exeLog.setCHANNEL_ID("0");
    	exeLog.setACTIVITY_SEQ_ID(activitySEQID);
    	exeLog.setTENANT_ID(tenantId);
    	exeLog.setBUSI_CODE(1008);
    	exeLog.setBEGIN_DATE(new Date());
    	exeLog.setPROCESS_STATUS(0);
    	exeLog.setACTIVITY_ID(actId);
    	LogToDb.recordActivityExecuteLog(exeLog, 0);
    	
		for (String channelId : channelList) {
			Date begin = new Date();
			LOG.info("[OrderCenter] filter channel id=" + channelId + " begin.");
			//get activity update rule 1:有进有出；2：覆盖
			List<PltActivityInfo> activityList = new ArrayList<PltActivityInfo>();
			activityList = getActivitysByActivitySeqId(activitySEQID,tenantId);
			if (activityList.size() >= 1) {
				Integer updateRule = activityList.get(0).getORDER_UPDATE_RULE();
				LOG.info("########################################## updateRule:" +updateRule);
				if (updateRule == null) {
					LOG.error("[OrderCenter] filterOrderStatus,activity update rule rule null,return.");
					return;
				}
				
				if (updateRule == 1) {
					int inOutFilterCount = 0;
					for(int i = 0 ; i < activityList.size() ; i++){
					//set parameter
					ParamMap param = new ParamMap();
					param.setREC_ID(activitySEQID);//current batch id
					param.setTENANT_ID(tenantId);
					param.setACTIVITY_ID(activityList.get(0).getACTIVITY_ID());
					param.setACTIVITY_SEQ_ID(activityList.get(i).getREC_ID());//before batch id
					param.setLIMIT_NUMBER((SystemCommonConfigManager.getSysCommonCfgValue("LIMIT_NUMBER") != null) ? SystemCommonConfigManager.getSysCommonCfgValue("LIMIT_NUMBER"):"10000");//param.setLIMIT_NUMBER("50000");//every time deal numbers
					param.setBEGIN_REC_ID("0");
					//set order_status 3
					param.setORDER_STATUS("3");
					int perActivitySeqIdFiltedCount = outIntoRuleOrder(param, channelId);
					inOutFilterCount += perActivitySeqIdFiltedCount;
				  }
					//过滤⼯单数量同步到PLT_ACTIVITY_PROCESS_LOG表--开始
					ActivityProcessLog log = new ActivityProcessLog();
					log.setACTIVITY_ID(activityList.get(0).getACTIVITY_ID());
					log.setACTIVITY_SEQ_ID(activitySEQID);
					log.setTENANT_ID(tenantId);
					log.setCHANNEL_ID(channelId);
					log.setINOUT_FILTER_AMOUNT(inOutFilterCount);
					ordermapper.UpdateInOutFilterCountToActivityProcessLog(log);
					LOG.info(">>>>>>>>>>>>>>有进有出过滤的工单数量 :" + inOutFilterCount+"  渠道:" +channelId);
					//过滤⼯单数量同步到PLT_ACTIVITY_PROCESS_LOG表--结束
					//DEPRECATED
	  				//updateRepetitiveOrder(activitySEQID, tenantId, channelId);
				}
				// only for front line channel, because other channels order already sent.
				else if (updateRule == 2 && (channelId.equals("5") || channelId.toLowerCase().contains("d"))) {
					Map<String, Object> activityMap = new HashMap<String, Object>();
					activityMap.put("currentActivitySeq", activitySEQID);
					activityMap.put("TENANT_ID", tenantId);
					activityMap.put("CHANNEL_ID", channelId);
					activityMap.put("ACTIVITY_ID", activityList.get(0).getACTIVITY_ID());
					activityMap.put("beforeActivitySeq", activityList.get(0).getREC_ID());
					activityMap.put("LIMIT_NUMBER", (SystemCommonConfigManager.getSysCommonCfgValue("LIMIT_NUMBER")!=null) ? SystemCommonConfigManager.getSysCommonCfgValue("LIMIT_NUMBER"):"10000");//every time deal numbers records
					//DEPRECATED method
					//filterRuleOrder(activitySEQID, tenantId, channelId);
					//set order_status 4
					coverRuleOrder(activityMap);
					//如果周期性活动里进行了覆盖过滤，由于更改的plt_activity_process_log表里的记录是上一个批次的，
					//   因此需要调用统计表进行工单数据的更新
					LOG.info("覆盖过滤 执行完毕，调用 统计接口更新 数据，租户：{}，活动：{}，批次：{}，渠道:{}",tenantId,activityList.get(0).getACTIVITY_ID(), activityList.get(0).getREC_ID()+"",channelId);
					apiManage.channelInitHandle(activityList.get(0).getACTIVITY_ID(), tenantId, activityList.get(0).getREC_ID()+"", "true");
				}
				//only for front line channel, because other channels order already touch.
				if (channelId.equals("5")) {
					//set parameter
					ParamMap param = new ParamMap();
					param.setREC_ID(activitySEQID);//current batch id
					param.setTENANT_ID(tenantId);
					param.setACTIVITY_ID(activityList.get(0).getACTIVITY_ID());
					param.setACTIVITY_SEQ_ID(activityList.get(0).getREC_ID());//before batch id
					param.setLIMIT_NUMBER((SystemCommonConfigManager.getSysCommonCfgValue("LIMIT_NUMBER")!=null) ? SystemCommonConfigManager.getSysCommonCfgValue("LIMIT_NUMBER"):"10000");//every time deal numbers
					param.setBEGIN_REC_ID("0");
					param.setCHANN_ID(channelId);
					
					String touchOGR = ordermapper.getTouchLimitDayFromChannel(param);
					if(touchOGR == null ||touchOGR.trim().equals("")) {
						LOG.info("[OrderCenter] updateOrderByTouch,activityseqid="
								+ activitySEQID + " touch limit day null .");
						return;
					}
					//set order_status 2
					param.setTOUCH_LIMIT_DAY(touchOGR);	
					param.setORDER_STATUS("2");
					filterOrderByTouch(param);
					//DEPRECATED
//					updateOrderByTouchLimitDay(activitySEQID, tenantId, channelId);
				}
			}
			
			exeLog.setPROCESS_STATUS(1);
			exeLog.setEND_DATE(new Date());
			LogToDb.recordActivityExecuteLog(exeLog, 1);
			Date end = new Date();
			LOG.info("[OrderCenter] filter channel id=" + channelId + " end,time cost:" + (end.getTime() - begin.getTime()));
		}
	}
//
//	// 过滤黑名单
//	@Override
//	public void filterBlackUser(String tenant_id, Integer activity_seq_id, String channelid) {
//		ParamMap paramMap = new ParamMap();
//		paramMap.setACTIVITY_SEQ_ID(activity_seq_id);
//		paramMap.setTENANT_ID(tenant_id);
//		
//		//获取活动Id
//		Map<String, Object> activityMap = new HashMap<String, Object>();
//		activityMap.put("currentActivitySeq", activity_seq_id);
//		activityMap.put("TENANT_ID", tenant_id);
//		String activityId = ordermapper.selectActivityByActivitySEQID(activityMap);	
//	    paramMap.setACTIVITY_ID(activityId);
//		
//		paramMap.setType("1"); // 过滤后的更新状态
//		//set channel id for route table
//		paramMap.setCHANN_ID(channelid);
//		getJodisProperties();
//		JodisPoolConfiguration jpc = new JodisPoolConfiguration();
//		Jedis jedis = jpc.createJedisPool().getResource();
//		// isFilterSMS=true ，过滤并更新order_info 和order_info_sms两张表，并移入历史 表
//		// isFilterSMS=false ,过滤并更新order_info表 ,并移入历史表
//
//		LOG.info("===> ORDER_INFO FILTER BLACK USER  IS BEGINNING=====");
//		filterAndHisOrderInfoBlack(paramMap, jedis); // 过滤工单表并移入历史，删除原工单
//		LOG.info("<=== ORDER_INFO  FILTER  BLACK  USER  ENDED====");
//
//		if (jedis != null) {
//			jedis.close();
//		}
//	}
	
	@Autowired
	private BusiTools BusiTools;


	// 有进有出(工单表)
	private int outIntoRuleOrder(ParamMap param, String channelid) {
		// set channel_id for route table
		param.setCHANN_ID(channelid);
		//记录有进有出规则过滤的工单数目
		int outIntoRuleFilteredOrderCount = 0;
		//获取工单过滤使用的表名
		param.setCHANN_ID(channelid);
		param.setBusiType(0);
		String tableName = ordermapper.getOrderTableName(param);
	  if( tableName != null ) { //如果tableName ==null，表示活动+批次+租户+渠道所对应的工单还没有产生过
		  param.setTaleName(tableName);
		  Date begin = new Date();
		 while (true) {
			LOG.info("[OrderCenter] outIntoRuleOrder channel id=" + channelid +" begin.");
			List<OrderAndOrderSMS> userIdSet = new ArrayList<OrderAndOrderSMS>();
			// get before activity order list
			userIdSet = ordermapper.selectOrderUSERID(param);
			if (userIdSet == null || userIdSet.isEmpty()) {
				LOG.info("[OrderCenter] outIntoRuleOrder channel id=" + channelid +" before order list empty.");
				break;
			}
			
			param.setUSER_ID_SQL(getWhereSqlByUserId(userIdSet));
			//update order's order_status=3
			int updateNumber = ordermapper.updateOrderByUserList(param);
			//更新的工单数目
			outIntoRuleFilteredOrderCount +=updateNumber;
			
			//获取工单移入的历史表的表名
			String hisTableName = OrderTableManager.getAssignedTable(param.getACTIVITY_ID(), param.getTENANT_ID(), channelid, 1, updateNumber, param.getREC_ID());
			param.setTaleName(hisTableName);
			//move to his table
			ordermapper.insertOrderToHis(param);
			
			//delete order
			ordermapper.deleteOrders(param);
			
			//把param里的table名设置成工单表
			param.setTaleName(tableName); 
			
			param.setBEGIN_REC_ID(userIdSet.get(userIdSet.size() -1).getREC_ID().toString());
			//order records end
			if(userIdSet.size() < Integer.parseInt(param.getLIMIT_NUMBER()))
				break;
		  }
		    Date end = new Date();
			LOG.info("[OrderCenter] outIntoRuleOrder channel id=" + channelid +" end,time cost:" + (end.getTime() - begin.getTime()));
			return outIntoRuleFilteredOrderCount;
		}
	        return outIntoRuleFilteredOrderCount;
	}

	// 有进有出(短信工单表)
	// DEPRECIATE
	private void outIntoRuleOrderSMS(ParamMap param) {

		System.out.println("updateRule== 1");
		List<String> userIdSet = new ArrayList<String>();
		userIdSet = ordermapper.selectOrderSMSUSERID(param);
		for (String user : userIdSet) {
			param.setUSER_ID(user);
			OrderAndOrderSMS orderSMS = new OrderAndOrderSMS();
			orderSMS = ordermapper.selectOrderInfoSMSByUserId(param);
			if (null != orderSMS && orderSMS.getCHANNEL_STATUS().equals("0")) {

				// 更新过滤状态 更新INVALID_DATE=sysdate
				updateOrderSMS(orderSMS, "3");

			}

		}
	}

	// 覆盖（工单表）（传入的param是上次的活动）
	private void coverRuleOrder(ParamMap param, String channelid) {
		LOG.info("[OrderCenter] coverRuleOrder channel id=" + channelid +" end.");
		List<OrderAndOrderSMS> userOrderList = new ArrayList<OrderAndOrderSMS>();
		
		userOrderList = ordermapper.selectOrderForCover(param);
		if (null != userOrderList) {
			// 批量
			for (OrderAndOrderSMS order : userOrderList) {
				// 过滤重复工单 更新INVALID_DATE=sysdate
				updateOrder(order, "3");
			}

		}
		LOG.info("[OrderCenter] coverRuleOrder channel id=" + channelid +" end.");
	}

	// 覆盖（短信工单表）（传入的param是上次的活动）
	// DEPRECIATE
	private void coverRuleOrderSMS(ParamMap param) {

		System.out.println("updateRule==2");

		List<OrderAndOrderSMS> userOrderSMSList = new ArrayList<OrderAndOrderSMS>();

		userOrderSMSList = ordermapper.selectOrderSMSForCover(param);
		if (null != userOrderSMSList) {
			// 批量
			for (OrderAndOrderSMS order : userOrderSMSList) {
				// 过滤重复工单 更新INVALID_DATE=sysdate
				updateOrderSMS(order, "3");
			}

		}

	}


	// 根据接触频次过滤工单
	private void updateOrderByTouch(ParamMap param, String touchOGR) {
		//接触过滤的工单数
		int filteredOrderCount = 0;
		
		// 得到date格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date orderTouch = new Date();

		List<ParamMap> orderUserOrgSet = new ArrayList<ParamMap>();
		OrderAndOrderSMS order = new OrderAndOrderSMS();
		
		orderUserOrgSet = ordermapper.getUserOrgFromOrder(param);
		for (ParamMap userOrg : orderUserOrgSet) {
			String user = userOrg.getUSER_ID();
			String org = userOrg.getORG_PATH();
			param.setUSER_ID(user);
			param.setORG_PATH(org);
			Date beginDate = new Date();
			beginDate = userOrg.getBEGIN_DATE();
			System.out.println("beginDate:" + beginDate);

			order = ordermapper.getOrderByTouch(param);
			// 按照本次活动的USERID和orgpath找到上次活动中同一用户和客户经理下的对应工单
			if (order != null) {

				// 接触时间
				String contactDate = order.getCONTACT_DATE();
				//added by shenyj for bug
				if(contactDate == null) 
					continue;
				
				try { // orderInfo中的接触时间转换成date类型
					orderTouch = df.parse(contactDate);
				} catch (ParseException e) {
					System.out.println("日期格式不正确");
					e.printStackTrace();
				}
				Date date = touchTime(beginDate, touchOGR);
				// 判断接触时间和往前推一个接触频次的时间之间的大小
				long flag = orderTouch.getTime() - date.getTime();
				System.out.println("orderTouch:" + orderTouch.getTime() + "date:" + date.getTime());
				System.out.println("flag=" + flag);
				if (flag > 0 && !(order.getCONTACT_CODE().equals("0"))) { // 过滤工单
					System.out.println("******************here3*****************"); // REC_ID（Integer）
					// orderInfo中的自增ID
					updateOrder(order, "2");
					filteredOrderCount++;
				}
			}

		}	
		//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--开始
		ActivityProcessLog log = new ActivityProcessLog();
		log.setACTIVITY_ID(param.getACTIVITY_ID());
		log.setACTIVITY_SEQ_ID(param.getACTIVITY_SEQ_ID());
		log.setTENANT_ID(param.getTENANT_ID());
		log.setCHANNEL_ID(param.getCHANN_ID());
		log.setTOUCH_FILTER_AMOUNT(filteredOrderCount);
		ordermapper.UpdateTouchedFilterCountToActivityProcessLog(log);
		//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--结束
	}

	// 根据接触频次过滤短信工单
	private void updateOrderSMSByTouch(ParamMap param, String touchSMS) {
		if (touchSMS != null && !(touchSMS.trim().equals(""))) {
			// 得到date格式
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date orderTouch = new Date();

			List<ParamMap> orderUserOrgSet = new ArrayList<ParamMap>();
			OrderAndOrderSMS order = new OrderAndOrderSMS();
			// 本次活动得到的UserOrg
			orderUserOrgSet = ordermapper.getUserOrgFromOrderSMS(param);
			for (ParamMap userOrg : orderUserOrgSet) {
				String user = userOrg.getUSER_ID();
				String org = userOrg.getORG_PATH();
				param.setUSER_ID(user);
				param.setORG_PATH(org);
				Date beginDate = new Date();
				beginDate = userOrg.getBEGIN_DATE();
				System.out.println("beginDate:" + beginDate);

				// 上次活动的UserOrg的一条工单记录
				order = ordermapper.getOrderSMSByTouch(param);
				// 按照本次活动的USERID和orgpath找到上次活动中同一用户和客户经理下的对应工单
				if (order != null) {

					// 接触时间
					String contactDate = order.getCONTACT_DATE();

					try { // orderInfo中的接触时间转换成date类型
						orderTouch = df.parse(contactDate);
					} catch (ParseException e) {
						System.out.println("日期格式不正确");
						e.printStackTrace();
					}
					Date date = touchTime(beginDate, touchSMS);
					// 判断接触时间和往前推一个接触频次的时间之间的大小
					long flag = orderTouch.getTime() - date.getTime();
					System.out.println("orderTouch:" + orderTouch.getTime() + "date:" + date.getTime());
					System.out.println("flag=" + flag);
					if (flag > 0 && !(order.getCONTACT_CODE().equals("0"))) { // 过滤工单
						System.out.println("******************here3*****************"); // REC_ID（Integer）
						// orderInfo中的自增ID
						updateOrderSMS(order, "4");
					}
				}
			}
		}

	}


	/**
	 * 获得当前时间往前推一个接触频次的时刻
	 */
	public Date touchTime(Date date, String touch) {
		int touchLimitDay = Integer.parseInt(touch);

		Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		cal.setTime(date);
		// 传入参数：接触频次
		cal.add(Calendar.DAY_OF_MONTH, -touchLimitDay);

		Date touchdate = cal.getTime();
		System.out.println("date:" + date);
		return touchdate;
	}

	// 修改过滤状态，更新INVALID_DATE，移入历史工单（除短信）
	public void updateOrder(OrderAndOrderSMS order, String status) {
		ParamMap paramOrder = new ParamMap();
		paramOrder.setREC_ID(order.getREC_ID());
		paramOrder.setType(status);
		paramOrder.setINVALID_DATE(getDateTime(getSysDateTime()));
		paramOrder.setTENANT_ID(order.getTENANT_ID());
		paramOrder.setCHANN_ID(order.getCHANNEL_ID());
		
		//以下两给参数是2.0版本中为了使用路由模块获取表名而设置的参数	
		paramOrder.setFILTER_COUNT(1);
		String activityId = ordermapper.selectActivityIdByActivitySEQID(paramOrder);
		paramOrder.setACTIVITY_ID(activityId);
		
		ordermapper.updateOrderStatus(paramOrder);
		// 移入历史
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		order.setBEGIN_DATE(getDateTime(format.format(order.getBEGIN_DATE())));
		order.setEND_DATE(getDateTime(format.format(order.getEND_DATE())));
		order.setLAST_UPDATE_TIME(getDateTime(format.format(order.getLAST_UPDATE_TIME())));
		order.setINPUT_DATE(getDateTime(format.format(order.getINPUT_DATE())));
		order.setINVALID_DATE(getDateTime(format.format(paramOrder.getINVALID_DATE())));
		order.setORDER_STATUS(paramOrder.getType());
		//ordermapper.insertOrderInfoHis(order);
		//ordermapper.insertOrderInfoHis(order,paramOrder); //工单2.0版本改造修改(加了ParamMap参数)
		// 删除工单
		//ordermapper.deleteOrderByRecId(order);
		//ordermapper.deleteOrderByRecId(order,paramOrder); //工单2.0版本改造修改(加了ParamMap参数)
	}

	// 修改过滤状态，更新INVALID_DATE，移入历史工单（短信）
	public void updateOrderSMS(OrderAndOrderSMS order, String status) {
		ParamMap paramOrder = new ParamMap();
		paramOrder.setREC_ID(order.getREC_ID());
		paramOrder.setType(status);
		paramOrder.setINVALID_DATE(getDateTime(getSysDateTime()));
		paramOrder.setTENANT_ID(order.getTENANT_ID());

		ordermapper.updateOrderSMSStatus(paramOrder);
		// 移入历史
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		order.setBEGIN_DATE(getDateTime(format.format(order.getBEGIN_DATE())));
		order.setEND_DATE(getDateTime(format.format(order.getEND_DATE())));
		order.setLAST_UPDATE_TIME(getDateTime(format.format(order.getLAST_UPDATE_TIME())));
		order.setINPUT_DATE(getDateTime(format.format(order.getINPUT_DATE())));
		order.setINVALID_DATE(getDateTime(format.format(paramOrder.getINVALID_DATE())));
		order.setORDER_STATUS(paramOrder.getType());
		
		//工单2.0改造 ParamMap请求参数
		ParamMap param = new ParamMap();
		param.setREC_ID(order.getACTIVITY_SEQ_ID());
		param.setTENANT_ID(order.getTENANT_ID());
		String activityId = ordermapper.selectActivityIdByActivitySEQID(param);	
		param.setCHANN_ID(order.getCHANNEL_ID());
		param.setACTIVITY_ID(activityId);
		param.setFILTER_COUNT(1);

		//ordermapper.insertOrderInfoSMSHis(order,param);
		// 删除工单
		//ordermapper.deleteOrderSmsByRecId(order,param);
	}
	//******************************************2017.03.15优化**************************************************//
	// -- 工单更新规则：覆盖 --
		private void coverRuleOrder(Map<String, Object> param) {
			LOG.info("[OrderCenter] coverRuleOrder channel id=" + param.get("CHANNEL_ID") + " begin.");
			//get before batch order number
			String orderNum = ordermapper.getChannelOrderNumber((Integer)param.get("beforeActivitySeq"), 
																(String)param.get("CHANNEL_ID"),
																(String)param.get("TENANT_ID"));
			int iOrderNum = 0;
			if (orderNum != null && !orderNum.equals("0") ) {
				iOrderNum = Integer.parseInt(orderNum);
			} else {
				LOG.info("[OrderCenter] coverRuleOrder channel id=" + param.get("CHANNEL_ID") + " before batch order number 0.");
				return;
			}
			
			String  activityId = (String)param.get("ACTIVITY_ID");
			Integer activitySeqId = (Integer)param.get("beforeActivitySeq");
			String  tenantId = (String)param.get("TENANT_ID");
			String  channelId = (String)param.get("CHANNEL_ID");

			//查询工单表名
			ParamMap map = new ParamMap();
			map.setACTIVITY_ID((String)param.get("ACTIVITY_ID"));
			map.setACTIVITY_SEQ_ID((Integer)param.get("beforeActivitySeq"));
			map.setTENANT_ID((String)param.get("TENANT_ID"));
			map.setCHANN_ID((String)param.get("CHANNEL_ID"));
			map.setBusiType(0);
			String updateTableName = ordermapper.getOrderTableName(map);
			if( updateTableName != null) {  //updateTableName ==null 表示活动+批次+租户+渠道所对应的工单还没有产生过
			param.put("tableName", updateTableName);
			//every time deal LIMIT_NUMBER records
			int limitNumber = Integer.parseInt((String)param.get("LIMIT_NUMBER"));
			int totalUpdateCount = 0;
//			for ( int i = 0; i < iOrderNum/limitNumber +1 ; ++i ) {
//				int updateCount = ordermapper.updateCoveredRuleOrder(param);
//				totalUpdateCount = totalUpdateCount + updateCount;
//				//获取移入的工单名称
//				String hisTableName = OrderTableManager.getAssignedTable(activityId, tenantId, channelId, 1, limitNumber, activitySeqId);
//				param.put("hisTableName", hisTableName);
//				ordermapper.insertCoveredOrder(param);
//				ordermapper.deleteBeforeOrder(param);
//			}
			//查询要更新的工单表的某个批次的工单的最大最小rec_id
			Map<String,Object> minAndMaxRecIdMap = ordermapper.queryMinAndMaxRecId(updateTableName,(Integer)param.get("beforeActivitySeq"),channelId,tenantId);
			int count = 0;
			if(minAndMaxRecIdMap == null){
				return;  //工单表中没有找到改批次的工单
			}
			count = ((Long) minAndMaxRecIdMap.get("count")).intValue();
			System.out.println("上一个批次的工单量："+ count + ",上一批次：" + activitySeqId);
//			int maxRecId = (Integer)minAndMaxRecIdMap.get("maxRecId");
//		    int minRecId = (Integer)minAndMaxRecIdMap.get("minRecId");
			int maxRecId = 0;
			int minRecId = 0;
		    if(count ==0){  //如果需要更新的工单
		    	LOG.info("批次: {}, 没有需要覆盖的工单",activitySeqId);
		    	return;
		    } 
		    try{
				maxRecId = (Integer)minAndMaxRecIdMap.get("maxRecId");
				minRecId = (Integer)minAndMaxRecIdMap.get("minRecId");
			}catch(Exception ex){
				maxRecId = ((Long)minAndMaxRecIdMap.get("maxRecId")).intValue();
				minRecId = ((Long)minAndMaxRecIdMap.get("minRecId")).intValue();
			}
		    int round = (maxRecId-minRecId)/limitNumber;
		    int lBeginRec = 0;
		    int lEndRec = 0;
		    for(int i = 0 ; i<=round; ++i){
		    	lBeginRec = minRecId + limitNumber*i;
		    	if(i==round){
		    		lEndRec = maxRecId;
		    	}else{
		    		lEndRec = lBeginRec + limitNumber-1;
		    	}
		    	param.put("lBeginRec", lBeginRec);
		    	param.put("lEndRec", lEndRec);
		    	int updateCount = ordermapper.updateCoveredRuleOrderV2(param);
		    	LOG.info("批量更新完工单状态");
		    	//获取移入的工单名称
				String hisTableName = OrderTableManager.getAssignedTable(activityId, tenantId, channelId, 1, limitNumber, activitySeqId);
				param.put("hisTableName", hisTableName);
				ordermapper.insertCoveredOrderV2(param);
				LOG.info("批量移完工单");
				ordermapper.deleteBeforeOrderV2(param);
				totalUpdateCount = totalUpdateCount + updateCount;
				LOG.info("批量删除完工单");
		    }
			//过滤单数量同步到PLT_ACTIVITY_PROCESS_LOG表--开始
			ActivityProcessLog log = new ActivityProcessLog();
			log.setACTIVITY_ID((String)param.get("ACTIVITY_ID"));
			log.setACTIVITY_SEQ_ID((Integer)param.get("beforeActivitySeq"));
			log.setTENANT_ID((String)param.get("TENANT_ID"));
			log.setCHANNEL_ID((String)param.get("CHANNEL_ID"));
			log.setCOVERAGE_FILTER_AMOUNT(totalUpdateCount);  //更新覆盖的工单数量
			ordermapper.UpdateCoveredFilterCountToActivityProcessLog(log);
			//过滤单数量同步到PLT_ACTIVITY_PROCESS_LOG表--结束
			LOG.info(">>>>>>>>>>>>>>覆盖过滤的工单数量 :" + totalUpdateCount+"  渠道:" +channelId);
			LOG.info("[OrderCenter] coverRuleOrder channel id=" + param.get("CHANNEL_ID") + " end.");
		 }
		}		
		//DEPRECATED
		public void addOrderHis(OrderAndOrderSMS order,String status){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			order.setBEGIN_DATE(getDateTime(format.format(order.getBEGIN_DATE())));
			order.setEND_DATE(getDateTime(format.format(order.getEND_DATE())));
			order.setLAST_UPDATE_TIME(getDateTime(format.format(order.getLAST_UPDATE_TIME())));
			order.setINPUT_DATE(getDateTime(format.format(order.getINPUT_DATE())));
			order.setINVALID_DATE(getDateTime(format.format(getDateTime(getSysDateTime()))));
			order.setORDER_STATUS(status);
			
			//Param参数是2.0版本改造而需要设置的参数
			ParamMap param = new ParamMap();
			param.setCHANN_ID(order.getCHANNEL_ID());
			param.setTENANT_ID(order.getTENANT_ID());
			param.setREC_ID(order.getACTIVITY_SEQ_ID());
			String activityId = ordermapper.selectActivityIdByActivitySEQID(param);	
			param.setACTIVITY_ID(activityId);
			param.setFILTER_COUNT(1);
			//ordermapper.insertOrderInfoHis(order);
			//ordermapper.insertOrderInfoHis(order,param); //2.0版本改造
		}
		
		/**
		 *  reserve order  base on activity percent
		 *  @param actId: activity's id
		 *  @param actSeqId:activity's batch id 
		 *  @param tenantId:tenant id
		 *  @param channelList: activity channels gennerated  order 
		 */
	public void reserveOrder(String actId,Integer actSeqId, String tenantId, List<String> channelList) {
		//judge activity is need reserving
		String percent = ordermapper.getActivityReservePercent(actId, tenantId);
		if (percent == null || percent.equals("0")) {
			LOG.info("[OrderCenter] reserveOrder activity id=" + actId + " no need reserve,reserve percent:" + percent );
			return;
		}
		
		for (String channelId : channelList) {
			LOG.info("[OrderCenter] reserveOrder channel id=" + channelId + " begin.");
			Integer remainSeqId = ordermapper.getActivityRemainRecord(actId, tenantId,channelId);
			if (remainSeqId == null) {//activity first running need reserve 
				int reserveOrderNumber = reserveOrderByChannelId(actId,actSeqId,tenantId,channelId,percent);
				
				//commit reserve order record
				PltActvityRemainInfo remain = new PltActvityRemainInfo();
				remain.setCHANNEL_ID(channelId);
				remain.setACTIVITY_ID(actId);
				remain.setACTIVITY_SEQ_ID(actSeqId);
				remain.setTENANT_ID(tenantId);
				remain.setSAVE_NUMBER(reserveOrderNumber);
				remain.setSAVE_TIMEE(new Date());
				ordermapper.insertRemainInfo(remain);
				LOG.info(">>>>>>>>>>>>>留存的工单数量 ："+reserveOrderNumber+"  渠道:"+channelId);
				
			} else {//activity no need reserve,but need filter reserve order records
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("ACTIVITY_SEQ_ID", actSeqId);
				paramMap.put("TENANT_ID", tenantId);
				paramMap.put("CHANNEL_ID", channelId);
				paramMap.put("RESERVE_SEQ_ID", remainSeqId);
				paramMap.put("ACTIVITY_ID", actId);
				
				//留存过滤的工单数 
				int filteredOrderCount = 0;
				
				//获取工单过滤使用的表名：
				ParamMap tbMap = new ParamMap();
				tbMap.setACTIVITY_ID(actId);
				tbMap.setACTIVITY_SEQ_ID(remainSeqId);
				tbMap.setTENANT_ID(tenantId);
				tbMap.setCHANN_ID(channelId);
				tbMap.setBusiType(2);
				String hisTableName = ordermapper.getOrderTableName(tbMap);
				if(hisTableName != null) {
				paramMap.put("hisTableName", hisTableName);
				//filter order in current batch 
				filteredOrderCount = ordermapper.updateOrderInfoByReserve(paramMap);
				ordermapper.insertFilterOrderByReserved(paramMap);
				ordermapper.deleteFilterOrderByReserved(paramMap);
				
				//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--开始
				ActivityProcessLog log = new ActivityProcessLog();
				log.setACTIVITY_ID(actId);
				log.setACTIVITY_SEQ_ID(actSeqId);
				log.setTENANT_ID(tenantId);
				log.setCHANNEL_ID(channelId);
				log.setRESERVE_FILTER_AMOUNT(filteredOrderCount);
				ordermapper.UpdateReservedFilterCountToActivityProcessLog(log);
				//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--结束
				LOG.info(">>>>>>>>>>>>>留存过滤的工单数量 ："+filteredOrderCount+"  渠道:"+channelId);
			  }	
			}
			LOG.info("[OrderCenter] reserveOrder channel id=" + channelId + " end.");
		}
	}
	
	/**
	 *  reserve order  base on activity percent
	 *  @param actSeqId:activity's batch id 
	 *  @param tenantId:tenant id
	 *  @param channelId: channel id
	 *  @param percent: activity reserve percent 
	 *  @return: reserve order number
	 */
	private int reserveOrderByChannelId(String actId,Integer actSeqId, String tenantId, String channelId ,String percent) {
		//get order number for every area_no
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ACTIVITY_SEQ_ID", actSeqId);
		paramMap.put("TENANT_ID", tenantId);
		paramMap.put("CHANNEL_ID", channelId);
		paramMap.put("ACTIVITY_ID", actId);
		//channel's reserve order number
		int reserveOrderSum = 0;
		
		List<Map<String,Object>> mapNumber = ordermapper.getOrderNumberByArea(paramMap);
		for(Map<String,Object> map:mapNumber){
			//set order_status= 7 for every area_no orders
			LOG.info("[OrderCenter] reserveOrderByChannelId area_no=" + map.get("AREA_NO") + " ,number=" +map.get("CNT"));
			int oriNumber = Integer.parseInt(map.get("CNT").toString());
			int remindNumber = oriNumber * Integer.parseInt(percent) / 100;
			//reserve order 0,no update
			if (remindNumber == 0)
				continue;
			paramMap.put("AREA_NO", map.get("AREA_NO"));
			paramMap.put("LIMIT_NUMBER", remindNumber);
			ordermapper.updateRemindOrderByArea(paramMap);
			reserveOrderSum += remindNumber;
		}
		//no need reserve
		if (reserveOrderSum == 0) {
			LOG.info("[OrderCenter] reserveOrderByChannelId reserver number 0.");
			return reserveOrderSum;
		}
		
		//获取工单留存过滤使用的表名：
		String hisTableName = OrderTableManager.getAssignedTable(actId, tenantId, channelId, 2, reserveOrderSum, actSeqId);
		paramMap.put("hisTableName", hisTableName);
		//insert reserve order to remind table 
		ordermapper.insertRemindOrder(paramMap);
		//delete original orders
		ordermapper.deleteRemindOrder(paramMap);
		//update reserve orders flag to 5
		ordermapper.updateRemindOrder(paramMap);
		
		//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--开始
		ActivityProcessLog log = new ActivityProcessLog();
		log.setACTIVITY_ID(actId);
		log.setACTIVITY_SEQ_ID(actSeqId);
		log.setTENANT_ID(tenantId);
		log.setCHANNEL_ID(channelId);
		log.setRESERVE_FILTER_AMOUNT(reserveOrderSum);
		ordermapper.UpdateReservedFilterCountToActivityProcessLog(log);
		//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--结束
		return reserveOrderSum;
	}
	
	/**
	 *  get activity list by activity seq id
	 *  @param actSeqId:activity's batch id 
	 *  @param tenantId:tenant id
	 *  @return: activity list info
	 */
	private List<PltActivityInfo> getActivitysByActivitySeqId(int actSeqId, String tenantId) {

		Map<String, Object> activityMap = new HashMap<String, Object>();
		activityMap.put("currentActivitySeq", actSeqId);
		activityMap.put("TENANT_ID", tenantId);
		String activityId = ordermapper.selectActivityByActivitySEQID(activityMap);

		activityMap.put("ACTIVITY_ID", activityId);
		return ordermapper.selectRuleByActivity(activityMap);
	}
	
	/*
	 * get update sql condition part
	 * @param: order list
	 * @return: condition sql
	 */
	private String getWhereSqlByUserId(List<OrderAndOrderSMS>  orderList) {
		StringBuilder whereSql = new StringBuilder();
		whereSql.append(" USER_ID in (");
		for (OrderAndOrderSMS order : orderList) {
			whereSql.append("'");
			whereSql.append(order.getUSER_ID());
			whereSql.append("'");
			whereSql.append(",");
			
		}
		//filter , char
		whereSql.deleteCharAt(whereSql.length() - 1);
		whereSql.append(")");
		return whereSql.toString();
	}

	/*
	 * filter order records by touch
	 * @param: param
	 * @return:
	 */
	private void filterOrderByTouch(ParamMap param) {
		Date begin = new Date();
		//接触过滤的工单数
		int touchOrderCount = 0;
		LOG.info("[OrderCenter] filterOrderByTouch channel id=" + param.getCHANN_ID() +" begin.");
		//获取工单表名称
		param.setBusiType(0);
		String tableName = ordermapper.getOrderTableName(param);
		if( tableName != null) {  // tableName == null 表示活动+批次+租户+渠道所对应的工单还没有产生过
			param.setTaleName(tableName);
		while (true) {
			List<OrderAndOrderSMS> userIdSet = new ArrayList<OrderAndOrderSMS>();
			
			// get before activity touch order list
			userIdSet = ordermapper.getTouchUserList(param);
			if (userIdSet == null ||userIdSet.isEmpty()) {
				LOG.info("[OrderCenter] filterOrderByTouch channel id=" + param.getCHANN_ID() +" before order list empty.");
				break;
			}
			
			param.setUSER_ID_SQL(getWhereSqlByUserId(userIdSet));
			
			// update order's order_status=2
			int updateNumber = ordermapper.updateOrderByUserList(param);
			//保存接触过滤的工单数
			touchOrderCount += updateNumber;
			
			//获取使用的工单过滤表名
			String hisTableName = OrderTableManager.getAssignedTable(param.getACTIVITY_ID(), param.getTENANT_ID(), param.getCHANN_ID(), 1, updateNumber, param.getREC_ID());
			param.setTaleName(hisTableName);
			// move to his table
			ordermapper.insertOrderToHis(param);
			// delete order
			ordermapper.deleteOrders(param);
			
			//param的table参数再设置成工单表tableName
			param.setTaleName(tableName);
			
			param.setBEGIN_REC_ID(userIdSet.get(userIdSet.size() - 1).getREC_ID().toString());
			// order records empty
			if (userIdSet.size() < Integer.parseInt(param.getLIMIT_NUMBER()))
				break;

		}
		//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--开始
		ActivityProcessLog log = new ActivityProcessLog();
		log.setACTIVITY_ID(param.getACTIVITY_ID());
		log.setACTIVITY_SEQ_ID(param.getACTIVITY_SEQ_ID());
		log.setTENANT_ID(param.getTENANT_ID());
		log.setCHANNEL_ID(param.getCHANN_ID());
		log.setTOUCH_FILTER_AMOUNT(touchOrderCount); //更新接触过滤工单的数量
		ordermapper.UpdateTouchedFilterCountToActivityProcessLog(log);
		//过滤工单数量同步到PLT_ACTIVITY_PROCESS_LOG表--结束
		Date end = new Date();
		LOG.info(">>>>>>>>>>>>>接触过滤的工单数量 ：" + touchOrderCount+"  渠道:"+param.getCHANN_ID());
		LOG.info("[OrderCenter] filterOrderByTouch channel id=" + param.getCHANN_ID() +" end,time cost:" + (end.getTime() - begin.getTime()));
	  }
	}

    /**
     * 黑名单过滤工单的实现方法
     */
	@Override
	public void filterOrderWithBlackUser(String activityId, Integer activitySEQID, String tenantId, List<String> channelList) {
		LOG.info("=========进入黑名单过滤的方法， 活动：{},批次:{},租户:{},有黑名单用户:{}",activityId,activitySEQID,tenantId);
		//查询黑名单用户
		String filteType = "0";
		//系统配置表里获取有效数据分区
		String effectiveFlag = baseMapper.getValueFromSysCommCfg("ASYNBLACKANDWHITE.EFFECTIVE_PARTITION."+tenantId);
		if(effectiveFlag ==null) {
			effectiveFlag="1";
		}
		List<String> blackUserPhone = ordermapper.getBlackUserIds(filteType,tenantId,Integer.parseInt(effectiveFlag));
		LOG.info("==========查询黑名单的结果,有如下手机号: " + blackUserPhone);
		//黑名单表中有数据时才执行下面的过滤逻辑
		if(blackUserPhone!=null && blackUserPhone.size() > 0) {
		LOG.info("=========活动：{},批次:{},租户:{},有黑名单用户:{}",activityId,activitySEQID,tenantId,blackUserPhone);
		String blackUserIdSql = getBlackUserPhoneSql(blackUserPhone);
		ParamMap param = new ParamMap();
		param.setACTIVITY_ID(activityId);
		param.setREC_ID(activitySEQID);
		param.setTENANT_ID(tenantId);
		param.setUSER_ID_SQL(blackUserIdSql);
		for (String channelId : channelList) {
			// 1、更新临时表中工单的状态：把黑名单对应的工单设置成 1
			param.setCHANN_ID(channelId);
			param.setORDER_STATUS("1");
			int count = ordermapper.updateOrderByUserList(param);
			if (count > 0) {
				// 2、把临时表中工单状态为1的工单移入过滤表中
				// 获取工单移入的历史表的表名
				String hisTableName = OrderTableManager.getAssignedTable(activityId, tenantId, channelId, 1, count,
						activitySEQID);
				param.setTaleName(hisTableName);
				ordermapper.insertOrderToHis(param);
				// 3、 从临时表中删除工单状态为1的工单
				ordermapper.deleteOrders(param);		
			}
			// 4、同步记录到PLT_ACTIVITY_PROCESS_LOG表中
			ActivityProcessLog log = new ActivityProcessLog();
			log.setACTIVITY_ID(activityId);
			log.setACTIVITY_SEQ_ID(activitySEQID);
			log.setTENANT_ID(tenantId);
			log.setCHANNEL_ID(channelId);
			log.setBLACK_FILTER_AMOUNT(count); // 更新黑名单过滤工单的数量
			ordermapper.UpdateBlackUserFilterCountToActivityProcessLog(log);
			LOG.info(">>>>>>>>>>>>>>> 黑名单过滤的工单数量: " + count + "  渠道：" + channelId);
		 }
	   }
		LOG.info("=========退出黑名单过滤的方法， 活动：{},批次:{},租户:{},有黑名单用户:{}",activityId,activitySEQID,tenantId);
	}
	
	/**
	 * 生成根据USER_ID查询的Sql:   USER_ID in( 'a','b','c')
	 * @param userIds
	 * @return
	 */
	private String getBlackUserPhoneSql(List<String> blackUserPhone) {
		StringBuilder whereSql = new StringBuilder();
		whereSql.append(" PHONE_NUMBER in ( ");
		for(String userPhone : blackUserPhone) {
			whereSql.append("'");
			whereSql.append(userPhone);
			whereSql.append("'");
			whereSql.append(",");
		}
		whereSql.deleteCharAt(whereSql.length() - 1);
		whereSql.append(")");
		return whereSql.toString();
	}

	/**
	 * 临时表plt_order_info_temp里删除过滤的工单，并且把过滤的工单数记录到plt_activity_process_log表里
	 */
	@Override
	public void delAndUpdateFilterCount(String activityId, Integer activitySeqId, String tenantId,
			List<String> enableChannel, String orderStatus) {

		ParamMap param = new ParamMap();
		param.setACTIVITY_ID(activityId);
		param.setTENANT_ID(tenantId);
		param.setREC_ID(activitySeqId);
		param.setORDER_STATUS(orderStatus);
		for (String channelId : enableChannel) {
			param.setCHANN_ID(channelId);
			// 查询临时里指定渠道下的工单状态为orderStatus的工单数
			int count = ordermapper.getOrderCount(param);
			if (count > 0) {  //临时表里有orderStatus状态的工单时才会执行下面的操作
				// 把临时表中工单状态为orderStatus的工单移入过滤表中
				String hisTableName = OrderTableManager.getAssignedTable(activityId, tenantId, channelId, 1, count,
						activitySeqId);
				param.setTaleName(hisTableName);
				ordermapper.insertOrderToHis(param);
				// 从临时表中删除工单状态为orderStatus的工单
				ordermapper.deleteOrders(param);
				// 同步记录到PLT_ACTIVITY_PROCESS_LOG表中
				ActivityProcessLog log = new ActivityProcessLog();
				log.setACTIVITY_ID(activityId);
				log.setACTIVITY_SEQ_ID(activitySeqId);
				log.setTENANT_ID(tenantId);
				log.setCHANNEL_ID(channelId);
				log.setSUCCESS_FILTER_AMOUNT(count);
				if (orderStatus.equals("6")) { // 更新成功过滤工单的数量
					LOG.info(">>>>>>>>>>>>>>>成功过滤的工单数量 :"+count+"  渠道："+channelId);
					ordermapper.UpdateSuccessFilterCountToActivityProcessLog(log);
				}
			}
		}
	}

	/**
	 * 删除具有相同手机号的重复的工单
	 * 每个渠道需要执行如下过程：
	 * 1、查询有重复的手机号,根据 having(count(tel)) 实现
	 * 2、根据手机号查询最小的工单的Rec_Id： min(Rec_Id)
	 * 3、根据手机号删除Rec_Id不等于min(Rec_Id)
	 */
	@Override
	public void delRepeatedOrder(String activityid, Integer activitySeqId, String tenantId,
			List<String> enableChannel) {
		LOG.info(">>>>>>>>>>>>>>进入删除重复工单的过滤方法,活动Id:" + activityid + ",批次:" + activitySeqId);
		int deleteRepeatedTotal = 0;
		ExecutorService exec = Executors.newCachedThreadPool();  
		ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();
		for (String channelId : enableChannel) { // 每个渠道单独执行删除重复工单的操作
			results.add(exec.submit(new DeleteRepeatedOrderTask(ordermapper, channelId, tenantId, activityid, activitySeqId)));
//			List<Map<String, Object>> repeatedTelPhoneList = ordermapper.queryRepeatedPhonePerChannel(channelId,
//					tenantId);
//			if (repeatedTelPhoneList != null && repeatedTelPhoneList.size() != 0) {
//				int deleteCountPerChannel = 0;
//				LOG.info(">>>>>>>>进入每个渠道删除重复工单的方法，渠道:" + channelId + ",重复的手机号：" + repeatedTelPhoneList);
//				// 针对每个手机号进行删除操作
//				for (Map<String, Object> map : repeatedTelPhoneList) {
//					String telPhone = (String) map.get("PHONE_NUMBER"); // 手机号
//					Integer repeatedCount =  ((Long)map.get("REPEAT_COUNT")).intValue(); 
//					int deleteCountPerPhone = repeatedCount - 1;       // 重复的数量
//					LOG.info("手机号码：" + telPhone + "有" + deleteCountPerPhone + "条重复的工单");
//					// 保留重复工单里的RecId最小的工单
//					int minRecId = ordermapper.queryMinRecId(telPhone, channelId, tenantId);
//					ordermapper.deleteRepatedOrder(minRecId, telPhone, channelId, tenantId);
//					LOG.info("Delete: 删除了手机号码：" + telPhone + "" + deleteCountPerPhone + "条重复的工单");
//					deleteCountPerChannel += deleteCountPerPhone;
//				}
//				ActivityProcessLog log = new ActivityProcessLog();
//				log.setACTIVITY_ID(activityid);
//				log.setACTIVITY_SEQ_ID(activitySeqId);
//				log.setTENANT_ID(tenantId);
//				log.setCHANNEL_ID(channelId);
//				log.setREPEAT_FILTER_AMOUNT(deleteCountPerChannel); // 更新黑名单过滤工单的数量
//				ordermapper.UpdateRepeateFilterCountToActivityProcessLog(log);
//				LOG.info("<<<<<<<<<退出每个渠道删除重复工单的方法，渠道:" + channelId + "共删除重复的工单数：" + deleteCountPerChannel);
//				deleteRepeatedTotal += deleteCountPerChannel;
//			}
		}
		for(Future<Integer> fs : results){
			try {
				deleteRepeatedTotal += fs.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}finally{
				exec.shutdown();
			}
		}
		if (deleteRepeatedTotal != 0) {
			LOG.info("<<<<<<<<<退出删除重复工单的过滤的方法,一共删除重复的工单数：" + deleteRepeatedTotal + ", 活动Id: " + activityid + ",批次:" + activitySeqId);
		} else{
			LOG.info("<<<<<<<<<退出删除重复工单的过滤的方法,没有重复的工单,活动Id:" + activityid + ",批次: " + activitySeqId);
		}
	}
}


