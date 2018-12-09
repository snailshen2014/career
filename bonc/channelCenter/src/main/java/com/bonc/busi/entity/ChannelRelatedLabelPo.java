package com.bonc.busi.entity;

/** 
 * <p>Title: ChannelRelatedLabelPo</p>
 *
 * <p>Description: 渠道相关标签</p>
 *
 * <p>Company:北京东方国信科技股份有限公司 </p> 
 *
 * @author yangjiang
 * @version v1.0.0
 * @date 2016年1月5日
 */
public class ChannelRelatedLabelPo {
	private String channelId;
	private String labelId;
	private String labelName;
	private String channelName;
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
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}	
}
