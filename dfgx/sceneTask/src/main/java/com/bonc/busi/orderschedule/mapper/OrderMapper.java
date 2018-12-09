package com.bonc.busi.orderschedule.mapper;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

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

public interface OrderMapper {

	/**
	 * generate activity record
	 * 
	 * @param Activity
	 * @return
	 */
	@InsertProvider(type = OrderOperation.class, method = "InsertActivityInfo")
	void InsertActivityInfo(PltActivityInfo activity);
	
	@Insert("INSERT INTO PLT_ACTIVITY_CHANNEL_DETAIL(CHANN_ID,ACTIVITY_ID,IS_SEND_SMS,FILTER_CON,FILTER_SQL,TOUCHLIMITDAY,MARKET_WORDS,"
    		+ "SMS_WORDS,ORDERISSUEDRULE,SPECIALTYPE,TENANT_ID,VAILDATE,NUMBERLIMIT,TARGET,TITLE,"
    		+ "CONTENT,URL,SEND_LEVEL,NOSEND_TIME,START_TIME,END_TIME,TIMES,INTERVAL_HOUR,"
    		+ "MODEL_ID,IMAGE_URL,TYPE,PRODUCT_LIST,IMGSIZE,RESERVE1,RESERVE2,RESERVE3,"
    		+"BUSINESS_HALL_NAME,BUSINESS_HALL_ID,CHANNEL_SPECIALFILTER_LIST,WECHAT_INFO,WECHAT_STATUS,ACTIVITY_SEQ_ID) "
    		+ "VALUES (#{CHANN_ID},#{ACTIVITY_ID},#{IS_SEND_SMS},#{FILTER_CON},#{FILTER_SQL},#{TOUCHLIMITDAY},#{MARKET_WORDS},#{SMS_WORDS}"
    		+ ",#{ORDERISSUEDRULE},#{SPECIALTYPE},#{TENANT_ID},#{VAILDATE},#{NUMBERLIMIT},#{TARGET},#{TITLE},"
    		+ "#{CONTENT},#{URL},#{SEND_LEVEL},#{NOSEND_TIME},#{START_TIME},#{END_TIME},#{TIMES},"
    		+ "#{INTERVAL_HOUR},#{MODEL_ID},#{IMAGE_URL},#{TYPE},#{PRODUCT_LIST},#{IMGSIZE},#{RESERVE1},"
    		+ "#{RESERVE2},#{RESERVE3},#{BUSINESS_HALL_NAME},#{BUSINESS_HALL_ID},#{CHANNEL_SPECIALFILTER_LIST},#{WECHAT_INFO},#{WECHAT_STATUS},"
    		+"#{ACTIVITY_SEQ_ID}) ")
    public void insertChannelDetailInfo(PltActivityChannelDetail  channelDetail);
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
	void updateOrderFinishedStatus(PltActivityInfo at);

	@UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsFinishedStatus")
	void updateOrderSmsFinishedStatus(PltActivityInfo at);

	@InsertProvider(type = OrderOperation.class, method = "InsertChannelHandOffice")
	void InsertChannelHandOffice(ChannelHandOfficePo hand);

	@InsertProvider(type = OrderOperation.class, method = "InsertChannelWebOffice")
	void InsertChannelWebOffice(ChannelWebOfficePo web);

	@InsertProvider(type = OrderOperation.class, method = "InsertChannelWebchatInfo")
	void InsertChannelWebchatInfo(ChannelWebchatInfo chat);

	@InsertProvider(type = OrderOperation.class, method = "InsertChannelWebWoWindow")
	void InsertChannelWebWoWindow(ChannelWoWindowPo wo);

	@InsertProvider(type = OrderOperation.class, method = "InsertChannelMsm")
	void InsertChannelMsm(MsmChannelPo msm);

	@InsertProvider(type = OrderOperation.class, method = "InsertSuccessStandardPo")
	void InsertSuccessStandardPo(SuccessStandardPo success);

	@InsertProvider(type = OrderOperation.class, method = "InsertProduct")
	void InsertProduct(SuccessProductPo product);

	@InsertProvider(type = OrderOperation.class, method = "InsertGroupPop")
	void InsertGroupPop(ChannelGroupPopupPo pop);

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
	//@SelectProvider(type = OrderOperation.class, method = "getTouchLimitDayFromChannel")
	//String getTouchLimitDayFromChannel(Integer recID);

	
	

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

	@SelectProvider(type = OrderOperation.class, method = "getOrderNumByChannelId")
	String getOrderNumByChannelId(ActivityProcessLog log);
	
	/**
	 * 黑名单过滤，设置order_info表的order_status为1
	 * @param order
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderInfoStatusFilterBlack")
	void updateOrderInfoStatusFilterBlack(OrderAndOrderSMS order);

	/**
	 * 白名单过滤，设置order_info表的order_status为2
	 * @param order
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderInfoStatusFilterWhite")
	void updateOrderInfoStatusFilterWhite(OrderAndOrderSMS order);
	
	/**
	 * 黑名单过滤，设置order_sms表的order_status为1
	 * @param order
	 * @return
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsStatusFilterBlack")
	void updateOrderSmsStatusFilterBlack(OrderAndOrderSMS order);
	
	/**
	 * 白名单过滤，设置order_sms表的order_status为2
	 * @param order
	 * @return
	 */
	@UpdateProvider(type = OrderOperation.class, method = "updateOrderSmsStatusFilterWhite")
	void updateOrderSmsStatusFilterWhite(OrderAndOrderSMS order);
	
	/**
	 * 根据activity_seq_id查询PLT_ORDER_INFO各个字段的值
	 * @param param
	 * @return
	 */
	@SelectProvider(type = OrderOperation.class, method = "selectOrderInfoByActivitySeq")
	List<OrderAndOrderSMS> selectOrderInfoByActivitySeq(ParamMap param);
	
	/**
	 * 根据activity_seq_id查询PLT_ORDER_INFO_SMS各个字段的值
	 * @param param
	 * @return
	 */
	@SelectProvider(type = OrderOperation.class, method = "selectOrderSMSByActivitySeq")
	List<OrderAndOrderSMS> selectOrderSMSByActivitySeq(ParamMap param);
	/**
	 * 根据rec_id删除工单表（非短信）
	 * @param order
	 */
	@DeleteProvider(type = OrderOperation.class, method = "deleteOrderByRecId")
	void deleteOrderByRecId(OrderAndOrderSMS order);
	/**
	 * 根据rec_id删除短信工单表
	 * @param order
	 */
	@DeleteProvider(type = OrderOperation.class, method = "deleteOrderSmsByRecId")
	void deleteOrderSmsByRecId(OrderAndOrderSMS order);
	
	/*
	 * get invalid order activity_seq_id from plt_order_info table
	 * @param tenantId
	 * @return List<Integer>
	 */
	@SelectProvider(type = OrderOperation.class,method = "getInvalidActivitySeqId")
	List<Integer> getInvalidActivitySeqId(String tenantId);
	/*
	 * update invalid order records,set invalid_date=sysdate,input_date=sysdate,order_status='6'
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	@UpdateProvider(type = OrderOperation.class,method = "updateInvalidOrderRecords")
	void  updateInvalidOrderRecords(PltActivityInfo act);
	
	/*
	 * move invalid order records to his table
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	@InsertProvider(type = OrderOperation.class,method = "moveInvalidOrderRecords")
	void  moveInvalidOrderRecords(PltActivityInfo act);
	/*
	 * delete  invalid order records from plt_order_info
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	@DeleteProvider(type = OrderOperation.class,method = "deleteInvalidActivitySeqId")
	void deleteInvalidActivitySeqId(PltActivityInfo act);
	
	/*
	 * update activity_info table set activity_status='2' when order recores invalid
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	@UpdateProvider(type = OrderOperation.class,method = "updateActvityInfoInvalid")
	void  updateActvityInfoInvalid(PltActivityInfo act);
	/**
	 * 过滤工单
	 * */
	// 根据活动更新规则删除重复的工单记录
		@SelectProvider(type = OrderOperation.class, method = "selectActivityIdByActivitySEQID")
		String selectActivityIdByActivitySEQID(ParamMap param);
		
		@SelectProvider(type = OrderOperation.class, method = "selectUpdateRuleByActivity")
		List<ParamMap> selectUpdateRuleByActivity(ParamMap param);
		
		@SelectProvider(type = OrderOperation.class, method = "selectActivityForTouch")
		List<ParamMap> selectActivityForTouch(ParamMap param);

		// 优化后代码 根据活动标识得到工单表中的USERID(有进有出)
		@SelectProvider(type = OrderOperation.class, method = "selectOrderUSERID")
		List<String> selectOrderUSERID(ParamMap param);

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
		
		

	
}
