package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.activity.MsmChannelPo;
import com.bonc.busi.activity.ZongBuMsmChannelPo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.SpringUtil;

public class ZongBuMsmChannel extends ChannelFunc {
	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public ZongBuMsmChannel(final Activity act) {
		super(act);
	}

	@Override
	public int recordActivityDetail() {
		ZongBuMsmChannelPo msm = ActivityJsonFactory.getActivityProvPo().getZongBuMsmChannelPo();
		PltActivityChannelDetail detail = new PltActivityChannelDetail();
		detail.setCHANN_ID( msm.getChannelId());
		if (msm.getCycleTimes() != null)
			detail.setTIMES(Integer.parseInt(msm.getCycleTimes()));
		detail.setFILTER_CON(msm.getFilterCondition());
		detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
		detail.setFILTER_SQL(msm.getFilterConditionSql());
		if (msm.getIntervalHours() != null)
			detail.setINTERVAL_HOUR(Integer.parseInt(msm.getIntervalHours()));
		detail.setNOSEND_TIME(msm.getNoSendTime());
		detail.setEND_TIME(msm.getSendEndTime());
		detail.setSTART_TIME(msm.getSendStartTime());
		detail.setSEND_LEVEL(msm.getSendLevel());
		detail.setTENANT_ID(ActivityJsonFactory.tenantId());
		detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
		detail.setTOUCHLIMITDAY(msm.getTouchLimitDay());
		detail.setCONTENT(msm.getSmsContent());
		detail.setRESERVE1(msm.getMessageSendTime());
		/**
		 * 短信中的保存字段reserve2为短信端口号
		 * */
		detail.setRESERVE2(ActivityJsonFactory.getActivityProvPo().getSmsUsePort());
		mapper.insertChannelDetailInfo(detail);
		return 0;
	}


}
