package com.bonc.busi.orderschedule.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.bonc.busi.activity.ChannelGroupPopupPo;
import com.bonc.busi.activity.ChannelHandOfficePo;
import com.bonc.busi.activity.ChannelWebOfficePo;
import com.bonc.busi.activity.ChannelWebchatInfo;
import com.bonc.busi.activity.ChannelWoWindowPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.activity.MsmChannelPo;
import com.bonc.busi.activity.SuccessProductPo;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.orderschedule.bo.ActivityChannelStatus;
import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.bo.BlackWhiteUserList;
import com.bonc.busi.orderschedule.bo.Order;
import com.bonc.busi.orderschedule.bo.OrderAndOrderSMS;
import com.bonc.busi.orderschedule.bo.ParamMap;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.bo.PltActvityRemainInfo;
import com.bonc.busi.orderschedule.bo.WhiteBlackFilterUser;
import com.bonc.busi.task.bo.OrderCheckInfo;
import com.bonc.busi.task.bo.PltCommonLog;

public interface OrderMapper {

    /**
     * generate activity record
     *
     * @param Activity
     * @return
     */
    @InsertProvider(type = OrderOperation.class, method = "InsertActivityInfo")
    void InsertActivityInfo(PltActivityInfo activity);

    /**
     * xcloud datasource test
     *
     * @param
     * @return
     */

    @SelectProvider(type = OrderOperation.class, method = "SelectXcloud")
    void TestXcloud();

    /**
     * oracle datasource test
     *
     * @param
     * @return
     */

    @SelectProvider(type = OrderOperation.class, method = "SelectOracle")
    List<String> TestOracle();

    /**
     * judge black ,white user list
     *
     * @param user's id
     * @param filter type 01:blacklist,02:whitelist
     * @return user's id
     */
    @SelectProvider(type = OrderOperation.class, method = "isBlackWhiteUser")
    String isBlackWhiteUser(BlackWhiteUserList user);

    /**
     * get user group infomation from oracle by user group id.
     *
     * @param user group id
     * @return 0:success;-1 error
     */
    @SelectProvider(type = OrderOperation.class, method = "getUserGroupInfo")
    String getUserGroupInfo(String id);


    /**
     * load data to mysql from local dir
     *
     * @param sql
     * @return
     */
    @InsertProvider(type = OrderOperation.class, method = "loadDataToMysql")
    void loadDataToMysql(String sql);

    /**
     * generate Frontline channel record
     *
     * @param PltActivityChannelFrontline
     * @return
     */

    @InsertProvider(type = OrderOperation.class, method = "InsertChannelFrontline")
    void InsertChannelFrontline(FrontlineChannelPo front);

    /**
     * judge activity
     *
     * @param Activity
     * @return Activity
     */
    @SelectProvider(type = OrderOperation.class, method = "isActivityRun")
    String isActivityRun(PltActivityInfo at);

    @SelectProvider(type = OrderOperation.class, method = "getWechatStatus")
    String getWechatStatus(PltActivityChannelDetail detail);

    // DEPRECATED
    @UpdateProvider(type = OrderOperation.class, method = "updateWechatStatus")
    void updateWechatStatus(PltActivityChannelDetail detail);

    // DEPRECATED

    /**
     * get REC_ID from PLT_ACTIVITY_INFO by tenant_id ,activity_id
     *
     * @param Activity
     * @return REC_ID
     */
    @SelectProvider(type = OrderOperation.class, method = "getActivityRecid")
    Integer getActivityRecid(PltActivityInfo at);

    @UpdateProvider(type = OrderOperation.class, method = "updateActivityStatus")
    void updateActivityStatus(PltActivityInfo at);

    @UpdateProvider(type = OrderOperation.class, method = "updateOrderFinishedStatus")
    void updateOrderFinishedStatus(Order orderinfo);

    // DEPRECATED
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsFinishedStatus")
    void updateOrderSmsFinishedStatus(PltActivityInfo at);

    // DEPRECATED
    @InsertProvider(type = OrderOperation.class, method = "InsertChannelHandOffice")
    void InsertChannelHandOffice(ChannelHandOfficePo hand);

    // DEPRECATED
    @InsertProvider(type = OrderOperation.class, method = "InsertChannelWebOffice")
    void InsertChannelWebOffice(ChannelWebOfficePo web);

    @InsertProvider(type = OrderOperation.class, method = "InsertChannelWebchatInfo")
    void InsertChannelWebchatInfo(ChannelWebchatInfo chat);

    @InsertProvider(type = OrderOperation.class, method = "InsertChannelWebWoWindow")
    void InsertChannelWebWoWindow(ChannelWoWindowPo wo);

    // DEPRECATED
    @InsertProvider(type = OrderOperation.class, method = "InsertChannelMsm")
    void InsertChannelMsm(MsmChannelPo msm);

    @InsertProvider(type = OrderOperation.class, method = "InsertSuccessStandardPo")
    void InsertSuccessStandardPo(SuccessStandardPo success);

    @InsertProvider(type = OrderOperation.class, method = "InsertProduct")
    void InsertProduct(SuccessProductPo product);

    // DEPRECATED
    @InsertProvider(type = OrderOperation.class, method = "InsertGroupPop")
    void InsertGroupPop(ChannelGroupPopupPo pop);

    // for values fields contains "'" char
    @Insert("INSERT INTO PLT_ACTIVITY_CHANNEL_DETAIL(CHANN_ID,ACTIVITY_ID,IS_SEND_SMS,FILTER_CON,FILTER_SQL,TOUCHLIMITDAY,MARKET_WORDS,"
            + "SMS_WORDS,ORDERISSUEDRULE,SPECIALTYPE,TENANT_ID,VAILDATE,NUMBERLIMIT,TARGET,TITLE,"
            + "CONTENT,URL,SEND_LEVEL,NOSEND_TIME,START_TIME,END_TIME,TIMES,INTERVAL_HOUR,"
            + "MODEL_ID,IMAGE_URL,TYPE,PRODUCT_LIST,IMGSIZE,RESERVE1,RESERVE2,RESERVE3,"
            + "BUSINESS_HALL_NAME,BUSINESS_HALL_ID,CHANNEL_SPECIALFILTER_LIST,WECHAT_INFO,WECHAT_STATUS,CHANNEL_STATUS,ACTIVITY_SEQ_ID) "
            + "VALUES (#{CHANN_ID},#{ACTIVITY_ID},#{IS_SEND_SMS},#{FILTER_CON},#{FILTER_SQL},#{TOUCHLIMITDAY},#{MARKET_WORDS},#{SMS_WORDS}"
            + ",#{ORDERISSUEDRULE},#{SPECIALTYPE},#{TENANT_ID},#{VAILDATE},#{NUMBERLIMIT},#{TARGET},#{TITLE},"
            + "#{CONTENT},#{URL},#{SEND_LEVEL},#{NOSEND_TIME},#{START_TIME},#{END_TIME},#{TIMES},"
            + "#{INTERVAL_HOUR},#{MODEL_ID},#{IMAGE_URL},#{TYPE},#{PRODUCT_LIST},#{IMGSIZE},#{RESERVE1},"
            + "#{RESERVE2},#{RESERVE3},#{BUSINESS_HALL_NAME},#{BUSINESS_HALL_ID},#{CHANNEL_SPECIALFILTER_LIST},#{WECHAT_INFO},#{WECHAT_STATUS},"
            + "#{CHANNEL_STATUS},#{ACTIVITY_SEQ_ID}) ")
    public void insertChannelDetailInfo(PltActivityChannelDetail channelDetail);

    /**
     * get max data id from xcloud
     *
     * @param
     * @return
     */

    @InsertProvider(type = OrderOperation.class, method = "getMaxDataIdFromXcloud")
    String getMaxDataIdFromXcloud(String sql);

    // 根据活动ID选择增加记录，将PLT_ORDER_INFO中的记录写到 PLT_CHANNEL_ORDER_LIST
    @SelectProvider(type = OrderOperation.class, method = "getActivityIdByStatus")
    List<String> getActivityIdByStatus();

    @SelectProvider(type = OrderOperation.class, method = "getRECIdByActivityId")
    Integer getRECIdByActivityId(String activityId);

    /*
     * @SelectProvider(type = OrderOperation.class,method =
     * "selectPltOrderInfo") Order selectPltOrderInfo(Integer redId);
     */
    @SelectProvider(type = OrderOperation.class, method = "selectPltOrderInfo")
    List<Order> selectPltOrderInfo(Integer redId);

    @SelectProvider(type = OrderOperation.class, method = "selectSameOrderByUser")
    List<Integer> selectSameOrderByUser(ParamMap paramMap);

    @DeleteProvider(type = OrderOperation.class, method = "deleteOrderByrecId")
    void deleteOrderByrecId(Integer recId);

	/*
     * @UpdateProvider(type = OrderOperation.class,method = "updateOrderStatus")
	 * void updateOrderStatus(Integer recId);
	 */

    /**
     * update order_info.order_status=1
     *
     * @param order
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderStatusAfterFilter")
    void updateOrderStatusAfterFilter(Order order);

    /**
     * 查询黑名单所有需要过滤的RECID
     *
     * @return
     */
    @SelectProvider(type = OrderOperation.class, method = "getRECIdByFilterBlack")
    List<Integer> getRECIdByFilterBlack();

    /**
     * 查询白名单所有需要过滤的RECID
     *
     * @return
     */
    @SelectProvider(type = OrderOperation.class, method = "getRECIdByFilterWhite")
    List<Integer> getRECIdByFilterWhite();

    /*************************************************/
    @SelectProvider(type = OrderOperation.class, method = "getActivityIdByOGI")
    List<Integer> getActivityIdByOGI();

    // 客户经理渠道接触频次
    // @SelectProvider(type = OrderOperation.class, method =
    // "getTouchLimitDayFromChannel")
    // String getTouchLimitDayFromChannel(Integer recID);

    @UpdateProvider(type = OrderOperation.class, method = "updateOrderStatusByTouch")
    void updateOrderStatusByTouch(Integer recId);

    @DeleteProvider(type = OrderOperation.class, method = "cleanActivityInfo")
    void cleanActivityInfo(PltActivityInfo activity);

    @DeleteProvider(type = OrderOperation.class, method = "cleanChannelInfo")
    void cleanChannelInfo(PltActivityInfo activity);

    @DeleteProvider(type = OrderOperation.class, method = "cleanProductInfo")
    void cleanProductInfo(PltActivityInfo activity);

    @DeleteProvider(type = OrderOperation.class, method = "cleanSuccessInfo")
    void cleanSuccessInfo(PltActivityInfo activity);

    //DEPRECATED
    @InsertProvider(type = OrderOperation.class, method = "InsertActivityChannelStatus")
    void InsertActivityChannelStatus(ActivityChannelStatus status);

    @InsertProvider(type = OrderOperation.class, method = "InsertActivityProcessLog")
    void InsertActivityProcessLog(ActivityProcessLog log);

    @UpdateProvider(type = OrderOperation.class, method = "UpdateActivityProcessLog")
    void UpdateActivityProcessLog(ActivityProcessLog log);

    //适应2.0版本改造需求，增加了更新plt_activity_process_log表记录的方法
    @UpdateProvider(type = OrderOperation.class, method = "UpdateActivityProcessLogAddedOrderFilterAccountInfo")
    void UpdateActivityProcessLogAddedOrderFilterAccountInfo(ActivityProcessLog log);

    //DEPRECATED changing get number from order file ,causing too slow when order table big
    @SelectProvider(type = OrderOperation.class, method = "getOrderNumByChannelId")
    String getOrderNumByChannelId(ActivityProcessLog log);

    /**
     * 黑名单过滤，设置order_info表的order_status为1
     *
     * @param order
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderInfoStatusFilterBlack")
    void updateOrderInfoStatusFilterBlack(Map<String, Object> map);
    //void updateOrderInfoStatusFilterBlack(OrderAndOrderSMS order);

    /**
     * 白名单过滤，设置order_info表的order_status为2
     *
     * @param order
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderInfoStatusFilterWhite")
    void updateOrderInfoStatusFilterWhite(Map<String, Object> map);
    //void updateOrderInfoStatusFilterWhite(OrderAndOrderSMS order);

    /**
     * 黑名单过滤，设置order_sms表的order_status为1
     *
     * @param order
     * @return
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsStatusFilterBlack")
    void updateOrderSmsStatusFilterBlack(Map<String, Object> map);
    //void updateOrderSmsStatusFilterBlack(OrderAndOrderSMS order);

    /**
     * 白名单过滤，设置order_sms表的order_status为2
     *
     * @param order
     * @return
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsStatusFilterWhite")
    void updateOrderSmsStatusFilterWhite(Map<String, Object> map);
    //void updateOrderSmsStatusFilterWhite(OrderAndOrderSMS order);

    /**
     * 根据activity_seq_id查询PLT_ORDER_INFO各个字段的值
     *
     * @param param
     * @return
     */
    @SelectProvider(type = OrderOperation.class, method = "selectOrderInfoByActivitySeq")
    List<OrderAndOrderSMS> selectOrderInfoByActivitySeq(ParamMap param);

    /**
     * 根据activity_seq_id查询PLT_ORDER_INFO_SMS各个字段的值
     *
     * @param param
     * @return
     */
    @SelectProvider(type = OrderOperation.class, method = "selectOrderSMSByActivitySeq")
    List<OrderAndOrderSMS> selectOrderSMSByActivitySeq(ParamMap param);

    /**
     * 根据rec_id删除工单表（非短信）
     *
     * @param order
     */
    @DeleteProvider(type = OrderOperation.class, method = "deleteOrderByRecId")
    void deleteOrderByRecId(Map<String, Object> map);
    //void deleteOrderByRecId(OrderAndOrderSMS order);

    /**
     * 根据rec_id删除短信工单表
     *
     * @param order
     */
    @DeleteProvider(type = OrderOperation.class, method = "deleteOrderSmsByRecId")
    void deleteOrderSmsByRecId(Map<String, Object> map);
    //void deleteOrderSmsByRecId(OrderAndOrderSMS order);

    /*
     * get invalid order activity_seq_id from plt_order_info table
     *
     * @param tenantId
     *
     * @return List<Integer>
     */
    @SelectProvider(type = OrderOperation.class, method = "getInvalidActivitySeqId")
    List<PltActivityInfo> getInvalidActivitySeqId(String tenantId);

    // DEPRECATED
    /*
	 * update invalid order records,set
	 * invalid_date=sysdate,input_date=sysdate,order_status='6'
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 *
	 */
    @UpdateProvider(type = OrderOperation.class, method = "updateInvalidOrderRecords")
    void updateInvalidOrderRecords(PltActivityInfo act);

    // DEPRECATED
	/*
	 * move invalid order records to his table
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */
    @InsertProvider(type = OrderOperation.class, method = "moveInvalidOrderRecords")
    void moveInvalidOrderRecords(PltActivityInfo act);

    // DEPRECATED
	/*
	 * delete invalid order records from plt_order_info
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */
    @DeleteProvider(type = OrderOperation.class, method = "deleteInvalidActivitySeqId")
    void deleteInvalidActivitySeqId(PltActivityInfo act);

    /*
     * update activity_info table set activity_status='2' when order recores
     * invalid
     *
     * @param PltActivityInfo just using rec_id,tenant_id
     *
     * @return
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateActvityInfoInvalid")
    void updateActvityInfoInvalid(PltActivityInfo act);

    /**
     * 过滤工单
     */
    // 根据活动更新规则删除重复的工单记录
    @SelectProvider(type = OrderOperation.class, method = "selectActivityIdByActivitySEQID")
    String selectActivityIdByActivitySEQID(ParamMap param);

    @SelectProvider(type = OrderOperation.class, method = "selectUpdateRuleByActivity")
    List<ParamMap> selectUpdateRuleByActivity(ParamMap param);

    @SelectProvider(type = OrderOperation.class, method = "selectActivityForTouch")
    List<ParamMap> selectActivityForTouch(ParamMap param);

    // 优化后代码 根据活动标识得到工单表中的USERID(有进有出)
    @SelectProvider(type = OrderOperation.class, method = "selectOrderUSERID")
    List<OrderAndOrderSMS> selectOrderUSERID(ParamMap param);

    // 优化后代码 根据活动标识得到短信工单表中的USERID(有进有出)
    @SelectProvider(type = OrderOperation.class, method = "selectOrderSMSUSERID")
    List<String> selectOrderSMSUSERID(ParamMap param);

    // 根据USERID得到本次活动工单表一条记录(有进有出)
    @SelectProvider(type = OrderOperation.class, method = "selectOrderInfoByUserId")
    OrderAndOrderSMS selectOrderInfoByUserId(ParamMap param);

    // 根据USERID得到短信本次活动工单表一条记录(有进有出)
    @SelectProvider(type = OrderOperation.class, method = "selectOrderInfoSMSByUserId")
    OrderAndOrderSMS selectOrderInfoSMSByUserId(ParamMap param);

    // 覆盖用 工单表
    @SelectProvider(type = OrderOperation.class, method = "selectOrderForCover")
    List<OrderAndOrderSMS> selectOrderForCover(ParamMap param);

    // 覆盖用 短信工单表
    @SelectProvider(type = OrderOperation.class, method = "selectOrderSMSForCover")
    List<OrderAndOrderSMS> selectOrderSMSForCover(ParamMap param);

    // 下4个优化后代码保留
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderStatus")
    void updateOrderStatus(ParamMap param);

    @UpdateProvider(type = OrderOperation.class, method = "updateOrderSMSStatus")
    void updateOrderSMSStatus(ParamMap param);

    @InsertProvider(type = OrderOperation.class, method = "insertOrderInfoHis")
    void insertOrderInfoHis(Map<String, Object> map);
    //void insertOrderInfoHis(OrderAndOrderSMS in);

    @InsertProvider(type = OrderOperation.class, method = "insertOrderInfoSMSHis")
    void insertOrderInfoSMSHis(Map<String, Object> map);
    //void insertOrderInfoSMSHis(OrderAndOrderSMS in);

    // 客户经理渠道接触频次
    @SelectProvider(type = OrderOperation.class, method = "getTouchLimitDayFromChannel")
    String getTouchLimitDayFromChannel(ParamMap param);

    // 短信渠道接触频次
    @SelectProvider(type = OrderOperation.class, method = "getSMSTouchLimitDayFromChannel")
    String getSMSTouchLimitDayFromChannel(ParamMap param);

    // 客户经理渠道下得到工单表中的USER_ID,ORG_PATH,BEGIN_DATE
    @SelectProvider(type = OrderOperation.class, method = "getUserOrgFromOrder")
    List<ParamMap> getUserOrgFromOrder(ParamMap param);

    // 短信渠道下短信工单表中的USER_ID,ORG_PATH,BEGIN_DATE
    @SelectProvider(type = OrderOperation.class, method = "getUserOrgFromOrderSMS")
    List<ParamMap> getUserOrgFromOrderSMS(ParamMap param);

    // 客户经理渠道下每一用户下有接触历史的工单表
    @SelectProvider(type = OrderOperation.class, method = "getOrderByTouch")
    OrderAndOrderSMS getOrderByTouch(ParamMap param);

    // 短信渠道下每一用户下有接触历史的短信工单表
    @SelectProvider(type = OrderOperation.class, method = "getOrderSMSByTouch")
    OrderAndOrderSMS getOrderSMSByTouch(ParamMap param);

    /*
     * get white black user list from oracle db CLYX_ACTIVITY_FILTE_USERS
     *
     * @param
     *
     * @return List<WhiteBlackFilterUser>
     */
    @SelectProvider(type = OrderOperation.class, method = "getWhitBlackUserList")
    List<WhiteBlackFilterUser> getWhitBlackUserList();

    /*
     * get last log record time
     *
     * @param
     *
     * @return max time
     */
    @Select("SELECT DATE_FORMAT(max(START_TIME),'%Y-%m-%d %H:%i:%s') FROM  PLT_COMMON_LOG WHERE LOG_TYPE='55' ")
    String getLastLogTime();

    /*
     * get user label table's data status(1:changing,0:no changing)
     *
     * @param
     *
     * @return max time
     */
    @SelectProvider(type = OrderOperation.class, method = "getUserLabelDataStatus")
    String getUserLabelDataStatus(String sql);

    // *********过滤优化*************//
    // -- 当前活动批次-->活动ID --
    @SelectProvider(type = OrderOperation.class, method = "selectActivityByActivitySEQID")
    String selectActivityByActivitySEQID(Map<String, Object> map);

    // --活动ID-->该活动的各批次和更新规则--
    @SelectProvider(type = OrderOperation.class, method = "selectRuleByActivity")
    List<PltActivityInfo> selectRuleByActivity(Map<String, Object> map);

    // --查询上一批次工单 only for frontline channel cover,other channel order already
    // sent --
    @SelectProvider(type = OrderOperation.class, method = "selectLastOrder")
    List<OrderAndOrderSMS> selectLastOrder(Map<String, Object> param);

    // --批量删除上批次工单记录--
    @DeleteProvider(type = OrderOperation.class, method = "deleteBeforeOrder")
    void deleteBeforeOrder(Map<String, Object> param);

    /*
     * get activit seq id for checking activity reserve record
     *
     * @param: activityId
     *
     * @param: TenantId
     */
    @Select("SELECT  ACTIVITY_SEQ_ID FROM PLT_ACTIVITY_REMAIN_INFO "
            + " WHERE   ACTIVITY_ID =#{activityId}  AND TENANT_ID=#{TenantId} AND CHANNEL_ID=#{ChannelId}  LIMIT 1")
    public Integer getActivityRemainRecord(@Param("activityId") String activityId, @Param("TenantId") String TenantId,
                                           @Param("ChannelId") String ChannelId);

    /*
     * get activity reserve percent value
     *
     * @param: activityId
     *
     * @param: TenantId
     */
    @Select("SELECT  REMAIN_PERCENT FROM PLT_ACTIVITY_INFO "
            + " WHERE   ACTIVITY_ID =#{activityId}  AND TENANT_ID=#{TenantId} ORDER BY REC_ID DESC LIMIT 1")
    public String getActivityReservePercent(@Param("activityId") String activityId, @Param("TenantId") String TenantId);

    /*
     * get order number by area_no
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @SelectProvider(type = OrderOperation.class, method = "getOrderNumberByArea")
    public List<Map<String, Object>> getOrderNumberByArea(Map<String, Object> param);

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
    @UpdateProvider(type = OrderOperation.class, method = "updateRemindOrderByArea")
    public void updateRemindOrderByArea(Map<String, Object> param);

    /*
     * insert into remind orders to PLT_ORDER_INFO_REMIND table
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @InsertProvider(type = OrderOperation.class, method = "insertRemindOrder")
    void insertRemindOrder(Map<String, Object> param);

    /*
     * delete remind orders from original order table
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @InsertProvider(type = OrderOperation.class, method = "deleteRemindOrder")
    void deleteRemindOrder(Map<String, Object> param);

    /*
     * update remind orders order_status from 7 to 5
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @InsertProvider(type = OrderOperation.class, method = "updateRemindOrder")
    void updateRemindOrder(Map<String, Object> param);

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
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderInfoByReserve")
    public int updateOrderInfoByReserve(Map<String, Object> param);

    /*
     * insert into filtered reserve orders to his table
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @InsertProvider(type = OrderOperation.class, method = "insertFilterOrderByReserved")
    void insertFilterOrderByReserved(Map<String, Object> param);

    /*
     * delete filtered order info
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @InsertProvider(type = OrderOperation.class, method = "deleteFilterOrderByReserved")
    void deleteFilterOrderByReserved(Map<String, Object> param);

    /*
     * insert remain record info for filtering,when activity running next cycle
     */
    @Insert("INSERT INTO PLT_ACTIVITY_REMAIN_INFO(CHANNEL_ID,ACTIVITY_ID,ACTIVITY_SEQ_ID,TENANT_ID,SAVE_NUMBER,SAVE_TIME)  "
            + "VALUES (#{CHANNEL_ID},#{ACTIVITY_ID},#{ACTIVITY_SEQ_ID},#{TENANT_ID},#{SAVE_NUMBER},#{SAVE_TIME})")
    public void insertRemainInfo(PltActvityRemainInfo remain);

    /*
     * get channel order number
     *
     * @param: activity seq Id
     *
     * @param: TenantId
     *
     * @param: channel id
     */
    @Select("SELECT  ORI_AMOUNT FROM PLT_ACTIVITY_PROCESS_LOG "
            + " WHERE   ACTIVITY_SEQ_ID =#{seqid}  AND CHANNEL_ID=#{channelId} AND TENANT_ID=#{TenantId} ")
    public String getChannelOrderNumber(@Param("seqid") Integer seqId, @Param("channelId") String channelId,
                                        @Param("TenantId") String TenantId);

    /*
     * update order records to 4 flag for filter covered order info
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateCoveredRuleOrder")
    public int updateCoveredRuleOrder(Map<String, Object> param);

    /*
     * insert into covered ruler orders to his table
     *
     * @param: ACTIVITY_SEQ_ID
     *
     * @param: TENANT_ID
     *
     * @param: CHANNEL_ID
     */
    @InsertProvider(type = OrderOperation.class, method = "insertCoveredOrder")
    void insertCoveredOrder(Map<String, Object> param);

    /*
     * update order records to special flag(3:in out ruler,2:touch filter ruler)
         for filter order info
     *
     * @param: ParamMap
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateOrderByUserList")
    public int updateOrderByUserList(ParamMap param);

    /*
     * insert into  orders to his table
     *
     * @param: ParamMap
     */
    @InsertProvider(type = OrderOperation.class, method = "insertOrderToHis")
    void insertOrderToHis(ParamMap param);

    /*
     * delete in out ruler orders
     *
     * @param: ParamMap
     */
    @InsertProvider(type = OrderOperation.class, method = "deleteOrders")
    void deleteOrders(ParamMap param);

    /*
     * get touch order list in before batch
     *
     * @param: ParamMap
     */
    @SelectProvider(type = OrderOperation.class, method = "getTouchUserList")
    List<OrderAndOrderSMS> getTouchUserList(ParamMap param);

    /*
     * get filter black flag 0:no filter,1:filter
     *
     * @param: ParamMap
     */
    @Select("SELECT  FILTER_BLACKUSERLIST FROM PLT_ACTIVITY_INFO "
            + " WHERE   REC_ID =#{seqid}   AND TENANT_ID=#{TenantId} ")
    public Integer getFilterBlackUserFlag(@Param("seqid") Integer seqId,
                                          @Param("TenantId") String TenantId);

    @SelectProvider(type = OrderOperation.class, method = "getActivityPerChannelReserveCount")
    int getActivityPerChannelReserveCount(Map<String, Object> paramMap);


    @Select("SELECT COUNT(*) FROM PLT_ORDER_INFO_TEMP WHERE TENANT_ID = #{tenantId} AND  CHANNEL_ID = #{channelId}")
    int countTempTable(@Param("tenantId") String tenantId,@Param("channelId") String channelId);


    //根据条件查询获取工单表名
    @SelectProvider(type = OrderOperation.class, method = "getOrderTableName")
    public String getOrderTableName(ParamMap map);

    //更新plt_activity_process_log中有进有出过滤的工单的数量
    @UpdateProvider(type = OrderOperation.class, method = "UpdateInOutFilterCountToActivityProcessLog")
    void UpdateInOutFilterCountToActivityProcessLog(ActivityProcessLog log);

    //更新plt_activity_process_log中覆盖过滤的工单的数量
    @UpdateProvider(type = OrderOperation.class, method = "UpdateCoveredFilterCountToActivityProcessLog")
    void UpdateCoveredFilterCountToActivityProcessLog(ActivityProcessLog log);

    //更新plt_activity_process_log中接触过滤的工单的数量
    @UpdateProvider(type = OrderOperation.class, method = "UpdateTouchedFilterCountToActivityProcessLog")
    void UpdateTouchedFilterCountToActivityProcessLog(ActivityProcessLog log);

    //更新plt_activity_process_log中留存过滤的工单的数量
    @UpdateProvider(type = OrderOperation.class, method = "UpdateReservedFilterCountToActivityProcessLog")
    void UpdateReservedFilterCountToActivityProcessLog(ActivityProcessLog log);
    
  //更新plt_activity_process_log中黑名单过滤的工单的数量
    @UpdateProvider(type = OrderOperation.class, method = "UpdateBlackUserFilterCountToActivityProcessLog")
    void UpdateBlackUserFilterCountToActivityProcessLog(ActivityProcessLog log);
    
    //查询User黑白名单  0:黑名单     1：白名单
    @Select("SELECT USER_PHONE FROM PLT_ACTIVITY_FILTE_USERS WHERE FILTE_TYPE= #{filteType} and TENANT_ID=#{tenantId} and PARTITION_FLAG=#{flag}")
    List<String> getBlackUserIds(@Param("filteType") String filteType,@Param("tenantId") String tenantId,@Param("flag") int flag);

    //查询临时表里指定活动、批次、租户、渠道和状态的工单的数量
    @Select("SELECT COUNT(1) FROM PLT_ORDER_INFO_TEMP WHERE ACTIVITY_SEQ_ID =#{REC_ID} AND  TENANT_ID=#{TENANT_ID} "
    		+ "  AND CHANNEL_ID=#{CHANN_ID} AND ORDER_STATUS = #{ORDER_STATUS}")
	int getOrderCount(ParamMap param);

    //更新plt_activity_process_log中成功过滤的工单的数量
    @UpdateProvider(type = OrderOperation.class, method = "UpdateSuccessFilterCountToActivityProcessLog")
	void UpdateSuccessFilterCountToActivityProcessLog(ActivityProcessLog log);
    
    //更新plt_activity_process_log中成功过滤的工单的数量
    @UpdateProvider(type = OrderOperation.class, method = "UpdateRepeateFilterCountToActivityProcessLog")
	void UpdateRepeateFilterCountToActivityProcessLog(ActivityProcessLog log);

    ////向PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE 插入数据
    @Insert("INSERT INTO PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE(ACTIVITY_SEQ_ID,ACTIVITY_ID,TENANT_ID,CHANNEL_ID,STATUS,GEN_DATE) " +
            " VALUES (#{REC_ID},#{ACTIVITY_ID},#{TENANT_ID},#{CHANNELID},'1',#{LAST_ORDER_CREATE_TIME})")
    void insertChannelExecute(PltActivityInfo ac_obj);

    //查询重复的手机号
    @Select("SELECT PHONE_NUMBER,COUNT(PHONE_NUMBER) AS REPEAT_COUNT FROM "
    		+ "(SELECT PHONE_NUMBER,TENANT_ID FROM PLT_ORDER_INFO_TEMP WHERE CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId} ) TEMP "
    		+ " WHERE TEMP.TENANT_ID=#{tenantId} GROUP BY PHONE_NUMBER HAVING COUNT(PHONE_NUMBER) > 1")
	List<Map<String,Object>> queryRepeatedPhonePerChannel(@Param("channelId") String channelId, @Param("tenantId") String tenantId);

    //查询指定的手机号的最小的RecId
    @Select("SELECT MIN(REC_ID) FROM PLT_ORDER_INFO_TEMP WHERE PHONE_NUMBER=#{telPhone} AND CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId}")
	int queryMinRecId(@Param("telPhone") String telPhone, @Param("channelId") String channelId,@Param("tenantId")String tenantId);

    //删除重复的工单： 删除recId不等于最小的RecId的工单
    @Delete("DELETE FROM PLT_ORDER_INFO_TEMP WHERE REC_ID!=#{minRecId} AND PHONE_NUMBER=#{telPhone} AND CHANNEL_ID=#{channelId} AND TENANT_ID=#{tenantId}")
	void deleteRepatedOrder(@Param("minRecId")int minRecId, @Param("telPhone")String telPhone,@Param("channelId")String channelId, @Param("tenantId")String tenantId);

    //从plt_activity_process_logc查询工单数量
    @Select("SELECT CHANNEL_ID AS '渠道id',ORI_AMOUNT AS '原始工单数'," +
            "BLACK_FILTER_AMOUNT AS '黑名单过滤 '," +
            "SUCCESS_FILTER_AMOUNT AS '成功过滤数'," +
            "RESERVE_FILTER_AMOUNT AS '留存过滤数 '," +
            "INOUT_FILTER_AMOUNT AS '有进有出过滤数'," +
            "COVERAGE_FILTER_AMOUNT AS '覆盖过滤数' " +
            "FROM PLT_ACTIVITY_PROCESS_LOG WHERE TENANT_ID = #{tenantId} AND ACTIVITY_SEQ_ID = #{activitySeqId} AND ACTIVITY_ID = #{activityid} ")
    List<Map<String,Object>> countOrdersFromProcessLog(@Param("tenantId")String tenantId, @Param("activityid")String activityid, @Param("activitySeqId")Integer activitySeqId);
    
    /**
     * 查询指定批次渠道未执行的工单的数量以及最大、最小rec_id
     * @param tableName
     * @param activitySeqId
     * @param channelId
     * @param tenantId
     * @return
     */
    @Select("SELECT COUNT(1) as count,MAX(REC_ID) as maxRecId, MIN(REC_ID) as minRecId FROM ${tableName} WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=${activitySeqId} AND CHANNEL_ID=#{channelId} "
    		+ "AND ORDER_STATUS='5' AND CONTACT_CODE='0'")
	Map<String, Object> queryMinAndMaxRecId(@Param("tableName")String tableName, @Param("activitySeqId")Integer activitySeqId, @Param("channelId")String channelId, @Param("tenantId")String tenantId);

    /**
     * 覆盖过滤方式二  更新工单操作
     * @param param
     */
    @UpdateProvider(type = OrderOperation.class, method = "updateCoveredRuleOrderV2")
	int updateCoveredRuleOrderV2(Map<String, Object> param);

    /**
     * 覆盖过滤方式二  移工单操作
     * @param param
     */
    @InsertProvider(type = OrderOperation.class, method = "insertCoveredOrderV2")
	void insertCoveredOrderV2(Map<String, Object> param);
    
    /**
     * 覆盖过滤方式二  删除工单操作
     * @param param
     */
    @DeleteProvider(type = OrderOperation.class, method = "deleteBeforeOrderV2")
    void deleteBeforeOrderV2(Map<String, Object> param);
}
