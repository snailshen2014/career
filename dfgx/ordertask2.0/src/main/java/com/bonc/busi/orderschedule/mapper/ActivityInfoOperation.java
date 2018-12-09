package com.bonc.busi.orderschedule.mapper;

import java.util.List;


import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.ChannelWebchatInfo;
import com.bonc.busi.activity.ChannelWoWindowPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.activity.WebChatMidActivityPo;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
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

	public String InsertActivityInfo(PltActivityInfo at) {
		String s = "'";
		BEGIN();
		INSERT_INTO(PLT_ACTIVITY_INFO);
		VALUES("REC_ID", s + at.getREC_ID() + s);
		VALUES("ACTIVITY_ID", s + at.getACTIVITY_ID() + s);
		VALUES("ACTIVITY_DIVISION", s + at.getACTIVITY_DIVISION() + s);
		VALUES("ACTIVITY_NAME", s + at.getACTIVITY_NAME() + s);
		if (at.getACTIVITY_THEME() != null)
			VALUES("ACTIVITY_THEME", s + at.getACTIVITY_THEME() + s);
		VALUES("ACTIVITY_THEMEID", s + at.getACTIVITY_THEMEID() + s);
		VALUES("ACTIVITY_TYPE", s + at.getACTIVITY_TYPE() + s);

		// 活动开始日期"
		if (at.getBEGIN_DATE() != null)
			VALUES("BEGIN_DATE", s + at.getBEGIN_DATE() + s);

		if (at.getEND_DATE() != null)
			VALUES("END_DATE", s + at.getEND_DATE() + s);

		//   "state":"活动状态：8、停用，9、启用，10、未执行，11、暂停,12、暂存，13、审批中"
		VALUES("ORI_STATE", s + at.getORI_STATE() + s);
		VALUES("CREATE_NAME", s + at.getCREATE_NAME() + s);

		if (at.getCREATE_DATE() != null)
			VALUES("CREATE_DATE", s + at.getCREATE_DATE() + s);
		VALUES("ORG_RANGE", s + at.getORG_RANGE() + s);
		VALUES("TENANT_ID", s + at.getTENANT_ID() + s);
		if (at.getACTIVITY_STATUS() != null)
			VALUES("ACTIVITY_STATUS", s + Integer.toString(at.getACTIVITY_STATUS()) + s);
		VALUES("ACTIVITY_DESC", s + at.getACTIVITY_DESC() + s);
		VALUES("GROUP_ID", s + at.getGROUP_ID() + s);
		VALUES("GROUP_NAME", s + at.getGROUP_NAME() + s);
		VALUES("ACTIVITY_LEVEL", s + at.getACTIVITY_LEVEL() + s);
		VALUES("PARENT_ACTIVITY", s + at.getPARENT_ACTIVITY() + s);
		VALUES("POLICY_ID", s + at.getPOLICY_ID() + s);
		if (at.getORDER_GEN_RULE() != null)
			VALUES("ORDER_GEN_RULE", s + Integer.toString(at.getORDER_GEN_RULE()) + s);
		if (at.getORDER_LIFE_CYCLE() != null)
			VALUES("ORDER_LIFE_CYCLE", s + Integer.toString(at.getORDER_LIFE_CYCLE()) + s);
		if (at.getORDER_UPDATE_RULE() != null)
			VALUES("ORDER_UPDATE_RULE", s + Integer.toString(at.getORDER_UPDATE_RULE()) + s);
		if (at.getFILTER_BLACKUSERLIST() != null)
			VALUES("FILTER_BLACKUSERLIST", s + Integer.toString(at.getFILTER_BLACKUSERLIST()) + s);
		if (at.getFILTER_WHITEUSERLIST() != null)
			VALUES("FILTER_WHITEUSERLIST", s + Integer.toString(at.getFILTER_WHITEUSERLIST()) + s);
		if (at.getDELETE_ACTIVITY_USER() != null)
			VALUES("DELETE_ACTIVITY_USER", s + Integer.toString(at.getDELETE_ACTIVITY_USER()) + s);
		if (at.getDELETE_SUCCESSRULE_USER() != null)
			VALUES("DELETE_SUCCESSRULE_USER", s + Integer.toString(at.getDELETE_SUCCESSRULE_USER()) + s);
		VALUES("IS_SENDORDER", s + at.getIS_SENDORDER() + s);
		VALUES("ORG_LEVEL", s + at.getORG_LEVEL() + s);
		VALUES("OTHER_CHANNEL_EXERULE", s + at.getOTHER_CHANNEL_EXERULE() + s);
		VALUES("SELF_SEND_CHANNEL_RULE", s + at.getSELF_SEND_CHANNEL_RULE() + s);
		if (at.getSTRATEGY_DESC()!=null){
			String temp = at.getSTRATEGY_DESC();
			if (temp.indexOf("'") != -1) {
				temp = temp.replace("'", "\\'");
			}
			VALUES("STRATEGY_DESC", s + temp + s);
		}

		VALUES("ECHANNEL_SHOW_RULE", s + at.getECHANNEL_SHOW_RULE() + s);

		VALUES("PARENT_ACTIVITY_NAME", s + at.getPARENT_ACTIVITY_NAME() + s);
		VALUES("PARENT_ACTIVITY_STARTDATE", s + at.getPARENT_ACTIVITY_STARTDATE() + s);
		VALUES("PARENT_ACTIVITY_ENDDATE", s + at.getPARENT_ACTIVITY_ENDDATE() + s);
		VALUES("PARENT_PROVID", s + at.getPARENT_PROVID() + s);
		VALUES("CREATOR_ORGID", s + at.getCREATOR_ORGID() + s);
		VALUES("CREATOR_ORG_PATH", s + at.getCREATOR_ORG_PATH() + s);
		VALUES("USERGROUP_FILTERCON", s + at.getUSERGROUP_FILTERCON() + s);
//		VALUES("LAST_ORDER_CREATE_TIME", s + at.getLAST_ORDER_CREATE_TIME() + s);
		// 预留百分比
		if (at.getREMAIN_PERCENT() != null)
			VALUES("REMAIN_PERCENT", s + Integer.toString(at.getREMAIN_PERCENT()) + s);
		
		if(at.getREWARD_DESC() !=null){               
			VALUES("REWARD_DESC", s + at.getREWARD_DESC() + s);       //电信-奖励描述
		}
		if(at.getCHANNEL_SYNERGISM() != null){
			VALUES("CHANNEL_SYNERGISM", s + at.getCHANNEL_SYNERGISM() + s); //电信-渠道协同
		}
		String sql = SQL();
		logger.info("Activity record sql:" + sql);
		return sql;
	}
	/**
	 * generate Frontline channel record
	 * 
	 * @return
	 */
	public String InsertChannelFrontline(FrontlineChannelPo frontline) {
		String s = "'";
		BEGIN();
		INSERT_INTO(CHANNEL_DETAIL);
		VALUES("CHANN_ID", s + frontline.getChannelId() + s);
		VALUES("IS_SEND_SMS", s + frontline.getIsSendSMS() + s);
		if (frontline.getMarketingWords() != null) {
			String tmp = frontline.getMarketingWords();
			if (tmp.indexOf("'") != -1)
				tmp = tmp.replace("'", " ");

			VALUES("MARKET_WORDS", s + tmp + s);
		}
		VALUES("ORDERISSUEDRULE", s + frontline.getOrderIssuedRule() + s);
		VALUES("SMS_WORDS", s + frontline.getSmsWords() + s);
		VALUES("TOUCHLIMITDAY", s + frontline.getTouchLimitDay() + s);
		VALUES("TENANT_ID", s + frontline.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + frontline.getActivityId() + s);
		List<ChannelSpecialFilterPo> filter_list = frontline.getChannelSpecialFilterList();
		if (filter_list != null) {
			String jsonString = JSON.toJSONString(filter_list);
			jsonString = jsonString.replace("'", "\\'");
			System.out.println("InsertChannelFrontline:json string=" + jsonString);
			VALUES("CHANNEL_SPECIALFILTER_LIST", s + jsonString + s);
		}
		VALUES("ACTIVITY_SEQ_ID", s + frontline.getACTIVITY_SEQ_ID() + s);
		String sql = SQL();
		logger.info("InsertChannelFrontline sql:" + sql);
		return sql;
	}

	public String InsertChannelWebchatInfo(ChannelWebchatInfo chat) {
		String s = "'";
		BEGIN();
		INSERT_INTO(CHANNEL_DETAIL);
		VALUES("CHANN_ID", s + chat.getChannelId() + s);
		VALUES("IMAGE_URL", s + chat.getChannelWebchatImgurl() + s);
		// v0.6 deleted,add WebChatMidActivityPo
		// VALUES("MODEL_ID",s + chat.getChannelWebchatModelId() + s);
		VALUES("TITLE", s + chat.getChannelWebchatTitle() + s);
		VALUES("URL", s + chat.getChannelWebchatUrl() + s);
		VALUES("TENANT_ID", s + chat.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + chat.getActivityId() + s);

		VALUES("FILTER_CON", s + chat.getFilterCondition() + s);
		VALUES("ACTIVITY_SEQ_ID", s + chat.getACTIVITY_SEQ_ID() + s);
		String sql_con = chat.getFilterConditionSql();
		if (sql_con != null) {
			if (sql_con.indexOf("'") != -1)
				sql_con = sql_con.replace("'", "\\'");
			VALUES("FILTER_SQL", s + sql_con + s);
		}

		WebChatMidActivityPo wechat_info = chat.getWebChatMidActivityPo();
		if (wechat_info != null) {
			String jsonString = JSON.toJSONString(wechat_info);
			System.out.println("Wechat info,json string=" + jsonString);
			VALUES("WECHAT_INFO", s + jsonString + s);
			VALUES("WECHAT_STATUS", s + wechat_info.getState() + s);
		}

		String sql = SQL();
		logger.info("InsertChannelWebchatInfo record sql:" + sql);
		return sql;
	}
	
	public String InsertChannelWebWoWindow(ChannelWoWindowPo wo) {
		String s = "'";
		BEGIN();
		INSERT_INTO(CHANNEL_DETAIL);
		VALUES("CHANN_ID", s + wo.getChannelId() + s);
		VALUES("CONTENT", s + wo.getChannelWowindowContent() + s);
		VALUES("IMGSIZE", s + wo.getChannelWowindowImgsize() + s);
		VALUES("IMAGE_URL", s + wo.getChannelWowindowImgurl() + s);

		VALUES("TITLE", s + wo.getChannelWowindowTitle() + s);
		VALUES("URL", s + wo.getChannelWowindowUrl() + s);
		VALUES("TENANT_ID", s + wo.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + wo.getActivityId() + s);

		VALUES("FILTER_CON", s + wo.getFilterCondition() + s);
		VALUES("ACTIVITY_SEQ_ID", s + wo.getACTIVITY_SEQ_ID() + s);
		String sql_con = wo.getFilterConditionSql();
		if (sql_con != null) {
			if (sql_con.indexOf("'") != -1)
				sql_con = sql_con.replace("'", "\\'");
			VALUES("FILTER_SQL", s + sql_con + s);
		}

		String sql = SQL();
		logger.info("InsertChannelWebWoWindow record sql:" + sql);
		return sql;
	}

	public String cleanActivityInfo(PltActivityInfo activity) {
		String s = "'";
		BEGIN();
		DELETE_FROM(PLT_ACTIVITY_INFO);
		WHERE("TENANT_ID=" + s + activity.getTENANT_ID() + s + " and REC_ID=" + activity.getREC_ID());
		return SQL();
	}
}
