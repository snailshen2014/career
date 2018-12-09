package com.bonc.busi.backpage.bo;

import java.io.Serializable;

/**
 * 统计某一天工单中心执行的活动的总数、执行成功的活动数、执行失败的活动数
 * @author Administrator
 *
 */
public class ActivityStatistics implements Serializable {
	
	private static final long serialVersionUID = 5478945881622925170L;
	
	//日期(格式：yyyy-MM-dd)
	private String date;
	
	//某天总共执行的活动数
	private int total;
	
	//执行成功的活动数
	private int success;
	
	//执行失败的活动数
	private int fail;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFail() {
		return fail;
	}

	public void setFail(int fail) {
		this.fail = fail;
	}
}
