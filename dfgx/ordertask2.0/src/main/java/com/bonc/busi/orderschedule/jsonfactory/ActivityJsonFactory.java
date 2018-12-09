package com.bonc.busi.orderschedule.jsonfactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.bonc.busi.activity.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("ActivityJsonFactory")
public class ActivityJsonFactory {

    @Autowired
    ApiManage apiManage;
    @Autowired
    JdbcTemplate jdbcTemplate;

    private static ActivityProvPo activityProvPo;
    private static List<Map<String, Object>> activityAdaptor;
    private static final ThreadLocal<ActivityProvPo> sysnActivityProvPo = new ThreadLocal<>();

    /*
    * 初始化json工厂
    *  return true初始化成功  false初始化失败
     */
    public boolean initActivityProvPo(String activityId, String tenantId, Integer activitySeqId) {
        //1.从外部资源模块获取加载信息
        try {
            String respond = apiManage.getActivityDetail(activityId, tenantId, activitySeqId);
//            System.out.println("活动报文为：" + respond);
            //2.适配器 做电信转换
            String format = adapterActivityJson(respond,tenantId);
            //3.转换
            ActivityProvPo actjson = JSON.parseObject(format, ActivityProvPo.class);
            if (null == actjson) {
                return false;
            }
            sysnActivityProvPo.set(actjson);
//            setActivityProvPo(actjson);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * 适配器 做电信转换
     *
     * @param respond
     * @return
     */
    private  String adapterActivityJson(String respond,String tenantId) {
        //初始化activityAdaptor
        String sql = "SELECT KEY_NAME,VALUE_ORIGIN,VALUE_TARGET FROM  PLT_ACTIVITY_ADAPTER WHERE TENANT_ID = '"+tenantId+"'";
        activityAdaptor = jdbcTemplate.queryForList(sql);
        ActivityProvPo actjson = JSON.parseObject(respond, ActivityProvPo.class);
        String resultJson = "";
        ValueFilter valueFilter = new ValueFilter() {
            @Override  // 注意这里格式化的替换是全文替换 包括子对象中重名的name
            public Object process(Object object, String name, Object value) {
                for (Map<String, Object> map:activityAdaptor )
                if (map.get("KEY_NAME").equals(name)) {
                    if (map.get("VALUE_ORIGIN").equals(value) ){
                        return map.get("VALUE_TARGET");
                    }
                }
                return value;
            }
        };
        resultJson = JSON.toJSONString(actjson, valueFilter);
        return resultJson;
    }


    /**
     * 返回全量活动信息
     *
     * @return
     */
    public static ActivityProvPo getActivityProvPo() {
//        return activityProvPo;
        return  sysnActivityProvPo.get();
    }

    public static void setActivityProvPo(ActivityProvPo activityProvPo) {
        ActivityJsonFactory.activityProvPo = activityProvPo;
    }


    /**
     * 获取审核通过的渠道
     *
     * @return key为渠道号 value是<对象名，渠道对象>
     */
    public static HashMap<String, ChannelPo> getActivityChannelInfo() {
        HashMap<String, ChannelPo> channelMap = new HashMap<String, ChannelPo>();
        ActivityProvPo act = getActivityProvPo();
        //获取活动中的渠道id
        String channelCheck = act.getChannelCheck(); // 5,7,11,8,2,1,9
        String[] channelIds = channelCheck.split(",");

        if (strContains(channelIds, "1")) {
            ChannelHandOfficePo channelHandOfficePo = act.getChannelHandOfficePo();
            channelMap.put("1", channelHandOfficePo);
        }
        if (strContains(channelIds, "2")) {
            ChannelWebOfficePo channelWebOfficePo = act.getChannelWebOfficePo();
            channelMap.put("2", channelWebOfficePo);
        }
        if (strContains(channelIds, "9")) {
            ChannelWoWindowPo channelWoWindowPo = act.getChannelWoWindowPo();
            channelMap.put("9", channelWoWindowPo);
        }
        if (strContains(channelIds, "5")) {
            FrontlineChannelPo frontlineChannelPo = act.getFrontlineChannelPo();
            channelMap.put("5", frontlineChannelPo);
        }
        if (strContains(channelIds, "7")) {
            MsmChannelPo msmChannelPo = act.getMsmChannelPo();
            channelMap.put("7", msmChannelPo);
        }
        if (strContains(channelIds, "8")) {
            // List<ChannelGroupPopupPo> channelGroupPopupPoList =
            // act.getChannelGroupPopupPoList();
            ChannelGroupPopupPo channelGroupPopupPo = act.getChannelGroupPopupPo();
            channelMap.put("8", channelGroupPopupPo);
        }
        if (strContains(channelIds, "11")) {
            ChannelWebchatInfo channelWebchatInfo = act.getChannelWebchatInfo();
            channelMap.put("11", channelWebchatInfo);
        }
        if (strContains(channelIds, "12")) {
            TelePhoneChannelPo channelTelePhone = act.getChannelTelePhone();
            channelMap.put("12", channelTelePhone);
        }
        return channelMap;
    }

    /**
     * 是否包含话术替换
     */
    public static Set<String> talkWordVars() {
        String jsonString = JSON.toJSONString(ActivityJsonFactory.getActivityProvPo());
        Set<String> set = new HashSet<String>();
        subStrVars(jsonString != null ? jsonString : "", '$', '}', 0, 0, set);
        return set;
    }

    /*
      * 获取工单是否参照活动结束时间：1、是，0、否"
     */
    public static String getReferEndFlag() {
        return getActivityProvPo().getOrderIsConsultEndDate();
    }

    /**
     * 获取活动开始日期
     */
    public static String getStartDate() {
        return getActivityProvPo().getStartDate();
    }

    /**
     * 获取活动结束日期
     */
    public static String getEndDate() {
        return getActivityProvPo().getEndDate();
    }

    /**
     * 获取工单有效期
     *
     * @return
     */
    public static String getOrderCycle() {
        return getActivityProvPo().getOrderCycle();
    }

    /**
     * 活动类型
     *
     * @return
     */
    public static String getActivityType() {
        return getActivityProvPo().getActivityType();
    }

    /**
     * 获取用户组id
     *
     * @return 返回用户组id
     */
    public static String getUserGroupId() {
        return getActivityProvPo().getUserGroupId();
    }

    /**
     * 获取下发规则
     *
     * @return
     */
    // public static String getOrderIssuedRule() {
    // return getActivityProvPo().getChannelHandOfficePo().getOrderIssuedRule();
    // }

    /**
     * 获取渠道id
     *
     * @return
     */
    // public static String channelId(Object o) {
    // ChannelPo cp = (ChannelPo) o;
    // return ChannelPo.getChannelId();
    // }

    /**
     * 获取活动中的租户id
     */
    public static String tenantId() {
        return getActivityProvPo().getTenantId();
    }

    /**
     * 获取活动id
     */
    public static String getActivityId() {
        return getActivityProvPo().getActivityId();
    }

    /**
     * 获取组织机构路径
     */
    public static String getOrgPath() {
        return getActivityProvPo().getOrgRange();
    }

    /**
     * 获取弹窗列表
     */
    public static List<ChannelGroupPopupPo> getPopWinList() {
        return getActivityProvPo().getChannelGroupPopupPoList();
    }

    /**
     * 获取当前活动中的渠道id列表
     */
    public static List<String> getChannelIds() {
        String channelIds = getActivityProvPo().getChannelCheck();
        String[] split = channelIds.split(",");
        return Arrays.asList(split);
    }


    /**
     * 检查数组是否包含某个值的方法
     *
     * @param str
     * @param targetValue
     * @return
     */
    public static boolean strContains(String[] str, String targetValue) {
        return Arrays.asList(str).contains(targetValue);
    }

    /**
     * 拆解字符串中 两个字符间的字符串
     *
     * @param str 需要拆解字符串
     * @param a   第一个字符
     * @param b   第二个字符
     * @param le  第一个字符初始开始数的索引
     * @param ri  第二个字符初始开始数的索引
     * @param set 提取出来的字符组成的set
     */
    public static void subStrVars(String str, char a, char b, int le, int ri, Set<String> set) {
        int left = le;
        int right = ri;
        int one = str.indexOf(a, left + 1);
        int two = str.indexOf(b, right + 1);
        left = one;
        right = two;
        if (one >= 0 && two >= 0) {
            if (left < right) {
                set.add(str.substring(one, right + 1));
                int i = str.length();
                if (left != str.lastIndexOf(a) && right != str.lastIndexOf(b) && right < str.length() && left < str.length() && left < right) {
                    subStrVars(str, a, b, left, right, set);
                }
            } else {
                subStrVars(str, a, b, le, right, set);
            }
        }
    }


}
