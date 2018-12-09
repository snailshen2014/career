package com.bonc.common.base;
/*
 * 定义APP端调用的通用返回结果
 * Author:zengdy
 * Date:2015-07-08
 */

import java.io.Serializable;

public class BDIJsonResult  implements Serializable {
	private static final long serialVersionUID = -5835443811743436616L;
	
	private	String status = "1";		    //1：正常   判断状态：如果返回1，流程继续  其他情况下
	private	String message= null;			//status 不为1的情况，抛出内容
	private  	Object Data = null;			// 存放返回数据
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return Data;
	}
	public void setData(Object data) {
		Data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}


