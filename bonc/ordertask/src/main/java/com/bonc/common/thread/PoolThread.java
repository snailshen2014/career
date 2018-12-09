package com.bonc.common.thread;
/*
 * 线程管理
 * @author:曾定勇
 */

import	java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoolThread extends Thread{
	private final static Logger log= LoggerFactory.getLogger(PoolThread.class);
	ThreadBaseFunction		ThreadBaseFunctionIns = null;
	boolean							bNodataQuitFlag = false;
	
	// --- 构造函数 ---
	public PoolThread(ThreadBaseFunction func, boolean bNodataQuit){
		this.ThreadBaseFunctionIns = func;
		this.bNodataQuitFlag = bNodataQuit;
	}
	// --- 运行函数 ---
	@Override
	public	void run(){
		int		result;
		while(1 > 0){
			Object data = null;
			data = ThreadBaseFunctionIns.getData();
			
			if(data == null){   // --- 没有找到数据  ---
				//log.debug(" no data get ,return ---");
				if(bNodataQuitFlag)   // --- 如果配置了没有数据就退出，则退出 ---
					break;
				else{
					try{
					Thread.sleep(2000);    // --- 休息2秒钟 ---
					}catch(Exception e){
						e.printStackTrace();
					}
					continue;
				}
			}
			// --- 取到了数据,处理数据 ---
			result = ThreadBaseFunctionIns.handleData(data);
			if(result != 0){  // --- 非0表示出错，但不退出，继续执行  ---
				log.warn("--- error happened ,return ---");
				break;
			}			
		}
	}

}
