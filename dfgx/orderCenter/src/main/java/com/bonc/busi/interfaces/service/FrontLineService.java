package com.bonc.busi.interfaces.service;

import java.util.HashMap;

import com.bonc.busi.interfaces.model.frontline.ContactHistoryResp;
import com.bonc.busi.interfaces.model.frontline.OrderQueryReq;

public interface FrontLineService {
	
	HashMap<String, Object> taskAll(HashMap<String, Object> req);
	
	HashMap<String, Object> orderQuery(OrderQueryReq req);
	
	HashMap<String, Object> custManagerStatistic(HashMap<String, Object> req);

	HashMap<String, Object> userQuery(HashMap<String, String> req);

	ContactHistoryResp contactHistory(HashMap<String, Object> req);

	HashMap<String, Object> activityStatistic(HashMap<String, Object> parseObject);
	
	void modifyUserInfo(HashMap<String, Object> map);
}

