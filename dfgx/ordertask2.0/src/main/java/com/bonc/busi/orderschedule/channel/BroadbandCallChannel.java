package com.bonc.busi.orderschedule.channel;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.BroadbandCallList;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.WxwlChannelList;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.SpringUtil;

import java.util.List;

public class BroadbandCallChannel extends ChannelFunc {

    private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

    public BroadbandCallChannel(final Activity act) {
        super(act);
    }

    @Override
    public int recordActivityDetail() {
        BroadbandCallList callList = ActivityJsonFactory.getActivityProvPo().getBroadbandCallList();
        PltActivityChannelDetail detail = new PltActivityChannelDetail();
        detail.setCHANN_ID(callList.getChannelId());  //渠道id
        detail.setMARKET_WORDS(callList.getMarketingWords()); //话术内容
        detail.setTENANT_ID(ActivityJsonFactory.tenantId());  //租户id
        detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId()); //活动id
        detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());  //活动流水号
        detail.setFILTER_CON(callList.getFilterCondition()); //筛选条件
        detail.setFILTER_SQL(callList.getFilterConditionSql()); //筛选条件sql
        String specialFilterListString = null;
        if (callList.getChannelSpecialFilterList() != null) {
            specialFilterListString = JSON.toJSONString(callList.getChannelSpecialFilterList());
        }
        if (specialFilterListString != null) {
            detail.setCHANNEL_SPECIALFILTER_LIST(specialFilterListString);
        }
        // ordermapper.InsertActivityChannelStatus(status);
        mapper.insertChannelDetailInfo(detail);
        return 0;
    }

    @Override
    public void setRecomendInfoAndSmsTemplate() {
        List<ChannelSpecialFilterPo> special = ActivityJsonFactory.getActivityProvPo().getBroadbandCallList().getChannelSpecialFilterList();
        if (special!= null && special.size()>0){
            setRecomendInfo(getSpecialFilterMap(special,0));
            setSmsTemplate(getSpecialFilterMap(special,1));
            setProductId(getSpecialFilterMap(special,2));
            setProductName(getSpecialFilterMap(special,3));
            setFilterName(getSpecialFilterMap(special,4));
        }
    }
}
