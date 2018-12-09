package com.bonc.channelapi.hbase.client;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * Hbase数据转换为JSON
 *
 * @author caiqiang
 * @version 2016年8月4日
 * @see HbaseConvertUtil
 * @since
 */
public class HbaseConvertUtil {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(HbaseConvertUtil.class);

    /**
     * Description: 分割字符串
     * 
     * @param strValue
     * @return String[]
     */
    public static String[] getListValue(String strValue) {
        if (StringUtils.isEmpty(strValue)) {
            return null;
        }
        String[] fieldsString = null;
        if (strValue.contains(",")) {
            fieldsString = strValue.split(",");
        }
        if (strValue.contains("|")) {
            fieldsString = strValue.split("\\|");
        }
        return fieldsString;
    }

    /**
     * Description:指定result 转化为JSONObj
     * 
     * @param columnMap
     * @param result
     * @return JSONObject
     * @see
     */
    public static JSONObject result2JSONObj(LinkedHashMap<String, Integer> columnMap, Result result) {
        return result2JSONObj(columnMap, result, null, null);
    }

    /**
     * Description: 指定resultScanner 转化为JSONArray
     * 
     * @param columnMap
     * @param resultScanner
     * @return JSONArray
     * @throws Exception
     * @see
     */
    public static JSONArray resultScan2JSONArray(LinkedHashMap<String, Integer> columnMap,
                                                 ResultScanner resultScanner)
        throws Exception {
        return resultScan2JSONArray(columnMap, resultScanner, null, null);
    }

    /**
     * Description: 将指定列族/列查询结果resultScanner转化为JSONArray
     * 
     * @param columnMap
     * @param resultScanner
     * @param family
     * @param qualifier
     * @return JSONArray
     * @throws NumberFormatException
     * @throws TableNotFoundException
     * @throws Exception
     * @see
     */
    public static JSONArray resultScan2JSONArray(Map<String, Integer> columnMap,
                                                 ResultScanner resultScanner, String family,
                                                 String qualifier) {

        if (columnMap == null || resultScanner == null) {
            return new JSONArray();
        }
        JSONArray jsonArray = new JSONArray();

        try {

            for (Result result : resultScanner) {
                JSONObject jsonObj = result2JSONObj(columnMap, result, family, qualifier);
                if (jsonObj != null) {
                    jsonArray.add(jsonObj);
                }
            }
        }
        catch (RuntimeException e) {
            LOG.error("报错信息：" + e.getMessage());
            return jsonArray;
        }
        return jsonArray;

    }

    /**
     * Description: 将指定列族/列查询结果Result转化为JSONObj
     * 
     * @param columnMap
     * @param result
     * @param family
     * @param qualifier
     * @return JSONObject
     * @see
     */
    public static JSONObject result2JSONObj(Map<String, Integer> columnMap, Result result,
                                            String family, String qualifier) {
        if (columnMap == null || result == null || result.isEmpty()) {
            return null;
        }
        String[] strValueArray = null;
        if (StringUtils.isEmpty(family) || StringUtils.isEmpty(qualifier)) {

            strValueArray = getListValue(Bytes.toString(CellUtil.cloneValue(result.rawCells()[0])));
        }
        strValueArray = getListValue(Bytes.toString(result.getValue(Bytes.toBytes(family),
            Bytes.toBytes(qualifier))));

        if (strValueArray == null || strValueArray.length <= 0) {
            LOG.info("strValueArray is null or strValueArray.length <= 0");
            return null;
        }
        JSONObject jsonObj = new JSONObject();
        for (Entry<String, Integer> entry : columnMap.entrySet()) {
            jsonObj.put(entry.getKey(), strValueArray[entry.getValue()]);
        }
        LOG.info(JSON.toJSONString(jsonObj));
        return jsonObj;

    }

}
