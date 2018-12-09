package com.bonc.busi.orderschedule.channel;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ChannelOutcallPo;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.StyleBoxChannelList;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.SpringUtil;

import java.util.List;

public class OutCallChannel extends ChannelFunc {

    private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

    public OutCallChannel(final Activity act) {
        super(act);
    }

    @Override
    public int recordActivityDetail() {
        ChannelOutcallPo out = ActivityJsonFactory.getActivityProvPo().getChannelOutcallPo();
        PltActivityChannelDetail detail = new PltActivityChannelDetail();
        detail.setCHANN_ID(out.getChannelId());  //渠道id
        detail.setTENANT_ID(ActivityJsonFactory.tenantId());  //租户id
        detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId()); //活动id
        detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());  //活动流水号
        detail.setFILTER_CON(out.getFilterCondition()); //筛选条件
        detail.setFILTER_SQL(out.getFilterConditionSql()); //筛选条件sql
        detail.setRESERVE1(JSON.toJSONString(out.getDistributeList()));
        mapper.insertChannelDetailInfo(detail);
        return 0;
    }

}
