package com.bonc.common.thread;
/*
 * 线程管理类
 * @author:曾定勇
 */

import	java.lang.Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.dave.jfs.core.base.SysGlobal;

public class ThreadPoolManage {
	private final static Logger log= LoggerFactory.getLogger(ThreadPoolManage.class);
		
	ThreadBaseFunction		ThreadBaseFunctionIns = null;
	int									iThreadNum = 5;
	boolean							bNodataQuitFlag = true;
	
public  ThreadPoolManage(ThreadBaseFunction func){
		
		if(func == null) return ;
		this.ThreadBaseFunctionIns = func;
	}
	
public ThreadPoolManage(int threadNum,ThreadBaseFunction func){
		
		if(func == null) return ;
		if(threadNum <= 0 || threadNum >50) iThreadNum = 5;
		else this.iThreadNum = threadNum;
		this.ThreadBaseFunctionIns = func;
	}
	
	public ThreadPoolManage(int threadNum,ThreadBaseFunction func,boolean bNodataQuit){
		
		if(func == null) return ;
		if(threadNum <= 0 || threadNum >50) iThreadNum = 25;
		else this.iThreadNum = threadNum;
		this.ThreadBaseFunctionIns = func;
		this.bNodataQuitFlag = bNodataQuit;
	}
	public	void	start(){
		log.info(" --- 线程任务执行开始 ---");
		ThreadBaseFunctionIns.begin();
		//创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(iThreadNum);
        for(int i=0;i < iThreadNum;++i){
        	//log.debug("--- create thread :{}",i);
        	Thread  ThreadIns = new PoolThread(ThreadBaseFunctionIns,bNodataQuitFlag);
        	pool.execute(ThreadIns);
        }
      //关闭线程池
        pool.shutdown();
        

        try{
        	//pool.awaitTermination(3600, TimeUnit.SECONDS);		// --- 等待1小时---
        	pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        	ThreadBaseFunctionIns.end();
        }catch(Exception e){
        	log.error(e.toString());
        }
        log.info(" --- 线程任务执行结束 ---");
   
	}

	/*
	 * 网上参考代码
	 * void shutdownAndAwaitTermination(ExecutorService pool) {
   pool.shutdown(); // Disable new tasks from being submitted
   try {
     // Wait a while for existing tasks to terminate
     if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
       pool.shutdownNow(); // Cancel currently executing tasks
       // Wait a while for tasks to respond to being cancelled
       if (!pool.awaitTermination(60, TimeUnit.SECONDS))
           System.err.println("Pool did not terminate");
     }
   } catch (InterruptedException ie) {
     // (Re-)Cancel if current thread also interrupted
     pool.shutdownNow();
     // Preserve interrupt status
     Thread.currentThread().interrupt();
   }
 }
	 */

}
