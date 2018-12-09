package com.bonc.busi.track.service;

import java.util.HashMap;

public interface ActivityTrackService {

	
	//---------------新接口---------------------
	
	Object getActvityStatistic(HashMap<String, Object> req);

	Object getChannelStatistic(HashMap<String, Object> req);

	Object getOrderhistory(HashMap<String, Object> req);

	Object getUpdateRecord(HashMap<String, Object> req);
	
}
