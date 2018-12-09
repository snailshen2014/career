package com.bonc.channelapi.hbase.entity;

/**
 * 批量存储数据Model
 *
 * @author caiqiang
 * @version 2017年2月15日
 * @see SMSInfo
 * @since
 */
public class SMSInfo {

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 推送时间
     */
    private String pushTime;

    /**
     * 推送结果
     */
    private String pushResult;

    /**
     * 活动类型
     */
    private String activityId;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 系统类型
     */
    private String source_type = "INTERFACE_CJYX";

    public SMSInfo(String channelType, String phone, String eventType, String pushTime,
               String pushResult, String activityId, String smsContent, String source_type) {
        this.channelType = channelType;
        this.phone = phone;
        this.eventType = eventType;
        this.pushTime = pushTime;
        this.pushResult = pushResult;
        this.activityId = activityId;
        this.smsContent = smsContent;
        this.source_type = source_type;
    }

    public SMSInfo() {}

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public String getPushResult() {
        return pushResult;
    }

    public void setPushResult(String pushResult) {
        this.pushResult = pushResult;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }
}
