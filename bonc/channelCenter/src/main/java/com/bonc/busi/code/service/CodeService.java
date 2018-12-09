package com.bonc.busi.code.service;

import java.util.HashMap;
import java.util.List;

import com.bonc.busi.code.model.CodeReq;



/**
 * 码表服务接口
 * @author 高阳
 *
 */
public interface CodeService {
	
	/**
	 * 获取码表
	 * @param fieldName 映射的字段名
	 * @param code code值
	 * @return
	 */
	public String getValue(CodeReq req);
	
	public List<HashMap<String, String>> getCodeTable(CodeReq req);
	
	/*String getInterfacePama(CodeReq reqCode);*/
}
