package com.bonc.busi.task.base;
/*
 * @desc:并发管理
 * @author:曾定勇
 * @time:2016-12-20
 */

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelManage {
	// --- 定义日志变量 ---
	private final static Logger log= LoggerFactory.getLogger(ParallelManage.class);
	// --- 定义承载的基础实例变量 ---
	private	ParallelFunc	ParallelFuncIns = null;
	// --- 得到线程池变量  ---
	private	ExecutorService pool = Global.getExecutorService();
	// --- 定义处理线程的数量 ---
	private	int	iThreadNum = 2;
	// --- 定义等候各处理线程结束的最终时间 ---
	private	int	iMaxWaitSeconds = 600;   // --- 缺省时间10分钟 ---
	// --- 构造函数 (基础实例必须由外部传入)---
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
	public	int	execute() {
		try{
			// --- 得到线程的数量 ---
			int		iLocalThreadNum = getThreadNum();
		
			// --- 如果线程数定义不合理，则设置为4个线程 ---
			if(iLocalThreadNum < 0 || iLocalThreadNum > 20 )iLocalThreadNum=4;
			// --- 向处理实例设置处理线程数 ---
			ParallelFuncIns.setHandleThreadNum(iLocalThreadNum);
			
			// --- 调用基础实例的开始函数 ---
			if(ParallelFuncIns.begin() != 0){
				log.warn("call begin return failed !!!");
				return -1;
			}
			// --- 启动线程执行处理操作 ,此时读操作还未开始，这些线程启动后处于等待状态---
			for(int i=0;i <iLocalThreadNum;++i ){
				log.info("manange 启动线程:{}",i);
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
					// --- 判断各处理线程是否已经结束 ---
					if(iCurHandleThreadNum <= 0){
						log.info("处理线程结束");
						break;
					}
					else{
						Thread.sleep(1000);   // --- 休息一秒钟 ---
						++iTotalNum;
					}
					// --- 判断是否已经过了超时时间 ---
					if(iTotalNum > iMaxWaitSeconds){
						log.warn("超时时间到,退出,当前线程数:{},state={}",iCurHandleThreadNum,ParallelFuncIns.getGetThreadState());
						break;
					}
				}catch(Exception e){
					e.printStackTrace();
					break;
				}
			}
			// -----调用基础实例的结束函数----------------
			if(ParallelFuncIns.end() != 0){
				log.warn("call end return failed !!!");
				return -2;
			}
		}catch(Exception e){
			log.info("并行处理---异常结束");
			// --- 将SQL异常往上抛，其它异常本地截获 ---
			if(e.getCause() instanceof  SQLException){
				log.info("sql error happend:{}",e.getMessage());
				throw e;
			}
			else{
				e.printStackTrace();
				return -1;
			}
		}
		// --- 返回正常结束 ---
		log.info("并行处理---正常结束");
		return 0;
	}

}
