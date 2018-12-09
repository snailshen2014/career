package com.bonc.busi.interfaces.service;

import java.util.HashMap;

public interface StatusService {

	@Deprecated
	Object getActivityStatus(HashMap<String, Object> req);
	
	@Deprecated
	Object getActivityRecord(HashMap<String, Object> req);

	HashMap<String, Object> orderGenStatus(HashMap<String, Object> req);
	
}
