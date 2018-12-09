package com.bonc.busi.outer.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.outer.bo.FieldMapRecord;
import com.bonc.busi.outer.bo.FiledMapRequest;
import com.bonc.busi.outer.mapper.FieldMappingMapper;
import com.bonc.busi.outer.service.FieldMappingService;

@Service
public class FieldMappingServiceImpl implements FieldMappingService {

	@Autowired
	FieldMappingMapper fieldMapper;

	@Override
	@Transactional
	public Map<String, String> updateFieldMapping(FiledMapRequest request) throws Exception {
		// 保存字段之间的映射关系
		Map<String, String> fieldMap = new HashMap<String, String>();
		String tenantId = request.getTenantId(); // 租户
		String type = request.getType(); // 请求类型
		String[] fields = request.getFields(); // 字段集合
		String activityId = request.getActivityId();
		int activitySeqId = request.getActivitySeqId();
		String channelId = request.getChannelId();
		// 请求参数必须合法
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(tenantId) && fields != null && fields.length > 0
				&& StringUtils.isNotBlank(activityId) && StringUtils.isNotBlank(String.valueOf(activitySeqId))
				&& StringUtils.isNotBlank(channelId)) {
			int fieldLength = fields.length;
			if (type.equals("0")) { // 策略细分字段映射
				// step 1 : 从PLT_ORDER_TABLE_COLUMN_MAP_INFO查询可用的列
				List<Map<String, String>> choosedOrderField = fieldMapper.chooseOrderFiled(tenantId, fieldLength);
				// step 2 : 更新选取的列的使用记录
				String[] choosedFieldArray = new String[fieldLength]; // 保存从工单表中选取的字段的名称
				if (choosedOrderField != null && choosedOrderField.size() == fieldLength) { // 字段个数一一匹配
					for (int pos = 0; pos < fieldLength; pos++) {
						choosedFieldArray[pos] = choosedOrderField.get(pos).get("ORDER_COLUMN");
					}
					String updateFieldCollection = composeSqlCondition(choosedFieldArray);
					fieldMapper.updateOrderFieldUsingInfo(tenantId, updateFieldCollection);
					// step 3 : 在STRATEGY_ORDER_FIELD_MAPPING中记录字段之间的映射关系
					fieldMap = recordMappingRelation(request, choosedFieldArray);
				} else {
					throw new Exception("工单表里没有足够的空闲的字段了!");
				}
			}
		} else {
			 throw new Exception("请求的参数不合法,请检查");
		}

		return fieldMap;
	}

	/**
	 * STRATEGY_ORDER_FIELD_MAPPING表中增加字段之间的映射关系
	 * 
	 * @param request
	 * @param choosedFieldArray
	 * @return 字段之间的映射关系
	 */
	private Map<String, String> recordMappingRelation(FiledMapRequest request, String[] choosedFieldArray) {
		String tenantId = request.getTenantId(); // 租户
		String activityId = request.getActivityId();
		int activitySeqId = request.getActivitySeqId();
		String channelId = request.getChannelId();
		String[] fields = request.getFields(); // 字段集和
		Map<String, String> fieldMap = new HashMap<String, String>();
		FieldMapRecord fieldMapRecord = new FieldMapRecord();
		fieldMapRecord.setTenantId(tenantId);
		fieldMapRecord.setActivityId(activityId);
		fieldMapRecord.setActivitySeqId(activitySeqId);
		fieldMapRecord.setChannelId(channelId);
		for (int i = 0; i < fields.length; i++) {
			fieldMapRecord.setStrategyFieldName(fields[i]);
			fieldMapRecord.setOrderFieldName(choosedFieldArray[i]);
			fieldMap.put(fields[i], choosedFieldArray[i]);
			// fieldMapper.insertFieldMapRecord(fieldMapRecord);
		}
		return fieldMap;
	}

	/**
	 * 拼接sql中的in条件 ('','','')
	 * 
	 * @param choosedFieldArray 字段数组
	 * @return 字段数组拼接('','','')
	 */
	private String composeSqlCondition(String[] choosedFieldArray) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("( ");
		for (int i = 0; i < choosedFieldArray.length; i++) {
			buffer.append("'" + choosedFieldArray[i] + "'");
			if (i != choosedFieldArray.length - 1) {
				buffer.append(",");
			}
		}
		buffer.append(" )");
		return buffer.toString();
	}

	/**
	 * 策略细分字段与工单表字段的映射关系查询
	 */
	@Override
	public Map<String, String> queryFiledMapping(FiledMapRequest request) throws Exception {
		Map<String, String> fieldMap = new HashMap<String, String>();
		String tenantId = request.getTenantId();
		String activityId = request.getActivityId();
		int activitySeqId = request.getActivitySeqId();
		String channelId = request.getChannelId();
		// 这些查询条件不能为空
		if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(activityId)
				&& StringUtils.isNotBlank(String.valueOf(activitySeqId)) && StringUtils.isNotBlank(channelId)) {

			List<Map<String, String>> queryFiledMap = fieldMapper.queryFiledMapping(request);
			if (queryFiledMap != null && queryFiledMap.size() > 0) {
				for (int i = 0; i < queryFiledMap.size(); i++) {
					String strategyFieldName = queryFiledMap.get(i).get("STRATEGY_FIELD_NAME");
					String orderFieldName = queryFiledMap.get(i).get("ORDER_FIELD_NAME");
					fieldMap.put(strategyFieldName, orderFieldName);
				}
			}
		} else {
			throw new Exception("查询参数不合法，请检查");
		}
		return fieldMap;
	}
}
