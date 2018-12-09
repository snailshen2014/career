package com.bonc.busi.ftp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
import com.google.common.collect.Maps;
import com.theta.jfsframe.core.base.StringUtils;
import com.theta.jfsframe.interweb.base.InterwebGlobal;
import com.theta.jfsframe.pub.base.RmResult;
import com.theta.jfsframe.pub.intersys.entity.RemoteServerInfo;
import com.theta.jfsframe.interweb.dubbo.IntersrvClt;
import com.theta.jfsframe.pub.intersys.entity.FileDownloadLog;
import com.theta.jfsframe.pub.intersys.entity.InterfaceFileInfo;*/

public class FileDownLoadThread extends Thread{
	private static  Logger log = LoggerFactory.getLogger(FileDownLoadThread.class);
	
	private RemoteServerInfo serverInfo = null;
	private FTPClient ftpClient = null; //FTP 客户端代理
	 //FTP状态码 
    public  int i = 1; 

	public FileDownLoadThread(RemoteServerInfo srvInfo){
		serverInfo = srvInfo;
	}
	// ---------------------------------------------------------------------------------------
	@Override
    public void run() {
		log.debug("--- remote server :{} begin ---",serverInfo.getIp_address());
		if(connectServer() == false){
			log.error("联接服务器:{}失败",serverInfo.getIp_address());
			System.out.println("eeeeee");
			return;
		}
		setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件
		try{
			// --- 查找文件并下载 ---
			//ftpClient.enterLocalActiveMode();   //这句重要，不行换enterRemoteActiveMode 看看
			//ftpClient.enterRemotePassiveMode();
			ftpClient.enterLocalPassiveMode();    // --- 经测试，联接UBUNTU LINUX 这个方法可用 ---
			//ftpClient.enterRemoteActiveMode(host, port)
			ftpClient.changeWorkingDirectory(serverInfo.getFile_path());
			log.debug("remote path:{}",serverInfo.getFile_path());
			FTPFile[] files=null;
			log.debug("before lsit");
			files = ftpClient.listFiles();
			if(files != null){
				log.debug("file num={}",files.length);
			}
			else{
				log.debug("file is empty");
			}
			String[] names = ftpClient.listNames();

			if(names != null){
				log.debug("name num:{}",names.length);
			}
			else{
				log.debug("no file find");
			}
			log.debug("after list");
			String	operatorPath = null;
			String	strLocalFileName = null;
		//	Map<String, Object> mapPara = Maps.newHashMap();
			//RmResult RmResultIns = null;
			String			strFileType = null;
			for(String fileName:names){
				//System.out.println(fileName);
				// --- 查询此文件是否已经被下载过，如果是，则删除且不下载 ----
				/*
				mapPara.clear();
				mapPara.put("REMOTEFILENAME", fileName);
				RmResultIns = IntersrvClt.queryExistRemoteFile(mapPara);
				if(StringUtils.equals("000000", RmResultIns.getRmCode()) == false){  // --- 出现错误 ---
					log.error("查询下载文件是否存在出错:{}",RmResultIns.getRmInfo());
					continue;
				}
				else{
					Map  map = (Map)RmResultIns.getData();
					String		strExist = (String)map.get("EXISTFLAG");
					if(StringUtils.equals("TRUE",strExist)){   // --- 文件已经下载过了,删除该文件 ---
						// --- 暂时先不删除文件 ---
						//deleteFile(fileName);  
						continue;
					}
				}*/
				//get order file
				if (!fileName.equals(serverInfo.getOrder_file()))
					continue;
				System.out.println("download");
				/*
				// --- 找到本地的路径和 ----
				if(StringUtils.contains(fileName, "lygj")){    // --- 0元购机 ---
					if(StringUtils.contains(fileName, "xzdg")){  // --- 订购新增文件 ---
						operatorPath = InterwebGlobal.getConfig(serverInfo.getOperator_id()+".lygj_newsub.operator.path");
						strFileType = "5";
					}
					else if(StringUtils.contains(fileName, "dgqx")){  // --- 订购取消文件 ---
						operatorPath = InterwebGlobal.getConfig(serverInfo.getOperator_id()+".lygj_cancelsub.operator.path");
						strFileType = "6";
					}
					else if(StringUtils.contains(fileName, "yczt")){  // --- 用户状态文件 ---
						operatorPath = InterwebGlobal.getConfig(serverInfo.getOperator_id()+".lygj_userstate.operator.path");
						strFileType = "7";
					}
				}
				else if(StringUtils.contains(fileName, "gjzj")){    // --- 购机直降 ---
					if(StringUtils.contains(fileName, "xzdg")){  // --- 订购新增文件 ---
						operatorPath = InterwebGlobal.getConfig(serverInfo.getOperator_id()+".gjzj_newsub.operator.path");
						strFileType = "0";
					}
					else if(StringUtils.contains(fileName, "dgqx")){  // --- 订购取消文件 ---
						operatorPath = InterwebGlobal.getConfig(serverInfo.getOperator_id()+".gjzj_cancelsub.operator.path");
						strFileType = "1";
					}
					else if(StringUtils.contains(fileName, "yczt")){  // --- 用户状态文件 ---
						operatorPath = InterwebGlobal.getConfig(serverInfo.getOperator_id()+".gjzj_userstate.operator.path");
						strFileType = "2";
					}
					
					
				}
				if((operatorPath.lastIndexOf("/") + 1) != operatorPath.length()){			// --- 最后一个不是 / 分隔符
					strLocalFileName = operatorPath + "/" + fileName;
				}
				else
					strLocalFileName = operatorPath + fileName;*/
				
				// --- 开始下载 ---
				Thread.sleep(3000);    // --- 固定等3秒钟 ，避免文件上传中被下载-----------
				System.out.println(serverInfo.getLocal_store_path());
				strLocalFileName = serverInfo.getLocal_store_path();
				strLocalFileName += fileName;
				
				if(downloadFile(fileName,strLocalFileName) == false){
					log.error("下载文件:{}失败",fileName);
					System.out.println("download error");
					closeConnect();// 关闭连接
					return;
				}
				else{
					log.debug("下载文件:{} 成功",fileName);
					System.out.println("download ok");
					// --- 纪录下载日志 ---
					/*
					mapPara.clear();
					FileDownloadLog  FileDownloadLogNew = new FileDownloadLog();
					InterfaceFileInfo InterfaceFileInfoNew = new InterfaceFileInfo();
					FileDownloadLogNew.setDownload_time(new Date());
					FileDownloadLogNew.setRemote_file_name(fileName);
					FileDownloadLogNew.setLocal_file_time(strLocalFileName);
					FileDownloadLogNew.setRemote_server_id(serverInfo.getId());
					mapPara.put("FILEDOWNLOADLOG", FileDownloadLogNew);
					InterfaceFileInfoNew.setFile_name(strLocalFileName);
					InterfaceFileInfoNew.setOperator_id(serverInfo.getOperator_id());
					if(StringUtils.equals("0", strFileType)){
						InterfaceFileInfoNew.setFile_state("0");   // --- 购机直降订购新增已经稳定，直接处理 ---
					}
					else{
						InterfaceFileInfoNew.setFile_state("5");   // ---先改为5 ，等稳定后再改为0 ---
					}
					InterfaceFileInfoNew.setFile_type(strFileType);
					mapPara.put("INTERFACEFILEINFO", InterfaceFileInfoNew);
					RmResultIns = IntersrvClt.saveDownloadInfo(mapPara);
					if(StringUtils.equals("000000", RmResultIns.getRmCode()) == false){  // --- 出现错误 ---
						log.error("保存下载文件数据出错:{}",RmResultIns.getRmInfo());
						closeConnect();// 关闭连接
						return;
					}*/
					// --- 删除文件  ---
					//deleteFile(fileName);
				}
				
			}// --- for ----
		}catch(Exception e){
			e.printStackTrace();
			log.error("下载文件出错");
		}
		log.debug("--- remote server :{} end ---",serverInfo.getIp_address());
		closeConnect();// 关闭连接
	}
	// ----------------------------------------------------------------------------------------------
	/** 
     * 删除一个文件 
     */ 
    public  boolean deleteFile(String filename) { 
            boolean flag = true; 
            try { 
                    flag = ftpClient.deleteFile(filename); 
                    if (flag) { 
                            log.debug("删除文件成功！"); 
                    } else { 
                            log.warn("删除文件失败！"); 
                    } 
            } catch (IOException ioe) { 
                    ioe.printStackTrace(); 
            } 
            return flag; 
    } 
	// --------------------------------------------------------------------------------------------
	 /** 
     * 下载文件 
     * 
     * @param remoteFileName             --服务器上的文件名 
     * @param localFileName--本地文件名 
     * @return true 下载成功，false 下载失败 
     */ 
    public   boolean downloadFile(String remoteFileName, String localFileName) { 
    	System.out.println(remoteFileName + "|" +localFileName);
            boolean flag = true; 
            //connectServer(); 
            // 下载文件 
            BufferedOutputStream buffOut = null; 
            try { 
                    buffOut = new BufferedOutputStream(new FileOutputStream(localFileName)); 
                    flag = ftpClient.retrieveFile(remoteFileName, buffOut); 
            } catch (Exception e) { 
                    e.printStackTrace(); 
                    log.debug("本地文件下载失败！"); 
            } finally { 
                    try { 
                            if (buffOut != null) 
                                    buffOut.close(); 
                    } catch (Exception e) { 
                            e.printStackTrace(); 
                    } 
            } 
            return flag; 
    } 
	// --------------------------------------------------------------------------------------------
	/** 
     * 关闭连接 
     */ 
    public  void closeConnect() { 
            try { 
                    if (ftpClient != null) { 
                            ftpClient.logout(); 
                            ftpClient.disconnect(); 
                    } 
            } catch (Exception e) { 
                    e.printStackTrace(); 
            } 
    } 
    // ---------------------------------------------------------------------------------------------------
	/** 
     * 设置传输文件的类型[文本文件或者二进制文件] 
     * 
     * @param fileType--BINARY_FILE_TYPE、ASCII_FILE_TYPE 
     * 
     */ 
    private  void setFileType(int fileType) { 
            try { 
            	ftpClient.setFileType(fileType); 
            } catch (Exception e) { 
                    e.printStackTrace(); 
            } 
    } 
	
	 /** 
     * 设置FTP客服端的配置--一般可以不设置 
     * 
     * @return ftpConfig 
     */ 
    private  FTPClientConfig getFtpConfig() { 
            FTPClientConfig ftpConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX); 
            ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING); 
            return ftpConfig; 
    } 
	 /** 
     * 连接到服务器 
     * 
     * @return true 连接服务器成功，false 连接服务器失败 
     */ 
    private   boolean connectServer() { 
    	boolean flag = true; 
        if (ftpClient == null) { 
        	int reply; 
            try { 
            	ftpClient = new FTPClient(); 
            	ftpClient.setControlEncoding("GBK"); 
            //	if(serverInfo.getPort() > 0)
            //		ftpClient.setDefaultPort(serverInfo.getPort()); 
            	ftpClient.configure(getFtpConfig()); 
            	log.debug("ip:{}",serverInfo.getIp_address());
            	ftpClient.connect(serverInfo.getIp_address()); 
            	ftpClient.login(serverInfo.getAccount(), serverInfo.getPassword()); 
            	//System.out.print(ftpClient.getReplyString()); 
            	reply = ftpClient.getReplyCode(); 
            	ftpClient.setDataTimeout(120000); 

            	if (!FTPReply.isPositiveCompletion(reply)) { 
            		ftpClient.disconnect(); 
            		log.error("FTP server refused connection."); 
            		// logger.debug("FTP 服务拒绝连接！"); 
            		flag = false; 
            	} 
            	i++; 
            	log.debug("connect server end ");
            } catch (SocketException e) { 
                        flag = false; 
                        e.printStackTrace(); 
                        log.error("登录ftp服务器 {} 失败,连接超时！",serverInfo.getIp_address()); 
            } catch (IOException e) { 
                        flag = false; 
                        e.printStackTrace(); 
                        log.error("登录ftp服务器{} 失败，FTP服务器无法打开！",serverInfo.getIp_address()); 
            } 
        } 
        return flag; 
    }
    // --------------------------------------------------------------------------------------------------

}
