package com.bonc.busi.sys.mapper;

import org.apache.ibatis.annotations.*;
import com.bonc.busi.sys.entity.PltActivityInfo;

public interface ActivityInfoMapper {
	/**
	 * judge activity
	 * 
	 * @return Activity
	 */
	@SelectProvider(type = ActivityInfoOperation.class, method = "isActivityRun")
	String isActivityRun(PltActivityInfo at);
}
