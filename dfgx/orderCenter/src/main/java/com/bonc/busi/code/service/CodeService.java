package com.bonc.busi.code.service;

import java.util.HashMap;
import java.util.List;

import com.bonc.busi.code.model.CodeReq;



/**
 * code service 
 * we use it to operate table PLT_STATIC_CODE and PLT_XCLOUD_TABLE
 * @author gaoYang
 *
 */
public interface CodeService {

	/**
	 * @Table PLT_STATIC_CODE
	 * through tenantId,tableName and key get a value
	 * @param req
	 * @return
	 */
	String getCodeValue(CodeReq req);
	
	/**
	 * @Table PLT_STATIC_CODE
	 * 
	 * through tenantId and table get many Code bean,if you give a key ,maybe you will get one bean
	 * @param pama
	 * @return
	 */
	List<CodeReq> getCodes(HashMap<String, String> pama);

	/**
	 * @Table PLT_XCLOUD_TABLE
	 * 
	 * we put all xcloud code table in an independent table ,you maybe want get one or many xcloud code bean,
	 * you can use it
	 * 
	 * memory tenantId,tableName,fieldKey.
	 * warning: you better give me a tableName ,or you will get all code bean ,it's so bad
	 * @param pama
	 * @return
	 */
	List<CodeReq> getXcloudTable(HashMap<String, String> pama);
	
	/**
	 * @Table PLT_XCLOUD_TABLE
	 * 
	 * update some code,memory operate some code you know it,it's so Dangerous. so give s tableName is good,fieldKey is best
	 * @param table
	 * @return
	 */
	Integer updateCode(CodeReq table);
	
}
