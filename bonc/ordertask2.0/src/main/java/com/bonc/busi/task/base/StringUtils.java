package com.bonc.busi.task.base;
/*
 * @desc:STRING 工具
 * @author:曾定勇
 * @time:20161212
 */

public class StringUtils {
	
	/*
	 * 是否空串检查
	 */
	public	static boolean	isNotNull(String  data){
		if(data == null)  return false;
		if(data.equals("")) return false;
		if(data.length() == 0) return false;
		if(data.toUpperCase().equalsIgnoreCase("NULL")) return false;
		if(data.toUpperCase().equals("NULL")) return false;
		return true;	
	}

}
