package com.bonc.busi.orderschedule.dataintergrity;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MQZ on 2017/7/6.
 */
@Component
public class FormatColumnSize {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 自动适配格式化过长的列
     * 再插入活动表和活动细节表时调用
     * @param tableName 表名
     * @param ObjStr 需要转换的对象字符串
     */
    public String format(String tableName, String ObjStr ,String tenantId ){
        Map<String, String> map = JSON.parseObject(ObjStr, Map.class);
        Map<String, String> resultMap = new HashMap<>();
        String sql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID ='"+tenantId+"' */ " +
                " SELECT * FROM " + tableName  ;
//        ResultSet resultSet = jdbcTemplate.queryForObject(sql, ResultSet.class);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
//        SqlRowSetMetaData metaData = jdbcTemplate.queryForRowSet(sql).getMetaData();
        int columnCount = metaData.getColumnCount();
        //遍历数据库所有列
        for (int i = 1; i <= columnCount; i++) {
            //获取数据库列名
            String dbColumnName = metaData.getColumnName(i);
            //获取数据库列长度
            Integer dbColumnSize = metaData.getColumnDisplaySize(i);
            //遍历实体对象
            for (Map.Entry<String, String> entry: map.entrySet()) {
                String columnName = entry.getKey();
                String columnData = String.valueOf(entry.getValue());
                Integer columnSize = columnData.length();
                //如果数据库列名和实体对象中的列名相同，则判断长度，如果实体类中的长度大于数据库的长度则截取
                if (columnName.equals(dbColumnName) && dbColumnSize<columnSize){
                    columnData = columnData.substring(0, dbColumnSize);
                    //替换掉之前的value
                    map.put(columnName,columnData);
                }
            }

        }
        resultMap = map;
        return  JSON.toJSONStringWithDateFormat(resultMap,"yyyy-MM-dd HH:mm:ss");

    }

}
