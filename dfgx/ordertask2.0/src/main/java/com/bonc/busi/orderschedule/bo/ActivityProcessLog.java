package com.bonc.busi.orderschedule.bo;

import java.util.Date;

/**
 * 活动处理过程表(2.0版本） 为了适应2.0改造的需要,在原始的基础上增加并修改了几个字段
 * @author Administrator
 */
public class ActivityProcessLog {
	
	private String ACTIVITY_ID;
	private String TENANT_ID;
	private String CHANNEL_ID;
	private int ORI_AMOUNT;
	private int STATUS;
	Integer ACTIVITY_SEQ_ID;
	// 有进有出过滤工单数量
	private int INOUT_FILTER_AMOUNT;

	// 覆盖规则过滤工单数量，只之前批次数量
	private int COVERAGE_FILTER_AMOUNT;

	// 黑名单过滤数量
	private int BLACK_FILTER_AMOUNT;

	// 留存过滤数量，如果是首次则只留存数量
	private int RESERVE_FILTER_AMOUNT;

	// 接触过滤数量
	private int TOUCH_FILTER_AMOUNT;
	
	//成功过滤数量
	private int SUCCESS_FILTER_AMOUNT;
	
	//删除重复工单过滤数
	private int REPEAT_FILTER_AMOUNT;
	
	//开始生成时间
	private Date GEN_BEGIN_DATE;
	
	//生成结束时间
	private Date GEN_END_DATE;
	
	//批次工单开始时间
	private Date ORDER_BEGIN_DATE;
	
	//批次工单结束时间
	private Date ORDER_END_DATE;
	
	//预留字段RESERVE1~RESERVE10
	private String RESERVE1;
	private String RESERVE2;
	private String RESERVE3;
	private String RESERVE4;
	private String RESERVE5;
	private String RESERVE6;
	private String RESERVE7;
	private String RESERVE8;
	private String RESERVE9;
	private String RESERVE10;
	

	public void setACTIVITY_ID(String id) {
		this.ACTIVITY_ID = id;
	}

	public String getACTIVITY_ID() {
		return this.ACTIVITY_ID;
	}

	public void setTENANT_ID(String id) {
		this.TENANT_ID = id;
	}

	public String getTENANT_ID() {
		return this.TENANT_ID;
	}

	public void setCHANNEL_ID(String id) {
		this.CHANNEL_ID = id;
	}

	public String getCHANNEL_ID() {
		return this.CHANNEL_ID;
	}
    
	public void setSTATUS(int ss) {
		this.STATUS = ss;
	}

	public int getSTATUS() {
		return this.STATUS;
	}

	public void setACTIVITY_SEQ_ID(Integer ss) {
		this.ACTIVITY_SEQ_ID = ss;
	}

	public Integer getACTIVITY_SEQ_ID() {
		return this.ACTIVITY_SEQ_ID;
	}

	public int getORI_AMOUNT() {
		return ORI_AMOUNT;
	}

	public void setORI_AMOUNT(int oRI_AMOUNT) {
		ORI_AMOUNT = oRI_AMOUNT;
	}

	public int getINOUT_FILTER_AMOUNT() {
		return INOUT_FILTER_AMOUNT;
	}

	public void setINOUT_FILTER_AMOUNT(int iNOUT_FILTER_AMOUNT) {
		INOUT_FILTER_AMOUNT = iNOUT_FILTER_AMOUNT;
	}

	public int getCOVERAGE_FILTER_AMOUNT() {
		return COVERAGE_FILTER_AMOUNT;
	}

	public void setCOVERAGE_FILTER_AMOUNT(int cOVERAGE_FILTER_AMOUNT) {
		COVERAGE_FILTER_AMOUNT = cOVERAGE_FILTER_AMOUNT;
	}

	public int getBLACK_FILTER_AMOUNT() {
		return BLACK_FILTER_AMOUNT;
	}

	public void setBLACK_FILTER_AMOUNT(int bLACK_FILTER_AMOUNT) {
		BLACK_FILTER_AMOUNT = bLACK_FILTER_AMOUNT;
	}

	public int getRESERVE_FILTER_AMOUNT() {
		return RESERVE_FILTER_AMOUNT;
	}

	public void setRESERVE_FILTER_AMOUNT(int rESERVE_FILTER_AMOUNT) {
		RESERVE_FILTER_AMOUNT = rESERVE_FILTER_AMOUNT;
	}

	public int getTOUCH_FILTER_AMOUNT() {
		return TOUCH_FILTER_AMOUNT;
	}

	public void setTOUCH_FILTER_AMOUNT(int tOUCH_FILTER_AMOUNT) {
		TOUCH_FILTER_AMOUNT = tOUCH_FILTER_AMOUNT;
	}
	
	public int getSUCCESS_FILTER_AMOUNT() {
		return SUCCESS_FILTER_AMOUNT;
	}

	public void setSUCCESS_FILTER_AMOUNT(int sUCCESS_FILTER_AMOUNT) {
		SUCCESS_FILTER_AMOUNT = sUCCESS_FILTER_AMOUNT;
	}

	public Date getGEN_BEGIN_DATE() {
		return GEN_BEGIN_DATE;
	}

	public void setGEN_BEGIN_DATE(Date gEN_BEGIN_DATE) {
		GEN_BEGIN_DATE = gEN_BEGIN_DATE;
	}

	public Date getGEN_END_DATE() {
		return GEN_END_DATE;
	}

	public void setGEN_END_DATE(Date gEN_END_DATE) {
		GEN_END_DATE = gEN_END_DATE;
	}

	public Date getORDER_BEGIN_DATE() {
		return ORDER_BEGIN_DATE;
	}

	public void setORDER_BEGIN_DATE(Date oRDER_BEGIN_DATE) {
		ORDER_BEGIN_DATE = oRDER_BEGIN_DATE;
	}

	public Date getORDER_END_DATE() {
		return ORDER_END_DATE;
	}

	public void setORDER_END_DATE(Date oRDER_END_DATE) {
		ORDER_END_DATE = oRDER_END_DATE;
	}

	public String getRESERVE1() {
		return RESERVE1;
	}

	public void setRESERVE1(String rESERVE1) {
		RESERVE1 = rESERVE1;
	}

	public String getRESERVE2() {
		return RESERVE2;
	}

	public void setRESERVE2(String rESERVE2) {
		RESERVE2 = rESERVE2;
	}

	public String getRESERVE3() {
		return RESERVE3;
	}

	public void setRESERVE3(String rESERVE3) {
		RESERVE3 = rESERVE3;
	}

	public String getRESERVE4() {
		return RESERVE4;
	}

	public void setRESERVE4(String rESERVE4) {
		RESERVE4 = rESERVE4;
	}

	public String getRESERVE5() {
		return RESERVE5;
	}

	public void setRESERVE5(String rESERVE5) {
		RESERVE5 = rESERVE5;
	}

	public String getRESERVE6() {
		return RESERVE6;
	}

	public void setRESERVE6(String rESERVE6) {
		RESERVE6 = rESERVE6;
	}

	public String getRESERVE7() {
		return RESERVE7;
	}

	public void setRESERVE7(String rESERVE7) {
		RESERVE7 = rESERVE7;
	}

	public String getRESERVE8() {
		return RESERVE8;
	}

	public void setRESERVE8(String rESERVE8) {
		RESERVE8 = rESERVE8;
	}

	public String getRESERVE9() {
		return RESERVE9;
	}

	public void setRESERVE9(String rESERVE9) {
		RESERVE9 = rESERVE9;
	}

	public String getRESERVE10() {
		return RESERVE10;
	}

	public void setRESERVE10(String rESERVE10) {
		RESERVE10 = rESERVE10;
	}

	public int getREPEAT_FILTER_AMOUNT() {
		return REPEAT_FILTER_AMOUNT;
	}

	public void setREPEAT_FILTER_AMOUNT(int rEPEAT_FILTER_AMOUNT) {
		REPEAT_FILTER_AMOUNT = rEPEAT_FILTER_AMOUNT;
	}
}
