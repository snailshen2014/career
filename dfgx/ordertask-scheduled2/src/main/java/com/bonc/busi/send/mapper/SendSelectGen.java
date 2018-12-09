package com.bonc.busi.send.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;

import com.bonc.busi.send.model.ChannelModel;
import com.bonc.busi.send.model.QueryRange;
import com.bonc.busi.send.model.sms.SmsFileReq;

public class SendSelectGen {
//	private static final String SUMMARY_TABLE = "plt_activity_daily_summary";  
//	private static final String DETAIL_SUMMARY_TABLE = "plt_activity_daily_finished_detail"; 
	private static final String PLT_ORDER_INFO = "PLT_ORDER_INFO"; 
	private static final String PLT_ACTIVITY_INFO = "PLT_ACTIVITY_INFO";
//	private static final String USER_INFO = "UI_L_USER_LABEL_INFO_ALL_VIEW";
	
	private static final String PLT_CHANNEL_ORDER_LIST = "PLT_CHANNEL_ORDER_LIST"; 
	
	public String findOrderPage(QueryRange range){
    	StringBuilder whereBuilder = new StringBuilder("1=1");
    	whereBuilder.append(" and o.TENANT_ID='").append(range.getProvId()).append("' ");
    	whereBuilder.append(" and o.CHANNEL_ID='").append(range.getChannelId()).append("' ");
    	//工单的状态
    	whereBuilder.append(" and o.ORDER_STATUS='").append(range.getOrderStatus()).append("' ");
    	whereBuilder.append(" and a.REC_ID=o.ACTIVITY_SEQ_ID ");
    	//活动有效状态
    	whereBuilder.append(" and a.ACTIVITY_STATUS<>2 ");
    	
    	BEGIN();
    	SELECT(" * ");
        FROM(PLT_CHANNEL_ORDER_LIST+" o,"+PLT_ACTIVITY_INFO+" a");
        WHERE(whereBuilder.toString());
        ORDER_BY("ID");
		String LIMIT =" limit " + range.getSize();
		return SQL()+LIMIT;
	}
	
	public String getSmsList(SmsFileReq req){
		BEGIN();
		SELECT("TENANT_ID TENANT_ID,ACTIVITY_ID ACTIVITY_ID,PHONE_NUMBER TEL_PHONE,ORDER_CONTENT CONTENT");
		FROM(req.getTableName());
		WHERE(" CHANNEL_ID=#{channelId} ");
		String LIMIT = " LIMIT "+req.getSize();
		return SQL()+LIMIT;
	}
}
