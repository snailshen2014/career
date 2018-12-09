package com.bonc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.bo.ActivityChannelExecute;
import com.bonc.busi.outer.bo.ActivityProcessLog;
import com.bonc.busi.outer.bo.OrderTableUsingInfo;
import com.bonc.busi.outer.bo.OrderTablesAssignRecord4S;
import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.outer.service.StatusService;



/**
 * 
 * <p>Title: BONC - 工单中心 </p>
 * 
 * <p>Description: 活动控件类（用于活动状态变时接受通知） </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author zengdingyong
 * @version 1.0.0
 */

@RestController
@RequestMapping("/activity")
public class ActivityController {
	@Autowired
	private OrderActivityService OrderActivityServiceIns;
	
	@Autowired
	private StatusService statusService;
	
	@Autowired
	PltActivityInfoDao		PltActivityInfoDaoIns;
	
	private final static Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	
	/**
	 * 根据活动的Id、租户Id获取活动的有效批次
	 * 请求地址  http://ip:port/ordertask-service/activity/getActivitySEQIds/{activityId}/{tenantId}
	 * 请求类型:GET
	 * 响应结果:
	 *      {
     *         "locale": null,
     *         "message": null,     //返回的信息提示
     *         "code": "0",        //返回的代码，非0表示出错
     *         "data": [           //返回的数据项 
     *             22222,
     *             22223
     *            ]
     *     }
	 * @return 活动的有效批次
	 */
	@RequestMapping(value="/getActivitySEQIds/{activityId}/{tenantId}", method=RequestMethod.GET)
	public JsonResult getActivitySEQIdsById(@PathVariable("activityId") String activityId,@PathVariable("tenantId") String tenantId) {
		log.info("根据活动Id获取活动的有效批次服务的请求参数：" + activityId + "   " + tenantId);
		JsonResult	JsonResultIns = new JsonResult();
		try{
		   List<Integer> activitySEQIds = OrderActivityServiceIns.getActivitySEQIdsById(activityId,tenantId);
		   setJsonResult(JsonResultIns, activitySEQIds);
		}catch(Exception ex) {
		   setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 根据活的的Id 租户Id获取最新的有效批次
	 * 请求地址  http://ip:port/ordertask-service/activity/getLatestActivitySEQId/{activityId}/{tenantId}
	 * 请求类型:GET
	 * 响应结果:
	 *      {
     *         "locale": null,
     *         "message": null,     //返回的信息提示
     *         "code": "0",        //返回的代码，非0表示出错
     *         "data":22223          //返回的数据项             
     *            
     *     }
	 * @param activityId 活动的Id
	 * @return 活动最新的有效批次
	 */
	@RequestMapping(value="/getLatestActivitySEQId/{activityId}/{tenantId}" , method=RequestMethod.GET)
	public JsonResult getLatestActivitySEQIdById(@PathVariable("activityId") String activityId,@PathVariable("tenantId") String tenantId) {
		JsonResult JsonResultIns = new JsonResult();
		try {
			Integer activitySEQId = OrderActivityServiceIns.getLatestActivitySEQIdById(activityId,tenantId);
			setJsonResult(JsonResultIns, activitySEQId);
		} catch (Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 根据活动Id、租户Id获取所有批次是否有效
	 * 请求地址：http://ip:port/ordertask-service/activity/
	 * 请求类型：GET
	 * 响应结果：
	 *  {
     *   "locale": null,
     *   "message": "SUCCESS",
     *   "code": "0",
     *   "data": {
     *     "invalidData": null,    //无效批次集合
     *     "validData": [          //有效批次集合
     *         800011,
     *         920011,
     *         990011
     *                  ]
     *           }
     * }
	 * @return
	 */
	@RequestMapping("/getAllSeqValidInfo/{activityId}/{tenantId}")
	public JsonResult getAllSeqValidInfoByActivityIdAndTenantId(@PathVariable("activityId") String activityId,
			@PathVariable("tenantId") String tenantId) {
		JsonResult result = new JsonResult();
		try {
			List<Integer> validData = OrderActivityServiceIns.getActivitySEQIdsById(activityId, tenantId);
			List<Integer> invalidData = OrderActivityServiceIns.getActivityInvalidSEQIdsById(activityId, tenantId);
			Map<String,List<Integer>> busiData = new HashMap<String,List<Integer>>();
			busiData.put("validData", validData);
			busiData.put("invalidData", invalidData);
			setJsonResult(result, busiData);
		} catch (Exception ex) {
            setJsonResultOnException(result, ex.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 根据活动id,租户id,活动批次id查询工单的各种类型的数量:渠道原始工单数量/有进有出过滤工单数量/覆盖规则过滤工单数量/黑名单过滤数量/留存过滤数量/接触过滤数量
	 * 请求地址  http://ip:port/ordertask-service/activity/getOrderCount
	 * 请求类型:POST
	 * 请求参数：
	 *  {
     *   "activityId":"111111",   //活动Id
     *   "tenantId":"uni076",     //租户Id
     *   "activitySeqId":"12",      //活动批次
     *   "channelId":"7"          //渠道Id  请求参数包含这个值,返回的结果只是这个渠道下的工单数， 否则返回的是所有渠道下的工单数
     *  }
	 * 响应结果:
     * {
     *   "locale": null,
     *   "message": "SUCCESS",
     *   "data": [
     *    {
     *      "TOUCH_FILTER_AMOUNT": 11,
     *      "RESERVE_FILTER_AMOUNT": 13,
     *      "CHANNEL_ID": "12",
     *      "ORI_AMOUNT": 100,
     *      "COVERAGE_FILTER_AMOUNT": 10,
     *      "BLACK_FILTER_AMOUNT": 12,
     *      "ACTIVITY_ID": "111111",
     *      "INOUT_FILTER_AMOUNT": 20,
     *      "SUCCESS_FILTER_AMOUNT": 20,
     *      "REPEAT_FILTER_AMOUNT" : 20,
     *      "TENANT_ID": "uni076",
     *      "ACTIVITY_SEQ_ID": 12
     *    },
     *    {
     *     "TOUCH_FILTER_AMOUNT": 12,
     *     "RESERVE_FILTER_AMOUNT": 33,
     *     "CHANNEL_ID": "13",
     *     "ORI_AMOUNT": 100,
     *     "COVERAGE_FILTER_AMOUNT": 12,
     *     "BLACK_FILTER_AMOUNT": 22,
     *     "ACTIVITY_ID": "111111",
     *     "INOUT_FILTER_AMOUNT": 30,
     *     "SUCCESS_FILTER_AMOUNT": 20,
     *     "REPEAT_FILTER_AMOUNT" : 20,
     *     "TENANT_ID": "uni076",
     *     "ACTIVITY_SEQ_ID": 12
     *    }
     *  ],
     *  "code": "0"
     * }
	 * @return 工单的原始数量及各种过滤规则过滤的数量
	 */
	@RequestMapping(value = "/getOrderCount" , method = RequestMethod.POST)
	public JsonResult getOrderCount(@RequestBody ActivityProcessLog processLog) {
		//如果查询时有渠道Id这个条件,拼上
		if(processLog.getChannelId() !=null && !processLog.getChannelId().equals("")) {
			processLog.setWhereSql("  AND CHANNEL_ID = " + "'" +processLog.getChannelId() + "'");
		}
		JsonResult JsonResultIns = new JsonResult();
		try {
			List<Map<String, Integer>> orderCounts = OrderActivityServiceIns.getOrderCount(processLog);
			setJsonResult(JsonResultIns, orderCounts);
		} catch (Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
    /**
     * 获取工单的表名
     * @param orderTable 封装请求的参数： 活动Id、批次Id、渠道Id、租户Id以及busi_type(0/1/2/3)
     * 请求地址：请求地址  http://ip:port/ordertask-service/activity/getOrderTableName
     * 请求类型:POST
     * 请求参数：
     *  {
     *    "activityId":"1111",      //活动Id，可不传，通过活动批次及租户Id可以查询出得到
     *    "activitySeqId":"33333",  
     *    "channelId":"7",
     *    "tenantId":"uni076",
     *    "busiType":"0"         //   0:工单表   1：过滤表  2：留存过滤表  3：场景营销表   
     *  }
     * 请求响应：
     *   {
     *     "locale": null,
     *     "message": "SUCCESS",
     *     "code": "0",
     *     "data": "PLT_ORDER_INFO_45"   //表名
     *   }
     * @return
     */
	@RequestMapping(value = "/getOrderTableName" , method = RequestMethod.POST)
	public JsonResult getOrderTableName(@RequestBody OrderTablesAssignRecord4S orderTable) {
		JsonResult JsonResultIns = new JsonResult();
		String tableName = null;
		String activityId = orderTable.getActivityId();
		String channelId = orderTable.getChannelId();
		String tenantId = orderTable.getTenantId();
		boolean isActivityInValid = false;
		try {
			if(orderTable.getActivityId() == null) {
				activityId = PltActivityInfoDaoIns.getActivityIdBySeqIdAndTenantId(orderTable.getActivitySeqId(), orderTable.getTenantId());
			    orderTable.setActivityId(activityId);
			}
			tableName = OrderActivityServiceIns.getOrderTableName(orderTable);
			//判断活动是否失效，如果活动失效，则返回的table名称带_HIS后缀
			if(activityId != null && orderTable.getActivitySeqId() != null) {
				isActivityInValid = OrderActivityServiceIns.isActivityInvalid(activityId, orderTable.getActivitySeqId(),tenantId);
			}
			// 如果活动失效,返回_HIS表
			if (tableName != null) { // tableName!=null 表明有指定的工单
				if (isActivityInValid && !channelId.equals("7")) { // 短信渠道需要特殊处理：需要判断短信是否下发完毕
					tableName = tableName + "_HIS";
				}
				// 查询短信渠道是否指定完毕
				ActivityChannelExecute channelExecute = new ActivityChannelExecute();
				channelExecute.setActivitySeqId(orderTable.getActivitySeqId());
				channelExecute.setTenantId(orderTable.getTenantId());
				channelExecute.setChannelId("7");
				int count = OrderActivityServiceIns.getChannelFinishedCount(channelExecute);
				if (isActivityInValid && channelId.equals("7") && count > 0) {
					tableName = tableName + "_HIS";
				}
			}
			setJsonResult(JsonResultIns, tableName);
		} catch (Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	private void getChannelFinishedCount(ActivityChannelExecute channelExecute) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 通过活动Id 、 活动批次、租户Id获取生成工单的渠道编号列表
	 * 请求地址：  http://ip:port/ordertask-service/activity/getOrderChannelListByActivityIdAndSeqId/{activityId}/{activitySeqId}/{tenantId}
	 * 请求类型:GET
	 * 请求结果：
	 * {
     *   "locale": null,
     *   "message": "SUCCESS",
     *   "code": "0",
     *   "data": [
     *   "12",
     *   "13"
     *  ]
     * }
	 * @return
	 */
	@RequestMapping(value = "/getOrderChannelListByActivityIdAndSeqId/{activityId}/{activitySeqId}/{tenantId}" , method = RequestMethod.GET)
	public JsonResult getOrderChannelList(@PathVariable("activityId") String activityId, @PathVariable("activitySeqId") int activitySeqId,@PathVariable("tenantId") String tenantId) {
		JsonResult JsonResultIns = new JsonResult();
		List<String> channelIdList = new ArrayList<String>();
		try {
			channelIdList = OrderActivityServiceIns.getOrderChannelListByActivityIdAndSeqId(activityId, activitySeqId,tenantId);
			setJsonResult(JsonResultIns, channelIdList);
		} catch (Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 根据手机号、渠道查询工单表名
	 * 请求地址：  http://ip:port/ordertask-service/activity/getOrderTableNamesByPhoneAndChannelId
	 * 请求类型  POST
	 * 请求参数：
	 * {
     *  "phoneNumber":"13030300311",  //手机号
     *  "tenantId":"uni076",          //租户Id
     *  "channelId":"7"               //渠道
     * }
	 * 响应结果：
	 * {
     *  "locale": null,
     *  "message": "SUCCESS",
     *  "data": [                   //工单表名集合
     *    "PLT_ORDER_INFO_13"
     *    ],
     *  "code": "0"
     * }
	 */
	@RequestMapping(value = "/getOrderTableNamesByPhoneAndChannelId" , method = RequestMethod.POST)
	public JsonResult getOrderTableNamesByPhoneAndChannelId(@RequestBody RequestParamMap paramMap) {
		JsonResult JsonResultIns = new JsonResult();
		try {
			//先从索引表里查找，如果查找的结果不为空，则直接返回，否则再全局查找，然后把结果返回
			List<String> tableNameList = OrderActivityServiceIns.getOrderTableNameByPhoneNumber(paramMap);
			if(tableNameList!=null && tableNameList.size()!=0){
				setJsonResult(JsonResultIns, tableNameList);
				return JsonResultIns;
			} else {
				tableNameList = OrderActivityServiceIns.getOrderTableNamesByPhoneAndChannelId(paramMap);
				setJsonResult(JsonResultIns, tableNameList);
				return JsonResultIns;
			}
			
		} catch (Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 根据活动和渠道获取表名列表 
	 * 请求地址： http://ip:port/ordertask-service/activity/getOrderTableListByActivityAndChannel
	 * 请求类型： POST
	 * 请求参数：
	 * {
     *   "activityId":"41126",  //活动Id， 必传参数
     *   "channelId":"7",       //渠道Id，必传参数
     *   "tenantId":"uni076"，      //租户Id，必穿参数
     *   "busiType":"0"        //业务类型(0/1/2/3)   可选参数，  默认值就是0       
     * }
	 * 响应结果：
     * {
     *   "locale": null,
     *   "message": "SUCCESS",
     *   "code": "0",
     *   "data": [             //表列表
     *     "PLT_ORDER_INFO_13"
     *    ]
     * }
	 * @return
	 */
	@RequestMapping(value = "/getOrderTableListByActivityAndChannel" , method = RequestMethod.POST)
	public JsonResult getOrderTableListByActivityAndChannel(@RequestBody RequestParamMap paramMap) {
		JsonResult JsonResultIns = new JsonResult();
		//busiType的默认值是0
		paramMap.setWhereSql("  AND BUSI_TYPE = " + paramMap.getBusiType());
		try{
			List<String> tableList = OrderActivityServiceIns.getOrderTableListByActivityAndChannel(paramMap);
			setJsonResult(JsonResultIns, tableList);
		}catch(Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns; 
	}
	
	/**
	 * 根据渠道    活动Id(可选参数) 获取表列表
	 * 请求地址：http://ip:port/ordertask-service/activity/getOrderTableListByChannel
	 * 请求类型   POST
	 * 请求参数：
	 * {
     *   "activityId":"41126",  //活动Id， 可选参数，  如果传入该参数，查询条件将会拼上该条件
     *   "channelId":"7",       //渠道Id，必传参数
     *   "tenantId":"uni076"，      //租户Id，必穿参数
     *   "busiType":"0"        //业务类型(0/1/2/3)   可选参数，  默认值就是0       
     * }
     * 响应结果：
     * {
     *   "locale": null,
     *   "message": "SUCCESS",
     *   "code": "0",
     *   "data": [             //表列表
     *     "PLT_ORDER_INFO_13"
     *    ]
     * }
	 * @return
	 */
	@RequestMapping(value = "/getOrderTableListByChannel" , method = RequestMethod.POST)
	public JsonResult getOrderTableListByChannel(@RequestBody RequestParamMap paramMap) {
		JsonResult JsonResultIns = new JsonResult();
		StringBuilder whereSql = new StringBuilder();
		whereSql.append("  AND BUSI_TYPE = " + paramMap.getBusiType());
		//如果传入了活动Id，则拼接上条件
		if(paramMap.getActivityId() != null && !paramMap.getActivityId().equals("")) {
		   whereSql.append("  AND ACTIVITY_ID = " + "'" + paramMap.getActivityId() + "'");
		}
		
		try{
			paramMap.setWhereSql(whereSql.toString());
			List<String> tableList = OrderActivityServiceIns.getOrderTableListByChannel(paramMap);
			setJsonResult(JsonResultIns, tableList);
		}catch(Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 查询PLT_ORDER_TABLE_COLUMN_MAP_INFO表生成列映射map(宽表字段到工单表字段的映射)
	 * 请求地址： http://ip:port/ordertask-service/activity/getOrderTableColumnMap
	 * 请求类型  POST
	 * 请求参数：
	 * {
     *  "tenantId":"uni076"
     * } 
     * 响应结果：
     * 
     * {
     *  "locale": null,
     *  "message": "SUCCESS",
     *  "data": {
     *      "DEVICE_NUMBER": "PHONE_NUMBER",
     *      "N_03_M": "USERLABEL_RESERVE42",
     *      "ONLINE_DUR_LVL": "USERLABEL_RESERVE7",
	 *	    "KD_MOBILE_NUMBER1": "USERLABEL_RESERVE34",
	 *	    "KD_CHANNEL_TYPE": "USERLABEL_RESERVE46",
	 *	    "KD_CHR_1": "USERLABEL_RESERVE47",
	 *	    "BA_N_11": "USERLABEL_RESERVE4",
	 *	    "KD_XQ_DATE": "USERLABEL_RESERVE25",
	 *	    "KD_DQ_MONTH": "USERLABEL_RESERVE23"
	 *    }，
	 *  "code": "0" 
	 * }  
	 * @return
	 */
	@RequestMapping(value = "/getOrderTableColumnMap", method = RequestMethod.POST)
	public JsonResult getOrderTableColumnMap(@RequestBody RequestParamMap param) {
		JsonResult JsonResultIns = new JsonResult();
		try{
			Map<String,String> columnMap = OrderActivityServiceIns.getOrderTableColumnMap(param);
			setJsonResult(JsonResultIns, columnMap);
		}catch(Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**整个服务已整合到getOrderTableNamesByPhoneAndChannelId服务中
	 * 根据手机号码查询工单表名,查询的是PLT_ORDER_PHONE_INDEX_X表(X代表0 1 2... 9)
	 * 请求地址： http://ip:port/ordertask-service/activity/getOrderTableNameByPhoneNumber
	 * 请求类型  POST
	 * 请求参数：
	 *{
	 *   "phoneNumber":"15539392514",
	 *   "channelId":"81"
	 *   "tenantId":"uni076"
     *} 
     *响应结果：
     *{
	 * 	  "locale": null,
     *	  "message": "SUCCESS",
	 *	  "data": [
		    "PLT_ORDER_INFO_10",
		    "PLT_ORDER_INFO_11",
		    "PLT_ORDER_INFO_9"
		  ],
	 *	  "code": "0"
     *}
	 * @return
	 */
	@RequestMapping(value = "/getOrderTableNameByPhoneNumber", method = RequestMethod.POST)
	public JsonResult getOrderTableNameByPhoneNumber(@RequestBody RequestParamMap param) {
		JsonResult JsonResultIns = new JsonResult();
		
		try {
			List<String> orderTableName = OrderActivityServiceIns.getOrderTableNameByPhoneNumber(param);
			setJsonResult(JsonResultIns, orderTableName);
		} catch (Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}

		return JsonResultIns;
	}
	
	/**
	 * 根据活动、批次查询PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中可执行的渠道列表
	 * 请求地址： http://ip:port/ordertask-service/activity/getExecuteableChannelList
	 * 请求类型  POST
	 * 请求参数：
	 *{
	 *   "tenantId":"uni076",
	 *   "activityId":"10000",
	 *   "activitySeqId":"1000"
     *} 
	 * 响应结果：
	 *{
	 * "locale": null,
	 * "code": "0",
	 * "message": "SUCCESS",
	 * "data": [
	 *   "7",
	 *   "5"
	 * ]
     *} 
	 * @return
	 */
	@RequestMapping(value = "/getExecuteableChannelList",method = RequestMethod.POST)
	public JsonResult getExecuteableChannelList(@RequestBody RequestParamMap param) {
		 JsonResult JsonResultIns = new JsonResult();
		 try{
			 List<String> channelList = OrderActivityServiceIns.getExecuteableChannelList(param);
			 setJsonResult(JsonResultIns, channelList);
		 }catch(Exception ex) {
			 setJsonResultOnException(JsonResultIns, ex.getMessage());
		 }
		 return JsonResultIns;
	}
	
	/**
	 * 根据渠道查询PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中可执行的批次
	 * 请求地址  http://ip:port/ordertask-service/activity/getExecuteableActivitySeqs
	 * 请求类型  POST
	 * 请求参数：
	 * {
	 *   "tenantId":"uni076",
	 *   "channelId":"7",
	 *   "touchCode":"1"        1是初始状态    
     * } 
     * 响应结果：
     * {
	 *	  "locale": null,
	 *	  "message": "SUCCESS",
	 *	  "code": "0",
	 *	  "data": [
     *        870012,
     *        870013,
     *        870014
	 *	  ]
	 * } 
	 * @return
	 */
	@RequestMapping(value = "/getExecuteableActivitySeqs" , method = RequestMethod.POST)
	public JsonResult getExecuteableActivitySeqs(@RequestBody ActivityChannelExecute executeInterface) {
		JsonResult JsonResultIns = new JsonResult();
		try{
			List<Integer> acitivitySeqIdList = OrderActivityServiceIns.getExecuteableActivityInfo(executeInterface);
			setJsonResult(JsonResultIns, acitivitySeqIdList);
		}catch(Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 更新PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE中记录的状态
	 * 请求地址  http://ip:port/ordertask-service/activity/updateExecuteInterfaceStatus
	 * 请求类型  POST
	 * 请求参数：
	 * {
	 *   "tenantId":"uni076",         //租户
	 *   "activitySeqId":"1000005",  //批次
	 *   "channelId":"7",
	 *   "touchCode":"2"            //2:成功     3：失败    
     * } 
     * 响应结果：
     * {
	 *	  "locale": null,
	 *	  "message": "SUCCESS",
	 *	  "code": "0",            //更新成功
	 *	  "data": null
	 *	} 
	 * @return
	 */
	@RequestMapping(value = "/updateExecuteInterfaceStatus" , method = RequestMethod.POST)
	public JsonResult updateExecuteInterfaceStatus(@RequestBody ActivityChannelExecute executeInterface) {
		JsonResult JsonResultIns = new JsonResult();
		try {
			OrderActivityServiceIns.updateExecuteInterfaceStatus(executeInterface);
			setJsonResult(JsonResultIns, null);
		} catch (Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 工单表删除数据时更新PLT_ORDER_TABLES_USING_INFO表的的使用信息
	 * 请求参数：
	 * {
	 *	    "tenantId":"uni076",
	 *	    "tableName":"PLT_ORDER_INFO_40",
	 *	    "delCount":"5000",
	 *	    "busiType":"0"
     * }
     * 响应结果：
     * {
	 *	  "locale": null,
	 *	  "message": "SUCCESS",
	 *	  "code": "0",            //更新成功
	 *	  "data": null
	 *	}     
	 */
	@RequestMapping(value = "/updateOrderTableUsingInfo" , method = RequestMethod.POST)
	public JsonResult updateOrderTableUsingInfo(@RequestBody OrderTableUsingInfo usingInfo) {
		JsonResult JsonResultIns = new JsonResult();
		try{
			OrderActivityServiceIns.updateOrderTableUsingInfo(usingInfo);
			setJsonResult(JsonResultIns, null);
		}catch(Exception ex) {
			setJsonResultOnException(JsonResultIns, ex.getMessage());
		}
		return JsonResultIns;
	}
	
	/**
	 * 更改指定活动的状态
	 * 请求地址  http://ip:port/ordertask-service/activity/setstatus
	 * 请求类型 POST
	 * 请求参数：
	 * {
	 *   "tenant_id":"uni076",
	 *   "activityId":"10000",
	 *   "activityStatus":"2"     //2: 停止   
     * } 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/setstatus", method=RequestMethod.POST)
    public JsonResult setActivityStatus(@RequestBody ActivityStatus request){
	//public JsonResult setActivityStatus( ActivityStatus request){
		//JsonResult	JsonResultIns = new JsonResult();
		// --- 参数检查  ---
		//JsonResultIns.setCode("0");
		//JsonResultIns.setMessage("sucess");
		//log.warn("request"+request);
		log.warn("id="+request.getActivityId());
		log.warn("status:"+request.getActivityStatus());
		// --- 调用service执行对应的功能 ---
		return OrderActivityServiceIns.setActivityStatus(request);
	}
	
	/**
	 * 查询活动的状态
	 * 请求地址：http://ip:port/ordertask-service/activity/ordergenstatus
	 * 请求参数：
	 * {
	 *    "activityId":"41619",
	 *	  "tenantId":"uni076", 
     * } 
     * 响应结果：
     * {
	 *	  "UPDATE_TIME": "2017-06-25 00:46:00",
	 *	  "STATUS": 1                            -1：未开始      0：生成中     1：生成成功
     * }
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/ordergenstatus",method=RequestMethod.POST)
	public Object ordergenstatus(@RequestBody RequestParamMap req){
		log.info("活动状态查询接口请求参数——>>>>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		fields.add("activityId");
		try {
			resp = statusService.orderGenStatus(req);
		}catch (BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("code", e.getMessage());
		}
		return resp;
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
	 * 更新活动的成功标准（成功标准表和产品表）
	 * 请求地址：http://ip:port/ordertask-service/activity/updateActivitySuccess
	 * 请求参数：
	 * {
	 *    "activityId":"41619",
	 *	  "tenantId":"uni076"
	 * }
	 * 响应结果：
	 * {
	 *	  "code": "000000",
	 *	  "msg": 成功
	 * }
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/updateActivitySuccess",method=RequestMethod.POST)
	public Object updateActivitySuccess(@RequestBody RequestParamMap req){
		log.info("更新活动的成功标准——>>>>{}",JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		try {
			resp = OrderActivityServiceIns.updateActivitySuccess(req);
		}catch (BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("code", e.getMessage());
		}
		return resp;
	}


}
