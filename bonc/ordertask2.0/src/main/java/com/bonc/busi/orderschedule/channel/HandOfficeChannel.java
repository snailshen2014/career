package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.task.base.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.bonc.busi.activity.ChannelHandOfficePo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;

public class HandOfficeChannel extends ChannelFunc {

	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public HandOfficeChannel(final Activity act) {
        super(act);
    }

    @Override
	public int recordActivityDetail() {
		 ChannelHandOfficePo hand = ActivityJsonFactory.getActivityProvPo().getChannelHandOfficePo();
		PltActivityChannelDetail detail = new PltActivityChannelDetail();
		detail.setCONTENT(hand.getChannelHandofficeContent());
		detail.setTITLE(hand.getChannelHandofficeTitle());
		detail.setURL(hand.getChannelHandofficeUrl());
		detail.setCHANN_ID(hand.getChannelId());
		detail.setTENANT_ID(ActivityJsonFactory.tenantId());
		detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
		detail.setFILTER_CON(hand.getFilterCondition() );
		detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
		detail.setFILTER_SQL(hand.getFilterConditionSql());
		mapper.insertChannelDetailInfo(detail);
		return 0;
	}


}
