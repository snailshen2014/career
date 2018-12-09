package com.bonc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.bo.StoreDemandLabelRequest;
import com.bonc.busi.outer.bo.StoreRefreshOrderUsedLabel;
import com.bonc.busi.outer.service.UserLabelDetailsService;
import com.bonc.common.base.JsonResult;



@RestController
@RequestMapping("/userlabel")
public class UserLabelController{
	
	@Autowired
	private UserLabelDetailsService UserLabelDetailsServiceIns; 
	private final static Logger log = LoggerFactory.getLogger(UserLabelController.class);
	
	/**
	 * 获取用户标签表字段
	 * 请求地址：http://ip:port/ordertask-service/userlabel/getUserLabelDetails
	 * @param request
	 * 请求参数：
	 *  {
	 *     "tenantId": "uni076",         //租户是必传字段
	 *     "comment":""
	 *	    
	 *	}
	 * 返回结果：
	 *  {
	 *	    "code":"0",         //0表示成功    1表示失败,有异常
	 *		"columns": [
	 *	        {
	 *	            "dataType": "varchar",
	 *	            "length": 20,
	 *	            "columnName": "PROV_ID",
	 *	            "comment": "用户所属省份"
	 *	        },
	 *	        {
	 *	            "dataType": "varchar",
	 *	            "length": 40,
	 *	            "columnName": "USER_ID",
	 *	            "comment": "用户编码"
	 *	        },
	 *          ...
	 * 	    ]
	 *	}
	 * @return 
	 */
	@RequestMapping(value = "/getUserLabelDetails" , method = RequestMethod.POST)
	public String getUserLabelDetails(@RequestBody RequestParamMap param){

		String tenantId = param.getTenantId();
		String comment = param.getComment();
		log.info("TENANT_ID=" + tenantId);		
		Map<String,Object> message = new HashMap<String,Object>();
		try{			
			String validFlagKey = "ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+tenantId;
			String validFlag = UserLabelDetailsServiceIns.getValidFlag(validFlagKey);
			String validTableName = "PLT_USER_LABEL_"+validFlag;
			
			String myCatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL_0 WHERE TENANT_ID =" + "'" + tenantId +"'"+ "*/";
				
			String tableSchema = UserLabelDetailsServiceIns.getSchemaName(tenantId);
			param.setMyCatSql(myCatSql);
			param.setTableSchema(tableSchema);
			param.setValidTableName(validTableName);
			log.info("myCatSql=" + myCatSql + "    schemaName=" + tableSchema);	
			
			List<Map<String, Object>> userLabelDetails = new ArrayList<Map<String, Object>>();
			
			if(comment==null||comment.equals("")){
				userLabelDetails = UserLabelDetailsServiceIns.getUserLabel(param);
			}
			userLabelDetails = UserLabelDetailsServiceIns.getUserLabelIndistinct(param);
								
			message.put("code", "0");
			message.put("columns", userLabelDetails);			
		}catch(Exception e){
			e.printStackTrace();
			message.put("code", "1");
			message.put("columns", "error");
		};
		return JSON.toJSONString(message);
	}
	
	/**
	 * 保存渠道注册需要同步的标签到表中，供用户标签同步使用
	 * 请求地址：http://ip:port/ordertask-service/userlabel/storeDemandLabel
	 * @param request
	 * 请求参数：
	 *  {
	 *	    "columns": [
	 *	        {
	 *	            "dataType": "varchar",
	 *	            "length": 20,
	 *	            "xCloudColumn": "PROV_ID",
	 *	            "ColumnDesc": "用户所属省份"
	 *	        },
	 *	        {
	 *	            "dataType": "varchar",
	 *	            "length": 40,
	 *	            "xCloudColumn": "USER_ID",
	 *	            "ColumnDesc": "用户编码"
	 *	        }
	 * 	    ],
	 *	    "tenantId": "uni076"
	 *	}
	 * 返回结果：
	 *  {
	 *	  "locale": null,
	 *	  "message": "保存用户标签信息成功！",
	 *	  "code": "0",          //0表示成功    1表示失败,有异常
	 *	  "data": null
	 *	}
	 * @return
	 */
	@RequestMapping(value="/storeDemandLabel", method = RequestMethod.POST)
	public JsonResult storeDemandLabel(@RequestBody StoreDemandLabelRequest request){
		JsonResult jsonResult = new JsonResult();
		try{
			UserLabelDetailsServiceIns.storeDemandLabel(request);
			jsonResult.setCode("0");
			jsonResult.setMessage("保存用户标签信息成功！");
		}catch (Exception e) {
			jsonResult.setCode("1");
			jsonResult.setMessage(e.getMessage());
		}
		return jsonResult;
	}
	
	/**
	 * 保存刷新工单数据时需要刷新的用户标签数据
	 * 请求地址：http://ip:port/ordertask-service/userlabel/storeRefreshOrderUsedLabel
	 * 请求参数：
	 * {
	 *	    "columns": [
	 *	         "MB_OWE_FEE"
	 *	    ],
	 *	    "tenantId":"uni076",
	 *	    "channelId":"5"
	 *	}
	 * 响应结果：
	 * {
	 *	  "locale": null,
	 *	  "message": "SUCCESS",
	 *	  "data": null,
	 *	  "code": "0"    //0表示成功   1表示有异常
	 *	}
	 * @return
	 */
	@RequestMapping(value = "/storeRefreshOrderUsedLabel")
	public JsonResult storeRefreshOrderUsedLabel(@RequestBody StoreRefreshOrderUsedLabel request){
		JsonResult jsonResult = new JsonResult();
		try{
			UserLabelDetailsServiceIns.storeRefreshOrderUsedLabel(request);
			jsonResult.setCode("0");
			jsonResult.setMessage("SUCCESS");
		}catch(Exception ex){
			jsonResult.setCode("1");
			jsonResult.setMessage(ex.getMessage());
		}
		return jsonResult;
	}
}
