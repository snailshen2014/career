package com.bonc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.server.PathParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.outer.service.ChannelService;
import com.bonc.utils.IContants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.common.base.JsonResult;

/**
 * 工单服务类
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {
	private final static Logger log = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private OrderActivityService OrderActivityServiceIns;
	@Autowired
	private ChannelService channelService;
	
	/**
	 * 查询工单生成的步骤名称
	 * 请求地址：http://ip:port/ordertask-service/order/orderGenerateStepName
	 * 请求类型： POST
	 * 请求参数：
	 *  {
	 *   "activityId":"41614",
	 *   "tenantId":"uni081"
     *  } 
	 * @return
	 */
	@RequestMapping(value = "/orderGenerateStepName" , method = RequestMethod.POST )
	public JsonResult getOrderGenerateStepName(@RequestBody RequestParamMap param) {
		JsonResult jsonResult = new JsonResult();
		try {
			List  resultData = new ArrayList();	
			//拼出类似的Sql: /*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID ='uni076'*/
			String myCatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID =" + "'" +param.getTenantId() +"'"+ "*/";
			param.setMyCatSql(myCatSql);
			resultData = OrderActivityServiceIns.getOrderGenerateStepName(param);
			setJsonResult(jsonResult, resultData);
		} catch (Exception ex) {
			setJsonResultOnException(jsonResult, ex.getMessage());
		}
		return jsonResult;
	}
	
	/**
	 * 同步更新plt_order_tables_using_info中工单表的实际使用数量
	 * 请求地址：http://ip:port/ordertask-service/order/synOrderTableUsingCount/{tenantId}
	 * 请求类型： GET
	 * @param tenantId
	 * @return
	 */
	@RequestMapping(value = "/synOrderTableUsingCount/{tenantId}" , method = RequestMethod.GET)
	public JsonResult synOrderTableUsingCount(@PathVariable("tenantId") String tenantId) {
		JsonResult jsonResult = new JsonResult();
		try {
			OrderActivityServiceIns.synOrderTableUsingCount(tenantId);
			setJsonResult(jsonResult, null);
		} catch (Exception e) {
			setJsonResultOnException(jsonResult, e.getMessage());
		}
		return jsonResult;
	}
	
	/**
	 * 没有发生异常时设置JsonResult相关的属性
	 * @param result JsonResult实例
	 * @param data   JsonResult封装的业务数据
	 */
	private void setJsonResult(JsonResult result,Object data) {
		result.setCode("0");
		result.setMessage("SUCCESS");
		result.setData(data);
	}
	
	/**
	 * 发生异常时设置JsonResult相关信息
	 * @param result JsonResult实例
	 * @param errorMessage 发生异常时的错误提示信息
	 */
	private void setJsonResultOnException(JsonResult result,String errorMessage) {
		result.setCode("1");
		result.setMessage(errorMessage);
	}


	/**
	 * 工单创建初始化 渠道工单映射表
	 * 请求地址：http://ip:port/ordertask-service/order/createChannelOrderMapping
	 * @param request
	 * 样例
		 [{"channelId":"5","xCloudColumn":"MB_ARPU","columnDesc":"用户价值","tenantId":"uni076"},
		 {"channelId":"5","xCloudColumn":"MB_OWE_FEE","columnDesc":"所属费用","tenantId":"uni076"},
		 {"channelId":"7","xCloudColumn":"PB0002","columnDesc":"月使用流量","tenantId":"uni076"}]
	 *
	 * 请求正确响应：
	 *   {
	 *     "locale": null,
	 *     "message": "SUCCESS",
	 *     "code": "000000",
	 *     "data": "null"
	 *   }
	 *  错误响应
	 *   {
	 *     "locale": null,
	 *     "message": "具体错误信息",
	 *     "code": "999999", 或者 888888
	 *     "data": "null"
	 *   }
	 */
	@RequestMapping(value = "/createChannelOrderMapping" , method = RequestMethod.POST)
	private JsonResult createChannelOrderMapping(@RequestBody String request){
		JsonResult result = new JsonResult();
		try {
			List<Map<String,String>> channelColumnList = JSON.parseObject(request, List.class);
			result = channelService.createChannelOrderMapping(channelColumnList);
		}catch (JSONException je){
			log.error(je.toString());
			result.setCode(IContants.BUSI_ERROR_CODE);
			result.setMessage("传入参数格式有误 ，参考JsonArray格式");
		}
		catch (IndexOutOfBoundsException ie){
			log.error(ie.toString());
			result.setCode(IContants.BUSI_ERROR_CODE);
			result.setMessage("工单字段已经被分配完");
		}catch (Exception e){
			log.error(e.toString());
			result.setCode(IContants.SYSTEM_ERROR_CODE);
			result.setMessage(e.toString());
		}
		return result;
	}

	/**
	 * 修改渠道渠道工单表映射
	 * 经修改后统一由上面接口代替
	 *
	 * 说明：增删改 统一接口，如果传入渠道的xCloudColumn没有则增加，如果已有则更新，传值是空则删除
	 * 		如果传入未初始化渠道也会插入！！！
	 * 	请求地址：http://ip:port/ordertask-service/order/modifChannelOrderMapping
	 * @param request
	 	 * 样例1
	 {"channelId":"5","xCloudColumnOld":"MB_ARPU","xCloudColumnNew":"MB_ARPU","columnDesc":"用户价值修改"}
	  * 样例2
	 {"channelId":"5","xCloudColumnOld":"MB_ARPU1","xCloudColumnNew":"MB_ARPU1","columnDesc":"用户价值添加"}
	  * 样例3
	  {"channelId":"5","xCloudColumnOld":"MB_ARPU1","xCloudColumnNew":"","columnDesc":"用户价值删除"}

	 请求正确响应：
	   {
	     "locale": null,
	 *     "message": "插入成功", 或者 停用成功 或者修改成功
	 *     "code": "000000",
	 *     "data": "null"
	 *   }
	 *  错误响应
	 *   {
	 *     "locale": null,
	 *     "message": "具体错误信息",
	 *     "code": "999999", 或者 888888
	 *     "data": "null"
	 *   }
	 */
	@RequestMapping(value = "/modifChannelOrderMapping" , method = RequestMethod.POST)
	private JsonResult modifChannelOrderMapping(@RequestBody String request){
		JsonResult result = new JsonResult();
		try {
			List<Map<String,String>>  modifMap = JSON.parseObject(request, List.class);
			result = channelService.modifChannelOrderMapping(modifMap);
		}catch (JSONException je){
			log.error(je.toString());
			result.setCode(IContants.BUSI_ERROR_CODE);
			result.setMessage("传入参数格式有误 ，参考Json格式");
		} catch (IndexOutOfBoundsException ie){
			log.error(ie.toString());
			result.setCode(IContants.BUSI_ERROR_CODE);
			result.setMessage("工单字段已经被分配完");
		}catch (Exception e){
			log.error(e.toString());
			result.setCode(IContants.SYSTEM_ERROR_CODE);
			result.setMessage(e.toString());
		}
		return result;

	}


	public static void main(String[] args) {
		String request = " [{\"channelId\":\"5\",\"xCloudColumn\":\"MB_ARPU\",\"columnDesc\":\"用户价值\"}," +
				" {\"channelId\":\"5\",\"xCloudColumn\":\"MB_OWE_FEE\",\"columnDesc\":\"所属费用\"}," +
				" {\"channelId\":\"7\",\"xCloudColumn\":\"PB0002\",\"columnDesc\":\"月使用流量\"}]";
		try{
			List<Map> channelColumnList = JSON.parseObject(request, List.class);
			for (Map c : channelColumnList){
				System.out.println(c);
			}
		}catch (JSONException je){
			log.error(je.toString());
		}
	}
}
