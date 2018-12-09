package com.bonc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	//webService相关参数的properties
	public static Properties webService = new Properties();
	//微信渠道相关配置信息的properties
	public static Properties config = new Properties();
	static{
		
		loadProperties("/webservice-config.properties",webService);
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
     *   
            获取webService的相关参数
     */   
	public static String getWebService(String key){
		
		return webService.getProperty(key);
		
	}
	
    public static String getConfig(String key){
		
		return config.getProperty(key);
		
	}
}
