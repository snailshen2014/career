package com.bonc.channelapi.hbase.entity;


import com.bonc.channelapi.hbase.constant.BaseConstant;


/**
 * 返回数据定义
 * 
 * @author caiqiang
 * @version 2016年7月28日
 * @see JsonResult
 * @since
 */
public class JsonResult {
    /**
     * 返回状态码
     */
    private String code = BaseConstant.CODE_SUCCESS;

    /**
     * 返回提示信息
     */
    private String message = BaseConstant.MSG_SUCCESS;

    /**
     * 返回数据
     */
    private Object data;

    public JsonResult(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public JsonResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResult(Object data) {
        this.data = data;
    }

    public JsonResult() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
