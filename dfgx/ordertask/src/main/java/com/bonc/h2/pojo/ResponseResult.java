package com.bonc.h2.pojo;

import java.util.Date;

public class ResponseResult {

	private Object data;//返回的数据
	private String errorCode;//错误码
	private String deciceNumber;//电话号码
	private String serviceType;//服务类型
	private String operateNumber;//由服务人来决定是数据抽取还是及时查询，，可能这个标志需要变为帐单项明细查询的帐期和业务代码
	private String sequenceNum; //请求命令的序列号
	private String place;//受理地点
	private Date createTime;//创建时间
	
	private String flag;//响应包标志位  1成功；0失败
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public String getDeciceNumber() {
		return deciceNumber;
	}
	
	public void setDeciceNumber(String deciceNumber) {
		this.deciceNumber = deciceNumber;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getServiceType() {
		return serviceType;
	}
	
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getOperateNumber() {
		return operateNumber;
	}

	public void setOperateNumber(String operateNumber) {
		this.operateNumber = operateNumber;
	}

	public String getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(String sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getFlag() {
		return ("1".equals(flag)?"0":"1");
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}