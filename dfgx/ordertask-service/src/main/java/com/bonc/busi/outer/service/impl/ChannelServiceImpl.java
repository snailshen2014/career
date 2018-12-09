package com.bonc.busi.outer.service.impl;

import com.bonc.busi.outer.mapper.ChannelMapper;
import com.bonc.busi.outer.service.ChannelService;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.IContants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by MQZ on 2017/11/4.
 */

@Service("ChannelService")
public class ChannelServiceImpl implements ChannelService {
    private static final Logger log = LoggerFactory.getLogger(ChannelServiceImpl.class);

    @Autowired
    private ChannelMapper channelMapper;
    @Override
    public JsonResult createChannelOrderMapping(List<Map<String,String>> channelColumnList) {
        JsonResult msg = new JsonResult();
        StringBuilder sqlValues = new StringBuilder();
        StringBuilder sqlXCloudColumns = new StringBuilder();
        Set<String> newColumns = new HashSet<>();
        Set<String> resultSet = new HashSet<>();
        StringBuilder sqlDistinctChannelId = new StringBuilder();
        Set<String> channelSet = new HashSet<>();
        Set<String> tenantIdSet = new HashSet<>();
        /**  先查询是否有当前渠道 如果有则返回通知其调用修改接口 **/
        for (Map<String,String> channelInfo :channelColumnList){
            channelSet.add(channelInfo.get("channelId"));
            tenantIdSet.add(channelInfo.get("tenantId"));
        }
        if (channelSet.size()>1){
            msg.setCode(IContants.BUSI_ERROR_CODE);
            msg.setMessage("一次只能传入一个渠道！！！" );
            return msg;
        }
        if (tenantIdSet.size()>1){
            msg.setCode(IContants.BUSI_ERROR_CODE);
            msg.setMessage("一次只能传入一个租户！！！" );
            return msg;
        }
        String tenantId = tenantIdSet.iterator().next();
        String mycatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = '"+tenantId+"'  */";
        //工单表分配规则 先分配用户标签预留字段 如果不够分配其他预留字段
        HashMap<String, Queue<String>> orderTableQueueMap = getDistributeQueue(channelSet);
        sqlXCloudColumns.append("(");
        List<String> baseColumns = channelMapper.getBaseColumns(tenantId);
        for (Map<String,String> channelInfo :channelColumnList){
            //剔除基础数据重复行云列
            if (!baseColumns.contains(channelInfo.get("xCloudColumn"))){
                //拼接插入sql ('5','PB0021','BUSINESS_RESERVE1','desc'),('5','PB0021','BUSINESS_RESERVE1','desc')
                sqlValues.append("('").append(channelInfo.get("channelId")).append("','")
                        .append(channelInfo.get("xCloudColumn")).append("','")
                        .append(orderTableQueueMap.get(channelInfo.get("channelId")).poll()).append("','")
                        .append(channelInfo.get("tenantId")).append("','")
                        .append(channelInfo.get("columnDesc")).append("',1,NOW()),");
                newColumns.add(channelInfo.get("xCloudColumn"));
                sqlXCloudColumns.append("'").append(channelInfo.get("xCloudColumn")).append("',");
            }
        }
        // 拼接sql
        for (String channelId : channelSet){
            sqlDistinctChannelId.append("'").append(channelId).append("'").append(",");
        }
        List<String> channelNum = channelMapper.checkExistChannelId(sqlDistinctChannelId.substring(0,sqlDistinctChannelId.length()-1),tenantId);
        if (!channelNum.isEmpty()){
//            msg.setCode(IContants.BUSI_ERROR_CODE);
//            msg.setMessage("该渠道已经被初始化过 ,渠道id为:" + channelNum.toString());
            /** 后期修改如果该渠道被初始化过 重新刷新该渠道列表  **/
            /* 1将之前没有的先更改状态  **/
            channelMapper.updateNotInIsUsed(channelNum.get(0),sqlXCloudColumns.substring(0,sqlXCloudColumns.length()-1)+")",tenantId);
            /* 2.将非重复的排除在外  **/
            Set<String> oldColumn = channelMapper.getXCloudColumnByChannelId(channelNum.get(0),tenantId);
            resultSet.addAll(newColumns);
            resultSet.removeAll(oldColumn);
            for (Map<String,String> channelInfo :channelColumnList){
                for (String result:resultSet){
                    if (channelInfo.get("xCloudColumn").equals(result)){
                        if (!baseColumns.contains(channelInfo.get("xCloudColumn"))) {
                            Integer lastColumnIndex = channelMapper.getLastOrderColumnByChannelId(channelInfo);
                            String nextOrderColumn = getNextOrderColumn(lastColumnIndex);
                            StringBuilder sql = new StringBuilder();
                            sql.append("('").append(channelInfo.get("channelId")).append("','")
                                    .append(channelInfo.get("xCloudColumn")).append("','")
                                    .append(nextOrderColumn).append("','")
                                    .append(channelInfo.get("tenantId")).append("','")
                                    .append(channelInfo.get("columnDesc")).append("',1,NOW()),");
                            channelMapper.insertInfoToTable(sql.substring(0, sql.length() - 1), mycatSql);
                        }
                    }
                }
            }
        }else {
            channelMapper.insertInfoToTable(sqlValues.substring(0,sqlValues.length()-1),mycatSql);
        }
        msg.setCode(IContants.CODE_SUCCESS);
        msg.setMessage(IContants.MSG_SUCCESS);
        return msg;
    }

    private  HashMap<String, Queue<String>> getDistributeQueue( Set<String> channelSet){
        HashMap<String, Queue<String>> queueMap = new HashMap<>();
        for (String channelId : channelSet){
            Queue<String> queue = new ArrayDeque<>();
            for (int i = 1; i < 51; i++) {
                queue.add("USERLABEL_RESERVE"+i);
            }
            for (int i = 1; i < 51; i++) {
                queue.add("BUSINESS_RESERVE"+i);
            }
            queueMap.put(channelId,queue);
        }
        return queueMap;
    }

    @Override
    public JsonResult modifChannelOrderMapping(List<Map<String, String>> list) {
        JsonResult msg = new JsonResult();
        msg.setCode(IContants.CODE_SUCCESS);
        for ( Map<String,String> map : list){
            //如果传入渠道的xCloudColumn没有则增加，如果已有则更新，传值是空则删除
            Map<String,String> channelInfo =   channelMapper.checkXCloudColumn(map);
            if (null==channelInfo || channelInfo.isEmpty()){ //空则插入
                //查询当前最大占用表，取其下一个
                Integer lastColumnIndex = channelMapper.getLastOrderColumnByChannelId(map);
                String nextOrderColumn = getNextOrderColumn(lastColumnIndex);
                StringBuilder sql = new StringBuilder();
                sql.append("('").append(map.get("channelId")).append("','")
                        .append(map.get("xCloudColumnNew")).append("','")
                        .append(nextOrderColumn).append("','")
                        .append(map.get("columnDesc")).append("',1,NOW()),");
                channelMapper.insertInfoToTable(sql.substring(0, sql.length() - 1), sql.substring(0,sql.length()-1));
//                msg.setMessage("插入成功");
            }else {
                if (null == map.get("xCloudColumnNew") || "".equals(map.get("xCloudColumnNew"))){
                    channelMapper.updateIsUse(map);
//                    msg.setMessage("停用成功");
                }else {
                    channelMapper.updateXCloudColumn(map);
//                    msg.setMessage("更新成功");
                }
            }
        }
        msg.setMessage("更新成功");
        return msg;
    }

    private String getNextOrderColumn(Integer last){
        //init
        List<String> link = new LinkedList<>();
        for (int i = 1; i < 51; i++) {
            link.add("USERLABEL_RESERVE"+i);
        }
        for (int i = 1; i < 51; i++) {
            link.add("BUSINESS_RESERVE"+i);
        }
        return link.get(last);
    }


    public static void main(String[] args) {
        ChannelServiceImpl channelService = new ChannelServiceImpl();
//        String request = " [{\"channelId\":\"5\",\"xCloudColumn\":\"MB_ARPU\",\"columnDesc\":\"用户价值\"}," +
//                " {\"channelId\":\"5\",\"xCloudColumn\":\"MB_OWE_FEE\",\"columnDesc\":\"所属费用\"}," +
//                " {\"channelId\":\"7\",\"xCloudColumn\":\"PB0002\",\"columnDesc\":\"月使用流量\"}, " +
//                "{\"channelId\":\"5\",\"xCloudColumn\":\"PCC098\",\"columnDesc\":\"客戶每月使用費用\"}]";
//            List<Map<String,String>> channelColumnList = JSON.parseObject(request, List.class);
//            channelService.createChannelOrderMapping(channelColumnList);
//        channelService.createChannelOrderMapping(channelColumnList);
        System.out.println(channelService.getNextOrderColumn(2));
    }
}
