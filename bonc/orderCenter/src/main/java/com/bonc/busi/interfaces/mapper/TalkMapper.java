package com.bonc.busi.interfaces.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Select;

public interface TalkMapper {

	@Select("SELECT ${extInfo} u.* FROM PLT_USER_LABEL u WHERE u.PARTITION_FLAG=#{partFlag} AND u.USER_ID=#{userId} AND u.TENANT_ID=#{tenantId} LIMIT 1")
	HashMap<String, Object> getUserInfo(HashMap<String, String> req);

	@Select("SELECT ${extInfo} u.* FROM PLT_USER_LABEL u WHERE u.PARTITION_FLAG=#{partFlag} AND u.DEVICE_NUMBER=#{phoneNumber} AND u.USER_TYPE=#{dataType} AND u.TENANT_ID=#{tenantId} LIMIT 1")
	HashMap<String, Object> getUserInfoByPhone(HashMap<String, String> req);
	
}
