package com.bonc.busi.orderschedule.activity;

import com.bonc.busi.orderschedule.bo.PltActivityInfo;

public interface ActivityInfoService {
	
	//根据活动状态和活动生命周期判断是否需要跑这个活动
	public boolean isActivityNeedRun();
	
	//记录活动基本信息
	boolean recordActivityInfo(int activitySeqId);
	
	//删除活动信息
	boolean cleanAcitivtyInfo(PltActivityInfo activity);


}
