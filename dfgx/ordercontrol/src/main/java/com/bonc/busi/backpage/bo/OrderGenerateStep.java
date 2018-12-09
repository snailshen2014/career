package com.bonc.busi.backpage.bo;

import java.io.Serializable;

//工单生成监控对应的Bean
public class OrderGenerateStep implements Serializable{

	private static final long serialVersionUID = -989655862935481949L;
	
	//步骤时间
	private String log_time;
	//步骤名称
	private String log_info;
	private String eND_DATE;
	private String cHANNEL_ID;
	private String bUSI_ITEM_1;
	public String getLog_time() {
		return log_time;
	}
	public void setLog_time(String log_time) {
		this.log_time = log_time;
	}
	public String getLog_info() {
		return log_info;
	}
	public void setLog_info(String log_info) {
		this.log_info = log_info;
	}
	public String geteND_DATE() {
		return eND_DATE;
	}
	public void seteND_DATE(String eND_DATE) {
		this.eND_DATE = eND_DATE;
	}
	public String getcHANNEL_ID() {
		return cHANNEL_ID;
	}
	public void setcHANNEL_ID(String cHANNEL_ID) {
		this.cHANNEL_ID = cHANNEL_ID;
	}
	public String getbUSI_ITEM_1() {
		return bUSI_ITEM_1;
	}
	public void setbUSI_ITEM_1(String bUSI_ITEM_1) {
		this.bUSI_ITEM_1 = bUSI_ITEM_1;
	}

}
