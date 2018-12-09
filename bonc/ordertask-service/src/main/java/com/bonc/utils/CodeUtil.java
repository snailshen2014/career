package com.bonc.utils;

import java.util.HashMap;

import org.apache.log4j.Logger;
import com.bonc.busi.outer.model.CodeReq;

public class CodeUtil {
	
	private static final Logger log = Logger.getLogger(CodeUtil.class);
	
	/**
	 * we defined this memory block to storage all codeTable
	 */
	private static HashMap<String, HashMap<String, HashMap<String, CodeReq>>> staticCode = new HashMap<String, HashMap<String,HashMap<String,CodeReq>>>();
	
	/**
	 * you can get a value by key  ,you have to provide tenantId and your code type of course your key
	 * 
	 * @param tenantId	we support MULTI-tenantId
	 * @param fieldName	code type
	 * @param key
	 * @return
	 */
	public static String getValue(String tenantId,String fieldName,String key) {
		try{
			return getValue(tenantId,fieldName).get(key).getFieldValue();
		}catch(Exception e){
			log.warn("not exist this code"+tenantId+"_"+fieldName+"_"+key);
			return key;
		}
	}
	
	/**
	 * get all code data by your code type,we put it to a HashMap
	 * @param tenantId
	 * @param fieldName
	 * @return
	 */
	public static HashMap<String, CodeReq> getValue(String tenantId,String fieldName) {
		return staticCode.get(tenantId).get(fieldName);
	}
	
	/**
	 * like getValue by codeType, we provide a method to give a hashMap 
	 * you can just get what you want ,no need CodeReq bean,only a String value
	 * @param tenantId
	 * @param fieldName
	 * @return
	 */
	public static HashMap<String, String> getCodeMap(String tenantId,String fieldName){
		HashMap<String, String> codeMap = new HashMap<String, String>();
		HashMap<String, CodeReq> code = getValue(tenantId,fieldName);
		if(null!=code){
			for(String key:code.keySet()){
				codeMap.put(key, code.get(key).getFieldValue());
			}
		}
		return codeMap;
	}
	
	/**
	 * get all memory block
	 * @return
	 */
	public static HashMap<String, HashMap<String, HashMap<String, CodeReq>>> getStaticCide(){
		return staticCode;
	}
	
	/**
	 * only this function can change my data block
	 * @param tenantId
	 * @param tableName
	 * @param codes
	 */
	public synchronized static String reload(String tenantId,String tableName,HashMap<String, CodeReq> codes){
		if(null==staticCode.get(tenantId)){
			HashMap<String, HashMap<String, CodeReq>> codeMap=new HashMap<String, HashMap<String,CodeReq>>();
			staticCode.put(tenantId, codeMap);
		}
		if(null==tableName){
			return "-2";
		}
		//delete old data
		staticCode.get(tenantId).remove(tableName);
		
		//load new data to block
		if(null!=codes){
			staticCode.get(tenantId).put(tableName, codes);
		}
		
		return "0";
	}
}
