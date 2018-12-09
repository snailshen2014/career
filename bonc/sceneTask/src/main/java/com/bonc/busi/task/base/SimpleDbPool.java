package com.bonc.busi.task.base;
/*
 * @desc:简单的数据库连接池，仅适用于使用完立即释放的场景
 * @author:曾定勇
 * @time:2016-12-27
 */

import	java.util.List;
import	java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SimpleDbPool {
	private final static Logger log= LoggerFactory.getLogger(SimpleDbPool.class);
	private	String m_strDbDriver;						// --- 数据库驱动库名 ---
	private	String m_strDbUrl;							// --- 数据库URL ---
	private	String m_strDbUser; 						// --- 数据库用户名 ---
	private	String m_strDbPassword;					// --- 数据库密码 ---
	private	boolean	m_bInit = false;
	private	List<DbConnectionInfo>		listCon = new ArrayList<DbConnectionInfo>();
	
	// --- 构造函数 ---
	public	SimpleDbPool(String DbDriver,String DbUrl,String DbUser,String DbPasword){
		m_strDbDriver = DbDriver;
		m_strDbUrl = DbUrl;
		m_strDbUser = DbUser;
		m_strDbPassword = DbPasword;
	}
	// --- 初始化  ---
	public	synchronized  boolean	init(){
		if(m_bInit)  return true;
		try{
			Class.forName(m_strDbDriver);					// --- 数据库驱动加载 ---
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}	
		m_bInit = true;
		return true;
	}
	// --- 获取连接 ---
	public	synchronized	DbConnectionInfo	getConnection(){
		try{
			int		result = getValidCon() ;
			log.info("result={}",result);
			if(result  == -1){   // --- 无可用连接 ---
				Connection	conDb = DriverManager.getConnection(m_strDbUrl , m_strDbUser,m_strDbPassword);
				conDb.setAutoCommit(true);
				Statement		smLocal = conDb.createStatement();
				DbConnectionInfo DbConnectionInfoIns = new DbConnectionInfo();
				DbConnectionInfoIns.setState('2');
				DbConnectionInfoIns.setConnection(conDb);
				DbConnectionInfoIns.setSm(smLocal);
				listCon.add(DbConnectionInfoIns);
				return  DbConnectionInfoIns;
			}
			else{
				DbConnectionInfo DbConnectionInfoIns = listCon.get(result);
				DbConnectionInfoIns.setState('2');
				return DbConnectionInfoIns;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	// --- 查询当前可用的连接 ---
	private	int	getValidCon(){
		for(int i=0;i <listCon.size();++i ){
			if(listCon.get(i).getState() == '1')  return i;
		}
		return -1;
	}
	// --- 释放连接 ---
	public	void		freeConnection(DbConnectionInfo  con){
		con.setState('1');
	}
	/* 
	 * 关闭连接池
	 */
	public	void		freePool(){
		try{
			for(int i=0;i <listCon.size();++i ){
				DbConnectionInfo DbConnectionInfoIns = listCon.get(i);
				DbConnectionInfoIns.getSm().close();
				DbConnectionInfoIns.getConnection().close();
			}
		}catch(Exception e){
			
		}
	}

}
