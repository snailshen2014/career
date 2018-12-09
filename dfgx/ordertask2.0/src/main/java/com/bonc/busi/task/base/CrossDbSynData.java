package com.bonc.busi.task.base;
/*
 * @desc:跨库同步数据，采用内存中同步的方法
 * @author:曾定勇
 * @time:2016-12-27
 */

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import	java.util.List;
//import java.util.Map;
import java.util.ArrayList;
//import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossDbSynData extends ParallelFunc{
	private final static Logger log= LoggerFactory.getLogger(CrossDbSynData.class);
	private	String m_strFromDbDriver;						// --- 源数据库驱动库名 ---
	private	String m_strFromDbUrl;							// --- 源数据库URL ---
	private	String m_strFromDbUser; 						// --- 源数据库用户名 ---
	private	String m_strFromDbPassword;					// --- 源数据库密码 ---
//	private	String m_strToDbDriver;							// --- 目标数据库驱动库名 ---
//	private	String m_strToDbUrl;								// --- 目标数据库URL ---
//	private	String m_strToDbUser;								// --- 目标数据库用户名 ---
//	private	String m_strToDbPassword;						// --- 目标数据库密码 ---
	private	String m_strFromDbSql;							// --- 从源数据库取数的SQL语句 ---
	private	String m_strToDbTableName;					// --- 目标数据库表名 ---
	private	String m_strToDbTableCols;						// --- 目标数据库字段名  ---
	
	private	Connection 		m_conFrom = null;
	//private	Connection 		m_conTo = null;
	private	Statement 		m_smFrom =null;
	//private	Statement 		m_smToCols =null;   // --- 用于纪录MYSQL字段属性 ---
//	private	PreparedStatement 		m_psTo =null;
	private	ResultSet 			m_rsFrom = null;
	private	String				m_strToSqlInsert  = null;
	private	ResultSetMetaData	m_rsmdToCols = null;
	private	ResultSetMetaData	m_rsmdFrom = null;
	
	private	int  				m_iBatchRecs = 5000;    // --- 一次取5000条
	private	int				m_iCommitRecs = 200;  // 200次提交  
	//private	int  				m_iBatchRecs = 50;
	private	int				m_iTotal = 0;
	private	java.util.Date		m_dateBegin = null;
	private	SimpleDbPool	m_cssDbPool = null;
	
	
	public	CrossDbSynData(
			String FromDbDriver,						
			String FromDbUrl,  // --- 源数据库URL ---
			String FromDbUser,  						// --- 源数据库用户名 ---
			String FromDbPassword,					// --- 源数据库密码 ---
			String ToDbDriver,		
			String ToDbUrl,									// --- 目标数据库URL ---
			String ToDbUser,								// --- 目标数据库用户名 ---
			String ToDbPassword,						// --- 目标数据库密码 ---
			String FromDbSql,							// --- 从源数据库取数的SQL语句 ---
			String ToDbTableName,					// --- 目标数据库表名 ---
			String ToDbTableCols						// --- 目标数据库字段名  ---
			){
		m_strFromDbDriver = FromDbDriver;
		m_strFromDbUrl = FromDbUrl;
		m_strFromDbUser = FromDbUser;
		m_strFromDbPassword = FromDbPassword;
//		m_strToDbDriver = ToDbDriver;
//		m_strToDbUrl = ToDbUrl;
	//	m_strToDbUser = ToDbUser;
	//	m_strToDbPassword = ToDbPassword;
		m_strFromDbSql = FromDbSql;
		m_strToDbTableName = ToDbTableName;
		m_strToDbTableCols = ToDbTableCols;	
		m_cssDbPool  = new SimpleDbPool(ToDbDriver,ToDbUrl,ToDbUser,ToDbPassword);
	}
	
//	private	int  				m_iBatchRecs = 5000;    // --- 一次取5000条
//	private	int				m_iCommitRecs = 200;  // 200次提交  
	//private	int  				m_iBatchRecs = 50;
	public		void		setBatchRecs(int  iBatchRecs){
		this.m_iBatchRecs = iBatchRecs;
	}
	public		void		setCommitRecs(int  iCommitRecs){
		this.m_iCommitRecs = iCommitRecs;
	}
	public		int		getBatchRecs(){
		return this.m_iBatchRecs;
	}
	public		int		getCommitRecs(){
		return m_iCommitRecs;
	}
	
	/*
	 * 开始方法
	 */
	@Override
	public	int	begin(){
		// --- 得到数据库链接 ---
		m_dateBegin = new java.util.Date();
		try{
			Class.forName(m_strFromDbDriver);				// --- 源数据库驱动加载 ---
			if(m_cssDbPool.init() == false){
				log.info("SimpleDbPool 初始化错误");
				return -1;
			}
			//Class.forName(m_strToDbDriver);					// --- 目标数据库驱动加载 ---
			// --- 得到源数据库的连接 ---
			m_conFrom = DriverManager.getConnection(m_strFromDbUrl , m_strFromDbUser,m_strFromDbPassword);
			// --- 得到目标数据库的连接 ---
			//m_conTo = DriverManager.getConnection(m_strToDbUrl , m_strToDbUser,m_strToDbPassword);
			// --- 在源数据执行SQL 语句 -----------------------------------
			m_smFrom = m_conFrom.createStatement();
			m_rsFrom = m_smFrom.executeQuery(m_strFromDbSql);
			m_rsmdFrom= m_rsFrom.getMetaData();    // --- 得到源数据库的RSMD ---
			
			StringBuilder		sb = new StringBuilder();
			// --- 生成目标入库SQL语句 ---
			sb.append("INSERT INTO ");
			sb.append(m_strToDbTableName);
			sb.append(" (");
			sb.append(m_strToDbTableCols);
			sb.append(") ");
			sb.append("VALUES ");
			/*
			String		cols[]  = m_strToDbTableCols.split(",");
			for(int i = 0;i < cols.length;++i){
				if(i > 0)  sb.append(",");
				sb.append("?");
			}
			sb.append(")");
			*/
			m_strToSqlInsert = sb.toString();
			// --- 结束插入语句生成 ---
			DbConnectionInfo	DbConnectionInfoTmp = m_cssDbPool.getConnection();
			if(DbConnectionInfoTmp == null){
				log.error("得不到数据库连接");
				return -1;
			}
			Connection	conTo = DbConnectionInfoTmp.getConnection();
			//conTo.setAutoCommit(true);
			// --- 得到MYSQL的字段属性 -------------------------------------------
			sb.setLength(0);
			sb.append("SELECT ");
			sb.append(m_strToDbTableCols);
			sb.append("  FROM ");
			sb.append(m_strToDbTableName);
			sb.append("  WHERE 1=0 ");
			log.info("sql cols ={}",sb.toString());
			//Statement	smToCols = conTo.createStatement();
			Statement	smToCols = DbConnectionInfoTmp.getSm();
			ResultSet		rsDbToCols = smToCols.executeQuery(sb.toString());
			m_rsmdToCols = rsDbToCols.getMetaData();
			while (rsDbToCols.next());
			//smToCols.close();
			rsDbToCols.close();
			//smToCols = null;
			rsDbToCols = null;
			conTo.commit();
			smToCols.close();
			smToCols = null;
			// --- 释放连接 ------------------------------------------
			m_cssDbPool.freeConnection(DbConnectionInfoTmp);
			// --- 结束目标数据库 字段属性获取 -----------------------------------------------		
			log.info("mysql insert sql ={}",m_strToSqlInsert);
		//	m_psTo = m_conTo.prepareStatement(m_strToSqlInsert);
				//	ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	/*
	 * 结束方法
	 */
	@Override
	public	int	end(){
		log.info("userlabelsyn finished ,num:{},time:{}",m_iTotal,new java.util.Date().getTime()-m_dateBegin.getTime());
		m_cssDbPool.freePool();
		return 0;
	}
	/*
	 * 提取数据，子类需要实现此方法
	 */
	@Override
	public	Object		get(){
		int			recs= 0;
		//List<Map<Integer,Object>>		listData = new ArrayList<Map<Integer,Object>>();
		List<String>		listData = new ArrayList<String>();
		try{
			while(m_rsFrom.next()){
				++m_iTotal;
				++recs;
				String  strResult = CommonUtils.qryRowToMysqlRow(m_rsmdToCols,m_rsmdFrom,m_rsFrom);
				if(strResult == null){   // --- 出错了 ---
					log.info("返回为空");
					return null;
				}	
				listData.add(strResult);
				if(recs >= m_iBatchRecs){             // --- 返回  ---
					log.info("total recs ={}",m_iTotal);
					break;
				}
			}// --- while ---
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		if(listData.size() > 0)  return listData;
		return null;
	}
	/*
	 * 处理
	 */
	@Override
	public		int		handle(Object data){
	//	try{
		List<String>   listData   =  (List<String>) data;
		// --- 单独连接数据库  ---
		// --- 得到目标数据库的连接 ---
		DbConnectionInfo	DbConnectionInfoTmp = m_cssDbPool.getConnection();
		if(DbConnectionInfoTmp == null){
			log.error("得不到数据库连接");
			return -1;
		}
		StringBuilder			sb = new StringBuilder();
		int						iRecNums = DbConnectionInfoTmp.getRecNums();
		sb.append(m_strToSqlInsert);
		try{
			//Connection	conTo = DbConnectionInfoTmp.getConnection();
			//conTo.setAutoCommit(true);
			Statement		smLocal = DbConnectionInfoTmp.getSm();
			//PreparedStatement 		psTo =null;
			//psTo = conTo.prepareStatement(m_strToSqlInsert);
			int i=0;
			for(String item:listData){
				if(i>0){
					sb.append(",");
				}
				sb.append(item);
				++i;
			}
			++iRecNums;
			//smLocal.addBatch(sb.toString());
			DbConnectionInfoTmp.setRecNums(iRecNums);
			log.info("iRecNums:{}",iRecNums);
			smLocal.execute(sb.toString());
			if(iRecNums >= m_iCommitRecs){   // --- 100万提交一次  ---
				//smLocal.executeBatch();
				DbConnectionInfoTmp.getConnection().commit();
				smLocal.close();
				iRecNums = 0;
				DbConnectionInfoTmp.setRecNums(0);
				log.info(" bactch execute");
				
			}
			//log.info("sql={}",sb.toString());
			//smLocal.execute(sb.toString());
		
			//psTo.executeBatch();		
			//conTo.commit();
			//smLocal.close();
			//log.info(" bactch execute");
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}finally{
			// --- 释放连接 ------------------------------------------
			m_cssDbPool.freeConnection(DbConnectionInfoTmp);
		}
		return 0;
	}

}
