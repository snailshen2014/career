package com.bonc.busi.orderschedule.bo;

public class SpecialFilter {
	private String sql;
	private String recommend;
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
}
