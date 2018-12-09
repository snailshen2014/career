package com.bonc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SystemHelper {
	/**
	 * 判断当前操作系统版本
	 */
//	public static boolean isWindowsSystem() {
//		boolean ifIs = false;
//		Properties properties = System.getProperties();
//		String systemName = (String) properties.get("os.name");
//		if (systemName != null && !"".equals(systemName)) {
//			systemName = systemName.toLowerCase();
//			if (StringEdit.searchString(systemName, "windows")) {
//				ifIs = true;
//			}
//		}
//		return ifIs;
//	}
	
	public Properties getProperties() {
		String sConfigFile = "config/H2Server.properties";
		InputStream inStream = null;
		Properties propConfig = new Properties();
		try {
			
			inStream = getClass().getClassLoader().getResourceAsStream("H2Server.properties");;
			if (inStream == null) {
				System.out.println("Can't locate file:" + sConfigFile);
			}
			propConfig.load(inStream); //this may throw IOException
			inStream.close();
		} catch (IOException _ioEx) {
			System.out.println("Can't locate file:" + sConfigFile);
		} finally {
			inStream = null;
		}
		return propConfig;
	}
}
