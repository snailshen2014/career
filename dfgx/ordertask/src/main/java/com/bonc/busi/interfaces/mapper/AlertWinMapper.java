package com.bonc.busi.interfaces.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface AlertWinMapper {

//	@Update("UPDATE PLT_ORDER_INFO_POPWIN SET RESERVE1='0' WHERE TENANT_ID=#{TENANT_ID} AND CHANNEL_ID=#{CHANNEL_ID} AND RESERVE1 > '0' LIMIT 100000")
//	int updateLimitNum(Map<String, Object> map);

	@Update("UPDATE ${tableName} SET RESERVE1='0' WHERE TENANT_ID=#{TENANT_ID} AND CHANNEL_ID=#{CHANNEL_ID} AND RESERVE1 > '0'"
			+ " AND REC_ID>= #{min} AND REC_ID <= #{max} ")
	int updateLimitNum(Map<String, Object> map);
	
	@Select("SELECT MAX(REC_ID) max,MIN(REC_ID) min FROM ${tableName} WHERE TENANT_ID=#{TENANT_ID} AND CHANNEL_ID=#{CHANNEL_ID}  AND RESERVE1 > '0'")
	public HashMap<String, Long> getPopwinRange(Map<String, Object> map);
}
