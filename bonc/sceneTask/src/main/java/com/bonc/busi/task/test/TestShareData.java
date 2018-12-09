package com.bonc.busi.task.test;

/**
 * @desc 用来测试kafka拉取数据的速度
 * @author yangjingxiong 
 */
public class TestShareData {
	public static final int MESSAGENUM = 10005;// 测试,拉取信息总数
	private int pollnum = 0;// 拉取信息数量
	private int handleNUM = 0;// 处理信息数量

	public synchronized int getPollnum() {
		return pollnum;
	}

	public synchronized void setPollnum(int pollnum) {
		this.pollnum = pollnum;
	}

	public synchronized int getHandleNUM() {
		return handleNUM;
	}

	public synchronized void setHandleNUM(int handleNUM) {
		this.handleNUM = handleNUM;
	}
}
