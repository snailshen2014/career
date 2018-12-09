package com.bonc.channelapi.hbase.entity;

/**
 * ������ѯmodel
 *
 * @author caiqiang
 * @version 2017��2��15��
 * @see QueryContent
 * @since
 */

public class QueryContent {

    /**
     * �û��ֻ���
     */
    private String phone;

    /**
     * ��ʼʱ��
     */
    private String startTime;

    /**
     * ����ʱ��
     */
    private String endTime;

    /**
     * ҵ�����
     */
    private String businessCode;

    public QueryContent(String phone, String startTime, String endTime, String businessCode) {
        this.phone = phone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.businessCode = businessCode;
    }

    public QueryContent() {}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

}
