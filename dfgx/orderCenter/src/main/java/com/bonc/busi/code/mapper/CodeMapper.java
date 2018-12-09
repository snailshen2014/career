package com.bonc.busi.code.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.code.model.CodeReq;

public interface CodeMapper {

	/**
	 * update 
	 * @param table
	 */
	@Update("UPDATE PLT_STATIC_CODE SET ${fieldValue} LOAD_DATE=NOW() WHERE TENANT_ID=#{tenantId} AND TABLE_NAME=#{fieldName} AND FIELD_KEY=#{fieldKey} ")
	Integer updateCode(CodeReq table);
	
	@Select("SELECT FIELD_VALUE FROM PLT_STATIC_CODE WHERE TENANT_ID=#{tenantId} AND TABLE_NAME=#{fieldName} AND FIELD_KEY=#{fieldKey}")
	String getCodeValue(CodeReq req);

	@Select("SELECT TENANT_ID tenantId,TABLE_NAME fieldName,FIELD_KEY fieldKey,FIELD_VALUE fieldValue,DATE_FORMAT(LOAD_DATE,'%Y%m%d%H%i%s') loadDate"
			+ " FROM PLT_STATIC_CODE WHERE TENANT_ID=#{tenantId} ${_TABLE_NAME} ${_FIELD_KEY}")
	public List<CodeReq> getCodes(HashMap<String, String> tenantId);
	
	@Select("SELECT TENANT_ID tenantId,TABLE_NAME fieldName,FIELD_KEY fieldKey,FIELD_VALUE fieldValue,DATE_FORMAT(LOAD_DATE,'%Y%m%d%H%i%s') loadDate"
			+ " FROM PLT_XCLOUD_TABLE WHERE TENANT_ID=#{tenantId} AND STATUS=1 ${_TABLE_NAME} ${_FIELD_KEY}")
	public List<CodeReq> getXcloudCodes(HashMap<String, String> tenantId);

}
