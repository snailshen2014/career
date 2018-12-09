package com.bonc.utils;

import java.util.HashMap;

import com.bonc.busi.code.model.CodeReq;

public class CodeUtil {
	//宽带、移动字段对应的码表 以及公共字段对应的码表
	private static HashMap<String, String> tableMap = new HashMap<>();
	private static HashMap<String, String> staticCode = new HashMap<>();

	public static String getTable(String key) {
		return tableMap.get(key);
	}
	public static synchronized String setTable(String key,String value) {
		return tableMap.put(key, value);
	}
	public static String getValue(String key) {
		return staticCode.get(key);
	}
	public static synchronized void setValue(String key, String value) {
		staticCode.put(key, value);
	}
	
	public static HashMap<String, String> getStaticCide(){
		return staticCode;
	}

	private static HashMap<String, String> fieldMap = new HashMap<String,String>();

	public static String getFieldMap(String key) {
		return fieldMap.get(key);
	}
	
	public static synchronized void setFieldMap(String key, String value) {
		fieldMap.put(key, value);
	}

	// 字段映射码表key生成
	public static String getTableKey(CodeReq code) {
		return code.getTenantId() + IContants.DO_SPLIT + code.getFieldType()
				+ IContants.DO_SPLIT + code.getFieldName();
	}

	// 码表key生成
	public static String getCodeKey(CodeReq code) {
		return code.getTenantId() + IContants.DO_SPLIT + code.getFieldName()
				+ code.getFieldKey();
	}
	
	private static HashMap<String,HashMap<String, String>> codeTables = new HashMap<String,HashMap<String,String>>();
	
	public static HashMap<String,String> getCodeTable(String tenantId,
			String tableName) {
		return codeTables.get(tenantId+IContants.DO_SPLIT+tableName);
	}

	public static synchronized void setCodeTable(String tenantId,
			String tableName,HashMap<String, String> map) {
		codeTables.put(tenantId+IContants.DO_SPLIT+tableName,map);
	}
}
