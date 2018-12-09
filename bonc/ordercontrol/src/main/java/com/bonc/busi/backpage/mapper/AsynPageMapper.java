package com.bonc.busi.backpage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AsynPageMapper {
	


	
	@Select("SELECT TENANT_ID,DATE_FORMAT(START_TIME,'%Y-%m-%d %H:%i:%s')AS START_TIME,BUSI_DESC,BUSI_ITEM_1 FROM PLT_COMMON_LOG WHERE LOG_TYPE IN ('00','05') "
			+ "AND START_TIME BETWEEN DATE_SUB(NOW(), INTERVAL 5 DAY) AND NOW() AND TENANT_ID = #{tenantId} ORDER BY START_TIME")
	public List<Map<String,Object>> getAsynUserLabel(@Param("tenantId") String tenantId);
	
	
	@Select("SELECT TENANT_ID,DATE_FORMAT(START_TIME,'%Y-%m-%d %H:%i:%s')AS START_TIME,BUSI_DESC,BUSI_ITEM_1 FROM PLT_COMMON_LOG WHERE LOG_TYPE IN ('00','05') "
			+ "AND START_TIME BETWEEN DATE_SUB(NOW(), INTERVAL 5 DAY) AND NOW() AND TENANT_ID = #{tenantId} ORDER BY START_TIME LIMIT ${begin},${end} ")
	public List<Map<String,Object>> getAsynUserLabelParam(@Param("tenantId") String tenantId,@Param("begin") int begin,@Param("end") int end);
	
	
	@Select("SELECT COUNT(*) FROM PLT_COMMON_LOG WHERE LOG_TYPE IN ('00','05') "
			+ "AND START_TIME BETWEEN DATE_SUB(NOW(), INTERVAL 5 DAY) AND NOW() AND TENANT_ID = #{tenantId} ORDER BY START_TIME")
	public Integer getAsynUserLabelTotal(@Param("tenantId") String tenantId);
	
	
}
