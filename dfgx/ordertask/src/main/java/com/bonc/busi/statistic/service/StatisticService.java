package com.bonc.busi.statistic.service;

import java.util.HashMap;

public interface StatisticService {

	/**
	 * 每天定时任务，统计每个活动的批次信息
	 */
	public void statisticBench(String tenantId,String activitySeqId);
	
	/**
	 * 增量统计活动
	 * @param tenantId　租户标识
	 * @param activitySeqId　活动标识　对应活动表中的 REC_ID
	 */
	public void incrStatistic(HashMap<String, Object> incrActivity);

	/**
	 * 备份客户经理统计 
	 */
	public void backStatisitic();


	/**
	 * 统计 接触次数，然后更新PLT_ORDER_STATISTIC_SEND 表    1/5min
	 */
	public void statisticConNum();


}
