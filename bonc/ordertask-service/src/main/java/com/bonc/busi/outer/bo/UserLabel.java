package com.bonc.busi.outer.bo;

import java.io.Serializable;

/**
 * 用户标签
 * @author Administrator
 *
 */
public class UserLabel implements Serializable {

	private static final long serialVersionUID = -5185370422282894657L;
	
	//标签名称
	private String xCloudColumn;
	
	//标签类型
	private String dataType;
	
	//标签长度
	private int length;
	
	//标签备注
	private String columnDesc;


	public String getxCloudColumn() {
		return xCloudColumn;
	}

	public void setxCloudColumn(String xCloudColumn) {
		this.xCloudColumn = xCloudColumn;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getColumnDesc() {
		return columnDesc;
	}

	public void setColumnDesc(String columnDesc) {
		this.columnDesc = columnDesc;
	}

	@Override
	public String toString() {
		return "UserLabel [xCloudColumn=" + xCloudColumn + ", dataType=" + dataType + ", length=" + length
				+ ", columnDesc=" + columnDesc + "]";
	}
}
