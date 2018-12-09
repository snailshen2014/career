package com.bonc.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.statistic.service.RemoveUserService;
import com.bonc.utils.CodeUtil;

@Controller
@RequestMapping("")
public class IndexController {
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private RemoveUserService removeUserService;
	
	@RequestMapping(value="/index")
	public String index(HttpServletRequest request ,HttpServletResponse response){
		return "index";
	}
	
	@RequestMapping(value="/")
	public String home(HttpServletRequest request ,HttpServletResponse response){
		return index(request,response);
	}
	
	@RequestMapping(value="/codetype")
	@ResponseBody
	public Object getCode(@RequestBody CodeReq req){
		if("0".equals(req.getFieldType())){
			return JSON.toJSONString(CodeUtil.getStaticCide(),SerializerFeature.WriteMapNullValue);
		}else {
			return codeService.getValue(req);
		}
	}
	
	@RequestMapping(value="/updateOrder")
	public String updateAll(@RequestBody HashMap<String, Object> req){
//		String dataId = "20171006";
		String dataId = req.get("dataId").toString();
		removeUserService.updateOrder(dataId);
		return "index";
	}
	
	@RequestMapping(value="/init")
	public String home(){
		removeUserService.initload();
		return "";
	}
	
	@RequestMapping(value="/updateAll")
	@ResponseBody
	public Object updateOrder(@RequestBody HashMap<String, Object> req){
//		String dataId = "20171005";
		String dataId = req.get("dataId").toString();
		removeUserService.updateAll(dataId);
		return dataId;
	}
	
}
