package com.bonc.busi.outer.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by MQZ on 2017/11/4.
 */

public interface ChannelMapper {

    @Select("SELECT CHANNEL_ID FROM PLT_ORDER_CHANNEL_COLUMN_MAPPING WHERE CHANNEL_ID IN (${sql}) AND TENANT_ID=#{tenantId} ")
    List<String> checkExistChannelId( @Param("sql") String distinctChannelIdSql,@Param("tenantId") String tenantId);

    @Insert("REPLACE INTO PLT_ORDER_CHANNEL_COLUMN_MAPPING(CHANNEL_ID,XCLOUD_COLUMN,ORDER_COLUMN,COLUMN_DESC,IS_USE,LAST_UPDATE_TIME) VALUES ${sql}")
    void replaceInfoToTable(@Param("sql") String valueSql);

    @Select("SELECT * FROM PLT_ORDER_CHANNEL_COLUMN_MAPPING WHERE CHANNEL_ID = #{channelId} AND XCLOUD_COLUMN = #{xCloudColumnOld}")
    Map<String,String> checkXCloudColumn(Map<String, String> map);

    @Select("SELECT COUNT(1) FROM PLT_ORDER_CHANNEL_COLUMN_MAPPING WHERE CHANNEL_ID = #{channelId} AND TENANT_ID = #{tenantId}")
    Integer getLastOrderColumnByChannelId(Map<String, String> map);

    @Update("UPDATE PLT_ORDER_CHANNEL_COLUMN_MAPPING SET IS_USE = 0 ,LAST_UPDATE_TIME = NOW() WHERE CHANNEL_ID = #{channelId} AND  XCLOUD_COLUMN = #{xCloudColumnOld}")
    void updateIsUse(Map<String, String> map);

    @Update("UPDATE PLT_ORDER_CHANNEL_COLUMN_MAPPING SET IS_USE = 1 ,LAST_UPDATE_TIME = NOW() ,XCLOUD_COLUMN = #{xCloudColumnNew} WHERE CHANNEL_ID = #{channelId} AND  XCLOUD_COLUMN = #{xCloudColumnOld}")
    void updateXCloudColumn(Map<String, String> map);

    @Update("UPDATE PLT_ORDER_CHANNEL_COLUMN_MAPPING SET IS_USE = 0 ,LAST_UPDATE_TIME = NOW() WHERE CHANNEL_ID = #{channelId} AND   XCLOUD_COLUMN NOT IN ${sql} AND TENANT_ID = #{tenantId}")
    void updateNotInIsUsed(@Param("channelId") String channelId, @Param("sql") String valueSql,@Param("tenantId")String tenantId);

    @Insert("${mycatSql}INSERT INTO PLT_ORDER_CHANNEL_COLUMN_MAPPING(CHANNEL_ID,XCLOUD_COLUMN,ORDER_COLUMN,TENANT_ID,COLUMN_DESC,IS_USE,LAST_UPDATE_TIME) VALUES ${sql}")
    void insertInfoToTable( @Param("sql") String valueSql ,@Param("mycatSql") String mycatSql);

    @Select("SELECT XCLOUD_COLUMN FROM PLT_ORDER_CHANNEL_COLUMN_MAPPING WHERE CHANNEL_ID = #{channelId} AND IS_USE = '1' AND TENANT_ID = #{tenantId} ")
    Set<String> getXCloudColumnByChannelId(@Param("channelId") String channelId,@Param("tenantId")String tenantId);

    @Select("SELECT SOURCE_TABLE_COLUMN AS baseColumn from PLT_ORDER_TABLE_COLUMN_MAP_INFO where TENANT_ID = #{tenantId} AND IN_USE ='1' AND SOURCE_TABLE_ALIAS ='a' AND ORDER_COLUMN NOT LIKE '%RESERVE%'")
    List<String> getBaseColumns(@Param("tenantId")String tenantId);
}
