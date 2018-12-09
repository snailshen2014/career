/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: MapUtil.java
 * @Prject: channelCenter
 * @Package: com.bonc.utils
 * @Description: MapUtil
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月8日 下午5:06:37
 * @version: V1.0  
 */

package com.bonc.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: MapUtil
 * @Description: MapUtil
 * @author: LiJinfeng
 * @date: 2016年12月8日 下午5:06:37
 */
public class MapUtil {
	
	/**
	 * @Title: allFieldsIsNotNull
	 * @Description: 判断HashMap<String,Object>是否为空及其键值对是否为null
	 * @return: boolean
	 * @param hashMap
	 * @return
	 * @throws: 
	 */
	public static boolean allFieldsIsNotNull(HashMap<String,Object> hashMap){
		
		boolean result = false;
		if(hashMap == null || hashMap.isEmpty() || hashMap.size() < 1){
			return result;
		}
		Set<Entry<String, Object>> entrySet = hashMap.entrySet();
		for(Entry<String, Object> entry:entrySet){
			if(StringUtils.isBlank(entry.getKey())){
				return result;
			}
			if(entry.getValue() == null){
				return result;
			}
		}
		result = true;
		return result;
		
	}
	
	/**
	 * @Title: someFieldsIsNotNull
	 * @Description: 判断HashMap<String,Object>是否为空及指定键值对是否存在且不为null
	 * @return: boolean
	 * @param hashMap
	 * @param keyList
	 * @return
	 * @throws: 
	 */
	public static boolean someFieldsIsNotNull(HashMap<String, Object> hashMap,List<String> keyList){
		
		boolean result = false;
		if(hashMap == null || hashMap.isEmpty() || hashMap.size() < 1){
			return result;
		}
		for(String key:keyList){
			if(hashMap.get(key) == null || StringUtils.isBlank(String.valueOf(hashMap.get(key)))){
				return result;
			}
		}
		result = true;
		return result;
		
	}
	
	/**
	 * @Title: bean2HashMap
	 * @Description: 将Bean转化成HashMap
	 * @return: HashMap<String,Object>
	 * @param obj
	 * @return
	 * @throws: 
	 */
	public static HashMap<String,Object> bean2HashMap(Object obj){
		
		if(obj == null){  
            return null;  
        } 
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property:propertyDescriptors) {  
                String key = property.getName();  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
                    hashMap.put(key, value);  
                }  
            }  
        } catch (Exception e) {  
            return null;
        }  
	    return hashMap;
	    
	}
	
	/**
	 * @Title: hashMap2Bean
	 * @Description: 将HashMap转化成Bean
	 * @return: Boolean
	 * @param hashMap
	 * @param obj
	 * @return
	 * @throws: 
	 */
	public static Boolean hashMap2Bean(HashMap<String,Object> hashMap,Object obj){
		
		Boolean result = false;
		if(hashMap == null || hashMap.isEmpty() || hashMap.size()<1 || obj==null){
			return result;
		}
		try {  
            BeanUtils.populate(obj, hashMap);  
        } catch (Exception e) {  
            return result;
        }  
		result = true;
		return result;
		
	}

}
