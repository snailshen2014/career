package com.bonc.busi.outer.service;

import java.util.HashMap;
import java.util.List;

public interface ActivityTrackService {

	
	//---------------新接口---------------------
	
	Object getActvityStatistic(HashMap<String, Object> req);

	Object getChannelStatistic(HashMap<String, Object> req);

	Object getOrderhistory(HashMap<String, Object> req);

	Object getUpdateRecord(HashMap<String, Object> req);
	List<HashMap<String, Object>> allocation(HashMap<String, Object> dataMap);
	
}
