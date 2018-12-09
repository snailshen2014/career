package com.bonc.busi.interfaces.service;

import java.util.HashMap;

import com.bonc.busi.interfaces.model.alertwin.ActivityQueryReq;
import com.bonc.busi.interfaces.model.alertwin.ActivityQueryResp;

public interface AlertWinService {

	ActivityQueryResp activityQuery(ActivityQueryReq req);

	void updateLimitNum();

	void alertTimes(HashMap<String, String> req);
}
