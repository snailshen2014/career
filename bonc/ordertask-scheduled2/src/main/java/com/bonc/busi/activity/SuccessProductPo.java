package com.bonc.busi.activity;


/**
 * 成功标准的产品
 * @author ICE
 *
 */
@SuppressWarnings("serial")
public class SuccessProductPo extends StockMarketingPo{

	//产品id
	private String productId;
	//产品编码
	private String productCode;
	//产品集中排序
	private String ord;
	//产品名称
	private String productName;
	//产品描述
	private String productDes;
	//产品是否有效
	private String isvalid;
	//产品成功分类
	private String productSuccessType;
	//产品归属地
	private String productDistrict;
	//租户id

	//产品来源
	private String productClasstype1;
	
	public String getProductClasstype1() {
		return productClasstype1;
	}

	public void setProductClasstype1(String productClasstype1) {
		this.productClasstype1 = productClasstype1;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setPoductName(String productName) {
		this.productName = productName;
	}

	public String getProductDes() {
		return productDes;
	}

	public void setProductDes(String productDes) {
		this.productDes = productDes;
	}

	public String getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(String isvalid) {
		this.isvalid = isvalid;
	}

	public String getProductSuccessType() {
		return productSuccessType;
	}

	public void setProductSuccessType(String productSuccessType) {
		this.productSuccessType = productSuccessType;
	}

	public String getProductDistrict() {
		return productDistrict;
	}

	public void setProductDistrict(String productDistrict) {
		this.productDistrict = productDistrict;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getOrd() {
		return ord;
	}

	public void setOrd(String ord) {
		this.ord = ord;
	}
	//add by shenyj for tenant_id,activity_id
			private String activityId; //活动id
			private String tenantId; //租户id
			public String getActivityId() {
				return activityId;
			}

			public void setActivityId(String activityId) {
				this.activityId = activityId;
			}
			public String getTenantId() {
				return this.tenantId;
			}

			public void setTenantId(String tenantId) {
				this.tenantId = tenantId;
			}
			private String activity_seq_id;
			public String getActivity_seq_id() {
				return this.activity_seq_id;
			}

			public void setActivity_seq_id(String id) {
				this.activity_seq_id = id;
			}
}
