package com.bonc.busi.orderschedule.bo;
/*
 * The respond when call resource distribute interface
 */

import java.util.List;

import com.bonc.busi.activity.SuccessProductPo;

public class ResourceRespond {
	private String rule_type_id;
	private String rule_type_name;
	private String res_type_id;
	private String draw_type_id;
	private String condition_resume;
	private String condition_sql;
	private String rule_type_sort;
	private List<SqlRun> sql_list;
	
	public void setRule_type_id(String id){
		this.rule_type_id = id;
	}
	public String getRule_type_id() {
		return rule_type_id;
	 }
	public void setRule_type_name(String na){
		this.rule_type_name = na;
	}
	public String getRule_type_name() {
		return rule_type_name;
	 }
	public void setRes_type_id(String id){
		this.res_type_id = id;
	}
	public String getRes_type_id() {
		return res_type_id;
	 }
	public void setCondition_resume(String con){
		this.condition_resume = con;
	}
	public String getCondition_resume() {
		return condition_resume;
	 }
	public void setDraw_type_id(String id){
		this.draw_type_id = id;
	}
	public String getDraw_type_id() {
		return draw_type_id;
	 }
	public void setCondition_sql(String sql){
		this.condition_sql = sql;
	}
	public String getCondition_sql() {
		return condition_sql;
	 }
	public void setRule_type_sort(String sort){
		this.rule_type_sort = sort;
	}
	public String getRule_type_sort() {
		return rule_type_sort;
	 }
	public void setSql_list(List<SqlRun> runlist){
		this.sql_list = runlist;
	}
	public List<SqlRun> getSql_list() {
		return sql_list;
	 }
}
