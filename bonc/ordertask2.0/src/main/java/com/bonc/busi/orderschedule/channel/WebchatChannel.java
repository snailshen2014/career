package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.task.base.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.bonc.busi.activity.ChannelWebchatInfo;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;

public class WebchatChannel extends ChannelFunc {
	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public WebchatChannel(final Activity act) {
		super(act);
	}

	@Override
	public int recordActivityDetail() {
		ChannelWebchatInfo wechat = ActivityJsonFactory.getActivityProvPo().getChannelWebchatInfo();
		wechat.setActivityId(ActivityJsonFactory.getActivityId());
		wechat.setTenantId(ActivityJsonFactory.tenantId());
		wechat.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
		mapper.InsertChannelWebchatInfo(wechat);
		return 0;
	}

}
