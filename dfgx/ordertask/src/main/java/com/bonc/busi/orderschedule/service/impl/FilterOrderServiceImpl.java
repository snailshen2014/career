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

import com.bonc.busi.outer.mapper.SmsOrderMapper;
import com.bonc.busi.task.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.orderschedule.bo.BlackWhiteUserList;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.orderschedule.bo.ParamMap;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.bo.PltActvityRemainInfo;
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
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private BaseMapper baseMapper;
	@Autowired
	private BusiTools busiTools;
	@Autowired
	private SmsOrderMapper smsOrderMapper;

	
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
	public void filterOrderStatus(Integer activitySEQID, String tenantId, List<String> channelList) {
		for (String channelId : channelList) {
			Date begin = new Date();
			LOG.info("[OrderCenter] filter channel id=" + channelId + " begin.");
			//get activity update rule 1:有进有出；2：覆盖
			List<PltActivityInfo> activityList = new ArrayList<PltActivityInfo>();
			activityList = getActivitysByActivitySeqId(activitySEQID,tenantId);
			if (activityList.size() >= 1) {
				Integer updateRule = activityList.get(0).getORDER_UPDATE_RULE();
				if (updateRule == null) {
					LOG.error("[OrderCenter] filterOrderStatus,activity update rule rule null,return.");
					return;
				}
				
				if (updateRule == 1) {
					for(int i = 0 ; i < activityList.size() ; i++){
						//set parameter
						ParamMap param = new ParamMap();
						param.setREC_ID(activitySEQID);//current batch id
						param.setTENANT_ID(tenantId);
						param.setACTIVITY_ID(activityList.get(0).getACTIVITY_ID());
						param.setACTIVITY_SEQ_ID(activityList.get(i).getREC_ID());//before batch id
						param.setLIMIT_NUMBER("50000");//every time deal numbers
						param.setBEGIN_REC_ID("0");
						//set order_status 3
						param.setORDER_STATUS("3");
						outIntoRuleOrder(param, channelId);
					}
					//DEPRECATED
	  				//updateRepetitiveOrder(activitySEQID, tenantId, channelId);
				}
				// only for front line channel, because other channels order already sent.
				else if (updateRule == 2 && channelId.equals("5")) {
					Map<String, Object> activityMap = new HashMap<String, Object>();
					activityMap.put("currentActivitySeq", activitySEQID);
					activityMap.put("TENANT_ID", tenantId);
					activityMap.put("CHANNEL_ID", channelId);
					activityMap.put("ACTIVITY_ID", activityList.get(0).getACTIVITY_ID());
					activityMap.put("beforeActivitySeq", activityList.get(0).getREC_ID());
					activityMap.put("LIMIT_NUMBER", "50000");//every time deal numbers records
					//DEPRECATED method
					//filterRuleOrder(activitySEQID, tenantId, channelId);
					//set order_status 4
					coverRuleOrder(activityMap);
				}
				//only for front line channel, because other channels order already touch.
				if (channelId.equals("5")||channelId.equals("19")) {
					//set parameter
					ParamMap param = new ParamMap();
					param.setREC_ID(activitySEQID);//current batch id
					param.setTENANT_ID(tenantId);
					param.setACTIVITY_ID(activityList.get(0).getACTIVITY_ID());
					param.setACTIVITY_SEQ_ID(activityList.get(0).getREC_ID());//before batch id
					param.setLIMIT_NUMBER("50000");//every time deal numbers
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
			
			
			Date end = new Date();
			LOG.info("[OrderCenter] filter channel id=" + channelId + " end,time cost:" + (end.getTime() - begin.getTime()));
		}
	}

	// 过滤黑名单
	@Override
	public void filterBlackUser(String tenant_id, Integer activity_seq_id, String channelid) {
		ParamMap paramMap = new ParamMap();
		paramMap.setACTIVITY_SEQ_ID(activity_seq_id);
		paramMap.setTENANT_ID(tenant_id);
		paramMap.setType("1"); // 过滤后的更新状态
		//set channel id for route table
		paramMap.setCHANN_ID(channelid);
		getJodisProperties();
		JodisPoolConfiguration jpc = new JodisPoolConfiguration();
		Jedis jedis = jpc.createJedisPool().getResource();
		// isFilterSMS=true ，过滤并更新order_info 和order_info_sms两张表，并移入历史 表
		// isFilterSMS=false ,过滤并更新order_info表 ,并移入历史表

		LOG.info("===> ORDER_INFO FILTER BLACK USER  IS BEGINNING=====");
		filterAndHisOrderInfoBlack(paramMap, jedis); // 过滤工单表并移入历史，删除原工单
		LOG.info("<=== ORDER_INFO  FILTER  BLACK  USER  ENDED====");

		if (jedis != null) {
			jedis.close();
		}
	}

	@Override
	public void filterAndHisOrderInfoBlack(ParamMap paramMap, Jedis jedis) {
		// TODO Auto-generated method stub
		System.out.println("Filter plt_order_info  black  user  list  is  beginning...........");
		// orders 既有黑名单也有白名单，也有都不是的工单
		List<OrderAndOrderSMS> orders = ordermapper.selectOrderInfoByActivitySeq(paramMap);
		// 遍历orders,判断每个order是否为黑名单
		if (orders.size() > 0) {
			for (OrderAndOrderSMS order : orders) {
				String s = "'";
				String userKey = "'clyxbw'+" + s + order.getPHONE_NUMBER() + s + "+'0'";
				try {
					if (jedis.exists(userKey)) {
						LOG.info("phoneNumber is blackUser ==> phoneNumber : {} , userId : {}, recid:{}",
								order.getPHONE_NUMBER(), order.getUSER_ID(), order.getREC_ID());

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
						LOG.info(
								"redisKey not exists in Redis ===> redisKey : {} , activity_seq_id : {} , userId : {} ,phoneNumber : {},tenant_id:{}",
								userKey, order.getACTIVITY_SEQ_ID(), order.getUSER_ID(), order.getPHONE_NUMBER(),
								order.getTENANT_ID());
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}
			// 关闭jedis，移到过滤黑白名单程序里了
			// if (jedis != null) {
			// jedis.close();
			// }
		} else {
			LOG.info("NO data  in table order_info where  activity_seq_id = {} , tenant_id = {}",
					paramMap.getACTIVITY_SEQ_ID(), paramMap.getTENANT_ID());
		}

	}

	@Override
	public void filterAndHisOrderSmsBlack(ParamMap paramMap, Jedis jedis) {
		// TODO Auto-generated method stub
		System.out.println("Filter  plt_order_info_sms  black  user  list  is  beginning...........");
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
						ordermapper.insertOrderInfoSMSHis(orderSMS);
						LOG.info("==> Insert order_info_sms_his  is  successful");

						// 移入历史后删除短信工单表
						ordermapper.deleteOrderSmsByRecId(orderSMS);

					}
				} else {
					LOG.info(
							"redisKey not exists in Redis ===> redisKey : {} ,userId : {} ,phoneNumber : {}, activity_seq_id : {}, tenant_id: {}",
							userKey, orderSMS.getUSER_ID(), orderSMS.getPHONE_NUMBER(), orderSMS.getACTIVITY_SEQ_ID(),
							orderSMS.getTENANT_ID());
				}
			}
			// 关闭jedis，移到过滤黑白名单程序里了
			// if (jedis != null) {
			// jedis.close();
			// }
		} else {
			LOG.info("NO data  in table order_info_sms  where  activity_seq_id = {} , tenant_id = {}",
					paramMap.getACTIVITY_SEQ_ID(), paramMap.getTENANT_ID());
		}

	}

	// 过滤白名单
	@Override
	public void filterWhiteUser(String tenant_id, Integer activity_seq_id, String channelid) {
		ParamMap paramMap = new ParamMap();
		paramMap.setACTIVITY_SEQ_ID(activity_seq_id);
		paramMap.setTENANT_ID(tenant_id);
		paramMap.setType("2"); // 过滤后的更新状态
		//set channel id for route table
		paramMap.setCHANN_ID(channelid);
		getJodisProperties();
		JodisPoolConfiguration jpc = new JodisPoolConfiguration();
		Jedis jedis = jpc.createJedisPool().getResource();
	
		LOG.info("===> ORDER_INFO FILTER  WHITE USER  IS BEGINNING====");
		filterAndHisOrderInfoWhite(paramMap, jedis); // 过滤工单表并移入历史 ，删除原工单
		LOG.info("<=== ORDER_INFO  FILTER  WHITE  USER  ENDED====");
		
		if (jedis != null) {
			jedis.close();
		}

	}

	@Override
	public void filterAndHisOrderInfoWhite(ParamMap paramMap, Jedis jedis) {
		System.out.println("Filter  plt_order_info  white  user  list  is  beginning...........");
		// orders 既有黑名单也有白名单，也有都不是的工单
		List<OrderAndOrderSMS> orders = ordermapper.selectOrderInfoByActivitySeq(paramMap);
		// 遍历orders,判断每个order是否为白名单
		if (orders.size() > 0) {

			for (OrderAndOrderSMS order : orders) {
				String s = "'";
				String userKey = "'clyxbw'+" + s + order.getPHONE_NUMBER() + s + "+'1'";
				try {
					if (jedis.exists(userKey)) {
						LOG.info("phoneNumber is whiteUser ==> phoneNumber : {} ,userId : {} ,activity_seq_id :{} ",
								order.getPHONE_NUMBER(), order.getUSER_ID(), order.getACTIVITY_SEQ_ID());
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
						LOG.info(
								"redisKey not exists in Redis ===> redisKey : {} , activity_seq_id : {} , userId : {} ,phoneNumber : {},tenant_id:{}",
								userKey, order.getACTIVITY_SEQ_ID(), order.getUSER_ID(), order.getPHONE_NUMBER(),
								order.getTENANT_ID());

					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}
			// 关闭jedis，移到过滤黑白名单程序里了
			// if (jedis != null) {
			// jedis.close();
			// }

		} else {
			LOG.info("NO data  in table order_info where  activity_seq_id = {} , tenant_id = {}",
					paramMap.getACTIVITY_SEQ_ID(), paramMap.getTENANT_ID());
		}

	}

	@Override
	public void filterAndHisOrderSmsWhite(ParamMap paramMap, Jedis jedis) {
		System.out.println("Filter  plt_order_info_sms  white  user  list  is  beginning...........");
		List<OrderAndOrderSMS> orderSMSs = ordermapper.selectOrderSMSByActivitySeq(paramMap);
		// 遍历orders,判断每个order是否为白名单
		if (orderSMSs.size() > 0) {

			for (OrderAndOrderSMS orderSMS : orderSMSs) {
				String s = "'";
				String userKey = "'clyxbw'+" + s + orderSMS.getPHONE_NUMBER() + s + "+'1'";
				if (jedis.exists(userKey)) {
					LOG.info("phoneNumber is whiteUser ==> phoneNumber : {} ,userId : {} ,activity_seq_id :{} ",
							orderSMS.getPHONE_NUMBER(), orderSMS.getUSER_ID(), orderSMS.getACTIVITY_SEQ_ID());
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
					LOG.info(
							"redisKey not exists in Redis ===> redisKey : {} ,userId : {} ,phoneNumber : {}, activity_seq_id : {}, tenant_id: {}",
							userKey, orderSMS.getUSER_ID(), orderSMS.getPHONE_NUMBER(), orderSMS.getACTIVITY_SEQ_ID(),
							orderSMS.getTENANT_ID());
				}
			}
			// 关闭jedis，移到过滤黑白名单程序里了
			// if (jedis != null) {
			// jedis.close();
			// }

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
		// modified by shenyj for call api one times
		if (jodisProperties != null)
			return jodisProperties;

		// TODO Auto-generated method stub
		String zkPath = BusiTools.getValueFromGlobal("SPRING.JODIS.ZKPATH");
		String product = BusiTools.getValueFromGlobal("SPRING.JODIS.PRODUCT");
		// String password =
		// BusiTools.getValueFromGlobal("SPRING.JODIS.PASSWORD");
		String zkTimeOut = BusiTools.getValueFromGlobal("SPRING.JODIS.ZKTIMEOUT");
		String zkProxyDir = BusiTools.getValueFromGlobal("SPRING.JODIS.ZKPROXYDIR");
		String poolTotal = BusiTools.getValueFromGlobal("SPRING.JODIS.POOLTOTAL");
		String poolMaxIdle = BusiTools.getValueFromGlobal("SPRING.JODIS.POOLMAXIDLE");
		String poolMinIdle = BusiTools.getValueFromGlobal("SPRING.JODIS.POOLMINIDLE");

		jodisProperties = JodisProperties.newInstance();
		// jodisProperties.setPassword(password);
		jodisProperties.setProduct(product);
		jodisProperties.setZkPath(zkPath);
		if (zkTimeOut != null)
			jodisProperties.setZkTimeout(Integer.parseInt(zkTimeOut));
		jodisProperties.setZkProxyDir(zkProxyDir);
		if (poolTotal != null)
			jodisProperties.setPoolTotal(Integer.parseInt(poolTotal));
		if (poolMaxIdle != null)
			jodisProperties.setPoolMaxIdle(Integer.parseInt(poolMaxIdle));
		if (poolMinIdle != null)
			jodisProperties.setPoolMinIdle(Integer.parseInt(poolMinIdle));
		return jodisProperties;
	}

	// 有进有出(工单表)
	private void outIntoRuleOrder(ParamMap param, String channelid) {
		// set channel_id for route table
		param.setCHANN_ID(channelid);
		while (true) {
			Date begin = new Date();
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
			ordermapper.updateOrderByUserList(param);
			//move to his table
			ordermapper.insertOrderToHis(param);
			// 短信渠道 分发进月份尾号表
			if (param.getCHANN_ID().equals("7")){
				moveToSmsHis(param);
			}
			//delete order
			ordermapper.deleteOrders(param);
			param.setBEGIN_REC_ID(userIdSet.get(userIdSet.size() -1).getREC_ID().toString());
			//order records end
			if(userIdSet.size() < Integer.parseInt(param.getLIMIT_NUMBER()))
				break;
			Date end = new Date();
			LOG.info("[OrderCenter] outIntoRuleOrder channel id=" + channelid +" end,time cost:" + (end.getTime() - begin.getTime()));

		}

	}

	private void moveToSmsHis(ParamMap param) {
		Integer activitySeqId = param.getACTIVITY_SEQ_ID();
		String hisTableName = "PLT_ORDER_INFO_SMS_HIS_";
		String tenantId = param.getTENANT_ID();
		String channelId = param.getCHANN_ID();
		hisTableName += busiTools.routeMonth();
		String moveOrderTemplate = baseMapper.getValueFromSysCommCfg("ACTIVITYSTATUS.MOVE");
		String deleteOrderTemplate = baseMapper.getValueFromSysCommCfg("ACTIVITYSTATUS.DELETE");
		LOG.info("============= 移工单操作开始");
		//updateMoveSmsOrderStatus();
		long beginTime = System.currentTimeMillis();
		int batchCount = 100000; //一次移动10万条
		Map<String,Object> countAndRecIdInfo = smsOrderMapper.queryCountAndRecId(hisTableName,activitySeqId,tenantId,channelId);
		int smsOrderCount = ((Long) countAndRecIdInfo.get("count")).intValue();
		if(smsOrderCount ==0){  //如果没有待移动的工单
			return;
		}
		int maxRecId = ((Long) countAndRecIdInfo.get("maxRecId")).intValue();
		int minRecId = ((Long) countAndRecIdInfo.get("minRecId")).intValue();
		LOG.info("批次："+ activitySeqId + ", 共有"+ smsOrderCount + "工单 , 最大RecId:" +maxRecId + ",最小RecId:" + minRecId);

		String replaceTenantId = moveOrderTemplate.replaceFirst("TTTTTNENAT_ID", tenantId);
		String replaceSeqId = replaceTenantId.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(activitySeqId));
		String replaceTargetTable = replaceSeqId.replaceFirst("TTTTTABLENAME_HIS", "TARGET_TABLE");
		String replaceSourceTableName = replaceTargetTable.replaceAll("TTTTTABLENAME", hisTableName);

		String deleteReplaceTenantId = deleteOrderTemplate.replaceFirst("TTTTTNENAT_ID", tenantId);
		String deleteReplaceSeqId = deleteReplaceTenantId.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(activitySeqId));
		String deleteReplaceSouceTableName = deleteReplaceSeqId.replaceFirst("TTTTTABLENAME", hisTableName);

		int round = (maxRecId-minRecId)/batchCount;
		int lBeginRec = 0;
		int lEndRec = 0;
		for(int i = 0 ; i<=round; ++i){   //批量移动，每次最多移动batchCount条
			lBeginRec = minRecId + batchCount*i;
			if(i==round){
				lEndRec = maxRecId;
			}else{
				lEndRec = lBeginRec + batchCount-1;
			}
			StringBuffer sbuffer = new StringBuffer();
			for(int phoneNumberLastDigit=0; phoneNumberLastDigit<10;phoneNumberLastDigit++){
				try{
					LOG.info("-----------------------------批量开始从按月分配的临时表中移动工单,lBeginRec={},lEndRec={}",lBeginRec,lEndRec);
					sbuffer.setLength(0);
					String targetHisTable = hisTableName+String.valueOf(phoneNumberLastDigit);  //根据按月拆分的表名得到按月按手机尾号拆分的表名PLT_ORDER_INFO_SMS_HIS_110
					String replaceTargetOrderTable = replaceSourceTableName.replaceFirst("TARGET_TABLE", targetHisTable);
					String appendPhoneNumberSelectCondition = replaceTargetOrderTable+"  AND PHONE_NUMBER LIKE '%LAST_NUMBER'";
					String moveOrderSql = appendPhoneNumberSelectCondition.replaceFirst("LAST_NUMBER", String.valueOf(phoneNumberLastDigit));
					sbuffer.append(moveOrderSql);
					sbuffer.append(" AND REC_ID >= ");
					sbuffer.append(lBeginRec);
					sbuffer.append(" AND REC_ID <= ");
					sbuffer.append(lEndRec);
					//目标sql: /*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */INSERT INTO PLT_ORDER_INFO_SMS_HIS_110 SELECT * FROM PLT_ORDER_INFO_SMS_HIS_11 WHERE ACTIVITY_SEQ_ID = 450043
					//        AND CHANNEL_ID = '7' AND PHONE_NUMBER LIKE '%0' AND REC_ID >=10001 AND REC_ID <=9999
					LOG.info("moveOrderSql={}", sbuffer.toString());
					int moveCount = jdbcTemplate.update(sbuffer.toString());
					LOG.info("-----------------------------批量结束从按月分配的临时表中移动工单,lBeginRec={},lEndRec={},批量移动了{}",lBeginRec,lEndRec,moveCount);
					LOG.info("-----------------------------批量开始从按月分配的临时表中删除工单,lBeginRec={},lEndRec={}",lBeginRec,lEndRec);
					sbuffer.setLength(0);
					appendPhoneNumberSelectCondition= deleteReplaceSouceTableName + "  AND PHONE_NUMBER LIKE '%LAST_NUMBER'";
					String deletOrderSql = appendPhoneNumberSelectCondition.replaceFirst("LAST_NUMBER", String.valueOf(phoneNumberLastDigit));
					sbuffer.append(deletOrderSql);
					sbuffer.append(" AND REC_ID >= ");
					sbuffer.append(lBeginRec);
					sbuffer.append(" AND REC_ID <= ");
					sbuffer.append(lEndRec);
					LOG.info("deleteOrderSql={}", sbuffer.toString());
					int deleteCount = jdbcTemplate.update(sbuffer.toString());
					LOG.info("-----------------------------批量结束从按月分配的临时表中删除工单,lBeginRec={},lEndRec={},批量删除了{}",lBeginRec,lEndRec,deleteCount);
				}catch(Exception ex){
					ex.printStackTrace();
					continue;
				}
			}
		}
		long endTime = System.currentTimeMillis();
		float usedTime = (endTime-beginTime)/1000;
		LOG.info("批次："+ activitySeqId+"移历史完成,耗时:" + usedTime + "秒");
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

	//DEPRECATED
	@Override
	// param 中REC_ID代表本次活动的ACTIVITY_SEQ_ID
	// ，ACTIVITY_SEQ_ID代表上次活动的ACTIVITY_SEQ_ID
	public void updateRepetitiveOrder(Integer activitySEQId, String tenantId, String channelid) {
		LOG.info("[OrderCenter] updateRepetitiveOrder,activityseqid=" + activitySEQId + ",channel=" + channelid
				+ " begin.");
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
			if(updateRule == null) {
				LOG.error("[OrderCenter] Update rule null." );
				return;
			}
			// 有进有出
			if (updateRule == 1) {
				outIntoRuleOrder(param, channelid);
			}
			// 覆盖 only for front line channel, because other channels order already sent.
			else if (updateRule == 2 && channelid.equals("5")) {
				//coverRuleOrder(param, channelid);
			}

		}
		LOG.info("[OrderCenter] updateRepetitiveOrder,activityseqid=" + activitySEQId + ",channel=" + channelid
				+ " end.");
	}

	// 根据接触频次过滤工单
	private void updateOrderByTouch(ParamMap param, String touchOGR) {
		
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

	//DEPRECATED
	@Override
	// param 中REC_ID代表本次活动的ACTIVITY_SEQ_ID
	// ，ACTIVITY_SEQ_ID代表上次活动的ACTIVITY_SEQ_ID
	public void updateOrderByTouchLimitDay(Integer activitySEQId, String tenantId, String channelid) {
		LOG.info("[OrderCenter] updateOrderByTouchLimitDay,activityseqid=" + activitySEQId + ",channel=" + channelid
				+ " begin.");
		ParamMap param = new ParamMap();
		param.setREC_ID(activitySEQId);
		param.setTENANT_ID(tenantId);

		String activityId = ordermapper.selectActivityIdByActivitySEQID(param);
		param.setACTIVITY_ID(activityId);
		List<ParamMap> activitySEQ = new ArrayList<ParamMap>();
		activitySEQ = ordermapper.selectActivityForTouch(param);
		
		//only front line channel filter,because other channel order default behavior contact
		if (activitySEQ != null && activitySEQ.size() >= 2 && channelid.equals("5")) {
			param.setACTIVITY_SEQ_ID(activitySEQ.get(1).getREC_ID());
			// 客户经理接触频次： 一个活动下的客户经理接触频次是一样的
			String touchOGR = ordermapper.getTouchLimitDayFromChannel(param);
			if(touchOGR == null ||touchOGR.trim().equals("")) {
				LOG.info("[OrderCenter] updateOrderByTouchLimitDay,activityseqid="
						+ activitySEQId + " touch limit day null .");
				return;
			}
				
			updateOrderByTouch(param, touchOGR);
			// 短信渠道接触频次：一个活动下的短信接触频次是一样的
//			String touchSMS = ordermapper.getSMSTouchLimitDayFromChannel(param);

		}
		LOG.info("[OrderCenter] updateOrderByTouchLimitDay,activityseqid=" + activitySEQId + ",channel=" + channelid
				+ " end.");
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
	//******************************************2017.03.15优化**************************************************//
	//DEPRECATED
	@Override
	public void filterRuleOrder(Integer activitySEQId, String tenantId, String channelid) {
		LOG.info("[OrderCenter] filterRuleOrder,activityseqid=" + activitySEQId + ",channel=" + channelid
				+ " begin.");

		Map<String, Object> activityMap = new HashMap<String, Object>();
		activityMap.put("currentActivitySeq", activitySEQId);
		activityMap.put("TENANT_ID", tenantId);
		activityMap.put("CHANNEL_ID", channelid);
		String activityId = ordermapper.selectActivityByActivitySEQID(activityMap);

		activityMap.put("ACTIVITY_ID", activityId);
		List<PltActivityInfo> activityList = new ArrayList<PltActivityInfo>();
		activityList = ordermapper.selectRuleByActivity(activityMap);

		if (activityList.size() >= 2) {
			Integer updateRule = activityList.get(0).getORDER_UPDATE_RULE();
			activityMap.put("lastActivitySeq", activityList.get(1).getREC_ID());
			if (updateRule == null) {
				LOG.error("[OrderCenter] filterRuleOrder,Update rule null.");
				return;
			}
			// 有进有出
			if (updateRule == 1) {
				//outIntoRuleOrder(activityMap);
			}
			// 覆盖
			// only for front line channel, because other channels order already
			// sent.
			else if (updateRule == 2 && channelid.equals("5")) {
				coverRuleOrder(activityMap);
			}

		}

		LOG.info("[OrderCenter] filterRuleOrder,activityseqid=" + activitySEQId + ",channel=" + channelid
				+ " end.");
	}
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
			//every time deal LIMIT_NUMBER records
			int limitNumber = Integer.parseInt((String)param.get("LIMIT_NUMBER"));
			for ( int i = 0; i < iOrderNum/limitNumber +1 ; ++i ) {
				ordermapper.updateCoveredRuleOrder(param);
				ordermapper.insertCoveredOrder(param);
				if ( param.get("CHANNEL_ID").equals("7")){
					ParamMap paramMap = new ParamMap();
					paramMap.setACTIVITY_SEQ_ID((Integer)param.get("beforeActivitySeq"));
					paramMap.setTENANT_ID((String)param.get("TENANT_ID"));
					paramMap.setCHANN_ID((String) param.get("CHANNEL_ID"));
					moveToSmsHis(paramMap);
				}
				ordermapper.deleteBeforeOrder(param);
			}
			LOG.info("[OrderCenter] coverRuleOrder channel id=" + param.get("CHANNEL_ID") + " end.");
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
			ordermapper.insertOrderInfoHis(order);
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
				int reserveOrderNumber = reserveOrderByChannelId(actSeqId,tenantId,channelId,percent);
				
				//commit reserve order record
				PltActvityRemainInfo remain = new PltActvityRemainInfo();
				remain.setCHANNEL_ID(channelId);
				remain.setACTIVITY_ID(actId);
				remain.setACTIVITY_SEQ_ID(actSeqId);
				remain.setTENANT_ID(tenantId);
				remain.setSAVE_NUMBER(reserveOrderNumber);
				remain.setSAVE_TIMEE(new Date());
				ordermapper.insertRemainInfo(remain);
				
			} else {//activity no need reserve,but need filter reserve order records
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("ACTIVITY_SEQ_ID", actSeqId);
				paramMap.put("TENANT_ID", tenantId);
				paramMap.put("CHANNEL_ID", channelId);
				paramMap.put("RESERVE_SEQ_ID", remainSeqId);
				//filter order in current batch 
				ordermapper.updateOrderInfoByReserve(paramMap);
				ordermapper.insertFilterOrderByReserved(paramMap);
				if (channelId.equals("7")){
					ParamMap param = new ParamMap();
					param.setCHANN_ID(channelId);
					param.setTENANT_ID(tenantId);
					param.setACTIVITY_SEQ_ID(actSeqId);
					moveToSmsHis(param);
				}
				ordermapper.deleteFilterOrderByReserved(paramMap);
				
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
	private int reserveOrderByChannelId(Integer actSeqId, String tenantId, String channelId ,String percent) {
		//get order number for every area_no
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ACTIVITY_SEQ_ID", actSeqId);
		paramMap.put("TENANT_ID", tenantId);
		paramMap.put("CHANNEL_ID", channelId);
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
			
		//insert reserve order to remind table 
		ordermapper.insertRemindOrder(paramMap);
		//delete original orders
		ordermapper.deleteRemindOrder(paramMap);
		//update reserve orders flag to 5
		ordermapper.updateRemindOrder(paramMap);
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
		LOG.info("[OrderCenter] filterOrderByTouch channel id=" + param.getCHANN_ID() +" begin.");
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
			ordermapper.updateOrderByUserList(param);
			// move to his table
			ordermapper.insertOrderToHis(param);
			if (param.getCHANN_ID().equals("7")){
				moveToSmsHis(param);
			}
			// delete order
			ordermapper.deleteOrders(param);
			param.setBEGIN_REC_ID(userIdSet.get(userIdSet.size() - 1).getREC_ID().toString());
			// order records empty
			if (userIdSet.size() < Integer.parseInt(param.getLIMIT_NUMBER()))
				break;

		}
		Date end = new Date();
		LOG.info("[OrderCenter] filterOrderByTouch channel id=" + param.getCHANN_ID() +" end,time cost:" + (end.getTime() - begin.getTime()));

	}
}


