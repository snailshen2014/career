package com.bonc.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;

import com.bonc.common.base.JsonResult;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.task.service.BaseTaskSrv;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.task.base.BusiTools;



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
	private BaseTaskSrv  BaseTaskSrvIns;
	@Autowired
	private	BusiTools		BusiToolsIns;
	
	//@Autowired    private StringRedisTemplate stringRedisTemplate;
	
	private final static Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	/*
	 * 设置活动状态
	 */
/*	//@RequestMapping(value="/setstatus")
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
		
		//return JsonResultIns;
	}*/
	
	@RequestMapping(value="/orderendcheck")
	public JsonResult OrderEndCheck(){
		//BaseTaskSrvIns.checkOrderSucess();
		BaseTaskSrvIns.orderCheck();
		//BaseTaskSrvIns.asynUserLabel();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/asynuserlabel")
	public JsonResult AsynUserLabel(){
		BaseTaskSrvIns.asynUserLabel();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	
	// --- 更新工单表的userid ---
	@RequestMapping(value="/updateuserid")
	public JsonResult UpdateUserId(){
		BaseTaskSrvIns.updateUserId();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	
	@RequestMapping(value="/testftp")
	public JsonResult TestFtpUpload(){
		//log.info("rtn:"+FtpTools.upload("192.168.0.178", "wxwl", "WXwl#186", 21, "/ftp_xcloud/wxwl/data/asyn/22.txt", 
	//			"e:/tmpp/USER_LABEL_DATA_200.csv"));
		log.info("rtn:"+FtpTools.downloadXcloudFile("10.162.156.117", "clyx_xcloud_034", "Efg_87h_ty6", 21,
				"/xccloud/clyx_xccloud_034/USER_LABEL2222.csv",
				"/mnt/test/USER_LABEL_DATA_200.csv",false));
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/testdb/{dbtype}/{timedur}",method=RequestMethod.GET)
	//@RequestMapping(value="/testdb")
	public JsonResult TestDb(@PathVariable(value = "timedur") String timeDur,
			@PathVariable(value = "dbtype") String dbtype){
		BaseTaskSrvIns.testDbConnection(dbtype,timeDur);
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/testredis")
	public JsonResult TestRedis(){
	//	stringRedisTemplate.opsForValue().set("phone_no","12024234");
	//	log.info("phone_no:{}",stringRedisTemplate.opsForValue().get("phone_no"));
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/testhightime")
	public JsonResult TestHighTime(){
	//	stringRedisTemplate.opsForValue().set("phone_no","12024234");
	//	log.info("phone_no:{}",stringRedisTemplate.opsForValue().get("phone_no"));
		log.info(" num:{}",BaseTaskSrvIns.getOrderNum());
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/testmycat")
	public JsonResult TestMycat(){
	//	stringRedisTemplate.opsForValue().set("phone_no","12024234");
	//	log.info("phone_no:{}",stringRedisTemplate.opsForValue().get("phone_no"));
		log.info(" num:{}", BaseTaskSrvIns.testMycat());
		//BaseTaskSrvIns.updateOrderUserLabel();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/testorderuser")
	public JsonResult TestOrderUser(){
	//	stringRedisTemplate.opsForValue().set("phone_no","12024234");
	//	log.info("phone_no:{}",stringRedisTemplate.opsForValue().get("phone_no"));
		 BaseTaskSrvIns.updateOrderUserLabel();
		//BaseTaskSrvIns.updateOrderUserLabel();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	
	@RequestMapping(value="/testfiltersucess")
	public JsonResult TestFilterSucess(){
	//	stringRedisTemplate.opsForValue().set("phone_no","12024234");
	//	log.info("phone_no:{}",stringRedisTemplate.opsForValue().get("phone_no"));
		SuccessStandardPo		SuccessStandardPoIns = new SuccessStandardPo();
		
//		List<SuccessProductPo> listProduct = new ArrayList<>();
//		SuccessProductPo s1 = new SuccessProductPo();
//		SuccessProductPo s2 = new SuccessProductPo();
//		SuccessProductPo s3 = new SuccessProductPo();
//		s1.setProductCode("8016186");
//		s2.setProductCode("8148462");
//		s3.setProductCode("8127481");
		
//		listProduct.add(s1);
//		listProduct.add(s2);
//		listProduct.add(s3);
		
//		SuccessStandardPoIns.setSuccessProductList(listProduct);;
//		SuccessStandardPoIns.setMatchingType("2");
		SuccessStandardPoIns.setSuccessType("2");
		SuccessStandardPoIns.setActivity_seq_id("210018");
		SuccessStandardPoIns.setTenantId("uni076");
		
//		SuccessStandardPoIns.setSuccessConditionSQL("S_07 IN( '公开版4G终端', '水货4G终端', '联通4G定制机')");
//		SuccessStandardPoIns.setSuccessConditionSQL("CH_C_03  = '76b2gh7'");
		SuccessStandardPoIns.setSuccessTypeConditionSql("TE_N_04='4'");
		//SuccessStandardPoIns.setMatchingType("1");
		 BaseTaskSrvIns.orderFilterSucess(210018, SuccessStandardPoIns, "uni076");
		//BaseTaskSrvIns.updateOrderUserLabel();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	/*
	 * 测试活动序列号 
	 */
	@RequestMapping(value="/testActivityId")
	public JsonResult TestActivityId(){
	//	stringRedisTemplate.opsForValue().set("phone_no","12024234");
	//	log.info("phone_no:{}",stringRedisTemplate.opsForValue().get("phone_no"));
		
		//BaseTaskSrvIns.updateOrderUserLabel();
		int  id = BusiToolsIns.getActivitySeqId();
		log.info("id={}",id);
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage(String.valueOf(id));
		return JsonResultIns;
	}

}
