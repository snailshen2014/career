package com.bonc.busi.divide.model;

import java.util.List;

public class DivideArupBean {

	private Integer recId;// 当前的组织机构路径
	private Double arup;// 排序的依据
	
	private String orgPath;
	private List<Integer> recIds;
	private String isExe;
	
	

	public String getIsExe() {
		return isExe;
	}

	public void setIsExe(String isExe) {
		this.isExe = isExe;
	}

	public String getOrgPath() {
		return orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public List<Integer> getRecIds() {
		return recIds;
	}

	public void setRecIds(List<Integer> recIds) {
		this.recIds = recIds;
	}

	public Integer getRecId() {
		return recId;
	}

	public void setRecId(Integer recId) {
		this.recId = recId;
	}

	public Double getArup() {
		return arup;
	}

	public void setArup(Double arup) {
		this.arup = arup;
	}

}
