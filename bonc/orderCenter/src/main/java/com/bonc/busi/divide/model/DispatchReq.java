package com.bonc.busi.divide.model;

import java.util.HashMap;

import com.bonc.busi.interfaces.model.ReqHeader;

public class DispatchReq extends ReqHeader{


	private String activityId; // 活动ID
	private HashMap<String, String> isExe;
	private HashMap<String, String> orderMap;
	
	
	public HashMap<String, String> getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(HashMap<String, String> orderMap) {
		this.orderMap = orderMap;
	}

	public HashMap<String, String> getIsExe() {
		return isExe;
	}

	public void setIsExe(HashMap<String, String> isExe) {
		this.isExe = isExe;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

}
