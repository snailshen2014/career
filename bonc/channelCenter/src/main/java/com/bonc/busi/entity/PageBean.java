/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: PageBean.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.entity
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月5日 上午10:28:19
 * @version: V1.0  
 */

package com.bonc.busi.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @ClassName: PageBean
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年12月5日 上午10:28:19
 */
@Component("pageBean")
@Scope("prototype")
public class PageBean {
	
	private Integer total;
	private Integer totalPage;
	private Integer pageSize;
	private Integer currentPage;
	private Integer startPage;
	private Integer endPage;
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	
	public Integer getTotalPage() {	
		// 不能整除
		if( total%pageSize > 0){ 
			this.totalPage = total/pageSize+1;
		}
		// 能够整除
		else{	
			this.totalPage = total/pageSize;
		}
		return totalPage;	
	}
	public void setTotalPage(Integer totalPage) {		
		this.totalPage = totalPage;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public Integer getCurrentPage() {		
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	
	public Integer getStartPage() {
		startPage = (this.currentPage - 1) * this.pageSize;
		return startPage;
	}
	public void setStartPage(Integer startPage) {
		this.startPage = startPage;
	}
	
	public Integer getEndPage() {
	    if(this.currentPage < this.totalPage){
	    	this.endPage = this.startPage+this.pageSize;
	    }
	    if(this.currentPage == this.totalPage){
	    	this.endPage = this.total;
	    }
		return endPage;
	}
	public void setEndPage(Integer endPage) {
		this.endPage = endPage;
	}
	
	@Override
	public String toString() {
		return "PageBean [total=" + total + ", totalPage=" + totalPage + ", pageSize=" + pageSize + ", currentPage="
				+ currentPage + ", startPage=" + startPage + "]";
	}
	
	

}
