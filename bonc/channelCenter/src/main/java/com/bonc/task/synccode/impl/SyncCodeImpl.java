/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: SyncCodeToMySql.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.synccode
 * @Description: SyncCodeImpl
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月16日 下午4:01:05
 * @version: V1.0  
 */

package com.bonc.task.synccode.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bonc.busi.codetable.mapper.CodeTableMapper;
import com.bonc.busi.globalCFG.mapper.GlobalCFGMapper;
import com.bonc.task.synccode.SyncCode;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.Constants;

/**
 * @ClassName: SyncCodeToMySql
 * @Description: SyncCodeImpl
 * @author: LiJinfeng
 * @date: 2017年1月16日 下午4:01:05
 */
@Component
public class SyncCodeImpl implements SyncCode {
	
	@Autowired
	private CodeTableMapper codeTableMapper;
	
	@Autowired
	private GlobalCFGMapper globalCFGMapper;
	
	private static Log log = LogFactory.getLog(SyncCodeImpl.class);
	
	private static String split = ".";
		

	/* (non Javadoc)
	 * @Title: SyncCode
	 * @Description: 批量码表同步
	 * @see com.bonc.task.synccode.SyncCode#SyncCode()
	 */
	@Override
	public void syncCode(String tenantId) {
		
		log.info("start syncCode");
		syncMysqlCode(tenantId);
		syncXcloudCode(tenantId);
		log.info("syncCode end");
		
	}
	
	/**
	 * @Title: syncMysqlCode
	 * @Description: 将MySQL固定码表同步至内存
	 * @return: void
	 * @param tenantId
	 * @throws: 
	 */
	private void syncMysqlCode(String tenantId){
		
		//获取mysql中固定码表数据
		List<HashMap<String, Object>> fixedCodeList = new ArrayList<HashMap<String, Object>>();
		String globalCFG = globalCFGMapper.getGlobalCFG(Constants.FIXED_TABLE);
		if(StringUtils.isBlank(globalCFG)){
			log.warn("fixed table list is null");
			return;
		}
		List<String> fixedTableList = Arrays.asList(globalCFG.split(Constants.SEPARATOR));
		if(fixedTableList == null || fixedTableList.isEmpty()){
			log.warn("fixed table list is null");
			return;
		}
		for(String fixedTable:fixedTableList){
			List<HashMap<String, Object>> subFixedCodeList = 
					codeTableMapper.getCodeListByFieldName(tenantId,fixedTable);
			if(subFixedCodeList == null || fixedTableList.isEmpty()){
				log.warn(fixedTable +":fixed code list is null in mysql");
				continue;
			}
			fixedCodeList.addAll(subFixedCodeList);
		}
		if(fixedCodeList == null || fixedCodeList.isEmpty()){
			log.warn("all fixed code list is null in mysql");
			return;
		}
		//放入内存中
		for(HashMap<String, Object> fixedCode:fixedCodeList){
			
			CodeUtil.setValue(tenantId+split+fixedCode.get("fieldName")+split+fixedCode.get("fieldKey"), 
					String.valueOf(fixedCode.get("fieldValue")));
			
		}
		
	}
	
	/**
	 * @Title: syncXcloudCode
	 * @Description: 将行云码表同步至内存
	 * @return: void
	 * @param tenantId
	 * @throws: 
	 */
	private void syncXcloudCode(String tenantId){
		
		//获取XCLOUD_CODE_TABLE
		List<HashMap<String, Object>> xcloudSqlList = 
				codeTableMapper.getCodeListByFieldName(tenantId,Constants.XCLOUD_TABLE);
		if(xcloudSqlList == null || xcloudSqlList.isEmpty()){
			log.warn("sql list from mysql is null!");
			return;
		}
		//缓存SQL语句至内存
		for(HashMap<String, Object> xcloudSql:xcloudSqlList){
			
			CodeUtil.setTable(tenantId+split+xcloudSql.get("fieldName")+split+xcloudSql.get("fieldKey"), 
					String.valueOf(xcloudSql.get("fieldValue")));
			
		}
		//获取行云中的码表数据
		List<HashMap<String, Object>> xcloudCodeList = new ArrayList<HashMap<String, Object>>();
		for(HashMap<String, Object> xcloudSql:xcloudSqlList){
			List<HashMap<String, Object>> subXcloudCodeList = new ArrayList<HashMap<String, Object>>();
			try {
				subXcloudCodeList = codeTableMapper.getXcloudCodeList(String.valueOf(xcloudSql.get("fieldValue")));
			} catch (Exception e) {
				log.warn(xcloudSql.get("fieldKey")+"sql is error");
				continue;
			}
			if(subXcloudCodeList == null || subXcloudCodeList.isEmpty()){
				log.warn(xcloudSql.get("fieldName")+":"+xcloudSql.get("fieldKey")+":xcloud data is null");
				continue;
			}
			xcloudCodeList.addAll(subXcloudCodeList);			
			/*break;*/
		}
		if(xcloudCodeList == null || xcloudCodeList.isEmpty()){	
			log.warn("valueList from xcloud is null");
			return;
		}
		//放入内存
		for(HashMap<String, Object> xcloudCode:xcloudCodeList){
			
			CodeUtil.setValue(tenantId+split+xcloudCode.get("FIELDNAME")+split+xcloudCode.get("FIELDKEY"), 
					String.valueOf(xcloudCode.get("FIELDVALUE")));
			
		}
		return;
	}


	 /*(non Javadoc)
	 * @Title: syncCode
	 * @Description: 单个码表记录同步
	 * @param fieldName
	 * @param fieldKey
	 * @see com.bonc.task.synccode.SyncCode#syncCode(java.lang.String, java.lang.String)
	 */
	@Override
	public String syncCode(String tenantId,String fieldName, String fieldKey) {
		
		String key = tenantId+split+fieldName+split+fieldKey;
		String value = CodeUtil.getValue(key);
		if(value != null){
			return value;
		}
		return fieldKey;
	}
	/*@Override
	public String syncCode(String tenantId,String fieldName, String fieldKey) {
		
		String key = tenantId+split+fieldName+split+fieldKey;
		String tableKey = tenantId+split+Constants.XCLOUD_TABLE+split+fieldName;
		//内存取
		String value = CodeUtil.getValue(key);
		if(value != null){
			return value;
		}
		//mysql取一个字段的码表数据
		List<HashMap<String, Object>> fixedCodeList = codeTableMapper.getCodeListByFieldName(tenantId, fieldName);
		if(fixedCodeList != null && !fixedCodeList.isEmpty()){
			for(HashMap<String, Object> fixedCode:fixedCodeList){
				
				CodeUtil.setValue(tenantId+split+fixedCode.get("fieldName")+split+fixedCode.get("fieldKey"), 
						String.valueOf(fixedCode.get("fieldValue")));
				
			}
			value = CodeUtil.getValue(key);
			if(value != null){
				return value;
			}
		}
		//内存取SQL语句
		String sql = CodeUtil.getTable(tableKey);
		if(sql == null){
			//MySQL取SQL语句
			HashMap<String, Object> xcloudSql = 
					codeTableMapper.getCodeByFieldNameAndFieldKey(tenantId,Constants.XCLOUD_TABLE,fieldName);
			if(xcloudSql == null){
				log.warn(fieldName+":sql is not exist");
				return fieldKey;
			}
			//将SQL语句放入内存
			CodeUtil.setTable(tableKey, String.valueOf(xcloudSql.get("fieldValue")));
			sql = CodeUtil.getTable(tableKey);
		}
		//行云去码表数据
		
		List<HashMap<String, Object>> xcloudCodeList = new ArrayList<HashMap<String, Object>>();
		try {
			xcloudCodeList = codeTableMapper.getXcloudCodeList(sql);
		} catch (Exception e) {
			log.warn(fieldName+":sql is error");
			return fieldKey;
		}
		if(xcloudCodeList == null || xcloudCodeList.isEmpty()){
			log.warn(fieldName+":xcloud data is null");
			return fieldKey;
		}
		//放内存，返回结果
		for(HashMap<String, Object> xcloudCode:xcloudCodeList){
			
			CodeUtil.setValue(tenantId+split+xcloudCode.get("FIELDNAME")+split+xcloudCode.get("FIELDKEY"), 
					String.valueOf(xcloudCode.get("FIELDVALUE")));
			
		}
		value = CodeUtil.getValue(key);
		if(value == null){
			log.warn(fieldName+fieldKey+":value is not exist in xcloud");
			return fieldKey;
		}
		return value;
		
	}*/

}
