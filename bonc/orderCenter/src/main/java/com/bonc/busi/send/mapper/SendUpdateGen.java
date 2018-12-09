package com.bonc.busi.send.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import com.bonc.busi.send.model.ChannelModel;
import com.bonc.busi.send.model.QueryRange;

public class SendUpdateGen {
	
	private static final String PLT_ORDER_INFO = "PLT_ORDER_INFO"; 
	private static final String PLT_CHANNEL_ORDER_LIST = "PLT_CHANNEL_ORDER_LIST"; 
	
	public String updateOrderDo(ChannelModel model){
    	StringBuilder whereBuilder = new StringBuilder("1=1");
    	whereBuilder.append(" and TENANT_ID='").append(model.getProvId()).append("' ");
    	whereBuilder.append(" and CHANNEL_ID='").append(model.getChannelId()).append("' ");
    	
    	//工单的状态
    	whereBuilder.append(" and ORDER_STATUS='").append(model.getChannelId()).append("' ");
    	BEGIN();
    	UPDATE(PLT_ORDER_INFO);
    	SET(" ORDER_STATUS='1' ");
        WHERE(whereBuilder.toString());
		return SQL()+" limit "+model.getPageSize();
	}
	
	public String updateDxOrder(QueryRange range){
		StringBuilder whereBuilder = new StringBuilder("1=1");
    	whereBuilder.append(" and TENANT_ID='").append(range.getProvId()).append("' ");
    	whereBuilder.append(" and CHANNEL_ID='").append(range.getChannelId()).append("' ");
    	//工单的状态
    	whereBuilder.append(" and ORDER_STATUS='0' ");
    	whereBuilder.append(" and ID>=").append(range.getStart());
    	whereBuilder.append(" and ID<=").append(range.getEnd());
		BEGIN();
    	UPDATE(PLT_CHANNEL_ORDER_LIST);
    	SET(" ORDER_STATUS='"+range.getOrderStatus()+"' ");
    	SET(" ORDER_SEND_TIME=now() ");
        WHERE(whereBuilder.toString());
		return SQL();
	}
}
