package com.bonc.busi.activity;


/**
 * 
 * <p>Title: JEEAC - FilterCodeInfo </p>
 * 
 * <p>Description: 筛选条件码表(po) - clyx_filter_code </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class FilterCodeInfo extends StockMarketingPo {
	
	/**
	 * id
	 */
	private String codeId;
	
	/**
	 * 显示名称
	 */
	private String codeLabel;
	
	/**
	 * 名称
	 */
	private String codeName;
	
	/**
	 * 类型
	 */
	private String type;
	
	/**
	 * 排序
	 */
	private String ord;
	
	private String userCount;
	
	public String getCodeLabel() {
		return codeLabel;
	}

	public void setCodeLabel(String codeLabel) {
		this.codeLabel = codeLabel;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getOrd() {
		return ord;
	}

	public void setOrd(String ord) {
		this.ord = ord;
	}
	
}
