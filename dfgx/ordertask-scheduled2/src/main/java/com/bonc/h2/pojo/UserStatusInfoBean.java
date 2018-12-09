package com.bonc.h2.pojo;

import java.util.HashMap;
import java.util.Map;

public class UserStatusInfoBean {
	//服务状态编码
	private String serviceType;
	//服务状态编码 中文描述
	private String serviceTypeDesc;
	
	private String responseCode;
	
	private Map<String,String> map;
	
	public UserStatusInfoBean(){
		this.map=new HashMap();
		map.put("0", "开通");
		map.put("1", "申请停机");
		map.put("2", "挂失停机");
		map.put("3", "并机停机");
		map.put("4", "局方停机");
		map.put("5", "欠费停机");
		map.put("6", "申请销号");
		map.put("7", "高额停机");
		map.put("8", "欠费预销号");
		map.put("9", "欠费销号");
	}
	
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceTypeDesc() {
		this.serviceTypeDesc = map.get(serviceType);
		return serviceTypeDesc;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	
}
