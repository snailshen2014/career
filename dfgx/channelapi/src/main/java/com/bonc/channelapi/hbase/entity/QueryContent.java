package com.bonc.channelapi.hbase.entity;

/**
 * 批量查询model
 *
 * @author caiqiang
 * @version 2017年2月15日
 * @see QueryContent
 * @since
 */

public class QueryContent {

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 业务编码
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
