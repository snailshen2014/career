package com.bonc.busi.sendField.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.bonc.busi.sendField.po.SendField;

/**
 * @ClassName: SendFieldMapper
 * @Description: 下发数据字段  操作类
 * @author: sky
 * @date: 2016年11月25日
 */
public interface SendFieldMapper {
	
	/**
	 * @Title: findSendField
	 * @Description: 查找YJQD_SEND_FIELD表中所有数据，并按ORD字段进行排序（升序）
	 * @return: List<Map>
	 * @return
	 * @throws: 
	 */
	public List<Map> findSendField(@Param("tenantId") String tenantId);

	/**
	 * @Title: insertSendField
	 * @Description: 向YJQD_SEND_FIELD表中插入数据
	 * @return: void
	 * @param sendField
	 * @throws: 
	 */
	public void insertSendField(
			@Param("sendField") SendField sendField);

	/**
	 * @Title: deleteSendField
	 * @Description: 根据COLUMN_NAME字段删除信息
	 * @return: void
	 * @param sendField
	 * @throws: 
	 */
	public void deleteSendField(
			@Param("sendField") SendField sendField);

}
