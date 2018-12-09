package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.task.base.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.bonc.busi.activity.ChannelWebOfficePo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;

public class WebOfficeChannel extends ChannelFunc {
	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public WebOfficeChannel(final Activity act) {
		super(act);
	}

	@Override
	public int recordActivityDetail() {
		ChannelWebOfficePo web = ActivityJsonFactory.getActivityProvPo().getChannelWebOfficePo();
		// sync detail info
		PltActivityChannelDetail detail = new PltActivityChannelDetail();
		detail.setCONTENT(web.getChannelWebofficeContent());
		detail.setTITLE(web.getChannelWebofficeTitle());
		detail.setURL(web.getChannelWebofficeUrl());
		detail.setCHANN_ID(web.getChannelId());
		detail.setTENANT_ID(ActivityJsonFactory.tenantId());
		detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
		detail.setFILTER_CON(web.getFilterCondition() );
		detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
		detail.setFILTER_SQL(web.getFilterConditionSql());
		mapper.insertChannelDetailInfo(detail);
		return 0;
	}


}
