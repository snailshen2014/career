package com.bonc.busi.orderschedule.bo;

public class SqlRun {
	private String temp_table;
    private String target_data_type;
    private String target_data_res_url;
    private String target_username;
    private String target_password;
    
    public void setTemp_table(String tab){
    	this.temp_table = tab;
    }
    public String getTemp_table() {
    	return temp_table;
    }
    public void setTarget_data_type(String type){
    	this.target_data_type = type;
    }
    public String getTarget_data_type() {
    	return target_data_type;
    }
    public void setTarget_data_res_url(String url){
    	this.target_data_res_url = url;
    }
    public String getTarget_data_res_url() {
    	return target_data_res_url;
    }
    public void setTarget_username(String name){
    	this.target_username = name;
    }
    public String getTarget_username() {
    	return target_username;
    }
    public void setTarget_password(String pwd){
    	this.target_password = pwd;
    }
    public String getTarget_password() {
    	return target_password;
    }
}
