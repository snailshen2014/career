package com.bonc.busi.service.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

public interface DynamicAsynMapper {

    @Select("SELECT DISTINCT TENANT_ID,COLUMN_NAME,DATA_TYPE,LENGTH,LABEL_COMMENT FROM PLT_DEMAND_SYN_USERLABEL WHERE TENANT_ID = #{tenantId} ")
	public List<Map<String, Object>> getColumnsList(String tenantId);

}
