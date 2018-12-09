package com.bonc.busi.entity;

import java.util.List;

public class ActivityBo  {
	/**
	 * 活动po
	 */
	private ActivityPo po = new ActivityPo();
	/**
	 * 父活动po
	 */
	private ActivityPo parentPo = new ActivityPo();  
	/**
	 * 父活动省分ids
	 */
	private String parentProvIds;
	/**
	 * 数据下发列表
	 */
	private List<DataSendingConfigPo> dataSendingConfigList;
	/**
	 * 用户群名称
	 */
	private String userGroupName;
	/**
	 * 产品列表
	 */
	private List<ProductInfo> productList;
	/**
	 * 目标渠道列表
	 */
	private List<ChannelInfo> channelInfoList;

	/**
	 * 渠道默认映射关系列表
	 */
	private List<ChannelRelatedLabelPo> channelRelateLabelList;

	/**
	 * 成本上传模板文件
	 */
	private byte[] templateFile;
	/**
	 * 渠道批次信息
	 */
	private List<String> channelBatchList;
	/**
	 * 上报成本信息
	 */
	private CostPo costInfo;

	/**
	 * 活动省份ID
	 */
	private String provIds;

	/**
	 * 活动周期
	 */
	private ActivityCycleInfo activityCycleInfo;
	/**
	 * 弹窗渠道信息保存
	 */
	private String popInfo;
	
	/**
	 * 不拆分活动列表
	 */
	private List<?> offLineList;
	
	

	public ActivityCycleInfo getActivityCycleInfo() {
		return activityCycleInfo;
	}

	public List<String> getChannelBatchList() {
		return channelBatchList;
	}

	public List<ChannelInfo> getChannelInfoList() {
		return channelInfoList;
	}
	
	public List<ChannelRelatedLabelPo> getChannelRelateLabelList() {
		return channelRelateLabelList;
	}

	public CostPo getCostInfo() {
		return costInfo;
	}

	public List<DataSendingConfigPo> getDataSendingConfigList() {
		return dataSendingConfigList;
	}

	public List<?> getOffLineList() {
		return offLineList;
	}

	public ActivityPo getParentPo() {
		return parentPo;
	}

	public String getParentProvIds() {
		return parentProvIds;
	}
	
	public ActivityPo getPo() {
		return po;
	}

	public String getPopInfo() {
		return popInfo;
	}

	public List<ProductInfo> getProductList() {
		return productList;
	}

	public String getProvIds() {
		return provIds;
	}

	public byte[] getTemplateFile() {
		return templateFile;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setActivityCycleInfo(ActivityCycleInfo activityCycleInfo) {
		this.activityCycleInfo = activityCycleInfo;
	}

	public void setChannelBatchList(List<String> channelBatchList) {
		this.channelBatchList = channelBatchList;
	}

	public void setChannelInfoList(List<ChannelInfo> channelInfoList) {
		this.channelInfoList = channelInfoList;
	}

	public void setChannelRelateLabelList(
			List<ChannelRelatedLabelPo> channelRelateLabelList) {
		this.channelRelateLabelList = channelRelateLabelList;
	}

	public void setCostInfo(CostPo costInfo) {
		this.costInfo = costInfo;
	}

	public void setDataSendingConfigList(
			List<DataSendingConfigPo> dataSendingConfigList) {
		this.dataSendingConfigList = dataSendingConfigList;
	}

	public void setOffLineList(List<?> offLineList) {
		this.offLineList = offLineList;
	}
	
	public void setParentPo(ActivityPo parentPo) {
		this.parentPo = parentPo;
	}

	public void setParentProvIds(String parentProvIds) {
		this.parentProvIds = parentProvIds;
	}

	public void setPo(ActivityPo po) {
		this.po = po;
	}

	public void setPopInfo(String popInfo) {
		this.popInfo = popInfo;
	}

	public void setProductList(List<ProductInfo> productList) {
		this.productList = productList;
	}

	public void setProvIds(String provIds) {
		this.provIds = provIds;
	}


	public void setTemplateFile(byte[] templateFile) {
		this.templateFile = templateFile;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}


}
