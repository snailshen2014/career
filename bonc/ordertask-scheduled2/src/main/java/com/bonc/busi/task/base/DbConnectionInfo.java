package com.bonc.busi.task.base;
/*
 * @desc:数据库连接信息
 * @author:曾定勇
 * @time:2016-12-27
 */

import java.sql.Connection;
import java.sql.Statement;

public class DbConnectionInfo {
	private	char		m_cState = '0';			// --- 状态,0-未初始化,1-未使用,2-使用中
	private	Connection	m_cssCon = null;			// --- 连接 ---
	private	Statement 		m_cssSm =null;	
	private	int		m_iRecNums = 0;
	
	public		int    getRecNums(){
		return this.m_iRecNums;
	}
	public		void	  setRecNums(int iRecNums){
		this.m_iRecNums = iRecNums;
	}
	
	public	 Statement  getSm(){
		return m_cssSm;
	}
	public	void	setSm(Statement sm){
		this.m_cssSm = sm;
	}
	public	 char  getState(){
		return m_cState;
	}
	public	void	setState(char State){
		this.m_cState = State;
	}
	
	public	 Connection  getConnection(){
		return m_cssCon;
	}
	public	void	setConnection(Connection con){
		this.m_cssCon = con;
	}
}
