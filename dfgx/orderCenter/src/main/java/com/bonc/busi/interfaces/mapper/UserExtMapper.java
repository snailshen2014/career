package com.bonc.busi.interfaces.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserExtMapper {

	@Select("SELECT ${fields} TENANT_ID FROM PLT_USER_EXT_INFO WHERE TENANT_ID=#{tenantId} AND USER_ID=#{userId}")
	List<HashMap<String, Object>> getUserExtInfo(@Param("tenantId")String tenantId,@Param("fields") String fields,@Param("userId")Object userId);
	
	@Insert("INSERT INTO PLT_USER_EXT_INFO (TENANT_ID,PROV_ID,AREA_NO,USER_ID,SERVICE_TYPE,CUST_NAME,SEX,TELPHONE,TELPHONE_RESERVE,ADDRESS,ORG_PATH,REMARK,RESERVE1,RESERVE2,RESERVE3) "
			+ " VALUES (#{tenantId},#{provId},#{areaNo},#{userId},#{serviceType},#{custName},#{sex},#{telphone},#{telphoneReserve},#{address},#{orgPath},#{remark},#{ext1},#{ext2},#{ext3})")
	Integer addUserExtInfo(HashMap<String, Object> map);
	
	@Update("UPDATE PLT_USER_EXT_INFO SET CUST_NAME=#{custName},SEX=#{sex},TELPHONE=#{telphone},TELPHONE_RESERVE=#{telphoneReserve},ADDRESS=#{address}, "
			+ " REMARK=#{remark},RESERVE1=#{ext1},RESERVE2=#{ext2},RESERVE3=#{ext3},ORG_PATH=#{orgPath} WHERE TENANT_ID=#{tenantId} AND USER_ID=#{userId} ")
	Integer updateUserExtInfo(HashMap<String, Object> map);

	@Select("SELECT TENANT_ID tenantId,PROV_ID provId,AREA_ID areaNo,USER_TYPE serviceType FROM PLT_USER_LABEL WHERE PARTITION_FLAG=#{partFlag} AND USER_ID=#{userId} AND TENANT_ID=#{tenantId}")
	List<HashMap<String, Object>> getUserInfo(HashMap<String, Object> map);
}
