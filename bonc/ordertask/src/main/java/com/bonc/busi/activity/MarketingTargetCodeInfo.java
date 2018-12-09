package com.bonc.busi.activity;


/**
 * 
 * <p>Title: JEEAC - MarketingTargetCodeInfo </p>
 * 
 * <p>Description: 营销目标码表(po) - clyx_marketing_target </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class MarketingTargetCodeInfo extends StockMarketingPo {
	
	/**
	 * 目标id
	 */
	private String targetId;
	
	/**
	 * 目标名称
	 */
	private String targetName;
	
	/**
	 * 排序
	 */
	private String ord;

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getOrd() {
		return ord;
	}

	public void setOrd(String ord) {
		this.ord = ord;
	}
	
	
}
