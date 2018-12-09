package com.bonc.utils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.bonc.common.utils.BoncExpection;

public class StringUtil {
	/**
	 * 生成32位编码
	 *
	 * @return string
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}

	public static void main(String[] args) {
		System.out.println(getUUID());
	}

	public static boolean validateStr(Object req){
		return null==req||"".equals((req+"").trim());
	}

	public static void validate(HashMap<String, Object> req,List<String> fields) {
		for(String field:fields){
			if(StringUtil.validateStr(req.get(field))){
				throw new BoncExpection(IContants.CODE_FAIL,field+" is empty !");
			}
		}
	}
}
