package com.bonc.controller.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.divide.model.DispatchReq;
import com.bonc.busi.divide.model.DividedResp;
import com.bonc.busi.divide.model.RulePreReq;
import com.bonc.busi.divide.service.ManuDivideService;
import com.bonc.busi.interfaces.model.RespHeader;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

/**
 * 
 * @author 高阳
 * @date 2016/11/17
 * @memo 手动划分控制类
 *
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/manudivide/")
public class ManuDivideController {
	private static final Logger log = Logger.getLogger(ManuDivideController.class);
	
	@Autowired
	private ManuDivideService manuDivideService;
	
	/**
	 * 1/活动划分统计列表
	 * 维度：当前ORG_PATH 的一批工单出一条记录(AREA_NO) 
	 * 属性：
	 * 	批次属性：账期、工单下发时间
	 * 	活动属性:关联活动表获取
	 * 	未分配工单量：该批次挂在当前组织机构上的
	 * 	已分配工单量：
	 * @param request
	 * @return
	 */
	@RequestMapping(value="divideActivityList")
	public Object divideActivityList(String req){
		log.info("活动划分统计列表——>>>>"+req);
		RespHeader respHeader = new RespHeader();
		if(null==req){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("request info is null!");
			return respHeader;
		}
		HashMap<String,Object> request = null;
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("error request info ——>"+req);
			return respHeader;
		}
		List<String> fields = new ArrayList<String>();
		fields.add("tenantId");
		fields.add("orgPath");
//		fields.add("channelId");
		fields.add("pageSize");
		fields.add("pageNum");
		try {
			StringUtil.validate(request, fields);
			long start = System.currentTimeMillis();
			HashMap<String, Object> resp = manuDivideService.divideActivityList(request);
			long end = System.currentTimeMillis();
			log.info("划配活动查询接口总耗时——>"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	
	@RequestMapping(value="getAreaList")
	public Object getAreaList(String req){
		log.info("getAreaList——>>>>"+req);
		RespHeader respHeader = new RespHeader();
		if(null==req){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！——>>>>"+req);
			return respHeader;
		}
		HashMap<String, Object> request = null;
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！");
			return respHeader;
		}
		try{
			long start = System.currentTimeMillis();
			List<HashMap<String,String>> resp = manuDivideService.getAreaList(request);
			long end = System.currentTimeMillis();
			log.info("userDivideList——>"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	
	/**
	 * 2/已划分工单列表
	 * 维度：一个批次、一个子级组织机构路径，一条记录
	 * 属性：
	 * 	归属组织属性：ORG_PATH、上级ORG_PATH、
	 * 	批次属性：地市(用户地市和客户经理地市一致，使用用户地市)
	 *  接受工单总数：有效工单
	 * 	
	 * @param request
	 * @return
	 */
	@RequestMapping(value="userDivideList")
	public Object userDivideList(String req){
		log.info("userDivideList——>>>>"+req);
		RespHeader respHeader = new RespHeader();
		if(null==req){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！——>>>>"+req);
			return respHeader;
		}
		HashMap<String, Object> request = null;
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！");
			return respHeader;
		}
		try{
			long start = System.currentTimeMillis();
			HashMap<String,Object> resp = manuDivideService.userDivideList(request);
			long end = System.currentTimeMillis();
			log.info("userDivideList——>"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	
	@RequestMapping(value="dividedOrderList")
	public Object dividedOrderList(String req){
		log.info("已划分工单列表——>>>>"+req);
		RespHeader respHeader = new RespHeader();
		if(null==req){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！——>>>>"+req);
			return respHeader;
		}
		HashMap<String, Object> request = null;
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！");
			return respHeader;
		}
		try{
			long start = System.currentTimeMillis();
			DividedResp resp = manuDivideService.dividedCellCount(request);
			long end = System.currentTimeMillis();
			log.info("已划分组织查询接口总耗时——>"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	
	/**
	 * 3/调配工单列表(工单状态是未执行的)
	 * @param request
	 * @return
	 */
	@RequestMapping(value="dispatchOrderList")
	public Object dispatchOrderList(@RequestParam("req") String req){
		log.info("调配工单列表——>>>>"+req);
		RespHeader respHeader = new RespHeader();
		if(null==req){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！——>>>>"+req);
			return respHeader;
		}
		HashMap<String, Object> request = null;
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！");
			return respHeader;
		}
		try{
			long start = System.currentTimeMillis();
			Object resp = manuDivideService.dispatchOrderList(request);
			long end = System.currentTimeMillis();
			log.info("调配工单列表查询接口总耗时——>"+(end-start)/1000.0+"s");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	
	/**
	 * 4/确认调配
	 * @param request
	 * @return
	 */
	@RequestMapping(value="dispatchOrder")
	public Object dispatchOrder(String req){
		log.info("确认调配——>>>>"+req);
		RespHeader respHeader = new RespHeader();
		if(null==req){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("请求参数不能为空！");
			return respHeader;
		}
		DispatchReq request = null;
		try{
			request = JSON.parseObject(req, DispatchReq.class);
		}catch(Exception e){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！——>>>>"+req);
			return respHeader;
		}
		try{
			long start = System.currentTimeMillis();
			manuDivideService.dispatchOrder(request);
			long end = System.currentTimeMillis();
			log.info("确认调配接口总耗时——>"+(end-start)/1000.0+"s");
			respHeader.setCode(IContants.CODE_SUCCESS);
			respHeader.setMsg(IContants.MSG_SUCCESS);
			return JSON.toJSONString(respHeader);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			e.printStackTrace();
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	
	/**
	 * 5/按规则分配列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="rulePreDivide")
	public Object rulePreDivide(String req){
		log.info("按规则分配列表——>>>>"+req);
		RespHeader respHeader = new RespHeader();
		if(null==req){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("请求参数不能为空！");
			return respHeader;
		}
		RulePreReq request = null;
		try{
			request = JSON.parseObject(req, RulePreReq.class);
		}catch(Exception e){
			respHeader.setCode(IContants.CODE_FAIL);
			respHeader.setMsg("错误的JSON请求参数！——>>>>"+req);
			return respHeader;
		}
		try{
			long start = System.currentTimeMillis();
			Object resp = manuDivideService.rulePreDivide(request);
			long end = System.currentTimeMillis();
			log.info("按规则分配预览接口总耗时——>"+(end-start)/1000.0+"s"+JSON.toJSONString(resp));
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			respHeader.setCode(IContants.BUSI_ERROR_CODE);
			respHeader.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return respHeader;
		}catch (Exception e) {
			e.printStackTrace();
			respHeader.setCode(IContants.SYSTEM_ERROR_CODE);
			respHeader.setMsg(IContants.SYSTEM_ERROR_MSG);
			return respHeader;
		}
	}
	

	/**
	 * 7/按明细分配列表
	 * @param request
	 * @return
	 */
//	@RequestMapping(value="detailPreDivide")
//	public Object detailPreDivide(String req){
//		log.info("按明细分配列表——>>>>"+req);
//		RespHeader resp = new RespHeader();
//		if(null==req){
//			resp.setCode(IContants.CODE_FAIL);
//			resp.setMsg("请求参数不能为空！");
//			return resp;
//		}
//		RulePreReq request = null;
//		try{
//			request = JSON.parseObject(req, RulePreReq.class);
//		}catch(Exception e){
//			resp.setCode(IContants.CODE_FAIL);
//			resp.setMsg("错误的JSON请求参数！——>>>>"+req);
//			return resp;
//		}
//		try{
//			long start = System.currentTimeMillis();
//			manuDivideService.detailPreDivide(request);
//			long end = System.currentTimeMillis();
//			log.info("按明细分配预览接口总耗时——>"+(end-start)/1000.0+"s"+JSON.toJSONString(resp));
//			resp.setCode(IContants.CODE_SUCCESS);
//			resp.setMsg("success!");
//			return JSON.toJSONString(resp);
//		}catch(BoncExpection e){
//			resp.setCode(IContants.BUSI_ERROR_CODE);
//			resp.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
//			return resp;
//		}catch (Exception e) {
//			e.printStackTrace();
//			resp.setCode(IContants.SYSTEM_ERROR_CODE);
//			resp.setMsg(IContants.SYSTEM_ERROR_MSG);
//			return resp;
//		}
//	}
	
	/**
	 * 8/确认划分列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="confirmDivide")
	public Object confirmDivide(String req){
		RespHeader resp = new RespHeader();
		if(null==req){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("请求参数不能为空！");
			return resp;
		}
		HashMap<String, Object> request = null;
		try{
			request = JSON.parseObject(req, HashMap.class);
		}catch(Exception e){
			resp.setCode(IContants.CODE_FAIL);
			resp.setMsg("错误的JSON请求参数！——>>>>"+req);
			return resp;
		}
		try{
			long start = System.currentTimeMillis();
			manuDivideService.confirmDivide(request);
			long end = System.currentTimeMillis();
			log.info("确认分配接口总耗时——>"+(end-start)/1000.0+"s"+JSON.toJSONString(resp));
			resp.setCode(IContants.CODE_SUCCESS);
			resp.setMsg("success!");
			return JSON.toJSONString(resp);
		}catch(BoncExpection e){
			resp.setCode(IContants.BUSI_ERROR_CODE);
			resp.setMsg(IContants.BUSI_ERROR_MSG+e.getMsg());
			return resp;
		}catch (Exception e) {
			e.printStackTrace();
			resp.setCode(IContants.SYSTEM_ERROR_CODE);
			resp.setMsg(IContants.SYSTEM_ERROR_MSG);
			return resp;
		}
	}
	
}
