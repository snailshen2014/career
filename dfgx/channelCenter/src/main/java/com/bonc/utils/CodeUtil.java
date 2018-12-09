package com.bonc.utils;

import java.util.HashMap;

public class CodeUtil {
	//宽带、移动字段对应的码表 以及公共字段对应的码表
	private static HashMap<String, String> tableMap = new HashMap<String,String>(250000);
	private static HashMap<String, String> codeMap = new HashMap<String,String>(250000);

	public static String getTable(String key) {
		return tableMap.get(key);
	}
	public static void setTable(String key,String value) {
		if(key==null || value==null){
			return;
		}
		tableMap.put(key, value);
	}
	public static String getValue(String key) {
		return codeMap.get(key);
	}
	public static void setValue(String key, String value) {
		if(key==null || value==null){
			return;
		}
		codeMap.put(key, value);
	}
	
	public static Boolean isSync(){
		if(codeMap.isEmpty()){
			return false;
		}
		return true;
	}
	/**
	 * @Title: clear
	 * @Description: 清除本地缓存
	 * @return: void
	 * @throws: 
	 */
	public static void clearTable(){
		tableMap.clear();
		/*codeMap.clear();*/
		/*staticCode.put("uni076.PROV_ID.076", "河南");*/
	}
	
	/*public static HashMap<String, String> getStaticCode(){
		return staticCode;
	}*/
	
	
	/*private static HashMap<String, String> fieldMap = new HashMap<String,String>();// 宽带

	public static String getFieldMap(String key) {
		return fieldMap.get(key);
	}
	
	public static synchronized void setFieldMap(String key, String value) {
		fieldMap.put(key, value);
	}*/

	// 字段映射码表key生成
	/*public static String getTableKey(CodeReq code) {
		return code.getTenantId() + Constants.DO_SPLIT + code.getFieldType()
				+ Constants.DO_SPLIT + code.getFieldName();
	}

	// 码表key生成
	public static String getCodeKey(CodeReq code) {
		return code.getTenantId() + Constants.DO_SPLIT + code.getFieldName()
				+ code.getFieldKey();
	}*/

}
