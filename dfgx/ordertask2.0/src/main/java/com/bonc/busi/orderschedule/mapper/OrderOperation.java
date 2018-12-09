package com.bonc.busi.orderschedule.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.DELETE_FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.GROUP_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ChannelGroupPopupPo;
import com.bonc.busi.activity.ChannelHandOfficePo;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.ChannelWebOfficePo;
import com.bonc.busi.activity.ChannelWebchatInfo;
import com.bonc.busi.activity.ChannelWoWindowPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.activity.MsmChannelPo;
import com.bonc.busi.activity.SuccessProductPo;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.activity.WebChatMidActivityPo;
import com.bonc.busi.orderschedule.bo.ActivityChannelStatus;
import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.bo.BlackWhiteUserList;
import com.bonc.busi.orderschedule.bo.Order;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.orderschedule.bo.ParamMap;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.routing.OrderTableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderOperation {
	private static final Logger logger = LoggerFactory.getLogger(OrderOperation.class);

	// Activity table
	private static final String PLT_ACTIVITY_INFO = "PLT_ACTIVITY_INFO";
	// white balce user
	private static final String CLYX_ACTIVITY_FILTE_USERS = "CLYX_ACTIVITY_FILTE_USERS";
	// user group
	private static final String USERGROUP_CONFIG = "user_tool_weights_conditions";
	// channel frontline
	private static final String CHANNEL_DETAIL = "PLT_ACTIVITY_CHANNEL_DETAIL";

	// PLT_ORDER_INFO
	private static final String ORDER_INFO = "PLT_ORDER_INFO";
	private static final String ORDER_SMS_INFO = "PLT_ORDER_INFO_SMS";
	
	private static final String TEMP_TABLE = "PLT_ORDER_INFO_TEMP";

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
		VALUES("STRATEGY_DESC", s + at.getSTRATEGY_DESC() + s);
		VALUES("ECHANNEL_SHOW_RULE", s + at.getECHANNEL_SHOW_RULE() + s);

		VALUES("PARENT_ACTIVITY_NAME", s + at.getPARENT_ACTIVITY_NAME() + s);
		VALUES("PARENT_ACTIVITY_STARTDATE", s + at.getPARENT_ACTIVITY_STARTDATE() + s);
		VALUES("PARENT_ACTIVITY_ENDDATE", s + at.getPARENT_ACTIVITY_ENDDATE() + s);
		VALUES("PARENT_PROVID", s + at.getPARENT_PROVID() + s);
		VALUES("CREATOR_ORGID", s + at.getCREATOR_ORGID() + s);
		VALUES("CREATOR_ORG_PATH", s + at.getCREATOR_ORG_PATH() + s);
		VALUES("USERGROUP_FILTERCON", s + at.getUSERGROUP_FILTERCON() + s);
		VALUES("LAST_ORDER_CREATE_TIME", s + at.getLAST_ORDER_CREATE_TIME() + s);
		// 预留百分比
		if (at.getREMAIN_PERCENT() != null)
			VALUES("REMAIN_PERCENT", s + Integer.toString(at.getREMAIN_PERCENT()) + s);
		String sql = SQL();
		logger.info("Activity record sql:" + sql);
		return sql;
	}

	public String SelectXcloud() {
		// String sql = "SELECT count(*) FROM UI_L_USER_LABEL_INFO_ALL_ORG";

		BEGIN();
		SELECT(" MONTH_ID");
		FROM("UI_L_USER_LABEL_INFO_ALL_ORG");
		// WHERE(whereBuilder.toString());
		// ORDER_BY(" ");//根据活动优先级排序 高在前
		// ORDER_BY(" ");//根据地域级别排序 地域级别高在前
		return SQL();

		// System.out.println("ddddd");

	}

	public String SelectOracle() {
		// String sql = "SELECT count(*) FROM UI_L_USER_LABEL_INFO_ALL_ORG";
		BEGIN();
		SELECT("CI_WA_NAME");

		FROM("user_tool_weights_conditions");

		// WHERE(“ROWNUM < 2");
		// ORDER_BY(" ");//根据活动优先级排序 高在前
		// ORDER_BY(" ");//根据地域级别排序 地域级别高在前
		return SQL();

		// System.out.println("ddddd");

	}

	/**
	 * judge black ,white user list
	 * 
	 * @param user's
	 *            id
	 * @param filter
	 *            type 01:blacklist,02:whitelist
	 * @return user's id
	 */
	public String isBlackWhiteUser(BlackWhiteUserList user) {
		String quo = "'";
		StringBuilder whereBuilder = new StringBuilder(" USER_ID= ");
		whereBuilder.append(quo + user.getUSER_ID() + quo);
		whereBuilder.append("and FILTE_TYPE = " + quo + user.getFILTE_TYPE() + quo);
		BEGIN();
		SELECT("USER_ID");
		FROM(CLYX_ACTIVITY_FILTE_USERS);
		// FILTE_TYPE=01 blacklist，=02 white user list
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/**
	 * get user group infomation from oracle by user group id.
	 * 
	 * @param user
	 *            group id
	 * @return 0:success;-1 error
	 */
	public String getUserGroupInfo(String id) {
		/*
		 * String quo = "'"; StringBuilder fieldBuilder = new StringBuilder(
		 * "a.CI_WA_ID,to_char(a.CONDITIONS) as CONDITIONS,to_char(a.SQL) as SQL ,a.CI_WA_CREATERNAME,a.CI_WA_NAME"
		 * ); fieldBuilder.append(
		 * ",a.CI_WA_CREATEDATE,a.CI_WA_DESC,a.CI_WA_MAX_MONTH_ID,a.CI_WA_DESCONLY,a.CI_WA_MULTICONDITION,a.CI_WA_PHOTOCONDITION"
		 * ); fieldBuilder.append(
		 * ",a.CI_WA_PARENTID,a.FILTER_DIMEID,a.FILTER_TABLE,a.USER_COUNT,a.IMPORT_TABLE,a.CREATE_TYPE"
		 * ); StringBuilder whereBuilder = new StringBuilder(" a.CI_WA_ID = ");
		 * whereBuilder.append(quo + id + quo); whereBuilder.append(
		 * " and a.CI_WA_MAX_MONTH_ID =  ( select max(b.CI_WA_MAX_MONTH_ID)  ");
		 * whereBuilder.append( " from " + USERGROUP_CONFIG +
		 * "  b where a.CI_WA_ID = b.CI_WA_ID) and rownum<2");
		 */
		String quo = "'";
		StringBuilder fieldBuilder = new StringBuilder("to_char(a.SQL) as SQL ");
		StringBuilder whereBuilder = new StringBuilder(" a.CI_WA_ID = ");
		whereBuilder.append(quo + id + quo);
		whereBuilder.append(" and a.CI_WA_MAX_MONTH_ID =  ( select max(b.CI_WA_MAX_MONTH_ID)  ");
		whereBuilder.append(" from " + USERGROUP_CONFIG + "  b where a.CI_WA_ID = b.CI_WA_ID) and rownum<2");

		BEGIN();
		SELECT(fieldBuilder.toString());
		FROM(USERGROUP_CONFIG + "  a");
		// FILTE_TYPE=01 blacklist，=02 white user list
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/**
	 * load data to mysql from local dir
	 * 
	 * @param sql
	 * @return
	 */
	public String loadDataToMysql(String sql) {
		return sql;
	}

	/**
	 * generate Frontline channel record
	 * 
	 * @param PltActivityChannelFrontline
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

	/**
	 * judge activity
	 * 
	 * @param Activity
	 * @return Activity
	 */
	public String isActivityRun(PltActivityInfo at) {
		String quo = "'";
		StringBuilder whereBuilder = new StringBuilder(" ACTIVITY_ID= ");
		whereBuilder.append(quo + at.getACTIVITY_ID() + quo);
		whereBuilder.append(" and TENANT_ID = " + quo + at.getTENANT_ID() + quo);
		BEGIN();
		// SELECT("DATE_FORMAT(max(CREATE_DATE),'%Y%m%d')");
		SELECT("DATE_FORMAT(max(LAST_ORDER_CREATE_TIME),'%Y%m%d')");
		FROM(PLT_ACTIVITY_INFO);
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/**
	 * get REC_ID from PLT_ACTIVITY_INFO by tenant_id ,activity_id
	 * 
	 * @param Activity
	 * @return REC_ID
	 */
	public String getActivityRecid(PltActivityInfo at) {
		// select cast(REC_ID as char) as seq from henan0.PLT_ACTIVITY_INFO
		// where ACTIVITY_ID = '16445' and TENANT_ID = 'uni076';

		String quo = "'";
		StringBuilder whereBuilder = new StringBuilder(" ACTIVITY_ID= ");
		whereBuilder.append(quo + at.getACTIVITY_ID() + quo);
		whereBuilder.append(" and TENANT_ID = " + quo + at.getTENANT_ID() + quo);
		BEGIN();
		SELECT("max(REC_ID) ");
		FROM(PLT_ACTIVITY_INFO);
		WHERE(whereBuilder.toString());
		return SQL();
	}

	public String InsertChannelHandOffice(ChannelHandOfficePo hand) {
		String s = "'";
		BEGIN();
		INSERT_INTO(CHANNEL_DETAIL);
		VALUES("CONTENT", s + hand.getChannelHandofficeContent() + s);
		VALUES("TITLE", s + hand.getChannelHandofficeTitle() + s);
		VALUES("URL", s + hand.getChannelHandofficeUrl() + s);
		VALUES("TENANT_ID", s + hand.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + hand.getActivityId() + s);
		VALUES("CHANN_ID", s + hand.getChannelId() + s);
		VALUES("FILTER_CON", s + hand.getFilterCondition() + s);
		VALUES("ACTIVITY_SEQ_ID", s + hand.getACTIVITY_SEQ_ID() + s);
		String sql_con = hand.getFilterConditionSql();
		if (sql_con != null) {
			if (sql_con.indexOf("'") != -1)
				sql_con = sql_con.replace("'", "\\'");
			VALUES("FILTER_SQL", s + sql_con + s);
		}

		String sql = SQL();
		logger.info("InsertChannelHandOffice record sql:" + sql);
		return sql;
	}

	public String InsertChannelWebOffice(ChannelWebOfficePo web) {
		String s = "'";
		BEGIN();
		INSERT_INTO(CHANNEL_DETAIL);
		VALUES("CHANN_ID", s + web.getChannelId() + s);
		VALUES("CONTENT", s + web.getChannelWebofficeContent() + s);
		VALUES("TITLE", s + web.getChannelWebofficeTitle() + s);
		VALUES("URL", s + web.getChannelWebofficeUrl() + s);
		VALUES("TENANT_ID", s + web.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + web.getActivityId() + s);

		VALUES("FILTER_CON", s + web.getFilterCondition() + s);
		VALUES("ACTIVITY_SEQ_ID", s + web.getACTIVITY_SEQ_ID() + s);
		String sql_con = web.getFilterConditionSql();
		if (sql_con != null) {
			if (sql_con.indexOf("'") != -1)
				sql_con = sql_con.replace("'", "\\'");
			VALUES("FILTER_SQL", s + sql_con + s);
		}

		String sql = SQL();
		logger.info("InsertChannelWebOffice record sql:" + sql);
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

	public String InsertChannelMsm(MsmChannelPo msm) {
		String s = "'";
		BEGIN();
		INSERT_INTO(CHANNEL_DETAIL);
		VALUES("CHANN_ID", s + msm.getChannelId() + s);
		if (msm.getCycleTimes() != null)
			VALUES("TIMES", s + Integer.parseInt(msm.getCycleTimes()) + s);
		VALUES("FILTER_CON", s + msm.getFilterCondition() + s);
		VALUES("ACTIVITY_SEQ_ID", s + msm.getACTIVITY_SEQ_ID() + s);
		String sql_con = msm.getFilterConditionSql();
		if (sql_con != null) {
			if (sql_con.indexOf("'") != -1)
				sql_con = sql_con.replace("'", "\\'");
			VALUES("FILTER_SQL", s + sql_con + s);
		}

		if (msm.getIntervalHours() != null)
			VALUES("INTERVAL_HOUR", s + Integer.parseInt(msm.getIntervalHours()) + s);
		VALUES("NOSEND_TIME", s + msm.getNoSendTime() + s);

		VALUES("END_TIME", s + msm.getSendEndTime() + s);
		VALUES("START_TIME", s + msm.getSendStartTime() + s);
		VALUES("SEND_LEVEL", s + msm.getSendLevel() + s);
		VALUES("TENANT_ID", s + msm.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + msm.getActivityId() + s);
		VALUES("TOUCHLIMITDAY", s + msm.getTouchLimitDay() + s);
		// sms content
		VALUES("CONTENT", s + msm.getSmsContent() + s);

		// sms send time
		VALUES("RESERVE1", s + msm.getMessageSendTime() + s);
		String sql = SQL();
		logger.info("InsertChannelMsm record sql:" + sql);
		return sql;
	}

	public String InsertSuccessStandardPo(SuccessStandardPo success) {
		String s = "'";
		BEGIN();
		INSERT_INTO("PLT_ACTIVITY_SUCESS_CFG");
		VALUES("TENANT_ID", s + success.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + success.getActivityId() + s);
		VALUES("MATCHINGTYPE", s + success.getMatchingType() + s);
		String suc_con = success.getSuccessCondition();
		if (suc_con != null){
			if (suc_con.indexOf("'") != -1);
			suc_con = suc_con.replace("'", "\\'");
			VALUES("SUCESSCONDITIONE", s + suc_con + s);
		}
		String sql_con = success.getSuccessConditionSQL();
		if (sql_con != null) {
			if (sql_con.indexOf("'") != -1)
				sql_con = sql_con.replace("'", "\\'");
			VALUES("SUCESSCONDITIONSQL", s + sql_con + s);
		}
		VALUES("SUCESSNAME", s + success.getSuccessName() + s);

		VALUES("SUCESSPOINTS", s + success.getSuccessPoint() + s);
		VALUES("SUCESSREWARD", s + success.getSuccessReward() + s);
		VALUES("SUCESSTYPE", s + success.getSuccessType() + s);
		VALUES("ACTIVITY_SEQ_ID", s + success.getActivity_seq_id() + s);
		String successTypeSql = success.getSuccessTypeConditionSql();
		if (successTypeSql != null) {
			if (successTypeSql.indexOf("'") != -1)
				successTypeSql = successTypeSql.replace("'", "\\'");
			VALUES("SUCCESS_TYPE_CON_SQL", s + successTypeSql + s);
		}

		String sql = SQL();
//		logger.info("InsertSuccessStandardPo record sql:" + sql);
		return sql;

	}

	public String InsertProduct(SuccessProductPo product) {
		String s = "'";
		BEGIN();
		INSERT_INTO("PLT_ACTIVITY_PRODUCT_LIST");
		VALUES("TENANT_ID", s + product.getTenantId() + s);
		VALUES("ACTIVITY_ID", s + product.getActivityId() + s);
		VALUES("PRODUCTCODE", s + product.getProductCode() + s);

		VALUES("PRODUCTNAME", s + product.getProductName() + s);
		VALUES("PRODUCTDES", s + product.getProductDes() + s);
		VALUES("ISVALID", s + product.getIsvalid() + s);
		VALUES("ORD", s + product.getOrd() + s);

		VALUES("PRODUCTDISTRICT", s + product.getProductDistrict() + s);
		VALUES("ACTIVITY_SEQ_ID", s + product.getActivity_seq_id() + s);
		String sql = SQL();
//		logger.info("InsertProduct record sql:" + sql);
		return sql;
	}

	public String updateActivityStatus(PltActivityInfo at) {
		String s = "'";
		StringBuilder where = new StringBuilder();
		where.append("TENANT_ID=" + s + at.getTENANT_ID() + s);
		where.append(" and REC_ID=" + at.getREC_ID());

		BEGIN();
		UPDATE(PLT_ACTIVITY_INFO);
		// order already generate
		SET("ACTIVITY_STATUS = \'1\'");
		SET("LAST_ORDER_CREATE_TIME=" + s + at.getLAST_ORDER_CREATE_TIME() + s);
		SET("ORDER_BEGIN_DATE=" + s + at.getORDER_BEGIN_DATE() + s);
		SET("ORDER_END_DATE=" + s + at.getORDER_END_DATE() + s);
		WHERE(where.toString());
		String sql = SQL();
		logger.info("工单生成结束 生成工单sql:" + sql);
		return sql;

	}

	public String updateOrderFinishedStatus(Order orderinfo) {
		String s = "'";
		StringBuilder where = new StringBuilder();
		where.append("ACTIVITY_SEQ_ID=" + orderinfo.getACTIVITY_SEQ_ID());
		where.append(" and TENANT_ID=" + s + orderinfo.geTENANT_ID() + s);
		where.append(" and CHANNEL_ID=" + s + orderinfo.getCHANNEL_ID() + s);
		where.append(" and ORDER_STATUS =" + s + 0 + s);

		String channelId = orderinfo.getCHANNEL_ID();
		String table = getTableByChannelId(channelId);

		BEGIN();

		UPDATE(table);
		// order already generate
		SET("ORDER_STATUS = \'5\'");
		WHERE(where.toString());
		return SQL() + " LIMIT " + orderinfo.geRETRY_TIMES();
	}

	public String updateOrderSmsFinishedStatus(PltActivityInfo at) {
		String s = "'";
		StringBuilder where = new StringBuilder();
		where.append("ACTIVITY_SEQ_ID=" + s + at.getREC_ID() + s);
		where.append(" and TENANT_ID=" + s + at.getTENANT_ID() + s);
		where.append(" and ORDER_STATUS =" + s + 0 + s);
		BEGIN();
		UPDATE(ORDER_SMS_INFO);
		// order already generate
		SET("ORDER_STATUS = \'5\'");
		WHERE(where.toString());
		return SQL();
	}

	/**
	 * get max data id from xcloud
	 * 
	 * @param
	 * @return
	 */

	public String getMaxDataIdFromXcloud(String sql) {
		return sql;
	}

	public String InsertGroupPop(ChannelGroupPopupPo pop) {
		String s = "'";
		BEGIN();
		INSERT_INTO(CHANNEL_DETAIL);
		VALUES("BUSINESS_HALL_ID", s + pop.getBusinessHall() + s);
		VALUES("BUSINESS_HALL_NAME", s + pop.getBusinessHallName() + s);
		VALUES("CHANN_ID", s + pop.getChannelId() + s);
		VALUES("TENANT_ID", s + pop.getTenant_id() + s);
		VALUES("ACTIVITY_ID", s + pop.getActivity_Id() + s);
		VALUES("CONTENT", s + pop.getContent() + s);

		VALUES("FILTER_CON", s + pop.getFilterCondition() + s);
		VALUES("ACTIVITY_SEQ_ID", s + pop.getACTIVITY_SEQ_ID() + s);
		String sql_con = pop.getFilterConditionSql();
		if (sql_con != null) {
			if (sql_con.indexOf("'") != -1)
				sql_con = sql_con.replace("'", "\\'");
			VALUES("FILTER_SQL", s + sql_con + s);
		}

		VALUES("NUMBERLIMIT", s + pop.getNumberLimit() + s);
		VALUES("TARGET", s + pop.getTarget() + s);
		String sql = SQL();
		logger.info("InsertGroupPop record sql:" + sql);
		return sql;
	}

	// 得到工单已生成状态的活动ID
	public String getActivityIdByStatus() {

		BEGIN();
		SELECT("ACTIVITY_ID ");
		FROM(PLT_ACTIVITY_INFO);
		WHERE(" ACTIVITY_STATUS = 2 AND TENANT_ID= 'uni076' ");
		// System.out.println(SQL());
		return SQL();
	}

	// 根据REC_ID查询PLT_ORDER_INFO各个字段的值
	public String selectPltOrderInfo(Integer at) {
		BEGIN();
		SELECT("ORDER_ID,USER_ID,ACTIVITY_SEQ_ID,PHONE_NUMBER,ORDER_STATUS,TENANT_ID");
		FROM(ORDER_INFO);
		WHERE("ACTIVITY_SEQ_ID = " + at + " AND ORDER_STATUS ='0' AND TENANT_ID='uni076'");
		return SQL();
	}

	// SELECT REC_ID,ORDER_UPDATE_RULE FROM PLT_ACTIVITY_INFO WHERE
	// ORDER_GEN_RULE=1 OR ORDER_GEN_RULE=0;

	/*
	 * public String selectUpdateRuleByActivity() { BEGIN();
	 * SELECT("REC_ID,ORDER_UPDATE_RULE"); FROM(" PLT_ACTIVITY_INFO "); WHERE(
	 * " (ORDER_GEN_RULE=1 OR 0) AND ACTIVITY_STATUS!=2 AND TENANT_ID='uni076'"
	 * ); return SQL(); }
	 */
	// ORDER_GEN_RULE 工单生成规则 0:按月 1：按日 2：一次性
	// (是否在活动的筛选条件中待定)ORI_STATE 活动状态：8、停用，9、启用，10、未执行，11、暂停,12、暂存，13、审批中"
	// ACTIVITY_STATUS 0:活动初始状态 1: 工单已生成 2：活动失效 3. 工单生成中 8，9：工单挂起，恢复

	// 以下无用

	// 优化后代码
	public String selectUserIDFromOrder(Integer recId) {
		BEGIN();
		StringBuilder sb = new StringBuilder();
		sb.append(
				"REC_ID,ACTIVITY_SEQ_ID,BATCH_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID,USER_ID,PHONE_NUMBER,CONTACT_DATE,CONTACT_TYPE,CONTACT_CODE,ORG_PATH,MANUAL_PATH,USER_PATH");
		sb.append(
				",EXE_PATH,ORDER_STATUS,CHANNEL_STATUS,DEAL_MONTH,SERVICE_TYPE,BEGIN_DATE,END_DATE,LAST_UPDATE_TIME,CITYID,AREAID,USER_ORG_ID,INPUT_DATE,INVALID_DATE");
		SELECT(sb.toString());
		FROM(" PLT_ORDER_INFO ");
		WHERE(" ACTIVITY_SEQ_ID= " + recId + " AND  ORDER_STATUS ='0' AND TENANT_ID='uni076'");
		ORDER_BY(" BEGIN_DATE DESC");
		String sql = SQL();
		logger.info(" sql:" + sql);

		return sql;
	}

	// 优化后代码
	public String selectUserIDFromOrderSMS(Integer recId) {
		BEGIN();
		StringBuilder sb = new StringBuilder();
		sb.append(
				"REC_ID,ACTIVITY_SEQ_ID,BATCH_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID,USER_ID,PHONE_NUMBER,CONTACT_DATE,CONTACT_TYPE,CONTACT_CODE,ORG_PATH,MANUAL_PATH,USER_PATH");
		sb.append(
				",EXE_PATH,ORDER_STATUS,CHANNEL_STATUS,DEAL_MONTH,SERVICE_TYPE,BEGIN_DATE,END_DATE,LAST_UPDATE_TIME,CITYID,AREAID,USER_ORG_ID,PREPARE_SEND_STATUS,INPUT_DATE,INVALID_DATE");
		SELECT(sb.toString());
		FROM(" PLT_ORDER_INFO_SMS ");
		WHERE(" ACTIVITY_SEQ_ID= " + recId + " AND  ORDER_STATUS ='0' AND TENANT_ID='uni076'");
		ORDER_BY(" BEGIN_DATE DESC");
		String sql = SQL();
		logger.info("sql:" + sql);

		return sql;
	}

	/*
	 * public String selectUserIDFromOrder(Integer recId) { BEGIN();
	 * SELECT("USER_ID"); FROM(" PLT_ORDER_INFO "); WHERE(" ACTIVITY_SEQ_ID= " +
	 * recId + " AND TENANT_ID='uni076'"); return SQL(); }
	 */

	// 得到一个活动下对应的各个用户工单记录列表
	public String selectSameOrderByUser(ParamMap paramMap) {
		BEGIN();
		SELECT("REC_ID");
		FROM(" PLT_ORDER_INFO ");
		WHERE(" ACTIVITY_SEQ_ID= " + paramMap.getACTIVITY_SEQ_ID() + " AND  USER_ID = '" + paramMap.getUSER_ID()
				+ "' AND  ORDER_STATUS ='0' AND TENANT_ID='uni076' ");

		ORDER_BY(" BEGIN_DATE DESC");
		return SQL();

	}

	// 以上无用

	public String deleteOrderByrecId(Integer recId) {
		BEGIN();
		DELETE_FROM("PLT_ORDER_INFO");
		WHERE(" REC_ID=" + recId + " AND TENANT_ID='uni076'");
		return SQL();
	}

	/*
	 * public String updateOrderStatus(Integer recId) { BEGIN();
	 * UPDATE("PLT_ORDER_INFO"); SET("ORDER_STATUS = '2' ");
	 * 
	 * WHERE(" REC_ID=" + recId + " AND TENANT_ID='uni076'"); return SQL(); }
	 */

	/**
	 * 黑白名单过滤，设置order表的order_status为1
	 * 
	 * @param order
	 * @return
	 */
	public String updateOrderStatusAfterFilter(Order order) {
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("USER_ID=" + s + order.getUSER_ID() + s);
		whereBuilder.append("and TENANT_ID=" + s + order.geTENANT_ID() + s);
		BEGIN();
		UPDATE(ORDER_INFO);
		SET("ORDER_STATUS='1'");
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/**
	 * 查询黑名单过滤字段=1的RECID
	 * 
	 * @return rec_id of FILTER_BLACKUSERLIST=1
	 */
	public String getRECIdByFilterBlack() {
		StringBuilder wherebBuilder = new StringBuilder();
		wherebBuilder.append("FILTER_BLACKUSERLIST=1 and TENANT_ID='uni076'");
		BEGIN();
		SELECT("REC_ID");
		FROM(PLT_ACTIVITY_INFO);
		WHERE(wherebBuilder.toString());
		return SQL();
	}

	/**
	 * 查询白名单过滤字段=1的RECID
	 * 
	 * @return rec_id of FILTER_WHITEUSERLIST=1
	 */
	public String getRECIdByFilterWhite() {
		StringBuilder wherebBuilder = new StringBuilder();
		wherebBuilder.append("FILTER_WHITEUSERLIST=1 and TENANT_ID='uni076'");
		BEGIN();
		SELECT("REC_ID");
		FROM(PLT_ACTIVITY_INFO);
		WHERE(wherebBuilder.toString());
		return SQL();
	}

	public String getWechatStatus(PltActivityChannelDetail detail) {
		String quo = "'";
		StringBuilder whereBuilder = new StringBuilder("");
		whereBuilder.append(
				"  a.REC_ID = (" + "select max(REC_ID) from PLT_ACTIVITY_CHANNEL_DETAIL b" + " where b.ACTIVITY_ID=");
		whereBuilder.append(quo + detail.getACTIVITY_ID() + quo);
		whereBuilder.append("and b.TENANT_ID = " + quo + detail.getTENANT_ID() + quo);
		whereBuilder.append("and b.CHANN_ID = " + quo + 11 + quo);
		whereBuilder.append(")");

		BEGIN();
		SELECT("WECHAT_STATUS ");
		FROM(CHANNEL_DETAIL + " a");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		System.out.println("getWechatStatus's sql:" + sql);
		return sql;
	}

	public String updateWechatStatus(PltActivityChannelDetail detail) {
		String s = "'";
		StringBuilder where = new StringBuilder();
		where.append("ACTIVITY_ID=" + s + detail.getACTIVITY_ID() + s);
		where.append(" and TENANT_ID=" + s + detail.getTENANT_ID() + s);
		where.append("and CHANN_ID = " + s + 11 + s);
		BEGIN();
		UPDATE(CHANNEL_DETAIL);
		SET("WECHAT_STATUS=" + s + detail.getWECHAT_STATUS() + s);

		WHERE(where.toString());
		return SQL();
	}

	/*************************************************/

	// 这个无用
	public String updateOrderStatusByTouch(Integer recId) {
		BEGIN();
		UPDATE("PLT_ORDER_INFO");
		SET("ORDER_STATUS = '3' ");

		WHERE(" REC_ID=" + recId + " AND TENANT_ID='uni076'");
		String sql = SQL();
		// logger.info("updateOrderStatusByTouch record sql:" + sql);

		return sql;
	}

	// 移入历史工单（除短信）
	public String insertOrderInfoHis(Map<String,Object> map /*OrderAndOrderSMS in*/) {
		OrderAndOrderSMS in = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		String table = this.getTableByChannelId(in.getCHANNEL_ID());
		//table += "_HIS"; 2.0版本无  _HIS表,由路由模块分配
		String s = "'";
		BEGIN();
		INSERT_INTO(table);
		VALUES("REC_ID", s + in.getREC_ID() + s);
		VALUES("ACTIVITY_SEQ_ID", s + in.getACTIVITY_SEQ_ID() + s);
		VALUES("BATCH_ID", s + in.getBATCH_ID() + s);
		VALUES("CHANNEL_ID", s + in.getCHANNEL_ID() + s);
		VALUES("MARKETING_WORDS", s + in.getMARKETING_WORDS() + s);
		VALUES("TENANT_ID", s + in.getTENANT_ID() + s);
		VALUES("USER_ID", s + in.getUSER_ID() + s);
		VALUES("PHONE_NUMBER", s + in.getPHONE_NUMBER() + s);
		VALUES("CONTACT_DATE", s + in.getCONTACT_DATE() + s);
		VALUES("CONTACT_TYPE", s + in.getCONTACT_TYPE() + s);
		VALUES("CONTACT_CODE", s + in.getCONTACT_CODE() + s);
		VALUES("ORG_PATH ", s + in.getORG_PATH() + s);
		VALUES("MANUAL_PATH", s + in.getMANUAL_PATH() + s);
		VALUES("MANUAL_DATE", s + in.getMANUAL_DATE() + s);
		VALUES("USER_PATH", s + in.getUSER_PATH() + s);
		VALUES("EXE_PATH", s + in.getEXE_PATH() + s);
		VALUES("ORDER_STATUS", s + in.getORDER_STATUS() + s);
		VALUES("CHANNEL_STATUS", s + in.getCHANNEL_STATUS() + s);
		VALUES("DEAL_MONTH", s + in.getDEAL_MONTH() + s);
		VALUES("SERVICE_TYPE", s + in.getSERVICE_TYPE() + s);
		VALUES("BEGIN_DATE", s + in.getBEGIN_DATE() + s);
		VALUES("END_DATE", s + in.getEND_DATE() + s);
		VALUES("LAST_UPDATE_TIME", s + in.getLAST_UPDATE_TIME() + s);
		VALUES("CITYID", s + in.getCITYID() + s);
		VALUES("AREAID", s + in.getAREAID() + s);
		VALUES("USER_ORG_ID", s + in.getUSER_ORG_ID() + s);
		VALUES("INPUT_DATE", s + in.getINPUT_DATE() + s);
		VALUES("INVALID_DATE", s + in.getINVALID_DATE() + s);

		VALUES("MB_ARPU", s + in.getMB_ARPU() + s);
		VALUES("MB_ONLINE_DUR", s + in.getMB_ONLINE_DUR() + s);
		VALUES("MB_VALUE_LEVEL", s + in.getMB_VALUE_LEVEL() + s);
		VALUES("CUST_TYPE", s + in.getCUST_TYPE() + s);
		VALUES("USER_STATUS", s + in.getUSER_STATUS() + s);
		VALUES("MB_OWE_FEE", s + in.getMB_OWE_FEE() + s);
		VALUES("KD_OWE_FEE", s + in.getKD_OWE_FEE() + s);
		VALUES("MB_AGREEMENT_TYPE", s + in.getMB_AGREEMENT_TYPE() + s);
		VALUES("MIX_FLAG", s + in.getMIX_FLAG() + s);
		VALUES("ELEC_CHANNEL", s + in.getELEC_CHANNEL() + s);
		VALUES("PROV_ID", s + in.getPROV_ID() + s);
		VALUES("AREA_NO", s + in.getAREA_NO() + s);
		VALUES("RENT_FEE", s + in.getRENT_FEE() + s);
		VALUES("NETIN_CHANNEL", s + in.getNETIN_CHANNEL() + s);
		VALUES("MB_FIRST_OWE_MONTH", s + in.getMB_FIRST_OWE_MONTH() + s);
		VALUES("KD_FIRST_OWE_MONTH", s + in.getKD_FIRST_OWE_MONTH() + s);
		VALUES("AGREEMENT_EXPIRE_TIME", s + in.getAGREEMENT_EXPIRE_TIME() + s);
		VALUES("CONTACT_NUM", s + in.getCONTACT_NUM() + s);
		VALUES("CUST_NAME", s + in.getCUST_NAME() + s);
		VALUES("KD_NETIN_MONTHS", s + in.getKD_NETIN_MONTHS() + s);
		VALUES("PRODUCT_CLASS", s + in.getPRODUCT_CLASS() + s);
		VALUES("ACCT_FEE", s + in.getACCT_FEE() + s);
		VALUES("WENDING_FLAG", s + in.getWENDING_FLAG() + s);
		VALUES("CHNL_TYPE4", s + in.getCHNL_TYPE4() + s);
		VALUES("PAY_MODE", s + in.getPAY_MODE() + s);
		VALUES("RESERVE1", s + in.getRESERVE1() + s);
		VALUES("RESERVE2", s + in.getRESERVE2() + s);
		VALUES("RESERVE3", s + in.getRESERVE3() + s);
		VALUES("RESERVE4", s + in.getRESERVE4() + s);
		VALUES("RESERVE5", s + in.getRESERVE5() + s);
		String sql = SQL();
		// logger.info("InsertOrderInfoHis record sql:" + sql);

		return sql;
	}

	// 移入历史工单（短信）
	public String insertOrderInfoSMSHis(Map<String,Object> map /*OrderAndOrderSMS in*/) {
		OrderAndOrderSMS in = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		String table = "PLT_ORDER_INFO_SMS_HIS";
		String s = "'";
		BEGIN();
		INSERT_INTO(table); //INSERT_INTO("PLT_ORDER_INFO_SMS_HIS");
		VALUES("REC_ID", s + in.getREC_ID() + s);
		VALUES("ACTIVITY_SEQ_ID", s + in.getACTIVITY_SEQ_ID() + s);
		VALUES("BATCH_ID", s + in.getBATCH_ID() + s);
		VALUES("CHANNEL_ID", s + in.getCHANNEL_ID() + s);
		VALUES("MARKETING_WORDS", s + in.getMARKETING_WORDS() + s);
		VALUES("TENANT_ID", s + in.getTENANT_ID() + s);
		VALUES("USER_ID", s + in.getUSER_ID() + s);
		VALUES("PHONE_NUMBER", s + in.getPHONE_NUMBER() + s);
		VALUES("CONTACT_DATE", s + in.getCONTACT_DATE() + s);
		VALUES("CONTACT_TYPE", s + in.getCONTACT_TYPE() + s);
		VALUES("CONTACT_CODE", s + in.getCONTACT_CODE() + s);
		VALUES("ORG_PATH ", s + in.getORG_PATH() + s);
		VALUES("MANUAL_PATH", s + in.getMANUAL_PATH() + s);
		VALUES("MANUAL_DATE", s + in.getMANUAL_DATE() + s);
		VALUES("USER_PATH", s + in.getUSER_PATH() + s);
		VALUES("EXE_PATH", s + in.getEXE_PATH() + s);
		VALUES("ORDER_STATUS", s + in.getORDER_STATUS() + s);
		VALUES("CHANNEL_STATUS", s + in.getCHANNEL_STATUS() + s);
		VALUES("DEAL_MONTH", s + in.getDEAL_MONTH() + s);
		VALUES("SERVICE_TYPE", s + in.getSERVICE_TYPE() + s);
		VALUES("BEGIN_DATE", s + in.getBEGIN_DATE() + s);
		VALUES("END_DATE", s + in.getEND_DATE() + s);
		VALUES("LAST_UPDATE_TIME", s + in.getLAST_UPDATE_TIME() + s);
		VALUES("CITYID", s + in.getCITYID() + s);
		VALUES("AREAID", s + in.getAREAID() + s);
		VALUES("USER_ORG_ID", s + in.getUSER_ORG_ID() + s);
		VALUES("PREPARE_SEND_STATUS", s + in.getPREPARE_SEND_STATUS() + s);
		VALUES("INPUT_DATE", s + in.getINPUT_DATE() + s);
		VALUES("INVALID_DATE", s + in.getINVALID_DATE() + s);

		VALUES("MB_ARPU", s + in.getMB_ARPU() + s);
		VALUES("MB_ONLINE_DUR", s + in.getMB_ONLINE_DUR() + s);
		VALUES("MB_VALUE_LEVEL", s + in.getMB_VALUE_LEVEL() + s);
		VALUES("CUST_TYPE", s + in.getCUST_TYPE() + s);
		VALUES("USER_STATUS", s + in.getUSER_STATUS() + s);
		VALUES("MB_OWE_FEE", s + in.getMB_OWE_FEE() + s);
		VALUES("KD_OWE_FEE", s + in.getKD_OWE_FEE() + s);
		VALUES("MB_AGREEMENT_TYPE", s + in.getMB_AGREEMENT_TYPE() + s);
		VALUES("MIX_FLAG", s + in.getMIX_FLAG() + s);
		VALUES("ELEC_CHANNEL", s + in.getELEC_CHANNEL() + s);
		VALUES("PROV_ID", s + in.getPROV_ID() + s);
		VALUES("AREA_NO", s + in.getAREA_NO() + s);
		VALUES("RENT_FEE", s + in.getRENT_FEE() + s);
		VALUES("NETIN_CHANNEL", s + in.getNETIN_CHANNEL() + s);
		VALUES("MB_FIRST_OWE_MONTH", s + in.getMB_FIRST_OWE_MONTH() + s);
		VALUES("KD_FIRST_OWE_MONTH", s + in.getKD_FIRST_OWE_MONTH() + s);
		VALUES("AGREEMENT_EXPIRE_TIME", s + in.getAGREEMENT_EXPIRE_TIME() + s);
		VALUES("CONTACT_NUM", s + in.getCONTACT_NUM() + s);
		VALUES("CUST_NAME", s + in.getCUST_NAME() + s);
		VALUES("KD_NETIN_MONTHS", s + in.getKD_NETIN_MONTHS() + s);
		VALUES("PRODUCT_CLASS", s + in.getPRODUCT_CLASS() + s);
		VALUES("ACCT_FEE", s + in.getACCT_FEE() + s);
		VALUES("WENDING_FLAG", s + in.getWENDING_FLAG() + s);
		VALUES("CHNL_TYPE4", s + in.getCHNL_TYPE4() + s);
		VALUES("PAY_MODE", s + in.getPAY_MODE() + s);
		VALUES("RESERVE1", s + in.getRESERVE1() + s);
		VALUES("RESERVE2", s + in.getRESERVE2() + s);
		VALUES("RESERVE3", s + in.getRESERVE3() + s);
		VALUES("RESERVE4", s + in.getRESERVE4() + s);
		VALUES("RESERVE5", s + in.getRESERVE5() + s);
		String sql = SQL();
		// logger.info("InsertOrderInfoHis record sql:" + sql);

		return sql;
	}

	// 根据匹配好的ActivityID查询出所需要的REC_ID（REC_IDy用来关联PLT_ORDER_INFO中的ACTIVITY_SEQ_ID）
	// eg:SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID=15992
	public String getRECIdByActivityId(String activityId) {

		BEGIN();
		SELECT("REC_ID ");
		FROM(PLT_ACTIVITY_INFO);
		WHERE("ACTIVITY_ID=" + activityId + " AND TENANT_ID= 'uni076' ");
		return SQL();
	}

	public String cleanActivityInfo(PltActivityInfo activity) {
		/*
		 * BEGIN(); DELETE_FROM("a.*" + "b.*" + "c.* "+ "d.*"); //DELETE_FROM(
		 * "PLT_ACTIVITY_CHANNEL_DETAIL b" ); //DELETE_FROM(
		 * "PLT_ACTIVITY_PRODUCT_LIST c" ); //DELETE_FROM(
		 * "PLT_ACTIVITY_SUCESS_CFG d" ); FROM (" PLT_ACTIVITY_INFO a ") ;
		 * INNER_JOIN(
		 * "PLT_ACTIVITY_CHANNEL_DETAIL b on b.ACTIVITY_ID = a.ACTIVITY_ID" );
		 * INNER_JOIN(
		 * "PLT_ACTIVITY_PRODUCT_LIST c on c.ACTIVITY_ID = a.ACTIVITY_ID" );
		 * INNER_JOIN(
		 * "PLT_ACTIVITY_SUCESS_CFG d on d.ACTIVITY_ID = a.ACTIVITY_ID" );
		 * 
		 * return SQL();
		 */
		String s = "'";
		BEGIN();
		DELETE_FROM(PLT_ACTIVITY_INFO);
		WHERE("TENANT_ID=" + s + activity.getTENANT_ID() + s + " and REC_ID=" + activity.getREC_ID());

		return SQL();
	}

	public String cleanChannelInfo(PltActivityInfo activity) {
		String s = "'";
		BEGIN();
		DELETE_FROM("PLT_ACTIVITY_CHANNEL_DETAIL");
		WHERE("ACTIVITY_ID=" + s + activity.getACTIVITY_ID() + s + " and TENANT_ID=" + s + activity.getTENANT_ID() + s);
		return SQL();
	}

	public String cleanProductInfo(PltActivityInfo activity) {
		String s = "'";
		BEGIN();
		DELETE_FROM("PLT_ACTIVITY_PRODUCT_LIST");
		WHERE("ACTIVITY_ID=" + s + activity.getACTIVITY_ID() + s + " and TENANT_ID=" + s + activity.getTENANT_ID() + s);
		return SQL();
	}

	public String cleanSuccessInfo(PltActivityInfo activity) {
		String s = "'";
		BEGIN();
		DELETE_FROM("PLT_ACTIVITY_SUCESS_CFG");
		WHERE("ACTIVITY_ID=" + s + activity.getACTIVITY_ID() + s + " and TENANT_ID=" + s + activity.getTENANT_ID() + s);
		return SQL();
	}

	public String InsertActivityChannelStatus(ActivityChannelStatus status) {
		String s = "'";
		BEGIN();
		INSERT_INTO("PLT_ACTIVITY_CHANNEL_STATUS");
		VALUES("ACTIVITY_SEQ_ID", s + status.getACTIVITY_SEQ_ID() + s);
		VALUES("ACTIVITY_ID", s + status.getACTIVITY_ID() + s);
		VALUES("TENANT_ID", s + status.getTENANT_ID() + s);

		VALUES("CHANNEL_ID", s + status.getCHANNEL_ID() + s);
		VALUES("STATUS", s + "0" + s);

		String sql = SQL();
		logger.info("InsertActivityChannelStatus record sql:" + sql);
		return sql;
	}

	public String InsertActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		INSERT_INTO("PLT_ACTIVITY_PROCESS_LOG");

		VALUES("ACTIVITY_ID", s + log.getACTIVITY_ID() + s);
		VALUES("TENANT_ID", s + log.getTENANT_ID() + s);
		VALUES("CHANNEL_ID", s + log.getCHANNEL_ID() + s);

		VALUES("ORDER_BEGIN_DATE", s + log.getORDER_BEGIN_DATE() + s);
		VALUES("ACTIVITY_SEQ_ID", s + log.getACTIVITY_SEQ_ID() + s);
		VALUES("ORI_AMOUNT", s + log.getORI_AMOUNT() +s );
		VALUES("INOUT_FILTER_AMOUNT", s + log.getINOUT_FILTER_AMOUNT() +s );
		VALUES("COVERAGE_FILTER_AMOUNT", s + log.getCOVERAGE_FILTER_AMOUNT() +s );
		VALUES("BLACK_FILTER_AMOUNT", s + log.getBLACK_FILTER_AMOUNT() +s );
		VALUES("RESERVE_FILTER_AMOUNT", s + log.getRESERVE_FILTER_AMOUNT() +s );
		VALUES("TOUCH_FILTER_AMOUNT", s + log.getTOUCH_FILTER_AMOUNT() +s );
		VALUES("STATUS", s + log.getSTATUS() +s );
		String sql = SQL();
		logger.info("InsertActivityChannelStatus record sql:" + sql);
		return sql;
	}

	public String UpdateActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET("STATUS =" + s + log.getSTATUS() + s);
		SET("ORI_AMOUNT=" + s + log.getORI_AMOUNT() + s);
		SET("ORDER_END_DATE=" + s + log.getORDER_END_DATE() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 根据工单中心2.0改造需求，在工单过滤模块中更新plt_activity_process_log表记录
	 * @param log 更新记录
	 * @return sql语句.
	 */
	public String UpdateActivityProcessLogAddedOrderFilterAccountInfo(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET("STATUS =" + s + log.getSTATUS() + s);
		SET("INOUT_FILTER_AMOUNT=" + s + log.getINOUT_FILTER_AMOUNT() + s);
		SET("COVERAGE_FILTER_AMOUNT=" + s + log.getCOVERAGE_FILTER_AMOUNT() + s);
		SET("BLACK_FILTER_AMOUNT=" + s + log.getBLACK_FILTER_AMOUNT() + s);
		SET("RESERVE_FILTER_AMOUNT=" + s + log.getRESERVE_FILTER_AMOUNT() + s);
		SET("TOUCH_FILTER_AMOUNT=" + s + log.getTOUCH_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}

	// DEPRECATED
	public String getOrderNumByChannelId(ActivityProcessLog log) {
		String channelId = log.getCHANNEL_ID();
		String table = getTableByChannelId(channelId);

		BEGIN();
		SELECT("count(*)");
		FROM(table);
		WHERE("ACTIVITY_SEQ_ID=" + log.getACTIVITY_SEQ_ID() + " and CHANNEL_ID=" + "'" + log.getCHANNEL_ID() + "'"
				+ " and TENANT_ID=" + "'" + log.getTENANT_ID() + "'");
		return SQL();
	}

	/**
	 * 黑名单过滤，设置order_info表的order_status为1
	 * 
	 * @param order
	 * @return
	 */
	public String updateOrderInfoStatusFilterBlack(Map<String,Object> map/*OrderAndOrderSMS order*/) {
		OrderAndOrderSMS order = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		//TODO,需要活动ID
		String table = this.getTableByChannelId(order.getCHANNEL_ID());
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("PHONE_NUMBER=" + s + order.getPHONE_NUMBER() + s);
		whereBuilder.append("AND  TENANT_ID= " + s + order.getTENANT_ID() + s);
		whereBuilder.append("AND  ACTIVITY_SEQ_ID= " + s + order.getACTIVITY_SEQ_ID() + s);
		BEGIN();
		UPDATE(table);
		SET("ORDER_STATUS='1'");
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/**
	 * 白名单过滤，设置order_info表的order_status为2
	 * 
	 * @param order
	 * @return
	 */
	public String updateOrderInfoStatusFilterWhite(Map<String,Object> map/*OrderAndOrderSMS order*/) {
		OrderAndOrderSMS order = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		String table = this.getTableByChannelId(order.getCHANNEL_ID());
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("PHONE_NUMBER=" + s + order.getPHONE_NUMBER() + s);
		whereBuilder.append("AND  TENANT_ID= " + s + order.getTENANT_ID() + s);
		whereBuilder.append("AND  ACTIVITY_SEQ_ID= " + s + order.getACTIVITY_SEQ_ID() + s);
		BEGIN();
		UPDATE(table);
		SET("ORDER_STATUS='2'");
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/**
	 * 黑名单过滤，设置order_sms表的order_status为1
	 * 
	 * @param order
	 * @return
	 */
	public String updateOrderSmsStatusFilterBlack(Map<String,Object> map /*OrderAndOrderSMS order*/) {
		OrderAndOrderSMS order = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		String table = "ORDER_SMS_INFO";
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("PHONE_NUMBER=" + s + order.getPHONE_NUMBER() + s);
		whereBuilder.append("AND  TENANT_ID= " + s + order.getTENANT_ID() + s);
		whereBuilder.append("AND  ACTIVITY_SEQ_ID= " + s + order.getACTIVITY_SEQ_ID() + s);
		BEGIN();
		UPDATE(table);//UPDATE(ORDER_SMS_INFO);
		SET("ORDER_STATUS='1'");
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/**
	 * 白名单过滤，设置order_sms表的order_status为2
	 * 
	 * @param order
	 * @return
	 */
	public String updateOrderSmsStatusFilterWhite(Map<String,Object> map/*OrderAndOrderSMS order*/) {
		OrderAndOrderSMS order = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		String table = "ORDER_SMS_INFO";
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("PHONE_NUMBER=" + s + order.getPHONE_NUMBER() + s);
		whereBuilder.append("AND  TENANT_ID= " + s + order.getTENANT_ID() + s);
		whereBuilder.append("AND  ACTIVITY_SEQ_ID= " + s + order.getACTIVITY_SEQ_ID() + s);
		BEGIN();
		UPDATE(table);//UPDATE(ORDER_SMS_INFO);
		SET("ORDER_STATUS='2'");
		WHERE(whereBuilder.toString());
		return SQL();
	}

	// 根据activity_seq_id查询PLT_ORDER_INFO各个字段的值
	public String selectOrderInfoByActivitySeq(ParamMap param) {
		String table = this.getTableByChannelId(param.getCHANN_ID());
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("ACTIVITY_SEQ_ID =" + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND ORDER_STATUS ='0'");
		whereBuilder.append("AND TENANT_ID= " + s + param.getTENANT_ID() + s);
		BEGIN();
		// SELECT("REC_ID,USER_ID,ACTIVITY_SEQ_ID,PHONE_NUMBER,ORDER_STATUS,TENANT_ID,CHANNEL_ID");
		SELECT("*");
		FROM(table);
		WHERE(whereBuilder.toString());
		return SQL();
	}

	// 根据activity_seq_id查询PLT_ORDER_INFO_SMS各个字段的值
	public String selectOrderSMSByActivitySeq(ParamMap param) {
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("ACTIVITY_SEQ_ID =" + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND ORDER_STATUS ='0'");
		whereBuilder.append("AND TENANT_ID= " + s + param.getTENANT_ID() + s);
		BEGIN();
		SELECT("*");
		FROM(ORDER_SMS_INFO);
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/*
	 * get invalid order activity_seq_id from plt_order_info table
	 * 
	 * @param tenantId
	 * 
	 * @return List<Integer>
	 */

	public String getInvalidActivitySeqId(String tenantId) {
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ORDER_END_DATE < SYSDATE()");
		whereBuilder.append(" AND ACTIVITY_STATUS = 1");
		whereBuilder.append(" AND TENANT_ID= " + s + tenantId + s);
		BEGIN();
		SELECT("REC_ID, ACTIVITY_ID");
		FROM(PLT_ACTIVITY_INFO);
		WHERE(whereBuilder.toString());
		return SQL();
	}
	/*
	 * move invalid order records to his table
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public String moveInvalidOrderRecords(PltActivityInfo act) {
		String s = "'";
		StringBuilder sb = new StringBuilder();
		sb.append("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID =");
		sb.append(s);
		sb.append(act.getTENANT_ID());
		sb.append(s);
		sb.append("*/");
		sb.append(" insert into PLT_ORDER_INFO_HIS select * from PLT_ORDER_INFO");
		sb.append(" where ACTIVITY_SEQ_ID=" + act.getREC_ID());
		return sb.toString();
	}
	/*
	 * delete invalid order records from plt_order_info
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public String deleteInvalidActivitySeqId(PltActivityInfo act) {
		String s = "'";
		BEGIN();
		DELETE_FROM(ORDER_INFO);
		WHERE(" ACTIVITY_SEQ_ID=" + s + act.getREC_ID() + s + "AND  TENANT_ID= " + s + act.getTENANT_ID() + s);

		return SQL();
	}
	/*
	 * update invalid order records,set
	 * invalid_date=sysdate,input_date=sysdate,order_status='6'
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public String updateInvalidOrderRecords(PltActivityInfo act) {

		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("ACTIVITY_SEQ_ID=" + s + act.getREC_ID() + s);
		whereBuilder.append("AND  TENANT_ID= " + s + act.getTENANT_ID() + s);

		BEGIN();
		UPDATE(ORDER_INFO);
		SET("INVALID_DATE= SYSDATE()");
		SET("INPUT_DATE= SYSDATE()");
		SET("ORDER_STATUS = \'6\'");
		WHERE(whereBuilder.toString());
		return SQL();

	}
	/*
	 * update activity_info table set activity_status='2' when order recores
	 * invalid
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public String updateActvityInfoInvalid(PltActivityInfo act) {
		String s = "'";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("REC_ID=" + s + act.getREC_ID() + s);
		whereBuilder.append("AND  TENANT_ID= " + s + act.getTENANT_ID() + s);

		BEGIN();
		UPDATE(PLT_ACTIVITY_INFO);
		SET("ACTIVITY_STATUS = 2");
		WHERE(whereBuilder.toString());
		return SQL();

	}

	/**
	 * 根据更新规则过滤工单
	 * 
	 */
	// 优化后代码(传一个租户id和activity_id,活动的增量统计)
	public String selectActivityIdByActivitySEQID(ParamMap param) {
		BEGIN();
		SELECT("ACTIVITY_ID");
		FROM(" PLT_ACTIVITY_INFO ");
		WHERE("  TENANT_ID='" + param.getTENANT_ID() + "' AND REC_ID = " + param.getREC_ID());
		String sql = SQL();
		logger.info("selectActivityIdByActivitySEQID sql:" + sql);
		return sql;
	}

	public String selectUpdateRuleByActivity(ParamMap param) {
		BEGIN();

		SELECT("REC_ID,ORDER_UPDATE_RULE");
		FROM(" PLT_ACTIVITY_INFO ");
		WHERE(" (ORDER_GEN_RULE = 1 OR ORDER_GEN_RULE = 2)   AND TENANT_ID='" + param.getTENANT_ID()
				+ "' AND ACTIVITY_ID = '" + param.getACTIVITY_ID() + "'");
		ORDER_BY(" LAST_ORDER_CREATE_TIME DESC");
		String sql = SQL();
//		logger.info("selectUpdateRuleByActivity sql:" + sql);
		return sql;
	}

	public String selectActivityForTouch(ParamMap param) {
		BEGIN();

		SELECT("REC_ID");
		FROM(" PLT_ACTIVITY_INFO ");
		WHERE("  TENANT_ID='" + param.getTENANT_ID() + "' AND ACTIVITY_ID = '" + param.getACTIVITY_ID() + "'");
		ORDER_BY(" LAST_ORDER_CREATE_TIME DESC");
		String sql = SQL();
//		logger.info("selectUpdateRuleByActivity sql:" + sql);
		return sql;
	}

	// 优化后代码 根据活动标识得到上次活动中工单表中的USERID
	public String selectOrderUSERID(ParamMap param) {
		String table = param.getTaleName();//getTableByChannelId(param.getCHANN_ID());
        checkTableName(table);
		StringBuilder whereBuilder = new StringBuilder();

		whereBuilder.append("  ACTIVITY_SEQ_ID = " + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND REC_ID > " + param.getBEGIN_REC_ID());
		whereBuilder.append(" AND CHANNEL_ID = '" + param.getCHANN_ID() + "'");
		whereBuilder.append(" AND  ORDER_STATUS ='5'  AND CONTACT_CODE='0' ");
		whereBuilder.append("  AND TENANT_ID = '" + param.getTENANT_ID() + "'");

		BEGIN();
		SELECT("USER_ID,REC_ID");
		FROM(table);
		WHERE(whereBuilder.toString());
		String sql = SQL() + " ORDER BY REC_ID ASC LIMIT " + param.getLIMIT_NUMBER();
		logger.info(" [OrderCenter]:selectOrderUSERID:" + sql);
		return sql;
	}

	// 优化后代码 根据活动标识得到上次活动中短信工单表中的USERID
	public String selectOrderSMSUSERID(ParamMap param) {
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND  ORDER_STATUS ='5' AND CHANNEL_STATUS='0'");
		BEGIN();
		SELECT("USER_ID");
		FROM("PLT_ORDER_INFO_SMS");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		logger.info(" sql:" + sql);
		return sql;
	}

	// 优化后代码 根据USERID得到本次活动的工单表
	public String selectOrderInfoByUserId(ParamMap param) {
		String table = getTableByChannelId(param.getCHANN_ID());
		StringBuilder whereBuilder = new StringBuilder();

		whereBuilder.append("  ACTIVITY_SEQ_ID = " + param.getREC_ID());
		whereBuilder.append(" AND USER_ID = '" + param.getUSER_ID() + "'");
		whereBuilder.append(" AND CHANNEL_ID = " + param.getCHANN_ID());
		whereBuilder.append(" AND  ORDER_STATUS ='0' ");
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");

		BEGIN();
		SELECT(" * ");
		FROM(table);
		WHERE(whereBuilder.toString());
		/* ORDER_BY(" BEGIN_DATE DESC"); */
		String sql = SQL();
		logger.info(" sql:" + sql);

		return sql;
	}

	// 优化后代码 根据USERID得到本次活动的短信工单表
	public String selectOrderInfoSMSByUserId(ParamMap param) {

		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getREC_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND USER_ID = '" + param.getUSER_ID() + "'");
		whereBuilder.append(" AND  ORDER_STATUS ='0' ");
		BEGIN();

		SELECT(" * ");
		FROM(" PLT_ORDER_INFO_SMS ");
		WHERE(whereBuilder.toString());
		/* ORDER_BY(" BEGIN_DATE DESC"); */
		String sql = SQL();
		logger.info("sql:" + sql);

		return sql;
	}

	// 覆盖用 工单表(上次活动)
	public String selectOrderForCover(ParamMap param) {
		BEGIN();
		SELECT(" * ");
		// only for frontline channel cover,other channel order already sent
		FROM(" PLT_ORDER_INFO ");
		WHERE(" ACTIVITY_SEQ_ID= " + param.getACTIVITY_SEQ_ID() + " AND TENANT_ID='" + param.getTENANT_ID()
				+ "'  AND  ORDER_STATUS ='5' AND CHANNEL_STATUS='0'");
		return SQL();
	}

	// 覆盖用 短信工单表(上次活动)
	public String selectOrderSMSForCover(ParamMap param) {
		BEGIN();
		SELECT(" * ");
		FROM(" PLT_ORDER_INFO_SMS ");
		WHERE(" ACTIVITY_SEQ_ID= " + param.getACTIVITY_SEQ_ID() + " AND TENANT_ID='" + param.getTENANT_ID()
				+ "'  AND  ORDER_STATUS ='5' AND CHANNEL_STATUS='0'");
		return SQL();
	}

	// 更新工单（除短信）状态和真正失效时间
	public String updateOrderStatus(ParamMap param) {
		String table = getTableByChannelId(param.getCHANN_ID());
		String s = "'";
		BEGIN();
		UPDATE(table);
		SET("ORDER_STATUS = '" + param.getType() + "'");
		SET("INVALID_DATE = " + s + param.getINVALID_DATE() + s);
		WHERE(" REC_ID=" + param.getREC_ID() + " AND TENANT_ID='" + param.getTENANT_ID() + "'");
		String sql = SQL();
		// logger.info("sql:" + sql);

		return sql;
	}

	// 更新工单（短信）状态和真正失效时间
	public String updateOrderSMSStatus(ParamMap param) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ORDER_INFO_SMS");
		SET("ORDER_STATUS = '" + param.getType() + "'");
		SET("INVALID_DATE = " + s + param.getINVALID_DATE() + s);
		WHERE(" REC_ID=" + param.getREC_ID() + " AND TENANT_ID='" + param.getTENANT_ID() + "'");
		String sql = SQL();
		return sql;
	}

	// 活动表里活动未失效并且活动状态已启用的活动ID(ACTIVITY_ID 为varchar类型)
	public String getActivityIdByOGI(ParamMap param) {
		BEGIN();
		SELECT("REC_ID");
		FROM(" PLT_ACTIVITY_INFO ");
		WHERE(" ( TENANT_ID='" + param.getTENANT_ID() + "' ) AND ORI_STATE = '9' AND ACTIVITY_STATUS !=2 AND  ");
		String sql = SQL();
		// logger.info("updateOrderStatusByTouch record sql:" + sql);

		return sql;
	}

	/**
	 * 接触频次过滤工单
	 * 
	 */
	// 根据上次活动中活动ID和客户经理的渠道ID找到相应的接触频次(varchar类型)
	public String getTouchLimitDayFromChannel(ParamMap param) {

		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND CHANN_ID = '5' ");

		BEGIN();
		SELECT("TOUCHLIMITDAY");
		FROM(" PLT_ACTIVITY_CHANNEL_DETAIL ");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		return sql;
	}

	// 根据上次活动中活动ID 短信渠道的接触频次
	public String getSMSTouchLimitDayFromChannel(ParamMap param) {

		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND CHANN_ID = '7' ");

		BEGIN();
		SELECT("TOUCHLIMITDAY");
		FROM(" PLT_ACTIVITY_CHANNEL_DETAIL ");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		return sql;
	}

	// 客户经理渠道下得到本次活动工单表中的USER_ID,ORG_PATH
	public String getUserOrgFromOrder(ParamMap param) {
		String table = "PLT_ORDER_INFO";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getREC_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND ORDER_STATUS = '0' ");

		BEGIN();
		SELECT(" USER_ID,ORG_PATH,BEGIN_DATE ");
		//FROM(" PLT_ORDER_INFO ");
		FROM(table);
		WHERE(whereBuilder.toString());
		String sql = SQL();
		return sql;
	}

	// 短信渠道下本次活动下短信工单表中的USER_ID,ORG_PATH
	public String getUserOrgFromOrderSMS(ParamMap param) {
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getREC_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND ORDER_STATUS = '0' ");

		BEGIN();
		SELECT(" USER_ID,ORG_PATH,BEGIN_DATE ");
		FROM(" PLT_ORDER_INFO_SMS ");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		return sql;

	}

	// 客户经理渠道下每一用户下有接触历史的工单表
	public String getOrderByTouch(ParamMap param) {
		String table = "PLT_ORDER_INFO";
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND ORDER_STATUS = '5' ");
		whereBuilder.append(" AND USER_ID = '" + param.getUSER_ID() + "'");
		whereBuilder.append(" AND ORG_PATH = '" + param.getORG_PATH() + "'");

		BEGIN();
		SELECT(" * ");
		FROM(table); //FROM(" PLT_ORDER_INFO ");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		return sql;
	}

	// 短信渠道下每一用户下有接触历史的短信工单表
	public String getOrderSMSByTouch(ParamMap param) {
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_SEQ_ID = " + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");
		whereBuilder.append(" AND ORDER_STATUS = '5' ");
		whereBuilder.append(" AND USER_ID = '" + param.getUSER_ID() + "'");
		whereBuilder.append(" AND ORG_PATH = '" + param.getORG_PATH() + "'");

		BEGIN();
		SELECT(" * ");
		FROM(" PLT_ORDER_INFO_SMS ");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		return sql;
	}

	// 删除工单表
	public String deleteOrderByRecId(Map<String,Object> map /*OrderAndOrderSMS order*/) {
		OrderAndOrderSMS order = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		//TODO 路由方式获取表名
		String table = this.getTableByChannelId(order.getCHANNEL_ID());
		String s = "'";
		StringBuilder wherebBuilder = new StringBuilder();
		wherebBuilder.append(" REC_ID=" + order.getREC_ID());
		wherebBuilder.append(" AND TENANT_ID= " + s + order.getTENANT_ID() + s);
		BEGIN();
		DELETE_FROM(table);
		WHERE(wherebBuilder.toString());
		return SQL();
	}

	// 删除短信工单表
	public String deleteOrderSmsByRecId(Map<String,Object> map /*OrderAndOrderSMS order*/) {
		OrderAndOrderSMS order = (OrderAndOrderSMS) map.get("orderSMS");
		ParamMap param = (ParamMap) map.get("paramMap");
		String table = "PLT_ORDER_INFO_SMS";
		String s = "'";
		StringBuilder wherebBuilder = new StringBuilder();
		wherebBuilder.append(" REC_ID=" + order.getREC_ID());
		wherebBuilder.append(" AND TENANT_ID= " + s + order.getTENANT_ID() + s);
		BEGIN();
		DELETE_FROM(table);//DELETE_FROM("PLT_ORDER_INFO_SMS");
		WHERE(wherebBuilder.toString());
		return SQL();
	}

	/*
	 * get table by channel id
	 * 
	 * @param String channelId
	 * 
	 * @return table name
	 */
	public String getTableByChannelId(String channelId) {
		// sms order has been remove _SMS_HIS_XX table when sent,so need find
		// his table
		// select is_finish from PLT_ORDER_STATISTIC_SEND 1：his.0:当前表
//		if (channelId.equals("7"))
//			return "PLT_ORDER_INFO_SMS";
//		else if (channelId.equals("5"))
//			return "PLT_ORDER_INFO";
//		else if (channelId.equals("1") || channelId.equals("2") || channelId.equals("9"))
//			return "PLT_ORDER_INFO_ONE";
//		else if (channelId.indexOf("8") != -1)
//			return "PLT_ORDER_INFO_POPWIN";
//		else if (channelId.equals("11"))
//			return "PLT_ORDER_INFO_WEIXIN";
//		else
//			return "PLT_ORDER_INFO";

		return "PLT_ORDER_INFO_TEMP";
	}
	
	/*
	 * get white black user list from oracle db CLYX_ACTIVITY_FILTE_USERS
	 * 
	 * @param
	 * 
	 * @return List<WhiteBlackFilterUser>
	 */
	public String getWhitBlackUserList() {
		BEGIN();
		SELECT("USER_ID,USER_PHONE,FILTE_TYPE");
		FROM("CLYX_ACTIVITY_FILTE_USERS");
		return SQL();

	}
	/*
	 * get user label table's data status(1:changing,0:no changing)
	 * 
	 * @param
	 * 
	 * @return max time
	 */

	public String getUserLabelDataStatus(String sql) {
		return sql;
	}

	// *******过滤优化**********//
	// -- 当前活动批次-->活动ID --
	public String selectActivityByActivitySEQID(Map<String, Object> param) {
		BEGIN();
		SELECT("ACTIVITY_ID");
		FROM(" PLT_ACTIVITY_INFO ");
		WHERE("  REC_ID = " + param.get("currentActivitySeq") + " AND TENANT_ID='" + param.get("TENANT_ID") + "'");
		String sql = SQL();
		return sql;
	}

	// --活动ID-->该活动的各批次和更新规则--
	public String selectRuleByActivity(Map<String, Object> param) {
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" ACTIVITY_ID = '" + param.get("ACTIVITY_ID") + "'");
		whereBuilder.append(" AND (ORDER_GEN_RULE = 1 OR ORDER_GEN_RULE = 2)");
		whereBuilder.append(" AND TENANT_ID='" + param.get("TENANT_ID") + "'");
		whereBuilder.append(" AND ACTIVITY_STATUS='1' ");
		BEGIN();
		SELECT("REC_ID,ORDER_UPDATE_RULE,ACTIVITY_ID");
		FROM(" PLT_ACTIVITY_INFO ");
		WHERE(whereBuilder.toString());
		ORDER_BY(" LAST_ORDER_CREATE_TIME DESC");
		String sql = SQL();
//		logger.info("selectUpdateRuleByActivity sql:" + sql);
		return sql;
	}

	// --查询上一批次工单 only for frontline channel cover,other channel order already
	// sent --
	public String selectLastOrder(Map<String, Object> param) {
		String table = getTableByChannelId((String) param.get("CHANNEL_ID"));
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("lastActivitySeq"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append("  AND  ORDER_STATUS ='5' AND CHANNEL_STATUS='0' ");

		BEGIN();
		SELECT(" * ");
		FROM(table);
		WHERE(whereBuilder.toString());
		return SQL();
	}

	// --批量删除上批次工单记录--
	public String deleteBeforeOrder(Map<String, Object> param) {
		String table = (String)param.get("tableName");//getTableByChannelId((String) param.get("CHANNEL_ID"));
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("beforeActivitySeq"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append("  AND  ORDER_STATUS ='4' ");

		BEGIN();
		DELETE_FROM(table);
		WHERE(whereBuilder.toString());
		return SQL() + " LIMIT " + param.get("LIMIT_NUMBER");
	}

	/*
	 * get order number by area_no
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */

	public String getOrderNumberByArea(Map<String, Object> param) {
		String table = TEMP_TABLE;
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND CHANNEL_ID= '" + param.get("CHANNEL_ID") + "' ");
		whereBuilder.append("  AND  ORDER_STATUS ='0' ");

		BEGIN();
		SELECT(" AREA_NO,count(*) CNT ");
		FROM(table);
		WHERE(whereBuilder.toString());
		GROUP_BY(" AREA_NO");
		return SQL();

	}

	/*
	 * update order records to 7 flag for every area_no and limit number
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 * 
	 * @param: AREA_NO
	 * 
	 * @param: LIMIT_NUMBER
	 */
	public String updateRemindOrderByArea(Map<String, Object> param) {
		String table = TEMP_TABLE;//getTableByChannelId((String) param.get("CHANNEL_ID"));
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND CHANNEL_ID= '" + param.get("CHANNEL_ID") + "' ");
		whereBuilder.append(" AND AREA_NO='" + param.get("AREA_NO") + "'");
		// whereBuilder.append(" LIMIT " + param.get("LIMIT_NUMBER"));
		whereBuilder.append(" AND ORDER_STATUS='0'");

		BEGIN();
		UPDATE(table);

		SET("ORDER_STATUS = \'7\'");
		WHERE(whereBuilder.toString());
		return SQL() + " LIMIT " + param.get("LIMIT_NUMBER");

	}

	/*
	 * insert into remind orders to PLT_ORDER_INFO_REMIND table
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */
	public String insertRemindOrder(Map<String, Object> param) {
		String table = TEMP_TABLE;//getTableByChannelId((String) param.get("CHANNEL_ID"));
		String hisTableName = (String)param.get("hisTableName");
		checkTableName(hisTableName);
		StringBuilder whereBuilder = new StringBuilder();
//		if (!param.get("CHANNEL_ID").equals("7")) {
//			whereBuilder.append(" select *,'' FROM " + table + " where ");
//		} else {
//			whereBuilder.append(" select * FROM " + table + " where ");
//		}
		whereBuilder.append(" select * FROM " + table + " where ");
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND CHANNEL_ID= '" + param.get("CHANNEL_ID") + "' ");
		whereBuilder.append(" AND ORDER_STATUS='7'");

		BEGIN();
		INSERT_INTO(/*"PLT_ORDER_INFO_REMAIN"*/ hisTableName + whereBuilder);
		String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '" + param.get("TENANT_ID")
				+ "'  */ ";
		return sql + SQL();
	}

	/*
	 * delete remind orders from original order table
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */
	public String deleteRemindOrder(Map<String, Object> param) {
		String table = TEMP_TABLE;//getTableByChannelId((String) param.get("CHANNEL_ID"));

		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND CHANNEL_ID= '" + param.get("CHANNEL_ID") + "' ");
		whereBuilder.append(" AND ORDER_STATUS='7'");

		BEGIN();
		DELETE_FROM(table);
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/*
	 * update remind orders order_status from 7 to 5
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */
	public String updateRemindOrder(Map<String, Object> param) {
		String tableName = TEMP_TABLE;
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append("  AND CHANNEL_ID= '" + param.get("CHANNEL_ID")+ "' ");
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");

		BEGIN();
		//UPDATE("PLT_ORDER_INFO_REMAIN");
		UPDATE(tableName);
		SET("ORDER_STATUS = \'5\'");
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/*
	 * update order records to 7 flag for filter reserve order info
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 * 
	 * @param: RESERVE_SEQ_ID
	 */
	public String updateOrderInfoByReserve(Map<String, Object> param) {
		String table = TEMP_TABLE;//getTableByChannelId((String) param.get("CHANNEL_ID"));
		String hisTableName = (String)param.get("hisTableName");
		checkTableName(hisTableName);
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  a.ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append(" AND a.TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND a.CHANNEL_ID='" + param.get("CHANNEL_ID") + "'");
		//whereBuilder.append(" AND EXISTS (SELECT 1 FROM PLT_ORDER_INFO_REMAIN b WHERE a.USER_ID=b.USER_ID ");
		whereBuilder.append(" AND EXISTS (SELECT 1 FROM " + hisTableName +" b WHERE a.USER_ID=b.USER_ID ");
		whereBuilder.append(" AND b.ACTIVITY_SEQ_ID=" + param.get("RESERVE_SEQ_ID") + " AND b.CHANNEL_ID='");
		whereBuilder.append(param.get("CHANNEL_ID") + "')");

		BEGIN();
		UPDATE(table + " a ");

		SET("a.ORDER_STATUS = \'7\'");
		WHERE(whereBuilder.toString());
		String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '" + param.get("TENANT_ID")
				+ "'  */ ";
		return sql + SQL();
		// return SQL();
	}

	/*
	 * insert into filtered reserve orders to his table
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */
	public String insertFilterOrderByReserved(Map<String, Object> param) {
		String table = TEMP_TABLE;//getTableByChannelId((String) param.get("CHANNEL_ID"));
		String hisTable = (String)param.get("hisTableName");; // + "_HIS"; //2.0版本无  _HIS表,由路由模块分配
		checkTableName(hisTable);
//		if (param.get("CHANNEL_ID").equals("7")) {
//			SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM");
//			String currentMoth;
//			currentMoth = sdfYm.format(new Date()).substring(5, 7);
//			hisTable += "_";
//			hisTable += currentMoth;
//		}
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" select * FROM " + table + " where ");

		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND ORDER_STATUS='7'");

		BEGIN();
		INSERT_INTO(hisTable + whereBuilder);
		String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '" + param.get("TENANT_ID")
				+ "'  */ ";
		return sql + SQL();

	}

	/*
	 * delete filtered order info
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */
	public String deleteFilterOrderByReserved(Map<String, Object> param) {
		String table = TEMP_TABLE;//getTableByChannelId((String) param.get("CHANNEL_ID"));
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("ACTIVITY_SEQ_ID"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND ORDER_STATUS='7'");

		BEGIN();
		DELETE_FROM(table);
		WHERE(whereBuilder.toString());
		return SQL();
	}
	/*
	 * update order records to 4 flag for filter covered order info
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */

	public String updateCoveredRuleOrder(Map<String, Object> param) {
		String table = (String)param.get("tableName");//getTableByChannelId((String) param.get("CHANNEL_ID"));
		checkTableName(table);
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  a.ACTIVITY_SEQ_ID=" + param.get("beforeActivitySeq"));
		whereBuilder.append(" AND a.TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND a.CHANNEL_ID='" + param.get("CHANNEL_ID") + "'");
		whereBuilder.append(" AND a.ORDER_STATUS='5' AND a.CONTACT_CODE='0'");

		BEGIN();
		UPDATE(table + " a ");

		SET("a.ORDER_STATUS = \'4\'");
		WHERE(whereBuilder.toString());
		return SQL() + " LIMIT " + param.get("LIMIT_NUMBER");
	}

	/*
	 * insert into covered ruler orders to his table
	 * 
	 * @param: ACTIVITY_SEQ_ID
	 * 
	 * @param: TENANT_ID
	 * 
	 * @param: CHANNEL_ID
	 */
	public String insertCoveredOrder(Map<String, Object> param) {
		String table = (String)param.get("hisTableName");//getTableByChannelId((String) param.get("CHANNEL_ID"));
		String tableName = (String)param.get("tableName"); //工单所在的表名， 非临时表及历史表
		checkTableName(table);
		checkTableName(tableName);
		String hisTable = table; //+ "_HIS";
//		if (param.get("CHANNEL_ID").equals("7")) {
//			SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM");
//			String currentMoth;
//			currentMoth = sdfYm.format(new Date()).substring(5, 7);
//			hisTable += "_";
//			hisTable += currentMoth;
//		}
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" select * FROM " + tableName + " where ");

		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("beforeActivitySeq"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND ORDER_STATUS='4'");

		BEGIN();
		INSERT_INTO(hisTable + whereBuilder);
		String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '" + param.get("TENANT_ID")
				+ "'  */ ";
		return sql + SQL() + " LIMIT " + param.get("LIMIT_NUMBER");
	}

	/*
	 * update order records to special flag(3:in out ruler,2:touch filter ruler)
	 * for filter order info
	 * 
	 * @param: ParamMap
	 */
	public String updateOrderByUserList(ParamMap param) {
		String table = TEMP_TABLE;//getTableByChannelId(param.getCHANN_ID());
		StringBuilder whereBuilder = new StringBuilder();

		whereBuilder.append("  ACTIVITY_SEQ_ID = " + param.getREC_ID());
		whereBuilder.append(" AND " + param.getUSER_ID_SQL());
		whereBuilder.append(" AND CHANNEL_ID = '" + param.getCHANN_ID() + "'");
		whereBuilder.append(" AND  ORDER_STATUS ='0' ");
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");

		BEGIN();
		UPDATE(table);
		// special flag(3:in out ruler,2:touch filter ruler)
		SET("ORDER_STATUS = '" + param.getORDER_STATUS() + "'");
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/*
	 * insert into in out ruler orders to his table
	 * 
	 * @param: ParamMap
	 */
	public String insertOrderToHis(ParamMap param) {
		String table = param.getTaleName();//getTableByChannelId(param.getCHANN_ID());
		checkTableName(table);
		String hisTable = table; //+ "_HIS";    //2.0版本无  _HIS表,由路由模块分配
//		if (param.getCHANN_ID().equals("7")) {
//			SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM");
//			String currentMoth;
//			currentMoth = sdfYm.format(new Date()).substring(5, 7);
//			hisTable += "_";
//			hisTable += currentMoth;
//		}
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" select * FROM " + TEMP_TABLE + " where ");   //把临时表里满足过滤条件的工单移到工单过滤表中

		whereBuilder.append("  ACTIVITY_SEQ_ID = " + param.getREC_ID());
		whereBuilder.append(" AND CHANNEL_ID = '" + param.getCHANN_ID() + "'");
		whereBuilder.append(" AND  ORDER_STATUS ='" + param.getORDER_STATUS() + "'");
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");

		BEGIN();
		INSERT_INTO(hisTable + whereBuilder);
		String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '" + param.getTENANT_ID() + "'  */ ";
		return sql + SQL();
	}

	/*
	 * delete in out ruler orders
	 * 
	 * @param: ParamMap
	 */
	public String deleteOrders(ParamMap param) {
		String table = TEMP_TABLE;//getTableByChannelId(param.getCHANN_ID());
        checkTableName(table);
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID = " + param.getREC_ID());
		whereBuilder.append(" AND CHANNEL_ID = '" + param.getCHANN_ID() + "'");
		whereBuilder.append(" AND  ORDER_STATUS ='" + param.getORDER_STATUS() + "'");
		whereBuilder.append(" AND TENANT_ID = '" + param.getTENANT_ID() + "'");

		BEGIN();
		DELETE_FROM(table);
		WHERE(whereBuilder.toString());
		return SQL();
	}

	/*
	 * get touch order list in before batch
	 * 
	 * @param: ParamMap
	 */
	public String getTouchUserList(ParamMap param) {
		String table = param.getTaleName();// getTableByChannelId(param.getCHANN_ID());
        checkTableName(table);
		StringBuilder whereBuilder = new StringBuilder();

		whereBuilder.append("  ACTIVITY_SEQ_ID = " + param.getACTIVITY_SEQ_ID());
		whereBuilder.append(" AND REC_ID > " + param.getBEGIN_REC_ID());
		whereBuilder.append(" AND CHANNEL_ID = " + param.getCHANN_ID());
		whereBuilder.append(" AND  ORDER_STATUS ='5' AND CONTACT_CODE != '0'");
		whereBuilder.append(" AND CONTACT_DATE >= DATE_SUB(NOW(),INTERVAL " + param.getTOUCH_LIMIT_DAY() + " DAY)");
		whereBuilder.append("  AND TENANT_ID = '" + param.getTENANT_ID() + "'");

		BEGIN();
		SELECT("USER_ID,REC_ID");
		FROM(table);
		WHERE(whereBuilder.toString());
		String sql = SQL() + " LIMIT " + param.getLIMIT_NUMBER();
		logger.info(" [OrderCenter]:selectOrderUSERID:" + sql);
		return sql;
	}
	
	/**
	 * 从PLT_ACTIVITY_REMAIN_INFO表中查询某活动的某一批次下的指定的渠道的过滤的工单数
	 * @param map  参数map
	 * @return   过滤的工单数: SELECT SAVE_NUMBER FORM PLT_ACTIVITY_REMAIN_INFO WHERE CHANNEL_ID = ? AND ACTIVITY_ID = ? AND ACTIVITY_SEQ_ID = ? AND TENANT_ID = ? 
	 */
	public String getActivityPerChannelReserveCount(Map<String, Object> map) {
		String s="'";
		//进行查询操作使用的表名
		String table = "PLT_ACTIVITY_REMAIN_INFO";
		
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  CHANNEL_ID = " + s + map.get("CHANNEL_ID") + s);
		whereBuilder.append("  AND ACTIVITY_ID = " + s +map.get("ACTIVITY_ID") + s);
		whereBuilder.append("  AND TENANT_ID = " + s +map.get("TENANT_ID") + s);
		whereBuilder.append("  AND ACTIVITY_SEQ_ID = " + map.get("RESERVE_SEQ_ID"));
		BEGIN();
		SELECT("SAVE_NUMBER");
		FROM(table);
		WHERE(whereBuilder.toString());
		String sql = SQL();
		logger.info(" [OrderCenter]:getActivityPerChannelReserveCount:" + sql);
		return sql;
	}
	
	/**
	 * 根据活动Id 租户Id 渠道Id 活动批次Id busiType获取工单表名
	 * @param map
	 * @return
	 */
	public String getOrderTableName(ParamMap map) {
		StringBuilder whereBuilder = new StringBuilder();
		String s = "'";
		whereBuilder.append("  ACTIVITY_ID = " + s + map.getACTIVITY_ID() + s);
		whereBuilder.append("  AND ACTIVITY_SEQ_ID = " + map.getACTIVITY_SEQ_ID());
		whereBuilder.append("  AND CHANNEL_ID = " + s + map.getCHANN_ID() + s);
		whereBuilder.append("  AND TENANT_ID = " + s +map.getTENANT_ID() + s);
		whereBuilder.append("  AND BUSI_TYPE = " + map.getBusiType());
		BEGIN();
		SELECT("TABLE_NAME");
		FROM("PLT_ORDER_TABLES_ASSIGN_RECORD_INFO");
		WHERE(whereBuilder.toString());
		String sql = SQL();
		logger.info("[OrderTask]:getOrderTableName: " + sql);
		return sql;
	}
	
	/**
	 * 判断工单表面是否为空，如果为空抛出异常终止服务
	 * @param tableName
	 */
	public void checkTableName(String tableName) {	
		if(StringUtils.isBlank(tableName)){
			try {
				throw new Exception("获取的工单表名是空的，终止服务");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 更新plt_activity_process_log中有进有出过滤的工单的数量
	 * @param log
	 */
	public String UpdateInOutFilterCountToActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET(" INOUT_FILTER_AMOUNT=" + s + log.getINOUT_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 更新plt_activity_process_log中覆盖过滤的工单的数量
	 * @param log
	 * @return
	 */
	public String UpdateCoveredFilterCountToActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET("  COVERAGE_FILTER_AMOUNT=" + s + log.getCOVERAGE_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 更新plt_activity_process_log中接触过滤的工单的数量
	 * @param log
	 * @return
	 */
	public String UpdateTouchedFilterCountToActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET(" INOUT_FILTER_AMOUNT=" + s + log.getINOUT_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 更新plt_activity_process_log中留存过滤的工单的数量
	 * @param log
	 * @return
	 */
	public String UpdateReservedFilterCountToActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET("  RESERVE_FILTER_AMOUNT=" + s + log.getRESERVE_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 更新plt_activity_process_log中黑名单过滤的工单的数量
	 * @param log
	 * @return
	 */
	public String UpdateBlackUserFilterCountToActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET("  BLACK_FILTER_AMOUNT=" + s + log.getBLACK_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 更新plt_activity_process_log中成功过滤的工单的数量
	 * @param log
	 * @return
	 */
	public String UpdateSuccessFilterCountToActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET("  SUCCESS_FILTER_AMOUNT=" + s + log.getSUCCESS_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 更新plt_activity_process_log中删除重复工单过滤的数量
	 * @param log
	 * @return
	 */
	public String UpdateRepeateFilterCountToActivityProcessLog(ActivityProcessLog log) {
		String s = "'";
		BEGIN();
		UPDATE("PLT_ACTIVITY_PROCESS_LOG");
		SET("  REPEAT_FILTER_AMOUNT=" + s + log.getREPEAT_FILTER_AMOUNT() + s);
		WHERE(" ACTIVITY_SEQ_ID=" + s + log.getACTIVITY_SEQ_ID() + s + " and TENANT_ID=" + s + log.getTENANT_ID() + s
				+ " and CHANNEL_ID=" + s + log.getCHANNEL_ID() + s);
		return SQL();
	}
	
	/**
	 * 覆盖过滤方式二  更新工单步骤
	 * @param param
	 * @return
	 */
	public String updateCoveredRuleOrderV2(Map<String, Object> param) {
		String table = (String)param.get("tableName");//getTableByChannelId((String) param.get("CHANNEL_ID"));
		checkTableName(table);
		int lBeginRec = (int)param.get("lBeginRec");
		int lEndRec = (int)param.get("lEndRec");
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  a.ACTIVITY_SEQ_ID=" + param.get("beforeActivitySeq"));
		whereBuilder.append(" AND a.TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND a.CHANNEL_ID='" + param.get("CHANNEL_ID") + "'");
		whereBuilder.append(" AND a.ORDER_STATUS='5' AND a.CONTACT_CODE='0'");
		whereBuilder.append(" AND a.REC_ID>="+lBeginRec);
		whereBuilder.append(" AND a.REC_ID<="+lEndRec);
		BEGIN();
		UPDATE(table + " a ");

		SET("a.ORDER_STATUS = \'4\'");
		WHERE(whereBuilder.toString());
		return SQL();
	}
	
	/**
	 * 覆盖过滤方式二    移工单步骤
	 * @param param
	 * @return
	 */
	public String insertCoveredOrderV2(Map<String, Object> param) {
		String table = (String)param.get("hisTableName");//getTableByChannelId((String) param.get("CHANNEL_ID"));
		String tableName = (String)param.get("tableName"); //工单所在的表名， 非临时表及历史表
		checkTableName(table);
		checkTableName(tableName);
		int lBeginRec = (int)param.get("lBeginRec");
		int lEndRec = (int)param.get("lEndRec");
		String hisTable = table; //+ "_HIS";
//		if (param.get("CHANNEL_ID").equals("7")) {
//			SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM");
//			String currentMoth;
//			currentMoth = sdfYm.format(new Date()).substring(5, 7);
//			hisTable += "_";
//			hisTable += currentMoth;
//		}
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(" select * FROM " + tableName + " where ");

		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("beforeActivitySeq"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND CHANNEL_ID='" + param.get("CHANNEL_ID") + "'");
		whereBuilder.append(" AND ORDER_STATUS='4'");
		whereBuilder.append(" AND REC_ID>="+lBeginRec);
		whereBuilder.append(" AND REC_ID<="+lEndRec);

		BEGIN();
		INSERT_INTO(hisTable + whereBuilder);
		String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '" + param.get("TENANT_ID")
				+ "'  */ ";
		return sql + SQL();
	}
	
	/**
	 * 覆盖过滤方式二    删除工单步骤
	 * @param param
	 * @return
	 */
	public String deleteBeforeOrderV2(Map<String, Object> param) {
		String table = (String)param.get("tableName");//getTableByChannelId((String) param.get("CHANNEL_ID"));
		int lBeginRec = (int)param.get("lBeginRec");
		int lEndRec = (int)param.get("lEndRec");
		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append("  ACTIVITY_SEQ_ID=" + param.get("beforeActivitySeq"));
		whereBuilder.append(" AND TENANT_ID= '" + param.get("TENANT_ID") + "' ");
		whereBuilder.append(" AND CHANNEL_ID='" + param.get("CHANNEL_ID") + "'");
		whereBuilder.append("  AND  ORDER_STATUS ='4' ");
		whereBuilder.append(" AND REC_ID>="+lBeginRec);
		whereBuilder.append(" AND REC_ID<="+lEndRec);

		BEGIN();
		DELETE_FROM(table);
		WHERE(whereBuilder.toString());
		return SQL();
	}
}
