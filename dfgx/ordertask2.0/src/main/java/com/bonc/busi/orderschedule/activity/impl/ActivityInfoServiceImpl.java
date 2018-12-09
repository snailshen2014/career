package com.bonc.busi.orderschedule.activity.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.orderschedule.dataintergrity.FormatColumnSize;
import com.bonc.busi.task.mapper.BaseMapper;
import kafka.utils.Json;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ActivityProvPo;
import com.bonc.busi.orderschedule.activity.ActivityInfoService;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.utils.DateUtil;
import com.bonc.utils.HttpUtil;

@Service("ActivityInfoService")
public class ActivityInfoServiceImpl implements ActivityInfoService {
    private final static Logger logger = LoggerFactory.getLogger(ActivityInfoServiceImpl.class);
    @Autowired
    private ApiManage apiManage;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private BaseMapper baseMapper;

//    @Autowired
//    private FormatColumnSize formatColumnSize;

    @Override
    public boolean isActivityNeedRun() {
        // "活动类型：1、周期性（按月），2、周期性（按日），3、一次性",
        String flag = ActivityJsonFactory.getActivityProvPo().getActivityType();
        String activityState = ActivityJsonFactory.getActivityProvPo().getState();
        String activityEndDate = ActivityJsonFactory.getActivityProvPo().getEndDate();
        String act_id = ActivityJsonFactory.getActivityId();
        String tenant_id = ActivityJsonFactory.tenantId();
        if (!activityState.equals("9")) {
            // log
            logger.info("[Ordertask2.0] Activity id=" + ActivityJsonFactory.getActivityId()
                    + ":activity status!=9 no need run.");
            return false;
        }
        if (isActivityNeedRun(act_id, tenant_id, Integer.parseInt(flag), activityEndDate) == -1) {
//            logger.info("[Ordertask2.0] Activity id=" + ActivityJsonFactory.getActivityId() + " no need run.");
            return false;
        }
        return true;
    }

    /**
     * judge is activity need running by activityCycle
     *
     * @param activity                 id
     * @param tenant_id
     * @param flag:activity            cycle
     * @param activityEndDate:activity end date
     * @return -1:no run,0:run
     */
    private int isActivityNeedRun(String activity, String tenant_id, int flag, String activityEndDate) {
        // 1、月，2、日，3、一次性",
        PltActivityInfo act = new PltActivityInfo();
        act.setACTIVITY_ID(activity);
        act.setTENANT_ID(tenant_id);
        String create_time = activityInfoMapper.isActivityRun(act);
        // new activity
        if (create_time == null) {
            return 0;
        }
        // when activity expire ,the activity can not run
        if (activityEndDate != null) {
            String now = getCurrentTime("yyyy-MM-dd");
            if (activityEndDate.compareTo(now) < 0) {
                return -1;
            }
        }
        int rtn = -1;
        switch (flag) {
            case 1:// one month one time
                String actmonth = create_time.substring(0, 6);
//                String nowmonth = getCurrentTime("yyyyMM");
                String nowdate = getCurrentTime("yyyyMMdd");
                String nowmonth = nowdate.substring(0, 6);
                //周期性判断账期
//                if(!judgeDateIdIsChanged(activity,tenant_id)) return -1;
                Integer orderAppointDate = ActivityJsonFactory.getActivityProvPo().getOrderAppointDate();
                if (null != orderAppointDate  && orderAppointDate>0 && orderAppointDate<32){
                    Integer day = new Integer(nowdate.substring(6,8));
                    if (!actmonth.equals(nowmonth) && isExistsActivityList(activity, tenant_id) && day >= orderAppointDate){
                        rtn = 0;
                    }else {
                        break;
                    }
                }else {
                    if (!actmonth.equals(nowmonth) && isExistsActivityList(activity, tenant_id) )
                        rtn = 0;
                }
                break;
            case 2:// day
                String actday = create_time.substring(0, 8);
                String nowday = getCurrentTime("yyyyMMdd");
                //周期性判断账期
//                if(!judgeDateIdIsChanged(activity,tenant_id)) return -1;
                if (!actday.equals(nowday) && isExistsActivityList(activity, tenant_id))
                    rtn = 0;
                break;
            case 3:// only one time
                rtn = -1;
                break;
            default:
                return -1;
        }

//        System.out.println("[Ordertask2.0] Activity id=" + activity + ",teantid=" + tenant_id + "activityType=" + flag + ",return=" + rtn);
        return rtn;
    }

    /**
     * 周期性判断账期
     * @param activityId 活动id
     * @param tenant_id 租户id
     * @return
     */
    private boolean judgeDateIdIsChanged(String activityId, String tenant_id) {
        //目前此需求只有电信有
        boolean btn = true;
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        if(type != null && type.equals("1")) {
            String strDXCurMonthDayUrl =SystemCommonConfigManager.getSysCommonCfgValue("GET_MONTH_TIME");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("1", "1");
            params.put("tenant_id", tenant_id);
            Map<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put("req", JSON.toJSONString(params));
            String sendPost = HttpUtil.doGet(strDXCurMonthDayUrl, requestMap);
            Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
            String strCurMonthDay = (String) resultMap.get("MAX_DATE");
            String	dbDateId = baseMapper.getValueFromSysCommCfg("ASYNUSER.XCLOUD.DATEID."+tenant_id);
            if (dbDateId.equalsIgnoreCase(strCurMonthDay)){
                btn = false;
            }
            logger.info("租户:{}.账期为{},工单同步账期为{},周期性判断账期返回值{}",tenant_id,strCurMonthDay,dbDateId,btn);
        }
        return btn;
    }


    public String getCurrentTime(String formater) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(formater);// 可以方便地修改日期格式
        return dateFormat.format(now);
    }

    /**
     * judge activity is exists activity list just now
     *
     * @param activity  id
     * @param tenant_id
     * @return true or false
     */
    private boolean isExistsActivityList(String activity, String tenant_id) {
        List<String> act_list = apiManage.getActivityList(tenant_id);
        return act_list.contains(activity);

    }


    @Override
    public boolean recordActivityInfo(int activitySeqId) {
        // commitActivityInfo
        ActivityProvPo act = ActivityJsonFactory.getActivityProvPo();

        PltActivityInfo ac_obj = new PltActivityInfo();
        ac_obj.setREC_ID(activitySeqId);
        ac_obj.setACTIVITY_ID(act.getActivityId());
        ac_obj.setACTIVITY_DIVISION(act.getActivityDivision());
        ac_obj.setACTIVITY_NAME(act.getActivityName());

        if (act.getActivityTheme() != null)
            ac_obj.setACTIVITY_THEME(act.getActivityTheme());

        ac_obj.setACTIVITY_THEMEID(act.getActivityThemeCode());
        //  "activityType":"活动类型：1、周期性（按月），2、周期性（按日），3、一次性"
        ac_obj.setACTIVITY_TYPE(act.getActivityType());
        // 活动开始日期"
        if (act.getStartDate() != null)
            ac_obj.setBEGIN_DATE(DateUtil.getDate(act.getStartDate()));

        if (act.getEndDate() != null)
            ac_obj.setEND_DATE(DateUtil.getDate(act.getEndDate()));
        //   "state":"活动状态：8、停用，9、启用，10、未执行，11、暂停,12、暂存，13、审批中"
        ac_obj.setORI_STATE(act.getState());
        ac_obj.setCREATE_NAME(act.getCreateName());

        if (act.getCreateDate() != null)
            ac_obj.setCREATE_DATE(DateUtil.getDate(act.getCreateDate()));
        ac_obj.setORG_RANGE(act.getOrgRange());
        ac_obj.setTENANT_ID(act.getTenantId());
        // 初始状态设置0，工单生成后改为1
        ac_obj.setACTIVITY_STATUS(0);
        ac_obj.setACTIVITY_DESC(act.getActivityDesc());
        ac_obj.setGROUP_ID(act.getUserGroupId());
        ac_obj.setGROUP_NAME(act.getUserGroupName());
        // "urgencyLevel":"优先级：1、高，2、中，3、低",
        ac_obj.setACTIVITY_LEVEL(act.getUrgencyLevel());
        // "parentActivity":"关联总部活动Id"
        ac_obj.setPARENT_ACTIVITY(act.getParentActivity());
        // "policyId":"所属政策Id"
        ac_obj.setPOLICY_ID(act.getPolicyId());
        // 数据更新周期 1、月，2、日，3、一次性",
        if (act.getActivityType() != null && !act.getActivityType().equals(""))
            ac_obj.setORDER_GEN_RULE(Integer.parseInt(act.getActivityType()));
        // 工单周期
        if (act.getOrderCycle() != null && !act.getOrderCycle().equals(""))
            ac_obj.setORDER_LIFE_CYCLE(Integer.parseInt(act.getOrderCycle()));
        // "工单更新规则 1、有进有出，2、覆盖",
        if (act.getOrderUpdateRule() != null && !act.getOrderUpdateRule().equals(""))
            ac_obj.setORDER_UPDATE_RULE(Integer.parseInt(act.getOrderUpdateRule()));
        // 是否剔除黑名单 1、是，0、否",
        if (act.getIsDeleteBlackUser() != null && !act.getIsDeleteBlackUser().equals(""))
            ac_obj.setFILTER_BLACKUSERLIST(Integer.parseInt(act.getIsDeleteBlackUser()));
        // 是否剔除白名单 1、是，0、否",
        if (act.getIsDeleteWhiteUser() != null && !act.getIsDeleteWhiteUser().equals(""))
            ac_obj.setFILTER_WHITEUSERLIST(Integer.parseInt(act.getIsDeleteWhiteUser()));

        // 是否同一活动分类用户剔除
        if (act.getIsDeleteSameType() != null && !act.getIsDeleteSameType().equals(""))
            ac_obj.setDELETE_ACTIVITY_USER(Integer.parseInt(act.getIsDeleteSameType()));
        // 是否同一活动成功标准类型用户剔除",
        if (act.getIsDeleteSameSuccess() != null && !act.getIsDeleteSameSuccess().equals(""))
            ac_obj.setDELETE_SUCCESSRULE_USER(Integer.parseInt(act.getIsDeleteSameSuccess()));
        // 仅针对处于接触频次限制的目标客户：1、发送工单，0、不发送工单 (先不考虑字段)
        ac_obj.setIS_SENDORDER(act.getIsSendOrder());
        // "orgLevel":"活动行政级别:1、集团，2、省级，3、市级，4、其他"
        ac_obj.setORG_LEVEL(act.getOrgLevel());
        // :"客户经理与弹窗互斥发送规则：1、各自执行，0、互斥执行"
        ac_obj.setOTHER_CHANNEL_EXERULE(act.getOtherChannelExeRule());
        // :"短信微信互斥发送规则：1、各自重复发送，0、互斥发送
        ac_obj.setSELF_SEND_CHANNEL_RULE(act.getSelfSendChannelRule());
        //  "strategyDesc":"策略描述",
        ac_obj.setSTRATEGY_DESC(act.getStrategyDesc());
        // "电子渠道互斥发送规则：1、各自展示，0、展示其中一个
        ac_obj.setECHANNEL_SHOW_RULE(act.geteChannelShowRule());
        ac_obj.setPARENT_ACTIVITY_NAME(act.getParentActivityName());
        ac_obj.setPARENT_ACTIVITY_STARTDATE(act.getParentActivityStartDate());
        ac_obj.setPARENT_ACTIVITY_ENDDATE(act.getParentActivityEndDate());
        ac_obj.setPARENT_PROVID(act.getParentProvId());
        ac_obj.setCREATOR_ORGID(act.getCreateOrgId());
        ac_obj.setCREATOR_ORG_PATH(act.getCreateOrgPath());
        ac_obj.setUSERGROUP_FILTERCON(act.getUserGroupFilterCondition());
//        ac_obj.setLAST_ORDER_CREATE_TIME(DateUtil.getDateTime(getCurrentTime("yyyy-MM-dd HH:mm:ss")));
        // 预留百分比
        if (act.getObligateOrder() != null && !act.getObligateOrder().equals(""))
            ac_obj.setREMAIN_PERCENT(Integer.parseInt(act.getObligateOrder()));
        //SERVICE_PROVIDER_TYPE 判断电信还是联通的标识，如果配置了并且值是1，则为电信，否则为联通
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        if(type != null && type.equals("1")){    //只有电信才有REWARD_DESC和CHANNEL_SYNERGISM
            logger.info(">>>>>>>>>>>>>>>> 进入设置电信项目活动保存特有的字段  <<<<<<<<<<<");
            String rewardDesc = act.getReward_desc();
            String channelSynergism = act.getChannelSynergism();
            if(rewardDesc !=null){
                if(rewardDesc.length() > 127){
                    rewardDesc = rewardDesc.substring(0, 126);
                }
                ac_obj.setREWARD_DESC(rewardDesc);
            }
            if(channelSynergism !=null){
                ac_obj.setCHANNEL_SYNERGISM(channelSynergism);
            }
            logger.info(">>>>>>>>>>>>>>>> 退出设置电信项目活动保存特有的字段  <<<<<<<<<<<");
        }
        //在插入前校验下 防止超过数据库列大小
//        String actObjStr = JSON.toJSONStringWithDateFormat(ac_obj,"yyyy-MM-dd HH:mm:ss");
//        String formatStr = formatColumnSize.format("PLT_ACTIVITY_INFO", actObjStr,act.getTenantId());
//        ac_obj = JSON.parseObject(formatStr,PltActivityInfo.class);
        activityInfoMapper.InsertActivityInfo(ac_obj);
        return false;
    }

    @Override
    public boolean cleanAcitivtyInfo(PltActivityInfo activity) {
        activityInfoMapper.cleanActivityInfo(activity);
        return true;
    }

}
