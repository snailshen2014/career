package com.bonc.busi.orderschedule.mapper;

import java.util.HashMap;
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
import com.bonc.busi.activity.TelePhoneChannelPo;
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
	 * @param user's
	 *            id
	 * @param filter
	 *            type 01:blacklist,02:whitelist
	 * @return user's id
	 */
	@SelectProvider(type = OrderOperation.class, method = "isBlackWhiteUser")
	String isBlackWhiteUser(BlackWhiteUserList user);

	/**
	 * get user group infomation from oracle by user group id.
	 * 
	 * @param user
	 *            group id
	 * @return 0:success;-1 error
	 */
	@SelectProvider(type = OrderOperation.class, method = "getUserGroupInfo")
	String getUserGroupInfo(String id);

	/**
	 * create order records on xcloud
	 * 
	 * @param sql
	 * @return
	 */
	@InsertProvider(type = OrderOperation.class, method = "genOrderRec")
	void genOrderRec(String sql) throws java.sql.SQLException;

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

	@UpdateProvider(type = OrderOperation.class, method = "updateWechatStatus")
	void updateWechatStatus(PltActivityChannelDetail detail);

	// DEPRECIATE
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

	// DEPRECIATE
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsFinishedStatus")
	void updateOrderSmsFinishedStatus(PltActivityInfo at);

	// DEPRECIATE
	@InsertProvider(type = OrderOperation.class, method = "InsertChannelHandOffice")
	void InsertChannelHandOffice(ChannelHandOfficePo hand);

	// DEPRECIATE
	@InsertProvider(type = OrderOperation.class, method = "InsertChannelWebOffice")
	void InsertChannelWebOffice(ChannelWebOfficePo web);

	@InsertProvider(type = OrderOperation.class, method = "InsertChannelWebchatInfo")
	void InsertChannelWebchatInfo(ChannelWebchatInfo chat);

	@InsertProvider(type = OrderOperation.class, method = "InsertChannelWebWoWindow")
	void InsertChannelWebWoWindow(ChannelWoWindowPo wo);
	
	@InsertProvider(type = OrderOperation.class, method = "InsertChannelTel")
	void InsertChannelTel(TelePhoneChannelPo tel);

	// DEPRECIATE
	@InsertProvider(type = OrderOperation.class, method = "InsertChannelMsm")
	void InsertChannelMsm(MsmChannelPo msm);

	@InsertProvider(type = OrderOperation.class, method = "InsertSuccessStandardPo")
	void InsertSuccessStandardPo(SuccessStandardPo success);

	@InsertProvider(type = OrderOperation.class, method = "InsertProduct")
	void InsertProduct(SuccessProductPo product);

	// DEPRECIATE
	@InsertProvider(type = OrderOperation.class, method = "InsertGroupPop")
	void InsertGroupPop(ChannelGroupPopupPo pop);

	// for values fields contains "'" char
	@Insert("INSERT INTO PLT_ACTIVITY_CHANNEL_DETAIL(CHANN_ID,ACTIVITY_ID,IS_SEND_SMS,FILTER_CON,FILTER_SQL,TOUCHLIMITDAY,MARKET_WORDS,"
			+ "SMS_WORDS,ORDERISSUEDRULE,SPECIALTYPE,TENANT_ID,VAILDATE,NUMBERLIMIT,TARGET,TITLE,"
			+ "CONTENT,URL,SEND_LEVEL,NOSEND_TIME,START_TIME,END_TIME,TIMES,INTERVAL_HOUR,"
			+ "MODEL_ID,IMAGE_URL,TYPE,PRODUCT_LIST,IMGSIZE,RESERVE1,RESERVE2,RESERVE3,"
			+ "BUSINESS_HALL_NAME,BUSINESS_HALL_ID,CHANNEL_SPECIALFILTER_LIST,WECHAT_INFO,WECHAT_STATUS,CHANNEL_STATUS"
			+ ",ACTIVITY_SEQ_ID,MESS_NODISTURB_CODE,MESS_ORDER_CODE,MESS_EFFECTIVE_TIME,ORDER_FAILURE_REPLY,ORDER_OVERTIMEREPLY_REPLY) "
			+ "VALUES (#{CHANN_ID},#{ACTIVITY_ID},#{IS_SEND_SMS},#{FILTER_CON},#{FILTER_SQL},#{TOUCHLIMITDAY},#{MARKET_WORDS},#{SMS_WORDS}"
			+ ",#{ORDERISSUEDRULE},#{SPECIALTYPE},#{TENANT_ID},#{VAILDATE},#{NUMBERLIMIT},#{TARGET},#{TITLE},"
			+ "#{CONTENT},#{URL},#{SEND_LEVEL},#{NOSEND_TIME},#{START_TIME},#{END_TIME},#{TIMES},"
			+ "#{INTERVAL_HOUR},#{MODEL_ID},#{IMAGE_URL},#{TYPE},#{PRODUCT_LIST},#{IMGSIZE},#{RESERVE1},"
			+ "#{RESERVE2},#{RESERVE3},#{BUSINESS_HALL_NAME},#{BUSINESS_HALL_ID},#{CHANNEL_SPECIALFILTER_LIST},#{WECHAT_INFO},#{WECHAT_STATUS},"
			+ "#{CHANNEL_STATUS},#{ACTIVITY_SEQ_ID},#{messNodisturbCode},#{messOrderCode},#{messEffectiveTime},#{orderFailureReply},#{orderOvertimeReply}) ")
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

	@InsertProvider(type = OrderOperation.class, method = "InsertActivityChannelStatus")
	void InsertActivityChannelStatus(ActivityChannelStatus status);

	@InsertProvider(type = OrderOperation.class, method = "InsertActivityProcessLog")
	void InsertActivityProcessLog(ActivityProcessLog log);

	@UpdateProvider(type = OrderOperation.class, method = "UpdateActivityProcessLog")
	void UpdateActivityProcessLog(ActivityProcessLog log);

	//DEPRECATED changing get number from order file ,causing too slow when order table big
	@SelectProvider(type = OrderOperation.class, method = "getOrderNumByChannelId")
	String getOrderNumByChannelId(ActivityProcessLog log);

	/**
	 * 黑名单过滤，设置order_info表的order_status为1
	 * 
	 * @param order
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderInfoStatusFilterBlack")
	void updateOrderInfoStatusFilterBlack(OrderAndOrderSMS order);

	/**
	 * 白名单过滤，设置order_info表的order_status为2
	 * 
	 * @param order
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderInfoStatusFilterWhite")
	void updateOrderInfoStatusFilterWhite(OrderAndOrderSMS order);

	/**
	 * 黑名单过滤，设置order_sms表的order_status为1
	 * 
	 * @param order
	 * @return
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsStatusFilterBlack")
	void updateOrderSmsStatusFilterBlack(OrderAndOrderSMS order);

	/**
	 * 白名单过滤，设置order_sms表的order_status为2
	 * 
	 * @param order
	 * @return
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsStatusFilterWhite")
	void updateOrderSmsStatusFilterWhite(OrderAndOrderSMS order);

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
	void deleteOrderByRecId(OrderAndOrderSMS order);

	/**
	 * 根据rec_id删除短信工单表
	 * 
	 * @param order
	 */
	@DeleteProvider(type = OrderOperation.class, method = "deleteOrderSmsByRecId")
	void deleteOrderSmsByRecId(OrderAndOrderSMS order);

	/*
	 * get invalid order activity_seq_id from plt_order_info table
	 * 
	 * @param tenantId
	 * 
	 * @return List<Integer>
	 */
	@SelectProvider(type = OrderOperation.class, method = "getInvalidActivitySeqId")
	List<PltActivityInfo> getInvalidActivitySeqId(String tenantId);

	// DEPRECIATE
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

	// DEPRECIATE
	/*
	 * move invalid order records to his table
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */
	@InsertProvider(type = OrderOperation.class, method = "moveInvalidOrderRecords")
	void moveInvalidOrderRecords(PltActivityInfo act);

	// DEPRECIATE
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
	void insertOrderInfoHis(OrderAndOrderSMS in);

	@InsertProvider(type = OrderOperation.class, method = "insertOrderInfoSMSHis")
	void insertOrderInfoSMSHis(OrderAndOrderSMS in);

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
//	@SelectProvider(type = OrderOperation.class, method = "getWhitBlackUserList")
//	List<WhiteBlackFilterUser> getWhitBlackUserList(int begin,int end);
	/*
	 * get white black user list from oracle db CLYX_ACTIVITY_FILTE_USERS
	 * 
	 * @param
	 * 
	 * @return List<WhiteBlackFilterUser>
	 */
	@Select("SELECT  t.USER_ID,t.USER_PHONE,t.FILTE_TYPE FROM (select rownum rn,USER_ID,USER_PHONE,FILTE_TYPE from CLYX_ACTIVITY_FILTE_USERS) t "
			+ " WHERE   t.rn between #{begin}   AND #{end} ")
	public List<WhiteBlackFilterUser> getWhitBlackUserList(@Param("begin") int begin, 
			@Param("end") int end);
	
	/*
	 * get white black user size from oracle db CLYX_ACTIVITY_FILTE_USERS
	 * 
	 * @param
	 * 
	 * @return List<WhiteBlackFilterUser>
	 */
	@SelectProvider(type = OrderOperation.class, method = "getWhitBlackUserSize")
	Integer getWhitBlackUserSize();
	

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
	public void updateOrderInfoByReserve(Map<String, Object> param);

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
	@Select("SELECT  CHANNEL_ORDER_NUM FROM PLT_ACTIVITY_PROCESS_LOG "
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
	public void updateCoveredRuleOrder(Map<String, Object> param);

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
	public void updateOrderByUserList(ParamMap param);

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

	@Select("SELECT DATE_ID FROM PLT_SUCCESS_PROCESS_LOG "
			+"WHERE PROCESS_TIME < #{dateTime} AND TENANT_ID =#{tenantId} ORDER BY PROCESS_TIME DESC  LIMIT 1")
			public String getReviewDateId(@Param("dateTime") String dateTime,@Param("tenantId") String tenantId);



	@Insert("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076' */INSERT INTO PLT_ORDER_INFO_POPWIN_0 " +
			" SELECT *  FROM PLT_ORDER_INFO_POPWIN WHERE TENANT_ID = #{tenantId} AND ACTIVITY_SEQ_ID = #{activitySeqId}   AND RIGHT(PHONE_NUMBER,1) REGEXP'[^0-9]' ")
	void movePopWinOrderByNotNum(@Param("tenantId") String tenantId,@Param("activitySeqId") Integer activitySeqId);

	/**
	 * 根据尾号获取 recid 最大值 recid最小值 总数
	 * @param tenantId
	 * @param activitySeqId
	 * @param j
	 * @return
	 */
	@Select(" SELECT MAX(REC_ID) AS MAX,MIN(REC_ID) AS MIN,COUNT(1) AS ROW FROM PLT_ORDER_INFO_POPWIN WHERE TENANT_ID = #{tenantId}  AND ACTIVITY_SEQ_ID = #{activitySeqId} AND  RIGHT(PHONE_NUMBER,1) = #{tailNum} ")
	@Results({
			@Result(property = "MAX",column = "MAX",javaType = Integer.class),
			@Result(property = "MIN",column = "MIN",javaType = Integer.class),
			@Result(property = "ROW",column = "ROW",javaType = Integer.class)
	})
	HashMap<String,Integer> getRecIdCount(@Param("tenantId")String tenantId,@Param("activitySeqId") Integer activitySeqId, @Param("tailNum")int j);

	@Select("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */INSERT INTO PLT_ORDER_INFO_POPWIN_#{tailNum} SELECT * FROM PLT_ORDER_INFO_POPWIN WHERE " +
			"   ACTIVITY_SEQ_ID = #{activitySeqId} AND  RIGHT(PHONE_NUMBER,1) = #{tailNum} " +
			" AND REC_ID >= #{min} AND REC_ID < #{curMax}")
	void movePopWinOrderByTailNum(@Param("tenantId")String tenantId,@Param("activitySeqId") Integer activitySeqId, @Param("min") Integer min,@Param("curMax") Integer curMax,@Param("tailNum")int j);


	@Select("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */SELECT COUNT(1) AS ROW FROM PLT_ORDER_INFO_POPWIN_#{tailNum} WHERE ACTIVITY_SEQ_ID = #{activitySeqId}")
	@Result(property = "ROW",column = "ROW",javaType = Integer.class)
	Integer countPopWinTable(@Param("activitySeqId") Integer activitySeqId, @Param("tailNum")int j);

	@Select("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE REC_ID IN(SELECT DISTINCT(ACTIVITY_SEQ_ID) FROM PLT_ORDER_INFO_POPWIN )")
	@Result(property = "REC_ID",column = "REC_ID",javaType = Integer.class)
	List<Integer> getValidPopWinSeqId();


	@Delete("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */ DELETE FROM PLT_ORDER_INFO_POPWIN")
    void deletePopWin();

	@Delete("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'uni076'  */DELETE FROM  PLT_ORDER_INFO_POPWIN WHERE " +
			"   ACTIVITY_SEQ_ID = #{activitySeqId} AND  RIGHT(PHONE_NUMBER,1) = #{tailNum} " +
			" AND REC_ID >= #{min} AND REC_ID < #{curMax}")
	void deletePopWinOrderByTailNum(@Param("tenantId")String tenantId,@Param("activitySeqId") Integer activitySeqId, @Param("min") Integer min,@Param("curMax") Integer curMax,@Param("tailNum")int j);
}
