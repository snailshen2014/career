package com.bonc.busi.orderschedule.bo;
/*
 * for channel special filter
 */
public class SpecialFilter {
	//condition sql
	private String sql;
	//recomend info
	private String recommend;
	private String smsTemplate;
	
	private String productId;
	private String productName;
	
	public void setSql (String sql){
		this.sql = sql;
	}
	public String getSql (){
		return this.sql;
	}
	
	public void setRecommend(String s){
		this.recommend = s;
	}
	public String getRecommend() {
		return this.recommend;
	}
	public void setSmsTemplate (String sms){
		this.smsTemplate = sms;
	}
	public String getSmsTemplate (){
		return this.smsTemplate;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
