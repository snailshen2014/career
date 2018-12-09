package com.bonc.busi.interfaces.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Update;

public interface AlertWinMapper {

	@Update("UPDATE PLT_ORDER_INFO SET RESERVE1=0 WHERE TENANT_ID=#{TENANT_ID} AND CHANNEL_ID=#{CHANNEL_ID} ")
	void updateLimitNum(Map<String, Object> map);

}
