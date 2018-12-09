/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: CodeTableMapper.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.codetable
 * @Description: CodeTableMapper
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月16日 下午4:10:45
 * @version: V1.0  
 */

package com.bonc.busi.codetable.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @ClassName: CodeTableMapper
 * @Description: CodeTableMapper
 * @author: LiJinfeng
 * @date: 2017年1月16日 下午4:10:45
 */
public interface CodeTableMapper {
	
	/**
	 * @Title: getCodeListByFieldName
	 * @Description: 根据FieldName字段获取信息
	 * @return: List<HashMap<String,Object>>
	 * @param tenantId
	 * @param fieldName
	 * @return
	 * @throws: 
	 */
    @Select("SELECT FIELD_NAME fieldName,FIELD_KEY fieldKey,FIELD_VALUE fieldValue FROM PLT_CODE_TABLE "
    		+ "WHERE FIELD_NAME = #{fieldName} AND TENANT_ID = #{tenantId}")
	public List<HashMap<String, Object>> getCodeListByFieldName(@Param("tenantId")String tenantId,
			@Param("fieldName")String fieldName);
	
	/**
	 * @Title: getCodeByFieldNameAndFieldKey
	 * @Description: 根据FieldName字段与FieldKey字段获取信息
	 * @return: HashMap<String,Object>
	 * @param tenantId
	 * @param fieldName
	 * @param fieldKey
	 * @return
	 * @throws: 
	 */
    @Select("SELECT FIELD_NAME fieldName,FIELD_KEY fieldKey,FIELD_VALUE fieldValue FROM PLT_CODE_TABLE "
    		+ "WHERE FIELD_NAME = #{fieldName} AND FIELD_KEY = #{fieldKey} AND TENANT_ID = #{tenantId}")
	public HashMap<String, Object>  getCodeByFieldNameAndFieldKey(@Param("tenantId")String tenantId,
			@Param("fieldName")String fieldName,@Param("fieldKey")String fieldKey);
	
	/**
	 * @Title: getXcloudCodeList
	 * @Description: 根据SQL从新云获取一个字段对应的全部码表数据
	 * @return: List<HashMap<String,Object>>
	 * @param xcloudSql
	 * @return
	 * @throws: 
	 */
    @Select("${sql}")
	public List<HashMap<String, Object>> getXcloudCodeList(@Param("sql")String sql);

}
