package com.bonc.busi.orderschedule.bo;

import java.io.Serializable;
import java.util.Date;

public class PltActivityExecuteLog implements Serializable {
    private String  BUSI_ITEM;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.REC_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private Integer REC_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.CHANNEL_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private String CHANNEL_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private String ACTIVITY_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_SEQ_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private Integer ACTIVITY_SEQ_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.TENANT_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private String TENANT_ID;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.BUSI_CODE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private Integer BUSI_CODE;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.OPER_TIME
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private Date OPER_TIME;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.PROCESS_STATUS
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private Integer PROCESS_STATUS;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.BEGIN_DATE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private Date BEGIN_DATE;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column PLT_ACTIVITY_EXECUTE_LOG.END_DATE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    private Date END_DATE;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.REC_ID
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.REC_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public Integer getREC_ID() {
        return REC_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.REC_ID
     *
     * @param REC_ID the value for PLT_ACTIVITY_EXECUTE_LOG.REC_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setREC_ID(Integer REC_ID) {
        this.REC_ID = REC_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.CHANNEL_ID
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.CHANNEL_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public String getCHANNEL_ID() {
        return CHANNEL_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.CHANNEL_ID
     *
     * @param CHANNEL_ID the value for PLT_ACTIVITY_EXECUTE_LOG.CHANNEL_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setCHANNEL_ID(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID == null ? null : CHANNEL_ID.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_ID
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public String getACTIVITY_ID() {
        return ACTIVITY_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_ID
     *
     * @param ACTIVITY_ID the value for PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setACTIVITY_ID(String ACTIVITY_ID) {
        this.ACTIVITY_ID = ACTIVITY_ID == null ? null : ACTIVITY_ID.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_SEQ_ID
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_SEQ_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public Integer getACTIVITY_SEQ_ID() {
        return ACTIVITY_SEQ_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_SEQ_ID
     *
     * @param ACTIVITY_SEQ_ID the value for PLT_ACTIVITY_EXECUTE_LOG.ACTIVITY_SEQ_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setACTIVITY_SEQ_ID(Integer ACTIVITY_SEQ_ID) {
        this.ACTIVITY_SEQ_ID = ACTIVITY_SEQ_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.TENANT_ID
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.TENANT_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public String getTENANT_ID() {
        return TENANT_ID;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.TENANT_ID
     *
     * @param TENANT_ID the value for PLT_ACTIVITY_EXECUTE_LOG.TENANT_ID
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setTENANT_ID(String TENANT_ID) {
        this.TENANT_ID = TENANT_ID == null ? null : TENANT_ID.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.BUSI_CODE
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.BUSI_CODE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public Integer getBUSI_CODE() {
        return BUSI_CODE;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.BUSI_CODE
     *
     * @param BUSI_CODE the value for PLT_ACTIVITY_EXECUTE_LOG.BUSI_CODE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setBUSI_CODE(Integer BUSI_CODE) {
        this.BUSI_CODE = BUSI_CODE;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.OPER_TIME
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.OPER_TIME
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public Date getOPER_TIME() {
        return OPER_TIME;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.OPER_TIME
     *
     * @param OPER_TIME the value for PLT_ACTIVITY_EXECUTE_LOG.OPER_TIME
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setOPER_TIME(Date OPER_TIME) {
        this.OPER_TIME = OPER_TIME;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.PROCESS_STATUS
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.PROCESS_STATUS
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public Integer getPROCESS_STATUS() {
        return PROCESS_STATUS;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.PROCESS_STATUS
     *
     * @param PROCESS_STATUS the value for PLT_ACTIVITY_EXECUTE_LOG.PROCESS_STATUS
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setPROCESS_STATUS(Integer PROCESS_STATUS) {
        this.PROCESS_STATUS = PROCESS_STATUS;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.BEGIN_DATE
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.BEGIN_DATE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public Date getBEGIN_DATE() {
        return BEGIN_DATE;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.BEGIN_DATE
     *
     * @param BEGIN_DATE the value for PLT_ACTIVITY_EXECUTE_LOG.BEGIN_DATE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setBEGIN_DATE(Date BEGIN_DATE) {
        this.BEGIN_DATE = BEGIN_DATE;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column PLT_ACTIVITY_EXECUTE_LOG.END_DATE
     *
     * @return the value of PLT_ACTIVITY_EXECUTE_LOG.END_DATE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public Date getEND_DATE() {
        return END_DATE;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column PLT_ACTIVITY_EXECUTE_LOG.END_DATE
     *
     * @param END_DATE the value for PLT_ACTIVITY_EXECUTE_LOG.END_DATE
     *
     * @mbggenerated Fri Jun 09 15:54:59 CST 2017
     */
    public void setEND_DATE(Date END_DATE) {
        this.END_DATE = END_DATE;
    }


    public String getBUSI_ITEM() {
        return BUSI_ITEM;
    }

    public void setBUSI_ITEM(String BUSI_ITEM) {
        this.BUSI_ITEM = BUSI_ITEM;
    }
}