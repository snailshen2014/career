package com.bonc.busi.outer.service;

import java.util.HashMap;

import com.bonc.busi.outer.bo.RequestParamMap;

public interface StatusService {

	@Deprecated
	Object getActivityStatus(HashMap<String, Object> req);
	
	@Deprecated
	Object getActivityRecord(HashMap<String, Object> req);

	HashMap<String, Object> orderGenStatus(RequestParamMap req);

}
