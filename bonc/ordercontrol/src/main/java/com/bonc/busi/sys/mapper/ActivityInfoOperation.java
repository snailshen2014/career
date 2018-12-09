package com.bonc.busi.sys.mapper;

import java.util.List;


import com.alibaba.fastjson.JSON;
import com.bonc.busi.sys.entity.PltActivityInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class ActivityInfoOperation {

	private static final Logger logger = LoggerFactory.getLogger(ActivityInfoOperation.class);

	// Activity table
	private static final String PLT_ACTIVITY_INFO = "PLT_ACTIVITY_INFO";
	// channel frontline
	private static final String CHANNEL_DETAIL = "PLT_ACTIVITY_CHANNEL_DETAIL";

	/**
	 * judge activity
	 * 
	 * @return Activity
	 */
	public String isActivityRun(PltActivityInfo at) {
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder(" ACTIVITY_ID= ");
		whereBuilder.append(s + at.getACTIVITY_ID() + s);
		whereBuilder.append(" and TENANT_ID = " + s + at.getTENANT_ID() + s);
		whereBuilder.append(" and ACTIVITY_STATUS = 1" );
		BEGIN();
		SELECT("DATE_FORMAT(max(LAST_ORDER_CREATE_TIME),'%Y%m%d')");
		FROM(PLT_ACTIVITY_INFO);
		WHERE(whereBuilder.toString());
		return SQL();
	}
}
