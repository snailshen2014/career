package com.bonc.busi.sendField.po;
/**
 * @ClassName: SendField
 * @Description: 下发数据字段配置码表
 * @author sky
 * @date: 2016年11月25日 
 */
public class SendField {
    /**
     * 列名
     */
	private String columnName;
	/**
	 * 别名
	 */
	private String alias;
	/**
	 * 排序号
	 */
	private int ord;
	/**
	 * 描述
	 */
	private String comments;
	/**
	 * 租户ID
	 */
	private String tenantId;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getOrd() {
		return ord;
	}

	public void setOrd(int ord) {
		this.ord = ord;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "SendField [columnName=" + columnName + ", alias=" + alias + ", ord=" + ord + ", comments=" + comments
				+ "]";
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	

	
	
}
