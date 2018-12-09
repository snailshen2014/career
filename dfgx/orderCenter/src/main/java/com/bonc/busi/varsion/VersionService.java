package com.bonc.busi.varsion;

import java.util.HashMap;

/**
 * 
 * when server start call method init to bind all monitor's version 
 * and bind every monitor to a deal service extend @VersionSync 
 * 
 * we define a task to sacn MYSQL version and compare with memory version
 * if not equals sync method will be called
 * 
 * @author gaoYang
 *
 */
public interface VersionService {

	/**
	 * task called
	 */
	void scan();

	/**
	 * server start called
	 */
	void init();
	
	
	/**
	 * 
	 * we provide a method to change we monitor's version in MYSQL ,
	 * scan process can get it and notice it's service
	 * @param tenantId
	 * @param busiCode a monitor name
	 * @param content
	 */
	void changeVersion(String tenantId,String busiCode,String content);

	/**
	 * 
	 * we want look the busiName's memory version and MYSQL version 
	 * @param tenantId
	 * @param busiName
	 * @return
	 */
	HashMap<String, String> getVersion(String tenantId, String busiName);
	
	/**
	 * register a service to a monitor
	 * @param tenantId
	 * @param busiName
	 * @return
	 */
	public void register(String tenantId,String busiName,String version);
}
