package com.bonc.controller;

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
import com.bonc.busi.outer.model.ActivityStatus;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.send.service.SentService;
import com.bonc.busi.task.service.BaseTaskSrv;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.service.MutltiDSInterface;




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
	private MutltiDSInterface  MutltiDSInterfaceIns;
	
	@Autowired
	private SentService sentService;
	//@Autowired    private StringRedisTemplate stringRedisTemplate;
	
	private final static Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	/*
	 * 设置活动状态
	 */
	//@RequestMapping(value="/setstatus")
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
	}
	
	@RequestMapping(value="/createorder")
	public JsonResult createOrder(){
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("开始工单生成");
		return JsonResultIns;
	}
	
	@RequestMapping(value="/createchannelorder")
	public JsonResult createChannelOrder(){
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("开始渠道工单生成");
		return JsonResultIns;
	}
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
	@RequestMapping(value="/asynusersms")
	public JsonResult asynusersms(){
		sentService.sent();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/sendsmssynch")
	public JsonResult sendsmssynch(){
		sentService.synchSend();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}
	@RequestMapping(value="/testftp")
	public JsonResult TestFtpUpload(){
		//log.info("rtn:"+FtpTools.upload("192.168.0.178", "wxwl", "WXwl#186", 21, "/ftp_xcloud/wxwl/data/asyn/22.txt", 
	//			"e:/tmpp/USER_LABEL_DATA_200.csv"));
		/*
		log.info("rtn:"+FtpTools.downloadXcloudFile("192.168.0.178", "wxwl", "WXwl#186", 21, 
		"/ftp_xcloud/wxwl/data/asyn/22.txt",
		"e:/tmpp/USER_LABEL_DATA_200.csv",false));
		*/
		log.info("rtn:"+FtpTools.downloadXcloudFile("10.162.6.226", "open_076", "S7KZlfd9U", 21, 
				"/files/prov/076/dataasyn/USER_LABEL2222.csv",
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
	@RequestMapping(value="/testmultids")
	public JsonResult TestMultiDs(){
	//	stringRedisTemplate.opsForValue().set("phone_no","12024234");
	//	log.info("phone_no:{}",stringRedisTemplate.opsForValue().get("phone_no"));
		MutltiDSInterfaceIns.test1();
		//BaseTaskSrvIns.updateOrderUserLabel();
		JsonResult	JsonResultIns = new JsonResult();
		JsonResultIns.setCode("0");
		JsonResultIns.setMessage("sucess");
		return JsonResultIns;
	}

}
