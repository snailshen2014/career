package com.bonc.channelapi.hbase.client;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bonc.channelapi.hbase.util.ApplicationUtil;


/**
 * 读取Hbase配置，创建连接
 *
 * @author caiqiang
 * @version 2016年8月4日
 * @see HBaseClient
 * @since
 */
public class HBaseClient {

    /**
     * 日志对象
     */
//    private static final Logger LOG = LoggerFactory.getLogger(HBaseClient.class);

    /**
     * HBase配置
     */
    private static Configuration CONF = null;

    /**
     * HBase连接
     */
    private static HConnection CONNECTION = null;

    /**
     * @param config
     * @return Configuration
     * @see
     */
    public static synchronized Configuration getConfiguration() {

        HbaseConf hbaseConf = ApplicationUtil.getBean(HbaseConf.class);

        if (CONF == null) {
            CONF = HBaseConfiguration.create();
            if (!StringUtils.isEmpty(hbaseConf.getClientPause())) {
                CONF.set("hbase.client.pause", hbaseConf.getClientPause());
            }

            if (!StringUtils.isEmpty(hbaseConf.getClientRetriesNumber())) {
                CONF.set("hbase.client.retries.number", hbaseConf.getClientRetriesNumber());
            }

            if (!StringUtils.isEmpty(hbaseConf.getIpcClientTcpnodelay())) {
                CONF.set("hbase.ipc.client.tcpnodelay", hbaseConf.getIpcClientTcpnodelay());
            }

            if (!StringUtils.isEmpty(hbaseConf.getIpcPingInterval())) {
                CONF.set("hbase.ipc.client.tcpnodelay", hbaseConf.getIpcPingInterval());
            }

            if (!StringUtils.isEmpty(hbaseConf.getRpcTimeout())) {
                CONF.set("hbase.rpc.timeout", hbaseConf.getRpcTimeout());
            }

            if (!StringUtils.isEmpty(hbaseConf.getZookeeperQuorum())) {
                CONF.set("hbase.zookeeper.quorum", hbaseConf.getZookeeperQuorum());
            }
            if (!StringUtils.isEmpty(hbaseConf.getZkClientPort())) {
                CONF.set("hbase.zookeeper.property.clientPort", hbaseConf.getZkClientPort());
            }

            if (!StringUtils.isEmpty(hbaseConf.getZookeeperZnodeParent())) {
                CONF.set("zookeeper.znode.parent", hbaseConf.getZookeeperZnodeParent());
            }

            if (!StringUtils.isEmpty(hbaseConf.getZookeeperSessionTimeout())) {
                CONF.set("zookeeper.session.timeout", hbaseConf.getZookeeperSessionTimeout());
            }

            if (!StringUtils.isEmpty(hbaseConf.getMetaScannerCaching())) {
                CONF.set("hbase.meta.scanner.caching", hbaseConf.getMetaScannerCaching());
            }

            if (!StringUtils.isEmpty(hbaseConf.getClientPrefetchLimit())) {
                CONF.set("hbase.client.prefetch.limit", hbaseConf.getClientPrefetchLimit());
            }

        }
        return CONF;
    }

    /**
     * Description: 获取连接
     * 
     * @return HConnection
     * @throws IOException
     * @see
     */
    public static synchronized HConnection getHConnection()
        throws IOException, Exception {
        // HConnectionPool connFactory = new HConnectionPool();
        // GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // poolConfig.setMinIdle(2);
        // poolConfig.setMaxTotal(2);
        // GenericObjectPool<HConnection> hConnectionPool = new GenericObjectPool<HConnection>(
        // connFactory, poolConfig);
        //
        // CONNECTION = hConnectionPool.borrowObject();
        // CONNECTION = HConnectionManager.createConnection(getConfiguration());
        if (CONNECTION == null) {
            CONNECTION = HConnectionManager.createConnection(getConfiguration());
        }
        return CONNECTION;
    }

}
