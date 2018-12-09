package com.bonc.busi.outer.bo;

import java.io.Serializable;
import java.util.Date;

public class ActivityProcessLog implements Serializable{

	private static final long serialVersionUID = 558117439725062353L;
	private String activityId;
	private String tenantId;
	private String channelId;
	private String oriAmount;
	private int status;
	Integer activitySeqId;
	// 有进有出过滤工单数量
	private String inoutFilterAmount;

	// 覆盖规则过滤工单数量，只之前批次数量
	private String coverageFilterAmount;

	// 黑名单过滤数量
	private String blackFilterAmount;

	// 留存过滤数量，如果是首次则只留存数量
	private String reserveFilterAmount;

	// 接触过滤数量
	private String touchFilterAmount;
	
	//成功过滤数量
	private String successFilterAmount;
	
	//开始生成时间
	private Date genBeginDate;
	
	//生成结束时间
	private Date genEndDate;
	
	//批次工单开始时间
	private Date orderBeginDate;
	
	//批次工单结束时间
	private Date orderEndDate;
	
	//预留字段RESERVE1~RESERVE10
	private String reserve1;
	private String reserve2;
	private String reserve3;
	private String reserve4;
	private String reserve5;
	private String reserve6;
	private String reserve7;
	private String reserve8;
	private String reserve9;
	private String reserve10;
	
	//查询条件(处理某些字段可传可不传的情况)
	private String whereSql;
	
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
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getOriAmount() {
		return oriAmount;
	}
	public void setOriAmount(String oriAmount) {
		this.oriAmount = oriAmount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Integer getActivitySeqId() {
		return activitySeqId;
	}
	public void setActivitySeqId(Integer activitySeqId) {
		this.activitySeqId = activitySeqId;
	}
	public String getInoutFilterAmount() {
		return inoutFilterAmount;
	}
	public void setInoutFilterAmount(String inoutFilterAmount) {
		this.inoutFilterAmount = inoutFilterAmount;
	}
	public String getCoverageFilterAmount() {
		return coverageFilterAmount;
	}
	public void setCoverageFilterAmount(String coverageFilterAmount) {
		this.coverageFilterAmount = coverageFilterAmount;
	}
	public String getBlackFilterAmount() {
		return blackFilterAmount;
	}
	public void setBlackFilterAmount(String blackFilterAmount) {
		this.blackFilterAmount = blackFilterAmount;
	}
	public String getReserveFilterAmount() {
		return reserveFilterAmount;
	}
	public void setReserveFilterAmount(String reserveFilterAmount) {
		this.reserveFilterAmount = reserveFilterAmount;
	}
	public String getTouchFilterAmount() {
		return touchFilterAmount;
	}
	public void setTouchFilterAmount(String touchFilterAmount) {
		this.touchFilterAmount = touchFilterAmount;
	}
	
	public String getSuccessFilterAmount() {
		return successFilterAmount;
	}
	public void setSuccessFilterAmount(String successFilterAmount) {
		this.successFilterAmount = successFilterAmount;
	}
	public Date getGenBeginDate() {
		return genBeginDate;
	}
	public void setGenBeginDate(Date genBeginDate) {
		this.genBeginDate = genBeginDate;
	}
	public Date getGenEndDate() {
		return genEndDate;
	}
	public void setGenEndDate(Date genEndDate) {
		this.genEndDate = genEndDate;
	}
	public Date getOrderBeginDate() {
		return orderBeginDate;
	}
	public void setOrderBeginDate(Date orderBeginDate) {
		this.orderBeginDate = orderBeginDate;
	}
	public Date getOrderEndDate() {
		return orderEndDate;
	}
	public void setOrderEndDate(Date orderEndDate) {
		this.orderEndDate = orderEndDate;
	}
	public String getReserve1() {
		return reserve1;
	}
	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}
	public String getReserve2() {
		return reserve2;
	}
	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}
	public String getReserve3() {
		return reserve3;
	}
	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}
	public String getReserve4() {
		return reserve4;
	}
	public void setReserve4(String reserve4) {
		this.reserve4 = reserve4;
	}
	public String getReserve5() {
		return reserve5;
	}
	public void setReserve5(String reserve5) {
		this.reserve5 = reserve5;
	}
	public String getReserve6() {
		return reserve6;
	}
	public void setReserve6(String reserve6) {
		this.reserve6 = reserve6;
	}
	public String getReserve7() {
		return reserve7;
	}
	public void setReserve7(String reserve7) {
		this.reserve7 = reserve7;
	}
	public String getReserve8() {
		return reserve8;
	}
	public void setReserve8(String reserve8) {
		this.reserve8 = reserve8;
	}
	public String getReserve9() {
		return reserve9;
	}
	public void setReserve9(String reserve9) {
		this.reserve9 = reserve9;
	}
	public String getReserve10() {
		return reserve10;
	}
	public void setReserve10(String reserve10) {
		this.reserve10 = reserve10;
	}
	public String getWhereSql() {
		return whereSql;
	}
	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}

}
