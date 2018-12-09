package com.bonc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.service.ActivityTrackService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@RestController
@RequestMapping("/track/")
public class ActivityTrackController {

	private static final Logger log = Logger.getLogger(ActivityTrackController.class);
	
	
	@Autowired
	private ActivityTrackService activityTrackService;
	
	
	    //4.工单记录接口
	    /**
	     * 参数名	                               是否必传
		 *	tennatId	             是
	     *  channelId	            是
		 *	activitySeqId	是
		 *	orderDate	            是
		 * 	pageSize	            是
		 *	pageNum	                       是
	     * @param req
	     * @return
	     */
		@RequestMapping(value="updaterecord")
		public Object updaterecord(@RequestBody HashMap<String,Object> req){
			log.info("查询工单记录接口updaterecord请求参数——>"+JSON.toJSONString(req));
			HashMap<String, Object> resp = new HashMap<String,Object>();
			List<String> fields = new ArrayList<String>();
			fields.add("tenantId");
			fields.add("channelId");
			fields.add("orderDate");
			
			try{
				vailidateActivityIdOrSeqId(req);
				validate(req, fields);
				long start = System.currentTimeMillis();
				Object result = activityTrackService.getUpdateRecord(req);
				long end = System.currentTimeMillis();
				log.info("查询工单记录接口updaterecord总耗时——>"+(end-start)/1000.0+"s");
				return result;
			}catch(BoncExpection e){
				resp.put("code", IContants.BUSI_ERROR_CODE);
				resp.put("msg", e.getMsg());
			}catch(Exception e){
//							e.printStackTrace();
				resp.put("code", IContants.SYSTEM_ERROR_CODE);
				resp.put("msg", e.getMessage());
			}
			return resp;
		}
	
	
	
	private void validate(HashMap<String, Object> req,List<String> fields) {
		for(String field:fields){
			if(StringUtil.validateStr(req.get(field))){
				throw new BoncExpection(IContants.CODE_FAIL,field+" is empty");
			}
		}
	}
	
	private void vailidateActivityIdOrSeqId(HashMap<String, Object> req) {
		
		if(StringUtil.validateStr(req.get("activityId"))&&StringUtil.validateStr(req.get("activitySeqId"))){
			throw new BoncExpection(IContants.CODE_FAIL,"activityId and activitySeqId is empty");
		}
		
	}
	
	//3.工单更新历史接口
	/**
	 * 请求参数
	 * {"tenantId":"uni081","activityId":"41614","page":"1","channelId":"1","pageSize":15,"pageNum":1,"rows":"15"}
	 */
	@RequestMapping(value="orderhistory")
	public Object orderhistory(@RequestBody HashMap<String,Object> req){
		log.info("查询工单更新历史接口orderhistory请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String,Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		fields.add("channelId");
		fields.add("pageSize");
		fields.add("pageNum");
		
		try{
			vailidateActivityIdOrSeqId(req);
			validate(req, fields);
			long start = System.currentTimeMillis();
			Object result = activityTrackService.getOrderhistory(req);
			long end = System.currentTimeMillis();
			log.info("查询工单更新历史接口orderhistory总耗时——>"+(end-start)/1000.0+"s");
			return result;
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		}catch(Exception e){
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", e.getMessage());
		}
		return resp;
	}		
}
