package com.bonc.busi.task.base;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpTools {
	private final static Logger log= LoggerFactory.getLogger(FtpTools.class);
	/*
	 * 连接FTP服务器
	 */
	private	static FTPClient		connectFtpServer(String SrvIp,String User,String Password,int Port){
		FTPClient ftpClient = null; //FTP 客户端代理
	    int reply; 
	    try { 
	    	ftpClient = new FTPClient(); 
	        ftpClient.setControlEncoding("UTF-8"); 
	        //ftpClient.configure(getFtpConfig()); 
	        if(Port >= 0)
	        	ftpClient.connect(SrvIp, Port);
	        else
	        	ftpClient.connect(SrvIp); 
	        ftpClient.login(User,Password); 
	        reply = ftpClient.getReplyCode(); 
	        if (!FTPReply.isPositiveCompletion(reply)) { 
	            ftpClient.disconnect(); 
	            log.error("FTP server refused connection."); 
	            log.error("FTP 服务拒绝连接！"); 
	            return null;
	         } 
	        int defaultTimeoutSecond=30*60 * 1000;  // --- 设置超时 1800秒（30分钟）---
            ftpClient.setDefaultTimeout(defaultTimeoutSecond);  
            ftpClient.setConnectTimeout(defaultTimeoutSecond );  
            ftpClient.setDataTimeout(defaultTimeoutSecond);  
	        log.info(" --- connect server sucess ---");
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
	 * 退出FTP，退出FTP也可能抛异常，所以单独处理 
	 */
	private	static	void		disconnect(FTPClient ftpClient){
		try{
			ftpClient.logout(); 
    		ftpClient.disconnect(); 
		}catch(Exception e){
			
		}
		
	}
	/*
	 * 上传文件到FTP服务器
	 */
	public	static	int	upload(String SrvIp,String User,String Password,int Port,
			String RemoteFileName,String LocalFileName){
		FTPClient ftpClient = null; //FTP 客户端代理
		try{
			ftpClient = connectFtpServer(SrvIp,User,Password,Port);
			if(ftpClient == null){
				log.warn("连不上FTP服务器");
				return -1;
			}
			log.info("连上了FTP服务器");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件
              //ftpClient.enterLocalPassiveMode(); 
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE); 
			// --- 是否自动创建远程路径 ? ---
            // ---得到远程目录 ---
            String		strRemotePath = getPath(RemoteFileName);
            if(strRemotePath != null){
            	ftpClient.enterLocalPassiveMode(); 
            	if(ftpClient.changeWorkingDirectory(strRemotePath) == false){   // --- 工作路径不存在 ---
            		// --- 创建路径 ---
            		ftpClient.enterLocalPassiveMode(); 
            		if(ftpClient.makeDirectory(strRemotePath) == false){
            			log.warn("--- create path:{} failed",strRemotePath);
            			disconnect(ftpClient);
            			return -1;
            		}
            		ftpClient.enterLocalPassiveMode(); 
            		if(ftpClient.changeWorkingDirectory(strRemotePath) == false){ 
            			log.warn("--- enter working  path:{} failed",strRemotePath);
            			disconnect(ftpClient);
            			return -1;
            		}
            	}
            }
			InputStream input = new FileInputStream(LocalFileName);
			ftpClient.enterLocalPassiveMode(); 
			boolean flag = ftpClient.storeFile(RemoteFileName, input); 
			if(flag){
				log.info("upload file :{} sucess ",RemoteFileName);
			}else{
				log.info("upload file :{} failed ",RemoteFileName);
				return -1;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			return -1;
		}
		return 0;
	}
	/*
	 *  下载行云生成的文件，先检查文件是否存在，如果文件不存在，检查临时文件是否存在
	 */
	public	static String		downloadXcloudFile(String SrvIp,String User,String Password,int Port,
			String RemoteFileName,String LocalFileName,boolean  deleteFileFlag){
		// --- 联接远程FTP ---
		FTPClient ftpClient = null; //FTP 客户端代理
		try{
			ftpClient = connectFtpServer(SrvIp,User,Password,Port);
			if(ftpClient == null){
				log.warn("连不上FTP服务器");
				return "can't connect to FTP server";
			}
			log.info("连上了FTP服务器");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件
			// --- 判断本地路径是否存在，不存在则创建 ---
			String		LocalPath = getPath(LocalFileName);
			if(LocalPath != null){
				File file = new File(LocalPath);  
				if( !file.exists() ){  
					log.info("path:"+LocalPath +" not exist !!!");  
					// --- 创建目录 ---
					if(file.mkdirs()  == false) {
						log.error("创建路径: {} 失败!!! " ,LocalPath);
						disconnect(ftpClient);
						return "create local path failed";
				 }
				}
				else{
						log.info("path:"+LocalPath +" already exist ");  
				}
			}// --- 本地路径处理 ---
			// --- 远程路径处理 ---
			// --- 得到远程的路径 ---
			String		RemotePath = getPath(RemoteFileName);
			log.info("远程路径:{}",RemotePath);
			if(RemotePath != null){
				ftpClient.enterLocalPassiveMode();
				if(ftpClient.changeWorkingDirectory(RemotePath) == false){
					log.error("进入工作目录:{}失败",RemotePath);
					disconnect(ftpClient);
					return "enter into ftp working path : "+RemotePath +" failed!";
				}
			}
			// --- 查找文件是否存在 ---
			ftpClient.enterLocalPassiveMode();
			String[] names = ftpClient.listNames();
			if(names == null){
				disconnect(ftpClient);
				log.warn("无文件存在");
				return "no file exist";
			}
			String		strRemoteFile= getFile(RemoteFileName);
			String		strRemoteTempFile= strRemoteFile+".xcloud.temp";
			log.info("远程文件名{},远程临时文件名:{}",strRemoteFile,strRemoteTempFile);
			boolean			bFile = false;
			boolean			bTempFile = false;
			for(String fileName:names){
				log.info("目录下的文件名:{}",fileName);
				if(fileName.equalsIgnoreCase(strRemoteFile)){  // --- 找到了文件 ---
					bFile  = true;
					break;
				}
				else  if(fileName.equalsIgnoreCase(strRemoteTempFile)){  // --- 找到了临时文件 ---
					bTempFile = true;
				}
			}
			if(bFile == false && bTempFile == false ){
				disconnect(ftpClient);
				log.warn("没有找到文件或临时文件");
				return "can't find file or temp file ";
			}
			int			iSleep = 0;
			while(bFile == false){
				++iSleep;
				ftpClient.enterLocalPassiveMode();
				String[] filenames = ftpClient.listNames();
				for(String fileName:filenames){
					log.info("current path filename:{}",fileName);
					if(fileName.equalsIgnoreCase(strRemoteFile)){  // --- 找到了文件 ---
						bFile  = true;
						break;
					}
				}
				if(iSleep > 120){           // --- 超过了二分钟 ---
					break;
				}
				// --- 没有找到文件则等待 ---
				try{
					Thread.sleep(1000);
				}catch(Exception e){}
			}
			if(bFile == false){
				disconnect(ftpClient);
				log.warn("有临时文件但找不到正式文件");
				return "file can't finded but temp file exist ";
			}
			// --- 提取文件 ---
			log.info("开始提取文件");
			// --- 提取文件 ---
			 BufferedOutputStream buffOut = null; 
			 buffOut = new BufferedOutputStream(new FileOutputStream(LocalFileName)); 
			 ftpClient.enterLocalPassiveMode();
             if(ftpClient.retrieveFile(RemoteFileName, buffOut) == false){
            	 log.error("提取文件："+RemoteFileName +"失败!!");
            	 disconnect(ftpClient);
            	 return "提取文件失败";
             }
             else{
            	 log.info(" --- sucess download remote file :{},local file : {} ",RemoteFileName,LocalFileName);
             }
			// --- 退出 ---
            buffOut.flush();
            buffOut.close();
            // --- 判断是否删除远程文件  ---
            if(deleteFileFlag){
            	// --- 删除失败也不管了 ，只要传输成功就行 -----------------
            	deleteRemoteFile(SrvIp,User,Password,Port,RemoteFileName);
            }
            disconnect(ftpClient);
			log.info("从FTP获取文件:"+LocalFileName + "结束");		
		}
		catch(Exception e){
				e.printStackTrace();
				return e.toString();
		}
		return "000000";
	}
	/*
	 * 从FTP服务器得到数据，远程路径需要提前准备好，本地路径如不存在会自动创建
	 */
	public	static int	download(String SrvIp,String User,String Password,int Port,
			String RemoteFileName,String LocalFileName,boolean  deleteFileFlag){
		// --- 联接远程FTP ---
		FTPClient ftpClient = null; //FTP 客户端代理
		try{
			ftpClient = connectFtpServer(SrvIp,User,Password,Port);
			if(ftpClient == null){
				log.warn("连不上FTP服务器");
				return -1;
			}
			log.info("连上了FTP服务器");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件
			//ftpClient.enterLocalActiveMode();   //这句重要，不行换enterRemoteActiveMode 看看
			//ftpClient.enterRemotePassiveMode();
			//ftpClient.enterLocalPassiveMode();    // --- 经测试，联接UBUNTU LINUX 这个方法可用 ---
			//ftpClient.enterRemoteActiveMode(host, port)
			//ftpClient.enterRemoteActiveMode(InetAddress.getByName(SrvIp),21);   

			// --- 判断本地路径是否存在，不存在则创建 ---
			String		LocalPath = getPath(LocalFileName);
			if(LocalPath != null){
				File file = new File(LocalPath);  
				if( !file.exists() ){  
			       log.info("path:"+LocalPath +" not exist !!!");  
			       // --- 创建目录 ---
			       if(file.mkdirs()  == false) {
			           log.error("创建路径: {} 失败!!! " ,LocalPath);
			           disconnect(ftpClient);
			            return -1;
			       }
				}
				else{
					log.info("path:"+LocalPath +" already exist ");  
				}
			}
			log.info("开始提取文件");
			// --- 提取文件 ---
			 BufferedOutputStream buffOut = null; 
			 buffOut = new BufferedOutputStream(new FileOutputStream(LocalFileName)); 
			 ftpClient.enterLocalPassiveMode();
             if(ftpClient.retrieveFile(RemoteFileName, buffOut) == false){
            	 log.error("提取文件："+RemoteFileName +"失败!!");
            	 disconnect(ftpClient);
            	 return -1;
             }
             else{
            	 log.info(" --- sucess download remote file :{},local file : {} ",RemoteFileName,LocalFileName);
             }
			// --- 退出 ---
            buffOut.flush();
            buffOut.close();
            // --- 判断是否删除远程文件  ---
            if(deleteFileFlag){
            	// --- 删除失败也不管了 ，只要传输成功就行 -----------------
            	deleteRemoteFile(SrvIp,User,Password,Port,RemoteFileName);
            }
            disconnect(ftpClient);
			log.info("从FTP获取文件:"+LocalFileName + "结束");		
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			return -1;

		}
		return 0;
	}
	/*
	 * 删除远程服务器上的文件
	 */
	public	static	int	deleteRemoteFile(String SrvIp,String User,String Password,int Port,
			String	strFileName){
		FTPClient ftpClient = null; //FTP 客户端代理
		try{
			ftpClient = connectFtpServer(SrvIp,User,Password,Port);
			if(ftpClient == null){
				log.warn("deleteRemoteFile can't   connect FTP SERVER !!!");
				return -1;
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.deleteFile(strFileName);
			
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		return 0;	
	}
	/*
	 * 查询本地某目录下的所有文件
	 */
	public	static	void		listLocalFile(String localPath){
		File file = new File(localPath);  
		File[]  filetmp= file.listFiles();
		for(File item:filetmp){
			log.info(" filename:"+item.getName() + " length:"+item.length());
		}
	}
	/*
	 * 提取目录
	 */
	public	static	String		getPath(String strFileAll){
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
	public	static String	getFile(String strFileAll){
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
	 * 替换SQL中的特定字段
	 */
	public static 	String		getChangeSql(String sqlData,Map<String,Object> mapData){
		StringBuilder		sb = new StringBuilder();
		StringBuilder		sbTmp = new StringBuilder();
		sb.setLength(0);
		int		i = 0;
		int		iWhere = sqlData.indexOf("WHERE");
		boolean		bEqualCode = false;
		for(i = 0;i < iWhere ;++i){
			if(bEqualCode){    
				boolean		bKongGe = true;
				boolean		bDyh = false;
				sbTmp.setLength(0);
				while(1 > 0){
					if(sqlData.charAt(i) == ' '){   // --- 空格 ---
						if(bKongGe) {
							++i;
							continue;
						}
						else{   // --- 该退出了  ---
							break;
						}
					}
					else if(sqlData.charAt(i) == ','){  // --- 该退出了  ---
						break;
					}
					else if(sqlData.charAt(i) == '\''){  
						if(bDyh == false){
							sb.append(sqlData.charAt(i));
							bDyh = true;
							++i;
							continue;
						}
						else{
							break;
						}			
					}
					else{
						if(bKongGe )   bKongGe = false;  // --- 不再是第一次空格了 ---
						sbTmp.append(sqlData.charAt(i) );
						++i;
					}
				}
				// --- 替换  ---
				sb.append(mapData.get(sbTmp.toString()));
				// --- 如果是逗号还得带进来 ---
				sb.append(sqlData.charAt(i));
				bEqualCode = false;
				continue;
			}
			if(sqlData.charAt(i) == '='){  // --- 发现了= 号
				bEqualCode = true;
			}
			sb.append(sqlData.charAt(i));
		}
		sb.append(sqlData.substring(iWhere));
		return sb.toString();
	}
	/*
	 * 
	 */



}
