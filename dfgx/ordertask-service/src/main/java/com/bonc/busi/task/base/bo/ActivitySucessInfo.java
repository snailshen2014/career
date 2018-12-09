package com.bonc.busi.task.base.bo;
/*
 * @desc:活动成功相关信息
 * @author:曾定勇
 * @time:2016-11-26
 */

import java.util.Date;

public class ActivitySucessInfo {
	//private 	String		activityId;   // --- 活动编号 ---
	private	int			recId;		// --- 工单编号 ---
	private	int			ACTIVITY_SEQ_ID;   // --- 活动序列号  ---
	private	String		sucessType;		// --- 成功类型 ---
	private	String		sucessConSql;			// --- 成功条件SQL --
	private    Date         lastOrderCreateTime;   // --- 最近工单生成时间 ---
	private    String		matchingType;  // ---  匹配类型 ---
	
	public int getACTIVITY_SEQ_ID(){
		return ACTIVITY_SEQ_ID;
	}
	public void   setACTIVITY_SEQ_ID(int ACTIVITY_SEQ_ID){
		this.ACTIVITY_SEQ_ID = ACTIVITY_SEQ_ID;
	}
	/*
	public String getActivityId(){
		return activityId;
	}
	public void   setActivityId(String activityId){
		this.activityId = activityId;
	}
	*/
	public int getRecId(){
		return recId;
	}
	public void   setRecId(int recId){
		this.recId = recId;
	}
	public String getSucessType(){
		return sucessType;
	}
	public void   setSucessType(String sucessType){
		this.sucessType = sucessType;
	}
	public String getSucessConSql(){
		return sucessConSql;
	}
	public void   setSucessConSql(String sucessConSql){
		this.sucessConSql = sucessConSql;
	}
	
	public Date getLastOrderCreateTime(){
		return lastOrderCreateTime;
	}
	public void   setLastOrderCreateTime(Date lastOrderCreateTime){
		this.lastOrderCreateTime = lastOrderCreateTime;
	}
	public String getMatchingType(){
		return matchingType;
	}
	public void   setMatchingType(String matchingType){
		this.matchingType = matchingType;
	}
	
	
}
