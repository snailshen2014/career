package com.bonc.busi.activity;




/**
 * 
 * <p>Title: JEEAC - ActivityChannelInfo </p>
 * 
 * <p>Description: 活动渠道信息(po) - clyx_activity_channel </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class ActivityChannelInfo extends StockMarketingPo {
	
	/**
	 * 活动id
	 */
	private String activityId;
	
	/**
	 * 渠道id
	 */
	private String channelId;
	
	/**
	 * 筛选偏好id
	 */
	private String labelId;
	
	/**
	 * 用户数
	 */
	private Integer num;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}
	
}
