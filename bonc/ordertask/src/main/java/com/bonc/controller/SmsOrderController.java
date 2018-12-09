package com.bonc.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonc.busi.outer.service.SmsOrderService;
import com.bonc.busi.task.bo.SmsOrderToHisTableRequest;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.task.service.BaseTaskSrv;
import com.bonc.busi.task.service.OrderCheckService;
import com.bonc.common.base.JsonResult;

/**
 * 短信工单Controller
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/smsorder")
public class SmsOrderController {
	
	@Autowired
	SmsOrderService service;
	
	@Autowired
	BaseTaskSrv task;
	
	@Autowired
	OrderCheckService orderCheckService;
	
	@Autowired
	BaseMapper BaseMapperIns;
	
	/**
	 * 河南短信发送移历史改造
	 * 由于工单量激增,短信的每一张按月的历史表需要根据手机尾号拆分成10张表，接收到请求后，单独启动一个线程完成同步历史工单到行云以及移动工单的操作
	 * http://IP:PORT/ordertask/smsorder/orderToHistory
	 * POST
	 * Body接受参数
	 * 请求参数：
	 * {
	 *	    "activityId":"10000",
	 *	    "activitySeqId":1000,
	 *	    "tenantId":"uni076",
	 *	    "channelId":"7",
	 *	    "hisTableName":"PLT_ORDER_INFO_SMS_HIS_11"
	 *	}
	 * 响应结果：
	 * {
	 *	  "locale": null,
	 *	  "message": "success",
	 *	  "code": "0",
	 *	  "data": null
	 *	}
	 * @return
	 */
	@RequestMapping(value = "/orderToHistory" , method = RequestMethod.POST)
	@ResponseBody
	public JsonResult moveSmsOrderToHistory(@RequestBody SmsOrderToHisTableRequest request){
		JsonResult result = new JsonResult();
		String activityId = request.getActivityId();
		int activitySeqId = request.getActivitySeqId();
		String tenantId = request.getTenantId();
		String channelId = request.getChannelId();
		String hisTableName = request.getHisTableName();
		if(StringUtils.isNotBlank(activityId) && StringUtils.isNotBlank(String.valueOf(activitySeqId)) 
			&& StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(channelId)&& StringUtils.isNotBlank(hisTableName)){
			//启动独立的线程完成移动工单的操作，并在plt_order_move_record表里做记录
			service.moveSmsOrderToHis(activityId, activitySeqId, tenantId, channelId, hisTableName);
		}else{
			result.setCode("1");
			result.setMessage("请求参数不完整,请检查");
			return result;
		}
		result.setCode("0");
		result.setMessage("success");
		return result;
	}
		
	/**
	 * 工单数据同步到行云上的测试方法
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public void uploadSmsOrderHis(){
	 try{
		Connection connection = null;
		Statement statement = null;
		String driverName = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
		String url = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.URL");
		String username = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.USER");
		String password = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
		String order_xcloud_suffix = BaseMapperIns.getValueFromSysCommCfg("ORDER_XCLOUD_SUFFIX.uni076");
		if(StringUtils.isBlank(order_xcloud_suffix)){
			System.err.println("======== 缺少ORDER_XCLOUD_SUFFIX配置,请配置");
			return;
		}
		Class.forName(driverName);
		connection = DriverManager.getConnection(url , 
				username, 
				password);
		System.out.println("======================"+connection!=null);
		
		long begin = System.currentTimeMillis();
		String sql = "insert into XLD_ORDER_INFO_SMS_HIS "
				+ "{Select REC_ID,CONTACT_DATE,PHONE_NUMBER,ACTIVITY_SEQ_ID,TENANT_ID,CHANNEL_ID,ORDER_STATUS,CONTACT_CODE FROM PLT_ORDER_INFO_SMS_HIS_11 WHERE TENANT_ID='uni076'}@ORDER_XCLOUD_SUFFIX";
		String replaceOrderXcloudSuffix = sql.replaceFirst("ORDER_XCLOUD_SUFFIX", order_xcloud_suffix);
		statement = connection.createStatement();
		statement.execute(replaceOrderXcloudSuffix);
		System.out.println("====================upload sucess");
		System.out.println("========== 耗时："+(System.currentTimeMillis()-begin)/1000);
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	}
	
	/**
	 * 测试从行云上查询数据
	 * @return
	 */
	@RequestMapping("/queryHisSmsOrder")
	@ResponseBody
	public Object queryHisSmsOrderFromXclound(){
		 try{
				Connection connection = null;
				Statement statement =null;
				ResultSet resultSet = null;
				String driverName = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.DRIVER");
				String url = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.URL");
				String username = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.USER");
				String password = BaseMapperIns.getValueFromSysCommCfg("DS.XCLOUD.PASSWORD");
				Class.forName(driverName);
				connection = DriverManager.getConnection(url , 
						username, 
						password);
				System.out.println("======================"+connection!=null);
				long begin = System.currentTimeMillis();
				String sql = "select count(1) as total from XLD_ORDER_INFO_SMS_HIS";
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if(resultSet != null){
					resultSet.next();
					System.out.println("====查询的数量: "+ resultSet.getInt("total"));
				}
				System.out.println("====================query sucess");
				System.out.println("========== 耗时："+(System.currentTimeMillis()-begin)/1000);
			   }catch(Exception ex){
				   ex.printStackTrace();
			   }
		 return 1;
	}
	
	/**
	 * 3号环境上测试事后成功检查,谨慎调用
	 */
	@RequestMapping("/ordercheck2")
	public void orderCheck(){
		task.orderCheckV2();
	}

}
