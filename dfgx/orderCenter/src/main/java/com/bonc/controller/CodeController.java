package com.bonc.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.varsion.VersionService;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.IContants;

@RestController
@RequestMapping("/code/")
public class CodeController {

	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private VersionService version;
	
	@RequestMapping(value="all")
	public Object allCode(){
		return JSON.toJSONString(CodeUtil.getStaticCide());
	}
	
//	@RequestMapping(value="tableMap")
//	public Object code(){
//		return JSON.toJSONString(CodeUtil.getCodeTables());
//	}
	 
	@RequestMapping(value="changeVersion")
	public Object changeVersion(@RequestParam("tenantId") String tenantId,
			@RequestParam("busiName") String busiName,@RequestParam("content") String content){
		HashMap<String, String> resp = new HashMap<String, String>();
		try {
			resp.put("code", IContants.CODE_SUCCESS);
			version.changeVersion(tenantId, busiName,content);
		} catch (Exception e) {
			resp.put("code", IContants.CODE_FAIL);
		}
		return resp;
	}
	
	@RequestMapping(value="getVersion")
	public Object getVersion(@RequestParam("tenantId") String tenantId,
			@RequestParam("busiName") String busiName){
		HashMap<String, String> resp = new HashMap<String, String>();
		try {
			resp.put("code", IContants.CODE_SUCCESS);
			resp = version.getVersion(tenantId, busiName);
		} catch (Exception e) {
			resp.put("code", IContants.CODE_FAIL);
		}
		return resp;
	}
}
