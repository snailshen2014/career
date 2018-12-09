package com.bonc.busi.activity;

import java.util.Properties;




public class ClyxPropertyInfo extends StockMarketingPo{
	
	private static String bpmGenZong;
	private static String tableQuery;
	private static String userExtract;
	private static String userBreak;
	private static String userTool;
	private static String userPool;
	
	public static String getUserTool() {
		return userTool;
	}

	public static void setUserTool(String userTool) {
		ClyxPropertyInfo.userTool = userTool;
	}

	public String getBpmGenZong() {
		return bpmGenZong;
	}

	public static String getTableQuery() {
		return tableQuery;
	}

	public static String getUserExtract() {
		return userExtract;
	}

	public static String getUserBreak() {
		return userBreak;
	}
	
	public static String getUserPool() {
		return userPool;
	}


	static {
		try {
			Properties prop = new Properties();
			prop.load(ClyxPropertyInfo.class.getResourceAsStream("/cfg.clyx.properties"));
			
			setPropertyInfo(prop);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setPropertyInfo(Properties prop) {
		try {
			ClyxPropertyInfo.bpmGenZong = prop.getProperty("bpmGenZong");
			ClyxPropertyInfo.tableQuery = prop.getProperty("tableQuery");
			ClyxPropertyInfo.userExtract = prop.getProperty("userExtract");
			ClyxPropertyInfo.userBreak = prop.getProperty("userBreak");
			ClyxPropertyInfo.userTool = prop.getProperty("userTool");
			ClyxPropertyInfo.userPool = prop.getProperty("userPool");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		
		return "{\"bpmGenZong\" : \"" + getBpmGenZong() + "\"," +
			   "\"tableQuery\" : \"" + getTableQuery() + "\"," +
			   "\"userExtract\" : \"" + getUserExtract() + "\"," +
			   "\"userBreak\" : \"" + getUserBreak() + "\"," +
			   "\"userPool\" : \"" + getUserPool() + "\"," +
			   "\"userTool\":\""+getUserTool()+"\""+
			   "}";
	}
	
}