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
import com.bonc.busi.task.service.SceneService;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

import oracle.jdbc.proxy.annotation.Post;

@RestController
@RequestMapping("/scenepower")
public class ScenePowerController {
	private final static Logger log = LoggerFactory.getLogger(ScenePowerController.class);
	
	@Autowired
	private SceneService sceneServiceImpl;
	
	
	//批量插入场景能力数据
	
	
	//查询场景能力状态返回状态码和数值
	@RequestMapping(value = "/addSenceRecordBatch")
	@Post
	public JsonResult addSenceRecordBatch(@RequestBody String req) {
		log.info("活动添加接口请求数据开始——>" + req);
		JsonResult JsonResultIns = new JsonResult();
		if (null == req) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("场景营销能力数据不能为空！——>>>>请求格式为：请求body中的JSON串,注意特殊字符可能会URL转码开始生成");
			return JsonResultIns;
		}
		HashMap<Object, Object> request = new HashMap<>();
		try{
			long start = System.currentTimeMillis();
			request = JSON.parseObject(req, HashMap.class);
			JsonResultIns = sceneServiceImpl.addSenceRecordBatch(request);
			long end = System.currentTimeMillis();
			log.info("活动添加接口请求数据结束——> 耗费时间为" + (end-start)/1000.0+"s");
		}catch(Exception e){
			e.printStackTrace();
			JsonResultIns.setCode(IContants.CODE_FAIL);
			log.error("业务出错"+ e.getMessage());
			JsonResultIns.setMessage("error request info ——>"+e.getMessage());
			return JsonResultIns;
		}
		return JsonResultIns;
	}
	
	@RequestMapping(value = "/queryScencePowerStatus")
	public JsonResult queryScencePowerStatus(@RequestBody String req) {
		log.info("场景能力查询接口开始——>" + req);
		JsonResult JsonResultIns = new JsonResult();
		if (null == req) {
			JsonResultIns.setCode("2");
			JsonResultIns.setMessage("场景营销能力数据不能为空！——>>>>请求格式为：请求body中的JSON串,注意特殊字符可能会URL转码开始生成");
			return JsonResultIns;
		}
		HashMap<Object, String> request = new HashMap<>();
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			JsonResultIns.setCode(IContants.CODE_FAIL);
			JsonResultIns.setMessage("error request info ——>"+req);
			return JsonResultIns;
		}
		long start = System.currentTimeMillis();
		try{
			JsonResultIns = sceneServiceImpl.queryScencePowerStatus(request);
		}catch(Exception e){
			e.printStackTrace();
			log.error("业务出错"+ e.getMessage());
		}
		long end = System.currentTimeMillis();
		log.info("场景能力查询接口结束——> 耗费时间为" + (end-start)/1000.0+"s");
		return JsonResultIns;
	}


}
