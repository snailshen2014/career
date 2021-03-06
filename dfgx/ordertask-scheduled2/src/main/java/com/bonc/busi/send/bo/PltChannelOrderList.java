package com.bonc.busi.send.bo;

import java.io.Serializable;
import java.util.Date;

public class PltChannelOrderList implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private Integer ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_SERIAL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private Integer ORDER_SERIAL_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.CHANNEL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String CHANNEL_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ACTIVITY_SEQ_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private Integer ACTIVITY_SEQ_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.PHONE_NUMBER
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String PHONE_NUMBER;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_STATUS
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String ORDER_STATUS;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.TENANT_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String TENANT_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.RETRY_TIMES
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private Short RETRY_TIMES;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_SEND_TIME
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private Date ORDER_SEND_TIME;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_BACK_TIME
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private Date ORDER_BACK_TIME;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String ORDER_CONTENT;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_ADD_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String ORDER_ADD_CONTENT;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_STATUS
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String ORDER_RESULT_STATUS;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private String ORDER_RESULT_CONTENT;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_CHANNEL_ORDER_LIST.SUB_CHANNEL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    private Integer SUB_CHANNEL_ID;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ID
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public Integer getID() {
        return ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ID
     *
     * @param ID the value for PLT_CHANNEL_ORDER_LIST.ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_SERIAL_ID
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_SERIAL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public Integer getORDER_SERIAL_ID() {
        return ORDER_SERIAL_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_SERIAL_ID
     *
     * @param ORDER_SERIAL_ID the value for PLT_CHANNEL_ORDER_LIST.ORDER_SERIAL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_SERIAL_ID(Integer ORDER_SERIAL_ID) {
        this.ORDER_SERIAL_ID = ORDER_SERIAL_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.CHANNEL_ID
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.CHANNEL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getCHANNEL_ID() {
        return CHANNEL_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.CHANNEL_ID
     *
     * @param CHANNEL_ID the value for PLT_CHANNEL_ORDER_LIST.CHANNEL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setCHANNEL_ID(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID == null ? null : CHANNEL_ID.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ACTIVITY_SEQ_ID
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ACTIVITY_SEQ_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public Integer getACTIVITY_SEQ_ID() {
        return ACTIVITY_SEQ_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ACTIVITY_SEQ_ID
     *
     * @param ACTIVITY_SEQ_ID the value for PLT_CHANNEL_ORDER_LIST.ACTIVITY_SEQ_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setACTIVITY_SEQ_ID(Integer ACTIVITY_SEQ_ID) {
        this.ACTIVITY_SEQ_ID = ACTIVITY_SEQ_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.PHONE_NUMBER
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.PHONE_NUMBER
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getPHONE_NUMBER() {
        return PHONE_NUMBER;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.PHONE_NUMBER
     *
     * @param PHONE_NUMBER the value for PLT_CHANNEL_ORDER_LIST.PHONE_NUMBER
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setPHONE_NUMBER(String PHONE_NUMBER) {
        this.PHONE_NUMBER = PHONE_NUMBER == null ? null : PHONE_NUMBER.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_STATUS
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_STATUS
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getORDER_STATUS() {
        return ORDER_STATUS;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_STATUS
     *
     * @param ORDER_STATUS the value for PLT_CHANNEL_ORDER_LIST.ORDER_STATUS
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_STATUS(String ORDER_STATUS) {
        this.ORDER_STATUS = ORDER_STATUS == null ? null : ORDER_STATUS.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.TENANT_ID
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.TENANT_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getTENANT_ID() {
        return TENANT_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.TENANT_ID
     *
     * @param TENANT_ID the value for PLT_CHANNEL_ORDER_LIST.TENANT_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setTENANT_ID(String TENANT_ID) {
        this.TENANT_ID = TENANT_ID == null ? null : TENANT_ID.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.RETRY_TIMES
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.RETRY_TIMES
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public Short getRETRY_TIMES() {
        return RETRY_TIMES;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.RETRY_TIMES
     *
     * @param RETRY_TIMES the value for PLT_CHANNEL_ORDER_LIST.RETRY_TIMES
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setRETRY_TIMES(Short RETRY_TIMES) {
        this.RETRY_TIMES = RETRY_TIMES;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_SEND_TIME
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_SEND_TIME
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public Date getORDER_SEND_TIME() {
        return ORDER_SEND_TIME;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_SEND_TIME
     *
     * @param ORDER_SEND_TIME the value for PLT_CHANNEL_ORDER_LIST.ORDER_SEND_TIME
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_SEND_TIME(Date ORDER_SEND_TIME) {
        this.ORDER_SEND_TIME = ORDER_SEND_TIME;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_BACK_TIME
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_BACK_TIME
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public Date getORDER_BACK_TIME() {
        return ORDER_BACK_TIME;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_BACK_TIME
     *
     * @param ORDER_BACK_TIME the value for PLT_CHANNEL_ORDER_LIST.ORDER_BACK_TIME
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_BACK_TIME(Date ORDER_BACK_TIME) {
        this.ORDER_BACK_TIME = ORDER_BACK_TIME;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_CONTENT
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getORDER_CONTENT() {
        return ORDER_CONTENT;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_CONTENT
     *
     * @param ORDER_CONTENT the value for PLT_CHANNEL_ORDER_LIST.ORDER_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_CONTENT(String ORDER_CONTENT) {
        this.ORDER_CONTENT = ORDER_CONTENT == null ? null : ORDER_CONTENT.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_ADD_CONTENT
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_ADD_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getORDER_ADD_CONTENT() {
        return ORDER_ADD_CONTENT;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_ADD_CONTENT
     *
     * @param ORDER_ADD_CONTENT the value for PLT_CHANNEL_ORDER_LIST.ORDER_ADD_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_ADD_CONTENT(String ORDER_ADD_CONTENT) {
        this.ORDER_ADD_CONTENT = ORDER_ADD_CONTENT == null ? null : ORDER_ADD_CONTENT.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_STATUS
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_STATUS
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getORDER_RESULT_STATUS() {
        return ORDER_RESULT_STATUS;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_STATUS
     *
     * @param ORDER_RESULT_STATUS the value for PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_STATUS
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_RESULT_STATUS(String ORDER_RESULT_STATUS) {
        this.ORDER_RESULT_STATUS = ORDER_RESULT_STATUS == null ? null : ORDER_RESULT_STATUS.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_CONTENT
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public String getORDER_RESULT_CONTENT() {
        return ORDER_RESULT_CONTENT;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_CONTENT
     *
     * @param ORDER_RESULT_CONTENT the value for PLT_CHANNEL_ORDER_LIST.ORDER_RESULT_CONTENT
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setORDER_RESULT_CONTENT(String ORDER_RESULT_CONTENT) {
        this.ORDER_RESULT_CONTENT = ORDER_RESULT_CONTENT == null ? null : ORDER_RESULT_CONTENT.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_CHANNEL_ORDER_LIST.SUB_CHANNEL_ID
     *
     * @return the value of PLT_CHANNEL_ORDER_LIST.SUB_CHANNEL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public Integer getSUB_CHANNEL_ID() {
        return SUB_CHANNEL_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_CHANNEL_ORDER_LIST.SUB_CHANNEL_ID
     *
     * @param SUB_CHANNEL_ID the value for PLT_CHANNEL_ORDER_LIST.SUB_CHANNEL_ID
     *
     * @mbggenerated Tue Nov 22 00:04:53 CST 2016
     */
    public void setSUB_CHANNEL_ID(Integer SUB_CHANNEL_ID) {
        this.SUB_CHANNEL_ID = SUB_CHANNEL_ID;
    }
}