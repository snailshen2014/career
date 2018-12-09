package com.bonc.busi.code.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.code.mapper.CodeMapper;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.utils.StringUtil;

@Service("codeService")
public class CodeServiceImpl implements CodeService {
	@Autowired
	private CodeMapper mapper;
	
	@Override
	public String getCodeValue(CodeReq req) {
		return mapper.getCodeValue(req);
	}

	/**
	 * @param tenantId
	 * @param tableName 
	 * @param fieldKey 
	 */
	@Override
	public List<CodeReq> getCodes(HashMap<String, String> pama) {
		if(!StringUtil.validateStr(pama.get("tableName"))){
			pama.put("_TABLE_NAME", "AND TABLE_NAME=#{tableName}");
		}
		if(!StringUtil.validateStr(pama.get("fieldKey"))){
			pama.put("_FIELD_KEY", "AND FIELD_KEY=#{fieldKey}");
		}
		return mapper.getCodes(pama);
	}

	/**
	 * @param tenantId 
	 * @param tableName 
	 * @param fieldKey 
	 */
	@Override
	public List<CodeReq> getXcloudTable(HashMap<String, String> pama) {
		if(!StringUtil.validateStr(pama.get("tableName"))){
			pama.put("_TABLE_NAME", "AND TABLE_NAME=#{tableName}");
		}
		if(!StringUtil.validateStr(pama.get("fieldKey"))){
			pama.put("_FIELD_KEY", "AND FIELD_KEY=#{fieldKey}");
		}
		return mapper.getXcloudCodes(pama);
	}

	@Override
	public Integer updateCode(CodeReq table) {
		return mapper.updateCode(table);
	}


}
