package com.bonc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	//字段转化值的properties
	public static Properties fields = new Properties();
	//FTP相关参数的properties
	public static Properties ftps = new Properties();
	
	public static Properties config = new Properties();
	
	static{
		loadProperties("/field.properties",fields);
		loadProperties("/ftp.properties",ftps);
		loadProperties("/config.properties",config);
	}
	
	 /**  
     *   
            加载对应的properties文件
     */   
	public static void loadProperties(String path, Properties prop){
		
		InputStream is = null;
		is = PropertiesUtil.class.getResourceAsStream(path);
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取配置信息
	 * @param key
	 * @return
	 */
	public static String getConfig(String key){
		return config.getProperty(key);
	}
	
		
	 /**  
     *   
            获取字段的转化名字
     */   
	public static String getField(String key){
		
		return fields.getProperty(key);
		
	}
	
	/**  
     *   
            获取FTP的相关参数
     */   
	public static String getFTP(String key){
		
		return ftps.getProperty(key);
		
	}
	
}
