package com.bonc.busi.activity;
import java.util.ArrayList;

import java.util.List;


import com.alibaba.fastjson.JSON;

public class JSONDeserialization {
	
	
		public void judgeJsonType (String jsonString){
			//传入的json数据为空
			if(jsonString==null){
			System.out.println("传入的json数据为空");
			//json数据不为空
			}else{
			
				//传来的json数据自带类型信息，可以转换成JavaBean对象
				String judgeType = jsonString.substring(2, 7);
					if("@type".equals(judgeType)){
						getObject(jsonString);
					}else{
						//根据传入的json数据格式判断属于哪种数据类型
								
							char[] ch=jsonString.toCharArray();
							char firstChar = ch[0];
									
							//传入的是List数组类型		
						 if((firstChar == '[')){ 
							getList(jsonString);
							//传入的是Object类或者是Map集合
						}else if(firstChar == '{'){ 
							getObjectMap(jsonString);
							//传入的是String字符串
						}else if(firstChar=='"'){
							getString(jsonString);
						}else{
							System.out.println("传入的json数据有误");
						}
					}
			}
		}
		
		
		//传入json数据,解析成Object对象
		
		public static Object getObject(String jsonString) {
	      Object obj = null;
	    	   obj = JSON.parse(jsonString);
	      
	        return obj;
	    }
		
		//传入一个String对象
			public static String getString(String jsonString) {
		        String s = null;
		        
		        	s = JSON.parseObject(jsonString,String.class);
				       
		        return s;
		    }
		//传入的是List数组
	public static List<Object> getList(String jsonString) {
	      
		List<Object> list = new ArrayList<Object>();
	       
	            list = JSON.parseArray(jsonString);
	       
	        return list;
	    }
	//传入的是object对象或map集合
	public static Object getObjectMap(String jsonString) {
			
			Object obj = null;
			obj = JSON.parseObject(jsonString);
				return obj;
	    }
	

}
