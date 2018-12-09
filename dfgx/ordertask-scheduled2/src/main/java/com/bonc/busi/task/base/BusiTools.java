package com.bonc.busi.task.base;
/*
 * @desc:同步数据
 * @author:曾定勇
 * @time:2016-12-01
 */
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileReader; 
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.InetAddress; 
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.TransactionDefinition;

import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.statistic.service.StatisticService;
import com.bonc.busi.task.base.Global;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.bo.SysCommonCfg;
import com.bonc.common.base.JsonResult;

@Service("BusiTools")
public class BusiTools {
	
	private final static Logger log= LoggerFactory.getLogger(BusiTools.class);
	
	@Autowired
	 private JdbcTemplate jdbcTemplate;
	@Autowired
	 private BaseMapper   TaskBaseMapperIns;
	@Autowired
	private static FTPClient ftp; 
	@Autowired	private StatisticService StatisticServiceIns;
	//@Autowired
	//private DataSourceTransactionManager tm;
	/*
	 * 从全局变量中得到参数
	 */
	public		String		getValueFromGlobal(String strKey){
		return TaskBaseMapperIns.getValueFromSysCommCfg(strKey);
	}
	/*
	 * 设置全局变量，同时更新数据库
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public	 boolean	setValueToGlobal(String strKey,String strValue){
		TaskBaseMapperIns.updateSysCommonCfg(strKey, strValue);
		return true;
	}

	/*
	 * 得到唯一的序列号
	 */
	//@Transactional(propagation=Propagation.NOT_SUPPORTED)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public		synchronized	int		getSequence(String  SequenceName){
		// --- 开启事物定义相关数据 -------------------------------------
		//TransactionDefinition def =  new DefaultTransactionDefinition();
		//TransactionStatus status =  tm.getTransaction(def);
		// --- 得到当前值  ---
		 Map<String, Object> mapResult = jdbcTemplate.queryForMap("SELECT INIT_VALUE,CUR_VALUE,STEP,MAX_VALUE FROM SYS_SEQUENCE "
				+ " WHERE  SEQUENCE_NAME = '" + SequenceName +"'");
		 if(mapResult == null){
			 return -1;
		 }
		 int		iCur_Value = (Integer)mapResult.get("CUR_VALUE");
		 int		iMax_Value = (Integer)mapResult.get("MAX_VALUE");
		 int		iInit_Value = (Integer)mapResult.get("INIT_VALUE");
		 if(iCur_Value == -1  || ( iCur_Value >= iMax_Value && iMax_Value != -1)){
			 // --- 更新初始值 ---
			  jdbcTemplate.execute("UPDATE SYS_SEQUENCE SET CUR_VALUE = INIT_VALUE WHERE "
			 		+ " SEQUENCE_NAME = '"+SequenceName +"'");
			 return iInit_Value;
		 }
		
		// int		iCur_Value = (Integer)mapResult.get("CUR_VALUE");
		int  sequenceId =-1;
		TaskBaseMapperIns.updateSequence(SequenceName);
		sequenceId = TaskBaseMapperIns.getSequenceValue(SequenceName);
		//tm.commit(status);
		//try{
		//	Thread.sleep(50000);
		//}catch(Exception e){
			
		//}
		return sequenceId;
	}
	/*
	 * 得到活动序列号的值
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public		synchronized	int		getActivitySequence(int  sectionId){
		int result = jdbcTemplate.update("UPDATE ACTIVITY_ID_INFO SET CUR_VALUE = CUR_VALUE + 1 WHERE "
				+ " SECTION_ID = '" + sectionId +"'");
		if(result == 1){
			Map<String,Object> mapResult = jdbcTemplate.queryForMap("SELECT CUR_VALUE FROM ACTIVITY_ID_INFO WHERE "
					+ " SECTION_ID = '" + sectionId +"'");
			return (Integer)mapResult.get("CUR_VALUE");
		}
		else return -1;		
	}
	/*
	 * 活动序列号生成服务
	 */
	public	int		getActivitySeqId(){
		int		seq = getSequence("ACTIVITY_SEQ_ID");
		if(seq == -1) return -1;
		return getActivitySequence(seq);
	}
	/*
	 * 通过FTP方式判断远程目录是否存在，如果不存在则创建
	 */
	public	int	checkAndCreateFtpPath(String SrvIp,String User,String Password,String Path){
		// --- 联接远程FTP ---
		FTPClient ftpClient = null; //FTP 客户端代理
		try{
			ftpClient = connectFtpServer(SrvIp,User,Password);
			if(ftpClient == null){
				log.warn("连不上FTP服务器");
				return -1;
			}
			log.info("连上了FTP服务器");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件
			//ftpClient.enterLocalActiveMode();   //这句重要，不行换enterRemoteActiveMode 看看
			//ftpClient.enterRemotePassiveMode();
			ftpClient.enterLocalPassiveMode();    // --- 经测试，联接UBUNTU LINUX 这个方法可用 ---
			//ftpClient.enterRemoteActiveMode(host, port)
			if(ftpClient.changeWorkingDirectory(Path) == false){   // --- 工作路径不存在 ---
				if(ftpClient.makeDirectory(Path) == false){
					log.error("--- 创建远程目录失败 ---");
					return -1;
				}
			}
			  ftpClient.logout(); 
              ftpClient.disconnect(); 
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	/*
	 * 检查本地目录是否存在，不存在则创建
	 */
	public	int	checkAndCreateLocalPath(String Path){
		String fileName = "Path";  
        File file = new File(fileName);  
        if( !file.exists() ){  
            log.info("path:"+Path +" not exist");  
            // --- 创建目录 ---
            if(file.mkdirs()  == false) return -1;
        }  
		return 0;
	}
	/*
	 * 连接FTP服务器
	 */
	private	FTPClient		connectFtpServer(String SrvIp,String User,String Password){
		FTPClient ftpClient = null; //FTP 客户端代理
	    int reply; 
	    try { 
	    	ftpClient = new FTPClient(); 
	        ftpClient.setControlEncoding("UTF-8"); 
	        //ftpClient.configure(getFtpConfig()); 
	        ftpClient.connect(SrvIp); 
	        ftpClient.login(User,Password); 
	        reply = ftpClient.getReplyCode(); 
	        if (!FTPReply.isPositiveCompletion(reply)) { 
	            ftpClient.disconnect(); 
	            log.error("FTP server refused connection."); 
	            log.error("FTP 服务拒绝连接！"); 
	            return null;
	         } 
	        int defaultTimeoutSecond=30*60 * 1000;  
            ftpClient.setDefaultTimeout(defaultTimeoutSecond);  
            ftpClient.setConnectTimeout(defaultTimeoutSecond );  
            ftpClient.setDataTimeout(defaultTimeoutSecond);  
              
	        //ftpClient.setDataTimeout(1800000);   // --- 设置超时 1800秒（30分钟）---
	         log.debug("connect server end ");
	    } catch (SocketException e) { 
	                        e.printStackTrace(); 
	                        log.error("登录ftp服务器 {} 失败,连接超时！",SrvIp); 
	                        return null;
	    } catch (IOException e) { 
	                        e.printStackTrace(); 
	                        log.error("登录ftp服务器{} 失败，FTP服务器无法打开！",SrvIp); 
	                        return null;
	    } 
		return ftpClient;
	}
	/*
	 * 从FTP服务器得到数据，远程路径和本地路径需要提前准备好。
	 */
	public	int	getDataFromFtpServer(String SrvIp,String User,String Password,
			String RemoteFileName,String LocalFileName,boolean  deleteFileFlag){
		// --- 联接远程FTP ---
		FTPClient ftpClient = null; //FTP 客户端代理
		boolean bQuit = false;
		try{
			ftpClient = connectFtpServer(SrvIp,User,Password);
			if(ftpClient == null){
				log.warn("连不上FTP服务器");
				return -1;
			}
			log.info("连上了FTP服务器");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件
			//ftpClient.enterLocalActiveMode();   //这句重要，不行换enterRemoteActiveMode 看看
			//ftpClient.enterRemotePassiveMode();
			ftpClient.enterLocalPassiveMode();    // --- 经测试，联接UBUNTU LINUX 这个方法可用 ---
			//ftpClient.enterRemoteActiveMode(host, port)
			//ftpClient.enterRemoteActiveMode(InetAddress.getByName(SrvIp),21);   

			
			int			RemoteSepPos = -1;
			RemoteSepPos  = RemoteFileName.lastIndexOf('/');
			if(RemoteSepPos == -1){
				RemoteSepPos  = RemoteFileName.lastIndexOf('\\');
			}
			if(RemoteSepPos != -1){
				String	RemotePath = RemoteFileName.substring(0, RemoteSepPos);
				//String   RemoteRealFileName = RemoteFileName.substring(RemoteSepPos + 1);
				log.info("remotepath:"+RemotePath);
				if(ftpClient.changeWorkingDirectory(RemotePath) == false){   // --- 工作路径不存在 ---
					log.info(" work path not exist "+RemotePath);
					ftpClient.logout(); 
					ftpClient.disconnect(); 
					return -1;
					/*
					if(ftpClient.makeDirectory(RemotePath) == false){
						log.error("--- 创建远程目录失败 ---");
						return -1;
					}
					*/
				}
			}
			String[]			listNames = null;
			ftpClient.enterLocalPassiveMode();
			listNames = ftpClient.listNames();
			if(listNames == null){
				ftpClient.logout(); 
				ftpClient.disconnect(); 
				return -1;
			}
			String   RemoteRealFileName = RemoteFileName.substring(RemoteSepPos + 1);
			boolean   bTempFile = false;
			boolean   bRealFile = false;
			for(String item:listNames){
				if(item.equals(RemoteRealFileName)){   // --- 找到了文件  ---
					log.info(" find the file:{}",RemoteRealFileName);
					bRealFile = true;
					break;
				}
				if(item.contains(RemoteRealFileName)){
					// --- 发现了临时文件 --
					bTempFile = true; 
					log.info("find the temp file:{}",item);
				}			
				log.info("file:"+item);
			}
			/*
			if(bRealFile == false){
				if(bTempFile == false){
					log.warn("没有文件："+RemoteRealFileName);
					return -1;
				}
				else{
					int i = 0;
					while(1  > 0){
						++i;
						if(i > 60) {
							log.warn("长时间没有等到文件：{}",RemoteRealFileName);
							return -1;
						}
						ftpClient.enterLocalPassiveMode();
						listNames = ftpClient.listNames();
						for(String item:listNames){
							log.info("waiting, file name:{} real file:{}",RemoteRealFileName,item);
							if(item.equals(RemoteRealFileName)) break;
							else{
								try{
									Thread.sleep(2000);
								}catch(Exception e){
									e.printStackTrace();
									return -1;
								}
							}
						}
					}
				}
			}
			*/
			
			/*
			// --- 判断文件是否存在 ---
			String[]			listNames = null;
			String   RemoteRealFileName = null;
			if(RemoteSepPos != -1){
				RemoteRealFileName = RemoteFileName.substring(RemoteSepPos + 1);
			}
			else{
				RemoteRealFileName = RemoteFileName;
			}
			log.info("real remote file:"+RemoteRealFileName);
			listNames = ftpClient.listNames(RemoteRealFileName);
			if(listNames == null){
				log.warn("文件不存在");
				return -1;
			}
			else{
				for(String item:listNames){
					log.info("file:"+item);
				}
			}
			*/
			log.info("判断本地路径");
			// --- 判断本地路径是否存在，不存在则创建 ---
			int			LocalSepPos = -1;
			LocalSepPos  = LocalFileName.lastIndexOf('/');
			if(LocalSepPos == -1){
				LocalSepPos  = LocalFileName.lastIndexOf('\\');
			}
			if(LocalSepPos != -1){
				String	LocalPath = LocalFileName.substring(0, LocalSepPos);
				File file = new File(LocalPath);  
		        if( !file.exists() ){  
		            log.info("path:"+LocalPath +" not exist");  
		            // --- 创建目录 ---
		            if(file.mkdirs()  == false) {
		            	log.error("创建路径失败!!! ," + LocalPath);
		            	ftpClient.logout(); 
		    			ftpClient.disconnect(); 
		            	return -1;
		            }
		        }  
		        
			}
			log.info("开始提取文件");
			// --- 提取文件 ---
			 BufferedOutputStream buffOut = null; 
			//OutputStreamWriter buffOut = null; 
			 buffOut = new BufferedOutputStream(new FileOutputStream(LocalFileName)); 
			 //buffOut = new OutputStreamWriter(new FileOutputStream(LocalFileName),"UTF-8"); 
			 ftpClient.enterLocalPassiveMode();
             if(ftpClient.retrieveFile(RemoteFileName, buffOut) == false){
            	 log.error("提取文件："+RemoteFileName +"失败!!");
            	 ftpClient.logout(); 
     			ftpClient.disconnect(); 
            	 return -1;
             }
             else{
            	 log.info("结束文件提取");
             }
			// --- 退出 ---
            buffOut.flush();
            buffOut.close();
            // --- 判断是否删除远程文件  ---
            if(deleteFileFlag){
            	// --- delete 总出错，不执行了 ---
            	//ftpClient.enterLocalPassiveMode();
            	//ftpClient.deleteFile(RemoteFileName);
            }
            bQuit = true;
            try{
            	ftpClient.logout(); 
            	ftpClient.disconnect(); 
            }catch(Exception e){
            	
            }
			
			String	LocalPath1 = LocalFileName.substring(0, LocalSepPos);
			File file1 = new File(LocalPath1);  
			File[]  filetmp= file1.listFiles();
			for(File item:filetmp){
				log.info(" filename:"+item.getName() + " other:"+item.length());
			}
			log.info("从FTP获取文件:"+LocalFileName + "结束");
			
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			if(bQuit) return 0;
			else return -1;

		}
		finally{
			
		}
		return 0;
	}
	/*
	 * 在行云上执行DDL语句
	 */
	public		JsonResult		execDdlOnXcloud(String	sqlDdl,String tenantId){	
		JsonResult	JsonResultIns = new JsonResult();
		Date				begin = new Date();
		Connection connection = null;
		Statement statement =null;
		boolean		bReturn = true;
		try{
			Class.forName(getValueFromGlobal("DS.XCLOUD.DRIVER"));
			connection = DriverManager.getConnection(getValueFromGlobal("DS.XCLOUD.URL."+tenantId) , 
					getValueFromGlobal("DS.XCLOUD.USER."+tenantId), 
					getValueFromGlobal("DS.XCLOUD.PASSWORD."+tenantId));
			statement = connection.createStatement();
			statement.execute(sqlDdl);
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
		}catch(Exception e){
	
			log.info("在行云上执行命令报错 !!!");
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.toString());
			e.printStackTrace();
			bReturn = false;
		}finally
		{
			if(statement != null)
			{
				try{
					statement.close();
					connection.close();
				}catch(Exception e){}
			}
		}	
		// --- 如果是从行云出库,则需要延时以供行云改文件名 ---
		if(sqlDdl.toUpperCase().indexOf("EXPORT") != -1)
		if(bReturn){
			// --- 行云要花时间改文件名 ---
			Date				end = new Date();
			long  			dur= (end.getTime() - begin.getTime())/1000/10;
			if(dur < 40)  dur = 40;
			try{Thread.sleep(dur*1000);}catch(Exception e){}
		}
		return JsonResultIns;
	}
	/*
	 * 从行云出库
	 */
	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	//@Transactional(propagation=Propagation.NOT_SUPPORTED)
	//@annotation(xcloud)
	public		boolean			getDataFromXcloud(String  sqlData){
		Date				begin = new Date();
		Connection connection = null;
		Statement statement =null;
		boolean		bReturn = true;
		try{
			//Class.forName("com.bonc.xcloud.jdbc.XCloudDriver");
			Class.forName(getValueFromGlobal("DS.XCLOUD.DRIVER"));
			//Connection connection = DriverManager.getConnection("jdbc:xcloud:@192.168.0.161:1803/jingzhunhua", 
			//		"ut_upload", "bonc123");
			connection = DriverManager.getConnection(getValueFromGlobal("DS.XCLOUD.URL."+SysVars.getTenantId()) , 
					getValueFromGlobal("DS.XCLOUD.USER."+SysVars.getTenantId()), 
					getValueFromGlobal("DS.XCLOUD.PASSWORD."+SysVars.getTenantId()));
			statement = connection.createStatement();
			statement.execute(sqlData);
		}catch(Exception e){
			log.info("从行云出库报错 !!!");
			e.printStackTrace();
			bReturn = false;
		}finally
		{
			if(statement!=null)
			{
				try{
					statement.close();
					connection.close();
				}catch(Exception e){}
			}
		}	
		if(bReturn){
			// --- 行云要花时间改文件名 ---
			Date				end = new Date();
			long  			dur= (end.getTime() - begin.getTime())/1000/10;
			if(dur < 20)  dur = 20;
			try{Thread.sleep(dur*1000);}catch(Exception e){}
		}
		return bReturn;
	}
	/*
	 * 倒入MYCAT 
	 */
	//@Transactional(propagation=Propagation.NOT_SUPPORTED)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean loadDataInMycat(String sqlData){
		try{
			jdbcTemplate.execute(sqlData);
		}catch(Exception e){
			log.info("入MYCAT 出错");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/*
	 * 倒入MYSQL
	 */
	//@Transactional(propagation=Propagation.NOT_SUPPORTED)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean loadDataInMysql(String sqlData,String TenantId){
		//Date				begin = new Date();
		Connection connection = null;
		Statement statement =null;
		boolean		bReturn = true;
		try{
			Class.forName(getValueFromGlobal("DS.MYSQL.DRIVER"));
			/*
			 try{   
				    //加载MySql的驱动类   
				    Class.forName("com.mysql.jdbc.Driver") ;   
				    }catch(ClassNotFoundException e){   
				    System.out.println("找不到驱动程序类 ，加载驱动失败！");   
				    e.printStackTrace() ;   
				    return false;
				    }   
				    */			    
			connection = DriverManager.getConnection(getValueFromGlobal("DS.MYSQL.URL."+TenantId) , 
					getValueFromGlobal("DS.MYSQL.USER."+TenantId), 
					getValueFromGlobal("DS.MYSQL.PASSWORD."+TenantId));
			//connection = DriverManager.getConnection("jdbc:mysql://10.162.2.119:31699/henan0" , "orderrun",
			//		"orderrun");
			statement = connection.createStatement();
			log.info("sql:{}",sqlData);
			statement.execute(sqlData);
		}catch(Exception e){
			log.info("入MYSQL数据库出错 !!!");
			e.printStackTrace();
			bReturn = false;
		}finally
		{
			if(statement!=null)
			{
				try{
					statement.close();
					connection.close();
				}catch(Exception e){}
			}
		}	
		return bReturn;
	}
	/*
	 * 在MYSQL上执行DDL语句
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public	boolean	executeDdlOnMysql(String sqlData,String TenantId){
		Date				dateBegin = new Date();
		Connection connection = null;
		Statement statement =null;
		boolean		bReturn = true;
		try{
			Class.forName(getValueFromGlobal("DS.MYSQL.DRIVER"));
			connection = DriverManager.getConnection(getValueFromGlobal("DS.MYSQL.URL."+TenantId) , 
						getValueFromGlobal("DS.MYSQL.USER."+TenantId), 
						getValueFromGlobal("DS.MYSQL.PASSWORD."+TenantId));
			statement = connection.createStatement();
			statement.execute(sqlData);
		}catch(Exception e){
					log.info("在MYSQL上执行DDL语句出错 !!!");
					e.printStackTrace();
					bReturn = false;
		}finally
		{
			if(statement!=null)
			{
				try{
					statement.close();
					connection.close();
				}catch(Exception e){}
			}
		}	
		if(bReturn){
			Date		dateEnd = new Date();
			log.info(" MYSQL DDL,SQL:{},DURATON TIME:{}",sqlData,dateEnd.getTime()-dateBegin.getTime());
		}
		return bReturn;
	}
	/*
	 * 删除文件
	 */
	public	void		deleteFile(String fileName){
		log.info("delete fileName:"+fileName);
		File file = new File(fileName);  
        if( file.exists() ){  
        	file.delete();
        }
	}
	/*
	 * 插入通用日志表，此方法需要单独起日志
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public	void	 insertPltCommonLog(PltCommonLog  logData){
		TaskBaseMapperIns.insertPltCommonLog(logData);
	}
	/*
	 * 插入通用日志表，此方法需要单独起日志
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public	void	 updatePltCommonLog(PltCommonLog  logData){
		TaskBaseMapperIns.updatePltCommonLog(logData);
	}
	/*
	 * 提取有效的租户数据和省编号数据
	 */
	public	 List<Map<String, Object>>    getValidTenantInfo(){
		return jdbcTemplate.queryForList("SELECT TENANT_ID,TENANT_NAME,"
				+ "PROV_ID FROM TENANT_INFO WHERE STATE='1' ");
	}
	/*
	 * 提取目录
	 */
	public		String		getPath(String strFileAll){
		int			LocalSepPos = -1;
		LocalSepPos  = strFileAll.lastIndexOf('/');
		if(LocalSepPos == -1){
			LocalSepPos  = strFileAll.lastIndexOf('\\');
		}
		String	LocalPath = null;
		if(LocalSepPos != -1)
		   LocalPath = strFileAll.substring(0, LocalSepPos);
		return LocalPath;
	}
	/*
	 * 提取文件名
	 */
	public	String	getFile(String strFileAll){
		int			LocalSepPos = -1;
		LocalSepPos  = strFileAll.lastIndexOf('/');
		if(LocalSepPos == -1){
			LocalSepPos  = strFileAll.lastIndexOf('\\');
		}
		String		fileName = null;
		if(LocalSepPos != -1){
			fileName = strFileAll.substring(LocalSepPos + 1);
		}
		else  fileName = strFileAll;
		return fileName;
	}
	
	/*
	 * 从行去出库，和MYSQL库，直接在内存中入库
	 */
	public		JsonResult		XcloudToMysql(String	sqlData,String TenantId,
			String MysqlTableName,String MysqlCols){
		JsonResult		JsonResultIns = new JsonResult();
		Date					dateBegin = new Date();
		Connection 		connectionXcloud = null;
		Connection 		connectionMysql = null;
		Statement 		statementXcloud =null;
		Statement 		stmMysqlCols =null;   // --- 用于纪录MYSQL字段属性 ---
		PreparedStatement 		statementMysql =null;
		StringBuilder			sb = new StringBuilder();
		try{
			// --- 连接行云 ------------------------------------------------------------------------
			Class.forName(getValueFromGlobal("DS.XCLOUD.DRIVER"));
			connectionXcloud = DriverManager.getConnection(getValueFromGlobal("DS.XCLOUD.URL") , 
					getValueFromGlobal("DS.XCLOUD.USER"), 
					getValueFromGlobal("DS.XCLOUD.PASSWORD"));
			// --- 连接MYSQL --------------------------------------------------------------------------
			Class.forName(getValueFromGlobal("DS.MYSQL.DRIVER"));
			connectionMysql = DriverManager.getConnection(getValueFromGlobal("DS.MYSQL.URL."+TenantId) , 
					getValueFromGlobal("DS.MYSQL.USER."+TenantId), 
					getValueFromGlobal("DS.MYSQL.PASSWORD."+TenantId));
			// --- 从行云执行SQL 语句 -----------------------------------
			statementXcloud = connectionXcloud.createStatement();
			ResultSet rsXcloud = statementXcloud.executeQuery(sqlData);
			// --- 生成MYSQL入库SQL语句 ---
			sb.append("INSERT INTO ");
			sb.append(MysqlTableName);
			sb.append(" (");
			sb.append(MysqlCols);
			sb.append(") ");
			sb.append("VALUE( ");
			String		cols[]  = MysqlCols.split(",");
			for(int i = 0;i < cols.length;++i){
				if(i > 0)  sb.append(",");
				sb.append("?");
			}
			sb.append(")");
			String			strMysqlInsert = sb.toString();
			connectionMysql.setAutoCommit(false);
			// --- 得到MYSQL的字段属性 -------------------------------------------
			sb.setLength(0);
			sb.append("SELECT ");
			sb.append(MysqlCols);
			sb.append("  FROM ");
			sb.append(MysqlTableName);
			sb.append("  WHERE 1=0 ");
			log.info("sql cols ={}",sb.toString());
			stmMysqlCols = connectionMysql.createStatement();
			ResultSet		rsMysqlCols = stmMysqlCols.executeQuery(sb.toString());
			ResultSetMetaData	rsmdMysqlCols = rsMysqlCols.getMetaData();
			while (rsMysqlCols.next());
			stmMysqlCols.close();
			stmMysqlCols = null;
			connectionMysql.commit();
			// --- 结束MYSQL 字段属性获取 -----------------------------------------------		
			log.info("mysql insert sql ={}",strMysqlInsert);
			statementMysql = connectionMysql.prepareStatement(strMysqlInsert,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSetMetaData   rsmdXcloud= rsXcloud.getMetaData();
			
			// ---  开始处理 ---
			log.info("开始提取数据");
			int  		recs = 0;
			int		iTotal = 0;
			while (rsXcloud.next()) {
				++recs;
				++iTotal;
				//log.info("recs={}",recs);
				String  strResult = dbRowExchange(rsmdMysqlCols,rsmdXcloud,rsXcloud,statementMysql);
				if("000000".equalsIgnoreCase(strResult) == false){  // --- 出错 ---
					JsonResultIns.setCode("000001");
					JsonResultIns.setMessage(strResult);
					return JsonResultIns;
				}
				//statementMysql.exec
				statementMysql.addBatch();
				if(recs == 1000){
					log.info("入库,总纪录数:{}",iTotal);
					// --- 入库 ---
					statementMysql.executeBatch();
					connectionMysql.commit();
					//statementMysql.close();
					//statementMysql = null;
					//statementMysql = connectionMysql.prepareStatement(strMysqlInsert);
					recs = 0;
				}
            }
			if(recs > 0){
				statementMysql.executeBatch();
				connectionMysql.commit();
				log.info("入库,总纪录数:{}",iTotal);
			}
			// --- 从行去查询数据 ---
			JsonResultIns.setCode("000000");
			JsonResultIns.setMessage("sucess");
			log.info("--- 结束数据提取,耗时={}",new Date().getTime()-dateBegin.getTime());
		}catch(Exception e){
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(e.toString());
			e.printStackTrace();
		}finally
		{
			if(statementXcloud != null)
			{
				try{
					statementXcloud.close();
					connectionXcloud.close();
				}catch(Exception e){}
			}
			if(statementMysql != null)
			{
				try{
					statementMysql.close();
					connectionMysql.close();
				}catch(Exception e){}
			}
		}	
		
		return JsonResultIns;
	}
	/*
	 * 数据库行值的数据交换
	 */
	public		String			dbRowExchange(ResultSetMetaData rsmdInsert,ResultSetMetaData  rsmdSelect,
			ResultSet  rsSelect,PreparedStatement  psInsert){
		try{
			for(int i=1;i <= rsmdInsert.getColumnCount();++i){
				switch(rsmdInsert.getColumnType(i)){
					case  java.sql.Types.VARCHAR:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.VARCHAR:
									psInsert.setString(i,rsSelect.getString(i));
									break;
								case  java.sql.Types.CHAR:
									psInsert.setString(i,rsSelect.getString(i));
									break;
								case  java.sql.Types.INTEGER:
									psInsert.setString(i,String.valueOf(rsSelect.getInt(i)));
									break;
								case  java.sql.Types.BIGINT:
									psInsert.setString(i,String.valueOf(rsSelect.getLong(i)));
									break;
								case  java.sql.Types.FLOAT:
									psInsert.setString(i,String.valueOf(rsSelect.getFloat(i)));
									break;
								case  java.sql.Types.DATE:
									psInsert.setString(i,rsSelect.getDate(i).toString());
									break;
								case  java.sql.Types.DOUBLE:
									psInsert.setString(i,String.valueOf(rsSelect.getDouble(i)));
									break;
								default:
									return "can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.VARCHAR";
								//	break;
							}						
						}
						break;
					case  java.sql.Types.INTEGER:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.INTEGER:
									psInsert.setInt(i,rsSelect.getInt(i));
									break;
								case  java.sql.Types.VARCHAR:
									psInsert.setInt(i,Integer.parseInt(rsSelect.getString(i)));
									break;
								case  java.sql.Types.CHAR:
									psInsert.setInt(i,Integer.parseInt(rsSelect.getString(i)));
									break;
								case  java.sql.Types.BIGINT:
									psInsert.setString(i,String.valueOf(rsSelect.getLong(i)));
									break;
								default:
									return "can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.INTEGER";
							}	
						}
						break;
					case  java.sql.Types.DATE:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.DATE:
									psInsert.setDate(i, rsSelect.getDate(i));
									break;
								default:
									return "can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.DATE";
							}
						}
						break;
					case  java.sql.Types.BIGINT:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.BIGINT:
									psInsert.setLong(i, rsSelect.getLong(i));
									break;
								case  java.sql.Types.INTEGER:
									psInsert.setLong(i,rsSelect.getInt(i));
									break;
								case  java.sql.Types.VARCHAR:
									psInsert.setLong(i,Long.parseLong(rsSelect.getString(i)));
									break;
								case  java.sql.Types.CHAR:
									psInsert.setLong(i,Long.parseLong(rsSelect.getString(i)));
									break;
								default:
									return "can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.BIGINT";
							}
						}
						break;
					default:
						return " unsupported: "+ rsmdInsert.getColumnType(i) ;
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
			return e.toString();
		}
		return "000000";
	}
	/*
	 * 数据交换 
	 */
	/*
	 * 失效工单，和ORDERCENTER中的逻辑保持一致
	 */
	public	void		expireActivityHandle(String tenantId,int  activitySeqId,String activityId){
		StringBuilder			sb = new StringBuilder();
			String		strTenantId =tenantId;
			int			iActivitySeqId =activitySeqId;
		
			String		strMove = getValueFromGlobal("ACTIVITYSTATUS.MOVE");
			String		strDelete = getValueFromGlobal("ACTIVITYSTATUS.DELETE");
			String		strUpdate = getValueFromGlobal("ACTIVITYSTATUS.UPDATE");
			String		strTableName = getValueFromGlobal("ACTIVITYSTATUS.TABLE");
			String		strTmpMove = strMove.replaceFirst("TTTTTENANT_ID", strTenantId);
			String		strTmpDelete = strDelete.replaceFirst("TTTTTENANT_ID", strTenantId);
			String		strTmpUpdate = strUpdate.replaceFirst("TTTTTENANT_ID", strTenantId);
			String		strLastMove = strTmpMove.replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(iActivitySeqId));
			String		strLastDelete = strTmpDelete.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));
			String		strLastUpdate = strTmpUpdate.replaceFirst("AAAAACTIVITY_SEQ_ID",String.valueOf(iActivitySeqId));
			
			String		strTableItem[] = strTableName.split(",");
			
			for(String rec:strTableItem){
				String		strLocalUpdate = strLastUpdate.replaceAll("TTTTTABLENAME", rec);
				String		strLocalMove = strLastMove.replaceAll("TTTTTABLENAME", rec);
				String		strLocalDelete = strLastDelete.replaceAll("TTTTTABLENAME", rec);

				sb.setLength(0);
				sb.append("SELECT MAX(REC_ID) AS MAXID,MIN(REC_ID) AS MINID  FROM ");
				sb.append(rec);
				sb.append(" WHERE ACTIVITY_SEQ_ID = ");
				sb.append(iActivitySeqId);
				sb.append("  AND TENANT_ID ='");
				sb.append(tenantId);
				sb.append("'");
				Map<String,Object>  mapResult = jdbcTemplate.queryForMap(sb.toString());
				if(mapResult == null ) continue;
				long			lMinRec = 0;
				long			lMaxRec =0;
				try{
					lMinRec = (Long)mapResult.get("MINID");
					lMaxRec = (Long)mapResult.get("MAXID");
				}catch(Exception e){  // --- 捕获空指针 ---
					continue;
				}
				log.info("sql={},table ={},min rec ={},max rec={},={}",sb.toString(),rec,lMinRec,lMaxRec,mapResult.toString());
				long			lBeginRec = 0L;
				long			lEndRec = 0L;
				long			lRound = (lMaxRec-lMinRec)/10000L;
				for(long i=0;i <= lRound ;++i){
					lBeginRec = lMinRec + 10000L*i;
					if(i == lRound){
						lEndRec = lMaxRec;
					}
					else{
						lEndRec = lBeginRec + 10000L-1L;
					}
					log.info("round = {}",i);
					// --- 开始执行 ---
					sb.setLength(0);
					sb.append(strLocalUpdate);
					sb.append(" AND REC_ID >= ");
					sb.append(lBeginRec);
					sb.append(" AND REC_ID <= ");
					sb.append(lEndRec);
					log.info("更新工单sql = {}",sb.toString());
					int result = jdbcTemplate.update(sb.toString());
					log.info("更新工单时间:{} ,工单序列号:{},数量:{}",rec,iActivitySeqId,result);		
					sb.setLength(0);
					sb.append(strLocalMove);
					sb.append(" AND REC_ID >= ");
					sb.append(lBeginRec);
					sb.append(" AND REC_ID <= ");
					sb.append(lEndRec);
					log.info("工单移入历史sql = {}",sb.toString());
					result = jdbcTemplate.update(sb.toString());
					log.info("工单移入历史:{} ,工单序列号:{},数量:{}",rec,iActivitySeqId,result);		
					sb.setLength(0);
					sb.append(strLocalDelete);
					sb.append(" AND REC_ID >= ");
					sb.append(lBeginRec);
					sb.append(" AND REC_ID <= ");
					sb.append(lEndRec);
					log.info("删除工单sql = {}",sb.toString());
					result = jdbcTemplate.update(sb.toString());
					log.info("删除工单:{} ,工单序列号:{},数量:{}",rec,iActivitySeqId,result);		
				}
			}
			// --- 调用统计 ---
//			HashMap<String,String>   mapActivity = new HashMap<String,String>();
//			mapActivity.put("activityId",activityId);
//			mapActivity.put("tenantId", strTenantId);
			//StatisticServiceIns.invalidActivity(mapActivity);
			log.info("活动批次失效结束");
		
		}

	public boolean judgeFileExist(String localPath, String localFileName) {

		File file = new File(localPath);

		File[] files = file.listFiles();

		for (File fileNames : files) {
			String fileName = fileNames.getName();
			if (fileName.equals(localFileName)) {
				return true;
			}
		}
		return false;
	}

}
