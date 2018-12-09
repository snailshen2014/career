package com.bonc.h2.pojo;

public class UserFlowInfoBean {
	//本月套餐剩余流量
	private int package_last_flow=0;
	//结转剩余流量  结转 表示之前没用完的，转入到本月的
	private int before_last_flow=0;
	//本月套餐剩余流量+结转剩余流量=总剩余流量
	private String total_last_flow;
	
	private String responseCode;
	
	
	public int getPackage_last_flow() {
		return package_last_flow;
	}
	public void setPackage_last_flow(int package_last_flow) {
		this.package_last_flow = package_last_flow;
	}
	public int getBefore_last_flow() {
		return before_last_flow;
	}
	public void setBefore_last_flow(int before_last_flow) {
		this.before_last_flow = before_last_flow;
	}
	public String getTotal_last_flow() {
		int total=package_last_flow+before_last_flow;
		double tra = (Double.valueOf(total))/1024;
		java.text.DecimalFormat df =new java.text.DecimalFormat("#.##");  
		this.total_last_flow = df.format(tra);
		return total_last_flow;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	
	
}
