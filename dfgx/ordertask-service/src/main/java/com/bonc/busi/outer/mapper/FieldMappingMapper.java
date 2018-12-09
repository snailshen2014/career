package com.bonc.busi.outer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.outer.bo.FieldMapRecord;
import com.bonc.busi.outer.bo.FiledMapRequest;

/**
 * 字段与工单字段的数据库方法
 * @author Administrator
 *
 */
public interface FieldMappingMapper {

	//选择与请求字段进行对应的工单的字段
	@Select("SELECT ORDER_COLUMN FROM PLT_ORDER_TABLE_COLUMN_MAP_INFO WHERE  ORDER_COLUMN LIKE 'BUSINESS%' AND  "
			+ " TENANT_ID=#{tenantId} AND IN_USE = 0  ORDER BY REC_ID DESC LIMIT  #{limitNum}")
	List<Map<String, String>> chooseOrderFiled(@Param("tenantId") String tenantId,@Param("limitNum") int limitNum);

	//更新MAP_INFO表中的字段的使用情况
	@Update("UPDATE PLT_ORDER_TABLE_COLUMN_MAP_INFO SET IN_USE =1 WHERE ORDER_COLUMN IN  ${condition} AND TENANT_ID=#{tenantId}")
	void updateOrderFieldUsingInfo(@Param("tenantId") String tenantId, @Param("condition") String updateFieldCollection);

	//在STRATEGY_ORDER_FIELD_MAPPING表中插入字段对应关系
	@Insert("INSERT INTO STRATEGY_ORDER_FIELD_MAPPING(TENANT_ID,ACTIVITY_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,STRATEGY_FIELD_NAME,ORDER_FIELD_NAME) "
			+ " VALUES (#{tenantId},#{activityId},${activitySeqId},#{channelId},#{strategyFieldName},#{orderFieldName})")
	void insertFieldMapRecord(FieldMapRecord fieldMapRecord);

	//根据条件查询策略细分字段与工单表字段之间的映射关系
	@Select("SELECT STRATEGY_FIELD_NAME,ORDER_FIELD_NAME FROM STRATEGY_ORDER_FIELD_MAPPING WHERE "
			+ " ACTIVITY_ID=#{activityId} AND ACTIVITY_SEQ_ID=${activitySeqId} AND CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId}")
	List<Map<String, String>> queryFiledMapping(FiledMapRequest request);
}
