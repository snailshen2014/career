package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.activity.ChannelWoWindowPo;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class WoWindowChannel extends ChannelFunc {
	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public WoWindowChannel(final Activity act) {
		super(act);
	}

	@Override
	public int recordActivityDetail() {
		ChannelWoWindowPo wo = ActivityJsonFactory.getActivityProvPo().getChannelWoWindowPo();
		// sync wowind channel info
		wo.setTenantId(ActivityJsonFactory.tenantId());
		wo.setActivityId(ActivityJsonFactory.getActivityId());
		wo.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
		if (wo.getChannelId() != null)
			mapper.InsertChannelWebWoWindow(wo);
		return 0;
	}


}
