package com.bonc.busi.activity;
/**
 * 
 * <p>Title: JEEAC - MappedChannelInfo </p>
 * 
 * <p>Description: 渠道映射关系(po) </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */
public class MappedChannelInfo {
	private String id; // id
	private String sourceChannelId; // 源渠道id
	private String targetChannelId; // 目标渠道id
	private String activityId; //活动id
	/*private String enabled; // 是否选中
*/	/*private String normalSourceChannelId; // 源渠道id正式
	private String normalTargetChannelId; // 目标渠道id正式
*/	
	private String huashuId; //话术id
	
	
	
	public String getHuashuId() {
		return huashuId;
	}
	public void setHuashuId(String huashuId) {
		this.huashuId = huashuId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSourceChannelId() {
		return sourceChannelId;
	}
	public void setSourceChannelId(String sourceChannelId) {
		this.sourceChannelId = sourceChannelId;
	}
	public String getTargetChannelId() {
		return targetChannelId;
	}
	public void setTargetChannelId(String targetChannelId) {
		this.targetChannelId = targetChannelId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
}
