package com.bonc.busi.interfaces.model.frontline;

import com.bonc.busi.interfaces.model.ReqHeader;

public class OrderStatisticReq extends ReqHeader{

	private String pama;	//查询约束条件
	private String accPeriod;	//账期
	private Integer pageSize;	//页码大小 require=true
	private Integer pageNum;	//页码号，以1为第一页 require=true
	private Integer pageStart;	//其实页

	public String getPama() {
		return pama;
	}

	public void setPama(String pama) {
		this.pama = pama;
	}

	public String getAccPeriod() {
		return accPeriod;
	}

	public void setAccPeriod(String accPeriod) {
		this.accPeriod = accPeriod;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageStart() {
		return pageStart;
	}

	public void setPageStart(Integer pageStart) {
		this.pageStart = pageStart;
	}

}
