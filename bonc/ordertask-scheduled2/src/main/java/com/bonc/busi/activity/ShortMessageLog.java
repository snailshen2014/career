package com.bonc.busi.activity;

import java.sql.Date;

/**
 * 活动短信日志信息
 * 
 * @author zhangxiaonan
 */
public class ShortMessageLog {
    /**
     * 日志ID
     */
    private String id;
    /**
     * 网关ID
     */
    private String smsSetId;
    /**
     * 电话号码
     */
    private String telephone;
    /**
     * 短信内容 
     */
    private String content;
    /**
     * 发送级别
     */
    private int sendLevel;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 状态
     */
    private int status;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 错误编码
     */
    private String errCode;
    /**
     * 备注
     */
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSmsSetId() {
        return smsSetId;
    }

    public void setSmsSetId(String smsSetId) {
        this.smsSetId = smsSetId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSendLevel() {
        return sendLevel;
    }

    public void setSendLevel(int sendLevel) {
        this.sendLevel = sendLevel;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
