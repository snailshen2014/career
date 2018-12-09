package com.bonc.channelapi.hbase.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;


/**
 * @author caiqiang
 * @version 2016年7月28日
 * @see HbaseOperateClient
 * @since
 */
@Component(value = "hbaseOperateClient")
public class HbaseOperateClient {

    private static final Logger LOG = LoggerFactory.getLogger(HbaseOperateClient.class);

    /**
     * Description: 单行查询
     * 
     * @param rowkey
     * @param tableName
     * @return Result
     * @throws IOException
     * @see
     */
    public static Result getResult(byte[] rowkey, String tableName)
        throws IOException, Exception {

        Get get = new Get(rowkey);
        HTableInterface table = HBaseClient.getHConnection().getTable(tableName);
        Result result = table.get(get);

        table.close();
        return result;
    }

    public static void delete(String tableName, byte[] rowkey)
        throws IOException, Exception {

        @SuppressWarnings("resource")
        HBaseAdmin hBaseAdmin = new HBaseAdmin(HBaseClient.getConfiguration());
        HTableInterface table = HBaseClient.getHConnection().getTable(tableName);
        if (hBaseAdmin.tableExists(tableName)) {

            Delete del = new Delete(rowkey);
            table.delete(del);
        }
        else {
            LOG.info("######################  table not exits  #################");
        }
        table.close();
    }

    public static void deleteTable(String tableName)
        throws IOException, Exception {

        @SuppressWarnings("resource")
        HBaseAdmin hBaseAdmin = new HBaseAdmin(HBaseClient.getConfiguration());
        HTableInterface table = HBaseClient.getHConnection().getTable(tableName);
        if (hBaseAdmin.tableExists(tableName)) {
            hBaseAdmin.disableTable(tableName);
            hBaseAdmin.deleteTable(tableName);
        }
        else {
            LOG.info("######################  table not exits  #################");
        }
        table.close();
    }

    /**
     * Description:范围查询
     * 
     * @param startRowkey
     * @param endRowkey
     * @param tableName
     * @return ResultScanner
     * @throws IOException
     *             ResultScanner
     * @see
     */
    public static ResultScanner getResultScanner(byte[] startRowkey, byte[] endRowkey, String tableName)
        throws IOException, TableNotFoundException, Exception {

        Scan scan = new Scan(startRowkey, endRowkey);
        scan.setSmall(true);
        HTableInterface table = HBaseClient.getHConnection().getTable(tableName);
        ResultScanner rs = table.getScanner(scan);

        table.close();
        return rs;
    }

    /**
     * Description:实现HBase 伪分页
     * 
     * @param resultScanner
     * @return Map<Integer,Result>
     * @see
     */
    public static List<Object> getPageResults(JSONArray returnArrayResult, int pageSize,
                                              int pageNumber) {

        int rowIndex = 0;
        List<Object> objects = new ArrayList<Object>();

        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = startIndex + pageSize;

        for (Object object : returnArrayResult) {

            if (startIndex <= rowIndex && rowIndex < endIndex) {
                objects.add(object);
            }
            rowIndex++ ;
        }
        return objects;
    }

    /**
     * Description:通过添加协处理器 返回范围内的数据总条数。 说明scan的范围是[start,end)
     * 
     * @param tableNameStr
     * @param start
     * @param end
     * @return
     * @throws Throwable
     *             Long
     * @see
     */
    public static Long count(String tableNameStr, byte[] start, byte[] end)
        throws Throwable {

        Configuration hbaseconfig = HBaseClient.getConfiguration();
        HTable hTable = new HTable(hbaseconfig, TableName.valueOf(tableNameStr));

        // 这里有一个ColumnInterperter类型的参数ci。即列解释器，用于解析列中的值。
        LongColumnInterpreter columnInterpreter = new LongColumnInterpreter();
        AggregationClient aggregationClient = new AggregationClient(hbaseconfig);
        Scan scan = new Scan();

        scan.setStartRow(start);
        scan.setStopRow(end);

        try {
            Long count = aggregationClient.rowCount(hTable, columnInterpreter, scan);
            return count;
        }
        catch (TableNotFoundException e) {
            e.printStackTrace();
            return 0L;
        }

    }

    /**
     * Description: 构建rowkey
     * 
     * @param args
     * @return byte[]
     * @see
     */
    public static byte[] buildRowkey(String hashStr, String... args) {
        byte[] rowkey = Bytes.add(Bytes.toBytes((short)hashStr.hashCode() & 0x7fff),
            Bytes.toBytes(hashStr));
        for (String str : args) {
            if (!StringUtils.isEmpty(str)) {

                rowkey = Bytes.add(rowkey, Bytes.toBytes(str));
            }
        }
        return rowkey;
    }

}
