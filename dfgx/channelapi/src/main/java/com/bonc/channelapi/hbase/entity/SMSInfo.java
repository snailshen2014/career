package com.bonc.channelapi.hbase.entity;

/**
 * �����洢����Model
 *
 * @author caiqiang
 * @version 2017��2��15��
 * @see SMSInfo
 * @since
 */
public class SMSInfo {

    /**
     * ��������
     */
    private String channelType;

    /**
     * �û��ֻ���
     */
    private String phone;

    /**
     * �¼�����
     */
    private String eventType;

    /**
     * ����ʱ��
     */
    private String pushTime;

    /**
     * ���ͽ��
     */
    private String pushResult;

    /**
     * �����
     */
    private String activityId;

    /**
     * ��������
     */
    private String smsContent;

    /**
     * ϵͳ����
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
