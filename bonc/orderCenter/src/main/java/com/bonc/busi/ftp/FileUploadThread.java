package com.bonc.busi.ftp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
import com.theta.jfsframe.core.base.StringUtils;
import com.theta.jfsframe.pub.intersys.entity.RemoteServerInfo;*/

public class FileUploadThread extends Thread{
	private static  Logger log = LoggerFactory.getLogger(FileUploadThread.class);
	
	private RemoteServerInfo serverInfo = null;
	private FTPClient ftpClient = null; //FTP 客户端代理
	 //FTP状态码 
    public  int i = 1; 
    
    public FileUploadThread(RemoteServerInfo srvInfo){
    	serverInfo = srvInfo;
    }
    
	@Override
    public void run() {
		log.debug("--- begin ---");
	//	if(StringUtils.isBlank(serverInfo.getUpload_path()))  return;   // --- 没有配置远程服务器的目录，则不用上传 ---
		try{
			File file = new File(serverInfo.getLocal_upload_path());
			 File fileList[] = file.listFiles(); 
			 if(fileList == null)  return;   // --- 无文件需要处理 ---
			 if(fileList.length == 0)  return;
			 // --- 联接服务器 ---
			 if(connectServer() == false){
				log.error("联接服务器:{}失败",serverInfo.getIp_address());
				return;
			}
			setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件
			ftpClient.enterLocalPassiveMode();    // --- 经测试，联接UBUNTU LINUX 这个方法可用 ---
			 for (File f : fileList) { 
				 if (f.isDirectory()) {            // 文件夹中还有文件夹
					 log.warn("{}中有目录",f.getName());
					 continue;
				 }
				 InputStream input = new FileInputStream(f);
				 ftpClient.changeWorkingDirectory(serverInfo.getUpload_path()); 
				 boolean flag = ftpClient.storeFile(f.getName(), input); 
                 if (flag) { 
                         log.debug("上传文件成功！"); 
                 } else { 
                         log.debug("上传文件失败！"); 
                         return;
                 } 
                 input.close(); 
                 // ---- 备份文件 ---
                 input = new FileInputStream(f);
                 //File fileOut = new File(serverInfo.getLocal_bak_path());
                 String strBakFileName = null;
                 if((serverInfo.getLocal_bak_path().lastIndexOf("/") + 1) != serverInfo.getLocal_bak_path().length()){			// --- 最后一个不是 / 分隔符
                	 strBakFileName = serverInfo.getLocal_bak_path() + "/" + f.getName();
         		}
         		else
         			strBakFileName = serverInfo.getLocal_bak_path() + f.getName();
                 File fileOut = new File(strBakFileName);
                OutputStream out = new FileOutputStream(fileOut);
                byte[] buffer = new byte[1024];  
                int byteread = 0; // 读取的字节数 
                while ((byteread = input.read(buffer)) != -1) {  
                    out.write(buffer, 0, byteread);  
                }  
                input.close();
                out.close();
                 // --- 删除文件 ---
                f.delete();
				 
			 }
		}catch(Exception e){
			log.error("上载文件:{}",e.getMessage());
			e.printStackTrace();
		}
		
		log.debug("--- end ---");
	}
	// -------------------------------------------------------------------------------------------------
	 /** 
     * 上传单个文件，并重命名 
     * 
     * @param localFile--本地文件路径 
     * @param distFolder--新的文件名,可以命名为空"" 
     * @return true 上传成功，false 上传失败 
     */ 
    public  boolean uploadFile(File localFile,  File localRootFile,  String distFolder) { // ---此函数不可用，需要改造 ---
            boolean flag = true; 
            try { 
                   // connectServer(); 
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE); 
                    ftpClient.enterLocalPassiveMode(); 
                    ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE); 
                    InputStream input = new FileInputStream(localFile); 
//                    if (input == null) { 
//                            System.out.println("本地文件"+localFile.getPath()+"不存在!"); 
//                    } 
//                    if (newFileName.trim().equals("")) { 
//                            newFileName = localFile.getName(); 
//                    } 

                    String furi1 = localFile.getParentFile().getAbsoluteFile().toURI().toString(); 
                    String furi2 = localRootFile.getParentFile().getAbsoluteFile().toURI().toString(); 

                    String objFolder = distFolder + File.separator + furi1.substring(furi2.length()); 

                    ftpClient.changeWorkingDirectory("/"); 
                    ftpClient.makeDirectory(objFolder); 
                   log.debug("a>>>>>>> : {}" ,distFolder + File.separator + localFile.getParent()); 
                    log.debug("x>>>>>>> : {}" ,objFolder); 
                    ftpClient.changeWorkingDirectory(objFolder); 

                    log.debug("b>>>>>>> : {}" , localFile.getPath() + " " + ftpClient.printWorkingDirectory()); 
                    flag = ftpClient.storeFile(localFile.getName(), input); 
                    if (flag) { 
                            System.out.println("上传文件成功！"); 
                    } else { 
                            System.out.println("上传文件失败！"); 
                    } 
                    input.close(); 
            } catch (IOException e) { 
                    e.printStackTrace(); 
                    log.debug("本地文件上传失败！"); 
            } catch (Exception e) { 
                    e.printStackTrace(); 
            } 
            return flag; 
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
