package com.bonc.busi.orderschedule;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.orderschedule.activity.ActivityInfoService;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.bo.PltActivityExecuteLog;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.channel.ChannelManage;
import com.bonc.busi.orderschedule.channel.OrderService;
import com.bonc.busi.orderschedule.files.OrderFileMannager;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.log.LogToDb;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.service.FilterOrderService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.service.BaseTaskSrv;
import com.bonc.common.thread.ThreadBaseFunction;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * @Desc: scheduled class for  order producer running
 * @Author: shenyanjun@bonc.com.cn
 * @Time: 2017-04-12 for productization add
 */

@Component
public class GenOrderIns extends ThreadBaseFunction {
    @Autowired
    private BusiTools asynDataIns;
    @Autowired
    private ApiManage apiManage;
    @Autowired
    private ActivityJsonFactory jsonInit;
    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private FilterOrderService filterService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BaseTaskSrv baseTask;
    @Autowired
    private OrderMapper ordermapper;

    private static final Logger log = LoggerFactory.getLogger(GenOrderIns.class);

    private static final String IS_RUN = "ORDER_RUNNING_";


    @Override
    public int handleData(Object data) {
        Map<String, Object> tenantIdAndActivityList = (Map<String, Object>) data;
        List<String> activityList = (List<String>) tenantIdAndActivityList.get("activityList");
        String tenantId = (String) tenantIdAndActivityList.get("tenantId");
        log.info("[Ordertask2.0] {}本次调用活动列表: {}", tenantId, activityList.toString());
        // --- 首先判断是否已经有进程在处理 ---
        if (!judgeIsOrderRun(tenantId)) return -1;
        //获取活动列表
        for (String activityid : activityList) {
            //生成活动批次号
            apiManage.genActivitySeqId();
            Integer activitySeqId = apiManage.getActivitySeqId();
            //工单生成日志初始化
            PltActivityExecuteLog exeLog = new PltActivityExecuteLog();
            exeLog.setCHANNEL_ID("0");
            exeLog.setACTIVITY_SEQ_ID(activitySeqId);
            exeLog.setTENANT_ID(tenantId);
            exeLog.setACTIVITY_ID(activityid);
            //初始化Json工厂
            boolean initActivityFlag = jsonInit.initActivityProvPo(activityid, tenantId, activitySeqId);
            //判断初始化工厂是否成功（接口未调通、返回为null、返回报文非json格式不能解析）
            if (!initActivityFlag) {
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(2001);
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                log.error("[Ordertask2.0] Activity id={}  init activity Json error!!!", activityid);
                continue;
            }
            try {
                //判断是否需要跑活动
                if (!activityInfoService.isActivityNeedRun()) {
                    exeLog.setBEGIN_DATE(new Date());
                    exeLog.setPROCESS_STATUS(0);
                    exeLog.setBUSI_CODE(2002);
                    LogToDb.recordActivityExecuteLog(exeLog, 0);
                    log.debug("[Ordertask2.0] Activity id={} no need run. or state is not 9" ,activityid);
                    continue;
                }
                log.info("[Ordertask2.0]" + tenantId + "活动id：" + activityid + "活动批次：" + "活动报文：" + JSON.toJSONString(ActivityJsonFactory.getActivityProvPo()));
                //活动json中的Channelids
                List<String> orgChannelIds = ActivityJsonFactory.getChannelIds();
                //审批过的channelids 包括处理特殊渠道
                List<String> enableChannel = apiManage.getEnableChannel(activityid, orgChannelIds);
                if (enableChannel.isEmpty()) {
                    log.info("[Ordertask2.0] Activity id={} 没有渠道审批通过", activityid);
                    continue;
                }
                //记录活动基本信息
                activityInfoService.recordActivityInfo(activitySeqId);
                // truncate临时表
                orderService.cleanTempOrderTable(tenantId);
                for (String channelId : enableChannel) {
                    ChannelManage channelManage = new ChannelManage(channelId, activitySeqId, tenantId);
                    if (!channelManage.execute()) {
                        //资源化配，执行行云，入库，入库出错 清除活动表返回 不清除活动防止继续在此活动死循环 列表循环中断！！ 如有问题以后再修改
                        exeLog.setBEGIN_DATE(new Date());
                        exeLog.setPROCESS_STATUS(0);
                        exeLog.setBUSI_CODE(2004);
                        LogToDb.recordActivityExecuteLog(exeLog, 0);
                        log.info("[OrderTask2.0] Activity id={} 资源化配，执行行云，入库，入库,或下载出错", activityid);
                        asynDataIns.setValueToGlobal(IS_RUN + tenantId, "0"); //---将标识位置为0 生成结束
                        return -1;
//                        break;
                    }
                    //同步PLT_ACTIVITY_PROCESS_LOG
                    ActivityProcessLog activityProLog = new ActivityProcessLog();
                    activityProLog.setACTIVITY_ID(activityid);
                    activityProLog.setACTIVITY_SEQ_ID(activitySeqId);
                    activityProLog.setTENANT_ID(tenantId);
                    activityProLog.setCHANNEL_ID(channelId);
                    activityProLog.setSTATUS(0);
                    activityProLog.setORI_AMOUNT((OrderFileMannager.getOrderNumber()));
                    activityProLog.setORDER_END_DATE(TimeUtil.getDateTime(TimeUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss")));
                    ordermapper.UpdateActivityProcessLog(activityProLog);
                    GenOrderIns.log.info(">>>>>>>>>>>>>>> 工单的原始数量: " + OrderFileMannager.getOrderNumber() + "  渠道：" + channelId);
                }
                //黑名单过滤
                if (ActivityJsonFactory.getActivityProvPo().getIsDeleteBlackUser() != null
                        && ActivityJsonFactory.getActivityProvPo().getIsDeleteBlackUser().equals("1")) {
                    exeLog.setBEGIN_DATE(new Date());
                    exeLog.setPROCESS_STATUS(0);
                    exeLog.setBUSI_CODE(1013);
                    LogToDb.recordActivityExecuteLog(exeLog, 0);
                    filterService.filterOrderWithBlackUser(activityid, activitySeqId, tenantId, enableChannel);
                    exeLog.setPROCESS_STATUS(1);
                    exeLog.setEND_DATE(new Date());
                    LogToDb.recordActivityExecuteLog(exeLog, 1);
                }
                //工单过滤 （覆盖规则，有进有出规则）
                filterService.filterOrderStatus(activityid, activitySeqId, tenantId, enableChannel);
                
                // 保存成功产品
                orderService.commitSuccessInfo();
                // 成功标准过滤
                String orderFilterSucessFlag = apiManage.orderFilterSucess(tenantId, activitySeqId, activityid);
                if ("false".equals(orderFilterSucessFlag)) {
                    log.info("调用事前成功检查出错");
                    exeLog.setBEGIN_DATE(new Date());
                    exeLog.setPROCESS_STATUS(0);
                    exeLog.setBUSI_CODE(2005);
                    LogToDb.recordActivityExecuteLog(exeLog, 0);
                    // TODO异常处理
                } else if ("YW".equals(orderFilterSucessFlag)) {
                    log.info("异网不跑成功标准");
                } else {
                    exeLog.setBUSI_CODE(1011);
                    orderService.waitExecuteLogStatus(exeLog);
                }
                //成功过滤执行完成后，需要将临时表order_status=6的工单移入过滤表并且从临时表里删除;把过滤数同步到plt_activity_process_log表里
                filterService.delAndUpdateFilterCount(activityid, activitySeqId, tenantId, enableChannel, "6");
                // 留存过滤
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(1014);
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                filterService.reserveOrder(activityid, activitySeqId, tenantId, enableChannel);
                exeLog.setPROCESS_STATUS(1);
                exeLog.setEND_DATE(new Date());
                LogToDb.recordActivityExecuteLog(exeLog, 1);

                //从临时表里删除重复的工单:在一个渠道下有相同的手机号的重复的工单
                filterService.delRepeatedOrder(activityid, activitySeqId, tenantId, enableChannel);

                //从临时表转到工单表
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(1010);
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                for (String channelId : enableChannel) {
                    orderService.moveToOrderTable(tenantId, channelId, activitySeqId);
                }
                exeLog.setPROCESS_STATUS(1);
                exeLog.setEND_DATE(new Date());
                LogToDb.recordActivityExecuteLog(exeLog, 1);
                //成功转入工单表后把工单状态置为1, 同时更新渠道执行表
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(0000);
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                orderService.updateActivityStatus(ChannelManage.getOrderValidDate(), ChannelManage.getOrderInValidDate(), enableChannel);
                //返回工单内部统计数量保存置日志exe表
                String countStr = orderService.countOrders(tenantId,activitySeqId,activityid);
                exeLog.setBUSI_ITEM(countStr);
                exeLog.setPROCESS_STATUS(1);
                exeLog.setEND_DATE(new Date());
                LogToDb.recordActivityExecuteLog(exeLog, 1);
                // 当一个批次工单生成完成之后调用 通知渠道 统计
                // 统计
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(1012);
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                apiManage.channelInitHandle(activityid, tenantId, activitySeqId + "","");
                exeLog.setPROCESS_STATUS(1);
                exeLog.setEND_DATE(new Date());
                LogToDb.recordActivityExecuteLog(exeLog, 1);
                log.info("[Ordertask2.0] Activity id={} 生成工单完毕", activityid);
            } catch (BoncExpection bec) {
                bec.printStackTrace();
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(2004);
                exeLog.setBUSI_ITEM(bec.getMsg());
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                log.error("[OrderTask2.0] Activity id:" + activityid + bec.getMsg());
                if (bec.getMsg().contains("Error")){
                    // 资源化配超时或者状态为4需要重跑
                    PltActivityInfo activityInfo = new PltActivityInfo();
                    activityInfo.setREC_ID(activitySeqId);
                    activityInfo.setTENANT_ID(tenantId);
                    activityInfoService.cleanAcitivtyInfo(activityInfo);
                }
                continue;
            } catch (DataIntegrityViolationException dve) {
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(2003);
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                log.error("[Ordertask2.0] Activity id=" + activityid + dve.getMessage());
                continue;
            } catch (Exception e) {
                log.error("[OrderTask2.0] 其他异常信息为：" + e.getMessage());
                e.printStackTrace();
                exeLog.setBEGIN_DATE(new Date());
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_CODE(2010);
                LogToDb.recordActivityExecuteLog(exeLog, 0);
                PltActivityInfo activityInfo = new PltActivityInfo();
                activityInfo.setREC_ID(activitySeqId);
                activityInfo.setTENANT_ID(tenantId);
                activityInfoService.cleanAcitivtyInfo(activityInfo);
                log.info("[OrderTask2.0] Activity id:" + activityid + "工单生成异常结束");
                exeLog.setPROCESS_STATUS(0);
                exeLog.setBUSI_ITEM("其他异常信息为：" + e.getMessage());
                exeLog.setEND_DATE(new Date());
                LogToDb.recordActivityExecuteLog(exeLog, 1);
                continue;
            }
        }//----activityList-- 结束
        asynDataIns.setValueToGlobal(IS_RUN + tenantId, "0"); //------将标识位置为0 生成结束
        log.info("{}   此次工单生成调用正常结束",tenantId);
        return 0;
    }

    private boolean judgeIsOrderRun(String tenantId){
        boolean result = true;
        String strRunFlag;
        strRunFlag = asynDataIns.getValueFromGlobal(IS_RUN + tenantId);
        if (StringUtils.isBlank(strRunFlag)){
            strRunFlag = asynDataIns.getValueFromStatus(IS_RUN + tenantId);
        }
        if (StringUtils.isNotBlank(strRunFlag)) {
            if (strRunFlag.equals("1")) {
                log.warn("--- 租户：{} 生成工单正在运行 ---", tenantId);
                result = false;
            } else {
                if (!asynDataIns.setProgramRunningStatus(IS_RUN + tenantId, "1")) {
                    log.error("One tenant received two task,error");
                  result = false;
                }
            }
        }
        return result;
    }
}
