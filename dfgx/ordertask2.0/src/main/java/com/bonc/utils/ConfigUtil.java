package com.bonc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigUtil {
	
	private static Log log = LogFactory.getLog(ConfigUtil.class);
	
	private static Properties mConfig;

	static {
		initProperties();
	}
	
	public static void reLoad (){
		initProperties();
	}
	
	private static void initProperties(){
		mConfig = new Properties();
		InputStream is = null;
		try {
			//获取配置文件输入流
			is =ConfigUtil.class.getResourceAsStream("/config.properties");
			mConfig.load(is);
			log.info("成功加载配置文件config.properties.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public static String inputStream2String (InputStream in) { 
        StringBuffer out = new StringBuffer(); 
        byte[] b = new byte[4096]; 
        try {
			for (int n; (n = in.read(b)) != -1;) { 
				out.append(new String(b, 0, n)); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
        return   out.toString(); 
	} 
	
	/**
	 * Retrieve a property value
	 * 
	 * @param key   Name of the property
	 * @return String Value of property requested, null if not found
	 */
	public static String getProperty(String key) {
		log.debug("Fetching property [" + key + "=" + mConfig.getProperty(key) + "]");
		return mConfig.getProperty(key);
	}

	/**
	 * Retrieve a property value
	 * 
	 * @param key Name of the property
	 * @param defaultValue Default value of property if not found
	 * @return String Value of property requested or defaultValue
	 */
	public static String getProperty(String key, String defaultValue) {
		log.debug("Fetching property [" + key + "=" + mConfig.getProperty(key) + ",defaultValue=" + defaultValue + "]");
		String value = mConfig.getProperty(key);
		return value == null?defaultValue:value;
	}

	/**
	 * Retrieve a property as a boolean ... defaults to false if not present.
	 */
	public static boolean getBooleanProperty(String name) {
		return getBooleanProperty(name, false);
	}

	/**
	 * Retrieve a property as a boolean ... with specified default if not
	 * present.
	 */
	public static boolean getBooleanProperty(String name, boolean defaultValue) {
		// get the value first, then convert
		String value = getProperty(name);
		return value != null?(Boolean.valueOf(value)):defaultValue;
	}

	/**
	 * Retrieve a property as an int ... defaults to 0 if not present.
	 */
	public static int getIntProperty(String name) {
		return getIntProperty(name, 0);
	}

	/**
	 * Retrieve a property as a int ... with specified default if not present.
	 */
	public static int getIntProperty(String name, int defaultValue) {
		// get the value first, then convert
		String value = ConfigUtil.getProperty(name);

		if (value == null)
			return defaultValue;
		return (new Integer(value)).intValue();
	}

	/**
	 * Retrieve all property keys
	 * 
	 * @return Enumeration A list of all keys
	 */
	@SuppressWarnings("rawtypes")
	public static Enumeration keys() {
		return mConfig.keys();
	}

	public static boolean isTrue(String name) {
		return "true".equalsIgnoreCase(getProperty(name) == null ? "" : getProperty(name).trim());
	}
}
