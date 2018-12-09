package com.bonc.channelapi.hbase.client;


import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 配置文件类
 * 
 * @author caiqiang
 * @version 2016年8月3日
 * @see HbaseConf
 */
@ConfigurationProperties(prefix = "habseconf")
public class HbaseConf {

    /**
     * 重试的休眠时间
     */
    private String clientPause;

    /**
     * 重试的次数
     */
    private String clientRetriesNumber;

    /**
     * 是否关闭Nagle
     */
    private String ipcClientTcpnodelay;

    /**
     * 网络超时时间
     */
    private String ipcPingInterval;

    /**
     * rpc的超时时间
     */
    private String rpcTimeout;

    /**
     * 设置zookeeper IP 
     */
    private String zookeeperQuorum;
    /**
     * 设置zookeeper  port
     */
    private String zkClientPort;

    /**
     * hbase根目录
     */
    private String zookeeperZnodeParent;

    /**
     * zookeeper建立连接超时时间
     */
    private String zookeeperSessionTimeout;

    /**
     * meta表 扫描缓存
     */
    private String metaScannerCaching;

    /**
     * 控制预取的数目
     */
    private String clientPrefetchLimit;

    public String getClientPause() {
        return clientPause;
    }

    public void setClientPause(String clientPause) {
        this.clientPause = clientPause;
    }

    public String getClientRetriesNumber() {
        return clientRetriesNumber;
    }

    public void setClientRetriesNumber(String clientRetriesNumber) {
        this.clientRetriesNumber = clientRetriesNumber;
    }

    public String getIpcClientTcpnodelay() {
        return ipcClientTcpnodelay;
    }

    public void setIpcClientTcpnodelay(String ipcClientTcpnodelay) {
        this.ipcClientTcpnodelay = ipcClientTcpnodelay;
    }

    public String getIpcPingInterval() {
        return ipcPingInterval;
    }

    public void setIpcPingInterval(String ipcPingInterval) {
        this.ipcPingInterval = ipcPingInterval;
    }

    public String getRpcTimeout() {
        return rpcTimeout;
    }

    public void setRpcTimeout(String rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public void setZookeeperQuorum(String zookeeperQuorum) {
        this.zookeeperQuorum = zookeeperQuorum;
    }
    
     public String getZkClientPort() {
        return zkClientPort;
    }
    
    public void setZkClientPort(String zkClientPort) {
        this.zkClientPort = zkClientPort;
    }

    public String getZookeeperZnodeParent() {
        return zookeeperZnodeParent;
    }

    public void setZookeeperZnodeParent(String zookeeperZnodeParent) {
        this.zookeeperZnodeParent = zookeeperZnodeParent;
    }

    public String getZookeeperSessionTimeout() {
        return zookeeperSessionTimeout;
    }

    public void setZookeeperSessionTimeout(String zookeeperSessionTimeout) {
        this.zookeeperSessionTimeout = zookeeperSessionTimeout;
    }

    public String getMetaScannerCaching() {
        return metaScannerCaching;
    }

    public void setMetaScannerCaching(String metaScannerCaching) {
        this.metaScannerCaching = metaScannerCaching;
    }

    public String getClientPrefetchLimit() {
        return clientPrefetchLimit;
    }

    public void setClientPrefetchLimit(String clientPrefetchLimit) {
        this.clientPrefetchLimit = clientPrefetchLimit;
    }

}
