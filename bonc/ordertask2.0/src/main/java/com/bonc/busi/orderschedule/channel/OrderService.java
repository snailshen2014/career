package com.bonc.busi.orderschedule.channel;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.SuccessProductPo;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.PltActivityExecuteLog;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.common.DateTimeFun;
import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.log.LogToDb;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.routing.OrderTableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bonc.utils.DateUtil.getDateTime;

/**
 * Created by MQZ on 2017/6/2.
 */
@Service("OrderService")
public class OrderService {
    private final static Logger logdb = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private ApiManage apiManage;
    @Autowired
    private OrderMapper ordermapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Value("${spring.mail.username}")
//    private String mailFrom;

    /**
     * 把临时表移入工单表
     */
    public void moveToOrderTable(String tenantId, String channelId, int activitySeqId) {
        int transcationNums = Integer.parseInt(SystemCommonConfigManager.getSysCommonCfgValue("TRANSCATION_NUMS"));
        int rows = ordermapper.countTempTable(tenantId, channelId);
        String orderTableName = OrderTableManager.getAssignedTable(ActivityJsonFactory.getActivityId(), tenantId, channelId, 0, rows, activitySeqId);
        for (int i = 0; i < rows / transcationNums + 1; ++i) {
            ///*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID'  */INSERT INTO 'TTTTTBALENAME SELECT * FROM PLT_ORDER_INFO_TEMP WHERE REC_ID => BBBBBEGINNUMS AND REC_ID < (BBBBBEGINNUMS+TTTTTRANSCTIONNUMS) AND CHANNELID =
            ///*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID'  */INSERT INTO  TTTTTBALENAME(CCCCCOLUMN) SELECT CCCCCOLUMN FROM PLT_ORDER_INFO_TEMP WHERE  CHANNEL_ID = CCCCCHANNEL_ID  limit NNNNNUM
            String copySQL = SystemCommonConfigManager.getSysCommonCfgValue("GENORDER.MYSQL.COPY");
            String orderColumn = SystemCommonConfigManager.getSysCommonCfgValue("GENORDER.MYSQL.COLUMN");
            // --- 替换租户编号 ---
            copySQL = copySQL.replaceFirst("TTTTTENANT_ID", tenantId);
            // --- 替换路由表名 ---
            copySQL = copySQL.replaceFirst("TTTTTBALENAME", orderTableName);
            // --- 替换开始nums ---
//            copySQL = copySQL.replaceAll("BBBBBEGINNUMS", i * transcationNums + "");
            // --- 替换事务数量 ---
//            copySQL = copySQL.replaceFirst("TTTTTRANSCTIONNUMS", transcationNums + "");
            // --- 替换列名 ---
            copySQL = copySQL.replaceAll("CCCCCOLUMN", orderColumn);
            // --- 替换渠道 ---
            copySQL = copySQL.replaceAll("CCCCCHANNEL_ID", channelId);
            // --- 替换limit数量 ---
            copySQL = copySQL.replaceAll("NNNNNUM", transcationNums + "");
            // --- 替换select中的ORDER_STATUS 值 使得写死5
            copySQL = copySQL.replaceAll("ORDER_STATUS" , "'5'");
            copySQL = copySQL.replaceFirst("'5'","ORDER_STATUS");
            logdb.info("sql:" + copySQL);
            jdbcTemplate.execute(copySQL);
            String deleteSQL = SystemCommonConfigManager.getSysCommonCfgValue("GENORDER.MYSQL.DELETE");
            // --- 替换租户编号 ---
            deleteSQL = deleteSQL.replaceFirst("TTTTTENANT_ID", tenantId);
            // --- 替换渠道 ---
            deleteSQL = deleteSQL.replaceAll("CCCCCHANNEL_ID", channelId);
            // --- 替换limit数量 ---
            deleteSQL = deleteSQL.replaceAll("NNNNNUM", transcationNums + "");
            jdbcTemplate.execute(deleteSQL);
            logdb.info("sql:" + deleteSQL);
//            ordermapper.moveToOrderTable(tableName, i * transcationNums, transcationNums, tenantId);
        }

        //弹窗渠道添加手机号索引
        String response = apiManage.popwinaddindex(tenantId, orderTableName, activitySeqId, ActivityJsonFactory.getActivityId() ,channelId);
        if (response == null || response.equals("") || response.contains("ERROR")) {
            logdb.info("-----------渠道調用手機索引出错!!!!!!!!!!!!!!!!------------");
        }
        PltActivityExecuteLog exeLog = new PltActivityExecuteLog();
        exeLog.setBUSI_CODE(1016);
        exeLog.setACTIVITY_SEQ_ID(activitySeqId);
        exeLog.setTENANT_ID(tenantId);
        waitExecuteLogStatus(exeLog);
        //电信 划配接口调用    调用条件  1 电信   2 渠道中只有虚拟渠道
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        if ("1".equals(type)){
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("tenantId",tenantId);
            params.put("activityId",ActivityJsonFactory.getActivityId());
            params.put("activitySeqId",activitySeqId);
            params.put("orderCount",rows);
            params.put("channelId",channelId);
            apiManage.callAllotOrder(params);
        }
    }


    public void commitSuccessInfo() {
        SuccessStandardPo success = ActivityJsonFactory.getActivityProvPo().getSuccessStandardPo();
        if (success != null) {
            String activity_seq_id = apiManage.getActivitySeqId().toString();
            success.setTenantId(ActivityJsonFactory.tenantId());
            success.setActivityId(ActivityJsonFactory.getActivityId());
            success.setActivity_seq_id(activity_seq_id);
            ordermapper.InsertSuccessStandardPo(success);
            List<SuccessProductPo> list = success.getSuccessProductList();
            if (list != null) {
                for (SuccessProductPo p : list) {
                    p.setActivityId(ActivityJsonFactory.getActivityId());
                    p.setTenantId(ActivityJsonFactory.tenantId());
                    p.setActivity_seq_id(activity_seq_id);
                    ordermapper.InsertProduct(p);
                }
            }
        }

    }

    /**
     * @param begin,        order valid datetime
     * @param end           , order invalid datetime
     * @param enableChannel
     */
    public void updateActivityStatus(String begin, String end, List<String> enableChannel) {
        PltActivityInfo ac_obj = new PltActivityInfo();
        ac_obj.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
        String tenantId = ActivityJsonFactory.tenantId();
        ac_obj.setTENANT_ID(tenantId);
        ac_obj.setLAST_ORDER_CREATE_TIME(getDateTime(DateTimeFun.getCurrentTime("yyyy-MM-dd HH:mm:ss")));
        //t order_begin_date,order_end_date
        if (ActivityJsonFactory.getStartDate() != null)
            ac_obj.setORDER_BEGIN_DATE(getDateTime(begin));
        if (ActivityJsonFactory.getEndDate() != null)
            ac_obj.setORDER_END_DATE(getDateTime(end));
        // get activity rec_id
        Integer ActRecId = apiManage.getActivitySeqId();
        ac_obj.setREC_ID(ActRecId);
        ordermapper.updateActivityStatus(ac_obj);
        //向PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE 插入数据
        for (String channelId : enableChannel) {
            ac_obj.setCHANNELID(channelId);
            ordermapper.insertChannelExecute(ac_obj);
        }
    }

    /**
     * 清空临时表
     */
    public void cleanTempOrderTable(String tenantId) {
        ///*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID'  */TRUNCATE TABLE PLT_ORDER_INFO_TEMP
        String truncateSQL = SystemCommonConfigManager.getSysCommonCfgValue("GENORDER.MYSQL.TRUNCATE");
        // --- 替换租户编号 ---
        truncateSQL = truncateSQL.replaceFirst("TTTTTENANT_ID", tenantId);
        logdb.info("sql:" + truncateSQL);
        jdbcTemplate.execute(truncateSQL);
    }


    /**
     * 等待状态码
     *
     * @param exeLog 执行日志对象
     * @throws InterruptedException
     */
    public void waitExecuteLogStatus(PltActivityExecuteLog exeLog) {
        long startTime = System.currentTimeMillis();
        while (true) {
            Integer status = LogToDb.getActivityExecuteLogStatus(exeLog);
            long endTime = System.currentTimeMillis();
            if (1 == status) {
                break;
            } if( (float)(endTime-startTime)/1000 > 20 * 60){  //20分钟算超时
                logdb.info("等待 超时 批次：{}, busi_code: {}",exeLog.getACTIVITY_SEQ_ID(),exeLog.getBUSI_CODE());
                break;
            }else {
                logdb.info("等待 批次：{}, busi_code: {}",exeLog.getACTIVITY_SEQ_ID(),exeLog.getBUSI_CODE());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /*
        从plt_activity_process_logc查询工单数量
     */
    public String countOrders(String tenantId, Integer activitySeqId, String activityid) {
        List<Map<String, Object>> countList = ordermapper.countOrdersFromProcessLog(tenantId, activityid, activitySeqId);
        return JSON.toJSONString(countList);
    }


    /**
     * 发送纯文本的简单邮件
     * @param to
     * @param subject
     * @param content
     */
//    public  void sendSimpleMail(String to, String subject, String content){
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("orderrun@126.com");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(content);
//        try {
//            sender.send(message);
//            logdb.info("简单邮件已经发送。");
//        } catch (Exception e) {
//            e.printStackTrace();
//            logdb.error("发送简单邮件时发生异常！", e);
//        }
//    }

}