package com.bonc.busi.activity;

/**
 * 活动短信
 * 
 * @author zhangxiaonan
 */
public class ShortMessage {
    /**
     * 网关ID
     */
    private String smsSetId;
    /**
     * 电话号码
     */
    private String telPhone;
    /**
     * 短信内容
     */
    private String sendContent;
    /**
     * 发送级别
     */
    private int sendLevel = 1;

    public String getSmsSetId() {
        return smsSetId;
    }

    public void setSmsSetId(String smsSetId) {
        this.smsSetId = smsSetId;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    public String getSendContent() {
        return sendContent;
    }

    public void setSendContent(String sendContent) {
        this.sendContent = sendContent;
    }

    public int getSendLevel() {
        return sendLevel;
    }

    public void setSendLevel(int sendLevel) {
        this.sendLevel = sendLevel;
    }
}
