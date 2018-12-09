/**  
 * Copyright ©1997-2017 BONC Corporation, All Rights Reserved.
 * @Title: ListUtil.java
 * @Prject: ordertask
 * @Package: com.bonc.utils
 * @Description: ListUtil
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2017年1月11日 下午9:09:35
 * @version: V1.0  
 */

package com.bonc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: ListUtil
 * @Description: ListUtil
 * @author: LiJinfeng
 * @date: 2017年1月11日 下午9:09:35
 */
public class ListUtil {
	
	/**
	 * @Title: splitList
	 * @Description: 将一个list分割为等长的size个list
	 * @return: List<List<HashMap<String,Object>>>
	 * @param sourceList
	 * @param size
	 * @return
	 * @throws: 
	 */
	public static List<List<HashMap<String, Object>>> splitList(
			List<HashMap<String, Object>> sourceList,int size) { 
		if(sourceList == null){
			return null;
		}
        List<List<HashMap<String, Object>>> listArr = new ArrayList<List<HashMap<String, Object>>>();  
        //获取被拆分的数组个数  
        /*int arrSize = sourceList.size()%size==0?sourceList.size()/size:sourceList.size()/size+1;*/  
        for(int i=0;i<size;i++) {  
        	List<HashMap<String, Object>>  sub = new ArrayList<HashMap<String, Object>>();
        	if(i<size-1){
        		sub = sourceList.subList(
        				i*(sourceList.size()/size), (i+1)*(sourceList.size()/size));
        	}
        	else{
        		sub = sourceList.subList(
        				i*(sourceList.size()/size),sourceList.size());
        	}
          
            listArr.add(sub);  
        }  
        return listArr;  
        
    }  
	
	
	public static void main(String[] args) {
		
		List<HashMap<String, Object>> sourceList = new ArrayList<HashMap<String, Object>>();
		for(int i = 0;i<11;i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(String.valueOf(i), i);
			sourceList.add(map);
		}
		List<List<HashMap<String, Object>>> splitList = splitList(sourceList, 5);
		System.out.println(splitList.toString());
	}

}
