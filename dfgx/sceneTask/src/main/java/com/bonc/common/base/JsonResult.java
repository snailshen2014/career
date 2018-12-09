package com.bonc.common.base;

/*
 * 定义APP端调用的通用返回结果
 * Author:zengdy
 * Date:2015-07-08
 */

import java.io.Serializable;
import java.util.Locale;

public class JsonResult  implements Serializable {
	private static final long serialVersionUID = -5835443811743436616L;
	
	private	String Code = null;				// ---- 返回的代码，非0表示出错，可能是系统出错，也可能是业务出错 
	private	String Message= null;			// --- 远程返回的信息提示
	private  	Object Data = null;				// --- 返回的数据项 ---
	private 	Locale  locale = null;
	
	public Locale getLocale(){
		return this.locale;
	}
	public void setLocale(Locale inLocale){
		this.locale = inLocale;
	}
	
	public String getCode(){
		return Code;
	}
	
	public String getMessage(){
         return Message;
	}
	
	public void setCode(String inCode){
		this.Code = inCode;
	}
	
	public void setMessage(String inMessage){
		this.Message = inMessage;
	}
	
	public Object getData() {
		return Data;
	}

	public void setData(Object data) {
		this.Data = data;
	}
	

}

