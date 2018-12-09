package com.bonc.busi.activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 短信发送结果
 * 
 * @author zhangxiaonan
 */
public class ShortMessageResult {
    /**
     * 发送成功标志。0成功，1失败，-1未知状态。
     */
    private int resultType;
    /**
     * 发送成功的短信
     */
    private List<ShortMessageLog> successMsgLogs = new ArrayList<ShortMessageLog>();
    /**
     * 发送失败的短信
     */
    private List<ShortMessageLog> failedMsgLogs = new ArrayList<ShortMessageLog>();

    public boolean isSuccess() {
        return resultType == 0;
    }

    public boolean isFailed() {
        return resultType == 1;
    }

    public boolean isUnknown() {
        return resultType == -1;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public void addSuccessMsgLog(ShortMessageLog message) {
        successMsgLogs.add(message);
    }

    public void addFailedMsgLog(ShortMessageLog message) {
        failedMsgLogs.add(message);
    }

    public void addSuccessMsgLogs(List<ShortMessageLog> messages) {
        successMsgLogs.addAll(messages);
    }

    public void addFailedMsgLogs(List<ShortMessageLog> messages) {
        failedMsgLogs.addAll(messages);
    }

    public List<ShortMessageLog> getSuccessMsgLogs() {
        return successMsgLogs;
    }

    public List<ShortMessageLog> getFailedMsgLogs() {
        return failedMsgLogs;
    }
}
