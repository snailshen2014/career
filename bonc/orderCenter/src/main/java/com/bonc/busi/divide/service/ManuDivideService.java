package com.bonc.busi.divide.service;

import java.util.HashMap;
import java.util.List;

import com.bonc.busi.divide.model.DispatchReq;
import com.bonc.busi.divide.model.DividedResp;
import com.bonc.busi.divide.model.RulePreReq;

public interface ManuDivideService {

	HashMap<String, Object> divideActivityList(HashMap<String, Object> request);

	DividedResp dividedCellCount(HashMap<String, Object> pama);

	HashMap<String, Object> userDivideList(HashMap<String, Object> request);
	
	Object dispatchOrderList(HashMap<String, Object> request);

	void dispatchOrder(DispatchReq request);
	
	Object rulePreDivide(RulePreReq request);

	void confirmDivide(HashMap<String, Object> request);

	List<HashMap<String, String>> getAreaList(HashMap<String, Object> request);

}
