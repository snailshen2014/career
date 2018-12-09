package com.bonc.busi.sys.service;

import java.util.Map;

public interface TelecomOrderMonitorService {

	/**
	 * 根据租户Id查询该租户下哪个活动正在跑，哪些活动在等待跑，以及哪些活动以及跑完了(跑完的活动需要给出最近跑的时间以及工单数)
	 * @param tenantId
	 * @return
	 */
	Map<String, Object> queryOrderStateById(String tenantId);
}
