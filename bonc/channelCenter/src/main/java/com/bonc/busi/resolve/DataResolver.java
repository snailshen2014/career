package com.bonc.busi.resolve;

import java.util.Map;

/**
 * 数据解析接口  
 * 对接收的数据进行解析   
 * @author sky
 *
 */
public interface DataResolver {
	/**
	 * 解析数据
	 * @throws Exception 
	 */
	 Map<String,Object> resolveData(String src) throws Exception;
	
}