package com.bonc.busi.outer.service;

import java.util.HashMap;

public interface TrackService {

	Object getContactTrack(HashMap<String, Object> req);

	Object getActivityChannelNum(HashMap<String, Object> req);

	Object updateHistory(HashMap<String, Object> req);

	Object getOrderRecord(HashMap<String, Object> req);

	Object countDetail(HashMap<String, Object> req);

	Object filterList(HashMap<String, Object> req);

}
