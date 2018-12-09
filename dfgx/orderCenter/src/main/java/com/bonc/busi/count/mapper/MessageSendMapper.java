package com.bonc.busi.count.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.bonc.busi.count.model.SendCount;

public interface MessageSendMapper {
	@SelectProvider(method="activityCount",type=MessageSelect.class)
	public List<SendCount> findCountList(SendCount sendCount);
	
	@SelectProvider(method="activityCountByCondition",type=MessageSelect.class)
	public String findCountTotal(SendCount sendCount);

}
