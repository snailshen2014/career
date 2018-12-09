package com.bonc.controller.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.orderschedule.service.OrderService;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.scene.service.SceneService;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@RestController
@RequestMapping("/scenemarket")
public class SceneMarketController {
	private final static Logger log = LoggerFactory.getLogger(SceneMarketController.class);
	
	@Autowired
	private OrderActivityService OrderActivityServiceIns;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SceneService sceneService;
	
	@RequestMapping(value = "/startActivity")
	public JsonResult startActivity(@RequestBody String request) {
		log.info("活动添加接口请求数据——>" + request);
		JsonResult JsonResultIns = new JsonResult();
		if (null == request) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("场景营销活动数据不能为空！——>>>>请求格式为：req=JSON串,注意特殊字符可能会URL转码开始生成");
			return JsonResultIns;
		}
		JsonResultIns = orderService.startSceneMarketActivity(request);
		return JsonResultIns;
	}

	@RequestMapping(value = "/queryOrderInfo ")
	public Object queryOrderInfo(@RequestBody HashMap<String, Object> req) {
		log.info("工单查询接口请求参数——>" + JSON.toJSONString(req));
		JsonResult JsonResultIns = new JsonResult();
		if (null == req) {
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("场景营销请求参数不能为空！");
			return JsonResultIns;

		}
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("phoneNum");
		fields.add("contactDateStart");
		fields.add("contactDateEnd");
//		fields.add("envType");
//		fields.add("channelId");
		fields.add("tenantId");
		// --参数判断，是否必传，是否选传--
		for(String field:fields){
			if(StringUtil.validateStr(req.get(field))){
				resp.put("code", IContants.CODE_FAIL);
				resp.put("msg", field+" is empty");
				return resp;
			}
		}
		//--查询，Map-->json.toString(Map)--
		try{
			long start = System.currentTimeMillis();
			List<HashMap<String,Object>> result = OrderActivityServiceIns.queryActivityOrderInfo(req);
			long end = System.currentTimeMillis();
			log.info("场景营销工单查询接口总耗时——>>>>"+(end-start)/1000.0+"s");
			return JSON.toJSONString(result);
		}catch(BoncExpection e){
			resp.put("code", IContants.BUSI_ERROR_CODE);
			resp.put("msg", e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("code", IContants.SYSTEM_ERROR_CODE);
			resp.put("code", e.getMessage());
		}
		return resp;
	}

	@RequestMapping(value = "/addOrderInfo")
	public JsonResult addOrderInfo(@RequestBody String request) {
		log.info("工单添加接口请求参数——>" + request);
		JsonResult JsonResultIns = new JsonResult();
		if (null == request) {
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("场景营销工单信息不能为空！");
			return JsonResultIns;
		}
		OrderAndOrderSMS sceneOrder = new OrderAndOrderSMS();
		try {
			sceneOrder = JSON.parseObject(request, OrderAndOrderSMS.class);
		} catch (Exception e) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("错误的JSON请求数据！");
			return JsonResultIns;
		}
		//--生成工单--
		/*if (!(genAllChannelOrderInfo(actjson, ogr_range, PltCommonLogIns))) {
			JsonResultIns.setCode("3");
			JsonResultIns.setMessage("场景营销活动生成工单失败");
			return JsonResultIns;
		}*/
		//--过滤工单--
		//--更改状态--
		//--调用统计--
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("场景营销工单生成成功");
		return JsonResultIns;
	}
	
	@RequestMapping(value = "/querySuccessNum ")
	public Object querySuccessNum(@RequestBody HashMap<String, Object> req) {
		log.info("场景成功工单数查询请求参数——>" + JSON.toJSONString(req));
		JsonResult JsonResultIns = new JsonResult();
		if (null == req) {
			JsonResultIns.setCode("1");
			JsonResultIns.setMessage("场景营销请求参数不能为空！");
			return JsonResultIns;

		}
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> fields = new ArrayList<String>();
		fields.add("activityId");
//		fields.add("contactDateStart");
//		fields.add("contactDateEnd");
		fields.add("tenantId");
		// --参数判断，是否必传，是否选传--
		for(String field:fields){
			if(StringUtil.validateStr(req.get(field))){
				resp.put("code", IContants.CODE_FAIL);
				resp.put("msg", field+" is empty!");
				return resp;
			}
		}
		//--查询，Map-->json.toString(Map)--
		try{
			long start = System.currentTimeMillis();
			HashMap<String, Object> result = sceneService.querySuccessNum(req);
			long end = System.currentTimeMillis();
			log.info("场景营销成功工单数查询接口总耗时——>>>>"+(end-start)/1000.0+"s");
			return JSON.toJSONString(result);
		}catch(BoncExpection e){
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
