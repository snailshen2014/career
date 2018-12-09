package com.bonc.busi.activity;

/**
 * 渠道特殊筛选规则
 * @author ICE
 *
 */
public class ChannelSpecialFilterPo {

	/**
	 * 渠道类型
	 */
	private String ChannelType;
	
	/**
	 * 筛选名称
	 */
	private String filterName;
	/**
	 * 筛选条件
	 */
	private String filterCondition;
	/**
	 * 筛选条件sql
	 */
	private String filterConditionSql;
	/**
	 * 营销话术
	 */
	private String marketingWords;
	/**
	 * 推荐信息
	 */
	private String recommenedInfo;
	/**
	 * 排序
	 */
	private String ord;
	public String getRecommenedInfo() {
		return recommenedInfo;
	}
	public void setRecommenedInfo(String recommenedInfo) {
		this.recommenedInfo = recommenedInfo;
	}
	public String getChannelType() {
		return ChannelType;
	}
	public void setChannelType(String channelType) {
		ChannelType = channelType;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public String getFilterCondition() {
		return filterCondition;
	}
	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}
	public String getMarketingWords() {
		return marketingWords;
	}
	public void setMarketingWords(String marketingWords) {
		this.marketingWords = marketingWords;
	}
	public String getOrd() {
		return ord;
	}
	public void setOrd(String ord) {
		this.ord = ord;
	}
	public String getFilterConditionSql() {
		return filterConditionSql;
	}
	public void setFilterConditionSql(String filterConditionSql) {
		this.filterConditionSql = filterConditionSql;
	}
	
	
	
}
