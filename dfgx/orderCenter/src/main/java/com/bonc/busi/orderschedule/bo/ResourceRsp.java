package com.bonc.busi.orderschedule.bo;

public class ResourceRsp {
	//{"condition_resume":"","condition_sql":"","draw_type_id":"","res_type_id":"","rule_type_id":"4","rule_type_name":"","temp_table":"tmp_2"}
	private String condition_resume;
	private String condition_sql;
	private String draw_type_id;
	private String res_type_id;
	private String rule_type_id;
	private String rule_type_name;
	private String temp_table;
	private String draw_business_id;
	public void setDraw_business_id(String id) {
		this.draw_business_id = id;
	}
	public String getDraw_business_id(){
		return this.draw_business_id;
	}
	public void setCondition_resume(String res){
		this.condition_resume = res;
	}
	public String getCondition_resume() {
		return condition_resume;
	}
	public void setCondition_sql(String sql){
		this.condition_sql = sql;
	}
	public String getCondition_sql() {
		return condition_sql;
	 }
	public void setDraw_type_id(String id){
		this.draw_type_id = id;
	}
	public String getDraw_type_id() {
		return draw_type_id;
	 }
	public void setRes_type_id(String id){
		this.res_type_id = id;
	}
	public String getRes_type_id() {
		return res_type_id;
	 }
	public void setRule_type_id(String id){
		this.rule_type_id = id;
	}
	public String getRule_type_id() {
		return rule_type_id;
	 }
	public void setRule_type_name(String name){
		this.rule_type_name = name;
	}
	public String getRule_type_name() {
		return rule_type_name;
	 }
	public void setTemp_table(String tab){
		this.temp_table = tab;
	}
	public String getTemp_table() {
		return temp_table;
	 }
}
