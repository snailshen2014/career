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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import com.bonc.busi.orderschedule.bo.BlackWhiteUserList;
import com.bonc.busi.orderschedule.bo.Order;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.orderschedule.bo.ParamMap;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.orderschedule.utils.JodisPoolConfiguration;
import com.bonc.busi.orderschedule.utils.JodisProperties;
import com.bonc.busi.task.base.BusiTools;

import redis.clients.jedis.Jedis;

@Service("filterOrderService")
@EnableAutoConfiguration
// @ConfigurationProperties(prefix = "xcloud", ignoreUnknownFields = false)
public class FilterOrderServiceImpl implements FilterOrderService {
	private final static Logger LOG = LoggerFactory.getLogger(FilterOrderServiceImpl.class);

	@Autowired
	private OrderMapper ordermapper;

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
	public void filterOrderStatus(Integer activitySEQID, String tenantId, boolean isSMS) {

		filterBlackUser(tenantId, activitySEQID, isSMS); // 过滤黑名单
		filterWhiteUser(tenantId, activitySEQID, isSMS); // 过滤白名单
		updateRepetitiveOrder(activitySEQID, tenantId, isSMS); // 删除重复工单(传入数据暂定)
		updateOrderByTouchLimitDay(activitySEQID, tenantId, isSMS);// 根据接触频次过滤工单(传入数据暂定)

	}

	
	// 过滤黑名单
	@Override
	public void filterBlackUser(String tenant_id, Integer activity_seq_id, boolean isFilterSMS) {
		ParamMap paramMap = new ParamMap();
		paramMap.setACTIVITY_SEQ_ID(activity_seq_id);
		paramMap.setTENANT_ID(tenant_id);
		paramMap.setType("1"); // 过滤后的更新状态
		getJodisProperties();
		JodisPoolConfiguration jpc = new JodisPoolConfiguration();
		Jedis jedis = jpc.createJedisPool().getResource();
		// isFilterSMS=true ，过滤并更新order_info 和order_info_sms两张表，并移入历史 表
		// isFilterSMS=false ,过滤并更新order_info表 ,并移入历史表
		if (isFilterSMS) {
			LOG.debug("=====ORDER_INFO AND ORDER_SMS FILTER BLACK USER  IS BEGINNING====");
			filterAndHisOrderInfoBlack(paramMap, jedis); // 过滤工单表并移入历史，删除原工单
			filterAndHisOrderSmsBlack(paramMap, jedis); // 过滤短信工单表并移入历史，删除原短信工单
		} else {
			LOG.debug("=====ORDER_INFO FILTER BLACK USER ORDER  IS BEGINNING=====");
			filterAndHisOrderInfoBlack(paramMap, jedis); // 过滤工单表并移入历史，删除原工单
		}

	}

	@Override
	public void filterAndHisOrderInfoBlack(ParamMap paramMap, Jedis jedis) {
		// TODO Auto-generated method stub
		// orders 既有黑名单也有白名单，也有都不是的工单
		List<OrderAndOrderSMS> orders = ordermapper.selectOrderInfoByActivitySeq(paramMap);
		// 遍历orders,判断每个order是否为黑名单
		if (orders.size() > 0) {
			for (OrderAndOrderSMS order : orders) {
				String s = "'";
				String userKey = "'clyxbw'+" + s + order.getPHONE_NUMBER() + s + "+'0'";
				if (jedis.exists(userKey)) {
					LOG.info("phoneNumber is blackUser ==> phoneNumber : {} , userId : {}, recid:{}", order.getPHONE_NUMBER(),
							order.getUSER_ID(),order.getREC_ID());
					if (jedis.get(userKey) != null) {
						LOG.info("==> redisKey : {} , userValue : {} ", userKey, jedis.get(userKey));
						ordermapper.updateOrderInfoStatusFilterBlack(order); // 更新工单状态
						LOG.info("==> update order_info.order_status=1 is  successful");
						// 移入历史表
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						order.setBEGIN_DATE(getDateTime(format.format(order.getBEGIN_DATE())));
						order.setEND_DATE(getDateTime(format.format(order.getEND_DATE())));
						order.setLAST_UPDATE_TIME(getDateTime(format.format(order.getLAST_UPDATE_TIME())));
						order.setINPUT_DATE(getDateTime(format.format(order.getINPUT_DATE())));
						order.setINVALID_DATE(getDateTime(format.format(order.getINVALID_DATE())));
						order.setORDER_STATUS(paramMap.getType());

						ordermapper.insertOrderInfoHis(order); // 移入历史表
						LOG.info("==> Insert order_info_his  is  successful");

						// 移入历史后删除原工单表
						ordermapper.deleteOrderByRecId(order);

					}
				} else {
					LOG.info("redisKey not in Redis ===> redisKey : {} ,userId : {} ,phoneNumber : {}", userKey,
							order.getUSER_ID(), order.getPHONE_NUMBER());
				}
			}
			if (jedis != null) {
	 	        jedis.close();
	 	    }
		} else {
			LOG.info("NO data  in table order_info where  activity_seq_id = {} , tenant_id = {}",
					paramMap.getACTIVITY_SEQ_ID(), paramMap.getTENANT_ID());
		}
	
	}

	@Override
	public void filterAndHisOrderSmsBlack(ParamMap paramMap, Jedis jedis) {
		// TODO Auto-generated method stub
		// orders 既有黑名单也有白名单，也有都不是的工单
		List<OrderAndOrderSMS> orderSMSs = ordermapper.selectOrderSMSByActivitySeq(paramMap);
		// 遍历orders,判断每个order是否为黑名单
		if (orderSMSs.size() > 0) {
			for (OrderAndOrderSMS orderSMS : orderSMSs) {
				String s = "'";
				String userKey = "'clyxbw'+" + s + orderSMS.getPHONE_NUMBER() + s + "+'0'";
				if (jedis.exists(userKey)) {
					LOG.info("phoneNumber is blackUser ==> phoneNumber : {} , userId : {} ", orderSMS.getPHONE_NUMBER(),
							orderSMS.getUSER_ID());
					if (jedis.get(userKey) != null) {
						LOG.info("==> redisKey : {} , userValue : {} ", userKey, jedis.get(userKey));
						ordermapper.updateOrderSmsStatusFilterBlack(orderSMS); // 更新工单状态
						LOG.info("==> update order_sms.order_status=1 is  successful");
						
						// 移入历史表
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						orderSMS.setBEGIN_DATE(getDateTime(format.format(orderSMS.getBEGIN_DATE())));
						orderSMS.setEND_DATE(getDateTime(format.format(orderSMS.getEND_DATE())));
						orderSMS.setLAST_UPDATE_TIME(getDateTime(format.format(orderSMS.getLAST_UPDATE_TIME())));
						orderSMS.setINPUT_DATE(getDateTime(format.format(orderSMS.getINPUT_DATE())));
						orderSMS.setINVALID_DATE(getDateTime(format.format(orderSMS.getINVALID_DATE())));
						orderSMS.setORDER_STATUS(paramMap.getType());
						System.out.println(orderSMS.getPREPARE_SEND_STATUS());
						ordermapper.insertOrderInfoSMSHis(orderSMS);
						LOG.info("==> Insert order_info_sms_his  is  successful");

						// 移入历史后删除短信工单表
						ordermapper.deleteOrderSmsByRecId(orderSMS);

					}
				} else {
					LOG.info("redisKey not in Redis ===> redisKey : {} ,userId : {} ,phoneNumber : {}", userKey,
							orderSMS.getUSER_ID(), orderSMS.getPHONE_NUMBER());
				}
			}
			if (jedis != null) {
	 	        jedis.close();
	 	    }
		} else {
			LOG.info("NO data  in table order_info_sms  where  activity_seq_id = {} , tenant_id = {}",
					paramMap.getACTIVITY_SEQ_ID(), paramMap.getTENANT_ID());
		}


	}

	// 过滤白名单
	@Override
	public void filterWhiteUser(String tenant_id, Integer activity_seq_id, boolean isFilterSMS) {
		ParamMap paramMap = new ParamMap();
		paramMap.setACTIVITY_SEQ_ID(activity_seq_id);
		paramMap.setTENANT_ID(tenant_id);
		paramMap.setType("2"); // 过滤后的更新状态
		getJodisProperties();
		JodisPoolConfiguration jpc = new JodisPoolConfiguration();
		Jedis jedis = jpc.createJedisPool().getResource();
		if (isFilterSMS) {
			LOG.info("====ORDER_INFO AND ORDER_SMS FILTER WHITE USER  IS BEGINNING====");
			filterAndHisOrderInfoWhite(paramMap, jedis); // 过滤工单表并移入历史 ,删除原工单
			filterAndHisOrderSmsWhite(paramMap, jedis); // 过滤短信工单表并移入历史 ，删除原短信工单

		} else {
			LOG.info("====WHITE USER ORDER FILTER IS BEGINNING====");
			filterAndHisOrderInfoWhite(paramMap, jedis); // 过滤工单表并移入历史 ，删除原工单
		}

	}

	@Override
	public void filterAndHisOrderInfoWhite(ParamMap paramMap, Jedis jedis) {
		// orders 既有黑名单也有白名单，也有都不是的工单
		List<OrderAndOrderSMS> orders = ordermapper.selectOrderInfoByActivitySeq(paramMap);
		// 遍历orders,判断每个order是否为白名单
		if (orders.size() > 0) {

			for (OrderAndOrderSMS order : orders) {
				String s = "'";
				String userKey = "'clyxbw'+" + s + order.getPHONE_NUMBER() + s + "+'1'";
				if (jedis.exists(userKey)) {
					LOG.info("phoneNumber is blackUser ==> phoneNumber : {} , userId : {}", order.getPHONE_NUMBER(),
							order.getUSER_ID());
					if (jedis.get(userKey) != null) {
						LOG.info("==> redisKey : {} , userValue : {} ", userKey, jedis.get(userKey));
						// 更新工单状态
						ordermapper.updateOrderInfoStatusFilterWhite(order);
						LOG.info("==> update order_info.order_status=2  is  successful");
						// 移入历史表
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						order.setBEGIN_DATE(getDateTime(format.format(order.getBEGIN_DATE())));
						order.setEND_DATE(getDateTime(format.format(order.getEND_DATE())));
						order.setLAST_UPDATE_TIME(getDateTime(format.format(order.getLAST_UPDATE_TIME())));
						order.setINPUT_DATE(getDateTime(format.format(order.getINPUT_DATE())));
						order.setINVALID_DATE(getDateTime(format.format(order.getINVALID_DATE())));
						order.setORDER_STATUS(paramMap.getType());
						ordermapper.insertOrderInfoHis(order);
						LOG.info("==> Insert order_info_his  is  successful");

						// 移入历史后删除原工单表
						ordermapper.deleteOrderByRecId(order);

					}
				} else {
					LOG.info("redisKey not in Redis ===> redisKey : {} ,userId : {} ,phoneNumber : {}", userKey,
							order.getUSER_ID(), order.getPHONE_NUMBER());
				}
			}
			if (jedis != null) {
	 	        jedis.close();
	 	    }
			
		} else {
			LOG.info("NO data  in table order_info where  activity_seq_id = {} , tenant_id = {}",
					paramMap.getACTIVITY_SEQ_ID(), paramMap.getTENANT_ID());
		}
		
	
	}

	@Override
	public void filterAndHisOrderSmsWhite(ParamMap paramMap, Jedis jedis) {
		List<OrderAndOrderSMS> orderSMSs = ordermapper.selectOrderSMSByActivitySeq(paramMap);
		// 遍历orders,判断每个order是否为白名单
		if (orderSMSs.size() > 0) {

			for (OrderAndOrderSMS orderSMS : orderSMSs) {
				String s = "'";
				String userKey = "'clyxbw'+" + s + orderSMS.getPHONE_NUMBER() + s + "+'1'";
				if (jedis.exists(userKey)) {
					LOG.info("phoneNumber is whiteUser ==> phoneNumber : {} ,userId : {} ", orderSMS.getPHONE_NUMBER(),
							orderSMS.getUSER_ID());
					if (jedis.get(userKey) != null) {
						LOG.info("==> redisKey : {} , userValue : {} ", userKey, jedis.get(userKey));
						ordermapper.updateOrderSmsStatusFilterWhite(orderSMS); // 更新工单状态
						LOG.info("===> update order_sms.order_status=2 is  successful");

						// 移入历史表
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						orderSMS.setBEGIN_DATE(getDateTime(format.format(orderSMS.getBEGIN_DATE())));
						orderSMS.setEND_DATE(getDateTime(format.format(orderSMS.getEND_DATE())));
						orderSMS.setLAST_UPDATE_TIME(getDateTime(format.format(orderSMS.getLAST_UPDATE_TIME())));
						orderSMS.setINPUT_DATE(getDateTime(format.format(orderSMS.getINPUT_DATE())));
						orderSMS.setINVALID_DATE(getDateTime(format.format(orderSMS.getINVALID_DATE())));
						orderSMS.setORDER_STATUS(paramMap.getType());
						ordermapper.insertOrderInfoSMSHis(orderSMS);
						LOG.info("===> Insert order_info_sms_his  is  successful");

						// 移入历史后删除短信工单表
						ordermapper.deleteOrderSmsByRecId(orderSMS);

					}
				} else {
					LOG.info("redisKey not in Redis ===> redisKey : {} ,userId : {} ,phoneNumber : {}", userKey,
							orderSMS.getUSER_ID(), orderSMS.getPHONE_NUMBER());
				}
			}
			if (jedis != null) {
	 	        jedis.close();
	 	    }
			
		} else {
			LOG.info(" NO data  in table order_info_sms  where  activity_seq_id = {} , tenant_id = {}",
					paramMap.getACTIVITY_SEQ_ID(), paramMap.getTENANT_ID());
		}


	}

	@Autowired
	private BusiTools BusiTools;

	public JodisProperties jodisProperties;

	@Override
	public JodisProperties getJodisProperties() {
		// TODO Auto-generated method stub
		String zkPath = BusiTools.getValueFromGlobal("SPRING.JODIS.ZKPATH");
		String product = BusiTools.getValueFromGlobal("SPRING.JODIS.PRODUCT");
//		String password = BusiTools.getValueFromGlobal("SPRING.JODIS.PASSWORD");
		String zkTimeOut = BusiTools.getValueFromGlobal("SPRING.JODIS.ZKTIMEOUT");
		String zkProxyDir = BusiTools.getValueFromGlobal("SPRING.JODIS.ZKPROXYDIR");
		String poolTotal = BusiTools.getValueFromGlobal("SPRING.JODIS.POOLTOTAL");
		String poolMaxIdle = BusiTools.getValueFromGlobal("SPRING.JODIS.POOLMAXIDLE");
		String poolMinIdle = BusiTools.getValueFromGlobal("SPRING.JODIS.POOLMINIDLE");

		jodisProperties = JodisProperties.newInstance();
//		jodisProperties.setPassword(password);
		jodisProperties.setProduct(product);
		jodisProperties.setZkPath(zkPath);
		if (zkTimeOut != null )
			jodisProperties.setZkTimeout(Integer.parseInt(zkTimeOut));
		jodisProperties.setZkProxyDir(zkProxyDir);
		if(poolTotal != null)
			jodisProperties.setPoolTotal(Integer.parseInt(poolTotal));
		if (poolMaxIdle != null)
			jodisProperties.setPoolMaxIdle(Integer.parseInt(poolMaxIdle));
		if (poolMinIdle != null)
			jodisProperties.setPoolMinIdle(Integer.parseInt(poolMinIdle));
		return jodisProperties;
	}
	// 有进有出(工单表)
		private void outIntoRuleOrder(ParamMap param) {

			System.out.println("updateRule== 1");
			List<String> userIdSet = new ArrayList<String>();
			userIdSet = ordermapper.selectOrderUSERID(param);
			for (String user : userIdSet) {
				param.setUSER_ID(user);
				OrderAndOrderSMS order = new OrderAndOrderSMS();
				order = ordermapper.selectOrderInfoByUserId(param);
				if (null != order && order.getCHANNEL_STATUS().equals("0")) {

					// 更新过滤状态 更新INVALID_DATE=sysdate
					updateOrder(order, "3");

				}
			}

		}

		// 有进有出(短信工单表)
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
		private void coverRuleOrder(ParamMap param) {

			System.out.println("updateRule==2");

			List<OrderAndOrderSMS> userOrderList = new ArrayList<OrderAndOrderSMS>();

			userOrderList = ordermapper.selectOrderForCover(param);
			if (null != userOrderList) {
				// 批量
				for (OrderAndOrderSMS order : userOrderList) {
					// 过滤重复工单 更新INVALID_DATE=sysdate
					updateOrder(order, "3");
				}

			}

		}

		// 覆盖（短信工单表）（传入的param是上次的活动）
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

		@Override
		// param 中REC_ID代表本次活动的ACTIVITY_SEQ_ID
		// ，ACTIVITY_SEQ_ID代表上次活动的ACTIVITY_SEQ_ID
		public void updateRepetitiveOrder(Integer activitySEQId, String tenantId, boolean isSMS) {
			System.out.println("here**************");
			// 得到按月按日的活动表的REC_ID和ORDER_UPDATE_RULE
			ParamMap param = new ParamMap();
			param.setREC_ID(activitySEQId);
			param.setTENANT_ID(tenantId);

			String activityId = ordermapper.selectActivityIdByActivitySEQID(param);
			param.setACTIVITY_ID(activityId);
			List<ParamMap> activitySEQ = new ArrayList<ParamMap>();
			activitySEQ = ordermapper.selectUpdateRuleByActivity(param);

			if (activitySEQ.size() >= 2) {

				Integer updateRule = activitySEQ.get(0).getORDER_UPDATE_RULE();
				param.setACTIVITY_SEQ_ID(activitySEQ.get(1).getREC_ID());

				// 只过滤工单表
				if (isSMS == false) {
					// 有进有出
					if (updateRule == 1) {
						outIntoRuleOrder(param);
						// 覆盖
					} else if (updateRule == 2) {

						coverRuleOrder(param);
					}
					// 过滤工单表和短信工单表
				} else if (isSMS == true) {
					// 有进有出
					if (updateRule == 1) {
						outIntoRuleOrder(param);
						outIntoRuleOrderSMS(param);
						// 覆盖
					} else if (updateRule == 2) {

						coverRuleOrder(param);
						coverRuleOrderSMS(param);
					}

				}

			}
		}

		  

		// 根据接触频次过滤工单
		private void updateOrderByTouch(ParamMap param, String touchOGR) {
			if (touchOGR != null && !(touchOGR.trim().equals(""))) {

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
					System.out.println("beginDate:"+beginDate);

					order = ordermapper.getOrderByTouch(param);
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
						Date date = touchTime(beginDate, touchOGR);
						// 判断接触时间和往前推一个接触频次的时间之间的大小
						long flag = orderTouch.getTime() - date.getTime();
						System.out.println("orderTouch:"+orderTouch.getTime()+"date:"+date.getTime());
						System.out.println("flag=" + flag);
						if (flag > 0 && !(order.getCONTACT_CODE().equals("0"))) { // 过滤工单
							System.out.println("******************here3*****************"); // REC_ID（Integer）
							// orderInfo中的自增ID
							updateOrder(order, "4");
						}
					}

				}
			}
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
					System.out.println("beginDate:"+beginDate);
					
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
						System.out.println("orderTouch:"+orderTouch.getTime()+"date:"+date.getTime());
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

		@Override
		// param 中REC_ID代表本次活动的ACTIVITY_SEQ_ID
		// ，ACTIVITY_SEQ_ID代表上次活动的ACTIVITY_SEQ_ID
		public void updateOrderByTouchLimitDay(Integer activitySEQId, String tenantId, boolean isSMS) {

			System.out.println("here**************");

			ParamMap param = new ParamMap();

			param.setREC_ID(activitySEQId);
			param.setTENANT_ID(tenantId);

			String activityId = ordermapper.selectActivityIdByActivitySEQID(param);
			param.setACTIVITY_ID(activityId);
			List<ParamMap> activitySEQ = new ArrayList<ParamMap>();
			activitySEQ = ordermapper.selectActivityForTouch(param);
			if (activitySEQ != null && activitySEQ.size() >= 2) {

				param.setACTIVITY_SEQ_ID(activitySEQ.get(1).getREC_ID());

				// 客户经理接触频次： 一个活动下的客户经理接触频次是一样的
				String touchOGR = ordermapper.getTouchLimitDayFromChannel(param);
				System.out.println("tohchOGR:" + touchOGR);
				// 短信渠道接触频次：一个活动下的短信接触频次是一样的
				String touchSMS = ordermapper.getSMSTouchLimitDayFromChannel(param);
				System.out.println("touchSMS:" + touchSMS);

				// 只过滤工单
				if (isSMS == false) {
					updateOrderByTouch(param, touchOGR);
					// 过滤工单和短信工单
				} else if (isSMS == true) {
					updateOrderByTouch(param, touchOGR);
					updateOrderSMSByTouch(param, touchSMS);
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

			ordermapper.updateOrderStatus(paramOrder);
			// 移入历史
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			order.setBEGIN_DATE(getDateTime(format.format(order.getBEGIN_DATE())));
			order.setEND_DATE(getDateTime(format.format(order.getEND_DATE())));
			order.setLAST_UPDATE_TIME(getDateTime(format.format(order.getLAST_UPDATE_TIME())));
			order.setINPUT_DATE(getDateTime(format.format(order.getINPUT_DATE())));
			order.setINVALID_DATE(getDateTime(format.format(paramOrder.getINVALID_DATE())));
			order.setORDER_STATUS(paramOrder.getType());
			ordermapper.insertOrderInfoHis(order);
			// 删除工单
			 ordermapper.deleteOrderByRecId(order);
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

			ordermapper.insertOrderInfoSMSHis(order);
			// 删除工单
			 ordermapper.deleteOrderSmsByRecId(order);
		}


}
