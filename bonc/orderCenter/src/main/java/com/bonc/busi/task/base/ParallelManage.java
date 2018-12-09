package com.bonc.busi.task.base;
/*
 * @desc:并发管理
 * @author:曾定勇
 * @time:2016-12-20
 */

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelManage {
	private final static Logger log= LoggerFactory.getLogger(ParallelManage.class);
	private	ParallelFunc	ParallelFuncIns = null;
	// --- 得到线程池变量  ---
	private	ExecutorService pool = Global.getExecutorService();
	private	int	iThreadNum = 2;
	private	int	iMaxWaitSeconds = 10800;   // --- 缺省时间180分钟 ---
	// --- 构造函数 ---
	public	ParallelManage(ParallelFunc ParallelFuncIns){
		this.ParallelFuncIns = ParallelFuncIns;
	}
	// --- 构造函数 （设置线程数据）---
	public	ParallelManage(ParallelFunc ParallelFuncIns,int ThreadNum){
		this.ParallelFuncIns = ParallelFuncIns;
		this.iThreadNum = ThreadNum;
	}
	// --- 构造函数 （设置线程数，最大超时时间 ）---
	public	ParallelManage(ParallelFunc ParallelFuncIns,int ThreadNum,int MaxWaitSeconds){
		this.ParallelFuncIns = ParallelFuncIns;
		this.iThreadNum = ThreadNum;
		this.iMaxWaitSeconds = MaxWaitSeconds;
	}
	/*
	 * 设置最长超时时间
	 */
	public	void		setMaxWaitSeconds(int MaxWaitSeconds){
		this.iMaxWaitSeconds = MaxWaitSeconds;
	}
	/*
	 * 获得最长超时时间
	 */
	public	int		getMaxWaitSeconds(){
		return this.iMaxWaitSeconds ;
	}
	/*
	 * 得到线程数量 
	 */
	public	int		getThreadNum(){
		return iThreadNum;
	}
	/*
	 * 设置线程数量
	 */
	public	void		setThreadNum(int ThreadNum){
		this.iThreadNum = ThreadNum;
	}
	/*
	 * 执行方法
	 */
	public	int	execute(){
		// --- 得到线程的数量 ---
		int		iLocalThreadNum = getThreadNum();
		
		if(iLocalThreadNum < 0 || iLocalThreadNum > 20 )iLocalThreadNum=4;
		// --- 设置处理线程数 ---
		ParallelFuncIns.setHandleThreadNum(iLocalThreadNum);
		if(ParallelFuncIns.begin() != 0){
			log.warn("call begin return failed !!!");
			return -1;
		}
		// --- 执行中间处理 ---
		/*
		// --- 启动线程执行数据提取操作  ---
		pool.execute(new Runnable() {  
		    public void run() {  
		    	ParallelFuncIns.getData();
		    }  
		   });  
		   */
		// --- 启动线程执行处理操作 ---
		for(int i=0;i <iLocalThreadNum;++i ){
			pool.execute(new Runnable() {  
			    public void run() {  
			    	ParallelFuncIns.handleData();
			    }  
			   });  
		}
		// --- 主线程执行取数据 ---
		ParallelFuncIns.getData();
		log.info("get 结束");
		
		// --- 等待线程结束  ---	
		int		iCurHandleThreadNum = 0;
		int		iTotalNum = 0;
		while(1 > 0){
			try{
				iCurHandleThreadNum = ParallelFuncIns.getCurHandleThreadNum();
				if(iCurHandleThreadNum == 0){
					log.info("处理线程结束");
					break;
				}
				else{
					Thread.sleep(1000);   // --- 休息一秒钟 ---
					++iTotalNum;
				}
				if(iTotalNum > iMaxWaitSeconds){
					log.warn("超时时间到,退出");
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
				break;
			}
		}
		// ---------------------
		if(ParallelFuncIns.end() != 0){
			log.warn("call end return failed !!!");
			return -2;
		}
		return 0;
	}

}
