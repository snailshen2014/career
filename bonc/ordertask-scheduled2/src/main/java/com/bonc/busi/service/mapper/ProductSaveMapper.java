package com.bonc.busi.service.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface ProductSaveMapper {

	
	//获取需要处理受理成功的用户
	@Select("SELECT ACTIVITY_ID activityId,USER_ID userId,TENANT_ID tenantId,PRODUCT_ID productId,DATE_FORMAT(OPERATEDATE,'%Y%m%d') DATE_ID FROM PLT_ORDER_PRODUCT_SAVE WHERE TENANT_ID=#{TenantId}  AND RESERVE1 = '1'  AND (RESERVE2 <> '1' OR RESERVE2 IS NULL)")
	List<HashMap<String, Object>> getUserList(String TenantId);
	
	//获取相关活动的表名
	@Select("  SELECT TABLE_NAME,ACTIVITY_SEQ_ID FROM PLT_ORDER_TABLES_ASSIGN_RECORD_INFO WHERE BUSI_TYPE = '0' AND ACTIVITY_ID IN (${activityIds}) AND TENANT_ID=#{tenantId} "
			+" GROUP BY TABLE_NAME,ACTIVITY_SEQ_ID")
	List<HashMap<String, Object>> getTableNames(HashMap<String, Object> user);
	
	
	//获取临时表工单数据
	@Select("${sql}SELECT REC_ID orderRecId,USER_ID userId,DATE_ID dateId,PRODUCT_ID productId,ACTIVITY_SEQ_ID activitySeqId,TABLE_NAME tableName "
			+ " FROM PLT_ORDER_PRODUCT_SAVE_MEM WHERE TENANT_ID=#{TenantId}  AND REC_ID  > ${curOrderRecId} AND NEW_TIME = #{newDate} "
			+ " ORDER BY REC_ID LIMIT ${avgCount}")
	List<HashMap<String, Object>> getOrderInfo(@Param("sql")String sql,@Param("TenantId")String TenantId,@Param("curOrderRecId")long curOrderRecId,@Param("newDate")String newDate,@Param("avgCount")Integer avgCount);

	//更新受理表数据更新标识
	@Update(" UPDATE PLT_ORDER_PRODUCT_SAVE SET RESERVE2='1' WHERE USER_ID IN (${userIds})")
	void updateProductSave(@Param("userIds")String userIds);

	

	
	
}
