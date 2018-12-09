package com.bonc.busi.activity;



/**
 * 
 * <p>Title: JEEAC - ChannelCodeInfo </p>
 * 
 * <p>Description: 渠道信息(po) - clyx_channelcode_info </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */
@SuppressWarnings("serial")
public class ChannelCodeInfo extends StockMarketingPo {
	
	/**
	 * 渠道编码id
	 */
	private String channelId; 
	
	/**
	 * 渠道名称
	 */
	private String channelName;
	
	/**
	 * 渠道描述
	 */
	private String channelDesc; 
	
	/**
	 * 渠道类型 1：一级 2：二级 
	 */
	private String channelClassType;
	
	/**
	 * 排序
	 */
	private String ord;
	private String isMessage; // 特殊类型 1-短信类型 2-手厅 3-其它
	private String channelLevel; // 渠道优先级
	private String channelCode; // 渠道编码 集团短信:101,集团手厅:102,集团网厅:103,全国助销弹窗:104,本地短信:201,营业厅:202,网格经理:203,10010外呼:204
	private String channelShortName; // 渠道正式名称
	private String huashuContent; //话术内容
	private String url; //网厅，手厅,沃视的网址
	private String title; //网厅，手厅,沃视窗的标题
	private String imgUrl; //沃视窗的图片网址
	private String imgSize; //沃视窗的图片网址
	private String targetId; //偏好渠道id
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelDesc() {
		return channelDesc;
	}

	public void setChannelDesc(String channelDesc) {
		this.channelDesc = channelDesc;
	}

	public String getChannelClassType() {
		return channelClassType;
	}

	public void setChannelClassType(String channelClassType) {
		this.channelClassType = channelClassType;
	}

	public String getOrd() {
		return ord;
	}

	public void setOrd(String ord) {
		this.ord = ord;
	}

	public String getIsMessage() {
		return isMessage;
	}

	public void setIsMessage(String isMessage) {
		this.isMessage = isMessage;
	}

	public String getChannelLevel() {
		return channelLevel;
	}

	public void setChannelLevel(String channelLevel) {
		this.channelLevel = channelLevel;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelShortName() {
		return channelShortName;
	}

	public void setChannelShortName(String channelShortName) {
		this.channelShortName = channelShortName;
	}

	public String getHuashuContent() {
		return huashuContent;
	}

	public void setHuashuContent(String huashuContent) {
		this.huashuContent = huashuContent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getImgSize() {
		return imgSize;
	}

	public void setImgSize(String imgSize) {
		this.imgSize = imgSize;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
}
