package com.bonc.busi.activity;

public class MappedChannelHuashu {
	private String id; //id
	private String activityId; //活动id
	private String huashu; //话术内容
	private String mappedChannelId;//配置渠道id
	
	/**
	 * @author liyang
	 * 添加网站字段，只有网厅渠道时，才可输入网址
	 * 
	 * 修改：
	 * 2016年3月1日
	 * 手厅，网厅，沃视窗渠道时，才可输入网址
	 * 并添加标题字段
	 * 
	 * 沃视窗添加图片url和图片尺寸
	 */
	private String url; 
	
	private String title;
	
	private String imgUrl;
	private String imgSize;
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getMappedChannelId() {
		return mappedChannelId;
	}
	public void setMappedChannelId(String mappedChannelId) {
		this.mappedChannelId = mappedChannelId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHuashu() {
		return huashu;
	}
	public void setHuashu(String huashu) {
		this.huashu = huashu;
	}
	
}
