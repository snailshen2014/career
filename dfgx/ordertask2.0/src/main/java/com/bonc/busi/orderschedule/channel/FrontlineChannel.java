package com.bonc.busi.orderschedule.channel;

import java.util.List;

import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.task.base.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;

public class FrontlineChannel extends ChannelFunc {
	
	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public FrontlineChannel(final Activity act) {
		super(act);
	}

	@Override
	public int recordActivityDetail() {
		FrontlineChannelPo front = ActivityJsonFactory.getActivityProvPo().getFrontlineChannelPo();
		front.setTenantId(ActivityJsonFactory.tenantId());
		front.setActivityId(ActivityJsonFactory.getActivityId());
		front.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
		mapper.InsertChannelFrontline(front);
		return 0;
	}

	@Override
	public void setRecomendInfoAndSmsTemplate() {
		List<ChannelSpecialFilterPo> front = ActivityJsonFactory.getActivityProvPo().getFrontlineChannelPo().getChannelSpecialFilterList();
		if (front!= null && front.size()>0){
			setRecomendInfo(getSpecialFilterMap(front,0));
			setSmsTemplate(getSpecialFilterMap(front,1));
			setProductId(getSpecialFilterMap(front,2));
            setProductName(getSpecialFilterMap(front,3));
			setFilterName(getSpecialFilterMap(front,4));
		}
	}


}
