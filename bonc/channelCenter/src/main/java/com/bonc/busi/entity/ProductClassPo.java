package com.bonc.busi.entity;

import java.util.List;

/**
 * 
 * <p>Title: JEEAC - ProductClassPo </p>
 * 
 * <p>Description: 产品分类(po) </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author liyang
 * @version 1.0.0
 */
public class ProductClassPo{
	/**
	 *  id
	 */
	private String productClassId;
	/**
	 * 产品分类名称
	 */
	private String productClassName;
	/**
	 * 产品分类编码
	 */
	private String productClassCode;
	/**
	 * 产品分类描述
	 */
	private String productClassDesc;
	/**
	 * 产品分类父ID
	 */
	private String parentId;
	/**
	 * 排序
	 */
	private Integer ord;
	/**
	 * 是否有效
	 */
	private Integer isValid;
	/**
	 * 是否叶子节点
	 */
	private Integer isLeaf;
	
	/**
	 * 是否根节点
	 */
	private String isRoot;
	
	private String provId; // 省分id
	
	private List<String> productClassIds;
	
	private String areaId;
	
	
	
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getIsRoot() {
		return isRoot;
	}
	public void setIsRoot(String isRoot) {
		this.isRoot = isRoot;
	}
	public String getProvId() {
		return provId;
	}
	public void setProvId(String provId) {
		this.provId = provId;
	}
	public String getProductClassId() {
		return productClassId;
	}
	public void setProductClassId(String productClassId) {
		this.productClassId = productClassId;
	}
	public String getProductClassName() {
		return productClassName;
	}
	public void setProductClassName(String productClassName) {
		this.productClassName = productClassName;
	}
	public String getProductClassCode() {
		return productClassCode;
	}
	public void setProductClassCode(String productClassCode) {
		this.productClassCode = productClassCode;
	}
	public String getProductClassDesc() {
		return productClassDesc;
	}
	public void setProductClassDesc(String productClassDesc) {
		this.productClassDesc = productClassDesc;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Integer getOrd() {
		return ord;
	}
	public void setOrd(Integer ord) {
		this.ord = ord;
	}
	public Integer getIsValid() {
		return isValid;
	}
	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}
	public Integer getIsLeaf() {
		return isLeaf;
	}
	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}
	public List<String> getProductClassIds() {
		return productClassIds;
	}
	public void setProductClassIds(List<String> productClassIds) {
		this.productClassIds = productClassIds;
	}
}
