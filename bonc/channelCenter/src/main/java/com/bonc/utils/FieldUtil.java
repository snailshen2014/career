/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: FieldEmptyUtil.java
 * @Prject: channelCenter
 * @Package: com.bonc.utils
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月23日 上午11:46:13
 * @version: V1.0  
 */

package com.bonc.utils;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @ClassName: FieldEmptyUtil
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年11月23日 上午11:46:13
 */
public class FieldUtil {
	
	private static Log log = LogFactory.getLog(FieldUtil.class);

	/**
	 * @Title: FieldEmpty
	 * @Description: 判断对象各个字段是否为空
	 * @return: Boolean
	 * @param object
	 * @return
	 * @throws: 
	 */
	public static Boolean someFieldIsNotEmpty(Object object,List<String> fieldList){
		
		Boolean result = false;
		
		for (Field field : object.getClass().getDeclaredFields()) {
			if(!fieldList.contains(field.getName())){
				continue;
			}
			field.setAccessible(true);
		    try {
				if (field.get(object) == null) { 				
					log.warn(object.getClass().toString()+"的"+field.getName()+"属性为空");
				    return result;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return result;
			}
		}
		
		result = true;
		return result;
		
	}
	
}
