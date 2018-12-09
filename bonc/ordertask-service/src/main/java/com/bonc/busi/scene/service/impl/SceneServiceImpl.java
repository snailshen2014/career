package com.bonc.busi.scene.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.*;
import com.bonc.busi.outer.bo.OrderTablesAssignRecord4S;
import com.bonc.busi.outer.bo.PltActivityInfo;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.busi.outer.model.PltActivityChannelDetail;
import com.bonc.busi.scene.mapper.OrderMapper;
import com.bonc.utils.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.scene.bo.ScenePowerInfo;
import com.bonc.busi.scene.bo.ScenePowerStatus;
import com.bonc.busi.scene.mapper.SceneMapper;
import com.bonc.busi.scene.service.SceneService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;


@Service("sceneService")
public class SceneServiceImpl implements SceneService {

    private final int TRANSACTION_TIMES = 100000;
    private static final Logger logger = Logger.getLogger(SceneServiceImpl.class);

    @Autowired
    private SceneMapper mapper;
    @Autowired
    private BusiTools busiTools;
    @Autowired
    private OrderMapper ordermapper;
    @Autowired
    PltActivityInfoDao PltActivityInfoDaoIns;

    private Integer activitySeqId;

    private Integer getActivitySeqId() {
        return this.activitySeqId;
    }

    @Override
    public HashMap<String, Object> querySuccessNum(HashMap<String, Object> req) {
        // 先查询活动信息
//        List<Object> recIds = mapper.queryActivitySeq(req);
//        if (recIds.size() <= 0) {
//            throw new BoncExpection(IContants.CODE_FAIL, " activity not exists! ");
//        }
//
//        String recSql = "";
//        for (int i = 0, max = recIds.size() - 1; i <= max; i++) {
//            recSql = (recSql + recIds.get(i) + (i == max ? "" : ","));
//        }
//        req.put("recSql", recSql);
        Integer  recIds = mapper.querySceneActivitySeq(req);
        req.put("recSql", recIds);

        if (!StringUtil.validateStr(req.get("contactDateStart"))) {
            req.put("contactDateStartSql", " AND BEGIN_DATE>=#{contactDateStart} ");
        }
        if (!StringUtil.validateStr(req.get("contactDateEnd"))) {
            req.put("contactDateEndSql", " AND BEGIN_DATE<=#{contactDateEnd} ");
        }

        //工单2.0添加获取表名
        //调用接口获取表名
        String orderNameUrl = busiTools.getValueFromGlobal("ORDERSERVICE_GETORDERTABLENAME");
        //2.0获取表名
        OrderTablesAssignRecord4S orderTablesAssignRecord4S = new OrderTablesAssignRecord4S();
        orderTablesAssignRecord4S.setActivityId((String) req.get("activityId"));
        orderTablesAssignRecord4S.setChannelId("7");
        //因为场景营销目前只有短信渠道暂且按统一渠道处理 ！！！！！！！！！！！！
        //TODO
        orderTablesAssignRecord4S.setActivitySeqId(recIds);
        orderTablesAssignRecord4S.setTenantId((String) req.get("tenantId"));
        orderTablesAssignRecord4S.setBusiType(3);
        String orderTableName = PltActivityInfoDaoIns.getOrderTableName(orderTablesAssignRecord4S);
        if (null == orderTableName){
            logger.info("--------------场景营销路由表名为null----------------");
            BoncExpection boncExpection = new BoncExpection();
            boncExpection.setMsg("路由表名为null");
            throw boncExpection;
        }
        req.put("tableName",orderTableName);
        HashMap<String, Object> resp = mapper.querySuccessNum(req);
        return resp;
    }

    @Override
    public JsonResult addSenceRecordBatch(HashMap<Object, Object> request) {
        JsonResult result = new JsonResult();
        result.setCode("0");
        result.setMessage("insert sence record success");
        List<String> phoneNums;
        try {
            phoneNums = (List<String>) request.get("phoneNums");
        } catch (Exception e) {
            result.setCode("3");
            result.setMessage("参数信息有误" + e.getMessage());
            return result;
        }
        // 1.获取批次号 插入一条状态信息 状态码为0
        ScenePowerStatus scenePowerStatus = new ScenePowerStatus();
        scenePowerStatus.setBeginDate(new Date());
        int batchId = busiTools.getSequence("SCENCE_BATCH_ID");
        String tenantId = (String) request.get("tenantId");
        scenePowerStatus.setBatchId(batchId);
        scenePowerStatus.setEndDate(new Date());
        scenePowerStatus.setStatus("0");
        scenePowerStatus.setTenantId(tenantId);
        // 1.1 如果批次号重复则清除之前的批次号数据再插入新的数据
        Integer oldBatchId = mapper.queryIsExistBatchId(batchId, tenantId);
        if (null != oldBatchId || !"".equals(oldBatchId)) {
            mapper.delOldScenePowerStatus(batchId, tenantId);
        }

        mapper.addScenePowerStatus(scenePowerStatus);

        // 2.批量数据入库
        ScenePowerInfo scenePowerInfo = new ScenePowerInfo();
        scenePowerInfo.setBatchId(batchId);
        scenePowerInfo.setTenantId((String) request.get("tenantId"));
        scenePowerInfo.setPhoneNumber(phoneNums);
        Integer insertPhoneNums = insertBatchRecord(scenePowerInfo);
        result.setMessage("insertPhoneNums :" + insertPhoneNums);
        result.setData(batchId);
        // 3.入库完成更新状态库 状态码为1
        scenePowerStatus.setEndDate(new Date());
        scenePowerStatus.setStatus("1");
        mapper.updateScenePowerStates(scenePowerStatus);
        // 4.异步处理扫表查询 进行统计 启动多线程处理 (改在ordertask中进行)
//		AsynDealAnalyse();

        // 5.返回插入完成信息

        return result;
    }

    /**
     * 批量插入手机号
     *
     * @param scenePowerInfo
     * @return 返回成功数量
     */
    private Integer insertBatchRecord(ScenePowerInfo scenePowerInfo) {
        Integer countResult = 0;
        List<String> phoneNums = scenePowerInfo.getPhoneNumber();
        //拆分手机号，50000为一次

        for (int i = 0; i < ((phoneNums.size() % TRANSACTION_TIMES != 0) ? phoneNums.size() / TRANSACTION_TIMES + 1 : phoneNums.size() / TRANSACTION_TIMES); i++) {
            //拼接手机号
            StringBuilder sbb = new StringBuilder();
            String valueSql = "";
            for (int j = i * TRANSACTION_TIMES; j < i * TRANSACTION_TIMES + TRANSACTION_TIMES && j < phoneNums.size(); j++) {
                sbb.append("(");
                sbb.append("'").append(scenePowerInfo.getBatchId()).append("',");
                sbb.append("'").append(String.valueOf(phoneNums.get(j))).append("',");
                sbb.append("'").append(scenePowerInfo.getTenantId().toString()).append("'),");
                valueSql = sbb.substring(0, sbb.lastIndexOf(","));
                valueSql += ")";
            }
            Integer insertPhoneNums = mapper.addScenePowerInfo(valueSql.substring(0, valueSql.length() - 1));
            countResult += insertPhoneNums;
            sbb.setLength(0);
        }
        return countResult;
    }


    @Override
    public JsonResult queryScencePowerStatus(HashMap<Object, String> request) {
        JsonResult result = new JsonResult();
        result.setCode("0");
        result.setMessage("query sence record success");
        if (null == request.get("batchId")) {
            result.setCode("-1");
            result.setMessage("参数信息有误");
            return result;
        }
        //1.根据批次号查询对应状态码
        HashMap<String, Object> statusAndNums = mapper.queryScencePowerStatus(String.valueOf(request.get("batchId")), (String) request.get("tenantId"));
        if (null == statusAndNums) {
            result.setMessage("查询结果为空");
            return result;
        }
        //2.1 状态码为3 返回处理完成，同时count数值
        //2.2 状态码为2 返回正在处理
        if ("3".equals(statusAndNums.get("STATE"))) {
            result.setCode("3");
            result.setMessage("处理完成");
            result.setData(statusAndNums.get("RESULT_NUM"));
        } else if ("2".equals(statusAndNums.get("STATE"))) {
            result.setCode("2");
            result.setMessage("处理中");
        } else if ("1".equals(statusAndNums.get("STATE"))) {
            result.setCode("1");
            result.setMessage("处理中");
        } else {
            result.setCode("0");
            result.setMessage("未开始");
        }
        return result;
    }


    // 场景营销活动
    @Override
    public JsonResult startSceneMarketActivity(String response) {
        JsonResult JsonResultIns = new JsonResult();
        ActivityProvPo actjson = new ActivityProvPo();
        try {
            actjson = JSON.parseObject(response, ActivityProvPo.class);
        } catch (Exception e) {
            JsonResultIns.setCode("2");
            JsonResultIns.setMessage("错误的JSON请求数据！");
            return JsonResultIns;
        }

        // judge run activity is running?
        String act_id = actjson.getActivityId();
        String tenant_id = actjson.getTenantId();

        if (act_id == null || tenant_id == null) {
            logger.error("[OrderCenter] activityType,ActivityId,tenantId param error!");
            JsonResultIns.setCode("2");
            JsonResultIns.setMessage("参数ActivityId或TenantId为空");
            return JsonResultIns;
        }
        // record activity info
        if (!recordActivityInfo(actjson)) {
            logger.error("[OrderCenter] Activity id=" + act_id + " get activity sequence error.");
            JsonResultIns.setCode("2");
            JsonResultIns.setMessage("活动启用失败");
            return JsonResultIns;
        }
        // sync success info
        try {
            commitSuccessInfo(actjson);
        } catch (Exception e) {
            JsonResultIns.setCode("2");
            JsonResultIns.setMessage("活动成功标准，产品列表入库失败");
        }
        // record activity info
        if (!addActivityChannelDetail(actjson)) {
            logger.error("[OrderCenter] Activity id=" + act_id + " get activityChannelDetail sequence error.");
            JsonResultIns.setCode("2");
            JsonResultIns.setMessage("活动明细表录入失败");
            return JsonResultIns;
        }


        JsonResultIns.setCode("1");
        JsonResultIns.setMessage("活动启用成功");
        return JsonResultIns;

    }

    /**
     * 查询活动订单
     *
     */
    @Override
    public List<HashMap<String, Object>> queryActivityOrderInfo(HashMap<String, Object> req) {
        if(!StringUtil.validateStr(req.get("envType"))){
            req.put("envTypeAnd", " AND RESERVE1 = #{envType} ");
        }
        if(!StringUtil.validateStr(req.get("eventId"))){
            req.put("eventIdAnd", " AND RESERVE2 = #{eventId} ");
        }
        if(!StringUtil.validateStr(req.get("channelId"))){
            req.put("channelIdAnd", " AND CHANNEL_ID = #{channelId} ");
        }
        if(!StringUtil.validateStr(req.get("activityId"))){
            req.put("activitySeqId", " AND	ACTIVITY_SEQ_ID IN (SELECT IFNULL(REC_ID,0) FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_ID = #{activityId}) ");
        }
        //查询活动批次号
        Integer  recId = mapper.querySceneActivitySeq(req);
        //2.0获取表名
        OrderTablesAssignRecord4S orderTablesAssignRecord4S = new OrderTablesAssignRecord4S();
        orderTablesAssignRecord4S.setActivityId((String) req.get("activityId"));
        orderTablesAssignRecord4S.setChannelId((String) req.get("channelId"));
        orderTablesAssignRecord4S.setActivitySeqId(recId);
        orderTablesAssignRecord4S.setTenantId((String) req.get("tenantId"));
        orderTablesAssignRecord4S.setBusiType(3);
        String orderTableName = PltActivityInfoDaoIns.getOrderTableName(orderTablesAssignRecord4S);
        req.put("orderTableName",orderTableName);
        return  PltActivityInfoDaoIns.selectActivityOrder(req);
    }

    // 场景营销活动
    public boolean addActivityChannelDetail(ActivityProvPo actjson) {
        boolean flag = false;
        PltActivityChannelDetail detail = new PltActivityChannelDetail();
        detail.setCHANN_ID("6");
        detail.setTENANT_ID(actjson.getTenantId());
        detail.setACTIVITY_SEQ_ID(getActivitySeqId());
        ordermapper.insertChannelDetailInfo(detail);
        flag = true;
        return flag;
    }

    // 场景营销活动
    private boolean recordActivityInfo(ActivityProvPo act) {
        // 3.commitActivityInfo
        // set activitySeqId
        this.activitySeqId = busiTools.getActivitySeqId();
        if (this.activitySeqId == -1) {
            logger.error("[OrderCenter] get activity sequence id error.");
            return false;
        }

        PltActivityInfo ac_obj = new PltActivityInfo();
        ac_obj.setREC_ID(this.activitySeqId);
        ac_obj.setACTIVITY_ID(act.getActivityId());
        // ac_obj.setACTIVITY_DIVISION(act.getActivityDivision());
        ac_obj.setACTIVITY_NAME(act.getActivityName());
        // ac_obj.setACTIVITY_THEME(act.getActivityTheme());
        // ac_obj.setACTIVITY_THEMEID(act.getActivityThemeCode());
        /*
		 * //  "activityType":"活动类型：1、周期性（按月），2、周期性（按日），3、一次性"
		 * ac_obj.setACTIVITY_TYPE(act.getActivityType());
		 */
        // 活动开始日期,不能为空"
        if (act.getStartDate() != null) {
            ac_obj.setBEGIN_DATE(getDate(act.getStartDate()));
        } else {
            throw new BoncExpection(IContants.CODE_FAIL, "StartDate is empty");
        }

        if (act.getEndDate() != null) {
            ac_obj.setEND_DATE(getDate(act.getEndDate()));
        } else {
            throw new BoncExpection(IContants.CODE_FAIL, "EndDate is empty");
        }
        //   "state":"活动状态：8、停用，9、启用，10、未执行，11、暂停,12、暂存，13、审批中"
        ac_obj.setORI_STATE(act.getState());
        ac_obj.setCREATE_NAME(act.getCreateName());

        if (act.getCreateDate() != null)
            ac_obj.setCREATE_DATE(getDate(act.getCreateDate()));
        ac_obj.setORG_RANGE(act.getOrgRange());
        ac_obj.setTENANT_ID(act.getTenantId());
        // 初始状态设置0，工单生成后改为1
        ac_obj.setACTIVITY_STATUS(1);
        ac_obj.setACTIVITY_DESC(act.getActivityDesc());
        // ac_obj.setGROUP_ID(act.getUserGroupId());
        ac_obj.setGROUP_NAME(act.getUserGroupName());
        // "urgencyLevel":"优先级：1、高，2、中，3、低",
        // ac_obj.setACTIVITY_LEVEL(act.getUrgencyLevel());
        // "parentActivity":"关联总部活动Id"
        // ac_obj.setPARENT_ACTIVITY(act.getParentActivity());
        // "policyId":"所属政策Id"
        // ac_obj.setPOLICY_ID(act.getPolicyId());
        // 数据更新周期 1、月，2、日，3、一次性",
        // if (act.getActivityType() != null)
        // ac_obj.setORDER_GEN_RULE(Integer.parseInt(act.getActivityType()));
        // 工单周期
        // if (act.getOrderCycle() != null)
        // ac_obj.setORDER_LIFE_CYCLE(Integer.parseInt(act.getOrderCycle()));
        // "工单更新规则 1、有进有出，2、覆盖",
        // if (act.getOrderUpdateRule() != null)
        // ac_obj.setORDER_UPDATE_RULE(Integer.parseInt(act.getOrderUpdateRule()));
        // 是否剔除黑名单 1、是，0、否",
        // if (act.getIsDeleteBlackUser() != null)
        // ac_obj.setFILTER_BLACKUSERLIST(Integer.parseInt(act.getIsDeleteBlackUser()));
        // 是否剔除白名单 1、是，0、否",
        // if (act.getIsDeleteWhiteUser() != null)
        // ac_obj.setFILTER_WHITEUSERLIST(Integer.parseInt(act.getIsDeleteWhiteUser()));

        // 是否同一活动分类用户剔除
        // if (act.getIsDeleteSameType() != null)
        // ac_obj.setDELETE_ACTIVITY_USER(Integer.parseInt(act.getIsDeleteSameType()));
        // 是否同一活动成功标准类型用户剔除",
        // if (act.getIsDeleteSameSuccess() != null)
        // ac_obj.setDELETE_SUCCESSRULE_USER(Integer.parseInt(act.getIsDeleteSameSuccess()));
        // 仅针对处于接触频次限制的目标客户：1、发送工单，0、不发送工单 (先不考虑字段)
        // ac_obj.setIS_SENDORDER(act.getIsSendOrder());
        // "orgLevel":"活动行政级别:1、集团，2、省级，3、市级，4、其他"
        ac_obj.setORG_LEVEL(act.getOrgLevel());
        // :"客户经理与弹窗互斥发送规则：1、各自执行，0、互斥执行"
        // ac_obj.setOTHER_CHANNEL_EXERULE(act.getOtherChannelExeRule());
        // :"短信微信互斥发送规则：1、各自重复发送，0、互斥发送
        // ac_obj.setSELF_SEND_CHANNEL_RULE(act.getSelfSendChannelRule());
        //  "strategyDesc":"策略描述",
        // ac_obj.setSTRATEGY_DESC(act.getStrategyDesc());
        // "电子渠道互斥发送规则：1、各自展示，0、展示其中一个
        // ac_obj.setECHANNEL_SHOW_RULE(act.geteChannelShowRule());
        // ac_obj.setPARENT_ACTIVITY_NAME(act.getParentActivityName());
        // ac_obj.setPARENT_ACTIVITY_STARTDATE(act.getParentActivityStartDate());
        // ac_obj.setPARENT_ACTIVITY_ENDDATE(act.getParentActivityEndDate());
        // ac_obj.setPARENT_PROVID(act.getParentProvId());
        ac_obj.setCREATOR_ORGID(act.getCreateOrgId());
        ac_obj.setCREATOR_ORG_PATH(act.getCreateOrgPath());
        // ac_obj.setUSERGROUP_FILTERCON(act.getUserGroupFilterCondition());
        // ac_obj.setLAST_ORDER_CREATE_TIME(getDateTime(getCurrentTime("yyyy-MM-dd
        // HH:mm:ss")));
        ac_obj.setACTIVITY_SOURCE("1");
        commitActivityInfo(ac_obj);
        return true;
    }

    private void commitChannelInfo(ActivityProvPo act) {
        // 渠道-本地弹窗 8
        if (act.getChannelGroupPopupPoList() != null) {
            for (ChannelGroupPopupPo po : act.getChannelGroupPopupPoList()) {
                if (po.getChannelId() != null) {
                    po.setActivity_Id(act.getActivityId());
                    po.setTenant_id(act.getTenantId());
                    ordermapper.InsertGroupPop(po);
                }
            }
        }

        // channelHandOfficePo 1-手厅
        if (act.getChannelHandOfficePo() != null) {
            ChannelHandOfficePo hand = act.getChannelHandOfficePo();
            if (hand.getChannelId() != null) {
                hand.setActivityId(act.getActivityId());
                hand.setTenantId(act.getTenantId());
                ordermapper.InsertChannelHandOffice(hand);
            }
        }
        // channelWebOfficePo 2-网厅
        if (act.getChannelWebOfficePo() != null) {
            ChannelWebOfficePo web = act.getChannelWebOfficePo();
            if (web.getChannelId() != null) {
                web.setActivityId(act.getActivityId());
                web.setTenantId(act.getTenantId());
                ordermapper.InsertChannelWebOffice(web);
            }
        }
        // channelWebchatInfo 11-微信
        if (act.getChannelWebchatInfo() != null) {
            ChannelWebchatInfo wechat = act.getChannelWebchatInfo();
            if (wechat.getChannelId() != null) {
                wechat.setActivityId(act.getActivityId());
                wechat.setTenantId(act.getTenantId());
                ordermapper.InsertChannelWebchatInfo(wechat);
            }
        }
        // channelWoWindowPo 9-活视窗
        if (act.getChannelWoWindowPo() != null) {
            ChannelWoWindowPo wo = act.getChannelWoWindowPo();
            wo.setTenantId(act.getTenantId());
            wo.setActivityId(act.getActivityId());
            if (wo.getChannelId() != null)
                ordermapper.InsertChannelWebWoWindow(wo);
        }
        // frontlineChannelPo 5
        if (act.getFrontlineChannelPo() != null) {
            FrontlineChannelPo front = act.getFrontlineChannelPo();
            front.setTenantId(act.getTenantId());
            front.setActivityId(act.getActivityId());
            if (front.getChannelId() != null)
                ordermapper.InsertChannelFrontline(front);
        }
        // msmChannelPo 7-短信
        if (act.getMsmChannelPo() != null) {
            MsmChannelPo msm = act.getMsmChannelPo();
            msm.setTenantId(act.getTenantId());
            act.setActivityId(msm.getActivityId());
            if (msm.getChannelId() != null) {
                ordermapper.InsertChannelMsm(msm);
            }
        }

    }

    private void commitSuccessInfo(ActivityProvPo act) {
        SuccessStandardPo success = act.getSuccessStandardPo();
        if (success != null) {
            String activity_seq_id = getActivitySeqId().toString();
            success.setTenantId(act.getTenantId());
            success.setActivityId(act.getActivityId());
            success.setActivity_seq_id(activity_seq_id);
            ordermapper.InsertSuccessStandardPo(success);
            List<SuccessProductPo> list = success.getSuccessProductList();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    SuccessProductPo p = list.get(i);
                    p.setActivityId(act.getActivityId());
                    p.setTenantId(act.getTenantId());
                    p.setActivity_seq_id(activity_seq_id);
                    ordermapper.InsertProduct(p);
                }
            }
        }

    }
    public void commitActivityInfo(PltActivityInfo activity) {
        ordermapper.InsertActivityInfo(activity);
    }


    private Timestamp getDate(String datetime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsed = null;
        try {
            parsed = format.parse(datetime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new java.sql.Timestamp(parsed.getTime());

    }
}