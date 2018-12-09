package com.bonc.utils;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPClient;    
import org.apache.commons.net.ftp.FTPReply;   

public class FTPUtil {
	
	private static FTPClient ftp;      
    /**  
     * 连接FTP服务器  
     * @param path 上传到ftp服务器哪个路径下     
     * @param addr 地址  
     * @param port 端口号  
     * @param username 用户名  
     * @param password 密码  
     * @return  
     * @throws Exception  
     */    
    public static Boolean connect(String addr,String port,String username,String password,String path) throws Exception {      
        Boolean result = false;      
        ftp = new FTPClient();      
        int reply;      
        ftp.connect(addr,Integer.parseInt(port));      
        ftp.login(username,password);      
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);      
        reply = ftp.getReplyCode();      
        if (!FTPReply.isPositiveCompletion(reply)) {      
            ftp.disconnect();      
            return result;      
        }      
        ftp.changeWorkingDirectory(path);      
        result = true;      
        return result;      
    }      
    /**  
     * 将文件上传至FTP服务器上  
     * @param file 上传的文件或文件夹  
     * @throws Exception  
     */    
    public static Boolean upload(File file) throws Exception{  
    	
    	Boolean result = false;
        if(file.isDirectory()){           
            ftp.makeDirectory(file.getName());                
            ftp.changeWorkingDirectory(file.getName());      
            String[] files = file.list();             
            for (int i = 0; i < files.length; i++) {      
                File file1 = new File(file.getPath()+"/"+files[i] );      
                if(file1.isDirectory()){      
                    upload(file1);      
                    ftp.changeToParentDirectory();      
                }else{                    
                    File file2 = new File(file.getPath()+"/"+files[i]);      
                    FileInputStream input = new FileInputStream(file2);      
                    ftp.storeFile(file2.getName(), input);      
                    input.close();                            
                }                 
            } 
            result = true;
        }else{      
            File file2 = new File(file.getPath());      
            FileInputStream input = new FileInputStream(file2);      
            ftp.storeFile(file2.getName(), input);      
            input.close(); 
            result = true;
        }   
        
        return result;
    }

}
