package com.bonc.common.thread;
/*
 * 线程对应的函数定义，所有实例要继承此CLASS
 * @author:zengdingyong
 */
public class ThreadBaseFunction {

	/*
	 * 获取数据（建议一次从数据库中取一批数据，如：500,1000
	 */
	public	synchronized Object	getData(){
		return null;
	}
	/*
	 * 获取数据（建议一次从数据库中取一批数据，如：500,1000
	 */
	public	synchronized Object	getData(Object para,int iSerialId){
		return null;
	}
	/*
	 * 获取数据，不需要同步
	 */
	public	int	get(){
		return 0;
	}
	/*
	 * 处理数据
	 */
	public	int 	handle(Object data,Object para){
		return 0;
	}
	/*
	 * 执行数据（建议批量执行）
	 */
	public int	handleData(Object data){
		return 0;
	}
	/*
	 * 开始
	 */
	public	int	begin(){
		return 0;
	}
	/*
	 * 结束
	 */
	public	int	end(){
		return 0;
	}
	/*
	 * 结束判断
	 */
	public	boolean	endCheck(){
		return true;
	}
}
