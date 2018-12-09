package com.bonc.busi.interfaces.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.util.HashMap;

public class ContactProvider {
	
	/**
	 * 客户经理接触操作影响统计信息
	 * @param map
	 * @return
	 */
	public String contactStatistc(HashMap<String, Object> map){
		BEGIN();
		UPDATE(" PLT_ORDER_STATISTIC ");
		if((Boolean)map.get("isFirst")){
			if("2".equals(map.get("THIS_CHANNEL_STATUS"))){
				SET("VISIT_NUMS_TODAY=VISIT_NUMS_TODAY+1 ");
				SET("VISIT_NUMS_TOTAL=VISIT_NUMS_TOTAL+1 ");
				SET("VISITED_NO_SUCCESS=VISITED_NO_SUCCESS+1 ");
			}
			SET(map.get("lastCode")+"="+map.get("lastCode")+"-1");
		}else{
			if("2".equals(map.get("THIS_CHANNEL_STATUS"))&&!"2".equals(map.get("LAST_CHANNEL_STATUS"))){
				SET("VISIT_NUMS_TODAY=VISIT_NUMS_TODAY+1 ");
				SET("VISIT_NUMS_TOTAL=VISIT_NUMS_TOTAL+1 ");
				SET("VISITED_NO_SUCCESS=VISITED_NO_SUCCESS+1 ");
			}
			if(!"2".equals(map.get("THIS_CHANNEL_STATUS"))&&"2".equals(map.get("LAST_CHANNEL_STATUS"))){
				if("1".equals(map.get("TODAY_CONTACT"))){
					SET("VISIT_NUMS_TODAY=VISIT_NUMS_TODAY-1 ");
				}
				SET("VISIT_NUMS_TOTAL=VISIT_NUMS_TOTAL-1 ");
				SET("VISITED_NO_SUCCESS=VISITED_NO_SUCCESS-1 ");
			}
			SET(map.get("lastType")+"="+map.get("lastType")+"-1");
			SET(map.get("lastCode")+"="+map.get("lastCode")+"-1");
		}
		SET(map.get("thisType")+"="+map.get("thisType")+"+1");
		SET(map.get("thisCode")+"="+map.get("thisCode")+"+1");
		//TODO 看看多ＷＨＥＲＥ会不会自动加ＡＮＤ
		WHERE("TENANT_ID=#{tenantId} ");
		WHERE("ORG_PATH=#{orderOrgPath} ");
		WHERE("LOGIN_ID=#{orderLoginId} ");
		WHERE("ACTIVITY_SEQ_ID=#{activityId} ");
		return SQL();
	}
	
	/**
	 * 客户经理接触操作影响统计信息
	 * @param map
	 * @return
	 */
	public String updateActivityProc(HashMap<String, Object> map){
		BEGIN();
		UPDATE(" PLT_ORDER_STATISTIC_SEND ");
		if("2".equals(map.get("CHANNEL_STATUS"))&&!"2".equals(map.get("LAST_CHANNEL_STATUS"))){
			SET("SEND_SUC_NUM=SEND_SUC_NUM+1" );
		}
		if(!"2".equals(map.get("CHANNEL_STATUS"))&&"2".equals(map.get("LAST_CHANNEL_STATUS"))){
			SET("SEND_SUC_NUM=SEND_SUC_NUM-1" );
		}
		SET("SEND_SUC_NUM=SEND_SUC_NUM ");
		WHERE(" TENANT_ID=#{tenantId} ");
		WHERE(" CHANNEL_ID=#{channelId}");
		WHERE(" ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} ");
		return SQL();
	}
}
