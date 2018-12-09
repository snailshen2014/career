package com.bonc.controller;

import java.util.Map;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.outer.bo.FiledMapRequest;
import com.bonc.busi.outer.service.FieldMappingService;
import com.bonc.common.base.JsonResult;

/**
 * 策略细分字段或用户标签字段与工单字段映射的服务控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/field")
public class FieldMappingController {
	
	@Autowired
	FieldMappingService mappingService;

	/**
	 * 查询PLT_ORDER_TABLE_COLUMN_MAP_INFO表中可用的列，为请求中的字段建立一一对应关系，并更新
	 * PLT_ORDER_TABLE_COLUMN_MAP_INFO使用的列
	 * @param request  请求参数
	 * 请求参数：
	 * {
	 *   "tenantId":"uni076",
	 *   "activityId":"10000",
	 *   "activitySeqId":2000,
	 *   "channelId":"5",
	 *   "type":"0",
	 *   "fields":["test1","test2"]
     * }
     * 返回结果：
     *  {
	 *	  "locale": null,
	 *	  "message": "SUCCESS",
	 *	  "code": "0",   //0表示正确
	 *	  "data": {      //返回生成的映射关系
	 *	    "test1": "BUSINESS_RESERVE50",
	 *	    "test2": "BUSINESS_RESERVE49"
	 *	  }
     *  }
	 * @return
	 */
	@RequestMapping(value = "/fieldMapping", method = RequestMethod.POST)
	public JsonResult fieldMapping(@RequestBody FiledMapRequest request){
		JsonResult jsonResult = new JsonResult();
		try {
			Map<String, String> fieldMap = mappingService.updateFieldMapping(request);
			setJsonResult(jsonResult, fieldMap);
		} catch (Exception e) {
			setJsonResultOnException(jsonResult, e.getMessage());
		}
		return jsonResult;
	}
	
	/**
	 * 查询策略细分字段映射关系
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryFiledMaping", method = RequestMethod.POST)
	public JsonResult queryFieldMapping(@RequestBody FiledMapRequest request){
		JsonResult jsonResult = new JsonResult();
		try{
		Map<String, String> fieldMap = mappingService.queryFiledMapping(request);
		setJsonResult(jsonResult,fieldMap);
		}catch(Exception ex){
			setJsonResultOnException(jsonResult,ex.getMessage());
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
}
