package com.bonc.busi.scene.mapper;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.*;
import com.bonc.busi.outer.bo.PltActivityInfo;
import org.apache.log4j.Logger;

import java.util.List;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class OrderOperation {
    private static final Logger logger = Logger.getLogger(OrderOperation.class);

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

    public String InsertActivityInfo(PltActivityInfo at) {
        String s = "'";
        BEGIN();
        INSERT_INTO(PLT_ACTIVITY_INFO);
        VALUES("REC_ID", s + at.getREC_ID() + s);
        VALUES("ACTIVITY_ID", s + at.getACTIVITY_ID() + s);
        //VALUES("ACTIVITY_DIVISION", s + at.getACTIVITY_DIVISION() + s);
        VALUES("ACTIVITY_NAME", s + at.getACTIVITY_NAME() + s);
        //VALUES("ACTIVITY_THEME", s + at.getACTIVITY_THEME() + s);
        //VALUES("ACTIVITY_THEMEID", s + at.getACTIVITY_THEMEID() + s);
        //VALUES("ACTIVITY_TYPE", s + at.getACTIVITY_TYPE() + s);

        // 活动开始日期"
        if (at.getBEGIN_DATE() != null) {
            VALUES("BEGIN_DATE", s + at.getBEGIN_DATE() + s);
            VALUES("ORDER_BEGIN_DATE", s + at.getBEGIN_DATE() + s);
        }

        if (at.getEND_DATE() != null) {
            VALUES("END_DATE", s + at.getEND_DATE() + s);
            VALUES("ORDER_END_DATE", s + at.getEND_DATE() + s);
        }

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
        //VALUES("GROUP_ID", s + at.getGROUP_ID() + s);
        VALUES("GROUP_NAME", s + at.getGROUP_NAME() + s);
        //VALUES("ACTIVITY_LEVEL", s + at.getACTIVITY_LEVEL() + s);
        //VALUES("PARENT_ACTIVITY", s + at.getPARENT_ACTIVITY() + s);
        //VALUES("POLICY_ID", s + at.getPOLICY_ID() + s);
        //if (at.getORDER_GEN_RULE() != null)
        ////	VALUES("ORDER_GEN_RULE", s + Integer.toString(at.getORDER_GEN_RULE()) + s);
        //if (at.getORDER_LIFE_CYCLE() != null)
        //	VALUES("ORDER_LIFE_CYCLE", s + Integer.toString(at.getORDER_LIFE_CYCLE()) + s);
        //if (at.getORDER_UPDATE_RULE() != null)
        //	VALUES("ORDER_UPDATE_RULE", s + Integer.toString(at.getORDER_UPDATE_RULE()) + s);
        //if (at.getFILTER_BLACKUSERLIST() != null)
        //	VALUES("FILTER_BLACKUSERLIST", s + Integer.toString(at.getFILTER_BLACKUSERLIST()) + s);
        //if (at.getFILTER_WHITEUSERLIST() != null)
        //	VALUES("FILTER_WHITEUSERLIST", s + Integer.toString(at.getFILTER_WHITEUSERLIST()) + s);
        //if (at.getDELETE_ACTIVITY_USER() != null)
        //	VALUES("DELETE_ACTIVITY_USER", s + Integer.toString(at.getDELETE_ACTIVITY_USER()) + s);
        //if (at.getDELETE_SUCCESSRULE_USER() != null)
        //	VALUES("DELETE_SUCCESSRULE_USER", s + Integer.toString(at.getDELETE_SUCCESSRULE_USER()) + s);
        //VALUES("IS_SENDORDER", s + at.getIS_SENDORDER() + s);
        VALUES("ORG_LEVEL", s + at.getORG_LEVEL() + s);
        //VALUES("OTHER_CHANNEL_EXERULE", s + at.getOTHER_CHANNEL_EXERULE() + s);
        //VALUES("SELF_SEND_CHANNEL_RULE", s + at.getSELF_SEND_CHANNEL_RULE() + s);
        //VALUES("STRATEGY_DESC", s + at.getSTRATEGY_DESC() + s);
        //VALUES("ECHANNEL_SHOW_RULE", s + at.getECHANNEL_SHOW_RULE() + s);

        //VALUES("PARENT_ACTIVITY_NAME", s + at.getPARENT_ACTIVITY_NAME() + s);
        //VALUES("PARENT_ACTIVITY_STARTDATE", s + at.getPARENT_ACTIVITY_STARTDATE() + s);
        //VALUES("PARENT_ACTIVITY_ENDDATE", s + at.getPARENT_ACTIVITY_ENDDATE() + s);
        //VALUES("PARENT_PROVID", s + at.getPARENT_PROVID() + s);
        VALUES("CREATOR_ORGID", s + at.getCREATOR_ORGID() + s);
        VALUES("CREATOR_ORG_PATH", s + at.getCREATOR_ORG_PATH() + s);
        //VALUES("USERGROUP_FILTERCON", s + at.getUSERGROUP_FILTERCON() + s);
        //if (at.getLAST_ORDER_CREATE_TIME() != null) {
        //	VALUES("LAST_ORDER_CREATE_TIME", s + at.getLAST_ORDER_CREATE_TIME() + s);
        //}
        VALUES("ACTIVITY_SOURCE", s + at.getACTIVITY_SOURCE() + s);
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
        VALUES("MARKET_WORDS", s + frontline.getMarketingWords() + s);
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

        VALUES("SUCESSCONDITIONE", s + success.getSuccessCondition() + s);
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
        logger.info("InsertSuccessStandardPo record sql:" + sql);
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
        VALUES("ISVALID", s + product.getIsvalid() + s);
        VALUES("ORD", s + product.getOrd() + s);

        VALUES("PRODUCTDISTRICT", s + product.getProductDistrict() + s);
        VALUES("ACTIVITY_SEQ_ID", s + product.getActivity_seq_id() + s);
        String sql = SQL();
        logger.info("InsertProduct record sql:" + sql);
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
        WHERE(where.toString());
        return SQL();

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


}
