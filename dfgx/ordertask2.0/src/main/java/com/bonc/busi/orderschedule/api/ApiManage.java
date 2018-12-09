package com.bonc.busi.orderschedule.api;

import com.bonc.busi.activity.ChannelPo;
import com.bonc.common.base.JsonResult;

import java.util.HashMap;
import java.util.List;

public interface ApiManage {
    /*
     * 获取活动详情
     */
    public List<String> getActivityList(String tenantId);

    /*
     * 获取活动列表
     */
    public String getActivityDetail(String activityId, String tenantId, Integer activitySeqId);

    /*
     * 获取渠道审核id
     */
    public List<String> getEnableChannel(String activityId, List<String> orgChannelIds);

    /*
     * 获取账期
     */
    public String getMaxdate(String tenantId);

    /*
     * 获取用户群
     */
    public String getGroupId();

    /**
     * 资源划配 状态完毕时返回临时表名
     * return 临时表名
     */
    public String getRuleTypestmpTable(String channelId, ChannelPo cp);

    /**
     * 下载行云远程hdfsftp文件
     */
    int downXCloudLoadFile(String fileName);

    /*
     * 在行云上执行DDL语句
	 */
    public JsonResult execDdlOnXcloud(String sqlDdl, String tenantId);


    /**
     * 获取活动批次号
     *
     * @return
     */
    public Integer getActivitySeqId();

    /**
     * 生成活动批次号
     */
    public void genActivitySeqId();

    /**
     * 导入mysql
     *
     * @return
     */
    public boolean loadDataInMysql(String sqlData, String tenantId);

    /**
     * 调用成功过滤
     */
    public String orderFilterSucess(String TenantId, Integer ActivitySeqId, String activityid);

    /**
     * 渠道接口（工单生成完成时调用）
     * @param updateFlag: 是否是更新统计数据的标识，当传了该参数就表示该调用是为了更新统计表的数,不是初次统计，该参数的值可以为任何值,例如true
     */
    public void channelInitHandle(String activityId, String tenantId, String activitySeqId,String updateFlag);

    /**
     * 弹窗渠道添加手机索引
     */
    public String popwinaddindex(String TenantId, String OrderTableName, int ActivitySeqid, String activityId , String channelId);


    /**
     * 在mycat上执行sql
    */
    public JsonResult execDdlSql(String xCloudSql);


    /**
     * 虚拟渠道调用划配
     * @param params
     */
    void callAllotOrder(HashMap<String, Object> params);
}