package com.bonc.busi.scene.mapper;

import com.bonc.busi.activity.*;
import com.bonc.busi.outer.bo.PltActivityInfo;
import com.bonc.busi.outer.model.PltActivityChannelDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderMapper {

	/**
	 * generate activity record
	 * 
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
    public void insertChannelDetailInfo(PltActivityChannelDetail channelDetail);





	/**
	 * generate Frontline channel record
	 *
	 * @return
	 */
	@InsertProvider(type = OrderOperation.class, method = "InsertChannelFrontline")
	void InsertChannelFrontline(FrontlineChannelPo front);






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




}
