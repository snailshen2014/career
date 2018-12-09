package com.bonc.busi.orderschedule.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * column mapping db interface
 * @author yanjunshen
 *
 */
public interface SqlMappingMapper {
	/**
	 *  get order table column mapping info
	 *  @return table column mapping info
	 */
	@Select ("select ORDER_COLUMN_SEQ,ORDER_COLUMN,ORDER_COLUMN_DES,SOURCE_TABLE_ALIAS,SOURCE_TABLE_COLUMN,COLUMN_TYPE,SQL_BLOCK from PLT_ORDER_TABLE_COLUMN_MAP_INFO "
			 + " where TENANT_ID=#{tenantId} and in_use=1 order by order_column_seq asc")
	public List<Map<String, Object>> getTableColumnMappingInfo(@Param("tenantId") String tenantId);
	
	/**
	 *  get select sql's table part
	 *  @return table ,alias list
	 */
	@Select ("select SOURCE_TABLE,TABLE_SEQ,TABLE_ALIAS,TABLE_TYPE from PLT_ORDER_DATA_SOURCE_TABLES_DEF where  "
			+ " TENANT_ID=#{tenantId} order by TABLE_SEQ asc")
	public List<Map<String, Object>> getSqlTableMappingInfo(@Param("tenantId") String tenantId);
	

	/**
	 *  get select sql's condition part
	 *  @return condition part
	 */
	@Select ("Select CON_SEQ,CON_SQL,CON_TYPE,CON_ADD from PLT_ORDER_DATA_CONDITION  where TENANT_ID=#{tenantId} order by con_seq asc")
	public List<Map<String, Object>> getSqlConditionMappingInfo(@Param("tenantId") String tenantId);
}
