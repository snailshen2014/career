package com.bonc.busi.resolve.impl;
/***********************************************************************
 * Module:  DXDataResolver.java
 * Author:  sky
 * Purpose: Defines the Class DXDataResolver
 ***********************************************************************/

import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.resolve.DataResolver;
import com.bonc.busi.resolve.DataValidator;

/**
 * 工单系统传递格式  数据解析实现类 
 *
 * @author sky
 *
 */
@Component(value="orderDataResolver") 
public class OrderDataResolver implements DataResolver,DataValidator{

	@Override
	public  Map<String,Object> resolveData(String src) throws Exception{
		
		Map<String,Object> map;
		boolean a = fomatValidate(src);
		if(a){
			map = (Map<String, Object>) JSON.parse(src);
			return map;
		}else{
			throw new Exception("数据格式不正确！");
		}
	}

	@Override
	public boolean fomatValidate(String src) {
		
		try{
			JSON.parseObject(src);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	@Override
	public boolean busiValidate(Object src) {
		
		return false;
	}
}