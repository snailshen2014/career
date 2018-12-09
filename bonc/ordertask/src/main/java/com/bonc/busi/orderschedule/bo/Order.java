package com.bonc.busi.orderschedule.bo;
import java.math.*;

//select * from UT_UPLOAD.UI_L_USER_LABEL_INFO_ALL_ORG;
public class Order {
	private Integer ACTIVITY_SEQ_ID;
	private String ORDER_ID;
	private String USER_ID;
	private String PHONE_NUMBER;
	private String CHANNEL_ID;
	private String CUST_NAME;
	private BigDecimal ACCT_FEE;
	private String WENDING_FLAG;
	private String JIAZHI_FLAG;
	private String IS_LOWER_USER;
	private String IS_SANWU;
	private String USER_STATUS;
	private String NET_DATE;
	private String USER_LEVEL;
	private String PRODUCT_CLASS;
	private String SERVICE_TYPE;
	private String IS_GROUP;
	private String TERMINAL_TYPE;
	private String IS_4GUSER;
	private String PARENT_ORDER_ID;
	private String ORDER_STATUS;
	private String RETRY_TIMES;
	private String ORDER_FINISH_DATE;
	private String ORDER_RESULT_CONTENT;
	private String ORDER_RESULT_STATUS;
	private String TENANT_ID;
	private Integer IS_DISPATCH;
	private String RECEIVER;
	private String MANUAL_RECEIVER;
	private String MANUAL_TIME;
	private String type;//渠道类型
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getACTIVITY_SEQ_ID() {
		return ACTIVITY_SEQ_ID;
	}
	public void setACTIVITY_SEQ_ID(Integer activity_seq_id) {
		this.ACTIVITY_SEQ_ID = activity_seq_id;
	}
	public String getORDER_ID() {
		return ORDER_ID;
	}
	public void setORDER_ID(String orderid) {
		this.ORDER_ID = orderid;
	}

	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String usrid) {
		this.USER_ID = usrid;
	}
	public String getPHONE_NUMBER() {
		return PHONE_NUMBER;
	}
	public void setPHONE_NUMBER(String phoneid) {
		this.PHONE_NUMBER = phoneid;
	}
	public String getCHANNEL_ID() {
		return CHANNEL_ID;
	}
	public void setCHANNEL_ID(String channelid) {
		this.CHANNEL_ID = channelid;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cname) {
		this.CUST_NAME = cname;
	}
	public BigDecimal getACCT_FEE() {
		return ACCT_FEE;
	}
	public void setACCT_FEE(BigDecimal fee) {
		this.ACCT_FEE = fee;
	}
	public String getWENDING_FLAG() {
		return WENDING_FLAG;
	}
	public void setWENDING_FLAG(String flag) {
		this.WENDING_FLAG = flag;
	}
	public String getJIAZHI_FLAG() {
		return JIAZHI_FLAG;
	}
	public void setJIAZHI_FLAG(String flag) {
		this.JIAZHI_FLAG = flag;
	}
	public String getIS_LOWER_USER() {
		return IS_LOWER_USER;
	}
	public void setIS_LOWER_USER(String flag) {
		this.IS_LOWER_USER = flag;
	}
	public String getIS_SANWU() {
		return IS_SANWU;
	}
	public void setIS_SANWU(String flag) {
		this.IS_SANWU = flag;
	}
	public String getUSER_STATUS() {
		return USER_STATUS;
	}
	public void setUSER_STATUS(String flag) {
		this.USER_STATUS = flag;
	}
	public String getNET_DATE() {
		return NET_DATE;
	}
	public void setNET_DATE(String date) {
		this.NET_DATE = date;
	}
	public String getUSER_LEVEL() {
		return USER_LEVEL;
	}
	public void setUSER_LEVEL(String level) {
		this.USER_LEVEL = level;
	}
	public String getPRODUCT_CLASS() {
		return PRODUCT_CLASS;
	}
	public void setPRODUCT_CLASS(String cl) {
		this.PRODUCT_CLASS = cl;
	}
	public String getSERVICE_TYPE() {
		return SERVICE_TYPE;
	}
	public void setSERVICE_TYPE(String type) {
		this.SERVICE_TYPE = type;
	}
	public String getIS_GROUP() {
		return IS_GROUP;
	}
	public void setIS_GROUP(String flag) {
		this.IS_GROUP = flag;
	}
	public String geTERMINAL_TYPE() {
		return TERMINAL_TYPE;
	}
	public void setTERMINAL_TYPE(String flag) {
		this.TERMINAL_TYPE = flag;
	}
	public String geTIS_4GUSER() {
		return IS_4GUSER;
	}
	public void setIS_4GUSER(String flag) {
		this.IS_4GUSER = flag;
	}
	public String gePARENT_ORDER_ID() {
		return PARENT_ORDER_ID;
	}
	public void setPARENT_ORDER_ID(String porderid) {
		this.PARENT_ORDER_ID = porderid;
	}
	public String geORDER_STATUS() {
		return ORDER_STATUS;
	}
	public void setPORDER_STATUS(String status) {
		this.ORDER_STATUS = status;
	}
	public String geRETRY_TIMES() {
		return RETRY_TIMES;
	}
	public void setRETRY_TIMES(String times) {
		this.RETRY_TIMES = times;
	}
	public String geORDER_FINISH_DATE() {
		return ORDER_FINISH_DATE;
	}
	public void setORDER_FINISH_DATE(String date) {
		this.ORDER_FINISH_DATE = date;
	}
	public String geORDER_RESULT_CONTENT() {
		return ORDER_RESULT_CONTENT;
	}
	public void setORDER_RESULT_CONTENT(String content) {
		this.ORDER_RESULT_CONTENT = content;
	}
	public String geORDER_RESULT_STATUS() {
		return ORDER_RESULT_STATUS;
	}
	public void setORDER_RESULT_STATUS(String status) {
		this.ORDER_RESULT_STATUS = status;
	}
	public String geTENANT_ID() {
		return TENANT_ID;
	}
	public void setTENANT_ID(String tid) {
		this.TENANT_ID = tid;
	}
	public Integer geIS_DISPATCH() {
		return IS_DISPATCH;
	}
	public void setIS_DISPATCH(Integer dispatch) {
		this.IS_DISPATCH = dispatch;
	}
	public String geRECEIVER() {
		return RECEIVER;
	}
	public void setRECEIVER(String receiver) {
		this.RECEIVER = receiver;
	}
	public String geMANUAL_RECEIVER() {
		return MANUAL_RECEIVER;
	}
	public void setMANUAL_RECEIVER(String receiver) {
		this.MANUAL_RECEIVER = receiver;
	}
	public String geMANUAL_TIME() {
		return MANUAL_TIME;
	}
	public void setMANUAL_TIME(String  mtime) {
		this.MANUAL_TIME = mtime;
	}
}
