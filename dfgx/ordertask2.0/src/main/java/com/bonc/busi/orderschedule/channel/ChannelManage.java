package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.activity.ChannelPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.orderschedule.common.DateTimeFun;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.utils.IContants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChannelManage {
    private static final Logger logger = LoggerFactory.getLogger(ChannelManage.class);
    private ChannelFunc channelFunc = null;
    private ChannelPo channelPo = null;

    private static final ThreadLocal<Activity> act = new ThreadLocal<Activity>() {
        @Override
        protected Activity initialValue() {
            return new Activity();
        }
    };

    // 无参构造
    public ChannelManage() {
        logger.info("工单生成渠道---无参构造");
    }

    public ChannelManage(String channelId, int activitySeqId, String tenantId) {
        genOrderLifeReferActivityDate();

        act.get().setActivitySeqId(activitySeqId);
        act.get().setTenantId(tenantId);

        // 渠道-本地弹窗 81
        if (channelId.equals(IContants.TC_CHANNEL_1)) {
            for (int i = 0; i < ActivityJsonFactory.getPopWinList().size(); i++) {
                if (ActivityJsonFactory.getPopWinList().get(i).getBusinessHall().equals("1")) {
                    this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelGroupPopupPoList().get(i);
                    //设置父渠道id
                    act.get().setSupChannelId(IContants.TC_CHANNEL);
                    act.get().setChannelId(IContants.TC_CHANNEL_1);
                    this.channelFunc = new GroupPopupChannel(act.get());
                }
            }
        }
        // 渠道-本地弹窗 82
        if (channelId.equals(IContants.TC_CHANNEL_2)) {
            for (int i = 0; i < ActivityJsonFactory.getPopWinList().size(); i++) {
                if (ActivityJsonFactory.getPopWinList().get(i).getBusinessHall().equals("2")) {
                    this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelGroupPopupPoList().get(i);
                    act.get().setSupChannelId(IContants.TC_CHANNEL);
                    act.get().setChannelId(IContants.TC_CHANNEL_2);
                    this.channelFunc = new GroupPopupChannel(act.get());
                }
            }
        }
        // 渠道-本地弹窗 83
        if (channelId.equals(IContants.TC_CHANNEL_3)) {
            for (int i = 0; i < ActivityJsonFactory.getPopWinList().size(); i++) {
                if (ActivityJsonFactory.getPopWinList().get(i).getBusinessHall().equals("3")) {
                    this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelGroupPopupPoList().get(i);
                    act.get().setSupChannelId(IContants.TC_CHANNEL);
                    act.get().setChannelId(IContants.TC_CHANNEL_3);
                    this.channelFunc = new GroupPopupChannel(act.get());
                }
            }
        }

        // channelHandOfficePo 1-手厅
        if (channelId.equals(IContants.ST_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelHandOfficePo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.ST_CHANNEL);
            act.get().setChannelId(IContants.ST_CHANNEL);
            this.channelFunc = new HandOfficeChannel(act.get());
        }
        // channelWebOfficePo 2-网厅
        if (channelId.equals(IContants.WT_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelWebOfficePo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.WT_CHANNEL);
            act.get().setChannelId(IContants.WT_CHANNEL);
            this.channelFunc = new WebOfficeChannel(act.get());
        }
        // channelWebchatInfo 11-微信
        if (channelId.equals(IContants.WX_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelWebchatInfo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.WX_CHANNEL);
            act.get().setChannelId(IContants.WX_CHANNEL);
            this.channelFunc = new WebchatChannel(act.get());
        }
        // channelWoWindowPo 9-活视窗
        if (channelId.equals(IContants.WSC_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelWoWindowPo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.WSC_CHANNEL);
            act.get().setChannelId(IContants.WSC_CHANNEL);
            this.channelFunc = new WoWindowChannel(act.get());
        }
        // frontlineChannelPo 5-客户经理
        if (channelId.equals(IContants.YX_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getFrontlineChannelPo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.YX_CHANNEL);
            act.get().setChannelId(IContants.YX_CHANNEL);
            this.channelFunc = new FrontlineChannel(act.get());
        }
        // msmChannelPo 7-短信
        if (channelId.equals(IContants.DX_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getMsmChannelPo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.DX_CHANNEL);
            act.get().setChannelId(IContants.DX_CHANNEL);
            this.channelFunc = new MsmChannel(act.get());
        }
        // msmChannelPo 14-电话
        if (channelId.equals(IContants.Tel_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelTelePhone();
            //设置父渠道id
            act.get().setSupChannelId(IContants.Tel_CHANNEL);
            act.get().setChannelId(IContants.Tel_CHANNEL);
            this.channelFunc = new TeleChannel(act.get());
        }
        // msmChannelPo 15- 其他渠道
        if (channelId.equals(IContants.Other_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelOtherPo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.Other_CHANNEL);
            act.get().setChannelId(IContants.Other_CHANNEL);
            this.channelFunc = new OtherChannel(act.get());
        }
        // msmChannelPo 3- 集团短信渠道
        if (channelId.equals(IContants.ZongBuSms_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getZongBuMsmChannelPo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.ZongBuSms_CHANNEL);
            act.get().setChannelId(IContants.ZongBuSms_CHANNEL);
            this.channelFunc = new ZongBuMsmChannel(act.get());
        }
        // WxwlChannelList 16 - 维系渠道
        if (channelId.equals(IContants.Weixi_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getWxwlChannelList();
            //设置父渠道id
            act.get().setSupChannelId(IContants.Weixi_CHANNEL);
            act.get().setChannelId(IContants.Weixi_CHANNEL);
            this.channelFunc = new WeixiChannel(act.get());
        }
        // StyleBoxChannelList 17- 九宫格渠道
        if (channelId.equals(IContants.StyleBox_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getStyleBoxChannelList();
            //设置父渠道id
            act.get().setSupChannelId(IContants.StyleBox_CHANNEL);
            act.get().setChannelId(IContants.StyleBox_CHANNEL);
            this.channelFunc = new StyleBoxChannel(act.get());
        }
        // MinAcceptChannelList 18- 极简渠道
        if (channelId.equals(IContants.MinAccept_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getMinAcceptChannelList();
            //设置父渠道id
            act.get().setSupChannelId(IContants.MinAccept_CHANNEL);
            act.get().setChannelId(IContants.MinAccept_CHANNEL);
            this.channelFunc = new MinAcceptChannel(act.get());
        }
        //  OutCallChannel  21- 海南外呼渠道
        if (channelId.equals(IContants.OutCall_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getChannelOutcallPo();
            //设置父渠道id
            act.get().setSupChannelId(IContants.OutCall_CHANNEL);
            act.get().setChannelId(IContants.OutCall_CHANNEL);
            this.channelFunc = new OutCallChannel(act.get());
        }
        //  OutCallChannel  22- 广西外呼渠道
        if (channelId.equals(IContants.Waihu_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getWaihuChannelList();
            //设置父渠道id
            act.get().setSupChannelId(IContants.Waihu_CHANNEL);
            act.get().setChannelId(IContants.Waihu_CHANNEL);
            this.channelFunc = new WaihuChannel(act.get());
        }
        //  OutCallChannel  23- 广西客服渠道
        if (channelId.equals(IContants.KeFu_CHANNEL)) {
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getKefuChannelList();
            //设置父渠道id
            act.get().setSupChannelId(IContants.KeFu_CHANNEL);
            act.get().setChannelId(IContants.KeFu_CHANNEL);
            this.channelFunc = new KefuChannel(act.get());
        }
        // broadbandCallChannel  24  - 黑龙江宽带渠道
        if (channelId.equals(IContants.Broadband_CHANNEL)){
            this.channelPo = ActivityJsonFactory.getActivityProvPo().getBroadbandCallList();
            //设置父渠道id
            act.get().setSupChannelId(IContants.Broadband_CHANNEL);
            act.get().setChannelId(IContants.Broadband_CHANNEL);
            this.channelFunc = new BroadbandCallChannel(act.get());
        }
        // TelecomD 电信通用渠道 d  按客户经理走
        if (channelId.contains("d") || channelId.contains("D")) {
            List<FrontlineChannelPo> channelDPoList = ActivityJsonFactory.getActivityProvPo().getDChannel();
            for (int i = 0; i < channelDPoList.size(); i++) {
                if (channelDPoList.get(i).getChannelId().equals(channelId)) {
                    this.channelPo = channelDPoList.get(i);
                    //设置父渠道id
                    act.get().setSupChannelId(channelId);
                    act.get().setChannelId(channelId);
                    this.channelFunc = new DChannel(act.get());
                }
            }
        }


    }


    /**
     */
    public boolean execute() {
        // 生成工单
        if (null == channelPo.getOrderIssuedRule()){
            logger.error("该渠道下发规则orderIssuedRule为null");
            return false;
        }
        return this.channelFunc.genChannelOrders(channelPo);
    }


    /**
     * count order begin_date ,end_date refer to orderIsConsultEndDate
     */
    private static void genOrderLifeReferActivityDate() {
        String activityCycle = ActivityJsonFactory.getActivityType();
        String orderCycle = ActivityJsonFactory.getOrderCycle();
        String refer = ActivityJsonFactory.getReferEndFlag();
        String endDate = ActivityJsonFactory.getEndDate();
        int flag = Integer.parseInt(activityCycle);
        // for count date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        // record order begin date
        act.get().setOrderBeginDate(DateTimeFun.getCurrentTime("yyyy-MM-dd HH:mm:ss"));

        Date activityEndDate = null;
        String endDateAfter = null;
        if (endDate != null) {
            // add hh mm ss
            endDateAfter = endDate;
            try {
                // yyyy-mm-dd
                if (endDateAfter.trim().length() == 10) {
                    endDateAfter += " 23:59:59";
                } else {
                    logger.error("[OrderTask2.0] actiivty endDate format error.!");
                    return;
                }
                activityEndDate = dateFormat.parse(endDateAfter);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logger.error("[OrderTask2.0] dateFormat activity enddate error.");
                return;
            }
        }

        String resultEndDate = null;
        switch (flag) {
            case 1:// one month one time
                //判断报文中是否指定了工单生成时间，如果有按照下月的指定日期前一天当失效批次日期
                Integer orderAppointDate = ActivityJsonFactory.getActivityProvPo().getOrderAppointDate();
                String tmpEndDate = null;
                Date date = null;
                if (null!=orderAppointDate && orderAppointDate>0 && orderAppointDate<32){
                    calendar.add(Calendar.MONTH, Integer.parseInt(orderCycle));
                    calendar.set(Calendar.DATE, orderAppointDate -1);
                    date = calendar.getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    tmpEndDate = format.format(date);
                    tmpEndDate += " 23:59:59";
                }else {
                    calendar.add(Calendar.MONTH, Integer.parseInt(orderCycle) - 1);
                     date = calendar.getTime();
                    // yyyy-MM-dd
                    tmpEndDate = DateTimeFun.getLastDayOfMonth(date);
                    tmpEndDate += " 23:59:59";
                }
                if (endDate == null) {
                    // set end date
                    resultEndDate = tmpEndDate;
                } else {
                    // refer to activity enddate
                    if (refer != null && refer.equals("1")) {
                        // set end date
                        resultEndDate = endDateAfter;
                    } else {
                        // set smaller date
                        if (date.before(activityEndDate)) {
                            // set date
                            resultEndDate = tmpEndDate;
                        } else {
                            // set activityEndDate
                            resultEndDate = endDateAfter;
                        }
                    }
                }
                break;
            case 2:// day
                calendar.add(Calendar.DATE, Integer.parseInt(orderCycle) - 1);
                Date date2 = calendar.getTime();
                SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
                String tmpEndDate2 = dateFormat3.format(date2);
                tmpEndDate2 += " 23:59:59";

                if (endDate == null) {
                    // set end date
                    resultEndDate = tmpEndDate2;
                } else {
                    // refer to activity enddate
                    if (refer != null && refer.equals("1")) {
                        // set end date
                        resultEndDate = endDateAfter;
                    } else {
                        // set smaller date
                        if (date2.before(activityEndDate)) {
                            // set date
                            resultEndDate = tmpEndDate2;
                        } else {
                            // set activityEndDate
                            resultEndDate = endDateAfter;
                        }
                    }
                }
                break;
            case 3:// only one time
                if (endDate != null) {
                    // set activityEndDate
                    resultEndDate = endDateAfter;
                } else {
                    // set activityEndDate
                    resultEndDate = "2099-12-31 23:59:59";
                }

                break;
            default:
                break;
        }

        // record order end date
        act.get().setOrderEndDate(resultEndDate);
    }

    /**
     * get order valid date
     *
     * @return
     */
    public static String getOrderValidDate() {
        return act.get().getOrderBeginDate();
    }

    /**
     * get order invalid date
     *
     * @return
     */
    public static String getOrderInValidDate() {
        return act.get().getOrderEndDate();
    }

}
