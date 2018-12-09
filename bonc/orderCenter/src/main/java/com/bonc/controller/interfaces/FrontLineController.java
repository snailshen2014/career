package com.bonc.controller.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.interfaces.model.ReqHeader;
import com.bonc.busi.interfaces.model.RespHeader;
import com.bonc.busi.interfaces.model.frontline.ContactHistoryResp;
import com.bonc.busi.interfaces.model.frontline.ContactReq;
import com.bonc.busi.interfaces.model.frontline.OrderQueryReq;
import com.bonc.busi.interfaces.service.ContactService;
import com.bonc.busi.interfaces.service.FrontLineService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@RestController
@SuppressWarnings("unchecked")
@RequestMapping("/interface/frontline/")
public class FrontLineController {
	private static final Logger log = Logger.getLogger(FrontLineController.class);

	@Autowired
	private FrontLineService frontLineService;
	
	@Autowired
	private ContactService contactService;
	
	private Boolean publicVerify(ReqHeader req){
		return null==req||null==req.getTenantId()||null==req.getOrgPath();
	}
	
	private RespHeader errorReq(String code,String msg){
		RespHeader respHeader = new RespHeader();
		respHeader.setCode(code);
		if(null==msg)
			respHeader.setMsg("pama is null! ,request fomart is req=JSONStr, be careful some sprcial char maybe url encode");
		else
			respHeader.setMsg(msg);
		return respHeader;
	}
	
	/**
	 * 1,
	 * app side want get an customer manager all activity statistic info
	 * we provide this interface
	 * 
	 * in web side they only need get an activity page list 
	 * so far only app side call this interface
	 * 
	 * interface support query mode and execute mode
	 * mulit-orgPath: some manager only need query and have Multiple orgPath, we need like any path
	 * loginId: some manager only can execute , use wendfing_flag as loginId,find this loginId's order
	 * @param req
	 * @return
	 */
	@RequestMapping(value="taskall" ,method=RequestMethod.POST)
	public Object taskAll(String req){
		log.info("taskall interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL,null);
		}
		HashMap<String, Object> reqMap = null;
		try {
			reqMap = (HashMap<String, Object>) JSON.parseObject(req, Map.class);
		} catch (Exception e) {
			return errorReq(IContants.CODE_FAIL,"error resolve json param :"+req);
		}
		try{
			long start = System.currentTimeMillis();
			HashMap<String, Object> resp = frontLineService.taskAll(reqMap);
			long end = System.currentTimeMillis();
			log.info("taskall lose time :"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
	}
	

	/**
	 * 2,
	 *  customer manager statistic interface 
	 * 
	 * when front line manager execute order ,then want look a info ,i have execute how many order
	 * how many success in every activity
	 * how many fail
	 * how many execute today
	 * through this interface can get a customer manager's activity execute statistic info
	 * 
	 * interface support query mode and execute mode
	 * mulit-orgPath: some manager only need query and have Multiple orgPath, we need like any path
	 * loginId: some manager only can execute , use wendfing_flag as loginId,find this loginId's order
	 * @param req
	 * @return
	 */
	@RequestMapping(value="activitystatistic",method=RequestMethod.POST)
	public Object activitystatistic(String req){
		log.info("activitystatistic interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL, null);
		}
		HashMap<String, Object> reqMap = null;
		try {
			reqMap = JSON.parseObject(req, HashMap.class);
		} catch (Exception e) {
			return errorReq(IContants.CODE_FAIL,"error resolve json param :"+req);
		}
		try{
			long start = System.currentTimeMillis();
			HashMap<String, Object> resp = frontLineService.activityStatistic(reqMap);
			long end = System.currentTimeMillis();
			log.info("activitystatistic lose time :"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
	}
	
	/**
	 * 3,
	 * some manager want look under current orgPath level's employees Executive ranking
	 * like all center,all unit or all Business hall
	 * 
	 * this interface get the first few employees in the range
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value="custmanagerstatistic" ,method=RequestMethod.POST)
	public Object custmanagerstatistic(String req){
		log.info("custmanagerstatistic interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL, null);
		}
		HashMap<String, Object> reqMap = null;
		try {
			reqMap = (HashMap<String, Object>) JSON.parseObject(req, Map.class);
		} catch (Exception e) {
			return errorReq(IContants.CODE_FAIL,"error resolve json param :"+req);
		}
		try{
			long start = System.currentTimeMillis();
			HashMap<String, Object> resp = frontLineService.custManagerStatistic(reqMap);
			long end = System.currentTimeMillis();
			resp.put("code", IContants.CODE_SUCCESS);
			log.info("custmanagerstatistic lose time :"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
	}
	
	/**
	 * 4,
	 * order query interface
	 * 
	 * when customer manager execute order,they want query some order by userInfo and orderInfo
	 * 
	 * in MYSQL ,we have around 50 fields store userInfo ,we need through userId field Association
	 * user table query order,and query some userInfo what manager want look 
	 * 
	 * interface support query mode and execute mode
	 * mulit-orgPath: some manager only need query and have Multiple orgPath, we need like any path
	 * loginId: some manager only can execute , use wendfing_flag as loginId,find this loginId's order
	 * @param req
	 * @return
	 */
	@RequestMapping(value="orderquery")
	public Object orderquery(@RequestParam("req") String req){
		log.info("orderquery interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL, null);
		}
		OrderQueryReq reqMap = null;
		try {
			reqMap = JSON.parseObject(req, OrderQueryReq.class);
		} catch (Exception e) {
			return errorReq(IContants.CODE_FAIL,"error resolve json param :"+req);
		}
		try{
			long start = System.currentTimeMillis();
			HashMap<String, Object> resp = frontLineService.orderQuery(reqMap);
			long end = System.currentTimeMillis();
			log.info("orderquery lose time :"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
	}
	
	/**
	 * 5,
	 * user order query interface
	 * 
	 * query the user's all order,this is all activity
	 * 
	 * when customer executer order ,they need know info of this activity
	 * label info of user,how to talk with user,
	 * if some variable in talk word,we need according userInfo replace it
	 * 
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value="userquery")
	public Object userquery(String req){
		log.info("orderquery interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL, null);
		}
		HashMap<String, String> request = null;
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			return errorReq(IContants.CODE_FAIL,"error resolve json param :"+req);
		}
		try{
			// userId and phoneNumber must have one
			if(StringUtil.validateStr(request.get("userId"))&&StringUtil.validateStr(request.get("phoneNum"))){
				throw new BoncExpection(IContants.CODE_FAIL,"userId and phoneNum not all null!");
			}
			long start = System.currentTimeMillis();
			HashMap<String, Object> resp = frontLineService.userQuery(request);
			long end = System.currentTimeMillis();
			resp.put("code",IContants.CODE_SUCCESS);
			log.info("userquery lose "+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
	}
	
	/**
	 * 5,
	 * 
	 * receipt interface
	 * 
	 * we will bring the customer execute info to the order,and update the customer statistic info
	 * our @activitystatistic interface @taskall interface all read this info
	 * 
	 * we also update activitySeq channel info ,to look this channel's execute situation
	 * @param req
	 * @return
	 */
	@RequestMapping(value="receipt")
	public Object receipt(String req){
		log.info("receipt interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL, null);
		}
		ContactReq requset = null;
		try{
			requset = JSON.parseObject(req, ContactReq.class);
		}catch(Exception e){
			return errorReq(IContants.CODE_FAIL,"error resolve json param :"+req);
		}
		if(null==requset.getPama()||publicVerify(requset)){
			return errorReq(IContants.CODE_FAIL,"some pama is null :"+req );
		}
		try {
			long start = System.currentTimeMillis();
			contactService.contact(requset);
			long end = System.currentTimeMillis();
			log.info("userquery lose "+(end-start)/1000.0+"s");
			return errorReq(IContants.CODE_SUCCESS, "success!");
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
	}
	
	/**
	 * @deprecated
	 * 	this interface to query a user all channel's contact record 
	 * 
	 * course this interface query is too slow ,it will not use
	 * through channelcoord interface query it
	 * @param req
	 * @return
	 */
	@RequestMapping(value="contacthistory")
	public Object contacthistory(String req){
		log.info("receipt interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL, null);
		}
		HashMap<String, Object> request = null;
		try{
			request = JSON.parseObject(req,HashMap.class); 
		}catch(Exception e){
			return errorReq(IContants.CODE_FAIL,"error resolve json param :"+req);
		}
		try {
			long start = System.currentTimeMillis();
			ContactHistoryResp resp = frontLineService.contactHistory(request);
			long end = System.currentTimeMillis();
			log.info("contacthistory lose "+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
		
	}
	
	
	
	/**
	 * 8,　
	 * when app side execute order ,they may find some userinfo is not right,they can modify 
	 * the userInfo,only some filds can modify
	 * @param req
	 * @return
	 */
	@RequestMapping(value="modifyuserext")
	public Object modifyuserext(@RequestBody HashMap<String, Object> req){
		log.info("modifyuserext interface pama :"+req);
		if(null==req){
			return errorReq(IContants.CODE_FAIL, null);
		}
		if(null==req.get("userId")||null==req.get("orgPath")||null==req.get("tenantId")||null==req.get("channelId")){
			return errorReq(IContants.CODE_FAIL,"some pama is null :"+req );
		}
		try {
			long start = System.currentTimeMillis();
			frontLineService.modifyUserInfo(req);
			long end = System.currentTimeMillis();
			log.info("modifyuserext lose "+(end-start)/1000.0+"s");
			return errorReq(IContants.CODE_SUCCESS, "success!");
		}catch(BoncExpection e){
			return errorReq(IContants.BUSI_ERROR_CODE, IContants.BUSI_ERROR_MSG+e.getMsg());
		}catch (Exception e) {
			return errorReq(IContants.SYSTEM_ERROR_CODE, IContants.SYSTEM_ERROR_MSG);
		}
	}
	
}
