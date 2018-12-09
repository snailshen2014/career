package com.bonc.busi.activity;

import java.util.List;

public class BroadbandCallList implements ChannelPo{
    private String activityId;//活动id
    private String channelId;//渠道编码
    private String tenantId;//租户id
    private String filterCondition;//筛选条件
    private String filterConditionSql;//筛选条件对应sql
    private String filterSqlCondition;//条件回显到用户群标示
    private String orderIssuedRule;  //工单下发规则
    private String marketingWords; //营销话术
    private String msmWords;//短信内容
    private List<ChannelSpecialFilterPo> channelSpecialFilterList;

    public List<ChannelSpecialFilterPo> getChannelSpecialFilterList() {
        return channelSpecialFilterList;
    }

    public void setChannelSpecialFilterList(List<ChannelSpecialFilterPo> channelSpecialFilterList) {
        this.channelSpecialFilterList = channelSpecialFilterList;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    @Override
    public String getFilterConditionSql() {
        return filterConditionSql;
    }

    public void setFilterConditionSql(String filterConditionSql) {
        this.filterConditionSql = filterConditionSql;
    }

    public String getFilterSqlCondition() {
        return filterSqlCondition;
    }

    public void setFilterSqlCondition(String filterSqlCondition) {
        this.filterSqlCondition = filterSqlCondition;
    }

    @Override
    public String getOrderIssuedRule() {
        return orderIssuedRule;
    }

    public void setOrderIssuedRule(String orderIssuedRule) {
        this.orderIssuedRule = orderIssuedRule;
    }

    public String getMarketingWords() {
        return marketingWords;
    }

    public void setMarketingWords(String marketingWords) {
        this.marketingWords = marketingWords;
    }

    public String getMsmWords() {
        return msmWords;
    }

    public void setMsmWords(String msmWords) {
        this.msmWords = msmWords;
    }

    // for ACTIVITY_SEQ_ID
    private Integer ACTIVITY_SEQ_ID;

    public void setACTIVITY_SEQ_ID(Integer id) {
        this.ACTIVITY_SEQ_ID = id;
    }

    public Integer getACTIVITY_SEQ_ID() {
        return this.ACTIVITY_SEQ_ID;
    }
}
