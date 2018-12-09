package com.bonc.controller.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.track.service.ActivityTrackService;
import com.bonc.busi.track.service.TrackService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;
import com.mysql.jdbc.Field;

@RestController
@RequestMapping("/track/")
public class ActivityTrackController {

	private static final Logger log = Logger.getLogger(ActivityTrackController.class);
	
	@Autowired
	private TrackService trackService;
	
	@Autowired
	private ActivityTrackService activityTrackService;
	
	//接触类工单明细接口
	@RequestMapping(value="contacttrack")
	public Object contacttrack(@RequestBody HashMap<String, Object> req){
		log.info("contacttrack请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
//		fields.add("activityId");
		fields.add("pageSize");
		fields.add("pageNum");
		fields.add("channelId");
		try {
			vailidateActivityIdOrSeqId(req);
			validate(req, fields);
			long start = System.currentTimeMillis();
			Object result=trackService.getContactTrack(req);
			long end = System.currentTimeMillis();
			log.info("工单执行记录查询总耗时——>>>>"+(end-start)/1000.0+"s");
			return result;
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", e.getMessage());
		}
		return resp;
	}
	
	@RequestMapping(value="orderrecord")
	public Object orderrecord(@RequestBody HashMap<String, Object> req){
		log.info("orderrecord请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		fields.add("activitySeqId");
		fields.add("pageSize");
		fields.add("pageNum");
		fields.add("channelId");
		try {
			validate(req, fields);
			long start = System.currentTimeMillis();
			Object result=trackService.getOrderRecord(req);
			long end = System.currentTimeMillis();
			log.info("工单执行记录查询总耗时——>>>>"+(end-start)/1000.0+"s");
			return result;
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", e.getMessage());
		}
		return resp;
	}
	
	/**
	 * 活动总计接口
	 * @param req
	 * @return
	 */
	@RequestMapping(value="activitytotal")
	public Object activityinfo(@RequestBody HashMap<String, Object> req){
		log.info("activityinfo请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp=new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
//		fields.add("activityId");
		try {
			vailidateActivityIdOrSeqId(req);
			validate(req,fields);
			long start = System.currentTimeMillis();
			Object result=trackService.getActivityChannelNum(req);
			long end = System.currentTimeMillis();
			log.info("工单执行记录查询总耗时——>>>>"+(end-start)/1000.0+"s");
			return result;
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
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

	@RequestMapping(value="updatehistory")
	public Object updatehistory(@RequestBody HashMap<String, Object> req){
		log.info("updatehistory请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
//		fields.add("activityId");
		fields.add("channelId");
		fields.add("pageSize");
		fields.add("pageNum");
		try {
			//验证必传字段
			vailidateActivityIdOrSeqId(req);
			validate(req,fields);
			
			long start = System.currentTimeMillis();
			Object result=trackService.updateHistory(req);
			long end = System.currentTimeMillis();
			log.info("工单执行记录查询总耗时——>>>>"+(end-start)/1000.0+"s");
			return result;
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", e.getMessage());
		}
		return resp;
	}
	
	/**
	 * 查询活动的各个每个批次的详细情况 ，免打扰用户数/成功过滤数/无效工单数，请求参数是活动的ID，租户ID,渠道ID
	 * @param req
	 * @param countdetail 
	 * @return
	 */
	@Deprecated
	@RequestMapping(value="countdetail")
	public Object countdetail(@RequestBody HashMap<String, Object> req){
		log.info("countdetail请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		fields.add("activityId");
		try {
			//验证必传字段
			validate(req,fields);
			
			long start = System.currentTimeMillis();
			Object result=trackService.countDetail(req);
			long end = System.currentTimeMillis();
			log.info("活动明细统计查询总耗时——>>>>"+(end-start)/1000.0+"s");
			return result;
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", e.getMessage());
		}
		return resp;
	}
	
	/**
	 * 查询过滤的用户明细
	 * @param req
	 * 	type 1:黑名单过滤数，2规则过滤数，3成功过滤数，4留存过滤
	 * 	beginDateStart 工单生成开始时间
	 * 	beginDateEnd 工单生成结束时间
	 *  orderStatus 
	 * @return
	 */
	@RequestMapping(value="filterlist")
	public Object filterlist(@RequestBody HashMap<String, Object> req){
		log.info("filterlist请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
//		fields.add("activityId");
		fields.add("pageSize");
		fields.add("pageNum");
		fields.add("channelId");
		fields.add("type");
		try {
			vailidateActivityIdOrSeqId(req);
			validate(req, fields);
			long start = System.currentTimeMillis();
			Object result=trackService.filterList(req);
			long end = System.currentTimeMillis();
			log.info("filterlist查询总耗时——>>>>"+(end-start)/1000.0+"s");
			return result;
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", e.getMessage());
		}
		return resp;
	}
	
//	//查询留存工单数
//	@RequestMapping(value="remaintrack")
//	public Object remaintrack(@RequestBody HashMap<String, Object> req){
//		log.info("查询留存工单数remaintrack请求参数——>"+JSON.toJSONString(req));
//		HashMap<String, Object> resp = new HashMap<String,Object>();
//		List<String> fields = new ArrayList<String>();
//		fields.add("tenantId");
//		fields.add("pageSize");
//		fields.add("pageNum");
////		fields.add("channelId");
//		try {
//			vailidateActivityIdOrSeqId(req);
//			validate(req, fields);
//			long start = System.currentTimeMillis();
//			Object result=trackService.getRemainTrack(req);
//			long end = System.currentTimeMillis();
//			log.info("remaintrack查询总耗时——>>>>"+(end-start)/1000.0+"s");
//			return result;
//		}catch(BoncExpection e){
//			resp.put("code", IContants.BUSI_ERROR_CODE);
//			resp.put("msg", e.getMsg());
//		} catch (Exception e) {
//			e.printStackTrace();
//			resp.put("code", IContants.SYSTEM_ERROR_CODE);
//			resp.put("msg", e.getMessage());
//		}
//		return resp;
//	}
	
	
	//1.活动跟踪总计接口
	@RequestMapping(value="actvitystatistic")
	public Object actvitystatistic(@RequestBody HashMap<String,Object> req){
		log.info("查询活动跟踪总计actvitystatistic请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String,Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		
		try{
			vailidateActivityIdOrSeqId(req);
			validate(req, fields);
			long start = System.currentTimeMillis();
			Object result = activityTrackService.getActvityStatistic(req);
			long end = System.currentTimeMillis();
			log.info("查询活动跟踪总计接口actvitystatistic总耗时——>"+(end-start)/1000.0+"s");
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
	//2.渠道总计接口
	@RequestMapping(value="channelstatistic")
	public Object channelstatistic(@RequestBody HashMap<String,Object> req){
		log.info("查询渠道总计channelstatistic请求参数——>"+JSON.toJSONString(req));
		HashMap<String, Object> resp = new HashMap<String,Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		try{
			vailidateActivityIdOrSeqId(req);
			validate(req, fields);
			long start = System.currentTimeMillis();
			Object result = activityTrackService.getChannelStatistic(req);
			long end = System.currentTimeMillis();
			log.info("查询渠道总计接口channelstatistic总耗时——>"+(end-start)/1000.0+"s");
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
	
	//3.工单更新历史接口
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
	
	
	//4.工单记录接口
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
//						e.printStackTrace();
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("msg", e.getMessage());
		}
		return resp;
	}
		
}
