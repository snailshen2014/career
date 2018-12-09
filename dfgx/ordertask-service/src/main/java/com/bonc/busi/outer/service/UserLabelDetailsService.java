package com.bonc.busi.outer.service;

import java.util.List;
import java.util.Map;

import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.bo.StoreDemandLabelRequest;
import com.bonc.busi.outer.bo.StoreRefreshOrderUsedLabel;

public interface UserLabelDetailsService {

	/**
	 * 根据租户ID查询对应的租户库名：clyx_app_gd_hlj,clyx_app_gd_hainan,sichuan
	 */
	String getSchemaName(String tenantId);
	
	/**
	 * 查询用户标签表的全部字段
	 */
	List<Map<String, Object>> getUserLabel(RequestParamMap param);
	
	/**
	 * 模糊查询用户标签表的部分字段
	 */
	List<Map<String, Object>> getUserLabelIndistinct(RequestParamMap param);
	
	/**
	 * 获取当前有效的用户标签表标识
	 */
	String getValidFlag(String string);
	
	/**
	 * 保存渠道注册时需要的用户标签到表中
	 * @param request
	 * @throws Exception 
	 */
	void storeDemandLabel(StoreDemandLabelRequest request) throws Exception;

	/**
	 * 保存刷新工单数据要刷新的的用户标签字段
	 * @param request
	 * @throws Exception 
	 */
	void storeRefreshOrderUsedLabel(StoreRefreshOrderUsedLabel request) throws Exception;
}
