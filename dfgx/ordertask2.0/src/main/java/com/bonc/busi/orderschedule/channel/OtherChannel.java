package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.activity.ChannelHandOfficePo;
import com.bonc.busi.activity.ChannelOtherPo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.SpringUtil;

public class OtherChannel extends ChannelFunc {

    private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

    public OtherChannel(final Activity act) {
        super(act);
    }

    @Override
    public int recordActivityDetail() {
        ChannelOtherPo other = ActivityJsonFactory.getActivityProvPo().getChannelOtherPo();
        PltActivityChannelDetail detail = new PltActivityChannelDetail();
        detail.setCHANN_ID(other.getChannelId());
        detail.setTENANT_ID(ActivityJsonFactory.tenantId());
        detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
        detail.setFILTER_CON(other.getFilterCondition());
        detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
        detail.setFILTER_SQL(other.getFilterConditionSql());
        detail.setRESERVE1(other.getExportModelName());
        mapper.insertChannelDetailInfo(detail);
        return 0;
    }


}
