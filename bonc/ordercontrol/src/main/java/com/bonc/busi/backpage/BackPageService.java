package com.bonc.busi.backpage;

import com.bonc.busi.backpage.bo.ActivityPo;
import com.bonc.busi.backpage.bo.ActivityStatistics;
import com.bonc.busi.backpage.bo.CreateTenantBo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MQZ on 2017/6/7.
 */
public interface BackPageService {

    /*
        获取工单表使用状态
     */
    public Object getUsedTableList(String tenantId);

    /*
        获取有效的租户id
     */
    List<Map<String, Object>>  getValidTenantInfo();

    /*
        获取SysCfg
     */
    List<HashMap> getSysCfg();

    /*
        删除cfg 列
     */
    void delCfgRow(String key);


    /*
        添加或者更新
     */
    void insertOrUpdateCfg(Map cfgMap);


    /*
        初始化租户数据
     */
    void initTenantData(CreateTenantBo cfg);
    
    /**
     * 获取活动列表信息，如果指定了活动Id，则返回指定的活动信息
     * @param tenantId  租户Id
     * @param activityId  活动Id
     */
    List<ActivityPo> getActivityList(String tenantId, String activityId);

    /**
     * 查询工单的执行过程
     * @param activityId
     * @param tenantId
     * @return
     */
	public List<Map<String, Object>> getActivityOrderGenerateStep(String activityId, String tenantId);

	/**
	 * 重跑指定的活动：     删除活动的相关信息(活动表，process_log表,detail表,execute_interface表)  条件 ： 活动Id+活动最新的批次
	 * @param activityId
	 * @param tenantId
	 */
	public void recycleActivityOrder(String activityId, String tenantId);

    /**
     * 拼接行云的 select table where
     * @param tenantId
     * @return
     */
    List<HashMap> XSqlSelect(String tenantId);
    List<HashMap> XSqlTable (String tenantId);
    List<HashMap> XSqlWhere (String tenantId);
    void delSelectRow(String key, String tenantId);
    void delTableRow(String key, String tenantId);
    void delWhereRow(String req, String tenantId);
    void insertOrUpdateXSQL(Map map);

    /**
     * 工单表的容量进行扩容：扩容5000000
     * @param tenantId
     */
	public void addTableCapacity(String tenantId);

	/**
	 * 停止指定的活动： 修改活动表里的activity_status的状态为2
	 * @param tenantId
	 * @param activityId
	 */
	public void stopActivity(String tenantId, String activityId,String serviceURL);


    void simpleInitTenantData(CreateTenantBo cfg);

	/**
	 * 获取活动数的统计信息：统计一周的时间每天的活动总数/成功/失败数
	 * @return
	 */
	public List<ActivityStatistics> getActivityStatisticsList(String tenantId);

	List<String> tablename(String tenantId, String mysqlschemaname);
}
