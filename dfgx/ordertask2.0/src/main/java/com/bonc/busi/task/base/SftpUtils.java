package com.bonc.busi.task.base;

import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.jcraft.jsch.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Java Secure Channel
 *
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:19:19
 */
public class SftpUtils {
    private Logger logger = LoggerFactory.getLogger(SftpUtils.class);
    private Session session = null;
    private ChannelSftp channel = null;
    private final static Logger log = LoggerFactory.getLogger(SftpUtils.class);

    /*
     *  下载行云生成的文件，先检查文件是否存在，如果文件不存在，检查临时文件是否存在
     */
    public String downloadXcloudFile(String SrvIp, String User, String Password, int Port,
                                     String RemoteFileName, String LocalFileName, boolean deleteFileFlag) {
        // --- 联接远程FTP ---
        SftpUtils sftpClient = null; //FTP 客户端代理
        try {
            sftpClient = connect(SrvIp, Port, User, Password);
            if (sftpClient == null) {
                log.warn("连不上SFTP服务器");
                return "can't connect to SFTP server";
            }
            log.info("连上了SFTP服务器");
            // --- 判断本地路径是否存在，不存在则创建 ---
            String LocalPath = FtpTools.getPath(LocalFileName);
            if (LocalPath != null) {
                File file = new File(LocalPath);
                if (!file.exists()) {
                    log.info("path:" + LocalPath + " not exist !!!");
                    // --- 创建目录 ---
                    if (file.mkdirs() == false) {
                        log.error("创建路径: {} 失败!!! ", LocalPath);
                        disconnect();
                        return "create local path failed";
                    }
                } else {
                    log.info("path:" + LocalPath + " already exist ");
                }
            }// --- 本地路径处理 ---
            // --- 远程路径处理 ---
            // --- 得到远程的路径 ---
            String RemotePath = FtpTools.getPath(RemoteFileName);
            log.info("远程路径:{}", RemotePath);

            // --- 查找文件是否存在 ---
            String[] names = sftpClient.list(RemotePath);
            if (names == null) {
                disconnect();
                log.warn("无文件存在");
                return "no file exist";
            }
            String strRemoteFile = FtpTools.getFile(RemoteFileName);
            String strRemoteTempFile = strRemoteFile + ".xcloud.temp";
            log.info("远程文件名{},远程临时文件名:{}", strRemoteFile, strRemoteTempFile);
            boolean bFile = false;
            boolean bTempFile = false;
            for (String fileName : names) {
//				log.info("目录下的文件名:{}",fileName);
                if (fileName.equalsIgnoreCase(strRemoteFile)) {  // --- 找到了文件 ---
                    bFile = true;
                    break;
                } else if (fileName.equalsIgnoreCase(strRemoteTempFile)) {  // --- 找到了临时文件 ---
                    bTempFile = true;
                }
            }
            if (bFile == false && bTempFile == false) {
                disconnect();
                log.warn("没有找到文件或临时文件");
                return "can't find file or temp file ";
            }
            int iSleep = 0;
            while (bFile == false) {
                ++iSleep;
                String[] filenames = sftpClient.listNames();
                for (String fileName : filenames) {
                    log.info("current path filename:{}", fileName);
                    if (fileName.equalsIgnoreCase(strRemoteFile)) {  // --- 找到了文件 ---
                        bFile = true;
                        break;
                    }
                }
                if (iSleep > 120) {           // --- 超过了二分钟 ---
                    break;
                }
                // --- 没有找到文件则等待 ---
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
            if (bFile == false) {
                disconnect();
                log.warn("有临时文件但找不到正式文件");
                return "file can't finded but temp file exist ";
            }
            // --- 提取文件 ---
            log.info("开始提取文件");
            // --- 提取文件 ---
            sftpClient.get(RemoteFileName,LocalFileName);
            log.info(" --- sucess download remote file :{},local file : {} ", RemoteFileName, LocalFileName);
            // --- 判断是否删除远程文件  ---
            if (deleteFileFlag) {
                // --- 删除失败也不管了 ，只要传输成功就行 -----------------
                deleteRemoteFile(RemoteFileName);
            }
            disconnect();
            log.info("从SFTP获取文件:" + LocalFileName + "结束");
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return "000000";
    }

    public SftpUtils connect(String ip, int port, String hostname, String password) {
        try {
            Properties config = new Properties();
            int timeout = 5000;
            int aliveMax = 5;
            JSch jsch = new JSch(); // 创建JSch对象
            session = jsch.getSession(hostname, ip, port); // 根据用户名，主机ip，端口获取一个Session对象
            if (password != null) {
                session.setPassword(password); // 设置密码
            }
            config.put("userauth.gssapi-with-mic", "no");
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config); // 为Session对象设置properties
            session.setTimeout(timeout); // 设置timeout时间
            session.setServerAliveCountMax(aliveMax);
            session.connect(); // 通过Session建立链接
            channel = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
            channel.connect(); // 建立SFTP通道的连接
            logger.info("SSH Channel connected.");
        } catch (JSchException e) {
            throw new BoncExpection(IContants.SFTP_ERROR_CODE, e.toString());
        }
        return this;
    }


    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
            logger.info("SSH Channel disconnected.");
        }
    }

    /**
     * 发送文件
     */
    public void put(String src, String dst) {
        try {
            channel.put(src, dst, new FileProgressMonitor());
        } catch (SftpException e) {
            throw new BoncExpection(IContants.SFTP_ERROR_CODE, e.toString());
        }
    }

    /**
     * 获取文件
     */
    public void get(String src, String dst) {
        try {
            channel.get(src, dst, new FileProgressMonitor());
        } catch (SftpException e) {
            e.printStackTrace();
            log.error("提取文件失败");
            throw new BoncExpection(IContants.SFTP_ERROR_CODE, e.toString());
        }
    }

    public String[] getFileList(String filedir) throws SftpException {
        return list(filedir);
    }

    /**
     * 切换目录
     *
     * @param pathName
     * @return
     */
    public boolean changeDir(String pathName) {
        if (pathName == null || pathName.trim().equals("")) {
            logger.debug("invalid pathName");
            return false;
        }
        try {
            channel.cd(pathName.replaceAll("\\\\", "/"));
            logger.debug("directory successfully changed,current dir=" + channel.pwd());
            return true;
        } catch (SftpException e) {
            logger.error("failed to change directory", e);
            return false;
        }
    }


    /**
     * 列出当前目录下的文件及文件夹
     *
     * @return String[]
     */
    @SuppressWarnings("unchecked")
    private String[] list(String filedir) {
        Vector<ChannelSftp.LsEntry> list = null;
        try {
            //ls方法会返回两个特殊的目录，当前目录(.)和父目录(..)
            list = channel.ls(filedir);
        } catch (SftpException e) {
            logger.error("can not list directory", e);
            return new String[0];
        }

        List<String> resultList = new ArrayList<String>();
        for (ChannelSftp.LsEntry entry : list) {
            resultList.add(entry.getFilename());
        }
        return resultList.toArray(new String[0]);
    }


    /**
     * 获取当前目录下的文件列表
     * @return
     */
    private String[] listNames(){
        Vector<ChannelSftp.LsEntry> list = null;
        try {
            list = channel.ls(channel.pwd());
        } catch (SftpException e) {
            e.printStackTrace();
            log.error("ssh sftp获取当前目录下文件列表失败！！！");
        }
        List<String> resultList = new ArrayList<String>();
        for (ChannelSftp.LsEntry entry : list) {
            resultList.add(entry.getFilename());
        }
        return resultList.toArray(new String[0]);
    }

    private void deleteRemoteFile(String remoteFileName) {
        try {
            channel.rm(remoteFileName);
        } catch (SftpException e) {
            e.printStackTrace();
            log.error("ssh sftp 远程删除失败");
        }
    }

    public static void main(String[] args) throws SftpException {
        SftpUtils sftpUtils = new SftpUtils();
        sftpUtils.connect("172.16.91.235", 24, "test", "test");
        String[] fileList = sftpUtils.getFileList("/");
        System.out.println(fileList);
    }


}
