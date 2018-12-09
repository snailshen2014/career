package com.bonc.busi.orderschedule.mapper;

import org.apache.ibatis.annotations.*;

import com.bonc.busi.activity.ChannelWebchatInfo;
import com.bonc.busi.activity.ChannelWoWindowPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;

import java.util.List;

public interface ActivityInfoMapper {
	/**
	 * judge activity
	 * 
	 * @return Activity
	 */
	@SelectProvider(type = ActivityInfoOperation.class, method = "isActivityRun")
	String isActivityRun(PltActivityInfo at);
	
	
	/**
	 * generate activity record
	 * 
	 * @return
	 */
	@InsertProvider(type = ActivityInfoOperation.class, method = "InsertActivityInfo")
	void InsertActivityInfo(PltActivityInfo activity);
	
	/**
	 * generate Frontline channel record
	 * 
	 * @return
	 */
	@InsertProvider(type = ActivityInfoOperation.class, method = "InsertChannelFrontline")
	void InsertChannelFrontline(FrontlineChannelPo front);

	
	// for values fields contains "'" char
	@Insert("INSERT INTO PLT_ACTIVITY_CHANNEL_DETAIL(CHANN_ID,ACTIVITY_ID,IS_SEND_SMS,FILTER_CON,FILTER_SQL,TOUCHLIMITDAY,MARKET_WORDS,"
			+ "SMS_WORDS,ORDERISSUEDRULE,SPECIALTYPE,TENANT_ID,VAILDATE,NUMBERLIMIT,TARGET,TITLE,"
			+ "CONTENT,URL,SEND_LEVEL,NOSEND_TIME,START_TIME,END_TIME,TIMES,INTERVAL_HOUR,"
			+ "MODEL_ID,IMAGE_URL,TYPE,PRODUCT_LIST,IMGSIZE,RESERVE1,RESERVE2,RESERVE3,RESERVE4,RESERVE5,RESERVE6,RESERVE7,RESERVE8,"
			+ "BUSINESS_HALL_NAME,BUSINESS_HALL_ID,CHANNEL_SPECIALFILTER_LIST,WECHAT_INFO,WECHAT_STATUS,CHANNEL_STATUS,ACTIVITY_SEQ_ID) "
			+ "VALUES (#{CHANN_ID},#{ACTIVITY_ID},#{IS_SEND_SMS},#{FILTER_CON},#{FILTER_SQL},#{TOUCHLIMITDAY},#{MARKET_WORDS},#{SMS_WORDS}"
			+ ",#{ORDERISSUEDRULE},#{SPECIALTYPE},#{TENANT_ID},#{VAILDATE},#{NUMBERLIMIT},#{TARGET},#{TITLE},"
			+ "#{CONTENT},#{URL},#{SEND_LEVEL},#{NOSEND_TIME},#{START_TIME},#{END_TIME},#{TIMES},"
			+ "#{INTERVAL_HOUR},#{MODEL_ID},#{IMAGE_URL},#{TYPE},#{PRODUCT_LIST},#{IMGSIZE},#{RESERVE1},"
			+ "#{RESERVE2},#{RESERVE3},#{RESERVE4},#{RESERVE5},#{RESERVE6},#{RESERVE7},#{RESERVE8},"
			+ "#{BUSINESS_HALL_NAME},#{BUSINESS_HALL_ID},#{CHANNEL_SPECIALFILTER_LIST},#{WECHAT_INFO},#{WECHAT_STATUS},"
			+ "#{CHANNEL_STATUS},#{ACTIVITY_SEQ_ID}) ")
	public void insertChannelDetailInfo(PltActivityChannelDetail channelDetail);

	
	@InsertProvider(type = ActivityInfoOperation.class, method = "InsertChannelWebchatInfo")
	void InsertChannelWebchatInfo(ChannelWebchatInfo wechat);


	@InsertProvider(type = ActivityInfoOperation.class, method = "InsertChannelWebWoWindow")
	void InsertChannelWebWoWindow(ChannelWoWindowPo wo);

	@DeleteProvider(type = ActivityInfoOperation.class, method = "cleanActivityInfo")
	void cleanActivityInfo(PltActivityInfo activity);

	@Select("SELECT * FROM PLT_ACTIVITY_CHANNEL_DETAIL WHERE TENANT_ID = #{TENANT_ID} AND ACTIVITY_ID = #{ACTIVITY_ID} " +
			"AND ACTIVITY_SEQ_ID = #{ACTIVITY_SEQ_ID} AND BUSINESS_HALL_ID = #{BUSINESS_HALL_ID}")
	List<PltActivityChannelDetail> queryChannelDetailInfo(PltActivityChannelDetail detail);
}
