package com.bonc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.backpage.AsynPageService;

@Controller
@RequestMapping("asyn")
public class AsynPageController {
	@Autowired
	AsynPageService service;

	
//	@RequestMapping(value = "asynuserlabelmonitor", method = { RequestMethod.GET })
	@ResponseBody
	public Object asynUserLabel(HttpServletRequest request) {
		
		String tenantId = request.getParameter("tenantId");
		System.out.println(tenantId);
		try {
			
//			JsonConfig jc = new JsonConfig();
//			jc.registerJsonValueProcessor(Date.class,new JsonDateValueProcessor());
//			net.sf.json.JSONArray JO = net.sf.json.JSONArray.fromObject(service.getAsynUserLabel(tenantId), jc);
			List<Map<String, Object>> result = service.getAsynUserLabel(tenantId);
			
			HashMap<String, Object> resp = new HashMap<String, Object>();
			resp.put("rows",result);
			resp.put("total",result.size());
			System.out.println(result.toString());
			return JSON.toJSONString(resp);
		} catch (Exception e) {
			HashMap<String, Object> resp = new HashMap<String, Object>();
			e.printStackTrace();
			resp.put("rows", new ArrayList<Integer>());
			resp.put("total", 0);
			return resp;
		}
	}

 
@RequestMapping(value = "asynuserlabelmonitor", method = { RequestMethod.GET })
@ResponseBody
public Object asynUserLabelMonitor(@Param("tenantId")String tenantId,@Param("pageSize")String pageSize,@Param("pageNumber")String pageNumber){
	
	System.out.println("tenantId--"+tenantId+" pageSizeTemp== "+pageSize+" pageNumber== "+pageNumber);
	
	int begin = 0;
	if(Integer.parseInt(pageNumber)!=1){
		begin = (Integer.parseInt(pageNumber) - 1)*Integer.parseInt(pageSize);
	}
	
	int end = Integer.parseInt(pageSize);
	
	try {
		
		//每页需要展示的数据
		List<Map<String, Object>> result = service.getAsynUserLabelParam(tenantId,begin,end);
		
        //根据查询条件，获取符合查询条件的数据总量
	    int total = service.getAsynUserLabelTotal(tenantId);
		
			
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("rows",result);
		resp.put("total",total);
		System.out.println(result.toString());
		return JSON.toJSONString(resp);
	} catch (Exception e) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		e.printStackTrace();
		resp.put("rows", new ArrayList<Integer>());
		resp.put("total", 0);
		return resp;
	}
}
}



