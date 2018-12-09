package com.bonc.busi.outer.service;

import java.util.Map;

import com.bonc.busi.outer.bo.FiledMapRequest;

/**
 * 策略细分字段或用户标签字段与工单子段映射服务接口
 * @author Administrator
 *
 */
public interface FieldMappingService {

	/**
	 * 建立字段与工单字段的映射关系
	 * @param request
	 * @throws Exception 
	 */
	Map<String, String> updateFieldMapping(FiledMapRequest request) throws Exception;

	/**
	 * 查询策略细分字段与工单表字段的映射关系
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> queryFiledMapping(FiledMapRequest request) throws Exception;

}
