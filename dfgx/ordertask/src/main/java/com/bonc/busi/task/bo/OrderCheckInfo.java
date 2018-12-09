package com.bonc.busi.task.bo;
/*
 * @desc:工单成功判断时的基本要素
 * @author:曾定勇
 * @time:2016-11-28
 */

public class OrderCheckInfo {
	private	String	userId;			// --- 用户编号 ---
	private	long	orderRedId;		// --- 工单纪录编号 ---
	
	public	String  getUserId(){
		return this.userId;
	}
	public	long  getOrderRedId(){
		return this.orderRedId;
	}
	public	void  setUserId(String userId){
		this.userId  = userId;
	}
	public	void  setOrderRecId(long orderRedId){
		this.orderRedId  = orderRedId;
	}
	
}
