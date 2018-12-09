package com.bonc.busi.task.base;
/*
 * @desc:并发基本方法 ，各方法基于些
 * @author:曾定勇
 * @time:2016-12-20
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ParallelFunc {
	// --- 处理线程数量 ---
	private int iHandleThreadNum = 0;
	// --- 取数线程状态 ---
	protected char cGetThreadState = '0';
	// --- 处理线程当前数量 ---
	private int iCurHandleThreadNum = 0;
	protected boolean bNoDataQuitFlag = true; // --- 无数据退出标识 ---
	protected int iMaxQueueSize = 20;

	protected BlockingQueue<Object> buffer = new LinkedBlockingQueue<Object>();

	public void setNoDataQuitFlag(boolean NoDataQuitFlag) {
		this.bNoDataQuitFlag = NoDataQuitFlag;
	}

	/*
	 * 获取数据获取线程状态
	 * 
	 */
	public char getGetThreadState() {
		return cGetThreadState;
	}

	/*
	 * 设置最大队列
	 */
	public void setMaxQueueSize(int MaxQueueSize) {
		this.iMaxQueueSize = MaxQueueSize;
	}

	/*
	 * 开始方法
	 */
	public int begin() {
		return 0;
	}

	/*
	 * 结束方法
	 */
	public int end() {
		return 0;
	}

	/*
	 * 提取数据，子类需要实现此方法
	 */
	public Object get() {
		return null;
	}

	/*
	 * 获取数据方法
	 */
	public void getData() {
		cGetThreadState = '1'; // --- 取数据运行中 ---
		while (1 > 0) {
			try {
				if (buffer.size() > iMaxQueueSize) { // --- 超过最大队列数 ---
					Thread.sleep(1000); // --- 休息1秒钟 ---
					continue;
				}
				Object obj = get();
				if (obj == null) { // --- 无数据可以提取 ---
					if (bNoDataQuitFlag) {
						cGetThreadState = '9'; // --- 无数据结束 ---
						break;
					} else {
						Thread.sleep(5000); // --- 休息5秒钟 ---
						continue;
					}
				} // --- obj == null ---
					// System.out.println("put into buffer");
				buffer.put(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // --- while ---
	}

	/*
	 * 处理
	 */
	public int handle(Object data) {
		return 0;
	}

	/*
	 * 数据处理方法
	 */
	public void handleData() {
		try {
			while (1 > 0) {
				Object obj = buffer.poll();
				if (obj == null) { // --- 没有取到数据 ---
					if (cGetThreadState != '9') {
						try {
							Thread.sleep(500);
						} catch (Exception e) {
						}
						continue;
					}
					// System.out.println("没有取到数据");
					if (bNoDataQuitFlag) { // --- 无数据退出 ---
						break;
					}
					break;
				}
				// System.out.println("调用handle");
				if (handle(obj) != 0) {
					break;
				}

			}
			// --iCurHandleThreadNum;
		} finally {
			minusCurHandleThreadNum(); // --- 确何任何情况下都可以减去一
			System.out.println(" 退出线程:" + Thread.currentThread().getName());
		}
	}
	
	/*
	 * 减少线程数
	 */
	//private	  void		minusCurHandleThreadNum(){
	protected	synchronized void		minusCurHandleThreadNum(){
		--iCurHandleThreadNum;
	}

	/*
	 * 设置处理线程数量
	 */
	public void setHandleThreadNum(int HandleThreadNum) {
		this.iHandleThreadNum = HandleThreadNum;
		this.iCurHandleThreadNum = HandleThreadNum;
	}

	/*
	 * 得到当前处理线程的数量
	 */
	public int getCurHandleThreadNum() {
		return this.iCurHandleThreadNum;
	}

}
