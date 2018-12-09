package com.bonc.busi.task.base;
/*
 * 全局，保存全局变量
 */

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors; 




public class Global {
	// --- 系统缓存变量 ---
	//public  static  Map<String,String>   mapSysCommCfg = new HashMap<String,String>();
	// --- 当前同步租户数据 ---
	//public  static  Map<String,Object>   mapGlobalTenantInfo = null;
	private	static ExecutorService cachedThreadPool = null;
	private	static boolean	bInit = false;
	/*
	 * 初始化方法
	 */
	public	static boolean	init(){
		if(bInit)  return true;
		// --- 创建一个没有限制的线程池  -----------------------
		cachedThreadPool = Executors.newCachedThreadPool();
		System.out.println("线程池变量创建完成");
		
		// --- 其它初始化 ---
		bInit = true;
		return true;
	}
	/*
	 * 得到线程变量
	 */
	public	static ExecutorService	getExecutorService(){
		return cachedThreadPool;
	}
	
}
