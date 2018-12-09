package com.bonc.busi.code.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.code.model.CodeReq;

public interface CodeMapper {

	/**
	 * 从行云取码表数据
	 * @param codeTable
	 * @return
	 */
	@SelectProvider(type = CodeSelectGen.class, method = "getXcloudCode")
	List<String> getXcloudCode (CodeReq req);

	/**
	 * 向mysql 插入码表数据
	 * @param staticCode
	 */
	@Insert("INSERT INTO PLT_STATIC_CODE (TENANT_ID,TABLE_NAME,FIELD_KEY,FIELD_VALUE,LOAD_DATE) "
			+ " values (#{tenantId},#{fieldName},#{fieldKey},#{fieldValue},now())")
	public void addCode(CodeReq codeTable);
	
	/**
	 * 从mysql取码表数据 保持一周内有效
	 * @param staticCode
	 */
	@Select("SELECT FIELD_VALUE,IF(LOAD_DATE>CURDATE()-7,TRUE,FALSE) IS_LOAD FROM PLT_STATIC_CODE WHERE TENANT_ID=#{tenantId} AND TABLE_NAME=#{fieldName} AND FIELD_KEY=#{fieldKey} limit 1")
	public HashMap<String, Object> getLocalCode(CodeReq codeReq);

	/**
	 * 更新mysql码表
	 * @param table
	 */
	@Update("UPDATE PLT_STATIC_CODE SET FIELD_VALUE=#{fieldValue},LOAD_DATE=NOW() WHERE TENANT_ID=#{tenantId} AND TABLE_NAME=#{fieldName} AND FIELD_KEY=#{fieldKey} ")
	void updateCode(CodeReq table);

	/**
	 * 从mysql 中取xcloud　sql
	 * @param req
	 * @return
	 */
	@Select("SELECT * FROM PLT_STATIC_CODE WHERE TENANT_ID=#{tenantId} AND TABLE_NAME='XCLOUD_CODE_TABLE' ")
	List<HashMap<String, String>> getXcloudSql(CodeReq req);

	/**
	 * 
	 * @param req
	 * @return
	 */
	@Select("SELECT * FROM PLT_STATIC_CODE WHERE TENANT_ID=#{tenantId} AND TABLE_NAME=#{fieldName}")
	List<HashMap<String, String>> getCodeTable(CodeReq req);
}
